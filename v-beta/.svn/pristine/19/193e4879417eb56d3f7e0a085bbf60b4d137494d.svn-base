package com.dsc.spos.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "DCP_UNITCONVERT")
public class DcpUnitconvert {
    @EmbeddedId
    private DcpUnitconvertId id;

    @Column(name = "UNIT_RATIO", nullable = false, precision = 23, scale = 8)
    private BigDecimal unitRatio;

    @Column(name = "MEMO")
    private String memo;

    @Column(name = "STATUS", nullable = false)
    private Long status;

    @Column(name = "CREATEOPID", length = 32)
    private String createopid;

    @Column(name = "CREATEOPNAME", length = 64)
    private String createopname;

    @Column(name = "CREATETIME")
    private Date createtime;

    @Column(name = "LASTMODIOPID", length = 32)
    private String lastmodiopid;

    @Column(name = "LASTMODIOPNAME", length = 64)
    private String lastmodiopname;

    @Column(name = "LASTMODITIME")
    private Date lastmoditime;

    @Column(name = "TRAN_TIME", length = 20)
    private String tranTime;

    @Column(name = "CREATEDEPTID", length = 32)
    private String createdeptid;

}