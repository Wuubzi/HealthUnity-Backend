package com.healthUnity.backend.Services;

import com.healthUnity.backend.Models.Especialidades;
import com.healthUnity.backend.Repositories.EspecialidadesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EspecialidadesService {

    private final EspecialidadesRepository especialidadesRepository;

    @Autowired
    public EspecialidadesService(EspecialidadesRepository especialidadesRepository) {
        this.especialidadesRepository = especialidadesRepository;
    }

    public List<Especialidades> getEspecialidades(){
        return especialidadesRepository.findAll();
    }


}
