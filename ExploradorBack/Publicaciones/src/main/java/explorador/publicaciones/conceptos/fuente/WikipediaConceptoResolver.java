package explorador.publicaciones.conceptos.fuente;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import explorador.data.ExploradorConfig;
import explorador.publicaciones.conceptos.ConceptoResolver;
import explorador.publicaciones.modelo.Concepto;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WikipediaConceptoResolver implements ConceptoResolver {

    private static final String API_URL = "https://%s.wikipedia.org/w/api.php";
    private static final String USER_AGENT = "Explorador/1.0 (validacion de conceptos cientificos)";
    private static final int MAX_TITULOS_POR_PETICION = 50;

    private final HttpClient cliente;
    private final ObjectMapper mapper;
    private final String idioma;
    private final String baseUrl;
    private final int timeoutMs;
    private final Map<String, Optional<Concepto>> cache = new ConcurrentHashMap<>();

    public WikipediaConceptoResolver() {
        this(ExploradorConfig.obtener("conceptos.wikipedia.idioma_validacion", "en"),
                Integer.parseInt(ExploradorConfig.obtener("conceptos.wikipedia.timeout_ms", "5000")),
                null,
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build());
    }

    WikipediaConceptoResolver(String idioma, int timeoutMs, String baseUrlOverride, HttpClient cliente) {
        this.idioma = idioma;
        this.timeoutMs = timeoutMs;
        this.cliente = cliente;
        this.mapper = new ObjectMapper();
        this.baseUrl = baseUrlOverride != null ? baseUrlOverride : API_URL.formatted(idioma);
    }

    @Override
    public List<Concepto> resolver(List<String> candidatos) {
        if (candidatos == null || candidatos.isEmpty()) {
            return List.of();
        }
        List<String> unicos = new ArrayList<>(new LinkedHashSet<>(candidatos.stream()
                .filter(candidato -> candidato != null && !candidato.isBlank())
                .toList()));

        List<Concepto> resultado = new ArrayList<>();
        List<String> pendientes = new ArrayList<>();
        for (String candidato : unicos) {
            Optional<Concepto> enCache = cache.get(candidato);
            if (enCache != null) {
                enCache.ifPresent(resultado::add);
            } else {
                pendientes.add(candidato);
            }
        }

        for (int i = 0; i < pendientes.size(); i += MAX_TITULOS_POR_PETICION) {
            List<String> lote = pendientes.subList(i,
                    Math.min(i + MAX_TITULOS_POR_PETICION, pendientes.size()));
            resolverLote(lote, resultado);
        }
        return resultado;
    }

    private void resolverLote(List<String> lote, List<Concepto> resultado) {
        try {
            String url = baseUrl + "?action=query&format=json&formatversion=2&redirects=1&titles="
                    + codificarTitulos(lote);
            HttpRequest peticion = HttpRequest.newBuilder(URI.create(url))
                    .header("User-Agent", USER_AGENT)
                    .timeout(Duration.ofMillis(timeoutMs))
                    .GET()
                    .build();
            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() != 200) {
                degradar(lote, resultado);
                return;
            }
            Map<String, Concepto> porTermino = parsear(respuesta.body(), lote);
            for (String candidato : lote) {
                Concepto concepto = porTermino.get(candidato);
                cache.put(candidato, Optional.ofNullable(concepto));
                if (concepto != null) {
                    resultado.add(concepto);
                }
            }
        } catch (IOException e) {
            degradar(lote, resultado);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            degradar(lote, resultado);
        }
    }

    private void degradar(List<String> lote, List<Concepto> resultado) {
        for (String candidato : lote) {
            resultado.add(new Concepto(candidato, null));
        }
    }

    private String codificarTitulos(List<String> titulos) {
        return URLEncoder.encode(String.join("|", titulos), StandardCharsets.UTF_8)
                .replace("+", "%20");
    }

    Map<String, Concepto> parsear(String json, List<String> lote) throws IOException {
        JsonNode query = mapper.readTree(json).path("query");

        Map<String, String> resolucion = new LinkedHashMap<>();
        for (JsonNode redireccion : query.path("redirects")) {
            resolucion.put(redireccion.path("from").asText(), redireccion.path("to").asText());
        }
        for (JsonNode normalizado : query.path("normalized")) {
            resolucion.put(normalizado.path("from").asText(), normalizado.path("to").asText());
        }

        Map<String, String> existentes = new LinkedHashMap<>();
        for (JsonNode pagina : query.path("pages")) {
            if (!pagina.has("missing")) {
                String titulo = pagina.path("title").asText();
                existentes.put(titulo.toLowerCase(Locale.ROOT), titulo);
            }
        }

        Map<String, Concepto> resultado = new LinkedHashMap<>();
        for (String candidato : lote) {
            String titulo = resolverTitulo(candidato, resolucion);
            String canonico = existentes.get(titulo.toLowerCase(Locale.ROOT));
            if (canonico != null) {
                resultado.put(candidato, new Concepto(canonico, urlDe(canonico)));
            }
        }
        return resultado;
    }

    private String resolverTitulo(String candidato, Map<String, String> resolucion) {
        String actual = candidato;
        Set<String> visitados = new HashSet<>();
        while (resolucion.containsKey(actual) && visitados.add(actual)) {
            actual = resolucion.get(actual);
        }
        return actual;
    }

    private String urlDe(String titulo) {
        String codificado = URLEncoder.encode(titulo.replace(' ', '_'), StandardCharsets.UTF_8)
                .replace("+", "%20");
        return "https://" + idioma + ".wikipedia.org/wiki/" + codificado;
    }
}
