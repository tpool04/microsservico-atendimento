package br.com.tonypool.auth.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.tonypool.auth.model.PasswordResetToken;
import br.com.tonypool.auth.model.Cliente;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHashAndUsedFalse(String tokenHash);
    Optional<PasswordResetToken> findFirstByClienteAndUsedFalseOrderByCreatedAtDesc(Cliente cliente);
    void deleteAllByExpiryAtBefore(LocalDateTime dateTime);
}
