package explorador.fuentes.bo;

import explorador.data.ExploradorConfig;
import explorador.fuentes.dao.CheckpointDAO;
import explorador.fuentes.dao.CheckpointDAOImpl;
import explorador.fuentes.modelo.CheckpointFuente;
import explorador.fuentes.modelo.PublicacionBruta;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FuentesBOImpl implements FuentesBO {

    private final FuenteAdapter adapter;
    private final CheckpointDAO checkpointDao;
    private final FiltroRelevancia filtro;

    public FuentesBOImpl() {
        this(new ArxivAdapter(), new CheckpointDAOImpl(), new FiltroRelevancia());
    }

    public FuentesBOImpl(FuenteAdapter adapter, CheckpointDAO checkpointDao, FiltroRelevancia filtro) {
        this.adapter = adapter;
        this.checkpointDao = checkpointDao;
        this.filtro = filtro;
    }

    @Override
    public List<PublicacionBruta> procesar(Set<String> arxivCategorias, Set<String> keywords) {
        int maxResultados = Integer.parseInt(
                ExploradorConfig.obtener("fuente.arxiv.consulta_max", "50"));

        CheckpointFuente checkpoint = checkpointDao.leer(adapter.nombre());
        List<PublicacionBruta> recientes = adapter.consultarRecientes(arxivCategorias, maxResultados);

        List<PublicacionBruta> nuevas = new ArrayList<>();
        for (PublicacionBruta pub : recientes) {
            if (!checkpoint.getIdsVistos().contains(pub.getIdOrigen())) {
                nuevas.add(pub);
            }
        }

        for (PublicacionBruta pub : nuevas) {
            checkpoint.getIdsVistos().add(pub.getIdOrigen());
        }
        checkpoint.setFechaUltimaConsulta(LocalDateTime.now());
        checkpointDao.escribir(checkpoint);

        return filtro.filtrar(nuevas, keywords);
    }
}
