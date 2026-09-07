# **Reqs funcionales**

| Código | Nombre | Requisito |
| :---- | :---- | :---- |
| RF01 | Notificación de nuevas publicaciones | El producto de software deberá notificar al usuario de la publicación de un nuevo paper. [Por ahora solo papers; no se descarta más tipos de fuentes] |
| RF02 | Registro de áreas de interés | El producto de software deberá permitir al usuario registrar una o varias áreas de interés científico para personalizar el contenido mostrado. |
| RF03 | Modificación de áreas de interés | El producto de software deberá permitir al usuario modificar sus áreas de interés en cualquier momento. |
| RF04 | Categorías de áreas de interés | El producto de software deberá definir una cantidad predeterminada de categorías de áreas de interés. |
| RF05 | Consulta periódica de fuentes | El producto de software deberá consultar periódicamente las fuentes de información configuradas para detectar nuevas publicaciones relacionadas con los intereses del usuario. [En v1 la única fuente configurada es arxiv] |
| RF06 | Presentación limitada de publicaciones | El producto de software deberá mostrar una cantidad limitada de publicaciones nuevas. La "mejor" opción se define con un puntaje determinista. El resto de alternativas quedan ocultas hasta que el usuario decida "ver más" opciones. |
| RF07 | Título y descripción de publicaciones | El producto de software deberá presentar un título y una descripción introductoria de no más de 10 y 40 palabras respectivamente de cada publicación con el objetivo de ayudar al usuario a decidir si desea leer el artículo completo, sin sustituir su contenido. Las primeras 5 palabras del título deben ser las más importantes para despertar interés en la publicación. [El motivo de esto es que en dispositivos móviles el correo, que es donde pienso mandar las notificaciones, suele mostrar de 5 a 7 palabras por correo] |
| RF08 | Acceso al artículo original | El producto de software deberá permitir acceder al artículo científico original desde la plataforma. |
| RF09 | Guardado en biblioteca personal | El producto de software deberá permitir al usuario guardar publicaciones de interés en una biblioteca personal. |
| RF10 | Consulta de la biblioteca personal | El producto de software deberá permitir consultar posteriormente las publicaciones almacenadas en la biblioteca personal. |
| RF11 | Identificación de conceptos relevantes | El producto de software deberá identificar conceptos relevantes presentes en una publicación de forma heurística (frecuencia de términos), sin modelos de IA. |
| RF12 | Publicaciones relacionadas | El producto de software deberá ofrecer al usuario una lista de publicaciones relacionadas con una publicación consultada para facilitar el descubrimiento de contenido afín sin requerir una búsqueda explícita. |
| RF13 | Registro de publicaciones consultadas | El producto de software deberá registrar las publicaciones consultadas por el usuario. |
| RF14 | Recuperación del historial | El producto de software deberá permitir recuperar el historial de publicaciones consultadas. |

