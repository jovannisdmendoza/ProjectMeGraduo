package com.security.appsecurity.utils.Password;
import lombok.RequiredArgsConstructor;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenCleanupService {

    private final PasswordResetTokenRepository tokenRepository;

    @Scheduled(cron = "0 0 */8 * * *")
    public void cleanUpExpiredTokens() {
        Date now = new Date();
        List<PasswordResetToken> expiredTokens = tokenRepository.findByExpirationTimeBefore(now);
        tokenRepository.deleteAll(expiredTokens);
        System.out.println("Ejecutando...");
    }

}
