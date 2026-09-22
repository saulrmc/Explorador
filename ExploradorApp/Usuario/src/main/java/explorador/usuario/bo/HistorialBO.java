package explorador.usuario.bo;

import explorador.usuario.modelo.PublicacionConsultada;

import java.util.List;
import java.util.Set;

public interface HistorialBO {
    List<PublicacionConsultada> listar();

    void registrar(int publicacionId, String titulo);

    void limpiar();

    void limpiarPorPublicacionIds(Set<Integer> publicacionIds);
}
