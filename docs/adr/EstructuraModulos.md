# ADR-003 – Roles de los módulos y reglas de dependencia

## Contexto

Se adoptó un monolito modular (ADR-001). El módulo `Data` provee infraestructura compartida (`JsonPersistencia`, `ExploradorConfig`), pero también albergaba contratos genéricos (`Gestionable`, `Persistible`, `Estado`) que acoplaban a todos los DAOs y hacían que `Data` pareciera una capa. `ExploradorRS` agrupa todos los recursos REST, por lo que también puede parecer una capa.

## Decisión

- `Data` queda como **infraestructura compartida**, sin contratos genéricos. Se eliminan `Gestionable`, `Persistible` y `Estado`; cada DAO declara sus propios métodos.
- Los módulos de dominio se comunican entre sí mediante interfaces `BO`.
- `ExploradorRS` es el único módulo de **entrega/composición** y el único empaquetado como WAR. Los módulos de dominio no son desplegables por separado.

## Justificación

Preserva el monolito modular del ADR-001 y respeta RNF06/RNF07 (tareas claras y cambios locales). Al tener un único WAR, se evita un monolito distribuido. La apariencia de "capa" de `Data` y `ExploradorRS` es esperada en un monolito modular: un núcleo compartido de infraestructura y una única raíz de composición.

## Consecuencias

### Positivas

- Roles claros: dominio (negocio), infraestructura (`Data`) y entrega (`ExploradorRS`).
- Bajo acoplamiento: cambiar la persistencia u otro contrato genérico no afecta a todos los módulos.
- Cambios localizados en un módulo o un conjunto reducido de módulos.

### Negativas

- Algo de repetición en los DAOs (cada uno declara su interfaz propia).
- La regla de dependencia (dominio → `Data`; dominio → dominio vía `BO`; solo `ExploradorRS` expone REST) debe respetarse conscientemente.
