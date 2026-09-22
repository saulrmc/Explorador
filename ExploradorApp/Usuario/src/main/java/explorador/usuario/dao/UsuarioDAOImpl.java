package explorador.usuario.dao;

import explorador.data.JsonPersistencia;
import explorador.usuario.modelo.Usuario;

public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String ARCHIVO = "usuario";
    private final JsonPersistencia persistencia;

    public UsuarioDAOImpl() {
        this.persistencia = new JsonPersistencia("Usuario");
    }

    // A diferencia de otros DAOs del modulo, no se cachea el estado en memoria:
    // al existir un unico usuario, releer el archivo en cada operacion no es un
    // problema de rendimiento y mantiene simple la logica.
    @Override
    public Usuario leer() {
        return persistencia.leer(ARCHIVO, Usuario.class);
    }

    @Override
    public void escribir(Usuario usuario) {
        persistencia.escribir(ARCHIVO, usuario);
    }
}
