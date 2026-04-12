package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_BsNoSetSubjectDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_BsNoSetSubjectDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_BsNoSetSubjectDetailQuery extends SPosBasicService<DCP_BsNoSetSubjectDetailQueryReq, DCP_BsNoSetSubjectDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_BsNoSetSubjectDetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_BsNoSetSubjectDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_BsNoSetSubjectDetailQueryReq>(){};
    }

    @Override
    protected DCP_BsNoSetSubjectDetailQueryRes getResponseType() {
        return new DCP_BsNoSetSubjectDetailQueryRes();
    }

    @Override
    protected DCP_BsNoSetSubjectDetailQueryRes processJson(DCP_BsNoSetSubjectDetailQueryReq req) throws Exception {
        DCP_BsNoSetSubjectDetailQueryRes res = this.getResponse();

        res.setDatas(new ArrayList<>());

        String arSql="select a.*,b.account as accountname,c.subjectname as MANUEXPSUBJECTNAME,e.subjectname as MGMTEXPSUBJECTNAME,d.subjectname as SALESEXPSUBJECTNAME,f.subjectname as RDEXPSUBJECTNAME," +
                " g1.op_name as createbyname,g2.op_name as modifybyname,g3.op_name as confirmbyName,g4.op_name as cancelbyName,h.REASON_NAME as bsname " +
                " from DCP_BSNOSETUPSUBJECT  a " +
                " left join DCP_ACOUNT_SETTING b on a.eid=b.eid and a.accountid=b.accountid  " +
                " left join dcp_coa c on c.eid=a.eid and c.coarefid=a.coarefid and c.accountid=a.accountid and c.subjectid=a.MANUEXPSUBJECT " +
                " left join dcp_coa d on d.eid=a.eid and d.coarefid=a.coarefid and d.accountid=a.accountid and d.subjectid=a.SALESEXPSUBJECT " +
                " left join dcp_coa e on e.eid=a.eid and e.coarefid=a.coarefid and e.accountid=a.accountid and e.subjectid=a.MGMTEXPSUBJECT " +
                " left join dcp_coa f on f.eid=a.eid and f.coarefid=a.coarefid and f.accountid=a.accountid and f.subjectid=a.RDEXPSUBJECT " +
                " left join platform_staffs_lang g1 on g1.eid=a.eid and g1.opno=a.createby and g1.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang g2 on g2.eid=a.eid and g2.opno=a.modifyby and g2.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang g3 on g3.eid=a.eid and g3.opno=a.confirmby and g3.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang g4 on g4.eid=a.eid and g4.opno=a.cancelby and g4.lang_type='"+req.getLangType()+"' " +
                " left join DCP_REASON_LANG h on h.eid=a.eid and h.bsno=a.bsno and h.lang_type='"+req.getLangType()+"'" +

                "" +
                " where a.eid='"+req.geteId()+"' and a.accountid='"+req.getRequest().getAccountId()+"' " +
                " ";
        List<Map<String, Object>> list = this.doQueryData(arSql, null);
        DCP_BsNoSetSubjectDetailQueryRes.Level1Elm level1Elm = res.new Level1Elm();

        if(list.size()>0){
            level1Elm.setAccountId(list.get(0).get("ACCOUNTID").toString());
            level1Elm.setAccountName(list.get(0).get("ACCOUNTNAME").toString());
            level1Elm.setCoaRefID(list.get(0).get("COAREFID").toString());
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
                DCP_BsNoSetSubjectDetailQueryRes.SetupList setupList = res.new SetupList();
                setupList.setAccountId(map.get("ACCOUNTID").toString());
                setupList.setAccountName(map.get("ACCOUNTNAME").toString());
                setupList.setBsNo(map.get("BSNO").toString());
                setupList.setBsName(map.get("BSNAME").toString());
                setupList.setCoaRefId(map.get("COAREFID").toString());
                setupList.setManuExpSubject(map.get("MANUEXPSUBJECT").toString());
                setupList.setManuExpSubjectName(map.get("MANUEXPSUBJECTNAME").toString());
                setupList.setSalesExpSubject(map.get("SALESEXPSUBJECT").toString());
                setupList.setSalesExpSubjectName(map.get("SALESEXPSUBJECTNAME").toString());
                setupList.setMgmtExpSubject(map.get("MGMTEXPSUBJECT").toString());
                setupList.setMgmtExpSubjectName(map.get("MGMTEXPSUBJECTNAME").toString());
                setupList.setRdExpSubject(map.get("RDEXPSUBJECT").toString());
                setupList.setRdExpSubjectName(map.get("RDEXPSUBJECTNAME").toString());
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
    protected String getQuerySql(DCP_BsNoSetSubjectDetailQueryReq req) throws Exception {


        return "";
    }
}


