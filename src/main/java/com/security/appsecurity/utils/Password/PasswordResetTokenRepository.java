// package com.security.appsecurity.utils.Password;

// import org.springframework.data.repository.CrudRepository;

// public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long>{
//     PasswordResetToken findByToken(String passwordResetToken);
// }

package com.security.appsecurity.utils.Password;
import org.springframework.data.repository.CrudRepository;

import com.security.appsecurity.Persistence.Entity.UseEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String passwordResetToken);
    Optional<PasswordResetToken>  findByUser(UseEntity passwordResetToken);
    List<PasswordResetToken> findByExpirationTimeBefore(Date now);
    void deleteByExpirationTimeBefore(Date now);
}
