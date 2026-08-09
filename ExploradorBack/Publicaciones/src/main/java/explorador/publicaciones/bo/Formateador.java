package explorador.publicaciones.bo;

import explorador.fuentes.modelo.PublicacionOriginal;
import explorador.publicaciones.modelo.Publicacion;

public interface Formateador {
    Publicacion formatear(PublicacionOriginal original);
}
