package explorador.usuario.dao;

import explorador.usuario.modelo.FuenteHabilitada;

import java.util.List;

public interface FuenteHabilitadaDAO {
    List<FuenteHabilitada> listarTodas();

    List<String> listarHabilitadas();

    boolean setHabilitada(String nombre, boolean habilitada);
}
