package explorador.notificaciones.bo;

import explorador.notificaciones.dao.RegistroNotificacionDAO;
import explorador.notificaciones.dao.RegistroNotificacionDAOImpl;
import explorador.notificaciones.modelo.RegistroNotificacion;
import explorador.publicaciones.modelo.Publicacion;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class NotificadorBOImpl implements NotificadorBO {

    private static final int MAX_PALABRAS_ASUNTO = 7;

    private final RegistroNotificacionDAO registroDao;
    private final EnviadorCorreo enviadorCorreo;

    public NotificadorBOImpl() {
        this(new RegistroNotificacionDAOImpl(), new SmtpEnviadorCorreo());
    }

    public NotificadorBOImpl(RegistroNotificacionDAO registroDao) {
        this(registroDao, new SmtpEnviadorCorreo());
    }

    public NotificadorBOImpl(RegistroNotificacionDAO registroDao, EnviadorCorreo enviadorCorreo) {
        this.registroDao = registroDao;
        this.enviadorCorreo = enviadorCorreo;
    }

    @Override
    public void notificarNuevas(List<Publicacion> publicaciones, String correoDestino) {
        for (Publicacion publicacion : publicaciones) {
            if (publicacion.getOriginal() == null
                    || publicacion.getOriginal().getUrl() == null) {
                continue;
            }
            if (registroDao.existe(publicacion.getId())) {
                continue;
            }

            String asunto = primerasPalabras(publicacion.getTitulo(), MAX_PALABRAS_ASUNTO);
            String contenido = publicacion.getTitulo() + "\n\n"
                    + publicacion.getDescripcion() + "\n\n"
                    + "Articulo original: " + publicacion.getOriginal().getUrl();

            if (!enviadorCorreo.enviar(correoDestino, asunto, contenido)) {
                continue;
            }

            RegistroNotificacion registro = new RegistroNotificacion();
            registro.setPublicacionId(publicacion.getId());
            registro.setAsunto(asunto);
            registro.setEnviado(LocalDateTime.now());
            registroDao.agregar(registro);
        }
        registroDao.guardar();
    }

    @Override
    public boolean fueNotificada(int publicacionId) {
        return registroDao.existe(publicacionId);
    }

    @Override
    public void limpiarPorPublicacionIds(Set<Integer> publicacionIds) {
        registroDao.eliminarPorPublicacionIds(publicacionIds);
        registroDao.guardar();
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
