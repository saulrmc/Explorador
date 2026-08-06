package explorador.notificaciones.bo;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import explorador.data.ExploradorConfig;
import explorador.notificaciones.dao.RegistroNotificacionDAO;
import explorador.notificaciones.dao.RegistroNotificacionDAOImpl;
import explorador.notificaciones.modelo.RegistroNotificacion;
import explorador.publicaciones.modelo.Publicacion;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class NotificadorBOImpl implements NotificadorBO {

    private static final int MAX_PALABRAS_ASUNTO = 7;

    private final RegistroNotificacionDAO registroDao;

    public NotificadorBOImpl() {
        this(new RegistroNotificacionDAOImpl());
    }

    public NotificadorBOImpl(RegistroNotificacionDAO registroDao) {
        this.registroDao = registroDao;
    }

    @Override
    public void notificarNuevas(List<Publicacion> publicaciones, String correoDestino) {
        for (Publicacion publicacion : publicaciones) {
            if (registroDao.existe(publicacion.getId())) {
                continue;
            }

            String asunto = primerasPalabras(publicacion.getTitulo(), MAX_PALABRAS_ASUNTO);
            String contenido = publicacion.getTitulo() + "\n\n"
                    + publicacion.getDescripcion() + "\n\n"
                    + "Articulo original: " + publicacion.getUrl();

            enviar(correoDestino, asunto, contenido);

            RegistroNotificacion registro = new RegistroNotificacion();
            registro.setPublicacionId(publicacion.getId());
            registro.setAsunto(asunto);
            registro.setEnviado(LocalDateTime.now());
            registroDao.agregar(registro);
        }
    }

    @Override
    public boolean fueNotificada(int publicacionId) {
        return registroDao.existe(publicacionId);
    }

    @Override
    public void limpiarPorPublicacionIds(Set<Integer> publicacionIds) {
        registroDao.eliminarPorPublicacionIds(publicacionIds);
    }

    private void enviar(String destinatario, String asunto, String contenido) {
        String emisor = ExploradorConfig.obtener("correo.emisor");
        String clave = ExploradorConfig.obtener("correo.clave_aplicacion");

        if (emisor == null || clave == null || emisor.isBlank() || emisor.startsWith("TU_")) {
            System.out.println("Correo no enviado: configuracion SMTP incompleta para: " + destinatario);
            return;
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

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
        } catch (Exception e) {
            System.out.println("Error al enviar correo");
            e.printStackTrace();
        }
    }

    private String primerasPalabras(String texto, int max) {
        if (texto == null || texto.isBlank()) {
            return "";
        }
        String normalizado = texto.replaceAll("\\s+", " ").trim();
        String[] palabras = normalizado.split(" ");
        if (palabras.length <= max) {
            return normalizado;
        }
        return String.join(" ", Arrays.copyOf(palabras, max));
    }
}
