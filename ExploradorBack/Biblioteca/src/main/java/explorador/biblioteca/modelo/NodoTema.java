package explorador.biblioteca.modelo;

public class NodoTema {
    private int id;
    private String nombre;
    private TipoTema tipo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoTema getTipo() {
        return tipo;
    }

    public void setTipo(TipoTema tipo) {
        this.tipo = tipo;
    }
}
