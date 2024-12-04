package com.example.dians2.repository;

import com.example.dians2.model.Issuer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CodeRepository extends JpaRepository<Issuer,Long> {
    List<Issuer> findAll();
    Optional<Issuer> findByIssuerCode(String issuerCode);
}
