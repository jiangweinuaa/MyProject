package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;


public class DCP_GoodsSetExQueryRes extends JsonRes
{

	private List<level1Elm> datas;//参考天气

	public List<level1Elm> getDatas() 
	{
		return datas;
	}

	public void setDatas(List<level1Elm> datas) 
	{
		this.datas = datas;
	}
	
	public class level2Elm
	{
		private String shopId;
		private String pluno;
		private String material_pluno;
		private String material_pluName;
		private String qty;
		private String material_qty;
		private String loss_rate;
		private String SNONAME;
		private String SNO;
		private String UPSNO;
		private String SHORTCUT_CODE;
		

	public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
	public String getPluno() {
		return pluno;
	}
	public void setPluno(String pluno) {
		this.pluno = pluno;
	}
	public String getMaterial_pluno() {
		return material_pluno;
	}
	public void setMaterial_pluno(String material_pluno) {
		this.material_pluno = material_pluno;
	}
	public String getMaterial_pluName() {
		return material_pluName;
	}
	public void setMaterial_pluName(String material_pluName) {
		this.material_pluName = material_pluName;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getMaterial_qty() {
		return material_qty;
	}
	public void setMaterial_qty(String material_qty) {
		this.material_qty = material_qty;
	}
	public String getLoss_rate() {
		return loss_rate;
	}
	public void setLoss_rate(String loss_rate) {
		this.loss_rate = loss_rate;
	}
	public String getSNONAME() {
	return SNONAME;
	}
	public void setSNONAME(String sNONAME) {
	SNONAME = sNONAME;
	}
	public String getSNO() {
	return SNO;
	}
	public void setSNO(String sNO) {
	SNO = sNO;
	}
	public String getUPSNO() {
	return UPSNO;
	}
	public void setUPSNO(String uPSNO) {
	UPSNO = uPSNO;
	}
	public String getSHORTCUT_CODE() {
	return SHORTCUT_CODE;
	}
	public void setSHORTCUT_CODE(String sHORTCUT_CODE) {
	SHORTCUT_CODE = sHORTCUT_CODE;
	}
		
	}
	
	public class level1Elm
	{
		private String PLUNO;
		private String PLUNAME;
		private String SPEC;
		private String SHORTCUT_CODE;
		private String PUNIT;
		private String SUNIT;
		private String CUNIT;
		private String WUNIT;
		private String SNONAME;
		private String SNO;
		private String UPSNO;
		private String status;
		
		private List<TB_PRICE_SHOP> TB_PRICE_SHOP;
		private List<level2Elm> boomList;
		
	public String getPLUNO() {
		return PLUNO;
	}
	public void setPLUNO(String pLUNO) {
		PLUNO = pLUNO;
	}
	public String getPLUNAME() {
		return PLUNAME;
	}
	public void setPLUNAME(String pLUNAME) {
		PLUNAME = pLUNAME;
	}
	public String getSPEC() {
		return SPEC;
	}
	public void setSPEC(String sPEC) {
		SPEC = sPEC;
	}
	public String getSHORTCUT_CODE() {
		return SHORTCUT_CODE;
	}
	public void setSHORTCUT_CODE(String sHORTCUT_CODE) {
		SHORTCUT_CODE = sHORTCUT_CODE;
	}
	public String getPUNIT() {
		return PUNIT;
	}
	public void setPUNIT(String pUNIT) {
		PUNIT = pUNIT;
	}
	public String getSUNIT() {
		return SUNIT;
	}
	public void setSUNIT(String sUNIT) {
		SUNIT = sUNIT;
	}
	public String getCUNIT() {
		return CUNIT;
	}
	public void setCUNIT(String cUNIT) {
		CUNIT = cUNIT;
	}
	public String getWUNIT() {
		return WUNIT;
	}
	public void setWUNIT(String wUNIT) {
		WUNIT = wUNIT;
	}
	public List<TB_PRICE_SHOP> getTB_PRICE_SHOP() {
		return TB_PRICE_SHOP;
	}
	public void setTB_PRICE_SHOP(List<TB_PRICE_SHOP> tB_PRICE_SHOP) {
		TB_PRICE_SHOP = tB_PRICE_SHOP;
	}
	public List<level2Elm> getBoomList() {
		return boomList;
	}
	public void setBoomList(List<level2Elm> boomList) {
		this.boomList = boomList;
	}
	public String getSNONAME() {
	return SNONAME;
	}
	public void setSNONAME(String sNONAME) {
	SNONAME = sNONAME;
	}
	public String getSNO() {
	return SNO;
	}
	public void setSNO(String sNO) {
	SNO = sNO;
	}
	public String getUPSNO() {
	return UPSNO;
	}
	public void setUPSNO(String uPSNO) {
	UPSNO = uPSNO;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

		
	}

	public class TB_PRICE_SHOP
	{
		private String UNIT;		
		private String UNIT_NAME;		
		private String PRICE1;//零售价		
		private String PRICE2;//底价		
		private String PRICE3;//最高退价		
		private String EFFDATE;//生效日期		
		private String LEDATE;//失效日期		
		private String OPENPRICE;		
		private String ISDISCOUNT;		
		private String status;		
		private String shopId;	
		

		public String getUNIT_NAME() {
			return UNIT_NAME;
		}
		public void setUNIT_NAME(String uNIT_NAME) {
			UNIT_NAME = uNIT_NAME;
		}
		public String getUNIT() {
			return UNIT;
		}
		public void setUNIT(String uNIT) {
			UNIT = uNIT;
		}
		public String getPRICE1() {
			return PRICE1;
		}
		public void setPRICE1(String pRICE1) {
			PRICE1 = pRICE1;
		}
		public String getPRICE2() {
			return PRICE2;
		}
		public void setPRICE2(String pRICE2) {
			PRICE2 = pRICE2;
		}
		public String getPRICE3() {
			return PRICE3;
		}
		public void setPRICE3(String pRICE3) {
			PRICE3 = pRICE3;
		}
		public String getEFFDATE() {
			return EFFDATE;
		}
		public void setEFFDATE(String eFFDATE) {
			EFFDATE = eFFDATE;
		}
		public String getLEDATE() {
			return LEDATE;
		}
		public void setLEDATE(String lEDATE) {
			LEDATE = lEDATE;
		}
		public String getOPENPRICE() {
			return OPENPRICE;
		}
		public void setOPENPRICE(String oPENPRICE) {
			OPENPRICE = oPENPRICE;
		}
		public String getISDISCOUNT() {
			return ISDISCOUNT;
		}
		public void setISDISCOUNT(String iSDISCOUNT) {
			ISDISCOUNT = iSDISCOUNT;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}



	}


}
