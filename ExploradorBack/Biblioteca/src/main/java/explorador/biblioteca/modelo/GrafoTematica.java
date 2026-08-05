package explorador.biblioteca.modelo;

import java.util.ArrayList;
import java.util.List;

public class GrafoTematica {
    private List<NodoTema> nodos;
    private List<Arista> aristas;

    public List<NodoTema> getNodos() {
        if (nodos == null) {
            nodos = new ArrayList<>();
        }
        return nodos;
    }

    public void setNodos(List<NodoTema> nodos) {
        this.nodos = nodos;
    }

    public List<Arista> getAristas() {
        if (aristas == null) {
            aristas = new ArrayList<>();
        }
        return aristas;
    }

    public void setAristas(List<Arista> aristas) {
        this.aristas = aristas;
    }
}
