package com.healthUnity.backend.Services;

import com.healthUnity.backend.DTO.FavoritoDoctorProjection;
import com.healthUnity.backend.DTO.Request.DoctorFavoritoRequestDTO;
import com.healthUnity.backend.DTO.Request.OpinionRequestDTO;
import com.healthUnity.backend.DTO.Response.*;
import com.healthUnity.backend.Models.*;
import com.healthUnity.backend.Repositories.*;
import com.healthUnity.backend.Utils.DateFormatter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final Opinion_DoctoresRepository opinionDoctoresRepository;
    private final HorariosDoctorRepository horariosDoctorRepository;
    private final PacienteRepository pacienteRepository;
    private final FavoritoDoctorRepository favoritoDoctorRepository;
    private final DateFormatter dateFormatter;



    @Autowired
    public DoctorService(DoctorRepository doctorRepository,
                         Opinion_DoctoresRepository opinionDoctoresRepository,
                         HorariosDoctorRepository horariosDoctorRepository,
                         PacienteRepository pacienteRepository,
                         FavoritoDoctorRepository favoritoDoctorRepository,
                         DateFormatter dateFormatter) {
        this.doctorRepository = doctorRepository;
        this.opinionDoctoresRepository = opinionDoctoresRepository;
        this.horariosDoctorRepository = horariosDoctorRepository;
        this.pacienteRepository = pacienteRepository;
        this.favoritoDoctorRepository = favoritoDoctorRepository;
        this.dateFormatter = dateFormatter;
    }

    public List<TopDoctorsResponseDTO> getTopDoctores() {
        List<Object[]> results = opinionDoctoresRepository.findTop5DoctoresConRating();
        return results.stream().map(r -> new TopDoctorsResponseDTO(
                ((Number) r[0]).longValue(),
                (String) r[1],
                (String) r[2],
                (String) r[3],
                (String) r[4],
                ((Number) r[5]).doubleValue(),
                ((Number) r[6]).intValue()
        )).collect(Collectors.toList());
    }

    public PaginatedDoctorResponse getDoctores(
            int page,
            int limit,
            String search,
            Long especialidadId,
            String orderBy
    ) {
        // Validar y establecer valor por defecto para orderBy
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "relevancia";
        }

        // Validar que el orderBy sea uno de los valores permitidos
        String finalOrderBy = orderBy;
        if (!finalOrderBy.equals("rating") &&
                !finalOrderBy.equals("reviews") &&
                !finalOrderBy.equals("relevancia")) {
            finalOrderBy = "relevancia";
        }

        // Crear el Pageable (sin Sort porque lo manejamos en SQL)
        Pageable pageable = PageRequest.of(page, limit);

        // Ejecutar la query correcta
        Page<Object[]> resultPage;

        if (search != null && !search.trim().isEmpty() && especialidadId != null) {
            resultPage = doctorRepository.findByNombreAndEspecialidad(
                    search.trim(), especialidadId, finalOrderBy, pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            resultPage = doctorRepository.findByNombreContaining(
                    search.trim(), finalOrderBy, pageable);
        } else if (especialidadId != null) {
            resultPage = doctorRepository.findByEspecialidadId(
                    especialidadId, finalOrderBy, pageable);
        } else {
            resultPage = doctorRepository.findAllDoctores(finalOrderBy, pageable);
        }

        // Convertir Object[] a DTO
        List<DoctorDTOResponse> doctoresDTO = resultPage.getContent()
                .stream()
                .map(DoctorDTOResponse::new)
                .collect(Collectors.toList());

        // Crear respuesta
        return new PaginatedDoctorResponse(
                doctoresDTO,
                page + 1, // Volver a base 1
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                resultPage.hasNext(),
                resultPage.hasPrevious(),
                limit
        );
    }


public Doctores getDoctorById(Long idDoctor) {
        Optional<Doctores> doctorOptional = doctorRepository.findById(idDoctor);
        if (doctorOptional.isEmpty()) {
            throw new EntityNotFoundException("Doctor no encontrado");
        }
        return doctorOptional.get();
    }

    public List<OpinionesDoctores> getOpinionDoctorById( Long idDoctor){
        return opinionDoctoresRepository.findAllByDoctor_IdDoctor(idDoctor);
    }

    public HorarioResponseDTO getHorarioDoctor(Long idDoctor){

        Optional<Doctores> doctorOptional = doctorRepository.findById(idDoctor);
        if (doctorOptional.isEmpty()) {
            throw new EntityNotFoundException("Doctor no encontrado");
        }

        List<HorariosDoctor> horariosDoctorList = horariosDoctorRepository.findAllByDoctor_IdDoctor(idDoctor);

        Map<Integer, List<RangoHorarioDTO>> agrupadoPorDia = horariosDoctorList.stream()
                .collect(Collectors.groupingBy(
                        HorariosDoctor::getDiaSemana,
                        Collectors.mapping(
                                h -> new RangoHorarioDTO(h.getHoraInicio(), h.getHoraFin()),
                                Collectors.toList()
                        )
                ));

        // Convertir al DTO final
        List<DiaHorarioDTO> dias = agrupadoPorDia.entrySet().stream()
                .map(entry -> new DiaHorarioDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new HorarioResponseDTO(idDoctor, dias);

    }

    public ResponseDTO añadirFavoritos(DoctorFavoritoRequestDTO data, HttpServletRequest request) {
        Optional<Doctores> doctorOptional = doctorRepository.findById(data.getIdDoctor());
        if (doctorOptional.isEmpty()) {
            throw new EntityNotFoundException("Doctor no encontrado");
        }
        Optional<Paciente> paciente = pacienteRepository.findById(data.getIdPaciente());
        if (paciente.isEmpty()) {
            throw new EntityNotFoundException("Paciente no encontrado");
        }
        Doctores doctor = doctorOptional.get();
        Paciente paciente1 = paciente.get();

        FavoritoDoctores favoritoDoctores = new FavoritoDoctores();
        favoritoDoctores.setDoctor(doctor);
        favoritoDoctores.setPaciente(paciente1);
        favoritoDoctorRepository.save(favoritoDoctores);
        return getResponseDTO(200, "Doctor agregado a favoritos", request);
    }

    public ResponseDTO eliminarFavoritos(Long id_favorito, HttpServletRequest request) {
        Optional<FavoritoDoctores> favoritoDoctorOptional = favoritoDoctorRepository.findById(id_favorito);
        if (favoritoDoctorOptional.isEmpty()) {
            throw new EntityNotFoundException("Favorito no encontrado");
        }
        favoritoDoctorRepository.delete(favoritoDoctorOptional.get());
        return getResponseDTO(200, "Doctor eliminado de favoritos", request);
    }

    public List<FavoritoDoctorResponseDTO> getDoctoresFavoritos(Long idPaciente) {

        // Aquí puedes omitir la verificación de Paciente si confías en que el ID es válido
        // o mantenerla si es estrictamente necesario.

        // 1. Obtener los datos usando la Projection
        List<FavoritoDoctorProjection> projections =
                favoritoDoctorRepository.findAllFavoritosDtoByPacienteId(idPaciente);

        // 2. Mapear la Projection al DTO final
        return projections.stream()
                .map(p -> {
                    FavoritoDoctorResponseDTO dto = new FavoritoDoctorResponseDTO();

                    dto.setIdFavorito(p.getIdFavorito());
                    dto.setIdDoctor(p.getIdDoctor());
                    dto.setNombre(p.getNombre());
                    dto.setApellido(p.getApellido());
                    dto.setDoctor_image(p.getDoctor_image());
                    dto.setEspecialidad(p.getEspecialidad());

                    // Mapeo de rating y reviews
                    dto.setRating(p.getRating());
                    dto.setNumber_reviews(p.getNumber_reviews() != null ? p.getNumber_reviews() : 0);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public ResponseDTO añadirOpinion(OpinionRequestDTO data, HttpServletRequest request) {
        Optional<Doctores> doctorOptional = doctorRepository.findById(data.getIdDoctor());
        if (doctorOptional.isEmpty()) {
            throw new EntityNotFoundException("Doctor no encontrado");
        }
        Optional<Paciente> pacienteOptional = pacienteRepository.findById(data.getIdPaciente());
        if (pacienteOptional.isEmpty()) {
            throw new EntityNotFoundException("Paciente no encontrado");
        }
        Doctores doctor = doctorOptional.get();
        Paciente paciente = pacienteOptional.get();

        OpinionesDoctores opinion = new OpinionesDoctores();
        opinion.setDoctor(doctor);
        opinion.setPaciente(paciente);
        opinion.setEstrellas(data.getEstrellas());
        opinion.setDetalles(data.getComentario());
        opinion.setFecha(LocalDate.now());

        opinionDoctoresRepository.save(opinion);
        return getResponseDTO(200, "Opinion guardada", request);
    }

    private ResponseDTO getResponseDTO(int status, String message, HttpServletRequest url) {
        ResponseDTO response = new ResponseDTO();
        response.setStatus(status);
        response.setMessage(message);
        response.setUrl(url.getRequestURL().toString());
        response.setTimestamp(dateFormatter.formatearFecha());
        return response;
    }
}
