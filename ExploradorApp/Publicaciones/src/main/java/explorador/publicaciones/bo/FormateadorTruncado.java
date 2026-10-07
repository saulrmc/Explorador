
package explorador.publicaciones.bo;

import explorador.fuentes.modelo.PublicacionOriginal;


import explorador.publicaciones.modelo.Publicacion;

import java.util.Arrays;

public class FormateadorTruncado implements Formateador {

    private static final int MAX_PALABRAS_TITULO = 10;
    private static final int MAX_PALABRAS_DESCRIPCION = 40;

    @Override
    public Publicacion formatear(PublicacionOriginal original) {
        Publicacion publicacion = new Publicacion();
        publicacion.setId(original.getId());
        publicacion.setTitulo(primerasPalabras(original.getTitulo(), MAX_PALABRAS_TITULO));
        publicacion.setDescripcion(primerasPalabras(original.getResumen(), MAX_PALABRAS_DESCRIPCION));
        publicacion.setScore(0.0);
        publicacion.setOriginal(original);
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