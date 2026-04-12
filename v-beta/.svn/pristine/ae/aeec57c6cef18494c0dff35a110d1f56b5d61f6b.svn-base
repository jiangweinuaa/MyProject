package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_BankReceipetDeleteReq;
import com.dsc.spos.json.cust.res.DCP_BankReceipetDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_BankReceipetDelete extends SPosAdvanceService<DCP_BankReceipetDeleteReq, DCP_BankReceipetDeleteRes> {

    @Override
    protected void processDUID(DCP_BankReceipetDeleteReq req, DCP_BankReceipetDeleteRes res) throws Exception {
        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        if (CollectionUtils.isNotEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "未查询到数据!");
        }

        String status = qData.get(0).get("STATUS").toString();
        if (!Constant.STATUS_0.equals(status)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "数据状态不为新建，不可删除！");
        }

        ColumnDataValue delCondition = new ColumnDataValue();
        delCondition.add("EID", DataValues.newString(req.geteId()));
        delCondition.add("CMNO", DataValues.newString(req.getRequest().getCmNo()));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_BANKRECEIPT", delCondition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_BANKRECEIPTDETAIL", delCondition)));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BankReceipetDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BankReceipetDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BankReceipetDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_BankReceipetDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_BankReceipetDeleteReq> getRequestType() {
        return new TypeToken<DCP_BankReceipetDeleteReq>() {
        };
    }

    @Override
    protected DCP_BankReceipetDeleteRes getResponseType() {
        return new DCP_BankReceipetDeleteRes();
    }

    @Override
    protected String getQuerySql(DCP_BankReceipetDeleteReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT a.STATUS,b.PENDOFFSETNO  ")
                .append(" FROM DCP_BANKRECEIPT a ")
                .append(" LEFT JOIN DCP_BANKRECEIPTDETAIL b on a.eid=b.eid and a.CMNO=b.CMNO ")
        ;

        querySql.append("WHERE a.EID='").append(req.geteId()).append("'");

        if (Check.isNotEmpty(req.getRequest().getCmNo())) {
            querySql.append(" AND a.CMNO='").append(req.getRequest().getCmNo()).append("'");
        }

        return querySql.toString();
    }
}
