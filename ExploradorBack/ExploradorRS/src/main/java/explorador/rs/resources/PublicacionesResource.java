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
import explorador.publicaciones.modelo.Publicacion;
import explorador.usuario.bo.AreaInteresBO;
import explorador.usuario.bo.AreaInteresBOImpl;
import explorador.usuario.modelo.AreaInteres;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/v1/publicaciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PublicacionesResource {

    private final PublicacionesBO publicacionesBO;
    private final AreaInteresBO areaBO;

    public PublicacionesResource() {
        this.publicacionesBO = new PublicacionesBOImpl();
        this.areaBO = new AreaInteresBOImpl();
    }

    @GET
    public Response listarLimitadas(@QueryParam("limite") Integer limite) {
        int max = limite != null ? limite
                : Integer.parseInt(ExploradorConfig.obtener("explorador.limite_publicaciones", "10"));
        return Response.ok(publicacionesBO.listarLimitadas(obtenerKeywords(), max)).build();
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
        return Response.ok(publicacion).build();
    }

    @GET
    @Path("/{id}/relacionadas")
    public Response listarRelacionadas(@PathParam("id") int id) {
        if (publicacionesBO.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Publicacion no encontrada"))
                    .build();
        }
        return Response.ok(publicacionesBO.listarRelacionadas(id, 5)).build();
    }

    private Set<String> obtenerKeywords() {
        return areaBO.listar().stream()
                .map(AreaInteres::getNombre)
                .collect(Collectors.toSet());
    }
}
