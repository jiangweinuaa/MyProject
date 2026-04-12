package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_InterSettlementUpdateReq;
import com.dsc.spos.json.cust.res.DCP_InterSettlementUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_InterSettlementUpdate extends SPosAdvanceService<DCP_InterSettlementUpdateReq,DCP_InterSettlementUpdateRes> {
    @Override
    protected void processDUID(DCP_InterSettlementUpdateReq req, DCP_InterSettlementUpdateRes res) throws Exception {

        for (DCP_InterSettlementUpdateReq.InterList oneInter:req.getRequest().getInterList()){

            ColumnDataValue condition = new ColumnDataValue();
            ColumnDataValue dcp_intersettlement = new ColumnDataValue();

            condition.add("BILLNO", DataValues.newString(oneInter.getBillNo()));
            condition.add("ITEM", DataValues.newString(oneInter.getItem()));
            condition.add("YEAR", DataValues.newString(req.getRequest().getYear()));
            condition.add("MONTH", DataValues.newString(req.getRequest().getMonth()));

            dcp_intersettlement.add("STATUS",DataValues.newString(oneInter.getStatus()));
            dcp_intersettlement.add("UNSETTAMT",DataValues.newString(oneInter.getUnsettAmt()));
            dcp_intersettlement.add("SETTLEDAMT",DataValues.newString(oneInter.getSettledAmt()));
            dcp_intersettlement.add("UNPOSTEDAMT",DataValues.newString(oneInter.getUnpostedAmt()));
            dcp_intersettlement.add("POSTEDAMT",DataValues.newString(oneInter.getPostedAmt()));
            dcp_intersettlement.add("INVOICEQTY",DataValues.newString(oneInter.getInvoiceQty()));
            dcp_intersettlement.add("INVOICEAMT",DataValues.newString(oneInter.getInvoiceAmt()));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_INTERSETTLEMENT",condition,dcp_intersettlement)));
        }
        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_InterSettlementUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_InterSettlementUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_InterSettlementUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_InterSettlementUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_InterSettlementUpdateReq> getRequestType() {
        return new TypeToken<DCP_InterSettlementUpdateReq>(){};
    }

    @Override
    protected DCP_InterSettlementUpdateRes getResponseType() {
        return new DCP_InterSettlementUpdateRes();
    }
}
