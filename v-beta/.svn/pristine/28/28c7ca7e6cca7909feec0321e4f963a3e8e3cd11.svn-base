package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProductGroupStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ProductGroupStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_ProductGroupStatusUpdate  extends SPosAdvanceService<DCP_ProductGroupStatusUpdateReq, DCP_ProductGroupStatusUpdateRes> {

    @Override
    protected void processDUID(DCP_ProductGroupStatusUpdateReq req, DCP_ProductGroupStatusUpdateRes res) throws Exception {

        String eId = req.geteId();

        List<DCP_ProductGroupStatusUpdateReq.Datas> datas = req.getRequest().getDatas();
        for (DCP_ProductGroupStatusUpdateReq.Datas data : datas){
            String pGroupNo = data.getPGroupNo();
            String status = data.getStatus();
            UptBean ub1 = new UptBean("MES_PRODUCT_GROUP");
            ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("PGROUPNO", new DataValue(pGroupNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));
        }


        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProductGroupStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProductGroupStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProductGroupStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ProductGroupStatusUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_ProductGroupStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_ProductGroupStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_ProductGroupStatusUpdateRes getResponseType() {
        return new DCP_ProductGroupStatusUpdateRes();
    }
}

