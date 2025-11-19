package com.healthUnity.backend.DTO.Request;

import lombok.Data;

import java.time.LocalTime;

@Data
public  class HorarioDetalle {
    private Long idHorarioDoctor;
    private int diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private boolean activo;
}
