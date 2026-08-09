package explorador.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public final class JsonPersistencia {

    private final ObjectMapper mapper;
    private final Path directorio;

    public JsonPersistencia(String nombreModulo) {
        this(nombreModulo, crearMapper(), Paths.get("Data"));
    }

    public JsonPersistencia(String nombreModulo, ObjectMapper mapper) {
        this(nombreModulo, mapper, Paths.get("Data"));
    }

    public JsonPersistencia(String nombreModulo, Path directorioBase) {
        this(nombreModulo, crearMapper(), directorioBase);
    }

    public JsonPersistencia(String nombreModulo, ObjectMapper mapper, Path directorioBase) {
        this.mapper = mapper;
        this.directorio = directorioBase.resolve(nombreModulo);
    }

    public <T> T leer(String nombreArchivo, Class<T> tipo) {
        return leer(nombreArchivo, tipo, null);
    }

    public <T> T leer(String nombreArchivo, Class<T> tipo, T valorPorDefecto) {
        Path archivo = directorio.resolve(nombreArchivo + ".json");
        if (!Files.exists(archivo)) {
            return valorPorDefecto;
        }
        try {
            return mapper.readValue(archivo.toFile(), tipo);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo: " + archivo, e);
        }
    }

    public <T> List<T> leerLista(String nombreArchivo, Class<T> tipo) {
        Path archivo = directorio.resolve(nombreArchivo + ".json");
        if (!Files.exists(archivo)) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(archivo.toFile(),
                    mapper.getTypeFactory().constructCollectionType(List.class, tipo));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo: " + archivo, e);
        }
    }

    public void escribir(String nombreArchivo, Object objeto) {
        Path archivo = directorio.resolve(nombreArchivo + ".json");
        try {
            Files.createDirectories(directorio);
            Path temporal = directorio.resolve(nombreArchivo + ".tmp");
            mapper.writeValue(temporal.toFile(), objeto);
            try {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo escribir el archivo: " + archivo, e);
        }
    }

    private static ObjectMapper crearMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
