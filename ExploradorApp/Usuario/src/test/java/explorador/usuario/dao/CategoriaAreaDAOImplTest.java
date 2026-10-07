package explorador.usuario.dao;

import explorador.db.JsonPersistencia;
import explorador.usuario.modelo.CategoriaAreaEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoriaAreaDAOImplTest {

    @TempDir
    Path directorio;

    private CategoriaAreaDAOImpl dao;

    @BeforeEach
    void setUp() {
        CategoriaAreaDAOImpl.reiniciar();
        dao = new CategoriaAreaDAOImpl(new JsonPersistencia("Usuario", directorio));
    }

    @Test
    void agregarNoEscribeHastaGuardar() throws Exception {
        dao.agregar(CategoriaAreaEnum.COMPUTACION);

        assertFalse(Files.exists(archivoJson()));

        dao.guardar();
        assertTrue(Files.exists(archivoJson()));
    }

    @Test
    void guardarSinCambiosNoVuelveAEscribir() throws Exception {
        dao.guardar();
        assertFalse(Files.exists(archivoJson()));

        dao.agregar(CategoriaAreaEnum.FISICA);
        dao.guardar();
        assertTrue(Files.exists(archivoJson()));
    }

    @Test
    void agregarEvitaDuplicados() {
        assertTrue(dao.agregar(CategoriaAreaEnum.COMPUTACION));
        assertFalse(dao.agregar(CategoriaAreaEnum.COMPUTACION));

        assertEquals(1, dao.leerTodos().size());
    }

    @Test
    void eliminarQuitaLaCategoriaDelListado() {
        dao.agregar(CategoriaAreaEnum.COMPUTACION);
        dao.agregar(CategoriaAreaEnum.FISICA);

        assertTrue(dao.eliminar(CategoriaAreaEnum.COMPUTACION));
        assertFalse(dao.eliminar(CategoriaAreaEnum.COMPUTACION));

        assertEquals(List.of(CategoriaAreaEnum.FISICA), dao.leerTodos());
    }

    @Test
    void laCargaInicialReconstruyeLoPersistido() throws Exception {
        dao.agregar(CategoriaAreaEnum.COMPUTACION);
        dao.agregar(CategoriaAreaEnum.MATEMATICAS);
        dao.guardar();

        CategoriaAreaDAOImpl.reiniciar();
        CategoriaAreaDAOImpl segundo = new CategoriaAreaDAOImpl(new JsonPersistencia("Usuario", directorio));

        assertEquals(List.of(CategoriaAreaEnum.COMPUTACION, CategoriaAreaEnum.MATEMATICAS), segundo.leerTodos());
    }

    private Path archivoJson() {
        return directorio.resolve("Usuario").resolve("areas.json");
    }
}
