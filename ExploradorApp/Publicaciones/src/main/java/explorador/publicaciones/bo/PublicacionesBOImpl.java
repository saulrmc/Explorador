package explorador.publicaciones.bo;

import explorador.fuentes.modelo.PublicacionOriginal;
import explorador.publicaciones.dao.PublicacionDAO;
import explorador.publicaciones.dao.PublicacionDAOImpl;
import explorador.publicaciones.modelo.EstadoPublicacion;
import explorador.publicaciones.modelo.Publicacion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PublicacionesBOImpl implements PublicacionesBO {

    private final PublicacionDAO publicacionDao;

    public PublicacionesBOImpl() {
        this(new PublicacionDAOImpl());
    }

    public PublicacionesBOImpl(PublicacionDAO publicacionDao) {
        this.publicacionDao = publicacionDao;
    }

    @Override
    public List<Publicacion> registrar(List<PublicacionOriginal> originales) {
        List<Publicacion> creadas = new ArrayList<>();
        for (PublicacionOriginal original : originales) {
            if (publicacionDao.existePorUrl(original.getUrl())) {
                continue;
            }
            creadas.add(publicacionDao.crear(original));
        }
        return creadas;
    }

    @Override
    public List<Publicacion> listar() {
        return publicacionDao.leerTodos();
    }

    @Override
    public Publicacion obtener(int id) {
        Publicacion publicacion = publicacionDao.leer(id);
        if (publicacion == null) {
            return null;
        }
        if (publicacion.getEstadoPublicacion() == EstadoPublicacion.NO_VISTO) {
            publicacion.setEstadoPublicacion(EstadoPublicacion.VISTO);
            publicacionDao.actualizar(publicacion);
        }
        return publicacion;
    }

    @Override
    public List<Publicacion> listarRelacionadas(int id, int limite) {
        Publicacion base = publicacionDao.leer(id);
        if (base == null) {
            return List.of();
        }

        Set<String> etiquetasBase = conjunto(base.getEtiquetas());

        return publicacionDao.leerTodos().stream()
                .filter(publicacion -> publicacion.getId() != id)
                .map(publicacion -> new Relacion(publicacion, coincidencias(publicacion, etiquetasBase)))
                .filter(relacion -> relacion.puntaje() > 0)
                .sorted(Comparator.comparingLong(Relacion::puntaje).reversed())
                .limit(limite)
                .map(Relacion::publicacion)
                .toList();
    }

    private long coincidencias(Publicacion publicacion, Set<String> etiquetasBase) {
        Set<String> etiquetas = conjunto(publicacion.getEtiquetas());
        return etiquetasBase.stream().filter(etiquetas::contains).count();
    }

    private Set<String> conjunto(List<String> valores) {
        return valores == null ? Set.of() : new HashSet<>(valores);
    }

    private record Relacion(Publicacion publicacion, long puntaje) {
    }
}
