package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderApplyRefundReq extends JsonBasicReq
{
	private levelRequest request;

	public levelRequest getRequest()
	{
		return request;
	}

	public void setRequest(levelRequest request)
	{
		this.request = request;
	}

	public class levelRequest
	{
		private String eId;
		private String orderNo;
		private String refundType;
		private String shopId;
		private String opNo;
		private String opName;
		
		private String refundReasonNo;
		private String refundReasonName;
		private String refundReason;
		private String tot_amt;
		
		private List<levelGoods> goods;

		public String geteId()
		{
			return eId;
		}

		public void seteId(String eId)
		{
			this.eId = eId;
		}

		public String getOrderNo()
		{
			return orderNo;
		}

		public void setOrderNo(String orderNo)
		{
			this.orderNo = orderNo;
		}

		public String getRefundType()
		{
			return refundType;
		}

		public void setRefundType(String refundType)
		{
			this.refundType = refundType;
		}

		public String getShopId()
		{
			return shopId;
		}

		public void setShopId(String shopId)
		{
			this.shopId = shopId;
		}

		public String getOpNo()
		{
			return opNo;
		}

		public void setOpNo(String opNo)
		{
			this.opNo = opNo;
		}

		public String getOpName()
		{
			return opName;
		}

		public void setOpName(String opName)
		{
			this.opName = opName;
		}

		public String getRefundReasonNo()
		{
			return refundReasonNo;
		}

		public void setRefundReasonNo(String refundReasonNo)
		{
			this.refundReasonNo = refundReasonNo;
		}

		public String getRefundReasonName()
		{
			return refundReasonName;
		}

		public void setRefundReasonName(String refundReasonName)
		{
			this.refundReasonName = refundReasonName;
		}

		public String getRefundReason()
		{
			return refundReason;
		}

		public void setRefundReason(String refundReason)
		{
			this.refundReason = refundReason;
		}

		public String getTot_amt()
		{
			return tot_amt;
		}

		public void setTot_amt(String tot_amt)
		{
			this.tot_amt = tot_amt;
		}

		public List<levelGoods> getGoods()
		{
			return goods;
		}

		public void setGoods(List<levelGoods> goods)
		{
			this.goods = goods;
		}
		
		
	}
	
	public class levelGoods
	{
		private String item;
		private String pluNo;
		private String sUnit;		
		private String featureNo;
		private String specName;
		private String attrName;
		private double oldPrice;
		private double price;
		private double qty;
		private double amt;
		
		/**
		 * 是否赠品（Y/N）
		 */
		private String gift;
		
		/**
		 * 套餐类型1、正常商品 2、套餐主商品  3、套餐子商品
		 */
		private String packageType;
		
		/**
		 * 套餐主商品项次	
		 */
		private String packageMitem;

		public String getItem()
		{
			return item;
		}

		public void setItem(String item)
		{
			this.item = item;
		}

		public String getPluNo()
		{
			return pluNo;
		}

		public void setPluNo(String pluNo)
		{
			this.pluNo = pluNo;
		}

		public String getsUnit()
		{
			return sUnit;
		}

		public void setsUnit(String sUnit)
		{
			this.sUnit = sUnit;
		}

		public String getFeatureNo()
		{
			return featureNo;
		}

		public void setFeatureNo(String featureNo)
		{
			this.featureNo = featureNo;
		}

		public String getSpecName()
		{
			return specName;
		}

		public void setSpecName(String specName)
		{
			this.specName = specName;
		}

		public String getAttrName()
		{
			return attrName;
		}

		public void setAttrName(String attrName)
		{
			this.attrName = attrName;
		}

		public double getOldPrice()
		{
			return oldPrice;
		}

		public void setOldPrice(double oldPrice)
		{
			this.oldPrice = oldPrice;
		}

		public double getPrice()
		{
			return price;
		}

		public void setPrice(double price)
		{
			this.price = price;
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

		public String getGift()
		{
			return gift;
		}

		public void setGift(String gift)
		{
			this.gift = gift;
		}

		public String getPackageType()
		{
			return packageType;
		}

		public void setPackageType(String packageType)
		{
			this.packageType = packageType;
		}

		public String getPackageMitem()
		{
			return packageMitem;
		}

		public void setPackageMitem(String packageMitem)
		{
			this.packageMitem = packageMitem;
		}
		
		
		
	}

}
