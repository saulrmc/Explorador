package explorador.usuario.dao;

import explorador.data.JsonPersistencia;
import explorador.usuario.modelo.Usuario;

public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String ARCHIVO = "usuario";
    private final JsonPersistencia persistencia;

    public UsuarioDAOImpl() {
        this.persistencia = new JsonPersistencia("Usuario");
    }

    @Override
    public Usuario leer() {
        return persistencia.leer(ARCHIVO, Usuario.class);
    }

    @Override
    public void escribir(Usuario usuario) {
        persistencia.escribir(ARCHIVO, usuario);
    }
}
