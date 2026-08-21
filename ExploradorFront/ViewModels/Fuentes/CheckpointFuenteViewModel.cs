namespace ExploradorFront.ViewModels.Fuentes;

public class CheckpointFuenteViewModel
{
    public string NombreFuente { get; set; } = "";
    public HashSet<string> IdsVistos { get; set; } = [];
    public DateTime FechaUltimaConsulta { get; set; }
}
