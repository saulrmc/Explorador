package explorador.biblioteca.dao;

import explorador.biblioteca.modelo.GrafoTematica;
import explorador.biblioteca.modelo.PublicacionGuardada;
import explorador.data.JsonPersistencia;

import java.util.List;

public class BibliotecaDAOImpl implements BibliotecaDAO {

    private static final String ARCHIVO_GUARDADAS = "guardadas";
    private final JsonPersistencia persistencia;

    public BibliotecaDAOImpl() {
        this.persistencia = new JsonPersistencia("Biblioteca");
    }

    @Override
    public GrafoTematica leerBiblioteca() {
        return persistencia.leer(ARCHIVO_GUARDADAS, GrafoTematica.class, new GrafoTematica());
    }

    @Override
    public void escribirBiblioteca(PublicacionGuardada publicacionGuardada) {
        persistencia.escribir(ARCHIVO_GUARDADAS, publicacionGuardada);
    }
}
