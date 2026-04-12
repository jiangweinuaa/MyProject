package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderPay_OpenReq;
import com.dsc.spos.json.cust.req.DCP_OrderPay_OpenReq.level2Elm;
import com.dsc.spos.json.cust.req.DCP_OrderPay_OpenReq.level3Elm;
import com.dsc.spos.json.cust.res.DCP_OrderPay_OpenRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderPay;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 聚合支付
 * Yapi  http://183.233.190.204:10004/project/148/interface/api/3462
 *
 * @author wangzyc
 */
public class DCP_OrderPay_Open extends SPosAdvanceService<DCP_OrderPay_OpenReq, DCP_OrderPay_OpenRes> {
    Logger logger = LogManager.getLogger(this.getClass().getName());

    @Override
    protected void processDUID(DCP_OrderPay_OpenReq req, DCP_OrderPay_OpenRes res) throws Exception {
        DCP_OrderPay_OpenRes response = this.getResponseType();
        // TODO Auto-generated method stub
        // 区分是会员卡支付 还是 微信支付  传 CreatePay 节点代表 微信支付 不传 CreatePay 节点代表会员卡支付
        RedisPosPub rpp = new RedisPosPub();
        String checkKey="OrderPay:"+req.getRequest().getOrderNo();        
        try {
            Calendar cal = Calendar.getInstance();//获得当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dfss = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            SimpleDateFormat dfs = new SimpleDateFormat("yyyyMMddHHmmss");
            if(!rpp.IsExistStringKey(checkKey))
            {
            	String createDate = df.format(cal.getTime());
            	rpp.setEx(checkKey,300,createDate);
            }else
            {
            	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "订单:"+req.getRequest().getOrderNo()+"频繁支付,请确认没有重复支付!!");
            } 
            Date now = new Date(cal.getTimeInMillis());
            // 生成唯一支付单号，微信支付要保证每次发起支付时单号都不一样，最长32位
            String prepay_id = java.util.UUID.randomUUID().toString().replace("-", "");

            DCP_OrderPay_OpenReq.level1Elm request = req.getRequest();
            String eId = req.geteId();
            String shopId = req.getApiUser().getShopId();


            String orderNo = request.getOrderNo();
            String orgType = request.getOrgType();
            String orgId = request.getOrgId();
            if (orgType.equals("2")) shopId = orgId;
            String openId = request.getOpenId(); // 第三方应用ID
            String loadDocType = request.getLoadDocType();
            String orderAmount = request.getOrderAmount();
            String pointAmount = request.getPointAmount();
            String opNo = request.getOpNo();
            String opName = request.getOpName();
            String workNo = request.getWorkNo();
            String squadNo = request.getSquadNo();
            String machineNo = request.getMachineNo();
            String sendMsg = request.getSendMsg();

            String sql_order = "select * from DCP_ORDER where EID = '" + eId + "' and ORDERNO = '" + orderNo + "'";
            List<Map<String, Object>> getOrderHead = StaticInfo.dao.executeQuerySQL(sql_order, null);
            if (getOrderHead==null||getOrderHead.isEmpty())
            {
                HelpTools.writelog_fileName("***************  DCP_OrderPay_Open接口传参异常：该订单不存在,单号orderNo=" + orderNo, "ScanPayAddLog");
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "订单不存在!");
            }
            String orderStatus = getOrderHead.get(0).getOrDefault("STATUS","").toString();
            String orderPayStatus = getOrderHead.get(0).getOrDefault("PAYSTATUS","").toString();
            String orderTotAmtStr = getOrderHead.get(0).getOrDefault("TOT_AMT","0").toString();
            if ("3".equals(orderStatus)||"12".equals(orderStatus))
            {
                HelpTools.writelog_fileName("***************  DCP_OrderPay_Open接口传参异常：该订单状态已取消或已退单,无须支付，单号orderNo=" + orderNo, "ScanPayAddLog");
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该订单状态已取消或已退单,无须支付!");
            }
            BigDecimal orderTotAmt = new BigDecimal("0");
            try
            {
                orderTotAmt = new BigDecimal(orderTotAmtStr);
            }
            catch (Exception e)
            {

            }


            List<level3Elm> pay = request.getPay();
            BigDecimal tot_pay = new BigDecimal("0");
            for (level3Elm level3Elm : pay) {
                level3Elm.setPaySerNum("");
                //level3Elm.setPaySerNum(prepay_id);
                String payType = level3Elm.getPayType();
                String payItemStr = level3Elm.getPay();
                if (payType==null||payType.trim().isEmpty())
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "支付的payType节点不能为空!");
                }
                if (payItemStr==null||payItemStr.trim().isEmpty())
                {
                    HelpTools.writelog_fileName("***************  DCP_OrderPay_Open接口传参异常：支付的金额pay节点不能为空,单号orderNo=" + orderNo, "ScanPayAddLog");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "支付的金额pay节点不能为空!");
                }
                if ("#P1".equalsIgnoreCase(payType)||"#P2".equalsIgnoreCase(payType))
                {
                    //后面，就不需要赋值了
                    level3Elm.setRefNo(prepay_id);
                }
                BigDecimal payItemAmt = new BigDecimal("0");
                try
                {
                    payItemAmt = new BigDecimal(payItemStr);
                }
                catch (Exception e)
                {

                }
                if (payItemAmt.compareTo(BigDecimal.ZERO)<=0)
                {
                    HelpTools.writelog_fileName("***************  DCP_OrderPay_Open接口传参异常：支付的金额pay节点不能小于等于0,单号orderNo=" + orderNo, "ScanPayAddLog");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "支付的金额pay节点不能为0!");
                }
                tot_pay = tot_pay.add(payItemAmt);
                //检核移动支付和支付记录付款金额是否一致
                if (null != request.getCreatePay())
                {
                    String createPay_payType = request.getCreatePay().getPay_type();
                    if (createPay_payType!=null&&payType.equals(createPay_payType))
                    {
                        String createPay_pay = request.getCreatePay().getPay_amt();
                        BigDecimal createPay_pay_b = new BigDecimal("0");
                        try
                        {
                            createPay_pay_b = new BigDecimal(createPay_pay);
                        }
                        catch (Exception e)
                        {

                        }
                        if (createPay_pay_b.compareTo(BigDecimal.ZERO)<=0)
                        {
                            HelpTools.writelog_fileName("***************  DCP_OrderPay_Open接口传参异常：移动支付金额不能为0,单号orderNo=" + orderNo, "ScanPayAddLog");
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "移动支付金额不能为0");
                        }

                        BigDecimal createPay_spwn = payItemAmt.subtract(createPay_pay_b);

                        if (createPay_spwn.compareTo(BigDecimal.ZERO)!=0)
                        {
                            HelpTools.writelog_fileName("***************  DCP_OrderPay_Open接口传参异常：移动支付金额("+createPay_pay+")与付款记录中金额("+payItemStr+")不一致!单号orderNo=" + orderNo, "ScanPayAddLog");
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "移动支付金额("+createPay_pay+")与付款记录中金额("+payItemStr+")不一致!");
                        }
                    }
                }

                //payType = (Check.Null(payType)&&request.getCreatePay()!=null)?"":payType;
                //level3Elm.setPayType(payType);
            }

            if (orderTotAmt.compareTo(tot_pay)>0)
            {
                HelpTools.writelog_fileName("***************  DCP_OrderPay_Open接口传参异常：付款的合计金额("+tot_pay+")小于订单总金额("+orderTotAmt+"),单号orderNo=" + orderNo, "ScanPayAddLog");
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "付款的合计金额("+tot_pay+")小于订单总金额("+orderTotAmt+")!");
            }

            List<level3Elm> payList = request.getPay();
            
            String crmPayUrl = PosPub.getPAY_INNER_URL(eId);
            String dcpUrl = PosPub.getDCP_INNER_URL(eId);
            String memberUrl = PosPub.getCRM_INNER_URL(eId);

            if (memberUrl.trim().equals("") || dcpUrl.trim().equals("") || crmPayUrl.trim().equals("")) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "CrmUrl、Mobile_Url、PlatformCentreURL移动支付接口参数未设置!");
            }

            ParseJson pj = new ParseJson();
            String reqJson = pj.beanToJson(req);

            HelpTools.writelog_fileName("*************** 移动支付 DCP_OrderPay_Open 请求json：" + reqJson, "ScanPayAddLog");

            // CreatePay 相关入参
            level2Elm createPay = request.getCreatePay();
            String payAmt = "";
            if (null != createPay) {
                payAmt = createPay.getPay_amt();
                if (!Check.Null(createPay.getPay_type()))
                {

                    if (payAmt==null||payAmt.trim().isEmpty())
                    {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "移动支付金额不能为0!");
                    }
                }
            }
            boolean isMultiPay = false;//是否组合支付 (移动支付+会员支付(会员卡券、现金券、积分抵现等))
            boolean isUseMobliePay = false;//是否使用了移动支付
            boolean isUseMemberPay = false;//是否使用了会员支付(会员卡券、现金券、积分抵现等)
            if (createPay != null && !Check.Null(payAmt))
            {
                isUseMobliePay = true;
                //如果付款记录大于1且使用了移动支付，那么一定是组合支付
                if (pay.size()>1)
                {
                    isMultiPay = true;
                    isUseMemberPay = true;
                }
            }
            //如果没有使用移动支付，那么一定是会员支付
            if (!isUseMobliePay)
            {
                isUseMemberPay = true;
            }



            String createDate = df.format(cal.getTime());
            String appid = req.getApiUserCode();
            //移动支付
            if (isUseMobliePay)
            {
                if (Check.Null(shopId)) {
                    shopId = createPay.getShop_code();
                }
                String pay_type = createPay.getPay_type();
                String shop_code = createPay.getShop_code();
                String pos_code = createPay.getPos_code();
                String order_name = createPay.getOrder_name();
                String order_des = createPay.getOrder_des();
                String pay_amt = createPay.getPay_amt();
                String pay_nodiscountamt = createPay.getPay_nodiscountamt();
                String ip = createPay.getIp() == null ? "" : createPay.getIp();
                String operation_id = createPay.getOperation_id() == null ? "" : createPay.getOperation_id();
                String notify_url = createPay.getNotify_url();
                String allow_pay_type = createPay.getAllow_pay_type() == null ? "" : createPay.getAllow_pay_type();
                String trade_type = Check.Null(createPay.getTrade_type()) ? "JSAPI" : createPay.getTrade_type();
                String openid = createPay.getOpenid(); // 微信openid微信JSAPI时必填

                // 查询下单时间 下单超时时间
                String sql = "select * from DCP_ORDER  a " +
                        " LEFT JOIN DCP_ECOMMERCE b ON a.EID  = b.EID  AND a.CHANNELID  = b.CHANNELID  AND a.LOADDOCTYPE = b.LOADDOCTYPE" +
                        " where a.EID = '" + eId + "' and a.ORDERNO = '" + orderNo + "' ";
                List<Map<String, Object>> getOrderInfo = this.doQueryData(sql, null);
                String createDateTime = ""; // 下单时间
                String expireTime = ""; // 下单超时时间 单位 分钟
                if (!CollectionUtils.isEmpty(getOrderInfo)) {
                    createDateTime = getOrderInfo.get(0).get("CREATE_DATETIME").toString();
                    expireTime = getOrderInfo.get(0).get("EXPIRETIME").toString();

                    // 应 SA 需求 渠道类型=WAIMAI的EXPIRETIME为空时，加一个默认值15分钟，程序里写死
                    expireTime = Check.Null(expireTime) == true ? "15" : expireTime;

                }
                if (Check.Null(expireTime)) {
                    expireTime = "15";
                }
                // 此处做个兼容 下单时间 有的精确到毫秒 有的精确到秒
                int length = createDateTime.length();
                Date createTimeDate = null;
                if (length > 14) {
                    createTimeDate = dfss.parse(createDateTime); // 下单时间
                } else {
                    createTimeDate = dfs.parse(createDateTime); // 下单时间
                }
                JSONObject createPayReq = new JSONObject();
                createPayReq.put("serviceId", "CreatePay");

                JSONObject payReq = new JSONObject();
                payReq.put("pay_type", pay_type);
                payReq.put("shop_code", shop_code);
                payReq.put("pos_code", pos_code);
                payReq.put("order_id", prepay_id);
                payReq.put("order_name", order_name);
                payReq.put("order_des", order_des);
                payReq.put("pay_amt", pay_amt);
                payReq.put("pay_nodiscountamt", pay_nodiscountamt);
                payReq.put("ip", ip);
                payReq.put("operation_id", operation_id);
                payReq.put("notify_url", notify_url);
                payReq.put("allow_pay_type", allow_pay_type);
                payReq.put("trade_type", trade_type);
                payReq.put("appid", createPay.getAppid());
                payReq.put("openid", openid);

                createPayReq.put("request", payReq);

                String reqStr = payReq.toString();
                String sign = PosPub.encodeMD5(reqStr + appid);

                JSONObject signJson = new JSONObject();
                signJson.put("sign", sign);
                signJson.put("key", appid);

                createPayReq.put("sign", signJson);

//                String  str = createPayReq.toString();
                //********** 已经准备好CreatePay的json，开始调用 *************
                String payResStr = "";
                JSONObject payResJson = new JSONObject();
                String paySuccess = ""; // TRUE 或 FALSE
                //CRM 接口提示： 返回false时，并不表示一定是创建支付失败，有可能顾客还在支付中，需判断serviceStatus
                String payServiceStatus = "";
                String payServiceDescription = "";
                Boolean isSuc = false;
                try
                {
                    payResStr = HttpSend.Sendcom(createPayReq.toString(), crmPayUrl);
                    HelpTools.writelog_fileName("移动支付 DCP_OrderPay_Open 调用CreatePay接口，请求json：" + createPayReq.toString()
                            + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id, "ScanPayAddLog");
                    payResJson = JSON.parseObject(payResStr);//String转json

                    HelpTools.writelog_fileName("移动支付 DCP_OrderPay_Open 调用CreatePay接口返回信息：" + payResStr + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id, "ScanPayAddLog");

                    paySuccess = payResJson.getOrDefault("success","").toString().toUpperCase(); // TRUE 或 FALSE
                    //CRM 接口提示： 返回false时，并不表示一定是创建支付失败，有可能顾客还在支付中，需判断serviceStatus
                    payServiceStatus = payResJson.getOrDefault("serviceStatus","").toString().toUpperCase();
                    payServiceDescription = payResJson.getOrDefault("serviceDescription","").toString().toUpperCase();
                }
                catch (Exception e)
                {

                }

                if ("TRUE".equalsIgnoreCase(paySuccess)) {
                    isSuc = true;
                }

                res.setSuccess(isSuc);
                res.setServiceStatus(payServiceStatus);
                res.setServiceDescription(payServiceDescription);

                //移动支付预下单失败，直接返回
                if (!isSuc)
                {
                    return;
                }
                JSONObject resDatas = new JSONObject();
                resDatas = payResJson.getJSONObject("datas");
                if (res.getDatas()==null)
                {
                    res.setDatas(response.new level1Elm());
                }
                res.getDatas().setCreatePay(resDatas);
                // MemberPay 节点先返回空
                if (res.getDatas().getMemberPay()==null)
                {
                    res.getDatas().setMemberPay(new JSONObject());
                }


                {
                    // 调用CreatePay 成功则 写缓存
                    // 轮询七分钟调用 Query 服务 若查询结果为支付成功， 则 调用订金补录 DCP_OrderModify_PayAdd 服务。
                    try {
                        // 移动支付请求信息写缓存

                        // 写缓存时间 过期时间
                        cal.add(cal.MINUTE, 7); // CreatePay 过期时间6分钟
                        String strTimeout = df.format(cal.getTime());

                        String createDateStr = "";//订单上的下单时间+超时支付时间(分钟)
                        // 下单时间过期时间
                        if (!Check.Null(expireTime) && PosPub.isNumeric(expireTime)) {
                            cal.setTime(createTimeDate);//订单上的下单时间
                            cal.add(cal.MINUTE, Integer.parseInt(expireTime)); // DCP_ECOMMERCE/EXPIRETIME :未支付失效时长， 单位分钟
                            createDateStr = df.format(cal.getTime());
                        }

                        JSONObject requestPayReq = new JSONObject();
                        requestPayReq.put("orderNo", orderNo);
                        requestPayReq.put("prepayId", prepay_id);
                        requestPayReq.put("workNo", workNo);
                        requestPayReq.put("squadNo", squadNo);
                        requestPayReq.put("machineNo", machineNo);
                        requestPayReq.put("opNo", opNo);
                        requestPayReq.put("opName", opName);
                        requestPayReq.put("orgType", orgType);
                        requestPayReq.put("orgId", orgId);
                        requestPayReq.put("loadDocType", loadDocType);
                        requestPayReq.put("openId", openId);
                        requestPayReq.put("orderAmount", orderAmount);
                        requestPayReq.put("pointAmount", pointAmount);
                        requestPayReq.put("sendMsg", sendMsg);

                        requestPayReq.put("createPay", createPay);

                        requestPayReq.put("pay", payList);

                        requestPayReq.put("uploadTime", strTimeout); // 写缓存过期时间
                        requestPayReq.put("expireTime", createDateStr); // 未支付有效时长
                        requestPayReq.put("sign", req.getApiUserCode()); // sige
                        // JOB 会定时轮询缓存信息， 查询
                        String requestJsonStr = requestPayReq.toString();
                        HelpTools.writelog_fileName("移动支付 DCP_OrderPay_Open 写缓存信息开始 json："
                                + requestJsonStr.toString() + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id, "ScanPayAddLog");

                        // 写Redis 缓存
                        RedisPosPub Rpp = new RedisPosPub();
                        String redis_key = "ScanPayOrder";
                        String hash_key = eId + ":" + shopId + ":" + orderNo;
                        boolean nRet_redis = Rpp.setHashMap(redis_key, hash_key, requestJsonStr);
                        if (nRet_redis)
                        {
                            HelpTools.writelog_fileName(
                                    "移动支付 DCP_OrderPay_Open 写缓存成功OK" + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id,
                                    "ScanPayAddLog");
                        }
                        else
                        {
                            HelpTools.writelog_fileName(
                                    "移动支付 DCP_OrderPay_Open 写缓存失败Error" + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id,
                                    "ScanPayAddLog");
                        }


                        Date timeout = df.parse(strTimeout);//移动支付超时时间
                        Date createDateExpireTime = null;//订单未支付超时时间
                        if (!Check.Null(createDateStr)) {
                            createDateExpireTime = df.parse(createDateStr);
                        }

                        /**
                         *   调用CreatePay接口 成功后 发起一个线程来轮询查询Query 服务 查询是否支付成功 若成功则 调用订金补录 DCP_OrderModify_PayAdd 服务
                         */
                        // 调用线程
                        Date finalCreateDateExpireTime = createDateExpireTime;
                        String shopid = shopId;
                        //boolean isUseMemberPay_thread = isUseMemberPay;
                        //String memberPayNo_thread = memberPayNo;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String sql = "";
                                while (true) {
                                    try {
                                        // 获取当前时间 比较 预计结束时间
                                        Date time = new Date();
                                        String createDate = df.format(time);
                                        String refundDatetime = dfss.format(time);
                                        Date parse = null;
                                        parse = df.parse(createDate);
                                        RedisPosPub rpp = new RedisPosPub();

//                                                String dcpUrl = PosPub.getDCP_URL(eId);
                                        HelpTools.writelog_fileName(
                                                "***********移动支付 DCP_OrderPay_Open  线程调用" + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id,
                                                "ScanPayAddLog");

                                        // ********************************* 先检查订单下单 是否超时 超时进行退单 Begin *********************************
                                        if (finalCreateDateExpireTime != null) {
                                            if (parse.compareTo(finalCreateDateExpireTime) == 0 || parse.compareTo(finalCreateDateExpireTime) == 1) {
                                                HelpTools.writelog_fileName(
                                                        "***********移动支付 DCP_OrderPay_Open  下单超时" + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id,
                                                        "ScanPayAddLog");
                                                sql = "select * from DCP_ORDER where EID = '" + eId + "' and ORDERNO = '" + orderNo + "'";
                                                List<Map<String, Object>> getOrderInfo = StaticInfo.dao.executeQuerySQL(sql, null);
                                                if (!CollectionUtils.isEmpty(getOrderInfo)) {
                                                    String status = getOrderInfo.get(0).get("STATUS").toString();
                                                    String channelId = getOrderInfo.get(0).get("CHANNELID").toString();
                                                    String tot_amt = getOrderInfo.get(0).get("TOT_AMT").toString();
                                                    String payStatus = getOrderInfo.get(0).get("PAYSTATUS").toString();
                                                    if (status.equals("3")) {
                                                        HelpTools.writelog_fileName(
                                                                "***********移动支付 DCP_OrderPay_Open  单号:" + orderNo + " 订单状态为已取消 " + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id,
                                                                "ScanPayAddLog");
                                                        rpp.DeleteHkey(redis_key, hash_key);
                                                        break;
                                                    }
                                                    if (status.equals("12")) {
                                                        HelpTools.writelog_fileName(
                                                                "***********移动支付 DCP_OrderPay_Open  单号:" + orderNo + " 订单状态为已退单 " + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id,
                                                                "ScanPayAddLog");
                                                        rpp.DeleteHkey(redis_key, hash_key);
                                                        break;
                                                    }
                                                    if (payStatus.equals("3")) {
                                                        HelpTools.writelog_fileName(
                                                                "***********移动支付 DCP_OrderPay_Open  单号:" + orderNo + " 订单付款状态为已支付 " + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id,
                                                                "ScanPayAddLog");
                                                        rpp.DeleteHkey(redis_key, hash_key);
                                                        break;
                                                    }

                                                }
                                                break;
                                            }
                                        }

                                        // ********************************* 先检查订单下单 是否超时 超时进行退单 End *********************************
                                        // 检查缓存超时时间 有无超时 如果超出超时时间则 终止线程
                                        if (parse.compareTo(timeout) == 0 || parse.compareTo(timeout) == 1) {
                                            HelpTools.writelog_fileName(
                                                    "***********移动支付 DCP_OrderPay_Open  支付缓存已超时" + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id,
                                                    "ScanPayAddLog");
                                            //Thread.sleep(10 * 1000);
                                            break;
                                        }


                                        String allStr =  requestJsonStr ;//这里不要取缓存了，缓存里面下次再支付，就会被覆盖了。
                                        //原来这里有，取缓存逻辑，不要取了，存在一个外卖单号，第1次取消，第2次再支付情况，缓存主键是外卖单号会被覆盖，就不能去缓存，直接取内存表示当前的支付循环
                                        JSONObject allJson = new JSONObject(new TreeMap<String, Object>());
                                        allJson = JSONObject.parseObject(allStr);
                                        HelpTools.writelog_fileName(
                                                "***********移动支付 DCP_OrderPay_Open  线程调用3" + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id,
                                                "ScanPayAddLog");

                                        // createPay

                                        JSONObject createPayReqJson = new JSONObject(new TreeMap<String, Object>());
                                        String createPayStr = allJson.get("createPay").toString();
                                        createPayReqJson = JSONObject.parseObject(createPayStr);

                                        HelpTools.writelog_fileName(
                                                "***********移动支付 DCP_OrderPay_Open  线程调用4" + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id,
                                                "ScanPayAddLog");
                                        // pay
                                        JSONArray payJson = allJson.getJSONArray("pay");

                                        HelpTools.writelog_fileName(
                                                "***********移动支付 DCP_OrderPay_Open  线程调用5" + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id,
                                                "ScanPayAddLog");
                                        String pay_type = createPayReqJson.get("pay_type").toString();
                                        String shop_code = createPayReqJson.get("shop_code").toString();
                                        String pos_code = createPayReqJson.get("pos_code").toString();
                                        String operation_id = "";
                                        try {
                                            operation_id = createPayReqJson.get("operation_id").toString();
                                        } catch (Exception e) {
                                            operation_id = "";
                                        }

                                        String ip = createPayReqJson.get("ip").toString();


                                        // ************* 先走一下查询， 看有没有支付成功 *****************

//                                                String crmPayUrl = PosPub.getPAY_URL(eId);
                                        String orderNo = allJson.get("orderNo").toString();
                                        String prepayId = allJson.get("prepayId").toString();
                                        String opNo = allJson.getOrDefault("opNo", "").toString();
                                        String opName = "";
                                        try {
                                            opName = allJson.getOrDefault("opName", "").toString();
                                        } catch (Exception e) {
                                            opName = "";
                                        }

                                        String workNo = allJson.getOrDefault("workNo", "").toString();
                                        String squadNo = allJson.getOrDefault("squadNo", "").toString();
                                        String machineNo = allJson.getOrDefault("machineNo", "").toString();
                                        String loadDocType = allJson.getOrDefault("loadDocType", "").toString();

                                        String uploadTime = allJson.getOrDefault("uploadTime", "").toString(); // 写缓存时间
                                        String expireTime = allJson.getOrDefault("expireTime", "").toString();

                                        String sign = allJson.getOrDefault("sign", "").toString();
                                        HelpTools.writelog_fileName(
                                                "***********移动支付 DCP_OrderPay_Open  线程调用6" + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id,
                                                "ScanPayAddLog");

                                        JSONObject QueryReq = new JSONObject();
                                        QueryReq.put("serviceId", "Query");

                                        JSONObject payReq = new JSONObject();
                                        payReq.put("pay_type", pay_type);
                                        payReq.put("shop_code", shop_code);
                                        payReq.put("pos_code", pos_code);
                                        payReq.put("order_id", prepayId);
                                        payReq.put("trade_no", "");
                                        payReq.put("operation_id", operation_id);
                                        payReq.put("ip", ip);

                                        QueryReq.put("request", payReq);

                                        String queryReqStr = payReq.toString();
                                        String querySign = PosPub.encodeMD5(queryReqStr + sign);

                                        JSONObject querySignJson = new JSONObject();
                                        querySignJson.put("sign", querySign);
                                        querySignJson.put("key", sign);

                                        QueryReq.put("sign", querySignJson);

                                        //********** 已经准备好Query的json，开始调用 *************
                                        String queryResStr = HttpSend.Sendcom(QueryReq.toString(), crmPayUrl).trim();

                                        HelpTools.writelog_fileName("*********** 移动支付 调用Query接口信息：地址（" + crmPayUrl + "）  请求Json：" + QueryReq + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id, "ScanPayAddLog");
                                        HelpTools.writelog_fileName("*********** 移动支付 调用Query返回信息：" + queryResStr + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id, "ScanPayAddLog");

                                        JSONObject queryResJson = new JSONObject();
                                        queryResJson = JSON.parseObject(queryResStr);//String转json

                                        String querySuccess = queryResJson.getString("success").toUpperCase(); // TRUE 或 FALSE
                                        String queryStatus = queryResJson.getString("serviceStatus").toUpperCase();
                                        String queryServiceDescription = queryResJson.getString("serviceDescription").toUpperCase();
                                        String trade_no_query = "";
                                        if (queryStatus.equals("008") || queryServiceDescription.equals("已关闭")) { //008,已关闭。    写两个条件的原因是， 防止CRM乱TM改状态、描述
                                            rpp.DeleteHkey(redis_key, hash_key);
                                            HelpTools.writelog_fileName("*********** 支付已关闭,waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id, "ScanPayAddLog");
                                            break;
                                        }

                                        if (queryStatus.equals("NOTPAY") || queryServiceDescription.equals("未支付")) { //NOTPAY,未支付。    写两个条件的原因是， 防止CRM乱TM改状态、描述
                                            HelpTools.writelog_fileName("*********** 订单未支付,waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id, "ScanPayAddLog");
                                            // 设置每隔多长时间执行一次
                                            Thread.sleep(10 * 1000);
                                            continue;
                                        }
                                        if (querySuccess.toUpperCase().equals("TRUE")) {
                                            // 查询支付成功后 填充Pay{RefNo} 银联卡交易流水号
                                            try {
                                                JSONObject queryResJson2 = new JSONObject(new TreeMap<String, Object>());
                                                String queryResStr2 = queryResJson.get("datas").toString();
                                                queryResJson2 = JSONObject.parseObject(queryResStr2);
                                                trade_no_query = queryResJson2.get("trade_no").toString();
                                                //payList.forEach(p -> p.setRefNo(p.getPaySerNum()));
                                                //payList.forEach(p -> p.setPaySerNum(trade_no_query));
                                                for (DCP_OrderPay_OpenReq.level3Elm payItem : payList) {
                                                    if ("#P1".equalsIgnoreCase(payItem.getPayType()) || "#P2".equalsIgnoreCase(payItem.getPayType())) {
                                                        //payItem.setRefNo(payItem.getPaySerNum());
                                                        payItem.setPaySerNum(trade_no_query);
                                                    }
                                                }

                                            } catch (Exception e) {
                                                payList.forEach(p -> p.setRefNo(""));
                                            }

                                            // 查询到支付成功
                                            // 判断订单支付状态 payStatus 是否等于 3 (已付清)。
                                            sql = "select * from DCP_ORDER where EID = '" + eId + "' and ORDERNO = '" + orderNo + "'";
                                            List<Map<String, Object>> getOrderInfo = StaticInfo.dao.executeQuerySQL(sql, null);
                                            String payStatus = "";
                                            if (!CollectionUtils.isEmpty(getOrderInfo)) {
                                                payStatus = getOrderInfo.get(0).get("PAYSTATUS").toString();
                                                if (payStatus.equals("3")) {
                                                    // 已付清 则删除该缓存
                                                    rpp.DeleteHkey(redis_key, hash_key);
                                                    HelpTools.writelog_fileName("***********  移动支付 DCP_OrderPay_Open 订单已付清，waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id, "ScanPayAddLog");
                                                    break;
                                                } else {
                                                    // 未付清 调用 订金补录，成功后 删除该缓存
                                                    JSONObject modify_PayRequest = new JSONObject();
                                                    modify_PayRequest.put("serviceId", "DCP_OrderModify_PayAdd_Open");
                                                    modify_PayRequest.put("version", "3.0");
                                                    modify_PayRequest.put("langType", "zh_CN");

                                                    JSONObject modify_PayAddReq = new JSONObject();
                                                    modify_PayAddReq.put("orderNo", orderNo);
                                                    modify_PayAddReq.put("workNo", workNo);
                                                    modify_PayAddReq.put("squadNo", squadNo);
                                                    modify_PayAddReq.put("shopId", shopid);
                                                    modify_PayAddReq.put("machineNo", machineNo);
                                                    modify_PayAddReq.put("opNo", opNo);
                                                    modify_PayAddReq.put("opName", opName);
                                                    modify_PayAddReq.put("loadDocType", loadDocType);
//                                                            modify_PayAddReq.put("pay", payJson);
                                                    modify_PayAddReq.put("pay", payList);
                                                    modify_PayRequest.put("request", modify_PayAddReq);

                                                    String payRequestStr = modify_PayAddReq.toString();
                                                    String modify_PaySign = PosPub.encodeMD5(payRequestStr + sign);

                                                    JSONObject modify_PaySignJson = new JSONObject();
                                                    modify_PaySignJson.put("sign", modify_PaySign);
                                                    modify_PaySignJson.put("key", sign);

                                                    modify_PayRequest.put("sign", modify_PaySignJson);

                                                    //********** 已经准备好 DCP_OrderModify_PayAdd 的json，开始调用 *************
                                                    HelpTools.writelog_fileName("*********** 移动支付 调用 DCP_OrderModify_PayAdd_Open 接口信息：地址（" + dcpUrl + "）  请求Json：" + modify_PayRequest + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id, "ScanPayAddLog");
                                                    String modify_PayResStr = HttpSend.Sendcom(modify_PayRequest.toString(), dcpUrl).trim();
                                                    HelpTools.writelog_fileName("*********** 移动支付 调用 DCP_OrderModify_PayAdd_Open 返回信息：" + modify_PayResStr + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id, "ScanPayAddLog");

                                                    JSONObject modify_PayResJson = new JSONObject();
                                                    modify_PayResJson = JSON.parseObject(modify_PayResStr);//String转json

                                                    String modify_PaySuccess = modify_PayResJson.getString("success").toUpperCase(); // TRUE 或 FALSE
                                                    String modify_PayStatus = modify_PayResJson.getString("serviceStatus").toUpperCase();
                                                    String modify_PayServiceDescription = modify_PayResJson.getString("serviceDescription").toUpperCase();

                                                    if ("true".equalsIgnoreCase(modify_PaySuccess))
                                                    {
                                                        // 定金补录成功
                                                        rpp.DeleteHkey(redis_key, hash_key);
                                                        HelpTools.writelog_fileName("*********** 定金补录成功，删除该缓存，redis_key=" + redis_key + ",hash_key=" + hash_key + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id, "ScanPayAddLog");
                                                        break;
                                                    }
                                                    else
                                                    {
                                                        //Thread.sleep(10 * 1000);
                                                        //continue;
                                                        //订金增加接口失败后，需要撤销移动支付

                                                        String ret_order_id = PosPub.getGUID(false);//退款单号


                                                        com.alibaba.fastjson.JSONObject RefundPayReq = new com.alibaba.fastjson.JSONObject();
                                                        RefundPayReq.put("serviceId", "Refund");

                                                        com.alibaba.fastjson.JSONObject payReq_refund= new com.alibaba.fastjson.JSONObject();
                                                        payReq_refund.put("pay_type", pay_type);
                                                        payReq_refund.put("shop_code", shop_code);
                                                        payReq_refund.put("pos_code", pos_code);
                                                        payReq_refund.put("order_id", prepay_id);
                                                        payReq_refund.put("trade_no", trade_no_query);
                                                        payReq_refund.put("ret_order_id", ret_order_id);
                                                        payReq_refund.put("pay_amt", pay_amt);
                                                        payReq_refund.put("return_amount", pay_amt);

                                                        RefundPayReq.put("request", payReq_refund);

                                                        String reqStr = payReq_refund.toString();
                                                        sign = PosPub.encodeMD5(reqStr + appid);

                                                        com.alibaba.fastjson.JSONObject signJson = new com.alibaba.fastjson.JSONObject();
                                                        signJson.put("sign", sign);
                                                        signJson.put("key", appid);

                                                        RefundPayReq.put("sign", signJson);
                                                        String refundPayReqStr = RefundPayReq.toString();
                                                        HelpTools.writelog_waimai("移动支付 调用 DCP_OrderModify_PayAdd_Open订金增加接口失败,需要撤销移动支付，调用Refund接口，【组装】请求req：" + refundPayReqStr
                                                                + ",单号orderNo="+orderNo+",原支付单号order_id="+prepay_id+",原交易单号trade_no="+trade_no_query+",外卖单号orderNo="+orderNo);
                                                        HelpTools.writelog_fileName("移动支付 调用 DCP_OrderModify_PayAdd_Open订金增加接口失败,需要撤销移动支付，调用Refund接口，【组装】请求req：" + refundPayReqStr
                                                                + ",单号orderNo="+orderNo+",原支付单号order_id="+prepay_id+",原交易单号trade_no="+trade_no_query+",外卖单号orderNo="+orderNo, "ScanPayAddLog");

                                                        String payResStr = HttpSend.Sendcom(refundPayReqStr, crmPayUrl);
                                                        HelpTools.writelog_waimai("移动支付 调用 DCP_OrderModify_PayAdd_Open订金增加接口失败,需要撤销移动支付，调用Refund接口，返回res:" + payResStr + ",原支付单号order_id="+prepay_id+",原交易单号trade_no="+trade_no_query+",外卖单号orderNo="+orderNo);
                                                        HelpTools.writelog_fileName("移动支付 调用 DCP_OrderModify_PayAdd_Open订金增加接口失败,需要撤销移动支付，调用Refund接口，返回res:" + payResStr + ",单号orderNo="+",原支付单号order_id="+prepay_id+",原交易单号trade_no="+trade_no_query+",外卖单号orderNo="+orderNo, "ScanPayAddLog");
                                                        com.alibaba.fastjson.JSONObject payResJson = new com.alibaba.fastjson.JSONObject();
                                                        payResJson = JSON.parseObject(payResStr);//String转json
                                                        String paySuccess = payResJson.getOrDefault("success","").toString(); // TRUE 或 FALSE
                                                        //CRM 接口提示： 返回false时，并不表示一定是创建支付失败，有可能顾客还在支付中，需判断serviceStatus
                                                        String payServiceStatus = payResJson.getOrDefault("serviceStatus","").toString();
                                                        String payServiceDescription = payResJson.getOrDefault("serviceDescription","").toString();
                                                        if (!"true".equalsIgnoreCase(paySuccess))
                                                        {
                                                            HelpTools.writelog_fileName("移动支付 调用 DCP_OrderModify_PayAdd_Open订金增加接口失败,需要撤销移动支付，调用Refund接口，返回失败:"+payServiceDescription+",单号orderNo="+",原支付单号order_id="+prepay_id+",原交易单号trade_no="+trade_no_query+",外卖单号orderNo="+orderNo, "ScanPayAddLog");
                                                            HelpTools.writelog_waimai("移动支付 调用 DCP_OrderModify_PayAdd_Open订金增加接口失败,需要撤销移动支付，调用Refund接口，返回失败:"+payServiceDescription+",单号orderNo="+",原支付单号order_id="+prepay_id+",原交易单号trade_no="+trade_no_query+",外卖单号orderNo="+orderNo);
                                                            //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, payServiceDescription);
                                                        }
                                                        break;

                                                    }
                                                }
                                            } else {
                                                HelpTools.writelog_fileName("*********** 定金补录失败" + orderNo + "订单不存在", "ScanPayAddLog");
                                                Thread.sleep(10 * 1000);
                                                continue;
                                            }
                                        } else {
                                            Thread.sleep(10 * 1000);
                                            continue;
                                        }

                                    } catch (Exception e) {
                                        try {
                                            StringWriter errors = new StringWriter();
                                            PrintWriter pw = new PrintWriter(errors);
                                            e.printStackTrace(pw);

                                            pw.flush();
                                            pw.close();

                                            errors.flush();
                                            errors.close();

                                            logger.error("\r\nrequestId=" + req.getRequestId() + ",plantType=" + req.getPlantType() + ",version=" + req.getVersion() + "," + "******移动支付 DCP_OrderPay_Open 报错信息" + e.getMessage() + "\r\n" + errors.toString() + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id + "\r\n");
                                            HelpTools.writelog_fileName(
                                                    "***********移动支付DCP_OrderPay_Open异常：" + e.getMessage() + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id,
                                                    "ScanPayAddLog");
                                            pw = null;
                                            errors = null;
                                        } catch (IOException e1) {
                                            logger.error("\r\nrequestId=" + req.getRequestId() + ",plantType=" + req.getPlantType() + ",version=" + req.getVersion() + "," + "******移动支付 DCP_OrderPay_Open 报错信息" + e.getMessage() + ",waimai单号=" + orderNo + ",支付单号order_id=" + prepay_id + "\r\n");
                                        }
                                    }
                                }
                            }

                        }).start();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        res.setSuccess(false);
                        res.setServiceStatus("200");
                        res.setServiceDescription("服务执行失败:" + e.getMessage());

                    }

                }
            }
            else {
                JSONObject modify_PayRequest = new JSONObject();
                modify_PayRequest.put("serviceId", "DCP_OrderModify_PayAdd_Open");
                modify_PayRequest.put("version", "3.0");
                modify_PayRequest.put("langType", "zh_CN");

                JSONObject modify_PayAddReq = new JSONObject();
                modify_PayAddReq.put("orderNo", orderNo);
                modify_PayAddReq.put("workNo", workNo);
                modify_PayAddReq.put("squadNo", squadNo);
                modify_PayAddReq.put("shopId", shopId);
                modify_PayAddReq.put("machineNo", machineNo);
                modify_PayAddReq.put("opNo", opNo);
                modify_PayAddReq.put("opName", opName);
                modify_PayAddReq.put("loadDocType", loadDocType);
                modify_PayAddReq.put("pay", pay);

                modify_PayRequest.put("request", modify_PayAddReq);

                String payRequestStr = modify_PayAddReq.toString();
                String modify_PaySign = PosPub.encodeMD5(payRequestStr + appid);

                JSONObject modify_PaySignJson = new JSONObject();
                modify_PaySignJson.put("sign", modify_PaySign);
                modify_PaySignJson.put("key", appid);

                modify_PayRequest.put("sign", modify_PaySignJson);

                //********** 已经准备好 DCP_OrderModify_PayAdd 的json，开始调用 *************
                HelpTools.writelog_fileName("*********** 移动支付 调用 DCP_OrderModify_PayAdd_Open 接口信息：地址（" + dcpUrl + "）  请求Json：" + modify_PayRequest + ",单号orderNO=" + orderNo, "ScanPayAddLog");
                String modify_PayResStr = HttpSend.Sendcom(modify_PayRequest.toString(), dcpUrl).trim();
                HelpTools.writelog_fileName("*********** 移动支付 调用 DCP_OrderModify_PayAdd_Open 返回信息：" + modify_PayResStr + ",单号orderNO=" + orderNo, "ScanPayAddLog");

                JSONObject modify_PayResJson = new JSONObject();
                modify_PayResJson = JSON.parseObject(modify_PayResStr);//String转json

                String modify_PaySuccess = modify_PayResJson.getOrDefault("success", "").toString(); // TRUE 或 FALSE
                String modify_PayStatus = modify_PayResJson.getOrDefault("serviceStatus", "").toString();
                String modify_PayServiceDescription = modify_PayResJson.getOrDefault("serviceDescription", "").toString();
                boolean isSuc = false;
                if (("true".equalsIgnoreCase(modify_PaySuccess)))
                {
                    isSuc = true;
                    // 定金补录成功
                    HelpTools.writelog_fileName("*********** 会员卡支付 定金补录成功,单号orderNO=" + orderNo, "ScanPayAddLog");
                }
                else
                {
                    HelpTools.writelog_fileName("*********** 会员卡支付 定金补录失败,单号orderNO=" + orderNo, "ScanPayAddLog");
                }

                res.setSuccess(isSuc);
                res.setServiceStatus(modify_PayStatus);
                res.setServiceDescription(modify_PayServiceDescription);
                return;
            }

        } catch (Exception e) {
            // TODO: handle exception
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败：" + e.getMessage());
        }finally {
            rpp.DeleteKey(checkKey);
        }
        

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderPay_OpenReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderPay_OpenReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderPay_OpenReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrderPay_OpenReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        DCP_OrderPay_OpenReq.level1Elm request = req.getRequest();
        String orderNo = request.getOrderNo();
        String orgType = request.getOrgType();
        String loadDocType = request.getLoadDocType();
        String openId = request.getOpenId();

        List<level3Elm> pay = request.getPay();

        if (Check.Null(orderNo)) {
            errMsg.append("业务订单号不可为空值, ");
            isFail = true;
        }

        if (Check.Null(orgType)) {
            errMsg.append("组织类型不可为空值, ");
            isFail = true;
        }

        if (Check.Null(loadDocType)) {
            errMsg.append("订单类型不可为空值, ");
            isFail = true;
        }

        if (Check.Null(openId)) {
            errMsg.append("第三方应用ID不可为空值, ");
            isFail = true;
        }

        if (CollectionUtils.isEmpty(pay)) {
            errMsg.append("会员支付pay节点不能为空, ");
            isFail = true;
        }else
        {
        	 for(level3Elm payData :pay)
        	 {
        		 if(payData.getFuncNo().equals("301"))
        		 {
        			 if(Check.Null(payData.getCtType()))
        			 {
        				 errMsg.append("卡支付卡类型[ctType]值不能为空, ");
        				 isFail = true;
        			 }
        		 } 
    			 if (payData.getFuncNo().equals("304") || payData.getFuncNo().equals("305")|| payData.getFuncNo().equals("307"))//现金券/折扣券
    			 {
        			 if(Check.Null(payData.getCtType()))
        			 {
        				 errMsg.append("券支付券类型[ctType]值不能为空, ");
        				 isFail = true;
        			 }
    			 }
        	 }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;

    }

    @Override
    protected TypeToken<DCP_OrderPay_OpenReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_OrderPay_OpenReq>() {
        };
    }

    @Override
    protected DCP_OrderPay_OpenRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_OrderPay_OpenRes();
    }

}
