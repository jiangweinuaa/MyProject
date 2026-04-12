package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProductGroupUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ProductGroupUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_ProductGroupUpdate extends SPosAdvanceService<DCP_ProductGroupUpdateReq, DCP_ProductGroupUpdateRes> {

    @Override
    protected void processDUID(DCP_ProductGroupUpdateReq req, DCP_ProductGroupUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        DCP_ProductGroupUpdateReq.LevelRequest request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());

        UptBean ub1 = new UptBean("MES_PRODUCT_GROUP");
        ub1.addUpdateValue("PGROUPNAME", new DataValue(request.getPGroupName(), Types.VARCHAR));
        ub1.addUpdateValue("DEPARTID", new DataValue(request.getDepartId(), Types.VARCHAR));
        ub1.addUpdateValue("MEMO", new DataValue(request.getMemo(), Types.VARCHAR));
        ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
        ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getEmployeeName(), Types.VARCHAR));
        ub1.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));

        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("PGROUPNO", new DataValue(req.getRequest().getPGroupNo(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProductGroupUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProductGroupUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProductGroupUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ProductGroupUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_ProductGroupUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_ProductGroupUpdateReq>(){};
    }

    @Override
    protected DCP_ProductGroupUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_ProductGroupUpdateRes();
    }

}


