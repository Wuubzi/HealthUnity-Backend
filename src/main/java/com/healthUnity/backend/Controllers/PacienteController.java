package com.healthUnity.backend.Controllers;

import com.healthUnity.backend.DTO.Request.CompleteProfileRequestDTO;
import com.healthUnity.backend.DTO.Request.RegisterRequestDTO;
import com.healthUnity.backend.DTO.Response.CompleteProfileResponseDTO;
import com.healthUnity.backend.DTO.Response.PacienteResponseDTO;
import com.healthUnity.backend.DTO.Response.RegisterResponseDTO;
import com.healthUnity.backend.DTO.Response.ResponseDTO;
import com.healthUnity.backend.Services.PacienteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/paciente")
public class PacienteController {

    private final PacienteService pacienteService;

    @Autowired
    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> registerPaciente(@Valid @RequestBody RegisterRequestDTO data, HttpServletRequest request){
        return new ResponseEntity<>(pacienteService.registerPaciente(data, request), HttpStatus.OK);
    }

    @PostMapping("/complete-profile")
    public ResponseEntity<CompleteProfileResponseDTO> completeProfile(@Valid @RequestBody CompleteProfileRequestDTO data){
        return new ResponseEntity<>(pacienteService.completeProfile(data), HttpStatus.OK);
    }

    @GetMapping("/getPaciente")
    public ResponseEntity<PacienteResponseDTO> getPaciente(@RequestParam String gmail){
        return new ResponseEntity<>(pacienteService.getPaciente(gmail), HttpStatus.OK);
    }

    @PutMapping("/update-profile")
    public ResponseEntity<ResponseDTO> updateProfile(@Valid @RequestBody CompleteProfileRequestDTO data, HttpServletRequest request){
        return new ResponseEntity<>(pacienteService.updateProfile(data, request), HttpStatus.OK);
    }


}
