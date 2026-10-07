
package explorador.publicaciones.dao;

import explorador.fuentes.modelo.PublicacionOriginal;


import java.util.List;

public interface PublicacionDAO {
    Integer crear(PublicacionOriginal modelo);

    boolean actualizar(PublicacionOriginal modelo);

    boolean eliminar(Integer id);

    PublicacionOriginal leer(Integer id);

    List<PublicacionOriginal> leerTodos();

    boolean existePorOrigen(String fuente, String idOrigen);

    void guardar();
}