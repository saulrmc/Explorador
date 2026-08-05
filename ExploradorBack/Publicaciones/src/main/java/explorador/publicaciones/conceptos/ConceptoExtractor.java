package explorador.publicaciones.conceptos;

import java.util.List;

public interface ConceptoExtractor {
    List<String> extraer(String resumen);
}
