package explorador.publicaciones.bo;

import explorador.fuentes.modelo.PublicacionOriginal;
import explorador.publicaciones.modelo.DefinicionConcepto;
import explorador.publicaciones.modelo.Publicacion;

import java.util.List;
import java.util.Set;

public interface PublicacionesBO {
    List<Publicacion> registrarBrutas(List<PublicacionOriginal> originales);

    List<Publicacion> listarLimitadas(Set<String> categorias, int limite);

    List<Publicacion> rankear(List<Publicacion> publicaciones, Set<String> categorias);

    List<Publicacion> listar();

    Publicacion obtener(int id);

    List<Publicacion> listarRelacionadas(int id, int limite);

    Set<Integer> podar(Set<Integer> protegidos);

    DefinicionConcepto definirConcepto(String concepto);
}
