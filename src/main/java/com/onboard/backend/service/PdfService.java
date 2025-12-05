package com.onboard.backend.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.onboard.backend.entity.Factura;
import com.onboard.backend.entity.Reserva;
import com.onboard.backend.entity.Usuario;
import com.onboard.backend.entity.Vehiculo;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class PdfService {



    public byte[] generarFacturaPdf(Factura factura, Usuario cliente, Vehiculo vehiculo, Reserva reserva, String metodoPago){

        try {

            // === Documento ===
            Document document = new Document(PageSize.A4, 50, 50, 70, 50);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            // === Fuentes y colores ===
            BaseColor azul = new BaseColor(40, 80, 120);
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, azul);
            Font fontNormal = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.DARK_GRAY);
            Font fontEtiqueta = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font fontTotal = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);

            // === Logo ===
            try {
                InputStream logoStream = new ClassPathResource("static/images/logo.png").getInputStream();
                Image logo = Image.getInstance(logoStream.readAllBytes());
                logo.scaleToFit(120, 50);
                logo.setAlignment(Element.ALIGN_RIGHT);
                document.add(logo);
            } catch (Exception e) {
                // Logo opcional
            }

            // === Título ===
            Paragraph titulo = new Paragraph("Factura Electrónica", fontTitulo);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            // === Datos factura ===
            PdfPTable datosFactura = new PdfPTable(2);
            datosFactura.setWidthPercentage(100);
            datosFactura.setWidths(new float[] { 2, 4 });
            datosFactura.setSpacingAfter(20);

            addCell(datosFactura, "ID Factura:", fontEtiqueta);
            addCell(datosFactura, factura.getIdFactura(), fontNormal);
            addCell(datosFactura, "Fecha de emisión:", fontEtiqueta);
            addCell(datosFactura, factura.getFechaEmision().toString(), fontNormal);
            addCell(datosFactura, "Estado de pago:", fontEtiqueta);
            addCell(datosFactura, factura.getEstadoPago(), fontNormal);
            addCell(datosFactura, "Método de pago:", fontEtiqueta);
            addCell(datosFactura, metodoPago, fontNormal);
            document.add(datosFactura);

            // === Cliente ===
            addSectionTitle(document, "Información del Cliente", fontEtiqueta);
            addInfoTable(document, Map.of(
                    "Nombre", cliente.getNombre(),
                    "Correo", cliente.getCorreo(),
                    "Teléfono", cliente.getTelefono(),
                    "Dirección", cliente.getDireccion()));

            // === Vehículo ===
            addSectionTitle(document, "Vehículo Reservado", fontEtiqueta);
            addInfoTable(document, Map.of(
                    "Vehículo", vehiculo.getMarca() + " " + vehiculo.getModelo() + " (" + vehiculo.getAnio() + ")",
                    "Placa", vehiculo.getPlaca(),
                    "Tipo", vehiculo.getTipoVehiculo(),
                    "Transmisión", vehiculo.getTipoTransmision(),
                    "Combustible", vehiculo.getCombustible(),
                    "Capacidad", vehiculo.getCapacidadPasajeros() + " pasajeros"));

            // === Reserva ===
            addSectionTitle(document, "Detalles de la Reserva", fontEtiqueta);
            long dias = ChronoUnit.DAYS.between(reserva.getFechaInicio(), reserva.getFechaFin());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            addInfoTable(document, Map.of(
                    "Fecha de inicio", reserva.getFechaInicio().format(formatter),
                    "Fecha de fin", reserva.getFechaFin().format(formatter),
                    "Duración", dias + " días",
                    "Lugar de entrega/recogida", reserva.getLugarEntregaYRecogida()));

            // === Resumen económico ===
            PdfPTable tabla = new PdfPTable(2);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[] { 4, 2 });
            tabla.setSpacingBefore(20);
            tabla.setSpacingAfter(30);

            addHeaderCell(tabla, "Concepto", fontEtiqueta);
            addHeaderCell(tabla, "Monto (USD)", fontEtiqueta);

            BigDecimal subtotal = factura.getTotal().subtract(factura.getImpuesto());

            addCell(tabla, factura.getRazon(), fontNormal);
            addCell(tabla, "$" + subtotal, fontNormal);

            addCell(tabla, "Impuesto", fontNormal);
            addCell(tabla, "$" + factura.getImpuesto(), fontNormal);

            addCell(tabla, "Total", fontTotal);
            addCell(tabla, "$" + factura.getTotal(), fontTotal);

            document.add(tabla);

            // === Cierre ===
            LineSeparator separator = new LineSeparator();
            separator.setLineColor(BaseColor.LIGHT_GRAY);
            document.add(separator);

            Paragraph gracias = new Paragraph("Gracias por utilizar OnBoard.", fontNormal);
            gracias.setAlignment(Element.ALIGN_CENTER);
            gracias.setSpacingBefore(20);
            document.add(gracias);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF de la factura", e);
        }
    }

    private void addSectionTitle(Document document, String title, Font font) throws DocumentException {
        Paragraph section = new Paragraph(title, font);
        section.setSpacingBefore(10);
        section.setSpacingAfter(5);
        document.add(section);
    }

    private void addInfoTable(Document document, Map<String, String> data) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 2, 4 });
        for (Map.Entry<String, String> entry : data.entrySet()) {
            addCell(table, entry.getKey() + ":", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
            addCell(table, entry.getValue(), new Font(Font.FontFamily.HELVETICA, 12));
        }
        table.setSpacingAfter(15);
        document.add(table);
    }

    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(8);
        table.addCell(cell);
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new BaseColor(230, 230, 250));
        cell.setPadding(8);
        table.addCell(cell);
    }
}
