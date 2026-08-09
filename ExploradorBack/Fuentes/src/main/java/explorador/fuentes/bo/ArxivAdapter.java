package explorador.fuentes.bo;

import explorador.fuentes.modelo.PublicacionOriginal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ArxivAdapter implements FuenteAdapter {

    private static final String BASE_URL = "https://export.arxiv.org/api/query";

    @Override
    public String nombre() {
        return "arxiv";
    }

    @Override
    public List<PublicacionOriginal> consultarRecientes(Set<String> categorias, int maxResultados) {
        if (categorias == null || categorias.isEmpty()) {
            return new ArrayList<>();
        }
        String xml = ejecutarConsulta(construirConsulta(categorias, maxResultados));
        return parsear(xml);
    }

    private String construirConsulta(Set<String> categorias, int maxResultados) {
        StringBuilder filtro = new StringBuilder();
        for (String categoria : categorias) {
            if (filtro.length() > 0) {
                filtro.append("+OR+");
            }
            filtro.append("cat:").append(categoria);
        }
        return BASE_URL + "?search_query=(" + filtro + ")&start=0&max_results=" + maxResultados
                + "&sortBy=submittedDate&sortOrder=descending";
    }

    private String ejecutarConsulta(String url) {
        try {
            HttpClient cliente = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
            HttpRequest peticion = HttpRequest.newBuilder(URI.create(url)).GET().build();
            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() != 200) {
                throw new IllegalStateException("Arxiv respondio con estado: " + respuesta.statusCode());
            }
            return respuesta.body();
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar arxiv", e);
        }
    }

    private List<PublicacionOriginal> parsear(String xml) {
        List<PublicacionOriginal> publicaciones = new ArrayList<>();
        try {
            DocumentBuilderFactory factoria = DocumentBuilderFactory.newInstance();
            factoria.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factoria.setExpandEntityReferences(false);
            Document doc = factoria.newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml)));

            NodeList entradas = doc.getElementsByTagName("entry");
            for (int i = 0; i < entradas.getLength(); i++) {
                Element entrada = (Element) entradas.item(i);

                String idUrl = texto(entrada, "id");
                String id = idUrl.substring(idUrl.lastIndexOf('/') + 1);

                PublicacionOriginal pub = new PublicacionOriginal();
                pub.setIdOrigen(id);
                pub.setFuente(nombre());
                pub.setTitulo(normalizar(texto(entrada, "title")));
                pub.setResumen(normalizar(texto(entrada, "summary")));
                pub.setFechaPublicacion(parsearFecha(texto(entrada, "published")));
                pub.setUrl("https://arxiv.org/abs/" + id);
                pub.setAutores(leerAutores(entrada));
                pub.setEtiquetas(leerEtiquetas(entrada));
                pub.setPalabrasClave(leerEtiquetas(entrada));
                pub.setConfianza(0.5);
                publicaciones.add(pub);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear la respuesta de arxiv", e);
        }
        return publicaciones;
    }

    private List<String> leerAutores(Element entrada) {
        List<String> autores = new ArrayList<>();
        NodeList nodos = entrada.getElementsByTagName("author");
        for (int i = 0; i < nodos.getLength(); i++) {
            Element autor = (Element) nodos.item(i);
            String nombre = normalizar(texto(autor, "name"));
            if (!nombre.isBlank()) {
                autores.add(nombre);
            }
        }
        return autores;
    }

    private List<String> leerEtiquetas(Element entrada) {
        List<String> etiquetas = new ArrayList<>();
        NodeList nodos = entrada.getElementsByTagName("category");
        for (int i = 0; i < nodos.getLength(); i++) {
            Element categoria = (Element) nodos.item(i);
            String termino = categoria.getAttribute("term");
            if (!termino.isBlank()) {
                etiquetas.add(termino);
            }
        }
        return etiquetas;
    }

    private String texto(Element entrada, String etiqueta) {
        NodeList nodos = entrada.getElementsByTagName(etiqueta);
        return nodos.getLength() > 0 ? nodos.item(0).getTextContent() : "";
    }

    private String normalizar(String texto) {
        return texto == null ? "" : texto.replaceAll("\\s+", " ").trim();
    }

    private LocalDate parsearFecha(String fecha) {
        try {
            return OffsetDateTime.parse(fecha).toLocalDate();
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
}
