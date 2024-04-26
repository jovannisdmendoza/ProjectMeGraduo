package com.security.appsecurity.utils;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

// DEL VIDEO DE JWT
@Component
public class JwtUtils {
    
    @Value("${security.jwt.key.private}")
    private String privateKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    public String createToken(Authentication authentication){
        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

        //authentication es quien tiene los usuarios que se autenticaron
        //aqui sacamos usuarios y autenticaciones
        String username = authentication.getPrincipal().toString();

        //Aqui tomamos las autorizaciones o permisos usando streams
        // para convertirlo en un String separados por comas
        // devuelve un objeto de de grantherAutority
        // en String usando map para llamar a la clase llamar a la clase GrantedAuthority
        // y llama el metodo getAuthority collect para separar los permisps por ","
        String authorities = authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority:: getAuthority)
        .collect(Collectors.joining(","));

        String jwtToken = JWT.create()
        .withIssuer(this.userGenerator) //Usuario Generador del token
        .withSubject(username) // Usuario El Sujeto a quien se le genera el token Usuario que se autentica
        .withClaim("authorities", authorities) // generamos un claim para los permisos
        .withIssuedAt(new Date() ) // fecha en la que se crea el token
        .withExpiresAt(new Date(System.currentTimeMillis()+18000000)) // fecha en la que espira el token,
        // significa del momento actual espira despues de media hora los //18000000
        .withJWTId(UUID.randomUUID().toString()) // JwtId importado de UUID un identificador cualquiera
        .withNotBefore(new Date(System.currentTimeMillis()))// a partir de que momento el token de considera valido
        // nota: tambien se puede colocar que el token sea valido despues de media hora o dos horas sumandole al currentTime...()
        .sign(algorithm);// aqui mandamos la firma, que es el algoritmo de encriptacion
        return jwtToken;
    }

    public DecodedJWT validateToken(String token){// Recibimos el token
        try{
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey); // necesita el algoritmo de encriptacion
            JWTVerifier verifier = JWT.require(algorithm) // se le manda el algoritmo
            .withIssuer(this.userGenerator) // se le manda el usuario generador
            .build(); // de construye el objeto verifier
            DecodedJWT decodedJWT = verifier.verify(token); // y se le manda a verifier el token 
            return decodedJWT; // se returna si el token existe
        }catch(JWTVerificationException exception){
            throw new JWTVerificationException("Token invalid, not Authorized");
        }
    }

    public String extractUsername(DecodedJWT decoder){
        return decoder.getSubject().toString();
    }

    public Claim  getExpecificClaim(DecodedJWT decodedJWT, String claimName){
        return decodedJWT.getClaim(claimName);
    }

    public Map<String,Claim> returnAllClaim(DecodedJWT decodedJWT ){
        return decodedJWT.getClaims();
    }
}
