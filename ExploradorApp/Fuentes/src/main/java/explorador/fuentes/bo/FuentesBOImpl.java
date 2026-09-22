package explorador.fuentes.bo;

import explorador.data.ExploradorConfig;
import explorador.fuentes.dao.CheckpointDAO;
import explorador.fuentes.dao.CheckpointDAOImpl;
import explorador.fuentes.modelo.CheckpointFuente;
import explorador.fuentes.modelo.PublicacionOriginal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FuentesBOImpl implements FuentesBO {

    private final FuenteAdapter adapter;
    private final CheckpointDAO checkpointDao;

    public FuentesBOImpl() {
        this(new ArxivAdapter(), new CheckpointDAOImpl());
    }

    public FuentesBOImpl(FuenteAdapter adapter, CheckpointDAO checkpointDao) {
        this.adapter = adapter;
        this.checkpointDao = checkpointDao;
    }

    @Override
    public List<PublicacionOriginal> procesar(Set<String> arxivCategorias) {
        int maxResultados = Integer.parseInt(
                ExploradorConfig.obtener("fuente.arxiv.consulta_max", "50"));

        CheckpointFuente checkpoint = checkpointDao.leer(adapter.nombre());
        List<PublicacionOriginal> recientes = adapter.consultarRecientes(arxivCategorias, maxResultados);

        List<PublicacionOriginal> nuevas = new ArrayList<>();
        for (PublicacionOriginal pub : recientes) {
            if (!checkpoint.getIdsVistos().contains(pub.getIdOrigen())) {
                nuevas.add(pub);
            }
        }

        for (PublicacionOriginal pub : nuevas) {
            checkpoint.getIdsVistos().add(pub.getIdOrigen());
        }
        checkpoint.setFechaUltimaConsulta(LocalDateTime.now());
        checkpointDao.escribir(checkpoint);

        return nuevas;
    }
}
