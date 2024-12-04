package com.example.dians2.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "issuers",uniqueConstraints = @UniqueConstraint(columnNames = "issuer_code"))
public class Issuer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "issuer_code", nullable = false, unique = true)
    private String issuerCode;

    @OneToMany(mappedBy = "issuer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IssuerData> issuerDataList;

    public Issuer(String issuerCode) {
        this.issuerCode = issuerCode;
    }
}
