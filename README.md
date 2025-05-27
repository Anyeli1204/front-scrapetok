[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/u_WItgZm)
# ScrapeTok: Plataforma de Análisis para TikTok 
CS2031 - Desarrollo Basado en Plataforma

## Integrantes
|  Name|  Contact |
|-----------|-----------|
| Anyeli Azumi Tamara Ureta   | anyeli.tamara@utec.edu.pe |
| Bruno Gonzalo Vega Napan    | bruno.vega@utec.edu.pe |
|Gerard Deker Iruri Espinoza  | gerard.iruri@utec.edu.pe |
| Josue Carrillo Paurinotto   | josue.carrillo@utec.edu.pe |
|Luis Enrique Cahuana Garcia  | luis.cahuana@utec.edu.pe|

## Indice
- [Introducción]
- [Objetivos del Proyecto]
- [Identificación del Problema o Necesidad]
- [Descripción de la Solución]
- [Modelo de Entidades]
- [Testing y Manejo de Errores]
- [Medidas de Seguridad Implementadas]
- [Eventos y Asincronía]
- [GitHub]
- [Conclusión]
- [Referencias]


## Introducción
TikTok se ha posicionado como una de las redes sociales más influyentes del mundo, donde millones de creadores y marcas compiten por captar la atención de los usuarios. En este entorno altamente competitivo, comprender el rendimiento de cada publicación y compararlo con el de la competencia se vuelve crucial. Sin embargo, actualmente muchas personas gestionan esta información de forma manual, recolectando métricas post por post y almacenándolas en hojas de Excel, lo que limita el análisis por su lentitud, poca precisión y falta de escalabilidad.

ScrapeTok surge como una solución, utilizando la técnica de Web Scrapping y en conjunto con la API externa Apify se automatiza la recolección de metadatos públicos de TikTok y los almacena en una Base de Datos relacional para que después esto datos se transforme rápidamente en  visualizaciones dinámicas como gráficos de barras o líneas que muestran patrones de comportamiento, evolución de interacciones y tendencias emergentes.

## Objetivos del Proyecto
### Objetivo principal
Desarrollar una plataforma que facilite a creadores de contenido y marcas el análisis del desempeño de sus publicaciones en TikTok. Para ello, la plataforma procesará, almacenará y presentará los datos previamente extraídos mediante una API externa (APIFY), ofreciendo a los usuarios reportes detallados y de fácil interpretación.

### Objetivos específicos 
- Brindar reportes sobre el alcance de los contenidos más virales en cada nicho (deportes, cocina, entre otros) en TikTok.
- Implementar filtros personalizados que permitan a los usuarios acceder y segmentar la información según sus intereses.
- Generar gráficos dinámicos basados en los filtros aplicados, con opción de descarga.
- Permitir la descarga de los registros filtrados.

## Identificación del Problema o Necesidad
### Descripción del Problema
La generación de contenido es una carrera constante por captar la atención del público. Tik Tok, una de las plataformas más dinámicas y con mayor crecimiento, representa una gran oportunidad para que usuarios, influencers y marcas  destaquen. No obstante, esta misma velocidad con la que evoluciona el contenido plantea un problema: No existe software accesible a personas corrientes dedicado al análisis de tendencias. Los creadores de contenido, marcas e influencers no poseen herramientas para realizar una comparación entre sus vistas con la competencia.

TikTok no ofrece estadísticas públicas detalladas ni funcionalidades avanzadas para resolver el problema. Por lo que para obtener esta información se requiere navegar manualmente por perfiles y hashtags. Para solucionar este problema Scraptok propone lo siguiente:

- Monitorear el rendimiento de publicaciones propias y ajenas.
- Detectar hashtags y cuentas en tendencia.
- Realizar consultas filtradas por fechas, palabras clave o engagement.
- Visualizar datos de forma clara en formato de gráficos y tablones.

### Justificación
Es relevante solucionar el problema pues el marcado de tik tok es muy extenso permitiendo que marcas, influencers, o personas en general se interesen en Scapetok. Estos requieren conocer su desempeño frente a otros, para poder así tomar decisiones basados en datos concretos, ajustar su estrategia de marketing para tener mayor alcance y optimizar las publicaciones realizadas.

## Descripción de la Solución
### Funcionalidades Implementadas:
La aplicación scrapetok se diseñó con las siguientes características:
- **Autenticación y roles (Usuario / Admin)**
Existe una separación de permisos y funcionalidades según el rol asignado. Los administradores pueden otorgar permisos de administrador a otros usuarios.
- **Scrapeo de contenido TikTok por hashtags, perfiles o palabras clave**
Los usuarios pueden enviar solicitudes para obtener información de posts. A estas solicitudes se puede aplicar filtros para realizar una búsqueda según hashtags específicos, fechas, nombres de usuarios o términos clave. Esta funcionalidad realiza un llamado a APIFY por lo que por cada scrapeo realizado existe costo que es asumido en la demo por el administrador, costando alrededor de 0.004 dólares por post scrapeado.
- **Consulta a la base de datos (Data base queries)**
Los usuarios que hayan realizado como mínimo un scrapeo tienen acceso a consultar la data scrapeada  las veces que se desee. Se puede personalizar la búsqueda con filtros más específicos en comparación con la funcionalidad del llamado a APIFY.
- **Scrapeo del contenido viral del momento**
Los administradores pueden enviar solicitudes para obtener información sobre los videos más virales del momento. Para ello, definen filtros de búsqueda personalizados, como hashtags o palabras clave presentes en las publicaciones. Esta funcionalidad también realiza un llamado a la API externa, por lo que existe un costo que es asumido por el mismo administrador.
- **Envío automático de alertas por correo electrónico**
Sistema de eventos asincrónicos que envía resúmenes globales diarios del contenido viral directamente a los usuarios, agrupados en lotes para optimizar el rendimiento.
- **Visualización dinámica de los datos (Gráficas y Tablones)**
Para facilitar la comprensión de los datos, se generan gráficos y tablas. Cuando un usuario realiza una consulta a la base de datos, los resultados se presentan mediante gráficos que representan su búsqueda. De manera similar, cuando un administrador ejecuta un proceso de scrapeo del contenido viral , los datos obtenidos también se visualizan a través de gráficos y tablas.
- **Historial de búsquedas y filtro personalizados**
Cada búsqueda realizada por el usuario queda registrada en su historial junto con los filtros aplicados y el número de cuentas analizadas, lo que permite trazabilidad y análisis posterior. Esta información aparecerá en la pestaña correspondiente al perfil del usuario en el frontend.
- **Sistema de preguntas y respuestas**
Los usuarios pueden enviar preguntas al administrador, quien responde desde un panel propio, generando así un historial de interacción y soporte.

### Tecnologías Utilizadas
Se usaron las siguientes tecnologías y herramientas:
- Spring Security – Autenticación y control de accesos.
- JPA / Hibernate – Gestión de persistencia de datos y mapeo objeto-relacional.
- PostgreSQL – Base de datos en desarrollo y producción.
- Apify API – API externa utilizado para la obtención de información pública de TikTok.
- JavaMail / Spring Events – Envío asincrónico de correos electrónicos por lotes.
- ModelMapper – Para transformar automáticamente entidades a DTOs.

## Modelo de Entidades
### Diagrama Entidad-Relación (ER)
El siguiente diagrama ER representa las entidades principales y sus relaciones:

### Descripción de Entidades
|  Entidad  |  Descripción |
|-----------|-----------|
| GENERALACCOUNT  | Representa a cualquier usuario registrado. Contiene información personal y rol asignado (ADMIN o regular) |
| ADMIN PROFILE | Representa a un administrador. Contiene información específica para los administradores, pues estos pueden emitir alertas, responder preguntas y ejecutar scrapping global |
| USERAPIFYCALLHISTORIAL  | Almacena el historial de scraping del usuario. Incluye filtros y cuentas analizadas |
| USERAPIFFILTERS | Almacena los filtros usados por el usuario cuando realizo el web scrapping |
|DAILYALERTS | Notificaciones sobre los post más virales que se envían por email a usuarios|
|QUESTANDANSWER | Representa el registro de las preguntas hechas por usuarios y respuestas brindadas por administradores|
|ADMINTIKTOKMETRICS | Registra el scrapeo realizado por los administradores correspondiente a los post más virales|
|USERTIKTOKMETRICS | Registra el scrapeo realizado por los usuarios.|


**OneToOne**: GeneralAccount y AdminProfile. Un administrador tiene atributos específicos que un usuario no tiene. 

**OneToOne**: GeneralAccount y UserApifyCallHistorial. Un usuario registrado después de realizar un scrapeo tiene un solo historial.

**OneToMany**: GeneralAccount y UserTikTokMetrics. Un usuario puede realizar varios webs scrapings.

**OneToMany**: GeneralAccount y QuestionsAndAnswers. Un usuario puede realizar varias preguntas.

**ManyToMany**: GeneralAccount y DailyAlerts. Varios usuarios reciben varias alertas de los contenidos más virales.

**OneToMany**: AdminProfile y QuestionAndAnsers: Un administrador puede contestar varias preguntas.

**OneToMany**: AdminProfile y DailyAlerts: Un administrador puede crear varias alertas.

**OneToMany**: AdminProfile y AdminTikTokMetrics: Un administrador puede realizar varios scrapeos de contenidos más virales.

## Testing y Manejo de Errores
### Niveles de Testing Realizados
Durante el desarrollo del backend de ScrapeTok, se implementaron diferentes niveles de pruebas.
- **Pruebas Unitarias**
- **Pruebas con TestContainers**
Se utilizaron herramientas como JUnit junto con TestContainers para probar el correcto funcionamiento de la capa de persistencia. Se verificó que la tabla correspondiente a la entidad se cree correctamente en la base de datos, así como las operaciones básicas de CRUD.
- **Pruebas de API**
Se utilizaron herramientas como Postman para probar los endpoints expuestos, verificando códigos de respuesta HTTP, estructura del JSON y comportamiento esperado ante entradas válidas e inválidas.
- **Validación de DTOs y Anotaciones**
Las clases DTO fueron testeadas a través de validación automática con anotaciones como @NotNull, @NotBlank, @Valid, permitiendo detectar errores en datos incompletos o mal 

### Resultados
El testing permitió identificar y corregir errores en múltiples áreas, como:
- Control de respuestas conflictivas (por ejemplo, preguntas ya respondidas)
- Validaciones fallidas en los campos requeridos (IDs nulos, strings vacíos)
- Manejo correcto de errores de conexión con servidores externos como Apify

### Manejo de Errores
A través de la clase GlobalExceptionHandler, se interceptan y gestionan los errores lanzados desde los servicios y controladores.
|  Excepción  |  Código HTTP | Significado |
|-----------|-----------|-----------|
|  ResourceNotFoundException  |  404 Not Found | Se lanza cuando un recurso no existe en la BD |
|  IllegalArgumentException  |  400 Bad Request | Se lanza por argumentos inválidos del cliente |
|  ApifyConnectionException  |  502 Bad Gateway | Falla al conectarse con el servicio de Apify |
|  ServiceUnavailableException  |  503 Service Unavailable | Servicios temporales fuera de línea |
|  MethodArgumentNotValidException  |  400 Bad Request | Errores de validación en las solicitudes |
|  Exception  |  500 Internal Server Error | Fallos no previstos en la aplicación |

## Medidas de Seguridad Implementadas
### Seguridad de datos:
Para mantener la seguridad de los datos se implementó lo siguiente:
- Tokens JWT. Se usaron tokens jwt para que cada usuario reciba un token único al registrarse o iniciar sesión. Los tokens tienen fecha de expiración para evitar reutilización.
- Validación de los inputs. Se usaron anotaciones de validación como @NotNull, @NotBlank, @Valid, y @Enumerated para asegurar que los datos entrantes cumplieran con las reglas esperadas
- DTOs. Se usaron dtos para evitar la exposición de atributos sensibles y limitar el alcance sobre lo que los clientes pueden enviar o recibir.

### Prevención de Vulnerabilidades
Para evitar que se exploten vulnerabilidades se realizó lo siguiente:
- **Protección contra Inyección SQL** Todo acceso a base de datos se realiza mediante Spring Data JPA, previniendo ataques por inyección de comandos SQL.
- **Prevención de XSS e Inyecciones HTML/Script** No se realiza renderizado de datos en el backend. Las respuestas están diseñadas para ser consumidas por el frontend.
- **Manejo centralizado de errores** El sistema de manejo de excepciones asegura que no se filtren información confidencial al cliente

## Eventos y Asincronia
### Eventos Utilizados
Como se mencionó en una de las funcionalidades que se implementó es el envío de alertas, esto se realizó con sistema de eventos personalizados basado en el mecanismo de eventos de Spring (ApplicationEventPublisher) para la gestión del envío masivo de correos electrónicos de alertas globales diarias. 

El evento utilizado fue AlertEmailEvent, que encapsula la el destinatario, el asunto y el cuerpo, de manera desacoplada del controlador o servicio que lo genera.

### ¿Comó se implemento?
- En el servicio TopGlobalEmailService, una vez generada la alerta y construidos los correos, se particionó la lista de usuarios en lotes de 50 destinatarios.
- Por cada destinatario de cada lote, se publicó un evento AlertEmailEvent
- El listener correspondiente (AlertEmailListener) se encarga de procesar el evento y enviar el correo electrónico.

### Importancia de la asincronía en la Implementación
El envío masivo de emails es una tarea intensiva. Procesarla asincrónicamente permite que el backend siga atendiendo solicitudes sin quedar bloqueado. Además al ejecutarse fuera del flujo principal, se permite capturar errores e incluso colas sin impactar al usuario. El envío  por lotes, permite también que no sobrecargar el servidor SMTP y generar error por límites.

## GitHub
### Uso de GitHub Projects
A través del uso de Github Project se logro:
- **Asignar  Issues por cada funcionalidad implementada**
Cada funcionalidad del backend (como autenticación, manejo de filtros, integración con Apify, envío de correos, manejo de errores, etc.) fue registrada como un issue, permitiendo dividir el trabajo y priorizar las tareas.

- **Revisión mediante Pull Requests** 
Toda nueva funcionalidad fue integrada al branch principal (main) a través de  pull request asociados a los issues. Se utilizó el sistema de revisión para asegurar la calidad del código antes del merge.

## Conclusión
### Logros del proyecto
Se logró cumplir satisfactoriamente con los objetivos propuestos al inicio. Algunos de estos logros fueron:
- Extracción efectiva de información viral desde TikTok utilizando la API de Apify, permitiendo a usuarios y administradores obtener métricas relevantes por hashtags, cuentas o palabras clave.

- Visualización y almacenamiento estructurado de los datos scrapeados, a través de tablas y gráficas.

- Implementación de alertas masivas por correo con contenido de los post más virales, contribuyendo a una mejor experiencia informativa.  

### Aprendizajes
Durante el desarrollo del proyecto, el equipo adquirió conocimientos como:
- Uso de Spring Boot para diseñar la API,  incluyendo seguridad, validaciones, arquitectura por capas, y acceso a datos con JPA.

- Uso de servicios externos (Apify) y su integración con lógica de negocio. 

- Buenas prácticas de mantenimiento del código, documentación, y versionamiento a través de GitHub.

- Aplicación de patrones de diseño, especialmente en el manejo de errores y validaciones globales.


### Trabajo Futuro
Algunas mejoras a implementar en el futuro son:
- Sistemas de recomendación personalizados, basados en hashtags más vistos o contenidos más interactuados.
- Automatización completa del envío de alertas con triggers temporales 
- Programación automática del scrapeo del contenido viral y el posterior envío de correos 
- Manejo de pagos (actualmente el administrador asume el pago de todos los usuarios)
- Implementación de tiers de suscripción a la app.

## Referencias
A continuación, se listan las fuentes y herramientas que sirvieron de apoyo para el desarrollo del proyecto:
- Spring Boot Documentation
- Spring Security Docs: https://spring.io/projects/spring-security
- Jakarta Validation API
- ModelMapper
- GitHub
- Apify Official Docs: https://docs.apify.com/
- OpenAI ChatGPT – Apoyo en generación de estructuras y recomendaciones técnicas.





