package com.example.dians2.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "issuers_data")
public class IssuerData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(name = "last_trade_price", nullable = false)
    private Double lastTradePrice;

    @Column(name = "max")
    private Double max;

    @Column(name = "min")
    private Double min;

    @Column(name = "avg_price")
    private Double avgPrice;

    @Column(name = "percentage_change")
    private Double percentageChange;

    @Column(name = "volume")
    private Double volume;

    @Column(name = "turnover_best")
    private Double turnoverBest;

    @Column(name = "total_turnover")
    private Double totalTurnover;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issuer_code", referencedColumnName = "issuer_code", nullable = false)
    private Issuer issuer;
}
