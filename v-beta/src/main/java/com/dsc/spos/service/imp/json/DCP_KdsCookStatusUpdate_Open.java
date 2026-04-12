package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_KdsCookStatusUpdate_OpenReq;
import com.dsc.spos.json.cust.res.DCP_KdsCookStatusUpdate_OpenRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.util.List;

public class DCP_KdsCookStatusUpdate_Open extends SPosAdvanceService<DCP_KdsCookStatusUpdate_OpenReq, DCP_KdsCookStatusUpdate_OpenRes>
{

    Logger logger = LogManager.getLogger(DCP_KdsCookStatusUpdate_Open.class.getName());

    @Override
    protected void processDUID(DCP_KdsCookStatusUpdate_OpenReq req, DCP_KdsCookStatusUpdate_OpenRes res) throws Exception
    {

        DCP_KdsCookStatusUpdate_OpenReq.level1Elm request = req.getRequest();

        String cookStatus=request.getCookStatus();

        if (Check.Null(cookStatus))   cookStatus="0";

        if (PosPub.isNumeric(cookStatus) ==false) cookStatus="0";


        String eId = req.geteId();

        try
        {
            UptBean ub1 = null;
            ub1 = new UptBean("DCP_KDSCOOKSET");
            //add Value
            ub1.addUpdateValue("COOKSTATUS", new DataValue(cookStatus, Types.INTEGER));
            //condition
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("SHOPID", new DataValue(request.getShopId(), Types.VARCHAR));
            ub1.addCondition("COOKID", new DataValue(request.getCookId(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));


            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        }
        catch (Exception e)
        {
            try
            {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);

                pw.flush();
                pw.close();

                errors.flush();
                errors.close();

                logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******DCP_KdsCookStatusUpdate_Open报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******DCP_KdsCookStatusUpdate_Open报错信息1" + e.getMessage() + "******\r\n");
            }
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_KdsCookStatusUpdate_OpenReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_KdsCookStatusUpdate_OpenReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_KdsCookStatusUpdate_OpenReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_KdsCookStatusUpdate_OpenReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_KdsCookStatusUpdate_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getShopId())) {
            errMsg.append("门店编号不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getMachineId())) {
            errMsg.append("机台编号不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getCookId())) {
            errMsg.append("机器人编号不能为空,");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_KdsCookStatusUpdate_OpenReq> getRequestType()
    {
        return new TypeToken<DCP_KdsCookStatusUpdate_OpenReq>(){};
    }

    @Override
    protected DCP_KdsCookStatusUpdate_OpenRes getResponseType()
    {
        return new DCP_KdsCookStatusUpdate_OpenRes();
    }


}
