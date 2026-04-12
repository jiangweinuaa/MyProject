package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_InterSettBillDeleteReq;
import com.dsc.spos.json.cust.res.DCP_InterSettBillDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_InterSettBillDelete extends SPosAdvanceService<DCP_InterSettBillDeleteReq, DCP_InterSettBillDeleteRes> {
    @Override
    protected void processDUID(DCP_InterSettBillDeleteReq req, DCP_InterSettBillDeleteRes res) throws Exception {
        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);

        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无符合条件数据！");
        }

        String mStatus = qData.get(0).get("MSTATUS").toString();

        if ("1".equals(mStatus)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "该单据已审核！请取消审核后再试！");
        }

        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", req.geteId());
        condition.add("BILLNO", req.getRequest().getBillNo());

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTBILL", condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTBILLDETAIL", condition)));

        for (Map<String, Object> oneDetail : qData) {

            ColumnDataValue ofCond = new ColumnDataValue();
            ColumnDataValue ofValue = new ColumnDataValue();

            ofCond.add("BILLNO", oneDetail.get("SOURCENO").toString());
            ofCond.add("ITEM", oneDetail.get("SOURCENOSEQ").toString());

            ofValue.add("STATUS", "0");

            ofValue.add("MODIFYBY", req.getEmployeeNo());
            ofValue.add("MODIFY_DATE", DateFormatUtils.getNowPlainDate());
            ofValue.add("MODIFY_TIME", DateFormatUtils.getNowPlainTime());

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_INTERSETTLEMENT", ofCond, ofValue)));
        }
        
        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_InterSettBillDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_InterSettBillDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_InterSettBillDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_InterSettBillDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_InterSettBillDeleteReq> getRequestType() {
        return new TypeToken<DCP_InterSettBillDeleteReq>() {
        };
    }

    @Override
    protected DCP_InterSettBillDeleteRes getResponseType() {
        return new DCP_InterSettBillDeleteRes();
    }

    @Override
    protected String getQuerySql(DCP_InterSettBillDeleteReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT a.STATUS MSTATUS, b.* FROM DCP_INTERSETTBILL a ")
                .append(" INNER JOIN DCP_INTERSETTBILLDETAIL b on a.eid=b.eid and a.BILLNO=b.BILLNO ")
        ;

        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            querySql.append(" AND q.BILLNO='").append(req.getRequest().getBillNo()).append("'");
        }

        return querySql.toString();
    }
}
