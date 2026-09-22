package explorador.publicaciones.bo;

import explorador.publicaciones.modelo.Publicacion;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RankerDeterminista implements Ranker {

    @Override
    public List<Publicacion> ordenar(List<Publicacion> publicaciones, Set<String> categorias) {
        List<Publicacion> ordenadas = new ArrayList<>(publicaciones);
        for (Publicacion publicacion : ordenadas) {
            publicacion.setScore(calcular(publicacion, categorias));
        }
        ordenadas.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return ordenadas;
    }

    private double calcular(Publicacion publicacion, Set<String> categorias) {
        double recencia = recencia(fechaPublicacion(publicacion));
        double coincidencia = coincidencia(publicacion, categorias);
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

    private double coincidencia(Publicacion publicacion, Set<String> categorias) {
        if (categorias == null || categorias.isEmpty()) {
            return 0.0;
        }
        Set<String> etiquetas = etiquetas(publicacion);
        if (etiquetas.isEmpty()) {
            return 0.0;
        }
        long coincidencias = etiquetas.stream().filter(categorias::contains).count();
        return (double) coincidencias / etiquetas.size();
    }

    private Set<String> etiquetas(Publicacion publicacion) {
        if (publicacion.getOriginal() == null) {
            return Set.of();
        }
        List<String> etiquetas = publicacion.getOriginal().getEtiquetas();
        return etiquetas == null ? Set.of() : new HashSet<>(etiquetas);
    }
}
