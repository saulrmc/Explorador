package explorador.notificaciones.dao;

import explorador.notificaciones.modelo.RegistroNotificacion;

import java.util.List;
import java.util.Set;

public interface RegistroNotificacionDAO {
    List<RegistroNotificacion> leerTodos();

    void agregar(RegistroNotificacion registro);

    boolean existe(int publicacionId);

    void eliminarPorPublicacionIds(Set<Integer> publicacionIds);
}
