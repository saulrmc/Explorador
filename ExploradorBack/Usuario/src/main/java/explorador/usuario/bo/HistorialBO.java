package explorador.usuario.bo;

import explorador.usuario.modelo.PublicacionConsultada;

import java.util.List;

public interface HistorialBO {
    List<PublicacionConsultada> listar();

    void registrar(int publicacionId, String titulo);

    void limpiar();
}
