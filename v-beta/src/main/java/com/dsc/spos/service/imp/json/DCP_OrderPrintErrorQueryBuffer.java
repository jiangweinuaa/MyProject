package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_OrderPrintErrorQueryBufferReq;
import com.dsc.spos.json.cust.res.DCP_OrderPrintErrorQueryBufferRes;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Map;

public class DCP_OrderPrintErrorQueryBuffer extends SPosBasicService<DCP_OrderPrintErrorQueryBufferReq, DCP_OrderPrintErrorQueryBufferRes> {

    private String shopLogFileName = "WaimaiOrderPrintError";
    @Override
    protected boolean isVerifyFail(DCP_OrderPrintErrorQueryBufferReq req) throws Exception {


        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(req.getRequest().geteId()))
        {
            errCt++;
            errMsg.append("企业编号eId不可为空值, ");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getShopNo()))
        {
            errCt++;
            errMsg.append("当前门店shopNo不可为空值, ");
            isFail = true;
        }

        if (isFail)
        {
            throw new DispatchService.SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_OrderPrintErrorQueryBufferReq> getRequestType() {
        return new TypeToken<DCP_OrderPrintErrorQueryBufferReq>(){};
    }

    @Override
    protected DCP_OrderPrintErrorQueryBufferRes getResponseType() {
        return new DCP_OrderPrintErrorQueryBufferRes();
    }

    @Override
    protected DCP_OrderPrintErrorQueryBufferRes processJson(DCP_OrderPrintErrorQueryBufferReq req) throws Exception {

        DCP_OrderPrintErrorQueryBufferRes res = this.getResponse();
        res.setSuccess(true);
        String eId = req.getRequest().geteId();
        String shopNo = req.getRequest().getShopNo();
        String redis_key = orderRedisKeyInfo.redis_OrderPrintError + ":" + eId + ":" + shopNo;

        res.setDatas(new ArrayList<DCP_OrderPrintErrorQueryBufferRes.level1Elm>());
        StringBuffer errorPrintOrderNoStr = new StringBuffer("");
        try
        {
            RedisPosPub redis = new RedisPosPub();

            // String redis_key = "WMORDER:99:10001";

            //Map<String, String> ordermap = redis.getALLHashMap(redis_key);

            Map<String, String> ordermap = redis.getALLHashMap_Hscan(redis_key);
            for (Map.Entry<String, String> entry : ordermap.entrySet())
            {
                if (entry.getValue() != null)
                {
                    try
                    {
                        String orderJson = entry.getValue();
                        errorPrintOrderNoStr.append(orderJson+",");
                        DCP_OrderPrintErrorQueryBufferRes.level1Elm oneLv1 = res.new level1Elm();
                        oneLv1.setOrderNo(orderJson);
                        res.getDatas().add(oneLv1);
                    }
                    catch (Exception e)
                    {
                        continue;
                    }
                }
            }

        }
        catch (Exception e)
        {
            HelpTools.writelog_fileName("获取【打印异常的外卖订单】异常:" +e.getMessage()+ "，redis_key:" + redis_key, shopLogFileName);
        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_OrderPrintErrorQueryBufferReq req) throws Exception {
        return null;
    }
}
