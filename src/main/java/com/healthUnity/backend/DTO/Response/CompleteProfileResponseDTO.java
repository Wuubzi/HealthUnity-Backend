package com.healthUnity.backend.DTO.Response;

import lombok.Data;

@Data
public class CompleteProfileResponseDTO {
    private String timestamp;
    private String message;
    private int Status;
    private String url;
    private boolean profileCompleted;
}
