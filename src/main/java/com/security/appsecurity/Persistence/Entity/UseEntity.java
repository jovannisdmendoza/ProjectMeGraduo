package com.security.appsecurity.Persistence.Entity;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;
// DEL VIDEO DE SECURITY
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;

    @Column(name = "is_enable")
    private boolean isEnable;
    @Column(name = "account_no_expired")
    private boolean accountNoEspired;
    @Column(name = "account_no_locked")
    private boolean accountNoLocked;
    @Column(name = "credential_account")
    private boolean credentialAccount;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL )
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name="user_id"), inverseJoinColumns = @JoinColumn(name="role_id"))
    private Set<RoleEntity> roles = new HashSet<>();
  
}
