package com.healthUnity.backend.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarioAnualDTO {
    private int anio;
    private List<CalendarioMesDTO> meses;
    private int totalCitasAnio;
}