package com.dsc.spos.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "MES_BATCH")
public class MesBatch {
    @EmbeddedId
    private MesBatchId id;

    @Column(name = "PRODUCTDATE")
    private Date productdate;

    @Column(name = "LOSEDATE")
    private Date losedate;

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

}