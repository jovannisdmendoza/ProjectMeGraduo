package com.security.appsecurity.Controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/method")
//@PreAuthorize("denyAll()")
public class TextAuthController {
    //Se necesita el PreAutorize en la cabecera de la clase 
    //para trabajar con todos los endpoints
    @GetMapping("/get")
    //@PreAuthorize("hasAuthority('READ')")
    public String helloGet(){
        return "Hello Word - GET";
    }
    
    @PostMapping("/post")
    //@PreAuthorize("hasAuthority('CREATE') or hasAuthority('READ')")
    public String helloPost(){
        return "Hello Word -  POST";
    }

    @PutMapping("/put")
    //@PreAuthorize("hasAuthority('READ')")
    public String helloPut(){
        return "Hello Word - . PUT";
    }

    @PatchMapping("/patch")
   // @PreAuthorize("hasAuthority('REFACTOR')")
    public String helloPatch(){
        return "Hello PATCH - ";
    }

    
   // @PreAuthorize("hasAuthority('CREATE')")
    @DeleteMapping("/delete")
    public String helloDelete(){
        return "Hello Word - DELETED";
    }
}
