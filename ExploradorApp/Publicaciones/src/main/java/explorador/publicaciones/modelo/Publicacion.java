package explorador.publicaciones.modelo;

import explorador.fuentes.modelo.PublicacionOriginal;

/**
 * Vista de una publicacion preparada para pantalla. No se almacena:
 * se construye a partir del original crudo cada vez que se lee.
 */
public class Publicacion {
    private int id;
    private String titulo;
    private String descripcion;
    private double score;
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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public PublicacionOriginal getOriginal() {
        return original;
    }

    public void setOriginal(PublicacionOriginal original) {
        this.original = original;
    }
}
