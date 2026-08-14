package explorador.publicaciones.modelo;

public class Concepto {
    private String termino;
    private String url;

    public Concepto() {
    }

    public Concepto(String termino, String url) {
        this.termino = termino;
        this.url = url;
    }

    public String getTermino() {
        return termino;
    }

    public void setTermino(String termino) {
        this.termino = termino;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
