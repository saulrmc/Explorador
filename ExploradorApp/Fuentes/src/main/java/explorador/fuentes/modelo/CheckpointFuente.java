package explorador.fuentes.modelo;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class CheckpointFuente {
    private String nombreFuente;
    private Set<String> idsVistos;
    private LocalDateTime fechaUltimaConsulta;

    public String getNombreFuente() {
        return nombreFuente;
    }

    public void setNombreFuente(String nombreFuente) {
        this.nombreFuente = nombreFuente;
    }

    public Set<String> getIdsVistos() {
        if (idsVistos == null) {
            idsVistos = new HashSet<>();
        }
        return idsVistos;
    }

    public void setIdsVistos(Set<String> idsVistos) {
        this.idsVistos = idsVistos;
    }

    public LocalDateTime getFechaUltimaConsulta() {
        return fechaUltimaConsulta;
    }

    public void setFechaUltimaConsulta(LocalDateTime fechaUltimaConsulta) {
        this.fechaUltimaConsulta = fechaUltimaConsulta;
    }
}
