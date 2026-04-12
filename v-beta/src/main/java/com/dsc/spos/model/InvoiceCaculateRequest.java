package com.dsc.spos.model;
import java.util.List;

/**
 * 发票失算接口POS_InvoiceCaculate_Open请求
 * @author 86187
 *
 */
public class InvoiceCaculateRequest {

   private String saleType;
   private String saleNo;
   private String shopId;
   private String oprType;
   private String freeCode;
   private String passport;
   private String invSplitType;
   private List<InvoiceList> invoiceList;
   private List<GoodsList> goodsList;
   private List<PayList> payList;
   public void setSaleType(String saleType) {
        this.saleType = saleType;
    }
    public String getSaleType() {
        return saleType;
    }

   public void setSaleNo(String saleNo) {
        this.saleNo = saleNo;
    }
    public String getSaleNo() {
        return saleNo;
    }

   public void setShopId(String shopId) {
        this.shopId = shopId;
    }
    public String getShopId() {
        return shopId;
    }

   public void setOprType(String oprType) {
        this.oprType = oprType;
    }
    public String getOprType() {
        return oprType;
    }

   public void setFreeCode(String freeCode) {
        this.freeCode = freeCode;
    }
    public String getFreeCode() {
        return freeCode;
    }

   public void setPassport(String passport) {
        this.passport = passport;
    }
    public String getPassport() {
        return passport;
    }

   public void setInvSplitType(String invSplitType) {
        this.invSplitType = invSplitType;
    }
    public String getInvSplitType() {
        return invSplitType;
    }

   public void setInvoiceList(List<InvoiceList> invoiceList) {
        this.invoiceList = invoiceList;
    }
    public List<InvoiceList> getInvoiceList() {
        return invoiceList;
    }

   public void setGoodsList(List<GoodsList> goodsList) {
        this.goodsList = goodsList;
    }
    public List<GoodsList> getGoodsList() {
        return goodsList;
    }

   public void setPayList(List<PayList> payList) {
        this.payList = payList;
    }
    public List<PayList> getPayList() {
        return payList;
    }
    
    
  
   public class InvoiceList {

       private String item;
       private String invNo;
       private String invMemo;
       private String carrierCode;
       private String loveCode;
       private String buyerGuiNo;
       public void setItem(String item) {
            this.item = item;
        }
        public String getItem() {
            return item;
        }

       public void setInvNo(String invNo) {
            this.invNo = invNo;
        }
        public String getInvNo() {
            return invNo;
        }

       public void setInvMemo(String invMemo) {
            this.invMemo = invMemo;
        }
        public String getInvMemo() {
            return invMemo;
        }

       public void setCarrierCode(String carrierCode) {
            this.carrierCode = carrierCode;
        }
        public String getCarrierCode() {
            return carrierCode;
        }

       public void setLoveCode(String loveCode) {
            this.loveCode = loveCode;
        }
        public String getLoveCode() {
            return loveCode;
        }

       public void setBuyerGuiNo(String buyerGuiNo) {
            this.buyerGuiNo = buyerGuiNo;
        }
        public String getBuyerGuiNo() {
            return buyerGuiNo;
        }

   }
   

  public class GoodsList {

      private String invItem;
      private String invNo;
      private String item;
      private String mItem;
      private String pluNo;
      private String pluName;
      private String barcode;
      private double taxRate;
      private String taxCode;
      private double qty;
      private double amt;
      public void setInvItem(String invItem) {
           this.invItem = invItem;
       }
       public String getInvItem() {
           return invItem;
       }

      public void setInvNo(String invNo) {
           this.invNo = invNo;
       }
       public String getInvNo() {
           return invNo;
       }

      public void setItem(String item) {
           this.item = item;
       }
       public String getItem() {
           return item;
       }

      public void setMItem(String mItem) {
           this.mItem = mItem;
       }
       public String getMItem() {
           return mItem;
       }

      public void setPluNo(String pluNo) {
           this.pluNo = pluNo;
       }
       public String getPluNo() {
           return pluNo;
       }

      public void setPluName(String pluName) {
           this.pluName = pluName;
       }
       public String getPluName() {
           return pluName;
       }

      public void setBarcode(String barcode) {
           this.barcode = barcode;
       }
       public String getBarcode() {
           return barcode;
       }

      public void setTaxRate(double taxRate) {
           this.taxRate = taxRate;
       }
       public double getTaxRate() {
           return taxRate;
       }

      public void setTaxCode(String taxCode) {
           this.taxCode = taxCode;
       }
       public String getTaxCode() {
           return taxCode;
       }

      public void setQty(double qty) {
           this.qty = qty;
       }
       public double getQty() {
           return qty;
       }

      public void setAmt(double amt) {
           this.amt = amt;
       }
       public double getAmt() {
           return amt;
       }

  }
  
 
 public class PayList {

	 private String payType;
     private String payCode;
     private String payName;
     private double payAmt;
     private String isOrderPay;
     private String canInvoice;
     private double extra;
     private double change;
     
     public String getPayType()
	{
		return payType;
	}
	public void setPayType(String payType)
	{
		this.payType = payType;
	}
	public void setPayCode(String payCode) {
          this.payCode = payCode;
      }
      public String getPayCode() {
          return payCode;
      }

     public void setPayName(String payName) {
          this.payName = payName;
      }
      public String getPayName() {
          return payName;
      }

     public void setPayAmt(double payAmt) {
          this.payAmt = payAmt;
      }
      public double getPayAmt() {
          return payAmt;
      }

     public void setIsOrderPay(String isOrderPay) {
          this.isOrderPay = isOrderPay;
      }
      public String getIsOrderPay() {
          return isOrderPay;
      }

     public void setCanInvoice(String canInvoice) {
          this.canInvoice = canInvoice;
      }
      public String getCanInvoice() {
          return canInvoice;
      }

     public void setExtra(double extra) {
          this.extra = extra;
      }
      public double getExtra() {
          return extra;
      }

     public void setChange(double change) {
          this.change = change;
      }
      public double getChange() {
          return change;
      }

 }

}