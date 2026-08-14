package explorador.publicaciones.conceptos;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import explorador.data.ExploradorConfig;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WikipediaFuenteDefinicion implements FuenteDefinicion {

    private static final String BASE_URL = "https://%s.wikipedia.org/api/rest_v1/page/summary/";
    private static final String USER_AGENT = "Explorador/1.0 (consultas de conceptos cientificos)";

    private final HttpClient cliente;
    private final ObjectMapper mapper;
    private final String idiomaDefinicion;
    private final String idiomaFallback;
    private final String baseUrlOverride;
    private final int timeoutMs;
    private final Map<String, DefinicionConcepto> cache = new ConcurrentHashMap<>();

    public WikipediaFuenteDefinicion() {
        this(ExploradorConfig.obtener("conceptos.wikipedia.idioma_definicion", "es"),
                ExploradorConfig.obtener("conceptos.wikipedia.idioma_fallback", "en"),
                Integer.parseInt(ExploradorConfig.obtener("conceptos.wikipedia.timeout_ms", "5000")),
                null,
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build());
    }

    WikipediaFuenteDefinicion(String idiomaDefinicion, String idiomaFallback, int timeoutMs,
                              String baseUrlOverride, HttpClient cliente) {
        this.idiomaDefinicion = idiomaDefinicion;
        this.idiomaFallback = idiomaFallback;
        this.timeoutMs = timeoutMs;
        this.baseUrlOverride = baseUrlOverride;
        this.cliente = cliente;
        this.mapper = new ObjectMapper();
    }

    @Override
    public DefinicionConcepto definir(String concepto) {
        if (concepto == null || concepto.isBlank()) {
            return null;
        }
        String clave = concepto.trim().toLowerCase();
        DefinicionConcepto enCache = cache.get(clave);
        if (enCache != null) {
            return enCache;
        }
        DefinicionConcepto definicion = consultar(idiomaDefinicion, concepto);
        if (definicion == null) {
            definicion = consultar(idiomaFallback, concepto);
        }
        if (definicion != null) {
            cache.put(clave, definicion);
        }
        return definicion;
    }

    private DefinicionConcepto consultar(String idioma, String concepto) {
        try {
            HttpRequest peticion = HttpRequest.newBuilder(URI.create(construirUrl(idioma, concepto)))
                    .header("User-Agent", USER_AGENT)
                    .timeout(Duration.ofMillis(timeoutMs))
                    .GET()
                    .build();
            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() != 200) {
                return null;
            }
            return parsear(respuesta.body(), concepto);
        } catch (IOException e) {
            throw new RuntimeException("Error de conexion al consultar la definicion en Wikipedia", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Consulta a Wikipedia interrumpida", e);
        }
    }

    String construirUrl(String idioma, String concepto) {
        String base = baseUrlOverride != null ? baseUrlOverride : BASE_URL.formatted(idioma);
        String codificado = URLEncoder.encode(concepto.trim(), StandardCharsets.UTF_8)
                .replace("+", "%20");
        return base + codificado;
    }

    DefinicionConcepto parsear(String json, String concepto) {
        try {
            JsonNode raiz = mapper.readTree(json);
            String extract = raiz.path("extract").asText(null);
            if (extract == null || extract.isBlank()) {
                return null;
            }
            String titulo = raiz.path("title").asText(concepto.trim());
            String url = raiz.path("content_urls").path("desktop").path("page").asText(null);
            return new DefinicionConcepto(titulo, extract, url);
        } catch (IOException e) {
            throw new RuntimeException("Error al parsear la respuesta de Wikipedia", e);
        }
    }
}
