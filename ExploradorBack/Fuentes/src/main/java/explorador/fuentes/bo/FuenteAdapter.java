package explorador.fuentes.bo;

import explorador.fuentes.modelo.PublicacionBruta;

import java.util.List;
import java.util.Set;

public interface FuenteAdapter {
    String nombre();

    List<PublicacionBruta> consultarRecientes(Set<String> categorias, int maxResultados);
}
