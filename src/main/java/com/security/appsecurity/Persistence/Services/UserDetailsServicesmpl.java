package com.security.appsecurity.Persistence.Services;




import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.security.appsecurity.Controllers.Dto.AuthCreateUserRequest;
import com.security.appsecurity.Controllers.Dto.AuthLoginRequest;
import com.security.appsecurity.Controllers.Dto.AuthResponse;
import com.security.appsecurity.Controllers.Dto.AuthResponseLogin;
import com.security.appsecurity.Persistence.Entity.RoleEntity;
import com.security.appsecurity.Persistence.Entity.RoleEnum;
import com.security.appsecurity.Persistence.Entity.UseEntity;
import com.security.appsecurity.Persistence.Repository.RoleRepository;
import com.security.appsecurity.Persistence.Repository.UserRepository;
import com.security.appsecurity.event.RegistrationCompleteEvent;
import com.security.appsecurity.utils.JwtUtils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


// DEL VIDEO DE SECURITY
@Service
@AllArgsConstructor
public class UserDetailsServicesmpl  implements UserDetailsService{
    // DEL VIDEO DE JWT
    @Autowired
    PasswordEncoder passwordEncoder;
    // DEL VIDEO DE JWT
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    // DEL VIDEO DE JWT
    @Autowired
    private JwtUtils jwtUtils;

    private final ApplicationEventPublisher publisher;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UseEntity userEntity = userRepository.findUserEntityByUsername(username)
        .orElseThrow(()-> new UsernameNotFoundException("El usuario, " + username +" no existe"));
        
        //Donde le vamos a decir a Security cuales  seran los Permisos  o roles del Sistemas 
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        userEntity.getRoles().forEach(role-> authorityList.add((new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name())))));

        //para los permiso convertimoslos roles a un flujo con 
        // stream(), porque dentro de userEntity tiene una lista de roles
        // y los roles tiene una lista de permisos
        userEntity.getRoles().
        stream()
        .flatMap(role-> role.getPermissionsList().stream())
        .forEach(permission -> authorityList.add((new SimpleGrantedAuthority(permission.getName()))));
        
        return new User(userEntity.getUsername(),
        userEntity.getPassword(),
        userEntity.isEnable(),
        userEntity.isAccountNoEspired(),
        userEntity.isCredentialAccount(),
        userEntity.isAccountNoLocked(),
        authorityList
        );
    }

    // DEL VIDEO DE JWT

    public AuthResponseLogin LoginUser(AuthLoginRequest authLoginRequest) throws UsernameNotFoundException, BadCredentialsException{ // Recibimos la clase AuthLoginRequest
        String username = authLoginRequest.username(); // lo asignamos a dos variables
        String password = authLoginRequest.password();
        System.out.println("user name : UserDetails : "+ username );
        System.out.println("user password : UserDetails : "+ password );
        UserDetails userDetails = this.loadUserByUsername(username);
        if(userDetails.isEnabled()==false){
            throw new BadCredentialsException("El usuario no ha verificado su cuenta, por favor ingrese a su correo institucional y haga clic en *Verificar cuenta* para finalizar el proceso de registro. ");
        }
        Authentication authentication = this.authenticate(username,password); //nos autenticamos con este metodo
        SecurityContextHolder.getContext().setAuthentication(authentication);// si paso los filtros anterior se aguarda en el
        // Security contexHolder
        String accesToken = jwtUtils.createToken(authentication);
        AuthResponseLogin authResponse = new AuthResponseLogin(username,"Usuario Autenticado", accesToken);
        return authResponse;
    }
    private Authentication authenticate(String username, String password) throws BadCredentialsException, UsernameNotFoundException{
        UserDetails userDetails = this.loadUserByUsername(username);
        System.out.println("UserDetails : "+ userDetails );
        if(userDetails== null){ // si el usuario no existe pasa
            throw new BadCredentialsException(" El usuario no existe en el sistema");
        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())){ // si la contraseña es incorrecta votamos el error
            throw new BadCredentialsException(" Usuario o contraseña inválidos");
        }
        if(userDetails.isEnabled()==false){
            throw new BadCredentialsException("El usuario no ha verificado su cuenta, por favor ingrese a su correo institucional y haga clic en *Verificar cuenta* para finalizar el proceso de registro.");     
        }
        // si todo paso devolvemos el objeto de autenticacion "Authenticacion "
        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }

    public AuthResponse createUser(AuthCreateUserRequest authCreateUser, String url) throws IllegalArgumentException, BadCredentialsException{
        String username = authCreateUser.username();
        String password = authCreateUser.password();
        List<String> roleRequest = authCreateUser.roleRequest().roleListName();
        // Optional <RoleEntity>rolExisting = roleRepository.findRoleEntitiesByRoleEnumIn(roleRequest);
        // if(isValidRoles(roleRequest)){
        //     throw new IllegalArgumentException("El Rol no existe en la Base de Datos");
        // }
        Set<RoleEntity> roleEntitySet = roleRepository.findRoleEntitiesByRoleEnumIn(roleRequest).stream().collect(Collectors.toSet());
        if(roleEntitySet.isEmpty()){
            throw new IllegalArgumentException("The Roles no exist");
        }
        // if()
        Optional<UseEntity> entidad = userRepository.findUserEntityByUsername(username);
        if(entidad.isPresent() ){
            UseEntity userEntityFound = userRepository.findUserEntityByUsername(username)
            .orElseThrow(()-> new UsernameNotFoundException("El Usuario" + username +"no Existe"));
            AuthResponse authResponse = new AuthResponse(userEntityFound.getUsername(), 
            "Usuario ya existe en el sistema, sin embargo no se encuentra autenticado, se ha enviado un nuevo enlace de verificacion a su correo");
            publisher.publishEvent(new RegistrationCompleteEvent(userEntityFound, url));
            return authResponse;
        }
        UseEntity userEntity = UseEntity.builder()
        .username(username)
        .password(passwordEncoder.encode(password))
        .roles(roleEntitySet)
        .isEnable(false)
        .accountNoLocked(true)
        .accountNoEspired(true)
        .credentialAccount(true) //credentialNoExpired
        .build();
        UseEntity userCreated = userRepository.save(userEntity);
        publisher.publishEvent(new RegistrationCompleteEvent(userEntity, url));
        AuthResponse authResponse = new AuthResponse(userCreated.getUsername(), "Usuario Creado Correctamente Se ha enviado un correo de confirmacion");
        return authResponse;     
    }

    public AuthResponseLogin AuthenticaUserSystem(UseEntity userCreated){
        ArrayList<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        userCreated.getRoles().forEach(role-> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));
        userCreated.getRoles().
        stream()
        .flatMap(role -> role.getPermissionsList().stream())
        .forEach(permmision -> authorityList.add(new SimpleGrantedAuthority(permmision.getName())));;
        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated.getUsername(), userCreated.getPassword(), authorityList);
        String acessToken = jwtUtils.createToken(authentication);
        AuthResponseLogin authResponse = new AuthResponseLogin(userCreated.getUsername(), "Usuario Creado Correctamente", acessToken);
        return authResponse;
    }
    public boolean isValidRole(String role) {
        return Arrays.stream(RoleEnum.values())
                     .anyMatch(enumRole -> enumRole.name().equalsIgnoreCase(role));
    }

    public boolean isValidRoles(List<String> roles) {
        List<String> invalidRoles = roles.stream()
                                         .filter(role -> !isValidRole(role))
                                         .collect(Collectors.toList());

        return invalidRoles.isEmpty();
    }



   
    
}
