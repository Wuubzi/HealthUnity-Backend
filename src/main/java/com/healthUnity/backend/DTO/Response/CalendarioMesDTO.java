package com.healthUnity.backend.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarioMesDTO {
    private int mes; // 1-12
    private int anio;
    private String nombreMes; // "Enero", "Febrero", etc.
    private List<CitaDiaDTO> dias;
}