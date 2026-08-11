package explorador.rs.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import explorador.biblioteca.bo.BibliotecaBO;
import explorador.biblioteca.bo.BibliotecaBOImpl;
import explorador.biblioteca.modelo.EntradaBiblioteca;
import explorador.data.ExploradorConfig;
import explorador.fuentes.bo.FuentesBO;
import explorador.fuentes.bo.FuentesBOImpl;
import explorador.fuentes.modelo.PublicacionOriginal;
import explorador.notificaciones.bo.NotificadorBO;
import explorador.notificaciones.bo.NotificadorBOImpl;
import explorador.publicaciones.bo.PublicacionesBO;
import explorador.publicaciones.bo.PublicacionesBOImpl;
import explorador.publicaciones.modelo.Publicacion;
import explorador.usuario.bo.CategoriaAreaBO;
import explorador.usuario.bo.CategoriaAreaBOImpl;
import explorador.usuario.bo.HistorialBO;
import explorador.usuario.bo.HistorialBOImpl;
import explorador.usuario.bo.UsuarioBO;
import explorador.usuario.bo.UsuarioBOImpl;
import explorador.usuario.modelo.CategoriaArea;
import explorador.usuario.modelo.Usuario;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@WebListener
public class AppStartupListener implements ServletContextListener {

    private ScheduledExecutorService planificador;
    private ScheduledExecutorService planificadorPoda;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        UsuarioBO usuarioBO = new UsuarioBOImpl();
        CategoriaAreaBO areaBO = new CategoriaAreaBOImpl();
        FuentesBO fuentesBO = new FuentesBOImpl();
        PublicacionesBO publicacionesBO = new PublicacionesBOImpl();
        NotificadorBO notificadorBO = new NotificadorBOImpl();
        BibliotecaBO bibliotecaBO = new BibliotecaBOImpl();
        HistorialBO historialBO = new HistorialBOImpl();

        int minutos = Integer.parseInt(
                ExploradorConfig.obtener("fuente.arxiv.intervalo_minutos", "1"));

        planificador = Executors.newSingleThreadScheduledExecutor();
        planificador.scheduleAtFixedRate(() -> ejecutarCiclo(usuarioBO, areaBO, fuentesBO, publicacionesBO, notificadorBO),
                0, minutos, TimeUnit.MINUTES);

        int podaHoras = Integer.parseInt(
                ExploradorConfig.obtener("publicaciones.poda_intervalo_horas", "24"));
        planificadorPoda = Executors.newSingleThreadScheduledExecutor();
        planificadorPoda.scheduleAtFixedRate(() -> ejecutarPoda(bibliotecaBO, publicacionesBO, historialBO, notificadorBO),
                podaHoras, podaHoras, TimeUnit.HOURS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (planificador != null) {
            planificador.shutdownNow();
        }
        if (planificadorPoda != null) {
            planificadorPoda.shutdownNow();
        }
    }

    private void ejecutarCiclo(UsuarioBO usuarioBO, CategoriaAreaBO areaBO, FuentesBO fuentesBO,
                               PublicacionesBO publicacionesBO, NotificadorBO notificadorBO) {
        try {
            List<CategoriaArea> areas = areaBO.listar();
            if (areas.isEmpty()) {
                System.out.println("Sin areas de interes configuradas, se omite la consulta de fuentes.");
                return;
            }

            Set<String> categorias = categoriasArxiv(areas);

            List<PublicacionOriginal> originales = fuentesBO.procesar(categorias);
            List<Publicacion> nuevas = publicacionesBO.registrarBrutas(originales);
            System.out.println("Ciclo de fuentes: " + nuevas.size() + " publicaciones nuevas.");

            if (nuevas.isEmpty()) {
                return;
            }

            List<Publicacion> mejores = publicacionesBO.rankear(nuevas, categorias);
            int max = Integer.parseInt(ExploradorConfig.obtener("notificaciones.max_por_batch", "5"));
            List<Publicacion> top = mejores.stream().limit(max).toList();

            Usuario usuario = usuarioBO.obtener();
            if (usuario != null && usuario.getCorreo() != null && !usuario.getCorreo().isBlank()) {
                notificadorBO.notificarNuevas(top, usuario.getCorreo());
            }
        } catch (Exception e) {
            System.err.println("Error en el ciclo de consulta de fuentes: " + e.getMessage());
        }
    }

    private void ejecutarPoda(BibliotecaBO bibliotecaBO, PublicacionesBO publicacionesBO,
                              HistorialBO historialBO, NotificadorBO notificadorBO) {
        try {
            Set<Integer> protegidos = bibliotecaBO.listarGuardadas().stream()
                    .map(EntradaBiblioteca::getPublicacionId)
                    .collect(Collectors.toSet());

            Set<Integer> removidos = publicacionesBO.podar(protegidos);
            if (removidos.isEmpty()) {
                return;
            }

            historialBO.limpiarPorPublicacionIds(removidos);
            notificadorBO.limpiarPorPublicacionIds(removidos);
            System.out.println("Poda: " + removidos.size() + " publicaciones eliminadas.");
        } catch (Exception e) {
            System.err.println("Error en la poda de publicaciones: " + e.getMessage());
        }
    }

    private Set<String> categoriasArxiv(List<CategoriaArea> areas) {
        Set<String> categorias = new HashSet<>();
        for (CategoriaArea area : areas) {
            categorias.addAll(area.getArxiv());
        }
        return categorias;
    }
}
