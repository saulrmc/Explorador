package explorador.notificaciones.dao;

import explorador.notificaciones.modelo.RegistroNotificacion;

import java.util.List;

public interface RegistroNotificacionDAO {
    List<RegistroNotificacion> leerTodos();

    void agregar(RegistroNotificacion registro);

    boolean existe(int publicacionId);
}
