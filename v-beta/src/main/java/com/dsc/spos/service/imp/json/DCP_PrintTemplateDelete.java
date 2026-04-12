package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PrintTemplateDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PrintTemplateDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_PrintTemplateDelete extends SPosAdvanceService<DCP_PrintTemplateDeleteReq, DCP_PrintTemplateDeleteRes> {

    @Override
    protected void processDUID(DCP_PrintTemplateDeleteReq req, DCP_PrintTemplateDeleteRes res) throws Exception {

        String eId = req.geteId();
        String modularNo = req.getRequest().getModularNo();

        DelBean db1 = new DelBean("DCP_MODULAR_PRINT");
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db1.addCondition("MODULARNO", new DataValue(modularNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        DelBean db2 = new DelBean("DCP_MODULAR_PRINT_USER");
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("MODULARNO", new DataValue(modularNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        DelBean db3 = new DelBean("DCP_MODULAR_PRINT_ORG");
        db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db3.addCondition("MODULARNO", new DataValue(modularNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db3));

        DelBean db4 = new DelBean("DCP_MODULAR_PRINT_CUSTOMER");
        db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db4.addCondition("MODULARNO", new DataValue(modularNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db4));

        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PrintTemplateDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PrintTemplateDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PrintTemplateDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PrintTemplateDeleteReq req) throws Exception {
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
    protected TypeToken<DCP_PrintTemplateDeleteReq> getRequestType() {
        return new TypeToken<DCP_PrintTemplateDeleteReq>() {
        };
    }

    @Override
    protected DCP_PrintTemplateDeleteRes getResponseType() {
        return new DCP_PrintTemplateDeleteRes();
    }
}

