package explorador.usuario.bo;

import explorador.usuario.modelo.AreaInteres;
import explorador.usuario.modelo.CategoriaArea;

import java.util.List;

public interface AreaInteresBO {
    List<AreaInteres> listar();

    AreaInteres obtener(int id);

    AreaInteres guardar(AreaInteres area, explorador.comun.Estado estado);

    void eliminar(int id);

    List<CategoriaArea> categorias();
}
