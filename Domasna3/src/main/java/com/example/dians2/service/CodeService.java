package com.example.dians2.service;

import com.example.dians2.model.Issuer;
import com.example.dians2.model.IssuerData;

import java.util.List;

public interface CodeService {
    public List<String> getAllCodes();

    public Issuer saveIssuer(Issuer issuer);

    public void saveIssuerData(IssuerData issuerData, Issuer issuer);
}
