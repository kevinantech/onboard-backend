package com.onboard.backend.service;

import com.onboard.backend.entity.Reserva;
import com.onboard.backend.entity.Usuario;
import com.onboard.backend.entity.Vehiculo;
import com.onboard.backend.exception.InvalidInputException;
import com.onboard.backend.repository.ReservaRepository;
import com.onboard.backend.repository.UsuarioRepository;
import com.onboard.backend.repository.VehiculoRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
public class ContratoAlquilerService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private EmailService emailService;

    public void generarContratosPdfParaUsuarioYPropietario(String idReserva) throws Exception {

        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new InvalidInputException(
                        "Reservation not found",
                        "RESERVATION_NOT_FOUND",
                        "The reservation with ID " + idReserva + " does not exist in the system."));

        Vehiculo vehiculo = vehiculoRepository.findById(reserva.getIdVehiculo())
                .orElseThrow(() -> new InvalidInputException(
                        "Vehicle not found",
                        "VEHICLE_NOT_FOUND",
                        "The vehicle with ID " + reserva.getIdVehiculo() + " does not exist in the system."));

        Usuario cliente = usuarioRepository.findById(reserva.getIdCliente())
                .orElseThrow(() -> new InvalidInputException(
                        "Client not found",
                        "CLIENT_NOT_FOUND",
                        "The client with ID " + reserva.getIdCliente() + " does not exist in the system."));

        Usuario propietario = usuarioRepository.findById(vehiculo.getIdPropietario())
                .orElseThrow(() -> new InvalidInputException(
                        "Owner not found",
                        "OWNER_NOT_FOUND",
                        "The owner with ID " + vehiculo.getIdPropietario() + " does not exist in the system."));

        long horas = ChronoUnit.HOURS.between(reserva.getFechaInicio(), reserva.getFechaFin());
        float precioPorHora = vehiculo.getPrecioPorDia() / 24f;
        float total = horas * precioPorHora * 1.10f;

        byte[] pdfCliente = generarPdfComoBytes("/pdf/contrato_cliente.html", cliente, propietario, vehiculo, reserva,
                total);
        byte[] pdfPropietario = generarPdfComoBytes("/pdf/contrato_propietario.html", cliente, propietario, vehiculo,
                reserva, total);
        emailService.enviarContratosPdf(
                cliente.getCorreo(), cliente.getNombre(), pdfCliente,
                propietario.getCorreo(), propietario.getNombre(), pdfPropietario);

    }

    private byte[] generarPdfComoBytes(String plantillaPath,
            Usuario cliente,
            Usuario propietario,
            Vehiculo vehiculo,
            Reserva reserva,
            float total) throws Exception {

        var resource = new ClassPathResource("templates" + plantillaPath);
        String html = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        html = html.replace("${ciudad}", cliente.getDireccion().split(",")[0]);
        html = html.replace("${fecha}", LocalDate.now().format(formatter));

        String saludoCliente;
        if (cliente.getTipoIdentificacion().toString().equalsIgnoreCase("NIT")) {
            saludoCliente = "Apreciada empresa <strong>" + cliente.getNombre() + "</strong>,";
        } else {
            saludoCliente = "Estimado(a) señor(a) <strong>" + cliente.getNombre() + "</strong>,";
        }

        String saludoPropietario;
        if (propietario.getTipoIdentificacion().toString().equalsIgnoreCase("NIT")) {
            saludoPropietario = "Apreciada empresa <strong>" + propietario.getNombre() + "</strong>,";
        } else {
            saludoPropietario = "Estimado(a) señor(a) <strong>" + propietario.getNombre() + "</strong>,";
        }

        html = html.replace("${saludo}", saludoPropietario);

        html = html.replace("${saludo}", saludoCliente);

        html = html.replace("${nombreCliente}", cliente.getNombre());
        html = html.replace("${tipoIdentificacionCliente}", cliente.getTipoIdentificacion().toString());
        html = html.replace("${idCliente}", cliente.getIdUsuario());
        html = html.replace("${direccionCliente}", cliente.getDireccion());
        html = html.replace("${telefonoCliente}", cliente.getTelefono());

        html = html.replace("${nombrePropietario}", propietario.getNombre());
        html = html.replace("${tipoIdentificacionPropietario}", propietario.getTipoIdentificacion().toString());
        html = html.replace("${idPropietario}", propietario.getIdUsuario());
        html = html.replace("${direccionPropietario}", propietario.getDireccion());
        html = html.replace("${telefonoPropietario}", propietario.getTelefono());

        html = html.replace("${placa}", vehiculo.getPlaca());
        html = html.replace("${marca}", vehiculo.getMarca());
        html = html.replace("${modelo}", vehiculo.getModelo());
        html = html.replace("${anio}", String.valueOf(vehiculo.getAnio()));
        html = html.replace("${tipoVehiculo}", vehiculo.getTipoVehiculo());
        html = html.replace("${transmision}", vehiculo.getTipoTransmision());
        html = html.replace("${combustible}", vehiculo.getCombustible());
        html = html.replace("${kilometraje}", String.valueOf(vehiculo.getKilometraje()));
        html = html.replace("${capacidad}", String.valueOf(vehiculo.getCapacidadPasajeros()));
        html = html.replace("${descripcion}", vehiculo.getDescripcion());
        html = html.replace("${urlSoat}", vehiculo.getSoat());
        html = html.replace("${urlTecno}", vehiculo.getTecnomecanica());
        html = html.replace("${urlAntecedentes}", vehiculo.getAntecedentes());

        html = html.replace("${fechaInicio}", reserva.getFechaInicio().toLocalDate().format(formatter));
        html = html.replace("${fechaFin}", reserva.getFechaFin().toLocalDate().format(formatter));
        html = html.replace("${lugarRecogida}", reserva.getLugarEntregaYRecogida());
        html = html.replace("${lugarEntrega}", reserva.getLugarEntregaYRecogida());
        html = html.replace("${precioPorDia}", String.format("%.2f", vehiculo.getPrecioPorDia()));
        html = html.replace("${total}", String.format("%.2f", total));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(baos);
            builder.run();
            return baos.toByteArray();
        }
    }
}
