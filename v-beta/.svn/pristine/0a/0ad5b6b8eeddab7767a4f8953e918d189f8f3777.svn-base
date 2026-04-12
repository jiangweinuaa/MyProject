package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockAdjustDeleteReq;
import com.dsc.spos.json.cust.res.DCP_StockAdjustDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_StockAdjustDelete extends SPosAdvanceService<DCP_StockAdjustDeleteReq, DCP_StockAdjustDeleteRes> {

    @Override
    protected void processDUID(DCP_StockAdjustDeleteReq req, DCP_StockAdjustDeleteRes res) throws Exception {

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String adjustNo = req.getRequest().getAdjustNo();

        String sql="select * from dcp_adjust a where a.eid='"+eId+"' " +
                " and a.organizationno='"+organizationNO+"' and a.status='0'";
        List<Map<String, Object>> list = this.doQueryData(sql, null);
        if(list.size()<=0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无法删除！".toString());
        }

        //TEMPLATEID
        DelBean db1 = new DelBean("DCP_ADJUST");
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        db1.addCondition("ADJUSTNO", new DataValue(adjustNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        DelBean db2 = new DelBean("DCP_ADJUST_DETAIL");
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        db2.addCondition("ADJUSTNO", new DataValue(adjustNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));


        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockAdjustDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockAdjustDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockAdjustDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_StockAdjustDeleteReq req) throws Exception {
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
    protected TypeToken<DCP_StockAdjustDeleteReq> getRequestType() {
        return new TypeToken<DCP_StockAdjustDeleteReq>() {
        };
    }

    @Override
    protected DCP_StockAdjustDeleteRes getResponseType() {
        return new DCP_StockAdjustDeleteRes();
    }
}

