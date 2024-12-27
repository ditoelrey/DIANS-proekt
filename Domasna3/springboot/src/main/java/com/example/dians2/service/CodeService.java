package com.example.dians2.service;

import com.example.dians2.model.Issuer;
import com.example.dians2.model.IssuerData;
import com.example.dians2.repository.CodeRepository;
import com.example.dians2.repository.IssuerDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CodeService {

    private final CodeRepository codeRepository;

    @Autowired
    public CodeService(CodeRepository codeRepository, IssuerDataRepository issuerDataRepository) {
        this.codeRepository = codeRepository;
        this.issuerDataRepository = issuerDataRepository;
    }

    private final IssuerDataRepository issuerDataRepository;

    public List<String> getAllCodes() {
        return codeRepository.findAllByOrderByIssuerCodeAsc().stream().map(Issuer::getIssuerCode).collect(Collectors.toList());
    }

public Issuer saveIssuer(Issuer issuer) {
    Optional<Issuer> existingIssuer = codeRepository.findByIssuerCode(issuer.getIssuerCode());
    if (existingIssuer.isPresent()) {
        System.out.println("Issuer already exists: " + existingIssuer.get());
        return existingIssuer.get();
    } else {
        Issuer savedIssuer = codeRepository.save(issuer);
        System.out.println("Saved new issuer: " + savedIssuer);
        return null;
    }
}

    public void saveIssuerData(IssuerData issuerData, Issuer issuer) {
        Issuer managedIssuer = saveIssuer(issuer);
        issuerData.setIssuer(managedIssuer);
        issuerDataRepository.save(issuerData);
    }


}

