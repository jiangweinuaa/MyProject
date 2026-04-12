package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_OrderTempQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderTempQuery_OpenRes;
import com.dsc.spos.json.cust.res.DCP_OrderTempQuery_OpenRes.Datas;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.entity.order;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.cxf.common.util.CollectionUtils;

/**
 * 服务函数：DCP_OrderTempQuery_Open
 * 服务说明：订单挂单数据查询
 * @author jinzma
 * @since  2023-12-26
 */
public class DCP_OrderTempQuery_Open extends SPosBasicService<DCP_OrderTempQuery_OpenReq, DCP_OrderTempQuery_OpenRes> {

    @Override
    protected boolean isVerifyFail(DCP_OrderTempQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (req.getRequest()==null) {
            errMsg.append("request不可为空值, ");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getShopId())) {
                errMsg.append("门店编号不可为空值, ");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_OrderTempQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_OrderTempQuery_OpenReq>(){};
    }

    @Override
    protected DCP_OrderTempQuery_OpenRes getResponseType() {
        return new DCP_OrderTempQuery_OpenRes();
    }

    @Override
    protected DCP_OrderTempQuery_OpenRes processJson(DCP_OrderTempQuery_OpenReq req) throws Exception {
        try{
            DCP_OrderTempQuery_OpenRes res = this.getResponse();
            Datas datas = res.new Datas();
            datas.setOrderList(new ArrayList<>());
            List<order> orderList = new ArrayList<>();
            String eId = req.geteId();
            if (Check.Null(eId)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企业编号不能为空 ");
            }
            String shopId = req.getRequest().getShopId();
            String machineId = req.getRequest().getMachineId();
            String billNo = req.getRequest().getBillNo();

            ParseJson pj = new ParseJson();
            RedisPosPub redis = new RedisPosPub();
            String redis_key = orderRedisKeyInfo.redis_OrderTemp + ":" + eId + ":" + shopId +"*";

            //过滤单号
            if (!Check.Null(billNo)){
                redis_key = orderRedisKeyInfo.redis_OrderTemp + ":" + eId + ":" + shopId + ":" + billNo;
            }

            List<String> tempOrders = redis.getString_scan(redis_key);
            if (!CollectionUtils.isEmpty(tempOrders)){
                for (String tempOrder:tempOrders){
                    String orderJson = redis.getString(tempOrder);
                    order order = pj.jsonToBean(orderJson,new TypeToken<order>(){});

                    //过滤机台
                    if (!Check.Null(machineId)){
                        if (!machineId.equals(order.getMachineNo())){
                            continue;
                        }
                    }

                    orderList.add(order);
                }
            }

            datas.setOrderList(orderList.stream().distinct().collect(Collectors.toList()));
            datas.setTotal(String.valueOf(datas.getOrderList().size()));

            res.setDatas(datas);
            return res;

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_OrderTempQuery_OpenReq req) throws Exception {
        return null;
    }
}
