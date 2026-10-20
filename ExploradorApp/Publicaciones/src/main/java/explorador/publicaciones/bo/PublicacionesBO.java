
package explorador.publicaciones.bo;

import explorador.fuentes.modelo.PublicacionOriginal;


import explorador.publicaciones.modelo.Publicacion;
import explorador.publicaciones.modelo.PublicacionRankeada;

import java.util.List;
import java.util.Set;

public interface PublicacionesBO {
    List<PublicacionOriginal> registrarBrutas(List<PublicacionOriginal> originales);

    List<PublicacionRankeada> listarLimitadas(Set<String> categorias, int limite);

    List<PublicacionRankeada> rankear(List<Publicacion> publicaciones, Set<String> categorias);

    List<Publicacion> listar();

    Publicacion obtener(int id);

    List<Publicacion> listarRelacionadas(int id, int limite);
}