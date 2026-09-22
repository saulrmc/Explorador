package explorador.usuario.dao;

import explorador.usuario.modelo.Usuario;

public interface UsuarioDAO {
    Usuario leer();

    void escribir(Usuario usuario);
}
