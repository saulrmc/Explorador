package explorador.usuario.dao;

import explorador.data.JsonPersistencia;
import explorador.usuario.modelo.PublicacionConsultada;

import java.util.ArrayList;
import java.util.List;

public class HistorialDAOImpl implements HistorialDAO {

    private static final String ARCHIVO = "historial";
    private static final Object BLOQUEO = new Object();

    private static List<PublicacionConsultada> registros;
    private static boolean sucio;

    private final JsonPersistencia persistencia;

    public HistorialDAOImpl() {
        this(new JsonPersistencia("Usuario"));
    }

    HistorialDAOImpl(JsonPersistencia persistencia) {
        this.persistencia = persistencia;
    }

    @Override
    public List<PublicacionConsultada> leerTodos() {
        synchronized (BLOQUEO) {
            cargar();
            return new ArrayList<>(registros);
        }
    }

    @Override
    public void reemplazarTodos(List<PublicacionConsultada> nuevosRegistros) {
        synchronized (BLOQUEO) {
            cargar();
            registros = nuevosRegistros == null ? new ArrayList<>() : new ArrayList<>(nuevosRegistros);
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
            registros = new ArrayList<>(persistencia.leerLista(ARCHIVO, PublicacionConsultada.class));
        }
    }

    static void reiniciar() {
        synchronized (BLOQUEO) {
            registros = null;
            sucio = false;
        }
    }
}
