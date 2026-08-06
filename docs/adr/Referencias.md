# ADR-002 – Referencias por ID entre entidades

## Contexto

Varias clases de distintos módulos necesitan referirse a entidades que pertenecen a otros módulos (`PublicacionGuardada` → `Publicacion`, `RegistroNotificacion` → `Publicacion`, `PublicacionConsultada` → `Publicacion`). La persistencia es JSON (`JsonPersistencia`) y, al serializar, JSON es un árbol: no existen referencias compartidas entre nodos.

## Decisión

Las referencias entre entidades de módulos distintos se almacenan por **ID** (clave primaria del módulo poseedor) en lugar de embeder la instancia, salvo excepciones explícitas y documentadas.

## Justificación

Si una entidad se embebe dentro de otra, el JSON se convierte en un árbol de clases: la instancia se duplica en cada archivo que la referencia, lo que agranda los archivos; además, si la entidad poseída cambia, la copia embebida en la poseedora queda desactualizada, generando dos fuentes de verdad.

## Consecuencias

### Positivas

- Archivos JSON de tamaño acotado.
- Sin desactualización entre módulos: la entidad poseída es la única fuente de verdad.
- Cambios en la entidad poseída no exigen migrar a quienes la referencian.

### Negativas

- Cada acceso a la instancia requiere una búsqueda adicional (join) en el módulo poseedor.
- Riesgo de referencias colgantes si la entidad poseída se elimina; la política de retención debe proteger las referencias vigentes (ver ADR-004).
