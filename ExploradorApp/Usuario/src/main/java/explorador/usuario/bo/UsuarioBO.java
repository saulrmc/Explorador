package explorador.usuario.bo;

import explorador.usuario.modelo.CategoriaGenerica;

import java.util.List;

public interface UsuarioBO {
    // Devuelve las categorias genericas que el usuario tiene como interes (seleccion guardada)
    List<CategoriaGenerica> listarIntereses();

    boolean actualizar(String nombre, String correo);
}
