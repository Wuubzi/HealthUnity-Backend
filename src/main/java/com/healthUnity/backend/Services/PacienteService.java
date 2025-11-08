package com.healthUnity.backend.Services;

import com.healthUnity.backend.DTO.Request.CompleteProfileRequestDTO;
import com.healthUnity.backend.DTO.Request.RegisterRequestDTO;
import com.healthUnity.backend.DTO.Response.CompleteProfileResponseDTO;
import com.healthUnity.backend.DTO.Response.PacienteResponseDTO;
import com.healthUnity.backend.DTO.Response.RegisterResponseDTO;
import com.healthUnity.backend.DTO.Response.ResponseDTO;
import com.healthUnity.backend.Models.DetallesUsuario;
import com.healthUnity.backend.Models.Paciente;
import com.healthUnity.backend.Repositories.PacienteRepository;
import com.healthUnity.backend.Utils.DateFormatter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final DateFormatter dateFormatter;

    @Autowired
    public PacienteService(PacienteRepository pacienteRepository,
                           DateFormatter dateFormatter) {
        this.pacienteRepository = pacienteRepository;
        this.dateFormatter = dateFormatter;
    }

 public CompleteProfileResponseDTO completeProfile(CompleteProfileRequestDTO data) {
     Optional<Paciente> pacienteOptional = pacienteRepository.findPacienteByDetallesUsuario_Gmail(data.getGmail());
     if (pacienteOptional.isPresent()) {
         throw new RuntimeException("El paciente ya tiene un perfil completado");
     }

     Paciente paciente = getPaciente(data);
     pacienteRepository.save(paciente);

     CompleteProfileResponseDTO response = new CompleteProfileResponseDTO();
     response.setStatus(200);
     response.setMessage("Usuario registrado exitosamente");
     response.setUrl("/api/v1/paciente/complete-profile");
     response.setTimestamp(dateFormatter.formatearFecha());

     return response;
 }

    private static Paciente getPaciente(CompleteProfileRequestDTO data) {
        Paciente paciente = new Paciente();
        DetallesUsuario detallesUsuario = new DetallesUsuario();
        detallesUsuario.setNombre(data.getNombre());
        detallesUsuario.setApellido(data.getApellido());
        detallesUsuario.setGmail(data.getGmail());
        detallesUsuario.setDireccion(data.getDireccion());
        detallesUsuario.setTelefono(data.getTelefono());
        detallesUsuario.setFechaNacimiento(data.getFechaNacimiento());
        detallesUsuario.setGenero(data.getGenero());
        detallesUsuario.setUrlImagen(data.getUrl_imagen());
        paciente.setDetallesUsuario(detallesUsuario);
        return paciente;
    }

    public PacienteResponseDTO getPaciente(String gmail){
        Optional<Paciente> pacienteOptional = pacienteRepository.findPacienteByDetallesUsuario_Gmail(gmail);

         if (pacienteOptional.isEmpty()) {
             throw new EntityNotFoundException("Paciente no encontrado");
         }
            Paciente paciente = pacienteOptional.get();
            PacienteResponseDTO response = new PacienteResponseDTO();
            response.setId(paciente.getIdPaciente());
            response.setNombre(paciente.getDetallesUsuario().getNombre());
            response.setApellido(paciente.getDetallesUsuario().getApellido());
            response.setGmail(paciente.getDetallesUsuario().getGmail());
            response.setDireccion(paciente.getDetallesUsuario().getDireccion());
            response.setTelefono(paciente.getDetallesUsuario().getTelefono());
            response.setFechaNacimiento(paciente.getDetallesUsuario().getFechaNacimiento());
            response.setGenero(paciente.getDetallesUsuario().getGenero());
            response.setUrl_imagen(paciente.getDetallesUsuario().getUrlImagen());
            return response;
    }

    public ResponseDTO updateProfile(CompleteProfileRequestDTO data, HttpServletRequest request) {
        Paciente paciente = pacienteRepository.findPacienteByDetallesUsuario_Gmail(data.getGmail())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));

        // Actualizas los detalles del paciente existente
        DetallesUsuario detalles = paciente.getDetallesUsuario();
        detalles.setNombre(data.getNombre());
        detalles.setApellido(data.getApellido());
        detalles.setGmail(data.getGmail());
        detalles.setDireccion(data.getDireccion());
        detalles.setTelefono(data.getTelefono());
        detalles.setFechaNacimiento(data.getFechaNacimiento());
        detalles.setGenero(data.getGenero());
        detalles.setUrlImagen(data.getUrl_imagen());

        paciente.setDetallesUsuario(detalles);

        pacienteRepository.save(paciente);

        ResponseDTO response = new ResponseDTO();
        response.setStatus(200);
        response.setMessage("Usuario actualizado exitosamente");
        response.setUrl(request.getRequestURL().toString());
        response.setTimestamp(dateFormatter.formatearFecha());
        return response;
    }


    public RegisterResponseDTO registerPaciente(RegisterRequestDTO data, HttpServletRequest request){
     RegisterResponseDTO response = new RegisterResponseDTO();
     Optional<Paciente> pacienteOptional = pacienteRepository.findPacienteByDetallesUsuario_Gmail(data.getGmail());
     response.setUrl(request.getRequestURL().toString());
     response.setProfileCompleted(pacienteOptional.isPresent());
     response.setStatus(200);
     response.setTimestamp(dateFormatter.formatearFecha());
     return response;
 }


}
