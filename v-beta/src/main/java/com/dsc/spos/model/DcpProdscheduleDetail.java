package com.dsc.spos.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "DCP_PRODSCHEDULE_DETAIL")
public class DcpProdscheduleDetail {
    @EmbeddedId
    private DcpProdscheduleDetailId id;

    @Column(name = "ORGANIZATIONNO", nullable = false, length = 32)
    private String organizationno;

    @Column(name = "PLUNO", length = 40)
    private String pluno;

    @Column(name = "FEATURENO", length = 40)
    private String featureno;

    @Column(name = "UPPLUNO", length = 32)
    private String uppluno;

    @Column(name = "RDATE", length = 8)
    private String rdate;

    @Column(name = "PGROUPNO", length = 40)
    private String pgroupno;

    @Column(name = "PUNIT", length = 10)
    private String punit;

    @Column(name = "PQTY", precision = 23, scale = 8)
    private BigDecimal pqty;

    @Column(name = "POQTY", precision = 23, scale = 8)
    private BigDecimal poqty;

    @Column(name = "STOCKQTY", precision = 23, scale = 8)
    private BigDecimal stockqty;

    @Column(name = "SHORTQTY", precision = 23, scale = 8)
    private BigDecimal shortqty;

    @Column(name = "ADVICEQTY", precision = 23, scale = 8)
    private BigDecimal adviceqty;

    @Column(name = "MINQTY", precision = 23, scale = 8)
    private BigDecimal minqty;

    @Column(name = "MULQTY", precision = 23, scale = 8)
    private BigDecimal mulqty;

    @Column(name = "REMAINTYPE", length = 1)
    private String remaintype;

    @Column(name = "PREDAYS")
    private Integer predays;

    @Column(name = "BASEUNIT", length = 10)
    private String baseunit;

    @Column(name = "BASEQTY", precision = 23, scale = 8)
    private BigDecimal baseqty;

    @Column(name = "UNITRATIO", precision = 23, scale = 8)
    private BigDecimal unitratio;

    @Column(name = "MEMO")
    private String memo;

    @Column(name = "BOMNO", length = 32)
    private String bomno;

    @Column(name = "VERSIONNUM")
    private Integer versionnum;

    @Column(name = "GITEM")
    private Integer gitem;

    @Column(name = "SOURCETYPE", length = 1)
    private String sourcetype;

    @Column(name = "ODDVALUE", precision = 23, scale = 8)
    private BigDecimal oddvalue;

    @Column(name = "SEMIWOTYPE", length = 1)
    private String semiwotype;

    @Column(name = "SEMIWODEPTTYPE", length = 1)
    private String semiwodepttype;
}