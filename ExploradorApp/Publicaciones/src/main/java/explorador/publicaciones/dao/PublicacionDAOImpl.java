
package explorador.publicaciones.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper; //SQLite no maneja listas :c
import explorador.db.SQLiteDBManager;
import explorador.fuentes.modelo.PublicacionOriginal;


import explorador.publicaciones.modelo.EstadoPublicacion;
import explorador.publicaciones.modelo.Publicacion;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PublicacionDAOImpl implements PublicacionDAO {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    //En realidad la creación de la tabla podría estar separada de esta clase pero
    //mucha cosa ya, si funciona, funciona
    private void crearTabla() {
        String sql = """
                CREATE TABLE IF NOT EXISTS publicacion (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    titulo TEXT NOT NULL,
                    resumen TEXT,
                    autores TEXT,
                    fecha_publicacion TEXT,
                    etiquetas TEXT,
                    url TEXT NOT NULL UNIQUE,
                    estado TEXT NOT NULL DEFAULT 'NO_VISTO'
                )
                """;
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al crear la tabla publicacion", e);
        }
    }
    @Override
    public Publicacion crear(PublicacionOriginal modelo) {
        Publicacion publicacion = mapear(modelo);
        String sql = "INSERT INTO publicacion(titulo, resumen, autores, fecha_publicacion, etiquetas, url, estado) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";
        //conexión a SQLite para insertar una nueva Publicacion
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, publicacion.getTitulo());
            ps.setString(2, publicacion.getResumen());
            ps.setString(3, serializar(publicacion.getAutores()));
            ps.setString(4, fechaATexto(publicacion.getFechaPublicacion()));
            ps.setString(5, serializar(publicacion.getEtiquetas()));
            ps.setString(6, publicacion.getUrl());
            ps.setString(7, publicacion.getEstadoPublicacion().name());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    publicacion.setId(claves.getInt(1));
                }
            }
            return publicacion;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al guardar publicacion en SQLite", e);
        }
    }
    @Override
    public Publicacion leer(Integer id) {
        // 1. SELECT ... FROM publicacion WHERE id = ?
        // 2. si hay fila, construir Publicacion con los datos de esa fila
        // 3. si no hay fila, devolver null
        String sql = "SELECT  id, titulo, resumen, autores, fecha_publicacion, etiquetas, url, estado" +
                " FROM publicacion WHERE id = ?";
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al leer publicacion de SQLite", e);
        }
    }
    @Override
    public List<Publicacion> leerTodos() {
        String sql = "SELECT id, titulo, resumen, autores, fecha_publicacion, etiquetas, url, estado "
                + "FROM publicacion";
        List<Publicacion> publicaciones = new ArrayList<>();
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                publicaciones.add(mapear(rs));
            }
            return publicaciones;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al leer publicaciones de SQLite", e);
        }
    }
    @Override
    public boolean actualizar(Publicacion modelo) {
        String sql = "UPDATE publicacion SET titulo = ?, resumen = ?, autores = ?, "
                + "fecha_publicacion = ?, etiquetas = ?, url = ?, estado = ? WHERE id = ?";
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, modelo.getTitulo());
            ps.setString(2, modelo.getResumen());
            ps.setString(3, serializar(modelo.getAutores()));
            ps.setString(4, fechaATexto(modelo.getFechaPublicacion()));
            ps.setString(5, serializar(modelo.getEtiquetas()));
            ps.setString(6, modelo.getUrl());
            ps.setString(7, modelo.getEstadoPublicacion().name());
            ps.setInt(8, modelo.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al actualizar publicacion en SQLite", e);
        }
    }
    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM publicacion WHERE id = ?";
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al eliminar publicacion de SQLite", e);
        }
    }

    @Override
    public boolean existePorUrl(String url) {
        String sql = "SELECT 1 FROM publicacion WHERE url = ?";
        try (Connection conn = SQLiteDBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, url);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al verificar publicacion en SQLite", e);
        }
    }

    private Publicacion mapear(PublicacionOriginal original) {
        Publicacion publicacion = new Publicacion();
        publicacion.setTitulo(original.getTitulo());
        publicacion.setResumen(original.getResumen());
        publicacion.setAutores(original.getAutores());
        publicacion.setFechaPublicacion(original.getFechaPublicacion());
        publicacion.setEtiquetas(original.getEtiquetas());
        publicacion.setUrl(original.getUrl());
        publicacion.setEstadoPublicacion(EstadoPublicacion.NO_VISTO);
        return publicacion;
    }

    private Publicacion mapear(ResultSet rs) throws SQLException {
        Publicacion publicacion = new Publicacion();
        publicacion.setId(rs.getInt("id"));
        publicacion.setTitulo(rs.getString("titulo"));
        publicacion.setResumen(rs.getString("resumen"));
        publicacion.setAutores(deserializar(rs.getString("autores")));
        String fecha = rs.getString("fecha_publicacion");
        publicacion.setFechaPublicacion(
                fecha == null || fecha.isBlank() ? null : LocalDate.parse(fecha));
        publicacion.setEtiquetas(deserializar(rs.getString("etiquetas")));
        publicacion.setUrl(rs.getString("url"));
        publicacion.setEstadoPublicacion(EstadoPublicacion.valueOf(rs.getString("estado")));
        return publicacion;
    }
    private String serializar(List<String> valores) {
        if (valores == null) {
            return "[]";
        }
        try {
            return MAPPER.writeValueAsString(valores);
        } catch (Exception e) {
            throw new RuntimeException("Error al serializar lista", e);
        }
    }

    private List<String> deserializar(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return MAPPER.readValue(json, new TypeReference<List<String>>() { });
        } catch (Exception e) {
            throw new RuntimeException("Error al deserializar lista", e);
        }
    }

    private String fechaATexto(LocalDate fecha) {
        return fecha == null ? null : fecha.toString();
    }
}