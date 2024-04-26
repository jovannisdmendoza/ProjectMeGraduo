package com.security.appsecurity.utils.Password;

import lombok.*;

@Data
public class PasswordResetRequest {
    private String email;
    private String oldPassword;
    private String newPassword;
}
