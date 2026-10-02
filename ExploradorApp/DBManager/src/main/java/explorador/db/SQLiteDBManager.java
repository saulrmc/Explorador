package explorador.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDBManager {
    private static SQLiteDBManager instancia;
    private static final String url = "jdbc:sqlite:BD.db";
    private Connection conexion;

    static synchronized SQLiteDBManager getInstance() {
        if (instancia == null) {
            instancia = new SQLiteDBManager();
        }
        return instancia;
    }
    public Connection getConnection() throws SQLException, ClassNotFoundException {
        try {
            if(conexion == null || conexion.isClosed()){
                conexion = DriverManager.getConnection(url);
            }
            return conexion;
        }
        catch (SQLException e) {
            System.err.println(e);
            throw e;
        }
    }
}
