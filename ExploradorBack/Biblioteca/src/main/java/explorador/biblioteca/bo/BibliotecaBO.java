package explorador.biblioteca.bo;

import explorador.biblioteca.modelo.EntradaBiblioteca;
import explorador.publicaciones.modelo.Publicacion;

import java.util.List;

public interface BibliotecaBO {
    EntradaBiblioteca guardar(Publicacion publicacion);

    List<EntradaBiblioteca> listarGuardadas();

    EntradaBiblioteca obtener(int id);

    void eliminar(int id);

    List<EntradaBiblioteca> listarPorTema(String tema);
}
