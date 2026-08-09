package explorador.notificaciones.bo;

import explorador.data.JsonPersistencia;
import explorador.fuentes.modelo.PublicacionOriginal;
import explorador.notificaciones.dao.RegistroNotificacionDAOImpl;
import explorador.publicaciones.modelo.Publicacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificadorBOImplTest {

    @TempDir
    Path directorio;

    private RegistroNotificacionDAOImpl dao;
    private EnviadorStub enviador;

    @BeforeEach
    void setUp() {
        RegistroNotificacionDAOImpl.reiniciar();
        dao = new RegistroNotificacionDAOImpl(new JsonPersistencia("Notificaciones", directorio));
        enviador = new EnviadorStub(true);
    }

    @Test
    void cuandoElEnvioFallaNoRegistraNiEscribe() throws Exception {
        enviador.exito = false;
        NotificadorBOImpl notificador = new NotificadorBOImpl(dao, enviador);

        notificador.notificarNuevas(List.of(publicacion(1)), "destino@ejemplo.com");

        assertFalse(dao.existe(1));
        assertFalse(Files.exists(archivoJson()));
    }

    @Test
    void cuandoElEnvioTieneExitoRegistraUnaVez() {
        NotificadorBOImpl notificador = new NotificadorBOImpl(dao, enviador);

        notificador.notificarNuevas(List.of(publicacion(1), publicacion(2)), "destino@ejemplo.com");
        notificador.notificarNuevas(List.of(publicacion(1), publicacion(2)), "destino@ejemplo.com");

        assertEquals(2, enviador.llamadas);
        assertTrue(dao.existe(1));
        assertTrue(dao.existe(2));
        assertEquals(2, dao.leerTodos().size());
    }

    @Test
    void cuandoLaPublicacionNoTieneOriginalSeOmite() {
        NotificadorBOImpl notificador = new NotificadorBOImpl(dao, enviador);

        Publicacion publicacion = new Publicacion();
        publicacion.setId(1);
        publicacion.setTitulo("Sin original");

        notificador.notificarNuevas(List.of(publicacion), "destino@ejemplo.com");

        assertEquals(0, enviador.llamadas);
        assertFalse(dao.existe(1));
    }

    @Test
    void limpiarPorPublicacionIdsPersisteLaEliminacion() throws Exception {
        NotificadorBOImpl notificador = new NotificadorBOImpl(dao, enviador);
        notificador.notificarNuevas(List.of(publicacion(1), publicacion(2)), "destino@ejemplo.com");

        RegistroNotificacionDAOImpl.reiniciar();
        RegistroNotificacionDAOImpl daoRecargado = new RegistroNotificacionDAOImpl(
                new JsonPersistencia("Notificaciones", directorio));
        notificador = new NotificadorBOImpl(daoRecargado, enviador);

        notificador.limpiarPorPublicacionIds(java.util.Set.of(1));

        assertFalse(daoRecargado.existe(1));
        assertTrue(daoRecargado.existe(2));

        RegistroNotificacionDAOImpl.reiniciar();
        RegistroNotificacionDAOImpl daoFinal = new RegistroNotificacionDAOImpl(
                new JsonPersistencia("Notificaciones", directorio));
        assertTrue(daoFinal.existe(2));
        assertFalse(daoFinal.existe(1));
    }

    private Publicacion publicacion(int id) {
        Publicacion publicacion = new Publicacion();
        publicacion.setId(id);
        publicacion.setTitulo("Titulo " + id);
        publicacion.setDescripcion("Descripcion " + id);
        PublicacionOriginal original = new PublicacionOriginal();
        original.setIdOrigen("id" + id);
        original.setTitulo("Titulo " + id);
        original.setResumen("Resumen " + id);
        original.setUrl("https://arxiv.org/abs/id" + id);
        publicacion.setOriginal(original);
        return publicacion;
    }

    private Path archivoJson() {
        return directorio.resolve("Notificaciones").resolve("enviadas.json");
    }

    private static final class EnviadorStub implements EnviadorCorreo {

        boolean exito;
        int llamadas;

        EnviadorStub(boolean exito) {
            this.exito = exito;
        }

        @Override
        public boolean enviar(String destinatario, String asunto, String contenido) {
            llamadas++;
            return exito;
        }
    }
}
