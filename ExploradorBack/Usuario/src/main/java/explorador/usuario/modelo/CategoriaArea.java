package explorador.usuario.modelo;

import java.util.List;

public enum CategoriaArea {
    COMPUTACION("Computacion", List.of("cs.AI", "cs.LG", "cs.CL", "cs.CV", "cs.SE", "cs.PL")),
    MATEMATICAS("Matematicas", List.of("math.NT", "math.NA", "math.AP", "math.CO", "math.OC")),
    FISICA("Fisica", List.of("physics.class-ph", "physics.comp-ph", "physics.flu-dyn")),
    QUIMICA("Quimica", List.of("physics.chem-ph", "cond-mat.mtrl-sci")),
    BIOLOGIA("Biologia", List.of("q-bio.BM", "q-bio.GN", "q-bio.QM")),
    MEDICINA("Medicina", List.of()),
    INGENIERIA("Ingenieria", List.of("eess.SY", "eess.SP", "cs.RO")),
    CIENCIAS_TIERRA("Ciencias de la Tierra", List.of("physics.geo-ph")),
    ECONOMIA("Economia", List.of("econ.EM", "econ.GN")),
    ESTADISTICA("Estadistica", List.of("stat.ML", "stat.ME", "stat.AP"));

    private final String nombre;
    private final List<String> arxiv;

    CategoriaArea(String nombre, List<String> arxiv) {
        this.nombre = nombre;
        this.arxiv = arxiv;
    }

    public String getNombre() {
        return nombre;
    }

    public List<String> getArxiv() {
        return arxiv;
    }
}
