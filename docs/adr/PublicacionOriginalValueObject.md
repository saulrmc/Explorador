# ADR-005 – `PublicacionOriginal` como value object embebido en `Publicacion`

## Contexto

`Publicacion` (módulo Publicaciones) necesita conservar los datos que la fuente aportó: `idOrigen`, `fuente`, `url`, `autores`, `fechaPublicacion`, `etiquetas`, `palabrasClave`, `confianza`, `titulo` y `resumen` completos. Estos datos los produce el módulo Fuentes (`PublicacionOriginal`, renombrada desde `PublicacionBruta`) y se consumen junto con la publicación procesada en casi todos los usos: dedupe por origen, ranking, publicaciones relacionadas, biblioteca, notificaciones y la API.

ADR-002 establece que las referencias entre entidades de módulos distintos se almacenan por ID, salvo excepciones explícitas y documentadas. Al embeder una clase del módulo Fuentes dentro del JSON persistido de Publicaciones, esta composición podría parecer una excepción a esa regla.

## Decisión

`PublicacionOriginal` es un **value object**, no una entidad: no posee identidad propia (no tiene id de sistema), no se persiste en el módulo Fuentes ni tiene ciclo de vida allí; es producida por el adapter y consumida por el formateador.

`Publicacion` la **embebe** (composición): `Publicacion.original` referencia la instancia que llega desde Fuentes y ambas se serializan juntas en el archivo `Data/Publicaciones/publicaciones.json`. No existe un JSON propio para el original en Fuentes.

Esta composición se documenta como una **no-excepción aclarada** de ADR-002: a quien revise el código le puede parecer una excepción (un tipo de otro módulo persistido en el archivo de otro módulo), pero no lo es del todo, porque no se trata de una referencia a una entidad con identidad propia y no hay duplicación entre archivos.

## Justificación

- El original se consume siempre junto con la publicación procesada; embebido, una sola lectura aporta todo sin joins por ID entre módulos.
- No hay duplicación: el original no se persiste en ningún otro archivo, por lo que no existe el riesgo de copias desactualizadas que motivó ADR-002.
- Un esquema de referencia por ID obligaría a Fuentes a persistir y retener los originales, a coordinar su poda con la de Publicaciones (ADR-004) y a resolver el join en cada consulta, sin beneficio dado que el dato nace y muere con la publicación.
- La dependencia de tipo Publicaciones → Fuentes ya existía (registro de publicaciones originales).

## Consecuencias

### Positivas

- `Publicacion` es autocontenida: la API y los módulos consumidores obtienen todo en una sola lectura.
- Sin riesgo de referencias colgantes hacia el original.
- El JSON queda autodescriptivo: separa lo aportado por la fuente (`original`) de lo procesado/presentado.

### Negativas

- El formato de persistencia de Publicaciones queda acoplado a una clase de Fuentes: un cambio de forma en `PublicacionOriginal` implica migrar `publicaciones.json`.
- Se retiene `titulo` y `resumen` completos aunque no se consumen tras el formateo, aumentando el tamaño del archivo (ver alternativa evaluada).

## Alternativa evaluada

Se consideró omitir `titulo` y `resumen` completos de la serialización para reducir el tamaño del JSON y alinearse con la filosofía de `SW.md` y ADR-004 (incentivar la lectura de la fuente original, no retener contenido). Se descartó por ahora en favor de conservar el original completo; si en el futuro se desea, basta excluir esos dos campos de la serialización sin cambios estructurales.
