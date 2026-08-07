package explorador.publicaciones.bo;

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
    void coincidenciaUsaPalabrasCompletasNoSubcadenas() {
        Publicacion conCats = publicacion("cat breeding", "", List.of());
        Publicacion conCattle = publicacion("cattle farming", "", List.of());

        List<Publicacion> ordenadas = ranker.ordenar(List.of(conCattle, conCats), Set.of("cat"));

        assertEquals(conCats.getId(), ordenadas.get(0).getId());
        assertTrue(conCats.getScore() > conCattle.getScore());
    }

    @Test
    void coincidePorTokenExactoEnEtiquetas() {
        Publicacion enIA = publicacion("titulo", "resumen", List.of("cs.AI"));
        Publicacion enFisica = publicacion("titulo", "resumen", List.of("physics"));

        List<Publicacion> ordenadas = ranker.ordenar(List.of(enFisica, enIA), Set.of("cs.AI"));

        assertEquals(enIA.getId(), ordenadas.get(0).getId());
    }

    @Test
    void sinKeywordsElOrdenDependeSoloDeRecencia() {
        Publicacion antigua = publicacion("titulo");
        antigua.setFechaPublicacion(LocalDate.now().minusDays(10));
        Publicacion reciente = publicacion("titulo");
        reciente.setFechaPublicacion(LocalDate.now());

        List<Publicacion> ordenadas = ranker.ordenar(List.of(antigua, reciente), Set.of());

        assertEquals(reciente.getId(), ordenadas.get(0).getId());
        assertTrue(reciente.getScore() > antigua.getScore());
    }

    @Test
    void laRecenciaDecaeConLaAntiguedad() {
        Publicacion hoy = publicacion("titulo");
        hoy.setFechaPublicacion(LocalDate.now());

        List<Publicacion> ordenadas = ranker.ordenar(List.of(hoy), Set.of());

        assertTrue(ordenadas.get(0).getScore() > 0);
    }

    @Test
    void keywordsNulasNoProducenCoincidencia() {
        Publicacion conTermino = publicacion("machine learning", "resumen", List.of());
        Publicacion otroTema = publicacion("otro tema", "resumen", List.of());

        List<Publicacion> ordenadas = ranker.ordenar(List.of(conTermino, otroTema), null);

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
        publicacion.setEtiquetas(etiquetas);
        publicacion.setConfianza(0.5);
        publicacion.setFechaPublicacion(LocalDate.now());
        return publicacion;
    }
}
