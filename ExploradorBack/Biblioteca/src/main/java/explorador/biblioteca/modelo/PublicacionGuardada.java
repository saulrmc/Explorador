package explorador.biblioteca.modelo;

import java.time.LocalDateTime;
//TODO: creo que esta clase va a cambiar de significado
public class PublicacionGuardada {
    private int id;
    private int publicacionId;
    private LocalDateTime fechaGuardado;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPublicacionId() {
        return publicacionId;
    }

    public void setPublicacionId(int publicacionId) {
        this.publicacionId = publicacionId;
    }

    public LocalDateTime getFechaGuardado() {
        return fechaGuardado;
    }

    public void setFechaGuardado(LocalDateTime fechaGuardado) {
        this.fechaGuardado = fechaGuardado;
    }
}
