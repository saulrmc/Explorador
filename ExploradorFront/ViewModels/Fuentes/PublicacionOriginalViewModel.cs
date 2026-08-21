namespace ExploradorFront.ViewModels.Fuentes;

public class PublicacionOriginalViewModel
{
    public string IdOrigen { get; set; } = "";
    public string Fuente { get; set; } = "";
    public string Titulo { get; set; } = "";
    public string Resumen { get; set; } = "";
    public List<string> Autores { get; set; } = [];
    public DateOnly FechaPublicacion { get; set; }
    public List<string> Etiquetas { get; set; } = [];
    public List<string> PalabrasClave { get; set; } = [];
    public string Url { get; set; } = "";
    public double Confianza { get; set; }
}
