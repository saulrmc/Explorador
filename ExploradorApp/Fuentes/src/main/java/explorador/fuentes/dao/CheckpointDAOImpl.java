package explorador.fuentes.dao;

import explorador.data.JsonPersistencia;
import explorador.fuentes.modelo.CheckpointFuente;

import java.util.ArrayList;
import java.util.List;

public class CheckpointDAOImpl implements CheckpointDAO {

    private static final String ARCHIVO = "checkpoint";
    private final JsonPersistencia persistencia;

    public CheckpointDAOImpl() {
        this.persistencia = new JsonPersistencia("Fuentes");
    }

    @Override
    public CheckpointFuente leer(String nombreFuente) {
        return persistencia.leerLista(ARCHIVO, CheckpointFuente.class).stream()
                .filter(checkpoint -> checkpoint.getNombreFuente().equals(nombreFuente))
                .findFirst()
                .orElseGet(() -> {
                    CheckpointFuente nuevo = new CheckpointFuente();
                    nuevo.setNombreFuente(nombreFuente);
                    nuevo.setIdsVistos(new java.util.HashSet<>());
                    return nuevo;
                });
    }

    @Override
    public void escribir(CheckpointFuente checkpoint) {
        List<CheckpointFuente> todos = new ArrayList<>(persistencia.leerLista(ARCHIVO, CheckpointFuente.class));
        boolean existe = false;
        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).getNombreFuente().equals(checkpoint.getNombreFuente())) {
                todos.set(i, checkpoint);
                existe = true;
                break;
            }
        }
        if (!existe) {
            todos.add(checkpoint);
        }
        persistencia.escribir(ARCHIVO, todos);
    }
}
