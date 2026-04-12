package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PurReceiveDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PurReceiveDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_PurReceiveDelete extends SPosAdvanceService<DCP_PurReceiveDeleteReq, DCP_PurReceiveDeleteRes> {
    @Override
    protected void processDUID(DCP_PurReceiveDeleteReq req, DCP_PurReceiveDeleteRes res) throws Exception {

        String querySql = String.format("SELECT * FROM DCP_PURRECEIVE WHERE EID='%s' and BILLNO='%s' ", req.geteId(), req.getRequest().getBillNo());
        List<Map<String, Object>> qData = doQueryData(querySql, null);

        if (null == qData || qData.isEmpty()) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, req.getRequest().getBillNo() + "已经删除!");
        }

        Map<String, Object> oneData = qData.get(0);

        if (!"0".equals(oneData.get("STATUS").toString())) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非[0-新建]不可删除！");
        }

        ColumnDataValue conditionReceive = new ColumnDataValue();
        conditionReceive.add("EID", DataValues.newString(req.geteId()));
        conditionReceive.add("RECEIVINGNO", DataValues.newString(oneData.get("RECEIVINGNO")));

        ColumnDataValue dcp_receiving = new ColumnDataValue();
        dcp_receiving.add("STATUS", DataValues.newInteger(6));

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_RECEIVING", conditionReceive, dcp_receiving)));
//            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_RECEIVING_DETAIL",conditionReceive,dcp_receiving)));

        ColumnDataValue condition = new ColumnDataValue();
        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("BILLNO", DataValues.newString(req.getRequest().getBillNo()));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_PURRECEIVE", condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_PURRECEIVE_DETAIL", condition)));


        //先删数据
        ColumnDataValue condition1 = new ColumnDataValue();

        condition1.add("EID", req.geteId());
        condition1.add("BILLNO", req.getRequest().getBillNo());

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_DETAIL", condition1)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_ROUTE", condition1)));


        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurReceiveDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurReceiveDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurReceiveDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_PurReceiveDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PurReceiveDeleteReq> getRequestType() {
        return new TypeToken<DCP_PurReceiveDeleteReq>() {

        };
    }

    @Override
    protected DCP_PurReceiveDeleteRes getResponseType() {
        return new DCP_PurReceiveDeleteRes();
    }
}
