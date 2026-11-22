package explorador.fuentes.modelo;

public class FuenteHabilitada {
    private String nombre;
    private boolean habilitada;

    public FuenteHabilitada() {}

    public FuenteHabilitada(String nombre, boolean habilitada) {
        this.nombre = nombre;
        this.habilitada = habilitada;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isHabilitada() {
        return habilitada;
    }

    public void setHabilitada(boolean habilitada) {
        this.habilitada = habilitada;
    }
}
