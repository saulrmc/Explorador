package explorador.usuario.bo;

import explorador.usuario.modelo.Usuario;

public interface UsuarioBO {
    Usuario obtener();

    boolean actualizar(String nombre, String correo);
}
