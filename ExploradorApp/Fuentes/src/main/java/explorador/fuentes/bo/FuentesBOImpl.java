package explorador.fuentes.bo;

import explorador.data.ExploradorConfig;
import explorador.fuentes.dao.CheckpointDAO;
import explorador.fuentes.dao.CheckpointDAOImpl;
import explorador.fuentes.mapeo.CategoriaFuenteMapper;
import explorador.fuentes.modelo.CheckpointFuente;
import explorador.fuentes.modelo.PublicacionOriginal;
import explorador.usuario.bo.UsuarioBO;
import explorador.usuario.bo.UsuarioBOImpl;
import explorador.usuario.modelo.CategoriaGenerica;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FuentesBOImpl implements FuentesBO {

    private final UsuarioBO usuarioBO;
    private final FuenteHabilitadaConsulta fuenteHabilitadaConsulta;
    private final CheckpointDAO checkpointDao;
    private final Map<String, FuenteAdapter> adapters;

    public FuentesBOImpl() {
        this(new UsuarioBOImpl(), new FuenteHabilitadaConsultaImpl(), new CheckpointDAOImpl(), crearAdaptersPorDefecto());
    }

    public FuentesBOImpl(UsuarioBO usuarioBO, FuenteHabilitadaConsulta fuenteHabilitadaConsulta,
                         CheckpointDAO checkpointDao, Map<String, FuenteAdapter> adapters) {
        this.usuarioBO = usuarioBO;
        this.fuenteHabilitadaConsulta = fuenteHabilitadaConsulta;
        this.checkpointDao = checkpointDao;
        this.adapters = adapters;
    }

    private static Map<String, FuenteAdapter> crearAdaptersPorDefecto() {
        Map<String, FuenteAdapter> mapa = new HashMap<>();
        FuenteAdapter arxiv = new ArxivAdapter();
        mapa.put(arxiv.nombre(), arxiv);
        return mapa;
    }

    @Override
    public List<PublicacionOriginal> procesar() {
        int maxResultados = Integer.parseInt(
                ExploradorConfig.obtener("fuente.arxiv.consulta_max", "50"));

        List<CategoriaGenerica> intereses = usuarioBO.listarIntereses();
        List<String> fuentesHabilitadas = fuenteHabilitadaConsulta.listarHabilitadas();

        List<PublicacionOriginal> nuevasTotales = new ArrayList<>();
        for (String nombreFuente : fuentesHabilitadas) {
            FuenteAdapter adapter = adapters.get(nombreFuente);
            if (adapter == null) {
                continue; // no hay adapter para esta fuente, se omite
            }
            Set<String> codigos = CategoriaFuenteMapper.mapear(intereses, nombreFuente);
            if (codigos.isEmpty()) {
                continue; // sin mapeo para estas categorias, se omite
            }
            nuevasTotales.addAll(procesarFuente(adapter, codigos, maxResultados));
        }
        return nuevasTotales;
    }

    private List<PublicacionOriginal> procesarFuente(FuenteAdapter adapter, Set<String> codigos, int maxResultados) {
        CheckpointFuente checkpoint = checkpointDao.leer(adapter.nombre());
        List<PublicacionOriginal> recientes = adapter.consultarRecientes(codigos, maxResultados);

        List<PublicacionOriginal> nuevas = new ArrayList<>();
        for (PublicacionOriginal pub : recientes) {
            if (!checkpoint.getIdsVistos().contains(pub.getUrl())) {
                nuevas.add(pub);
            }
        }

        for (PublicacionOriginal pub : nuevas) {
            checkpoint.getIdsVistos().add(pub.getUrl());
        }
        checkpoint.setFechaUltimaConsulta(LocalDateTime.now());
        checkpointDao.escribir(checkpoint);

        return nuevas;
    }
}