package com.healthUnity.backend.Controllers;

import com.healthUnity.backend.Models.Especialidades;
import com.healthUnity.backend.Services.EspecialidadesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/especialidades")
public class EspecialidadesController {

    private final EspecialidadesService especialidadesService;

    @Autowired
    public EspecialidadesController(EspecialidadesService especialidadesService) {
        this.especialidadesService = especialidadesService;
    }

    @GetMapping("/getEspecialidades")
    public List<Especialidades> getEspecialidades(){
        return especialidadesService.getEspecialidades();
    }
}
