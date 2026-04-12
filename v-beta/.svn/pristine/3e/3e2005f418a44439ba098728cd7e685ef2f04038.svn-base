package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataProcessReq;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_InterSettleDataProcess extends SPosAdvanceService<DCP_InterSettleDataProcessReq, DCP_InterSettleDataProcessRes> {

    private static final String OP_TYPE_CONFIRM = "confirm";
    private static final String OP_TYPE_UNCONFIRM = "unConfirm";

    @Override
    protected void processDUID(DCP_InterSettleDataProcessReq req, DCP_InterSettleDataProcessRes res) throws Exception {

        ColumnDataValue dcp_intersettle_detail = new ColumnDataValue();
        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", req.geteId());
        condition.add("BILLNO", req.getRequest().getBillNo());
        if (OP_TYPE_CONFIRM.equals(req.getRequest().getOprType())) {
            dcp_intersettle_detail.add("STATUS", DataValues.newString("2"));
        } else if (OP_TYPE_UNCONFIRM.equals(req.getRequest().getOprType())) {
            dcp_intersettle_detail.add("STATUS", DataValues.newString("0"));
        }

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_INTERSETTLE_DETAIL", condition, dcp_intersettle_detail)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_InterSettleDataProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_InterSettleDataProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_InterSettleDataProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_InterSettleDataProcessReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_InterSettleDataProcessReq> getRequestType() {
        return new TypeToken<DCP_InterSettleDataProcessReq>() {
        };
    }

    @Override
    protected DCP_InterSettleDataProcessRes getResponseType() {
        return new DCP_InterSettleDataProcessRes();
    }
}
