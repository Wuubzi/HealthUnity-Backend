package com.healthUnity.backend.DTO.Response;

import com.healthUnity.backend.DTO.Request.HorarioDetalle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorarioDoctorResponseDTO {
    private Integer duracionCita;
    private Integer tiempoDescanso;
    private List<HorarioDetalle> horarios;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HorarioDetalle {
        private Long idHorarioDoctor;
        private int diaSemana;
        private String diaNombre;
        private LocalTime horaInicio;
        private LocalTime horaFin;
        private int duracionHoras;
        private int citasDisponibles;
    }
}

