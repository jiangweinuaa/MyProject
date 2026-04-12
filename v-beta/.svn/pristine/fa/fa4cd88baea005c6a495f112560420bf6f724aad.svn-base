package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_VendorAdjDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_VendorAdjDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_VendorAdjDetailQuery extends SPosBasicService<DCP_VendorAdjDetailQueryReq, DCP_VendorAdjDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_VendorAdjDetailQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        }

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_VendorAdjDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_VendorAdjDetailQueryReq>(){};
    }

    @Override
    protected DCP_VendorAdjDetailQueryRes getResponseType() {
        return new DCP_VendorAdjDetailQueryRes();
    }

    @Override
    protected DCP_VendorAdjDetailQueryRes processJson(DCP_VendorAdjDetailQueryReq req) throws Exception {
        DCP_VendorAdjDetailQueryRes res = this.getResponse();

        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        String warehouse="";
        if(getQData.size()>0){
            warehouse=getQData.get(0).get("WAREHOUSE").toString();
        }

        String detailSql=this.getDetailSql(req,warehouse);
        List<Map<String, Object>> getDData=this.doQueryData(detailSql, null);

        if (getQData != null && !getQData.isEmpty()) {
            for (Map<String, Object> row : getQData){
                DCP_VendorAdjDetailQueryRes.level1Elm level1Elm = res.new level1Elm();
                level1Elm.setOrganizationNo(row.get("ORGANIZATIONNO").toString());
                level1Elm.setOrg_Name(row.get("ORG_NAME").toString());
                level1Elm.setAdjustNO(row.get("ADJUSTNO").toString());
                level1Elm.setBDate(row.get("BDATE").toString());
                level1Elm.setOtype(row.get("OTYPE").toString());
                level1Elm.setSStockInNo(row.get("SSTOCKINNO").toString());
                level1Elm.setSupplier(row.get("SUPPLIER").toString());
                level1Elm.setSupplierName(row.get("SUPPLIERNAME").toString());
                level1Elm.setPayDateNo(row.get("PAYDATENO").toString());
                level1Elm.setBillDateNo(row.get("BILLDATENO").toString());
                level1Elm.setTaxCode(row.get("TAXCODE").toString());
                level1Elm.setTaxRate(row.get("TAXRATE").toString());
                level1Elm.setTaxName(row.get("TAXNAME").toString());
                level1Elm.setCurrency(row.get("CURRENCY").toString());
                level1Elm.setCurrencyName(row.get("CURRENCYNAME").toString());
                level1Elm.setExRate(row.get("EXRATE").toString());
                level1Elm.setTot_Amt(row.get("TOT_AMT").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setAdjList(new ArrayList<>());
                for (Map<String, Object> detailData : getDData){
                    DCP_VendorAdjDetailQueryRes.AdjList adjList = res.new AdjList();
                    adjList.setOrganizationNo(detailData.get("ORGANIZATIONNO").toString());
                    adjList.setAdjustNO(detailData.get("ADJUSTNO").toString());
                    adjList.setSStockInNo(detailData.get("SSTOCKINNO").toString());
                    adjList.setPluName(detailData.get("PLUNAME").toString());
                    adjList.setPluNo(detailData.get("PLUNO").toString());
                    adjList.setItem(detailData.get("ITEM").toString());
                    adjList.setPqty(detailData.get("PQTY").toString());
                    adjList.setPunit(detailData.get("PUNIT").toString());
                    adjList.setPunitName(detailData.get("PUNITNAME").toString());
                    adjList.setBaseUnitName(detailData.get("BASEUNITNAME").toString());
                    adjList.setReceiving_Qty(detailData.get("RECEIVING_QTY").toString());
                    adjList.setProc_Rate(detailData.get("PROC_RATE").toString());
                    adjList.setBaseUnit(detailData.get("BASEUNIT").toString());
                    adjList.setBaseQty(detailData.get("BASEQTY").toString());
                    adjList.setUnit_Ratio(detailData.get("UNIT_RATIO").toString());
                    adjList.setPoQty(detailData.get("POQTY").toString());
                    adjList.setStockIn_Qty(detailData.get("STOCKIN_QTY").toString());
                    adjList.setRetwQty(detailData.get("RETWQTY").toString());
                    adjList.setPurOrderNo(detailData.get("PURORDERNO").toString());
                    adjList.setPoItem(detailData.get("POITEM").toString());
                    adjList.setFeatureNo(detailData.get("FEATURENO").toString());
                    adjList.setLocation(detailData.get("LOCATION").toString());
                    adjList.setBatch_No(detailData.get("BATCH_NO").toString());
                    adjList.setDistriPrice(detailData.get("DISTRIPRICE").toString());
                    adjList.setDistriAmt(detailData.get("DISTRIAMT").toString());
                    adjList.setTaxCode(detailData.get("TAXCODE").toString());
                    adjList.setTaxRate(detailData.get("TAXRATE").toString());
                    adjList.setInclTax(detailData.get("INCLTAX").toString());
                    adjList.setAmt(detailData.get("AMT").toString());
                    adjList.setPreTaxAmt(detailData.get("PRETAXAMT").toString());
                    adjList.setTaxAmt(detailData.get("TAXAMT").toString());

                    adjList.setAdjTaxAmt(detailData.get("ADJTAXAMT").toString());
                    adjList.setAdjAmtPreTax(detailData.get("ADJAMTPRETAX").toString());
                    adjList.setAdjAmt(detailData.get("ADJAMT").toString());

                    adjList.setAdjPrice(detailData.get("ADJPRICE").toString());
                    adjList.setAdjTaxAmted(detailData.get("ADJTAXAMTED").toString());
                    adjList.setAdjAmtPreTaxed(detailData.get("ADJAMTPRETAXED").toString());
                    adjList.setAdjAmtTaxed(detailData.get("ADJAMTTAXED").toString());
                    adjList.setMemo(detailData.get("MEMO").toString());
                    adjList.setLocationName(detailData.get("LOCATIONNAME").toString());
                    adjList.setTaxName(detailData.get("TAXNAME").toString());
                    level1Elm.getAdjList().add(adjList);
                }

                level1Elm.setCreateBy(row.get("CREATEBY").toString());
                level1Elm.setCreateByName(row.get("CREATEBYNAME").toString());
                level1Elm.setCreate_Date(row.get("CREATE_DATE").toString());
                level1Elm.setCreate_Time(row.get("CREATE_TIME").toString());
                level1Elm.setModifyBy(row.get("MODIFYBY").toString());
                level1Elm.setModifyByName(row.get("MODIFYBYNAME").toString());
                level1Elm.setModify_Date(row.get("MODIFY_DATE").toString());
                level1Elm.setModify_Time(row.get("MODIFY_TIME").toString());
                level1Elm.setConfirmBy(row.get("CONFIRMBY").toString());
                level1Elm.setConfirmByName(row.get("CONFIRMBYNAME").toString());
                level1Elm.setConfirm_Date(row.get("CONFIRM_DATE").toString());
                level1Elm.setConfirm_Time(row.get("CONFIRM_TIME").toString());
                level1Elm.setCancelBy(row.get("CANCELBY").toString());
                level1Elm.setCancelByName(row.get("CANCELBYNAME").toString());
                level1Elm.setCancel_Date(row.get("CANCEL_DATE").toString());
                level1Elm.setCancel_Time(row.get("CANCEL_TIME").toString());


                res.getDatas().add(level1Elm);

            }

        }


        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_VendorAdjDetailQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();

        StringBuffer sqlbuf=new StringBuffer();

        sqlbuf.append(" "
                + " select "
                + " a.organizationNo,c.org_name,a.adjustno,a.bdate,a.otype ,a.sstockinno,a.supplier,d.sname as suppliername,a.payDateNo,a.billDateNo," +
                "  a.taxCode,a.taxRate,a.currency,a.exRate,a.tot_Amt,a.memo,e.name as currencyname,a.status,f.taxname," +
                " nvl(g.warehouse,h.warehouse) as warehouse,a.createby,a.create_date,a.create_time,a.modifyBy,a.modify_date,a.modify_time,a.CONFIRMBY,a.confirm_date,a.confirm_time,a.cancelby,a.cancel_date,a.cancel_time," +
                " i1.op_name as createbyname,i2.op_name as modifybyname,i3.op_name as confirmbyname,i4.op_name as cancelbyname "
                + " from DCP_VENDORADJ a"+
                " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.organizationno and c.lang_type='"+req.getLangType()+"' " +
                " left join dcp_bizpartner d on d.BIZPARTNERNO=a.supplier and d.eid=a.eid " +
                " left join DCP_CURRENCY_LANG e on e.eid=a.eid and e.currency=a.currency and e.lang_type='"+req.getLangType()+"' " +
                " left join DCP_TAXCATEGORY_LANG f on f.eid=a.eid and f.taxcode=a.taxcode and f.lang_type='"+req.getLangType()+"' " +
                " left join dcp_sstockin g on g.eid=a.eid and g.sstockinno=a.sstockinno" +
                " left join dcp_sstockout h on h.eid=a.eid and h.sstockoutno=a.sstockinno" +
                " left join platform_staffs_lang i1 on i1.eid=a.eid and i1.opno=a.createby and i1.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang i2 on i2.eid=a.eid and i2.opno=a.modifyby and i2.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang i3 on i3.eid=a.eid and i3.opno=a.confirmby and i3.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang i4 on i4.eid=a.eid and i4.opno=a.cancelby and i4.lang_type='"+req.getLangType()+"' "

                + " where a.eid='"+eId+"' and adjustno='"+req.getRequest().getAdjustNo()+"' " +
                " and a.organizationno='"+req.getRequest().getOrganizationNo()+"' "
                + " ");

        return sqlbuf.toString();
    }

    private String getDetailSql(DCP_VendorAdjDetailQueryReq req,String warehouse) throws Exception{
        StringBuffer sb = new StringBuffer();
        sb.append("select a.*,b.plu_name as pluname,c.uname as punitname,d.uname as baseunitname,f.taxname,e.locationname " +
                " from DCP_VENDORADJ_DETAIL a " +
                " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang c on c.eid=a.eid and c.unit=a.punit and c.lang_type='"+req.getLangType()+"'" +
                " left join dcp_unit_lang d on d.eid=a.eid and d.unit =a.baseunit and d.lang_type='"+req.getLangType()+"'" +
                " left join dcp_location e on e.eid=a.eid and e.organizationno=a.organizationno and e.warehouse='"+warehouse+"' and e.location=a.location " +
                " left join DCP_TAXCATEGORY_LANG f on f.eid=a.eid and f.taxcode=a.taxcode and f.lang_type='"+req.getLangType()+"' "+
                " where a.eid='"+req.geteId()+"' " +
                " and a.organizationno='"+req.getRequest().getOrganizationNo()+"' " +
                " and a.adjustno='"+req.getRequest().getAdjustNo()+"' ");

        return sb.toString();
    }

}


