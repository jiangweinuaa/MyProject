package com.dsc.spos.service.imp.json;


import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_FeeEnableReq;
import com.dsc.spos.json.cust.res.DCP_FeeEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_FeeEnable extends SPosAdvanceService<DCP_FeeEnableReq, DCP_FeeEnableRes> {

    private static final String ORP_TYPE_ENABLE = "1";
    private static final String ORP_TYPE_DISABLE = "2";


    @Override
    protected void processDUID(DCP_FeeEnableReq req, DCP_FeeEnableRes res) throws Exception {
        try {
            ColumnDataValue dcp_fee = new ColumnDataValue();

            int status = ORP_TYPE_ENABLE.equals(req.getRequest().getOprType()) ? 100 : 0;

            dcp_fee.add("STATUS", DataValues.newInteger(status));
            dcp_fee.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_fee.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDate()));

            //condition
            ColumnDataValue condition = new ColumnDataValue();
            condition.add("EID", DataValues.newString(req.geteId()));
            condition.add("FEE", DataValues.newString(req.getRequest().getFee()));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_FEE", condition, dcp_fee)));

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_FeeEnableReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_FeeEnableReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_FeeEnableReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_FeeEnableReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_FeeEnableReq> getRequestType() {
        return null;
    }

    @Override
    protected DCP_FeeEnableRes getResponseType() {
        return null;
    }
}
