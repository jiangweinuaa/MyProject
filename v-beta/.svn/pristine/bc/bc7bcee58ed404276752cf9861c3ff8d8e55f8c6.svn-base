package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_COADetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_COADetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_COADetailQuery extends SPosBasicService<DCP_COADetailQueryReq, DCP_COADetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_COADetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_COADetailQueryReq> getRequestType() {
        return new TypeToken<DCP_COADetailQueryReq>(){};
    }

    @Override
    protected DCP_COADetailQueryRes getResponseType() {
        return new DCP_COADetailQueryRes();
    }

    @Override
    protected DCP_COADetailQueryRes processJson(DCP_COADetailQueryReq req) throws Exception {
        DCP_COADetailQueryRes res = this.getResponse();

        res.setDatas(new ArrayList<>());

        String sql="select a.*,b.account as accountname,c.op_name as createbyname,d.op_name as modifybyname," +
                " e.cfname as drCashChgCodename,f.cfname as crCashChgCodename  from dcp_coa a " +
                " left join DCP_ACOUNT_SETTING b on a.eid=b.eid and a.accountid=b.accountid " +
                " left join platform_staffs_lang c on c.eid=a.eid and c.opno=a.createby and c.lang_type='"+req.getLangType()+"' " +
                " left join platform_staffs_lang d on d.eid=a.eid and d.opno=a.modifyby and d.lang_type='"+req.getLangType()+"' " +
                " left join DCP_CFTEMPLATE e on e.eid=a.eid and e.CFCODE=a.drCashChgCode " +
                " left join DCP_CFTEMPLATE f on f.eid=a.eid and f.CFCODE=a.crCashChgCode " +
                " where a.eid='"+req.geteId()+"' " +
                " and a.coarefid='"+req.getRequest().getCoaRefID()+"'" +
                " and a.subjectid='"+req.getRequest().getSubjectId()+"'" +
                //" and a.accountid !='ALLCOA' " +
                "";

        if(Check.NotNull(req.getRequest().getAccountId())){
            sql+=" and a.accountid='"+req.getRequest().getAccountId()+"' ";
        }

        List<Map<String, Object>> list = this.doQueryData(sql, null);

        //list 包含全部
        if(list.size()>0){
            Map<String, Object> stringObjectMap = list.get(0);
            if(Check.Null(req.getRequest().getAccountId())){
                stringObjectMap=list.stream().filter(x->x.get("ACCOUNTID").toString().equals("ALLCOA")).collect(Collectors.toList()).get(0);
            }

            DCP_COADetailQueryRes.Level1Elm level1Elm = res.new Level1Elm();
            level1Elm.setCoaRefID(stringObjectMap.get("COAREFID").toString());
            level1Elm.setAccType(stringObjectMap.get("ACCTYPE").toString());
            level1Elm.setSubjectId(stringObjectMap.get("SUBJECTID").toString());
            level1Elm.setSubjectName(stringObjectMap.get("SUBJECTNAME").toString());
            level1Elm.setSubjectCat(stringObjectMap.get("SUBJECTCAT").toString());
            level1Elm.setLevelID(stringObjectMap.get("LEVELID").toString());
            level1Elm.setUpSubjectId(stringObjectMap.get("UPSUBJECTID").toString());
            level1Elm.setAuxiliaryType(stringObjectMap.get("AUXILIARYTYPE").toString());
            level1Elm.setMemo(stringObjectMap.get("MEMO").toString());
            level1Elm.setSubjectProperty(stringObjectMap.get("SUBJECTPROPERTY").toString());
            level1Elm.setSubjectType(stringObjectMap.get("SUBJECTTYPE").toString());
            level1Elm.setDirection(stringObjectMap.get("DIRECTION").toString());
            level1Elm.setIsDirection(stringObjectMap.get("ISDIRECTION").toString());
            level1Elm.setExpType(stringObjectMap.get("EXPTYPE").toString());
            level1Elm.setFinAnalSource(stringObjectMap.get("FINANALSOURCE").toString());
            level1Elm.setIsCashSubject(stringObjectMap.get("ISCASHSUBJECT").toString());
            level1Elm.setIsEnableDptMng(stringObjectMap.get("ISENABLEDPTMNG").toString());
            level1Elm.setIsEnableTradObjMng(stringObjectMap.get("ISENABLETRADOBJMNG").toString());
            level1Elm.setIsEnableProdCatMng(stringObjectMap.get("ISENABLEPRODCATMNG").toString());
            level1Elm.setIsEnableManMng(stringObjectMap.get("ISENABLEMANMNG").toString());
            level1Elm.setIsMultiCurMng(stringObjectMap.get("ISMULTICURMNG").toString());
            level1Elm.setIsSubsysSubject(stringObjectMap.get("ISSUBSYSSUBJECT").toString());
            level1Elm.setDrCashChgCode(stringObjectMap.get("DRCASHCHGCODE").toString());
            level1Elm.setCrCashChgCode(stringObjectMap.get("CRCASHCHGCODE").toString());
            level1Elm.setIsFreeChars1(stringObjectMap.get("ISFREECHARS1").toString());
            level1Elm.setFreeChars1TypeId(stringObjectMap.get("FREECHARS1_TYPEID").toString());
            level1Elm.setFreeChars1CtrlMode(stringObjectMap.get("FREECHARS1_CTRLMODE").toString());
            level1Elm.setIsFreeChars2(stringObjectMap.get("ISFREECHARS2").toString());
            level1Elm.setFreeChars2TypeId(stringObjectMap.get("FREECHARS2_TYPEID").toString());
            level1Elm.setFreeChars2CtrlMode(stringObjectMap.get("FREECHARS2_CTRLMODE").toString());
            level1Elm.setIsFreeChars3(stringObjectMap.get("ISFREECHARS3").toString());
            level1Elm.setFreeChars3TypeId(stringObjectMap.get("FREECHARS3_TYPEID").toString());
            level1Elm.setFreeChars3CtrlMode(stringObjectMap.get("FREECHARS3_CTRLMODE").toString());
            level1Elm.setStatus(stringObjectMap.get("STATUS").toString());
            level1Elm.setFirstSubjectId(stringObjectMap.get("FIRSTSUBJECTID")==null?"":stringObjectMap.get("FIRSTSUBJECTID").toString());

            level1Elm.setCreateBy(stringObjectMap.get("CREATEBY")==null?"":stringObjectMap.get("CREATEBY").toString());
            level1Elm.setCreateByName(stringObjectMap.get("CREATEBYNAME")==null?"":stringObjectMap.get("CREATEBYNAME").toString());
            level1Elm.setCreateDate(stringObjectMap.get("CREATE_DATE")==null?"":stringObjectMap.get("CREATE_DATE").toString());
            level1Elm.setCreateTime(stringObjectMap.get("CREATE_TIME")==null?"":stringObjectMap.get("CREATE_TIME").toString());
            level1Elm.setModifyBy(stringObjectMap.get("MODIFYBY")==null?"":stringObjectMap.get("MODIFYBY").toString());
            level1Elm.setModifyByName(stringObjectMap.get("MODIFYBYNAME")==null?"":stringObjectMap.get("MODIFYBYNAME").toString());
            level1Elm.setModifyDate(stringObjectMap.get("MODIFY_DATE")==null?"":stringObjectMap.get("MODIFY_DATE").toString());
            level1Elm.setModifyTime(stringObjectMap.get("MODIFY_TIME")==null?"":stringObjectMap.get("MODIFY_TIME").toString());

            level1Elm.setDrCashChgCodeName(stringObjectMap.get("DRCASHCHGCODENAME")==null?"":stringObjectMap.get("DRCASHCHGCODENAME").toString());
            level1Elm.setCrCashChgCodeName(stringObjectMap.get("CRCASHCHGCODENAME")==null?"":stringObjectMap.get("CRCASHCHGCODENAME").toString());
            //private String createBy;
            //        private String createDate;
            //        private String createTime;
            //        private String modifyBy;
            //        private String modifyDate;
            //        private String modifyTime;

            List<Map<String, Object>> filterRows = list.stream().filter(x -> !x.get("ACCOUNTID").toString().equals("ALLCOA")).collect(Collectors.toList());
            level1Elm.setAccountList(
                    filterRows.stream().map(x -> {
                        DCP_COADetailQueryRes.AccountList accountList = res.new AccountList();
                        accountList.setAccountId(x.get("ACCOUNTID").toString());
                        accountList.setAccountName(x.get("ACCOUNTNAME").toString());
                        return accountList;
                    }).collect(Collectors.toList())
            );

            res.getDatas().add(level1Elm);
        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_COADetailQueryReq req) throws Exception {


        return "";
    }
}


