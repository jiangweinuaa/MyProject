package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_InterSettSettingVQueryReq;
import com.dsc.spos.json.cust.res.DCP_InterSettSettingVQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_InterSettSettingVQuery extends SPosBasicService<DCP_InterSettSettingVQueryReq, DCP_InterSettSettingVQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_InterSettSettingVQueryReq req) throws Exception {
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
    protected TypeToken<DCP_InterSettSettingVQueryReq> getRequestType() {
        return new TypeToken<DCP_InterSettSettingVQueryReq>(){};
    }

    @Override
    protected DCP_InterSettSettingVQueryRes getResponseType() {
        return new DCP_InterSettSettingVQueryRes();
    }

    @Override
    protected DCP_InterSettSettingVQueryRes processJson(DCP_InterSettSettingVQueryReq req) throws Exception {
        DCP_InterSettSettingVQueryRes res = this.getResponse();
        int totalRecords=0;
        int totalPages=0;
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {
            //算總頁數
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> row : getQData){
                DCP_InterSettSettingVQueryRes.levelElm level1Elm = res.new levelElm();

                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setProcessNo(row.get("PROCESSNO").toString());
                level1Elm.setBusinessType(row.get("BTYPE").toString());
                level1Elm.setSupplyObject(row.get("SUPPLYOBJECT").toString());
                level1Elm.setSupplyObjectName(row.get("SUPPLYOBJECTNAME").toString());
                level1Elm.setDemandObject(row.get("DEMANDOBJECT").toString());
                level1Elm.setPriceType(row.get("PRICETYPE").toString());
                level1Elm.setDemandObjectName(row.get("DEMANDOBJECTNAME").toString());
                level1Elm.setRelationship(row.get("RELATIONSHIP").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                level1Elm.setCreateBy(row.get("CREATEBY").toString());
                level1Elm.setCreate_Date(row.get("CREATE_DATE").toString());
                level1Elm.setCreate_Time(row.get("CREATE_TIME").toString());
                level1Elm.setModifyBy(row.get("MODIFYBY").toString());
                level1Elm.setModify_Date(row.get("MODIFY_DATE").toString());
                level1Elm.setModify_Time(row.get("MODIFY_TIME").toString());
                level1Elm.setConfirmBy(row.get("CONFIRMBY").toString());
                level1Elm.setConfirm_Date(row.get("CONFIRM_DATE").toString());
                level1Elm.setConfirm_Time(row.get("CONFIRM_TIME").toString());
                level1Elm.setCancelBy(row.get("CANCELBY").toString());
                level1Elm.setCancel_Date(row.get("CANCEL_DATE").toString());
                level1Elm.setCancel_Time(row.get("CANCEL_TIME").toString());
                level1Elm.setVersionNum(row.get("VERSIONNUM").toString());
                res.getDatas().add(level1Elm);

            }

        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_InterSettSettingVQueryReq req) throws Exception {
        String eId = req.geteId();
        StringBuffer sqlbuf=new StringBuffer();
        //String status = req.getRequest().getStatus();
        //String businessType = req.getRequest().getBusinessType();
        String demandObject = req.getRequest().getDemandObject();
        String relationship = req.getRequest().getRelationship();
        //String processNo = req.getRequest().getProcessNo();
        String supplyObject = req.getRequest().getSupplyObject();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with INTERSETTSETTING as ("
                + " select distinct a.PROCESSNO from DCP_INTERSETTSETTING_V a"
                + " where a.eid='"+eId+"' "
        );

        //if(Check.NotNull(businessType)){
        //   sqlbuf.append(" and a.BTYPE='"+businessType+"'");
        //}
        if(Check.NotNull(demandObject)){
            sqlbuf.append(" and (a.demandObject='"+demandObject+"' ) ");
        }
        if(Check.NotNull(supplyObject)){
            sqlbuf.append(" and a.supplyObject='"+supplyObject+"' ");
        }

        if(Check.NotNull(relationship)){
            sqlbuf.append(" and a.relationship='"+relationship+"' ");
        }
        //if(Check.NotNull(processNo)){
        //   sqlbuf.append(" and a.processNo='"+processNo+"' ");
        //}


        //if (!Check.Null(status)){
        //    sqlbuf.append(" and a.status='"+status+"' ");
        //}

        sqlbuf.append(" ORDER by a.processNo");
        sqlbuf.append(" )");

        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.PROCESSNO asc,a.versionnum) as rn,"
                + " a.status,a.PROCESSNO,a.BTYPE,a.supplyObject,a.demandObject ,a.relationship,a.memo," +
                "a.createBy,a.create_Date,a.create_Time,a.modifyBy,a.modify_Date,a.modify_Time,a.confirmBy,a.confirm_Date,a.confirm_Time," +
                "a.cancelBy,a.cancel_Date,a.cancel_Time,c.org_name as supplyObjectName,d.org_name as demandObjectName,a.priceType,a.versionnum   "
                + " from DCP_INTERSETTSETTING_V a"
                + " inner join INTERSETTSETTING b on a.PROCESSNO=b.PROCESSNO " +
                " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.SUPPLYOBJECT and c.lang_type='"+req.getLangType()+"' " +
                " left join dcp_org_lang d on d.eid=a.eid and d.organizationno=a.demandobject and d.lang_type='"+req.getLangType()+"' "
                + " where a.eid='"+eId+"' "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}


