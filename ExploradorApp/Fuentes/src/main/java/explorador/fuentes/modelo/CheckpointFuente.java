package explorador.fuentes.modelo;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class CheckpointFuente {
    private String nombreFuente;
    private LocalDateTime fechaUltimaConsulta;
    private Set<String> idsVistos = new HashSet<>();

    public String getNombreFuente() {
        return nombreFuente;
    }

    public void setNombreFuente(String nombreFuente) {
        this.nombreFuente = nombreFuente;
    }

    public LocalDateTime getFechaUltimaConsulta() {
        return fechaUltimaConsulta;
    }

    public void setFechaUltimaConsulta(LocalDateTime fechaUltimaConsulta) {
        this.fechaUltimaConsulta = fechaUltimaConsulta;
    }

    public Set<String> getIdsVistos() {
        return idsVistos;
    }

    public void setIdsVistos(Set<String> idsVistos) {
        this.idsVistos = idsVistos;
    }
}
