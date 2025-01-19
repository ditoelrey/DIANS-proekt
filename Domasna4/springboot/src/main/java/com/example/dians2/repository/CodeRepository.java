package com.example.dians2.repository;

import com.example.dians2.model.Issuer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeRepository extends JpaRepository<Issuer,Long> {
    List<Issuer> findAllByOrderByIssuerCodeAsc();
    Optional<Issuer> findByIssuerCode(String issuerCode);
}
