package explorador.biblioteca.dao;

import explorador.biblioteca.modelo.GrafoTematica;
import explorador.biblioteca.modelo.PublicacionGuardada;
import explorador.data.JsonPersistencia;

import java.util.List;

public class NodoTemaDAOImpl implements NodoTemaDAO {
    private static final String ARCHIVO_GRAFO = "grafo";
    private final JsonPersistencia persistencia;
    public NodoTemaDAOImpl() {
        this.persistencia = new JsonPersistencia("Grafo");
    }
    @Override
    public GrafoTematica leerGrafo() {
        return persistencia.leer(ARCHIVO_GRAFO, GrafoTematica.class, new GrafoTematica());
    }
    @Override
    public void escribirGrafo(GrafoTematica grafo) {
        persistencia.escribir(ARCHIVO_GRAFO, grafo);
    }
    @Override
    public List<PublicacionGuardada> leerGuardadas() {
        return persistencia.leerLista(ARCHIVO_GRAFO, PublicacionGuardada.class);
    }

    @Override
    public void escribirGuardadas(List<PublicacionGuardada> guardadas) {
        persistencia.escribir(ARCHIVO_GRAFO, guardadas);
    }
}
