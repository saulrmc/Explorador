package explorador.publicaciones.conceptos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConceptoExtractorBasico implements ConceptoExtractor {

    private static final int MAX_CONCEPTOS = 8;
    private static final Set<String> STOPWORDS = Set.of(
            "the", "a", "an", "and", "or", "of", "to", "in", "for", "on", "with", "by",
            "this", "that", "these", "those", "is", "are", "was", "were", "be", "been",
            "as", "from", "we", "our", "their", "its", "his", "her", "which", "who",
            "de", "la", "el", "los", "las", "del", "un", "una", "en", "y", "que",
            "con", "por", "para", "es", "son", "como", "su", "sus", "se", "al", "lo");

    @Override
    public List<String> extraer(String resumen) {
        if (resumen == null || resumen.isBlank()) {
            return new ArrayList<>();
        }

        Map<String, Integer> frecuencias = new LinkedHashMap<>();
        for (String palabra : resumen.toLowerCase().split("[^a-z0-9]+")) {
            if (palabra.length() >= 4 && !STOPWORDS.contains(palabra)) {
                frecuencias.put(palabra, frecuencias.getOrDefault(palabra, 0) + 1);
            }
        }

        return frecuencias.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(MAX_CONCEPTOS)
                .map(Map.Entry::getKey)
                .toList();
    }
}
