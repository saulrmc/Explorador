package explorador.fuentes.bo;

import explorador.fuentes.modelo.PublicacionOriginal;

import java.util.List;
import java.util.Set;

public interface FuentesBO {
    List<PublicacionOriginal> procesar(Set<String> arxivCategorias);
}
