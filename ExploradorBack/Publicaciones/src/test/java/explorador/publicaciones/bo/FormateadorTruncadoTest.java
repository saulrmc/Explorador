package explorador.publicaciones.bo;

import explorador.fuentes.modelo.PublicacionBruta;
import explorador.publicaciones.modelo.Publicacion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormateadorTruncadoTest {

    private final FormateadorTruncado formateador = new FormateadorTruncado();

    @Test
    void tituloSeTruncaAMaximo10Palabras() {
        PublicacionBruta bruta = bruta(larga("palabra ", 15), larga("concepto ", 60));

        Publicacion publicacion = formateador.formatear(bruta);

        assertEquals(10, contarPalabras(publicacion.getTitulo()));
    }

    @Test
    void descripcionSeTruncaAMaximo40Palabras() {
        PublicacionBruta bruta = bruta(larga("palabra ", 5), larga("concepto ", 60));

        Publicacion publicacion = formateador.formatear(bruta);

        assertEquals(40, contarPalabras(publicacion.getDescripcion()));
    }

    @Test
    void textosCortosNoSeModifican() {
        PublicacionBruta bruta = bruta("Titulo corto", "Resumen breve");

        Publicacion publicacion = formateador.formatear(bruta);

        assertEquals("Titulo corto", publicacion.getTitulo());
        assertEquals("Resumen breve", publicacion.getDescripcion());
    }

    @Test
    void conservaTituloYResumenOriginales() {
        String titulo = larga("palabra ", 15);
        String resumen = larga("concepto ", 60);
        PublicacionBruta bruta = bruta(titulo, resumen);

        Publicacion publicacion = formateador.formatear(bruta);

        assertEquals(titulo, publicacion.getTituloOriginal());
        assertEquals(resumen, publicacion.getResumenOriginal());
        assertTrue(publicacion.getFechaIngreso() != null);
    }

    @Test
    void textosNulosOTextosConEspaciosProducenCadenaVacia() {
        PublicacionBruta bruta = bruta(null, "   ");

        Publicacion publicacion = formateador.formatear(bruta);

        assertEquals("", publicacion.getTitulo());
        assertEquals("", publicacion.getDescripcion());
    }

    private PublicacionBruta bruta(String titulo, String resumen) {
        PublicacionBruta bruta = new PublicacionBruta();
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
