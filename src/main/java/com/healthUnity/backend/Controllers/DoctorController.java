package com.healthUnity.backend.Controllers;

import com.healthUnity.backend.DTO.Request.DoctorFavoritoRequestDTO;
import com.healthUnity.backend.DTO.Request.OpinionRequestDTO;
import com.healthUnity.backend.DTO.Response.*;
import com.healthUnity.backend.Models.Doctores;
import com.healthUnity.backend.Models.OpinionesDoctores;
import com.healthUnity.backend.Services.DoctorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/getDoctores")
    public ResponseEntity<PaginatedDoctorResponse> getDoctores(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long especialidadId,
            @RequestParam(required = false, defaultValue = "relevancia") String orderBy
    ) {
        // Validaciones
        if (page < 1) page = 1;
        if (limit < 1) limit = 10;
        if (limit > 100) limit = 100;

        // Llamar servicio
        return new ResponseEntity<>( doctorService.getDoctores(
                page - 1, // Spring usa base 0
                limit,
                search,
                especialidadId,
                orderBy
        ), HttpStatus.OK);
    }


    @GetMapping("/getDoctorById")
    public
    Doctores getDoctorById(@RequestParam Long idDoctor) {
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

    @GetMapping("/get-doctores-favoritos")
    public List<FavoritoDoctorResponseDTO> getDoctoresFavoritos(@RequestParam Long idPaciente){
        return doctorService.getDoctoresFavoritos(idPaciente);
    }

    @PostMapping("/añadir-favoritos")
    public ResponseEntity<ResponseDTO> añadirFavoritos(@RequestBody DoctorFavoritoRequestDTO data, HttpServletRequest request) {
        return new ResponseEntity<>(doctorService.añadirFavoritos(data, request),HttpStatus.OK);
    }

    @PostMapping("/añadir-opinion")
    public ResponseEntity<ResponseDTO> añadirOpinion(@RequestBody OpinionRequestDTO opinion, HttpServletRequest request){
        return new ResponseEntity<>(doctorService.añadirOpinion(opinion,request), HttpStatus.OK);
    }

    @DeleteMapping("/eliminar-favoritos")
    public ResponseEntity<ResponseDTO> eliminarFavoritos(@RequestParam Long idFavorito, HttpServletRequest request) {
        return new ResponseEntity<>(doctorService.eliminarFavoritos(idFavorito, request),HttpStatus.OK);
    }
}
