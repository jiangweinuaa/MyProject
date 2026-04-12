package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_CurrencyDeleteReq;
import com.dsc.spos.json.cust.res.DCP_CurrencyDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_CurrencyDelete extends SPosAdvanceService<DCP_CurrencyDeleteReq, DCP_CurrencyDeleteRes>
{
    @Override
    protected void processDUID(DCP_CurrencyDeleteReq req, DCP_CurrencyDeleteRes res) throws Exception {
        // TODO Auto-generated method stub
        String eId = req.geteId();
        String currency = req.getRequest().getCurrency();
        String nation = req.getRequest().getNation();
        try
        {
            DelBean db1 = new DelBean("DCP_CURRENCY");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("NATION", new DataValue(nation, Types.VARCHAR));
            db1.addCondition("CURRENCY", new DataValue(currency, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            DelBean db2 = new DelBean("DCP_CURRENCY_LANG");
            db2.addCondition("NATION", new DataValue(nation,Types.VARCHAR));
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db2.addCondition("CURRENCY", new DataValue(currency, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));


            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        }
        catch (Exception e)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CurrencyDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CurrencyDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CurrencyDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_CurrencyDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
       // if(Check.Null(req.geteId())){
        //    errMsg.append("eId不可为空值, ");
        //    isFail = true;
       // }
        if(Check.Null(req.getRequest().getNation())){
            errMsg.append("国家地区不可为空值, ");
            isFail = true;
        }

        if(Check.Null(req.getRequest().getCurrency())){
            errMsg.append("币种编号不可为空值, ");
            isFail = true;
        }

        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_CurrencyDeleteReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_CurrencyDeleteReq>(){};
    }

    @Override
    protected DCP_CurrencyDeleteRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_CurrencyDeleteRes();
    }

}
