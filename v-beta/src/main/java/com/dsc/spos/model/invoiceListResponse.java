package com.dsc.spos.model;

import java.util.List;

/**
 * 开发票返回的单头
 */
public class invoiceListResponse
{
    private double accumAmt;
    private double accumTax;
    private String barcodeStr;
    private String bdate;
    private String belfirm;
    private String billNo;
    private String buyerGuiNo;
    private String carrierCode;
    private String carrierHiddenId;
    private String carrierShowId;
    private String carrierType;
    private String charityName;
    private String eId;
    private double endMonth;
    private double freeTaxAmt;
    private double giftExtraAmt;
    private double giftExtraTax;
    private double giftInvAmt;
    private double giftInvTax;
    private double invAmt;
    private String invFno;
    private String invFormat;
    private double invFreeAmt;
    private String invMemo;
    private String invNo;
    private int invOperateType;
    private int invSource;
    private int invSplitType;
    private double invTax;
    private double invTotAmt;
    private String invType;
    private String invType2c;
    private double invUAmt;
    private double invZeroAmt;
    private boolean invalidAllowed;
    private String invalidCode;
    private String invalidDate;
    private String invalidName;
    private String invalidOP;
    private String invalidTime;
    private String invoiceDate;
    private String invoiceTime;
    private String isEInvoice;
    private String isInvalid;
    private int item;
    private String leftQrStr;
    private String loveCode;
    private String oSDate;
    private double orderPayAmt;
    private double orderPayTax;
    private double orderUnTaxPayAmt;
    private double orderUnTaxPayTax;
    private double payAmt;
    private String platformResult;
    private int printCount;
    private boolean printDetails;
    private String randomCode;
    private double rebateAmt;
    private String rebateNo;
    private String rightQrStr;
    private String saleNo;
    private String saleType;
    private String sellerGuiNo;
    private String shopId;
    private int startMonth;
    private double taxRate;
    private double taxableAmt;
    private String taxationType;
    private double totAmt;
    private double totSaleAmt;
    private double totSaleTax;
    private double totSaleUAmt;
    private double unTaxPayAmt;
    private double unTaxPayTax;
    private int year;
    private double zeroTaxAmt;

    private List<invoiceDetailListResponse> detailList;

    public double getAccumAmt()
    {
        return accumAmt;
    }

    public void setAccumAmt(double accumAmt)
    {
        this.accumAmt = accumAmt;
    }

    public double getAccumTax()
    {
        return accumTax;
    }

    public void setAccumTax(double accumTax)
    {
        this.accumTax = accumTax;
    }

    public String getBarcodeStr()
    {
        return barcodeStr;
    }

    public void setBarcodeStr(String barcodeStr)
    {
        this.barcodeStr = barcodeStr;
    }

    public String getBdate()
    {
        return bdate;
    }

    public void setBdate(String bdate)
    {
        this.bdate = bdate;
    }

    public String getBelfirm()
    {
        return belfirm;
    }

    public void setBelfirm(String belfirm)
    {
        this.belfirm = belfirm;
    }

    public String getBillNo()
    {
        return billNo;
    }

    public void setBillNo(String billNo)
    {
        this.billNo = billNo;
    }

    public String getBuyerGuiNo()
    {
        return buyerGuiNo;
    }

    public void setBuyerGuiNo(String buyerGuiNo)
    {
        this.buyerGuiNo = buyerGuiNo;
    }

    public String getCarrierCode()
    {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode)
    {
        this.carrierCode = carrierCode;
    }

    public String getCarrierHiddenId()
    {
        return carrierHiddenId;
    }

    public void setCarrierHiddenId(String carrierHiddenId)
    {
        this.carrierHiddenId = carrierHiddenId;
    }

    public String getCarrierShowId()
    {
        return carrierShowId;
    }

    public void setCarrierShowId(String carrierShowId)
    {
        this.carrierShowId = carrierShowId;
    }

    public String getCarrierType()
    {
        return carrierType;
    }

    public void setCarrierType(String carrierType)
    {
        this.carrierType = carrierType;
    }

    public String getCharityName()
    {
        return charityName;
    }

    public void setCharityName(String charityName)
    {
        this.charityName = charityName;
    }

    public String geteId()
    {
        return eId;
    }

    public void seteId(String eId)
    {
        this.eId = eId;
    }

    public double getEndMonth()
    {
        return endMonth;
    }

    public void setEndMonth(double endMonth)
    {
        this.endMonth = endMonth;
    }

    public double getFreeTaxAmt()
    {
        return freeTaxAmt;
    }

    public void setFreeTaxAmt(double freeTaxAmt)
    {
        this.freeTaxAmt = freeTaxAmt;
    }

    public double getGiftExtraAmt()
    {
        return giftExtraAmt;
    }

    public void setGiftExtraAmt(double giftExtraAmt)
    {
        this.giftExtraAmt = giftExtraAmt;
    }

    public double getGiftExtraTax()
    {
        return giftExtraTax;
    }

    public void setGiftExtraTax(double giftExtraTax)
    {
        this.giftExtraTax = giftExtraTax;
    }

    public double getGiftInvAmt()
    {
        return giftInvAmt;
    }

    public void setGiftInvAmt(double giftInvAmt)
    {
        this.giftInvAmt = giftInvAmt;
    }

    public double getGiftInvTax()
    {
        return giftInvTax;
    }

    public void setGiftInvTax(double giftInvTax)
    {
        this.giftInvTax = giftInvTax;
    }

    public double getInvAmt()
    {
        return invAmt;
    }

    public void setInvAmt(double invAmt)
    {
        this.invAmt = invAmt;
    }

    public String getInvFno()
    {
        return invFno;
    }

    public void setInvFno(String invFno)
    {
        this.invFno = invFno;
    }

    public String getInvFormat()
    {
        return invFormat;
    }

    public void setInvFormat(String invFormat)
    {
        this.invFormat = invFormat;
    }

    public double getInvFreeAmt()
    {
        return invFreeAmt;
    }

    public void setInvFreeAmt(double invFreeAmt)
    {
        this.invFreeAmt = invFreeAmt;
    }

    public String getInvMemo()
    {
        return invMemo;
    }

    public void setInvMemo(String invMemo)
    {
        this.invMemo = invMemo;
    }

    public String getInvNo()
    {
        return invNo;
    }

    public void setInvNo(String invNo)
    {
        this.invNo = invNo;
    }

    public int getInvOperateType()
    {
        return invOperateType;
    }

    public void setInvOperateType(int invOperateType)
    {
        this.invOperateType = invOperateType;
    }

    public int getInvSource()
    {
        return invSource;
    }

    public void setInvSource(int invSource)
    {
        this.invSource = invSource;
    }

    public int getInvSplitType()
    {
        return invSplitType;
    }

    public void setInvSplitType(int invSplitType)
    {
        this.invSplitType = invSplitType;
    }

    public double getInvTax()
    {
        return invTax;
    }

    public void setInvTax(double invTax)
    {
        this.invTax = invTax;
    }

    public double getInvTotAmt()
    {
        return invTotAmt;
    }

    public void setInvTotAmt(double invTotAmt)
    {
        this.invTotAmt = invTotAmt;
    }

    public String getInvType()
    {
        return invType;
    }

    public void setInvType(String invType)
    {
        this.invType = invType;
    }

    public String getInvType2c()
    {
        return invType2c;
    }

    public void setInvType2c(String invType2c)
    {
        this.invType2c = invType2c;
    }

    public double getInvUAmt()
    {
        return invUAmt;
    }

    public void setInvUAmt(double invUAmt)
    {
        this.invUAmt = invUAmt;
    }

    public double getInvZeroAmt()
    {
        return invZeroAmt;
    }

    public void setInvZeroAmt(double invZeroAmt)
    {
        this.invZeroAmt = invZeroAmt;
    }

    public boolean isInvalidAllowed()
    {
        return invalidAllowed;
    }

    public void setInvalidAllowed(boolean invalidAllowed)
    {
        this.invalidAllowed = invalidAllowed;
    }

    public String getInvalidCode()
    {
        return invalidCode;
    }

    public void setInvalidCode(String invalidCode)
    {
        this.invalidCode = invalidCode;
    }

    public String getInvalidDate()
    {
        return invalidDate;
    }

    public void setInvalidDate(String invalidDate)
    {
        this.invalidDate = invalidDate;
    }

    public String getInvalidName()
    {
        return invalidName;
    }

    public void setInvalidName(String invalidName)
    {
        this.invalidName = invalidName;
    }

    public String getInvalidOP()
    {
        return invalidOP;
    }

    public void setInvalidOP(String invalidOP)
    {
        this.invalidOP = invalidOP;
    }

    public String getInvalidTime()
    {
        return invalidTime;
    }

    public void setInvalidTime(String invalidTime)
    {
        this.invalidTime = invalidTime;
    }

    public String getInvoiceDate()
    {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate)
    {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceTime()
    {
        return invoiceTime;
    }

    public void setInvoiceTime(String invoiceTime)
    {
        this.invoiceTime = invoiceTime;
    }

    public String getIsEInvoice()
    {
        return isEInvoice;
    }

    public void setIsEInvoice(String isEInvoice)
    {
        this.isEInvoice = isEInvoice;
    }

    public String getIsInvalid()
    {
        return isInvalid;
    }

    public void setIsInvalid(String isInvalid)
    {
        this.isInvalid = isInvalid;
    }

    public int getItem()
    {
        return item;
    }

    public void setItem(int item)
    {
        this.item = item;
    }

    public String getLeftQrStr()
    {
        return leftQrStr;
    }

    public void setLeftQrStr(String leftQrStr)
    {
        this.leftQrStr = leftQrStr;
    }

    public String getLoveCode()
    {
        return loveCode;
    }

    public void setLoveCode(String loveCode)
    {
        this.loveCode = loveCode;
    }

    public String getoSDate()
    {
        return oSDate;
    }

    public void setoSDate(String oSDate)
    {
        this.oSDate = oSDate;
    }

    public double getOrderPayAmt()
    {
        return orderPayAmt;
    }

    public void setOrderPayAmt(double orderPayAmt)
    {
        this.orderPayAmt = orderPayAmt;
    }

    public double getOrderPayTax()
    {
        return orderPayTax;
    }

    public void setOrderPayTax(double orderPayTax)
    {
        this.orderPayTax = orderPayTax;
    }

    public double getOrderUnTaxPayAmt()
    {
        return orderUnTaxPayAmt;
    }

    public void setOrderUnTaxPayAmt(double orderUnTaxPayAmt)
    {
        this.orderUnTaxPayAmt = orderUnTaxPayAmt;
    }

    public double getOrderUnTaxPayTax()
    {
        return orderUnTaxPayTax;
    }

    public void setOrderUnTaxPayTax(double orderUnTaxPayTax)
    {
        this.orderUnTaxPayTax = orderUnTaxPayTax;
    }

    public double getPayAmt()
    {
        return payAmt;
    }

    public void setPayAmt(double payAmt)
    {
        this.payAmt = payAmt;
    }

    public String getPlatformResult()
    {
        return platformResult;
    }

    public void setPlatformResult(String platformResult)
    {
        this.platformResult = platformResult;
    }

    public int getPrintCount()
    {
        return printCount;
    }

    public void setPrintCount(int printCount)
    {
        this.printCount = printCount;
    }

    public boolean isPrintDetails()
    {
        return printDetails;
    }

    public void setPrintDetails(boolean printDetails)
    {
        this.printDetails = printDetails;
    }

    public String getRandomCode()
    {
        return randomCode;
    }

    public void setRandomCode(String randomCode)
    {
        this.randomCode = randomCode;
    }

    public double getRebateAmt()
    {
        return rebateAmt;
    }

    public void setRebateAmt(double rebateAmt)
    {
        this.rebateAmt = rebateAmt;
    }

    public String getRebateNo()
    {
        return rebateNo;
    }

    public void setRebateNo(String rebateNo)
    {
        this.rebateNo = rebateNo;
    }

    public String getRightQrStr()
    {
        return rightQrStr;
    }

    public void setRightQrStr(String rightQrStr)
    {
        this.rightQrStr = rightQrStr;
    }

    public String getSaleNo()
    {
        return saleNo;
    }

    public void setSaleNo(String saleNo)
    {
        this.saleNo = saleNo;
    }

    public String getSaleType()
    {
        return saleType;
    }

    public void setSaleType(String saleType)
    {
        this.saleType = saleType;
    }

    public String getSellerGuiNo()
    {
        return sellerGuiNo;
    }

    public void setSellerGuiNo(String sellerGuiNo)
    {
        this.sellerGuiNo = sellerGuiNo;
    }

    public String getShopId()
    {
        return shopId;
    }

    public void setShopId(String shopId)
    {
        this.shopId = shopId;
    }

    public int getStartMonth()
    {
        return startMonth;
    }

    public void setStartMonth(int startMonth)
    {
        this.startMonth = startMonth;
    }

    public double getTaxRate()
    {
        return taxRate;
    }

    public void setTaxRate(double taxRate)
    {
        this.taxRate = taxRate;
    }

    public double getTaxableAmt()
    {
        return taxableAmt;
    }

    public void setTaxableAmt(double taxableAmt)
    {
        this.taxableAmt = taxableAmt;
    }

    public String getTaxationType()
    {
        return taxationType;
    }

    public void setTaxationType(String taxationType)
    {
        this.taxationType = taxationType;
    }

    public double getTotAmt()
    {
        return totAmt;
    }

    public void setTotAmt(double totAmt)
    {
        this.totAmt = totAmt;
    }

    public double getTotSaleAmt()
    {
        return totSaleAmt;
    }

    public void setTotSaleAmt(double totSaleAmt)
    {
        this.totSaleAmt = totSaleAmt;
    }

    public double getTotSaleTax()
    {
        return totSaleTax;
    }

    public void setTotSaleTax(double totSaleTax)
    {
        this.totSaleTax = totSaleTax;
    }

    public double getTotSaleUAmt()
    {
        return totSaleUAmt;
    }

    public void setTotSaleUAmt(double totSaleUAmt)
    {
        this.totSaleUAmt = totSaleUAmt;
    }

    public double getUnTaxPayAmt()
    {
        return unTaxPayAmt;
    }

    public void setUnTaxPayAmt(double unTaxPayAmt)
    {
        this.unTaxPayAmt = unTaxPayAmt;
    }

    public double getUnTaxPayTax()
    {
        return unTaxPayTax;
    }

    public void setUnTaxPayTax(double unTaxPayTax)
    {
        this.unTaxPayTax = unTaxPayTax;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public double getZeroTaxAmt()
    {
        return zeroTaxAmt;
    }

    public void setZeroTaxAmt(double zeroTaxAmt)
    {
        this.zeroTaxAmt = zeroTaxAmt;
    }

    public List<invoiceDetailListResponse> getDetailList()
    {
        return detailList;
    }

    public void setDetailList(List<invoiceDetailListResponse> detailList)
    {
        this.detailList = detailList;
    }
}

