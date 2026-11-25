package explorador.fuentes.bo;

import explorador.fuentes.modelo.PublicacionOriginal;

import java.util.List;

public interface FuentesBO {
    // Consulta fuentes habilitadas usando los intereses del usuario y devuelve publicaciones nuevas
    List<PublicacionOriginal> procesar();
}