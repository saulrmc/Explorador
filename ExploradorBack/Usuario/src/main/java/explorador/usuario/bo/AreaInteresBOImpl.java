package explorador.usuario.bo;

import explorador.usuario.dao.AreaInteresDAO;
import explorador.usuario.dao.AreaInteresDAOImpl;
import explorador.usuario.modelo.AreaInteres;
import explorador.usuario.modelo.CategoriaArea;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AreaInteresBOImpl implements AreaInteresBO {

    private final AreaInteresDAO areaDao;

    public AreaInteresBOImpl() {
        this.areaDao = new AreaInteresDAOImpl();
    }

    @Override
    public List<AreaInteres> listar() {
        return areaDao.leerTodos();
    }

    @Override
    public AreaInteres obtener(int id) {
        validarIdPositivo(id);
        return areaDao.leer(id);
    }

    @Override
    public AreaInteres crear(AreaInteres area) {
        validarArea(area);
        areaDao.crear(area);
        areaDao.guardar();
        return area;
    }

    @Override
    public AreaInteres actualizar(AreaInteres area) {
        validarArea(area);
        validarIdPositivo(area.getId());
        if (!areaDao.actualizar(area)) {
            throw new IllegalStateException("No se pudo actualizar el area con id: " + area.getId());
        }
        areaDao.guardar();
        return area;
    }

    @Override
    public void eliminar(int id) {
        validarIdPositivo(id);
        if (!areaDao.eliminar(id)) {
            throw new IllegalStateException("No se pudo eliminar el area con id: " + id);
        }
        areaDao.guardar();
    }

    @Override
    public List<CategoriaArea> categorias() {
        return Arrays.asList(CategoriaArea.values());
    }

    private void validarArea(AreaInteres area) {
        Objects.requireNonNull(area, "El area de interes es obligatoria");
        Objects.requireNonNull(area.getNombre(), "El nombre del area es obligatorio");
        if (area.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del area no puede estar vacio");
        }
        Objects.requireNonNull(area.getCategoria(), "La categoria del area es obligatoria");
    }

    private void validarIdPositivo(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El id debe ser mayor a 0");
        }
    }
}
