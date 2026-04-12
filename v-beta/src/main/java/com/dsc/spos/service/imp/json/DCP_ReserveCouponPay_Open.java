package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ReserveCouponPay_OpenReq;
import com.dsc.spos.json.cust.res.DCP_ReserveCouponPay_OpenRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: 会员体验券核销
 * @author: wangzyc
 * @create: 2021-08-03
 */
public class DCP_ReserveCouponPay_Open extends SPosAdvanceService<DCP_ReserveCouponPay_OpenReq, DCP_ReserveCouponPay_OpenRes> {
    @Override
    protected void processDUID(DCP_ReserveCouponPay_OpenReq req, DCP_ReserveCouponPay_OpenRes res) throws Exception {
        String eId = req.geteId();
        DCP_ReserveCouponPay_OpenReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        String memberId = request.getMemberId();
        String couponType = request.getCouponType();
        String reserveNo = request.getReserveNo();
        String opNo = request.getOpNo();
        String quantity = request.getQuantity();
        if (Check.Null(quantity)) {
            quantity = "1";
        }
        String couponCode = request.getCouponCode();

        String appid = req.getApiUserCode();

        res.setDatas(res.new level1Elm());

        try {
            // 先查下预约单存不存在
            String checkSql = "select * from DCP_RESERVE where eid = '"+eId+"' and SHOPID = '"+shopId+"' and RESERVENO = '"+reserveNo+"'";
            List<Map<String, Object>> checkReserveNo = this.doQueryData(checkSql, null);
            if(CollectionUtils.isEmpty(checkReserveNo)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "当前单号为:"+reserveNo+"的订单不存在！");
            }else{
                // 预约单A可以去扫预约单B的小程序码核销券 针对这种场景 增加预约单和券号的检核
                String couponCodeDB = checkReserveNo.get(0).get("COUPONCODE").toString();
                if(!couponCodeDB.equals(couponCode)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "当前单号为:"+reserveNo+"的订单所使用的券码不匹配！");
                }
            }

            String memberUrl = PosPub.getCRM_INNER_URL(eId);
            if (memberUrl.trim().equals("")) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "移动支付接口参数未设置!");
            }

            String couponPayNo=PosPub.getGUID(false);//调用CouponPay的orderno

            com.alibaba.fastjson.JSONArray couponlistArray = new com.alibaba.fastjson.JSONArray();
            JSONObject tempCoupon = new JSONObject(new TreeMap<String, Object>());
            tempCoupon.put("couponCode", couponCode);//券号
            tempCoupon.put("couponType", couponType);//券类型
            tempCoupon.put("quantity", quantity);//使用张数
            couponlistArray.add(tempCoupon);

            JSONObject couponPayReq = new JSONObject();
            couponPayReq.put("serviceId", "CouponPay");

            JSONObject cpReq = new JSONObject();
            cpReq.put("coupons", couponlistArray);
            cpReq.put("orderNo", couponPayNo);
            cpReq.put("orgType", "2");       // 1=公司 2=门店 3=渠道
            cpReq.put("orgId", shopId);

            couponPayReq.put("request", cpReq);

            String cpReqStr = cpReq.toString();
            String cpSign = PosPub.encodeMD5(cpReqStr + req.getApiUser().getUserKey());

            JSONObject cpSignJson = new JSONObject();
            cpSignJson.put("sign", cpSign);
            cpSignJson.put("key", appid);

            couponPayReq.put("sign", cpSignJson);
            //********** 已经准备好 CouponPay 的json，开始调用 *************
            String s = couponPayReq.toString();
            String cpResStr = HttpSend.Sendcom(couponPayReq.toString(), memberUrl).trim();
            HelpTools.writelog_fileName("***********会员体验券核销 DCP_ReserveCouponPay_Open 调用 CouponPay 接口，请求json：" + couponPayReq.toString()
                    + "*************", "DCP_ReserveCouponPay_Open");

            JSONObject cpResJson = new JSONObject();
            cpResJson = JSON.parseObject(cpResStr);//String转json
            HelpTools.writelog_fileName("***********会员体验券核销 DCP_ReserveCouponPay_Open 调用 CouponPay 接口返回信息：" + cpResStr + "*************", "DCP_ReserveCouponPay_Open");
            String cpSuccess = cpResJson.getString("success").toUpperCase(); // TRUE 或 FALSE
            //CRM 接口提示： 返回false时，并不表示一定是创建支付失败，有可能顾客还在支付中，需判断serviceStatus
            String cpsServiceStatus = cpResJson.getString("serviceStatus").toUpperCase();
            String cpsServiceDescription = cpResJson.getString("serviceDescription").toUpperCase();

            if (!Check.Null(cpSuccess) && cpSuccess.equals("TRUE")) {
                JSONObject std_data_res = cpResJson.getJSONObject("datas");
                String trade_no = std_data_res.getString("trade_no");

                String sql = "select * from DCP_RESERVE where eid = '" + eId + "' and SHOPID = '" + shopId + "' and RESERVENO = '" + reserveNo + "'";
                List<Map<String, Object>> data = this.doQueryData(sql, null);
                if (!CollectionUtils.isEmpty(data)) {
                    Map<String, Object> map = data.get(0);
                    String loaddoctype = map.get("LOADDOCTYPE").toString();
                    String itemsno = map.get("ITEMSNO").toString();
                    String MEMBERID = map.get("MEMBERID").toString();
                    String OPNO = map.get("OPNO").toString();

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String lastmodiopId = req.getOpNO();
                    String lastmodiopName = req.getOpName();
                    Date time = new Date();
                    String lastmoditime = df.format(time);

                    try {
                        String[] column = {"EID", "SHOPID", "MEMBERID", "RESERVENO", "ITEMSNO", "OPNO", "LOADDOCTYPE", "COUPONCODE",
                                "TRADENO", "CREATEOPID", "CREATEOPNAME", "CREATETIME"};

                        // 如果是到店分配，预约单里会存在无opNo；核销三步：
                        //1、券核销，2、回写预约单状态（若opNo为空，则回写），3、同步服务记录
                        UptBean ub1 = null;
                        ub1 = new UptBean("DCP_RESERVE");
                        ub1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
                        if (Check.Null(OPNO) && !Check.Null(request.getOpNo())) {
                            ub1.addUpdateValue("OPNO", new DataValue(request.getOpNo(), Types.VARCHAR));
                        }
                        ub1.addCondition("RESERVENO", new DataValue(reserveNo, Types.VARCHAR));
                        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));

                        this.addProcessData(new DataProcessBean(ub1));

                        DataValue[] insValuele = {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(shopId, Types.VARCHAR),
                                new DataValue(MEMBERID, Types.VARCHAR),
                                new DataValue(reserveNo, Types.VARCHAR),
                                new DataValue(itemsno, Types.VARCHAR),
                                new DataValue(opNo, Types.VARCHAR),
                                new DataValue(loaddoctype, Types.VARCHAR),
                                new DataValue(couponCode, Types.VARCHAR),
                                new DataValue(trade_no, Types.VARCHAR),
                                new DataValue(lastmodiopId, Types.VARCHAR),
                                new DataValue(lastmodiopName, Types.VARCHAR),
                                new DataValue(lastmoditime, Types.DATE)
                        };
                        InsBean ins = new InsBean("DCP_MEMBERSERVICE", column);
                        ins.addValues(insValuele);
                        this.addProcessData(new DataProcessBean(ins));

                        this.doExecuteDataToDB();

                        DCP_ReserveCouponPay_OpenRes.level1Elm level1Elm = res.new level1Elm();
                        level1Elm.setOrderNo(couponPayNo);
                        level1Elm.setTrade_no(trade_no);
                        res.setDatas(level1Elm);

                        res.setSuccess(true);
                        res.setServiceStatus("000");
                        res.setServiceDescription("服务执行成功");
                    } catch (Exception e) {
                        // 若券核销成功生成服务记录失败，则调用CouponPayReverse进行券撤销，并返回提示：生成服务记录失败，请重试！
                        JSONObject couponPayRReq = new JSONObject();
                        couponPayRReq.put("serviceId", "CouponPayReverse");

                        JSONObject cprReq = new JSONObject();
                        cprReq.put("orderNo", couponPayNo);
                        cprReq.put("trade_no", "");

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
                        HelpTools.writelog_fileName("***********会员体验券核销 DCP_ReserveCouponPay_Open 调用 CouponPayReverse 接口，请求json：" + couponPayRReq.toString()
                                + "*************", "DCP_ReserveCouponPay_Open");

                        JSONObject cprResJson = new JSONObject();
                        cprResJson = JSON.parseObject(cprResStr);//String转json
                        HelpTools.writelog_fileName("***********会员体验券核销 DCP_ReserveCouponPay_Open 调用 CouponPayReverse 接口返回信息：" + cprResStr + "*************", "DCP_ReserveCouponPay_Open");
                        String cprSuccess = cprResJson.getString("success").toUpperCase(); // TRUE 或 FALSE
                        //CRM 接口提示： 返回false时，并不表示一定是创建支付失败，有可能顾客还在支付中，需判断serviceStatus
                        String cpsrServiceStatus = cprResJson.getString("serviceStatus").toUpperCase();
                        String cpsrServiceDescription = cprResJson.getString("serviceDescription").toUpperCase();

                        if (!Check.Null(cprSuccess) && cprSuccess.equals("TRUE")) {
                            res.setSuccess(false);
                            res.setServiceStatus("100");
                            res.setServiceDescription("生成服务记录失败，请重试！");
                        }
                    }

                }

            } else {
                // 核销失败
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, cpsServiceDescription);
            }
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReserveCouponPay_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReserveCouponPay_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReserveCouponPay_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReserveCouponPay_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ReserveCouponPay_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(request.getShopId())) {
            errMsg.append("所属门店不能为空, ");
            isFail = true;
        }

        if (Check.Null(request.getCouponCode())) {
            errMsg.append("券号不能为空, ");
            isFail = true;
        }
        if (Check.Null(request.getReserveNo())) {
            errMsg.append("预约单号不能为空, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ReserveCouponPay_OpenReq> getRequestType() {
        return new TypeToken<DCP_ReserveCouponPay_OpenReq>() {
        };
    }

    @Override
    protected DCP_ReserveCouponPay_OpenRes getResponseType() {
        return new DCP_ReserveCouponPay_OpenRes();
    }
}
