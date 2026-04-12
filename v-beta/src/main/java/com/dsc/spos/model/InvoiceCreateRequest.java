package com.dsc.spos.model;
import java.util.List;

public class InvoiceCreateRequest {

   private String orgId;
   private String recipient;
   private String machineId;
   private String channelId;
   private String opNo;
   private String saleType;
   private String saleNo;
   private String freeCode;
   private String passport;
   private String invCount;
   private String invSplitType;
   private List<InvoiceList> invoiceList;
   private List<GoodsList> goodsList;
   private List<PayList> payList;
   public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
    public String getOrgId() {
        return orgId;
    }

   public String getRecipient()
	{
		return recipient;
	}
	public void setRecipient(String recipient)
	{
		this.recipient = recipient;
	}
public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
    public String getMachineId() {
        return machineId;
    }

   public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
    public String getChannelId() {
        return channelId;
    }

   public void setOpNo(String opNo) {
        this.opNo = opNo;
    }
    public String getOpNo() {
        return opNo;
    }

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

   public void setInvCount(String invCount) {
        this.invCount = invCount;
    }
    public String getInvCount() {
        return invCount;
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

       private String invItem;
       private String invNo;
       private String bDate;
       private String recordType;
       private String taxationType;
       private String invType;
       private String invFormat;
       private String invMemo;
       private String sellerGuiNo;
       private String buyerGuiNo;
       private String isEInvoice;
       private String carrierCode;
       private String carrierShowId;
       private String carrierHiddenId;
       private String loveCode;
       private String randomCode;
       private String rebateNo;
       private String rebatAmt;
       private String invalidOP;
       private String invalidReason;
       private String invalidReasonName;
       private String oSDate;
       private String leftQrStr;
       private String rightQrStr;
       private String barcodeStr;
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

       public void setBDate(String bDate) {
            this.bDate = bDate;
        }
        public String getBDate() {
            return bDate;
        }

       public void setRecordType(String recordType) {
            this.recordType = recordType;
        }
        public String getRecordType() {
            return recordType;
        }

       public void setTaxationType(String taxationType) {
            this.taxationType = taxationType;
        }
        public String getTaxationType() {
            return taxationType;
        }

       public void setInvType(String invType) {
            this.invType = invType;
        }
        public String getInvType() {
            return invType;
        }

       public void setInvFormat(String invFormat) {
            this.invFormat = invFormat;
        }
        public String getInvFormat() {
            return invFormat;
        }

       public void setInvMemo(String invMemo) {
            this.invMemo = invMemo;
        }
        public String getInvMemo() {
            return invMemo;
        }

       public void setSellerGuiNo(String sellerGuiNo) {
            this.sellerGuiNo = sellerGuiNo;
        }
        public String getSellerGuiNo() {
            return sellerGuiNo;
        }

       public void setBuyerGuiNo(String buyerGuiNo) {
            this.buyerGuiNo = buyerGuiNo;
        }
        public String getBuyerGuiNo() {
            return buyerGuiNo;
        }

       public void setIsEInvoice(String isEInvoice) {
            this.isEInvoice = isEInvoice;
        }
        public String getIsEInvoice() {
            return isEInvoice;
        }

       public void setCarrierCode(String carrierCode) {
            this.carrierCode = carrierCode;
        }
        public String getCarrierCode() {
            return carrierCode;
        }

       public void setCarrierShowId(String carrierShowId) {
            this.carrierShowId = carrierShowId;
        }
        public String getCarrierShowId() {
            return carrierShowId;
        }

       public void setCarrierHiddenId(String carrierHiddenId) {
            this.carrierHiddenId = carrierHiddenId;
        }
        public String getCarrierHiddenId() {
            return carrierHiddenId;
        }

       public void setLoveCode(String loveCode) {
            this.loveCode = loveCode;
        }
        public String getLoveCode() {
            return loveCode;
        }

       public void setRandomCode(String randomCode) {
            this.randomCode = randomCode;
        }
        public String getRandomCode() {
            return randomCode;
        }

       public void setRebateNo(String rebateNo) {
            this.rebateNo = rebateNo;
        }
        public String getRebateNo() {
            return rebateNo;
        }

       public void setRebatAmt(String rebatAmt) {
            this.rebatAmt = rebatAmt;
        }
        public String getRebatAmt() {
            return rebatAmt;
        }

       public void setInvalidOP(String invalidOP) {
            this.invalidOP = invalidOP;
        }
        public String getInvalidOP() {
            return invalidOP;
        }

       public void setInvalidReason(String invalidReason) {
            this.invalidReason = invalidReason;
        }
        public String getInvalidReason() {
            return invalidReason;
        }

       public void setInvalidReasonName(String invalidReasonName) {
            this.invalidReasonName = invalidReasonName;
        }
        public String getInvalidReasonName() {
            return invalidReasonName;
        }

       public void setOSDate(String oSDate) {
            this.oSDate = oSDate;
        }
        public String getOSDate() {
            return oSDate;
        }

       public void setLeftQrStr(String leftQrStr) {
            this.leftQrStr = leftQrStr;
        }
        public String getLeftQrStr() {
            return leftQrStr;
        }

       public void setRightQrStr(String rightQrStr) {
            this.rightQrStr = rightQrStr;
        }
        public String getRightQrStr() {
            return rightQrStr;
        }

       public void setBarcodeStr(String barcodeStr) {
            this.barcodeStr = barcodeStr;
        }
        public String getBarcodeStr() {
            return barcodeStr;
        }

   }
   

  public class GoodsList {

      private String invItem;
      private String invNo;
      private String oItem;
      private String pluNo;
      private String pluName;
      private String inclTax;
      private String taxCode;
      private String taxType;
      private String taxRate;
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

      public void setOItem(String oItem) {
           this.oItem = oItem;
       }
       public String getOItem() {
           return oItem;
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

      public void setInclTax(String inclTax) {
           this.inclTax = inclTax;
       }
       public String getInclTax() {
           return inclTax;
       }

      public void setTaxCode(String taxCode) {
           this.taxCode = taxCode;
       }
       public String getTaxCode() {
           return taxCode;
       }

      public void setTaxType(String taxType) {
           this.taxType = taxType;
       }
       public String getTaxType() {
           return taxType;
       }

      public void setTaxRate(String taxRate) {
           this.taxRate = taxRate;
       }
       public String getTaxRate() {
           return taxRate;
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
     private String payCodeErp;
     private double payAmt;
     private double sendPayAmt;
     private String isOrderPay;
     private String isTurnover;
     private String canOpenInvoice;
     private String ctType;
     private String ctId;
     private String taxCode;
     private String taxType;
     private String taxRate;
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

     public void setPayCodeErp(String payCodeErp) {
          this.payCodeErp = payCodeErp;
      }
      public String getPayCodeErp() {
          return payCodeErp;
      }

     public void setPayAmt(double payAmt) {
          this.payAmt = payAmt;
      }
      public double getPayAmt() {
          return payAmt;
      }

     public void setSendPayAmt(double sendPayAmt) {
          this.sendPayAmt = sendPayAmt;
      }
      public double getSendPayAmt() {
          return sendPayAmt;
      }

     public void setIsOrderPay(String isOrderPay) {
          this.isOrderPay = isOrderPay;
      }
      public String getIsOrderPay() {
          return isOrderPay;
      }

     public void setIsTurnover(String isTurnover) {
          this.isTurnover = isTurnover;
      }
      public String getIsTurnover() {
          return isTurnover;
      }

     public void setCanOpenInvoice(String canOpenInvoice) {
          this.canOpenInvoice = canOpenInvoice;
      }
      public String getCanOpenInvoice() {
          return canOpenInvoice;
      }

     public void setCtType(String ctType) {
          this.ctType = ctType;
      }
      public String getCtType() {
          return ctType;
      }

     public void setCtId(String ctId) {
          this.ctId = ctId;
      }
      public String getCtId() {
          return ctId;
      }

     public void setTaxCode(String taxCode) {
          this.taxCode = taxCode;
      }
      public String getTaxCode() {
          return taxCode;
      }

     public void setTaxType(String taxType) {
          this.taxType = taxType;
      }
      public String getTaxType() {
          return taxType;
      }

     public void setTaxRate(String taxRate) {
          this.taxRate = taxRate;
      }
      public String getTaxRate() {
          return taxRate;
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
