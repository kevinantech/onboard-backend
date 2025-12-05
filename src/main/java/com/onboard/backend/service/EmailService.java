package com.onboard.backend.service;

import jakarta.mail.internet.MimeMessage;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.onboard.backend.entity.Factura;
import com.onboard.backend.entity.Reserva;
import com.onboard.backend.entity.Usuario;
import com.onboard.backend.entity.Vehiculo;
import com.onboard.backend.model.EstadoOferta;
import com.onboard.backend.model.EstadoReserva;
import com.onboard.backend.model.EstadoVerificacion;

import java.util.Map;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private static final String LOGO_URL = "https://static.vecteezy.com/system/resources/previews/048/525/793/non_2x/realistic-sport-car-isolated-on-background-3d-rendering-illustration-png.png";
    @Autowired
    private PdfService pdfService;

    @Autowired
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    private void enviarCorreoConTemplate(String toEmail, String subject, String plantilla,
            Map<String, Object> variables) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setFrom("onboardnotifications@gmail.com");
            helper.setSubject(subject);

            Context context = new Context();
            if (variables != null) {
                variables.forEach(context::setVariable);
            }
            context.setVariable("logoUrl", LOGO_URL);

            String contenidoHtml = templateEngine.process(plantilla, context);
            helper.setText(contenidoHtml, true);

            mailSender.send(mensaje);

        } catch (Exception e) {
            logger.error("Error sending email to <{}>", toEmail, e);
        }
    }

    public void enviarCorreoEstadoCuenta(String toEmail, String nombreUsuario, EstadoVerificacion estado) {
        String plantilla;
        String asunto;

        switch (estado) {
            case APROBADO -> {
                plantilla = "email/estado_cuenta_usuario/aprobado";
                asunto = "✅ Your account has been approved!";
            }
            case RECHAZADO -> {
                plantilla = "email/estado_cuenta_usuario/rechazado";
                asunto = "❌ Your account registration was rejected";
            }
            case INACTIVO -> {
                plantilla = "email/estado_cuenta_usuario/eliminado";
                asunto = "🗑️ Your account has been deleted";
            }
            case SUSPENDIDO -> {
                plantilla = "email/estado_cuenta_usuario/suspendido";
                asunto = "⚠️ Your account has been suspended";
            }
            default -> {
                plantilla = "email/estado_cuenta_usuario/pendiente";
                asunto = "🎉 Welcome to OnBoard!";
            }
        }

        Map<String, Object> variables = Map.of("nombre", nombreUsuario);
        enviarCorreoConTemplate(toEmail, asunto, plantilla, variables);
    }

    public void enviarCorreoEstadoReserva(String toEmail, String nombreUsuario, String placa, EstadoReserva estado) {
        String plantilla;
        String asunto;

        switch (estado) {
            case ACTIVA -> {
                plantilla = "email/estado_reserva/activa";
                asunto = "✅ Your reservation is now active!";
            }
            case CANCELADA -> {
                plantilla = "email/estado_reserva/cancelada";
                asunto = "❌ Your reservation has been cancelled";
            }
            default -> {
                plantilla = "email/estado_reserva/pendiente";
                asunto = "⏳ Your reservation request is pending";
            }
        }

        Map<String, Object> variables = Map.of(
                "nombre", nombreUsuario,
                "placa", placa);

        enviarCorreoConTemplate(toEmail, asunto, plantilla, variables);
    }

    /*
     * public void enviarCorreoEstadoVehiculo(String toEmail, String nombreUsuario,
     * String placa, EstadoVerificacion estado) {
     * String plantilla;
     * String asunto;
     * 
     * switch (estado) {
     * case APROBADO -> {
     * plantilla = "email/estado_aprobacion_vehiculo/vehiculo_aprobado";
     * asunto = "✅ Your vehicle has been approved!";
     * }
     * case RECHAZADO -> {
     * plantilla = "email/estado_aprobacion_vehiculo/vehiculo_rechazado";
     * asunto = "❌ Your vehicle registration was rejected";
     * }
     * case INACTIVO -> {
     * plantilla = "email/estado_aprobacion_vehiculo/vehiculo_inactivo";
     * asunto = "🗑️ Your vehicle has been removed or marked inactive";
     * }
     * case SUSPENDIDO -> {
     * plantilla = "email/estado_aprobacion_vehiculo/vehiculo_suspendido";
     * asunto = "⚠️ Your vehicle listing has been suspended";
     * }
     * default -> {
     * plantilla = "email/estado_aprobacion_vehiculo/vehiculo_pendiente";
     * asunto = "🚗 Your vehicle is pending verification";
     * }
     * }
     * 
     * Map<String, Object> variables = Map.of(
     * "nombre", nombreUsuario,
     * "placa", placa
     * );
     * 
     * enviarCorreoConTemplate(toEmail, asunto, plantilla, variables);
     * }
     */

    public void enviarCorreoEstadoOferta(String toEmail, String nombreUsuario, String placa, EstadoOferta estado) {
        String plantilla;
        String asunto;

        switch (estado) {
            case ACTIVA -> {
                plantilla = "email/estado_oferta/activa";
                asunto = "✅ Your vehicle offer is now active!";
            }
            case INACTIVA -> {
                plantilla = "email/estado_oferta/inactiva";
                asunto = "❌ Your vehicle offer has been removed";
            }
            case EN_MANTENIMIENTO -> {
                plantilla = "email/estado_oferta/en_mantenimiento";
                asunto = "🔧 Your vehicle is under maintenance";
            }
            case FUERA_DE_SERVICIO -> {
                plantilla = "email/estado_oferta/fuera_de_servicio";
                asunto = "🚫 Your vehicle is out of service";
            }
            case SUSPENDIDO -> {
                plantilla = "email/estado_oferta/suspendido";
                asunto = "⚠️ Your vehicle offer has been suspended";
            }
            case RECHAZADA -> {
                plantilla = "email/estado_oferta/rechazada";
                asunto = "❗ Your vehicle offer has been rejected";
            }
            default -> {
                plantilla = "email/estado_oferta/pendiente";
                asunto = "⏳ Your vehicle offer is pending approval";
            }
        }

        Map<String, Object> variables = Map.of(
                "nombre", nombreUsuario,
                "placa", placa);

        enviarCorreoConTemplate(toEmail, asunto, plantilla, variables);
    }

    public void enviarContratosPdf(String emailCliente, String nombreCliente, byte[] pdfCliente,
            String emailPropietario, String nombrePropietario, byte[] pdfPropietario) {
        try {
            logger.info("Iniciando envío de contratos PDF.");
            logger.info("Email cliente: {}, Nombre cliente: {}", emailCliente, nombreCliente);
            logger.info("Email propietario: {}, Nombre propietario: {}", emailPropietario, nombrePropietario);

            // Cliente
            logger.info("Creando mensaje de correo para cliente...");
            MimeMessage mensajeCliente = mailSender.createMimeMessage();
            MimeMessageHelper helperCliente = new MimeMessageHelper(mensajeCliente, true, "UTF-8");

            helperCliente.setTo(emailCliente);
            helperCliente.setFrom("onboardnotifications@gmail.com");
            helperCliente.setSubject("📄 Rental Details - Client");
            logger.info("Encabezados del correo del cliente configurados.");

            Context contextCliente = new Context();
            contextCliente.setVariable("nombre", nombreCliente);
            contextCliente.setVariable("logoUrl", LOGO_URL);
            logger.info("Contexto de plantilla del cliente preparado.");

            String contenidoHtmlCliente = templateEngine.process("email/acuerdo_alquiler/contrato_cliente",
                    contextCliente);
            helperCliente.setText(contenidoHtmlCliente, true);
            logger.info("Contenido HTML del cliente generado.");

            if (pdfCliente == null || pdfCliente.length == 0) {
                logger.warn("PDF del cliente está vacío o nulo.");
            } else {
                helperCliente.addAttachment("Contrato_Cliente.pdf", new ByteArrayResource(pdfCliente));
                logger.info("Archivo PDF del cliente adjuntado.");
            }

            mailSender.send(mensajeCliente);
            logger.info("Correo enviado exitosamente al cliente: {}", emailCliente);

            // Propietario
            logger.info("Creando mensaje de correo para propietario...");
            MimeMessage mensajePropietario = mailSender.createMimeMessage();
            MimeMessageHelper helperPropietario = new MimeMessageHelper(mensajePropietario, true, "UTF-8");

            helperPropietario.setTo(emailPropietario);
            helperPropietario.setFrom("onboardnotifications@gmail.com");
            helperPropietario.setSubject("📄 Rental Details - Owner");
            logger.info("Encabezados del correo del propietario configurados.");

            Context contextPropietario = new Context();
            contextPropietario.setVariable("nombre", nombrePropietario);
            contextPropietario.setVariable("logoUrl", LOGO_URL);
            logger.info("Contexto de plantilla del propietario preparado.");

            String contenidoHtmlPropietario = templateEngine.process("email/acuerdo_alquiler/contrato_propietario",
                    contextPropietario);
            helperPropietario.setText(contenidoHtmlPropietario, true);
            logger.info("Contenido HTML del propietario generado.");

            if (pdfPropietario == null || pdfPropietario.length == 0) {
                logger.warn("PDF del propietario está vacío o nulo.");
            } else {
                helperPropietario.addAttachment("Contrato_Propietario.pdf", new ByteArrayResource(pdfPropietario));
                logger.info("Archivo PDF del propietario adjuntado.");
            }

            mailSender.send(mensajePropietario);
            logger.info("Correo enviado exitosamente al propietario: {}", emailPropietario);

        } catch (Exception e) {
            logger.error("Error al enviar contratos PDF a cliente <{}> y propietario <{}>", emailCliente,
                    emailPropietario, e);
        }
    }

    /*
     * public void enviarContratosPdf(String emailCliente, String nombreCliente,
     * byte[] pdfCliente,
     * String emailPropietario, String nombrePropietario, byte[] pdfPropietario) {
     * try {
     * MimeMessage mensajeCliente = mailSender.createMimeMessage();
     * MimeMessageHelper helperCliente = new MimeMessageHelper(mensajeCliente, true,
     * "UTF-8");
     * 
     * helperCliente.setTo(emailCliente);
     * helperCliente.setFrom("onboardnotifications@gmail.com");
     * helperCliente.setSubject("📄 Rental Details - Client");
     * 
     * Context contextCliente = new Context();
     * contextCliente.setVariable("nombre", nombreCliente);
     * contextCliente.setVariable("logoUrl", LOGO_URL);
     * 
     * String contenidoHtmlCliente =
     * templateEngine.process("email/acuerdo_alquiler/contrato_cliente",
     * contextCliente);
     * helperCliente.setText(contenidoHtmlCliente, true);
     * helperCliente.addAttachment("Contrato_Cliente.pdf", new
     * ByteArrayResource(pdfCliente));
     * 
     * mailSender.send(mensajeCliente);
     * 
     * MimeMessage mensajePropietario = mailSender.createMimeMessage();
     * MimeMessageHelper helperPropietario = new
     * MimeMessageHelper(mensajePropietario, true, "UTF-8");
     * 
     * helperPropietario.setTo(emailPropietario);
     * helperPropietario.setFrom("onboardnotifications@gmail.com");
     * helperPropietario.setSubject("📄 Rental Details - Owner");
     * 
     * Context contextPropietario = new Context();
     * contextPropietario.setVariable("nombre", nombrePropietario);
     * contextPropietario.setVariable("logoUrl", LOGO_URL);
     * 
     * String contenidoHtmlPropietario =
     * templateEngine.process("email/acuerdo_alquiler/contrato_propietario",
     * contextPropietario);
     * helperPropietario.setText(contenidoHtmlPropietario, true);
     * helperPropietario.addAttachment("Contrato_Propietario.pdf", new
     * ByteArrayResource(pdfPropietario));
     * 
     * mailSender.send(mensajePropietario);
     * 
     * } catch (Exception e) {
     * logger.
     * error("Error al enviar contratos PDF a cliente <{}> y propietario <{}>",
     * emailCliente,
     * emailPropietario, e);
     * }
     * }
     */

    public void enviarFacturaPorEmail(Factura factura, Reserva reserva, Usuario cliente, Vehiculo vehiculo,
            String metodoPago) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(cliente.getCorreo());
            helper.setFrom("onboardnotifications@gmail.com");
            helper.setSubject("🧾 Tu Factura Electrónica - OnBoard");

            Context context = new Context();
            context.setVariable("nombre", cliente.getNombre());
            context.setVariable("logoUrl", LOGO_URL);
            context.setVariable("total", factura.getTotal());
            context.setVariable("fecha", factura.getFechaEmision());

            String contenidoHtml = templateEngine.process("email/factura/factura_cliente", context);
            helper.setText(contenidoHtml, true);

            // 🚨 Aquí se pasa todo lo necesario explícitamente
            byte[] pdfFactura = pdfService.generarFacturaPdf(factura, cliente, vehiculo, reserva, metodoPago);
            helper.addAttachment("Factura_OnBoard.pdf", new ByteArrayResource(pdfFactura));

            mailSender.send(mensaje);
        } catch (Exception e) {
            logger.error("Error al enviar la factura por email a <{}>", cliente.getCorreo(), e);
        }
    }

}
