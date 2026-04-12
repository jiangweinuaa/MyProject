package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProdTemplateDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ProdTemplateDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_ProdTemplateDelete extends SPosAdvanceService<DCP_ProdTemplateDeleteReq, DCP_ProdTemplateDeleteRes> {

    @Override
    protected void processDUID(DCP_ProdTemplateDeleteReq req, DCP_ProdTemplateDeleteRes res) throws Exception {

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String templateId = req.getRequest().getTemplateId();

        //TEMPLATEID
        DelBean db1 = new DelBean("DCP_PRODTEMPLATE");
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        DelBean db2 = new DelBean("DCP_PRODTEMPLATE_GOODS");
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        DelBean db3 = new DelBean("DCP_PRODTEMPLATE_RANGE");
        db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db3.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db3));

        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProdTemplateDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProdTemplateDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProdTemplateDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ProdTemplateDeleteReq req) throws Exception {
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
    protected TypeToken<DCP_ProdTemplateDeleteReq> getRequestType() {
        return new TypeToken<DCP_ProdTemplateDeleteReq>() {
        };
    }

    @Override
    protected DCP_ProdTemplateDeleteRes getResponseType() {
        return new DCP_ProdTemplateDeleteRes();
    }
}

