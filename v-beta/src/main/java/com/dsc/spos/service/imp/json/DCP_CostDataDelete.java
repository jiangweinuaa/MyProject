package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CostDataDeleteReq;
import com.dsc.spos.json.cust.res.DCP_CostDataDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_CostDataDelete extends SPosAdvanceService<DCP_CostDataDeleteReq, DCP_CostDataDeleteRes> {
    @Override
    protected void processDUID(DCP_CostDataDeleteReq req, DCP_CostDataDeleteRes res) throws Exception {


        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);

        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无符合条件数据!无需删除");
        }

        String status = qData.get(0).get("STATUS").toString();

        if (!"0".equals(status)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前单据状态不为新增,不可删除！");
        }


        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
        condition.add("COSTNO", DataValues.newString(req.getRequest().getCostNo()));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_COSTDATA", condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_COSTDETAILIN", condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_COSTDETAILOUT", condition)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }

    @Override
    protected String getQuerySql(DCP_CostDataDeleteReq req) throws Exception {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT a.* FROM DCP_COSTDATA a ")
        ;

        sql.append("WHERE a.EID = '").append(req.geteId()).append("'");

        if (Check.isNotEmpty(req.getRequest().getAccountID())) {
            sql.append(" AND a.ACCOUNTID = '").append(req.getRequest().getAccountID()).append("'");
        }

        if (Check.isNotEmpty(req.getRequest().getCostNo())) {
            sql.append(" AND a.COSTNO = '").append(req.getRequest().getCostNo()).append("'");
        }


        return sql.toString();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CostDataDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CostDataDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CostDataDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CostDataDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostDataDeleteReq> getRequestType() {
        return new TypeToken<DCP_CostDataDeleteReq>() {
        };
    }

    @Override
    protected DCP_CostDataDeleteRes getResponseType() {
        return new DCP_CostDataDeleteRes();
    }
}
