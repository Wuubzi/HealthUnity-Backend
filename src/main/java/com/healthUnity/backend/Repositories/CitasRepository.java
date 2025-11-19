// Updated CitasRepository with new queries
package com.healthUnity.backend.Repositories;

import com.healthUnity.backend.Models.Citas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CitasRepository extends JpaRepository<Citas, Long> {
    // En el Repository
    Citas findFirstByPaciente_IdPacienteAndEstadoAndFechaGreaterThanEqualOrderByFechaAscHoraAsc(
            Long idPaciente,
            String estado,
            LocalDate fecha
    );
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

    // Queries para estadísticas
    @Query("SELECT COUNT(c) FROM Citas c WHERE c.doctor.idDoctor = :idDoctor AND c.fecha = :fecha")
    Long countCitasByDoctorAndFecha(@Param("idDoctor") Long idDoctor, @Param("fecha") LocalDate fecha);

    @Query("SELECT COUNT(DISTINCT c.paciente.idPaciente) FROM Citas c WHERE c.doctor.idDoctor = :idDoctor")
    Long countDistinctPacientesByDoctor(@Param("idDoctor") Long idDoctor);

    @Query("""
        SELECT COUNT(c) FROM Citas c 
        WHERE c.doctor.idDoctor = :idDoctor 
        AND LOWER(c.estado) = 'completada'
        AND c.fecha BETWEEN :startDate AND :endDate
    """)
    Long countCompletedCitasByDoctorAndDateRange(
            @Param("idDoctor") Long idDoctor,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT COUNT(c) FROM Citas c 
        WHERE c.doctor.idDoctor = :idDoctor 
        AND c.fecha BETWEEN :startDate AND :endDate
    """)
    Long countTotalCitasByDoctorAndDateRange(
            @Param("idDoctor") Long idDoctor,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT c FROM Citas c 
        JOIN FETCH c.paciente p
        JOIN FETCH p.detallesUsuario du
        WHERE c.doctor.idDoctor = :idDoctor 
        AND (c.fecha > :currentDate 
             OR (c.fecha = :currentDate AND c.hora >= :currentTime))
        AND LOWER(c.estado) IN ('pendiente', 'confirmada')
        ORDER BY c.fecha ASC, c.hora ASC
    """)
    List<Citas> findProximasCitasByDoctor(
            @Param("idDoctor") Long idDoctor,
            @Param("currentDate") LocalDate currentDate,
            @Param("currentTime") LocalTime currentTime);

    @Query("""
    SELECT COUNT(DISTINCT c.paciente.idPaciente) FROM Citas c 
    WHERE c.doctor.idDoctor = :idDoctor 
    AND LOWER(c.estado) = 'completada'
""")
    Long countDistinctPacientesByDoctorAndCompleted(@Param("idDoctor") Long idDoctor);


    @Query("""
        SELECT c FROM Citas c 
        JOIN FETCH c.paciente p
        JOIN FETCH p.detallesUsuario du
        WHERE c.doctor.idDoctor = :idDoctor 
        ORDER BY c.fecha DESC, c.hora DESC
    """)
    Page<Citas> findCitasByDoctorPaginated(
            @Param("idDoctor") Long idDoctor,
            Pageable pageable);

    // Método para filtrar por estado
    @Query("""
        SELECT c FROM Citas c 
        JOIN FETCH c.paciente p
        JOIN FETCH p.detallesUsuario du
        WHERE c.doctor.idDoctor = :idDoctor 
        AND LOWER(c.estado) = LOWER(:estado)
        ORDER BY c.fecha DESC, c.hora DESC
    """)
    Page<Citas> findCitasByDoctorAndEstadoPaginated(
            @Param("idDoctor") Long idDoctor,
            @Param("estado") String estado,
            Pageable pageable);

    @Query("""
        SELECT c FROM Citas c 
        JOIN FETCH c.paciente p
        JOIN FETCH p.detallesUsuario du
        WHERE c.doctor.idDoctor = :idDoctor 
        AND YEAR(c.fecha) = :anio
        ORDER BY c.fecha ASC, c.hora ASC
    """)
    List<Citas> findCitasByDoctorAndYear(
            @Param("idDoctor") Long idDoctor,
            @Param("anio") int anio);

    // Método alternativo para obtener citas entre fechas
    @Query("""
        SELECT c FROM Citas c 
        JOIN FETCH c.paciente p
        JOIN FETCH p.detallesUsuario du
        WHERE c.doctor.idDoctor = :idDoctor 
        AND c.fecha BETWEEN :fechaInicio AND :fechaFin
        ORDER BY c.fecha ASC, c.hora ASC
    """)
    List<Citas> findCitasByDoctorAndDateRange(
            @Param("idDoctor") Long idDoctor,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

}