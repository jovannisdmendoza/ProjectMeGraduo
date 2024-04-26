// package com.security.appsecurity.utils.Password;


// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;

// import com.security.appsecurity.Persistence.Entity.UseEntity;

// import java.util.Calendar;
// import java.util.Optional;

// @Service
// @RequiredArgsConstructor
// public class PasswordResetTokenServices {

//     private final PasswordResetTokenRepository passwordResetTokenRepository;
    
//     public void createPasswordResetTokenForUser(UseEntity user, String passwordToken) {
//         PasswordResetToken passwordRestToken = new PasswordResetToken(passwordToken, user);
//         passwordResetTokenRepository.save(passwordRestToken);
//     }

//     public String validatePasswordResetToken(String passwordResetToken) {
//         PasswordResetToken passwordToken = passwordResetTokenRepository.findByToken(passwordResetToken);
//         if(passwordToken == null){
//             return "Invalid verification token";
//         }
//        UseEntity user = passwordToken.getUser();
//         Calendar calendar = Calendar.getInstance();
//         if ((passwordToken.getExpirationTime().getTime()-calendar.getTime().getTime())<= 0){
//             return "Link already expired, resend link";
//         }
//         return "valid";
//     }

//     public Optional<UseEntity> findUserByPasswordToken(String passwordResetToken) {
//         return Optional.ofNullable(passwordResetTokenRepository.findByToken(passwordResetToken).getUser());
//     }
// }

package com.security.appsecurity.utils.Password;
import com.security.appsecurity.Persistence.Entity.UseEntity;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Calendar;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServices {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    
    public void createPasswordResetTokenForUser(UseEntity user, String passwordToken) {
        Optional<PasswordResetToken> userExistToken = passwordResetTokenRepository.findByUser(user);
        if(userExistToken.isPresent()){
            PasswordResetToken passwordResetToken = userExistToken.get();
            passwordResetToken.setToken(passwordToken); // Actualiza el token existente
            passwordResetTokenRepository.save(passwordResetToken);
        }else{
            PasswordResetToken passwordRestToken = new PasswordResetToken(passwordToken, user);
            passwordResetTokenRepository.save(passwordRestToken);
        }
    }

    public String validatePasswordResetToken(String passwordResetToken) {
        PasswordResetToken passwordToken = passwordResetTokenRepository.findByToken(passwordResetToken);
        if(passwordToken == null){
            return "El token para restablecer la contraseña no existe";
        }
        Calendar calendar = Calendar.getInstance();
        if ((passwordToken.getExpirationTime().getTime()-calendar.getTime().getTime())<= 0){
            // Eliminar el token expirado de la base de datos
            passwordResetTokenRepository.delete(passwordToken);
            return "El token para restablecer la contraseña ya expiró, solicite uno nuevo";
        }
        return "valid";
    }

    public Optional<UseEntity> findUserByPasswordToken(String passwordResetToken) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(passwordResetToken).getUser());
    }
}
