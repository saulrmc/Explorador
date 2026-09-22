package explorador.notificaciones.bo;

public interface EnviadorCorreo {
    boolean enviar(String destinatario, String asunto, String contenido);
}
