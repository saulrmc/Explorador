package explorador.publicaciones.bo;

import explorador.fuentes.modelo.PublicacionOriginal;
import explorador.publicaciones.dao.PublicacionDAO;
import explorador.publicaciones.dao.PublicacionDAOImpl;
import explorador.publicaciones.modelo.Publicacion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PublicacionesBOImpl implements PublicacionesBO {

    private final PublicacionDAO publicacionDao;
    private final Formateador formateador;
    private final Ranker ranker;

    public PublicacionesBOImpl() {
        this(new PublicacionDAOImpl(), new FormateadorTruncado(), new RankerDeterminista());
    }

    public PublicacionesBOImpl(PublicacionDAO publicacionDao, Formateador formateador,
                               Ranker ranker) {
        this.publicacionDao = publicacionDao;
        this.formateador = formateador;
        this.ranker = ranker;
    }

    @Override
    public List<PublicacionOriginal> registrarBrutas(List<PublicacionOriginal> originales) {
        List<PublicacionOriginal> creadas = new ArrayList<>();
        try {
            for (PublicacionOriginal original : originales) {
                if (publicacionDao.existePorOrigen(original.getFuente(), original.getIdOrigen())) {
                    continue;
                }
                publicacionDao.crear(original);
                creadas.add(original);
            }
        } finally {
            publicacionDao.guardar();
        }
        return creadas;
    }

    @Override
    public List<Publicacion> listarLimitadas(Set<String> categorias, int limite) {
        return ranker.ordenar(listar(), categorias).stream().limit(limite).toList();
    }

    @Override
    public List<Publicacion> rankear(List<Publicacion> publicaciones, Set<String> categorias) {
        return ranker.ordenar(publicaciones, categorias);
    }

    @Override
    public List<Publicacion> listar() {
        return publicacionDao.leerTodos().stream()
                .map(formateador::formatear)
                .toList();
    }

    @Override
    public Publicacion obtener(int id) {
        PublicacionOriginal original = publicacionDao.leer(id);
        return original == null ? null : formateador.formatear(original);
    }

    @Override
    public List<Publicacion> listarRelacionadas(int id, int limite) {
        PublicacionOriginal base = publicacionDao.leer(id);
        if (base == null) {
            return List.of();
        }

        Set<String> etiquetasBase = conjunto(base.getEtiquetas());

        return publicacionDao.leerTodos().stream()
                .filter(original -> original.getId() != id)
                .map(original -> new Relacion(original, coincidencias(original, etiquetasBase)))
                .filter(relacion -> relacion.puntaje() > 0)
                .sorted(Comparator.comparingLong(Relacion::puntaje).reversed())
                .limit(limite)
                .map(relacion -> formateador.formatear(relacion.publicacion()))
                .toList();
    }

    private long coincidencias(PublicacionOriginal original, Set<String> etiquetasBase) {
        Set<String> etiquetas = conjunto(original.getEtiquetas());
        return etiquetasBase.stream().filter(etiquetas::contains).count();
    }

    private Set<String> conjunto(List<String> valores) {
        return valores == null ? Set.of() : new HashSet<>(valores);
    }

    private record Relacion(PublicacionOriginal publicacion, long puntaje) {
    }
}
