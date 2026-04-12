package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_EmployeeDeleteReq;
import com.dsc.spos.json.cust.res.DCP_EmployeeDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_EmployeeDelete extends SPosAdvanceService<DCP_EmployeeDeleteReq, DCP_EmployeeDeleteRes> {

    @Override
    protected void processDUID(DCP_EmployeeDeleteReq req, DCP_EmployeeDeleteRes res) throws Exception {

        try {
            List<DCP_EmployeeDeleteReq.EmployeeNo> datas = req.getRequest().getDeletelist();

            String eid = req.geteId();

            for (DCP_EmployeeDeleteReq.EmployeeNo param : datas) {
                DelBean db1 = new DelBean("DCP_EMPLOYEE");

                db1.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                db1.addCondition("EMPLOYEENO", new DataValue(param.getEmployeeNo(), Types.VARCHAR));

                this.addProcessData(new DataProcessBean(db1));
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
    protected List<InsBean> prepareInsertData(DCP_EmployeeDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_EmployeeDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_EmployeeDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_EmployeeDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (req.getRequest().getDeletelist() == null || req.getRequest().getDeletelist().isEmpty()) {
            errMsg.append("删除列表不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for (DCP_EmployeeDeleteReq.EmployeeNo par : req.getRequest().getDeletelist() ) {
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
    protected TypeToken<DCP_EmployeeDeleteReq> getRequestType() {
        return new TypeToken<DCP_EmployeeDeleteReq>() {
        };
    }

    @Override
    protected DCP_EmployeeDeleteRes getResponseType() {
        return new DCP_EmployeeDeleteRes();
    }
}
