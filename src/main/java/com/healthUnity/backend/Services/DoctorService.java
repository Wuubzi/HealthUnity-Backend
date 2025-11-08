package com.healthUnity.backend.Services;

import com.healthUnity.backend.DTO.Response.DiaHorarioDTO;
import com.healthUnity.backend.DTO.Response.HorarioResponseDTO;
import com.healthUnity.backend.DTO.Response.RangoHorarioDTO;
import com.healthUnity.backend.DTO.Response.TopDoctorsResponseDTO;
import com.healthUnity.backend.Models.Doctores;
import com.healthUnity.backend.Models.HorariosDoctor;
import com.healthUnity.backend.Models.OpinionesDoctores;
import com.healthUnity.backend.Repositories.DoctorRepository;
import com.healthUnity.backend.Repositories.HorariosDoctorRepository;
import com.healthUnity.backend.Repositories.Opinion_DoctoresRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final Opinion_DoctoresRepository opinionDoctoresRepository;
    private final HorariosDoctorRepository horariosDoctorRepository;



    @Autowired
    public DoctorService(DoctorRepository doctorRepository,
                         Opinion_DoctoresRepository opinionDoctoresRepository,
                         HorariosDoctorRepository horariosDoctorRepository) {
        this.doctorRepository = doctorRepository;
        this.opinionDoctoresRepository = opinionDoctoresRepository;
        this.horariosDoctorRepository = horariosDoctorRepository;
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
}
