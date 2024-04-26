package com.security.appsecurity.config;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.security.appsecurity.config.filter.JwtTokenValidator;
import com.security.appsecurity.utils.JwtUtils;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

    


@Configuration

//TRabajar sin anotaciones
@EnableWebSecurity

//Trabajar con anotaciones, usando el controlador usando @PreAuthorize
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtils jwtUtils;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
            .csrf(csrfs -> csrfs.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            //cuando te vas a loguear con usuario y contraseña se usa el
            //httpbasic(...)
            .httpBasic(Customizer.withDefaults())
            //Session STALESS = Sin estado
            // quiere decir que si trabajamos con estado se crea un objeto de seccion en 
            //memoria el "Stales" dice que no vamos a guardar le seccion en memoria
            // la seccion va durar lo mismo que el token
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(http->{
                //Configurar los endpoints publicos
    
                http.requestMatchers(HttpMethod.POST, "/auth/**").permitAll();
                //esta linea es de prueba
                http.requestMatchers(HttpMethod.POST, "/auth/reset-password").permitAll();
                //Configurar los edpoints protegidos o privados
                http.requestMatchers(HttpMethod.GET, "/auth/verifyEmail").permitAll(); 
                http.requestMatchers(HttpMethod.GET, "/auth/**").permitAll(); 
                // http.requestMatchers(HttpMethod.POST, "/auth/post").hasRole("ADMIN");
                http.requestMatchers(HttpMethod.POST, "/method/post").hasAnyRole("ADMIN","STUDENT","DEVELOPER");
                // http.requestMatchers(HttpMethod.POST, "/auth/post").hasAuthority("CREATE","READ");
                http.requestMatchers(HttpMethod.PATCH, "/method/patch").hasAnyAuthority("REFACTOR");

                http.requestMatchers(HttpMethod.GET, "/method/get").hasAnyRole("DEVELOPER");
                //rechaza todo ya sea autenticados o no "denyALL()"
                http.anyRequest().denyAll();

                //permite pasar solamente a los autenticados
               // http.anyRequest().authenticated();
            })
            .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class)
           
            .build();
    }
    
    private CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(@SuppressWarnings("null") @NonNull HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(Arrays.asList("http://localhost:5173","http://localhost:4200","http://localhost:5173/", "http://localhost:8000/","http://localhost:8000",
                   "http://localhost:48496","http://localhost:5037/","https://d2zpl8rr-5173.use2.devtunnels.ms"));
                config.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "OPTIONS"));
                config.setAllowedHeaders(Arrays.asList("*"));
                config.setAllowCredentials(true);
                config.setExposedHeaders(Arrays.asList("Authorization"));
                config.setMaxAge(3600L);
                return config;
            }
        };
    }

    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
    //     return httpSecurity
    //         .csrf(csrfs -> csrfs.disable())
    //         //cuando te vas a loguear con usuario y contraseña se usa el
    //         //httpbasic(...)
    //         .httpBasic(Customizer.withDefaults())
    //         //Session STALESS = Sin estado
    //         // quiere decir que si trabajamos con estado se crea un objeto de seccion en 
    //         //memoria el "Stales" dice que no vamos a guardar le seccion en memoria
    //         // la seccion va durar lo mismo que el token
    //         .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    //         .build();
    // }

    

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService){

        //El dao este provide necesita dos componentes passworEncoder y el UserDetailsServices
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }

    // @Bean
    // public UserDetailsService userdetailsService(){
        
    // }

   
}
