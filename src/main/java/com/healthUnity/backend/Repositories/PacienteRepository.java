package com.healthUnity.backend.Repositories;

import com.healthUnity.backend.Models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente>  findPacienteByDetallesUsuario_Gmail(String gmail);
}
