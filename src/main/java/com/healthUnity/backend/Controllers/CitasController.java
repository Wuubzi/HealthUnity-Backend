package com.healthUnity.backend.Controllers;

import com.healthUnity.backend.DTO.Request.CitasRequestDTO;
import com.healthUnity.backend.DTO.Response.CitaResponseDTO;
import com.healthUnity.backend.DTO.Response.ResponseDTO;
import com.healthUnity.backend.Models.Citas;
import com.healthUnity.backend.Services.CitasService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/citas")
public class CitasController {

    private final CitasService citasService;

    @Autowired
    public CitasController(CitasService citasService) {
        this.citasService = citasService;
    }

    @GetMapping("/proxima-cita")
   public Citas getCitaProxima(@RequestParam Long idPaciente){
       return citasService.getCitaProxima(idPaciente);
   }

   @GetMapping("/getCitas")
    public List<CitaResponseDTO> getCitas(@RequestParam Long idPaciente, @RequestParam(defaultValue = "pendiente") String estado ) {
        return citasService.getCitas(idPaciente, estado);
   }

   @PostMapping("/añadirCitas")
    public ResponseEntity<ResponseDTO> añadirCitas(@Valid @RequestBody CitasRequestDTO cita, HttpServletRequest request){
        return new ResponseEntity<>(citasService.añadirCita(cita, request), HttpStatus.OK);
   }

   @PutMapping("/editarCitas")
   public ResponseEntity<ResponseDTO> editarCita(@RequestParam Long idCita, @Valid @RequestBody CitasRequestDTO data, HttpServletRequest request){
        return new ResponseEntity<>(citasService.actualizarCita(idCita, data,request), HttpStatus.OK);
   }

   @PutMapping("/cancelarCitas")
    public ResponseEntity<ResponseDTO> cancelarCita(@RequestParam Long idCita, HttpServletRequest request){
        return new ResponseEntity<>(citasService.cancelarCita(idCita, request), HttpStatus.OK);
    }

    @PutMapping("/completarCitas")
    public ResponseEntity<ResponseDTO> completarCita(@RequestParam Long idCita, HttpServletRequest request) {
        return new ResponseEntity<>(citasService.completarCita(idCita,request), HttpStatus.OK);
    }
}
