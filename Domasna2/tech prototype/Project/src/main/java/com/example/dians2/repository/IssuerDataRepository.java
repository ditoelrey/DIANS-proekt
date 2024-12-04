package com.example.dians2.repository;

import com.example.dians2.model.IssuerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface IssuerDataRepository extends JpaRepository<IssuerData, Long> {
//    List<IssuerData> findByCodeAndTimeRange(String code, LocalDateTime from, LocalDateTime to);
@Query("SELECT i FROM IssuerData i WHERE i.issuer.issuerCode = :issuerId AND i.date BETWEEN :startDate AND :endDate")
List<IssuerData> findByIssuerAndTimeRange(@Param("issuerId") String issuerId,
                                          @Param("startDate") Date startDate,
                                          @Param("endDate") Date endDate);
}
