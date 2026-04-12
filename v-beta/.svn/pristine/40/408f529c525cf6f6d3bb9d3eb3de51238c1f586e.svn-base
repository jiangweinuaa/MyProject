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
@Table(name = "DCP_GOODS")
public class DcpGood {

    @EmbeddedId
    private DcpGoodId id;

    @Column(name = "PLUTYPE", nullable = false, length = 48)
    private String plutype;

    @Column(name = "SPLITTYPE", length = 48)
    private String splittype;

    @Column(name = "ATTRGROUPID", length = 48)
    private String attrgroupid;

    @Column(name = "CATEGORY", nullable = false, length = 48)
    private String category;

    @Column(name = "BRAND", length = 48)
    private String brand;

    @Column(name = "SERIES", length = 48)
    private String series;

    @Column(name = "WARMTYPE", length = 48)
    private String warmtype;

    @Column(name = "SHORTCUT_CODE", length = 48)
    private String shortcutCode;

    @Column(name = "VIRTUAL", nullable = false, length = 1)
    private String virtual;

    @Column(name = "OPENPRICE", nullable = false, length = 1)
    private String openprice;

    @Column(name = "ISWEIGHT", nullable = false, length = 1)
    private String isweight;

    @Column(name = "STOCKMANAGETYPE", nullable = false)
    private Long stockmanagetype;

    @Column(name = "TAXCODE", length = 48)
    private String taxcode;

    @Column(name = "MEMO", length = 382)
    private String memo;

    @Column(name = "STATUS", nullable = false)
    private Long status;

    @Column(name = "BASEUNIT", nullable = false, length = 48)
    private String baseunit;

    @Column(name = "PRICE", precision = 23, scale = 8)
    private BigDecimal price;

    @Column(name = "SUPPRICE", precision = 23, scale = 8)
    private BigDecimal supprice;

    @Column(name = "SUNIT", nullable = false, length = 48)
    private String sunit;

    @Column(name = "BOM_UNIT", nullable = false, length = 48)
    private String bomUnit;

    @Column(name = "PROD_UNIT", nullable = false, length = 48)
    private String prodUnit;

    @Column(name = "PUNIT", nullable = false, length = 48)
    private String punit;

    @Column(name = "PURUNIT", nullable = false, length = 48)
    private String purunit;

    @Column(name = "CUNIT", nullable = false, length = 48)
    private String cunit;

    @Column(name = "WUNIT", nullable = false, length = 48)
    private String wunit;

    @Column(name = "ISBATCH", nullable = false, length = 1)
    private String isbatch;

    @Column(name = "SHELFLIFE")
    private Long shelflife;

    @Column(name = "STOCKINVALIDDAY")
    private Long stockinvalidday;

    @Column(name = "STOCKOUTVALIDDAY")
    private Long stockoutvalidday;

    @Column(name = "CHECKVALIDDAY")
    private Long checkvalidday;

    @Column(name = "CREATEOPID", length = 48)
    private String createopid;

    @Column(name = "CREATEOPNAME", length = 96)
    private String createopname;

    @Column(name = "CREATETIME")
    private Date createtime;

    @Column(name = "LASTMODIOPID", length = 48)
    private String lastmodiopid;

    @Column(name = "LASTMODIOPNAME", length = 96)
    private String lastmodiopname;

    @Column(name = "LASTMODITIME")
    private Date lastmoditime;

    @Column(name = "TRAN_TIME", length = 30)
    private String tranTime;

    @Column(name = "ISHOLIDAY", length = 1)
    private String isholiday;

    @Column(name = "RESTRICTSHOP")
    private Long restrictshop;

    @Column(name = "PACKAGETYPE")
    private Long packagetype;

    @Column(name = "MAINBARCODE", length = 48)
    private String mainbarcode;

    @Column(name = "MAXORDERSPEC", length = 48)
    private String maxorderspec;

    @Column(name = "REDISUPDATESUCCESS", length = 1)
    private String redisupdatesuccess;

    @Column(name = "HOLIDAYENDDATE", length = 8)
    private String holidayenddate;

    @Column(name = "HOLIDAYBEGINDATE", length = 8)
    private String holidaybegindate;

    @Column(name = "HOLIDAYBILLNO", length = 64)
    private String holidaybillno;

    @Column(name = "ISHOTGOODS", length = 1)
    private String ishotgoods;

    @Column(name = "OWN_GOODS", length = 1)
    private String ownGoods;

    @Column(name = "ISDOUBLEGOODS", length = 1)
    private String isdoublegoods;

    @Column(name = "PERCENTAGE_COMM", precision = 23, scale = 8)
    private BigDecimal percentageComm;

    @Column(name = "SPECPRICE", precision = 23, scale = 8)
    private BigDecimal specprice;

    @Column(name = "SPECPROM", length = 1)
    private String specprom;

    @Column(name = "WEIGHPLU", length = 10)
    private String weighplu;

    @Column(name = "WEIGHPLUTYPE", length = 10)
    private String weighplutype;

    @Column(name = "SELFBUILTSHOPID", length = 48)
    private String selfbuiltshopid;

    @Column(name = "SUPPLIER", length = 10)
    private String supplier;

    @Column(name = "PROD_SHOP", length = 1)
    private String prodShop;

    @Column(name = "PROD_HQ", length = 1)
    private String prodHq;

    @Column(name = "PROD_OEM", length = 1)
    private String prodOem;

    @Column(name = "SPEC", length = 200)
    private String spec;

    @Column(name = "PLUID", length = 32)
    private String pluid;

    @Column(name = "ETL_DATETIME")
    private Date etlDatetime;

    @Column(name = "ISSHOWHEADINVENTORY")
    private Long isshowheadinventory;

    @Column(name = "ONSHELFDATE")
    private Date onshelfdate;

    @Column(name = "RUNIT", length = 48)
    private String runit;

    @Column(name = "ISSENSITIVE", length = 1)
    private String issensitive;

    @Column(name = "ISCOMBINEBATCH", length = 1)
    private String iscombinebatch;

    @Column(name = "MATERIALPROPERTIES", length = 1)
    private String materialproperties;

    @Column(name = "PICKUNIT", length = 48)
    private String pickunit;

    @Column(name = "SETTLEMENTPRICE", precision = 23, scale = 8)
    private BigDecimal settlementprice;

    @Column(name = "PURCHASEDAY")
    private Long purchaseday;

    @Column(name = "GOODSTYPE", length = 10)
    private String goodstype;

    @Column(name = "PRODUCER")
    private String producer;

    @Column(name = "STORAGECON")
    private String storagecon;

    @Column(name = "MANUFACTURER")
    private String manufacturer;

    @Column(name = "HOTLINE")
    private String hotline;

    @Column(name = "INGRETABLE")
    private String ingretable;

    @Column(name = "NETCONTENT", length = 20)
    private String netcontent;

    @Column(name = "FOODPROLICNUM")
    private String foodprolicnum;

    @Column(name = "EATINGMETHOD")
    private String eatingmethod;

    @Column(name = "EXSTANDARD")
    private String exstandard;

    @Column(name = "PROPADDRESS")
    private String propaddress;

    @Column(name = "COMMISSIONRATIO", precision = 8, scale = 4)
    private BigDecimal commissionratio;

    @Column(name = "TESTMARKETDAYS")
    private Long testmarketdays;

    @Column(name = "ISMESBATCH", length = 1)
    private String ismesbatch;

    @Column(name = "SHELFLIFEN", length = 32)
    private String shelflifen;

    @Column(name = "TESTMARKETSALEQTY", precision = 23, scale = 8)
    private BigDecimal testmarketsaleqty;

    @Column(name = "TESTMARKETSALEAMT", precision = 23, scale = 8)
    private BigDecimal testmarketsaleamt;

    @Column(name = "TESTMARKETGROSSAMT", precision = 23, scale = 8)
    private BigDecimal testmarketgrossamt;

    @Column(name = "MANUFNO", length = 10)
    private String manufno;

    @Column(name = "PURPRICE", precision = 23, scale = 8)
    private BigDecimal purprice;

}