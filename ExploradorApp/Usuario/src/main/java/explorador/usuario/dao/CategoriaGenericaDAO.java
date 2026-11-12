package explorador.usuario.dao;

import explorador.usuario.modelo.Categoria;
import explorador.usuario.modelo.CategoriaGenerica;

import java.util.List;

public interface CategoriaGenericaDAO {
    List<Categoria> leerTodos();

    Categoria obtenerPrincipal();

    boolean agregar(CategoriaGenerica categoria);

    boolean marcarPrincipal(int id);

    boolean eliminar(String categoria);
}
