package explorador.fuentes.bo;

import explorador.fuentes.modelo.FuenteHabilitada;

import java.util.List;

public interface FuenteHabilitadaBO {
    List<FuenteHabilitada> listarTodas();

    void setHabilitada(String nombre, boolean habilitada);
}
