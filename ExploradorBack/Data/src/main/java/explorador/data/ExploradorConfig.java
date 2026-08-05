package explorador.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ExploradorConfig {

    private static final Properties PROPIEDADES = cargar();

    private ExploradorConfig() {
    }

    public static String obtener(String clave) {
        return PROPIEDADES.getProperty(clave);
    }

    public static String obtener(String clave, String valorPorDefecto) {
        return PROPIEDADES.getProperty(clave, valorPorDefecto);
    }

    private static Properties cargar() {
        Properties propiedades = new Properties();
        try (InputStream flujo = ExploradorConfig.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (flujo != null) {
                propiedades.load(flujo);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar config.properties", e);
        }
        return propiedades;
    }
}
