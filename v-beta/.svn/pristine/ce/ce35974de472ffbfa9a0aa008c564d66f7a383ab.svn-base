package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_FeeSetupSubjectCreateReq;
import com.dsc.spos.json.cust.res.DCP_FeeSetupSubjectCreateRes;
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

public class DCP_FeeSetupSubjectCreate extends SPosAdvanceService<DCP_FeeSetupSubjectCreateReq, DCP_FeeSetupSubjectCreateRes> {
    @Override
    protected void processDUID(DCP_FeeSetupSubjectCreateReq req, DCP_FeeSetupSubjectCreateRes res) throws Exception {

        String querySql = " SELECT COAREFID FROM DCP_FEESETUPSUBJECT WHERE EID='" + req.geteId() + "'" +
                " AND ACCOUNTID='" + req.getRequest().getAccountId() + "'" +
                " AND COAREFID='" + req.getRequest().getCoaRefID() + "'";

        List<Map<String, Object>> qData = doQueryData(querySql, null);
        if (CollectionUtils.isNotEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, req.getRequest().getCoaRefID() + "已存在，不可保存!");
        }

        for (DCP_FeeSetupSubjectCreateReq.SetupList oneList : req.getRequest().getSetupList()) {

            ColumnDataValue dcp_feeSetupSubject = new ColumnDataValue();

            dcp_feeSetupSubject.add("EID", req.geteId());
            dcp_feeSetupSubject.add("ACCOUNTID", req.getRequest().getAccountId());
            dcp_feeSetupSubject.add("COAREFID", req.getRequest().getCoaRefID());

            dcp_feeSetupSubject.add("FEE", oneList.getFee());
            dcp_feeSetupSubject.add("FEENATURE", oneList.getFeeNature());
            dcp_feeSetupSubject.add("ACCSUBJECT", oneList.getAccSubject());
            dcp_feeSetupSubject.add("REVSUBJECT", oneList.getRevSubject());
            dcp_feeSetupSubject.add("ADVSUBJECT", oneList.getAdvSubject());

            if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
                dcp_feeSetupSubject.add("STATUS", req.getRequest().getStatus());
            } else {
                dcp_feeSetupSubject.add("STATUS", "-1");
            }

            dcp_feeSetupSubject.add("CREATEBY", req.getEmployeeNo());
            dcp_feeSetupSubject.add("CREATE_DATE", DateFormatUtils.getNowPlainDate());
            dcp_feeSetupSubject.add("CREATE_TIME", DateFormatUtils.getNowPlainTime());

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_FEESETUPSUBJECT", dcp_feeSetupSubject)));

        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_FeeSetupSubjectCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_FeeSetupSubjectCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_FeeSetupSubjectCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_FeeSetupSubjectCreateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_FeeSetupSubjectCreateReq> getRequestType() {
        return new TypeToken<DCP_FeeSetupSubjectCreateReq>() {
        };
    }

    @Override
    protected DCP_FeeSetupSubjectCreateRes getResponseType() {
        return new DCP_FeeSetupSubjectCreateRes();
    }
}
