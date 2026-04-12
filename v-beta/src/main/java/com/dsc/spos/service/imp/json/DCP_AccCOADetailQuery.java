package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_AccCOADetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_AccCOADetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_AccCOADetailQuery  extends SPosBasicService<DCP_AccCOADetailQueryReq, DCP_AccCOADetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_AccCOADetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_AccCOADetailQueryReq> getRequestType() {
        return new TypeToken<DCP_AccCOADetailQueryReq>(){};
    }

    @Override
    protected DCP_AccCOADetailQueryRes getResponseType() {
        return new DCP_AccCOADetailQueryRes();
    }

    @Override
    protected DCP_AccCOADetailQueryRes processJson(DCP_AccCOADetailQueryReq req) throws Exception {
        DCP_AccCOADetailQueryRes res = this.getResponse();

        res.setDatas(new ArrayList<>());

        String sql="select * from dcp_coa a where a.eid='"+req.geteId()+"' " +
                " and a.coarefid='"+req.getRequest().getCoaRefID()+"'" +
                " and a.subjectid='"+req.getRequest().getSubjectId()+"' " +
                " and a.accountid!='ALLCOA' ";

        if(Check.NotNull(req.getRequest().getAccountId())){
            sql+=" and a.accountid='"+req.getRequest().getAccountId()+"' ";
        }

        List<Map<String, Object>> list = this.doQueryData(sql, null);

        if(list.size()>0){
            Map<String, Object> stringObjectMap = list.get(0);
            DCP_AccCOADetailQueryRes.Level1Elm level1Elm = res.new Level1Elm();
            level1Elm.setCoaRefID(stringObjectMap.get("COAREFID").toString());
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
            level1Elm.setFinAnalSource(stringObjectMap.get("FINANALYSOURC").toString());
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
            level1Elm.setFreeChars1TypeId(stringObjectMap.get("FREECHARS1TYPEID").toString());
            level1Elm.setFreeChars1CtrlMode(stringObjectMap.get("FREECHARS1CTRLMODE").toString());
            level1Elm.setIsFreeChars2(stringObjectMap.get("ISFREECHARS2").toString());
            level1Elm.setFreeChars2TypeId(stringObjectMap.get("FREECHARS2TYPEID").toString());
            level1Elm.setFreeChars2CtrlMode(stringObjectMap.get("FREECHARS2CTRLMODE").toString());
            level1Elm.setIsFreeChars3(stringObjectMap.get("ISFREECHARS3").toString());
            level1Elm.setFreeChars3TypeId(stringObjectMap.get("FREECHARS3TYPEID").toString());
            level1Elm.setFreeChars3CtrlMode(stringObjectMap.get("FREECHARS3CTRLMODE").toString());
            level1Elm.setStatus(stringObjectMap.get("STATUS").toString());
            level1Elm.setAccountId(stringObjectMap.get("ACCOUNTID").toString());
            level1Elm.setAccountName(stringObjectMap.get("ACCOUNTNAME").toString());

            res.getDatas().add(level1Elm);
        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_AccCOADetailQueryReq req) throws Exception {


        return "";
    }
}


