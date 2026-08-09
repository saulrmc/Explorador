package explorador.publicaciones.modelo;

import explorador.fuentes.modelo.PublicacionOriginal;

import java.time.LocalDateTime;
import java.util.List;

public class Publicacion {
    private int id;
    private String titulo;
    private String descripcion;
    private List<String> conceptos;
    private double score;
    private LocalDateTime fechaIngreso;
    private PublicacionOriginal original;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<String> getConceptos() {
        return conceptos;
    }

    public void setConceptos(List<String> conceptos) {
        this.conceptos = conceptos;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDateTime fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public PublicacionOriginal getOriginal() {
        return original;
    }

    public void setOriginal(PublicacionOriginal original) {
        this.original = original;
    }
}
