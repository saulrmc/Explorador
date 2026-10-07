package explorador.usuario.dao;

import explorador.data.JsonPersistencia;
import explorador.usuario.modelo.CategoriaAreaEnum;

import java.util.ArrayList;
import java.util.List;

public class CategoriaAreaDAOImpl implements CategoriaAreaDAO {

    private static final String ARCHIVO = "areas";
    private static final Object BLOQUEO = new Object();

    private static List<CategoriaAreaEnum> areas;
    private static boolean sucio;

    private final JsonPersistencia persistencia;

    public CategoriaAreaDAOImpl() {
        this(new JsonPersistencia("Usuario"));
    }

    CategoriaAreaDAOImpl(JsonPersistencia persistencia) {
        this.persistencia = persistencia;
    }

    static void reiniciar() {
        synchronized (BLOQUEO) {
            areas = null;
            sucio = false;
        }
    }

    @Override
    public List<CategoriaAreaEnum> leerTodos() {
        synchronized (BLOQUEO) {
            cargar();
            return new ArrayList<>(areas);
        }
    }

    @Override
    public boolean agregar(CategoriaAreaEnum categoria) {
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
    public boolean eliminar(CategoriaAreaEnum categoria) {
        synchronized (BLOQUEO) {
            cargar();
            boolean removed = areas.remove(categoria);
            if (removed) {
                sucio = true;
            }
            return removed;
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
            areas = new ArrayList<>(persistencia.leerLista(ARCHIVO, CategoriaAreaEnum.class));
            sucio = false;
        }
    }
}
