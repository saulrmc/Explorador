package explorador.publicaciones.bo;

import explorador.fuentes.modelo.PublicacionOriginal;
import explorador.publicaciones.dao.PublicacionDAO;
import explorador.publicaciones.modelo.Publicacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PublicacionesBOImplTest {

    private FakePublicacionDAO dao;
    private PublicacionesBOImpl publicaciones;

    @BeforeEach
    void setUp() {
        dao = new FakePublicacionDAO();
        publicaciones = new PublicacionesBOImpl(dao, new FormateadorTruncado(),
                new RankerDeterminista());
    }

    @Test
    void registrarBrutasEvitaDuplicadosPorOrigen() {
        publicaciones.registrarBrutas(List.of(bruta("arxiv", "abc")));
        publicaciones.registrarBrutas(List.of(bruta("arxiv", "abc")));

        assertEquals(1, dao.leerTodos().size());
    }

    @Test
    void registrarBrutasAlmacenaElOriginalCrudoSinProcesar() {
        PublicacionOriginal creada = publicaciones.registrarBrutas(List.of(bruta("arxiv", "abc"))).get(0);

        assertTrue(creada.getId() > 0);
        assertEquals("Titulo de prueba con varias palabras", creada.getTitulo());
        assertEquals("Resumen de prueba", creada.getResumen());
    }

    @Test
    void listarConstruyeLaVistaProcesadaAlLeer() {
        PublicacionOriginal bruta = bruta("arxiv", "abc");
        bruta.setTitulo("Titulo con mas de diez palabras para comprobar que se trunca al leer");
        publicaciones.registrarBrutas(List.of(bruta));

        Publicacion vista = publicaciones.listar().get(0);

        assertEquals(bruta.getId(), vista.getId());
        assertEquals(10, vista.getTitulo().split("\\s+").length);
        assertEquals(bruta.getTitulo(), dao.leer(bruta.getId()).getTitulo());
    }

    @Test
    void listarRelacionadasSoloDevuelvePublicacionesConCoincidencia() {
        PublicacionOriginal base = original(1, List.of("cs.AI"));
        PublicacionOriginal conEtiqueta = original(2, List.of("cs.AI"));
        PublicacionOriginal sinCoincidencia = original(3, List.of("physics"));
        dao.registrarTodos(List.of(base, conEtiqueta, sinCoincidencia));

        List<Publicacion> relacionadas = publicaciones.listarRelacionadas(1, 10);

        assertEquals(1, relacionadas.size());
        assertTrue(relacionadas.stream().noneMatch(p -> p.getId() == sinCoincidencia.getId()));
    }

    @Test
    void listarRelacionadasOrdenaPorEtiquetasCompartidas() {
        PublicacionOriginal base = original(1, List.of("cs.AI", "cs.LG"));
        PublicacionOriginal unaCoincidencia = original(2, List.of("cs.AI"));
        PublicacionOriginal dosCoincidencias = original(3, List.of("cs.AI", "cs.LG"));
        dao.registrarTodos(List.of(base, unaCoincidencia, dosCoincidencias));

        List<Publicacion> relacionadas = publicaciones.listarRelacionadas(1, 10);

        assertEquals(dosCoincidencias.getId(), relacionadas.get(0).getId());
        assertEquals(unaCoincidencia.getId(), relacionadas.get(1).getId());
    }

    @Test
    void listarRelacionadasNoIncluyeLaPropiaPublicacion() {
        PublicacionOriginal base = original(1, List.of("cs.AI"));
        dao.registrarTodos(List.of(base));

        assertTrue(publicaciones.listarRelacionadas(1, 10).isEmpty());
    }

    private PublicacionOriginal bruta(String fuente, String idOrigen) {
        PublicacionOriginal original = new PublicacionOriginal();
        original.setFuente(fuente);
        original.setIdOrigen(idOrigen);
        original.setTitulo("Titulo de prueba con varias palabras");
        original.setResumen("Resumen de prueba");
        original.setUrl("https://arxiv.org/abs/" + idOrigen);
        original.setEtiquetas(List.of("cs.AI"));
        return original;
    }

    private PublicacionOriginal original(int id, List<String> etiquetas) {
        PublicacionOriginal original = new PublicacionOriginal();
        original.setId(id);
        original.setEtiquetas(etiquetas);
        return original;
    }

    private static class FakePublicacionDAO implements PublicacionDAO {
        private final Map<Integer, PublicacionOriginal> porId = new HashMap<>();

        void registrarTodos(List<PublicacionOriginal> originales) {
            for (PublicacionOriginal original : originales) {
                porId.put(original.getId(), original);
            }
        }

        @Override
        public Integer crear(PublicacionOriginal modelo) {
            int id = porId.values().stream().mapToInt(PublicacionOriginal::getId).max().orElse(0) + 1;
            modelo.setId(id);
            porId.put(id, modelo);
            return id;
        }

        @Override
        public boolean actualizar(PublicacionOriginal modelo) {
            if (!porId.containsKey(modelo.getId())) {
                return false;
            }
            porId.put(modelo.getId(), modelo);
            return true;
        }

        @Override
        public boolean eliminar(Integer id) {
            return porId.remove(id) != null;
        }

        @Override
        public PublicacionOriginal leer(Integer id) {
            return porId.get(id);
        }

        @Override
        public List<PublicacionOriginal> leerTodos() {
            return new ArrayList<>(porId.values());
        }

        @Override
        public boolean existePorOrigen(String fuente, String idOrigen) {
            return porId.values().stream()
                    .anyMatch(original -> original.getFuente() != null
                            && original.getFuente().equals(fuente)
                            && original.getIdOrigen() != null
                            && original.getIdOrigen().equals(idOrigen));
        }

        @Override
        public void guardar() {
        }
    }
}
