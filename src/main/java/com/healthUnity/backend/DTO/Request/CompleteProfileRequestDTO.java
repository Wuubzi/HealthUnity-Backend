package com.healthUnity.backend.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompleteProfileRequestDTO {

    @NotBlank
    @Size(max = 20)
    private String nombre;

    @NotBlank
    @Size(max = 20)
    private String apellido;

    @NotBlank
    @Email
    @Size(max = 30)
    private String gmail;

    @NotNull
    @Past
    private String fecha_nacimiento;

    @NotBlank
    @Size(max = 10)
    private String genero;

    private String url_imagen;
}
