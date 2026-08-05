package explorador.fuentes.dao;

import explorador.fuentes.modelo.CheckpointFuente;

public interface CheckpointDAO {
    CheckpointFuente leer(String nombreFuente);

    void escribir(CheckpointFuente checkpoint);
}
