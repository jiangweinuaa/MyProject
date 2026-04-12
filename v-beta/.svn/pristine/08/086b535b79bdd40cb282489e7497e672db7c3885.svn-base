package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_VoucherEmpCreateReq;
import com.dsc.spos.json.cust.res.DCP_VoucherEmpCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCP_VoucherEmpCreate extends SPosAdvanceService<DCP_VoucherEmpCreateReq, DCP_VoucherEmpCreateRes>
{


    @Override
    protected void processDUID(DCP_VoucherEmpCreateReq req, DCP_VoucherEmpCreateRes res) throws Exception
    {
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String opNo= req.getRequest().getOpNo();
        String opName= req.getRequest().getOpName();
        String opNoOut= req.getRequest().getOpNoOut();

        String[] columns_DCP_VOUCHER_EMP =
                {
                        "EID","OPNO","OPNO_OUT","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"
                };

        DataValue[] insValue1 = null;

        insValue1 = new DataValue[]{
                new DataValue(req.geteId(), Types.VARCHAR),
                new DataValue(opNo, Types.VARCHAR),
                new DataValue(opNoOut, Types.VARCHAR),
                new DataValue(req.getOpNO(), Types.VARCHAR),
                new DataValue(req.getOpName(), Types.VARCHAR),
                new DataValue(lastmoditime, Types.DATE)
        };

        InsBean ib1 = new InsBean("DCP_VOUCHER_EMP", columns_DCP_VOUCHER_EMP);
        ib1.addValues(insValue1);
        this.addProcessData(new DataProcessBean(ib1)); // 新增

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_VoucherEmpCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_VoucherEmpCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_VoucherEmpCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_VoucherEmpCreateReq req) throws Exception
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
    protected TypeToken<DCP_VoucherEmpCreateReq> getRequestType()
    {
        return new TypeToken<DCP_VoucherEmpCreateReq>(){};
    }

    @Override
    protected DCP_VoucherEmpCreateRes getResponseType()
    {
        return new DCP_VoucherEmpCreateRes();
    }
}
