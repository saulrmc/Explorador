package explorador.publicaciones.bo;

import explorador.publicaciones.modelo.Publicacion;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
        double recencia = recencia(publicacion.getFechaPublicacion());
        double coincidencia = coincidencia(publicacion, keywords);
        return 0.5 * recencia + 0.4 * coincidencia + 0.1 * publicacion.getConfianza();
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
        String texto = (publicacion.getTitulo() + " " + publicacion.getDescripcion()
                + " " + publicacion.getEtiquetas()).toLowerCase();
        int coincidencias = 0;
        int total = 0;
        for (String keyword : keywords) {
            for (String token : keyword.toLowerCase().split("\\s+")) {
                if (token.length() >= 3) {
                    total++;
                    if (texto.contains(token)) {
                        coincidencias++;
                    }
                }
            }
        }
        return total == 0 ? 0.0 : (double) coincidencias / total;
    }
}
