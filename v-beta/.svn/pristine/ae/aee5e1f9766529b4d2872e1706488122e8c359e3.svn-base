package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DCP_OrgUpdateReq extends JsonBasicReq {

    private Request request;

    @Getter
    @Setter
    public class Request {

        private String organizationID;
        private String org_Type;
        private String orgForm;
        private String is_Corp;
        private String corp;

        @Deprecated //no use
        private String belfirm;
        private String memo;
        private String status;
        private String province;
        private String city;
        private String county;
        private String street;
        private String address;
        private String zipcode;
        private String phone;
        private String fax;
        private String email;
        private String bankname;
        private String bankaccount;
        private String area;
        private String isdistbr;

        @Deprecated //no use
        private String sname;
        @Deprecated //no use
        private String enablecredit;

        private String up_Org;
        private String in_Cost_WareHouse;
        private String in_Non_Cost_WareHouse;
        private String out_Cost_WareHouse;
        private String out_Non_Cost_WareHouse;
        private String inv_Cost_WareHouse;
        private String inv_Non_Cost_WareHouse;
        private String shopBeginTime;
        private String shopEndTime;
        private String thirdShop;
        @Deprecated
        private String disCentre;
        private String fileName;
        private String fileData;
        private String longitude;
        private String latitude;
        private String sellerGuiNo;
        private String einvoiceKey;
        private String dcCorpName;
        private String dcAddress;
        private String dcPhone;
        private String declCompany;
        private String return_Cost_WareHouse;
        private String openingDate;
        private String closeDate;
        private String opStatus;
        private String manager;
        private String partnerNo;
        private String taxpayer_Type;
        private String taxpay_No;
        private String taxation;
        private String outPutTax;
        private String USCI;
        private String registerNo;
        private String legalPerson;
        private String docode;
        private String billCode;
        private String contact;

        private String costCalculation;
        private String costDomain;
        private String inputTaxCode;

        private List<OrgLang> datasOrg_Lang;
        private List<Warehouse> dataswareHouse;
        private List<Account> accountList;
        private List<ShopTag> shop_tag;

        private String isDept;
        private String upDeptNo;
        private String is_prod_org;
        private String machOrganizationNo;
        private String orderDistbr;
        private String taxArea;
        private String currency;
        private String meiTuanShopId;
        private String dianPingShopId;
        private String ic_cost_warehouse;
        private String routeNo;
        private List<DCP_OrgUpdateReq.LicenseList> licenseList;
    }

    @Data
    public class LicenseList{
        private String imageType;
        private String licenseImgUrl;
        private String licenseImg;
    }

    @Getter
    @Setter
    public class OrgLang {
        private String lang_type;
        private String org_Name;
        private String sname;
    }

    @Getter
    @Setter
    public class Warehouse {
        private String companyNo;
        private String expand;
        private String is_Cost;
        private String wareHouse;
        private String wareHouse_Type;
        @Deprecated
        private String wareName;
        private String isLocation;
        private String stockManageType;
        private String isWMS;
        private String status;
        private String isCheckStock;

        private List<WarehouseLang> datasWare_Lang;

    }

    @Getter
    @Setter
    public class WarehouseLang {
        private String lang_Type;
        private String ware_Name;
        private String status;
        private String companyNo;
        private String eid;
        private String organizationNo;

    }

    @Getter
    @Setter
    public class Account {
        private String account;
        private String bankNo;
        private String status;

    }

    @Getter
    @Setter
    public class ShopTag {
        private String tagNo;
    }

}
