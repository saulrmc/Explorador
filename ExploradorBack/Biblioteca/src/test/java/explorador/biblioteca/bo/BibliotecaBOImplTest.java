package explorador.biblioteca.bo;

import explorador.biblioteca.dao.BibliotecaDAO;
import explorador.biblioteca.modelo.EntradaBiblioteca;
import explorador.biblioteca.modelo.GrafoTematica;
import explorador.biblioteca.modelo.PublicacionGuardada;
import explorador.fuentes.modelo.PublicacionOriginal;
import explorador.publicaciones.bo.PublicacionesBO;
import explorador.publicaciones.modelo.Publicacion;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BibliotecaBOImplTest {

    private final FakeBibliotecaDAO dao = new FakeBibliotecaDAO();
    private final FakePublicacionesBO publicaciones = new FakePublicacionesBO();
    private final BibliotecaBOImpl biblioteca = new BibliotecaBOImpl(dao, publicaciones);

    @Test
    void guardarAsignaIdSecuencialYComponeLaPublicacion() {
        publicaciones.registrar(publicacion(1, List.of("neural networks")));
        publicaciones.registrar(publicacion(2, List.of("machine learning")));

        EntradaBiblioteca primera = biblioteca.guardar(publicaciones.obtener(1));
        EntradaBiblioteca segunda = biblioteca.guardar(publicaciones.obtener(2));

        assertEquals(1, primera.getId());
        assertEquals(2, segunda.getId());
        assertEquals(1, primera.getPublicacionId());
        assertNotNull(primera.getFechaGuardado());
        assertEquals(2, dao.leerGuardadas().size());
    }

    @Test
    void guardarRechazaDuplicados() {
        publicaciones.registrar(publicacion(1, List.of("neural networks")));
        biblioteca.guardar(publicaciones.obtener(1));

        assertThrows(IllegalStateException.class,
                () -> biblioteca.guardar(publicaciones.obtener(1)));
    }

    @Test
    void guardarRechazaPublicacionSinIdValido() {
        assertThrows(IllegalArgumentException.class,
                () -> biblioteca.guardar(publicacion(0, List.of("neural networks"))));
    }

    @Test
    void obtenerDevuelveEntradaExistenteONull() {
        publicaciones.registrar(publicacion(1, List.of("neural networks")));
        EntradaBiblioteca guardada = biblioteca.guardar(publicaciones.obtener(1));

        assertEquals(guardada.getId(), biblioteca.obtener(guardada.getId()).getId());
        assertNull(biblioteca.obtener(999));
    }

    @Test
    void listarGuardadasOrdenaPorFechaDescendente() {
        Publicacion antigua = publicacion(1, List.of("neural networks"));
        Publicacion reciente = publicacion(2, List.of("machine learning"));
        publicaciones.registrar(antigua);
        publicaciones.registrar(reciente);

        PublicacionGuardada g1 = guardada(1, 1, LocalDateTime.now().minusDays(2));
        PublicacionGuardada g2 = guardada(2, 2, LocalDateTime.now());
        dao.guardadas.add(g1);
        dao.guardadas.add(g2);

        List<EntradaBiblioteca> entradas = biblioteca.listarGuardadas();
        assertEquals(2, entradas.size());
        assertEquals(2, entradas.get(0).getPublicacionId());
        assertEquals(1, entradas.get(1).getPublicacionId());
    }

    @Test
    void eliminarQuitaLaGuardadaYLimpiaNodosHuérfanosDelGrafo() {
        publicaciones.registrar(publicacion(1, List.of("neural networks", "deep learning")));
        publicaciones.registrar(publicacion(2, List.of("deep learning", "transformers")));
        EntradaBiblioteca a = biblioteca.guardar(publicaciones.obtener(1));
        biblioteca.guardar(publicaciones.obtener(2));

        GrafoTematica grafo = dao.leerGrafo();
        assertEquals(3, grafo.getNodos().size());
        assertEquals(2, grafo.getAristas().size());

        biblioteca.eliminar(a.getId());

        assertEquals(2, grafo.getNodos().size());
        assertEquals(1, grafo.getAristas().size());
        assertFalse(dao.guardadas.stream().anyMatch(g -> g.getId() == a.getId()));
    }

    @Test
    void eliminarInexistenteLanzaExcepcion() {
        assertThrows(IllegalStateException.class, () -> biblioteca.eliminar(99));
    }

    @Test
    void listarPorTemaUsaElGrafoYNormaliza() {
        publicaciones.registrar(publicacion(1, List.of("neural networks", "algebraic topology")));
        publicaciones.registrar(publicacion(2, List.of("algebraic geometry")));
        biblioteca.guardar(publicaciones.obtener(1));
        biblioteca.guardar(publicaciones.obtener(2));

        List<EntradaBiblioteca> coincidentes = biblioteca.listarPorTema("neural");
        assertEquals(1, coincidentes.size());
        assertEquals(1, coincidentes.get(0).getPublicacionId());

        List<EntradaBiblioteca> conAcentos = biblioteca.listarPorTema("algebra");
        assertEquals(2, conAcentos.size());

        assertTrue(biblioteca.listarPorTema("inexistente").isEmpty());
    }

    private Publicacion publicacion(int id, List<String> palabrasClave) {
        Publicacion publicacion = new Publicacion();
        publicacion.setId(id);
        publicacion.setTitulo("Titulo " + id);
        PublicacionOriginal original = new PublicacionOriginal();
        original.setPalabrasClave(palabrasClave);
        publicacion.setOriginal(original);
        return publicacion;
    }

    private PublicacionGuardada guardada(int id, int publicacionId, LocalDateTime fecha) {
        PublicacionGuardada guardada = new PublicacionGuardada();
        guardada.setId(id);
        guardada.setPublicacionId(publicacionId);
        guardada.setFechaGuardado(fecha);
        return guardada;
    }

    private static class FakeBibliotecaDAO implements BibliotecaDAO {
        private final List<PublicacionGuardada> guardadas = new ArrayList<>();
        private final GrafoTematica grafo = new GrafoTematica();

        @Override
        public GrafoTematica leerGrafo() {
            return grafo;
        }

        @Override
        public void escribirGrafo(GrafoTematica grafo) {
        }

        @Override
        public List<PublicacionGuardada> leerGuardadas() {
            return new ArrayList<>(guardadas);
        }

        @Override
        public void escribirGuardadas(List<PublicacionGuardada> guardadas) {
            this.guardadas.clear();
            this.guardadas.addAll(guardadas);
        }
    }

    private static class FakePublicacionesBO implements PublicacionesBO {
        private final Map<Integer, Publicacion> porId = new HashMap<>();

        void registrar(Publicacion publicacion) {
            porId.put(publicacion.getId(), publicacion);
        }

        @Override
        public List<Publicacion> registrarBrutas(List<PublicacionOriginal> originales) {
            return List.of();
        }

        @Override
        public List<Publicacion> listarLimitadas(Set<String> keywords, int limite) {
            return List.of();
        }

        @Override
        public List<Publicacion> rankear(List<Publicacion> publicaciones, Set<String> keywords) {
            return List.of();
        }

        @Override
        public List<Publicacion> listar() {
            return List.of();
        }

        @Override
        public Publicacion obtener(int id) {
            return porId.get(id);
        }

        @Override
        public List<Publicacion> listarRelacionadas(int id, int limite) {
            return List.of();
        }

        @Override
        public Set<Integer> podar(Set<Integer> protegidos) {
            return Set.of();
        }

        @Override
        public explorador.publicaciones.modelo.DefinicionConcepto definirConcepto(String concepto) {
            return null;
        }
    }
}
