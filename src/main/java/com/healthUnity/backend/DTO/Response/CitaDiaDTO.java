package com.healthUnity.backend.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaDiaDTO {
    private LocalDate fecha;
    private int totalCitas;
    private List<CitaSimpleDTO> citas;
}
