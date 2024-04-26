package com.security.appsecurity.Controllers.Dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"username","message","jwt"})
public record AuthResponseLogin (String username, String message, String jwt) {
    
}
