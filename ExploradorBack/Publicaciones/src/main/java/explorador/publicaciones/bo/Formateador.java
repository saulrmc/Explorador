package explorador.publicaciones.bo;

import explorador.fuentes.modelo.PublicacionBruta;
import explorador.publicaciones.modelo.Publicacion;

public interface Formateador {
    Publicacion formatear(PublicacionBruta bruta);
}
