package com.dsc.spos.waimai.kdniao;

public class kdnTCStoreReq {
    private String storeCode;
    private String storeName;
    private String storeGoodsType;
    private String provinceName;
    private String cityName;
    private String areaName;
    private String address;
    /**
     * 坐标类型 1：百度地图 2：高德地图 3：腾讯地图
     */
    private int lbsType = 2;

    private String longitude;
    private String latitude;
    private String contactName;
    private String mobile;
    private String phone;
    private String startBusinessTime;
    private String endBusinessTime;
    private String remark;
    /**
     * 附件类型(有上传附件时，该字段必填) 1：URL地址 2：Base64(暂不支持) 3：图片hash值
     */
    private String attachType;
    private String idCardFront;
    private String idCardBack;
    private String idCard;
    private String idCardName;
    private String license;
    private String creditCode;
    private String shopPicture;
    /**
     * 修改时必传 平台门店在KDN对应的门店状态  0：已失效，1：已激活
     */
    private String storeStatus;

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreGoodsType() {
        return storeGoodsType;
    }

    public void setStoreGoodsType(String storeGoodsType) {
        this.storeGoodsType = storeGoodsType;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getLbsType() {
        return lbsType;
    }

    public void setLbsType(int lbsType) {
        this.lbsType = lbsType;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStartBusinessTime() {
        return startBusinessTime;
    }

    public void setStartBusinessTime(String startBusinessTime) {
        this.startBusinessTime = startBusinessTime;
    }

    public String getEndBusinessTime() {
        return endBusinessTime;
    }

    public void setEndBusinessTime(String endBusinessTime) {
        this.endBusinessTime = endBusinessTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAttachType() {
        return attachType;
    }

    public void setAttachType(String attachType) {
        this.attachType = attachType;
    }

    public String getIdCardFront() {
        return idCardFront;
    }

    public void setIdCardFront(String idCardFront) {
        this.idCardFront = idCardFront;
    }

    public String getIdCardBack() {
        return idCardBack;
    }

    public void setIdCardBack(String idCardBack) {
        this.idCardBack = idCardBack;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getIdCardName() {
        return idCardName;
    }

    public void setIdCardName(String idCardName) {
        this.idCardName = idCardName;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getShopPicture() {
        return shopPicture;
    }

    public void setShopPicture(String shopPicture) {
        this.shopPicture = shopPicture;
    }

    public String getStoreStatus() {
        return storeStatus;
    }

    public void setStoreStatus(String storeStatus) {
        this.storeStatus = storeStatus;
    }
}
