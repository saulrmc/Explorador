package explorador.usuario.dao;

import explorador.data.JsonPersistencia;
import explorador.usuario.modelo.AreaInteres;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AreaInteresDAOImpl implements AreaInteresDAO {

    private static final String ARCHIVO = "areas";
    private static final Object BLOQUEO = new Object();

    private static List<AreaInteres> areas;
    private static int ultimoId;
    private static boolean sucio;

    private final JsonPersistencia persistencia;

    public AreaInteresDAOImpl() {
        this(new JsonPersistencia("Usuario"));
    }

    AreaInteresDAOImpl(JsonPersistencia persistencia) {
        this.persistencia = persistencia;
    }

    @Override
    public Integer crear(AreaInteres modelo) {
        synchronized (BLOQUEO) {
            cargar();
            ultimoId++;
            modelo.setId(ultimoId);
            areas.add(modelo);
            sucio = true;
            return ultimoId;
        }
    }

    @Override
    public boolean actualizar(AreaInteres modelo) {
        synchronized (BLOQUEO) {
            cargar();
            int indice = indiceDe(modelo.getId());
            if (indice < 0) {
                return false;
            }
            areas.set(indice, modelo);
            sucio = true;
            return true;
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        synchronized (BLOQUEO) {
            cargar();
            int indice = indiceDe(id);
            if (indice < 0) {
                return false;
            }
            areas.remove(indice);
            sucio = true;
            return true;
        }
    }

    @Override
    public AreaInteres leer(Integer id) {
        synchronized (BLOQUEO) {
            cargar();
            int indice = indiceDe(id);
            return indice < 0 ? null : areas.get(indice);
        }
    }

    @Override
    public List<AreaInteres> leerTodos() {
        synchronized (BLOQUEO) {
            cargar();
            return new ArrayList<>(areas);
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
            areas = new ArrayList<>(persistencia.leerLista(ARCHIVO, AreaInteres.class));
            areas.sort(Comparator.comparingInt(AreaInteres::getId));
            ultimoId = areas.stream()
                    .mapToInt(AreaInteres::getId)
                    .max().orElse(0);
        }
    }

    private int indiceDe(int id) {
        int bajo = 0;
        int alto = areas.size() - 1;
        while (bajo <= alto) {
            int medio = (bajo + alto) >>> 1;
            int idMedio = areas.get(medio).getId();
            if (idMedio < id) {
                bajo = medio + 1;
            } else if (idMedio > id) {
                alto = medio - 1;
            } else {
                return medio;
            }
        }
        return -(bajo + 1);
    }

    static void reiniciar() {
        synchronized (BLOQUEO) {
            areas = null;
            ultimoId = 0;
            sucio = false;
        }
    }
}
