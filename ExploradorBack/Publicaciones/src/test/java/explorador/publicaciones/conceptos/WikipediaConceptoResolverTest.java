package explorador.publicaciones.conceptos;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import explorador.publicaciones.modelo.Concepto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WikipediaConceptoResolverTest {

    private static HttpServer servidor;
    private static String baseUrl;
    private static Consumer<HttpExchange> handler;

    @BeforeAll
    static void levantarServidor() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress(0), 0);
        servidor.createContext("/", intercambio -> {
            if (handler != null) {
                handler.accept(intercambio);
            }
        });
        servidor.start();
        baseUrl = "http://localhost:" + servidor.getAddress().getPort();
    }

    @AfterAll
    static void detenerServidor() {
        servidor.stop(0);
    }

    private WikipediaConceptoResolver nuevoResolver() {
        return new WikipediaConceptoResolver("en", 5000, baseUrl,
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build());
    }

    private void responder(String json, int estado) throws IOException {
        handler = intercambio -> {
            try {
                byte[] respuesta = json.getBytes();
                intercambio.sendResponseHeaders(estado, respuesta.length);
                intercambio.getResponseBody().write(respuesta);
                intercambio.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Test
    void conservaSoloLosQueExistenConTituloCanonicoYUrl() throws IOException {
        responder("{\"query\":{\"pages\":["
                + "{\"pageid\":1,\"ns\":0,\"title\":\"Deep learning\"},"
                + "{\"ns\":0,\"title\":\"Invencion inexistente\",\"missing\":\"\"}]}}", 200);

        WikipediaConceptoResolver resolver = nuevoResolver();
        List<Concepto> conceptos = resolver.resolver(List.of("deep learning", "invencion inexistente"));

        assertEquals(1, conceptos.size());
        assertEquals("Deep learning", conceptos.get(0).getTermino());
        assertEquals("https://en.wikipedia.org/wiki/Deep_learning", conceptos.get(0).getUrl());
    }

    @Test
    void resuelveRedirectsAlTituloCanonico() throws IOException {
        responder("{\"query\":{\"redirects\":[{\"from\":\"Neural network\",\"to\":\"Artificial neural network\"}],"
                + "\"pages\":[{\"pageid\":2,\"ns\":0,\"title\":\"Artificial neural network\"}]}}", 200);

        WikipediaConceptoResolver resolver = nuevoResolver();
        List<Concepto> conceptos = resolver.resolver(List.of("Neural network"));

        assertEquals(1, conceptos.size());
        assertEquals("Artificial neural network", conceptos.get(0).getTermino());
        assertEquals("https://en.wikipedia.org/wiki/Artificial_neural_network", conceptos.get(0).getUrl());
    }

    @Test
    void cacheEvitaRepetirLaConsulta() throws IOException {
        int[] llamadas = {0};
        handler = intercambio -> {
            llamadas[0]++;
            try {
                String json = "{\"query\":{\"pages\":[{\"pageid\":3,\"ns\":0,\"title\":\"Learning\"}]}}";
                byte[] respuesta = json.getBytes();
                intercambio.sendResponseHeaders(200, respuesta.length);
                intercambio.getResponseBody().write(respuesta);
                intercambio.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        WikipediaConceptoResolver resolver = nuevoResolver();
        resolver.resolver(List.of("learning"));
        List<Concepto> segundos = resolver.resolver(List.of("learning"));

        assertEquals(1, llamadas[0]);
        assertEquals("Learning", segundos.get(0).getTermino());
    }

    @Test
    void listaVaciaONulaProduceListaVacia() {
        WikipediaConceptoResolver resolver = nuevoResolver();

        assertTrue(resolver.resolver(null).isEmpty());
        assertTrue(resolver.resolver(List.of()).isEmpty());
        assertTrue(resolver.resolver(Arrays.asList("  ", null)).isEmpty());
    }

    @Test
    void degradaConConceptosSinUrlSiLaConsultaFalla() throws IOException {
        responder("{}", 500);

        WikipediaConceptoResolver resolver = nuevoResolver();
        List<Concepto> conceptos = resolver.resolver(List.of("cualquier termino"));

        assertEquals(1, conceptos.size());
        assertEquals("cualquier termino", conceptos.get(0).getTermino());
        assertTrue(conceptos.get(0).getUrl() == null);
    }
}
