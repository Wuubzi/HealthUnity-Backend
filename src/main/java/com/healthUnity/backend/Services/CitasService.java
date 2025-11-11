package com.healthUnity.backend.Services;

import com.healthUnity.backend.DTO.Request.CitasRequestDTO;
import com.healthUnity.backend.DTO.Response.CitaResponseDTO;
import com.healthUnity.backend.DTO.Response.ResponseDTO;
import com.healthUnity.backend.Models.Citas;
import com.healthUnity.backend.Models.Doctores;
import com.healthUnity.backend.Models.Paciente;
import com.healthUnity.backend.Repositories.CitasRepository;
import com.healthUnity.backend.Repositories.DoctorRepository;
import com.healthUnity.backend.Repositories.PacienteRepository;
import com.healthUnity.backend.Utils.DateFormatter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CitasService {
    private final CitasRepository citasRepository;
    private final DoctorRepository doctorRepository;
    private final PacienteRepository pacienteRepository;
    private final DateFormatter dateFormatter;

    @Autowired
    public CitasService(CitasRepository citasRepository,
                        PacienteRepository pacienteRepository,
                        DoctorRepository doctorRepository,
                        DateFormatter dateFormatter) {
        this.citasRepository = citasRepository;
        this.pacienteRepository = pacienteRepository;
        this.doctorRepository = doctorRepository;
        this.dateFormatter = dateFormatter;
    }


    public Citas getCitaProxima(Long idPaciente) {
       Optional<Paciente> pacienteOptional = pacienteRepository.findById(idPaciente);
       if (pacienteOptional.isEmpty()) {
           throw new EntityNotFoundException("Paciente no encontrado");
       }

        Citas cita = citasRepository.findFirstByPaciente_IdPacienteAndFechaGreaterThanEqualOrderByFechaAscHoraAsc(
                idPaciente,
                LocalDate.now()
        );
        System.out.println(cita);
        return cita;
    }

    public List<CitaResponseDTO> getCitas(Long idPaciente, String estado) {
        Paciente paciente = pacienteRepository.findById(idPaciente).orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
        List<Citas> citas = citasRepository.findByPacienteAndEstado(idPaciente,estado);
        return citas.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CitaResponseDTO convertToDTO(Citas cita) {
        CitaResponseDTO dto = new CitaResponseDTO();
        dto.setIdCita(cita.getIdCita());
        dto.setFecha(cita.getFecha());
        dto.setHora(cita.getHora());
        dto.setEstado(cita.getEstado());

        // Información del doctor
        if (cita.getDoctor() != null) {
            dto.setIdDoctor(cita.getDoctor().getIdDoctor());
            if (cita.getDoctor().getDetallesUsuario() != null) {
                dto.setNombre_doctor(cita.getDoctor().getDetallesUsuario().getNombre());
                dto.setDoctor_image(cita.getDoctor().getDetallesUsuario().getUrlImagen());
                dto.setApellido_doctor(cita.getDoctor().getDetallesUsuario().getApellido());
                dto.setDireccion(cita.getDoctor().getDetallesUsuario().getDireccion());
            }

            // Información de la especialidad
            if (cita.getDoctor().getEspecialidad() != null) {
                dto.setEspecialidad(cita.getDoctor().getEspecialidad().getNombre());
            }
        }

        return dto;
    }

    public ResponseDTO añadirCita(@RequestBody CitasRequestDTO cita, HttpServletRequest request){
        Optional<Paciente> pacienteOptional = pacienteRepository.findById(cita.getIdPaciente());
        if (pacienteOptional.isEmpty()) {
            throw new EntityNotFoundException("Paciente no encontrado");
        }
        Optional<Doctores> doctoresOptional = doctorRepository.findById(cita.getIdDoctor());
        if (doctoresOptional.isEmpty()) {
            throw new EntityNotFoundException("Doctor no encontrado");
        }

        Citas citas = new Citas();
        citas.setPaciente(pacienteOptional.get());
        citas.setDoctor(doctoresOptional.get());
        citas.setFecha(cita.getFecha());
        citas.setHora(cita.getHora());
        citas.setRazon(cita.getRazon());
        citasRepository.save(citas);

        return getResponseDTO(200, "Cita agregada exitosamente", request);
    }

    public ResponseDTO cancelarCita(Long idCita, HttpServletRequest request){
        Optional<Citas> citasOptional = citasRepository.findById(idCita);
        if (citasOptional.isEmpty()) {
            throw new EntityNotFoundException("Cita no encontrada");
        }
        Citas cita = citasOptional.get();
        cita.setEstado("cancelada");
        citasRepository.save(cita);
        return getResponseDTO(200, "Cita cancelada exitosamente", request);
    }

    public ResponseDTO actualizarCita(Long idCita, CitasRequestDTO data,HttpServletRequest request) {
        Optional<Citas> cita = citasRepository.findById(idCita);
        if (cita.isEmpty()) {
            throw new EntityNotFoundException("Cita no encontrada");
        }

        Optional<Paciente> pacienteOptional = pacienteRepository.findById(data.getIdPaciente());
        if (pacienteOptional.isEmpty()) {
            throw new EntityNotFoundException("Paciente no encontrado");
        }
        Optional<Doctores> doctoresOptional = doctorRepository.findById(data.getIdDoctor());
        if (doctoresOptional.isEmpty()) {
            throw new EntityNotFoundException("Doctor no encontrado");
        }
        Citas citaActualizada = cita.get();
        citaActualizada.setRazon(data.getRazon());
        citaActualizada.setHora(data.getHora());
        citaActualizada.setFecha(data.getFecha());
        citaActualizada.setDoctor(doctoresOptional.get());
        citaActualizada.setPaciente(pacienteOptional.get());
        citasRepository.save(citaActualizada);
        return getResponseDTO(200, "Cita actualizada exitosamente", request);
    }

    private ResponseDTO getResponseDTO(int status, String message, HttpServletRequest url) {
        ResponseDTO response = new ResponseDTO();
        response.setStatus(status);
        response.setMessage(message);
        response.setUrl(url.getRequestURL().toString());
        response.setTimestamp(dateFormatter.formatearFecha());
        return response;
    }
}

