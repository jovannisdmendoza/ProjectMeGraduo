package com.security.appsecurity.Controllers.Dto;

import java.util.List;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Size;

@Validated
public record AuthCreateRoleRequest(
    @Size(max = 3, message = "the user cannot have more than 3 roles")    List<String> roleListName
) {
    
} 