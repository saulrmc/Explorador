package explorador.usuario.bo;

import explorador.usuario.dao.UsuarioDAO;
import explorador.usuario.dao.UsuarioDAOImpl;
import explorador.usuario.modelo.Usuario;

import java.util.Objects;

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

    @Override
    public Usuario obtener() {
        Usuario usuario = usuarioDao.leer();
        if (usuario != null) {
            usuario.setIntereses(categoriaBO.listar());
        }
        return usuario;
    }

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
