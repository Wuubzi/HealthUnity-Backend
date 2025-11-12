package com.healthUnity.backend.Controllers;

import com.healthUnity.backend.DTO.ChatMessage;
import com.healthUnity.backend.Models.Paciente;
import com.healthUnity.backend.Repositories.PacienteRepository;
import com.healthUnity.backend.Services.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/eva")
public class EvaController {

    private final PacienteRepository pacienteRepository;
    private final ChatService chatService;

    // Almacenamiento simple de conversaciones (en producción usa Redis o DB)
    private final Map<String, String> conversationHistory = new HashMap<>();

    @Autowired
    public EvaController(PacienteRepository pacienteRepository,
                         ChatService chatService) {
        this.pacienteRepository = pacienteRepository;
        this.chatService = chatService;
    }

    @PostMapping("/ask")
    public ResponseEntity<ChatMessage> ask(@RequestBody ChatMessage request) {
        try {
            // Buscar paciente por correo
            Optional<Paciente> pacienteOpt = pacienteRepository.findPacienteByDetallesUsuario_Gmail(request.getFrom());

            String contextoUsuario = "";
            Long idPaciente = null;
            String nombrePaciente = "usuario";

            if (pacienteOpt.isPresent()) {
                Paciente paciente = pacienteOpt.get();
                idPaciente = paciente.getIdPaciente();
                nombrePaciente = paciente.getDetallesUsuario() != null
                        ? paciente.getDetallesUsuario().getNombre()
                        : "usuario";

                contextoUsuario = String.format("""
                ## Información del usuario actual:
                - ID Paciente: %d
                - Nombre: %s
                - Email: %s
                """,
                        idPaciente,
                        nombrePaciente,
                        request.getFrom()
                );
            }

            // Fecha actual contextual
            LocalDate fechaActual = LocalDate.now();
            String fechaContexto = String.format("""
            ## Fecha actual: %s (%s)
            """,
                    fechaActual.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    fechaActual.getDayOfWeek().name()
            );

            // Recuperar historial previo del usuario
            String history = conversationHistory.getOrDefault(request.getFrom(), "");

            // Mantener el historial corto (últimos 1000 caracteres)
            if (history.length() > 1000) {
                history = history.substring(history.length() - 1000);
            }

            // Construir prompt con historial
            String promptCompleto = String.format("""
            Eres Eva, una asistente médica virtual amable y clara. 
            Siempre recuerdas lo que el usuario te ha dicho antes en esta conversación.

            %s
            %s

            ## Conversación anterior:
            %s

            Usuario: %s
            Eva:
            """,
                    contextoUsuario,
                    fechaContexto,
                    history,
                    request.getContent()
            );

            // Llamar al servicio de IA
            String response = chatService.ask(promptCompleto);

            // Actualizar historial
            String nuevoHistorial = history + "\nUsuario: " + request.getContent() + "\nEva: " + response;
            conversationHistory.put(request.getFrom(), nuevoHistorial);

            // Responder
            ChatMessage responseDTO = new ChatMessage();
            responseDTO.setContent(response);
            responseDTO.setFrom("eva");

            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            e.printStackTrace();
            ChatMessage errorResponse = new ChatMessage();
            errorResponse.setContent("Lo siento, ocurrió un error al procesar tu solicitud. Por favor intenta de nuevo.");
            errorResponse.setFrom("eva");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }


    @DeleteMapping("/conversation/{email}")
    public ResponseEntity<Void> clearConversation(@PathVariable String email) {
        conversationHistory.remove(email);
        return ResponseEntity.ok().build();
    }
}