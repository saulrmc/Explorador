package explorador.publicaciones.bo;

import explorador.fuentes.modelo.PublicacionOriginal;
import explorador.publicaciones.modelo.Publicacion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormateadorTruncadoTest {

    private final FormateadorTruncado formateador = new FormateadorTruncado();

    @Test
    void tituloSeTruncaAMaximo10Palabras() {
        PublicacionOriginal bruta = bruta(larga("palabra ", 15), larga("concepto ", 60));

        Publicacion publicacion = formateador.formatear(bruta);

        assertEquals(10, contarPalabras(publicacion.getTitulo()));
    }

    @Test
    void descripcionSeTruncaAMaximo40Palabras() {
        PublicacionOriginal bruta = bruta(larga("palabra ", 5), larga("concepto ", 60));

        Publicacion publicacion = formateador.formatear(bruta);

        assertEquals(40, contarPalabras(publicacion.getDescripcion()));
    }

    @Test
    void textosCortosNoSeModifican() {
        PublicacionOriginal bruta = bruta("Titulo corto", "Resumen breve");

        Publicacion publicacion = formateador.formatear(bruta);

        assertEquals("Titulo corto", publicacion.getTitulo());
        assertEquals("Resumen breve", publicacion.getDescripcion());
    }

    @Test
    void conservaTituloYResumenOriginales() {
        String titulo = larga("palabra ", 15);
        String resumen = larga("concepto ", 60);
        PublicacionOriginal bruta = bruta(titulo, resumen);

        Publicacion publicacion = formateador.formatear(bruta);

        assertEquals(titulo, publicacion.getOriginal().getTitulo());
        assertEquals(resumen, publicacion.getOriginal().getResumen());
        assertTrue(publicacion.getFechaIngreso() != null);
    }

    @Test
    void textosNulosOTextosConEspaciosProducenCadenaVacia() {
        PublicacionOriginal bruta = bruta(null, "   ");

        Publicacion publicacion = formateador.formatear(bruta);

        assertEquals("", publicacion.getTitulo());
        assertEquals("", publicacion.getDescripcion());
    }

    private PublicacionOriginal bruta(String titulo, String resumen) {
        PublicacionOriginal bruta = new PublicacionOriginal();
        bruta.setIdOrigen("1234");
        bruta.setFuente("arxiv");
        bruta.setTitulo(titulo);
        bruta.setResumen(resumen);
        return bruta;
    }

    private String larga(String palabra, int veces) {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < veces; i++) {
            texto.append(palabra);
        }
        return texto.toString().trim();
    }

    private int contarPalabras(String texto) {
        return texto.isBlank() ? 0 : texto.trim().split("\\s+").length;
    }
}
