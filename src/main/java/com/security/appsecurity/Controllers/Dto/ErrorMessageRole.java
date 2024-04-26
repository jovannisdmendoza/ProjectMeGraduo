package com.security.appsecurity.Controllers.Dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"message","error"})
public record ErrorMessageRole(String message, String error) {
    
}
