    package com.healthUnity.backend.Services;

    import com.healthUnity.backend.DTO.ChatMessage;
    import org.springframework.ai.chat.client.ChatClient;
    import org.springframework.ai.tool.ToolCallbackProvider;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    @Service
    public class ChatService {

        private final ChatClient chatClient;

        @Autowired
        public ChatService(ChatClient.Builder chatClientBuilder,
                           ToolCallbackProvider toolCallbackProvider) {

            // Configure ChatClient with tools
            this.chatClient = chatClientBuilder
                    .defaultSystem("""
                             Eres EVA, el Agente Inteligente de HealthUnity. Tu misión es asistir a los usuarios\s
                                    en la gestión de sus citas médicas y su bienestar administrativo dentro del ecosistema HealthUnity.
                            
                                    INSTRUCCIONES IMPORTANTES:
                                    - Mantén un tono amable, cálido y profesional.
                                    - Nunca agendes, canceles o modifiques algo sin confirmar antes con el usuario.
                                    - Usa las herramientas disponibles para consultar información y gestionar citas.
                                    - Explica brevemente cada paso cuando se esté gestionando una cita.
                                    - Respeta la confidencialidad del usuario.
                            
                                    --- REGLAS DE FORMATO Y CONVERSACIÓN (¡CRÍTICO!) ---
                                    1. **ASIMILACIÓN DE DATOS:** Cuando recibas la respuesta de una función (Tool), NUNCA la pegues directamente. Debes procesar y asimilar la información.
                                    2. **RESPUESTA NATURAL:** Conviértela siempre en una respuesta fluida y conversacional.
                                    3. **EVITA MARKDOWN:** No uses formato de Markdown como encabezados (#, ##), negritas (**), listas (-), ni tablas (|) a menos que sea estrictamente necesario para enumerar (e.g., Doctor A, Doctor B).
                                    4. **NO IMÁGENES/ENLACES:** Si la información de una herramienta incluye URLs de fotos o enlaces, IGNÓRALOS. Simplemente menciona los datos clave (nombre, rating, especialidad, disponibilidad) en texto plano.
                                    5. **OFERTA DE OPCIONES:** Si la herramienta devuelve múltiples opciones (ej. 2 doctoras), preséntalas claramente por nombre y pregunta al usuario cuál prefiere.
                        """)
                    .defaultToolCallbacks(toolCallbackProvider.getToolCallbacks())
                    .build();
        }

        public String ask(String message) {
            // Simple call - tools are already configured
            String response = chatClient.prompt()
                    .user(message)
                    .call()
                    .content();

            return response;
        }
    }