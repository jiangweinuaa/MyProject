package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_MStockOutDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_MStockOutDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_MStockOutDetailQuery extends SPosBasicService<DCP_MStockOutDetailQueryReq, DCP_MStockOutDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_MStockOutDetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_MStockOutDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_MStockOutDetailQueryReq>(){};
    }

    @Override
    protected DCP_MStockOutDetailQueryRes getResponseType() {
        return new DCP_MStockOutDetailQueryRes();
    }

    @Override
    protected DCP_MStockOutDetailQueryRes processJson(DCP_MStockOutDetailQueryReq req) throws Exception {
        DCP_MStockOutDetailQueryRes res = this.getResponse();

        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        String detailSql = this.getDetailSql(req);
        List<Map<String, Object>> getDData=this.doQueryData(detailSql, null);

        if (getQData != null && !getQData.isEmpty()) {

            for (Map<String, Object> row : getQData){
                DCP_MStockOutDetailQueryRes.Level1Elm level1Elm = res.new Level1Elm();
                level1Elm.setMStockOutNo(row.get("MSTOCKOUTNO").toString());
                level1Elm.setDocType(row.get("DOC_TYPE").toString());
                level1Elm.setWarehouse(row.get("WAREHOUSE").toString());
                level1Elm.setWarehouseName(row.get("WAREHOUSENAME").toString());
                level1Elm.setBDate(row.get("BDATE").toString());
                level1Elm.setAccountDate(row.get("ACCOUNT_DATE").toString());
                level1Elm.setTotPQty(row.get("TOT_PQTY").toString());
                level1Elm.setTotCQty(row.get("TOT_CQTY").toString());
                level1Elm.setTotAmt(row.get("TOT_AMT").toString());
                level1Elm.setTotDistriAmt(row.get("TOT_DISTRIAMT").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setAdjustStatus(row.get("ADJUSTSTATUS").toString());
                level1Elm.setLoadDocType(row.get("LOAD_DOCTYPE").toString());
                level1Elm.setLoadDocNo(row.get("LOAD_DOCNO").toString());
                level1Elm.setAutoProcess(row.get("AUTOPROCESS").toString());
                level1Elm.setCreateBy(row.get("CREATEOPID").toString());
                level1Elm.setCreateByName(row.get("CREATEOPNAME").toString());
                level1Elm.setCreateTime(row.get("CREATETIME").toString());
                level1Elm.setModifyBy(row.get("LASTMODIOPID").toString());
                level1Elm.setModifyTime(row.get("LASTMODITIME").toString());
                level1Elm.setModifyByName(row.get("LASTMODIOPNAME").toString());
                level1Elm.setProcessStatus(row.get("PROCESS_STATUS").toString());
                level1Elm.setProcessErpNo(row.get("PROCESS_ERP_NO").toString());
                level1Elm.setProcessErpOrg(row.get("PROCESS_ERP_ORG").toString());
                level1Elm.setAccountBy(row.get("ACCOUNTOPID").toString());
                level1Elm.setAccountByName(row.get("ACCOUNTOPNAME").toString());
                level1Elm.setAccountTime(row.get("ACCOUNTTIME").toString());
                level1Elm.setOMStockOutNo(row.get("OMSTOCKOUTNO").toString());
                level1Elm.setEmployeeId(row.get("EMPLOYEEID").toString());
                level1Elm.setEmployeeName(row.get("EMPLOYEENAME").toString());
                level1Elm.setDepartId(row.get("DEPARTID").toString());
                level1Elm.setDepartName(row.get("DEPTNAME").toString());
                level1Elm.setOType(row.get("OTYPE").toString());
                level1Elm.setOfNo(row.get("OFNO").toString());
                level1Elm.setOOType(row.get("OOTYPE").toString());
                level1Elm.setOOfNo(row.get("OOFNO").toString());

                level1Elm.setDatas(new ArrayList<>());
                if(CollUtil.isNotEmpty(getDData)){
                    for (Map<String, Object> detailRow : getDData){
                        DCP_MStockOutDetailQueryRes.Datas datas = res.new Datas();
                        datas.setItem(detailRow.get("ITEM").toString());
                        datas.setWarehouse(detailRow.get("WAREHOUSE").toString());
                        datas.setWarehouseName(detailRow.get("WAREHOUSENAME").toString());
                        datas.setIsLocation(detailRow.get("ISLOCATION").toString());
                        datas.setPluNo(detailRow.get("PLUNO").toString());
                        datas.setPluName(detailRow.get("PLUNAME").toString());
                        datas.setPUnit(detailRow.get("PUNIT").toString());
                        datas.setPUName(detailRow.get("PUNITNAME").toString());
                        datas.setPQty(detailRow.get("PQTY").toString());
                        datas.setOPQty(detailRow.get("OPQTY").toString());
                        datas.setBaseUnit(detailRow.get("BASEUNIT").toString());
                        datas.setBaseUName(detailRow.get("BASEUNITNAME").toString());
                        datas.setBaseQty(detailRow.get("BASEQTY").toString());
                        datas.setUnitRatio(detailRow.get("UNIT_RATIO").toString());
                        datas.setPrice(detailRow.get("PRICE").toString());
                        datas.setAmt(detailRow.get("AMT").toString());
                        datas.setDistriPrice(detailRow.get("DISTRIPRICE").toString());
                        datas.setDistriAmt(detailRow.get("DISTRIAMT").toString());
                        datas.setBatchNo(detailRow.get("BATCHNO").toString());
                        datas.setProdDate(detailRow.get("PRODDATE").toString());
                        datas.setLoseDate(detailRow.get("EXPDATE").toString());
                        datas.setIsBuckle(detailRow.get("ISBUCKLE").toString());
                        datas.setFeatureNo(detailRow.get("FEATURENO").toString());
                        datas.setFeatureName(detailRow.get("FEATURENAME").toString());
                        datas.setLocation(detailRow.get("LOCATION").toString());
                        datas.setOType(detailRow.get("OTYPE").toString());
                        datas.setOfNo(detailRow.get("OFNO").toString());
                        datas.setOItem(detailRow.get("OITEM").toString());
                        datas.setOOType(detailRow.get("OOTYPE").toString());
                        datas.setOOfNo(detailRow.get("OOFNO").toString());
                        datas.setOOItem(detailRow.get("OOITEM").toString());
                        datas.setLoadDocItem(detailRow.get("LOAD_DOCITEM").toString());
                        datas.setPItem(detailRow.get("PITEM").toString());
                        datas.setProcessNo(detailRow.get("PROCESSNO").toString());
                        datas.setProcessName(detailRow.get("PROCESSNAME").toString());
                        datas.setSItem(detailRow.get("SITEM").toString());
                        datas.setZItem(detailRow.get("ZITEM").toString());
                        level1Elm.getDatas().add(datas);
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
    protected String getQuerySql(DCP_MStockOutDetailQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();

        DCP_MStockOutDetailQueryReq.levelRequest request = req.getRequest();
        String mStockOutNo = request.getMStockOutNo();


        sqlbuf.append(" "
                + " select "
                + " a.*,c.WAREHOUSE_NAME as warehousename ,e1.op_name as createopname,e2.op_name as lastModiOpName,e3.op_name as confirmopname,e4.op_name as accountopname" +
                ",d1.departname as deptname,ee.name as employeename  "
                + " from DCP_MSTOCKOUT a"+
                " left join dcp_warehouse_lang c on c.eid=a.eid and c.warehouse=a.warehouse and c.lang_type='"+req.getLangType()+"' " +
                " "
                + " left join PLATFORM_STAFFS_LANG e1 on e1.eid=a.eid and e1.opno=a.createOpId and e1.lang_type='"+req.getLangType()+"'"
                + " left join PLATFORM_STAFFS_LANG e2 on e2.eid=a.eid and e2.opno=a.lastModiOpId and e2.lang_type='"+req.getLangType()+"' "
                + " left join PLATFORM_STAFFS_LANG e3 on e3.eid=a.eid and e3.opno=a.CONFIRMOPID and e3.lang_type='"+req.getLangType()+"' "
                + " left join PLATFORM_STAFFS_LANG e4 on e4.eid=a.eid and e4.opno=a.ACCOUNTOPID and e4.lang_type='"+req.getLangType()+"'" +
                " left join dcp_employee ee on ee.eid=a.eid and ee.employeeno=a.employeeid  "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.DEPARTID and d1.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.organizationno='"+req.getOrganizationNO()+"' and a.mstockoutno='"+mStockOutNo+"' "
               + " ");

        return sqlbuf.toString();
    }

    private String getDetailSql(DCP_MStockOutDetailQueryReq req) throws Exception{
        StringBuffer sb=new StringBuffer();
        sb.append(" " +
                "select a.*,b.warehouse_name as warehousename,b1.islocation,c.plu_name as pluname,d.uname as punitname,e.uname as baseunitname,f.featurename,g.processname " +
                "  from DCP_MSTOCKOUT_DETAIL a  " +
                " left join dcp_warehouse_lang b on a.eid=b.eid and a.warehouse=b.warehouse and b.lang_type='"+req.getLangType()+"' " +
                " left join dcp_warehouse b1 on a.eid=b1.eid and a.warehouse=b1.warehouse  " +

                " left join dcp_goods_lang c on c.eid=a.eid and a.pluno=c.pluno and c.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang d on d.eid=a.eid and d.unit=a.punit and d.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang e on e.eid=a.eid and e.unit=a.baseunit and e.lang_type='"+req.getLangType()+"' " +
                " left join dcp_goods_feature_lang f on f.eid=a.eid and f.pluno=a.pluno and f.featureno=a.featureno and f.lang_type='"+req.getLangType()+"' " +
                " left join MES_PROCESS g on g.eid=a.eid and g.processno=a.processno " +
                " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                " and a.mstockoutno='"+req.getRequest().getMStockOutNo()+"'");

        return sb.toString();
    }
}


