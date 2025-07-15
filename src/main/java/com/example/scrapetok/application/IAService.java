package com.example.scrapetok.application;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.example.scrapetok.repository.UserTiktokMetricsRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IAService {
    @Autowired
    UserTiktokMetricsRepository userTiktokMetricsRepository;
    private ChatCompletionsClient client;
    private final String model = "gpt-4.1";
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${github.token}")
    private String githubToken;

    @PostConstruct
    public void initClient() {
        String endpoint = "https://models.inference.ai.azure.com";
        this.client = new ChatCompletionsClientBuilder()
                .credential(new AzureKeyCredential(githubToken))
                .endpoint(endpoint)
                .buildClient();
    }

    public Map<String, String> chat1(Map<String, String> request) {
        try {
            String userMessage = request.get("message");
            if (userMessage == null || userMessage.trim().isEmpty()) {
                return Map.of("error", "El mensaje no debe estar vacío");
            }
            List<ChatRequestMessage> chatMessages = Arrays.asList(
                    new ChatRequestSystemMessage("""
            Eres un experto en análisis de tendencias y optimización de contenido en TikTok. Tu tarea es ayudarme a encontrar contenido relevante para scrapear, basándote en una temática o categoría que te proporcionaré.
            
            Tu respuesta debe estar en formato JSON con la siguiente estructura:
            
            {
              "hashtags": ["#hashtag1", "#hashtag2", "#hashtag3", "#hashtag4", "#hashtag5"],
              "keywords": ["palabra clave 1", "palabra clave 2", "palabra clave 3", "palabra clave 4", "palabra clave 5"],
              "usernames": ["@usuario1", "@usuario2", "@usuario3", "@usuario4", "@usuario5"]
            }
            
            Instrucciones:
            - Los hashtags deben ser actuales, relevantes y útiles para descubrir contenido viral relacionado con la temática dada.
            - Las keywords deben ser términos comunes en títulos o descripciones de videos de ese nicho.
            - Los usernames deben ser cuentas populares o influyentes dentro del tema, recomendadas para scrapear contenido similar o detectar patrones y tendencias.
            
            Responde únicamente con el JSON. No incluyas explicaciones, comentarios ni texto fuera del objeto JSON.
            """),
                    new ChatRequestUserMessage(userMessage)
            );
            ChatCompletionsOptions options = new ChatCompletionsOptions(chatMessages);
            options.setModel(model);
            ChatCompletions completions = client.complete(options);
            String response = completions.getChoices().get(0).getMessage().getContent();
            return Map.of("response", response);
        } catch (Exception e) {
            return Map.of("error", "Error al procesar la solicitud: " + e.getMessage());
        }
    }




    public Map<String, String> chat2() {
        try {
            LocalDate since = LocalDate.now().minusDays(3);
            List<UserTiktokMetricsRepository.HourlyInteractions> hourVsInteractions =  userTiktokMetricsRepository.findTotalInteractionsByHourPostedSince(since);
            List<UserTiktokMetricsRepository.HashtagEngagement> hashVsEng = userTiktokMetricsRepository.findAvgEngagementByHashtagSince(since);
            List<UserTiktokMetricsRepository.SoundEngagement> soundIdVsEng =  userTiktokMetricsRepository.findAvgEngagementBySoundSince(since);

            Map<String, Object> mapHours = hourVsInteractions.stream()
                    .collect(Collectors.toMap(
                            hi -> String.valueOf(hi.getHour()),
                            UserTiktokMetricsRepository.HourlyInteractions::getTotalInteractions
                    ));

            Map<String, Object> mapHashtags = hashVsEng.stream()
                    .collect(Collectors.toMap(
                            UserTiktokMetricsRepository.HashtagEngagement::getHashtag,
                            UserTiktokMetricsRepository.HashtagEngagement::getAvgEngagement
                    ));

            Map<String, Object> mapSounds = soundIdVsEng.stream()
                    .collect(Collectors.toMap(
                            UserTiktokMetricsRepository.SoundEngagement::getSoundId,
                            UserTiktokMetricsRepository.SoundEngagement::getAvgEngagement
                    ));
            List<Map<String, Object>> payload = List.of(mapHours, mapHashtags, mapSounds);
            String message = objectMapper.writeValueAsString(payload);
            //String message = "[{'cocina': 8.9,'saludable': 10.2,'lonchera': 6.8:, 'colegio':5.9},{'20121902912': 10.4,'20202020020': 21.3,'31231231233': 18.5}]";

            System.out.println(message);
            if (message == null || message.trim().isEmpty()) {
                return Map.of("error", "El mensaje no debe estar vacío");
            }
            List<ChatRequestMessage> chatMessages = Arrays.asList(
                    new ChatRequestSystemMessage("""
            Eres un analista experto en tendencias de TikTok. Yo te proporcionaré un dataset en formato JSON con:
            - Horas y el numero total de interacciones que tuvieron.
            - Hashtags y su % de engagement en un post.
            - Sonidos (identificados por su ID) y su nivel de interacción.
            Tu tarea es analizar estos datos y devolver un JSON con insights clave para optimizar futuras publicaciones, usando tanto los datos proporcionados como tu conocimiento general sobre el funcionamiento del algoritmo de TikTok.
            Debes responder en el siguiente formato JSON:
            {
              "hashtags_efectivos": ["#hashtag1", "#hashtag2", "#hashtag3", "#hashtag4", "#hashtag5"],
              "sonidos_efectivos": ["Nombre o ID del sonido 1", "Nombre o ID del sonido 2", "Nombre o ID del sonido 3"],
              "horario_ideal": {
                "hora": "Hora ideal para publicar (ej: 18:00)",
                "justificacion": "Explicación específica basada en el tipo de contenido presente en los datos y tendencias de comportamiento de los usuarios"
              },
              "duracion_promedio": {
                "segundos": "Duración estimada en segundos de los videos más exitosos",
                "justificacion": "Por qué esa duración es ideal en el contexto del contenido analizado"
              },
              "cantidad_optima_hashtags": {
                "cantidad": Número de hashtags ideal,
                "justificacion": "Por qué esa cantidad es mejor según el rendimiento de los hashtags que me diste"
              }
            }
            Instrucciones adicionales:
            - Selecciona los hashtags, sonidos y horas con mejor rendimiento del dataset.
            - No repitas frases genéricas. Usa lo que observas en los datos que te paso para dar razones específicas.
            - Por ejemplo, si hay muchos hashtags de fútbol o deporte, adapta la hora, duración y estilo de contenido a ese tipo de audiencia y su comportamiento típico.
            - Responde únicamente con el JSON. No incluyas texto adicional.
            """),
                    new ChatRequestUserMessage(message)
            );
            ChatCompletionsOptions options = new ChatCompletionsOptions(chatMessages);
            options.setModel(model);
            ChatCompletions completions = client.complete(options);
            String response = completions.getChoices().get(0).getMessage().getContent();
            System.out.println(response);
            return Map.of("response", response);
        } catch (Exception e) {
            return Map.of("error", "Error al procesar la solicitud: " + e.getMessage());
        }
    }

    public Map<String, String> chat3(Map<String, String> request) {
        try {
            String userMessage = request.get("message");
            if (userMessage == null || userMessage.trim().isEmpty()) {
                return Map.of("error", "El mensaje no debe estar vacío");
            }
            List<ChatRequestMessage> chatMessages = Arrays.asList(
                    new ChatRequestSystemMessage("""
            Eres un experto en contenido viral de TikTok. Tu tarea es ayudarme a optimizar mis publicaciones antes de subirlas.
            Yo te daré uno o varios hashtags, y tú me responderás en formato JSON con la siguiente estructura:
            {
              "titulo": "Tres ideas de título atractivas separadas por comas",
              "descripcion": "Tres descripciones creativas y llamativas separadas por comas",
              "hashtags": ["#hashtag1", "#hashtag2", ..., "#hashtag9"],
              "sonidos_sugeridos": ["Nombre de sonidos o canciones en tendencia (sin enlaces)"],
              "recomendacion": "Consejo estratégico y personalizado para mejorar el rendimiento del video"
            }
            Instrucciones:
            - Los tres títulos deben ser distintos, llamativos y relacionados con los hashtags.
            - Las tres descripciones deben ser distintas, creativas, y motivar a ver o compartir el video.
            - La lista de hashtags debe tener exactamente 9, mezclando tendencias, específicos y amplios.
            - En 'sonidos_sugeridos', incluye solo nombres de canciones o sonidos virales en TikTok, y asegúrate de que sean relevantes o se asocien emocionalmente o temáticamente a los hashtags o el contenido (por ejemplo, si los hashtags son de motivación, usa sonidos épicos o inspiradores).
            - La recomendación debe ser variada, concreta y estratégica: evita repetir consejos genéricos como “usa subtítulos” o “hazlo corto”. Piensa en sugerencias que aporten valor real según la temática, como técnicas de storytelling, estilo visual, plano ideal, interacción emocional, edición, retos, duetos, efectos visuales, gancho inicial, etc.
            Responde solo con el JSON. No incluyas explicaciones adicionales ni texto fuera del objeto JSON.
            """),
                    new ChatRequestUserMessage(userMessage)
            );
            ChatCompletionsOptions options = new ChatCompletionsOptions(chatMessages);
            options.setModel(model);
            ChatCompletions completions = client.complete(options);
            String response = completions.getChoices().get(0).getMessage().getContent();
            return Map.of("response", response);
        } catch (Exception e) {
            return Map.of("error", "Error al procesar la solicitud: " + e.getMessage());
        }
    }
}
