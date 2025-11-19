package com.healthUnity.backend.Controllers;

import com.healthUnity.backend.DTO.Request.SaveHorarioDoctorRequestDTO;
import com.healthUnity.backend.DTO.Response.*;
import com.healthUnity.backend.Services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/getDoctorProfile")
    public ResponseEntity<DoctorProfileResponseDTO> getDoctorProfile(@RequestParam String gmailDoctor){
        return new ResponseEntity<>(dashboardService.getDoctorProfile(gmailDoctor), HttpStatus.OK);
    }

    @GetMapping("/getStats")
    public ResponseEntity<DoctorStatsResponseDTO> getStats(@RequestParam Long idDoctor){
        return new ResponseEntity<>(dashboardService.getDoctorStats(idDoctor), HttpStatus.OK);
    }

    @GetMapping("/getProximasCitas")
    public List<ProximaCitaResponseDTO> getProximasCitas(@RequestParam Long idDoctor){
        return dashboardService.getProximasCitas(idDoctor);
    }

    @GetMapping("/getCitas")
    public ResponseEntity<CitasPageResponseDTO> getCitas(
            @RequestParam Long idDoctor,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(dashboardService.getCitasPaginadas(idDoctor, page, size, estado));
    }

    @GetMapping("/getCalendarioCitas")
    public ResponseEntity<CalendarioAnualDTO> getCalendarioCitas(@RequestParam Long idDoctor) {
        return ResponseEntity.ok(dashboardService.getCalendarioCitas(idDoctor));
    }

    @GetMapping("/getHorarioStats")
    public ResponseEntity<HorarioStatsResponseDTO> getHorarioStats(@RequestParam Long idDoctor) {
        return ResponseEntity.ok(dashboardService.getHorarioStats(idDoctor));
    }

    @GetMapping("/getHorarioDoctor")
    public ResponseEntity<HorarioDoctorResponseDTO> getHorarioDoctor(@RequestParam Long idDoctor) {
        return ResponseEntity.ok(dashboardService.getHorarioDoctor(idDoctor));
    }

    @PostMapping("/saveHorarioDoctor")
    public ResponseEntity<HorarioDoctorResponseDTO> saveHorarioDoctor(
            @RequestBody SaveHorarioDoctorRequestDTO request) {
        return ResponseEntity.ok(dashboardService.saveHorarioDoctor(request));
    }
}