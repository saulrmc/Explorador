package explorador.publicaciones.dao;

import explorador.data.JsonPersistencia;
import explorador.publicaciones.modelo.Publicacion;

import java.util.List;

public class PublicacionDAOImpl implements PublicacionDAO {

    private static final String ARCHIVO = "publicaciones";
    private final JsonPersistencia persistencia;

    public PublicacionDAOImpl() {
        this.persistencia = new JsonPersistencia("Publicaciones");
    }

    @Override
    public Integer crear(Publicacion modelo) {
        List<Publicacion> publicaciones = leerTodos();
        int id = publicaciones.stream().mapToInt(Publicacion::getId).max().orElse(0) + 1;
        modelo.setId(id);
        publicaciones.add(modelo);
        persistencia.escribir(ARCHIVO, publicaciones);
        return id;
    }

    @Override
    public boolean actualizar(Publicacion modelo) {
        List<Publicacion> publicaciones = leerTodos();
        for (int i = 0; i < publicaciones.size(); i++) {
            if (publicaciones.get(i).getId() == modelo.getId()) {
                publicaciones.set(i, modelo);
                persistencia.escribir(ARCHIVO, publicaciones);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean eliminar(Integer id) {
        List<Publicacion> publicaciones = leerTodos();
        boolean eliminado = publicaciones.removeIf(publicacion -> publicacion.getId() == id);
        if (eliminado) {
            persistencia.escribir(ARCHIVO, publicaciones);
        }
        return eliminado;
    }

    @Override
    public Publicacion leer(Integer id) {
        return leerTodos().stream()
                .filter(publicacion -> publicacion.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Publicacion> leerTodos() {
        return persistencia.leerLista(ARCHIVO, Publicacion.class);
    }

    @Override
    public boolean existePorOrigen(String fuente, String idOrigen) {
        return leerTodos().stream()
                .anyMatch(publicacion -> publicacion.getFuente().equals(fuente)
                        && publicacion.getIdOrigen().equals(idOrigen));
    }
}
