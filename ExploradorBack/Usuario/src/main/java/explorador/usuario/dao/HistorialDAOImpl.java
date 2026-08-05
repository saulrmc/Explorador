package explorador.usuario.dao;

import explorador.data.JsonPersistencia;
import explorador.usuario.modelo.PublicacionConsultada;

import java.util.List;

public class HistorialDAOImpl implements HistorialDAO {

    private static final String ARCHIVO = "historial";
    private final JsonPersistencia persistencia;

    public HistorialDAOImpl() {
        this.persistencia = new JsonPersistencia("Usuario");
    }

    @Override
    public List<PublicacionConsultada> leerTodos() {
        return persistencia.leerLista(ARCHIVO, PublicacionConsultada.class);
    }

    @Override
    public void reemplazarTodos(List<PublicacionConsultada> registros) {
        persistencia.escribir(ARCHIVO, registros);
    }
}
