package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_VoucherEmpUpdateReq;
import com.dsc.spos.json.cust.res.DCP_VoucherEmpUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCP_VoucherEmpUpdate extends SPosAdvanceService<DCP_VoucherEmpUpdateReq, DCP_VoucherEmpUpdateRes>
{


    @Override
    protected void processDUID(DCP_VoucherEmpUpdateReq req, DCP_VoucherEmpUpdateRes res) throws Exception
    {
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String opNo= req.getRequest().getOpNo();
        String opName= req.getRequest().getOpName();
        String opNoOut= req.getRequest().getOpNoOut();

        UptBean ub1 = new UptBean("DCP_VOUCHER_EMP");
        ub1.addUpdateValue("OPNO_OUT", new DataValue(opNoOut, Types.VARCHAR));
        ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
        ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(), Types.VARCHAR));
        ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));

        //condition
        ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        ub1.addCondition("OPNO", new DataValue(opNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_VoucherEmpUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_VoucherEmpUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_VoucherEmpUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_VoucherEmpUpdateReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        String opNo= req.getRequest().getOpNo();
        String opName= req.getRequest().getOpName();
        String opNoOut= req.getRequest().getOpNoOut();

        if (Check.Null(opNo))
        {
            errMsg.append("员工编号不可为空, ");
            isFail = true;
        }
        if (Check.Null(opName))
        {
            errMsg.append("员工名称不可为空, ");
            isFail = true;
        }
        if (Check.Null(opNoOut))
        {
            errMsg.append("外部员工编号不可为空, ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_VoucherEmpUpdateReq> getRequestType()
    {
        return new TypeToken<DCP_VoucherEmpUpdateReq>(){};
    }

    @Override
    protected DCP_VoucherEmpUpdateRes getResponseType()
    {
        return new DCP_VoucherEmpUpdateRes();
    }


}
