package com.healthUnity.backend.Repositories;

import com.healthUnity.backend.Models.Citas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface CitasRepository extends JpaRepository<Citas, Long> {
    Citas findFirstByPaciente_IdPacienteAndFechaGreaterThanEqualOrderByFechaAscHoraAsc(Long idPaciente, LocalDate fecha);
}
