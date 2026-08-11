package explorador.usuario.bo;

import explorador.usuario.modelo.CategoriaArea;

import java.util.List;

public interface CategoriaAreaBO {
    List<CategoriaArea> listar();

    void agregar(CategoriaArea categoria);

    void eliminar(CategoriaArea categoria);

    List<CategoriaArea> categorias();
}
