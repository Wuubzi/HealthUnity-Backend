package com.healthUnity.backend.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HorarioResponseDTO {
    private Long idDoctor;
    private List<DiaHorarioDTO> dias;

}
