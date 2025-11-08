package com.healthUnity.backend.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "doctores")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Doctores {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doctor")
    private Long idDoctor;

    private int experiencia;

    private String detalles;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_detalle_usuario")
    private DetallesUsuario detallesUsuario;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_especialidad")
    private Especialidades especialidad;

    @OneToOne
    @JoinColumn(name = "id_galeria")
    private Galeria galeria;


}
