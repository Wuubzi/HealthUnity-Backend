package com.healthUnity.backend.Controllers;

import com.healthUnity.backend.DTO.Response.HorarioResponseDTO;
import com.healthUnity.backend.DTO.Response.TopDoctorsResponseDTO;
import com.healthUnity.backend.Models.Doctores;
import com.healthUnity.backend.Models.OpinionesDoctores;
import com.healthUnity.backend.Services.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/doctor")
public class DoctorController {

    private final DoctorService  doctorService;

    @Autowired
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/top-doctores")
    public List<TopDoctorsResponseDTO> getTopDoctores(){
        return doctorService.getTopDoctores();
    }

    @GetMapping("/getDoctorById")
    public Doctores getDoctorById(@RequestParam Long idDoctor) {
        return doctorService.getDoctorById(idDoctor);
    }

    @GetMapping("/getReviewsByDoctorId")
    public  List<OpinionesDoctores> getOpinionDoctorById(@RequestParam Long idDoctor){
        return doctorService.getOpinionDoctorById(idDoctor);
    }

    @GetMapping("/getHorarioDoctor")
    public HorarioResponseDTO getHorarioDoctor(@RequestParam Long idDoctor) {
        return doctorService.getHorarioDoctor(idDoctor);
    }
}
