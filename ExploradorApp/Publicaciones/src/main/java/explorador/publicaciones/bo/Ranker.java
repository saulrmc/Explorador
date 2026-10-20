package explorador.publicaciones.bo;

import explorador.publicaciones.modelo.Publicacion;
import explorador.publicaciones.modelo.PublicacionRankeada;

import java.util.List;
import java.util.Set;

public interface Ranker {
    List<PublicacionRankeada> ordenar(List<Publicacion> publicaciones, Set<String> categorias);
}
