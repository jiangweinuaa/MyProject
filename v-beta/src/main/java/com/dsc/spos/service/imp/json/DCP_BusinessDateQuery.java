package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_BusinessDateQueryReq;
import com.dsc.spos.json.cust.res.DCP_BusinessDateQueryRes;
import com.dsc.spos.json.cust.res.DCP_BusinessDateQueryRes.level1Elm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.util.Map;

/**
 * 服务函数：DCP_BusinessDateQuery
 * 服务说明：获取营业日期
 * @author jinzma
 * @since  2022-06-24
 */
public class DCP_BusinessDateQuery extends SPosBasicService<DCP_BusinessDateQueryReq, DCP_BusinessDateQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_BusinessDateQueryReq req) throws Exception {
        return false;
    }
    
    @Override
    protected TypeToken<DCP_BusinessDateQueryReq> getRequestType() {
        return new TypeToken<DCP_BusinessDateQueryReq>(){};
    }
    
    @Override
    protected DCP_BusinessDateQueryRes getResponseType() {
        return new DCP_BusinessDateQueryRes();
    }
    
    @Override
    protected DCP_BusinessDateQueryRes processJson(DCP_BusinessDateQueryReq req) throws Exception {
        try{
            DCP_BusinessDateQueryRes res = this.getResponse();
            String eId = req.geteId();
            String shopId = req.getShopId();
            String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
            bDate = PosPub.GetStringDateLine(bDate,0);
            
            level1Elm oneLv1 = res.new level1Elm();
            oneLv1.setBusinessDate(bDate);
            res.setDatas(oneLv1);
            
            return res;
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    
    }
    
    @Override
    protected String getQuerySql(DCP_BusinessDateQueryReq req) throws Exception {
        return null;
    }
}
