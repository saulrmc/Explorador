package explorador.publicaciones.dao;

import explorador.comun.Persistible;
import explorador.publicaciones.modelo.Publicacion;

public interface PublicacionDAO extends Persistible<Publicacion, Integer> {
    boolean existePorOrigen(String fuente, String idOrigen);
}
