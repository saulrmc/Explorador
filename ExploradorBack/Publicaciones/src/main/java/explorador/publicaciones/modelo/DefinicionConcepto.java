package explorador.publicaciones.modelo;

public class DefinicionConcepto {
    private String concepto;
    private String definicion;
    private String url;

    public DefinicionConcepto() {
    }

    public DefinicionConcepto(String concepto, String definicion, String url) {
        this.concepto = concepto;
        this.definicion = definicion;
        this.url = url;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getDefinicion() {
        return definicion;
    }

    public void setDefinicion(String definicion) {
        this.definicion = definicion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
