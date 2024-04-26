package com.security.appsecurity.Persistence.Entity;

import jakarta.persistence.*;
import lombok.*;

// DEL VIDEO DE SECURITY
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "permissions")
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String name;
}
