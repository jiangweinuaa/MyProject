package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_AccCOAUpdateReq;
import com.dsc.spos.json.cust.res.DCP_AccCOAUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_AccCOAUpdate extends SPosAdvanceService<DCP_AccCOAUpdateReq, DCP_AccCOAUpdateRes> {

    @Override
    protected void processDUID(DCP_AccCOAUpdateReq req, DCP_AccCOAUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        //String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String modifyTime =	new SimpleDateFormat("HHmmss").format(new Date());
        String modifyDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        DCP_AccCOAUpdateReq.levelRequest request = req.getRequest();

        UptBean ub1 = new UptBean("DCP_COA");
        ub1.addUpdateValue("STATUS", DataValues.newString(request.getStatus()));
        ub1.addUpdateValue("SUBJECTNAME", DataValues.newString(request.getSubjectName()));
        ub1.addUpdateValue("COAREFID", DataValues.newString(request.getCoaRefID()));
        ub1.addUpdateValue("SUBJECTCAT", DataValues.newString(request.getSubjectCat()));
        ub1.addUpdateValue("UPSUBJECTID", DataValues.newString(request.getUpSubjectId()));
        ub1.addUpdateValue("FIRSTSUBJECTID", DataValues.newString(request.getFirstSubject()));
        ub1.addUpdateValue("LEVELID", DataValues.newString(request.getLevelID()));
        ub1.addUpdateValue("SUBJECTPROPERTY", DataValues.newString(request.getSubjectProperty()));
        ub1.addUpdateValue("SUBJECTTYPE", DataValues.newString(request.getSubjectType()));
        ub1.addUpdateValue("DIRECTION", DataValues.newString(request.getDirection()));
        ub1.addUpdateValue("ISDIRECTION", DataValues.newString(request.getIsDirection()));
        ub1.addUpdateValue("EXPTYPE", DataValues.newString(request.getExpType()));
        ub1.addUpdateValue("FINANALSOURCE", DataValues.newString(request.getFinAnalSource()));
        ub1.addUpdateValue("ISCASHSUBJECT", DataValues.newString(request.getIsCashSubject()));
        ub1.addUpdateValue("ISENABLEDPTMNG", DataValues.newString(request.getIsEnableDptMng()));
        ub1.addUpdateValue("ISENABLETRADOBJMNG", DataValues.newString(request.getIsEnableTradObjMng()));
        ub1.addUpdateValue("ISENABLEPRODCATMNG", DataValues.newString(request.getIsEnableProdCatMng()));
        ub1.addUpdateValue("ISENABLEMANMNG", DataValues.newString(request.getIsEnableManMng()));
        ub1.addUpdateValue("ISMULTICURMNG", DataValues.newString(request.getIsMultiCurMng()));
        ub1.addUpdateValue("ISSUBSYSSUBJECT", DataValues.newString(request.getIsSubsysSubject()));
        ub1.addUpdateValue("DRCASHCHGCODE", DataValues.newString(request.getDrCashChgCode()));
        ub1.addUpdateValue("CRCASHCHGCODE", DataValues.newString(request.getCrCashChgCode()));
        ub1.addUpdateValue("ISFREECHARS1", DataValues.newString(request.getIsFreeChars1()));
        ub1.addUpdateValue("FREECHARS1_TYPEID", DataValues.newString(request.getFreeChars1TypeID()));
        ub1.addUpdateValue("FREECHARS1_CTRLMODE", DataValues.newString(request.getFreeChars1CtrlMode()));
        ub1.addUpdateValue("ISFREECHARS2", DataValues.newString(request.getIsFreeChars2()));
        ub1.addUpdateValue("FREECHARS2_TYPEID", DataValues.newString(request.getFreeChars2TypeID()));
        ub1.addUpdateValue("FREECHARS2_CTRLMODE", DataValues.newString(request.getFreeChars2CtrlMode()));
        ub1.addUpdateValue("ISFREECHARS3", DataValues.newString(request.getIsFreeChars3()));
        ub1.addUpdateValue("FREECHARS3_TYPEID", DataValues.newString(request.getFreeChars3TypeID()));
        ub1.addUpdateValue("FREECHARS3_CTRLMODE", DataValues.newString(request.getFreeChars3CtrlMode()));
        ub1.addUpdateValue("MODIFYBY", DataValues.newString(req.getEmployeeNo()));
        ub1.addUpdateValue("MODIFY_DATE", DataValues.newString(modifyDate));
        ub1.addUpdateValue("MODIFY_TIME", DataValues.newString(modifyTime));
        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("COAREFID", new DataValue(request.getCoaRefID(), Types.VARCHAR));
        ub1.addCondition("SUBJECTID", new DataValue(request.getSubjectId(), Types.VARCHAR));
        ub1.addCondition("ACCOUNTID", new DataValue(request.getAccountId(), Types.VARCHAR));

        this.addProcessData(new DataProcessBean(ub1));


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_AccCOAUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_AccCOAUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_AccCOAUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_AccCOAUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");


        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_AccCOAUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_AccCOAUpdateReq>(){};
    }

    @Override
    protected DCP_AccCOAUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_AccCOAUpdateRes();
    }

}

