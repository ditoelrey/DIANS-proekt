package com.example.dians2.service;

import com.example.dians2.model.IssuerData;
import com.example.dians2.repository.IssuerDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class IssuerDataService {

    private final IssuerDataRepository codeRepository;

    @Autowired
    public IssuerDataService(IssuerDataRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    public List<IssuerData> getIssuersByCodeAndTimeRange(String code, LocalDate from, LocalDate to) {
//        Date fromDate = Date.from(from.atZone(ZoneId.systemDefault()).toInstant());
//        Date toDate = Date.from(to.atZone(ZoneId.systemDefault()).toInstant());
//        return codeRepository.findByIssuerAndTimeRange(code, fromDate, toDate);
        Date fromDate = Date.from(from.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date toDate = Date.from(to.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()); // Set to the end of the day

        return codeRepository.findByIssuerAndTimeRange(code, fromDate, toDate);
    }
}

