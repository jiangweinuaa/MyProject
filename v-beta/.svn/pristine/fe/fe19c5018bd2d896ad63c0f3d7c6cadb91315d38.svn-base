package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_TaxSubjectDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_TaxSubjectDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_TaxSubjectDetailQuery extends SPosBasicService<DCP_TaxSubjectDetailQueryReq, DCP_TaxSubjectDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_TaxSubjectDetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_TaxSubjectDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_TaxSubjectDetailQueryReq>(){};
    }

    @Override
    protected DCP_TaxSubjectDetailQueryRes getResponseType() {
        return new DCP_TaxSubjectDetailQueryRes();
    }

    @Override
    protected DCP_TaxSubjectDetailQueryRes processJson(DCP_TaxSubjectDetailQueryReq req) throws Exception {
        DCP_TaxSubjectDetailQueryRes res = this.getResponse();

        res.setDatas(new ArrayList<>());

        String arSql="select a.*,b.account,c.subjectname,d.taxname,e.op_name as createbyname,f.op_name as modifybyname,g.op_name as confirmbyname,h.op_name as cancelbyname " +
                " from DCP_TAXSUBJECT  a " +
                " left join DCP_ACOUNT_SETTING b on a.eid=b.eid and a.accountid=b.accountid  " +
                " left join DCP_COA c on c.eid=a.eid and c.accountid=a.accountid and c.subjectid=a.subjectid " +
                " left join DCP_TAXCATEGORY d0 on d0.eid=a.eid and d0.taxcode=a.taxcode  " +
                " left join DCP_TAXCATEGORY_LANG d on d.eid=a.eid and d.taxcode=a.taxcode and d0.taxarea=d.taxarea and d.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang e on e.eid=a.eid and e.opno=a.createby and e.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang f on f.eid=a.eid and f.opno=a.modifyby and f.lang_type='"+req.getLangType()+"'  " +
                " left join platform_staffs_lang g on g.eid=a.eid and g.opno=a.confirmby and g.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang h on h.eid=a.eid and h.opno=a.cancelby and h.lang_type='"+req.getLangType()+"'" +
                " where a.eid='"+req.geteId()+"' and a.accountid='"+req.getRequest().getAccountId()+"' " +
                " ";
        List<Map<String, Object>> list = this.doQueryData(arSql, null);
        DCP_TaxSubjectDetailQueryRes.Level1Elm level1Elm = res.new Level1Elm();

        if(list.size()>0){
            level1Elm.setAccountId(list.get(0).get("ACCOUNTID").toString());
            level1Elm.setAccountName(list.get(0).get("ACCOUNT").toString());
            level1Elm.setCoaRefID(list.get(0).get("COAREFID").toString());
            level1Elm.setStatus(list.get(0).get("STATUS").toString());
            level1Elm.setCreateBy(list.get(0).get("CREATEBY").toString());
            level1Elm.setCreateByName(list.get(0).get("CREATEBYNAME").toString());
            level1Elm.setModifyByName(list.get(0).get("MODIFYBYNAME").toString());
            level1Elm.setCreate_Date(list.get(0).get("CREATE_DATE").toString());
            level1Elm.setCreate_Time(list.get(0).get("CREATE_TIME").toString());
            level1Elm.setModifyBy(list.get(0).get("MODIFYBY").toString());
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
                DCP_TaxSubjectDetailQueryRes.SetupList setupList = res.new SetupList();
                setupList.setAccountId(map.get("ACCOUNTID").toString());
                setupList.setSetupType(map.get("SETUPTYPE").toString());
                setupList.setTaxCode(map.get("TAXCODE").toString());
                setupList.setSubjectId(map.get("SUBJECTID").toString());
                setupList.setMemo(map.get("SUBJECTNAME").toString());
                setupList.setSubjectName(map.get("SUBJECTNAME").toString());
                setupList.setTaxName(map.get("TAXNAME").toString());
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
    protected String getQuerySql(DCP_TaxSubjectDetailQueryReq req) throws Exception {


        return "";
    }
}


