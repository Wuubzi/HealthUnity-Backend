package com.healthUnity.backend.Controllers;

import com.healthUnity.backend.Models.Citas;
import com.healthUnity.backend.Services.CitasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
