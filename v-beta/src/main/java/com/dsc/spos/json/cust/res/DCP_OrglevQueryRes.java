package com.dsc.spos.json.cust.res;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.cust.req.DCP_OrgCreateReq;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DCP_OrglevQueryRes extends JsonRes {

    private List<Data> datas;

    @Getter
    @Setter
    public class Data {

        @JSONField(serialize=false)
        private String eId;

        private String status;
        private String organizationNo;
        private String org_Type;
        private String orgName;
        private String orgForm;
        private String disCentre;
        private String fileName;
        private String is_Corp;
        private String corp;
        private String corpName;
        private String memo;
        private String address;
        private String zipcode;
        private String phone;
        private String fax;
        private String email;
        private String bankname;
        private String bankaccount;
        private String area;
        private String isdistbr;
        private String sname;
        private String in_Cost_WareHouse;
        private String in_Non_Cost_WareHouse;
        private String out_Cost_WareHouse;
        private String out_Non_Cost_WareHouse;
        private String inv_Cost_WareHouse;
        private String inv_Non_Cost_WareHouse;
        private String enablecredit;
        private String up_Org;
        private String upOrgName;
        private String contact;
        private String contactName;
        private String up_Lev;
        private String province;
        private String city;
        private String county;
        private String street;

        private String sellerGuiNo;

        private String shopBeginTime;
        private String shopEndTime;

        private String lng;
        private String lat;

        private String belfirm;
        private String belfirmname;

        private String einvoiceKey;
        //private String machOrganizationNO;
        private String selfBeginTime;
        private String selfEndTime;
        private String dcCorpName;
        private String dcAddress;
        private String dcPhone;
        private String declCompany;
        private String thirdShop;

        private String return_Cost_WareHouse;
        private String manager;
        private String managerName;
        private String partnerNo;
        private String taxPayer_Type;
        private String taxPayer_No;
        private String taxation;
        private String outPutTax;
        private String USCI;
        private String registerNo;
        private String legalPerson;
        private String openingDate;
        private String closeDate;
        private String opStatus;
        private String billCode;

        private String topOrgNo;
        private String topOrgName;
        private String outPutTaxName;

        private String inputTaxName;
        private String inputTaxCode;
        private String inputTaxRate;
        private String outPutTaxRate;

        private List<Data> children;
        private List<OrgLang> datasOrg_lang;
        private List<Warehouse> dataswareHouse;
        private List<ShopTag> shop_Tag;

        private List<Delivery> delivery;
        private String[] production;
        private String[] productionFocus;
        private List<AutoTake> autoTake;
        private String isTake;
        private String range_Type;
        private String range;
        private List<Account> accountList;

        private String costCalculation;
        private String costDomain;

        private String creatorID;
        private String creatorName;
        private String creatorDeptID;
        private String creatorDeptName;
        private String create_datetime;
        private String lastmodifyID;
        private String lastmodifyName;
        private String lastmodify_datetime;

        private String isDept;
        private String upDeptNo;
        private String upDeptName;
        private String is_prod_org;
        private String machOrganizationNo;
        private String machOrganizationName;
        private String orderDistbr;
        private String orderDistbrName;
        private String taxArea;
        private String currency;
        private String meiTuanShopId;
        private String meiTuanShopName;
        private String dianPingShopId;
        private String dianPingShopName;
        private String ic_cost_warehouse;
        private String ic_cost_warehouseName;
        private String routeNo;
        private List<LicenseList> licenseList;

    }

    @lombok.Data
    public class LicenseList{
        private String item;
        private String imageType;
        private String licenseImgUrl;
        private String licenseImg;
    }


    // 组织多语言
    @Getter
    @Setter
    public class OrgLang {
        private String eId;
        private String organizationNo;
        private String lang_Type;
        private String org_Name;
        private String status;
        private String sname;
    }

    // 仓库列表
    @Getter
    @Setter
    public class Warehouse {

        @JSONField(serialize=false)
        private String eId;

        private String wareHouse;
        private String wareHouse_Type;
        private String is_Cost;
        private String status;
        private String isLocation;
        private String stockManageType;
        private String is_WMS;
        private String isCheckStock;


        private List<WarehouseLang> datasWare_Lang;


    }

    // 仓库多语言
    @Getter
    @Setter
    public class WarehouseLang {
        private String eId;
        private String organizationNo;
        private String lang_Type;
        private String ware_Name;
        private String status;
    }

    @Getter
    @Setter
    public class ShopTag {
        private String tagNo;
    }

    // 门店配送方式
    @Getter
    @Setter
    public class Delivery {
        private String deliveryType;
        private String priority;
    }

    // 生产内容
    @Getter
    @Setter
    public class Production {
        private String productionType;
    }

    // 集中点内容
    @Getter
    @Setter
    public class ProductionFocus {
        private String pFocusType;
    }

    // 自动接单
    @Getter
    @Setter
    public class AutoTake {
        private String load_doctype;
        private String isAutoTake;
        private String is_Auto_Express;
    }

    @Getter
    @Setter
    public class Account {
        private String eId;
        private String organizationNo;
        private String account;
        private String bankNo;
        private String bankName;
        private String bankDocNo;
        private String status;

    }

}
