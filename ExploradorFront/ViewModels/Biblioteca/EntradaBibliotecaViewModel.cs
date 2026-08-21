using ExploradorFront.ViewModels.Publicaciones;

namespace ExploradorFront.ViewModels.Biblioteca;

public class EntradaBibliotecaViewModel
{
    public int Id { get; set; }
    public DateTime FechaGuardado { get; set; }
    public PublicacionViewModel? Publicacion { get; set; }

    public int PublicacionId => Publicacion?.Id ?? 0;
}
