package explorador.notificaciones.bo;

import explorador.publicaciones.modelo.Publicacion;

import java.util.List;
import java.util.Set;

public interface NotificadorBO {
    void notificarNuevas(List<Publicacion> publicaciones, String correoDestino);

    boolean fueNotificada(int publicacionId);

    void limpiarPorPublicacionIds(Set<Integer> publicacionIds);
}
