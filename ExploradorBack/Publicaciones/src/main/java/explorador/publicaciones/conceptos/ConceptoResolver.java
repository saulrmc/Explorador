package explorador.publicaciones.conceptos;

import explorador.publicaciones.modelo.Concepto;

import java.util.List;

public interface ConceptoResolver {
    List<Concepto> resolver(List<String> candidatos);
}
