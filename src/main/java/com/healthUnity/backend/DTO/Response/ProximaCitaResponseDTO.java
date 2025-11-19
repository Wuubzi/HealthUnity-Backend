package com.healthUnity.backend.DTO.Response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProximaCitaResponseDTO {
    private Long idCita;
    private String nombrePaciente;
    private String imagenPaciente;
    private LocalDate fecha;
    private LocalTime hora;
    private String razon;
    private String estado;
}
