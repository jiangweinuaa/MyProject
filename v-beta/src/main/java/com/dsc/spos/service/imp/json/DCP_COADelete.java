package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_COADeleteReq;
import com.dsc.spos.json.cust.res.DCP_COADeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_COADelete extends SPosAdvanceService<DCP_COADeleteReq, DCP_COADeleteRes> {

    @Override
    protected void processDUID(DCP_COADeleteReq req, DCP_COADeleteRes res) throws Exception {

        String eId = req.geteId();

        if("ALLCOA".equals(req.getRequest().getAccountId())){
            //删掉全部
            DelBean db1 = new DelBean("DCP_COA");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("SUBJECTID", new DataValue(req.getRequest().getSubjectId(), Types.VARCHAR));
            db1.addCondition("COAREFID", new DataValue(req.getRequest().getCoaRefID(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));
        }else{
            DelBean db1 = new DelBean("DCP_COA");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("ACCOUNTID", new DataValue(req.getRequest().getAccountId(), Types.VARCHAR));
            db1.addCondition("SUBJECTID", new DataValue(req.getRequest().getSubjectId(), Types.VARCHAR));
            db1.addCondition("COAREFID", new DataValue(req.getRequest().getCoaRefID(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            UptBean ub1 = new UptBean("DCP_COA");
            ub1.addUpdateValue("ACCTYPE", DataValues.newString("2"));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("SUBJECTID", new DataValue(req.getRequest().getSubjectId(), Types.VARCHAR));
            ub1.addCondition("COAREFID", new DataValue(req.getRequest().getCoaRefID(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));
        }
        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_COADeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_COADeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_COADeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_COADeleteReq req) throws Exception {
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
    protected TypeToken<DCP_COADeleteReq> getRequestType() {
        return new TypeToken<DCP_COADeleteReq>() {
        };
    }

    @Override
    protected DCP_COADeleteRes getResponseType() {
        return new DCP_COADeleteRes();
    }
}

