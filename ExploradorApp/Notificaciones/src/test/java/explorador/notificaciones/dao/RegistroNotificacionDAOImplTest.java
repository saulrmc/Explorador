package explorador.notificaciones.dao;

import explorador.data.JsonPersistencia;
import explorador.notificaciones.modelo.RegistroNotificacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistroNotificacionDAOImplTest {

    @TempDir
    Path directorio;

    private RegistroNotificacionDAOImpl dao;

    @BeforeEach
    void setUp() {
        RegistroNotificacionDAOImpl.reiniciar();
        dao = new RegistroNotificacionDAOImpl(new JsonPersistencia("Notificaciones", directorio));
    }

    @Test
    void agregarNoEscribeHastaGuardar() throws Exception {
        dao.agregar(registro(1, "Titulo 1"));

        assertFalse(Files.exists(archivoJson()));

        dao.guardar();
        assertTrue(Files.exists(archivoJson()));
    }

    @Test
    void guardarSinCambiosNoVuelveAEscribir() throws Exception {
        dao.guardar();
        assertFalse(Files.exists(archivoJson()));

        dao.agregar(registro(1, "Titulo 1"));
        dao.guardar();
        assertTrue(Files.exists(archivoJson()));
    }

    @Test
    void laCargaInicialReconstruyeLoPersistido() throws Exception {
        dao.agregar(registro(1, "Titulo 1"));
        dao.agregar(registro(2, "Titulo 2"));
        dao.guardar();

        RegistroNotificacionDAOImpl.reiniciar();
        RegistroNotificacionDAOImpl segundo = new RegistroNotificacionDAOImpl(
                new JsonPersistencia("Notificaciones", directorio));

        assertEquals(2, segundo.leerTodos().size());
        assertTrue(segundo.existe(1));
        assertTrue(segundo.existe(2));
    }

    @Test
    void existeDetectaRegistrosEnMemoria() {
        dao.agregar(registro(7, "Titulo 7"));

        assertTrue(dao.existe(7));
        assertFalse(dao.existe(99));
    }

    @Test
    void eliminarPorPublicacionIdsQuitaLosRegistros() throws Exception {
        dao.agregar(registro(1, "Titulo 1"));
        dao.agregar(registro(2, "Titulo 2"));

        dao.eliminarPorPublicacionIds(Set.of(1));
        dao.guardar();

        assertEquals(1, dao.leerTodos().size());
        assertTrue(dao.existe(2));
        assertFalse(dao.existe(1));
    }

    private RegistroNotificacion registro(int publicacionId, String asunto) {
        RegistroNotificacion registro = new RegistroNotificacion();
        registro.setPublicacionId(publicacionId);
        registro.setAsunto(asunto);
        return registro;
    }

    private Path archivoJson() {
        return directorio.resolve("Notificaciones").resolve("enviadas.json");
    }
}
