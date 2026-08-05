# ADR-001 – Estilo arquitectónico

## Contexto

El proyecto consiste en un explorador de contenido académico desarrollado por una sola persona. El software requiere tener tareas claramente definidas y repartidas entre sus componentes (RNF07). Existen restricciones clave:
- Recursos limitados (RNF05): Desarrollo unipersonal y hardware estándar.


## Decisión

Se optó por implementar un monolito modular con acoplamiento cliente-servidor

## Justificación

Se eligió un monolito modular debido a que ofrece la simplicidad necesaria para que el software sea desarrollado e implementado por una persona. Además, ofrece un equilibrio favorable entre simplicidad, mantenibilidad y velocidad de desarrollo gracias a la separación en por dominios del software.

## Consecuencias

### Positivas

- Mayor velocidad de desarrollo
- Facilidad de mantenimiento
- Reutilización de código de proyectos previos

### Negativas

- Se deberá desarrollar una interfaz adicional por cada módulo que quiera comunicarse con los demás, lo que agrega complejidad de desarrollo.
