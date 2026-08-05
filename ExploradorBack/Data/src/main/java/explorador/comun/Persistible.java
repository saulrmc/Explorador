package explorador.comun;

import java.util.List;

public interface Persistible<M, I> {
    I crear(M modelo);

    boolean actualizar(M modelo);

    boolean eliminar(I id);

    M leer(I id);

    List<M> leerTodos();
}
