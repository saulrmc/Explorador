package explorador.db;

public class SQLiteDBManagerFactory extends DBManagerFactory{
    @Override
    public DBManager crearDBManager(String host, int puerto, String esquema, String usuario, String password) {
        return SQLiteDBManager.getInstance(host, puerto, esquema, usuario, password);
    }
}
