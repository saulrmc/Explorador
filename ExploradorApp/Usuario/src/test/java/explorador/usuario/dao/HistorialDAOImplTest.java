package explorador.usuario.dao;

import explorador.data.JsonPersistencia;
import explorador.usuario.modelo.PublicacionConsultada;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HistorialDAOImplTest {

    @TempDir
    Path directorio;

    private HistorialDAOImpl dao;

    @BeforeEach
    void setUp() {
        HistorialDAOImpl.reiniciar();
        dao = new HistorialDAOImpl(new JsonPersistencia("Usuario", directorio));
    }

    @Test
    void reemplazarNoEscribeHastaGuardar() throws Exception {
        dao.reemplazarTodos(List.of(registro(1, "Titulo 1")));

        assertFalse(Files.exists(archivoJson()));

        dao.guardar();
        assertTrue(Files.exists(archivoJson()));
    }

    @Test
    void guardarSinCambiosNoVuelveAEscribir() throws Exception {
        dao.guardar();
        assertFalse(Files.exists(archivoJson()));

        dao.reemplazarTodos(List.of(registro(1, "Titulo 1")));
        dao.guardar();
        assertTrue(Files.exists(archivoJson()));
    }

    @Test
    void laCargaInicialReconstruyeLoPersistido() throws Exception {
        dao.reemplazarTodos(List.of(registro(1, "Titulo 1"), registro(2, "Titulo 2")));
        dao.guardar();

        HistorialDAOImpl.reiniciar();
        HistorialDAOImpl segundo = new HistorialDAOImpl(new JsonPersistencia("Usuario", directorio));

        assertEquals(2, segundo.leerTodos().size());
        assertTrue(segundo.leerTodos().stream().anyMatch(r -> r.getPublicacionId() == 1));
        assertTrue(segundo.leerTodos().stream().anyMatch(r -> r.getPublicacionId() == 2));
    }

    @Test
    void reemplazarTodosReemplazaElContenido() {
        dao.reemplazarTodos(List.of(registro(1, "Titulo 1")));

        dao.reemplazarTodos(List.of(registro(2, "Titulo 2")));

        assertEquals(1, dao.leerTodos().size());
        assertEquals(2, dao.leerTodos().get(0).getPublicacionId());
    }

    @Test
    void reemplazarTodosConNuloDejaListaVacia() {
        dao.reemplazarTodos(List.of(registro(1, "Titulo 1")));

        dao.reemplazarTodos(null);

        assertTrue(dao.leerTodos().isEmpty());
    }

    @Test
    void leerTodosNoExponeLaListaInterna() {
        dao.reemplazarTodos(new ArrayList<>(List.of(registro(1, "Titulo 1"))));

        dao.leerTodos().clear();

        assertEquals(1, dao.leerTodos().size());
    }

    private PublicacionConsultada registro(int publicacionId, String titulo) {
        PublicacionConsultada registro = new PublicacionConsultada();
        registro.setPublicacionId(publicacionId);
        registro.setTitulo(titulo);
        return registro;
    }

    private Path archivoJson() {
        return directorio.resolve("Usuario").resolve("historial.json");
    }
}
