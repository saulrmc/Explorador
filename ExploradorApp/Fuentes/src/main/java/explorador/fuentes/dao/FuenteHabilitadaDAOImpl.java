package explorador.fuentes.dao;

import explorador.db.SQLiteDBManager;
import explorador.fuentes.modelo.FuenteHabilitada;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FuenteHabilitadaDAOImpl implements FuenteHabilitadaDAO {

    public FuenteHabilitadaDAOImpl() {
        crearTabla();
    }

    private void crearTabla() {
        String sql = """
                CREATE TABLE IF NOT EXISTS fuente_habilitada (
                    nombre TEXT PRIMARY KEY,
                    habilitada INTEGER NOT NULL DEFAULT 1
                )
                """;
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al crear la tabla fuente_habilitada", e);
        }
    }

    @Override
    public List<FuenteHabilitada> listarTodas() {
        String sql = "SELECT nombre, habilitada FROM fuente_habilitada ORDER BY nombre";
        List<FuenteHabilitada> fuentes = new ArrayList<>();
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                fuentes.add(mapear(rs));
            }
            return fuentes;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al listar fuentes habilitadas", e);
        }
    }

    @Override
    public List<String> listarHabilitadas() {
        String sql = "SELECT nombre FROM fuente_habilitada WHERE habilitada = 1 ORDER BY nombre";
        List<String> fuentes = new ArrayList<>();
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                fuentes.add(rs.getString("nombre"));
            }
            return fuentes;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al listar fuentes habilitadas", e);
        }
    }

    @Override
    public boolean setHabilitada(String nombre, boolean habilitada) {
        String sql = """
                INSERT INTO fuente_habilitada(nombre, habilitada)
                VALUES(?, ?)
                ON CONFLICT(nombre) DO UPDATE SET habilitada = excluded.habilitada
                """;
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, habilitada ? 1 : 0);
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al actualizar fuente habilitada", e);
        }
    }

    private FuenteHabilitada mapear(ResultSet rs) throws SQLException {
        FuenteHabilitada f = new FuenteHabilitada();
        f.setNombre(rs.getString("nombre"));
        f.setHabilitada(rs.getInt("habilitada") == 1);
        return f;
    }
}
