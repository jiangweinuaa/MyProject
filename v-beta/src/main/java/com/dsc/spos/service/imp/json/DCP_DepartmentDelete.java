package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DepartmentDeleteReq;
import com.dsc.spos.json.cust.res.DCP_DepartmentDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

public class DCP_DepartmentDelete extends SPosAdvanceService<DCP_DepartmentDeleteReq, DCP_DepartmentDeleteRes> {
    @Override
    protected void processDUID(DCP_DepartmentDeleteReq req, DCP_DepartmentDeleteRes res) throws Exception {
        try {
            DelBean del = new DelBean("DCP_DEPARTMENT");
            del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            del.addCondition("DEPARTNO", new DataValue(req.getRequest().getDeptNo(), Types.VARCHAR));

            DelBean delLang = new DelBean("DCP_DEPARTMENT_LANG");
            delLang.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            delLang.addCondition("DEPARTNO", new DataValue(req.getRequest().getDeptNo(), Types.VARCHAR));

            this.addProcessData(new DataProcessBean(del));
            this.addProcessData(new DataProcessBean(delLang));
            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DepartmentDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DepartmentDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DepartmentDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DepartmentDeleteReq req) throws Exception {
        // TODO Auto-generated method stub

        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (req.getRequest() == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_DepartmentDeleteReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_DepartmentDeleteReq>() {
        };
    }

    @Override
    protected DCP_DepartmentDeleteRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_DepartmentDeleteRes();
    }

}
