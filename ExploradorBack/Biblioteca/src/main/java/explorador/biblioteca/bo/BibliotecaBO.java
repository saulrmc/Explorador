package explorador.biblioteca.bo;

import explorador.biblioteca.modelo.PublicacionGuardada;
import explorador.publicaciones.modelo.Publicacion;

import java.util.List;

public interface BibliotecaBO {
    PublicacionGuardada guardar(Publicacion publicacion);

    List<PublicacionGuardada> listarGuardadas();

    void eliminar(int id);

    List<PublicacionGuardada> listarPorTema(String tema);
}
