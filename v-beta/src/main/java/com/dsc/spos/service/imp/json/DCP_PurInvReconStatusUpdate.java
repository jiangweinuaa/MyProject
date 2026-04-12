package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PurInvReconStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PurInvReconStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_PurInvReconStatusUpdate extends SPosAdvanceService<DCP_PurInvReconStatusUpdateReq, DCP_PurInvReconStatusUpdateRes> {

    private static final String OP_TYPE_CONFIRM = "confirm";
    private static final String OP_TYPE_UNCONFIRM = "unConfirm";
    private static final String OP_TYPE_CANCEL = "cancel";

    @Override
    protected void processDUID(DCP_PurInvReconStatusUpdateReq req, DCP_PurInvReconStatusUpdateRes res) throws Exception {

        String opType = req.getRequest().getOpType();

        ColumnDataValue dcp_purInv = new ColumnDataValue();
        ColumnDataValue dcp_purInvDetail = new ColumnDataValue();
        ColumnDataValue dcp_purInvRecon = new ColumnDataValue();
        if (OP_TYPE_CONFIRM.equals(opType)) {
            dcp_purInv.add("STATUS", "Y");
            dcp_purInv.add("CONFIRMBY", req.getEmployeeNo());
            dcp_purInv.add("CONFIRM_DATE", DateFormatUtils.getNowPlainDate());
            dcp_purInv.add("CONFIRM_TIME", DateFormatUtils.getNowPlainTime());

            dcp_purInvDetail.add("STATUS", "Y");
            dcp_purInvDetail.add("CONFIRMBY", req.getEmployeeNo());
            dcp_purInvDetail.add("CONFIRM_DATE", DateFormatUtils.getNowPlainDate());
            dcp_purInvDetail.add("CONFIRM_TIME", DateFormatUtils.getNowPlainTime());

            dcp_purInvRecon.add("STATUS", "Y");
            dcp_purInvRecon.add("CONFIRMBY", req.getEmployeeNo());
            dcp_purInvRecon.add("CONFIRM_DATE", DateFormatUtils.getNowPlainDate());
            dcp_purInvRecon.add("CONFIRM_TIME", DateFormatUtils.getNowPlainTime());


        } else if (OP_TYPE_UNCONFIRM.equals(opType)) {

            dcp_purInv.add("STATUS", "N");
            dcp_purInv.add("CONFIRMBY", "");
            dcp_purInv.add("CONFIRM_DATE", "");
            dcp_purInv.add("CONFIRM_TIME", "");

            dcp_purInvDetail.add("STATUS", "N");
            dcp_purInvDetail.add("CONFIRMBY", "");
            dcp_purInvDetail.add("CONFIRM_DATE", "");
            dcp_purInvDetail.add("CONFIRM_TIME", "");

            dcp_purInvRecon.add("STATUS", "N");
            dcp_purInvRecon.add("CONFIRMBY", "");
            dcp_purInvRecon.add("CONFIRM_DATE", "");
            dcp_purInvRecon.add("CONFIRM_TIME", "");


        } else if (OP_TYPE_CANCEL.equals(opType)) {

            dcp_purInv.add("STATUS", "X");
            dcp_purInv.add("CANCELBY", req.getEmployeeNo());
            dcp_purInv.add("CANCEL_DATE", DateFormatUtils.getNowPlainDate());
            dcp_purInv.add("CANCEL_TIME", DateFormatUtils.getNowPlainTime());

            dcp_purInvDetail.add("STATUS", "X");
            dcp_purInvDetail.add("CANCELBY", req.getEmployeeNo());
            dcp_purInvDetail.add("CANCEL_DATE", DateFormatUtils.getNowPlainDate());
            dcp_purInvDetail.add("CANCEL_TIME", DateFormatUtils.getNowPlainTime());

            dcp_purInvRecon.add("STATUS", "X");
            dcp_purInvRecon.add("CANCELBY", req.getEmployeeNo());
            dcp_purInvRecon.add("CANCEL_DATE", DateFormatUtils.getNowPlainDate());
            dcp_purInvRecon.add("CANCEL_TIME", DateFormatUtils.getNowPlainTime());

        }

        ColumnDataValue condition = new ColumnDataValue();
        condition.add("EID", req.geteId());
        condition.add("PURINVNO", req.getRequest().getPurInvNo());

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PURINV", condition, dcp_purInv)));
        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PURINVDETAIL", condition, dcp_purInvDetail)));
        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PURINVRECON", condition, dcp_purInvRecon)));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurInvReconStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurInvReconStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurInvReconStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_PurInvReconStatusUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PurInvReconStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_PurInvReconStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_PurInvReconStatusUpdateRes getResponseType() {
        return new DCP_PurInvReconStatusUpdateRes();
    }
}
