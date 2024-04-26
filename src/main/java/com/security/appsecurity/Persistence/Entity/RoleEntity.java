package com.security.appsecurity.Persistence.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;
// DEL VIDEO DE SECURITY
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="role_name")
    @Enumerated(EnumType.STRING)
    private RoleEnum roleEnum;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL )
    @JoinTable(name = "role_permission", joinColumns = @JoinColumn(name="role_id"), inverseJoinColumns = @JoinColumn(name="permission_id"))
    private Set<PermissionEntity> permissionsList = new HashSet<>();
    
}
