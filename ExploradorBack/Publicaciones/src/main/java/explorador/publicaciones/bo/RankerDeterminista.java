package explorador.publicaciones.bo;

import explorador.publicaciones.modelo.Publicacion;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RankerDeterminista implements Ranker {

    @Override
    public List<Publicacion> ordenar(List<Publicacion> publicaciones, Set<String> keywords) {
        List<Publicacion> ordenadas = new ArrayList<>(publicaciones);
        for (Publicacion publicacion : ordenadas) {
            publicacion.setScore(calcular(publicacion, keywords));
        }
        ordenadas.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return ordenadas;
    }

    private double calcular(Publicacion publicacion, Set<String> keywords) {
        double recencia = recencia(fechaPublicacion(publicacion));
        double coincidencia = coincidencia(publicacion, keywords);
        return 0.5 * recencia + 0.4 * coincidencia + 0.1 * confianza(publicacion);
    }

    private LocalDate fechaPublicacion(Publicacion publicacion) {
        return publicacion.getOriginal() == null ? null : publicacion.getOriginal().getFechaPublicacion();
    }

    private double confianza(Publicacion publicacion) {
        return publicacion.getOriginal() == null ? 0.0 : publicacion.getOriginal().getConfianza();
    }

    private double recencia(LocalDate fechaPublicacion) {
        if (fechaPublicacion == null) {
            return 0.0;
        }
        long dias = ChronoUnit.DAYS.between(fechaPublicacion, LocalDate.now());
        return 1.0 / (1.0 + dias);
    }

    private double coincidencia(Publicacion publicacion, Set<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return 0.0;
        }
        Set<String> tokensTexto = tokensTexto(publicacion);
        int coincidencias = 0;
        int total = 0;
        for (String keyword : keywords) {
            for (String token : keyword.toLowerCase().split("\\s+")) {
                if (token.length() >= 3) {
                    total++;
                    if (tokensTexto.contains(token)) {
                        coincidencias++;
                    }
                }
            }
        }
        return total == 0 ? 0.0 : (double) coincidencias / total;
    }

    private Set<String> tokensTexto(Publicacion publicacion) {
        String texto = publicacion.getTitulo() + " " + publicacion.getDescripcion()
                + " " + String.join(" ", etiquetasO(publicacion));
        return new HashSet<>(Arrays.asList(texto.toLowerCase().split("[^a-z0-9]+")));
    }

    private List<String> etiquetasO(Publicacion publicacion) {
        if (publicacion.getOriginal() == null) {
            return List.of();
        }
        List<String> etiquetas = publicacion.getOriginal().getEtiquetas();
        return etiquetas == null ? List.of() : etiquetas;
    }
}
