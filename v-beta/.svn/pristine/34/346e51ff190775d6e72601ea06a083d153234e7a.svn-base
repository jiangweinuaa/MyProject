package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SettleDataUpdateReq;
import com.dsc.spos.json.cust.res.DCP_SettleDataUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class DCP_SettleDataUpdate extends SPosAdvanceService<DCP_SettleDataUpdateReq, DCP_SettleDataUpdateRes> {
    @Override
    protected void processDUID(DCP_SettleDataUpdateReq req, DCP_SettleDataUpdateRes res) throws Exception {

        if (CollectionUtils.isNotEmpty(req.getRequest().getAdjList())) {
            for (DCP_SettleDataUpdateReq.AdjList oneAdj : req.getRequest().getAdjList()) {

                ColumnDataValue condition = new ColumnDataValue();

                condition.add("EID", DataValues.newString(req.geteId()));
                condition.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrganizationNo()));
                condition.add("BILLNO", DataValues.newString(oneAdj.getBillNo()));
                condition.add("ITEM", DataValues.newString(oneAdj.getItem()));

                ColumnDataValue dcp_settledata = new ColumnDataValue();

                dcp_settledata.add("STATUS",DataValues.newString(oneAdj.getStatus()));
                dcp_settledata.add("UNSETTLEAMT",DataValues.newString(oneAdj.getUnSettleAmt()));
                dcp_settledata.add("SETTLEAMT",DataValues.newString(oneAdj.getSettleAmt()));
                dcp_settledata.add("UNPAIDAMT",DataValues.newString(oneAdj.getUnPaidAmt()));
                dcp_settledata.add("PAIDAMT",DataValues.newString(oneAdj.getPaidAmt()));
                dcp_settledata.add("APQTY",DataValues.newString(oneAdj.getApQty()));
                dcp_settledata.add("APAMT",DataValues.newString(oneAdj.getApAmt()));
                dcp_settledata.add("UNAPAMT",DataValues.newString(oneAdj.getUnApAmt()));

                this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_SETTLEDATA",condition,dcp_settledata)));
            }

        }


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_SettleDataUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SettleDataUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SettleDataUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_SettleDataUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_SettleDataUpdateReq> getRequestType() {
        return new TypeToken<DCP_SettleDataUpdateReq>() {
        };
    }

    @Override
    protected DCP_SettleDataUpdateRes getResponseType() {
        return new DCP_SettleDataUpdateRes();
    }
}
