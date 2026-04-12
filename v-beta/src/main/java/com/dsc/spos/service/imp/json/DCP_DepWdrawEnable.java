package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DepWdrawEnableReq;
import com.dsc.spos.json.cust.res.DCP_DepWdrawEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_DepWdrawEnable extends SPosAdvanceService<DCP_DepWdrawEnableReq, DCP_DepWdrawEnableRes> {
    @Override
    protected void processDUID(DCP_DepWdrawEnableReq req, DCP_DepWdrawEnableRes res) throws Exception {

        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT DISTINCT DEPWDRAWCODE,STATUS FROM DCP_DEPWDRAW WHERE EID='").append(req.geteId()).append("'");
        querySql.append(" AND (1=2 ");
        for (DCP_DepWdrawEnableReq.DepList oneDep : req.getRequest().getDepList()) {
            querySql.append(" or DEPWDRAWCODE='").append(oneDep.getDepWdrawCode()).append("'");
        }
        querySql.append(")");

        List<Map<String, Object>> qData = doQueryData(querySql.toString(), null);
        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前传入存提码不存在!不可编辑");
        }

        for (DCP_DepWdrawEnableReq.DepList oneDep : req.getRequest().getDepList()) {
            ColumnDataValue dcp_depwdraw = new ColumnDataValue();
            ColumnDataValue condition = new ColumnDataValue();

            condition.add("EID", req.geteId());
            condition.add("DEPWDRAWCODE", oneDep.getDepWdrawCode());

            dcp_depwdraw.add("STATUS", req.getRequest().getOprType());
            if ("-1".equals(req.getRequest().getOprType())) {
                dcp_depwdraw.add("CLOSEOPID", req.getEmployeeNo());
                dcp_depwdraw.add("CLOSETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
            } else if ("100".equals(req.getRequest().getOprType())) {
                dcp_depwdraw.add("CONFIRMOPID", req.getEmployeeNo());
                dcp_depwdraw.add("CONFIRMTIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
            } else if ("0".equals(req.getRequest().getOprType())) {
                dcp_depwdraw.add("CANCELOPID", req.getEmployeeNo());
                dcp_depwdraw.add("CANCELTIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            }
            dcp_depwdraw.add("LASTMODIOPID", req.getEmployeeNo());
            dcp_depwdraw.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));


            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_DEPWDRAW", condition, dcp_depwdraw)));

        }


        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DepWdrawEnableReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DepWdrawEnableReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DepWdrawEnableReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_DepWdrawEnableReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_DepWdrawEnableReq> getRequestType() {
        return new TypeToken<DCP_DepWdrawEnableReq>() {
        };
    }

    @Override
    protected DCP_DepWdrawEnableRes getResponseType() {
        return new DCP_DepWdrawEnableRes();
    }
}
