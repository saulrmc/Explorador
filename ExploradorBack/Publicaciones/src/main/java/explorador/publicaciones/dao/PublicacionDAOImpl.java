package explorador.publicaciones.dao;

import explorador.data.JsonPersistencia;
import explorador.fuentes.modelo.PublicacionOriginal;
import explorador.publicaciones.modelo.Publicacion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PublicacionDAOImpl implements PublicacionDAO {

    private static final String ARCHIVO = "publicaciones";
    private static final Object BLOQUEO = new Object();

    private static List<Publicacion> publicaciones;
    private static int ultimoId;
    private static boolean sucio;

    private final JsonPersistencia persistencia;

    public PublicacionDAOImpl() {
        this(new JsonPersistencia("Publicaciones"));
    }

    PublicacionDAOImpl(JsonPersistencia persistencia) {
        this.persistencia = persistencia;
    }

    @Override
    public Integer crear(Publicacion modelo) {
        synchronized (BLOQUEO) {
            cargar();
            ultimoId++;
            modelo.setId(ultimoId);
            publicaciones.add(modelo);
            sucio = true;
            return ultimoId;
        }
    }

    @Override
    public boolean actualizar(Publicacion modelo) {
        synchronized (BLOQUEO) {
            cargar();
            int indice = indiceDe(modelo.getId());
            if (indice < 0) {
                return false;
            }
            publicaciones.set(indice, modelo);
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
            publicaciones.remove(indice);
            sucio = true;
            return true;
        }
    }

    @Override
    public Publicacion leer(Integer id) {
        synchronized (BLOQUEO) {
            cargar();
            int indice = indiceDe(id);
            return indice < 0 ? null : publicaciones.get(indice);
        }
    }

    @Override
    public List<Publicacion> leerTodos() {
        synchronized (BLOQUEO) {
            cargar();
            return new ArrayList<>(publicaciones);
        }
    }

    @Override
    public boolean existePorOrigen(String fuente, String idOrigen) {
        synchronized (BLOQUEO) {
            cargar();
            return publicaciones.stream().anyMatch(publicacion -> {
                PublicacionOriginal original = publicacion.getOriginal();
                return original != null
                        && fuente != null && fuente.equals(original.getFuente())
                        && idOrigen != null && idOrigen.equals(original.getIdOrigen());
            });
        }
    }

    @Override
    public void guardar() {
        synchronized (BLOQUEO) {
            cargar();
            if (!sucio) {
                return;
            }
            persistencia.escribir(ARCHIVO, publicaciones);
            sucio = false;
        }
    }

    private void cargar() {
        if (publicaciones == null) {
            publicaciones = new ArrayList<>(persistencia.leerLista(ARCHIVO, Publicacion.class));
            publicaciones.sort(Comparator.comparingInt(Publicacion::getId));
            ultimoId = publicaciones.stream()
                    .mapToInt(Publicacion::getId)
                    .max().orElse(0);
        }
    }

    private int indiceDe(int id) {
        int bajo = 0;
        int alto = publicaciones.size() - 1;
        while (bajo <= alto) {
            int medio = (bajo + alto) >>> 1;
            int idMedio = publicaciones.get(medio).getId();
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
            publicaciones = null;
            ultimoId = 0;
            sucio = false;
        }
    }
}
