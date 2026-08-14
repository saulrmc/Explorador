package explorador.publicaciones.bo;

import explorador.data.ExploradorConfig;
import explorador.fuentes.modelo.PublicacionOriginal;
import explorador.publicaciones.conceptos.ConceptoExtractor;
import explorador.publicaciones.conceptos.ConceptoExtractorBasico;
import explorador.publicaciones.conceptos.ConceptoResolver;
import explorador.publicaciones.conceptos.DefinicionConcepto;
import explorador.publicaciones.conceptos.FuenteDefinicion;
import explorador.publicaciones.conceptos.WikipediaConceptoResolver;
import explorador.publicaciones.conceptos.WikipediaFuenteDefinicion;
import explorador.publicaciones.dao.PublicacionDAO;
import explorador.publicaciones.dao.PublicacionDAOImpl;
import explorador.publicaciones.modelo.Concepto;
import explorador.publicaciones.modelo.Publicacion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PublicacionesBOImpl implements PublicacionesBO {

    private static final int PESO_ETIQUETA = 2;

    private final PublicacionDAO publicacionDao;
    private final Formateador formateador;
    private final Ranker ranker;
    private final ConceptoExtractor extractor;
    private final ConceptoResolver conceptoResolver;
    private final FuenteDefinicion fuenteDefinicion;

    public PublicacionesBOImpl() {
        this(new PublicacionDAOImpl(), new FormateadorTruncado(),
                new RankerDeterminista(), new ConceptoExtractorBasico(),
                new WikipediaConceptoResolver(), new WikipediaFuenteDefinicion());
    }

    public PublicacionesBOImpl(PublicacionDAO publicacionDao, Formateador formateador,
                               Ranker ranker, ConceptoExtractor extractor) {
        this(publicacionDao, formateador, ranker, extractor,
                new WikipediaConceptoResolver(), new WikipediaFuenteDefinicion());
    }

    public PublicacionesBOImpl(PublicacionDAO publicacionDao, Formateador formateador,
                               Ranker ranker, ConceptoExtractor extractor,
                               ConceptoResolver conceptoResolver, FuenteDefinicion fuenteDefinicion) {
        this.publicacionDao = publicacionDao;
        this.formateador = formateador;
        this.ranker = ranker;
        this.extractor = extractor;
        this.conceptoResolver = conceptoResolver;
        this.fuenteDefinicion = fuenteDefinicion;
    }

    @Override
    public List<Publicacion> registrarBrutas(List<PublicacionOriginal> originales) {
        List<Publicacion> creadas = new ArrayList<>();
        try {
            for (PublicacionOriginal original : originales) {
                if (publicacionDao.existePorOrigen(original.getFuente(), original.getIdOrigen())) {
                    continue;
                }
                Publicacion publicacion = formateador.formatear(original);
                List<String> candidatos = extractor.extraerCandidatos(
                        original.getTitulo(), original.getResumen());
                publicacion.setConceptos(limitarConceptos(conceptoResolver.resolver(candidatos)));
                publicacionDao.crear(publicacion);
                creadas.add(publicacion);
            }
        } finally {
            publicacionDao.guardar();
        }
        return creadas;
    }

    private List<Concepto> limitarConceptos(List<Concepto> conceptos) {
        int max = Integer.parseInt(
                ExploradorConfig.obtener("conceptos.wikipedia.max_conceptos", "8"));
        return conceptos.stream().limit(max).toList();
    }

    @Override
    public List<Publicacion> listarLimitadas(Set<String> categorias, int limite) {
        List<Publicacion> ordenadas = ranker.ordenar(publicacionDao.leerTodos(), categorias);
        return ordenadas.stream().limit(limite).toList();
    }

    @Override
    public List<Publicacion> rankear(List<Publicacion> publicaciones, Set<String> categorias) {
        return ranker.ordenar(publicaciones, categorias);
    }

    @Override
    public List<Publicacion> listar() {
        return publicacionDao.leerTodos();
    }

    @Override
    public Publicacion obtener(int id) {
        return publicacionDao.leer(id);
    }

    @Override
    public List<Publicacion> listarRelacionadas(int id, int limite) {
        Publicacion base = publicacionDao.leer(id);
        if (base == null) {
            return List.of();
        }

        Set<String> etiquetasBase = conjunto(etiquetas(base));
        Set<String> conceptosBase = conjuntoConceptos(base);

        return publicacionDao.leerTodos().stream()
                .filter(publicacion -> publicacion.getId() != id)
                .map(publicacion -> new Relacion(publicacion, puntaje(publicacion, etiquetasBase, conceptosBase)))
                .filter(relacion -> relacion.puntaje() > 0)
                .sorted(Comparator.comparingLong(Relacion::puntaje).reversed())
                .limit(limite)
                .map(Relacion::publicacion)
                .toList();
    }

    private long puntaje(Publicacion publicacion, Set<String> etiquetasBase, Set<String> conceptosBase) {
        Set<String> etiquetas = conjunto(etiquetas(publicacion));
        Set<String> conceptos = conjuntoConceptos(publicacion);
        long etiquetasComunes = etiquetasBase.stream().filter(etiquetas::contains).count();
        long conceptosComunes = conceptosBase.stream().filter(conceptos::contains).count();
        return PESO_ETIQUETA * etiquetasComunes + conceptosComunes;
    }

    private List<String> etiquetas(Publicacion publicacion) {
        if (publicacion.getOriginal() == null) {
            return List.of();
        }
        List<String> etiquetas = publicacion.getOriginal().getEtiquetas();
        return etiquetas == null ? List.of() : etiquetas;
    }

    private Set<String> conjunto(List<String> valores) {
        return valores == null ? Set.of() : new HashSet<>(valores);
    }

    private Set<String> conjuntoConceptos(Publicacion publicacion) {
        List<Concepto> conceptos = publicacion.getConceptos();
        if (conceptos == null) {
            return Set.of();
        }
        return conceptos.stream().map(Concepto::getTermino).collect(Collectors.toSet());
    }

    private record Relacion(Publicacion publicacion, long puntaje) {
    }

    @Override
    public Set<Integer> podar(Set<Integer> protegidos) {
        int dias = Integer.parseInt(
                ExploradorConfig.obtener("publicaciones.retencion_dias", "7"));
        LocalDateTime limite = LocalDateTime.now().minusDays(dias);
        Set<Integer> protegido = protegidos == null ? Set.of() : new HashSet<>(protegidos);

        Set<Integer> removidos = new HashSet<>();
        try {
            for (Publicacion publicacion : publicacionDao.leerTodos()) {
                if (protegido.contains(publicacion.getId())) {
                    continue;
                }
                if (publicacion.getFechaIngreso() != null
                        && publicacion.getFechaIngreso().isBefore(limite)) {
                    publicacionDao.eliminar(publicacion.getId());
                    removidos.add(publicacion.getId());
                }
            }
        } finally {
            publicacionDao.guardar();
        }
        return removidos;
    }

    @Override
    public DefinicionConcepto definirConcepto(String concepto) {
        return fuenteDefinicion.definir(concepto);
    }
}
