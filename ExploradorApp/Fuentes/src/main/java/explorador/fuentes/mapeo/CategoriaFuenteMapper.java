package explorador.fuentes.mapeo;

import explorador.usuario.modelo.CategoriaGenerica;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CategoriaFuenteMapper {

    // mapeo manual: nombreFuente -> (categoriaGenerica -> codigos externos)
    private static final Map<String, Map<CategoriaGenerica, List<String>>> MAPEO = Map.of(
            "arxiv", Map.ofEntries(
                    Map.entry(CategoriaGenerica.COMPUTACION, List.of("cs.AI", "cs.LG", "cs.CL", "cs.CV", "cs.SE", "cs.PL")),
                    Map.entry(CategoriaGenerica.MATEMATICAS, List.of("math.NT", "math.NA", "math.AP", "math.CO", "math.OC")),
                    Map.entry(CategoriaGenerica.FISICA, List.of("physics.class-ph", "physics.comp-ph", "physics.flu-dyn")),
                    Map.entry(CategoriaGenerica.QUIMICA, List.of("physics.chem-ph", "cond-mat.mtrl-sci")),
                    Map.entry(CategoriaGenerica.BIOLOGIA, List.of("q-bio.BM", "q-bio.GN", "q-bio.QM")),
                    Map.entry(CategoriaGenerica.INGENIERIA, List.of("eess.SY", "eess.SP", "cs.RO")),
                    Map.entry(CategoriaGenerica.CIENCIAS_TIERRA, List.of("physics.geo-ph")),
                    Map.entry(CategoriaGenerica.ECONOMIA, List.of("econ.EM", "econ.GN")),
                    Map.entry(CategoriaGenerica.ESTADISTICA, List.of("stat.ML", "stat.ME", "stat.AP"))
            )
    );

    private CategoriaFuenteMapper() {
    }

    // Devuelve codigos externos de una fuente para las categorias genericas dadas (union).
    // Si no hay mapeo para alguna pareja, se ignora. Si faltan datos, devuelve conjunto vacio.
    public static Set<String> mapear(Collection<CategoriaGenerica> genericas, String nombreFuente) {
        if (genericas == null || genericas.isEmpty() || nombreFuente == null || nombreFuente.isBlank()) {
            return Set.of();
        }
        Map<CategoriaGenerica, List<String>> porFuente = MAPEO.get(nombreFuente);
        if (porFuente == null) {
            return Set.of();
        }
        Set<String> codigos = new HashSet<>();
        for (CategoriaGenerica generica : genericas) {
            List<String> mapeados = porFuente.get(generica);
            if (mapeados != null) {
                codigos.addAll(mapeados);
            }
        }
        return codigos;
    }

    public static List<String> codigosDe(CategoriaGenerica generica, String nombreFuente) {
        if (generica == null || nombreFuente == null || nombreFuente.isBlank()) {
            return List.of();
        }
        Map<CategoriaGenerica, List<String>> porFuente = MAPEO.get(nombreFuente);
        if (porFuente == null) {
            return List.of();
        }
        List<String> codigos = porFuente.get(generica);
        return codigos == null ? List.of() : codigos;
    }
}
