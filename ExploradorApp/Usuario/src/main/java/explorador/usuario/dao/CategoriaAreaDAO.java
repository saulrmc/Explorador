package explorador.usuario.dao;

import explorador.usuario.modelo.Categoria;
import explorador.usuario.modelo.CategoriaAreaEnum;

import java.util.List;

public interface CategoriaAreaDAO {
    List<CategoriaAreaEnum> leerTodos();

    Categoria obtenerPrincipal();

    boolean agregar(CategoriaAreaEnum categoria);

    boolean marcarPrincipal(int id);

    boolean eliminar(CategoriaAreaEnum categoria);
}
