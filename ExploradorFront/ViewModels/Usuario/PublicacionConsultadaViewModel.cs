namespace ExploradorFront.ViewModels.Usuario;

public class PublicacionConsultadaViewModel
{
    public int Id { get; set; }
    public int PublicacionId { get; set; }
    public string Titulo { get; set; } = "";
    public DateTime FechaConsulta { get; set; }
}
