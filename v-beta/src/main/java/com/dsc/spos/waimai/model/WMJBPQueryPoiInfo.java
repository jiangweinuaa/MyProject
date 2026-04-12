package com.dsc.spos.waimai.model;

import java.util.List;

public class WMJBPQueryPoiInfo 
{
  private List<Data> data;
  public void setData(List<Data> data) {
       this.data = data;
   }
   public List<Data> getData() {
       return data;
   }

  public class Data {

      private String address;
      private String ePoiId;
      private String invoiceDescription;
      private int invoiceMinPrice;
      private int invoiceSupport;
      private int isOnline;
      private int isOpen;
      private int latitude;
      private long longitude;
      private String name;
      private String noticeInfo;
      private String openTime;
      private String phone;
      private String pictureUrl;
      private int preBook;
      private int preBookMaxDays;
      private int preBookMinDays;
      private double shippingFee;
      private String standbyTel;
      private String tagName;
      private int timeSelect;
      public void setAddress(String address) {
           this.address = address;
       }
       public String getAddress() {
           return address;
       }

      public void setEPoiId(String ePoiId) {
           this.ePoiId = ePoiId;
       }
       public String getEPoiId() {
           return ePoiId;
       }

      public void setInvoiceDescription(String invoiceDescription) {
           this.invoiceDescription = invoiceDescription;
       }
       public String getInvoiceDescription() {
           return invoiceDescription;
       }

      public void setInvoiceMinPrice(int invoiceMinPrice) {
           this.invoiceMinPrice = invoiceMinPrice;
       }
       public int getInvoiceMinPrice() {
           return invoiceMinPrice;
       }

      public void setInvoiceSupport(int invoiceSupport) {
           this.invoiceSupport = invoiceSupport;
       }
       public int getInvoiceSupport() {
           return invoiceSupport;
       }

      public void setIsOnline(int isOnline) {
           this.isOnline = isOnline;
       }
       public int getIsOnline() {
           return isOnline;
       }

      public void setIsOpen(int isOpen) {
           this.isOpen = isOpen;
       }
       public int getIsOpen() {
           return isOpen;
       }

      public void setLatitude(int latitude) {
           this.latitude = latitude;
       }
       public int getLatitude() {
           return latitude;
       }

      public void setLongitude(long longitude) {
           this.longitude = longitude;
       }
       public long getLongitude() {
           return longitude;
       }

      public void setName(String name) {
           this.name = name;
       }
       public String getName() {
           return name;
       }

      public void setNoticeInfo(String noticeInfo) {
           this.noticeInfo = noticeInfo;
       }
       public String getNoticeInfo() {
           return noticeInfo;
       }

      public void setOpenTime(String openTime) {
           this.openTime = openTime;
       }
       public String getOpenTime() {
           return openTime;
       }

      public void setPhone(String phone) {
           this.phone = phone;
       }
       public String getPhone() {
           return phone;
       }

      public void setPictureUrl(String pictureUrl) {
           this.pictureUrl = pictureUrl;
       }
       public String getPictureUrl() {
           return pictureUrl;
       }

      public void setPreBook(int preBook) {
           this.preBook = preBook;
       }
       public int getPreBook() {
           return preBook;
       }

      public void setPreBookMaxDays(int preBookMaxDays) {
           this.preBookMaxDays = preBookMaxDays;
       }
       public int getPreBookMaxDays() {
           return preBookMaxDays;
       }

      public void setPreBookMinDays(int preBookMinDays) {
           this.preBookMinDays = preBookMinDays;
       }
       public int getPreBookMinDays() {
           return preBookMinDays;
       }

      public void setShippingFee(double shippingFee) {
           this.shippingFee = shippingFee;
       }
       public double getShippingFee() {
           return shippingFee;
       }

      public void setStandbyTel(String standbyTel) {
           this.standbyTel = standbyTel;
       }
       public String getStandbyTel() {
           return standbyTel;
       }

      public void setTagName(String tagName) {
           this.tagName = tagName;
       }
       public String getTagName() {
           return tagName;
       }

      public void setTimeSelect(int timeSelect) {
           this.timeSelect = timeSelect;
       }
       public int getTimeSelect() {
           return timeSelect;
       }

  }
  
}