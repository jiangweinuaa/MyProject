package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ApSetupSubjectStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ApSetupSubjectStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_ApSetupSubjectStatusUpdate extends SPosAdvanceService<DCP_ApSetupSubjectStatusUpdateReq, DCP_ApSetupSubjectStatusUpdateRes> {

    @Override
    protected void processDUID(DCP_ApSetupSubjectStatusUpdateReq req, DCP_ApSetupSubjectStatusUpdateRes res) throws Exception {

        String eId = req.geteId();
        String opType = req.getRequest().getOpType();
        if("100".equals(opType)){
            UptBean ub1 = new UptBean("DCP_APSETUPSUBJECT");
            ub1.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("ACCOUNTID", new DataValue(req.getRequest().getAccountId(), Types.VARCHAR));
            ub1.addCondition("COAREFID", new DataValue(req.getRequest().getCoaRefID(), Types.VARCHAR));
            ub1.addCondition("SETUPTYPE", new DataValue(req.getRequest().getSetupType(), Types.VARCHAR));

            this.addProcessData(new DataProcessBean(ub1));


        }
        if("0".equals(opType)){
            UptBean ub1 = new UptBean("DCP_APSETUPSUBJECT");
            ub1.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("ACCOUNTID", new DataValue(req.getRequest().getAccountId(), Types.VARCHAR));
            ub1.addCondition("COAREFID", new DataValue(req.getRequest().getCoaRefID(), Types.VARCHAR));
            ub1.addCondition("SETUPTYPE", new DataValue(req.getRequest().getSetupType(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));


        }

        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ApSetupSubjectStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ApSetupSubjectStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ApSetupSubjectStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ApSetupSubjectStatusUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ApSetupSubjectStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_ApSetupSubjectStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_ApSetupSubjectStatusUpdateRes getResponseType() {
        return new DCP_ApSetupSubjectStatusUpdateRes();
    }
}

