# Observaciones
Sinceramente no encuentro mejor manera de anotar las correcciones que voy pensando...
## 27-08-2026
- **Observación:** El módulo de Biblioteca está combinando lo que le corresponde al grafo y las publicaciones guardadas. Inicialmente sí los pensaba como uno solo pero creo que fue mala idea. Además, pensaba cambiar la clase PublicacionGuardada porque lo que se guarda en la Biblioteca no debe ser una publicación procesada, sino de la fuente original (en "crudo"). El procesamiento de los atributos que se muestran en pantalla solo sirven como enganche para la lectura, nada más.
- **Idea:** Estaba pensando en que el grafo podría tener su propia subcarpeta dentro de la Biblioteca, de modo que se separa las responsabilidades de ambas partes de manera visual pero también a nivel lógico. Para el caso de la PublicacionGuardada tenía pensado 2 alternativas: 
La primera es darle otro significado, es decir que en lugar de que se almacene una publicación procesada mejor que almacene una publicación "cruda" como instancia. 
La segunda es instanciar una clase procesada y asumir que como el procesamiento ocurre una sola vez, no habrá desfase con el contenido del cual se extrajo

## 07-09-26
- **Observación:** El mayor problema que veo en todo el sistema es que he tratado de apurarme demasiado en presentar una solución que todo mi razonamiento ha ido en esa dirección en lugar de enfocarme en el problema.
