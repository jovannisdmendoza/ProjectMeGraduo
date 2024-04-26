package com.security.appsecurity.utils.Password;

import java.util.*;

import com.security.appsecurity.Persistence.Entity.UseEntity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
// @Table(name = "password_reset_token")
public class PasswordResetToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long token_id;
    private String token;
    private Date expirationTime;
    private static final int EXPIRATION_TIME = 5;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UseEntity user;

    public PasswordResetToken(String token, UseEntity user) {
        super();
        this.token = token;
        this.user = user;
        this.expirationTime = this.getTokenExpirationTime();
    }

    public PasswordResetToken(String token) {
        super();
        this.token = token;
        this.expirationTime = this.getTokenExpirationTime();
    }

    public Date getTokenExpirationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, EXPIRATION_TIME);
        return new Date(calendar.getTime().getTime());
    }
}
