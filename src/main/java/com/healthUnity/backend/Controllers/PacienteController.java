package com.healthUnity.backend.Controllers;

import com.healthUnity.backend.DTO.Request.CompleteProfileRequestDTO;
import com.healthUnity.backend.DTO.Response.CompleteProfileResponseDTO;
import com.healthUnity.backend.DTO.Response.ResponseDTO;
import com.healthUnity.backend.Services.PacienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/paciente")
public class PacienteController {

    private final PacienteService pacienteService;

    @Autowired
    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping("/complete-profile")
    public ResponseEntity<CompleteProfileResponseDTO> completeProfile(@Valid @RequestBody CompleteProfileRequestDTO data){
        return new ResponseEntity<>(pacienteService.completeProfile(data), HttpStatus.OK);
    }


}
