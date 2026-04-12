package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_InterSettSetDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_InterSettSetDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_InterSettSetDetailQuery extends SPosBasicService<DCP_InterSettSetDetailQueryReq, DCP_InterSettSetDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_InterSettSetDetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_InterSettSetDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_InterSettSetDetailQueryReq>(){};
    }

    @Override
    protected DCP_InterSettSetDetailQueryRes getResponseType() {
        return new DCP_InterSettSetDetailQueryRes();
    }

    @Override
    protected DCP_InterSettSetDetailQueryRes processJson(DCP_InterSettSetDetailQueryReq req) throws Exception {
        DCP_InterSettSetDetailQueryRes res = this.getResponse();

        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);

        String detailSql = this.getDetailSql(req);
        List<Map<String, Object>> getDetailData=this.doQueryData(detailSql, null);

        res.setDatas(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {
            for (Map<String, Object> row : getQData){
                DCP_InterSettSetDetailQueryRes.levelElm levelElm = res.new levelElm();
                levelElm.setStatus(row.get("STATUS").toString());
                levelElm.setProcessNo(row.get("PROCESSNO").toString());
                levelElm.setBusinessType(row.get("BTYPE").toString());
                levelElm.setSupplyObject(row.get("SUPPLYOBJECT").toString());
                levelElm.setDemandObject(row.get("DEMANDOBJECT").toString());
                levelElm.setSupplyObjectName(row.get("SUPPLYOBJECTNAME").toString());
                levelElm.setDemandObjectName(row.get("DEMANDOBJECTNAME").toString());
                levelElm.setRelationship(row.get("RELATIONSHIP").toString());
                levelElm.setMemo(row.get("MEMO").toString());
                levelElm.setCreateBy(row.get("CREATEBY").toString());
                levelElm.setCreate_Date(row.get("CREATE_DATE").toString());
                levelElm.setCreate_Time(row.get("CREATE_TIME").toString());
                levelElm.setModifyBy(row.get("MODIFYBY").toString());
                levelElm.setModify_Date(row.get("MODIFY_DATE").toString());
                levelElm.setModify_Time(row.get("MODIFY_TIME").toString());
                levelElm.setConfirmBy(row.get("CONFIRMBY").toString());
                levelElm.setConfirm_Date(row.get("CONFIRM_DATE").toString());
                levelElm.setConfirm_Time(row.get("CONFIRM_TIME").toString());
                levelElm.setCancelBy(row.get("CANCELBY").toString());
                levelElm.setCancel_Date(row.get("CANCEL_DATE").toString());
                levelElm.setCancel_Time(row.get("CANCEL_TIME").toString());
                levelElm.setPriceType(row.get("PRICETYPE").toString());
                levelElm.setBType(row.get("BTYPE").toString());
                levelElm.setVersionNum(row.get("VERSIONNUM").toString());

                levelElm.setObject(new ArrayList<>());

                for (Map<String, Object> detailRow : getDetailData){
                    DCP_InterSettSetDetailQueryRes.ObjectList objectList = res.new ObjectList();

                    objectList.setItem(detailRow.get("ITEM").toString());
                    objectList.setObject(detailRow.get("OBJECT").toString());
                    objectList.setObjectName(detailRow.get("OBJECTNAME").toString());
                    objectList.setPriceType(detailRow.get("PRICETYPE").toString());
                    objectList.setWarehouse(detailRow.get("WAREHOUSE").toString());
                    objectList.setWarehouseName(detailRow.get("WAREHOUSENAME").toString());
                    objectList.setSupplyObject(detailRow.get("SUPPLYOBJECT").toString());
                    objectList.setSupplyObjectName(detailRow.get("SUPPLYOBJECTNAME").toString());
                    objectList.setDemandObject(detailRow.get("DEMANDOBJECT1").toString());
                    objectList.setDemandObjectName(detailRow.get("DEMANDOBJECTNAME").toString());
                    levelElm.getObject().add(objectList);

                }

                res.getDatas().add(levelElm);

            }
        }

        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_InterSettSetDetailQueryReq req) throws Exception {
        String langType = req.getLangType();
        String eId = req.getRequest().getEId();
        if(Check.Null(eId)){
            eId=req.geteId();
        }
        String processNo = req.getRequest().getProcessNo();
        StringBuffer sqlbuf=new StringBuffer();
        String tableName="DCP_INTERSETTSETTING";
        if(Check.NotNull(req.getRequest().getVersionNum())){
            tableName="DCP_INTERSETTSETTING_V";
        }

        sqlbuf.append("" +
                "select a.*,c.org_name as supplyObjectName,d.org_name as demandObjectName " +
                " from "+tableName+" a " +
                " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.SUPPLYOBJECT and c.lang_type='"+req.getLangType()+"' " +
                " left join dcp_org_lang d on d.eid=a.eid and d.organizationno=a.demandobject and d.lang_type='"+req.getLangType()+"' "+
                " where a.eid='"+eId+"' " +
                " and a.processno='"+processNo+"' ");
        if(Check.NotNull(req.getRequest().getVersionNum())){
            sqlbuf.append(" and a.versionnum="+req.getRequest().getVersionNum());
        }
        return sqlbuf.toString();
    }

    private String getDetailSql(DCP_InterSettSetDetailQueryReq req) throws Exception{
        String langType = req.getLangType();
        String eId = req.getRequest().getEId();
        if(Check.Null(eId)){
            eId=req.geteId();
        }

        String tableName="DCP_INTERSETTSETDETAIL";
        if(Check.NotNull(req.getRequest().getVersionNum())){
            tableName="DCP_INTERSETTSETDETAIL_V";
        }
        String processNo = req.getRequest().getProcessNo();
        StringBuffer sqlbuf=new StringBuffer();
        sqlbuf.append("" +
                " select distinct a.*,c.org_name as objectName,d.WAREHOUSE_NAME as warehousename,e.org_name as supplyobjectname,f.org_name as DEMANDOBJECTNAME " +
                "  from "+tableName+" a " +
                " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.object and c.lang_type='"+req.getLangType()+"' " +
                " left join DCP_WAREHOUSE_LANG d on d.eid=a.eid and d.warehouse=a.warehouse and d.organizationno=a.object and d.lang_type='"+req.getLangType()+"'" +
                " left join dcp_org_lang e on e.eid=a.eid and e.organizationno=a.supplyobject and e.lang_type='"+req.getLangType()+"' " +
                " left join dcp_org_lang f on f.eid=a.eid and f.organizationno=a.demandobject1 and f.lang_type='"+req.getLangType()+"' " +
                " where a.eid='"+eId+"' and a.processno='"+processNo+"' " +
                " order by a.item " +
                " ");

        if(Check.NotNull(req.getRequest().getVersionNum())){
            sqlbuf.append(" and a.versionnum="+req.getRequest().getVersionNum());
        }

        return sqlbuf.toString();
    }

}

