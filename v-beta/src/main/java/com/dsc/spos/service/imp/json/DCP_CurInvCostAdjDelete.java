package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CurInvCostAdjDeleteReq;
import com.dsc.spos.json.cust.res.DCP_CurInvCostAdjDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_CurInvCostAdjDelete extends SPosAdvanceService<DCP_CurInvCostAdjDeleteReq, DCP_CurInvCostAdjDeleteRes> {
    @Override
    protected void processDUID(DCP_CurInvCostAdjDeleteReq req, DCP_CurInvCostAdjDeleteRes res) throws Exception {

        String dataSouse = req.getRequest().getDataSource();

        if (!"1".equals(dataSouse) || CollectionUtils.isEmpty(req.getRequest().getAdjList())) {
            ColumnDataValue condition = new ColumnDataValue();
            ColumnDataValue detailCondition = new ColumnDataValue();
            condition.add("EID", DataValues.newString(req.geteId()));
            condition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
            condition.add("YEAR", DataValues.newString(req.getRequest().getYear()));
            condition.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));
            condition.add("COST_CALCULATION", DataValues.newString(req.getRequest().getCost_Calculation()));
            condition.add("REFERENCENO", DataValues.newString(req.getRequest().getReferenceNo()));

            detailCondition.add("EID", DataValues.newString(req.geteId()));
            detailCondition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
            detailCondition.add("YEAR", DataValues.newString(req.getRequest().getYear()));
            detailCondition.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));
            detailCondition.add("REFERENCENO", DataValues.newString(req.getRequest().getReferenceNo()));

            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_CURINVCOSTADJ", condition)));
            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_CURINVCOSTDETAILADJ", detailCondition)));
        } else {

            String querySql = " SELECT * FROM DCP_CURINVCOSTDETAILADJ " +
                    "  WHERE EID='" + req.geteId() + "' AND ACCOUNTID='" + req.getRequest().getAccountID() + "'" +
                    " AND YEAR='" + req.getRequest().getYear() + "'" +
                    " AND PERIOD='" + Integer.parseInt(req.getRequest().getPeriod()) + "'" +
                    " AND REFERENCENO='" + req.getRequest().getReferenceNo() + "'";

            List<Map<String, Object>> qData = doQueryData(querySql, null);
            if (CollectionUtils.isNotEmpty(qData)) {
                for (DCP_CurInvCostAdjDeleteReq.InvList invList : req.getRequest().getAdjList()) {
                    ColumnDataValue condition = new ColumnDataValue();
                    condition.add("EID", DataValues.newString(req.geteId()));
                    condition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
                    condition.add("YEAR", DataValues.newString(req.getRequest().getYear()));
                    condition.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));
                    condition.add("REFERENCENO", DataValues.newString(req.getRequest().getReferenceNo()));
                    condition.add("ITEM", DataValues.newString(invList.getItem()));

                    this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_CURINVCOSTDETAILADJ", condition)));
                }

                if (qData.size() <= req.getRequest().getAdjList().size()) {
                    ColumnDataValue condition = new ColumnDataValue();

                    condition.add("EID", DataValues.newString(req.geteId()));
                    condition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
                    condition.add("YEAR", DataValues.newString(req.getRequest().getYear()));
                    condition.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));
                    condition.add("COST_CALCULATION", DataValues.newString(req.getRequest().getCost_Calculation()));
                    condition.add("REFERENCENO", DataValues.newString(req.getRequest().getReferenceNo()));

                    this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_CURINVCOSTADJ", condition)));
                }

            }
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CurInvCostAdjDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CurInvCostAdjDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CurInvCostAdjDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CurInvCostAdjDeleteReq req) throws Exception {

        return false;
    }

    @Override
    protected TypeToken<DCP_CurInvCostAdjDeleteReq> getRequestType() {
        return new TypeToken<DCP_CurInvCostAdjDeleteReq>() {
        };
    }

    @Override
    protected DCP_CurInvCostAdjDeleteRes getResponseType() {
        return new DCP_CurInvCostAdjDeleteRes();
    }
}
