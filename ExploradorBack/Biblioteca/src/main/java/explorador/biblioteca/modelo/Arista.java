package explorador.biblioteca.modelo;

public class Arista {
    private int origenId;
    private int destinoId;
    private int peso;

    public int getOrigenId() {
        return origenId;
    }

    public void setOrigenId(int origenId) {
        this.origenId = origenId;
    }

    public int getDestinoId() {
        return destinoId;
    }

    public void setDestinoId(int destinoId) {
        this.destinoId = destinoId;
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }
}
