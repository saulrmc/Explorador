package explorador.usuario.dao;

import explorador.usuario.modelo.CategoriaAreaEnum;

import java.util.List;

public interface CategoriaAreaDAO {
    List<CategoriaAreaEnum> leerTodos();

    boolean agregar(CategoriaAreaEnum categoria);

    boolean eliminar(CategoriaAreaEnum categoria);

    void guardar();
}
