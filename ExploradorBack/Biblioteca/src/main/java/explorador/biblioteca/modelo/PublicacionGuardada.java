package explorador.biblioteca.modelo;

import java.time.LocalDateTime;
import java.util.List;

public class PublicacionGuardada {
    private int id;
    private int publicacionId;
    private String titulo;
    private String url;
    private LocalDateTime fechaGuardado;
    private List<String> conceptos;
    private List<String> etiquetas;

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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getFechaGuardado() {
        return fechaGuardado;
    }

    public void setFechaGuardado(LocalDateTime fechaGuardado) {
        this.fechaGuardado = fechaGuardado;
    }

    public List<String> getConceptos() {
        return conceptos;
    }

    public void setConceptos(List<String> conceptos) {
        this.conceptos = conceptos;
    }

    public List<String> getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(List<String> etiquetas) {
        this.etiquetas = etiquetas;
    }
}
