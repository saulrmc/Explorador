package explorador.db;

import explorador.db.utils.TipoDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDBManager extends DBManager {
    private static SQLiteDBManager instancia;

    protected SQLiteDBManager(String host, int puerto, String esquema,
                             String usuario, String password) {
        super(host, puerto, esquema, usuario, password, TipoDB.SQLite);
    }
    static synchronized SQLiteDBManager getInstance(String host, int puerto,
                                                   String esquema,
                                                   String usuario,
                                                   String password) {
        if (instancia == null) {
            instancia = new SQLiteDBManager(host, puerto, esquema, usuario,
                    password);
        }
        return instancia;
    }
    @Override
    public Connection getConnection() throws SQLException, ClassNotFoundException {
        try {
            /*
            Por ahora creamos una conexion cada vez que se necesita acceder
            a la base de datos, por ser una aplicacion academica es una practica
            aceptable, en un sistema productivo se debe usar un pool de
            conexiones.
            */
            Class.forName("com.sqlite.cj.jdbc.Driver");
            String cadenaConexion = cadenaConexion();
            return DriverManager.getConnection(cadenaConexion, usuario, password);
        }
        catch (ClassNotFoundException | SQLException e) {
            System.err.println(e);
            throw e;
        }
    }
}
