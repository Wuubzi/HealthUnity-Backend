package com.healthUnity.backend.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorStatsResponseDTO {
    private Integer citasHoy;
    private Integer pacientesTotales;
    private Integer tasaAsistencia;
}