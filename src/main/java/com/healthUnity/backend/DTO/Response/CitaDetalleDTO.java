package com.healthUnity.backend.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaDetalleDTO {
    private Long idCita;
    private String nombrePaciente;
    private String imagenPaciente;
    private String contacto;
    private String email;
    private Integer edad;
    private LocalDate fecha;
    private LocalTime hora;
    private String tipo;
    private String estado;
}
