package explorador.usuario.dao;

import explorador.data.JsonPersistencia;
import explorador.usuario.modelo.AreaInteres;

import java.util.ArrayList;
import java.util.List;

public class AreaInteresDAOImpl implements AreaInteresDAO {

    private static final String ARCHIVO = "areas";
    private final JsonPersistencia persistencia;

    public AreaInteresDAOImpl() {
        this.persistencia = new JsonPersistencia("Usuario");
    }

    @Override
    public Integer crear(AreaInteres modelo) {
        List<AreaInteres> areas = leerTodos();
        int id = areas.stream().mapToInt(AreaInteres::getId).max().orElse(0) + 1;
        modelo.setId(id);
        areas.add(modelo);
        persistencia.escribir(ARCHIVO, areas);
        return id;
    }

    @Override
    public boolean actualizar(AreaInteres modelo) {
        List<AreaInteres> areas = leerTodos();
        for (int i = 0; i < areas.size(); i++) {
            if (areas.get(i).getId() == modelo.getId()) {
                areas.set(i, modelo);
                persistencia.escribir(ARCHIVO, areas);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean eliminar(Integer id) {
        List<AreaInteres> areas = leerTodos();
        boolean eliminado = areas.removeIf(area -> area.getId() == id);
        if (eliminado) {
            persistencia.escribir(ARCHIVO, areas);
        }
        return eliminado;
    }

    @Override
    public AreaInteres leer(Integer id) {
        return leerTodos().stream()
                .filter(area -> area.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<AreaInteres> leerTodos() {
        return persistencia.leerLista(ARCHIVO, AreaInteres.class);
    }
}
