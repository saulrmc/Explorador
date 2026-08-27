package explorador.biblioteca.dao;

import explorador.biblioteca.modelo.GrafoTematica;
import explorador.biblioteca.modelo.PublicacionGuardada;

import java.util.List;

public interface NodoTemaDAO {
    public GrafoTematica leerGrafo();
    public void escribirGrafo(GrafoTematica grafo);
    List<PublicacionGuardada> leerGuardadas();
    void escribirGuardadas(List<PublicacionGuardada> guardadas);
}
