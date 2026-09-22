package explorador.publicaciones.bo;

import explorador.fuentes.modelo.PublicacionOriginal;
import explorador.publicaciones.modelo.Publicacion;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RankerDeterministaTest {

    private final RankerDeterminista ranker = new RankerDeterminista();

    @Test
    void laCoincidenciaExigeEtiquetaExactaNoSubcadena() {
        Publicacion enIA = publicacion("titulo", "resumen", List.of("cs.AI"));
        Publicacion enIA2 = publicacion("titulo", "resumen", List.of("cs.AI.extra"));

        List<Publicacion> ordenadas = ranker.ordenar(List.of(enIA2, enIA), Set.of("cs.AI"));

        assertEquals(enIA.getId(), ordenadas.get(0).getId());
        assertTrue(enIA.getScore() > enIA2.getScore());
    }

    @Test
    void coincideCuandoLaEtiquetaEstaEnLasCategoriasDelUsuario() {
        Publicacion enIA = publicacion("titulo", "resumen", List.of("cs.AI"));
        Publicacion enFisica = publicacion("titulo", "resumen", List.of("physics"));

        List<Publicacion> ordenadas = ranker.ordenar(List.of(enFisica, enIA), Set.of("cs.AI"));

        assertEquals(enIA.getId(), ordenadas.get(0).getId());
    }

    @Test
    void laCoincidenciaConsideraElSolapeParcial() {
        Publicacion mitad = publicacion("titulo", "resumen", List.of("cs.AI", "physics"));
        Publicacion ninguna = publicacion("titulo", "resumen", List.of("physics"));

        List<Publicacion> ordenadas = ranker.ordenar(List.of(ninguna, mitad), Set.of("cs.AI"));

        assertEquals(mitad.getId(), ordenadas.get(0).getId());
        assertTrue(mitad.getScore() > ninguna.getScore());
    }

    @Test
    void sinCategoriasElOrdenDependeSoloDeRecencia() {
        Publicacion antigua = publicacion("titulo");
        antigua.getOriginal().setFechaPublicacion(LocalDate.now().minusDays(10));
        Publicacion reciente = publicacion("titulo");
        reciente.getOriginal().setFechaPublicacion(LocalDate.now());

        List<Publicacion> ordenadas = ranker.ordenar(List.of(antigua, reciente), Set.of());

        assertEquals(reciente.getId(), ordenadas.get(0).getId());
        assertTrue(reciente.getScore() > antigua.getScore());
    }

    @Test
    void laRecenciaDecaeConLaAntiguedad() {
        Publicacion hoy = publicacion("titulo");
        hoy.getOriginal().setFechaPublicacion(LocalDate.now());

        List<Publicacion> ordenadas = ranker.ordenar(List.of(hoy), Set.of());

        assertTrue(ordenadas.get(0).getScore() > 0);
    }

    @Test
    void categoriasNulasNoProducenCoincidencia() {
        Publicacion conEtiqueta = publicacion("titulo", "resumen", List.of("cs.AI"));
        Publicacion otroTema = publicacion("titulo", "resumen", List.of("physics"));

        List<Publicacion> ordenadas = ranker.ordenar(List.of(conEtiqueta, otroTema), null);

        assertEquals(ordenadas.get(0).getScore(), ordenadas.get(1).getScore(), 0.0001);
    }

    private Publicacion publicacion(String titulo) {
        return publicacion(titulo, "resumen", List.of());
    }

    private Publicacion publicacion(String titulo, String descripcion, List<String> etiquetas) {
        Publicacion publicacion = new Publicacion();
        publicacion.setId((titulo + descripcion).hashCode());
        publicacion.setTitulo(titulo);
        publicacion.setDescripcion(descripcion);
        PublicacionOriginal original = new PublicacionOriginal();
        original.setEtiquetas(etiquetas);
        original.setConfianza(0.5);
        original.setFechaPublicacion(LocalDate.now());
        publicacion.setOriginal(original);
        return publicacion;
    }
}
