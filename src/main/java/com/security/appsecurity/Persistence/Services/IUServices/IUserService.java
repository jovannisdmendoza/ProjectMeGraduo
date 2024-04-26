package com.security.appsecurity.Persistence.Services.IUServices;

import java.util.Optional;

import com.security.appsecurity.Persistence.Entity.UseEntity;

public interface IUserService {
    Optional<UseEntity> findUserEntityByUsername(String username);

    void createPasswordResetTokenForUser(UseEntity user, String passwordResetToken);

    public String validatePasswordResetToken(String token);

    public UseEntity findUserByPasswordToken(String token);

    String validateToken(String theToken);
}
