package explorador.notificaciones.bo;

import explorador.publicaciones.modelo.Publicacion;

import java.util.List;

public interface NotificadorBO {
    void notificarNuevas(List<Publicacion> publicaciones, String correoDestino);

    boolean fueNotificada(int publicacionId);
}
