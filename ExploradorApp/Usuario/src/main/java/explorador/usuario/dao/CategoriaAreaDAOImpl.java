package explorador.usuario.dao;

import explorador.db.SQLiteDBManager;
import explorador.usuario.modelo.Categoria;
import explorador.usuario.modelo.CategoriaAreaEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoriaAreaDAOImpl implements CategoriaAreaDAO {

    public CategoriaAreaDAOImpl() {
        crearTabla();
    }

    private void crearTabla() {
        String sql = """
                CREATE TABLE IF NOT EXISTS categoria (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL UNIQUE,
                    principal INTEGER NOT NULL DEFAULT 0
                )
                """;
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al crear la tabla categoria", e);
        }
    }

    @Override
    public List<CategoriaAreaEnum> leerTodos() {
        String sql = "SELECT nombre FROM categoria ORDER BY id";
        List<CategoriaAreaEnum> categorias = new ArrayList<>();
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categorias.add(CategoriaAreaEnum.valueOf(rs.getString("nombre")));
            }
            return categorias;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al leer categorias", e);
        }
    }

    @Override
    public Categoria obtenerPrincipal() {
        String sql = "SELECT id, nombre, principal FROM categoria WHERE principal = 1 LIMIT 1";
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("id"));
                categoria.setNombre(CategoriaAreaEnum.valueOf(rs.getString("nombre")));
                categoria.setPrincipal(rs.getInt("principal") == 1);
                return categoria;
            }
            return null;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al obtener categoria principal", e);
        }
    }

    @Override
    public boolean agregar(CategoriaAreaEnum nombre) {
        String sql = "INSERT OR IGNORE INTO categoria(nombre, principal) VALUES(?, 0)";
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre.name());
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al agregar categoria", e);
        }
    }

    @Override
    public boolean marcarPrincipal(int id) {
        //solo puede haber una principal
        String sqlQuitar = "UPDATE categoria SET principal = 0";
        String sqlMarcar = "UPDATE categoria SET principal = 1 WHERE id = ?";
        try (Connection conn = SQLiteDBManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try (Statement st = conn.createStatement();
                 PreparedStatement ps = conn.prepareStatement(sqlMarcar)) {
                st.executeUpdate(sqlQuitar);
                ps.setInt(1, id);
                boolean ok = ps.executeUpdate() > 0;
                if (!ok) {
                    conn.rollback();
                    return false;
                }
                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al marcar categoria principal", e);
        }
    }

    @Override
    public boolean eliminar(CategoriaAreaEnum categoria) {
        String sql = "DELETE FROM categoria WHERE nombre = ?";
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoria.name());
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al eliminar categoria", e);
        }
    }

}
