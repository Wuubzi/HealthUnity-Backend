package com.healthUnity.backend.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorarioStatsResponseDTO {
    private int horasSemanales;
    private int diasActivos;
    private int citasSemanales; // aproximadas
}