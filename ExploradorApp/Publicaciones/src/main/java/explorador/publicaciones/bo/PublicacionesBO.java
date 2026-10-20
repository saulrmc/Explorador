package explorador.publicaciones.bo;

import explorador.fuentes.modelo.PublicacionOriginal;
import explorador.publicaciones.modelo.Publicacion;

import java.util.List;

public interface PublicacionesBO {
    List<Publicacion> registrar(List<PublicacionOriginal> originales);

    List<Publicacion> listar();

    Publicacion obtener(int id);

    List<Publicacion> listarRelacionadas(int id, int limite);
}
