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
    private String lastTradePrice;

    @Column(name = "max")
    private String max;

    @Column(name = "min")
    private String min;

    @Column(name = "avg_price")
    private String avgPrice;

    @Column(name = "percentage_change")
    private String percentageChange;

    @Column(name = "volume")
    private String volume;

    @Column(name = "turnover_best")
    private String turnoverBest;

    @Column(name = "total_turnover")
    private String totalTurnover;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issuer_code", referencedColumnName = "issuer_code", nullable = false)
    private Issuer issuer;
}
