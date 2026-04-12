package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/03/05
 */
@Getter
@Setter
public class DCP_BizPartnerCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        private String saleDeptNo;
        private String enableContract;
        private String saleInvoiceCode;
        @JSONFieldRequired(display = "结束日期 账期计算的结束日期")
        private String endDate;
        private String memo;
        @JSONFieldRequired(display = "限定法人范围")
        private List<OrgList> orgList;
        private String invoiceAddress;
        private List<RestrictShopList> restrictShopList;
        private String creditType;
        private String mainCurrency;
        private String payType;
        private String billDateNo;
        private String deliveryAddress;
        private List<SName_lang> sName_lang;
        private String custGrade;
        private String saleEmpNo;
        private String mainCatgegory;
        private String taxCode;
        private List<CustTagList> custTagList;
        private String invoiceCode;
        private String payCenter;
        private List<ContactList> contactList;
        private String collectShop;
        private String grade;
        private String custPayDateNo;
        private List<LicenseList> licenseList;
        private String creditAmt;
        private String mainBrand;
        private String custPayType;
        @JSONFieldRequired(display = "交易对象状态码")
        private String status;
        @JSONFieldRequired(display = "交易对象类型")
        private String bizType;
        private String role;
        private String corpType;
        private String registerNo;
        private String isCredit;
        @JSONFieldRequired(display = "交易对象全称")
        private String fName;
        private String restrictShop;
        private String legalPerson;
        private String taxPayerNo;
        private String purEmpNo;
        private List<FName_lang> fName_lang;
        private String address;
        private String custBillDateNo;
        @JSONFieldRequired(display = "交易对象简称")
        private String sName;
        private String officeAddress;
        private String lifeValue;
        private String bizPartnerNo;
        @JSONFieldRequired(display = "开始日期 账期计算的起始日期")
        private String beginDate;
        private String custPayCenter;
        private String payDateNo;
        private String collectObject;
        private String purDeptNo;
        private String payer;
        private String payee;
    }

    @Getter
    @Setter
    public class OrgList {
        @JSONFieldRequired(display = "组织编号")
        private String orgNo;
        @JSONFieldRequired(display = "状态码")
        private String status;
    }

    @Getter
    @Setter
    public class RestrictShopList {
        @JSONFieldRequired(display = "门店编码")
        private String shopId;
    }

    @Getter
    @Setter
    public class SName_lang {
        @JSONFieldRequired(display = "交易对象简称")
        private String sname;
        @JSONFieldRequired(display = "语言别")
        private String langType;
    }

    @Getter
    @Setter
    public class CustTagList {
        private String tagNo;
    }


    @Getter
    @Setter
    public class ContactList {
        @JSONFieldRequired(display = "项次")
        private String item;
        @JSONFieldRequired(display = "通信类型")
        private String conType;
        @JSONFieldRequired(display = "主要联络方式")
        private String isMainContact;
        @JSONFieldRequired(display = "联系人")
        private String contact;
        @JSONFieldRequired(display = "备注")
        private String memo;
        @JSONFieldRequired(display = "通信内容")
        private String content;
        @JSONFieldRequired(display = "状态码")
        private String status;
    }

    @Getter
    @Setter
    public class LicenseList {
        @JSONFieldRequired(display = "生效日期")
        private String beginDate;
        @JSONFieldRequired(display = "项次")
        private String item;
        @JSONFieldRequired(display = "证照编号")
        private String licenseNo;
        @JSONFieldRequired(display = "截止日期")
        private String endDate;
        @JSONFieldRequired(display = "证照图片文件")
        private String licenseImg;
        @JSONFieldRequired(display = "证照类型")
        private String imgType;
        @JSONFieldRequired(display = "状态码")
        private String status;
    }

    @Getter
    @Setter
    public class FName_lang {
        @JSONFieldRequired(display = "全称")
        private String name;
        @JSONFieldRequired(display = "语言别")
        private String langType;
    }

}