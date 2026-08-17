package explorador.publicaciones.conceptos.fuente;

import com.sun.net.httpserver.HttpServer;
import explorador.publicaciones.modelo.DefinicionConcepto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WikipediaFuenteDefinicionTest {

    private static HttpServer servidor;
    private static String baseUrl;

    @BeforeAll
    static void levantarServidor() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress(0), 0);
        servidor.start();
        baseUrl = "http://localhost:" + servidor.getAddress().getPort() + "/summary/";
    }

    @AfterAll
    static void detenerServidor() {
        servidor.stop(0);
    }

    @Test
    void construirUrlCodificaElConcepto() {
        WikipediaFuenteDefinicion fuente = nuevaFuente("es", baseUrl);

        assertEquals(baseUrl + "redes%20neuronales", fuente.construirUrl("es", "redes neuronales"));
    }

    @Test
    void parsearExtraeDefinicionYUriDelResumen() {
        WikipediaFuenteDefinicion fuente = nuevaFuente("es", baseUrl);
        String json = "{\"title\":\"Red neuronal\","
                + "\"extract\":\"Una red neuronal es un modelo computacional.\","
                + "\"content_urls\":{\"desktop\":{\"page\":\"https://es.wikipedia.org/wiki/Red_neuronal\"}}}";

        DefinicionConcepto definicion = fuente.parsear(json, "red neuronal");

        assertEquals("Red neuronal", definicion.getConcepto());
        assertEquals("Una red neuronal es un modelo computacional.", definicion.getDefinicion());
        assertEquals("https://es.wikipedia.org/wiki/Red_neuronal", definicion.getUrl());
    }

    @Test
    void parsearDevuelveNullCuandoNoHayExtract() {
        WikipediaFuenteDefinicion fuente = nuevaFuente("es", baseUrl);

        assertNull(fuente.parsear("{\"title\":\"Sin resumen\"}", "sin"));
    }

    @Test
    void definirConsultaElServidorYExtraeLaDefinicion() throws IOException {
        String json = "{\"title\":\"Aprendizaje\","
                + "\"extract\":\"El aprendizaje es la adquisicion de conocimiento.\"}";
        servidor.createContext("/summary/aprendizaje", intercambio -> {
            byte[] respuesta = json.getBytes();
            intercambio.sendResponseHeaders(200, respuesta.length);
            intercambio.getResponseBody().write(respuesta);
            intercambio.close();
        });

        WikipediaFuenteDefinicion fuente = nuevaFuente("es", baseUrl);
        DefinicionConcepto definicion = fuente.definir("aprendizaje");

        assertEquals("Aprendizaje", definicion.getConcepto());
        assertEquals("El aprendizaje es la adquisicion de conocimiento.", definicion.getDefinicion());
    }

    @Test
    void definirDevuelveNullAnteRespuesta404() throws IOException {
        servidor.createContext("/summary/inexistente", intercambio -> {
            byte[] respuesta = "{}".getBytes();
            intercambio.sendResponseHeaders(404, respuesta.length);
            intercambio.getResponseBody().write(respuesta);
            intercambio.close();
        });

        WikipediaFuenteDefinicion fuente = nuevaFuente("es", baseUrl);

        assertNull(fuente.definir("inexistente"));
    }

    @Test
    void siNoHayDefinicionEnElIdiomaPrincipalUsaElFallback() throws IOException {
        int[] llamadas = {0};
        servidor.createContext("/summary/missing", intercambio -> {
            if (llamadas[0]++ == 0) {
                byte[] respuesta = "{}".getBytes();
                intercambio.sendResponseHeaders(404, respuesta.length);
            } else {
                String jsonEn = "{\"title\":\"Missing\",\"extract\":\"En ingles existe.\"}";
                byte[] respuesta = jsonEn.getBytes();
                intercambio.sendResponseHeaders(200, respuesta.length);
                intercambio.getResponseBody().write(respuesta);
            }
            intercambio.close();
        });

        WikipediaFuenteDefinicion fuente = nuevaFuente("es", baseUrl);
        DefinicionConcepto definicion = fuente.definir("missing");

        assertEquals("En ingles existe.", definicion.getDefinicion());
    }

    @Test
    void cacheEvitaRepetirLaConsulta() throws IOException {
        int[] llamadas = {0};
        servidor.createContext("/summary/cacheado", intercambio -> {
            llamadas[0]++;
            byte[] respuesta = "{\"extract\":\"Definicion cacheada.\"}".getBytes();
            intercambio.sendResponseHeaders(200, respuesta.length);
            intercambio.getResponseBody().write(respuesta);
            intercambio.close();
        });

        WikipediaFuenteDefinicion fuente = nuevaFuente("es", baseUrl);
        fuente.definir("cacheado");
        DefinicionConcepto segunda = fuente.definir("cacheado");

        assertEquals("Definicion cacheada.", segunda.getDefinicion());
        assertEquals(1, llamadas[0]);
    }

    @Test
    void conceptoNuloOVacioDevuelveNull() {
        WikipediaFuenteDefinicion fuente = nuevaFuente("es", baseUrl);

        assertNull(fuente.definir(null));
        assertNull(fuente.definir("   "));
    }

    private WikipediaFuenteDefinicion nuevaFuente(String idioma, String urlBase) {
        return new WikipediaFuenteDefinicion(idioma, "en", 5000, urlBase,
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build());
    }
}
