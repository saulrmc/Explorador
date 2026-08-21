namespace ExploradorFront.ViewModels.Biblioteca;

public class NodoTemaViewModel
{
    public int Id { get; set; }
    public string Nombre { get; set; } = "";
    public List<int> PublicacionIds { get; set; } = [];
}
