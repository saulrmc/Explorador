package explorador.usuario.bo;

import explorador.usuario.dao.CategoriaGenericaDAO;
import explorador.usuario.dao.CategoriaGenericaDAOImpl;
import explorador.usuario.modelo.Categoria;
import explorador.usuario.modelo.CategoriaGenerica;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CategoriaGenericaBOImpl implements CategoriaGenericaBO {

    private final CategoriaGenericaDAO categoriaDao;

    public CategoriaGenericaBOImpl() {
        this(new CategoriaGenericaDAOImpl());
    }

    public CategoriaGenericaBOImpl(CategoriaGenericaDAO categoriaDao) {
        this.categoriaDao = categoriaDao;
    }

    @Override
    public List<Categoria> listar() {
        return this.categoriaDao.leerTodos();
    }

    @Override
    public void agregar(CategoriaGenerica categoria) {
        validarCategoria(categoria);
        categoriaDao.agregar(categoria);
    }

    @Override
    public void eliminar(String categoria) {
        categoriaDao.eliminar(categoria);
    }

    @Override
    public List<CategoriaGenerica> categorias() {
        return Arrays.asList(CategoriaGenerica.values());
    }

    @Override
    public Categoria obtenerPrincipal() {
        return categoriaDao.obtenerPrincipal();
    }

    @Override
    public void marcarPrincipal(int id) {
        if (!categoriaDao.marcarPrincipal(id)) {
            throw new IllegalArgumentException("No existe una categoria con id: " + id);
        }
    }

    private void validarCategoria(CategoriaGenerica categoria) {
        Objects.requireNonNull(categoria, "La categoria es obligatoria");
    }
}
