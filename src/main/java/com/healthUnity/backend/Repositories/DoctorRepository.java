package com.healthUnity.backend.Repositories;

import com.healthUnity.backend.Models.Doctores;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface DoctorRepository extends JpaRepository<Doctores, Long> {

    /**
     * QUERY BASE: Todos los doctores
     */
    @Query(value = """
        SELECT 
            d.id_doctor,
            du.nombre,
            du.apellido,
            du.url_imagen,
            e.nombre,
            COALESCE(AVG(o.estrellas), 0.0) as rating,
            COALESCE(COUNT(o.id_opinion_doctor), 0) as reviews
        FROM doctores d
        LEFT JOIN opiniones_doctores o ON o.id_doctor = d.id_doctor
        JOIN detalles_usuario du ON d.id_detalle_usuario = du.id_detalle_usuario
        JOIN especialidades e ON d.id_especialidad = e.id_especialidad
        GROUP BY d.id_doctor, du.nombre, du.apellido, du.url_imagen, e.nombre, d.experiencia
        ORDER BY 
            CASE WHEN :orderBy = 'rating' THEN COALESCE(AVG(o.estrellas), 0.0) END DESC,
            CASE WHEN :orderBy = 'reviews' THEN COALESCE(COUNT(o.id_opinion_doctor), 0) END DESC,
            CASE WHEN :orderBy = 'relevancia' THEN d.experiencia END DESC
        """,
            countQuery = """
        SELECT COUNT(DISTINCT d.id_doctor)
        FROM doctores d
        """,
            nativeQuery = true)
    Page<Object[]> findAllDoctores(@Param("orderBy") String orderBy, Pageable pageable);

    /**
     * BUSCAR POR NOMBRE
     */
    @Query(value = """
        SELECT 
            d.id_doctor,
            du.nombre,
            du.apellido,
            du.url_imagen,
            e.nombre,
            COALESCE(AVG(o.estrellas), 0.0) as rating,
            COALESCE(COUNT(o.id_opinion_doctor), 0) as reviews
        FROM doctores d
        LEFT JOIN opiniones_doctores o ON o.id_doctor = d.id_doctor
        JOIN detalles_usuario du ON d.id_detalle_usuario = du.id_detalle_usuario
        JOIN especialidades e ON d.id_especialidad = e.id_especialidad
        WHERE 
            LOWER(du.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(du.apellido) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(CONCAT(du.nombre, ' ', du.apellido)) LIKE LOWER(CONCAT('%', :search, '%'))
        GROUP BY d.id_doctor, du.nombre, du.apellido, du.url_imagen, e.nombre, d.experiencia
        ORDER BY 
            CASE WHEN :orderBy = 'rating' THEN COALESCE(AVG(o.estrellas), 0.0) END DESC,
            CASE WHEN :orderBy = 'reviews' THEN COALESCE(COUNT(o.id_opinion_doctor), 0) END DESC,
            CASE WHEN :orderBy = 'relevancia' THEN d.experiencia END DESC
        """,
            countQuery = """
        SELECT COUNT(DISTINCT d.id_doctor)
        FROM doctores d
        JOIN detalles_usuario du ON d.id_detalle_usuario = du.id_detalle_usuario
        WHERE 
            LOWER(du.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(du.apellido) LIKE LOWER(CONCAT('%', :search, '%'))
        """,
            nativeQuery = true)
    Page<Object[]> findByNombreContaining(
            @Param("search") String search,
            @Param("orderBy") String orderBy,
            Pageable pageable);

    /**
     * FILTRAR POR ESPECIALIDAD
     */
    @Query(value = """
        SELECT 
            d.id_doctor,
            du.nombre,
            du.apellido,
            du.url_imagen,
            e.nombre,
            COALESCE(AVG(o.estrellas), 0.0) as rating,
            COALESCE(COUNT(o.id_opinion_doctor), 0) as reviews
        FROM doctores d
        LEFT JOIN opiniones_doctores o ON o.id_doctor = d.id_doctor
        JOIN detalles_usuario du ON d.id_detalle_usuario = du.id_detalle_usuario
        JOIN especialidades e ON d.id_especialidad = e.id_especialidad
        WHERE e.id_especialidad = :especialidadId
        GROUP BY d.id_doctor, du.nombre, du.apellido, du.url_imagen, e.nombre, d.experiencia
        ORDER BY 
            CASE WHEN :orderBy = 'rating' THEN COALESCE(AVG(o.estrellas), 0.0) END DESC,
            CASE WHEN :orderBy = 'reviews' THEN COALESCE(COUNT(o.id_opinion_doctor), 0) END DESC,
            CASE WHEN :orderBy = 'relevancia' THEN d.experiencia END DESC
        """,
            countQuery = """
        SELECT COUNT(DISTINCT d.id_doctor)
        FROM doctores d
        WHERE d.id_especialidad = :especialidadId
        """,
            nativeQuery = true)
    Page<Object[]> findByEspecialidadId(
            @Param("especialidadId") Long especialidadId,
            @Param("orderBy") String orderBy,
            Pageable pageable);

    /**
     * BUSCAR Y FILTRAR (Nombre + Especialidad)
     */
    @Query(value = """
        SELECT 
            d.id_doctor,
            du.nombre,
            du.apellido,
            du.url_imagen,
            e.nombre,
            COALESCE(AVG(o.estrellas), 0.0) as rating,
            COALESCE(COUNT(o.id_opinion_doctor), 0) as reviews
        FROM doctores d
        LEFT JOIN opiniones_doctores o ON o.id_doctor = d.id_doctor
        JOIN detalles_usuario du ON d.id_detalle_usuario = du.id_detalle_usuario
        JOIN especialidades e ON d.id_especialidad = e.id_especialidad
        WHERE 
            (LOWER(du.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR
             LOWER(du.apellido) LIKE LOWER(CONCAT('%', :search, '%')) OR
             LOWER(CONCAT(du.nombre, ' ', du.apellido)) LIKE LOWER(CONCAT('%', :search, '%')))
            AND e.id_especialidad = :especialidadId
        GROUP BY d.id_doctor, du.nombre, du.apellido, du.url_imagen, e.nombre, d.experiencia
        ORDER BY 
            CASE WHEN :orderBy = 'rating' THEN COALESCE(AVG(o.estrellas), 0.0) END DESC,
            CASE WHEN :orderBy = 'reviews' THEN COALESCE(COUNT(o.id_opinion_doctor), 0) END DESC,
            CASE WHEN :orderBy = 'relevancia' THEN d.experiencia END DESC
        """,
            countQuery = """
        SELECT COUNT(DISTINCT d.id_doctor)
        FROM doctores d
        JOIN detalles_usuario du ON d.id_detalle_usuario = du.id_detalle_usuario
        WHERE 
            (LOWER(du.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR
             LOWER(du.apellido) LIKE LOWER(CONCAT('%', :search, '%')))
            AND d.id_especialidad = :especialidadId
        """,
            nativeQuery = true)
    Page<Object[]> findByNombreAndEspecialidad(
            @Param("search") String search,
            @Param("especialidadId") Long especialidadId,
            @Param("orderBy") String orderBy,
            Pageable pageable
    );
}
