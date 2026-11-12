package explorador.usuario.modelo;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private int id;
    private String nombre;
    private String correo;
    private List<Categoria> intereses = new ArrayList<>();

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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public List<Categoria> getIntereses() {
        return intereses;
    }

    public void setIntereses(List<Categoria> intereses) {
        this.intereses = intereses == null ? new ArrayList<>() : intereses;
    }
}
