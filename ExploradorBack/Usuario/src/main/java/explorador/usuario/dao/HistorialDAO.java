package explorador.usuario.dao;

import explorador.usuario.modelo.PublicacionConsultada;

import java.util.List;

public interface HistorialDAO {
    List<PublicacionConsultada> leerTodos();

    void reemplazarTodos(List<PublicacionConsultada> registros);

    void guardar();
}
