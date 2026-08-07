package explorador.publicaciones.conceptos;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
class ConceptoExtractorBasicoTest {

    private final ConceptoExtractorBasico extractor = new ConceptoExtractorBasico();

    @Test
    void extraePalabrasFrecuentesOrdenadasPorFrecuencia() {
        String resumen = "neural networks neural network learning neural models";

        List<String> conceptos = extractor.extraer(resumen);

        assertEquals("neural", conceptos.get(0));
        assertTrue(conceptos.contains("networks"));
        assertTrue(conceptos.contains("learning"));
    }

    @Test
    void limitaAMaximoOchoConceptos() {
        StringBuilder resumen = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            resumen.append("termino").append(i).append(" ");
        }

        List<String> conceptos = extractor.extraer(resumen.toString());

        assertEquals(8, conceptos.size());
    }

    @Test
    void ignoraStopwordsYPalabrasCortas() {
        String resumen = "the of and with cat science";

        List<String> conceptos = extractor.extraer(resumen);

        assertFalse(conceptos.contains("the"));
        assertFalse(conceptos.contains("cat"));
        assertTrue(conceptos.contains("science"));
    }

    @Test
    void resumenVacioProduceListaVacia() {
        assertTrue(extractor.extraer(null).isEmpty());
        assertTrue(extractor.extraer("   ").isEmpty());
    }

    @Test
    void esDeterministaAnteEmpates() {
        String resumen = "alpha beta gamma delta";

        List<String> conceptos = extractor.extraer(resumen);

        assertEquals(conceptos, extractor.extraer(resumen));
    }
}
