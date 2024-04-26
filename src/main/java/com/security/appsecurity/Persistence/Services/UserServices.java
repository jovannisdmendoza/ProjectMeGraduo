package com.security.appsecurity.Persistence.Services;

import java.util.*;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.*;

import com.security.appsecurity.Persistence.Entity.UseEntity;
import com.security.appsecurity.Persistence.Repository.UserRepository;
import com.security.appsecurity.Persistence.Services.IUServices.IUserService;
import com.security.appsecurity.utils.Password.PasswordResetTokenServices;
import com.security.appsecurity.utils.Token.VerificationToken;
import com.security.appsecurity.utils.Token.VerificationTokenRepository;

import lombok.*;

@Service
@RequiredArgsConstructor
public class UserServices implements IUserService{
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenServices passwordResetTokenServices;
    private final VerificationTokenRepository tokenRepository;
    @Override
    public Optional<UseEntity> findUserEntityByUsername(String username) {
        return userRepository.findUserEntityByUsername(username);
    }

    @Override
    public void createPasswordResetTokenForUser(UseEntity useEntity, String passwordResetToken) {
        passwordResetTokenServices.createPasswordResetTokenForUser(useEntity, passwordResetToken);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        return passwordResetTokenServices.validatePasswordResetToken(token);
    }

    @Override
    public UseEntity findUserByPasswordToken(String token) {
        return passwordResetTokenServices.findUserByPasswordToken(token).get();
    }

    public void saveUserVerificationToken(UseEntity theUser, String token) {
        Optional<VerificationToken> userToken =  tokenRepository.findByUser(theUser);
        if(userToken.isPresent()){
            VerificationToken verificationToken = userToken.get();
            verificationToken.setToken(token);
            tokenRepository.save(verificationToken);
        }else{
            var verificationToken = new VerificationToken(token, theUser);
            tokenRepository.save(verificationToken);
        }   
    }

    public void changePassword(UseEntity useEntity, String newPassword) {
        useEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(useEntity);
    }

    @Override
    public String validateToken(String theToken) {
        VerificationToken token = tokenRepository.findByToken(theToken);
        if(token == null){
            return "Token de verificación invalido";
        }
        UseEntity user = token.getUser();
        Calendar calendar = Calendar.getInstance();
        if ((token.getExpirationTime().getTime()-calendar.getTime().getTime())<= 0){
            tokenRepository.delete(token);
            return "Este enlace de verificación ya expiró, por favor solicite uno nuevo";        
        }
        user.setEnable(true);
        userRepository.save(user);
        tokenRepository.delete(token);
        return "valid";
    }

   

   
    
}
