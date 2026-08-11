package explorador.usuario.dao;

import explorador.usuario.modelo.CategoriaArea;

import java.util.List;

public interface CategoriaAreaDAO {
    List<CategoriaArea> leerTodos();

    boolean agregar(CategoriaArea categoria);

    boolean eliminar(CategoriaArea categoria);

    void guardar();
}
