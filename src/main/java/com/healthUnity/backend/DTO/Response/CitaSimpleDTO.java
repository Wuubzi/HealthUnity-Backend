package com.healthUnity.backend.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaSimpleDTO {
    private Long idCita;
    private String nombrePaciente;
    private String imagenPaciente;
    private LocalTime hora;
    private String tipo;
    private String estado;
}