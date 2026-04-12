package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_OrglevQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrglevQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DCP_OrglevQuery extends SPosBasicService<DCP_OrglevQueryReq, DCP_OrglevQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_OrglevQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_OrglevQueryReq> getRequestType() {
        return new TypeToken<DCP_OrglevQueryReq>() {
        };
    }

    @Override
    protected DCP_OrglevQueryRes getResponseType() {
        return new DCP_OrglevQueryRes();
    }

    List<Map<String, Object>> allorglang = new ArrayList<>();
    List<Map<String, Object>> allorgwarhouse = new ArrayList<>();
    List<Map<String, Object>> allwhlang = new ArrayList<>();
    List<Map<String, Object>> shoptag = new ArrayList<>();
    List<Map<String, Object>> ShopOrderSet = new ArrayList<>();
    List<Map<String, Object>> ShopOrdertakeSet = new ArrayList<>();
    List<Map<String, Object>> allAccountList = new ArrayList<>();

    List<Map<String, Object>> allPlatFormList = new ArrayList<>();
    List<Map<String, Object>> allLicenseList = new ArrayList<>();

    List<Map<String, Object>> allDeptList = new ArrayList<>();

    List<Map<String, Object>> allRouteList = new ArrayList<>();


    @Override
    protected DCP_OrglevQueryRes processJson(DCP_OrglevQueryReq req) throws Exception {
        DCP_OrglevQueryRes res = new DCP_OrglevQueryRes();
        //判断一下status
        String statussql = "";
        List<String> orgForm = req.getRequest().getOrgForm();
        String supplier = req.getRequest().getSupplier();
        SimpleDateFormat hms = new SimpleDateFormat("HHmmss");
        SimpleDateFormat hms2 = new SimpleDateFormat("HH:mm:ss");

        String orgFormWhere = "";
        if (req.getRequest() != null) {
            orgForm = req.getRequest().getOrgForm();
        }

        String isHttps= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
        String httpStr=isHttps.equals("1")?"https://":"http://";
        String domainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
        if (domainName.endsWith("/")) {
            domainName = httpStr + domainName + "resource/image/";
        }else{
            domainName = httpStr + domainName + "/resource/image/";
        }

        String alldata = req.getRequest().getAlldata();

        if (req.getRequest().getStatus() != null && !req.getRequest().getStatus().isEmpty()) {
            statussql = " and A.status='" + req.getRequest().getStatus() + "' ";
        }
        //判断keyTxt，如果是TEXT的要显示TEXT的一个上级，以及TEXT的所有上级
        String skeytxt = "";
        if (req.getRequest().getKeyTxt() != null && !req.getRequest().getKeyTxt().isEmpty()) {
            //skeytxt=" and  (A.Org_Form='2' and ( B.OrganizationNo like '%"+req.getKeyTxt()+"%' or C.Org_Name like '%"+req.getKeyTxt()+"%'  ) ) or A.Org_Form!='2' ";
            skeytxt = " and  (  ( B.OrganizationNo like '%" + req.getRequest().getKeyTxt() + "%' or C.Org_Name like '%" + req.getRequest().getKeyTxt() + "%'  ) )  ";
        }

        if (null != orgForm && !orgForm.isEmpty()) {
            StringBuilder mWhere = new StringBuilder();
            for (String s : orgForm) {
                mWhere.append(",'").append(s).append("'");
            }
            mWhere.deleteCharAt(0);
            orgFormWhere = "and A.org_form in (" + mWhere + ") ";
        }

        StringBuffer sb = new StringBuffer();
        sb.append("select A.*,h.TAXNAME OUTPUTTAXNAME, TRIM(NVL(B.UP_ORG,'')) AS UP_ORG,TRIM(NVL(E.Org_Name,'')) AS UP_ORGNAME," +
                " D.Org_Name BELFIRMNAME,ee0.op_name CREATEOPNAME,ee1.op_name MODIFYOPNAME,g.DEPARTNAME CREATEDEPTNAME,ee2.name as managername," +
                " ee3.name as contactName,F.SNAME as CORPNAME,h2.TAXNAME AS INPUTTAXNAME,h1.TAXRATE AS OUTPUTTAXRATE,h3.TAXRATE AS INPUTTAXRATE," +
                " i.org_name as machOrganizationName,i1.org_name as orderDistbrName,j.WAREHOUSE_NAME as ic_cost_warehouseName " +
                " from DCP_ORG A "
                + " left join DCP_ORG_Level B on A.EID=B.EID and A.OrganizationNo=B.OrganizationNo "
                + " left join DCP_ORG_lang C on A.EID=C.EID and A.OrganizationNo=C.OrganizationNo  and C.lang_type='" + req.getLangType() + "'  "
                + " left join DCP_ORG_lang D on A.EID=D.EID and A.BELFIRM=D.OrganizationNo and D.lang_type='" + req.getLangType() + "' "
                + " left join DCP_ORG_lang E on A.EID=E.EID and B.UP_ORG=E.OrganizationNo and E.lang_type='" + req.getLangType() + "' "
                + " left join DCP_ORG F on a.EID=F.EID and a.CORP=F.ORGANIZATIONNO  "
                + " LEFT JOIN DCP_DEPARTMENT_LANG g on a.EID=g.EID AND a.CREATEDEPTID=g.DEPARTNO AND g.LANG_TYPE='" + req.getLangType() + "'"
                + " LEFT JOIN DCP_TAXCATEGORY_LANG h on h.EID=a.EID AND h.TAXCODE=a.OUTPUTTAX AND h.TAXAREA='CN' and h.lang_type='" + req.getLangType() + "' " //todo 这里会导致查询重复，因为org没有税区的概念 目前是直接给固定值CN
                + " LEFT JOIN DCP_TAXCATEGORY h1 on h1.EID=a.EID AND h1.TAXCODE=a.OUTPUTTAX AND h1.TAXAREA='CN'  "
                + " LEFT JOIN DCP_TAXCATEGORY_LANG h2 on h2.EID=a.EID AND h2.TAXCODE=a.INPUT_TAXCODE AND h2.TAXAREA='CN' and h2.lang_type='" + req.getLangType() + "' "
                + " LEFT JOIN DCP_TAXCATEGORY h3 on h3.EID=a.EID AND h3.TAXCODE=a.INPUT_TAXCODE AND h3.TAXAREA='CN' " +
                " left join dcp_org_lang i on i.eid=a.eid and i.organizationno=a.machOrganizationNo and i.lang_type='"+req.getLangType()+"' " +
                " left join dcp_org_lang i1 on i1.eid=a.eid and i1.organizationno=a.orderDistbr and i1.lang_type='"+req.getLangType()+"' " +
                " left join dcp_warehouse_lang j on j.eid=a.eid and j.warehouse=a.ic_cost_warehouse and j.lang_type='"+req.getLangType()+"'" +


                 " LEFT JOIN platform_staffs_lang ee0 ON ee0.EID=a.EID and ee0.opno=a.CREATEBY and ee0.lang_type='"+req.getLangType()+"'   "
                + " LEFT JOIN platform_staffs_lang ee1 ON ee1.EID=a.EID and ee1.opno=a.MODIFYBY and ee1.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN DCP_EMPLOYEE ee2 ON ee2.EID=a.EID and ee2.EMPLOYEENO=a.manager "
                + " LEFT JOIN DCP_EMPLOYEE ee3 ON ee3.EID=a.EID and ee3.EMPLOYEENO=a.CONTACT "
                + "  ");
        //0 全部数据 1-用户权限范围数据
        if (!Check.Null(alldata) && alldata.equals("1")) {
            sb.append(" inner join platform_staffs_shop ps on a.EID=ps.EID and a.organizationno=ps.SHOPID and ps.status='100' and ps.opno='" + req.getOpNO() + "' ");
        }
        sb.append(" where A.EID='" + req.geteId() + "' " + orgFormWhere + statussql + skeytxt);

        List<Map<String, Object>> allorg = this.doQueryData(sb.toString(), null);

        List<Map<String, Object>> allshop = allorg(allorg, "", req.geteId());
        if (req.getRequest() != null && req.getRequest().getKeyTxt() != null && !req.getRequest().getKeyTxt().isEmpty()) {
            allshop = allorg;
        }

        if (allshop != null && !allshop.isEmpty()) {
            //查询一下组织多语言
            String orgsqllang = "select * from DCP_ORG_Lang where EID='" + req.geteId() + "' ";
            allorglang = this.doQueryData(orgsqllang, null);
            //查询仓库表
            String warhousesql = "select * from DCP_WAREHOUSE where EID='" + req.geteId() + "' ";
            allorgwarhouse = this.doQueryData(warhousesql, null);
            //查询仓库多语言
            String whlangsql = "select * from DCP_WAREHOUSE_Lang where EID='" + req.geteId() + "' ";
            allwhlang = this.doQueryData(whlangsql, null);

            String platFormSql="select * from DCP_ORG_PLATFORM where eid='"+req.geteId()+"' ";
            allPlatFormList= this.doQueryData(platFormSql,null);

            String licenseSql="select * from DCP_ORG_LICENSE where eid='"+req.geteId()+"' ";
            allLicenseList= this.doQueryData(licenseSql,null);

            String departSql="select a.*,b.DEPARTNAME,c.departname as updepartname from DCP_DEPARTMENT a " +
                    " left join DCP_DEPARTMENT_lang b on a.eid=b.eid and a.departno=b.departno and b.lang_type='"+req.getLangType()+"' " +
                    " left join dcp_department_lang c on c.eid=a.eid and c.departno=a.updepartno and c.lang_type='"+req.getLangType()+"'" +
                    " where a.eid='"+req.geteId()+"' ";
            allDeptList = this.doQueryData(departSql, null);

            String routeSql="select a.* from MES_ROUTE_DETAIL a" +
                    " inner join mes_route b on a.eid=b.eid and a.routeno=b.routeno " +
                    " where a.eid='"+req.geteId()+"' ";
            allRouteList = this.doQueryData(routeSql, null);

            //查询门店的标签
            String shoptagsql = "select A.*,B.tagname from DCP_SHOP_TAG A "
                    + " left join Dcp_Tagtype_Lang B on A.EID=B.EID and A.tagno=B.tagno and B.TAGGROUPTYPE='SHOP' and b.lang_type='" + req.getLangType() + "' "
                    + " where a.eid='" + req.geteId() + "' ";
            shoptag = this.doQueryData(shoptagsql, null);

            //查询门店的订单设置       /// 此表被红艳删除， 3.0需要重新规划    BY jinzma 20200826
//			String sordersetsql="select * from DCP_SHOP_ORDERSet where EID='"+req.geteId()+"' and status='100'  ";
//			ShopOrderSet=this.doQueryData(sordersetsql, null);

            //查询门店的订单设置       /// 此表被红艳删除， 3.0需要重新规划    BY jinzma 20200826
//			String ShopOrdertakeSetsql="select * from DCP_SHOP_ORDERtakeSet where EID='"+req.geteId()+"' and status='100'  ";
//			ShopOrdertakeSet=this.doQueryData(ShopOrdertakeSetsql, null);

            String accountSql = " select * from ("
                    + " select A.EID,A.SHOPID AS ORGANIZATIONNO,A.ACCOUNT,A.BANKNO,A.STATUS, B.EBANKNO,BL.FULLNAME  from DCP_SHOP_ACCOUNT A "
                    + "left join DCP_BANK B on A.Eid=B.EID and A.BANKNO=B.BANKNO "
                    + "LEFT JOIN Dcp_Bank_Lang BL ON BL.EID=A.EID AND BL.BANKNO=A.BANKNO AND BL.LANG_TYPE='" + req.getLangType() + "' "
                    + "WHERE A.EID='" + req.geteId() + "'"
                    + ")";

            allAccountList = this.doQueryData(accountSql, null);

            res.setDatas(new ArrayList<>());
            for (Map<String, Object> allmap : allshop) {
                DCP_OrglevQueryRes.Data lv1 = res.new Data();
                lv1.setEId(allmap.get("EID").toString());
                lv1.setOrganizationNo(allmap.get("ORGANIZATIONNO").toString());
                lv1.setOrg_Type(allmap.get("ORG_TYPE").toString());

                String DISCENTRE = allmap.get("DISCENTRE").toString();
                if (DISCENTRE.equals("Y")) {
                    lv1.setOrgForm("3");
                } else {
                    lv1.setOrgForm(allmap.get("ORG_FORM").toString());
                }

                lv1.setDisCentre(allmap.get("DISCENTRE").toString());
                lv1.setIs_Corp(allmap.get("IS_CORP").toString());
                lv1.setCorp(allmap.get("CORP").toString());
                lv1.setCorpName(StringUtils.toString(allmap.get("CORPNAME"),""));


                lv1.setMemo(allmap.get("MEMO").toString());
                lv1.setStatus(allmap.get("STATUS").toString());
                lv1.setAddress(allmap.get("ADDRESS").toString());
                lv1.setZipcode(allmap.get("ZIPCODE").toString());
                lv1.setPhone(allmap.get("PHONE").toString());
                lv1.setFax(allmap.get("FAX").toString());
                lv1.setEmail(allmap.get("EMAIL").toString());
//				lv1.setBankname(allmap.get("FULLNAME").toString());
                lv1.setBankaccount(allmap.get("BANKACCOUNT").toString());
                lv1.setArea(allmap.get("AREA").toString());
                lv1.setIsdistbr(allmap.get("ISDISTBR").toString());
                lv1.setSname(allmap.get("SNAME").toString());
                lv1.setIn_Cost_WareHouse(allmap.get("IN_COST_WAREHOUSE").toString());
                lv1.setIn_Non_Cost_WareHouse(allmap.get("INV_COST_WAREHOUSE").toString());
                lv1.setInv_Cost_WareHouse(allmap.get("INV_COST_WAREHOUSE").toString());
                lv1.setInv_Non_Cost_WareHouse(allmap.get("INV_NON_COST_WAREHOUSE").toString());
                lv1.setOut_Cost_WareHouse(allmap.get("OUT_COST_WAREHOUSE").toString());
                lv1.setOut_Non_Cost_WareHouse(allmap.get("OUT_NON_COST_WAREHOUSE").toString());
                lv1.setEnablecredit(allmap.get("ENABLECREDIT").toString());
                lv1.setUp_Org(allmap.get("UP_ORG").toString());
                lv1.setUpOrgName(allmap.get("UP_ORGNAME").toString());
                lv1.setContact(StringUtils.toString(allmap.get("CONTACT"), ""));
                lv1.setContactName(StringUtils.toString(allmap.get("CONTACTNAME"), ""));
                lv1.setProvince(allmap.get("PROVINCE").toString());
                lv1.setCity(allmap.get("CITY").toString());
                lv1.setCounty(allmap.get("COUNTY").toString());
                lv1.setStreet(allmap.get("STREET").toString());
                lv1.setLat(allmap.get("LATITUDE").toString());
                lv1.setLng(allmap.get("LONGITUDE").toString());
                lv1.setStatus(allmap.get("STATUS").toString());
                lv1.setBelfirm(allmap.get("BELFIRM").toString());
                lv1.setBelfirmname(allmap.get("BELFIRMNAME").toString());

                lv1.setReturn_Cost_WareHouse(allmap.get("RETURN_COST_WAREHOUSE").toString());
                lv1.setManager(allmap.get("MANAGER").toString());
                lv1.setManagerName(allmap.get("MANAGERNAME").toString());
                lv1.setPartnerNo(allmap.get("PARTNERNO").toString());
                lv1.setTaxPayer_Type(allmap.get("TAXPAYER_TYPE").toString());
                lv1.setTaxPayer_No(allmap.get("TAXPAYER_NO").toString());
                lv1.setTaxation(allmap.get("TAXATION").toString());
                lv1.setOutPutTax(allmap.get("OUTPUTTAX").toString());
                lv1.setOutPutTaxName(allmap.get("OUTPUTTAXNAME").toString());
                lv1.setOutPutTaxRate(allmap.get("OUTPUTTAXRATE").toString());
                lv1.setInputTaxCode(allmap.get("INPUT_TAXCODE").toString());
                lv1.setInputTaxName(allmap.get("INPUTTAXNAME").toString());
                lv1.setInputTaxRate(allmap.get("INPUTTAXRATE").toString());

                lv1.setUSCI(allmap.get("USCI").toString());
                lv1.setRegisterNo(allmap.get("REGISTERNO").toString());
                lv1.setLegalPerson(allmap.get("LEGALPERSON").toString());
                lv1.setOpeningDate(DateFormatUtils.getDate(allmap.get("OPENINGDATE").toString()));
                lv1.setCloseDate(DateFormatUtils.getDate(allmap.get("CLOSEDATE").toString()));
                lv1.setOpStatus(allmap.get("OPSTATUS").toString());
                lv1.setBillCode(allmap.get("BILLCODE").toString());

                lv1.setCostCalculation(StringUtils.toString(allmap.get("COST_CALCULATION"),""));
                lv1.setCostDomain(StringUtils.toString(allmap.get("COST_DOMAIN"),""));

                lv1.setCreatorID(allmap.get("CREATEBY").toString());
                lv1.setLastmodifyID(allmap.get("MODIFYBY").toString());
                lv1.setCreate_datetime(allmap.get("CREATE_DTIME").toString());
                lv1.setLastmodify_datetime(allmap.get("MODIFY_DTIME").toString());
                lv1.setCreatorName(allmap.get("CREATEOPNAME").toString());
                lv1.setLastmodifyName(allmap.get("MODIFYOPNAME").toString());
                lv1.setCreatorDeptID(allmap.get("CREATEDEPTID").toString());
                lv1.setCreatorDeptName(allmap.get("CREATEDEPTNAME").toString());

                lv1.setIsDept(allmap.get("ISDEPT").toString());
                List<Map<String, Object>> deptFilter = allDeptList.stream().filter(x -> x.get("DEPARTNO").toString().equals(lv1.getOrganizationNo())).collect(Collectors.toList());
                if(deptFilter.size()>0){
                    lv1.setUpDeptNo(deptFilter.get(0).get("UPDEPARTNO").toString());
                    lv1.setUpDeptName(deptFilter.get(0).get("UPDEPARTNAME").toString());
                }
                lv1.setIs_prod_org(allmap.get("IS_PROD_ORG").toString());
                lv1.setMachOrganizationNo(allmap.get("MACHORGANIZATIONNO").toString());
                lv1.setMachOrganizationName(StringUtils.toString(allmap.get("MACHORGANIZATIONNAME"),""));
                lv1.setOrderDistbr(allmap.get("ORDERDISTBR").toString());
                lv1.setOrderDistbrName(allmap.get("ORDERDISTBRNAME").toString());
                lv1.setTaxArea(allmap.get("TAXAREA").toString());
                lv1.setCurrency(allmap.get("CURRENCY").toString());
                List<Map<String, Object>> platFormFilter = allPlatFormList.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(lv1.getOrganizationNo())).collect(Collectors.toList());
                if(platFormFilter.size()>0) {
                    lv1.setMeiTuanShopId(platFormFilter.get(0).get("MEITUANSHOPID").toString());
                    lv1.setDianPingShopId(platFormFilter.get(0).get("DIANPINGSHOPID").toString());
                }
                lv1.setIc_cost_warehouse(allmap.get("IC_COST_WAREHOUSE").toString());
                lv1.setIc_cost_warehouseName(allmap.get("IC_COST_WAREHOUSENAME").toString());

                List<Map<String, Object>> routeFilter = allRouteList.stream().filter(x -> x.get("CODE").toString().equals(lv1.getOrganizationNo())).collect(Collectors.toList());
                if(routeFilter.size()>0){
                    lv1.setRouteNo(routeFilter.get(0).get("ROUTENO").toString());
                }
                lv1.setLicenseList(new ArrayList<>());
                Stream<Map<String, Object>> licenseFilter = allLicenseList.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(lv1.getOrganizationNo()));
                String finalDomainName = domainName;
                licenseFilter.forEach(x -> {
                    DCP_OrglevQueryRes.LicenseList licenseList = res.new LicenseList();
                    licenseList.setItem(x.get("ITEM").toString());
                    licenseList.setImageType(x.get("IMGTYPE").toString());
                    licenseList.setLicenseImgUrl(finalDomainName +x.get("LICENSEIMG").toString());
                    licenseList.setLicenseImg(x.get("LICENSEIMG").toString());
                    lv1.getLicenseList().add(licenseList);
                });


                // yuanyy 2019-02-12 增加以下两个字段：  营业开始时间， 营业结束时间
                String shopBeginTime = allmap.get("SHOPBEGINTIME").toString();
                // 针对库中值 会出现NANANA 做下兼容 如果时间转化失败 则给个默认值
                try {
                    if (!Check.Null(shopBeginTime)) {
                        if (shopBeginTime.length() != 6) {
                            shopBeginTime = hms.format(hms2.parse(shopBeginTime));
                        }
                    }
                } catch (Exception e) {
                    shopBeginTime = "000000";
                }

                String shopEndTime = allmap.get("SHOPENDTIME").toString();
                try {
                    if (!Check.Null(shopEndTime)) {
                        if (shopEndTime.length() != 6) {
                            shopEndTime = hms.format(hms2.parse(shopEndTime));
                        }
                    }
                } catch (ParseException e) {
                    shopEndTime = "235959";
                }
                lv1.setShopBeginTime(shopBeginTime);
                lv1.setShopEndTime(shopEndTime);
                lv1.setSellerGuiNo(allmap.get("SELLERGUINO").toString());
                lv1.setRange_Type(allmap.get("RANGE_TYPE").toString());
                lv1.setRange(allmap.get("RANGE").toString());

                lv1.setEinvoiceKey(allmap.get("EINVOICEKEY").toString());
                //lv1.setMachOrganizationNO(allmap.get("MACHORGANIZATIONNO").toString());
                lv1.setSelfBeginTime(allmap.get("SELFBEGINTIME").toString());
                lv1.setSelfEndTime(allmap.get("SELFENDTIME").toString());
                lv1.setDcCorpName(StringUtils.toString(allmap.get("DC_CORP_NAME"),""));
                lv1.setDcAddress(allmap.get("DC_ADDRESS").toString());
                lv1.setDcPhone(allmap.get("DC_PHONE").toString());
                lv1.setDeclCompany(allmap.get("DECL_COMPANY").toString());
                lv1.setThirdShop(allmap.get("THIRD_SHOP").toString());
                //【ID1026956】3.0版本没有查询门店图片了 by jinzma 20220628
                lv1.setFileName(allmap.get("FILENAME").toString());


                //赋值组织多语言
                List<Map<String, Object>> listorglang = getorglang(allorglang, allmap.get("ORGANIZATIONNO").toString(), allmap.get("EID").toString());
                if (listorglang != null && !listorglang.isEmpty()) {
                    lv1.setDatasOrg_lang(new ArrayList<>());
                    for (Map<String, Object> map : listorglang) {
                        DCP_OrglevQueryRes.OrgLang lv2 = res.new OrgLang();
                        lv2.setEId(map.get("EID").toString());
                        lv2.setOrganizationNo(map.get("ORGANIZATIONNO").toString());
                        lv2.setLang_Type(map.get("LANG_TYPE").toString());
                        lv2.setOrg_Name(map.get("ORG_NAME").toString());
                        lv2.setStatus(map.get("STATUS").toString());
                        lv2.setSname(map.get("SNAME").toString());
                        lv1.getDatasOrg_lang().add(lv2);
                        if (req.getLangType().equals(map.get("LANG_TYPE").toString())) {
                            lv1.setOrgName(map.get("ORG_NAME").toString());
                        }
                    }
                }
                //赋值仓库表
                List<Map<String, Object>> listwh = getwh(allorgwarhouse, allmap.get("ORGANIZATIONNO").toString(), allmap.get("EID").toString());
                if (listwh != null && !listwh.isEmpty()) {
                    lv1.setDataswareHouse(new ArrayList<>());
                    for (Map<String, Object> map : listwh) {
                        DCP_OrglevQueryRes.Warehouse lv3 = res.new Warehouse();

                        lv3.setWareHouse_Type(map.get("WAREHOUSE_TYPE").toString());
                        lv3.setWareHouse(map.get("WAREHOUSE").toString());
                        lv3.setIsCheckStock(StringUtils.toString(map.get("ISCHECKSTOCK"), ""));
                        lv3.setIs_Cost(map.get("IS_COST").toString());
                        lv3.setStatus(map.get("STATUS").toString());
                        lv3.setIsLocation(map.get("ISLOCATION").toString());
                        lv3.setStockManageType(map.get("STOCKMANAGETYPE").toString());
                        lv3.setIs_WMS(map.get("ISWMS").toString());
                        //赋值仓库多语言
                        List<Map<String, Object>> listwhlang = getwhlang(allwhlang, allmap.get("ORGANIZATIONNO").toString(), allmap.get("EID").toString(), map.get("WAREHOUSE").toString());
                        if (listwhlang != null && !listwhlang.isEmpty()) {
                            lv3.setDatasWare_Lang(new ArrayList<>());
                            for (Map<String, Object> mapwhlang : listwhlang) {
                                DCP_OrglevQueryRes.WarehouseLang lv4 = res.new WarehouseLang();
                                lv4.setEId(mapwhlang.get("EID").toString());
                                lv4.setOrganizationNo(mapwhlang.get("ORGANIZATIONNO").toString());
                                lv4.setLang_Type(mapwhlang.get("LANG_TYPE").toString());
                                lv4.setWare_Name(mapwhlang.get("WAREHOUSE_NAME").toString());
                                lv4.setStatus(mapwhlang.get("STATUS").toString());

                                lv3.getDatasWare_Lang().add(lv4);
                            }
                        }
                        lv1.getDataswareHouse().add(lv3);
                    }
                }

                //赋值门店标签
                if (shoptag != null && !shoptag.isEmpty()) {
                    lv1.setShop_Tag(new ArrayList<>());
                    for (Map<String, Object> map : shoptag) {
                        if (map.get("SHOPID").toString().equals(lv1.getOrganizationNo())) {
                            DCP_OrglevQueryRes.ShopTag lv5 = res.new ShopTag();
                            lv5.setTagNo(map.get("TAGNO").toString());
//							lv5.setTagName(map.get("TAGNAME").toString());
//							lv5.setStatus(map.get("STATUS").toString());
                            lv1.getShop_Tag().add(lv5);
                        }
                    }
                }


                //门店银行账号列表
                if (allAccountList != null && !allAccountList.isEmpty()) {
                    lv1.setAccountList(new ArrayList<>());
                    for (Map<String, Object> map : allAccountList) {
                        String eid_detail = map.get("EID").toString();
                        String organizationNo_detail = map.get("ORGANIZATIONNO").toString();
                        if (organizationNo_detail.equals(lv1.getOrganizationNo())) {
                            DCP_OrglevQueryRes.Account lv6 = res.new Account();
                            lv6.setEId(eid_detail);
                            lv6.setOrganizationNo(organizationNo_detail);
                            lv6.setAccount(map.get("ACCOUNT").toString());
                            lv6.setBankNo(map.get("BANKNO").toString());
                            lv6.setBankName(map.get("FULLNAME").toString());
                            lv6.setBankDocNo(map.get("EBANKNO").toString());
                            lv6.setStatus(map.get("STATUS").toString());
                            lv1.getAccountList().add(lv6);
                        }
                    }
                }


                if (allmap.get("ORGANIZATIONNO").toString().equals(allmap.get("UP_ORG").toString())) {
                    continue;
                }

                lv1.setDelivery(new ArrayList<>());
                lv1.setAutoTake(new ArrayList<>());
                List<String> aa = new ArrayList<>();
                List<String> bb = new ArrayList<>();

                for (Map<String, Object> map : ShopOrderSet) {
                    if (allmap.get("ORGANIZATIONNO").toString().equals(map.get("SHOPID").toString())) {
                        if (map.get("ORDERSETTYPE").toString().equals("1")) {
                            DCP_OrglevQueryRes.Delivery deliv1 = res.new Delivery();
                            deliv1.setDeliveryType(map.get("ORDERSETVALUE").toString());
                            deliv1.setPriority(map.get("PRIORITY").toString());
                            lv1.getDelivery().add(deliv1);
                        }
                        if (map.get("ORDERSETTYPE").toString().equals("2")) {
                            lv1.setIsTake(map.get("ORDERSETVALUE").toString());
                        }
                        if (map.get("ORDERSETTYPE").toString().equals("3")) {
                            aa.add(map.get("ORDERSETVALUE").toString());
                        }
                        if (map.get("ORDERSETTYPE").toString().equals("4")) {
                            bb.add(map.get("ORDERSETVALUE").toString());
                        }

                    }
                }

                lv1.setProductionFocus(aa.toArray(new String[0]));
                lv1.setProduction(bb.toArray(new String[0]));

                lv1.setAutoTake(new ArrayList<>());
                if (ShopOrdertakeSet != null && !ShopOrdertakeSet.isEmpty()) {
                    for (Map<String, Object> map : ShopOrdertakeSet) {
                        if (allmap.get("ORGANIZATIONNO").toString().equals(map.get("SHOPID").toString())) {
                            DCP_OrglevQueryRes.AutoTake auto1 = res.new AutoTake();
                            auto1.setLoad_doctype(map.get("LOAD_DOCTYPE").toString());
                            auto1.setIsAutoTake(map.get("ISORDERTAKE").toString());
                            auto1.setIs_Auto_Express(map.get("IS_AUTO_EXPRESS").toString());

                            lv1.getAutoTake().add(auto1);
                        }
                    }
                }
                lv1.setTopOrgNo(lv1.getOrganizationNo());
                lv1.setTopOrgName(lv1.getOrgName());

                setchildren(lv1, allorg, req,domainName);
                res.getDatas().add(lv1);
            }
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

        return res;

    }

    //查询下级的组织
    protected List<Map<String, Object>> allorg(List<Map<String, Object>> allorg, String uporg, String eId) {
        List<Map<String, Object>> listorgtemp = new ArrayList();
        for (Map<String, Object> map : allorg) {
            if (map.get("UP_ORG").toString().equals(uporg) && map.get("EID").toString().equals(eId)) {
                listorgtemp.add(map);
            }
        }
        return listorgtemp;
    }

    //查询组织多语言
    protected List<Map<String, Object>> getorglang(List<Map<String, Object>> allorg, String uporg, String eId) {
        List<Map<String, Object>> listorgtemp = new ArrayList();
        for (Map<String, Object> map : allorg) {
            if (map.get("ORGANIZATIONNO").toString().equals(uporg) && map.get("EID").toString().equals(eId)) {
                listorgtemp.add(map);
            }
        }
        return listorgtemp;
    }

    //查询仓库
    protected List<Map<String, Object>> getwh(List<Map<String, Object>> allorg, String uporg, String eId) {
        List<Map<String, Object>> listorgtemp = new ArrayList();
        for (Map<String, Object> map : allorg) {
            if (map.get("ORGANIZATIONNO").toString().equals(uporg) && map.get("EID").toString().equals(eId)) {
                listorgtemp.add(map);
            }
        }
        return listorgtemp;
    }

    //查询仓库多语言
    protected List<Map<String, Object>> getwhlang(List<Map<String, Object>> allorg, String uporg, String eId, String WareHouse) {
        List<Map<String, Object>> listorgtemp = new ArrayList();
        for (Map<String, Object> map : allorg) {
            if (map.get("ORGANIZATIONNO").toString().equals(uporg) && map.get("EID").toString().equals(eId) && map.get("WAREHOUSE").toString().equals(WareHouse)) {
                listorgtemp.add(map);
            }
        }
        return listorgtemp;
    }

    //这里写一个递归的调用当前的方法
    protected void setchildren(DCP_OrglevQueryRes.Data lv2uporg, List<Map<String, Object>> allorg, DCP_OrglevQueryReq req,String domainName) throws Exception {
        //子层级上层的编码不能为空
        List<Map<String, Object>> listuporg = allorg(allorg, lv2uporg.getOrganizationNo(), lv2uporg.getEId());
        if (listuporg != null && !listuporg.isEmpty()) {
            lv2uporg.setChildren(new ArrayList<>());
            for (Map<String, Object> allmap : listuporg) {
                DCP_OrglevQueryRes.Data lv1 = new DCP_OrglevQueryRes().new Data();
                lv1.setEId(allmap.get("EID").toString());
                lv1.setOrganizationNo(allmap.get("ORGANIZATIONNO").toString());
                lv1.setOrg_Type(allmap.get("ORG_TYPE").toString());

                lv1.setTopOrgNo(lv2uporg.getTopOrgNo());
                lv1.setTopOrgName(lv2uporg.getTopOrgName());

                String DISCENTRE = allmap.get("DISCENTRE").toString();
                String fileName = allmap.get("FILENAME").toString();
                if (DISCENTRE.equals("Y")) {
                    lv1.setOrgForm("3");
                } else {
                    lv1.setOrgForm(allmap.get("ORG_FORM").toString());
                }

                lv1.setDisCentre(allmap.get("DISCENTRE").toString());
                lv1.setFileName(fileName);
                lv1.setIs_Corp(allmap.get("IS_CORP").toString());
                lv1.setCorp(allmap.get("CORP").toString());
                lv1.setCorpName(StringUtils.toString(allmap.get("CORPNAME"),""));
                lv1.setMemo(allmap.get("MEMO").toString());
                lv1.setStatus(allmap.get("STATUS").toString());
                lv1.setAddress(allmap.get("ADDRESS").toString());
                lv1.setZipcode(allmap.get("ZIPCODE").toString());
                lv1.setPhone(allmap.get("PHONE").toString());
                lv1.setFax(allmap.get("FAX").toString());
                lv1.setEmail(allmap.get("EMAIL").toString());
//				lv1.setBankname(allmap.get("FULLNAME").toString());
                lv1.setBankaccount(allmap.get("BANKACCOUNT").toString());
                lv1.setArea(allmap.get("AREA").toString());
                lv1.setIsdistbr(allmap.get("ISDISTBR").toString());
                lv1.setSname(allmap.get("SNAME").toString());
                lv1.setIn_Cost_WareHouse(allmap.get("IN_COST_WAREHOUSE").toString());
                lv1.setIn_Non_Cost_WareHouse(allmap.get("INV_COST_WAREHOUSE").toString());
                lv1.setInv_Cost_WareHouse(allmap.get("INV_COST_WAREHOUSE").toString());
                lv1.setInv_Non_Cost_WareHouse(allmap.get("INV_NON_COST_WAREHOUSE").toString());
                lv1.setOut_Cost_WareHouse(allmap.get("OUT_COST_WAREHOUSE").toString());
                lv1.setOut_Non_Cost_WareHouse(allmap.get("OUT_NON_COST_WAREHOUSE").toString());
                lv1.setEnablecredit(allmap.get("ENABLECREDIT").toString());
                lv1.setUp_Org(allmap.get("UP_ORG").toString());
                lv1.setUpOrgName(allmap.get("UP_ORGNAME").toString());
                lv1.setContact(StringUtils.toString(allmap.get("CONTACT"), ""));
                lv1.setContactName(StringUtils.toString(allmap.get("CONTACTNAME"), ""));
                lv1.setProvince(allmap.get("PROVINCE").toString());
                lv1.setCity(allmap.get("CITY").toString());
                lv1.setCounty(allmap.get("COUNTY").toString());
                lv1.setStreet(allmap.get("STREET").toString());
                lv1.setLat(allmap.get("LATITUDE").toString());
                lv1.setLng(allmap.get("LONGITUDE").toString());
                lv1.setStatus(allmap.get("STATUS").toString());
                lv1.setBelfirm(allmap.get("BELFIRM").toString());
                lv1.setBelfirmname(allmap.get("BELFIRMNAME").toString());

                lv1.setReturn_Cost_WareHouse(allmap.get("RETURN_COST_WAREHOUSE").toString());
                lv1.setManager(allmap.get("MANAGER").toString());
                lv1.setPartnerNo(allmap.get("PARTNERNO").toString());
                lv1.setTaxPayer_Type(allmap.get("TAXPAYER_TYPE").toString());
                lv1.setTaxPayer_No(allmap.get("TAXPAYER_NO").toString());
                lv1.setTaxation(allmap.get("TAXATION").toString());
                lv1.setOutPutTax(allmap.get("OUTPUTTAX").toString());
                lv1.setOutPutTaxName(allmap.get("OUTPUTTAXNAME").toString());
                lv1.setOutPutTaxRate(allmap.get("OUTPUTTAXRATE").toString());
                lv1.setInputTaxCode(allmap.get("INPUT_TAXCODE").toString());
                lv1.setInputTaxName(allmap.get("INPUTTAXNAME").toString());
                lv1.setInputTaxRate(allmap.get("INPUTTAXRATE").toString());

                lv1.setUSCI(allmap.get("USCI").toString());
                lv1.setRegisterNo(allmap.get("REGISTERNO").toString());
                lv1.setLegalPerson(allmap.get("LEGALPERSON").toString());
                lv1.setOpeningDate(DateFormatUtils.getDate(allmap.get("OPENINGDATE").toString()));
                lv1.setCloseDate(DateFormatUtils.getDate(allmap.get("CLOSEDATE").toString()));
                lv1.setOpStatus(allmap.get("OPSTATUS").toString());
                lv1.setBillCode(allmap.get("BILLCODE").toString());
                
                lv1.setCostCalculation(StringUtils.toString(allmap.get("COST_CALCULATION"),""));
                lv1.setCostDomain(StringUtils.toString(allmap.get("COST_DOMAIN"),""));

                lv1.setCreatorID(allmap.get("CREATEBY").toString());
                lv1.setLastmodifyID(allmap.get("MODIFYBY").toString());
                lv1.setCreate_datetime(allmap.get("CREATE_DTIME").toString());
                lv1.setLastmodify_datetime(allmap.get("MODIFY_DTIME").toString());
                lv1.setCreatorName(allmap.get("CREATEOPNAME").toString());
                lv1.setLastmodifyID(allmap.get("MODIFYOPNAME").toString());
                lv1.setCreatorDeptID(allmap.get("CREATEDEPTID").toString());
                lv1.setCreatorDeptName(allmap.get("CREATEDEPTNAME").toString());


                // yuanyy 2019-02-12 增加以下两个字段：  营业开始时间， 营业结束时间
                lv1.setShopBeginTime(allmap.get("SHOPBEGINTIME").toString());
                lv1.setShopEndTime(allmap.get("SHOPENDTIME").toString());
                lv1.setSellerGuiNo(allmap.get("SELLERGUINO").toString());
                lv1.setRange_Type(allmap.get("RANGE_TYPE").toString());
                lv1.setRange(allmap.get("RANGE").toString());

                lv1.setEinvoiceKey(allmap.get("EINVOICEKEY").toString());
                //lv1.setMachOrganizationNO(allmap.get("MACHORGANIZATIONNO").toString());
                lv1.setSelfBeginTime(allmap.get("SELFBEGINTIME").toString());
                lv1.setSelfEndTime(allmap.get("SELFENDTIME").toString());
                lv1.setDcCorpName(StringUtils.toString(allmap.get("DC_CORP_NAME"),""));
                lv1.setDcAddress(allmap.get("DC_ADDRESS").toString());
                lv1.setDcPhone(allmap.get("DC_PHONE").toString());
                lv1.setDeclCompany(allmap.get("DECL_COMPANY").toString());
                lv1.setThirdShop(allmap.get("THIRD_SHOP").toString());

                //赋值组织多语言
                List<Map<String, Object>> listorglang = getorglang(allorglang, allmap.get("ORGANIZATIONNO").toString(), allmap.get("EID").toString());
                if (listorglang != null && !listorglang.isEmpty()) {
                    lv1.setDatasOrg_lang(new ArrayList<>());
                    for (Map<String, Object> map : listorglang) {
                        DCP_OrglevQueryRes.OrgLang lv2 = new DCP_OrglevQueryRes().new OrgLang();
                        lv2.setEId(map.get("EID").toString());
                        lv2.setOrganizationNo(map.get("ORGANIZATIONNO").toString());
                        lv2.setLang_Type(map.get("LANG_TYPE").toString());
                        lv2.setOrg_Name(map.get("ORG_NAME").toString());
                        lv2.setStatus(map.get("STATUS").toString());

                        lv1.getDatasOrg_lang().add(lv2);

                        if (req.getLangType().equals(map.get("LANG_TYPE").toString())) {
                            lv1.setOrgName(map.get("ORG_NAME").toString());
                        }

                    }
                }
                //赋值仓库表
                List<Map<String, Object>> listwh = getwh(allorgwarhouse, allmap.get("ORGANIZATIONNO").toString(), allmap.get("EID").toString());
                if (listwh != null && !listwh.isEmpty()) {
                    lv1.setDataswareHouse(new ArrayList<>());
                    for (Map<String, Object> map : listwh) {
                        DCP_OrglevQueryRes.Warehouse lv3 = new DCP_OrglevQueryRes().new Warehouse();
                        lv3.setWareHouse_Type(map.get("WAREHOUSE_TYPE").toString());
                        lv3.setWareHouse(map.get("WAREHOUSE").toString());
                        lv3.setIs_Cost(map.get("IS_COST").toString());
                        lv3.setStatus(map.get("STATUS").toString());
                        lv3.setIsLocation(map.get("ISLOCATION").toString());
                        lv3.setStockManageType(map.get("STOCKMANAGETYPE").toString());
                        lv3.setIs_WMS(map.get("ISWMS").toString());

                        //赋值仓库多语言
                        List<Map<String, Object>> listwhlang = getwhlang(allwhlang, allmap.get("ORGANIZATIONNO").toString(), allmap.get("EID").toString(), map.get("WAREHOUSE").toString());
                        if (listwhlang != null && !listwhlang.isEmpty()) {
                            lv3.setDatasWare_Lang(new ArrayList<>());
                            for (Map<String, Object> mapwhlang : listwhlang) {
                                DCP_OrglevQueryRes.WarehouseLang lv4 = new DCP_OrglevQueryRes().new WarehouseLang();
                                lv4.setEId(mapwhlang.get("EID").toString());
                                lv4.setOrganizationNo(mapwhlang.get("ORGANIZATIONNO").toString());
                                lv4.setLang_Type(mapwhlang.get("LANG_TYPE").toString());
                                lv4.setWare_Name(mapwhlang.get("WAREHOUSE_NAME").toString());
                                lv4.setStatus(mapwhlang.get("STATUS").toString());

                                lv3.getDatasWare_Lang().add(lv4);
                            }
                        }
                        lv1.getDataswareHouse().add(lv3);
                    }
                }

                //赋值门店标签
                if (shoptag != null && !shoptag.isEmpty()) {
                    lv1.setShop_Tag(new ArrayList<>());
                    for (Map<String, Object> map : shoptag) {
                        if (map.get("SHOPID").toString().equals(lv1.getOrganizationNo())) {
                            DCP_OrglevQueryRes.ShopTag lv5 = new DCP_OrglevQueryRes().new ShopTag();
                            lv5.setTagNo(map.get("TAGNO").toString());
//							lv5.setTagName(map.get("TAGNAME").toString());
//							lv5.setStatus(map.get("STATUS").toString());

                            lv1.getShop_Tag().add(lv5);
                        }
                    }
                }


                //门店银行账号列表
                if (allAccountList != null && !allAccountList.isEmpty()) {
                    lv1.setAccountList(new ArrayList<>());
                    for (Map<String, Object> map : allAccountList) {
                        String eid_detail = map.get("EID").toString();
                        String organizationNo_detail = map.get("ORGANIZATIONNO").toString();
                        if (eid_detail.equals(lv1.getEId()) && organizationNo_detail.equals(lv1.getOrganizationNo())) {
                            DCP_OrglevQueryRes.Account lv6 = new DCP_OrglevQueryRes().new Account();
                            lv6.setEId(eid_detail);
                            lv6.setOrganizationNo(organizationNo_detail);
                            lv6.setAccount(map.get("ACCOUNT").toString());
                            lv6.setBankNo(map.get("BANKNO").toString());
                            lv6.setBankName(map.get("FULLNAME").toString());
                            lv6.setStatus(map.get("STATUS").toString());
                            lv1.getAccountList().add(lv6);
                        }
                    }
                }


                if (allmap.get("ORGANIZATIONNO").toString().equals(allmap.get("UP_ORG").toString())) {
                    continue;
                }


                lv1.setDelivery(new ArrayList<>());
                lv1.setAutoTake(new ArrayList<>());
                List<String> aa = new ArrayList<>();
                List<String> bb = new ArrayList<>();

                for (Map<String, Object> map : ShopOrderSet) {
                    if (allmap.get("ORGANIZATIONNO").toString().equals(map.get("SHOPID").toString())) {
                        if (map.get("ORDERSETTYPE").toString().equals("1")) {
                            DCP_OrglevQueryRes.Delivery deliv1 = new DCP_OrglevQueryRes().new Delivery();
                            deliv1.setDeliveryType(map.get("ORDERSETVALUE").toString());
                            deliv1.setPriority(map.get("PRIORITY").toString());
                            lv1.getDelivery().add(deliv1);
                        }
                        if (map.get("ORDERSETTYPE").toString().equals("2")) {
                            lv1.setIsTake(map.get("ORDERSETVALUE").toString());
                        }

                        if (map.get("ORDERSETTYPE").toString().equals("3")) {
                            aa.add(map.get("ORDERSETVALUE").toString());
                        }
                        if (map.get("ORDERSETTYPE").toString().equals("4")) {
                            bb.add(map.get("ORDERSETVALUE").toString());
                        }

                    }
                }
                lv1.setProductionFocus(aa.toArray(new String[0]));
                lv1.setProduction(bb.toArray(new String[0]));

                lv1.setAutoTake(new ArrayList<>());
                if (ShopOrdertakeSet != null && !ShopOrdertakeSet.isEmpty()) {
                    for (Map<String, Object> map : ShopOrdertakeSet) {
                        if (allmap.get("ORGANIZATIONNO").toString().equals(map.get("SHOPID").toString())) {
                            DCP_OrglevQueryRes.AutoTake auto1 = new DCP_OrglevQueryRes().new AutoTake();
                            auto1.setLoad_doctype(map.get("LOAD_DOCTYPE").toString());
                            auto1.setIsAutoTake(map.get("ISORDERTAKE").toString());
                            auto1.setIs_Auto_Express(map.get("IS_AUTO_EXPRESS").toString());

                            lv1.getAutoTake().add(auto1);
                        }
                    }
                }


                lv1.setIsDept(allmap.get("ISDEPT").toString());
                List<Map<String, Object>> deptFilter = allDeptList.stream().filter(x -> x.get("DEPARTNO").toString().equals(lv1.getOrganizationNo())).collect(Collectors.toList());
                if(deptFilter.size()>0){
                    lv1.setUpDeptNo(deptFilter.get(0).get("UPDEPARTNO").toString());
                    lv1.setUpDeptName(deptFilter.get(0).get("UPDEPARTNAME").toString());
                }
                lv1.setIs_prod_org(allmap.get("IS_PROD_ORG").toString());
                lv1.setMachOrganizationNo(allmap.get("MACHORGANIZATIONNO").toString());
                lv1.setMachOrganizationName(StringUtils.toString(allmap.get("MACHORGANIZATIONNAME"),""));
                lv1.setOrderDistbr(allmap.get("ORDERDISTBR").toString());
                lv1.setOrderDistbrName(allmap.get("ORDERDISTBRNAME").toString());
                lv1.setTaxArea(allmap.get("TAXAREA").toString());
                lv1.setCurrency(allmap.get("CURRENCY").toString());
                List<Map<String, Object>> platFormFilter = allPlatFormList.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(lv1.getOrganizationNo())).collect(Collectors.toList());
                if(platFormFilter.size()>0) {
                    lv1.setMeiTuanShopId(platFormFilter.get(0).get("MEITUANSHOPID").toString());
                    lv1.setDianPingShopId(platFormFilter.get(0).get("DIANPINGSHOPID").toString());
                }
                lv1.setIc_cost_warehouse(allmap.get("IC_COST_WAREHOUSE").toString());
                lv1.setIc_cost_warehouseName(allmap.get("IC_COST_WAREHOUSENAME").toString());

                List<Map<String, Object>> routeFilter = allRouteList.stream().filter(x -> x.get("CODE").toString().equals(lv1.getOrganizationNo())).collect(Collectors.toList());
                if(routeFilter.size()>0){
                    lv1.setRouteNo(routeFilter.get(0).get("ROUTENO").toString());
                }
                lv1.setLicenseList(new ArrayList<>());
                Stream<Map<String, Object>> licenseFilter = allLicenseList.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(lv1.getOrganizationNo()));
                licenseFilter.forEach(x -> {
                    DCP_OrglevQueryRes.LicenseList licenseList = new DCP_OrglevQueryRes().new LicenseList();
                    licenseList.setItem(x.get("ITEM").toString());
                    licenseList.setImageType(x.get("IMGTYPE").toString());
                    licenseList.setLicenseImgUrl(domainName+x.get("LICENSEIMG").toString());
                    licenseList.setLicenseImg(x.get("LICENSEIMG").toString());
                    lv1.getLicenseList().add(licenseList);
                });

                setchildren(lv1, allorg, req,domainName);
                lv2uporg.getChildren().add(lv1);
            }

        }

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_OrglevQueryReq req) throws Exception {
        return null;
    }

}
