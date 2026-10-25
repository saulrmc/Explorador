package explorador.usuario.modelo;

public class Categoria {
    private int id;
    private CategoriaAreaEnum nombre;
    private boolean principal; // ya que una publicación
    // puede estar relacionada a varias categorias, siempre habrá alguna
    // que sea más relevante que el resto

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CategoriaAreaEnum getNombre() {
        return nombre;
    }

    public void setNombre(CategoriaAreaEnum nombre) {
        this.nombre = nombre;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }
}
