package explorador.usuario.dao;

import explorador.data.JsonPersistencia;
import explorador.usuario.modelo.AreaInteres;
import explorador.usuario.modelo.CategoriaArea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AreaInteresDAOImplTest {

    @TempDir
    Path directorio;

    private AreaInteresDAOImpl dao;

    @BeforeEach
    void setUp() {
        AreaInteresDAOImpl.reiniciar();
        dao = new AreaInteresDAOImpl(new JsonPersistencia("Usuario", directorio));
    }

    @Test
    void crearAsignaIdsSecuenciales() {
        AreaInteres primera = crearArea("a");
        AreaInteres segunda = crearArea("b");

        assertEquals(1, primera.getId());
        assertEquals(2, segunda.getId());
    }

    @Test
    void crearNoReutilizaElUltimoIdEliminado() {
        AreaInteres una = crearArea("a");
        crearArea("b");

        dao.eliminar(una.getId());

        AreaInteres nueva = crearArea("c");
        assertEquals(3, nueva.getId());
    }

    @Test
    void leerEncuentraPorId() {
        crearArea("a");
        crearArea("b");
        crearArea("c");

        assertEquals("Area b", dao.leer(2).getNombre());
        assertNull(dao.leer(99));
    }

    @Test
    void actualizarModificaElAreaExistente() {
        AreaInteres creada = crearArea("a");
        creada.setNombre("Area actualizada");

        assertTrue(dao.actualizar(creada));
        assertEquals("Area actualizada", dao.leer(creada.getId()).getNombre());
    }

    @Test
    void actualizarDeAreaInexistenteDevuelveFalse() {
        AreaInteres fantasma = new AreaInteres();
        fantasma.setId(999);
        fantasma.setNombre("Fantasma");
        fantasma.setCategoria(CategoriaArea.COMPUTACION);

        assertFalse(dao.actualizar(fantasma));
    }

    @Test
    void eliminarQuitaElAreaDelListado() {
        AreaInteres creada = crearArea("a");
        crearArea("b");

        assertTrue(dao.eliminar(creada.getId()));
        assertNull(dao.leer(creada.getId()));
        assertEquals(1, dao.leerTodos().size());
    }

    @Test
    void eliminarDeAreaInexistenteDevuelveFalse() {
        assertFalse(dao.eliminar(999));
    }

    @Test
    void crearNoEscribeHastaGuardar() throws Exception {
        crearArea("a");

        Path archivo = archivoJson();
        assertFalse(Files.exists(archivo));

        dao.guardar();
        assertTrue(Files.exists(archivo));
    }

    @Test
    void guardarSinCambiosNoVuelveAEscribir() throws Exception {
        dao.guardar();
        assertFalse(Files.exists(archivoJson()));

        crearArea("a");
        dao.guardar();
        assertTrue(Files.exists(archivoJson()));
    }

    @Test
    void laCargaInicialReconstruyeLoPersistido() throws Exception {
        crearArea("a");
        crearArea("b");
        dao.guardar();

        AreaInteresDAOImpl.reiniciar();
        AreaInteresDAOImpl segundo = new AreaInteresDAOImpl(new JsonPersistencia("Usuario", directorio));

        assertEquals(2, segundo.leerTodos().size());
        assertEquals(1, segundo.leerTodos().get(0).getId());
        assertEquals(2, segundo.leerTodos().get(1).getId());

        AreaInteres tercera = new AreaInteres();
        tercera.setNombre("Area c");
        tercera.setCategoria(CategoriaArea.COMPUTACION);
        assertEquals(3, segundo.crear(tercera));
    }

    private AreaInteres crearArea(String sufijo) {
        AreaInteres area = new AreaInteres();
        area.setNombre("Area " + sufijo);
        area.setCategoria(CategoriaArea.COMPUTACION);
        dao.crear(area);
        return area;
    }

    private Path archivoJson() {
        return directorio.resolve("Usuario").resolve("areas.json");
    }
}
