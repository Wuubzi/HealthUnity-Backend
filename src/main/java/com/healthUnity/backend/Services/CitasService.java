package com.healthUnity.backend.Services;

import com.healthUnity.backend.Models.Citas;
import com.healthUnity.backend.Models.Paciente;
import com.healthUnity.backend.Repositories.CitasRepository;
import com.healthUnity.backend.Repositories.PacienteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CitasService {
    private final CitasRepository citasRepository;
    private final PacienteRepository pacienteRepository;

    @Autowired
    public CitasService(CitasRepository citasRepository,
                        PacienteRepository pacienteRepository) {
        this.citasRepository = citasRepository;
        this.pacienteRepository = pacienteRepository;
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
}
