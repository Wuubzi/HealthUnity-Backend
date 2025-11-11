package com.healthUnity.backend.Repositories;

import com.healthUnity.backend.Models.Citas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CitasRepository extends JpaRepository<Citas, Long> {
    Citas findFirstByPaciente_IdPacienteAndFechaGreaterThanEqualOrderByFechaAscHoraAsc(Long idPaciente, LocalDate fecha);
    @Query("""
        SELECT c FROM Citas c 
        JOIN FETCH c.doctor d
        JOIN FETCH d.detallesUsuario du
        JOIN FETCH d.especialidad e
        WHERE c.paciente.idPaciente = :idPaciente 
        AND LOWER(c.estado) = LOWER(:estado)
        ORDER BY c.fecha DESC, c.hora DESC
    """)
    List<Citas> findByPacienteAndEstado(
            @Param("idPaciente") Long idPaciente,
            @Param("estado") String estado);
}
