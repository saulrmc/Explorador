package explorador.biblioteca.modelo;

import explorador.publicaciones.modelo.Publicacion;

import java.time.LocalDateTime;

public class EntradaBiblioteca {
    private int id;
    private LocalDateTime fechaGuardado;
    private Publicacion publicacion;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPublicacionId() {
        return publicacion == null ? 0 : publicacion.getId();
    }

    public LocalDateTime getFechaGuardado() {
        return fechaGuardado;
    }

    public void setFechaGuardado(LocalDateTime fechaGuardado) {
        this.fechaGuardado = fechaGuardado;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
    }
}
