using ExploradorFront.ViewModels.Fuentes;

namespace ExploradorFront.ViewModels.Publicaciones;

public class PublicacionViewModel
{
    public int Id { get; set; }
    public string Titulo { get; set; } = "";
    public string Descripcion { get; set; } = "";
    public List<ConceptoViewModel> Conceptos { get; set; } = [];
    public double Score { get; set; }
    public DateTime FechaIngreso { get; set; }
    public PublicacionOriginalViewModel? Original { get; set; }
}
