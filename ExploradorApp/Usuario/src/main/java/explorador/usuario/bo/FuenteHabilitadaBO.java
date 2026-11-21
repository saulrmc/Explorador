package explorador.usuario.bo;

import explorador.usuario.modelo.FuenteHabilitada;

import java.util.List;

public interface FuenteHabilitadaBO {
    List<FuenteHabilitada> listarTodas();

    List<String> listarHabilitadas();

    void setHabilitada(String nombre, boolean habilitada);
}
