package explorador.publicaciones.conceptos;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
class ConceptoExtractorBasicoTest {

    private final ConceptoExtractorBasico extractor = new ConceptoExtractorBasico(4, 50);

    @Test
    void extraeTerminosMultipalabraRelevantes() {
        String resumen = "deep learning models for neural networks and graph learning";

        List<String> candidatos = extractor.extraerCandidatos("", resumen);

        assertTrue(candidatos.contains("deep learning"));
        assertTrue(candidatos.contains("neural networks"));
        assertTrue(candidatos.contains("graph learning"));
    }

    @Test
    void noGeneraTerminosQueAtraviesanStopwords() {
        String resumen = "deep learning for neural networks";

        List<String> candidatos = extractor.extraerCandidatos("", resumen);

        assertFalse(candidatos.contains("learning for"));
        assertFalse(candidatos.contains("for neural"));
    }

    @Test
    void ignoraStopwordsYPalabrasCortas() {
        String resumen = "the of and with cat science graph";

        List<String> candidatos = extractor.extraerCandidatos("", resumen);

        assertFalse(candidatos.contains("the"));
        assertFalse(candidatos.contains("cat"));
        assertTrue(candidatos.contains("science"));
    }

    @Test
    void ponderaLosTerminosDelTitulo() {
        String titulo = "reinforcement learning";
        String resumen = "we study a problem about models and data and systems";

        List<String> candidatos = extractor.extraerCandidatos(titulo, resumen);

        assertEquals("reinforcement learning", candidatos.get(0));
    }

    @Test
    void respetaElLimiteDeCandidatos() {
        ConceptoExtractorBasico conLimite = new ConceptoExtractorBasico(4, 10);
        StringBuilder resumen = new StringBuilder();
        for (int i = 0; i < 40; i++) {
            resumen.append("termino").append(i).append(" ");
        }

        List<String> candidatos = conLimite.extraerCandidatos("", resumen.toString());

        assertEquals(10, candidatos.size());
    }

    @Test
    void textoNuloOVacioProduceListaVacia() {
        assertTrue(extractor.extraerCandidatos(null, null).isEmpty());
        assertTrue(extractor.extraerCandidatos("", "   ").isEmpty());
    }

    @Test
    void esDeterministaAnteEmpates() {
        String resumen = "alpha beta gamma delta epsilon zeta eta theta";

        List<String> candidatos = extractor.extraerCandidatos("", resumen);

        assertEquals(candidatos, extractor.extraerCandidatos("", resumen));
    }
}
