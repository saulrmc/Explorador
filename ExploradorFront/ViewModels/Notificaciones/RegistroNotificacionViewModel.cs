namespace ExploradorFront.ViewModels.Notificaciones;

public class RegistroNotificacionViewModel
{
    public int PublicacionId { get; set; }
    public string Asunto { get; set; } = "";
    public DateTime Enviado { get; set; }
}
