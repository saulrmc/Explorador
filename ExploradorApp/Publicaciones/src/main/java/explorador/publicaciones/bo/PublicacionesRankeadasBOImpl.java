package explorador.publicaciones.bo;

import explorador.publicaciones.modelo.Publicacion;
import explorador.publicaciones.modelo.PublicacionRankeada;

import java.util.List;
import java.util.Set;

public class PublicacionesRankeadasBOImpl implements PublicacionesRankeadasBO {

    private final PublicacionesBO publicacionesBO;
    private final Ranker ranker;

    public PublicacionesRankeadasBOImpl() {
        this(new PublicacionesBOImpl(), new RankerDeterminista());
    }

    public PublicacionesRankeadasBOImpl(PublicacionesBO publicacionesBO, Ranker ranker) {
        this.publicacionesBO = publicacionesBO;
        this.ranker = ranker;
    }

    @Override
    public List<PublicacionRankeada> listarRankeadas(Set<String> categorias, int limite) {
        return ranker.ordenar(publicacionesBO.listar(), categorias).stream()
                .limit(limite)
                .toList();
    }

    @Override
    public List<PublicacionRankeada> rankear(List<Publicacion> publicaciones, Set<String> categorias) {
        return ranker.ordenar(publicaciones, categorias);
    }
}
