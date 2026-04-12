package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_TransApplyDeleteReq;
import com.dsc.spos.json.cust.res.DCP_TransApplyDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_TransApplyDelete extends SPosAdvanceService<DCP_TransApplyDeleteReq, DCP_TransApplyDeleteRes> {

    @Override
    protected void processDUID(DCP_TransApplyDeleteReq req, DCP_TransApplyDeleteRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String langType = req.getLangType();
        String organizationNO = req.getOrganizationNO();
        String billNo = req.getRequest().getBillNo();

        //1.单据状态非“0-新建”不可删除！
        StringBuffer sb=new StringBuffer("select * from DCP_TRANSAPPLY a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"'" +
                " and a.billno='"+billNo+"' and a.status='0'");
        List<Map<String, Object>> list = this.doQueryData( sb.toString(), null);
        if(list.size()<=0)
        {
            res.setSuccess(false);
            res.setServiceStatus("001");
            res.setServiceDescription("单据状态非[新建]不可删除！");
            return;
        }

        DelBean db1 = new DelBean("DCP_TRANSAPPLY");
        db1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        DelBean db2 = new DelBean("DCP_TRANSAPPLY_DETAIL");
        db2.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        DelBean db3 = new DelBean("DCP_TRANSAPPLY_SOURCE");
        db3.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db3.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db3));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_TransApplyDeleteReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_TransApplyDeleteReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_TransApplyDeleteReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_TransApplyDeleteReq req) throws Exception {
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
    protected TypeToken<DCP_TransApplyDeleteReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_TransApplyDeleteReq>(){};
    }

    @Override
    protected DCP_TransApplyDeleteRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_TransApplyDeleteRes();
    }

}

