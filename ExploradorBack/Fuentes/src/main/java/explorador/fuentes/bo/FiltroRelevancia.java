package explorador.fuentes.bo;

import explorador.fuentes.modelo.PublicacionBruta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FiltroRelevancia {

    public List<PublicacionBruta> filtrar(List<PublicacionBruta> publicaciones, Set<String> keywords) {
        if (publicaciones == null || publicaciones.isEmpty()
                || keywords == null || keywords.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> tokens = tokens(keywords);
        if (tokens.isEmpty()) {
            return new ArrayList<>();
        }

        List<PublicacionBruta> relevantes = new ArrayList<>();
        for (PublicacionBruta pub : publicaciones) {
            String texto = (pub.getTitulo() + " " + pub.getResumen()).toLowerCase();
            for (String token : tokens) {
                if (texto.contains(token)) {
                    relevantes.add(pub);
                    break;
                }
            }
        }
        return relevantes;
    }

    private List<String> tokens(Set<String> keywords) {
        List<String> tokens = new ArrayList<>();
        for (String keyword : keywords) {
            for (String token : keyword.toLowerCase().split("\\s+")) {
                if (token.length() >= 3) {
                    tokens.add(token);
                }
            }
        }
        return tokens;
    }
}
