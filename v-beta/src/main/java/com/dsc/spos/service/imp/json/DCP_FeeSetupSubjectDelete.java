package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_FeeSetupSubjectDeleteReq;
import com.dsc.spos.json.cust.res.DCP_FeeSetupSubjectDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_FeeSetupSubjectDelete extends SPosAdvanceService<DCP_FeeSetupSubjectDeleteReq, DCP_FeeSetupSubjectDeleteRes> {

    @Override
    protected void processDUID(DCP_FeeSetupSubjectDeleteReq req, DCP_FeeSetupSubjectDeleteRes res) throws Exception {


        String querySql = " SELECT STATUS FROM DCP_FEESETUPSUBJECT WHERE EID='" + req.geteId() + "'" +
                " AND ACCOUNTID='" + req.getRequest().getAccountId() + "'" +
                " AND COAREFID='" + req.getRequest().getCoaRefID() + "'";

        List<Map<String, Object>> qData = doQueryData(querySql, null);
        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, req.getRequest().getCoaRefID() + "不存在，不可删除!");
        } else {
            String status = qData.get(0).get("STATUS").toString();
            if (!"-1".equals(status)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, req.getRequest().getCoaRefID() + "当前状态不可删除!");
            }
        }

        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", req.geteId());
        condition.add("ACCOUNTID", req.getRequest().getAccountId());
        condition.add("COAREFID", req.getRequest().getCoaRefID());
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_FEESETUPSUBJECT", condition)));


        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_FeeSetupSubjectDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_FeeSetupSubjectDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_FeeSetupSubjectDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_FeeSetupSubjectDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_FeeSetupSubjectDeleteReq> getRequestType() {
        return new TypeToken<DCP_FeeSetupSubjectDeleteReq>() {
        };
    }

    @Override
    protected DCP_FeeSetupSubjectDeleteRes getResponseType() {
        return new DCP_FeeSetupSubjectDeleteRes();
    }
}
