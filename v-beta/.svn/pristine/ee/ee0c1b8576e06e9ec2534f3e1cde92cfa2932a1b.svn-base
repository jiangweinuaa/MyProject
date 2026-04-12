package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ARInvDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ARInvDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_ARInvDelete extends SPosAdvanceService<DCP_ARInvDeleteReq, DCP_ARInvDeleteRes> {
    @Override
    protected void processDUID(DCP_ARInvDeleteReq req, DCP_ARInvDeleteRes res) throws Exception {

        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);

        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", req.geteId());
        condition.add("ARNO", req.getRequest().getArNo());
        condition.add("ACCOUNTID", req.getRequest().getAccountId());
        condition.add("ITEM", req.getRequest().getItem());

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_SALESINV",condition)));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ARInvDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ARInvDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ARInvDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ARInvDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ARInvDeleteReq> getRequestType() {
        return new TypeToken<DCP_ARInvDeleteReq>() {
        };
    }

    @Override
    protected DCP_ARInvDeleteRes getResponseType() {
        return new DCP_ARInvDeleteRes();
    }

    @Override
    protected String getQuerySql(DCP_ARInvDeleteReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT STATUS FROM DCP_SALESINV a ");
        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getArNo())) {
            querySql.append(" AND a.ARNO='").append(req.getRequest().getArNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getAccountId())) {
            querySql.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountId()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getItem())) {
            querySql.append(" AND a.ITEM='").append(req.getRequest().getItem()).append("'");
        }

        return querySql.toString();
    }
}
