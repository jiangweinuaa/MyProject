package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_Acount_SetingEnableReq;
import com.dsc.spos.json.cust.res.DCP_Acount_SetingEnableRes;
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

public class DCP_Acount_SetingEnable extends SPosAdvanceService<DCP_Acount_SetingEnableReq, DCP_Acount_SetingEnableRes> {
    @Override
    protected void processDUID(DCP_Acount_SetingEnableReq req, DCP_Acount_SetingEnableRes res) throws Exception {

        String querySql = " SELECT * FROM DCP_ACOUNT_SETTING " +
                " WHERE EID='" + req.geteId() + "' AND ACCOUNTID='" + req.getRequest().getAccountID() + "'";

        List<Map<String, Object>> qData = doQueryData(querySql, null);
        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无符合条件数据！");
        }

        String accType = qData.get(0).get("ACCTTYPE").toString();

        if ("1".equals(accType) && "1".equals(req.getRequest().getOprType())) {
          String corp = qData.get(0).get("CORP").toString();
            querySql = " SELECT * FROM DCP_ACOUNT_SETTING " +
                    " WHERE EID='" + req.geteId() + "' AND STATUS='100' and CORP='" + corp + "' AND ACCTTYPE='1' AND ACCOUNTID!='" + req.getRequest().getAccountID() + "'";
            List<Map<String, Object>> exists = doQueryData(querySql, null);

            if (CollectionUtils.isNotEmpty(exists)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, corp + "已存在其它主帐套,不可启用！");
            }
        }

        ColumnDataValue condition = new ColumnDataValue();
        ColumnDataValue dcp_account_setting = new ColumnDataValue();

        String lastModiTime = DateFormatUtils.getNowDateTime();

        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));

        int status = "1".equals(req.getRequest().getOprType()) ? 100 : 0;

        dcp_account_setting.add("STATUS", DataValues.newInteger(status));

        dcp_account_setting.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
        dcp_account_setting.add("LASTMODITIME", DataValues.newDate(lastModiTime));

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_ACOUNT_SETTING", condition, dcp_account_setting)));
        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_Acount_SetingEnableReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_Acount_SetingEnableReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_Acount_SetingEnableReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_Acount_SetingEnableReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_Acount_SetingEnableReq> getRequestType() {
        return new TypeToken<DCP_Acount_SetingEnableReq>() {
        };
    }

    @Override
    protected DCP_Acount_SetingEnableRes getResponseType() {
        return new DCP_Acount_SetingEnableRes();
    }


}
