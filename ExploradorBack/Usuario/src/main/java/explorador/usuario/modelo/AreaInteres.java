package explorador.usuario.modelo;

public class AreaInteres {
    private int id;
    private String nombre;
    private CategoriaArea categoria;

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

    public CategoriaArea getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaArea categoria) {
        this.categoria = categoria;
    }
}
