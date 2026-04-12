package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CategorySubjectDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_CategorySubjectDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_CategorySubjectDetailQuery extends SPosBasicService<DCP_CategorySubjectDetailQueryReq, DCP_CategorySubjectDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_CategorySubjectDetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_CategorySubjectDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_CategorySubjectDetailQueryReq>(){};
    }

    @Override
    protected DCP_CategorySubjectDetailQueryRes getResponseType() {
        return new DCP_CategorySubjectDetailQueryRes();
    }

    @Override
    protected DCP_CategorySubjectDetailQueryRes processJson(DCP_CategorySubjectDetailQueryReq req) throws Exception {
        DCP_CategorySubjectDetailQueryRes res = this.getResponse();

        res.setDatas(new ArrayList<>());

        String arSql="select a.*,b.account as accountname,c.subjectname as REVSUBJECTNAME,e.subjectname as INVSUBJECTNAME,d.subjectname as COSTSUBJECTNAME,f.subjectname as DISCSUBJECTNAME, " +
                " g1.op_name as createbyname,g2.op_name as modifybyname,g3.op_name as confirmbyName,g4.op_name as cancelbyName,h.CATEGORY_NAME as categoryName " +
                " from DCP_CATEGORYSUBJECT   a " +
                " left join DCP_ACOUNT_SETTING b on a.eid=b.eid and a.accountid=b.accountid " +
                " left join dcp_coa c on c.eid=a.eid and c.coarefid=a.coarefid and c.accountid=a.accountid and c.subjectid=a.REVSUBJECT " +
                " left join dcp_coa d on d.eid=a.eid and d.coarefid=a.coarefid and d.accountid=a.accountid and d.subjectid=a.COSTSUBJECT " +
                " left join dcp_coa e on e.eid=a.eid and e.coarefid=a.coarefid and e.accountid=a.accountid and e.subjectid=a.INVSUBJECT " +
                " left join dcp_coa f on f.eid=a.eid and f.coarefid=a.coarefid and f.accountid=a.accountid and f.subjectid=a.DISCSUBJECT " +
                " left join platform_staffs_lang g1 on g1.eid=a.eid and g1.opno=a.createby and g1.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang g2 on g2.eid=a.eid and g2.opno=a.modifyby and g2.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang g3 on g3.eid=a.eid and g3.opno=a.confirmby and g3.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang g4 on g4.eid=a.eid and g4.opno=a.cancelby and g4.lang_type='"+req.getLangType()+"' " +
                " left join DCP_CATEGORY_LANG h on h.eid=a.eid and h.CATEGORY=a.CATEGORY and h.lang_type='"+req.getLangType()+"'" +

                "" +
                " where a.eid='"+req.geteId()+"' and a.accountid='"+req.getRequest().getAccountId()+"' " +
                " ";
        List<Map<String, Object>> list = this.doQueryData(arSql, null);
        DCP_CategorySubjectDetailQueryRes.Level1Elm level1Elm = res.new Level1Elm();

        if(list.size()>0){
            level1Elm.setAccountId(list.get(0).get("ACCOUNTID").toString());
            level1Elm.setAccountName(list.get(0).get("ACCOUNTNAME").toString());
            level1Elm.setCoaRefId(list.get(0).get("COAREFID").toString());
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
                DCP_CategorySubjectDetailQueryRes.SetupList setupList = res.new SetupList();
                setupList.setAccountId(map.get("ACCOUNTID").toString());

                setupList.setAccountId(map.get("ACCOUNTID").toString());
                setupList.setCategory(map.get("CATEGORY").toString());
                setupList.setCategoryName(map.get("CATEGORYNAME").toString());
                setupList.setCoaRefId(map.get("COAREFID").toString());
                setupList.setRevSubject(map.get("REVSUBJECT").toString());
                setupList.setRevSubjectName(map.get("REVSUBJECTNAME").toString());
                setupList.setCostSubject(map.get("COSTSUBJECT").toString());
                setupList.setCostSubjectName(map.get("COSTSUBJECTNAME").toString());
                setupList.setInvSubject(map.get("INVSUBJECT").toString());
                setupList.setInvSubjectName(map.get("INVSUBJECTNAME").toString());
                setupList.setDiscSubject(map.get("DISCSUBJECT").toString());
                setupList.setDiscSubjectName(map.get("DISCSUBJECTNAME").toString());
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
    protected String getQuerySql(DCP_CategorySubjectDetailQueryReq req) throws Exception {


        return "";
    }
}
