package explorador.usuario.bo;

import explorador.usuario.dao.UsuarioDAO;
import explorador.usuario.dao.UsuarioDAOImpl;
import explorador.usuario.modelo.Usuario;

import java.util.Objects;

public class UsuarioBOImpl implements UsuarioBO {

    private final UsuarioDAO usuarioDao;

    public UsuarioBOImpl() {
        this.usuarioDao = new UsuarioDAOImpl();
    }

    @Override
    public Usuario obtener() {
        return usuarioDao.leer();
    }

    @Override
    public void actualizar(String nombre, String correo) {
        Objects.requireNonNull(nombre, "El nombre es obligatorio");
        if (nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }
        Objects.requireNonNull(correo, "El correo es obligatorio");
        if (correo.isBlank()) {
            throw new IllegalArgumentException("El correo no puede estar vacio");
        }

        Usuario usuario = usuarioDao.leer();
        if (usuario == null) {
            usuario = new Usuario();
            usuario.setId(1);
        }
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuarioDao.escribir(usuario);
    }
}
