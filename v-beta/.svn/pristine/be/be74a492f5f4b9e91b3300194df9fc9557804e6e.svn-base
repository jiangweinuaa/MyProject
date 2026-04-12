package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_FinancialAuditExportReq;
import com.dsc.spos.json.cust.res.DCP_FinancialAuditExportRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.dao.DataValue.DataExpression;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCP_FinancialAuditExport extends SPosAdvanceService<DCP_FinancialAuditExportReq, DCP_FinancialAuditExportRes>
{

    @Override
    protected void processDUID(DCP_FinancialAuditExportReq req, DCP_FinancialAuditExportRes res) throws Exception
    {
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String eId=req.geteId();

        for (DCP_FinancialAuditExportReq.level2Elm data : req.getRequest().getShopList())
        {
            String shopId=data.getShopId();
            String bDate=data.getBDate();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat sdf_Date = new SimpleDateFormat("yyyy-MM-dd");

            UptBean ub1 = new UptBean("DCP_CLOSE_SHOP");
            ub1.addUpdateValue("EXPORTCOUNT", new DataValue(1, Types.INTEGER, DataExpression.UpdateSelf));

            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            ub1.addCondition("EDATE", new DataValue(sdf.format(sdf_Date.parse(bDate)), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));
        }

        //
        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
        return;
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_FinancialAuditExportReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_FinancialAuditExportReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_FinancialAuditExportReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_FinancialAuditExportReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (req.getRequest().getShopList()==null ||req.getRequest().getShopList().size()==0)
        {
            isFail = true;
            errMsg.append("shopList不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        else
        {
            for (DCP_FinancialAuditExportReq.level2Elm data : req.getRequest().getShopList())
            {
                String shopId=data.getShopId();
                String bDate=data.getBDate();
                if (Check.Null(shopId))
                {
                    errMsg.append("shopId不能为空, ");
                    isFail = true;
                }
                if (Check.Null(bDate))
                {
                    errMsg.append("bDate不能为空, ");
                    isFail = true;
                }
                if (Check.Null(bDate)==false && bDate.length()!=10)
                {
                    errMsg.append("营业日期bDate必须是YYYY-MM-DD格式, ");
                    isFail = true;
                }
            }
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_FinancialAuditExportReq> getRequestType()
    {
        return new TypeToken<DCP_FinancialAuditExportReq>(){};
    }

    @Override
    protected DCP_FinancialAuditExportRes getResponseType()
    {
        return new DCP_FinancialAuditExportRes();
    }


}
