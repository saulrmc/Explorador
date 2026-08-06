package explorador.usuario.bo;

import explorador.usuario.modelo.AreaInteres;
import explorador.usuario.modelo.CategoriaArea;

import java.util.List;

public interface AreaInteresBO {
    List<AreaInteres> listar();

    AreaInteres obtener(int id);

    AreaInteres crear(AreaInteres area);

    AreaInteres actualizar(AreaInteres area);

    void eliminar(int id);

    List<CategoriaArea> categorias();
}
