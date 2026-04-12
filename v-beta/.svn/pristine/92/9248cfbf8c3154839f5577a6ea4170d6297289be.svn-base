package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_VoucherEmpDeleteReq;
import com.dsc.spos.json.cust.res.DCP_VoucherEmpDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.util.List;

public class DCP_VoucherEmpDelete extends SPosAdvanceService<DCP_VoucherEmpDeleteReq, DCP_VoucherEmpDeleteRes>
{

    @Override
    protected void processDUID(DCP_VoucherEmpDeleteReq req, DCP_VoucherEmpDeleteRes res) throws Exception
    {
        List<DCP_VoucherEmpDeleteReq.level1Elm> empList= req.getRequest().getEmpList();

        for (DCP_VoucherEmpDeleteReq.level1Elm level1Elm : empList)
        {
            DelBean db1 = new DelBean("DCP_VOUCHER_EMP");
            db1.addCondition("OPNO", new DataValue(level1Elm.getOpNo(), Types.VARCHAR));
            db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));
        }


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_VoucherEmpDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_VoucherEmpDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_VoucherEmpDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_VoucherEmpDeleteReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        List<DCP_VoucherEmpDeleteReq.level1Elm> empList= req.getRequest().getEmpList();
        if (empList == null || empList.size()==0)
        {
            errMsg.append("列表不可为空, ");
            isFail = true;
        }

        for (DCP_VoucherEmpDeleteReq.level1Elm level1Elm : empList)
        {
            if (Check.Null(level1Elm.getOpNo()))
            {
                errMsg.append("员工编号不可为空, ");
                isFail = true;
            }
        }


        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_VoucherEmpDeleteReq> getRequestType()
    {
        return new TypeToken<DCP_VoucherEmpDeleteReq>(){};
    }

    @Override
    protected DCP_VoucherEmpDeleteRes getResponseType()
    {
        return new DCP_VoucherEmpDeleteRes();
    }


}
