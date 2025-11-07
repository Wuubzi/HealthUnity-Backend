package com.healthUnity.backend.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "pacientes")
@Data
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paciente")
    private Long  idPaciente;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_detalle_usuario")
    private DetallesUsuario detallesUsuario;
}
