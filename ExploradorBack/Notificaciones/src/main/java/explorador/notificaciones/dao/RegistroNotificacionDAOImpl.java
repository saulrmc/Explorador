package explorador.notificaciones.dao;

import explorador.data.JsonPersistencia;
import explorador.notificaciones.modelo.RegistroNotificacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RegistroNotificacionDAOImpl implements RegistroNotificacionDAO {

    private static final String ARCHIVO = "enviadas";
    private static final Object BLOQUEO = new Object();

    private static List<RegistroNotificacion> registros;
    private static boolean sucio;

    private final JsonPersistencia persistencia;

    public RegistroNotificacionDAOImpl() {
        this(new JsonPersistencia("Notificaciones"));
    }

    public RegistroNotificacionDAOImpl(JsonPersistencia persistencia) {
        this.persistencia = persistencia;
    }

    @Override
    public List<RegistroNotificacion> leerTodos() {
        synchronized (BLOQUEO) {
            cargar();
            return new ArrayList<>(registros);
        }
    }

    @Override
    public void agregar(RegistroNotificacion registro) {
        synchronized (BLOQUEO) {
            cargar();
            registros.add(registro);
            sucio = true;
        }
    }

    @Override
    public boolean existe(int publicacionId) {
        synchronized (BLOQUEO) {
            cargar();
            return registros.stream()
                    .anyMatch(registro -> registro.getPublicacionId() == publicacionId);
        }
    }

    @Override
    public void eliminarPorPublicacionIds(Set<Integer> publicacionIds) {
        synchronized (BLOQUEO) {
            cargar();
            registros.removeIf(registro -> publicacionIds.contains(registro.getPublicacionId()));
            sucio = true;
        }
    }

    @Override
    public void guardar() {
        synchronized (BLOQUEO) {
            cargar();
            if (!sucio) {
                return;
            }
            persistencia.escribir(ARCHIVO, registros);
            sucio = false;
        }
    }

    private void cargar() {
        if (registros == null) {
            registros = new ArrayList<>(persistencia.leerLista(ARCHIVO, RegistroNotificacion.class));
        }
    }

    public static void reiniciar() {
        synchronized (BLOQUEO) {
            registros = null;
            sucio = false;
        }
    }
}
