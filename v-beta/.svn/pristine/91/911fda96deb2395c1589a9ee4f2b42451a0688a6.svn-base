package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderTempCreate_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderTempCreate_OpenRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * 服务函数：DCP_OrderTempCreate_Open
 * 服务说明：订单挂单数据创建
 * @author jinzma
 * @since  2023-12-26
 */

public class DCP_OrderTempCreate_Open extends SPosAdvanceService<DCP_OrderTempCreate_OpenReq, DCP_OrderTempCreate_OpenRes> {
    @Override
    protected void processDUID(DCP_OrderTempCreate_OpenReq req, DCP_OrderTempCreate_OpenRes res) throws Exception {

        try{
            String eId = req.geteId();
            if (Check.Null(eId)){
                eId = req.getRequest().geteId();
                if (Check.Null(eId)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企业编号不能为空 ");
                }
            }


            String shopId = req.getRequest().getShopNo();
            String orderNo = req.getRequest().getOrderNo();

            ParseJson pj = new ParseJson();
            String tempOrder = pj.beanToJson(req.getRequest());

            RedisPosPub redis = new RedisPosPub();

            String redis_key = orderRedisKeyInfo.redis_OrderTemp + ":" + eId + ":" + shopId + ":" + orderNo;
            if (!redis.setEx(redis_key, 43200,tempOrder)){
                HelpTools.writelog_waimai("ORDER_TEMP【写缓存】Error" + " redis_key:" + redis_key + " tempOrder:" + tempOrder);
            }


            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderTempCreate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderTempCreate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderTempCreate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrderTempCreate_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (req.getRequest()==null) {
            errMsg.append("request不可为空值, ");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getShopNo())) {
                errMsg.append("门店编号不可为空值, ");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getOrderNo())) {
                errMsg.append("订单编号不可为空值, ");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_OrderTempCreate_OpenReq> getRequestType() {
        return new TypeToken<DCP_OrderTempCreate_OpenReq>(){};
    }

    @Override
    protected DCP_OrderTempCreate_OpenRes getResponseType() {
        return new DCP_OrderTempCreate_OpenRes();
    }
}
