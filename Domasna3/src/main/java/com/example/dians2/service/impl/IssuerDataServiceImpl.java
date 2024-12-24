package com.example.dians2.service.impl;

import com.example.dians2.model.IssuerData;
import com.example.dians2.repository.IssuerDataRepository;
import com.example.dians2.service.IssuerDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IssuerDataServiceImpl implements IssuerDataService {

    private final IssuerDataRepository issuerDataRepository;

    @Autowired
    public IssuerDataServiceImpl(IssuerDataRepository issuerDataRepository) {
        this.issuerDataRepository = issuerDataRepository;
    }

    public List<IssuerData> getIssuersByCodeAndTimeRange(String code, LocalDate from, LocalDate to) {
        Date fromDate = Date.from(from.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date toDate = Date.from(to.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()); // Set to the end of the day

        return issuerDataRepository.findByIssuerAndTimeRange(code, fromDate, toDate);
    }


    //  TODO NOVO

    public List<String> getLastTradePrices(String issuerCode, Date startDate, Date endDate) {
        List<IssuerData> data = issuerDataRepository.findByIssuerCodeAndDateRange(issuerCode, startDate, endDate);
        return data.stream()
                .map(IssuerData::getLastTradePrice)
                .collect(Collectors.toList());
    }

    public List<String> getDates(String issuerCode, Date startDate, Date endDate) {
        List<IssuerData> data = issuerDataRepository.findByIssuerCodeAndDateRange(issuerCode, startDate, endDate);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return data.stream()
                .map(issuerData -> formatter.format(issuerData.getDate()))
                .collect(Collectors.toList());
    }
}


