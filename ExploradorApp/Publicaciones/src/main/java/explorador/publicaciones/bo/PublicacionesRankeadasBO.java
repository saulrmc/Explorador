package explorador.publicaciones.bo;

import explorador.publicaciones.modelo.Publicacion;
import explorador.publicaciones.modelo.PublicacionRankeada;

import java.util.List;
import java.util.Set;

public interface PublicacionesRankeadasBO {
    List<PublicacionRankeada> listarRankeadas(Set<String> categorias, int limite);

    List<PublicacionRankeada> rankear(List<Publicacion> publicaciones, Set<String> categorias);
}
