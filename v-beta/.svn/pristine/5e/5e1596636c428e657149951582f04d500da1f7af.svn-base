package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrgCreateReq;
import com.dsc.spos.json.cust.req.DCP_OrgUpdateReq;
import com.dsc.spos.json.cust.res.DCP_OrgUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class DCP_OrgUpdate extends SPosAdvanceService<DCP_OrgUpdateReq, DCP_OrgUpdateRes> {

    @Override
    protected void processDUID(DCP_OrgUpdateReq req, DCP_OrgUpdateRes res) throws Exception {
        // TODO Auto-generated method stub
        if (req.getRequest().getOrganizationID() != null && req.getRequest().getUp_Org() != null) {
            if (req.getRequest().getOrganizationID().equals(req.getRequest().getUp_Org())) {
                res.setSuccess(false);
                res.setServiceDescription("上级组织不能选择本组织");
                return;
            }
        }

        if ("Y".equals(req.getRequest().getIs_Corp()) && "2".equals(req.getRequest().getTaxpayer_Type()) && !checkOutputTax(req.getRequest().getOutPutTax())) {
            res.setSuccess(false);
            res.setServiceDescription("销项税别不正确");
            return;
        }

        //1.法人IS_CORP=Y时&纳税人性质TAXPAYER_TYPE='2'（小规模纳税人），INPUT_TAXCODE（进项税别）不可为空且税率必须为0！
        if("Y".equals(req.getRequest().getIs_Corp())&&"2".equals(req.getRequest().getTaxpayer_Type())){
            if(Check.Null(req.getRequest().getInputTaxCode())){
                res.setSuccess(false);
                res.setServiceDescription( "进项税别不可为空！");
                return;
            }

            String taxCodeSql="select * from dcp_taxcategory a where a.eid='"+req.geteId()+"' and a.taxcode='"+req.getRequest().getInputTaxCode()+"' ";
            List<Map<String, Object>> list = this.doQueryData(taxCodeSql, null);
            if(list.size()==0){
                res.setSuccess(false);
                res.setServiceDescription( "进项税别不存在！");
                return;
            }

            BigDecimal inputTaxRate = new BigDecimal(list.get(0).get("TAXRATE").toString());
            if (inputTaxRate.compareTo(new BigDecimal("0")) != 0) {
                res.setSuccess(false);
                res.setServiceDescription( "进项税别的税率必须为0！");
                return;
            }

        }

        if("N".equals(req.getRequest().getIsDept())){
            String departSql="select * from dcp_department a where a.eid='"+req.geteId()+"' and a.DEPARTNO='"+req.getRequest().getOrganizationID()+"' and a.status<>'-1' ";
            List<Map<String, Object>> list = this.doQueryData(departSql, null);
            if(list.size()>0){
                res.setSuccess(false);
                res.setServiceDescription("部门"+req.getRequest().getOrganizationID()+"不为未启用状态,不可删除!");
                return;
            }
        }



        String fileName = req.getRequest().getFileName() == null ? "" : req.getRequest().getFileName();
        String fileData = req.getRequest().getFileData();

        String lastModifyTime = DateFormatUtils.getNowDateTime();

        ColumnDataValue condition = new ColumnDataValue();
        ColumnDataValue dcp_org = new ColumnDataValue();

        condition.add("OrganizationNo", DataValues.newString(req.getRequest().getOrganizationID()));
        condition.add("EID", DataValues.newString(req.geteId()));

        dcp_org.add("Org_Form", DataValues.newString(req.getRequest().getOrgForm()));
        dcp_org.add("ISDISTBR", DataValues.newString(req.getRequest().getIsdistbr()));
        dcp_org.add("SNAME", DataValues.newString(req.getRequest().getSname()));
//        dcp_org.add("BELFIRM", DataValues.newString(req.getRequest().getBelfirm()));
        dcp_org.add("BELFIRM", DataValues.newString(req.getRequest().getCorp())); //20241008 modi by 11217 for 修改取值
        dcp_org.add("Memo", DataValues.newString(req.getRequest().getMemo()));
        dcp_org.add("STATUS", DataValues.newInteger(req.getRequest().getStatus()));
        dcp_org.add("Is_Corp", DataValues.newString(req.getRequest().getIs_Corp()));
        dcp_org.add("Corp", DataValues.newString(req.getRequest().getCorp()));
        dcp_org.add("ZIPCODE", DataValues.newString(req.getRequest().getZipcode()));
        dcp_org.add("PROVINCE", DataValues.newString(req.getRequest().getProvince()));
        dcp_org.add("CITY", DataValues.newString(req.getRequest().getCity()));
        dcp_org.add("COUNTY", DataValues.newString(req.getRequest().getCounty()));
        dcp_org.add("STREET", DataValues.newString(req.getRequest().getStreet()));
        dcp_org.add("ADDRESS", DataValues.newString(req.getRequest().getAddress()));
        dcp_org.add("LATITUDE", DataValues.newString(req.getRequest().getLatitude()));
        dcp_org.add("LONGITUDE", DataValues.newString(req.getRequest().getLongitude()));
        dcp_org.add("PHONE", DataValues.newString(req.getRequest().getPhone()));
        dcp_org.add("FAX", DataValues.newString(req.getRequest().getFax()));
        dcp_org.add("EMAIL", DataValues.newString(req.getRequest().getEmail()));
        dcp_org.add("BANKNAME", DataValues.newString(req.getRequest().getBankname()));
        dcp_org.add("BANKACCOUNT", DataValues.newString(req.getRequest().getBankaccount()));

        dcp_org.add("In_Cost_WareHouse", DataValues.newString(req.getRequest().getIn_Cost_WareHouse()));
        dcp_org.add("in_Non_Cost_WareHouse", DataValues.newString(req.getRequest().getIn_Non_Cost_WareHouse()));
        dcp_org.add("Out_Cost_WareHouse", DataValues.newString(req.getRequest().getOut_Cost_WareHouse()));
        dcp_org.add("Out_Non_Cost_WareHouse", DataValues.newString(req.getRequest().getInv_Non_Cost_WareHouse()));
        dcp_org.add("Inv_Cost_WareHouse", DataValues.newString(req.getRequest().getInv_Cost_WareHouse()));
        dcp_org.add("Inv_Non_Cost_WareHouse", DataValues.newString(req.getRequest().getInv_Non_Cost_WareHouse()));

        dcp_org.add("FILENAME", DataValues.newString(req.getRequest().getFileName()));
        dcp_org.add("AREA", DataValues.newDecimal(req.getRequest().getArea()));
        dcp_org.add("SHOPBEGINTIME", DataValues.newString(req.getRequest().getShopBeginTime()));
        dcp_org.add("SHOPENDTIME", DataValues.newString(req.getRequest().getShopEndTime()));

        dcp_org.add("ENABLECREDIT", DataValues.newString(req.getRequest().getEnablecredit()));
        dcp_org.add("SELLERGUINO", DataValues.newString(req.getRequest().getSellerGuiNo()));
        dcp_org.add("EINVOICEKEY", DataValues.newString(req.getRequest().getEinvoiceKey()));
        dcp_org.add("DC_CORP_NAME", DataValues.newString(req.getRequest().getDcCorpName()));
        dcp_org.add("DC_ADDRESS", DataValues.newString(req.getRequest().getDcAddress()));
        dcp_org.add("DC_PHONE", DataValues.newString(req.getRequest().getDcPhone()));
//        dcp_org.add("CORP_NAME",DataValues.newString(req.getRequest().getCorp()));
        dcp_org.add("DECL_COMPANY", DataValues.newString(req.getRequest().getDeclCompany()));

        dcp_org.add("THIRD_SHOP", DataValues.newString(req.getRequest().getThirdShop()));
//        dcp_org.add("SELFBEGINTIME",DataValues.newString(req.getRequest().getShopBeginTime()));
//        dcp_org.add("SELFENDTIME",DataValues.newString(req.getRequest().getShopBeginTime()));
        dcp_org.add("DISCENTRE", DataValues.newString(req.getRequest().getDisCentre()));

        dcp_org.add("RETURN_COST_WAREHOUSE", DataValues.newString(req.getRequest().getReturn_Cost_WareHouse()));
        dcp_org.add("MANAGER", DataValues.newString(req.getRequest().getManager()));
        dcp_org.add("PARTNERNO", DataValues.newString(req.getRequest().getPartnerNo()));

        dcp_org.add("TAXPAYER_TYPE", DataValues.newString(req.getRequest().getTaxpayer_Type()));
        dcp_org.add("TAXPAYER_NO", DataValues.newString(req.getRequest().getTaxpay_No()));
        dcp_org.add("INPUT_TAXCODE", DataValues.newString(req.getRequest().getInputTaxCode()));

        dcp_org.add("TAXATION", DataValues.newString(req.getRequest().getTaxation()));
        dcp_org.add("OUTPUTTAX", DataValues.newString(req.getRequest().getOutPutTax()));
        dcp_org.add("USCI", DataValues.newString(req.getRequest().getUSCI()));
        dcp_org.add("REGISTERNO", DataValues.newString(req.getRequest().getRegisterNo()));
        dcp_org.add("OPENINGDATE", DataValues.newDate(req.getRequest().getOpeningDate()));
        dcp_org.add("LEGALPERSON", DataValues.newString(req.getRequest().getLegalPerson()));
        dcp_org.add("CLOSEDATE", DataValues.newDate(req.getRequest().getCloseDate()));
        dcp_org.add("OPSTATUS", DataValues.newString(req.getRequest().getOpStatus()));
//        dcp_org.add("BILLCODE", DataValues.newDate(req.getRequest().getBillCode()));
        dcp_org.add("BILLCODE", DataValues.newString(req.getRequest().getOrganizationID())); //20241008 modi by 11217 for dd
//        dcp_org.add("BILLCODE",DataValues.newDate(req.getRequest().getbillcode()));
        dcp_org.add("CONTACT", DataValues.newString(req.getRequest().getContact()));

        dcp_org.add("COST_CALCULATION", DataValues.newString(req.getRequest().getCostCalculation()));
        dcp_org.add("COST_DOMAIN", DataValues.newString(req.getRequest().getCostDomain()));

        dcp_org.add("MODIFYBY", DataValues.newString(req.getEmployeeNo()));
        dcp_org.add("MODIFY_DTIME", DataValues.newDate(lastModifyTime));

        dcp_org.add("ISDEPT",DataValues.newString(req.getRequest().getIsDept()));
        dcp_org.add("ORDERDISTBR",DataValues.newString(req.getRequest().getOrderDistbr()));
        dcp_org.add("MACHORGANIZATIONNO",DataValues.newString(req.getRequest().getMachOrganizationNo()));
        dcp_org.add("TAXAREA",DataValues.newString(req.getRequest().getTaxArea()));
        dcp_org.add("CURRENCY",DataValues.newString(req.getRequest().getCurrency()));
        dcp_org.add("IS_PROD_ORG",DataValues.newString(req.getRequest().getIs_prod_org()));
        dcp_org.add("IC_COST_WAREHOUSE",DataValues.newString(req.getRequest().getIc_cost_warehouse()));


        UptBean ub = DataBeans.getUptBean("DCP_ORG", condition, dcp_org);

        this.addProcessData(new DataProcessBean(ub));



        if (!Check.Null(fileData) && !Check.Null(fileName)) {
            try {
                String dirpath = System.getProperty("catalina.home") + "\\webapps\\shopImgs";

                //fileData数据
                String base64 = fileData;
                //有附件再保存
                if (Check.Null(base64) == false) {
                    base64 = base64.substring(base64.indexOf("base64,") + 7);
                    //http://blog.csdn.net/jsjwbxzy/article/details/45970231
                    //http请求中传输base64出现加号变空格的解决办法
                    base64 = base64.replace(" ", "+");//替换空格/*TMD接收进来有空格，发现本来应该是+号的*/
                    //根据RFC822规定，BASE64Encoder编码每76个字符，还需要加上一个回车换行
                    //http://blog.csdn.net/u010953266/article/details/52590570
                    int iLEN = base64.length() / 76 + 1;
                    String sNewBase64 = "";
                    for (int i = 0; i < iLEN; i++) {
                        if (i * 76 + 76 > base64.length()) {
                            sNewBase64 = sNewBase64 + base64.substring(i * 76, base64.length());
                        } else {
                            sNewBase64 = sNewBase64 + base64.substring(i * 76, i * 76 + 76) + "\r\n";
                        }
                    }


                    //Base64解码
                    @SuppressWarnings("restriction")
                    byte[] b = Base64.decodeBase64(base64);

                    File file = new File(dirpath);

                    //如果文件夹不存在则创建   \\webapps\\goodsimages
                    if (!file.exists() && !file.isDirectory()) {
                        boolean b1 = file.mkdir();
                        if (b1 == false) {
                            ////System.out.println("webapps已经存在或失败");
                        }
                    }

                    file = null;

                    //生成文件
                    String imgFilePath = dirpath + "\\" + fileName;
                    OutputStream out = new FileOutputStream(imgFilePath);
                    out.write(b);
                    out.flush();
                    out.close();

                } else {
                    //如果服务器上存在此图片删除
                    File file = new File(dirpath + "\\" + fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    file = null;

                }
            } catch (Exception ex) {

            }

        }


        //层级表
        DelBean del = null;
        InsBean ins = null;

        if (req.getRequest().getUp_Org() != null && !req.getRequest().getUp_Org().isEmpty()) {
            del = new DelBean("DCP_Org_Level");
            del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            del.addCondition("OrganizationNo", new DataValue(req.getRequest().getOrganizationID(), Types.VARCHAR));

            this.pData.add(new DataProcessBean(del));

            ColumnDataValue orgLev = new ColumnDataValue();
            orgLev.add("EID", DataValues.newString(req.geteId()));
            orgLev.add("OrganizationNo", DataValues.newString(req.getRequest().getOrganizationID()));
            if (StringUtils.isEmpty(req.getRequest().getOrg_Type())) {
                orgLev.add("ORG_TYPE", DataValues.newString(" "));
            } else {
                orgLev.add("ORG_TYPE", DataValues.newString(req.getRequest().getOrg_Type()));
            }

            orgLev.add("UP_ORG", DataValues.newString(req.getRequest().getUp_Org()));
            orgLev.add("STATUS", DataValues.newInteger(100));
            orgLev.add("Version", DataValues.newString(1));

            ins = DataBeans.getInsBean("DCP_Org_Level", orgLev);
            this.pData.add(new DataProcessBean(ins));
        }

        //处理组织多语言表
        del = new DelBean("DCP_ORG_LANG");
        del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        del.addCondition("OrganizationNo", new DataValue(req.getRequest().getOrganizationID(), Types.VARCHAR));

        this.pData.add(new DataProcessBean(del));

        //是否部门都得删
        del = new DelBean("DCP_DEPARTMENT");
        del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        del.addCondition("DEPARTNO", new DataValue(req.getRequest().getOrganizationID(), Types.VARCHAR));
        this.pData.add(new DataProcessBean(del));

        del = new DelBean("DCP_DEPARTMENT_LANG");
        del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        del.addCondition("DEPARTNO", new DataValue(req.getRequest().getOrganizationID(), Types.VARCHAR));
        this.pData.add(new DataProcessBean(del));
        if("Y".equals(req.getRequest().getIsDept())){
            String departmentName=req.getRequest().getOrganizationID();
            if(CollUtil.isNotEmpty(req.getRequest().getDatasOrg_Lang())){
                departmentName=req.getRequest().getDatasOrg_Lang().get(0).getOrg_Name();

                for (DCP_OrgUpdateReq.OrgLang orgLang:req.getRequest().getDatasOrg_Lang()){
                    String[] colnamelev={"EID","Lang_Type","DEPARTNO","DEPARTNAME","STATUS"};
                    DataValue[] insValuelev={
                            new DataValue(req.geteId(), Types.VARCHAR)
                            ,new DataValue(orgLang.getLang_type(), Types.VARCHAR)
                            ,new DataValue(req.getRequest().getOrganizationID(), Types.VARCHAR)
                            ,new DataValue(orgLang.getOrg_Name(), Types.VARCHAR)
                            ,new DataValue("100", Types.VARCHAR)
                    };
                    InsBean ins2=new InsBean("DCP_DEPARTMENT_LANG", colnamelev);
                    ins2.addValues(insValuelev);
                    this.pData.add(new DataProcessBean(ins2));
                }

            }
            String[] colname={"EID","DEPARTNO","DEPARTNAME","UPDEPARTNO","MEMO","STATUS","CREATEBY",
                    "CREATE_DTIME","MODIFYBY","MODIFY_DTIME"};
            DataValue[] insValue={new DataValue(req.geteId(), Types.VARCHAR)
                    ,new DataValue(req.getRequest().getOrganizationID(), Types.VARCHAR)
                    ,new DataValue(departmentName, Types.VARCHAR)
                    ,new DataValue(req.getRequest().getUpDeptNo(), Types.VARCHAR)
                    ,new DataValue(req.getRequest().getMemo(), Types.VARCHAR)
                    ,new DataValue("-1", Types.VARCHAR)
                    ,new DataValue(req.getOpNO(), Types.VARCHAR)
                    ,new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR)
                    ,new DataValue(req.getOpNO(), Types.VARCHAR)
                    ,new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR)};
            InsBean ins1=new InsBean("DCP_DEPARTMENT", colname);
            ins1.addValues(insValue);
            this.pData.add(new DataProcessBean(ins1));



        }



        del = new DelBean("DCP_ORG_LICENSE");
        del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        del.addCondition("OrganizationNo", new DataValue(req.getRequest().getOrganizationID(), Types.VARCHAR));
        this.pData.add(new DataProcessBean(del));
        if(CollUtil.isNotEmpty(req.getRequest().getLicenseList())){
            int licenseItem=0;
            for(DCP_OrgUpdateReq.LicenseList licenseList:req.getRequest().getLicenseList()){
                licenseItem++;
                ColumnDataValue license = new ColumnDataValue();
                license.add("EID", DataValues.newString(req.geteId()));
                license.add("OrganizationNo", DataValues.newString(req.getRequest().getOrganizationID()));
                license.add("IMGTYPE", DataValues.newString(licenseList.getImageType()));
                license.add("LICENSEIMG", DataValues.newString(licenseList.getLicenseImg()));
                license.add("ITEM", DataValues.newString(licenseItem));

                ins = DataBeans.getInsBean("DCP_ORG_LICENSE", license);
                this.pData.add(new DataProcessBean(ins));
            }
        }

        if (req.getRequest().getDatasOrg_Lang() != null) {
            for (DCP_OrgUpdateReq.OrgLang orglang : req.getRequest().getDatasOrg_Lang()) {
                ColumnDataValue lang = new ColumnDataValue();
                lang.add("EID", DataValues.newString(req.geteId()));
                lang.add("OrganizationNo", DataValues.newString(req.getRequest().getOrganizationID()));
                lang.add("Lang_Type", DataValues.newString(orglang.getLang_type()));
                lang.add("Org_Name", DataValues.newString(orglang.getOrg_Name()));
                lang.add("SNAME", DataValues.newString(orglang.getSname()));

                ins = DataBeans.getInsBean("DCP_Org_Lang", lang);
                this.pData.add(new DataProcessBean(ins));
            }
        }
        //处理仓库
        del = new DelBean("DCP_WareHouse");
        del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        del.addCondition("OrganizationNo", new DataValue(req.getRequest().getOrganizationID(), Types.VARCHAR));
        this.pData.add(new DataProcessBean(del));
        if (req.getRequest().getDataswareHouse() != null) {
            for (DCP_OrgUpdateReq.Warehouse orgwh : req.getRequest().getDataswareHouse()) {
                ColumnDataValue warehouse = new ColumnDataValue();
                warehouse.add("OrganizationNo", DataValues.newString(req.getRequest().getOrganizationID()));
                warehouse.add("EID", DataValues.newString(req.geteId()));
                warehouse.add("WareHouse", DataValues.newString(orgwh.getWareHouse()));
                warehouse.add("WareHouse_Type", DataValues.newString(orgwh.getWareHouse_Type()));
                warehouse.add("IS_COST", DataValues.newString(orgwh.getIs_Cost()));
                warehouse.add("STATUS", DataValues.newInteger(orgwh.getStatus()));
                warehouse.add("STOCKMANAGETYPE", DataValues.newString(orgwh.getStockManageType()));
                warehouse.add("ISWMS", DataValues.newString(orgwh.getIsWMS()));
                warehouse.add("isLocation", DataValues.newString(orgwh.getIsLocation()));
                warehouse.add("ISCHECKSTOCK", DataValues.newString(orgwh.getIsCheckStock()));

                ins = DataBeans.getInsBean("DCP_WareHouse", warehouse);
                this.pData.add(new DataProcessBean(ins));

                //仓库多语言
                del = new DelBean("DCP_WareHouse_Lang");
                del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                del.addCondition("OrganizationNo", new DataValue(req.getRequest().getOrganizationID(), Types.VARCHAR));
                del.addCondition("WareHouse", new DataValue(orgwh.getWareHouse(), Types.VARCHAR));
                this.pData.add(new DataProcessBean(del));

                if (orgwh.getDatasWare_Lang() != null) {
                    for (DCP_OrgUpdateReq.WarehouseLang orgwhlang : orgwh.getDatasWare_Lang()) {
                        ColumnDataValue warehouseLang = new ColumnDataValue();
                        warehouseLang.add("OrganizationNo", DataValues.newString(req.getRequest().getOrganizationID()));
                        warehouseLang.add("EID", DataValues.newString(req.geteId()));
                        warehouseLang.add("WareHouse", DataValues.newString(orgwh.getWareHouse()));
                        warehouseLang.add("Lang_Type", DataValues.newString(orgwhlang.getLang_Type()));
                        warehouseLang.add("WareHouse_Name", DataValues.newString(orgwhlang.getWare_Name()));
                        warehouseLang.add("STATUS", DataValues.newInteger(orgwhlang.getStatus()));

                        ins = DataBeans.getInsBean("DCP_WareHouse_Lang", warehouseLang);

                        this.pData.add(new DataProcessBean(ins));
                    }
                }

            }
        }

        //门店标签
        del = new DelBean("DCP_Shop_Tag");
        del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        del.addCondition("SHOPID", new DataValue(req.getRequest().getOrganizationID(), Types.VARCHAR));
        this.pData.add(new DataProcessBean(del));
        if (req.getRequest().getShop_tag() != null && !req.getRequest().getShop_tag().isEmpty()) {
            for (DCP_OrgUpdateReq.ShopTag shoptag : req.getRequest().getShop_tag()) {
                String[] colnamelev5 = {"EID", "SHOPID", "TagNo", "STATUS"};
                DataValue[] insValuelev5 = {
                        new DataValue(req.geteId(), Types.VARCHAR)
                        , new DataValue(req.getRequest().getOrganizationID(), Types.VARCHAR)
                        , new DataValue(shoptag.getTagNo(), Types.VARCHAR)
                        , new DataValue(req.getRequest().getStatus(), Types.VARCHAR)
                };
                ins = new InsBean("DCP_Shop_Tag", colnamelev5);
                ins.addValues(insValuelev5);
                this.pData.add(new DataProcessBean(ins));
            }
        }

        //缴款单银行账号
        del = new DelBean("DCP_SHOP_ACCOUNT");
        del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        del.addCondition("SHOPID", new DataValue(req.getRequest().getOrganizationID(), Types.VARCHAR));
        this.pData.add(new DataProcessBean(del));

        if (req.getRequest().getAccountList() != null && !req.getRequest().getAccountList().isEmpty()) {
            for (DCP_OrgUpdateReq.Account shoptag : req.getRequest().getAccountList()) {
                String[] colnamelev5 = {"EID", "SHOPID", "ACCOUNT", "BANKNO", "STATUS"};
                DataValue[] insValuelev5 = {
                        new DataValue(req.geteId(), Types.VARCHAR)
                        , new DataValue(req.getRequest().getOrganizationID(), Types.VARCHAR)
                        , new DataValue(shoptag.getAccount(), Types.VARCHAR)
                        , new DataValue(shoptag.getBankNo(), Types.VARCHAR)
                        , new DataValue(shoptag.getStatus(), Types.VARCHAR)
                };
                ins = new InsBean("DCP_SHOP_ACCOUNT", colnamelev5);
                ins.addValues(insValuelev5);
                this.pData.add(new DataProcessBean(ins));

            }
        }

        del = new DelBean("DCP_ORG_PLATFORM");
        del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        del.addCondition("OrganizationNo", new DataValue(req.getRequest().getOrganizationID(), Types.VARCHAR));
        this.pData.add(new DataProcessBean(del));
        if(Check.NotNull(req.getRequest().getMeiTuanShopId())||Check.NotNull(req.getRequest().getDianPingShopId())){
            ColumnDataValue op = new ColumnDataValue();
            op.add("EID", DataValues.newString(req.geteId()));
            op.add("OrganizationNo", DataValues.newString(req.getRequest().getOrganizationID()));
            op.add("MEITUANSHOPID", DataValues.newString(req.getRequest().getMeiTuanShopId()));
            op.add("DIANPINGSHOPID", DataValues.newString(req.getRequest().getDianPingShopId()));

            ins = DataBeans.getInsBean("DCP_ORG_PLATFORM", op);
            this.pData.add(new DataProcessBean(ins));
        }

        if(Check.NotNull(req.getRequest().getRouteNo())){
            del = new DelBean("MES_ROUTE");
            del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            del.addCondition("ROUTENO", new DataValue(req.getRequest().getRouteNo(), Types.VARCHAR));
            this.pData.add(new DataProcessBean(del));

            ColumnDataValue mainColumns=new ColumnDataValue();
            mainColumns.add("EID", DataValues.newString(req.geteId()));
            mainColumns.add("ROUTENO", DataValues.newString(req.getRequest().getRouteNo()));
            mainColumns.add("ROUTENAME", DataValues.newString(req.getRequest().getRouteNo()));
            mainColumns.add("STATUS", DataValues.newInteger("-1"));
            mainColumns.add("MEMO", DataValues.newString(""));
            mainColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));
            mainColumns.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
            mainColumns.add("CREATETIME", DataValues.newDate(lastModifyTime));
            mainColumns.add("LASTMODIOPID", DataValues.newString(req.getOpNO()));
            mainColumns.add("LASTMODITIME", DataValues.newDate(lastModifyTime));


            String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
            DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
            InsBean ib=new InsBean("MES_ROUTE",mainColumnNames);
            ib.addValues(mainDataValues);
            this.addProcessData(new DataProcessBean(ib));

        }



        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    private boolean checkOutputTax(String outputTax) throws Exception {
        String querySql = " SELECT * FROM DCP_TAXCATEGORY WHERE TAXCODE ='%s' ";
        querySql = String.format(querySql, outputTax);

        List data = doQueryData(querySql, null);

        if (null == data || data.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_OrgUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrgUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrgUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrgUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if (req.getRequest().getDatasOrg_Lang() == null || req.getRequest().getDatasOrg_Lang().isEmpty()) {
            errMsg.append("组织名称多语言不可为空, ");
            isFail = true;
        } else {
            boolean ishave = false;
            for (DCP_OrgUpdateReq.OrgLang orglang : req.getRequest().getDatasOrg_Lang()) {
                if (orglang.getLang_type().equals(req.getLangType())) {
                    ishave = true;
                    break;
                }
            }
            if (ishave == false) {
                errMsg.append("组织名称多语言没有维护" + req.getLangType());
                isFail = true;
            }
        }

        if ("Y".equals(req.getRequest().getIs_Corp()) && "2".equals(req.getRequest().getTaxpayer_Type()) && StringUtils.isEmpty(req.getRequest().getOutPutTax())) {
            errMsg.append("销项税别不可为空");
            isFail = true;
        }


        if ("Y".equals(req.getRequest().getIs_Corp())) {
            if (StringUtils.isEmpty(req.getRequest().getCostCalculation()) || StringUtils.isEmpty(req.getRequest().getCostDomain())) {
                errMsg.append("当前组织为法人时‘成本计算方式’和‘采用成本域否’不可为空");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_OrgUpdateReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_OrgUpdateReq>() {
        };
    }

    @Override
    protected DCP_OrgUpdateRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_OrgUpdateRes();
    }

}
