package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ApSetupSubjectDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ApSetupSubjectDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ApSetupSubjectDetailQuery extends SPosBasicService<DCP_ApSetupSubjectDetailQueryReq, DCP_ApSetupSubjectDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ApSetupSubjectDetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ApSetupSubjectDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ApSetupSubjectDetailQueryReq>(){};
    }

    @Override
    protected DCP_ApSetupSubjectDetailQueryRes getResponseType() {
        return new DCP_ApSetupSubjectDetailQueryRes();
    }

    @Override
    protected DCP_ApSetupSubjectDetailQueryRes processJson(DCP_ApSetupSubjectDetailQueryReq req) throws Exception {
        DCP_ApSetupSubjectDetailQueryRes res = this.getResponse();
        DCP_ApSetupSubjectDetailQueryRes.level1Elm level1Elm1 = res.new level1Elm();


        String arSql="select a.*,b.account,c.subjectname,d.subjectname as discsubjectname,e.op_name as createbyname,f.op_name as modifybyname,g.op_name as confirmbyname,h.op_name as cancelbyname " +
                " from DCP_APSETUPSUBJECT  a " +
                " left join DCP_ACOUNT_SETTING b on a.eid=b.eid and a.accountid=b.accountid  " +
                " left join DCP_COA c on c.eid=a.eid and c.accountid=a.accountid and c.subjectid=a.subjectid " +
                " left join DCP_COA d on d.eid=a.eid and d.accountid=a.accountid and d.subjectid=a.DISCSUBJECT  " +
                " left join platform_staffs_lang e on e.eid=a.eid and e.opno=a.createby and e.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang f on f.eid=a.eid and f.opno=a.modifyby and f.lang_type='"+req.getLangType()+"'  " +
                " left join platform_staffs_lang g on g.eid=a.eid and g.opno=a.confirmby and g.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang h on h.eid=a.eid and h.opno=a.cancelby and h.lang_type='"+req.getLangType()+"'" +
                " where a.eid='"+req.geteId()+"' and a.accountid='"+req.getRequest().getAccountId()+"' " +
                " ";
        if(Check.NotNull(req.getRequest().getStatus())){
            arSql+=" and a.status='"+req.getRequest().getStatus()+"'";
        }
        if(Check.NotNull(req.getRequest().getSetupType())){
            arSql+=" and a.setuptype='"+req.getRequest().getSetupType()+"'";
        }
        List<Map<String, Object>> list = this.doQueryData(arSql, null);

        if(list.size()>0){
            level1Elm1.setAccountId(list.get(0).get("ACCOUNTID").toString());
            level1Elm1.setAccountName(list.get(0).get("ACCOUNT").toString());
            level1Elm1.setCoaRefID(list.get(0).get("COAREFID").toString());
            level1Elm1.setStatus(list.get(0).get("STATUS").toString());
            level1Elm1.setCreateBy(list.get(0).get("CREATEBY").toString());
            level1Elm1.setCreateByName(list.get(0).get("CREATEBYNAME").toString());
            level1Elm1.setModifyByName(list.get(0).get("MODIFYBYNAME").toString());
            level1Elm1.setCreate_Date(list.get(0).get("CREATE_DATE").toString());
            level1Elm1.setCreate_Time(list.get(0).get("CREATE_TIME").toString());
            level1Elm1.setModifyBy(list.get(0).get("MODIFYBY").toString());
            level1Elm1.setModify_Date(list.get(0).get("MODIFY_DATE").toString());
            level1Elm1.setModify_Time(list.get(0).get("MODIFY_TIME").toString());
            level1Elm1.setConfirmBy(list.get(0).get("CONFIRMBY").toString());
            level1Elm1.setConfirmByName(list.get(0).get("CONFIRMBYNAME").toString());
            level1Elm1.setConfirm_Date(list.get(0).get("CONFIRM_DATE").toString());
            level1Elm1.setConfirm_Time(list.get(0).get("CONFIRM_TIME").toString());
            level1Elm1.setCancelBy(list.get(0).get("CANCELBY").toString());
            level1Elm1.setCancelByName(list.get(0).get("CANCELBYNAME").toString());
            level1Elm1.setCancel_Date(list.get(0).get("CANCEL_DATE").toString());
            level1Elm1.setCancel_Time(list.get(0).get("CANCEL_TIME").toString());

            level1Elm1.setSetupList(new ArrayList<>());
            for (Map<String, Object> map : list){
                DCP_ApSetupSubjectDetailQueryRes.SetupTypeList setupList = res.new SetupTypeList();
                setupList.setAccountId(map.get("ACCOUNTID").toString());
                setupList.setAccountName(map.get("ACCOUNT").toString());
                setupList.setSetupType(map.get("SETUPTYPE").toString());
                setupList.setSetupItem(map.get("SETUPITEM").toString());
                setupList.setSetupDiscrip(map.get("SETUPDISCRIP").toString());
                setupList.setSubjectId(map.get("SUBJECTID").toString());
                setupList.setSubjectName(map.get("SUBJECTNAME").toString());
                setupList.setSubjectDBCR(map.get("SUBJECTDBCR").toString());
                setupList.setSubjectSumType(map.get("SUBJECTSUMTYPE").toString());
                setupList.setDiscSubject(map.get("DISCSUBJECT").toString());
                setupList.setDiscSubjectName(map.get("DISCSUBJECTNAME").toString());
                setupList.setMemo(map.get("MEMO").toString());

                level1Elm1.getSetupList().add(setupList);
            }

        }


        res.setDatas(level1Elm1);
        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_ApSetupSubjectDetailQueryReq req) throws Exception {


        return "";
    }
}


