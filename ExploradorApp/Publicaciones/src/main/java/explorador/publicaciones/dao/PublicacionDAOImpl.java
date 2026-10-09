
package explorador.publicaciones.dao;

import explorador.db.SQLiteDBManager;
import explorador.fuentes.modelo.PublicacionOriginal;


import explorador.data.JsonPersistencia;
import explorador.publicaciones.modelo.EstadoPublicacion;
import explorador.publicaciones.modelo.Publicacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PublicacionDAOImpl implements PublicacionDAO {

    @Override
    public Publicacion leer() {
        String sql = "SELECT id, nombre, correo FROM publicacion WHERE id = 1";
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Publicacion u = new Publicacion();
                u.setId(rs.getInt("id"));
                u.setTitulo(rs.getString("titulo"));
                u.setResumen(rs.getString("resumen"));
                u.setEstadoPublicacion(EstadoPublicacion.VISTO);
                u.
                return u;
            }
            return null;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al leer publicacion de SQLite", e);
        }
    }

    @Override
    public void escribir(Publicacion publicacion) {
        String sql = "INSERT INTO publicacion(id, nombre, correo) VALUES(?, ?, ?) "
                + "ON CONFLICT(id) DO UPDATE SET nombre = excluded.nombre, correo = excluded.correo";
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, publicacion.getId());
            ps.setString(2, publicacion.getNombre());
            ps.setString(3, publicacion.getCorreo());
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al guardar publicacion en SQLite", e);
        }
    }
}