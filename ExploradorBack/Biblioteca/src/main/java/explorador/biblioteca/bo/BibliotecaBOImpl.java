package explorador.biblioteca.bo;

import explorador.biblioteca.dao.BibliotecaDAO;
import explorador.biblioteca.dao.BibliotecaDAOImpl;
import explorador.biblioteca.modelo.Arista;
import explorador.biblioteca.modelo.GrafoTematica;
import explorador.biblioteca.modelo.NodoTema;
import explorador.biblioteca.modelo.PublicacionGuardada;
import explorador.biblioteca.modelo.TipoTema;
import explorador.publicaciones.modelo.Publicacion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BibliotecaBOImpl implements BibliotecaBO {

    private final BibliotecaDAO bibliotecaDao;

    public BibliotecaBOImpl() {
        this(new BibliotecaDAOImpl());
    }

    public BibliotecaBOImpl(BibliotecaDAO bibliotecaDao) {
        this.bibliotecaDao = bibliotecaDao;
    }

    @Override
    public PublicacionGuardada guardar(Publicacion publicacion) {
        Objects.requireNonNull(publicacion, "La publicacion es obligatoria");

        List<PublicacionGuardada> guardadas = new ArrayList<>(bibliotecaDao.leerGuardadas());
        boolean existe = guardadas.stream()
                .anyMatch(guardada -> guardada.getPublicacionId() == publicacion.getId());
        if (existe) {
            throw new IllegalStateException("La publicacion ya esta guardada en la biblioteca");
        }

        PublicacionGuardada guardada = new PublicacionGuardada();
        guardada.setId(guardadas.stream().mapToInt(PublicacionGuardada::getId).max().orElse(0) + 1);
        guardada.setPublicacionId(publicacion.getId());
        guardada.setTitulo(publicacion.getTitulo());
        guardada.setUrl(publicacion.getUrl());
        guardada.setFechaGuardado(LocalDateTime.now());
        guardada.setConceptos(copiar(publicacion.getConceptos()));
        guardada.setEtiquetas(copiar(publicacion.getEtiquetas()));
        guardadas.add(guardada);
        bibliotecaDao.escribirGuardadas(guardadas);

        GrafoTematica grafo = bibliotecaDao.leerGrafo();
        enlazarTemas(grafo, publicacion);
        bibliotecaDao.escribirGrafo(grafo);

        return guardada;
    }

    @Override
    public List<PublicacionGuardada> listarGuardadas() {
        return bibliotecaDao.leerGuardadas();
    }

    @Override
    public void eliminar(int id) {
        List<PublicacionGuardada> guardadas = new ArrayList<>(bibliotecaDao.leerGuardadas());
        boolean eliminado = guardadas.removeIf(guardada -> guardada.getId() == id);
        if (!eliminado) {
            throw new IllegalStateException("No se pudo eliminar la publicacion guardada con id: " + id);
        }
        bibliotecaDao.escribirGuardadas(guardadas);
    }

    @Override
    public List<PublicacionGuardada> listarPorTema(String tema) {
        String normalizado = tema.toLowerCase();
        return bibliotecaDao.leerGuardadas().stream()
                .filter(guardada -> coinciden(guardada, normalizado))
                .toList();
    }

    private void enlazarTemas(GrafoTematica grafo, Publicacion publicacion) {
        List<String> nombres = new ArrayList<>();
        for (String concepto : copiar(publicacion.getConceptos())) {
            nombres.add(concepto.toLowerCase());
        }
        for (String etiqueta : copiar(publicacion.getEtiquetas())) {
            nombres.add(etiqueta.toLowerCase());
        }

        List<Integer> ids = new ArrayList<>();
        for (String nombre : nombres) {
            if (nombre.isBlank()) {
                continue;
            }
            NodoTema nodo = grafo.getNodos().stream()
                    .filter(n -> n.getNombre().equals(nombre))
                    .findFirst()
                    .orElseGet(() -> {
                        NodoTema nuevo = new NodoTema();
                        nuevo.setId(grafo.getNodos().stream()
                                .mapToInt(NodoTema::getId).max().orElse(0) + 1);
                        nuevo.setNombre(nombre);
                        nuevo.setTipo(nombre.contains(".") ? TipoTema.ETIQUETA : TipoTema.CONCEPTO);
                        grafo.getNodos().add(nuevo);
                        return nuevo;
                    });
            ids.add(nodo.getId());
        }

        for (int i = 0; i < ids.size(); i++) {
            for (int j = i + 1; j < ids.size(); j++) {
                int a = Math.min(ids.get(i), ids.get(j));
                int b = Math.max(ids.get(i), ids.get(j));
                Arista arista = grafo.getAristas().stream()
                        .filter(ar -> ar.getOrigenId() == a && ar.getDestinoId() == b)
                        .findFirst()
                        .orElseGet(() -> {
                            Arista nueva = new Arista();
                            nueva.setOrigenId(a);
                            nueva.setDestinoId(b);
                            nueva.setPeso(0);
                            grafo.getAristas().add(nueva);
                            return nueva;
                        });
                arista.setPeso(arista.getPeso() + 1);
            }
        }
    }

    private boolean coinciden(PublicacionGuardada guardada, String tema) {
        for (String concepto : copiar(guardada.getConceptos())) {
            if (concepto.contains(tema) || tema.contains(concepto)) {
                return true;
            }
        }
        for (String etiqueta : copiar(guardada.getEtiquetas())) {
            if (etiqueta.contains(tema) || tema.contains(etiqueta)) {
                return true;
            }
        }
        return false;
    }

    private List<String> copiar(List<String> lista) {
        return lista == null ? new ArrayList<>() : new ArrayList<>(lista);
    }
}
