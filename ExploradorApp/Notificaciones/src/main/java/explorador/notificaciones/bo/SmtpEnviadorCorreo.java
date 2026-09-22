package explorador.notificaciones.bo;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import explorador.data.ExploradorConfig;

import java.util.Properties;

public class SmtpEnviadorCorreo implements EnviadorCorreo {

    @Override
    public boolean enviar(String destinatario, String asunto, String contenido) {
        String emisor = ExploradorConfig.obtener("correo.emisor");
        String clave = ExploradorConfig.obtener("correo.clave_aplicacion");

        if (emisor == null || clave == null || emisor.isBlank() || emisor.startsWith("TU_")) {
            System.out.println("Correo no enviado: configuracion SMTP incompleta para: " + destinatario);
            return false;
        }

        String host = ExploradorConfig.obtener("correo.smtp.host", "smtp.gmail.com");
        int puerto = Integer.parseInt(ExploradorConfig.obtener("correo.smtp.puerto", "587"));
        boolean tls = Boolean.parseBoolean(ExploradorConfig.obtener("correo.smtp.tls", "true"));

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", String.valueOf(tls));
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", String.valueOf(puerto));

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emisor, clave);
                }
            });

            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(emisor));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject(asunto);
            mensaje.setText(contenido);

            Transport.send(mensaje);
            System.out.println("Correo enviado correctamente a: " + destinatario);
            return true;
        } catch (Exception e) {
            System.out.println("Error al enviar correo");
            e.printStackTrace();
            return false;
        }
    }
}
