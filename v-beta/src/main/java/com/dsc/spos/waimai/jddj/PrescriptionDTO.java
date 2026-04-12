package com.dsc.spos.waimai.jddj;
import java.util.List;

public class PrescriptionDTO {

   private String useDrugName;
   private int sex;
   private String identityNumber;
   private String birthday;
   private String phoneNumber;
   private String picUrl;
   private List<String> picUrlList;
   public void setUseDrugName(String useDrugName) {
        this.useDrugName = useDrugName;
    }
    public String getUseDrugName() {
        return useDrugName;
    }

   public void setSex(int sex) {
        this.sex = sex;
    }
    public int getSex() {
        return sex;
    }

   public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }
    public String getIdentityNumber() {
        return identityNumber;
    }

   public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public String getBirthday() {
        return birthday;
    }

   public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

   public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
    public String getPicUrl() {
        return picUrl;
    }

   public void setPicUrlList(List<String> picUrlList) {
        this.picUrlList = picUrlList;
    }
    public List<String> getPicUrlList() {
        return picUrlList;
    }

}