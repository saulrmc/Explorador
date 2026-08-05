package explorador.publicaciones.bo;

import explorador.fuentes.modelo.PublicacionBruta;
import explorador.publicaciones.conceptos.ConceptoExtractor;
import explorador.publicaciones.conceptos.ConceptoExtractorBasico;
import explorador.publicaciones.dao.PublicacionDAO;
import explorador.publicaciones.dao.PublicacionDAOImpl;
import explorador.publicaciones.modelo.Publicacion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PublicacionesBOImpl implements PublicacionesBO {

    private final PublicacionDAO publicacionDao;
    private final Formateador formateador;
    private final Ranker ranker;
    private final ConceptoExtractor extractor;

    public PublicacionesBOImpl() {
        this(new PublicacionDAOImpl(), new FormateadorTruncado(),
                new RankerDeterminista(), new ConceptoExtractorBasico());
    }

    public PublicacionesBOImpl(PublicacionDAO publicacionDao, Formateador formateador,
                               Ranker ranker, ConceptoExtractor extractor) {
        this.publicacionDao = publicacionDao;
        this.formateador = formateador;
        this.ranker = ranker;
        this.extractor = extractor;
    }

    @Override
    public List<Publicacion> registrarBrutas(List<PublicacionBruta> brutas) {
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

        Set<String> conceptosBase = new HashSet<>(base.getConceptos());
        Set<String> etiquetasBase = new HashSet<>(base.getEtiquetas());

        return publicacionDao.leerTodos().stream()
                .filter(publicacion -> publicacion.getId() != id)
                .peek(publicacion -> {
                    long comunes = conceptosBase.stream()
                                    .filter(concepto -> publicacion.getConceptos().contains(concepto))
                                    .count()
                            + etiquetasBase.stream()
                                    .filter(etiqueta -> publicacion.getEtiquetas().contains(etiqueta))
                                    .count();
                    publicacion.setScore(comunes);
                })
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(limite)
                .toList();
    }
}
