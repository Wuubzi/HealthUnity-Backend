package com.healthUnity.backend.Services;

import com.healthUnity.backend.DTO.Request.CompleteProfileRequestDTO;
import com.healthUnity.backend.DTO.Response.CompleteProfileResponseDTO;
import com.healthUnity.backend.DTO.Response.ResponseDTO;
import com.healthUnity.backend.Repositories.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    @Autowired
    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

 public CompleteProfileResponseDTO completeProfile(CompleteProfileRequestDTO data) {

     CompleteProfileResponseDTO response = new CompleteProfileResponseDTO();
     return response;

 }


}
