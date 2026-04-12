package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ROrderDeleteReq;
import com.dsc.spos.json.cust.req.DCP_ReturnApplyDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ROrderDeleteRes;
import com.dsc.spos.json.cust.res.DCP_ReturnApplyDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_ReturnApplyDelete extends SPosAdvanceService<DCP_ReturnApplyDeleteReq, DCP_ReturnApplyDeleteRes> {

    @Override
    protected boolean isVerifyFail(DCP_ReturnApplyDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }


    /**
     * 查询多语言信息
     */
    @Override
    protected String getQuerySql(DCP_ReturnApplyDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    protected void processDUID(DCP_ReturnApplyDeleteReq req, DCP_ReturnApplyDeleteRes res) throws Exception {
        // TODO Auto-generated method stub

        DelBean db1 = new DelBean("DCP_RETURNAPPLY");
        db1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
        db1.addCondition("BILLNO", new DataValue(req.getRequest().getBillNo(), Types.VARCHAR));
        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        DelBean db2 = new DelBean("DCP_RETURNAPPLY_DETAIL");
        db2.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
        db2.addCondition("BILLNO", new DataValue(req.getRequest().getBillNo(), Types.VARCHAR));
        db2.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        DelBean db3 = new DelBean("DCP_RETURNAPPLY_IMAGE");
        db3.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
        db3.addCondition("BILLNO", new DataValue(req.getRequest().getBillNo(), Types.VARCHAR));
        db3.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db3));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReturnApplyDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReturnApplyDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReturnApplyDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    protected TypeToken<DCP_ReturnApplyDeleteReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_ReturnApplyDeleteReq>(){};
    }
    @Override
    protected DCP_ReturnApplyDeleteRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_ReturnApplyDeleteRes();
    }

}
