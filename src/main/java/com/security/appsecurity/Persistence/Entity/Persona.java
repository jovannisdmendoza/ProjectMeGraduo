package com.security.appsecurity.Persistence.Entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "persona")
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String numeroDocumento;
    @Column(name = "password")
    private String password;

    @Column(name = "primer_nombre")
    private String primerNombre;

    @Column (name = "primer_apellido")
    private String primerApellido;

    @Column(name = "secundo_nombre")
    private String segundoNombre;

    @Column (name = "segundo_apellido")
    private String segundoApellido;

    @Column (name = "telefono")
    private String telefono;

    @Column(name = "direccion")
    private String direccion;
    


}
