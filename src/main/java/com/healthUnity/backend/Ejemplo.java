package com.healthUnity.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Ejemplo {

    @GetMapping("/hola")
    public String hola(){
        return "Hello World ";
    }
}
