package explorador.db;

import java.sql.Connection;
import java.sql.SQLException;
import explorador.db.utils.TipoDB;
import explorador.db.utils.CadenaConexion;
import explorador.db.utils.Crypto;

public abstract class DBManager {
    protected String host;
    protected int puerto;
    protected String esquema;
    protected String usuario;
    protected String password;
    protected TipoDB tipoDB;
    
    protected DBManager() {}
    
    protected DBManager(String host, int puerto, String esquema,
                        String usuario,
                        String password, TipoDB tipoDB) {
        
        try {
            this.host = host;
            this.puerto = puerto;
            this.esquema = esquema;
            this.usuario = usuario;
            this.password = Crypto.decrypt(password);
            this.tipoDB = tipoDB;
        }
        catch (Exception ex) {
            System.err.println("Hubo un error al configurar al configurar la "
                    + "conexion a la base de datos: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
    
    public abstract Connection getConnection() throws SQLException, 
                                                      ClassNotFoundException;
    
    protected String cadenaConexion() {
        CadenaConexion cadena = new CadenaConexion.Builder()
                .tipoDB(this.tipoDB)
                .servidor(this.host)
                .puerto(this.puerto)
                .schema(this.esquema)
                .build();
        
        return cadena.toString();
    }
}
