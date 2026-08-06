package explorador.publicaciones.dao;

import explorador.publicaciones.modelo.Publicacion;

import java.util.List;

public interface PublicacionDAO {
    Integer crear(Publicacion modelo);

    boolean actualizar(Publicacion modelo);

    boolean eliminar(Integer id);

    Publicacion leer(Integer id);

    List<Publicacion> leerTodos();

    boolean existePorOrigen(String fuente, String idOrigen);
}
