package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_EmployeeEnableReq;
import com.dsc.spos.json.cust.res.DCP_EmployeeEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.util.List;

public class DCP_EmployeeEnable extends SPosAdvanceService<DCP_EmployeeEnableReq, DCP_EmployeeEnableRes> {

    @Override
    protected void processDUID(DCP_EmployeeEnableReq req, DCP_EmployeeEnableRes res) throws Exception {

        try {
            List<DCP_EmployeeEnableReq.EmployeeNo> datas = req.getRequest().getEnablelist();

            String eid = req.geteId();
            int status = "1".equals(req.getRequest().getOprType())?100:0;

            for (DCP_EmployeeEnableReq.EmployeeNo param : datas) {
                UptBean uptBean = new UptBean("DCP_EMPLOYEE");

                uptBean.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                uptBean.addCondition("EMPLOYEENO", new DataValue(param.getEmployeeNo(), Types.VARCHAR));

                uptBean.addUpdateValue("STATUS",new DataValue(status,Types.INTEGER));

                this.addProcessData(new DataProcessBean(uptBean));
            }

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        }catch (Exception e) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_EmployeeEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_EmployeeEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_EmployeeEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_EmployeeEnableReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (req.getRequest().getEnablelist() == null || req.getRequest().getEnablelist().isEmpty()) {
            errMsg.append("删除列表不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for (DCP_EmployeeEnableReq.EmployeeNo par : req.getRequest().getEnablelist() ) {
            String employeeNo = par.getEmployeeNo();

            if (Check.Null(employeeNo)) {
                errMsg.append("人员编码不能为空值 ");
                isFail = true;
            }

        }

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_EmployeeEnableReq> getRequestType() {
        return new TypeToken<DCP_EmployeeEnableReq>() {
        };
    }

    @Override
    protected DCP_EmployeeEnableRes getResponseType() {
        return new DCP_EmployeeEnableRes();
    }
}
