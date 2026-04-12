package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockOutDownReq;
import com.dsc.spos.json.cust.res.DCP_StockOutDownRes;
import com.dsc.spos.scheduler.job.MES_StockoutAutoProcess;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_StockOutDown extends SPosAdvanceService<DCP_StockOutDownReq, DCP_StockOutDownRes>
{

    @Override
    protected boolean isVerifyFail(DCP_StockOutDownReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_StockOutDownReq.levelElm request = req.getRequest();


        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_StockOutDownReq> getRequestType() {
        return new TypeToken<DCP_StockOutDownReq>(){};
    }

    @Override
    protected DCP_StockOutDownRes getResponseType() {
        return new DCP_StockOutDownRes();
    }

    @Override
    protected void processDUID(DCP_StockOutDownReq req,DCP_StockOutDownRes res) throws Exception {
        List<DCP_StockOutDownReq.StockOutNoList> stockOutNoList = req.getRequest().getStockOutNoList();
        if(CollUtil.isNotEmpty(stockOutNoList)){
            List<String> collect = stockOutNoList.stream().map(x -> x.getStockOutNo()).distinct().collect(Collectors.toList());
            new MES_StockoutAutoProcess(req.geteId(),req.getOrganizationNO(),req.getOrganizationNO(),"",collect).doExe();
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockOutDownReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockOutDownReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockOutDownReq req) throws Exception {
        return null;
    }

}
