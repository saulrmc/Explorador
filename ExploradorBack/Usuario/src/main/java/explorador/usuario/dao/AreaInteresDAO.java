package explorador.usuario.dao;

import explorador.usuario.modelo.AreaInteres;

import java.util.List;

public interface AreaInteresDAO {
    Integer crear(AreaInteres modelo);

    boolean actualizar(AreaInteres modelo);

    boolean eliminar(Integer id);

    AreaInteres leer(Integer id);

    List<AreaInteres> leerTodos();
}
