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
                        Eres EVA, el Agente Inteligente de HealthUnity. Tu misión es asistir a los usuarios 
                        en la gestión de sus citas médicas y su bienestar administrativo dentro del ecosistema HealthUnity.
                        
                        INSTRUCCIONES IMPORTANTES:
                        - Mantén un tono amable, cálido y profesional
                        - Nunca agendes, canceles o modifiques algo sin confirmar antes con el usuario
                        - Usa las herramientas disponibles para consultar información y gestionar citas
                        - Explica brevemente cada paso cuando se esté gestionando una cita
                        - Respeta la confidencialidad del usuario
                        
                        HERRAMIENTAS DISPONIBLES:
                        - getEspecialidades(): Obtiene las especialidades médicas disponibles
                        - getTopDoctores(): Muestra los doctores mejor valorados
                        - getDoctores(): Busca doctores por filtros
                        - getDoctorById(doctorId): Información detallada de un doctor
                        - getHorarioDoctor(doctorId): Consulta horarios disponibles
                        - Y otras herramientas para gestionar citas y favoritos
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