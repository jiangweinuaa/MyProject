package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ReceivingDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ReceivingDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class DCP_ReceivingDelete extends SPosAdvanceService<DCP_ReceivingDeleteReq, DCP_ReceivingDeleteRes> {
    @Override
    protected void processDUID(DCP_ReceivingDeleteReq req, DCP_ReceivingDeleteRes res) throws Exception {

        //try {
            String query = " SELECT STATUS FROM DCP_RECEIVING a " +
                    " WHERE a.EID='%s' AND a.RECEIVINGNO='%s' ";
            query = String.format(query, req.geteId(), req.getRequest().getReceivingNo());
            List<Map<String, Object>> data = this.doQueryData(query, null);
            if (null != data && !data.isEmpty()) {
                Map<String, Object> bill = data.get(0);
                if (!StringUtils.equals("0",String.valueOf(bill.get("STATUS")))) {
                    res.setSuccess(false);
                    res.setServiceStatus("200");
                    res.setServiceDescription("单据状态非【0-新建】不可更改！");
                    return;
                }

                ColumnDataValue condition = new ColumnDataValue();

                condition.add("EID", DataValues.newString(req.geteId()));
                condition.add("RECEIVINGNO", DataValues.newString(req.getRequest().getReceivingNo()));

                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_RECEIVING", condition)));
                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_RECEIVING_DETAIL", condition)));

                //删除内部交易
                ColumnDataValue condition1 = new ColumnDataValue();
                condition1.add("EID", req.geteId());
                condition1.add("BILLNO", req.getRequest().getReceivingNo());
                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_DETAIL", condition1)));
                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_ROUTE", condition1)));


                this.doExecuteDataToDB();
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
            }

        //}catch (Exception e){

        //}

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReceivingDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReceivingDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReceivingDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReceivingDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ReceivingDeleteReq> getRequestType() {
        return new TypeToken<DCP_ReceivingDeleteReq>(){

        };
    }

    @Override
    protected DCP_ReceivingDeleteRes getResponseType() {
        return new DCP_ReceivingDeleteRes();
    }
}
