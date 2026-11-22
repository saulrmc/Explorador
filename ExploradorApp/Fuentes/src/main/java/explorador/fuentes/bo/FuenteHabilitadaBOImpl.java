package explorador.fuentes.bo;

import explorador.fuentes.dao.FuenteHabilitadaDAO;
import explorador.fuentes.dao.FuenteHabilitadaDAOImpl;
import explorador.fuentes.modelo.FuenteHabilitada;

import java.util.List;

public class FuenteHabilitadaBOImpl implements FuenteHabilitadaBO {

    private final FuenteHabilitadaDAO dao;

    public FuenteHabilitadaBOImpl() {
        this(new FuenteHabilitadaDAOImpl());
    }

    public FuenteHabilitadaBOImpl(FuenteHabilitadaDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<FuenteHabilitada> listarTodas() {
        return dao.listarTodas();
    }

    @Override
    public void setHabilitada(String nombre, boolean habilitada) {
        dao.setHabilitada(nombre, habilitada);
    }
}
