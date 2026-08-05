package explorador.publicaciones.bo;

import explorador.fuentes.modelo.PublicacionBruta;
import explorador.publicaciones.modelo.Publicacion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class FormateadorTruncado implements Formateador {

    private static final int MAX_PALABRAS_TITULO = 10;
    private static final int MAX_PALABRAS_DESCRIPCION = 40;

    @Override
    public Publicacion formatear(PublicacionBruta bruta) {
        Publicacion publicacion = new Publicacion();
        publicacion.setIdOrigen(bruta.getIdOrigen());
        publicacion.setFuente(bruta.getFuente());
        publicacion.setTituloOriginal(bruta.getTitulo());
        publicacion.setTitulo(primerasPalabras(bruta.getTitulo(), MAX_PALABRAS_TITULO));
        publicacion.setResumenOriginal(bruta.getResumen());
        publicacion.setDescripcion(primerasPalabras(bruta.getResumen(), MAX_PALABRAS_DESCRIPCION));
        publicacion.setUrl(bruta.getUrl());
        publicacion.setAutores(bruta.getAutores());
        publicacion.setEtiquetas(bruta.getEtiquetas());
        publicacion.setConfianza(bruta.getConfianza());
        publicacion.setFechaPublicacion(bruta.getFechaPublicacion());
        publicacion.setFechaIngreso(LocalDateTime.now());
        publicacion.setScore(0.0);
        publicacion.setConceptos(new ArrayList<>());
        return publicacion;
    }

    private String primerasPalabras(String texto, int max) {
        if (texto == null || texto.isBlank()) {
            return "";
        }
        String normalizado = texto.replaceAll("\\s+", " ").trim();
        String[] palabras = normalizado.split(" ");
        if (palabras.length <= max) {
            return normalizado;
        }
        return String.join(" ", Arrays.copyOf(palabras, max));
    }
}
