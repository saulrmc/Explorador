package explorador.notificaciones.dao;

import explorador.data.JsonPersistencia;
import explorador.notificaciones.modelo.RegistroNotificacion;

import java.util.ArrayList;
import java.util.List;

public class RegistroNotificacionDAOImpl implements RegistroNotificacionDAO {

    private static final String ARCHIVO = "enviadas";
    private final JsonPersistencia persistencia;

    public RegistroNotificacionDAOImpl() {
        this.persistencia = new JsonPersistencia("Notificaciones");
    }

    @Override
    public List<RegistroNotificacion> leerTodos() {
        return persistencia.leerLista(ARCHIVO, RegistroNotificacion.class);
    }

    @Override
    public void agregar(RegistroNotificacion registro) {
        List<RegistroNotificacion> registros = new ArrayList<>(leerTodos());
        registros.add(registro);
        persistencia.escribir(ARCHIVO, registros);
    }

    @Override
    public boolean existe(int publicacionId) {
        return leerTodos().stream()
                .anyMatch(registro -> registro.getPublicacionId() == publicacionId);
    }
}
