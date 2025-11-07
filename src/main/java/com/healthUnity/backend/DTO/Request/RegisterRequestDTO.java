package com.healthUnity.backend.DTO.Request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @Email
    String gmail;
}
