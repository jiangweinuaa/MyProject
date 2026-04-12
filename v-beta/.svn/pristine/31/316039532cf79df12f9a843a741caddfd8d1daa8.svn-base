package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DepartmentEnableReq;
import com.dsc.spos.json.cust.req.DCP_OrgCreateReq;
import com.dsc.spos.json.cust.res.DCP_DepartmentEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.util.List;

public class DCP_DepartmentEnable extends SPosAdvanceService<DCP_DepartmentEnableReq, DCP_DepartmentEnableRes> {
    @Override
    protected void processDUID(DCP_DepartmentEnableReq req, DCP_DepartmentEnableRes res) throws Exception {
        try {
            String status;//状态：-1未启用100已启用 0已禁用

            if ("1".equals(req.getRequest().getOprType())) { //操作类型：1-启用2-禁用
                status = "100";
            } else {
                status = "0";
            }
            String departmentNo = req.getRequest().getDeptNo();
            UptBean up1 = new UptBean("DCP_DEPARTMENT");
            up1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            up1.addCondition("DEPARTNO", new DataValue(departmentNo, Types.VARCHAR));

            up1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(up1));

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");


        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常：" + e.getMessage());

        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DepartmentEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DepartmentEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DepartmentEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DepartmentEnableReq req) throws Exception {

        StringBuilder errMsg = new StringBuilder();

        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(req.getRequest().getOprType())) {
            errMsg.append("操作类型不可为空值, ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(req.getRequest().getDeptNo())) {
            errMsg.append("部门编码不可为空值, ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_DepartmentEnableReq> getRequestType() {
        return new TypeToken<DCP_DepartmentEnableReq>(){

        };
    }

    @Override
    protected DCP_DepartmentEnableRes getResponseType() {
        return new DCP_DepartmentEnableRes();
    }


}
