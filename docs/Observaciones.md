# Observaciones
Sinceramente no encuentro mejor manera de anotar las correcciones que voy pensando...
## 27-08-2026
- **Observación:** El módulo de Biblioteca está combinando lo que le corresponde al grafo y las publicaciones guardadas. Inicialmente sí los pensaba como uno solo pero creo que fue mala idea. Además, pensaba cambiar la clase PublicacionGuardada porque lo que se guarda en la Biblioteca no debe ser una publicación procesada, sino de la fuente original (en "crudo"). El procesamiento de los atributos que se muestran en pantalla solo sirven como enganche para la lectura, nada más.
- **Idea:** Estaba pensando en que el grafo podría tener su propia subcarpeta dentro de la Biblioteca, de modo que se separa las responsabilidades de ambas partes de manera visual pero también a nivel lógico. Para el caso de la PublicacionGuardada tenía pensado 2 alternativas: 
La primera es darle otro significado, es decir que en lugar de que se almacene una publicación procesada mejor que almacene una publicación "cruda" como instancia. 
La segunda es instanciar una clase procesada y asumir que como el procesamiento ocurre una sola vez, no habrá desfase con el contenido del cual se extrajo

## 07-09-26
- **Observación:** El mayor problema que veo en todo el sistema es que he tratado de apurarme demasiado en presentar una solución que todo mi razonamiento ha ido en esa dirección en lugar de enfocarme en el problema.

## 08-09-26
- **Observación:** Una alternativa potencialmente útil frente al problema podría ser el uso de IA para el procesamiento de información antes de llegar a un ser humano. Aunque un LLM es bastante útil para procesar cifras gigantescas de tokens, por ejemplo superar el millón de token en el caso de los modelos más recientes de OpenIA y Anthopic (token es aproximadamente 4 caracteres o 3 cuartos de palabra en inglés [https://help.openai.com/en/articles/4936856-understanding-and-counting-tokens]), también ha generado controversia dentro de la comunidad de Arxiv con lo que se ha llamado "survey paper DDoS attack" que ha inundado la plataforma de contenido vago y repetitivo, lo que baja la calidad de la investigación y perjudica a nuevos investigadores [https://arxiv.org/html/2510.09686v1]. Aunque esto último escapa de la problemática principal no veo conveniente agregar una capa más de IA a mucho contenido que ya ha sido generado con IA.

## 20-09-26
- **Observación:** Ahora que me he remplanteado el problema considero que los únicos archivos que merecen la pena quedarse son este, el problema y la carpeta de ADRs ya que es importante tener una justificación para lo que se desarrolla independientemente de lo grande o pequeño del proyecto. 

## 21-09-26
- **Observación:** He comparado lo que quedó de la solución anterior con los requisitos actuales y el problema no era la arquitectura en sí, eso del monolito modular sigue en pie. Lo que sobraba era la presentación: ExploradorRS y ExploradorFront. El resto de módulos (Data, Fuentes, Usuario, Publicaciones) encaja bastante con los RF de ahora, así que creo que lo más conveniente es reutilizarlo en lugar de empezar de cero.
Además, me he quitado de encima la poda a los 7 días: ningún requisito la pide y si la quiero volver a meter, tendrá que ser con su justificación.
- **Pendiente:** 
  1. Cómo se disparará la sincronización diaria de ArXiv. La solución anterior sincronizaba al arrancar, eso es una opción, pero quiero pensarlo con calma en un ADR.
  2. La categorización: el enum con los códigos de ArXiv está hardcodeado.
  3. Estaba pensando en conservar el módulo de correos, pero no es una funcionalidad principal, así que lo decido cuando toque.
