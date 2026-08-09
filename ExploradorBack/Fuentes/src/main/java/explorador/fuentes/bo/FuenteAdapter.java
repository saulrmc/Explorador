package explorador.fuentes.bo;

import explorador.fuentes.modelo.PublicacionOriginal;

import java.util.List;
import java.util.Set;

public interface FuenteAdapter {
    String nombre();

    List<PublicacionOriginal> consultarRecientes(Set<String> categorias, int maxResultados);
}
