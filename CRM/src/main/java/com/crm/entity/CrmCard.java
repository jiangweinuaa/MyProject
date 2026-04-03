package com.crm.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * CRM 会员卡实体类
 */
@Data
@Entity
@Table(name = "CRM_CARD")
@org.hibernate.annotations.Table(appliesTo = "CRM_CARD")
public class CrmCard {

    @Id
    @Column(name = "CARDID")
    private Integer cardId;

    @Column(name = "EID")
    private String eid;

    @Column(name = "CARDNO")
    private String cardNo;

    @Column(name = "CARDSNNO")
    private String cardSnNo;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "MAINCARD")
    private Integer mainCard;

    @Column(name = "MEMBERID")
    private String memberId;

    @Column(name = "CARDTYPEID")
    private String cardTypeId;

    @Column(name = "CARDKIND")
    private Integer cardKind;

    @Column(name = "ECARDSIGN")
    private Integer eCardSign;

    @Column(name = "CARDMADED")
    private Integer cardMaded;

    @Column(name = "VALIDDATE")
    @Temporal(TemporalType.DATE)
    private Date validDate;

    @Column(name = "GENERATEDATE")
    @Temporal(TemporalType.DATE)
    private Date generateDate;

    @Column(name = "ISSUEDATE")
    @Temporal(TemporalType.DATE)
    private Date issueDate;

    @Column(name = "ACTIVEDATE")
    @Temporal(TemporalType.DATE)
    private Date activeDate;

    @Column(name = "MOBILE")
    private String mobile;

    @Column(name = "FACEAMOUNT", precision = 18, scale = 4)
    private BigDecimal faceAmount;

    @Column(name = "AMOUNT", precision = 18, scale = 4)
    private BigDecimal amount;

    @Column(name = "AMOUNT1", precision = 18, scale = 4)
    private BigDecimal amount1;

    @Column(name = "AMOUNT2", precision = 18, scale = 4)
    private BigDecimal amount2;

    @Column(name = "RECHTIMES")
    private Integer rechTimes;

    @Column(name = "RECHAMOUNT1", precision = 18, scale = 4)
    private BigDecimal rechAmount1;

    @Column(name = "RECHAMOUNT2", precision = 18, scale = 4)
    private BigDecimal rechAmount2;

    @Column(name = "LASTRECHTIME")
    @Temporal(TemporalType.DATE)
    private Date lastRechTime;

    @Column(name = "PAYTIMES", precision = 18, scale = 4)
    private BigDecimal payTimes;

    @Column(name = "PAYAMOUNT1", precision = 18, scale = 4)
    private BigDecimal payAmount1;

    @Column(name = "PAYAMOUNT2", precision = 18, scale = 4)
    private BigDecimal payAmount2;

    @Column(name = "LASTPAYTIME")
    @Temporal(TemporalType.DATE)
    private Date lastPayTime;

    @Column(name = "CONTIMES")
    private Integer conTimes;

    @Column(name = "CONAMOUNT", precision = 18, scale = 4)
    private BigDecimal conAmount;

    @Column(name = "LASTCONTIME")
    @Temporal(TemporalType.DATE)
    private Date lastConTime;

    @Column(name = "INTEGRATETIMES")
    private Integer integrateTimes;

    @Column(name = "ALLPOINT", precision = 18, scale = 4)
    private BigDecimal allPoint;

    @Column(name = "VALIDPOINT", precision = 18, scale = 4)
    private BigDecimal validPoint;

    @Column(name = "USEDPOINT", precision = 18, scale = 4)
    private BigDecimal usedPoint;

    @Column(name = "INVALIDPOINT", precision = 18, scale = 4)
    private BigDecimal invalidPoint;

    @Column(name = "BEAN", precision = 18, scale = 4)
    private BigDecimal bean;

    @Column(name = "SIGNATURE")
    private String signature;

    @Column(name = "COMPANYID")
    private String companyId;

    @Column(name = "SHOPID")
    private String shopId;

    @Column(name = "CHANNELID")
    private String channelId;

    @Column(name = "EMPLOYEEID")
    private String employeeId;

    @Column(name = "DEPARTID")
    private String departId;

    @Column(name = "REMARK")
    private String remark;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "CREATEOPID")
    private String createOpId;

    @Column(name = "CREATETIME")
    @Temporal(TemporalType.DATE)
    private Date createTime;

    @Column(name = "LASTMODIOPID")
    private String lastModiOpId;

    @Column(name = "LASTMODITIME")
    @Temporal(TemporalType.DATE)
    private Date lastModiTime;

    @Column(name = "OLDSTATUS")
    private Integer oldStatus;

    @Column(name = "BRANCHTYPE")
    private Integer branchType;

    @Column(name = "BRANCHID")
    private String branchId;

    @Column(name = "UNDERWAY")
    private Integer underway;

    @Column(name = "BEFSALESTATUS")
    private Integer befSaleStatus;

    @Column(name = "BEFSALEAMOUNT1", precision = 18, scale = 4)
    private BigDecimal befSaleAmount1;

    @Column(name = "BEFSALEAMOUNT2", precision = 18, scale = 4)
    private BigDecimal befSaleAmount2;

    @Column(name = "SALEBILLNO")
    private String saleBillNo;

    @Column(name = "AFTSALEAMOUNT1", precision = 18, scale = 4)
    private BigDecimal aftSaleAmount1;

    @Column(name = "AFTSALEAMOUNT2", precision = 18, scale = 4)
    private BigDecimal aftSaleAmount2;

    @Column(name = "CUSTID")
    private String custId;

    @Column(name = "FIXCARD")
    private Integer fixCard;

    @Column(name = "LEVELBEGINDATE")
    @Temporal(TemporalType.DATE)
    private Date levelBeginDate;

    @Column(name = "LEVELENDDATE")
    @Temporal(TemporalType.DATE)
    private Date levelEndDate;

    @Column(name = "TOBEINVALIDPOINT", precision = 18, scale = 4)
    private BigDecimal toBeInvalidPoint;

    @Column(name = "TOBEINVALIDENDDATE")
    @Temporal(TemporalType.DATE)
    private Date toBeInvalidEndDate;

    @Column(name = "LEVELBEGINDATE2")
    @Temporal(TemporalType.DATE)
    private Date levelBeginDate2;

    @Column(name = "BINDPASSWORD")
    private String bindPassword;

    @Column(name = "ACTIONKEY")
    private String actionKey;

    @Column(name = "DONEINVALIDENDDATE")
    @Temporal(TemporalType.DATE)
    private Date doneInvalidEndDate;

    @Column(name = "ORGCARDSNNO")
    private String orgCardSnNo;

    @Column(name = "ISOLDCARD")
    private Integer isOldCard;

    @Column(name = "BINDTOQIMAI")
    private Integer bindToQimai;

    @Column(name = "MEMBERNAME")
    private String memberName;

    @Column(name = "SEX")
    private Integer sex;

    @Column(name = "IDENTITYCODE")
    private String identityCode;

    @Column(name = "GENERATEBILLNO")
    private String generateBillNo;

    @Column(name = "BINDTOQIMAIDATE")
    @Temporal(TemporalType.DATE)
    private Date bindToQimaiDate;

    @Column(name = "CHANGEREASON")
    private String changeReason;

    @Column(name = "RECHARGEDEADLINE")
    private String rechargeDeadline;

    @Column(name = "BINDCARDVALIDDATE")
    @Temporal(TemporalType.DATE)
    private Date bindCardValidDate;
}
