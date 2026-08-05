package explorador.comun;

import java.util.List;

public interface Gestionable<M> {
    void guardar(M modelo, Estado estado);

    List<M> listar();

    M obtener(int id);

    void eliminar(int id);
}
