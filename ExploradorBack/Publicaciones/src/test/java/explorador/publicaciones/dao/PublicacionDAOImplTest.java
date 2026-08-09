package explorador.publicaciones.dao;

import explorador.data.JsonPersistencia;
import explorador.fuentes.modelo.PublicacionOriginal;
import explorador.publicaciones.modelo.Publicacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PublicacionDAOImplTest {

    @TempDir
    Path directorio;

    private PublicacionDAOImpl dao;

    @BeforeEach
    void setUp() {
        PublicacionDAOImpl.reiniciar();
        dao = new PublicacionDAOImpl(new JsonPersistencia("Publicaciones", directorio));
    }

    @Test
    void crearAsignaIdsSecuenciales() {
        Publicacion primera = crearPublicacion("a");
        Publicacion segunda = crearPublicacion("b");

        assertEquals(1, primera.getId());
        assertEquals(2, segunda.getId());
    }

    @Test
    void crearNoReutilizaElUltimoIdEliminado() {
        Publicacion una = crearPublicacion("a");
        crearPublicacion("b");

        dao.eliminar(una.getId());

        Publicacion nueva = crearPublicacion("c");
        assertEquals(3, nueva.getId());
    }

    @Test
    void leerEncuentraPorId() {
        crearPublicacion("a");
        crearPublicacion("b");
        crearPublicacion("c");

        assertEquals("Titulo b", dao.leer(2).getTitulo());
        assertNull(dao.leer(99));
    }

    @Test
    void actualizarModificaLaPublicacionExistente() {
        Publicacion creada = crearPublicacion("a");
        creada.setTitulo("Titulo actualizado");

        assertTrue(dao.actualizar(creada));
        assertEquals("Titulo actualizado", dao.leer(creada.getId()).getTitulo());
    }

    @Test
    void actualizarDePublicacionInexistenteDevuelveFalse() {
        Publicacion fantasma = new Publicacion();
        fantasma.setId(999);

        assertFalse(dao.actualizar(fantasma));
    }

    @Test
    void eliminarQuitaLaPublicacionDelListado() {
        Publicacion creada = crearPublicacion("a");
        crearPublicacion("b");

        assertTrue(dao.eliminar(creada.getId()));
        assertNull(dao.leer(creada.getId()));
        assertEquals(1, dao.leerTodos().size());
    }

    @Test
    void eliminarDePublicacionInexistenteDevuelveFalse() {
        assertFalse(dao.eliminar(999));
    }

    @Test
    void existePorOrigenDetectaDuplicados() {
        crearPublicacion("abc");

        assertTrue(dao.existePorOrigen("arxiv", "abc"));
        assertFalse(dao.existePorOrigen("arxiv", "xyz"));
        assertFalse(dao.existePorOrigen("arxiv", null));
        assertFalse(dao.existePorOrigen(null, "abc"));
    }

    @Test
    void crearNoEscribeHastaGuardar() throws Exception {
        crearPublicacion("a");

        Path archivo = archivoJson();
        assertFalse(Files.exists(archivo));

        dao.guardar();
        assertTrue(Files.exists(archivo));
    }

    @Test
    void guardarSinCambiosNoVuelveAEscribir() throws Exception {
        dao.guardar();
        assertFalse(Files.exists(archivoJson()));

        crearPublicacion("a");
        dao.guardar();
        assertTrue(Files.exists(archivoJson()));
    }

    @Test
    void laCargaInicialReconstruyeLoPersistido() throws Exception {
        crearPublicacion("a");
        crearPublicacion("b");
        dao.guardar();

        PublicacionDAOImpl.reiniciar();
        PublicacionDAOImpl segundo = new PublicacionDAOImpl(new JsonPersistencia("Publicaciones", directorio));

        assertEquals(2, segundo.leerTodos().size());
        assertEquals(1, segundo.leerTodos().get(0).getId());
        assertEquals(2, segundo.leerTodos().get(1).getId());
    }

    private Publicacion crearPublicacion(String idOrigen) {
        Publicacion publicacion = new Publicacion();
        publicacion.setTitulo("Titulo " + idOrigen);
        publicacion.setDescripcion("Descripcion " + idOrigen);
        PublicacionOriginal original = new PublicacionOriginal();
        original.setFuente("arxiv");
        original.setIdOrigen(idOrigen);
        original.setTitulo("Titulo " + idOrigen);
        original.setResumen("Resumen " + idOrigen);
        original.setUrl("https://arxiv.org/abs/" + idOrigen);
        publicacion.setOriginal(original);
        dao.crear(publicacion);
        return publicacion;
    }

    private Path archivoJson() {
        return directorio.resolve("Publicaciones").resolve("publicaciones.json");
    }
}
