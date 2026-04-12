package com.dsc.spos.model;

public class invoiceShopinfoResponse
{
    private String address;
    private String orgId;
    private String orgName;
    private String phone;
    private String sellerGuiNo;

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getOrgId()
    {
        return orgId;
    }

    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
    }

    public String getOrgName()
    {
        return orgName;
    }

    public void setOrgName(String orgName)
    {
        this.orgName = orgName;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getSellerGuiNo()
    {
        return sellerGuiNo;
    }

    public void setSellerGuiNo(String sellerGuiNo)
    {
        this.sellerGuiNo = sellerGuiNo;
    }
}
