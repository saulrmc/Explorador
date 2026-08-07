package explorador.publicaciones.bo;

import explorador.data.ExploradorConfig;
import explorador.fuentes.modelo.PublicacionBruta;
import explorador.publicaciones.conceptos.ConceptoExtractor;
import explorador.publicaciones.conceptos.ConceptoExtractorBasico;
import explorador.publicaciones.conceptos.DefinicionConcepto;
import explorador.publicaciones.conceptos.FuenteDefinicion;
import explorador.publicaciones.conceptos.WikipediaFuenteDefinicion;
import explorador.publicaciones.dao.PublicacionDAO;
import explorador.publicaciones.dao.PublicacionDAOImpl;
import explorador.publicaciones.modelo.Publicacion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PublicacionesBOImpl implements PublicacionesBO {

    private static final Object BLOQUEO_ESCRITURA = new Object();
    private static final int PESO_ETIQUETA = 2;

    private final PublicacionDAO publicacionDao;
    private final Formateador formateador;
    private final Ranker ranker;
    private final ConceptoExtractor extractor;
    private final FuenteDefinicion fuenteDefinicion;

    public PublicacionesBOImpl() {
        this(new PublicacionDAOImpl(), new FormateadorTruncado(),
                new RankerDeterminista(), new ConceptoExtractorBasico(),
                new WikipediaFuenteDefinicion());
    }

    public PublicacionesBOImpl(PublicacionDAO publicacionDao, Formateador formateador,
                               Ranker ranker, ConceptoExtractor extractor) {
        this(publicacionDao, formateador, ranker, extractor, new WikipediaFuenteDefinicion());
    }

    public PublicacionesBOImpl(PublicacionDAO publicacionDao, Formateador formateador,
                               Ranker ranker, ConceptoExtractor extractor,
                               FuenteDefinicion fuenteDefinicion) {
        this.publicacionDao = publicacionDao;
        this.formateador = formateador;
        this.ranker = ranker;
        this.extractor = extractor;
        this.fuenteDefinicion = fuenteDefinicion;
    }

    @Override
    public List<Publicacion> registrarBrutas(List<PublicacionBruta> brutas) {
        synchronized (BLOQUEO_ESCRITURA) {
            List<Publicacion> creadas = new ArrayList<>();
            for (PublicacionBruta bruta : brutas) {
                if (publicacionDao.existePorOrigen(bruta.getFuente(), bruta.getIdOrigen())) {
                    continue;
                }
                Publicacion publicacion = formateador.formatear(bruta);
                publicacion.setConceptos(extractor.extraer(bruta.getResumen()));
                publicacionDao.crear(publicacion);
                creadas.add(publicacion);
            }
            return creadas;
        }
    }

    @Override
    public List<Publicacion> listarLimitadas(Set<String> keywords, int limite) {
        List<Publicacion> ordenadas = ranker.ordenar(publicacionDao.leerTodos(), keywords);
        return ordenadas.stream().limit(limite).toList();
    }

    @Override
    public List<Publicacion> rankear(List<Publicacion> publicaciones, Set<String> keywords) {
        return ranker.ordenar(publicaciones, keywords);
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

        Set<String> etiquetasBase = conjunto(base.getEtiquetas());
        Set<String> conceptosBase = conjunto(base.getConceptos());

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
        Set<String> etiquetas = conjunto(publicacion.getEtiquetas());
        Set<String> conceptos = conjunto(publicacion.getConceptos());
        long etiquetasComunes = etiquetasBase.stream().filter(etiquetas::contains).count();
        long conceptosComunes = conceptosBase.stream().filter(conceptos::contains).count();
        return PESO_ETIQUETA * etiquetasComunes + conceptosComunes;
    }

    private Set<String> conjunto(List<String> valores) {
        return valores == null ? Set.of() : new HashSet<>(valores);
    }

    private record Relacion(Publicacion publicacion, long puntaje) {
    }

    @Override
    public Set<Integer> podar(Set<Integer> protegidos) {
        int dias = Integer.parseInt(
                ExploradorConfig.obtener("publicaciones.retencion_dias", "7"));
        LocalDateTime limite = LocalDateTime.now().minusDays(dias);
        Set<Integer> protegido = protegidos == null ? Set.of() : new HashSet<>(protegidos);

        synchronized (BLOQUEO_ESCRITURA) {
            Set<Integer> removidos = new HashSet<>();
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
            return removidos;
        }
    }

    @Override
    public DefinicionConcepto definirConcepto(String concepto) {
        return fuenteDefinicion.definir(concepto);
    }
}
