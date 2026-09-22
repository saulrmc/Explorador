package explorador.usuario.bo;

import explorador.usuario.dao.HistorialDAO;
import explorador.usuario.dao.HistorialDAOImpl;
import explorador.usuario.modelo.PublicacionConsultada;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class HistorialBOImpl implements HistorialBO {

    private final HistorialDAO historialDao;

    public HistorialBOImpl() {
        this.historialDao = new HistorialDAOImpl();
    }

    @Override
    public List<PublicacionConsultada> listar() {
        return historialDao.leerTodos();
    }

    @Override
    public void registrar(int publicacionId, String titulo) {
        if (publicacionId <= 0) {
            throw new IllegalArgumentException("El id de la publicacion debe ser mayor a 0");
        }
        Objects.requireNonNull(titulo, "El titulo es obligatorio");

        List<PublicacionConsultada> registros = historialDao.leerTodos();
        int id = registros.stream().mapToInt(PublicacionConsultada::getId).max().orElse(0) + 1;

        PublicacionConsultada registro = new PublicacionConsultada();
        registro.setId(id);
        registro.setPublicacionId(publicacionId);
        registro.setTitulo(titulo);
        registro.setFechaConsulta(LocalDateTime.now());

        registros.add(registro);
        historialDao.reemplazarTodos(registros);
        historialDao.guardar();
    }

    @Override
    public void limpiar() {
        historialDao.reemplazarTodos(new java.util.ArrayList<>());
        historialDao.guardar();
    }

    @Override
    public void limpiarPorPublicacionIds(java.util.Set<Integer> publicacionIds) {
        List<PublicacionConsultada> restantes = historialDao.leerTodos().stream()
                .filter(registro -> !publicacionIds.contains(registro.getPublicacionId()))
                .toList();
        historialDao.reemplazarTodos(restantes);
        historialDao.guardar();
    }
}
