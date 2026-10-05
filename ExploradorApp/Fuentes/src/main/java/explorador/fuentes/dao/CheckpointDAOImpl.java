package explorador.fuentes.dao;

import explorador.data.JsonPersistencia;
import explorador.fuentes.modelo.CheckpointFuente;

import java.util.ArrayList;
import java.util.List;

public class CheckpointDAOImpl implements CheckpointDAO {

    private static final String ARCHIVO = "checkpoints";
    private static final Object BLOQUEO = new Object();

    private static List<CheckpointFuente> checkpoints;
    private static boolean sucio;

    private final JsonPersistencia persistencia;

    public CheckpointDAOImpl() {
        this(new JsonPersistencia("Fuentes"));
    }

    CheckpointDAOImpl(JsonPersistencia persistencia) {
        this.persistencia = persistencia;
    }

    @Override
    public CheckpointFuente leer(String nombreFuente) {
        synchronized (BLOQUEO) {
            cargar();
            return checkpoints.stream()
                    .filter(checkpoint -> checkpoint.getNombreFuente().equals(nombreFuente))
                    .findFirst()
                    .orElseGet(() -> {
                        CheckpointFuente nuevo = new CheckpointFuente();
                        nuevo.setNombreFuente(nombreFuente);
                        nuevo.setIdsVistos(new java.util.HashSet<>());
                        return nuevo;
                    });
        }
    }

    @Override
    public void escribir(CheckpointFuente checkpoint) {
        synchronized (BLOQUEO) {
            cargar();
            boolean existe = false;
            for (int i = 0; i < checkpoints.size(); i++) {
                if (checkpoints.get(i).getNombreFuente().equals(checkpoint.getNombreFuente())) {
                    checkpoints.set(i, checkpoint);
                    existe = true;
                    break;
                }
            }
            if (!existe) {
                checkpoints.add(checkpoint);
            }
            persistencia.escribir(ARCHIVO, checkpoints);
            sucio = false;
        }
    }

    private void cargar() {
        if (checkpoints == null) {
            checkpoints = new ArrayList<>(persistencia.leerLista(ARCHIVO, CheckpointFuente.class));
            sucio = false;
        }
    }
}
