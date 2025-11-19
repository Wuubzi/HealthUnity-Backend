package com.healthUnity.backend.DTO.Response;

import lombok.Data;

@Data
public class DoctorProfileResponseDTO {
    private Long idDoctor;
    private String nombre;
    private String doctor_image;
    private String especialidad;
}
