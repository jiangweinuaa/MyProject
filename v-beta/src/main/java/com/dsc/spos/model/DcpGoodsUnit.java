package com.dsc.spos.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "DCP_GOODS_UNIT")
public class DcpGoodsUnit {
    @EmbeddedId
    private DcpGoodsUnitId id;

    @Column(name = "OQTY", nullable = false, precision = 23, scale = 8)
    private BigDecimal oqty;

    @Column(name = "QTY", nullable = false, precision = 23, scale = 8)
    private BigDecimal qty;

    @Column(name = "UNIT", nullable = false, length = 48)
    private String unit;

    @Column(name = "WEIGHT", precision = 23, scale = 8)
    private BigDecimal weight;

    @Column(name = "VOLUME", precision = 23, scale = 8)
    private BigDecimal volume;

    @Column(name = "SUNIT_USE", nullable = false, length = 1)
    private String sunitUse;

    @Column(name = "PUNIT_USE", nullable = false, length = 1)
    private String punitUse;

    @Column(name = "BOM_UNIT_USE", nullable = false, length = 1)
    private String bomUnitUse;

    @Column(name = "PROD_UNIT_USE", nullable = false, length = 1)
    private String prodUnitUse;

    @Column(name = "PURUNIT_USE", nullable = false, length = 1)
    private String purunitUse;

    @Column(name = "CUNIT_USE", nullable = false, length = 1)
    private String cunitUse;

    @Column(name = "LASTMODITIME")
    private Date lastmoditime;

    @Column(name = "TRAN_TIME", length = 30)
    private String tranTime;

    @Column(name = "UNITRATIO", nullable = false, precision = 23, scale = 8)
    private BigDecimal unitratio;

    @Column(name = "RUNIT_USE", length = 1)
    private String runitUse;

}