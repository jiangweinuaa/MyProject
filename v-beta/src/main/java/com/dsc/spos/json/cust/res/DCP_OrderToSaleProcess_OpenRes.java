package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.model.invoiceListResponse;
import com.dsc.spos.model.invoiceShopinfoResponse;

import java.util.List;

public class DCP_OrderToSaleProcess_OpenRes extends JsonRes
{

	private level1 datas;	

	public level1 getDatas()
	{
		return datas;
	}
	public void setDatas(level1 datas)
	{
		this.datas = datas;
	}

	public class level1
	{		
		private String billNo;
		private invoice invoiceInfo;		
		private level2 vipDatas;


		public invoice getInvoiceInfo()
		{
			return invoiceInfo;
		}
		public void setInvoiceInfo(invoice invoiceInfo)
		{
			this.invoiceInfo = invoiceInfo;
		}
		public String getBillNo()
		{
			return billNo;
		}
		public void setBillNo(String billNo)
		{
			this.billNo = billNo;
		}
		public level2 getVipDatas()
		{
			return vipDatas;
		}
		public void setVipDatas(level2 vipDatas)
		{
			this.vipDatas = vipDatas;
		}		

	}

	public class Card
	{
		private String cardNo;
		private String amount;
		private String amount1;
		private String amount2;
		private String getPoint;
		private String point_after;
		private String point_before;
		private String amount_after;
		private String amount_before;
		private String amount1_after;
		private String amount1_before;
		private String amount2_after;
		private String amount2_before;

		public String getCardNo()
		{
			return cardNo;
		}

		public void setCardNo(String cardNo)
		{
			this.cardNo = cardNo;
		}

		public String getAmount()
		{
			return amount;
		}

		public void setAmount(String amount)
		{
			this.amount = amount;
		}

		public String getAmount1()
		{
			return amount1;
		}

		public void setAmount1(String amount1)
		{
			this.amount1 = amount1;
		}

		public String getAmount2()
		{
			return amount2;
		}

		public void setAmount2(String amount2)
		{
			this.amount2 = amount2;
		}

		public String getGetPoint()
		{
			return getPoint;
		}

		public void setGetPoint(String getPoint)
		{
			this.getPoint = getPoint;
		}

		public String getPoint_after()
		{
			return point_after;
		}

		public void setPoint_after(String point_after)
		{
			this.point_after = point_after;
		}

		public String getPoint_before()
		{
			return point_before;
		}

		public void setPoint_before(String point_before)
		{
			this.point_before = point_before;
		}

		public String getAmount_after()
		{
			return amount_after;
		}

		public void setAmount_after(String amount_after)
		{
			this.amount_after = amount_after;
		}

		public String getAmount_before()
		{
			return amount_before;
		}

		public void setAmount_before(String amount_before)
		{
			this.amount_before = amount_before;
		}

		public String getAmount1_after()
		{
			return amount1_after;
		}

		public void setAmount1_after(String amount1_after)
		{
			this.amount1_after = amount1_after;
		}

		public String getAmount1_before()
		{
			return amount1_before;
		}

		public void setAmount1_before(String amount1_before)
		{
			this.amount1_before = amount1_before;
		}

		public String getAmount2_after()
		{
			return amount2_after;
		}

		public void setAmount2_after(String amount2_after)
		{
			this.amount2_after = amount2_after;
		}

		public String getAmount2_before()
		{
			return amount2_before;
		}

		public void setAmount2_before(String amount2_before)
		{
			this.amount2_before = amount2_before;
		}
	}


	public class invoice
	{
		private boolean success;
		private String serviceDescription;
		
		private List<invoiceListResponse> invoiceList;
		
		private invoiceShopinfoResponse shopInfo;
		
		public boolean isSuccess()
		{
			return success;
		}

		public void setSuccess(boolean success)
		{
			this.success = success;
		}

		public String getServiceDescription()
		{
			return serviceDescription;
		}

		public void setServiceDescription(String serviceDescription)
		{
			this.serviceDescription = serviceDescription;
		}

		public List<invoiceListResponse> getInvoiceList()
		{
			return invoiceList;
		}

		public void setInvoiceList(List<invoiceListResponse> invoiceList)
		{
			this.invoiceList = invoiceList;
		}

		public invoiceShopinfoResponse getShopInfo()
		{
			return shopInfo;
		}

		public void setShopInfo(invoiceShopinfoResponse shopInfo)
		{
			this.shopInfo = shopInfo;
		}		
		
		
	}

	public class level2
	{
		private String memberId;
		private String points;//总积分
		private String getPoints;//本次积分
		private String amount;//	
		private List<Card> cardsInfo;
		public String getMemberId()
		{
			return memberId;
		}
		public void setMemberId(String memberId)
		{
			this.memberId = memberId;
		}
		public String getPoints()
		{
			return points;
		}
		public void setPoints(String points)
		{
			this.points = points;
		}
		public String getGetPoints()
		{
			return getPoints;
		}
		public void setGetPoints(String getPoints)
		{
			this.getPoints = getPoints;
		}
		public String getAmount()
		{
			return amount;
		}
		public void setAmount(String amount)
		{
			this.amount = amount;
		}
		public List<Card> getCardsInfo()
		{
			return cardsInfo;
		}
		public void setCardsInfo(List<Card> cardsInfo)
		{
			this.cardsInfo = cardsInfo;
		}	

	}

}
