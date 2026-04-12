package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CustomerPOrderDetailReq;
import com.dsc.spos.json.cust.res.DCP_CustomerPOrderDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_CustomerPOrderDetail extends SPosBasicService<DCP_CustomerPOrderDetailReq, DCP_CustomerPOrderDetailRes> {

    @Override
    protected boolean isVerifyFail(DCP_CustomerPOrderDetailReq req) throws Exception {
        boolean isFail = false;

        return false;
    }

    @Override
    protected TypeToken<DCP_CustomerPOrderDetailReq> getRequestType() {
        return new TypeToken<DCP_CustomerPOrderDetailReq>() {
        };
    }

    @Override
    protected DCP_CustomerPOrderDetailRes getResponseType() {
        return new DCP_CustomerPOrderDetailRes();
    }

    @Override
    protected DCP_CustomerPOrderDetailRes processJson(DCP_CustomerPOrderDetailReq req) throws Exception {
        try {
            DCP_CustomerPOrderDetailRes res = this.getResponse();
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            res.setDatas(res.new Datas());
            res.getDatas().setDataList(new ArrayList<>());
            if (getQData != null && !getQData.isEmpty()) {
                Map<String, Object> oneHeader = getQData.get(0);
                DCP_CustomerPOrderDetailRes.DataList oneLv1 = res.new DataList();
                oneLv1.setDetail(new ArrayList<>());

                oneLv1.setTotDiscAmt(StringUtils.toString(oneHeader.get("TOTDISCAMT"), ""));
                oneLv1.setMemo(StringUtils.toString(oneHeader.get("MEMO"), ""));
                oneLv1.setTotTaxAmt(StringUtils.toString(oneHeader.get("TOTTAXAMT"), ""));
                oneLv1.setDeliverWarehouse(StringUtils.toString(oneHeader.get("DELIVERWAREHOUSE"), ""));
                oneLv1.setPayType(StringUtils.toString(oneHeader.get("PAYTYPE"), ""));
                oneLv1.setBillDateNo(StringUtils.toString(oneHeader.get("BILLDATENO"), ""));

                oneLv1.setContact(StringUtils.toString(oneHeader.get("CONTACT"), ""));
                oneLv1.setCloseByName(StringUtils.toString(oneHeader.get("CLOSEBYNAME"), ""));
                oneLv1.setDepartName(StringUtils.toString(oneHeader.get("DEPARTNAME"), ""));
                oneLv1.setLastModiOpName(StringUtils.toString(oneHeader.get("LASTMODIOPNAME"), ""));
                oneLv1.setEmployeeName(StringUtils.toString(oneHeader.get("EMPLOYEENAME"), ""));
                oneLv1.setOrgNo(StringUtils.toString(oneHeader.get("ORGANIZATIONNO"), ""));
                oneLv1.setOrgName(StringUtils.toString(oneHeader.get("ORGANIZATIONNAME"), ""));
                oneLv1.setPayOrgName(StringUtils.toString(oneHeader.get("PAYORGNAME"), ""));
                oneLv1.setRDate(StringUtils.toString(oneHeader.get("RDATE"), ""));
                oneLv1.setTelephone(StringUtils.toString(oneHeader.get("TELEPHONE"), ""));
                oneLv1.setConfirmTime(StringUtils.toString(oneHeader.get("CONFIRMTIME"), ""));
                oneLv1.setInvoiceCode(StringUtils.toString(oneHeader.get("INVOICECODE"), ""));
                oneLv1.setCustomerName(StringUtils.toString(oneHeader.get("CUSTOMERNAME"), ""));
                oneLv1.setDiscRate(StringUtils.toString(oneHeader.get("DISCRATE"), ""));
                oneLv1.setDeliverOrgName(StringUtils.toString(oneHeader.get("DELIVERORGNAME"), ""));
                oneLv1.setDeliverWarehouseName(StringUtils.toString(oneHeader.get("DELIVERWAREHOUSENAME"), ""));
                oneLv1.setPayOrgNo(StringUtils.toString(oneHeader.get("PAYORGNO"), ""));
                oneLv1.setBDate(StringUtils.toString(oneHeader.get("BDATE"), ""));
                oneLv1.setConfirmOpId(StringUtils.toString(oneHeader.get("CONFIRMOPID"), ""));
                oneLv1.setStatus(StringUtils.toString(oneHeader.get("STATUS"), ""));
                oneLv1.setDeliverOrgNo(StringUtils.toString(oneHeader.get("DELIVERORGNO"), ""));
                oneLv1.setInvoiceName(StringUtils.toString(oneHeader.get("INVOICENAME"), ""));
                oneLv1.setCreateOpName(StringUtils.toString(oneHeader.get("CREATEOPNAME"), ""));
                oneLv1.setSalesManName(StringUtils.toString(oneHeader.get("SALESMANNAME"), ""));
                oneLv1.setCloseBy(StringUtils.toString(oneHeader.get("CLOSEBY"), ""));
                oneLv1.setPayDateName(StringUtils.toString(oneHeader.get("PAYDATENAME"), ""));
                oneLv1.setSalesDepartName(StringUtils.toString(oneHeader.get("SALEDEPARTNAME"), ""));
                oneLv1.setTotCqty(StringUtils.toString(oneHeader.get("TOT_CQTY"), ""));
                oneLv1.setCurrencyName(StringUtils.toString(oneHeader.get("CURRENCYNAME"), ""));
                oneLv1.setPOrderNo(StringUtils.toString(oneHeader.get("PORDERNO"), ""));
                oneLv1.setCloseTime(StringUtils.toString(oneHeader.get("CLOSETIME"), ""));
                oneLv1.setTotQty(StringUtils.toString(oneHeader.get("TOT_PQTY"), ""));
                oneLv1.setDepartId(StringUtils.toString(oneHeader.get("DEPARTID"), ""));
                oneLv1.setCancelBy(StringUtils.toString(oneHeader.get("CANCELBY"), ""));
                oneLv1.setCancelByName(StringUtils.toString(oneHeader.get("CANCELBYNAME"), ""));
                oneLv1.setCurrency(StringUtils.toString(oneHeader.get("CURRENCY"), ""));
                oneLv1.setConfirmOpName(StringUtils.toString(oneHeader.get("CONFIRMOPNAME"), ""));
                oneLv1.setBillDateName(StringUtils.toString(oneHeader.get("BILLDATENAME"), ""));
                oneLv1.setLastModiOpId(StringUtils.toString(oneHeader.get("LASTMODIOPID"), ""));
                oneLv1.setTemplateNo(StringUtils.toString(oneHeader.get("TEMPLATENO"), ""));
                oneLv1.setCreateOpId(StringUtils.toString(oneHeader.get("CREATEOPID"), ""));
                oneLv1.setAddress(StringUtils.toString(oneHeader.get("ADDRESS"), ""));
                oneLv1.setSalesManNo(StringUtils.toString(oneHeader.get("SALESMANNO"), ""));
                oneLv1.setEmployeeId(StringUtils.toString(oneHeader.get("EMPLOYEEID"), ""));
                oneLv1.setTotAmt(StringUtils.toString(oneHeader.get("TOT_AMT"), ""));
                oneLv1.setTotPrexTaxAmt(StringUtils.toString(oneHeader.get("TOTPRETAXAMT"), ""));
                oneLv1.setLastModiTime(StringUtils.toString(oneHeader.get("LASTMODITIME"), ""));
                oneLv1.setSalesDepartId(StringUtils.toString(oneHeader.get("SALEDEPARTID"), ""));
                oneLv1.setTemplateName(StringUtils.toString(oneHeader.get("TEMPLATENAME"), ""));
                oneLv1.setCreateTime(StringUtils.toString(oneHeader.get("CREATETIME"), ""));
                oneLv1.setCancelTime(StringUtils.toString(oneHeader.get("CANCELTIME"), ""));
                oneLv1.setPayDateNo(StringUtils.toString(oneHeader.get("PAYDATENO"), ""));
                oneLv1.setCustomerNo(StringUtils.toString(oneHeader.get("CUSTOMERNO"), ""));
                oneLv1.setPayer(StringUtils.toString(oneHeader.get("PAYER"), ""));
                oneLv1.setPayerName(StringUtils.toString(oneHeader.get("PAYERNAME"), ""));
                for (Map<String, Object> dataMap : getQData) {
                    DCP_CustomerPOrderDetailRes.Detail oneLv2 = res.new Detail();
                    oneLv2.setCloseStatus(StringUtils.toString(dataMap.get("CLOSESTATUS"), "N"));
                    oneLv2.setDeliverOrgNo(StringUtils.toString(dataMap.get("DELIVERORGNO"), ""));
                    oneLv2.setPluNo(StringUtils.toString(dataMap.get("PLUNO"), ""));
                    oneLv2.setPluBarcode(StringUtils.toString(dataMap.get("PLUBARCODE"), ""));
                    oneLv2.setAmt(StringUtils.toString(dataMap.get("AMT"), ""));
                    oneLv2.setBaseQty(StringUtils.toString(dataMap.get("BASEQTY"), ""));
                    oneLv2.setQty(StringUtils.toString(dataMap.get("QTY"), ""));
                    oneLv2.setStockOutQty(StringUtils.toString(dataMap.get("STOCKOUTQTY"), ""));
                    oneLv2.setCategoryName(StringUtils.toString(dataMap.get("CATEGORY_NAME"), ""));
                    oneLv2.setListImage(StringUtils.toString(dataMap.get("LISTIMAGE"), ""));
                    oneLv2.setSpec(dataMap.get("SPEC").toString());
                    oneLv2.setTaxName(StringUtils.toString(dataMap.get("TAXNAME"), ""));
                    oneLv2.setPreTaxAmt(StringUtils.toString(dataMap.get("PRETAXAMT"), ""));
                    oneLv2.setBaseUnit(StringUtils.toString(dataMap.get("BASEUNIT"), ""));
                    oneLv2.setStockOutNoQty(StringUtils.toString(dataMap.get("STOCKOUTNOQTY"), ""));
                    oneLv2.setDeliverWarehouse(StringUtils.toString(dataMap.get("DELIVERWAREHOUSE"), ""));
                    oneLv2.setPrice(StringUtils.toString(dataMap.get("PRICE"), ""));
                    oneLv2.setIsGift(StringUtils.toString(dataMap.get("ISGIFT"), ""));
                    oneLv2.setOPrice(StringUtils.toString(dataMap.get("OPRICE"), ""));
                    oneLv2.setAvailableStockQty(StringUtils.toString(dataMap.get("RETURNQTY"), ""));
                    oneLv2.setTaxAmt(StringUtils.toString(dataMap.get("TAXAMT"), ""));
                    oneLv2.setTaxCalType(StringUtils.toString(dataMap.get("TAXCALTYPE"), ""));
                    oneLv2.setItem(StringUtils.toString(dataMap.get("ITEM"), ""));
                    oneLv2.setUnitName(StringUtils.toString(dataMap.get("UNITNAME"), ""));
                    oneLv2.setFeatureName(StringUtils.toString(dataMap.get("FEATURENAME"), ""));
                    oneLv2.setTaxCode(StringUtils.toString(dataMap.get("TAXCODE"), ""));
                    oneLv2.setBaseUnitName(StringUtils.toString(dataMap.get("BASEUNITNAME"), ""));
                    oneLv2.setDiscAmt(StringUtils.toString(dataMap.get("DISCAMT"), ""));
                    oneLv2.setDiscRate(StringUtils.toString(dataMap.get("DISCRATE"), ""));
                    oneLv2.setTaxRate(StringUtils.toString(dataMap.get("TAXRATE"), ""));
                    oneLv2.setUnit(StringUtils.toString(dataMap.get("UNIT"), ""));
                    oneLv2.setDeliverOrgName(StringUtils.toString(dataMap.get("DELIVERORGNAME"), ""));
                    oneLv2.setDeliverWarehouseName(StringUtils.toString(dataMap.get("DELIVERWAREHOUSENAME"), ""));
                    oneLv2.setInclTax(StringUtils.toString(dataMap.get("INCLTAX"), ""));
                    oneLv2.setFeatureNo(StringUtils.toString(dataMap.get("FEATURENO"), ""));
                    oneLv2.setCategory(StringUtils.toString(dataMap.get("CATEGORY"), ""));
                    oneLv2.setPluName(StringUtils.toString(dataMap.get("PLU_NAME"), ""));
                    oneLv2.setUnitRatio(StringUtils.toString(dataMap.get("UNITRATIO"), ""));
                    oneLv2.setRetailPrice(StringUtils.toString(dataMap.get("RETAILPRICE"), ""));
                    oneLv2.setRetailAmt(StringUtils.toString(dataMap.get("RETAILAMT"), ""));


                    oneLv1.getDetail().add(oneLv2);

                }

                res.getDatas().getDataList().add(oneLv1);
            }
            return res;
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_CustomerPOrderDetailReq req) throws Exception {

        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(" SELECT A.*,gl.PLU_NAME,ul0.UNAME UNITNAME,ul1.UNAME BASEUNITNAME, ")
                .append(" b.ITEM,b.PLUNO,b.FEATURENO,b.QTY,b.PRICE,b.OPRICE,b.DISCRATE, ")
                .append(" b.AMT,b.UNIT,b.UPDATE_TIME,b.TRAN_TIME,b.ISGIFT,b.PLUBARCODE, ")
                .append(" b.CATEGORY,b.TAXCODE,b.TAXRATE,b.INCLTAX,b.TAXCALTYPE,b.PRETAXAMT, ")
                .append(" b.TAXAMT,b.DISCAMT,b.RETAILPRICE,b.RETAILAMT,b.STATUS as CLOSESTATUS, ")
                .append(" b.BASEUNIT,b.BASEQTY,b.UNITRATIO,b.STOCKOUTNOQTY,b.STOCKOUTQTY,b.RETURNQTY, ")
                .append(" ee0.NAME EMPLOYEENAME,ee1.NAME CREATEOPNAME,ee2.NAME LASTMODIOPNAME, ")
                .append(" ee3.NAME CLOSEBYNAME,ee4.NAME CANCELBYNAME,dp0.DEPARTNAME,")
                .append(" dp1.DEPARTNAME SALEDEPARTNAME,dp0.DEPARTNAME CREATEDEPTNAME, ")
                .append(" cl0.NAME CURRENCYNAME,ol0.ORG_NAME ORGANIZATIONNAME,ol1.ORG_NAME PAYORGNAME, ")
                .append(" ol2.ORG_NAME DELIVERORGNAME,bl0.NAME BILLDATENAME,bl1.NAME PAYDATENAME,")
                .append(" il0.INVOICE_NAME INVOICENAME,wl0.WAREHOUSE_NAME DELIVERWAREHOUSENAME, ")
                .append(" fn.FEATURENAME,cl1.CATEGORY_NAME,tl1.TAXNAME,g.SPEC,a.payer,f.sname as payername ")
                .append(" FROM DCP_CUSTOMERPORDER a ")
                .append(" INNER JOIN DCP_CUSTOMERPORDER_DETAIL b ON a.EID=b.EID and a.SHOPNO=b.SHOPNO and a.PORDERNO=b.PORDERNO ")
                .append(" LEFT JOIN DCP_GOODS g on g.eid=b.eid and b.PLUNO=g.PLUNO ")
                .append(" LEFT JOIN DCP_GOODS_LANG gl ON b.EID=gl.EID AND b.PLUNO=gl.PLUNO AND gl.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_UNIT_LANG ul0 ON b.EID=ul0.EID AND b.UNIT=ul0.UNIT AND ul0.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_UNIT_LANG ul1 ON b.EID=ul1.EID AND b.BASEUNIT=ul1.UNIT AND ul1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_EMPLOYEE ee0 ON ee0.EID=a.EID and ee0.EMPLOYEENO=a.EMPLOYEEID")
                .append(" LEFT JOIN DCP_EMPLOYEE ee1 ON ee1.EID=a.EID and ee1.EMPLOYEENO=a.CREATEOPID")
                .append(" LEFT JOIN DCP_EMPLOYEE ee2 ON ee2.EID=a.EID and ee2.EMPLOYEENO=a.LASTMODIOPID")
                .append(" LEFT JOIN DCP_EMPLOYEE ee3 ON ee3.EID=a.EID and ee3.EMPLOYEENO=a.CLOSEBY")
                .append(" LEFT JOIN DCP_EMPLOYEE ee4 ON ee2.EID=a.EID and ee4.EMPLOYEENO=a.CANCELBY")
                .append(" LEFT JOIN DCP_DEPARTMENT_LANG dp0 on dp0.EID=a.EID AND dp0.DEPARTNO=a.DEPARTID AND dp0.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_DEPARTMENT_LANG dp1 on dp1.EID=a.EID AND dp1.DEPARTNO=a.SALEDEPARTID AND dp1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_DEPARTMENT_LANG dp2 on dp2.EID=a.EID AND dp2.DEPARTNO=a.CREATEDEPTID AND dp1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_CURRENCY_LANG cl0 ON cl0.eid = a.eid AND cl0.CURRENCY = a.CURRENCY and nation='CN' AND cl0.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT join DCP_ORG_LANG ol0 on ol0.eid=a.eid and ol0.ORGANIZATIONNO=a.ORGANIZATIONNO and ol0.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT join DCP_ORG_LANG ol1 on ol1.eid=a.eid and ol1.ORGANIZATIONNO=a.PAYORGNO and ol1.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT join DCP_ORG_LANG ol2 on ol2.eid=a.eid and ol2.ORGANIZATIONNO=a.DELIVERORGNO and ol2.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_BILLDATE_LANG bl0 ON bl0.eid = a.eid AND bl0.BILLDATENO = a.BILLDATENO AND bl0.lang_type ='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_PAYDATE_LANG bl1 ON bl1.eid = a.eid AND bl1.PAYDATENO = a.PAYDATENO AND bl1.lang_type ='").append(req.getLangType()).append("'")
                .append(" left join DCP_INVOICETYPE_LANG il0 on il0.eid =a.eid and il0.INVOICECODE=a.INVOICECODE and il0.lang_type='").append(req.getLangType()).append("'")
                .append(" left join dcp_warehouse_lang wl0 on wl0.eid=a.eid and wl0.warehouse=a.DELIVERWAREHOUSE and wl0.lang_type='").append(req.getLangType()).append("'")
                .append(" left join dcp_goods_feature_lang fn on b.eid=fn.eid and b.pluno=fn.pluno and b.featureno=fn.featureno and fn.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_CATEGORY_LANG cl1 on cl1.eid =b.eid and cl1.CATEGORY=b.CATEGORY and cl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" left join DCP_TAXCATEGORY_LANG tl1 on tl1.eid=b.eid and tl1.TAXCODE=b.TAXCODE and tl1.TAXAREA='CN' and tl1.lang_type='").append(req.getLangType()).append("'")
                .append(" left join dcp_bizpartner f on f.eid=a.eid and f.bizpartnerno=a.payer ")

                .append(" WHERE a.EID='").append(req.geteId()).append("'")

        ;
        if (StringUtils.isNotEmpty(req.getRequest().getPOrderNo())){
            sqlbuf.append(" AND a.PORDERNO='").append(req.getRequest().getPOrderNo()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getShopId())){
            sqlbuf.append(" AND a.SHOPNO='").append(req.getRequest().getShopId()).append("'");
        }

        String searchScope = req.getRequest().getSearchScope();
        if ("1".equals(searchScope)) {
            sqlbuf.append(" and nvl(b.qty,0)>nvl(b.STOCKOUTNOQTY,0)");
        }



        sqlbuf.append("ORDER BY b.PORDERNO,b.SHOPNO,b.ITEM");


        return sqlbuf.toString();
    }

}
