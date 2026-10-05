package explorador.usuario.dao;

import explorador.db.SQLiteDBManager;
import explorador.usuario.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAOImpl implements UsuarioDAO {
    // en realidad es una idea de cómo sería la tabla USUARIO pero al menos en este
    // caso no veo cómo más podría ser
    @Override
    public Usuario leer() {
        String sql = "SELECT id, nombre, correo FROM usuario WHERE id = 1";
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setCorreo(rs.getString("correo"));
                return u;
            }
            return null;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al leer usuario de SQLite", e);
        }
    }

    @Override
    public void escribir(Usuario usuario) {
        String sql = "INSERT INTO usuario(id, nombre, correo) VALUES(?, ?, ?) "
                + "ON CONFLICT(id) DO UPDATE SET nombre = excluded.nombre, correo = excluded.correo";
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuario.getId());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getCorreo());
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al guardar usuario en SQLite", e);
        }
    }
}
