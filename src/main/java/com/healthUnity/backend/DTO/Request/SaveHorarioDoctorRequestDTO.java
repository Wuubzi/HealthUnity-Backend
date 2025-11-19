package com.healthUnity.backend.DTO.Request;


import lombok.Data;

import java.util.List;

@Data
public class SaveHorarioDoctorRequestDTO {
    private Long idDoctor;
    private Integer duracionCita;
    private Integer tiempoDescanso;
    private List<HorarioDetalle> horarios;
}
