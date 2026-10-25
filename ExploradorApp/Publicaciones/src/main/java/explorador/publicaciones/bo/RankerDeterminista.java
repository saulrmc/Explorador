package explorador.publicaciones.bo;

import explorador.publicaciones.modelo.Publicacion;
import explorador.publicaciones.modelo.PublicacionRankeada;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RankerDeterminista implements Ranker {

    @Override
    public List<PublicacionRankeada> ordenar(List<Publicacion> publicaciones, Set<String> categorias) {
        List<PublicacionRankeada> ordenadas = new ArrayList<>();
        for (Publicacion publicacion : publicaciones) {
            PublicacionRankeada rankeada = new PublicacionRankeada(publicacion);
            rankeada.setScore(calcular(publicacion, categorias));
            ordenadas.add(rankeada);
        }
        ordenadas.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return ordenadas;
    }

    private double calcular(Publicacion publicacion, Set<String> categorias) {
        double recencia = recencia(fechaPublicacion(publicacion));
        double coincidencia = coincidencia(publicacion, categorias);
        return 0.6 * recencia + 0.4 * coincidencia;
    }

    private LocalDate fechaPublicacion(Publicacion publicacion) {
        return publicacion == null ? null : publicacion.getFechaPublicacion();
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
        List<String> etiquetas = publicacion.getEtiquetas();
        return etiquetas == null ? Set.of() : new HashSet<>(etiquetas);
    }
}
