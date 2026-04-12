package com.dsc.spos.model;

import java.math.BigDecimal;

/**
 * 商品明细
 */
public class JindieGoodsDetail
{

    private  int item;//项次
    private  int oItem;//来源项次
    private BigDecimal oQty;//来源数量
    private  String pluNo;//品号
    private  String unitId;//单位编码
    private  BigDecimal qty;//数量
    private  BigDecimal oldPrice;//原价
    private  BigDecimal price;//单价
    private  BigDecimal disc;//折扣额
    private  BigDecimal amt;//成交额
    private  BigDecimal oldAmt;//原价金额
    private  String memo;//备注

    public int getItem()
    {
        return item;
    }

    public void setItem(int item)
    {
        this.item = item;
    }

    public int getoItem()
    {
        return oItem;
    }

    public void setoItem(int oItem)
    {
        this.oItem = oItem;
    }

    public BigDecimal getoQty()
    {
        return oQty;
    }

    public void setoQty(BigDecimal oQty)
    {
        this.oQty = oQty;
    }

    public String getPluNo()
    {
        return pluNo;
    }

    public void setPluNo(String pluNo)
    {
        this.pluNo = pluNo;
    }

    public String getUnitId()
    {
        return unitId;
    }

    public void setUnitId(String unitId)
    {
        this.unitId = unitId;
    }

    public BigDecimal getQty()
    {
        return qty;
    }

    public void setQty(BigDecimal qty)
    {
        this.qty = qty;
    }

    public BigDecimal getOldPrice()
    {
        return oldPrice;
    }

    public void setOldPrice(BigDecimal oldPrice)
    {
        this.oldPrice = oldPrice;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public BigDecimal getDisc()
    {
        return disc;
    }

    public void setDisc(BigDecimal disc)
    {
        this.disc = disc;
    }

    public BigDecimal getAmt()
    {
        return amt;
    }

    public void setAmt(BigDecimal amt)
    {
        this.amt = amt;
    }

    public BigDecimal getOldAmt()
    {
        return oldAmt;
    }

    public void setOldAmt(BigDecimal oldAmt)
    {
        this.oldAmt = oldAmt;
    }

    public String getMemo()
    {
        return memo;
    }

    public void setMemo(String memo)
    {
        this.memo = memo;
    }


}
