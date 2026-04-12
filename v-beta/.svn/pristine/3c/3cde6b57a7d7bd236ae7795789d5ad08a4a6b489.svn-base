package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_InterSettSettingDeleteReq;
import com.dsc.spos.json.cust.res.DCP_InterSettSettingDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_InterSettSettingDelete extends SPosAdvanceService<DCP_InterSettSettingDeleteReq, DCP_InterSettSettingDeleteRes> {

    @Override
    protected void processDUID(DCP_InterSettSettingDeleteReq req, DCP_InterSettSettingDeleteRes res) throws Exception {

        String eId = req.geteId();
        String processNo = req.getRequest().getProcessNo();

        //TEMPLATEID
        DelBean db1 = new DelBean("DCP_INTERSETTSETTING");
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db1.addCondition("PROCESSNO", new DataValue(processNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        DelBean db2 = new DelBean("DCP_INTERSETTSETDETAIL");
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("PROCESSNO", new DataValue(processNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        DelBean db3 = new DelBean("DCP_INTERSETTSETTING_V");
        db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db3.addCondition("PROCESSNO", new DataValue(processNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db3));

        DelBean db4 = new DelBean("DCP_INTERSETTSETDETAIL_V");
        db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db4.addCondition("PROCESSNO", new DataValue(processNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db4));



        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_InterSettSettingDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_InterSettSettingDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_InterSettSettingDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_InterSettSettingDeleteReq req) throws Exception {
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
    protected TypeToken<DCP_InterSettSettingDeleteReq> getRequestType() {
        return new TypeToken<DCP_InterSettSettingDeleteReq>() {
        };
    }

    @Override
    protected DCP_InterSettSettingDeleteRes getResponseType() {
        return new DCP_InterSettSettingDeleteRes();
    }
}

