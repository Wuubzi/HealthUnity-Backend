package com.healthUnity.backend.Models;

import jakarta.persistence.*;

@Entity
@Table(name = "Detalles_usuario")
public class DetallesUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_usuario")
    private long idDetalleUsuario;

    @Column(name = "nombre")
    private String nombre;
    @Column
    private String apellido;
    @Column
    private String gmail;
    @Column(name = "fecha_nacimiento")
    private String fechaNacimiento;
    @Column
    private String genero;
    @Column(name = "url_imagen")
    private String urlImagen;
    @Column
    private String direccion;

    @OneToOne(mappedBy = "detallesUsuario")
    private Paciente paciente;
}
