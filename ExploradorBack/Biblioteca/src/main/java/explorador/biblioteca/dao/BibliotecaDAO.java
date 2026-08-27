package explorador.biblioteca.dao;

import explorador.biblioteca.modelo.GrafoTematica;
import explorador.biblioteca.modelo.PublicacionGuardada;

import java.util.List;

public interface BibliotecaDAO {
    GrafoTematica leerBiblioteca();

    void escribirBiblioteca(PublicacionGuardada publicacionGuardada);
}
