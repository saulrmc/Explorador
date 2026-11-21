package explorador.usuario.bo;

import explorador.usuario.dao.FuenteHabilitadaDAO;
import explorador.usuario.dao.FuenteHabilitadaDAOImpl;
import explorador.usuario.modelo.FuenteHabilitada;

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
    public List<String> listarHabilitadas() {
        return dao.listarHabilitadas();
    }

    @Override
    public void setHabilitada(String nombre, boolean habilitada) {
        dao.setHabilitada(nombre, habilitada);
    }
}
