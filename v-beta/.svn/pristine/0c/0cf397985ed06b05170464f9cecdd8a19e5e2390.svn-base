package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PurchaseApplyDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_PurchaseApplyDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_PurchaseApplyDetailQuery extends SPosBasicService<DCP_PurchaseApplyDetailQueryReq, DCP_PurchaseApplyDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_PurchaseApplyDetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_PurchaseApplyDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_PurchaseApplyDetailQueryReq>(){};
    }

    @Override
    protected DCP_PurchaseApplyDetailQueryRes getResponseType() {
        return new DCP_PurchaseApplyDetailQueryRes();
    }

    @Override
    protected DCP_PurchaseApplyDetailQueryRes processJson(DCP_PurchaseApplyDetailQueryReq req) throws Exception {
        DCP_PurchaseApplyDetailQueryRes res = this.getResponse();
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {

            for (Map<String, Object> row : getQData){
                DCP_PurchaseApplyDetailQueryRes.Level1Elm level1Elm = res.new Level1Elm();

                level1Elm.setBillNo(row.get("BILLNO").toString());
                level1Elm.setBDate(row.get("BDATE").toString());
                level1Elm.setRDate(row.get("RDATE").toString());
                level1Elm.setTotPqty(row.get("TOTPQTY").toString());
                level1Elm.setTotAmt(row.get("TOTAMT").toString());
                level1Elm.setTotCqty(row.get("TOTCQTY").toString());
                level1Elm.setTotPurAmt(row.get("TOTPURAMT").toString());
                level1Elm.setEmployeeId(row.get("EMPLOYEEID").toString());
                level1Elm.setEmployeeName(row.get("EMPLOYEENAME").toString());
                level1Elm.setDepartId(row.get("DEPARTID").toString());
                level1Elm.setDepartName(row.get("DEPARTNAME").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setCreateOpId(row.get("CREATEOPID").toString());
                level1Elm.setCreateOpName(row.get("CREATEOPNAME").toString());
                level1Elm.setCreateTime(row.get("CREATETIMES").toString());
                level1Elm.setLastModiOpId(row.get("LASTMODIOPID").toString());
                level1Elm.setLastModiOpName(row.get("LASTMODIOPNAME").toString());
                level1Elm.setLastModiTime(row.get("LASTMODITIMES").toString());
                level1Elm.setSubmitOpId(row.get("SUBMITOPID").toString());
                level1Elm.setSubmitOpName(row.get("SUBMITOPNAME").toString());
                level1Elm.setSubmitTime(row.get("SUBMITTIMES").toString());
                level1Elm.setConfirmOpId(row.get("CONFIRMOPID").toString());
                level1Elm.setConfirmOpName(row.get("CONFIRMOPNAME").toString());
                level1Elm.setConfirmTime(row.get("CONFIRMTIMES").toString());
                level1Elm.setCancelOpId(row.get("CANCELOPID").toString());
                level1Elm.setCancelOpName(row.get("CANCELOPNAME").toString());
                level1Elm.setCancelTime(row.get("CANCELTIMES").toString());
                level1Elm.setCloseOpId(row.get("CLOSEOPID").toString());
                level1Elm.setCloseOpName(row.get("CLOSEOPNAME").toString());
                level1Elm.setCloseTime(row.get("CLOSETIMES").toString());
                level1Elm.setDetail(new ArrayList<>());
                String detailSql="select a.*,b.plu_name as pluname,c.featurename,d.uname as punitname,e.uname as baseunitname,f.category_name as categoryname," +
                        " g.WAREHOUSE_NAME as WAREHOUSENAME,h.sname as suppliername,b1.spec,b1.category " +
                        " from DCP_PURCHASEAPPLY_DETAIL a " +
                        " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+req.getLangType()+"' " +
                        " left join dcp_goods b1 on a.eid=b1.eid and a.pluno=b1.pluno  " +
                        " left join dcp_goods_feature_lang c on c.eid=a.eid and c.pluno=a.pluno and c.featureno=a.featureno and c.lang_type='"+req.getLangType()+"' " +
                        " left join dcp_unit_lang d on d.eid=a.eid and d.unit=a.punit and d.lang_type='"+req.getLangType()+"' " +
                        " left join dcp_unit_lang e on e.eid=a.eid and e.unit=a.baseunit and e.lang_type='"+req.getLangType()+"' " +
                        " left join DCP_CATEGORY_LANG f on f.eid=a.eid and f.category=b1.category and f.lang_type='"+req.getLangType()+"' " +
                        " left join dcp_warehouse_lang g on g.eid=a.eid and g.warehouse=a.warehouse and g.lang_type='"+req.getLangType()+"' " +
                        " left join dcp_bizpartner h on h.eid=a.eid and a.supplier=h.bizpartnerno " +
                        " where a.eid='"+req.geteId()+"' and a.billno='"+req.getRequest().getBillNo()+"' " +
                        " order by a.item ";
                List<Map<String, Object>> detailDatas =this.doQueryData(detailSql, null);
                if(detailDatas.size()>0){
                    for (Map<String, Object> detailRow : detailDatas){
                        DCP_PurchaseApplyDetailQueryRes.Detail detail = res.new Detail();
                        detail.setItem(detailRow.get("ITEM").toString());
                        detail.setPluBarcode(detailRow.get("PLUBARCODE").toString());
                        detail.setPluNo(detailRow.get("PLUNO").toString());
                        detail.setPluName(detailRow.get("PLUNAME").toString());
                        detail.setFeatureNo(detailRow.get("FEATURENO").toString());
                        detail.setFeatureName(detailRow.get("FEATURENAME").toString());
                        detail.setSpec(detailRow.get("SPEC").toString());
                        detail.setCategory(detailRow.get("CATEGORY").toString());
                        detail.setCategoryName(detailRow.get("CATEGORYNAME").toString());
                        detail.setPUnit(detailRow.get("PUNIT").toString());
                        detail.setPUnitName(detailRow.get("PUNITNAME").toString());
                        detail.setPQty(detailRow.get("PQTY").toString());
                        detail.setBaseUnit(detailRow.get("BASEUNIT").toString());
                        detail.setBaseUnitName(detailRow.get("BASEUNITNAME").toString());
                        detail.setBaseQty(detailRow.get("BASEQTY").toString());
                        detail.setUnitRatio(detailRow.get("UNITRATIO").toString());
                        detail.setPrice(detailRow.get("PRICE").toString());
                        detail.setAmt(detailRow.get("AMT").toString());
                        detail.setPurPrice(detailRow.get("PURPRICE").toString());
                        detail.setPurAmt(detailRow.get("PURAMT").toString());
                        detail.setWarehouse(detailRow.get("WAREHOUSE").toString());
                        detail.setWarehouseName(detailRow.get("WAREHOUSENAME").toString());
                        detail.setPropQty(detailRow.get("PROPQTY").toString());
                        detail.setMinQty(detailRow.get("MINQTY").toString());
                        detail.setMulQty(detailRow.get("MULQTY").toString());
                        detail.setRefWqty(detailRow.get("REFWQTY").toString());
                        detail.setReviewQty(detailRow.get("REVIEWQTY").toString());
                        detail.setStatus(detailRow.get("STATUS").toString());
                        detail.setMemo(detailRow.get("MEMO").toString());
                        detail.setSupplier(detailRow.get("SUPPLIER").toString());
                        detail.setSupplierName(detailRow.get("SUPPLIERNAME").toString());
                        detail.setTemplateNo(detailRow.get("TEMPLATENO").toString());
                        detail.setPurType(detailRow.get("PURTYPE").toString());
                        detail.setPreDays(detailRow.get("PREDAYS").toString());
                        detail.setArrivalDate(detailRow.get("ARRIVALDATE").toString());
                        detail.setPurDate(detailRow.get("PURDATE").toString());
                        level1Elm.getDetail().add(detail);
                    }
                }
                res.getDatas().add(level1Elm);

            }


        }


        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_PurchaseApplyDetailQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();
        String billNo = req.getRequest().getBillNo();


        sqlbuf.append(" "
                + " select "
                + " a.*,e1.op_name as createopname,e2.op_name as lastModiOpName,c.name as employeename,d1.departname," +
                "e3.op_name as submitopname,e4.op_name as confirmopname,e5.op_name as closeopname,e6.op_name as cancelopname," +
                " to_char(a.CREATETIME,'yyyy-MM-dd HH:mm:ss') as createtimes,to_char(a.LASTMODITIME,'yyyy-MM-dd HH:mm:ss') as LASTMODITIMEs,to_char(a.SUBMITTIME,'yyyy-MM-dd HH:mm:ss') as SUBMITTIMEs,to_char(a.CONFIRMTIME,'yyyy-MM-dd HH:mm:ss') as CONFIRMTIMEs," +
                " to_char(a.CLOSETIME,'yyyy-MM-dd HH:mm:ss') as CLOSETIMEs,to_char(a.CANCELTIME,'yyyy-MM-dd HH:mm:ss') as CANCELTIMEs  "
                + " from DCP_PURCHASEAPPLY a"+
                " left join dcp_employee c on c.eid=a.eid and c.employeeno=a.employeeid"
                + " left join PLATFORM_STAFFS_LANG e1 on e1.eid=a.eid and e1.opno=a.createOpId and e1.lang_type='"+req.getLangType()+"'"
                + " left join PLATFORM_STAFFS_LANG e2 on e2.eid=a.eid and e2.opno=a.lastModiOpId and e2.lang_type='"+req.getLangType()+"' "
                + " left join PLATFORM_STAFFS_LANG e3 on e3.eid=a.eid and e3.opno=a.submitopid and e3.lang_type='"+req.getLangType()+"' "
                + " left join PLATFORM_STAFFS_LANG e4 on e4.eid=a.eid and e4.opno=a.confirmopid and e4.lang_type='"+req.getLangType()+"' "
                + " left join PLATFORM_STAFFS_LANG e5 on e5.eid=a.eid and e5.opno=a.closeopid and e5.lang_type='"+req.getLangType()+"' "
                + " left join PLATFORM_STAFFS_LANG e6 on e6.eid=a.eid and e6.opno=a.cancelopid and e6.lang_type='"+req.getLangType()+"' "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.departid and d1.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.billno='"+billNo+"' "
                + " ");

        return sqlbuf.toString();
    }
}


