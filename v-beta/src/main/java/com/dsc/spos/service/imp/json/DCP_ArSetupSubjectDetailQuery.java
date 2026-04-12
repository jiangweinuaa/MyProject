package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ArSetupSubjectDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ArSetupSubjectDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ArSetupSubjectDetailQuery extends SPosBasicService<DCP_ArSetupSubjectDetailQueryReq, DCP_ArSetupSubjectDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ArSetupSubjectDetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ArSetupSubjectDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ArSetupSubjectDetailQueryReq>(){};
    }

    @Override
    protected DCP_ArSetupSubjectDetailQueryRes getResponseType() {
        return new DCP_ArSetupSubjectDetailQueryRes();
    }

    @Override
    protected DCP_ArSetupSubjectDetailQueryRes processJson(DCP_ArSetupSubjectDetailQueryReq req) throws Exception {
        DCP_ArSetupSubjectDetailQueryRes res = this.getResponse();

        res.setDatas(new ArrayList<>());

        String arSql="select a.*,b.account as accountname,c.SUBJECTNAME," +
                " g1.op_name as createbyname,g2.op_name as modifybyname,g3.op_name as confirmbyName,g4.op_name as cancelbyName " +
                " from DCP_ARSETUPSUBJECT a " +
                " LEFT JOIN DCP_ACOUNT_SETTING b on a.eid=b.eid and a.ACCOUNTID=b.ACCOUNTID " +
                " left join dcp_coa c on c.eid=a.eid and c.accountid=a.accountid and c.coarefid=a.coarefid and c.subjectid=a.subjectid "+
                " left join platform_staffs_lang g1 on g1.eid=a.eid and g1.opno=a.createby and g1.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang g2 on g2.eid=a.eid and g2.opno=a.modifyby and g2.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang g3 on g3.eid=a.eid and g3.opno=a.confirmby and g3.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang g4 on g4.eid=a.eid and g4.opno=a.cancelby and g4.lang_type='"+req.getLangType()+"' " +
                " where a.eid='"+req.geteId()+"' and a.accountid='"+req.getRequest().getAccountId()+"' " +
                " and a.SETUPTYPE='"+req.getRequest().getSetupType()+"' ";
        List<Map<String, Object>> list = this.doQueryData(arSql, null);
        DCP_ArSetupSubjectDetailQueryRes.Level1Elm level1Elm = res.new Level1Elm();

        if(list.size()>0){
            level1Elm.setAccountId(list.get(0).get("ACCOUNTID").toString());
            level1Elm.setAccountName(list.get(0).get("ACCOUNTNAME").toString());
            level1Elm.setCoaRefID(list.get(0).get("COAREFID").toString());
            level1Elm.setSetupType(list.get(0).get("SETUPTYPE").toString());
            level1Elm.setStatus(list.get(0).get("STATUS").toString());
            level1Elm.setCreateBy(list.get(0).get("CREATEBY").toString());
            level1Elm.setCreateByName(list.get(0).get("CREATEBYNAME").toString());
            level1Elm.setCreate_Date(list.get(0).get("CREATE_DATE").toString());
            level1Elm.setCreate_Time(list.get(0).get("CREATE_TIME").toString());
            level1Elm.setModifyBy(list.get(0).get("MODIFYBY").toString());
            level1Elm.setModifyByName(list.get(0).get("MODIFYBYNAME").toString());
            level1Elm.setModify_Date(list.get(0).get("MODIFY_DATE").toString());
            level1Elm.setModify_Time(list.get(0).get("MODIFY_TIME").toString());
            level1Elm.setConfirmBy(list.get(0).get("CONFIRMBY").toString());
            level1Elm.setConfirmByName(list.get(0).get("CONFIRMBYNAME").toString());
            level1Elm.setConfirm_Date(list.get(0).get("CONFIRM_DATE").toString());
            level1Elm.setConfirm_Time(list.get(0).get("CONFIRM_TIME").toString());
            level1Elm.setCancelBy(list.get(0).get("CANCELBY").toString());
            level1Elm.setCancelByName(list.get(0).get("CANCELBYNAME").toString());
            level1Elm.setCancel_Date(list.get(0).get("CANCEL_DATE").toString());
            level1Elm.setCancel_Time(list.get(0).get("CANCEL_TIME").toString());

            level1Elm.setSetupList(new ArrayList<>());
            for (Map<String, Object> map : list){
                DCP_ArSetupSubjectDetailQueryRes.SetupList setupList = res.new SetupList();
                setupList.setAccountId(map.get("ACCOUNTID").toString());
                setupList.setAccountName(map.get("ACCOUNTNAME").toString());
                setupList.setSetupType(map.get("SETUPTYPE").toString());
                setupList.setSetupItem(map.get("SETUPITEM").toString());
                setupList.setSetupDiscrip(map.get("SETUPDISCRIP").toString());
                setupList.setSubjectId(map.get("SUBJECTID").toString());
                setupList.setSubjectName(map.get("SUBJECTNAME").toString());
                setupList.setSubjectDBCR(map.get("SUBJECTDBCR").toString());
                setupList.setSubjectSumType(map.get("SUBJECTSUMTYPE").toString());
                setupList.setDiscSubject(map.get("DISCSUBJECT").toString());
                setupList.setMemo(map.get("MEMO").toString());
                level1Elm.getSetupList().add(setupList);
            }

        }


        res.getDatas().add(level1Elm);
        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_ArSetupSubjectDetailQueryReq req) throws Exception {
        return "";
    }
}


