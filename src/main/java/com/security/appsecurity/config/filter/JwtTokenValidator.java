package com.security.appsecurity.config.filter;

import java.io.IOException;
import java.util.Collection;


import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.*;

import com.auth0.jwt.interfaces.*;
import com.security.appsecurity.utils.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// CLASE PARA JWT

public class JwtTokenValidator extends OncePerRequestFilter {

    
    private JwtUtils jwtUtils;

    public JwtTokenValidator(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION); // obten el header
        if(jwtToken!=null){// Bearer sdfsldfwjejwerjwlerj ejemplo de token
            jwtToken = jwtToken.substring(7); // para decirle el ignore los primeros 7 espacios del token Bearer

            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken); // validamos si el token es valido

            String username =jwtUtils.extractUsername(decodedJWT); // obtenemos el token  decodificado

            // recuperar los permisos que estan como claims invocadon el metodo de la clase
            String stringAuthorities = jwtUtils.getExpecificClaim(decodedJWT, "authorities").asString();
            
            //convertir a un GrantherAutority  mandame los permisos separados por "," y se los convierto a una lista de permisos
            Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(stringAuthorities);

            // seteamos el usuario en el contexto de spring Security
            SecurityContext context = SecurityContextHolder.getContext();

            // declarando el usuario para insertar en el contextHolder
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
            context.setAuthentication(authentication); // enviamos la autenticacion al Security contextHolder
            SecurityContextHolder.setContext(context);

        }
        filterChain.doFilter(request, response);
    }
    
}
