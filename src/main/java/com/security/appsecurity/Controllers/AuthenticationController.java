package com.security.appsecurity.Controllers;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.security.appsecurity.Controllers.Dto.AuthCreateUserRequest;
import com.security.appsecurity.Controllers.Dto.AuthLoginRequest;
import com.security.appsecurity.Controllers.Dto.AuthResponse;
import com.security.appsecurity.Controllers.Dto.AuthResponseLogin;
import com.security.appsecurity.Controllers.Dto.ErrorMessageRole;
import com.security.appsecurity.Error.dto.ErrorMessage;
import com.security.appsecurity.Persistence.Entity.UseEntity;
import com.security.appsecurity.Persistence.Services.UserDetailsServicesmpl;
import com.security.appsecurity.Persistence.Services.UserServices;
import com.security.appsecurity.event.listener.RegistrationCompleteEventListener;
import com.security.appsecurity.utils.Password.PasswordResetRequest;
import com.security.appsecurity.utils.Token.VerificationToken;
import com.security.appsecurity.utils.Token.VerificationTokenRepository;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;




@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserServices userService;
    private final VerificationTokenRepository tokenRepository;
    private final RegistrationCompleteEventListener eventListener;

    @Autowired
    private UserDetailsServicesmpl userDetailsService;
    
    @PostMapping("/log-in")
    public ResponseEntity<?> Login(@RequestBody @Valid AuthLoginRequest userRequest, final HttpServletRequest request) throws UsernameNotFoundException, BadCredentialsException{
        System.out.println(userRequest.username());
        System.out.println(userRequest.password());
        try {
            return new ResponseEntity<>(this.userDetailsService.LoginUser(userRequest), HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            String errorMessage = "Usuario no registrado en el sistema :  " + e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AuthResponse(userRequest.username(), errorMessage));
        } catch (BadCredentialsException e) {
            String errorMessage = "Credenciales incorrectas: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(userRequest.username(), errorMessage));
        }
    }
    @PostMapping("/sign-up")
    public ResponseEntity<?> register(@RequestBody 
    @Valid AuthCreateUserRequest authCreateUser, 
    final HttpServletRequest request)throws IllegalArgumentException,BadCredentialsException{
        try{
            return new ResponseEntity<AuthResponse>(this.userDetailsService.createUser(authCreateUser,applicationUrl(request)), HttpStatus.OK);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMessageRole("El Rol no Existe en la Base de datos", "IllegalArgumentException"));
        } catch (BadCredentialsException e) {
            String errorMessage =   e.getMessage();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("Error", errorMessage));
        }
    }
    @PostMapping("/password-reset-request")
    public String resetPasswordRequest(@RequestBody PasswordResetRequest passwordRequestUtil,
                               final HttpServletRequest servletRequest)
           throws MessagingException, UnsupportedEncodingException {
        Optional<UseEntity> user = userService.findUserEntityByUsername(passwordRequestUtil.getEmail());
        String passwordResetUrl = "";
        if (user.isPresent()) {
            String passwordResetToken = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user.get(), passwordResetToken);
            passwordResetUrl = passwordResetEmailLink(user.get(), applicationUrl(servletRequest), passwordResetToken);
            String passwordResetUrl2 = passwordResetEmailLink2(user.get(), applicationUrl(servletRequest), passwordResetToken);
            System.out.println(passwordResetUrl2);
        }
        else{
            return "Usuario no Existe en le Base de Datos";
        }
        return passwordResetUrl;
    }

    private String passwordResetEmailLink2(UseEntity user, String applicationUrl,
    String passwordToken) throws MessagingException, UnsupportedEncodingException {
        String url = applicationUrl+"/auth/reset-password?token="+passwordToken;
        //eventListener.sendPasswordResetVerificationEmail(url,user);
        System.out.println("Click the link to reset your password :  {}"+url);
        // log.info("", url); 
        return url;
    }
    private String applicationUrlFrontend = "http://localhost:4200";
    private String passwordResetEmailLink(UseEntity user, String applicationUrl, String passwordToken) throws MessagingException, UnsupportedEncodingException {
    String url = applicationUrlFrontend + "/change-password?token=" + passwordToken; // Modificar la URL aquí
    eventListener.sendPasswordResetVerificationEmail(url,user);
    System.out.println("Click the link to reset your password :  {}"+url);
    // log.info("", url); 
    return url;
}
    // @GetMapping("/verifyEmail")
    // public ResponseEntity<?> sendVerificationToken(@RequestParam("token") String token){
    //     VerificationToken theToken = tokenRepository.findByToken(token);
    //     if (theToken.getUser().isEnable()){
    //         AuthResponse authResponse = new AuthResponse(theToken.getUser().getUsername(), "Usuario ya fue autenticado, Inicie sesion");
    //          return  new ResponseEntity<> (authResponse,HttpStatus.OK);
    //     }
    //     String verificationResult = userService.validateToken(token);
    //     if (verificationResult.equalsIgnoreCase("valid")){
    //         AuthResponseLogin auth = userDetailsService.AuthenticaUserSystem(theToken.getUser());
    //          return  new ResponseEntity<> (auth,HttpStatus.OK);
    //     }
    //     AuthResponse authResponse = new AuthResponse(theToken.getUser().getUsername(), "Este enlace de verificación ha caducado,por favor realiza nuevamente el registro en el sistema.");
    //     return   new ResponseEntity<> (authResponse,HttpStatus.OK);
    // }

    @GetMapping("/verifyEmail")
    public ResponseEntity<?> sendVerificationToken(@RequestParam("token") String token) {
    VerificationToken theToken = tokenRepository.findByToken(token);
    
    // Verificar si el token es válido
    String verificationResult = userService.validateToken(token);
    if (!verificationResult.equalsIgnoreCase("valid")) {
        AuthResponse authResponse = new AuthResponse("Enlace de verificación no válido", "Obtenga un nuevo enlace de verificación");
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
    
    // Verificar si el usuario ya ha sido autenticado
    if (theToken.getUser().isEnable()) {
        AuthResponse authResponse = new AuthResponse(theToken.getUser().getUsername(), "Usuario ya fue autenticado, Inicie sesión");
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
    // Autenticar al usuario si el token es válido y el usuario no ha sido autenticado previamente
    AuthResponseLogin auth = userDetailsService.AuthenticaUserSystem(theToken.getUser());
    return new ResponseEntity<>(auth, HttpStatus.OK);
}

    // @GetMapping("/verifyEmailS")
    // public ResponseEntity<AuthResponse> sendVerificationTokenS(@RequestParam("token") String token){

    //     VerificationToken theToken = tokenRepository.findByToken(token);
    //     if (theToken.getUser().isEnable()){
    //         AuthResponse authResponse = new AuthResponse(theToken.getUser().getUsername(), "Usuario ya fue autenticado, Inicie sesion", "Me Graduo", true);
    //          return  new ResponseEntity<> (authResponse,HttpStatus.OK);
    //     }
    //     String verificationResult = userService.validateToken(token);
    //     if (verificationResult.equalsIgnoreCase("valid")){
    //          AuthResponse auth = userDetailsService.AuthenticaUserSystem(theToken.getUser());
    //          return  new ResponseEntity<> (auth,HttpStatus.OK);
    //     }
    //     AuthResponse authResponse = new AuthResponse("Enlace de verificación no válido", "Obtenga un nuevo enlace de verificación", "Me Graduo", true);
    //     return   new ResponseEntity<> (authResponse,HttpStatus.OK);
    // }
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody PasswordResetRequest passwordRequestUtil,
                                @RequestParam("token") String token){
        String tokenVerificationResult = userService.validatePasswordResetToken(token);
        if (!tokenVerificationResult.equalsIgnoreCase("valid")) {
            return userService.validatePasswordResetToken(token);
        }
        Optional<UseEntity> theUser = Optional.ofNullable(userService.findUserByPasswordToken(token));
        if (theUser.isPresent()) {
            userService.changePassword(theUser.get(), passwordRequestUtil.getNewPassword());
            return "Su contraseña ha sido modificada correctamente";
        }
        return userService.validatePasswordResetToken(token);
    }
    public String applicationUrl(HttpServletRequest request) {
        return "http://"+request.getServerName()+":"
                +request.getServerPort()+request.getContextPath();
    }

}
