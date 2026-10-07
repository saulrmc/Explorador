package explorador.usuario.bo;

import explorador.usuario.dao.CategoriaAreaDAO;
import explorador.usuario.dao.CategoriaAreaDAOImpl;
import explorador.usuario.modelo.CategoriaAreaEnum;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CategoriaAreaBOImpl implements CategoriaAreaBO {

    private final CategoriaAreaDAO areaDao;

    public CategoriaAreaBOImpl() {
        this.areaDao = new CategoriaAreaDAOImpl();
    }

    @Override
    public List<CategoriaAreaEnum> listar() {
        return areaDao.leerTodos();
    }

    @Override
    public void agregar(CategoriaAreaEnum categoria) {
        validarCategoria(categoria);
        if (areaDao.agregar(categoria)) {
            areaDao.guardar();
        }
    }

    @Override
    public void eliminar(CategoriaAreaEnum categoria) {
        validarCategoria(categoria);
        if (!areaDao.eliminar(categoria)) {
            throw new IllegalStateException("La categoria no esta seleccionada: " + categoria);
        }
        areaDao.guardar();
    }

    @Override
    public List<CategoriaAreaEnum> categorias() {
        return Arrays.asList(CategoriaAreaEnum.values());
    }

    private void validarCategoria(CategoriaAreaEnum categoria) {
        Objects.requireNonNull(categoria, "La categoria es obligatoria");
    }
}
