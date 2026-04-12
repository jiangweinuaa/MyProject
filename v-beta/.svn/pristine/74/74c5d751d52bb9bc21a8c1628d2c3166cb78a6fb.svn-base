package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ReserveCouponPayReverse_OpenReq;
import com.dsc.spos.json.cust.res.DCP_ReserveCouponPayReverse_OpenRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
*@description: 会员体验券核销撤销
*@author: wangzyc
*@create: 2022-01-07 
*/
public class DCP_ReserveCouponPayReverse_Open extends SPosAdvanceService<DCP_ReserveCouponPayReverse_OpenReq, DCP_ReserveCouponPayReverse_OpenRes> {
    @Override
    protected void processDUID(DCP_ReserveCouponPayReverse_OpenReq req, DCP_ReserveCouponPayReverse_OpenRes res) throws Exception {
        /**
         * 1. 撤销券核销
         * 2.撤销预约单状态为1，且根据门店参数SHOPDISTRIBUTION 判断 opno是否清楚
         * 3.清楚会员服务记录
         */
        DCP_ReserveCouponPayReverse_OpenReq.level1Elm request = req.getRequest();
        String orderNo = request.getOrderNo();
        String trade_no = request.getTrade_no();
        String appid = req.getApiUserCode();
        String eId = req.geteId();
        String shopId = request.getShopId();
        String reserveNo = request.getReserveNo();

        String memberUrl = PosPub.getCRM_INNER_URL(eId);
        if (memberUrl.trim().equals("")) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "移动支付接口参数未设置!");
        }

        JSONObject couponPayRReq = new JSONObject();
        couponPayRReq.put("serviceId", "CouponPayReverse");

        JSONObject cprReq = new JSONObject();
        cprReq.put("orderNo", orderNo);
        cprReq.put("trade_no", trade_no);

        couponPayRReq.put("request", cprReq);

        String cprReqStr = cprReq.toString();
        String cprSign = PosPub.encodeMD5(cprReqStr + req.getApiUser().getUserKey());

        JSONObject cprSignJson = new JSONObject();
        cprSignJson.put("sign", cprSign);
        cprSignJson.put("key", appid);

        couponPayRReq.put("sign", cprSignJson);
        //********** 已经准备好 CouponPay 的json，开始调用 *************
        String s2 = couponPayRReq.toString();
        String cprResStr = HttpSend.Sendcom(couponPayRReq.toString(), memberUrl).trim();
        HelpTools.writelog_fileName("***********会员体验券核销撤销 DCP_ReserveCouponPayReverse_Open 调用 CouponPayReverse 接口，请求json：" + couponPayRReq.toString()
                + "*************", "DCP_ReserveCouponPayReverse_Open");

        JSONObject cprResJson = new JSONObject();
        cprResJson = JSON.parseObject(cprResStr);//String转json
        HelpTools.writelog_fileName("***********会员体验券核销撤销 DCP_ReserveCouponPayReverse_Open 调用 CouponPayReverse 接口返回信息：" + cprResStr + "*************", "DCP_ReserveCouponPayReverse_Open");
        String cprSuccess = cprResJson.getString("success").toUpperCase(); // TRUE 或 FALSE
        String cpsrServiceStatus = cprResJson.getString("serviceStatus").toUpperCase();
        String cpsrServiceDescription = cprResJson.getString("serviceDescription").toUpperCase();

        if (!Check.Null(cprSuccess) && cprSuccess.equals("TRUE")) {
            // 撤销成功
            String sql = "select * from DCP_RESERVE where eid = '" + eId + "' and SHOPID = '" + shopId + "' and RESERVENO = '" + reserveNo + "'";
            List<Map<String, Object>> data = this.doQueryData(sql, null);
            if(!CollectionUtils.isEmpty(data)){
                Map<String, Object> map = data.get(0);
                String shopdistribution = map.get("SHOPDISTRIBUTION").toString();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                UptBean ub1 = null;
                ub1 = new UptBean("DCP_RESERVE");
                ub1.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));
                if ("Y".equals(shopdistribution)) {
                    ub1.addUpdateValue("OPNO", new DataValue("", Types.VARCHAR));
                }
                ub1.addUpdateValue("LASTMODITIME", new DataValue(df.format(new Date()), Types.DATE));
                ub1.addCondition("RESERVENO", new DataValue(reserveNo, Types.VARCHAR));
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(ub1));

                DelBean del = new DelBean("DCP_MEMBERSERVICE");
                del.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                del.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                del.addCondition("RESERVENO", new DataValue(reserveNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(del));

                this.doExecuteDataToDB();
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
            }else {
                res.setSuccess(false);
                res.setServiceStatus("100");
                res.setServiceDescription("该预约单号不存在，请重试！");
            }
        }else{
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("券撤销失败："+cpsrServiceDescription);
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReserveCouponPayReverse_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReserveCouponPayReverse_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReserveCouponPayReverse_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReserveCouponPayReverse_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ReserveCouponPayReverse_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(request.getShopId())) {
            errMsg.append("所属门店不能为空, ");
            isFail = true;
        }

        if (Check.Null(request.getOrderNo())) {
            errMsg.append("消费单号，商户唯一订单号不能为空, ");
            isFail = true;
        }
        if (Check.Null(request.getReserveNo())) {
            errMsg.append("预约单号不能为空, ");
            isFail = true;
        }

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ReserveCouponPayReverse_OpenReq> getRequestType() {
        return new TypeToken<DCP_ReserveCouponPayReverse_OpenReq>(){};
    }

    @Override
    protected DCP_ReserveCouponPayReverse_OpenRes getResponseType() {
        return new DCP_ReserveCouponPayReverse_OpenRes();
    }
}
