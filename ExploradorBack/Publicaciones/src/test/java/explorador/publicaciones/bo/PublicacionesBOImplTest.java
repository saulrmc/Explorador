package explorador.publicaciones.bo;

import explorador.fuentes.modelo.PublicacionOriginal;
import explorador.publicaciones.conceptos.ConceptoExtractorBasico;
import explorador.publicaciones.conceptos.DefinicionConcepto;
import explorador.publicaciones.conceptos.FuenteDefinicion;
import explorador.publicaciones.dao.PublicacionDAO;
import explorador.publicaciones.modelo.Concepto;
import explorador.publicaciones.modelo.Publicacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
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
                new RankerDeterminista(), new ConceptoExtractorBasico(4, 50),
                candidatos -> candidatos.stream()
                        .map(candidato -> new Concepto(candidato,
                                "https://en.wikipedia.org/wiki/" + candidato))
                        .toList(),
                concepto -> new DefinicionConcepto(
                        concepto, "Definicion de " + concepto, "https://ejemplo.test/" + concepto));
    }

    @Test
    void registrarBrutasEvitaDuplicadosPorOrigen() {
        publicaciones.registrarBrutas(List.of(bruta("arxiv", "abc")));
        publicaciones.registrarBrutas(List.of(bruta("arxiv", "abc")));

        assertEquals(1, dao.leerTodos().size());
    }

    @Test
    void registrarBrutasCreaPublicacionConConceptosYFecha() {
        Publicacion creada = publicaciones.registrarBrutas(List.of(bruta("arxiv", "abc"))).get(0);

        assertTrue(creada.getFechaIngreso() != null);
        assertTrue(creada.getOriginal().getUrl().endsWith("/abc"));
        assertTrue(creada.getConceptos().stream().anyMatch(c -> c.getUrl() != null));
    }

    @Test
    void listarRelacionadasSoloDevuelvePublicacionesConCoincidencia() {
        Publicacion base = publicacion(1, List.of("cs.AI"), List.of(concepto("neural")));
        Publicacion conEtiqueta = publicacion(2, List.of("cs.AI"), List.of());
        Publicacion conConcepto = publicacion(3, List.of(), List.of(concepto("neural")));
        Publicacion sinCoincidencia = publicacion(4, List.of("physics"), List.of(concepto("boring")));
        dao.registrarTodos(List.of(base, conEtiqueta, conConcepto, sinCoincidencia));

        List<Publicacion> relacionadas = publicaciones.listarRelacionadas(1, 10);

        assertEquals(2, relacionadas.size());
        assertTrue(relacionadas.stream().noneMatch(p -> p.getId() == sinCoincidencia.getId()));
    }

    @Test
    void listarRelacionadasPonderaLasEtiquetasSobreLosConceptos() {
        Publicacion base = publicacion(1, List.of("cs.AI"), List.of(concepto("neural")));
        Publicacion comparteEtiqueta = publicacion(2, List.of("cs.AI"), List.of());
        Publicacion comparteConcepto = publicacion(3, List.of(), List.of(concepto("neural")));
        dao.registrarTodos(List.of(base, comparteEtiqueta, comparteConcepto));

        List<Publicacion> relacionadas = publicaciones.listarRelacionadas(1, 10);

        assertEquals(comparteEtiqueta.getId(), relacionadas.get(0).getId());
    }

    @Test
    void listarRelacionadasNoIncluyeLaPropiaPublicacion() {
        Publicacion base = publicacion(1, List.of("cs.AI"), List.of(concepto("neural")));
        dao.registrarTodos(List.of(base));

        assertTrue(publicaciones.listarRelacionadas(1, 10).isEmpty());
    }

    @Test
    void podarEliminaAntiguasNoProtegidasYConservaLasProtegidas() {
        Publicacion antigua = publicacion(1, List.of(), List.of());
        antigua.setFechaIngreso(LocalDateTime.now().minusDays(10));
        Publicacion antiguaProtegida = publicacion(2, List.of(), List.of());
        antiguaProtegida.setFechaIngreso(LocalDateTime.now().minusDays(10));
        Publicacion reciente = publicacion(3, List.of(), List.of());
        reciente.setFechaIngreso(LocalDateTime.now());
        dao.registrarTodos(List.of(antigua, antiguaProtegida, reciente));

        Set<Integer> removidos = publicaciones.podar(Set.of(2));

        assertEquals(Set.of(1), removidos);
        assertEquals(2, dao.leerTodos().size());
    }

    @Test
    void definirConceptoDelegaEnLaFuenteDeDefiniciones() {
        DefinicionConcepto definicion = publicaciones.definirConcepto("aprendizaje");

        assertEquals("aprendizaje", definicion.getConcepto());
        assertEquals("Definicion de aprendizaje", definicion.getDefinicion());
    }

    private Concepto concepto(String termino) {
        return new Concepto(termino, "https://en.wikipedia.org/wiki/" + termino);
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

    private Publicacion publicacion(int id, List<String> etiquetas, List<Concepto> conceptos) {
        Publicacion publicacion = new Publicacion();
        publicacion.setId(id);
        publicacion.setTitulo("Titulo " + id);
        publicacion.setDescripcion("Descripcion " + id);
        PublicacionOriginal original = new PublicacionOriginal();
        original.setEtiquetas(etiquetas);
        publicacion.setOriginal(original);
        publicacion.setConceptos(conceptos);
        return publicacion;
    }

    private static class FakePublicacionDAO implements PublicacionDAO {
        private final Map<Integer, Publicacion> porId = new HashMap<>();

        void registrarTodos(List<Publicacion> publicaciones) {
            for (Publicacion publicacion : publicaciones) {
                porId.put(publicacion.getId(), publicacion);
            }
        }

        @Override
        public Integer crear(Publicacion modelo) {
            int id = porId.values().stream().mapToInt(Publicacion::getId).max().orElse(0) + 1;
            modelo.setId(id);
            porId.put(id, modelo);
            return id;
        }

        @Override
        public boolean actualizar(Publicacion modelo) {
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
        public Publicacion leer(Integer id) {
            return porId.get(id);
        }

        @Override
        public List<Publicacion> leerTodos() {
            return new ArrayList<>(porId.values());
        }

        @Override
        public boolean existePorOrigen(String fuente, String idOrigen) {
            return porId.values().stream()
                    .anyMatch(publicacion -> publicacion.getOriginal() != null
                            && publicacion.getOriginal().getFuente() != null
                            && publicacion.getOriginal().getFuente().equals(fuente)
                            && publicacion.getOriginal().getIdOrigen() != null
                            && publicacion.getOriginal().getIdOrigen().equals(idOrigen));
        }

        @Override
        public void guardar() {
        }
    }
}
