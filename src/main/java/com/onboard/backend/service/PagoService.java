package com.onboard.backend.service;

import com.onboard.backend.entity.Factura;
import com.onboard.backend.entity.Pago;
import com.onboard.backend.entity.Reserva;
import com.onboard.backend.entity.Usuario;
import com.onboard.backend.entity.Vehiculo;
import com.onboard.backend.model.EstadoReserva;
import com.onboard.backend.repository.FacturaRepository;
import com.onboard.backend.repository.PagoRepository;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.http.exceptions.HttpException;
import com.paypal.orders.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private PayPalHttpClient payPalClient;

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private VehiculoService vehiculoService;

    public static final String exitoso = "http://localhost:5173/pago/cancelado";
    public static final String cancelado = "http://localhost:5173/pago/exitoso";

    public String crearPago(String idFactura) throws Exception {
        Optional<Factura> facturaOpt = facturaRepository.findById(idFactura);
        if (facturaOpt.isEmpty()) {
            throw new RuntimeException("Factura no encontrada con ID: " + idFactura);
        }

        Factura factura = facturaOpt.get();
        BigDecimal total = factura.getTotal();

        // ⚠️ Formatear el valor con punto decimal (PayPal no acepta comas)
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat formatter = new DecimalFormat("0.00", symbols);
        String valor = formatter.format(total); // ← por ejemplo: "79.07"

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl(exitoso)
                .cancelUrl(cancelado)
                .brandName("OnBoard")
                .landingPage("LOGIN")
                .userAction("PAY_NOW");

        orderRequest.applicationContext(applicationContext);

        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
                .amountWithBreakdown(new AmountWithBreakdown()
                        .currencyCode("USD")
                        .value(valor));

        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));

        OrdersCreateRequest request = new OrdersCreateRequest();
        request.header("prefer", "return=representation");
        request.requestBody(orderRequest);

        try {
            HttpResponse<Order> response = payPalClient.execute(request);
            Order order = response.result();

            Pago pago = new Pago();
            pago.setIdPago(order.id());
            pago.setIdFactura(idFactura);
            pago.setFechaPago(LocalDate.now());
            pago.setEstadoPago(order.status());
            factura.setEstadoPago(order.status());
            pago.setDetalle("Orden PayPal creada");

            pagoRepository.save(pago);

            return "{\"orderId\": \"" + order.id() + "\"}";

        } catch (HttpException e) {
            return "{\"error\": \"Error al crear orden en PayPal: " + e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    public ResponseEntity<Map<String, String>> capturarPago(String orderId) throws Exception {
        OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
        request.requestBody(new OrderRequest());

        try {
            HttpResponse<Order> response = payPalClient.execute(request);
            Order orden = response.result();
            Capture capture = orden.purchaseUnits().get(0)
                    .payments().captures().get(0);

            String transactionId = capture.id();
            String status = capture.status();
            String payerEmail = orden.payer().email();
            String payerName = orden.payer().name().givenName() + " " + orden.payer().name().surname();

            Optional<Pago> pagoOpt = pagoRepository.findById(orderId);
            if (pagoOpt.isEmpty()) {
                throw new RuntimeException("No se encontró el pago con ID: " + orderId);
            }

            Pago pago = pagoOpt.get();
            Optional<Factura> facturaOpt = facturaRepository.findById(pago.getIdFactura());
            if (facturaOpt.isEmpty()) {
                throw new RuntimeException("No se encontró la factura con ID: " + pago.getIdFactura());
            }

            Factura factura = facturaOpt.get();

            pago.setEstadoPago(status);
            pago.setDetalle("Pago capturado: ID transacción " + transactionId);
            factura.setEstadoPago(status);

            Reserva reserva = reservaService.getReservaById(factura.getIdReserva())
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + factura.getIdReserva()));

            Usuario cliente = usuarioService.getUsuarioById(reserva.getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + reserva.getIdCliente()));

            Vehiculo vehiculo = vehiculoService.getVehiculoById(reserva.getIdVehiculo())
                    .orElseThrow(
                            () -> new RuntimeException("Vehículo no encontrado con ID: " + reserva.getIdVehiculo()));

            if ("COMPLETED".equals(status.toString())) {
                pago.setFechaPago(LocalDate.now());
                reservaService.actualizarEstadoReserva(reserva.getIdReserva(), EstadoReserva.ACTIVA);
            }

            pagoRepository.save(pago);
            facturaRepository.save(factura);

            emailService.enviarFacturaPorEmail(factura, reserva, cliente, vehiculo, "PayPal");

            return ResponseEntity.ok(Map.of(
                    "status", status,
                    "transactionId", transactionId,
                    "payerEmail", payerEmail,
                    "payerName", payerName,
                    "message", "Pago capturado exitosamente"));

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al capturar pago: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    public List<Pago> obtenerTodosLosPagos() {
        return pagoRepository.findAll();
    }

    public Optional<Pago> getPagoById(String id) {
        return pagoRepository.findById(id);
    }

    public void eliminarPago(String id) {
        pagoRepository.deleteById(id);
    }
}
