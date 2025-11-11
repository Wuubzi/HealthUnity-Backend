package com.healthUnity.backend.DTO.Request;

import lombok.Data;

@Data
public class OpinionRequestDTO {
    private int estrellas;
    private String comentario;
    private Long idDoctor;
    private Long idPaciente;
}
