package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ReserveOrderCreate_OpenReq;
import com.dsc.spos.model.ApiUser;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.res.DCP_ReserveOrderCreate_OpenRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: 预约单创建
 * @author: wangzyc
 * @create: 2021-08-03
 */
public class DCP_ReserveOrderCreate_Open extends SPosAdvanceService<DCP_ReserveOrderCreate_OpenReq, DCP_ReserveOrderCreate_OpenRes> {
    @Override
    protected void processDUID(DCP_ReserveOrderCreate_OpenReq req, DCP_ReserveOrderCreate_OpenRes res) throws Exception {
        String eId = req.geteId();
        DCP_ReserveOrderCreate_OpenReq.level1Elm request = req.getRequest();
        try {

            if(checkMaxReserve(req)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "当前时段门店预约人数已到达上限，请换个时间段再预约！");
            }

            // 新增管控逻辑-相同会员ID预约单已存在的相同时间段且status=0或1的单据时，报错提示：您已预约相同时间段的服务，不可继续预约；
            if(checkMemberReserve(req)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "您已预约相同时间段的服务，不可继续预约！");
            }



            /**
             * 1.生成预约单号
             */
            String reserveNo = autoReserveNo(req);
            String couponCode = request.getCouponCode();

            if(!Check.Null(couponCode)){
                if(checkCouponCode(req)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该体验券已被使用，不可继续使用！");
                }
            }
            String itemsNo = request.getItemsNo();
            String shopId = request.getShopId();
            String memberId = request.getMemberId();

            String appKey = req.getApiUserCode();
            String appid = req.getApiUser().getUserKey();
            String appType = req.getApiUser().getAppType();
            String mobile = request.getMobile();

            StringBuffer sqlbuf = new StringBuffer("");
            // DCP_RESERVE
            String[] columns = {
                    "EID", "SHOPID", "RESERVENO", "LOADDOCTYPE", "STATUS", "ITEMSNO", "SHOPDISTRIBUTION", "OPNO", "MEMBERID", "NAME",
                    "MOBILE", "BDATE", "\"TIME\"", "MEMO", "ISEVALUATE", "COUPONCODE", "CREATEOPID", "CREATEOPNAME", "CREATETIME", "LASTMODIOPID",
                    "LASTMODIOPNAME", "LASTMODITIME"
            };
            // 获取下 门店的预约设置
            sqlbuf.append("SELECT a.COUPONTYPEID, a.QTY,a.RESERVETYPE FROM DCP_SERVICEITEMS a where a.eid = '"+eId+"' and a.ITEMSNO = '"+itemsNo+"'");
            List<Map<String, Object>> getCouponTypeDetail = this.doQueryData(sqlbuf.toString(), null);
            sqlbuf.setLength(0);
            sqlbuf.append("SELECT RESERVEAUDIT, SMSALERTS FROM DCP_RESERVEPARAMETER where eid = '"+eId+"' AND SHOPID  = '"+shopId+"'");
            List<Map<String, Object>> getReserveparameter = this.doQueryData(sqlbuf.toString(), null);
            String smsalerts = ""; // 短信提醒Y/N   默认为Y
            String reserveaudit = ""; // 预约审核Y/N 默认N
            String coupontypEid = ""; // 券类型
            String cqty = ""; // 赠送券数量
            String reservetype = ""; // 支持预约方式 free：免费预约 coupon：用券预约 pay：支付后预约
            if(!CollectionUtils.isEmpty(getCouponTypeDetail)){
                Map<String, Object> map = getCouponTypeDetail.get(0);
                coupontypEid = map.get("COUPONTYPEID").toString();
                cqty = map.get("QTY").toString();
                reservetype = map.get("RESERVETYPE").toString();
            }

            if (!CollectionUtils.isEmpty(getReserveparameter)) {
                Map<String, Object> map = getReserveparameter.get(0);
                smsalerts = map.get("SMSALERTS").toString();
                if(Check.Null(smsalerts)){
                    smsalerts = "Y";
                }
                reserveaudit = map.get("RESERVEAUDIT").toString();
                if(Check.Null(reserveaudit)){
                    reserveaudit = "N";
                }
            }

            if("pay".equals(reservetype)||"coupon".equals(reservetype)){
                if(Check.Null(couponCode)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "预约失败,当前项目不支持无券预约！");
                }

                sqlbuf.setLength(0);
                sqlbuf.append("select COUPONCODE,COUPONTYPEID from CRM_COUPON where EID = ? and COUPONCODE = ?");
                List<Map<String, Object>> getCoupTypeEID = this.doQueryData(sqlbuf.toString(), new String[]{eId,couponCode});
                if(!CollectionUtils.isEmpty(getCoupTypeEID)){
                    String coupontypeid = getCoupTypeEID.get(0).get("COUPONTYPEID").toString();
                    if(!coupontypEid.equals(coupontypeid)){
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "预约失败,当前券类型不匹配！");
                    }
                }else {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "预约失败,当前券不存在！");
                }
            }


            String status = "1";
            // 判断若DCP_RESERVEPARAMETER.RESERVEAUDIT==Y，则创建预约单status==0.待审核；
            if (reserveaudit.equals("Y")) {
                status = "0";
            }

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String lastmodiopId = req.getOpNO();
            String lastmodiopName = req.getOpName();
            Date time = new Date();
            String lastmoditime = df.format(time);


            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

            String memberUrl = "";
            /**
             * 2. 券逻辑：
             * 1、送券：若couponCode未传值，根据[项目编号]查询DCP_SERVICEITEMS.COUPONTYPEID，
             * 调用CRM接口CouponPromSend发放电子券（注意数量），同步处理券码存库，若预约单创建失败则调用CouponPromSendReverse撤销发放；
             * 2、券核销：若couponCode传值，则直接存库&更新CRM_COUPON.STATUS==1；
             */
            if (Check.Null(couponCode)&&"free".equals(reservetype)&&!Check.Null(cqty)) {
                try {
                    sqlbuf.setLength(0);
                    sqlbuf.append("SELECT a.COUPONTYPEID, a.QTY, b.FACEAMOUNT " +
                            " FROM DCP_SERVICEITEMS a " +
                            " LEFT JOIN CRM_COUPONTYPE b ON a.eid = b.EID AND a.COUPONTYPEID = b.COUPONTYPEID " +
                            " WHERE a.eid = '" + eId + "' AND a.itemsNo = '" + itemsNo + "' AND a.status = '100'");
                    List<Map<String, Object>> datas = this.doQueryData(sqlbuf.toString(), null);
                    if (!CollectionUtils.isEmpty(datas)) {
                        Map<String, Object> data = datas.get(0);
                        String coupontypeid = data.get("COUPONTYPEID").toString();
                        String qty = data.get("QTY").toString();
                        String faceamount = data.get("FACEAMOUNT").toString();

                        memberUrl = PosPub.getCRM_INNER_URL(eId);
                        if (memberUrl.trim().equals("")) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "移动支付接口参数未设置!");
                        }

                        JSONObject couponPromSendReq = new JSONObject();
                        couponPromSendReq.put("serviceId", "CouponPromSend");

                        JSONObject cpsReq = new JSONObject();
                        cpsReq.put("memberId", memberId);    //  会员号
                        cpsReq.put("openId", "");             // 粉丝openId
                        cpsReq.put("activeId", "");           // 活动ID
                        cpsReq.put("orgType", "2");           // 推广机构类型
                        cpsReq.put("orgId", shopId);           // 推广机构
                        cpsReq.put("terminalId", "");           // 终端设备号
                        cpsReq.put("orderNo", reserveNo);        // 终商户唯一订单号
                        cpsReq.put("couponType", coupontypeid);  // 券类型
                        cpsReq.put("couponAmount", faceamount);  // 券面值
                        cpsReq.put("couponQty", qty);           // 张数

                        couponPromSendReq.put("request", cpsReq);

                        String cpsReqStr = cpsReq.toString();
                        String cpsSign = PosPub.encodeMD5(cpsReqStr + appid);

                        JSONObject cpsSignJson = new JSONObject();
                        cpsSignJson.put("sign", cpsSign);
                        cpsSignJson.put("key", appKey);

                        couponPromSendReq.put("sign", cpsSignJson);
                        //********** 已经准备好 CouponPromSend 的json，开始调用 *************
                        String s = couponPromSendReq.toString();
                        String cpsResStr = HttpSend.Sendcom(couponPromSendReq.toString(), memberUrl).trim();
                        HelpTools.writelog_fileName("***********预约单创建 DCP_ReserveOrderCreate 调用 CouponPromSend 接口，请求json：" + couponPromSendReq.toString()
                                + "*************", "DCP_ReserveOrderCreate");

                        JSONObject cpsResJson = new JSONObject();
                        cpsResJson = JSON.parseObject(cpsResStr);//String转json

                        HelpTools.writelog_fileName("***********预约单创建 DCP_ReserveOrderCreate 调用 CouponPromSend 接口返回信息：" + cpsResStr + "*************", "DCP_ReserveOrderCreate");
                        String cpsSuccess = cpsResJson.getString("success").toUpperCase(); // TRUE 或 FALSE
                        //CRM 接口提示： 返回false时，并不表示一定是创建支付失败，有可能顾客还在支付中，需判断serviceStatus
                        String cpsServiceStatus = cpsResJson.getString("serviceStatus").toUpperCase();
                        String cpsServiceDescription = cpsResJson.getString("serviceDescription").toUpperCase();

                        boolean isSuc = false;
                        if (!Check.Null(cpsSuccess) && cpsSuccess.equals("TRUE")) {
                            isSuc = true;
                        }

                        if (isSuc) {
                            JSONArray resDatas = cpsResJson.getJSONArray("datas");
                            for (int i = 0; i < resDatas.size(); i++) {
                                // 拿到随机一个券 然后存库
                                JSONObject jsonObject = resDatas.getJSONObject(i);
                                couponCode = jsonObject.getString("couponCode");
                                break;
                            }
                            // 调用成功后存库
                            try {
                                // 如果有传券码则 直接存库
                                DataValue[] insValue1 = new DataValue[]
                                        {
                                                new DataValue(eId, Types.VARCHAR),
                                                new DataValue(shopId, Types.VARCHAR),
                                                new DataValue(reserveNo, Types.VARCHAR),
                                                new DataValue(appType, Types.VARCHAR),
                                                new DataValue(status, Types.VARCHAR),
                                                new DataValue(itemsNo, Types.VARCHAR),
                                                new DataValue(request.getShopDistribution(), Types.VARCHAR),
                                                new DataValue(request.getOpNo(), Types.VARCHAR),
                                                new DataValue(request.getMemberId(), Types.VARCHAR),
                                                new DataValue(request.getName(), Types.VARCHAR),
                                                new DataValue(request.getMobile(), Types.VARCHAR),
                                                new DataValue(request.getDate(), Types.DATE),
                                                new DataValue(request.getTime(), Types.VARCHAR),
                                                new DataValue(request.getMemo(), Types.VARCHAR),
                                                new DataValue("", Types.VARCHAR), // ISEVALUATE 是否评价Y/空
                                                new DataValue(couponCode, Types.VARCHAR),
                                                new DataValue(request.getCreateOpId(), Types.VARCHAR),
                                                new DataValue(request.getCreateOpName(), Types.VARCHAR),
                                                new DataValue(lastmoditime, Types.DATE),
                                                new DataValue(request.getCreateOpId(), Types.VARCHAR),
                                                new DataValue(request.getCreateOpName(), Types.VARCHAR),
                                                new DataValue(lastmoditime, Types.DATE)
                                        };
                                InsBean ib1 = new InsBean("DCP_RESERVE", columns);
                                ib1.addValues(insValue1);
                                this.addProcessData(new DataProcessBean(ib1));
                                this.doExecuteDataToDB();

                                DCP_ReserveOrderCreate_OpenRes.level1Elm level1Elm = res.new level1Elm();
                                level1Elm.setReserveNo(reserveNo);
                                res.setDatas(level1Elm);
                            } catch (Exception e) {
                                // 若预约单创建失败则调用CouponPromSendReverse撤销发放；
                                JSONObject CouponPromSendReverseReq = new JSONObject();
                                CouponPromSendReverseReq.put("serviceId", "CouponPromSendReverse");

                                JSONObject cpsrReq = new JSONObject();
                                cpsrReq.put("orderNo", reserveNo);  //  商户唯一订单号
                                cpsrReq.put("trade_no", "");

                                CouponPromSendReverseReq.put("request", cpsrReq);

                                String cpsrReqStr = cpsrReq.toString();
                                String cpsrSign = PosPub.encodeMD5(cpsrReqStr + appid);

                                JSONObject cpsrSignJson = new JSONObject();
                                cpsrSignJson.put("sign", cpsrSign);
                                cpsrSignJson.put("key", appKey);

                                CouponPromSendReverseReq.put("sign", cpsrSignJson);

                                //********** 已经准备好 CouponPromSendReverse 的json，开始调用 *************
                                String s2 = CouponPromSendReverseReq.toString();
                                String cpsrResStr = HttpSend.Sendcom(CouponPromSendReverseReq.toString(), memberUrl).trim();
                                HelpTools.writelog_fileName("***********预约单创建 DCP_ReserveOrderCreate 调用 CouponPromSendReverse 接口，请求json：" + CouponPromSendReverseReq.toString()
                                        + "*************", "DCP_ReserveOrderCreate");

                                JSONObject cpsrResJson = new JSONObject();
                                cpsrResJson = JSON.parseObject(cpsrResStr);//String转json

                                HelpTools.writelog_fileName("***********预约单创建 DCP_ReserveOrderCreate 调用 CouponPromSendReverse 接口返回信息：" + cpsrResStr + "*************", "DCP_ReserveOrderCreate");
                                String cpsrSuccess = cpsrResJson.getString("success").toUpperCase(); // TRUE 或 FALSE

                                if (!Check.Null(cpsrSuccess) && cpsrSuccess.equals("TRUE")) {
                                    res.setSuccess(false);
                                    res.setServiceStatus("100");
                                    res.setServiceDescription("生成预约单失败，请重新发起预约!");
                                }
                            }
                        }else {
                            res.setSuccess(false);
                            res.setServiceStatus(cpsServiceStatus);
                            res.setServiceDescription(cpsServiceDescription);
                        }
                    }
                } catch (Exception e) {
                    res.setSuccess(false);
                    res.setServiceStatus("100");
                    res.setServiceDescription("发放电子券失败，请重新发起预约!");
                }
            } else {
//                // 如果有传券码则 直接存库
                DataValue[] insValue1 = new DataValue[]
                        {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(shopId, Types.VARCHAR),
                                new DataValue(reserveNo, Types.VARCHAR),
                                new DataValue(appType, Types.VARCHAR),
                                new DataValue(status, Types.VARCHAR),
                                new DataValue(itemsNo, Types.VARCHAR),
                                new DataValue(request.getShopDistribution(), Types.VARCHAR),
                                new DataValue(request.getOpNo(), Types.VARCHAR),
                                new DataValue(request.getMemberId(), Types.VARCHAR),
                                new DataValue(request.getName(), Types.VARCHAR),
                                new DataValue(request.getMobile(), Types.VARCHAR),
                                new DataValue(request.getDate(), Types.DATE),
                                new DataValue(request.getTime(), Types.VARCHAR),
                                new DataValue(request.getMemo(), Types.VARCHAR),
                                new DataValue("", Types.VARCHAR), // ISEVALUATE 是否评价Y/空
                                new DataValue(couponCode, Types.VARCHAR),
                                new DataValue(request.getCreateOpId(), Types.VARCHAR),
                                new DataValue(request.getCreateOpName(), Types.VARCHAR),
                                new DataValue(lastmoditime, Types.DATE),
                                new DataValue(request.getCreateOpId(), Types.VARCHAR),
                                new DataValue(request.getCreateOpName(), Types.VARCHAR),
                                new DataValue(lastmoditime, Types.DATE)
                        };
                InsBean ib1 = new InsBean("DCP_RESERVE", columns);
                ib1.addValues(insValue1);
                this.addProcessData(new DataProcessBean(ib1));

                this.doExecuteDataToDB();

                DCP_ReserveOrderCreate_OpenRes.level1Elm level1Elm = res.new level1Elm();
                level1Elm.setReserveNo(reserveNo);
                res.setDatas(level1Elm);
            }

            /**
             * 3. 发送短信
             */
            if (smsalerts.equals("Y")) {
                // 判断若DCP_RESERVEPARAMETER.SMSALERTS==Y，则异步调用CRM接口SendMobileMessage，文案内容见原型；
                if (Check.Null(memberUrl)) {
                    memberUrl = PosPub.getCRM_INNER_URL(eId);
                    if (memberUrl.trim().equals("")) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "移动支付接口参数未设置!");
                    }
                }
                sqlbuf.setLength(0);
                sqlbuf.append("SELECT a.ITEMSNO ,b.ITEMSNAME ,a.SHOPID ,c.ORG_NAME FROM DCP_RESERVEITEMS a  " +
                        "LEFT JOIN DCP_SERVICEITEMS b ON a.eid = b.eid AND a.ITEMSNO  = b.ITEMSNO  " +
                        "LEFT JOIN DCP_ORG_LANG c ON a.EID  = c.EID  AND a.SHOPID = c.ORGANIZATIONNO  AND c.LANG_TYPE  = '"+req.getLangType()+"' AND  c.STATUS  = '100' " +
                        "WHERE a.eid = '" + eId + "' and a.shopID = '" + shopId + "' and a.ITEMSNO = '" + itemsNo + "'");
                List<Map<String, Object>> getItemDesc = this.doQueryData(sqlbuf.toString(), null);
                StringBuffer smsg = new StringBuffer("");
                if (!CollectionUtils.isEmpty(getItemDesc)) {
                    Map<String, Object> itemDesc = getItemDesc.get(0);
                    String itemsName = itemDesc.get("ITEMSNAME").toString();
                    String org_name = itemDesc.get("ORG_NAME").toString();
                    String rtime = request.getDate() + " " + request.getTime();
                    // 手机号脱敏处理
                    String tmobile = StringUtils.overlay(mobile, "****", 3, 7);
                    smsg.append("您已成功预约" + itemsName + "服务，门店为" + org_name + "，时间为" + rtime + "，手机号为" + tmobile + "，门店已安排妥当，请您在15分钟内到门店开始护理体验、超过15分钟我们将默认为您取消护理哦！感谢您选择乐沙儿、祝您生活愉快！\n" +
                            "温馨提示：因疫情原因可能存在活动变动，您可以在活动前拨打门店电话咨询（门店电话可在我的预约-预约详情页查阅），谢谢理解与关注~");
                }

                JSONObject sendMessageReq = new JSONObject();
                sendMessageReq.put("serviceId", "SendMobileMessage");

                JSONObject smsgReq = new JSONObject();
                smsgReq.put("mobile", mobile);    //  手机号
                smsgReq.put("message", smsg);         // 消息文本内容
                smsgReq.put("messageType", "2");  // 消息类型
                smsgReq.put("channelId", req.getApiUser().getChannelId());  // 渠道编码
                smsgReq.put("channelName", ""); // 渠道编码

                sendMessageReq.put("request", smsgReq);

                String smsgReqStr = smsgReq.toString();
                String smsgSign = PosPub.encodeMD5(smsgReqStr + appid);

                JSONObject smsgSignJson = new JSONObject();
                smsgSignJson.put("sign", smsgSign);
                smsgSignJson.put("key", appKey);

                sendMessageReq.put("sign", smsgSignJson);
                //********** 已经准备好 SendMobileMessage 的json，开始调用 *************
                String s = sendMessageReq.toString();
                String smsgResStr = HttpSend.Sendcom(sendMessageReq.toString(), memberUrl).trim();
                HelpTools.writelog_fileName("***********预约单创建 DCP_ReserveOrderCreate 调用 SendMobileMessage 接口，请求json：" + sendMessageReq.toString()
                        + "*************", "DCP_ReserveOrderCreate");

                JSONObject smsgResJson = new JSONObject();
                smsgResJson = JSON.parseObject(smsgResStr);//String转json

                HelpTools.writelog_fileName("***********预约单创建 DCP_ReserveOrderCreate 调用 SendMobileMessage 接口返回信息：" + smsgResStr + "*************", "DCP_ReserveOrderCreate");
                String smsgSuccess = smsgResJson.getString("success").toUpperCase(); // TRUE 或 FALSE
                //CRM 接口提示： 返回false时，并不表示一定是创建支付失败，有可能顾客还在支付中，需判断serviceStatus
                String smsgServiceStatus = smsgResJson.getString("serviceStatus").toUpperCase();
                String smsgServiceDescription = smsgResJson.getString("serviceDescription").toUpperCase();

            }

        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReserveOrderCreate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReserveOrderCreate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReserveOrderCreate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReserveOrderCreate_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ReserveOrderCreate_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(request.getShopId())) {
            errMsg.append("所属门店不能为空,");
            isFail = true;
        }
        if (Check.Null(request.getItemsNo())) {
            errMsg.append("项目编号不能为空, ");
            isFail = true;
        }

        if (Check.Null(request.getMemberId())) {
            errMsg.append("预约人，会员ID不能为空, ");
            isFail = true;
        }
        if (Check.Null(request.getName())) {
            errMsg.append("会员名称不能为空, ");
            isFail = true;
        }
        if (Check.Null(request.getMobile())) {
            errMsg.append("手机号不能为空, ");
            isFail = true;
        }
        if (Check.Null(request.getDate())) {
            errMsg.append("预约日期不能为空, ");
            isFail = true;
        }
        if (Check.Null(request.getTime())) {
            errMsg.append("预约时间段不能为空, ");
            isFail = true;
        }
        if (Check.Null(request.getCreateOpId())) {
            errMsg.append("创建人ID，若为商城渠道传openId不能为空, ");
            isFail = true;
        }
        if (Check.Null(request.getCreateOpName())) {
            errMsg.append("创建人姓名,会员名称若无则用微信昵称不能为空, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ReserveOrderCreate_OpenReq> getRequestType() {
        return new TypeToken<DCP_ReserveOrderCreate_OpenReq>() {
        };
    }

    @Override
    protected DCP_ReserveOrderCreate_OpenRes getResponseType() {
        return new DCP_ReserveOrderCreate_OpenRes();
    }

    /**
     * 生成 预约单号
     *
     * @param req
     * @return 预约单号生成规则：YYD+[渠道类型]+[门店/公司编号]+YYYYMMDD+6位随机数字；
     */
    private String autoReserveNo(DCP_ReserveOrderCreate_OpenReq req) {
        String reserveNo = "";
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        ApiUser apiUser = req.getApiUser();
        DCP_ReserveOrderCreate_OpenReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();

        String result = "";
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            result += random.nextInt(10);
        }

        if (apiUser != null) {
            String appType = apiUser.getAppType();
            StringBuffer strbuf = new StringBuffer("");
            strbuf.append("YYD");
            strbuf.append(appType);
            strbuf.append(shopId);
            strbuf.append(sd.format(cal.getTime()));
            strbuf.append(result);
            reserveNo = strbuf.toString();
        }
        return reserveNo;
    }


    /**
     * 判断同一时段 到店分配是否到最大值
     * @param req
     * @return
     */
    private boolean checkMaxReserve(DCP_ReserveOrderCreate_OpenReq req) throws Exception {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        boolean bool = false;
        String eId = req.geteId();
        DCP_ReserveOrderCreate_OpenReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        String time = request.getTime();
        String date = request.getDate();
        String itemsNo = request.getItemsNo();
        // 1. 查询下参数中有无控制 时段预约单有无限制
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM DCP_RESERVEPARAMETER WHERE eid  = '"+eId+"' AND shopid = '"+shopId+"'");
        List<Map<String, Object>> data = this.doQueryData(sqlbuf.toString(), null);
        String appointments = "1"; // 到店分配最大预约数 默认为 1
        if(!CollectionUtils.isEmpty(data)){
            appointments = data.get(0).get("APPOINTMENTS").toString();
        }

        // 2.查询下当前预约单的时间段 预约人数
        sqlbuf.setLength(0);
        sqlbuf.append("select count(1) NUM from DCP_RESERVE where eid= '"+eId+"' and SHOPID = '"+shopId+"' and \"TIME\" = '"+time+"' and BDATE = to_date('"+date+"','YYYY-MM-DD') AND itemsNo = '"+itemsNo+"' AND status IN (0,1)");
        List<Map<String, Object>> getCount = this.doQueryData(sqlbuf.toString(), null);
        if(!CollectionUtils.isEmpty(getCount)){
            String num = getCount.get(0).get("NUM").toString();
            if(Integer.parseInt(num)>=Integer.parseInt(appointments)){
                bool = true;
            }
        }
        return bool;
    }

    /**
     * 查询会员当前时间段是否预约其他项目
     * @param req
     * @return
     */
    private boolean checkMemberReserve(DCP_ReserveOrderCreate_OpenReq req) throws Exception {
        boolean bool = false;
        DCP_ReserveOrderCreate_OpenReq.level1Elm request = req.getRequest();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        StringBuffer sqlbuf = new StringBuffer("");
        String time = request.getTime();
        String date = request.getDate();
        String eId = req.geteId();
        String shopId = request.getShopId();
        sqlbuf.append("select count(1) NUM from DCP_RESERVE where eid= '"+eId+"' and MEMBERID = '"+request.getMemberId()+"' and SHOPID = '"+shopId+"' and \"TIME\" = '"+time+"' and BDATE = to_date('"+date+"','YYYY-MM-DD')  AND status IN (0,1)");
        List<Map<String, Object>> getCount = this.doQueryData(sqlbuf.toString(), null);
        if(!CollectionUtils.isEmpty(getCount)){
            String num = getCount.get(0).get("NUM").toString();
            if(Integer.parseInt(num)>0){
                bool = true;
            }
        }
        return bool;
    }

    /**
     * 查询当前券码有无被使用
     * @param req
     * @return
     */
    private boolean checkCouponCode(DCP_ReserveOrderCreate_OpenReq req) throws Exception {
        boolean bool = false;
        DCP_ReserveOrderCreate_OpenReq.level1Elm request = req.getRequest();
        String couponCode =  request.getCouponCode();
        String eId = req.geteId();
        String shopId = request.getShopId();
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("select count(1) NUM from DCP_RESERVE where eid= '"+eId+"' and COUPONCODE = '"+couponCode+"'  AND status IN (0,1,2)");
        List<Map<String, Object>> getCount = this.doQueryData(sqlbuf.toString(), null);
        if(!CollectionUtils.isEmpty(getCount)){
            String num = getCount.get(0).get("NUM").toString();
            if(Integer.parseInt(num)>0){
                bool = true;
            }
        }
        return bool;
    }

}
