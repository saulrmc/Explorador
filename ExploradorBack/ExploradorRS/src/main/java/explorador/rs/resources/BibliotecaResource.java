package explorador.rs.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import explorador.biblioteca.bo.BibliotecaBO;
import explorador.biblioteca.bo.BibliotecaBOImpl;
import explorador.biblioteca.modelo.EntradaBiblioteca;
import explorador.publicaciones.bo.PublicacionesBO;
import explorador.publicaciones.bo.PublicacionesBOImpl;
import explorador.publicaciones.modelo.Publicacion;

import java.util.List;
import java.util.Map;

@Path("/v1/biblioteca")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BibliotecaResource {

    private final BibliotecaBO bibliotecaBO;
    private final PublicacionesBO publicacionesBO;

    public BibliotecaResource() {
        this.bibliotecaBO = new BibliotecaBOImpl();
        this.publicacionesBO = new PublicacionesBOImpl();
    }

    @GET
    public List<EntradaBiblioteca> listarGuardadas() {
        return bibliotecaBO.listarGuardadas();
    }

    @GET
    @Path("/{id}")
    public Response obtener(@PathParam("id") int id) {
        EntradaBiblioteca entrada = bibliotecaBO.obtener(id);
        if (entrada == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Publicacion guardada no encontrada"))
                    .build();
        }
        return Response.ok(entrada).build();
    }

    @GET
    @Path("/tema/{tema}")
    public List<EntradaBiblioteca> listarPorTema(@PathParam("tema") String tema) {
        return bibliotecaBO.listarPorTema(tema);
    }

    @POST
    @Path("/{publicacionId}")
    public Response guardar(@PathParam("publicacionId") int publicacionId) {
        Publicacion publicacion = publicacionesBO.obtener(publicacionId);
        if (publicacion == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Publicacion no encontrada"))
                    .build();
        }
        try {
            EntradaBiblioteca guardada = bibliotecaBO.guardar(publicacion);
            return Response.status(Response.Status.CREATED).entity(guardada).build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") int id) {
        try {
            bibliotecaBO.eliminar(id);
            return Response.noContent().build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
}
