package com.healthUnity.backend.Repositories;

import com.healthUnity.backend.Models.Doctores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctores, Long> {
}
