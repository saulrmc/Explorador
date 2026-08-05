package explorador.rs.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import explorador.usuario.bo.AreaInteresBO;
import explorador.usuario.bo.AreaInteresBOImpl;
import explorador.usuario.bo.HistorialBO;
import explorador.usuario.bo.HistorialBOImpl;
import explorador.usuario.bo.UsuarioBO;
import explorador.usuario.bo.UsuarioBOImpl;
import explorador.comun.Estado;
import explorador.usuario.modelo.AreaInteres;
import explorador.usuario.modelo.CategoriaArea;
import explorador.usuario.modelo.PublicacionConsultada;
import explorador.usuario.modelo.Usuario;

import java.util.List;
import java.util.Map;

@Path("/v1/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    private final UsuarioBO usuarioBO;
    private final AreaInteresBO areaBO;
    private final HistorialBO historialBO;

    public UsuarioResource() {
        this.usuarioBO = new UsuarioBOImpl();
        this.areaBO = new AreaInteresBOImpl();
        this.historialBO = new HistorialBOImpl();
    }

    @GET
    public Response obtenerPerfil() {
        Usuario usuario = usuarioBO.obtener();
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "El perfil no ha sido configurado"))
                    .build();
        }
        return Response.ok(usuario).build();
    }

    @PUT
    public Response actualizarPerfil(Usuario usuario) {
        try {
            usuarioBO.actualizar(usuario.getNombre(), usuario.getCorreo());
            return Response.ok(usuarioBO.obtener()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/areas")
    public List<AreaInteres> listarAreas() {
        return areaBO.listar();
    }

    @GET
    @Path("/areas/categorias")
    public List<CategoriaArea> listarCategorias() {
        return areaBO.categorias();
    }

    @POST
    @Path("/areas")
    public Response crearArea(AreaInteres area) {
        try {
            AreaInteres creada = areaBO.guardar(area, Estado.NUEVO);
            return Response.status(Response.Status.CREATED).entity(creada).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/areas/{id}")
    public Response actualizarArea(@PathParam("id") int id, AreaInteres area) {
        area.setId(id);
        try {
            if (areaBO.obtener(id) == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Area de interes no encontrada"))
                        .build();
            }
            return Response.ok(areaBO.guardar(area, Estado.MODIFICADO)).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/areas/{id}")
    public Response eliminarArea(@PathParam("id") int id) {
        try {
            areaBO.eliminar(id);
            return Response.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/historial")
    public List<PublicacionConsultada> listarHistorial() {
        return historialBO.listar();
    }

    @POST
    @Path("/historial")
    public Response registrarHistorial(PublicacionConsultada registro) {
        try {
            historialBO.registrar(registro.getPublicacionId(), registro.getTitulo());
            return Response.status(Response.Status.CREATED).entity(registro).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/historial")
    public Response limpiarHistorial() {
        historialBO.limpiar();
        return Response.noContent().build();
    }
}
