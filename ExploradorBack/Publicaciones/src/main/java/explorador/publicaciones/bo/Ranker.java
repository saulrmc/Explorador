package explorador.publicaciones.bo;

import explorador.publicaciones.modelo.Publicacion;

import java.util.List;
import java.util.Set;

public interface Ranker {
    List<Publicacion> ordenar(List<Publicacion> publicaciones, Set<String> keywords);
}
