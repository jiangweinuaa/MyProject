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
@Table(name = "DCP_PRODSCHEDULE_GEN")
public class DcpProdscheduleGen {
    @EmbeddedId
    private DcpProdscheduleGenId id;

    @Column(name = "ORGANIZATIONNO", nullable = false, length = 32)
    private String organizationno;

    @Column(name = "PLUNO", length = 40)
    private String pluno;

    @Column(name = "FEATURENO", length = 40)
    private String featureno;

    @Column(name = "UPPLUNO", length = 40)
    private String uppluno;

    @Column(name = "PGROUPNO", length = 40)
    private String pgroupno;

    @Column(name = "DEPARTID", length = 32)
    private String departid;

    @Column(name = "PUNIT", length = 10)
    private String punit;

    @Column(name = "PQTY", precision = 23, scale = 8)
    private BigDecimal pqty;

    @Column(name = "RDATE", length = 8)
    private String rdate;

    @Column(name = "PREDAYS")
    private Integer predays;

    @Column(name = "BEGINDATE", length = 8)
    private String begindate;

    @Column(name = "ENDDATE", length = 8)
    private String enddate;

    @Column(name = "TOWOQTY", precision = 23, scale = 8)
    private BigDecimal towoqty;

    @Column(name = "BASEUNIT", length = 10)
    private String baseunit;

    @Column(name = "BASEQTY", precision = 23, scale = 8)
    private BigDecimal baseqty;

    @Column(name = "UNITRATIO", precision = 23, scale = 8)
    private BigDecimal unitratio;

    @Column(name = "BOMNO", length = 32)
    private String bomno;

    @Column(name = "VERSIONNUM")
    private Long versionnum;

}