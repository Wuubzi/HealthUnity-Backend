package com.healthUnity.backend.Repositories;

import com.healthUnity.backend.Models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente>  findPacienteByDetallesUsuario_Gmail(String gmail);
}
