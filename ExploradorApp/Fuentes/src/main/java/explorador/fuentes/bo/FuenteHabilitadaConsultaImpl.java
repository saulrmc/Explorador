package explorador.fuentes.bo;

import explorador.fuentes.dao.FuenteHabilitadaDAO;
import explorador.fuentes.dao.FuenteHabilitadaDAOImpl;

import java.util.List;

public class FuenteHabilitadaConsultaImpl implements FuenteHabilitadaConsulta {

    private final FuenteHabilitadaDAO dao;

    public FuenteHabilitadaConsultaImpl() {
        this(new FuenteHabilitadaDAOImpl());
    }

    public FuenteHabilitadaConsultaImpl(FuenteHabilitadaDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<String> listarHabilitadas() {
        List<String> habilitadas = dao.listarHabilitadas();
        if (!habilitadas.isEmpty()) {
            return habilitadas;
        }
        return List.of("arxiv");
    }
}
