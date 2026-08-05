package explorador.publicaciones.bo;

import explorador.fuentes.modelo.PublicacionBruta;
import explorador.publicaciones.modelo.Publicacion;

import java.util.List;
import java.util.Set;

public interface PublicacionesBO {
    List<Publicacion> registrarBrutas(List<PublicacionBruta> brutas);

    List<Publicacion> listarLimitadas(Set<String> keywords, int limite);

    List<Publicacion> rankear(List<Publicacion> publicaciones, Set<String> keywords);

    List<Publicacion> listar();

    Publicacion obtener(int id);

    List<Publicacion> listarRelacionadas(int id, int limite);
}
