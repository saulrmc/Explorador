package explorador.usuario.dao;

import explorador.data.JsonPersistencia;
import explorador.usuario.modelo.CategoriaArea;

import java.util.ArrayList;
import java.util.List;

public class CategoriaAreaDAOImpl implements CategoriaAreaDAO {

    private static final String ARCHIVO = "areas";
    private static final Object BLOQUEO = new Object();

    private static List<CategoriaArea> areas;
    private static boolean sucio;

    private final JsonPersistencia persistencia;

    public CategoriaAreaDAOImpl() {
        this(new JsonPersistencia("Usuario"));
    }

    CategoriaAreaDAOImpl(JsonPersistencia persistencia) {
        this.persistencia = persistencia;
    }

    @Override
    public List<CategoriaArea> leerTodos() {
        synchronized (BLOQUEO) {
            cargar();
            return new ArrayList<>(areas);
        }
    }

    @Override
    public boolean agregar(CategoriaArea categoria) {
        synchronized (BLOQUEO) {
            cargar();
            if (areas.contains(categoria)) {
                return false;
            }
            areas.add(categoria);
            sucio = true;
            return true;
        }
    }

    @Override
    public boolean eliminar(CategoriaArea categoria) {
        synchronized (BLOQUEO) {
            cargar();
            boolean eliminado = areas.remove(categoria);
            if (eliminado) {
                sucio = true;
            }
            return eliminado;
        }
    }

    @Override
    public void guardar() {
        synchronized (BLOQUEO) {
            cargar();
            if (!sucio) {
                return;
            }
            persistencia.escribir(ARCHIVO, areas);
            sucio = false;
        }
    }

    private void cargar() {
        if (areas == null) {
            List<CategoriaArea> persistidas = persistencia.leerLista(ARCHIVO, CategoriaArea.class);
            areas = new ArrayList<>();
            for (CategoriaArea categoria : persistidas) {
                if (categoria != null && !areas.contains(categoria)) {
                    areas.add(categoria);
                }
            }
        }
    }

    static void reiniciar() {
        synchronized (BLOQUEO) {
            areas = null;
            sucio = false;
        }
    }
}
