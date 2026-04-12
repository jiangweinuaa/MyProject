package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_ROrderDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ROrderDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ROrderDetailQuery extends SPosBasicService<DCP_ROrderDetailQueryReq, DCP_ROrderDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ROrderDetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ROrderDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ROrderDetailQueryReq>(){};
    }

    @Override
    protected DCP_ROrderDetailQueryRes getResponseType() {
        return new DCP_ROrderDetailQueryRes();
    }

    @Override
    protected DCP_ROrderDetailQueryRes processJson(DCP_ROrderDetailQueryReq req) throws Exception {
        DCP_ROrderDetailQueryRes res = this.getResponse();

        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        String detailSql = this.getDetailSql(req);
        List<Map<String, Object>> getDetailData=this.doQueryData(detailSql, null);
        res.setDatas(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {
            for (Map<String, Object> row : getQData){
                DCP_ROrderDetailQueryRes.level1Elm level1Elm = res.new level1Elm();
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setBDate(row.get("BDATE").toString());
                level1Elm.setROrderNo(row.get("RORDERNO").toString());
                level1Elm.setRDate(row.get("RDATE").toString());
                level1Elm.setRDays(row.get("RDAYS").toString());
                level1Elm.setEmployeeId(row.get("EMPLOYEEID").toString());
                level1Elm.setEmployeeName(row.get("EMPLOYEENAME").toString());
                level1Elm.setDepartId(row.get("DEPARTID").toString());
                level1Elm.setDepartName(row.get("DEPARTNAME").toString());
                level1Elm.setTotCqty(row.get("TOTCQTY").toString());
                level1Elm.setTotPqty(row.get("TOTPQTY").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                level1Elm.setCreateBy(row.get("CREATEBY").toString());
                level1Elm.setCreateByName(row.get("CREATEBYNAME").toString());
                level1Elm.setCreateTime(row.get("CREATETIME").toString());
                level1Elm.setModifyBy(row.get("MODIFYBY").toString());
                level1Elm.setModifyByName(row.get("MODIFYBYNAME").toString());
                level1Elm.setModifyTime(row.get("MODIFYTIME").toString());
                level1Elm.setConfirmBy(row.get("CONFIRMBY").toString());
                level1Elm.setConfirmByName(row.get("CONFIRMBYNAME").toString());
                level1Elm.setConfirmTime(row.get("CONFIRMTIME").toString());
                level1Elm.setDepartId(row.get("DEPARTID").toString());
                level1Elm.setDepartName(row.get("DEPARTNAME").toString());

                level1Elm.setCancelBy(row.get("CANCELBY").toString());
                level1Elm.setCancelByName(row.get("CANCELBYNAME").toString());
                level1Elm.setCancelTime(row.get("CANCELTIME").toString());
                level1Elm.setCloseBy(row.get("CLOSEBY").toString());
                level1Elm.setCloseByName(row.get("CLOSEBYNAME").toString());
                level1Elm.setCloseTime(row.get("CLOSETIME").toString());

                level1Elm.setDetail(new ArrayList<>());
                if(CollUtil.isNotEmpty(getDetailData)){
                    for (Map<String, Object> detailRow : getDetailData){
                        DCP_ROrderDetailQueryRes.Detail detail = res.new Detail();
                        detail.setItem(detailRow.get("ITEM").toString());
                        detail.setPluBarcode(detailRow.get("PLUBARCODE").toString());
                        detail.setPluNo(detailRow.get("PLUNO").toString());
                        detail.setPluName(detailRow.get("PLUNAME").toString());
                        detail.setFeatureNo(detailRow.get("FEATURENO").toString());
                        detail.setFeatureName(detailRow.get("FEATURENAME").toString());
                        detail.setPUnit(detailRow.get("PUNIT").toString());
                        detail.setPUnitName(detailRow.get("PUNITNAME").toString());
                        detail.setPQty(detailRow.get("PQTY").toString());
                        detail.setBaseUnit(detailRow.get("BASEUNIT").toString());
                        detail.setBaseUnitName(detailRow.get("BASEUNITNAME").toString());
                        detail.setBaseQty(detailRow.get("BASEQTY").toString());
                        detail.setUnitRatio(detailRow.get("UNITRATIO").toString());
                        detail.setDailyPqty(detailRow.get("DAILYPQTY").toString());
                        detail.setAvgDeliverQty(detailRow.get("AVGDELIVERQTY").toString());
                        detail.setForecastQty(detailRow.get("FORECASTQTY").toString());
                        detail.setMinQty(detailRow.get("MINQTY").toString());
                        detail.setMulQty(detailRow.get("MULQTY").toString());
                        detail.setStockQty(detailRow.get("STOCKQTY").toString());
                        detail.setSafeQty(detailRow.get("SAFEQTY").toString());
                        detail.setProduceQty(detailRow.get("PRODUCEQTY").toString());
                        detail.setStatus(detailRow.get("STATUS").toString());
                        detail.setMemo(detailRow.get("MEMO").toString());
                        detail.setSpec(detailRow.get("SPEC").toString());
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
    protected String getQuerySql(DCP_ROrderDetailQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();

        StringBuffer sqlbuf=new StringBuffer();


        String rOrderNo = req.getRequest().getROrderNo();

        sqlbuf.append(" "
                + " select"
                + " a.bdate,a.RORDERNO,a.RDATE,a.RDAYS,a.TOTCQTY,a.TOTPQTY,a.memo,a.STATUS,"
                + " a.createby ,e1.op_name as createbyname,a.modifyby,e2.op_name as modifybyname,a.createTime,a.modifyTime,a.departId,d1.departname as departNAME,a.confirmby,a.confirmtime,e3.op_name as confirmbyname,"
                + " a.cancelby,e4.op_name as cancelbyname,a.closeby,e5.op_name as closebyname,a.canceltime,a.closetime,a.employeeid,e0.name as employeename  "
                + " from DCP_RORDER a"
                + " left join dcp_employee e0 on e0.eid=a.eid and e0.employeeno=a.EMPLOYEEID "
                + " left join platform_staffs_lang e1 on e1.eid=a.eid and e1.opno=a.createBy and e1.lang_type='"+req.getLangType()+"' "
                + " left join platform_staffs_lang e2 on e2.eid=a.eid and e2.opno=a.modifyby and e2.lang_type='"+req.getLangType()+"' "
                + " left join platform_staffs_lang e3 on e3.eid=a.eid and e3.opno=a.confirmby and e3.lang_type='"+req.getLangType()+"' "
                + " left join platform_staffs_lang e4 on e4.eid=a.eid and e4.opno=a.cancelby and e4.lang_type='"+req.getLangType()+"' "
                + " left join platform_staffs_lang e5 on e5.eid=a.eid and e5.opno=a.closeby and e5.lang_type='"+req.getLangType()+"' "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.departid and d1.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.organizationno='"+req.getOrganizationNO()+"' "
                + " and a.rorderno='"+rOrderNo+"'"
                + " ");

        return sqlbuf.toString();
    }

    private String getDetailSql(DCP_ROrderDetailQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eid = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String rOrderNo = req.getRequest().getROrderNo();
        String langType = req.getLangType();

        sqlbuf.append("" +
                "" +
                " select a.item,a.plubarcode,a.pluno,a.featureno,b.plu_name as pluname,c.featurename,a.punit,a.baseunit,d.uname as punitname,e.uname as baseunitname," +
                " a.pqty,a.baseqty,a.unitRatio,a.memo,a.status,a.dailyPqty,a.dailyqty as avgDeliverQty,a.forecastQty,a.mulqty ,a.minqty,a.stockQty,a.safeqty,nvl(f.PRODUCEQTY,0) as PRODUCEQTY,gul.spec " +
                " from DCP_RORDER_DETAIL a " +
                " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+langType+"' " +
                " left join dcp_goods_feature_lang c on a.eid=c.eid and a.pluno=c.pluno and a.featureno=c.featureno and c.lang_type='"+langType+"' " +
                " left join dcp_unit_lang d on a.eid=d.eid and a.punit=d.unit and d.lang_type='"+langType+"' " +
                " left join dcp_unit_lang e on e.eid=a.eid and e.unit=a.baseunit and e.lang_type='"+langType+"'" +
                " left join DCP_DEMAND f on f.eid=a.eid and f.organizationno=a.organizationno and f.orderno=a.rorderno and f.item=a.item and f.ordertype='3' " +
                " left join DCP_GOODS_UNIT_LANG gul on gul.eid=a.eid and gul.pluno=a.pluno and gul.ounit=a.punit and gul.lang_type='"+langType+"' " +
                " where a.eid='"+eid+"' and a.organizationno='"+organizationNO+"' and a.rorderno='"+rOrderNo+"'" +
                "");


        return sqlbuf.toString();
    }

}



