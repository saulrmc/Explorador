package explorador.biblioteca.modelo;

import java.util.ArrayList;
import java.util.List;

public class NodoTema {
    private int id;
    private String nombre;
    private List<Integer> publicacionIds;

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

    public List<Integer> getPublicacionIds() {
        if (publicacionIds == null) {
            publicacionIds = new ArrayList<>();
        }
        return publicacionIds;
    }

    public void setPublicacionIds(List<Integer> publicacionIds) {
        this.publicacionIds = publicacionIds;
    }
}
