package com.healthUnity.backend.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitasPageResponseDTO {
    private List<CitaDetalleDTO> citas;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int size;
}
