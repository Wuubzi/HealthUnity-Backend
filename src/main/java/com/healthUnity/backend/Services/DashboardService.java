package com.healthUnity.backend.Services;

import com.healthUnity.backend.DTO.Request.HorarioDetalle;
import com.healthUnity.backend.DTO.Request.SaveHorarioDoctorRequestDTO;
import com.healthUnity.backend.DTO.Response.*;
import com.healthUnity.backend.Models.Citas;
import com.healthUnity.backend.Models.DetallesUsuario;
import com.healthUnity.backend.Models.Doctores;
import com.healthUnity.backend.Models.HorariosDoctor;
import com.healthUnity.backend.Repositories.CitasRepository;
import com.healthUnity.backend.Repositories.DoctorRepository;
import com.healthUnity.backend.Repositories.HorariosDoctorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    private final DoctorRepository doctorRepository;
    private final HorariosDoctorRepository horariosDoctorRepository;
    private final CitasRepository citasRepository;

    public DashboardService(DoctorRepository doctorRepository,
                            CitasRepository citasRepository,
                            HorariosDoctorRepository horariosDoctorRepository) {
        this.doctorRepository = doctorRepository;
        this.citasRepository = citasRepository;
        this.horariosDoctorRepository = horariosDoctorRepository;
    }

    private static final Map<Integer, String> DIAS_SEMANA = new HashMap<>();
    static {
        DIAS_SEMANA.put(1, "Lunes");
        DIAS_SEMANA.put(2, "Martes");
        DIAS_SEMANA.put(3, "Miércoles");
        DIAS_SEMANA.put(4, "Jueves");
        DIAS_SEMANA.put(5, "Viernes");
        DIAS_SEMANA.put(6, "Sábado");
        DIAS_SEMANA.put(7, "Domingo");
    }

    public DoctorProfileResponseDTO getDoctorProfile(String gmailDoctor) {
        Optional<Doctores> doctoresOptional = doctorRepository.findByDetallesUsuario_Gmail(gmailDoctor);
        if (doctoresOptional.isEmpty()) {
            throw new EntityNotFoundException("Doctor no encontrado");
        }
        Doctores doctor = doctoresOptional.get();
        String nombre = doctor.getDetallesUsuario().getNombre() + " " + doctor.getDetallesUsuario().getApellido();

        DoctorProfileResponseDTO doctorResponse = new DoctorProfileResponseDTO();
        doctorResponse.setIdDoctor(doctor.getIdDoctor());
        doctorResponse.setNombre(nombre);
        doctorResponse.setEspecialidad(doctor.getEspecialidad().getNombre());
        doctorResponse.setDoctor_image(doctor.getDetallesUsuario().getUrlImagen());
        return doctorResponse;
    }

    public DoctorStatsResponseDTO getDoctorStats(Long idDoctor) {
        if (!doctorRepository.existsById(idDoctor)) {
            throw new EntityNotFoundException("Doctor no encontrado");
        }

        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);

        Long citasHoy = citasRepository.countCitasByDoctorAndFecha(idDoctor, today);


        Long pacientesTotales = citasRepository.countDistinctPacientesByDoctorAndCompleted(idDoctor);


        Long completedCitas = citasRepository.countCompletedCitasByDoctorAndDateRange(
                idDoctor, thirtyDaysAgo, today);
        Long totalCitas = citasRepository.countTotalCitasByDoctorAndDateRange(
                idDoctor, thirtyDaysAgo, today);

        int tasaAsistencia = 0;
        if (totalCitas > 0) {
            tasaAsistencia = (int) Math.round((completedCitas.doubleValue() / totalCitas.doubleValue()) * 100);
        }

        return new DoctorStatsResponseDTO(
                citasHoy.intValue(),
                pacientesTotales.intValue(),
                tasaAsistencia
        );
    }

    public List<ProximaCitaResponseDTO> getProximasCitas(Long idDoctor) {
        if (!doctorRepository.existsById(idDoctor)) {
            throw new EntityNotFoundException("Doctor no encontrado");
        }

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        List<Citas> citas = citasRepository.findProximasCitasByDoctor(idDoctor, currentDate, currentTime);

        // Limitar a 4 citas
        return citas.stream()
                .limit(4)
                .map(cita -> {
                    String nombrePaciente = cita.getPaciente().getDetallesUsuario().getNombre() +
                            " " + cita.getPaciente().getDetallesUsuario().getApellido();

                    return new ProximaCitaResponseDTO(
                            cita.getIdCita(),
                            nombrePaciente,
                            cita.getPaciente().getDetallesUsuario().getUrlImagen(),
                            cita.getFecha(),
                            cita.getHora(),
                            cita.getRazon(),
                            cita.getEstado()
                    );
                })
                .collect(   Collectors.toList());
    }

    public CitasPageResponseDTO getCitasPaginadas(Long idDoctor, int page, int size, String estado) {
        // Verificar que el doctor existe
        if (!doctorRepository.existsById(idDoctor)) {
            throw new EntityNotFoundException("Doctor no encontrado");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Citas> citasPage;

        // Filtrar por estado si se proporciona
        if (estado != null && !estado.trim().isEmpty()) {
            citasPage = citasRepository.findCitasByDoctorAndEstadoPaginated(idDoctor, estado, pageable);
        } else {
            citasPage = citasRepository.findCitasByDoctorPaginated(idDoctor, pageable);
        }

        // Convertir a DTO con toda la información
        List<CitaDetalleDTO> citasDTO = citasPage.getContent().stream()
                .map(cita -> {
                    DetallesUsuario detalles = cita.getPaciente().getDetallesUsuario();
                    String nombreCompleto = detalles.getNombre() + " " + detalles.getApellido();

                    // Calcular edad si existe fecha de nacimiento
                    Integer edad = null;
                    if (detalles.getFechaNacimiento() != null) {
                        // Convertir Date a LocalDate
                        LocalDate fechaNac = detalles.getFechaNacimiento().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                        edad = Period.between(fechaNac, LocalDate.now()).getYears();
                    }

                    return new CitaDetalleDTO(
                            cita.getIdCita(),
                            nombreCompleto,
                            detalles.getUrlImagen(),
                            detalles.getTelefono(),
                            detalles.getGmail(),
                            edad,
                            cita.getFecha(),
                            cita.getHora(),
                            cita.getRazon(),
                            cita.getEstado()
                    );
                })
                .collect(Collectors.toList());

        return new CitasPageResponseDTO(
                citasDTO,
                citasPage.getNumber(),
                citasPage.getTotalPages(),
                citasPage.getTotalElements(),
                citasPage.getSize()
        );
    }

    public CalendarioAnualDTO getCalendarioCitas(Long idDoctor) {
        // Verificar que el doctor existe
        if (!doctorRepository.existsById(idDoctor)) {
            throw new EntityNotFoundException("Doctor no encontrado");
        }

        int anioActual = Year.now().getValue();

        // Obtener todas las citas del año actual
        LocalDate inicioAnio = LocalDate.of(anioActual, 1, 1);
        LocalDate finAnio = LocalDate.of(anioActual, 12, 31);

        List<Citas> citasAnio = citasRepository.findCitasByDoctorAndDateRange(
                idDoctor, inicioAnio, finAnio);

        // Agrupar citas por mes y día
        Map<Integer, Map<LocalDate, List<Citas>>> citasPorMesYDia = citasAnio.stream()
                .collect(Collectors.groupingBy(
                        cita -> cita.getFecha().getMonthValue(),
                        Collectors.groupingBy(Citas::getFecha)
                ));

        // Crear la estructura del calendario
        List<CalendarioMesDTO> meses = new ArrayList<>();
        String[] nombresMeses = {
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };

        for (int mes = 1; mes <= 12; mes++) {
            List<CitaDiaDTO> diasConCitas = new ArrayList<>();
            Map<LocalDate, List<Citas>> citasDelMes = citasPorMesYDia.getOrDefault(mes, new HashMap<>());

            // Procesar cada día con citas en el mes
            for (Map.Entry<LocalDate, List<Citas>> entry : citasDelMes.entrySet()) {
                LocalDate fecha = entry.getKey();
                List<Citas> citasDelDia = entry.getValue();

                // Convertir citas a DTO simple
                List<CitaSimpleDTO> citasSimples = citasDelDia.stream()
                        .map(cita -> {
                            DetallesUsuario detalles = cita.getPaciente().getDetallesUsuario();
                            String nombreCompleto = detalles.getNombre() + " " + detalles.getApellido();

                            return new CitaSimpleDTO(
                                    cita.getIdCita(),
                                    nombreCompleto,
                                    detalles.getUrlImagen(),
                                    cita.getHora(),
                                    cita.getRazon(),
                                    cita.getEstado()
                            );
                        })
                        .collect(Collectors.toList());

                diasConCitas.add(new CitaDiaDTO(fecha, citasSimples.size(), citasSimples));
            }

            // Ordenar días por fecha
            diasConCitas.sort(Comparator.comparing(CitaDiaDTO::getFecha));

            CalendarioMesDTO mesDTO = new CalendarioMesDTO(
                    mes,
                    anioActual,
                    nombresMeses[mes - 1],
                    diasConCitas
            );

            meses.add(mesDTO);
        }

        return new CalendarioAnualDTO(
                anioActual,
                meses,
                citasAnio.size()
        );
    }

    public HorarioStatsResponseDTO getHorarioStats(Long idDoctor) {
        List<HorariosDoctor> horarios = horariosDoctorRepository.findAllByDoctor_IdDoctor(idDoctor);

        // Calcular horas semanales
        int horasSemanales = 0;
        int diasActivos = horarios.size();

        for (HorariosDoctor horario : horarios) {
            Duration duration = Duration.between(horario.getHoraInicio(), horario.getHoraFin());
            horasSemanales += (int) duration.toHours();
        }


        int citasSemanales = (int) Math.floor((horasSemanales * 60.0) / 45.0);

        return new HorarioStatsResponseDTO(horasSemanales, diasActivos, citasSemanales);
    }

    public HorarioDoctorResponseDTO getHorarioDoctor(Long idDoctor) {
        List<HorariosDoctor> horarios = horariosDoctorRepository.findAllByDoctor_IdDoctor(idDoctor);


        Integer duracionCita = 30;
        Integer tiempoDescanso = 15;

        List<HorarioDoctorResponseDTO.HorarioDetalle> horariosDetalle = horarios.stream()
                .map(h -> {
                    Duration duration = Duration.between(h.getHoraInicio(), h.getHoraFin());
                    int duracionHoras = (int) duration.toHours();


                    int minutosDisponibles = (int) duration.toMinutes();
                    int citasDisponibles = minutosDisponibles / (duracionCita + tiempoDescanso);

                    return new HorarioDoctorResponseDTO.HorarioDetalle(
                            h.getIdHorarioDoctor(),
                            h.getDiaSemana(),
                            DIAS_SEMANA.get(h.getDiaSemana()),
                            h.getHoraInicio(),
                            h.getHoraFin(),
                            duracionHoras,
                            citasDisponibles
                    );
                })
                .collect(Collectors.toList());

        return new HorarioDoctorResponseDTO(duracionCita, tiempoDescanso, horariosDetalle);
    }

    @Transactional
    public HorarioDoctorResponseDTO saveHorarioDoctor(SaveHorarioDoctorRequestDTO request) {
        Doctores doctor = doctorRepository.findById(request.getIdDoctor())
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));


        List<HorariosDoctor> horariosExistentes = horariosDoctorRepository
                .findAllByDoctor_IdDoctor(request.getIdDoctor());


        Map<Long, HorariosDoctor> horariosMap = horariosExistentes.stream()
                .collect(Collectors.toMap(HorariosDoctor::getIdHorarioDoctor, h -> h));

        List<HorariosDoctor> horariosAGuardar = new ArrayList<>();
        List<Long> idsRecibidos = new ArrayList<>();


        for (HorarioDetalle detalle : request.getHorarios()) {
            if (!detalle.isActivo()) {
                continue;
            }

            HorariosDoctor horario;

            if (detalle.getIdHorarioDoctor() != null) {

                horario = horariosMap.get(detalle.getIdHorarioDoctor());
                if (horario == null) {
                    throw new RuntimeException("Horario no encontrado: " + detalle.getIdHorarioDoctor());
                }
                idsRecibidos.add(detalle.getIdHorarioDoctor());
            } else {

                horario = new HorariosDoctor();
                horario.setDoctor(doctor);
            }

            horario.setDiaSemana(detalle.getDiaSemana());
            horario.setHoraInicio(detalle.getHoraInicio());
            horario.setHoraFin(detalle.getHoraFin());

            horariosAGuardar.add(horario);
        }


        List<HorariosDoctor> horariosAEliminar = horariosExistentes.stream()
                .filter(h -> !idsRecibidos.contains(h.getIdHorarioDoctor()))
                .collect(Collectors.toList());

        if (!horariosAEliminar.isEmpty()) {
            horariosDoctorRepository.deleteAll(horariosAEliminar);
        }


        horariosDoctorRepository.saveAll(horariosAGuardar);


        return getHorarioDoctor(request.getIdDoctor());
    }

}
