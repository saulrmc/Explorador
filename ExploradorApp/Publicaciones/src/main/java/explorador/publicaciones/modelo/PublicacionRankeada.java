package explorador.publicaciones.modelo;

public class PublicacionRankeada {
    private Publicacion publicacion;
    private double score;

    public PublicacionRankeada(Publicacion publicacion) {
        this.publicacion = publicacion;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
