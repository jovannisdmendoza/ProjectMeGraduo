package com.security.appsecurity.Controllers.Dto;

import jakarta.validation.constraints.NotBlank;


// DEL VIDEO DE JWT
public record AuthLoginRequest(@NotBlank String username,
                                @NotBlank String password) { // aqui esta el username y password

    

} 
