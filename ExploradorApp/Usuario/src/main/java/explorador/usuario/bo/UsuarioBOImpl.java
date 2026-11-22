package explorador.usuario.bo;

import explorador.usuario.dao.UsuarioDAO;
import explorador.usuario.dao.UsuarioDAOImpl;
import explorador.usuario.modelo.Categoria;
import explorador.usuario.modelo.CategoriaGenerica;
import explorador.usuario.modelo.Usuario;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UsuarioBOImpl implements UsuarioBO {

    private final UsuarioDAO usuarioDao;
    private final CategoriaGenericaBO categoriaBO;

    public UsuarioBOImpl() {
        this(new UsuarioDAOImpl(), new CategoriaGenericaBOImpl());
    }

    public UsuarioBOImpl(UsuarioDAO usuarioDAO) {
        this(usuarioDAO, new CategoriaGenericaBOImpl());
    }

    public UsuarioBOImpl(UsuarioDAO usuarioDao, CategoriaGenericaBO categoriaBO) {
        this.usuarioDao = usuarioDao;
        this.categoriaBO = categoriaBO;
    }

    // Devuelve las categorias genericas del usuario; si no hay usuario aun, usa id 1 como base
    @Override
    public List<CategoriaGenerica> listarIntereses() {
        Usuario usuario = usuarioDao.leer();
        if (usuario == null) {
            usuario = new Usuario();
            usuario.setId(1);
        }
        List<Categoria> seleccion = categoriaBO.listar();
        return seleccion.stream()
                .map(Categoria::getNombre)
                .collect(Collectors.toList());
    }

    // Actualiza nombre y correo del usuario (id 1). Si vienen vacios o nulos, no hace nada
    @Override
    public boolean actualizar(String nombre, String correo) {
        Objects.requireNonNull(nombre, "El nombre es obligatorio");
        Objects.requireNonNull(correo, "El correo es obligatorio");

        String nombreLimpio = nombre.trim();
        String correoLimpio = correo.trim();
        if (nombreLimpio.isEmpty() || correoLimpio.isEmpty()) {
            return false;
        }

        Usuario usuario = usuarioDao.leer();
        if (usuario == null) {
            usuario = new Usuario();
            usuario.setId(1);
        }
        usuario.setNombre(nombreLimpio);
        usuario.setCorreo(correoLimpio);
        usuarioDao.escribir(usuario);
        return true;
    }
}
