package explorador.rs.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import explorador.data.ExploradorConfig;
import explorador.publicaciones.bo.PublicacionesBO;
import explorador.publicaciones.bo.PublicacionesBOImpl;
import explorador.publicaciones.conceptos.DefinicionConcepto;
import explorador.publicaciones.modelo.Publicacion;
import explorador.usuario.bo.CategoriaAreaBO;
import explorador.usuario.bo.CategoriaAreaBOImpl;
import explorador.usuario.bo.HistorialBO;
import explorador.usuario.bo.HistorialBOImpl;
import explorador.usuario.modelo.CategoriaArea;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Path("/v1/publicaciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PublicacionesResource {

    private final PublicacionesBO publicacionesBO;
    private final CategoriaAreaBO areaBO;
    private final HistorialBO historialBO;

    public PublicacionesResource() {
        this.publicacionesBO = new PublicacionesBOImpl();
        this.areaBO = new CategoriaAreaBOImpl();
        this.historialBO = new HistorialBOImpl();
    }

    @GET
    public Response listarLimitadas(@QueryParam("limite") Integer limite) {
        int max = limite != null ? limite
                : Integer.parseInt(ExploradorConfig.obtener("explorador.limite_publicaciones", "10"));
        return Response.ok(publicacionesBO.listarLimitadas(obtenerCategorias(), max)).build();
    }

    @GET
    @Path("/todas")
    public List<Publicacion> listarTodas() {
        return publicacionesBO.listar();
    }

    @GET
    @Path("/{id}")
    public Response obtener(@PathParam("id") int id) {
        Publicacion publicacion = publicacionesBO.obtener(id);
        if (publicacion == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Publicacion no encontrada"))
                    .build();
        }
        try {
            historialBO.registrar(id, publicacion.getTitulo());
        } catch (IllegalArgumentException e) {
            System.err.println("No se pudo registrar la consulta en el historial: " + e.getMessage());
        }
        return Response.ok(publicacion).build();
    }

    @GET
    @Path("/{id}/relacionadas")
    public Response listarRelacionadas(@PathParam("id") int id,
                                       @QueryParam("limite") Integer limite) {
        if (publicacionesBO.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Publicacion no encontrada"))
                    .build();
        }
        int max = limite != null ? limite : 5;
        return Response.ok(publicacionesBO.listarRelacionadas(id, max)).build();
    }

    @GET
    @Path("/{id}/conceptos/{concepto}/definicion")
    public Response definirConcepto(@PathParam("id") int id,
                                    @PathParam("concepto") String concepto) {
        if (publicacionesBO.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Publicacion no encontrada"))
                    .build();
        }
        try {
            DefinicionConcepto definicion = publicacionesBO.definirConcepto(concepto);
            if (definicion == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "No se encontro definicion para el concepto: " + concepto))
                        .build();
            }
            return Response.ok(definicion).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_GATEWAY)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    private Set<String> obtenerCategorias() {
        Set<String> categorias = new HashSet<>();
        for (CategoriaArea area : areaBO.listar()) {
            categorias.addAll(area.getArxiv());
        }
        return categorias;
    }
}
