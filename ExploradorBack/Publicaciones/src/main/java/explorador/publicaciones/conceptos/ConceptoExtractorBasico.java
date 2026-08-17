package explorador.publicaciones.conceptos;

import explorador.data.ExploradorConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ConceptoExtractorBasico implements ConceptoExtractor {

    private static final int MAX_N = 3;
    private static final int VENTANA_TEMPRANA = 10;
    private static final double PESO_TITULO = 2.0;
    private static final double PESO_TEMPRANO = 0.5;

    private static final Set<String> STOPWORDS = Set.of(
            "a", "about", "above", "across", "after", "again", "against", "all", "am", "an",
            "and", "any", "are", "as", "at", "be", "because", "been", "before", "being",
            "below", "between", "both", "but", "by", "can", "could", "did", "do", "does",
            "doing", "down", "during", "each", "few", "for", "from", "further", "had", "has",
            "have", "having", "he", "her", "here", "hers", "herself", "him", "himself", "his",
            "how", "i", "if", "in", "into", "is", "it", "its", "itself", "just", "me", "more",
            "most", "my", "myself", "no", "nor", "not", "now", "of", "off", "on", "once",
            "only", "or", "other", "our", "ours", "ourselves", "out", "over", "own", "same",
            "she", "should", "so", "some", "such", "than", "that", "the", "their", "theirs",
            "them", "themselves", "then", "there", "these", "they", "this", "those", "through",
            "to", "too", "under", "until", "up", "very", "was", "we", "were", "what", "when",
            "where", "which", "while", "who", "whom", "why", "will", "with", "would", "you",
            "your", "yours", "yourself", "yourselves", "also", "thus", "however", "therefore",
            "moreover", "hence", "thereby", "via", "within", "without", "among",
            "de", "la", "el", "los", "las", "del", "un", "una", "en", "y", "que", "con", "por",
            "para", "es", "son", "como", "su", "sus", "se", "al", "lo");

    private final int minLongitud;
    private final int maxCandidatos;

    public ConceptoExtractorBasico() {
        this(Integer.parseInt(ExploradorConfig.obtener("conceptos.min_longitud", "4")),
                Integer.parseInt(ExploradorConfig.obtener("conceptos.max_candidatos", "50")));
    }

    public ConceptoExtractorBasico(int minLongitud, int maxCandidatos) {
        this.minLongitud = minLongitud;
        this.maxCandidatos = maxCandidatos;
    }

    @Override
    public List<String> extraerCandidatos(String titulo, String resumen) {
        Map<String, Candidato> porClave = new LinkedHashMap<>();
        agregarTexto(porClave, titulo, PESO_TITULO);
        agregarTexto(porClave, resumen, 1.0);

        return porClave.values().stream()
                .sorted(Comparator.comparingDouble((Candidato c) -> c.puntaje).reversed()
                        .thenComparing(Comparator.comparingInt((Candidato c) -> c.longitudTokens).reversed())
                        .thenComparing(c -> c.clave))
                .limit(maxCandidatos)
                .map(Candidato::texto)
                .toList();
    }

    private void agregarTexto(Map<String, Candidato> porClave, String texto, double pesoTexto) {
        if (texto == null || texto.isBlank()) {
            return;
        }
        int posicion = 0;
        for (List<String> tramo : tramos(texto)) {
            for (int n = 1; n <= Math.min(MAX_N, tramo.size()); n++) {
                for (int i = 0; i + n <= tramo.size(); i++) {
                    List<String> tokens = tramo.subList(i, i + n);
                    if (!valido(tokens)) {
                        continue;
                    }
                    String clave = clave(tokens);
                    double aparicion = pesoTexto * pesoPorLongitud(n)
                            + (posicion < VENTANA_TEMPRANA ? PESO_TEMPRANO : 0);
                    Candidato previo = porClave.get(clave);
                    if (previo == null) {
                        porClave.put(clave, new Candidato(String.join(" ", tokens), clave,
                                aparicion, n));
                    } else {
                        porClave.put(clave, new Candidato(previo.texto, clave,
                                previo.puntaje + aparicion, previo.longitudTokens));
                    }
                }
            }
            posicion += tramo.size();
        }
    }

    private List<List<String>> tramos(String texto) {
        List<List<String>> tramos = new ArrayList<>();
        List<String> actual = new ArrayList<>();
        for (String token : texto.split("[^\\p{L}\\p{N}]+")) {
            String limpio = token.replaceAll("'s$", "").replaceAll("'", "");
            if (limpio.isBlank()) {
                continue;
            }
            if (STOPWORDS.contains(limpio.toLowerCase(Locale.ROOT))) {
                cerrarTramo(tramos, actual);
                actual = new ArrayList<>();
            } else {
                actual.add(limpio);
            }
        }
        cerrarTramo(tramos, actual);
        return tramos;
    }

    private void cerrarTramo(List<List<String>> tramos, List<String> tramo) {
        if (!tramo.isEmpty()) {
            tramos.add(tramo);
        }
    }

    private boolean valido(List<String> tokens) {
        for (String token : tokens) {
            if (!tokenValido(token)) {
                return false;
            }
        }
        return true;
    }

    private boolean tokenValido(String token) {
        return token.length() >= minLongitud || esAcronimo(token);
    }

    private boolean esAcronimo(String token) {
        return token.length() >= 3 && token.equals(token.toUpperCase(Locale.ROOT));
    }

    private double pesoPorLongitud(int n) {
        return switch (n) {
            case 2 -> 1.5;
            case 3 -> 2.0;
            default -> 1.0;
        };
    }

    private String clave(List<String> tokens) {
        return String.join(" ", tokens).toLowerCase(Locale.ROOT);
    }

    private record Candidato(String texto, String clave, double puntaje, int longitudTokens) {
    }
}
