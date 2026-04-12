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
@Table(name = "DCP_ORG")
public class DcpOrg {
    @EmbeddedId
    private DcpOrgId id;

    @Column(name = "ORG_FORM", nullable = false, length = 1)
    private String orgForm;

    @Column(name = "ISDISTBR", length = 1)
    private String isdistbr;

    @Column(name = "ORG_TYPE", nullable = false, length = 1)
    private String orgType;

    @Column(name = "SNAME", length = 128)
    private String sname;

    @Column(name = "BELFIRM", length = 32)
    private String belfirm;

    @Column(name = "MEMO")
    private String memo;

    @Column(name = "STATUS")
    private Long status;

    @Column(name = "IS_CORP", nullable = false, length = 1)
    private String isCorp;

    @Column(name = "CORP", length = 10)
    private String corp;

    @Column(name = "ZIPCODE", length = 10)
    private String zipcode;

    @Column(name = "PROVINCE", length = 100)
    private String province;

    @Column(name = "CITY", length = 100)
    private String city;

    @Column(name = "COUNTY", length = 100)
    private String county;

    @Column(name = "STREET", length = 100)
    private String street;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "LATITUDE", precision = 18, scale = 10)
    private BigDecimal latitude;

    @Column(name = "LONGITUDE", precision = 18, scale = 10)
    private BigDecimal longitude;

    @Column(name = "PHONE", length = 30)
    private String phone;

    @Column(name = "FAX", length = 30)
    private String fax;

    @Column(name = "EMAIL", length = 128)
    private String email;

    @Column(name = "BANKNAME", length = 128)
    private String bankname;

    @Column(name = "BANKACCOUNT", length = 64)
    private String bankaccount;

    @Column(name = "IN_COST_WAREHOUSE", length = 32)
    private String inCostWarehouse;

    @Column(name = "IN_NON_COST_WAREHOUSE", length = 32)
    private String inNonCostWarehouse;

    @Column(name = "OUT_COST_WAREHOUSE", length = 32)
    private String outCostWarehouse;

    @Column(name = "OUT_NON_COST_WAREHOUSE", length = 32)
    private String outNonCostWarehouse;

    @Column(name = "INV_COST_WAREHOUSE", length = 32)
    private String invCostWarehouse;

    @Column(name = "INV_NON_COST_WAREHOUSE", length = 32)
    private String invNonCostWarehouse;

    @Column(name = "MACHORGANIZATIONNO", length = 100)
    private String machorganizationno;

    @Column(name = "FILENAME", length = 100)
    private String filename;

    @Column(name = "AREA", precision = 18, scale = 4)
    private BigDecimal area;

    @Column(name = "SHOPBEGINTIME", length = 20)
    private String shopbegintime;

    @Column(name = "SHOPENDTIME", length = 20)
    private String shopendtime;

    @Column(name = "ENABLECREDIT", length = 10)
    private String enablecredit;

    @Column(name = "SELLERGUINO", length = 40)
    private String sellerguino;

    @Column(name = "EINVOICEKEY")
    private String einvoicekey;

    @Column(name = "DC_CORP_NAME")
    private String dcCorpName;

    @Column(name = "DC_ADDRESS")
    private String dcAddress;

    @Column(name = "DC_PHONE", length = 30)
    private String dcPhone;

    @Column(name = "CORP_NAME", length = 180)
    private String corpName;

    @Column(name = "DECL_COMPANY", length = 120)
    private String declCompany;

    @Column(name = "THIRD_SHOP", length = 30)
    private String thirdShop;

    @Column(name = "SELFBEGINTIME", length = 20)
    private String selfbegintime;

    @Column(name = "SELFENDTIME", length = 20)
    private String selfendtime;

    @Column(name = "RANGE_TYPE", length = 10)
    private String rangeType;

    @Column(name = "\"RANGE\"", length = 2000)
    private String range;

    @Column(name = "CREATEBY", length = 20)
    private String createby;

    @Column(name = "CREATE_DTIME")
    private Date createDtime;

    @Column(name = "MODIFYBY", length = 20)
    private String modifyby;

    @Column(name = "MODIFY_DTIME")
    private Date modifyDtime;

    @Column(name = "UPDATE_TIME", length = 20)
    private String updateTime;

    @Column(name = "TRAN_TIME", length = 20)
    private String tranTime;

    @Column(name = "DISCENTRE", length = 1)
    private String discentre;

    @Column(name = "ISMANUALREG", length = 1)
    private String ismanualreg;

    @Column(name = "ORDERDISTBR", length = 32)
    private String orderdistbr;

    @Column(name = "CREDIT_TYPE", length = 1)
    private String creditType;

    @Column(name = "ARRIVALDAYS")
    private Long arrivaldays;

    @Column(name = "OPENINGDATE")
    private Date openingdate;

    @Column(name = "CREATEDEPTID", length = 40)
    private String createdeptid;

    @Column(name = "RETURN_COST_WAREHOUSE", length = 32)
    private String returnCostWarehouse;

    @Column(name = "MANAGER", length = 32)
    private String manager;

    @Column(name = "PARTNERNO", length = 20)
    private String partnerno;

    @Column(name = "TAXPAYER_TYPE", length = 1)
    private String taxpayerType;

    @Column(name = "TAXPAYER_NO", length = 40)
    private String taxpayerNo;

    @Column(name = "TAXATION", length = 1)
    private String taxation;

    @Column(name = "OUTPUTTAX", length = 32)
    private String outputtax;

    @Column(name = "USCI", length = 40)
    private String usci;

    @Column(name = "REGISTERNO", length = 40)
    private String registerno;

    @Column(name = "LEGALPERSON", length = 80)
    private String legalperson;

    @Column(name = "CLOSEDATE")
    private Date closedate;

    @Column(name = "OPSTATUS", length = 1)
    private String opstatus;

    @Column(name = "BILLCODE", length = 32)
    private String billcode;

}