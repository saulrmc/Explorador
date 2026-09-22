package explorador.usuario.bo;

import explorador.usuario.dao.CategoriaAreaDAO;
import explorador.usuario.dao.CategoriaAreaDAOImpl;
import explorador.usuario.modelo.CategoriaArea;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CategoriaAreaBOImpl implements CategoriaAreaBO {

    private final CategoriaAreaDAO areaDao;

    public CategoriaAreaBOImpl() {
        this.areaDao = new CategoriaAreaDAOImpl();
    }

    @Override
    public List<CategoriaArea> listar() {
        return areaDao.leerTodos();
    }

    @Override
    public void agregar(CategoriaArea categoria) {
        validarCategoria(categoria);
        if (areaDao.agregar(categoria)) {
            areaDao.guardar();
        }
    }

    @Override
    public void eliminar(CategoriaArea categoria) {
        validarCategoria(categoria);
        if (!areaDao.eliminar(categoria)) {
            throw new IllegalStateException("La categoria no esta seleccionada: " + categoria);
        }
        areaDao.guardar();
    }

    @Override
    public List<CategoriaArea> categorias() {
        return Arrays.asList(CategoriaArea.values());
    }

    private void validarCategoria(CategoriaArea categoria) {
        Objects.requireNonNull(categoria, "La categoria es obligatoria");
    }
}
