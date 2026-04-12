package com.dsc.spos.waimai.entity;

import java.io.Serializable;

public class orderGoodsItemAgio implements Serializable
{
    private String item;

    private double qty;

    private double amt;

    private double inputDisc;

    private double realDisc;

    private double disc;
    
    private double disc_merReceive;
    
    private double disc_custPayReal;

    private String dcType="";

    private String dcTypeName="";

    private String pmtNo;

    private String giftCtf;

    private String giftCtfNo;

    private String bsNo;

	public String getItem()
	{
		return item;
	}

	public void setItem(String item)
	{
		this.item = item;
	}

	public double getQty()
	{
		return qty;
	}

	public void setQty(double qty)
	{
		this.qty = qty;
	}

	public double getAmt()
	{
		return amt;
	}

	public void setAmt(double amt)
	{
		this.amt = amt;
	}

	public double getInputDisc()
	{
		return inputDisc;
	}

	public void setInputDisc(double inputDisc)
	{
		this.inputDisc = inputDisc;
	}

	public double getRealDisc()
	{
		return realDisc;
	}

	public void setRealDisc(double realDisc)
	{
		this.realDisc = realDisc;
	}

	public double getDisc()
	{
		return disc;
	}

	public void setDisc(double disc)
	{
		this.disc = disc;
	}

	public String getDcType()
	{
		return dcType;
	}

	public void setDcType(String dcType)
	{
		this.dcType = dcType;
	}

	public String getDcTypeName()
	{
		return dcTypeName;
	}

	public void setDcTypeName(String dcTypeName)
	{
		this.dcTypeName = dcTypeName;
	}

	public String getPmtNo()
	{
		return pmtNo;
	}

	public void setPmtNo(String pmtNo)
	{
		this.pmtNo = pmtNo;
	}

	public String getGiftCtf()
	{
		return giftCtf;
	}

	public void setGiftCtf(String giftCtf)
	{
		this.giftCtf = giftCtf;
	}

	public String getGiftCtfNo()
	{
		return giftCtfNo;
	}

	public void setGiftCtfNo(String giftCtfNo)
	{
		this.giftCtfNo = giftCtfNo;
	}

	public String getBsNo()
	{
		return bsNo;
	}

	public void setBsNo(String bsNo)
	{
		this.bsNo = bsNo;
	}

	public double getDisc_merReceive()
	{
		return disc_merReceive;
	}

	public void setDisc_merReceive(double disc_merReceive)
	{
		this.disc_merReceive = disc_merReceive;
	}

	public double getDisc_custPayReal()
	{
		return disc_custPayReal;
	}

	public void setDisc_custPayReal(double disc_custPayReal)
	{
		this.disc_custPayReal = disc_custPayReal;
	}

}
