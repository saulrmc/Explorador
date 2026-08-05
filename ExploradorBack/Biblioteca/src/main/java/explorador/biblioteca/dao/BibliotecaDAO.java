package explorador.biblioteca.dao;

import explorador.biblioteca.modelo.GrafoTematica;
import explorador.biblioteca.modelo.PublicacionGuardada;

import java.util.List;

public interface BibliotecaDAO {
    GrafoTematica leerGrafo();

    void escribirGrafo(GrafoTematica grafo);

    List<PublicacionGuardada> leerGuardadas();

    void escribirGuardadas(List<PublicacionGuardada> guardadas);
}
