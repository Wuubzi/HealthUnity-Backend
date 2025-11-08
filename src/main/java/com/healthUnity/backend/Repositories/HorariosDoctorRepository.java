package com.healthUnity.backend.Repositories;

import com.healthUnity.backend.Models.HorariosDoctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorariosDoctorRepository extends JpaRepository<HorariosDoctor, Long> {
    List<HorariosDoctor> findAllByDoctor_IdDoctor(Long idDoctor);
}
