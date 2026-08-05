package explorador.biblioteca.dao;

import explorador.biblioteca.modelo.GrafoTematica;
import explorador.biblioteca.modelo.PublicacionGuardada;
import explorador.data.JsonPersistencia;

import java.util.List;

public class BibliotecaDAOImpl implements BibliotecaDAO {

    private static final String ARCHIVO_GRAFO = "grafo";
    private static final String ARCHIVO_GUARDADAS = "guardadas";
    private final JsonPersistencia persistencia;

    public BibliotecaDAOImpl() {
        this.persistencia = new JsonPersistencia("Biblioteca");
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
        return persistencia.leerLista(ARCHIVO_GUARDADAS, PublicacionGuardada.class);
    }

    @Override
    public void escribirGuardadas(List<PublicacionGuardada> guardadas) {
        persistencia.escribir(ARCHIVO_GUARDADAS, guardadas);
    }
}
