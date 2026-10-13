
package explorador.publicaciones.dao;

import explorador.fuentes.modelo.PublicacionOriginal;
import explorador.publicaciones.modelo.Publicacion;


import java.util.List;

public interface PublicacionDAO {
    Publicacion crear(PublicacionOriginal modelo);

    boolean actualizar(Publicacion modelo);

    boolean eliminar(Integer id);

    Publicacion leer(Integer id);

    List<Publicacion> leerTodos();

    boolean existePorUrl(String url);
}