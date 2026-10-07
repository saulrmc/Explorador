package explorador.usuario.bo;

import explorador.usuario.modelo.CategoriaAreaEnum;

import java.util.List;

public interface CategoriaAreaBO {
    List<CategoriaAreaEnum> listar();

    void agregar(CategoriaAreaEnum categoria);

    void eliminar(CategoriaAreaEnum categoria);

    List<CategoriaAreaEnum> categorias();
}
