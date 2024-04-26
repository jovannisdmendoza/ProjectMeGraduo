package com.security.appsecurity.utils.Token;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.security.appsecurity.Persistence.Entity.UseEntity;

@Repository
public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long>{
    Optional<VerificationToken>  findByUser(UseEntity passwordResetToken);
    VerificationToken findByToken(String token);
}
