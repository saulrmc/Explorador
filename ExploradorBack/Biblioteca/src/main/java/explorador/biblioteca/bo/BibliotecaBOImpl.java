package explorador.biblioteca.bo;

import explorador.biblioteca.dao.BibliotecaDAO;
import explorador.biblioteca.dao.BibliotecaDAOImpl;
import explorador.biblioteca.modelo.Arista;
import explorador.biblioteca.modelo.EntradaBiblioteca;
import explorador.biblioteca.modelo.GrafoTematica;
import explorador.biblioteca.modelo.NodoTema;
import explorador.biblioteca.modelo.PublicacionGuardada;
import explorador.publicaciones.bo.PublicacionesBO;
import explorador.publicaciones.bo.PublicacionesBOImpl;
import explorador.publicaciones.modelo.Publicacion;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BibliotecaBOImpl implements BibliotecaBO {

    private static final Object BLOQUEO_ESCRITURA = new Object();

    private final BibliotecaDAO bibliotecaDao;
    private final PublicacionesBO publicacionesBO;

    public BibliotecaBOImpl() {
        this(new BibliotecaDAOImpl(), new PublicacionesBOImpl());
    }

    public BibliotecaBOImpl(BibliotecaDAO bibliotecaDao, PublicacionesBO publicacionesBO) {
        this.bibliotecaDao = bibliotecaDao;
        this.publicacionesBO = publicacionesBO;
    }

    @Override
    public EntradaBiblioteca guardar(Publicacion publicacion) {
        Objects.requireNonNull(publicacion, "La publicacion es obligatoria");
        validarIdPositivo(publicacion.getId());

        synchronized (BLOQUEO_ESCRITURA) {
            List<PublicacionGuardada> guardadas = new ArrayList<>(bibliotecaDao.leerGuardadas());
            boolean existe = guardadas.stream()
                    .anyMatch(guardada -> guardada.getPublicacionId() == publicacion.getId());
            if (existe) {
                throw new IllegalStateException("La publicacion ya esta guardada en la biblioteca");
            }

            PublicacionGuardada guardada = new PublicacionGuardada();
            guardada.setId(siguienteId(guardadas));
            guardada.setPublicacionId(publicacion.getId());
            guardada.setFechaGuardado(LocalDateTime.now());
            guardadas.add(guardada);
            bibliotecaDao.escribirGuardadas(guardadas);

            GrafoTematica grafo = bibliotecaDao.leerGrafo();
            enlazarTemas(grafo, publicacion);
            bibliotecaDao.escribirGrafo(grafo);

            return componer(guardada, publicacion);
        }
    }

    @Override
    public List<EntradaBiblioteca> listarGuardadas() {
        return bibliotecaDao.leerGuardadas().stream()
                .sorted(Comparator.comparing(PublicacionGuardada::getFechaGuardado).reversed())
                .map(this::componer)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public EntradaBiblioteca obtener(int id) {
        validarIdPositivo(id);
        return bibliotecaDao.leerGuardadas().stream()
                .filter(guardada -> guardada.getId() == id)
                .map(this::componer)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void eliminar(int id) {
        validarIdPositivo(id);
        synchronized (BLOQUEO_ESCRITURA) {
            List<PublicacionGuardada> guardadas = new ArrayList<>(bibliotecaDao.leerGuardadas());
            PublicacionGuardada guardada = guardadas.stream()
                    .filter(candidata -> candidata.getId() == id)
                    .findFirst()
                    .orElse(null);
            if (guardada == null) {
                throw new IllegalStateException("No se pudo eliminar la publicacion guardada con id: " + id);
            }
            guardadas.remove(guardada);
            bibliotecaDao.escribirGuardadas(guardadas);

            Publicacion publicacion = publicacionesBO.obtener(guardada.getPublicacionId());
            if (publicacion != null) {
                GrafoTematica grafo = bibliotecaDao.leerGrafo();
                desenlazarTemas(grafo, publicacion);
                bibliotecaDao.escribirGrafo(grafo);
            }
        }
    }

    @Override
    public List<EntradaBiblioteca> listarPorTema(String tema) {
        Objects.requireNonNull(tema, "El tema es obligatorio");
        String normalizado = normalizar(tema);

        GrafoTematica grafo = bibliotecaDao.leerGrafo();
        Set<Integer> ids = grafo.getNodos().stream()
                .filter(nodo -> normalizar(nodo.getNombre()).contains(normalizado))
                .flatMap(nodo -> nodo.getPublicacionIds().stream())
                .collect(Collectors.toSet());

        return bibliotecaDao.leerGuardadas().stream()
                .filter(guardada -> ids.contains(guardada.getPublicacionId()))
                .sorted(Comparator.comparing(PublicacionGuardada::getFechaGuardado).reversed())
                .map(this::componer)
                .filter(Objects::nonNull)
                .toList();
    }

    private EntradaBiblioteca componer(PublicacionGuardada guardada) {
        return componer(guardada, publicacionesBO.obtener(guardada.getPublicacionId()));
    }

    private EntradaBiblioteca componer(PublicacionGuardada guardada, Publicacion publicacion) {
        if (publicacion == null) {
            return null;
        }
        EntradaBiblioteca entrada = new EntradaBiblioteca();
        entrada.setId(guardada.getId());
        entrada.setFechaGuardado(guardada.getFechaGuardado());
        entrada.setPublicacion(publicacion);
        return entrada;
    }

    // TODO: el modelo de relaciones (aristas por co-ocurrencia) debe migrar a similitud semantica real.
    private void enlazarTemas(GrafoTematica grafo, Publicacion publicacion) {
        List<Integer> nodos = nodosPara(grafo, publicacion);
        for (int i = 0; i < nodos.size(); i++) {
            for (int j = i + 1; j < nodos.size(); j++) {
                incrementarArista(grafo, nodos.get(i), nodos.get(j));
            }
        }
    }

    private void desenlazarTemas(GrafoTematica grafo, Publicacion publicacion) {
        List<Integer> nodos = new ArrayList<>();
        for (NodoTema nodo : grafo.getNodos()) {
            if (nodo.getPublicacionIds().remove((Integer) publicacion.getId())) {
                nodos.add(nodo.getId());
            }
        }
        for (int i = 0; i < nodos.size(); i++) {
            for (int j = i + 1; j < nodos.size(); j++) {
                decrementarArista(grafo, nodos.get(i), nodos.get(j));
            }
        }
        grafo.getNodos().removeIf(nodo -> nodo.getPublicacionIds().isEmpty());
        grafo.getAristas().removeIf(arista ->
                !nodoExiste(grafo, arista.getOrigenId()) || !nodoExiste(grafo, arista.getDestinoId()));
    }

    private List<Integer> nodosPara(GrafoTematica grafo, Publicacion publicacion) {
        List<Integer> ids = new ArrayList<>();
        for (String keyword : copiar(publicacion.getOriginal().getPalabrasClave())) {
            if (keyword == null || keyword.isBlank()) {
                continue;
            }
            String normalizado = normalizar(keyword);
            NodoTema nodo = grafo.getNodos().stream()
                    .filter(n -> normalizar(n.getNombre()).equals(normalizado))
                    .findFirst()
                    .orElseGet(() -> {
                        NodoTema nuevo = new NodoTema();
                        nuevo.setId(grafo.getNodos().stream()
                                .mapToInt(NodoTema::getId).max().orElse(0) + 1);
                        nuevo.setNombre(keyword.trim());
                        grafo.getNodos().add(nuevo);
                        return nuevo;
                    });
            if (!nodo.getPublicacionIds().contains(publicacion.getId())) {
                nodo.getPublicacionIds().add(publicacion.getId());
            }
            ids.add(nodo.getId());
        }
        return ids;
    }

    private void incrementarArista(GrafoTematica grafo, int a, int b) {
        Arista arista = buscarArista(grafo, a, b);
        arista.setPeso(arista.getPeso() + 1);
    }

    private void decrementarArista(GrafoTematica grafo, int a, int b) {
        Arista arista = buscarArista(grafo, a, b);
        int peso = arista.getPeso() - 1;
        if (peso <= 0) {
            grafo.getAristas().remove(arista);
        } else {
            arista.setPeso(peso);
        }
    }

    private Arista buscarArista(GrafoTematica grafo, int a, int b) {
        int origen = Math.min(a, b);
        int destino = Math.max(a, b);
        return grafo.getAristas().stream()
                .filter(arista -> arista.getOrigenId() == origen && arista.getDestinoId() == destino)
                .findFirst()
                .orElseGet(() -> {
                    Arista nueva = new Arista();
                    nueva.setOrigenId(origen);
                    nueva.setDestinoId(destino);
                    nueva.setPeso(0);
                    grafo.getAristas().add(nueva);
                    return nueva;
                });
    }

    private boolean nodoExiste(GrafoTematica grafo, int id) {
        return grafo.getNodos().stream().anyMatch(nodo -> nodo.getId() == id);
    }

    private int siguienteId(List<PublicacionGuardada> guardadas) {
        return guardadas.stream().mapToInt(PublicacionGuardada::getId).max().orElse(0) + 1;
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinAcentos.toLowerCase(Locale.ROOT).trim();
    }

    private List<String> copiar(List<String> lista) {
        return lista == null ? new ArrayList<>() : new ArrayList<>(lista);
    }

    private void validarIdPositivo(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El id debe ser mayor a 0");
        }
    }
}
