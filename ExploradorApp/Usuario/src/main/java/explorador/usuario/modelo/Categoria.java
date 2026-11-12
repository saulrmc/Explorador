package explorador.usuario.modelo;

/**
 * Categoría genérica seleccionada por el usuario.
 */
public class Categoria {
    private int id;
    private CategoriaGenerica nombre;
    private boolean principal; // ya que una publicación
    // puede estar relacionada a varias categorias, siempre habrá alguna
    // que sea más relevante que el resto

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CategoriaGenerica getNombre() {
        return nombre;
    }

    public void setNombre(CategoriaGenerica nombre) {
        this.nombre = nombre;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }
}
