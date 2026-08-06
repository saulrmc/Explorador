# ADR-004 – Publicaciones como fuente única de datos visibles con retención

## Contexto

El frontend necesita la lista de publicaciones que el usuario puede revisar (RF06) sin aplicar lógica de negocio. Las publicaciones se persisten en JSON en el módulo `Publicaciones`. La biblioteca guarda solo referencias por ID (ADR-002) y el historial y las notificaciones también referencian publicaciones por ID. Sin control de crecimiento, los archivos JSON crecerían indefinidamente (RNF03: ~100 publicaciones diarias).

## Decisión

El JSON de `Publicaciones` es la base de datos de publicaciones visibles en el sistema. Se aplica retención: las publicaciones cuya fecha de ingreso supera una antigüedad configurable (por defecto una semana) y que **no están guardadas en la biblioteca** se eliminan en una poda periódica. Las guardadas en la biblioteca quedan protegidas. Tras la poda se eliminan las referencias colgantes del historial y de los registros de notificación. El frontend ordena y presenta las publicaciones; la poda no afecta el acceso al artículo original (la URL queda siempre disponible).

## Justificación

Mantiene acotado el tamaño de los archivos, evita la duplicación (ADR-002) y coincide con la filosofía de `SW.md`: el sistema incentiva la lectura de la fuente original, no retiene el contenido. Historial y notificaciones guardan sus propios datos de presentación (título/asunto), por lo que referencias colgantes temporales no rompen su uso; el acceso al contenido podado se mantiene por la URL del original.

## Consecuencias

### Positivas

- Archivos JSON acotados; sin duplicación.
- Frontend simple: obtiene de `Publicaciones` lo que puede mostrar y ordena.
- Alineado con `SW.md` (acceso a la fuente original, no retención de contenido).

### Negativas

- Contenido antiguo no está disponible en la plataforma tras la poda (mitigado por la URL del original).
- Referencias colgantes en historial y notificaciones entre podas (se limpian en cada poda).
- El frontend debe evitar mostrar publicaciones repetidas (mitigado porque se prioriza lo nuevo).

## Alternativa evaluada

Se consideró que la biblioteca guardara una copia (snapshot) completa de la `Publicacion` en cada `PublicacionGuardada`, lo que haría la biblioteca autosuficiente y la poda trivial por antigüedad. Se descartó porque duplicaba datos (ADR-002) y porque no le servía al frontend la lista de publicaciones desde `Publicaciones` sin lógica adicional. Si en el futuro se desea volver a esa opción, implicaría migrar las guardadas existentes para incorporar el snapshot.
