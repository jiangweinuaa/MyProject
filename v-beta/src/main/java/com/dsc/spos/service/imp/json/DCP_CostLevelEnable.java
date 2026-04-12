package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CostLevelEnableReq;
import com.dsc.spos.json.cust.res.DCP_CostLevelEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_CostLevelEnable extends SPosAdvanceService<DCP_CostLevelEnableReq, DCP_CostLevelEnableRes> {

    @Override
    protected void processDUID(DCP_CostLevelEnableReq req, DCP_CostLevelEnableRes res) throws Exception {
        try {

            ColumnDataValue dcp_costLevel = new ColumnDataValue();
            ColumnDataValue condition = new ColumnDataValue();
            condition.add("EID", DataValues.newString(req.geteId()));
            condition.add("COSTGROUPINGID", DataValues.newString(req.getRequest().getCostGroupingId()));

            dcp_costLevel.add("STATUS", DataValues.newInteger("1".equals(req.getRequest().getOprType())?100:0));
            dcp_costLevel.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_costLevel.add("LASTMODIOPNAME", DataValues.newString(req.getEmployeeName()));
            dcp_costLevel.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_COSTLEVEL",condition, dcp_costLevel)));

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");

        } catch (Exception e) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CostLevelEnableReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CostLevelEnableReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CostLevelEnableReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CostLevelEnableReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostLevelEnableReq> getRequestType() {
        return new TypeToken<DCP_CostLevelEnableReq>(){

        };
    }

    @Override
    protected DCP_CostLevelEnableRes getResponseType() {
        return new DCP_CostLevelEnableRes();
    }
}
