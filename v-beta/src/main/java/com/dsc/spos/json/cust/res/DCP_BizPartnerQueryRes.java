package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *
 * @author YapiGenerator自动生成
 * @date 2025/03/06
 */
@Getter
@Setter
public class DCP_BizPartnerQueryRes extends JsonRes{

   private List<Datas> datas;

    @Getter
    @Setter
    public class Datas{
          private String saleDeptNo;
          private String enableContract;
          private String lastmodifyID;
          private String payCenterName;
          private String saleInvoiceCode;
          private String endDate;
          private String memo;
          private String invoiceAddress;
          private List<RestrictShopList> restrictShopList;
          private String creditType;
          private String saleEmpName;
          private String mainCurrency;
          private String payType;
          private String billDateNo;
          private String deliveryAddress;
          private String custGrade;
          private String currName;
          private String creatorDeptID;
          private String custBillDateDesc;
          private String saleEmpNo;
          private String taxCode;
          private List<CustTagList> custTagList;
          private String purEmpName;
          private String invoiceCode;
          private String lastmodifyName;
          private String custPayDateDesc;
          private String payCenter;
          private List<FName_list> fName_list;
          private String collectShop;
          private String grade;
          private List<OrgList> orglist;
          private String custPayDateNo;
          private List<LicenseList> licenseList;
          private String creditAmt;
          private String custPayType;
          private String status;
          private String bizType;
          private String role;
          private String invoiceName;
          private List<Contactlist> contactlist;
          private String corpType;
          private List<BillList> billList;
          private String collectShopName;
          private String creatorName;
          private String taxName;
          private String registerNo;
          private String payDateDesc;
          private String billDateDesc;
          private String isCredit;
          private String fName;
          private String restrictShop;
          private String create_datetime;
          private String legalPerson;
          private String taxPayerNo;
          private String purEmpNo;
          private String lastmodify_datetime;
          private String creatorDeptName;
          private String custPayCenterName;
          private String address;
          private String custBillDateNo;
          private String sName;
          private List<SName_list> sName_list;
          private String creatorID;
          private String saleDeptName;
          private String officeAddress;
          private String lifeValue;
          private String saleInvoiceName;
          private String bizPartnerNo;
          private String beginDate;
          private String custPayCenter;
          private String mainBrands;
          private String mainCategory;
          private String payDateNo;
          private String collectObject;
          private String purDeptNo;
          private String purDeptName;
          private String payer;
          private String payerName;
          private String payee;
          private String payeeName;
    }
    @Getter
    @Setter
    public class RestrictShopList{
          private String shopName;
          private String shopId;
    }

    @Getter
    @Setter
    public class CustTagList{
          private String tagNo;
          private String tagName;
    }

    @Getter
    @Setter
    public class FName_list{
          private String name;
          private String langType;
    }

    @Getter
    @Setter
    public class OrgList{
          private String orgName;
          private String orgNo;
          private String status;
    }

    @Getter
    @Setter
    public class LicenseList{
          private String beginDate;
          private String item;
          private String licenseNo;
          private String endDate;
          private String licenseStatus;
          private String licenseImg;
          private String imgType;
          private String status;
    }

    @Getter
    @Setter
    public class Contactlist{
          private String item;
          private String conType;
          private String contact;
          private String memo;
          private String ismainContact;
          private String content;
          private String status;
    }

    @Getter
    @Setter
    public class BillList{
          private String item;
          private String isCheck;
          private String orgName;
          private String bdate;
          private String orgNo;
          private String contractNO;
          private String billType;
          private String edate;
          private String billDocno;
    }

    @Getter
    @Setter
    public class SName_list{
          private String name;
          private String langType;
    }


}