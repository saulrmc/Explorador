package explorador.usuario.bo;

import explorador.usuario.modelo.Categoria;
import explorador.usuario.modelo.CategoriaGenerica;

import java.util.List;

public interface CategoriaGenericaBO {
    List<Categoria> listar();

    void agregar(CategoriaGenerica categoria);

    void eliminar(String categoria);

    List<CategoriaGenerica> categorias();

    Categoria obtenerPrincipal();

    void marcarPrincipal(int id);
}
