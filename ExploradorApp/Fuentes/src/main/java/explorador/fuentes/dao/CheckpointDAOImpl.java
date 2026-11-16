package explorador.fuentes.dao;

import explorador.db.SQLiteDBManager;
import explorador.fuentes.modelo.CheckpointFuente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CheckpointDAOImpl implements CheckpointDAO {

    public CheckpointDAOImpl() {
        crearTabla();
    }

    private void crearTabla() {
        String sql = """
                CREATE TABLE IF NOT EXISTS checkpoint_fuente (
                    nombre_fuente TEXT PRIMARY KEY,
                    fecha_ultima_consulta TEXT,
                    ids_vistos TEXT
                )
                """;
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al crear la tabla checkpoint_fuente", e);
        }
    }

    @Override
    public CheckpointFuente leer(String nombreFuente) {
        String sql = "SELECT nombre_fuente, fecha_ultima_consulta, ids_vistos FROM checkpoint_fuente WHERE nombre_fuente = ?";
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreFuente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
            CheckpointFuente nuevo = new CheckpointFuente();
            nuevo.setNombreFuente(nombreFuente);
            nuevo.setIdsVistos(new HashSet<>());
            return nuevo;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al leer checkpoint de SQLite", e);
        }
    }

    @Override
    public void escribir(CheckpointFuente checkpoint) {
        String sql = """
                INSERT INTO checkpoint_fuente(nombre_fuente, fecha_ultima_consulta, ids_vistos)
                VALUES(?, ?, ?)
                ON CONFLICT(nombre_fuente) DO UPDATE SET
                    fecha_ultima_consulta = excluded.fecha_ultima_consulta,
                    ids_vistos = excluded.ids_vistos
                """;
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, checkpoint.getNombreFuente());
            ps.setString(2, checkpoint.getFechaUltimaConsulta() == null ? null : checkpoint.getFechaUltimaConsulta().toString());
            ps.setString(3, serializarIds(checkpoint.getIdsVistos()));
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al escribir checkpoint en SQLite", e);
        }
    }

    private CheckpointFuente mapear(ResultSet rs) throws SQLException {
        CheckpointFuente checkpoint = new CheckpointFuente();
        checkpoint.setNombreFuente(rs.getString("nombre_fuente"));
        String fecha = rs.getString("fecha_ultima_consulta");
        checkpoint.setFechaUltimaConsulta(fecha == null || fecha.isBlank() ? null : LocalDateTime.parse(fecha));
        checkpoint.setIdsVistos(deserializarIds(rs.getString("ids_vistos")));
        return checkpoint;
    }

    private String serializarIds(Set<String> ids) {
        if (ids == null || ids.isEmpty()) return "";
        return String.join("\n", ids);
    }

    private Set<String> deserializarIds(String raw) {
        if (raw == null || raw.isBlank()) return new HashSet<>();
        return new HashSet<>(Arrays.asList(raw.split("\n", -1)));
    }
}
