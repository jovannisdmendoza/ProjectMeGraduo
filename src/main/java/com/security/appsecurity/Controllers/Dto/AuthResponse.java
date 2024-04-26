package com.security.appsecurity.Controllers.Dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

//DEL VIDEO DE JWT

@JsonPropertyOrder({"username","message"})
public record AuthResponse(String username, String message) {
    
}