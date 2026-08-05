package explorador.fuentes.bo;

import explorador.fuentes.modelo.PublicacionBruta;

import java.util.List;
import java.util.Set;

public interface FuentesBO {
    List<PublicacionBruta> procesar(Set<String> arxivCategorias, Set<String> keywords);
}
