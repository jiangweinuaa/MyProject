package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.convert.Convert;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.waimai.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderReceivingAgreeOrRejectReq;
import com.dsc.spos.json.cust.req.DCP_OrderRefundReq.levelGoods;
import com.dsc.spos.json.cust.res.DCP_OrderReceivingAgreeOrRejectRes;
import com.dsc.spos.json.cust.res.DCP_OrderRefundRes.Card;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.entity.order;
import com.dsc.spos.waimai.entity.orderGoodsItem;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderPay;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.dsc.spos.waimai.jddj.HelpJDDJHttpUtil;
import com.google.gson.reflect.TypeToken;

//import oracle.sql.NUMBER;

public class DCP_OrderReceivingAgreeOrReject extends SPosAdvanceService<DCP_OrderReceivingAgreeOrRejectReq,DCP_OrderReceivingAgreeOrRejectRes>
{

    @Override
    protected void processDUID(DCP_OrderReceivingAgreeOrRejectReq req, DCP_OrderReceivingAgreeOrRejectRes res)
            throws Exception
    {
        // TODO Auto-generated method stub
        /************* 必传的节点 ******************/
        String eId_para = req.getRequest().geteId();// 请求传入的eId
        // 1：同意,2：拒绝
        String opType = req.getRequest().getOpType();
        String orderNo = req.getRequest().getOrderNo();
        String loadDocType = req.getRequest().getLoadDocType();
        String channelId = req.getRequest().getChannelId();
        String shopNo = req.getRequest().getShopId();
        String refundBdate = req.getRequest().getRefundBdate();
        String refundDatetime = req.getRequest().getRefundDatetime();
        String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        if (refundBdate==null||refundBdate.isEmpty())
        {
            refundBdate = sdate;
        }

        String opNo = req.getRequest().getOpNo();
        String opName = req.getRequest().getOpName();

        String refundReason = req.getRequest().getRefundReason();
        if (refundReason == null)
        {
            refundReason = "";
        }

        boolean isCanFlag = false;
        // 饿了么，美团，京东到家外卖才可以同意/拒绝接单接口
        if (loadDocType.equals(orderLoadDocType.ELEME) || loadDocType.equals(orderLoadDocType.MEITUAN)
                || loadDocType.equals(orderLoadDocType.JDDJ)|| loadDocType.equals(orderLoadDocType.WAIMAI)|| loadDocType.equals(orderLoadDocType.QIMAI)|| loadDocType.equals(orderLoadDocType.MTSG)|| loadDocType.equals(orderLoadDocType.DYWM))
        {
            isCanFlag = true;
        }
        if (!isCanFlag)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "渠道类型" + loadDocType + "暂不支持");
        }
        if (opType.equals("1") || opType.equals("2"))
        {
            isCanFlag = true;
        }
        if (!isCanFlag)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "操作类型类型" + loadDocType + "暂不支持");
        }

        boolean nResult = false;
        StringBuilder errorMeassge = new StringBuilder();

        UptBean up1 = new UptBean("DCP_ORDER");
        up1.addCondition("EID", new DataValue(eId_para, Types.VARCHAR));
        up1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));

        // 更新updatetime
        up1.addUpdateValue("UPDATE_TIME",
                           new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
        up1.addUpdateValue("TRAN_TIME",
                new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));

        // 接单的时候判断一下数据库的这单状态是否正确
        String sqlorder = "select * from dcp_order where eid='" + eId_para + "' and LOADDOCTYPE='" + loadDocType
                + "' and orderno='" + orderNo + "'  ";
        List<Map<String, Object>> listsql = this.doQueryData(sqlorder, null);
        String status_db = "";// 数据库订单状态
        int printCount = 0;//打印次数 PRINTCOUNT
        String app_poi_code = "";
        if (listsql != null && !listsql.isEmpty())
        {
            status_db = listsql.get(0).get("STATUS").toString();
            app_poi_code = listsql.get(0).getOrDefault("ORDERSHOP", "").toString();
            try
            {
                channelId = listsql.get(0).get("CHANNELID").toString();
            } catch (Exception e)
            {

            }
            String printCountStr = listsql.get(0).getOrDefault("PRINTCOUNT","0").toString();
            try
            {
                printCount = Integer.parseInt(printCountStr);

            } catch (Exception e)
            {

            }

        }

        if (opType.equals("1"))
        {
            if (!status_db.isEmpty()&&!status_db.equals("1"))
            {
                // 状态不对，清除缓存
                // 不能直接删除，因为可能缓存里面已经更新了最新的状态，所以对比下，如果缓存里面是statas=1，那么就直接删除
                String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId_para + ":" + shopNo;
                String hash_key = orderNo;
                try
                {
                    RedisPosPub redis = new RedisPosPub();
                    String ordermap = redis.getHashMap(redis_key, hash_key);
                    if (ordermap != null && ordermap.isEmpty() == false)
                    {
                        JSONObject obj = new JSONObject(ordermap);
                        String redis_status = obj.get("status").toString();
                        // String redis_refundStatus =
                        // obj.optString("refundStatus").toString();
                        if (redis_status.equals("1")) // 未接单状态时，直接删缓存
                        {
                            redis.DeleteHkey(redis_key, hash_key);
                            HelpTools.writelog_waimai("同意接单删除缓存:" + orderNo + " redis_key:" + redis_key
                                    + " hash_key:" + hash_key + " 缓存中订单状态redis_status:" + redis_status);
                        }
                    }
                } catch (Exception e)
                {

                }
                if ("2".equals(status_db)||"11".equals(status_db))
                {
                    if (printCount>0)
                    {
                        res.setSuccess(false);
                        res.setServiceDescription("当前单据数据库中status:"+status_db+"且已成功调用过接单接口,无需再调用！");
                        HelpTools.writelog_waimai("当前单据数据库中status:"+status_db+"且已成功调用过接单接口,无需再调用！,直接返回false");
                    }
                    else
                    {
                        res.setSuccess(true);
                        res.setServiceDescription("当前单据已接单/完成！");
                        HelpTools.writelog_waimai("当前单据数据库中status:"+status_db+",直接返回true");
                        try
                        {
                            printCount++;
                            up1.addUpdateValue("PRINTCOUNT", new DataValue(printCount, Types.VARCHAR));
                            this.addProcessData(new DataProcessBean(up1));
                            this.doExecuteDataToDB();
                        } catch (Exception e)
                        {
                            HelpTools.writelog_waimai("【数据库中更新打印次数】异常" + e.getMessage());
                        }
                    }

                }
                else
                {
                    res.setSuccess(false);
                    res.setServiceDescription("当前单据数据库中status:"+status_db+",无需接单");
                    HelpTools.writelog_waimai("当前单据数据库中status:"+status_db+",无需接单,返回false");
                }

                return;
            }
            if (loadDocType.equals(orderLoadDocType.ELEME))
            {
                Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopNo, loadDocType,app_poi_code);
                if (map != null)
                {
                    String elmAPPKey = map.get("APPKEY").toString();
                    String elmAPPSecret = map.get("APPSECRET").toString();
                    String elmAPPName = map.get("APPNAME").toString();
                    String elmIsTest = map.get("ISTEST").toString();
                    boolean elmIsSandbox = false;
                    if (elmIsTest != null && elmIsTest.equals("Y"))
                    {
                        elmIsSandbox = true;
                    }
                    String isJBP = map.getOrDefault("ISJBP","").toString();//鼎捷服务商模式
                    String userId = map.getOrDefault("USERID","").toString();
                    if ("Y".equals(isJBP))
                    {
                        nResult = WMELMOrderProcess.orderConfirm(userId,elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName, orderNo,
                                errorMeassge);
                    }
                    else
                    {
                        nResult = WMELMOrderProcess.orderConfirm(elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName, orderNo,
                                errorMeassge);
                    }

                } else
                {
                    nResult = WMELMOrderProcess.orderConfirm(orderNo, errorMeassge);
                }

                if (nResult)
                {
                    // description = "接单成功！";
                    //写下缓存，因为鼎捷外卖不推送，已接单消息
                    /*try
                    {
                        HelpTools.writelog_waimai(orderLoadDocType.ELEME+"【接单成功更新缓存】开始，单号orderNo=" + orderNo);
                        order dcpOrder = HelpTools.GetOrderInfoByOrderNO(this.dao, eId_para, "", orderNo);
                        dcpOrder.setStatus("2");
                        String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId_para + ":" + shopNo;
                        String hash_key = orderNo;
                        ParseJson pj = new ParseJson();
                        String hash_value = pj.beanToJson(dcpOrder);;
                        WriteRedis(redis_key, hash_key, hash_value);

                    }
                    catch (Exception e)
                    {
                        HelpTools.writelog_waimai(orderLoadDocType.ELEME+"【接单成功更新缓存】异常:"+e.getMessage()+",单号orderNo=" + orderNo);
                    }*/

                    try
                    {
                        printCount++;
                        up1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
                        up1.addUpdateValue("PRINTCOUNT", new DataValue(printCount, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(up1));
                        this.doExecuteDataToDB();
                    } catch (Exception e)
                    {
                        HelpTools.writelog_waimai("【饿了么接单成功更新数据库状态】异常" + e.getMessage());
                    }
                }
                else // 特殊处理下，防止一直请求都是 返回已经接单
                {
                    // {"error":{"code":808,"error_type":"waimai_action_error","message":"操作失败，订单已确认"}}
                    String orderConfirmResult = errorMeassge.toString();
                    HelpTools.writelog_waimai("【饿了么订单接单返回失败】订单号=" + orderNo + " 返回：" + orderConfirmResult);
                    if (orderConfirmResult != null && orderConfirmResult.contains("订单已确认"))
                    {
                        res.setSuccess(true);
                        res.setServiceStatus("100");
                        res.setServiceDescription(orderConfirmResult);
                        try
                        {
                            printCount++;
                            up1.addUpdateValue("PRINTCOUNT", new DataValue(printCount, Types.VARCHAR));
                            if ("1".equals(status_db))
                            {
                                up1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
                            }
                            this.addProcessData(new DataProcessBean(up1));
                            this.doExecuteDataToDB();
                        } catch (Exception e)
                        {

                        }
                        return;

                    }
                }

            } else if (loadDocType.equals(orderLoadDocType.MEITUAN))
            {
                Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopNo, loadDocType,app_poi_code);
                if (map == null)
                {
                    HelpTools.writelog_waimai("【获取美团外卖映射门店对应的渠道参数】为空！");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【获取美团外卖映射门店对应的渠道参数】为空！");
                }
                String isJbp = map.get("ISJBP").toString();

                if (isJbp != null && isJbp.equals("Y")) // 聚宝盆
                {
                    nResult = WMJBPOrderProcess.orderConfirm(eId_para, shopNo, orderNo, errorMeassge);
                } else
                {
                    nResult = WMMTOrderProcess.orderConfirm(orderNo, errorMeassge);
                }
                if (nResult)
                {
                    // description = "接单成功！";
                    //写下缓存，因为鼎捷外卖不推送，已接单消息
                   /* try
                    {
                        HelpTools.writelog_waimai(orderLoadDocType.MEITUAN+"【接单成功更新缓存】开始，单号orderNo=" + orderNo);
                        order dcpOrder = HelpTools.GetOrderInfoByOrderNO(this.dao, eId_para, "", orderNo);
                        dcpOrder.setStatus("2");
                        String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId_para + ":" + shopNo;
                        String hash_key = orderNo;
                        ParseJson pj = new ParseJson();
                        String hash_value = pj.beanToJson(dcpOrder);;
                        WriteRedis(redis_key, hash_key, hash_value);

                    }
                    catch (Exception e)
                    {
                        HelpTools.writelog_waimai(orderLoadDocType.MEITUAN+"【接单成功更新缓存】异常:"+e.getMessage()+",单号orderNo=" + orderNo);
                    }*/

                    try
                    {
                        up1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
                        printCount++;
                        up1.addUpdateValue("PRINTCOUNT", new DataValue(printCount, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(up1));
                        this.doExecuteDataToDB();
                    } catch (Exception e)
                    {
                        HelpTools.writelog_waimai("【美团接单成功更新数据库状态】异常" + e.getMessage());
                    }

                } else // 特殊处理下，防止一直请求都是 返回已经接单
                {
                    // {"error":{"code":808,"error_type":"waimai_action_error","message":"操作失败,订单已经确认过了"}}
                    String orderConfirmResult = errorMeassge.toString();
                    HelpTools.writelog_waimai("【美团订单接单返回失败】订单号=" + orderNo + " 返回：" + orderConfirmResult);
                    if (orderConfirmResult != null && orderConfirmResult.contains("订单已经确认"))
                    {
                        res.setSuccess(true);
                        res.setServiceStatus("100");
                        res.setServiceDescription(orderConfirmResult);
                        try
                        {
                            printCount++;
                            up1.addUpdateValue("PRINTCOUNT", new DataValue(printCount, Types.VARCHAR));
                            if ("1".equals(status_db))
                            {
                                up1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
                            }
                            this.addProcessData(new DataProcessBean(up1));
                            this.doExecuteDataToDB();
                        } catch (Exception e)
                        {

                        }
                        return;

                    }
                }
            }
            else if (loadDocType.equals(orderLoadDocType.MTSG))
            {
                Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopNo, loadDocType,app_poi_code);
                if (map == null)
                {
                    HelpTools.writelog_waimai("【获取美团闪购映射门店对应的渠道参数】为空！");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【获取美团闪购映射门店对应的渠道参数】为空！");
                }
                nResult = WMSGOrderProcess.orderConfirm(orderNo, errorMeassge);

                if (nResult)
                {
                    try
                    {
                        up1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
                        printCount++;
                        up1.addUpdateValue("PRINTCOUNT", new DataValue(printCount, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(up1));
                        this.doExecuteDataToDB();
                    } catch (Exception e)
                    {
                        HelpTools.writelog_waimai("【美团接单成功更新数据库状态】异常" + e.getMessage());
                    }

                } else // 特殊处理下，防止一直请求都是 返回已经接单
                {
                    // {"error":{"code":808,"error_type":"waimai_action_error","message":"操作失败,订单已经确认过了"}}
                    String orderConfirmResult = errorMeassge.toString();
                    HelpTools.writelog_waimai("【美团订单接单返回失败】订单号=" + orderNo + " 返回：" + orderConfirmResult);
                    if (orderConfirmResult != null && orderConfirmResult.contains("订单已经确认"))
                    {
                        res.setSuccess(true);
                        res.setServiceStatus("100");
                        res.setServiceDescription(orderConfirmResult);
                        try
                        {
                            printCount++;
                            up1.addUpdateValue("PRINTCOUNT", new DataValue(printCount, Types.VARCHAR));
                            if ("1".equals(status_db))
                            {
                                up1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
                            }
                            this.addProcessData(new DataProcessBean(up1));
                            this.doExecuteDataToDB();
                        } catch (Exception e)
                        {

                        }
                        return;

                    }
                }
            }
            else if (loadDocType.equals(orderLoadDocType.DYWM))
            {
                Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopNo, loadDocType,app_poi_code);
                if (map == null)
                {
                    HelpTools.writelog_waimai("【获取抖音外卖映射门店对应的渠道参数】为空！");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【获取抖音外卖映射门店对应的渠道参数】为空！");
                }

                String clientKey = map.get("APPKEY").toString();
                String clientSecret = map.get("APPSECRET").toString();
                //String elmAPPName = map.get("APPNAME").toString();
                String isTest = map.get("ISTEST").toString();
                boolean isSandbox = false;
                if (isTest != null && isTest.equals("Y"))
                {
                    isSandbox = true;
                }
                String isJBP = map.getOrDefault("ISJBP","").toString();//鼎捷服务商模式
                String userId = map.getOrDefault("USERID","").toString();
                nResult = WMDYOrderProcess.orderConfirm(isSandbox,clientKey,clientSecret,orderNo,errorMeassge);

                if (nResult)
                {
                    try
                    {
                        printCount++;
                        up1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
                        up1.addUpdateValue("PRINTCOUNT", new DataValue(printCount, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(up1));
                        this.doExecuteDataToDB();
                    } catch (Exception e)
                    {
                        HelpTools.writelog_waimai("【抖音外卖接单成功更新数据库状态】异常" + e.getMessage());
                    }
                }
                else // 特殊处理下，防止一直请求都是 返回已经接单
                {
                    // {"error":{"code":808,"error_type":"waimai_action_error","message":"操作失败，订单已确认"}}
                    String orderConfirmResult = errorMeassge.toString();
                    HelpTools.writelog_waimai("【抖音外卖接单返回失败】订单号=" + orderNo + " 返回：" + orderConfirmResult);
                    if (orderConfirmResult != null && orderConfirmResult.contains("订单已接单"))
                    {
                        res.setSuccess(true);
                        res.setServiceStatus("100");
                        res.setServiceDescription(orderConfirmResult);
                        try
                        {
                            printCount++;
                            up1.addUpdateValue("PRINTCOUNT", new DataValue(printCount, Types.VARCHAR));
                            if ("1".equals(status_db))
                            {
                                up1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
                            }
                            this.addProcessData(new DataProcessBean(up1));
                            this.doExecuteDataToDB();
                        } catch (Exception e)
                        {

                        }
                        return;

                    }
                }
            }
            else if (loadDocType.equals(orderLoadDocType.JDDJ))
            {

                if (opNo == null || opNo.trim().isEmpty())
                {
                    opNo = "pos";
                }
                nResult = HelpJDDJHttpUtil.orderAcceptOperate(orderNo, true, opNo, errorMeassge);
                if (nResult)
                {
                    // description = "接单成功！";
                    //写下缓存，因为鼎捷外卖不推送，已接单消息
                   /* try
                    {
                        HelpTools.writelog_waimai(orderLoadDocType.JDDJ+"【接单成功更新缓存】开始，单号orderNo=" + orderNo);
                        order dcpOrder = HelpTools.GetOrderInfoByOrderNO(this.dao, eId_para, "", orderNo);
                        dcpOrder.setStatus("2");
                        String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId_para + ":" + shopNo;
                        String hash_key = orderNo;
                        ParseJson pj = new ParseJson();
                        String hash_value = pj.beanToJson(dcpOrder);;
                        WriteRedis(redis_key, hash_key, hash_value);

                    }
                    catch (Exception e)
                    {
                        HelpTools.writelog_waimai(orderLoadDocType.JDDJ+"【接单成功更新缓存】异常:"+e.getMessage()+",单号orderNo=" + orderNo);
                    }*/

                    try // 自动调用拣货完成接口
                    {
                        HelpTools.writelog_waimai("【JDDJ自动调用拣货完成接口】开始！单号orderNo=" + orderNo);
                        boolean delivery_result = HelpJDDJHttpUtil.OrderJDZBDelivery(orderNo, opNo, errorMeassge);
                        HelpTools.writelog_waimai(
                                "【JDDJ自动调用拣货完成接口】完成！单号orderNo=" + orderNo + " 返回结果：" + delivery_result);
                    } catch (Exception e)
                    {
                        // description += "调用拣货完成接口异常："+e.getMessage();
                        HelpTools.writelog_waimai("【JDDJ自动调用拣货完成接口】异常：" + e.getMessage() + " 单号orderNo=" + orderNo);
                    }

                    try
                    {
                        up1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
                        printCount++;
                        up1.addUpdateValue("PRINTCOUNT", new DataValue(printCount, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(up1));
                        this.doExecuteDataToDB();
                    } catch (Exception e)
                    {
                        HelpTools.writelog_waimai("【JDDJ接单成功更新数据库状态】异常" + e.getMessage());
                    }

                }

            }
            else if (loadDocType.equals(orderLoadDocType.WAIMAI)||loadDocType.equals(orderLoadDocType.QIMAI))
            {

                if (opNo == null || opNo.trim().isEmpty())
                {
                    opNo = "pos";
                }
                nResult = true;
                if (nResult)
                {
                    try
                    {
                        up1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
                        printCount++;
                        up1.addUpdateValue("PRINTCOUNT", new DataValue(printCount, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(up1));
                        this.doExecuteDataToDB();

                        //写下缓存，因为鼎捷外卖不推送，已接单消息
                        /*try
                        {
                            HelpTools.writelog_waimai("【鼎捷WAIMAI接单成功更新缓存】开始，单号orderNo=" + orderNo);
                            order dcpOrder = HelpTools.GetOrderInfoByOrderNO(this.dao, eId_para, "", orderNo);
                            String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId_para + ":" + shopNo;
                            String hash_key = orderNo;
                            ParseJson pj = new ParseJson();
                            String hash_value = pj.beanToJson(dcpOrder);;
                            WriteRedis(redis_key, hash_key, hash_value);

                        }
                        catch (Exception e)
                        {
                            HelpTools.writelog_waimai("【鼎捷WAIMAI接单成功更新缓存】异常:"+e.getMessage()+",单号orderNo=" + orderNo);
                        }*/
                        //查询下数据库 原单信息


                    } catch (Exception e)
                    {
                        HelpTools.writelog_waimai("【鼎捷WAIMAI接单成功更新数据库状态】异常" + e.getMessage());
                    }

                }

            }
            else
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "渠道类型" + loadDocType + "暂不支持");
            }

            if (!nResult)
            {
                //针对异常的，删除下缓存，否则一直请求
                String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId_para + ":" + shopNo;
                String hash_key = orderNo;
                deleteRedis(redis_key,hash_key);
            }

        }
        else
        {
            // 更新单身已退数量
            String execsql = "update dcp_order_detail set rqty=qty,runpickqty=qty-pickqty where eid='" + eId_para + "' and orderno='"
                    + orderNo + "' ";
            ExecBean exSale = new ExecBean(execsql);
            this.addProcessData(new DataProcessBean(exSale));
            if (loadDocType.equals(orderLoadDocType.ELEME))
            {
                Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopNo, loadDocType,app_poi_code);
                if (map != null)
                {
                    String elmAPPKey = map.get("APPKEY").toString();
                    String elmAPPSecret = map.get("APPSECRET").toString();
                    String elmAPPName = map.get("APPNAME").toString();
                    String elmIsTest = map.get("ISTEST").toString();
                    boolean elmIsSandbox = false;
                    if (elmIsTest != null && elmIsTest.equals("Y"))
                    {
                        elmIsSandbox = true;
                    }
                    String isJBP = map.getOrDefault("ISJBP","").toString();//鼎捷服务商模式
                    String userId = map.getOrDefault("USERID","").toString();
                    if ("Y".equals(isJBP))
                    {
                        nResult = WMELMOrderProcess.orderCancel(userId,elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName, orderNo,
                                refundReason, "", errorMeassge);
                    }
                    else
                    {
                        nResult = WMELMOrderProcess.orderCancel(elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName, orderNo,
                                refundReason, "", errorMeassge);
                    }

                } else
                {
                    nResult = WMELMOrderProcess.orderCancel(orderNo, refundReason, "", errorMeassge);
                }

                if (nResult)
                {
                    // description = "接单成功！";
                    try
                    {
                        up1.addUpdateValue("STATUS", new DataValue("3", Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(up1));
                        this.doExecuteDataToDB();
                    } catch (Exception e)
                    {
                        HelpTools.writelog_waimai("【饿了么拒单成功更新数据库状态】异常" + e.getMessage());
                    }
                }

            } else if (loadDocType.equals(orderLoadDocType.MEITUAN))
            {
                Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopNo, loadDocType,app_poi_code);
                if (map == null)
                {
                    HelpTools.writelog_waimai("【获取美团外卖映射门店对应的渠道参数】为空！");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【获取美团外卖映射门店对应的渠道参数】为空！");
                }
                String isJbp = map.get("ISJBP").toString();

                if (isJbp != null && isJbp.equals("Y")) // 聚宝盆
                {
                    nResult = WMJBPOrderProcess.orderCancel(eId_para, shopNo, orderNo, refundReason, "", errorMeassge);
                } else
                {
                    nResult = WMMTOrderProcess.orderCancel(orderNo, refundReason, "", errorMeassge);
                }
                if (nResult)
                {
                    // description = "接单成功！";
                    try
                    {
                        up1.addUpdateValue("STATUS", new DataValue("3", Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(up1));
                        this.doExecuteDataToDB();
                    } catch (Exception e)
                    {
                        HelpTools.writelog_waimai("【美团拒单成功更新数据库状态】异常" + e.getMessage());
                    }

                } else // 特殊处理下，防止一直请求都是 返回已经接单
                {
                    // {"error":{"code":808,"error_type":"waimai_action_error","message":"操作失败,订单已经确认过了"}}
                    String orderConfirmResult = errorMeassge.toString();
                    HelpTools.writelog_waimai("【美团订单接单返回失败】订单号=" + orderNo + " 返回：" + orderConfirmResult);
                    if (orderConfirmResult != null && orderConfirmResult.contains("订单已经确认"))
                    {
                        res.setSuccess(true);
                        res.setServiceStatus("100");
                        res.setServiceDescription(orderConfirmResult);
                        return;

                    }
                }
            }
            else if (loadDocType.equals(orderLoadDocType.MTSG))
            {
                Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopNo, loadDocType,app_poi_code);
                if (map == null)
                {
                    HelpTools.writelog_waimai("【获取美团闪购映射门店对应的渠道参数】为空！");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【获取美团闪购映射门店对应的渠道参数】为空！");
                }

                nResult = WMSGOrderProcess.orderCancel(orderNo, refundReason, "", errorMeassge);

                if (nResult)
                {
                    // description = "接单成功！";
                    try
                    {
                        up1.addUpdateValue("STATUS", new DataValue("3", Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(up1));
                        this.doExecuteDataToDB();
                    } catch (Exception e)
                    {
                        HelpTools.writelog_waimai("【美团拒单成功更新数据库状态】异常" + e.getMessage());
                    }
                    //美团闪购 商家调用此接口取消订单，平台不会向商家系统推送取消订单消息和退款消息
                    try
                    {
                        //加锁
                        HelpTools.setWaiMaiOrderToSaleOrRefundRedisLock("1",eId_para,orderNo);
                        //查询下数据库 原单信息
                        order dcpOrder = HelpTools.GetOrderInfoByOrderNO(this.dao, eId_para, "", orderNo);
                        StringBuffer RefundOrCancelError = new StringBuffer();
                        HelpTools.OrderRefundOrCancelProcess(dcpOrder, sdate, RefundOrCancelError);
                    }
                    catch (Exception e)
                    {

                    }

                } else // 特殊处理下，防止一直请求都是 返回已经接单
                {
                    // {"error":{"code":808,"error_type":"waimai_action_error","message":"操作失败,订单已经确认过了"}}
                    String orderConfirmResult = errorMeassge.toString();
                    HelpTools.writelog_waimai("【美团订单接单返回失败】订单号=" + orderNo + " 返回：" + orderConfirmResult);
                    if (orderConfirmResult != null && orderConfirmResult.contains("订单已经取消"))
                    {
                        res.setSuccess(true);
                        res.setServiceStatus("100");
                        res.setServiceDescription(orderConfirmResult);
                        return;

                    }
                }
            }
            else if (loadDocType.equals(orderLoadDocType.DYWM))
            {
                Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId_para, shopNo, loadDocType,app_poi_code);
                if (map == null)
                {
                    HelpTools.writelog_waimai("【获取抖音外卖映射门店对应的渠道参数】为空！");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【获取抖音外卖映射门店对应的渠道参数】为空！");
                }

                String clientKey = map.get("APPKEY").toString();
                String clientSecret = map.get("APPSECRET").toString();
                //String elmAPPName = map.get("APPNAME").toString();
                String isTest = map.get("ISTEST").toString();
                boolean isSandbox = false;
                if (isTest != null && isTest.equals("Y"))
                {
                    isSandbox = true;
                }
                String isJBP = map.getOrDefault("ISJBP","").toString();//鼎捷服务商模式
                String userId = map.getOrDefault("USERID","").toString();
                nResult = WMDYOrderProcess.orderCancel(isSandbox,clientKey,clientSecret,orderNo,refundReason, "",errorMeassge);
                if (nResult)
                {
                    try
                    {
                        up1.addUpdateValue("STATUS", new DataValue("3", Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(up1));
                        this.doExecuteDataToDB();
                    } catch (Exception e)
                    {
                        HelpTools.writelog_waimai("【抖音外卖拒单成功更新数据库状态】异常" + e.getMessage());
                    }
                }

            }
            else if (loadDocType.equals(orderLoadDocType.JDDJ))
            {

                if (opNo == null || opNo.trim().isEmpty())
                {
                    opNo = "pos";
                }
                nResult = HelpJDDJHttpUtil.orderAcceptOperate(orderNo, false, opNo, errorMeassge);
                if (nResult)
                {
                    // description = "接单成功！";
                    try
                    {
                        up1.addUpdateValue("STATUS", new DataValue("3", Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(up1));
                        this.doExecuteDataToDB();
                    } catch (Exception e)
                    {
                        HelpTools.writelog_waimai("【JDDJ拒单成功更新数据库状态】异常" + e.getMessage());
                    }

                }

            }
            else if (loadDocType.equals(orderLoadDocType.WAIMAI))
            {
                if (opNo == null || opNo.trim().isEmpty())
                {
                    opNo = "pos";
                }
                if (!status_db.equals("1"))
                {
                    res.setSuccess(false);
                    res.setServiceStatus("200");
                    res.setServiceDescription("当前单据状态非订单开立，不能拒绝接单！");
                    return;
                }

                //查询下数据库 原单信息
                order dcpOrder = HelpTools.GetOrderInfoByOrderNO(this.dao, eId_para, "", orderNo);
                String machineNo = dcpOrder.getMachineNo();
                if(machineNo==null||machineNo.isEmpty())
                {
                    machineNo = "pos_001";
                }

                boolean mobilePayResult = true;//移动支付成功标记
                boolean memberPayResult = true;//会员支付成功标记
                StringBuffer otherChannelError = new StringBuffer("");

                //先调用移动支付退款，在调用会员支付
                //如果移动支付退款失败，就直接不能拒单
                //如果移动支付退款成功，会员支付失败往下走。

                if(dcpOrder.getPay()!=null&&!dcpOrder.getPay().isEmpty())
                {
                    boolean isHasMobilePay = false;

                    String memberpayno = "";

                    com.alibaba.fastjson.JSONObject reqheader_mobile=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());

                    //尾款处理,这个只是记录付款
                    com.alibaba.fastjson.JSONArray payslistArray=new com.alibaba.fastjson.JSONArray();
                    //这里才会扣款
                    com.alibaba.fastjson.JSONArray cardlistArray=new com.alibaba.fastjson.JSONArray();
                    //券列表
                    com.alibaba.fastjson.JSONArray couponlistArray=new com.alibaba.fastjson.JSONArray();
                    for (orderPay lPay : dcpOrder.getPay())
                    {
                        String payType = lPay.getPayType()==null?"":lPay.getPayType();
                        String funcNo = lPay.getFuncNo()==null?"":lPay.getFuncNo();

                        BigDecimal p_pay=new BigDecimal(lPay.getPay());
                        BigDecimal p_changed=new BigDecimal(lPay.getChanged());
                        BigDecimal p_extra=new BigDecimal(lPay.getExtra());

                        //pay-changed-extra累加起来
                        BigDecimal p_realpay=p_pay.subtract(p_changed).subtract(p_extra);
                        p_realpay=p_realpay.setScale(2,RoundingMode.HALF_UP);

                        //券面额
                        BigDecimal faceAmt=p_pay;//.add(p_extra);
                        faceAmt=faceAmt.setScale(2,RoundingMode.HALF_UP);


                        if(payType.toUpperCase().equals("#P1")||payType.toUpperCase().equals("#P2"))
                        {
                            if (isHasMobilePay)
                            {
                                continue;
                            }
                            //这个只会有一笔
                            isHasMobilePay = true;
                            reqheader_mobile.put("pay_type", payType);
                            reqheader_mobile.put("order_id", lPay.getPaySerNum());//被退款单号
                            reqheader_mobile.put("shop_code", shopNo);//下单交易机构，如门店号
                            reqheader_mobile.put("pos_code", machineNo);//终端设备，如POS机号
                            reqheader_mobile.put("pay_amt", p_realpay+"");//原交易金额
                            reqheader_mobile.put("return_amount",p_realpay+ "");//退款金额
                            reqheader_mobile.put("ret_order_id", "RE"+lPay.getPaySerNum());//发起退款的新单号
                            reqheader_mobile.put("operation_id", opNo);//操作员

                        }
                        else
                        {

                            //****会员卡扣款****
                            if (funcNo.equals("301"))
                            {
                                if (lPay.getPaySerNum()!=null&&!lPay.getPaySerNum().isEmpty())
                                {
                                    memberpayno = lPay.getPaySerNum();
                                }

                                com.alibaba.fastjson.JSONObject tempPay=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                tempPay.put("payType",lPay.getPayCode());//收款方式代号
                                tempPay.put("payName",lPay.getPayName());//收款方式名称
                                tempPay.put("payAmount",p_realpay);//付款金额
                                tempPay.put("noCode",lPay.getCardNo());//卡号
                                tempPay.put("isCardPay",1);//
                                payslistArray.add(tempPay);

                                //
                                com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                tempCard.put("cardNo",lPay.getCardNo());
                                tempCard.put("amount",p_realpay);//0只处理积分
                                tempCard.put("getPoint","0");//0只处理积分
                                cardlistArray.add(tempCard);
                            }
                            else if (funcNo.equals("302"))//积分扣减
                            {
                                if (lPay.getPaySerNum()!=null&&!lPay.getPaySerNum().isEmpty())
                                {
                                    memberpayno = lPay.getPaySerNum();
                                }
                                //
                                com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                tempCard.put("cardNo",lPay.getCardNo());
                                tempCard.put("usePoint",lPay.getDescore());//积分扣减
                                tempCard.put("amount","0");//0只处理积分
                                tempCard.put("getPoint","0");//0只处理积分
                                cardlistArray.add(tempCard);
                            }
                            else if (funcNo.equals("304") || funcNo.equals("305")|| funcNo.equals("307"))//现金券/折扣券
                            {
                                if (lPay.getPaySerNum()!=null&&!lPay.getPaySerNum().isEmpty())
                                {
                                    memberpayno = lPay.getPaySerNum();
                                }
                                //
                                com.alibaba.fastjson.JSONObject tempCoupon=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                tempCoupon.put("couponCode",lPay.getCardNo());//券号
                                tempCoupon.put("couponType","");//券类型
                                tempCoupon.put("quantity",lPay.getCouponQty());//使用张数
                                tempCoupon.put("faceAmount",faceAmt);//总面额
                                tempCoupon.put("buyAmount",p_pay);//抵账金额
                                couponlistArray.add(tempCoupon);
                            }
                            else
                            {
                                continue;
                            }

                        }

                    }

                    //判断下是否存在，需要调用CRM接口
                    if (isHasMobilePay || payslistArray.size() > 0 || cardlistArray.size() > 0
                            || couponlistArray.size() > 0)
                    {
                        String Yc_Url = "";
                        String Yc_Key = req.getApiUserCode();
                        String Yc_Sign_Key = req.getApiUser().getUserKey();
                        Yc_Url=PosPub.getCRM_INNER_URL(eId_para);

                        if (Yc_Url.trim().equals("") || Yc_Key.trim().equals("") || Yc_Sign_Key.trim().equals(""))
                        {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,
                                                        "CrmUrl、apiUserCode、userKey移动支付接口参数未设置!");
                        }


                        //先调用移动支付退款
                        if(isHasMobilePay)
                        {
                            try
                            {
                                com.alibaba.fastjson.JSONObject payReq_mobile=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                com.alibaba.fastjson.JSONObject signheader_mobile=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                String req_sign=reqheader_mobile.toString() + Yc_Sign_Key;

                                req_sign=DigestUtils.md5Hex(req_sign);

                                //
                                signheader_mobile.put("key", Yc_Key);//
                                signheader_mobile.put("sign", req_sign);//md5

                                payReq_mobile.put("serviceId", "Refund");

                                payReq_mobile.put("request", reqheader_mobile);
                                payReq_mobile.put("sign", signheader_mobile);


                                String str = payReq_mobile.toString();

                                HelpTools.writelog_waimai("移动支付退款接口Refund请求内容："+str+",单号orderNo="+orderNo);
                                //PosPub.WriteETLJOBLog("移动支付退款接口Refund请求内容："+str +"\r\n");

                                String	resbody = "";

                                //编码处理
                                str=URLEncoder.encode(str,"UTF-8");

                                resbody=HttpSend.Sendcom(str, Yc_Url);
                                HelpTools.writelog_waimai("移动支付退款接口Refund返回："+resbody+",单号orderNo="+orderNo);
                                //PosPub.WriteETLJOBLog("移动支付退款接口Refund返回："+resbody +"\r\n");

                                if(resbody==null||resbody.isEmpty())
                                {
                                    mobilePayResult = false;
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调用移动支付Refund接口返回为空！");
                                }
                                com.alibaba.fastjson.JSONObject jsonres = JSON.parseObject(resbody);

                                String serviceDescription=jsonres.get("serviceDescription").toString();
                                String success=jsonres.get("success").toString();
                                if(success.toUpperCase().equals("TRUE"))
                                {
                                    mobilePayResult = true;
                                }
                                else
                                {
                                    mobilePayResult = false;
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调用移动支付Refund接口失败:"+serviceDescription);
                                }


                            }
                            catch (Exception e)
                            {
                                // TODO: handle exception
                                mobilePayResult = false;
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调用移动支付Refund异常:"+e.getMessage());
                            }

                        }
                        else
                        {
                            try
                            {
                                com.alibaba.fastjson.JSONObject payReq=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                com.alibaba.fastjson.JSONObject reqheader=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                com.alibaba.fastjson.JSONObject signheader=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                                com.alibaba.fastjson.JSONArray goodslistArray=new com.alibaba.fastjson.JSONArray();
			                   /* for (orderGoodsItem detail: dcpOrder.getGoodsList())
			                    {
			                        com.alibaba.fastjson.JSONObject goods=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
			                        goods.put("goods_id",detail.getPluBarcode());
			                        goods.put("goods_name",detail.getPluName());
			                        goods.put("price",detail.getPrice());
			                        goods.put("quantity",detail.getQty());
			                        goods.put("amount",detail.getAmt());
			                        goods.put("allowPoint","1");
			                        goodslistArray.add(goods);
			                    }*/

                                reqheader.put("orderNo", memberpayno);//需唯一
                                reqheader.put("refundOrderNo", "RE"+orderNo);//新的退款单号
                                reqheader.put("orderAmount", dcpOrder.getTot_Amt()+"");//
                                //reqheader.put("pointAmount", dcpOrder.getTot_Amt()+"");//
                                reqheader.put("orgType", "2");//组织类型：1=公司 2=门店 3=渠道 	 总部：填公司 门店：填门店 第三方：填渠道
                                reqheader.put("orgId", shopNo);

                                reqheader.put("oprId", opNo);//
                                reqheader.put("goodsdetail", goodslistArray);
                                reqheader.put("cards", cardlistArray);
                                reqheader.put("coupons", couponlistArray);
                                reqheader.put("payDetail", payslistArray);

                                //
                                String req_sign=reqheader.toString() + Yc_Sign_Key;

                                req_sign=DigestUtils.md5Hex(req_sign);

                                //
                                signheader.put("key", Yc_Key);//
                                signheader.put("sign", req_sign);//md5

                                payReq.put("serviceId", "MemberPayRefund");

                                payReq.put("request", reqheader);
                                payReq.put("sign", signheader);


                                String str = payReq.toString();
                                HelpTools.writelog_waimai("会员退款接口MemberPayRefund请求内容："+str+",单号orderNo="+orderNo);
                                //PosPub.WriteETLJOBLog("会员退款接口MemberPayRefund请求内容："+str +"\r\n");

                                String	resbody = "";

                                //编码处理
                                str=URLEncoder.encode(str,"UTF-8");

                                resbody=HttpSend.Sendcom(str, Yc_Url);

                                //PosPub.WriteETLJOBLog("会员退款接口MemberPayRefund返回："+resbody +"\r\n");
                                HelpTools.writelog_waimai("会员退款接口MemberPayRefund返回："+resbody+",单号orderNo="+orderNo);

                                com.alibaba.fastjson.JSONObject jsonres = JSON.parseObject(resbody);
                                String memberPayRefundServiceDescription= "";
                                String memberPayRefundSuccess="";

                                if (jsonres.containsKey("serviceDescription"))
                                {
                                    memberPayRefundServiceDescription=jsonres.get("serviceDescription").toString();
                                }

                                if (jsonres.containsKey("success"))
                                {
                                    memberPayRefundSuccess=jsonres.get("success").toString();
                                }


                                if (!Check.Null(memberPayRefundSuccess) && memberPayRefundSuccess.toUpperCase().equals("TRUE"))
                                {

                                }
                                else
                                {
                                    otherChannelError.append(memberPayRefundServiceDescription);
                                    memberPayResult= false;
                                }


                                if(!memberPayResult)
                                {
                                    if(isHasMobilePay&&mobilePayResult)
                                    {
                                        res.setServiceDescription("移动支付退款成功，但会员退款接口异常:"+otherChannelError.toString());
                                    }
                                    else
                                    {
                                        memberPayResult = false;
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调用会员退款接口MemberPayRefund异常:"+otherChannelError.toString());
                                    }
                                }

                            }
                            catch (Exception e)
                            {
                                // TODO: handle exception
                                if(isHasMobilePay&&mobilePayResult)
                                {
                                    res.setServiceDescription("移动支付退款成功，但会员退款接口异常:"+e.getMessage());
                                }
                                else
                                {
                                    memberPayResult = false;
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
                                }

                            }

                        }

                    }


                }

                //都没有使用上面这些付款方式，直接更新单据状态就OK
                nResult = true;
                try
                {
                    up1.addUpdateValue("STATUS", new DataValue("3", Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(up1));
                    this.doExecuteDataToDB();
                    dcpOrder.setStatus("3");
                    //生成退单
                    StringBuffer RefundOrCancelError = new StringBuffer();
                    HelpTools.OrderRefundOrCancelProcess(dcpOrder, refundBdate, RefundOrCancelError);
                } catch (Exception e)
                {
                    HelpTools.writelog_waimai("【鼎捷WAIMAI拒单成功更新数据库状态】异常" + e.getMessage());
                }

            }
            else
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "渠道类型" + loadDocType + "暂不支持");
            }

            if (nResult)
            {
                // 拒绝接单后，删除缓存 鼎捷外卖 已经单独处理
                String redisKey = orderRedisKeyInfo.redis_OrderTableName + ":" + eId_para + ":" + shopNo;
                String hashKey = orderNo;
                this.deleteRedis(redisKey, hashKey);

            }

        }

        res.setSuccess(nResult);
        if (nResult)
        {
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
        } else
        {
            res.setServiceStatus("100");
            res.setServiceDescription(errorMeassge.toString());
        }

        // region 写订单日志
        if (nResult)
        {
            try
            {
                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                orderStatusLog onelv1 = new orderStatusLog();
                onelv1.setLoadDocType(loadDocType);
                onelv1.setChannelId(channelId);
                onelv1.setLoadDocBillType("");
                onelv1.setLoadDocOrderNo(orderNo);
                onelv1.seteId(eId_para);
                onelv1.setOpName(opName);
                onelv1.setOpNo(opNo);
                onelv1.setShopNo(shopNo);
                onelv1.setOrderNo(orderNo);
                onelv1.setMachShopNo("");
                onelv1.setShippingShopNo("");

                String statusType = "";
                String updateStaus = "";
                if (opType.equals("1"))
                {
                    statusType = "1";// 订单状态
                    updateStaus = "2";
                } else
                {
                    statusType = "1";// 退单状态
                    updateStaus = "3";// 部分退单成功
                }

                onelv1.setStatusType(statusType);
                onelv1.setStatus(updateStaus);
                StringBuilder statusTypeNameObj = new StringBuilder();
                String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
                String statusTypeName = statusTypeNameObj.toString();
                onelv1.setStatusTypeName(statusTypeName);
                onelv1.setStatusName(statusName);

                String memo = "";
                memo += statusName;
                onelv1.setMemo(memo);
                onelv1.setDisplay("1");

                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);

                orderStatusLogList.add(onelv1);

                StringBuilder errorStatusLogMessage = new StringBuilder();
                boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                if (nRet)
                {
                    HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                } else
                {
                    HelpTools.writelog_waimai(
                            "【写表tv_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
                }
                this.pData.clear();
                // endregion

                //增加KDS处理逻辑部分 同意接单才处理KDS相关逻辑
                if(opType.equals("1"))
                {
                    //是否启用KDS标记
                    String canKDS = "N";
                    String Crm_channel_KDS_SQL = "select * from crm_channel where eid='" + eId_para + "' and appno='KDS' and status=100";
                    List<Map<String, Object>> getData_KDS = this.doQueryData(Crm_channel_KDS_SQL, null);
                    if (getData_KDS != null && getData_KDS.size() > 0)
                    {
                        canKDS = "Y";
                    }
                    if (canKDS.equals("Y"))
                    {
                        //最大制作份数，商品数量大于此值，分多笔存入
                        BigDecimal kdsProduceQty = new BigDecimal(0);
                        String v_kdsProduceQty = PosPub.getPARA_SMS(dao, req.geteId(), req.getShopId(), "KdsProduceQty");
                        if (PosPub.isNumeric(v_kdsProduceQty))
                        {
                            kdsProduceQty = new BigDecimal(v_kdsProduceQty);
                        }
                        //查订单单头
                        String order_SQL = "select * from dcp_order where eid='" + eId_para + "' and LOADDOCTYPE='" + loadDocType + "' and orderno='" + orderNo + "'  ";
                        List<Map<String, Object>> getData_Order = this.doQueryData(order_SQL, null);


                        if (getData_Order != null && getData_Order.size() > 0)
                        {
                            //整单商品都是预制菜品标记
                            boolean b_orderBeforeDishFlag=false;

                            //整单商品都是免传菜标记
                            boolean b_orderUnCallFlag=false;
                            //整单商品都是免配菜标记
                            boolean b_orderUnSideFlag=false;
                            //整单商品都是免制作标记
                            boolean b_orderUnCookFlag=false;

                            String productSale_status="0";

                            //查预制菜品
                            String beforeDishTask_SQL = "         select a.eid,a.shopid,a.processtaskno,a.item, a.pluno,a.plubarcode,a.unitid,a.availqty from DCP_PROCESSTASK_DETAIL a " +
                                    "        inner join DCP_PROCESSTASK b on a.eid=b.eid and a.shopid=b.shopid and a.processtaskno=b.processtaskno " +
                                    "        where a.eid='"+eId_para+"' " +
                                    "        and a.shopid='"+shopNo+"' " +
                                    "        and b.otype='BEFORE' " +
                                    "        and a.bdate='"+sdate+"' " +
                                    "        and a.availqty>0 " +
                                    "        ORDER BY a.processtaskno,a.item ";
                            List<Map<String, Object>> getData_beforDishTask = this.doQueryData(beforeDishTask_SQL, null);

                            //查订单单身
                            String ordeDetail_SQL = "select a.*,b.category,b.ISDOUBLEGOODS from dcp_order_detail a " +
                                    "left join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno " +
                                    "where a.eid='" + eId_para + "' and a.LOADDOCTYPE='" + loadDocType + "' and a.orderno='" + orderNo + "'  ";
                            List<Map<String, Object>> getData_Order_detail = this.doQueryData(ordeDetail_SQL, null);

                            String processTaskNO = "";
                            String bdate = getData_Order.get(0).get("BDATE").toString();
                            BigDecimal bdm_tot_cqty = new BigDecimal(0);

                            // 配送时间
                            String shipDate = getData_Order.get(0).get("SHIPDATE").toString();
                            String shipStartTime = getData_Order.get(0).get("SHIPSTARTTIME").toString();
                            shipStartTime = shipStartTime.replace("-", "");
                            if (shipStartTime.isEmpty()) {
                                shipStartTime = new SimpleDateFormat("HHmmss").format(new Date());
                            }
                            String shipStartDateTime = shipDate + shipStartTime;// 日期格式如"20181223110438"使用yyyyMMddHHmmss

                            //统计品种数
                            List<String> temp_category = new ArrayList<>();

                            //生产任务明细可能不写，因为有预制菜品存在，如果标记不写明细，生产任务单头也不要写
                            boolean bHasProcessTaskDetail = false;

                            if (getData_Order_detail != null && getData_Order_detail.size() > 0)
                            {

                                StringBuffer sJoinPluno=new StringBuffer("");
                                StringBuffer sJoinCategory=new StringBuffer("");

                                //优化查询多pluno一起查
                                for (Map<String, Object> map_order_detail : getData_Order_detail)
                                {
                                    sJoinPluno.append(map_order_detail.get("ORDERNO").toString()+",");
                                    sJoinCategory.append(map_order_detail.get("CATEGORY").toString()+",");
                                }
                                //
                                Map<String, String> mapOrder=new HashMap<String, String>();
                                mapOrder.put("PLUNO", sJoinPluno.toString());
                                mapOrder.put("CATEGORY", sJoinCategory.toString());

                                //
                                MyCommon cm=new MyCommon();
                                String withasSql_Pluno=cm.getFormatSourceMultiColWith(mapOrder);
                                mapOrder=null;

                                //1、菜品控制表
                                String KDS_Control_SQL = "with a AS ( " +
                                        withasSql_Pluno + " ) " +
                                        "select  b.* from DCP_KDSDISHES_CONTROL b " +
                                        "inner join a on a.pluno=b.pluno and a.category=b.category " +
                                        "where b.eid='" + eId_para + "' and b.shopid='" + shopNo + "'";
                                List<Map<String, Object>> getData_KDS_Control = this.doQueryData(KDS_Control_SQL, null);


                                //2、总部菜品控制表
                                String KDS_HQControl_SQL = "with a AS ( " +
                                        withasSql_Pluno + " ) " +
                                        "select  b.* from DCP_HQKDSDISHES_CONTROL b " +
                                        "inner join a on (b.goodstype=2 and  a.pluno=b.id) or (b.goodstype=1 and a.category=b.id) " +
                                        "where b.eid='" + eId_para + "' order by b.goodstype desc ";
                                List<Map<String, Object>> getData_KDS_HQControl = this.doQueryData(KDS_HQControl_SQL, null);


                                //由于拆分数量成多笔，这个值要累加处理
                                int pItem = 0;

                                String processTaskMax_SQL = "select F_DCP_GETBILLNO('"+eId_para+"','"+shopNo+"','JGRW') PROCESSTASKNO FROM dual ";
                                List<Map<String, Object>> getData_processTaskMax = this.doQueryData(processTaskMax_SQL, null);
                                if (getData_processTaskMax != null && getData_processTaskMax.isEmpty() == false)
                                {
                                    processTaskNO = (String) getData_processTaskMax.get(0).get("PROCESSTASKNO");
                                }
                                else
                                {
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "取加工任务单号失败！");
                                }

                                StringBuffer pgoodsdetail=new StringBuffer();
                                for (Map<String, Object> map_order_detail : getData_Order_detail)
                                {
                                    //没有相同的就加入列表
                                    if (temp_category.contains(map_order_detail.get("CATEGORY").toString()) == false) {
                                        temp_category.add(map_order_detail.get("CATEGORY").toString());
                                    }

                                    /**
                                     * KDS 免配菜Y/N/空
                                     */
                                    String KDS_unside="";
                                    /**
                                     * KDS 免制作
                                     */
                                    String KDS_uncook="";
                                    /**
                                     * KDS 免传菜
                                     */
                                    String KDS_uncall="";

                                    //门店找到标记
                                    boolean bKDS_shopOK=false;
                                    if (getData_KDS_Control != null && getData_KDS_Control.size() > 0)
                                    {
                                        //根据品类及品号查找，先查门店的
                                        List<Map<String, Object>> getData_KDS_temp = getData_KDS_Control.stream().filter(p -> p.get("CATEGORY").toString().equals(map_order_detail.get("CATEGORY").toString()) && p.get("PLUNO").toString().equals(map_order_detail.get("PLUNO").toString())).collect(Collectors.toList());
                                        if (getData_KDS_temp != null && getData_KDS_temp.size() > 0)
                                        {
                                            KDS_unside=Convert.toStr(getData_KDS_temp.get(0).get("UNSIDE"),"");
                                            KDS_uncook=Convert.toStr(getData_KDS_temp.get(0).get("UNCOOK"),"");
                                            KDS_uncall=Convert.toStr(getData_KDS_temp.get(0).get("UNCALL"),"");
                                            //全部空表示未设置，此条记录无效，取总部
                                            if (!KDS_unside.equals("")|| !KDS_uncook.equals("")||!KDS_uncall.equals(""))
                                            {
                                                bKDS_shopOK=true;
                                            }
                                        }
                                    }
                                    //门店找不到，再找
                                    if (!bKDS_shopOK)
                                    {
                                        if (getData_KDS_HQControl != null && getData_KDS_HQControl.size()>0)
                                        {
                                            //根据品类及品号查找，门店没有，再查总部的
                                            List<Map<String, Object>>  getData_KDS_temp = getData_KDS_HQControl.stream().filter(p -> (p.get("GOODSTYPE").toString().equals("2") && p.get("ID").toString().equals(map_order_detail.get("PLUNO").toString())) ||
                                                    (p.get("GOODSTYPE").toString().equals("1") && p.get("ID").toString().equals(map_order_detail.get("CATEGORY").toString()))).collect(Collectors.toList());
                                            if (getData_KDS_temp != null && getData_KDS_temp.size() > 0)
                                            {
                                                KDS_unside=Convert.toStr(getData_KDS_temp.get(0).get("UNSIDE"),"");
                                                KDS_uncook=Convert.toStr(getData_KDS_temp.get(0).get("UNCOOK"),"");
                                                KDS_uncall=Convert.toStr(getData_KDS_temp.get(0).get("UNCALL"),"");
                                            }
                                        }
                                    }
                                    //是否有备注
                                    String isMemo = map_order_detail.get("ISMEMO").toString();
                                    //双拼菜标记
                                    String isDoubleGoods = map_order_detail.get("ISDOUBLEGOODS").toString();

                                    StringBuffer dMemo = new StringBuffer("");
                                    if (isMemo.equals("Y")) {
                                        String order_detail_memo_SQL = "select  * from dcp_order_detail_memo where eid='" + eId_para + "' and shopid='" + shopNo + "' and orderno='" + orderNo + "' and oitem=" + map_order_detail.get("ITEM").toString() + " ";
                                        List<Map<String, Object>> getData_order_detail_memo = this.doQueryData(order_detail_memo_SQL, null);
                                        if (getData_order_detail_memo != null && getData_order_detail_memo.size() > 0) {
                                            for (Map<String, Object> map_detail_memo : getData_order_detail_memo) {
                                                dMemo.append(map_detail_memo.get("MEMO").toString() + ",");
                                            }
                                            if (dMemo.length() > 0) {
                                                dMemo.deleteCharAt(dMemo.length() - 1);
                                            }
                                        }
                                    }

                                    //如果是套餐，PGOODSDETAIL字段给值
                                    //【pGoodsList.pluName*qty(flavorStuffDetail),pGoodsList.pluName*qty...】
                                    pgoodsdetail.setLength(0);
                                    if (map_order_detail.get("PACKAGETYPE").toString().equals("2"))
                                    {
                                        String sql_subPackage="select * from dcp_order_detail where orderno='"+orderNo+"' and packagemitem='"+map_order_detail.get("ITEM").toString()+"' ";
                                        List<Map<String, Object>> getData_order_detail_subPackage = this.doQueryData(sql_subPackage, null);
                                        if (getData_order_detail_subPackage != null && getData_order_detail_subPackage.size()>0)
                                        {
                                            for (Map<String, Object> map_sub : getData_order_detail_subPackage)
                                            {
                                                //处理拼接
                                                pgoodsdetail.append(map_sub.get("PLUNAME")+"*");
                                                pgoodsdetail.append(new DecimalFormat("#.##").format(map_sub.get("QTY")));
                                                pgoodsdetail.append(",");

                                            }
                                            //处理拼接字段值
                                            if (pgoodsdetail.length()>0)
                                            {
                                                //删除尾部逗号,
                                                pgoodsdetail.delete(pgoodsdetail.length()-1,pgoodsdetail.length());
                                                pgoodsdetail.insert(0,"【");
                                                pgoodsdetail.append("】");
                                            }

                                        }

                                    }

                                    String packageMitem=map_order_detail.get("PACKAGEMITEM").toString();
                                    String toppingtype=map_order_detail.get("TOPPINGTYPE").toString();//加料类型（1、正常商品 2、加料主商品  3、加料子商品）
                                    String toppingmitem=map_order_detail.get("TOPPINGMITEM").toString();//加料主商品项次
                                    if (Check.Null(toppingtype))
                                    {
                                        toppingtype="1";
                                    }
                                    if (Check.Null(toppingmitem))
                                    {
                                        toppingmitem="0";
                                    }

                                    if (Check.Null(packageMitem))
                                    {
                                        packageMitem="0";
                                    }

                                    String product_detail_goodsstatus="0";

                                    //写生产任务单身
                                    String[] columns_processTask_detail = {"EID", "SHOPID", "PROCESSTASKNO", "ORGANIZATIONNO", "ITEM", "MUL_QTY",
                                            "PQTY", "PSTOCKIN_QTY", "PUNIT", "BASEQTY", "PLUNO", "PLUNAME", "PRICE", "BASEUNIT", "UNIT_RATIO", "AMT", "DISTRIPRICE"
                                            , "DISTRIAMT", "BDATE", "OFNO", "OITEM", "GOODSSTATUS", "OPNO", "SPECNAME", "UNITID", "UNITNAME"
                                            , "FLAVORSTUFFDETAIL", "ISPACKAGE", "PGOODSDETAIL", "REPASTTYPE", "MEMO", "ISURGE", "REFUNDQTY", "FINALCATEGORY"};

                                    //取余量
                                    double dMode = 0;

                                    //单品全部使用预制菜标记
                                    boolean b_PluBeforeDishFlag=false;

                                    //*************************************双拼菜，使用BOM商品同步************************************
                                    if (isDoubleGoods != null && isDoubleGoods.equals("Y"))
                                    {

                                        StringBuffer sb_bom=new StringBuffer("        select a.bomno,a.bomtype,a.pluno,a.unit,a.mulqty," +
                                                                                     "        c.material_pluno,c.qty,c.material_unit,c.material_qty,c.loss_rate,c.isbuckle,c.isreplace,c.sortid," +
                                                                                     "        mgl.plu_name as materialPluName,mul.uname as materialUnitName,mu.udlength as materialUnitLength," +
                                                                                     "        mg.baseunit as materialBaseUnit,mbul.uname as materialBaseUnitName,mgu.unitratio as materialUnitRatio," +
                                                                                     "        mg.isbatch as materialIsBatch,mg.price,mg.category,kc.unside,kc.uncook,kc.uncall, " +
                                                                                     "        hqkc.unside hq_unside,hqkc.uncook hq_uncook,hqkc.uncall hq_uncall " +
                                                                                     "        from (" +
                                                                                     "        select row_number() over (partition by a.pluno,a.unit order by effdate desc) as rn,a.* from dcp_bom a" +
                                                                                     "        left join dcp_bom_range b on a.eid=b.eid and a.bomno=b.bomno and b.shopid ='"+shopNo+"'" +
                                                                                     "        where a.eId='"+eId_para+"' and a.effdate <=trunc(sysdate) and a.status='100' and a.bomtype = '0'" +
                                                                                     "        and a.pluno='"+map_order_detail.get("PLUNO").toString() +"' and a.unit='"+map_order_detail.get("SUNIT").toString()+"'" +
                                                                                     "        and (a.restrictshop=0 or (a.restrictshop=1 and b.shopid is not null))" +
                                                                                     "        )a" +
                                                                                     "        inner join dcp_bom_material c on a.bomno=c.bomno and c.eid='"+eId_para+"' and c.material_bdate <=trunc(sysdate) and material_edate >=trunc(sysdate)" +
                                                                                     "        left  join dcp_goods_lang mgl on mgl.eid=a.eid and mgl.pluno=c.material_pluno and mgl.lang_type='"+req.getLangType()+"'" +
                                                                                     "        left  join dcp_unit_lang mul on mul.eid=a.eid and mul.unit=c.material_unit and mul.lang_type='"+req.getLangType()+"'" +
                                                                                     "        left  join dcp_unit mu on mu.eid=a.eid and mu.unit=c.material_unit and mu.status='100'" +
                                                                                     "        inner join dcp_goods mg on mg.eid=a.eid and mg.pluno=c.material_pluno and mg.status='100'" +
                                                                                     "        left  join dcp_unit_lang mbul on mbul.eid=a.eid and mbul.unit=mg.baseunit and mbul.lang_type='"+req.getLangType()+"'" +
                                                                                     "        inner join dcp_goods_unit mgu on mgu.eid =a.eid and mgu.pluno=c.material_pluno and mgu.ounit=c.material_unit and mgu.unit=mg.baseunit" +
                                                                                     "        left join DCP_KDSDISHES_CONTROL kc on a.eid=kc.eid and kc.shopid='"+shopNo+"' and c.material_pluno=kc.pluno " +
                                                                                     "        left join dcp_hqkdsdishes_control hqkc on a.eid = hqkc.eid and ((hqkc.goodstype=2 and  c.material_pluno=hqkc.id) or (hqkc.goodstype=1 and mg.category=hqkc.id)) " +
                                                                                     "        where a.rn=1" +
                                                                                     "        order by a.pluno,c.sortid");

                                        List<Map<String, Object>> temp_Bom = this.doQueryData(sb_bom.toString(), null);

                                        if (temp_Bom != null && temp_Bom.size()>0)
                                        {
                                            for (Map<String, Object> map_Bom : temp_Bom)
                                            {
                                                //增加过滤掉预制菜品的数量,剩余数量再进行处理,此表无数量字段，1条记录就是数量1
                                                List<Map<String,Object>> temp_beforeDish=getData_beforDishTask.stream()
                                                        .filter(p->p.get("PLUNO").toString().equals(map_Bom.get("MATERIAL_PLUNO"))
                                                                && p.get("UNITID").toString().equals(map_Bom.get("MATERIAL_UNIT")))
                                                        .collect(Collectors.toList());
                                                //剩余数量= 10份做30分，3份能做9份
                                                //a=g.getQty()=3,b=qty=10
                                                //c=MATERIAL_QTY=30 d=?
                                                //d=a*c/b=9
                                                BigDecimal bdm_remainQty=new BigDecimal(map_order_detail.get("QTY").toString()).multiply(new BigDecimal(Convert.toDouble(map_Bom.get("MATERIAL_QTY").toString(), 1d))).divide(new BigDecimal(Convert.toDouble(map_Bom.get("QTY").toString(), 1d)), 2, RoundingMode.HALF_UP);

                                                if (temp_beforeDish != null && temp_beforeDish.size()>0)
                                                {
                                                    for (Map<String, Object> Map_BeforeDish : temp_beforeDish)
                                                    {
                                                        pItem+=1;

                                                        //预制菜品,任务明细表预制数据剩余数量是>0且<=1
                                                        //相比看谁大
                                                        //1.5>1,    全减成0
                                                        //0.3<0.5   0.5-0.3=0.2
                                                        BigDecimal bdm_before=new BigDecimal(Map_BeforeDish.get("AVAILQTY").toString());

                                                        //最后剩余可用量
                                                        BigDecimal bdm_newAvailQty=new BigDecimal(0);

                                                        if (bdm_remainQty.compareTo(bdm_before)>=0)
                                                        {
                                                            bdm_newAvailQty=new BigDecimal(0);
                                                            //数量减 =全减成0
                                                            bdm_remainQty=bdm_remainQty.subtract(bdm_before);

                                                            //占用记录移除，避免临时变量同一单相同商品未过滤，被重复使用
                                                            getData_beforDishTask.remove(Map_BeforeDish);
                                                        }
                                                        else
                                                        {
                                                            bdm_newAvailQty=bdm_before.subtract(bdm_remainQty);
                                                            //数量减
                                                            bdm_remainQty=new BigDecimal(0);
                                                        }

                                                        //更新
                                                        UptBean up = new UptBean("DCP_PROCESSTASK_DETAIL");
                                                        up.addUpdateValue("AVAILQTY", new DataValue(bdm_newAvailQty, Types.VARCHAR));
                                                        //非预制菜判断,预制菜只判断KDS_uncall就行
                                                        if ("Y".equals(KDS_uncall))
                                                        {
                                                            up.addUpdateValue("GOODSSTATUS", new DataValue("3", Types.VARCHAR));
                                                        }

                                                        up.addCondition("EID", new DataValue(Map_BeforeDish.get("EID").toString(), Types.VARCHAR));
                                                        up.addCondition("SHOPID", new DataValue(Map_BeforeDish.get("SHOPID").toString(), Types.VARCHAR));
                                                        up.addCondition("PROCESSTASKNO", new DataValue(Map_BeforeDish.get("PROCESSTASKNO").toString(), Types.VARCHAR));
                                                        up.addCondition("ITEM", new DataValue(Map_BeforeDish.get("ITEM").toString(), Types.VARCHAR));

                                                        this.addProcessData(new DataProcessBean(up));


                                                        //占用记录
                                                        String[] columns_beforeDishTask = {"EID", "SHOPID", "OFNO", "ITEM", "PLUNO", "UNITID",
                                                                "OTYPE", "BILLNO", "CREATEOPID", "CREATEOPNAME", "CREATETIME", "PLUBARCODE", "USEQTY"};
                                                        DataValue[] insValue_beforeDishTask = new DataValue[]{
                                                                new DataValue(eId_para, Types.VARCHAR),
                                                                new DataValue(shopNo, Types.VARCHAR),
                                                                new DataValue(orderNo, Types.VARCHAR),
                                                                new DataValue(Double.valueOf(pItem), Types.VARCHAR),
                                                                new DataValue(Convert.toStr(map_Bom.get("MATERIAL_PLUNO"),""), Types.VARCHAR),
                                                                new DataValue(Convert.toStr(map_Bom.get("MATERIAL_UNIT"),""), Types.VARCHAR),
                                                                new DataValue("ORDER", Types.VARCHAR),
                                                                new DataValue(Map_BeforeDish.get("PROCESSTASKNO").toString(), Types.VARCHAR),
                                                                new DataValue(req.getOpNO(), Types.VARCHAR),
                                                                new DataValue(req.getOpName(), Types.VARCHAR),
                                                                new DataValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), Types.VARCHAR),
                                                                new DataValue("", Types.VARCHAR),
                                                                new DataValue(bdm_before.subtract(bdm_newAvailQty).doubleValue(), Types.FLOAT),

                                                        };
                                                        InsBean ib_beforeDishTask = new InsBean("DCP_BEFOREDISHTASK", columns_beforeDishTask);
                                                        ib_beforeDishTask.addValues(insValue_beforeDishTask);
                                                        this.addProcessData(new DataProcessBean(ib_beforeDishTask));

                                                    }
                                                }

                                                //BOM子品要重新取值

                                                /**
                                                 * KDS 免配菜Y/N
                                                 */
                                                String KDS_unside_sub="N";
                                                /**
                                                 * KDS 免制作
                                                 */
                                                String KDS_uncook_sub="N";
                                                /**
                                                 * KDS 免传菜
                                                 */
                                                String KDS_uncall_sub="N";

                                                KDS_unside_sub=Convert.toStr(map_Bom.get("UNSIDE"),"N");
                                                KDS_uncook_sub=Convert.toStr(map_Bom.get("UNCOOK"),"N");
                                                KDS_uncall_sub=Convert.toStr(map_Bom.get("UNCALL"),"N");

                                                //全部空表示未设置，此条记录无效，取总部
                                                if (KDS_unside_sub.equals("")&& KDS_uncook_sub.equals("")&&KDS_uncall_sub.equals(""))
                                                {
                                                    KDS_unside_sub=Convert.toStr(map_Bom.get("HQ_UNSIDE"),"N");
                                                    KDS_uncook_sub=Convert.toStr(map_Bom.get("HQ_UNCOOK"),"N");
                                                    KDS_uncall_sub=Convert.toStr(map_Bom.get("HQ_UNCALL"),"N");
                                                }

                                                //购买数量>1,按数量1拆分
                                                if (bdm_remainQty.compareTo(new BigDecimal(1))>0)
                                                {
                                                    double dCopys=bdm_remainQty.doubleValue()/1;
                                                    int iCopys=(int)Math.floor(dCopys);
                                                    dMode=bdm_remainQty.doubleValue()%1;//余数

                                                    for (int ic = 0; ic < iCopys; ic++)
                                                    {
                                                        //单品预制菜不够扣的打标
                                                        b_PluBeforeDishFlag=true;

                                                        //整单预制菜不够扣打标,整单一次true就够了，不要被覆盖
                                                        if (!b_orderBeforeDishFlag)
                                                        {
                                                            b_orderBeforeDishFlag=true;
                                                        }

                                                        pItem+=1;


                                                        //非预制菜判断,预制菜只判断KDS_uncall就行
                                                        String processTaskDetail_goodsstatus="0";
                                                        if ("Y".equals(KDS_unside_sub))
                                                        {
                                                            processTaskDetail_goodsstatus="1";
                                                        }
                                                        if ("Y".equals(KDS_uncook_sub))
                                                        {
                                                            processTaskDetail_goodsstatus="2";
                                                        }
                                                        if ("Y".equals(KDS_uncall_sub))
                                                        {
                                                            processTaskDetail_goodsstatus="3";
                                                        }

                                                        //
                                                        DataValue[] insValue_processTask_detail = new DataValue[]{
                                                                new DataValue(eId_para, Types.VARCHAR),
                                                                new DataValue(shopNo, Types.VARCHAR),
                                                                new DataValue(processTaskNO, Types.VARCHAR),
                                                                new DataValue(shopNo, Types.VARCHAR),
                                                                new DataValue(pItem, Types.VARCHAR),
                                                                new DataValue(Convert.toDouble(map_Bom.get("MULQTY"),1d), Types.VARCHAR),
                                                                new DataValue(1d, Types.FLOAT),
                                                                new DataValue(0d, Types.VARCHAR),
                                                                new DataValue(Convert.toStr(map_Bom.get("MATERIAL_UNIT")), Types.VARCHAR),
                                                                new DataValue(new BigDecimal(1).multiply(new BigDecimal(Convert.toStr(map_Bom.get("MATERIALUNITRATIO")))).doubleValue(), Types.FLOAT),
                                                                new DataValue(Convert.toStr(map_Bom.get("MATERIAL_PLUNO"),""), Types.VARCHAR),
                                                                new DataValue(Convert.toStr(map_Bom.get("MATERIALPLUNAME"),""), Types.VARCHAR),
                                                                new DataValue(Convert.toDouble(map_Bom.get("PRICE"),0d), Types.VARCHAR),
                                                                new DataValue(Convert.toStr(map_Bom.get("MATERIALBASEUNIT"),""), Types.VARCHAR),
                                                                new DataValue(Convert.toDouble(map_Bom.get("MATERIALUNITRATIO"),1d), Types.VARCHAR),
                                                                new DataValue(new BigDecimal(1).multiply(new BigDecimal(Convert.toDouble(map_Bom.get("PRICE"),0d))).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(), Types.FLOAT),
                                                                new DataValue(map_order_detail.get("PRICE").toString(), Types.VARCHAR),
                                                                new DataValue(new BigDecimal(1).multiply(new BigDecimal(map_order_detail.get("PRICE").toString())).setScale(2, RoundingMode.HALF_UP).doubleValue(), Types.FLOAT),
                                                                new DataValue(bdate, Types.VARCHAR),
                                                                new DataValue(orderNo, Types.VARCHAR),
                                                                new DataValue(map_order_detail.get("ITEM").toString(), Types.VARCHAR),
                                                                new DataValue(processTaskDetail_goodsstatus, Types.VARCHAR),
                                                                new DataValue(opNo, Types.VARCHAR),
                                                                new DataValue(map_order_detail.get("SPECNAME").toString(), Types.VARCHAR),
                                                                new DataValue(map_order_detail.get("SUNIT").toString(), Types.VARCHAR),
                                                                new DataValue(map_order_detail.get("SUNITNAME").toString(), Types.VARCHAR),
                                                                new DataValue("", Types.VARCHAR),
                                                                new DataValue(map_order_detail.get("PACKAGETYPE").toString().equals("2") ? "Y" : "N", Types.VARCHAR),
                                                                new DataValue("", Types.VARCHAR),
                                                                new DataValue("2", Types.VARCHAR),
                                                                new DataValue(dMemo.toString(), Types.VARCHAR),
                                                                new DataValue("N", Types.VARCHAR),
                                                                new DataValue(0, Types.VARCHAR),
                                                                new DataValue(map_order_detail.get("CATEGORY").toString(), Types.VARCHAR),
                                                        };
                                                        InsBean ib_processTask_detail = new InsBean("dcp_processtask_detail", columns_processTask_detail);
                                                        ib_processTask_detail.addValues(insValue_processTask_detail);
                                                        this.addProcessData(new DataProcessBean(ib_processTask_detail));

                                                        bHasProcessTaskDetail=true;

                                                        //占用记录
                                                        String[] columns_beforeDishTask = {"EID", "SHOPID", "OFNO", "ITEM", "PLUNO", "UNITID",
                                                                "OTYPE", "BILLNO", "CREATEOPID", "CREATEOPNAME", "CREATETIME", "PLUBARCODE", "USEQTY"};
                                                        DataValue[] insValue_beforeDishTask = new DataValue[]{
                                                                new DataValue(eId_para, Types.VARCHAR),
                                                                new DataValue(shopNo, Types.VARCHAR),
                                                                new DataValue(orderNo, Types.VARCHAR),
                                                                new DataValue(Double.valueOf(pItem), Types.VARCHAR),
                                                                new DataValue(Convert.toStr(map_Bom.get("MATERIAL_PLUNO"),""), Types.VARCHAR),
                                                                new DataValue(Convert.toStr(map_Bom.get("MATERIAL_UNIT"),""), Types.VARCHAR),
                                                                new DataValue("ORDER", Types.VARCHAR),
                                                                new DataValue(processTaskNO, Types.VARCHAR),
                                                                new DataValue(req.getOpNO(), Types.VARCHAR),
                                                                new DataValue(req.getOpName(), Types.VARCHAR),
                                                                new DataValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), Types.VARCHAR),
                                                                new DataValue("", Types.VARCHAR),
                                                                new DataValue(1d, Types.FLOAT),

                                                        };
                                                        InsBean ib_beforeDishTask = new InsBean("DCP_BEFOREDISHTASK", columns_beforeDishTask);
                                                        ib_beforeDishTask.addValues(insValue_beforeDishTask);
                                                        this.addProcessData(new DataProcessBean(ib_beforeDishTask));

                                                    }
                                                }
                                                else
                                                {
                                                    dMode=bdm_remainQty.doubleValue();
                                                }
                                                //取余的单独处理1笔
                                                if (new BigDecimal(dMode).compareTo(BigDecimal.ZERO)>0)
                                                {
                                                    //单品预制菜不够扣的打标
                                                    b_PluBeforeDishFlag=true;

                                                    //整单预制菜不够扣打标,整单一次true就够了，不要被覆盖
                                                    if (!b_orderBeforeDishFlag)
                                                    {
                                                        b_orderBeforeDishFlag=true;
                                                    }

                                                    pItem+=1;


                                                    //非预制菜判断,预制菜只判断KDS_uncall就行
                                                    String processTaskDetail_goodsstatus="0";
                                                    if ("Y".equals(KDS_unside_sub))
                                                    {
                                                        processTaskDetail_goodsstatus="1";
                                                    }
                                                    if ("Y".equals(KDS_uncook_sub))
                                                    {
                                                        processTaskDetail_goodsstatus="2";
                                                    }
                                                    if ("Y".equals(KDS_uncall_sub))
                                                    {
                                                        processTaskDetail_goodsstatus="3";
                                                    }
                                                    //
                                                    DataValue[] insValue_processTask_detail = new DataValue[]{
                                                            new DataValue(eId_para, Types.VARCHAR),
                                                            new DataValue(shopNo, Types.VARCHAR),
                                                            new DataValue(processTaskNO, Types.VARCHAR),
                                                            new DataValue(shopNo, Types.VARCHAR),
                                                            new DataValue(pItem, Types.VARCHAR),
                                                            new DataValue(Convert.toDouble(map_Bom.get("MULQTY"),1d), Types.VARCHAR),
                                                            new DataValue(new BigDecimal(dMode).doubleValue(), Types.FLOAT),
                                                            new DataValue(0d, Types.VARCHAR),
                                                            new DataValue(Convert.toStr(map_Bom.get("MATERIAL_UNIT")), Types.VARCHAR),
                                                            new DataValue(new BigDecimal(dMode).multiply(new BigDecimal(Convert.toStr(map_Bom.get("MATERIALUNITRATIO")))).doubleValue(), Types.FLOAT),
                                                            new DataValue(Convert.toStr(map_Bom.get("MATERIAL_PLUNO"),""), Types.VARCHAR),
                                                            new DataValue(Convert.toStr(map_Bom.get("MATERIALPLUNAME"),""), Types.VARCHAR),
                                                            new DataValue(Convert.toDouble(map_Bom.get("PRICE"),0d), Types.VARCHAR),
                                                            new DataValue(Convert.toStr(map_Bom.get("MATERIALBASEUNIT"),""), Types.VARCHAR),
                                                            new DataValue(Convert.toDouble(map_Bom.get("MATERIALUNITRATIO"),1d), Types.VARCHAR),
                                                            new DataValue(new BigDecimal(dMode).multiply(new BigDecimal(Convert.toDouble(map_Bom.get("PRICE"),0d))).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(), Types.FLOAT),
                                                            new DataValue(map_order_detail.get("PRICE").toString(), Types.VARCHAR),
                                                            new DataValue(new BigDecimal(dMode).multiply(new BigDecimal(map_order_detail.get("PRICE").toString())).setScale(2, RoundingMode.HALF_UP).doubleValue(), Types.FLOAT),
                                                            new DataValue(bdate, Types.VARCHAR),
                                                            new DataValue(orderNo, Types.VARCHAR),
                                                            new DataValue(map_order_detail.get("ITEM").toString(), Types.VARCHAR),
                                                            new DataValue(processTaskDetail_goodsstatus, Types.VARCHAR),
                                                            new DataValue(opNo, Types.VARCHAR),
                                                            new DataValue(map_order_detail.get("SPECNAME").toString(), Types.VARCHAR),
                                                            new DataValue(map_order_detail.get("SUNIT").toString(), Types.VARCHAR),
                                                            new DataValue(map_order_detail.get("SUNITNAME").toString(), Types.VARCHAR),
                                                            new DataValue("", Types.VARCHAR),
                                                            new DataValue(map_order_detail.get("PACKAGETYPE").toString().equals("2") ? "Y" : "N", Types.VARCHAR),
                                                            new DataValue("", Types.VARCHAR),
                                                            new DataValue("2", Types.VARCHAR),
                                                            new DataValue(dMemo.toString(), Types.VARCHAR),
                                                            new DataValue("N", Types.VARCHAR),
                                                            new DataValue(0, Types.VARCHAR),
                                                            new DataValue(map_order_detail.get("CATEGORY").toString(), Types.VARCHAR),
                                                    };
                                                    InsBean ib_processTask_detail = new InsBean("dcp_processtask_detail", columns_processTask_detail);
                                                    ib_processTask_detail.addValues(insValue_processTask_detail);
                                                    this.addProcessData(new DataProcessBean(ib_processTask_detail));

                                                    bHasProcessTaskDetail=true;

                                                    //占用记录
                                                    String[] columns_beforeDishTask = {"EID", "SHOPID", "OFNO", "ITEM", "PLUNO", "UNITID",
                                                            "OTYPE", "BILLNO", "CREATEOPID", "CREATEOPNAME", "CREATETIME", "PLUBARCODE", "USEQTY"};
                                                    DataValue[] insValue_beforeDishTask = new DataValue[]{
                                                            new DataValue(eId_para, Types.VARCHAR),
                                                            new DataValue(shopNo, Types.VARCHAR),
                                                            new DataValue(orderNo, Types.VARCHAR),
                                                            new DataValue(Double.valueOf(pItem), Types.VARCHAR),
                                                            new DataValue(Convert.toStr(map_Bom.get("MATERIAL_PLUNO"),""), Types.VARCHAR),
                                                            new DataValue(Convert.toStr(map_Bom.get("MATERIAL_UNIT"),""), Types.VARCHAR),
                                                            new DataValue("ORDER", Types.VARCHAR),
                                                            new DataValue(processTaskNO, Types.VARCHAR),
                                                            new DataValue(req.getOpNO(), Types.VARCHAR),
                                                            new DataValue(req.getOpName(), Types.VARCHAR),
                                                            new DataValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), Types.VARCHAR),
                                                            new DataValue("", Types.VARCHAR),
                                                            new DataValue(dMode, Types.FLOAT),

                                                    };
                                                    InsBean ib_beforeDishTask = new InsBean("DCP_BEFOREDISHTASK", columns_beforeDishTask);
                                                    ib_beforeDishTask.addValues(insValue_beforeDishTask);
                                                    this.addProcessData(new DataProcessBean(ib_beforeDishTask));

                                                }

                                            }
                                        }
                                    }
                                    else
                                    {

                                        //*******************************非双拼菜品****************************************
                                        //增加过滤掉预制菜品的数量,剩余数量再进行处理,此表无数量字段，1条记录就是数量1
                                        List<Map<String,Object>> temp_beforeDish=getData_beforDishTask.stream()
                                                .filter(p->p.get("PLUNO").toString().equals(map_order_detail.get("PLUNO").toString())
                                                        && (Convert.toStr(p.get("PLUBARCODE"),"").equals(Convert.toStr(map_order_detail.get("PLUBARCODE").toString(),"")) || Convert.toStr(map_order_detail.get("PLUBARCODE").toString(),"").equals(""))
                                                        && (Convert.toStr(p.get("UNITID"),"").equals(Convert.toStr(map_order_detail.get("SUNIT").toString(),""))||Convert.toStr(map_order_detail.get("SUNIT").toString(),"").equals("") ) )
                                                .collect(Collectors.toList());
                                        //剩余数量=1.5/0.3
                                        BigDecimal bdm_remainQty=new BigDecimal(map_order_detail.get("QTY").toString());

                                        if (temp_beforeDish != null && temp_beforeDish.size()>0)
                                        {
                                            for (Map<String, Object> Map_BeforeDish : temp_beforeDish)
                                            {
                                                pItem+=1;

                                                //预制菜品,任务明细表预制数据剩余数量是>0且<=1
                                                //相比看谁大
                                                //1.5>1,    全减成0
                                                //0.3<0.5   0.5-0.3=0.2
                                                BigDecimal bdm_before=new BigDecimal(Map_BeforeDish.get("AVAILQTY").toString());

                                                //最后剩余可用量
                                                BigDecimal bdm_newAvailQty=new BigDecimal(0);

                                                if (bdm_remainQty.compareTo(bdm_before)>=0)
                                                {
                                                    bdm_newAvailQty=new BigDecimal(0);
                                                    //数量减 =全减成0
                                                    bdm_remainQty=bdm_remainQty.subtract(bdm_before);

                                                    //占用记录移除，避免临时变量同一单相同商品未过滤，被重复使用
                                                    getData_beforDishTask.remove(Map_BeforeDish);
                                                }
                                                else
                                                {
                                                    bdm_newAvailQty=bdm_before.subtract(bdm_remainQty);
                                                    //数量减
                                                    bdm_remainQty=new BigDecimal(0);
                                                }


                                                //更新
                                                UptBean up = new UptBean("DCP_PROCESSTASK_DETAIL");
                                                up.addUpdateValue("AVAILQTY", new DataValue(bdm_newAvailQty, Types.VARCHAR));

                                                //非预制菜判断,预制菜只判断KDS_uncall就行
                                                if ("Y".equals(KDS_uncall))
                                                {
                                                    up.addUpdateValue("GOODSSTATUS", new DataValue("3", Types.VARCHAR));
                                                }

                                                up.addCondition("EID", new DataValue(Map_BeforeDish.get("EID").toString(), Types.VARCHAR));
                                                up.addCondition("SHOPID", new DataValue(Map_BeforeDish.get("SHOPID").toString(), Types.VARCHAR));
                                                up.addCondition("PROCESSTASKNO", new DataValue(Map_BeforeDish.get("PROCESSTASKNO").toString(), Types.VARCHAR));
                                                up.addCondition("ITEM", new DataValue(Map_BeforeDish.get("ITEM").toString(), Types.VARCHAR));

                                                this.addProcessData(new DataProcessBean(up));


                                                //占用记录
                                                String[] columns_beforeDishTask = {"EID", "SHOPID", "OFNO", "ITEM", "PLUNO", "UNITID",
                                                        "OTYPE", "BILLNO", "CREATEOPID", "CREATEOPNAME", "CREATETIME", "PLUBARCODE", "USEQTY"};
                                                DataValue[] insValue_beforeDishTask = new DataValue[]{
                                                        new DataValue(eId_para, Types.VARCHAR),
                                                        new DataValue(shopNo, Types.VARCHAR),
                                                        new DataValue(orderNo, Types.VARCHAR),
                                                        new DataValue(Double.valueOf(pItem), Types.VARCHAR),
                                                        new DataValue(Convert.toStr(map_order_detail.get("PLUNO").toString(),""), Types.VARCHAR),
                                                        new DataValue(Convert.toStr(map_order_detail.get("SUNIT").toString(),""), Types.VARCHAR),
                                                        new DataValue("ORDER", Types.VARCHAR),
                                                        new DataValue(Map_BeforeDish.get("PROCESSTASKNO").toString(), Types.VARCHAR),
                                                        new DataValue(req.getOpNO(), Types.VARCHAR),
                                                        new DataValue(req.getOpName(), Types.VARCHAR),
                                                        new DataValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), Types.VARCHAR),
                                                        new DataValue("", Types.VARCHAR),
                                                        new DataValue(bdm_before.subtract(bdm_newAvailQty).doubleValue(), Types.FLOAT),

                                                };
                                                InsBean ib_beforeDishTask = new InsBean("DCP_BEFOREDISHTASK", columns_beforeDishTask);
                                                ib_beforeDishTask.addValues(insValue_beforeDishTask);
                                                this.addProcessData(new DataProcessBean(ib_beforeDishTask));

                                            }
                                        }

                                        //购买数量>1,按数量1拆分
                                        if (bdm_remainQty.compareTo(new BigDecimal(1))>0)
                                        {
                                            double dCopys=bdm_remainQty.doubleValue()/1;
                                            int iCopys=(int)Math.floor(dCopys);
                                            dMode=bdm_remainQty.doubleValue()%1;//余数

                                            for (int ic = 0; ic < iCopys; ic++)
                                            {
                                                //单品预制菜不够扣的打标
                                                b_PluBeforeDishFlag=true;

                                                //整单预制菜不够扣打标,整单一次true就够了，不要被覆盖
                                                if (!b_orderBeforeDishFlag)
                                                {
                                                    b_orderBeforeDishFlag=true;
                                                }

                                                pItem+=1;

                                                //非预制菜判断,预制菜只判断KDS_uncall就行
                                                String processTaskDetail_goodsstatus="0";
                                                if ("Y".equals(KDS_unside))
                                                {
                                                    processTaskDetail_goodsstatus="1";
                                                }
                                                if ("Y".equals(KDS_uncook))
                                                {
                                                    processTaskDetail_goodsstatus="2";
                                                }
                                                if (map_order_detail.get("PACKAGETYPE").toString().equals("2"))
                                                {
                                                    processTaskDetail_goodsstatus="2";
                                                }
                                                if ("Y".equals(KDS_uncall))
                                                {
                                                    processTaskDetail_goodsstatus="3";
                                                }
                                                //
                                                DataValue[] insValue_processTask_detail = new DataValue[]{
                                                        new DataValue(eId_para, Types.VARCHAR),
                                                        new DataValue(shopNo, Types.VARCHAR),
                                                        new DataValue(processTaskNO, Types.VARCHAR),
                                                        new DataValue(shopNo, Types.VARCHAR),
                                                        new DataValue(pItem, Types.VARCHAR),
                                                        new DataValue(1d, Types.VARCHAR),
                                                        new DataValue(1d, Types.FLOAT),
                                                        new DataValue(0d, Types.VARCHAR),
                                                        new DataValue(Convert.toStr(map_order_detail.get("SUNIT")), Types.VARCHAR),
                                                        new DataValue(new BigDecimal(1).multiply(new BigDecimal(1)).doubleValue(), Types.FLOAT),
                                                        new DataValue(Convert.toStr(map_order_detail.get("PLUNO"),""), Types.VARCHAR),
                                                        new DataValue(Convert.toStr(map_order_detail.get("PLUNAME"),""), Types.VARCHAR),
                                                        new DataValue(Convert.toDouble(map_order_detail.get("PRICE"),0d), Types.VARCHAR),
                                                        new DataValue(Convert.toStr(map_order_detail.get("SUNIT"),""), Types.VARCHAR),
                                                        new DataValue(1d, Types.VARCHAR),
                                                        new DataValue(new BigDecimal(1).multiply(new BigDecimal(Convert.toDouble(map_order_detail.get("PRICE"),0d))).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(), Types.FLOAT),
                                                        new DataValue(map_order_detail.get("PRICE").toString(), Types.VARCHAR),
                                                        new DataValue(new BigDecimal(1).multiply(new BigDecimal(map_order_detail.get("PRICE").toString())).setScale(2, RoundingMode.HALF_UP).doubleValue(), Types.FLOAT),
                                                        new DataValue(bdate, Types.VARCHAR),
                                                        new DataValue(orderNo, Types.VARCHAR),
                                                        new DataValue(map_order_detail.get("ITEM").toString(), Types.VARCHAR),
                                                        new DataValue(processTaskDetail_goodsstatus, Types.VARCHAR),
                                                        new DataValue(opNo, Types.VARCHAR),
                                                        new DataValue(map_order_detail.get("SPECNAME").toString(), Types.VARCHAR),
                                                        new DataValue(map_order_detail.get("SUNIT").toString(), Types.VARCHAR),
                                                        new DataValue(map_order_detail.get("SUNITNAME").toString(), Types.VARCHAR),
                                                        new DataValue("", Types.VARCHAR),
                                                        new DataValue(map_order_detail.get("PACKAGETYPE").toString().equals("2") ? "Y" : "N", Types.VARCHAR),
                                                        new DataValue("", Types.VARCHAR),
                                                        new DataValue("2", Types.VARCHAR),
                                                        new DataValue(dMemo.toString(), Types.VARCHAR),
                                                        new DataValue("N", Types.VARCHAR),
                                                        new DataValue(0, Types.VARCHAR),
                                                        new DataValue(map_order_detail.get("CATEGORY").toString(), Types.VARCHAR),
                                                };
                                                InsBean ib_processTask_detail = new InsBean("dcp_processtask_detail", columns_processTask_detail);
                                                ib_processTask_detail.addValues(insValue_processTask_detail);
                                                this.addProcessData(new DataProcessBean(ib_processTask_detail));

                                                bHasProcessTaskDetail=true;

                                                //占用记录
                                                String[] columns_beforeDishTask = {"EID", "SHOPID", "OFNO", "ITEM", "PLUNO", "UNITID",
                                                        "OTYPE", "BILLNO", "CREATEOPID", "CREATEOPNAME", "CREATETIME", "PLUBARCODE", "USEQTY"};
                                                DataValue[] insValue_beforeDishTask = new DataValue[]{
                                                        new DataValue(eId_para, Types.VARCHAR),
                                                        new DataValue(shopNo, Types.VARCHAR),
                                                        new DataValue(orderNo, Types.VARCHAR),
                                                        new DataValue(Double.valueOf(pItem), Types.VARCHAR),
                                                        new DataValue(Convert.toStr(map_order_detail.get("PLUNO"),""), Types.VARCHAR),
                                                        new DataValue(Convert.toStr(map_order_detail.get("SUNIT"),""), Types.VARCHAR),
                                                        new DataValue("ORDER", Types.VARCHAR),
                                                        new DataValue(processTaskNO, Types.VARCHAR),
                                                        new DataValue(req.getOpNO(), Types.VARCHAR),
                                                        new DataValue(req.getOpName(), Types.VARCHAR),
                                                        new DataValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), Types.VARCHAR),
                                                        new DataValue("", Types.VARCHAR),
                                                        new DataValue(1d, Types.FLOAT),

                                                };
                                                InsBean ib_beforeDishTask = new InsBean("DCP_BEFOREDISHTASK", columns_beforeDishTask);
                                                ib_beforeDishTask.addValues(insValue_beforeDishTask);
                                                this.addProcessData(new DataProcessBean(ib_beforeDishTask));

                                            }
                                        }
                                        else
                                        {
                                            dMode=bdm_remainQty.doubleValue();
                                        }
                                        //取余的单独处理1笔
                                        if (new BigDecimal(dMode).compareTo(BigDecimal.ZERO)>0)
                                        {
                                            //单品预制菜不够扣的打标
                                            b_PluBeforeDishFlag=true;

                                            //整单预制菜不够扣打标,整单一次true就够了，不要被覆盖
                                            if (!b_orderBeforeDishFlag)
                                            {
                                                b_orderBeforeDishFlag=true;
                                            }

                                            pItem+=1;

                                            //非预制菜判断,预制菜只判断KDS_uncall就行
                                            String processTaskDetail_goodsstatus="0";
                                            if ("Y".equals(KDS_unside))
                                            {
                                                processTaskDetail_goodsstatus="1";
                                            }
                                            if ("Y".equals(KDS_uncook))
                                            {
                                                processTaskDetail_goodsstatus="2";
                                            }
                                            if (map_order_detail.get("PACKAGETYPE").toString().equals("2"))
                                            {
                                                processTaskDetail_goodsstatus="2";
                                            }
                                            if ("Y".equals(KDS_uncall))
                                            {
                                                processTaskDetail_goodsstatus="3";
                                            }

                                            //
                                            DataValue[] insValue_processTask_detail = new DataValue[]{
                                                    new DataValue(eId_para, Types.VARCHAR),
                                                    new DataValue(shopNo, Types.VARCHAR),
                                                    new DataValue(processTaskNO, Types.VARCHAR),
                                                    new DataValue(shopNo, Types.VARCHAR),
                                                    new DataValue(pItem, Types.VARCHAR),
                                                    new DataValue(1d, Types.VARCHAR),
                                                    new DataValue(new BigDecimal(dMode).multiply(BigDecimal.valueOf(1d)).doubleValue(), Types.FLOAT),
                                                    new DataValue(0d, Types.VARCHAR),
                                                    new DataValue(Convert.toStr(map_order_detail.get("SUNIT")), Types.VARCHAR),
                                                    new DataValue(new BigDecimal(dMode).multiply(new BigDecimal(1d)).doubleValue(), Types.FLOAT),
                                                    new DataValue(Convert.toStr(map_order_detail.get("PLUNO"),""), Types.VARCHAR),
                                                    new DataValue(Convert.toStr(map_order_detail.get("PLUNAME"),""), Types.VARCHAR),
                                                    new DataValue(Convert.toDouble(map_order_detail.get("PRICE"),0d), Types.VARCHAR),
                                                    new DataValue(Convert.toStr(map_order_detail.get("SUNIT"),""), Types.VARCHAR),
                                                    new DataValue(1d, Types.VARCHAR),
                                                    new DataValue(new BigDecimal(dMode).multiply(new BigDecimal(Convert.toDouble(map_order_detail.get("PRICE"),0d))).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(), Types.FLOAT),
                                                    new DataValue(map_order_detail.get("PRICE").toString(), Types.VARCHAR),
                                                    new DataValue(new BigDecimal(dMode).multiply(new BigDecimal(map_order_detail.get("PRICE").toString())).setScale(2, RoundingMode.HALF_UP).doubleValue(), Types.FLOAT),
                                                    new DataValue(bdate, Types.VARCHAR),
                                                    new DataValue(orderNo, Types.VARCHAR),
                                                    new DataValue(map_order_detail.get("ITEM").toString(), Types.VARCHAR),
                                                    new DataValue(processTaskDetail_goodsstatus, Types.VARCHAR),
                                                    new DataValue(opNo, Types.VARCHAR),
                                                    new DataValue(map_order_detail.get("SPECNAME").toString(), Types.VARCHAR),
                                                    new DataValue(map_order_detail.get("SUNIT").toString(), Types.VARCHAR),
                                                    new DataValue(map_order_detail.get("SUNITNAME").toString(), Types.VARCHAR),
                                                    new DataValue("", Types.VARCHAR),
                                                    new DataValue(map_order_detail.get("PACKAGETYPE").toString().equals("2") ? "Y" : "N", Types.VARCHAR),
                                                    new DataValue("", Types.VARCHAR),
                                                    new DataValue("2", Types.VARCHAR),
                                                    new DataValue(dMemo.toString(), Types.VARCHAR),
                                                    new DataValue("N", Types.VARCHAR),
                                                    new DataValue(0, Types.VARCHAR),
                                                    new DataValue(map_order_detail.get("CATEGORY").toString(), Types.VARCHAR),
                                            };
                                            InsBean ib_processTask_detail = new InsBean("dcp_processtask_detail", columns_processTask_detail);
                                            ib_processTask_detail.addValues(insValue_processTask_detail);
                                            this.addProcessData(new DataProcessBean(ib_processTask_detail));

                                            bHasProcessTaskDetail=true;

                                            //占用记录
                                            String[] columns_beforeDishTask = {"EID", "SHOPID", "OFNO", "ITEM", "PLUNO", "UNITID",
                                                    "OTYPE", "BILLNO", "CREATEOPID", "CREATEOPNAME", "CREATETIME", "PLUBARCODE", "USEQTY"};
                                            DataValue[] insValue_beforeDishTask = new DataValue[]{
                                                    new DataValue(eId_para, Types.VARCHAR),
                                                    new DataValue(shopNo, Types.VARCHAR),
                                                    new DataValue(orderNo, Types.VARCHAR),
                                                    new DataValue(Double.valueOf(pItem), Types.VARCHAR),
                                                    new DataValue(Convert.toStr(map_order_detail.get("PLUNO"),""), Types.VARCHAR),
                                                    new DataValue(Convert.toStr(map_order_detail.get("SUNIT"),""), Types.VARCHAR),
                                                    new DataValue("ORDER", Types.VARCHAR),
                                                    new DataValue(processTaskNO, Types.VARCHAR),
                                                    new DataValue(req.getOpNO(), Types.VARCHAR),
                                                    new DataValue(req.getOpName(), Types.VARCHAR),
                                                    new DataValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), Types.VARCHAR),
                                                    new DataValue("", Types.VARCHAR),
                                                    new DataValue(dMode, Types.FLOAT),

                                            };
                                            InsBean ib_beforeDishTask = new InsBean("DCP_BEFOREDISHTASK", columns_beforeDishTask);
                                            ib_beforeDishTask.addValues(insValue_beforeDishTask);
                                            this.addProcessData(new DataProcessBean(ib_beforeDishTask));

                                        }

                                    }

                                    //此商品被预制菜处理了
                                    if (!b_PluBeforeDishFlag)
                                    {
                                        product_detail_goodsstatus="2";

                                        //非预制菜判断,预制菜只判断KDS_uncall就行
                                        if ("Y".equals(KDS_uncall))
                                        {
                                            product_detail_goodsstatus="3";
                                        }
                                        else
                                        {
                                            //一旦有1个商品不是免传菜，后面的生产任务就不能变3
                                            b_orderUnCallFlag=true;
                                        }
                                    }
                                    else
                                    {
                                        //预制菜不够扣，正常处理

                                        //非预制菜判断,预制菜只判断KDS_uncall就行
                                        if ("Y".equals(KDS_unside))
                                        {
                                            product_detail_goodsstatus="1";
                                        }
                                        else
                                        {
                                            if (map_order_detail.get("PACKAGETYPE").toString().equals("2"))
                                            {
                                                product_detail_goodsstatus="1";
                                            }
                                            else
                                            {
                                                //一旦有1个商品不是免配菜，后面的生产任务就不能变1
                                                b_orderUnSideFlag=true;
                                            }
                                        }
                                        if ("Y".equals(KDS_uncook))
                                        {
                                            product_detail_goodsstatus="2";
                                        }
                                        else
                                        {
                                            if (map_order_detail.get("PACKAGETYPE").toString().equals("2"))
                                            {
                                                product_detail_goodsstatus="2";
                                            }
                                            else
                                            {
                                                //一旦有1个商品不是免配菜，后面的生产任务就不能变2
                                                b_orderUnCookFlag=true;
                                            }
                                        }

                                        if ("Y".equals(KDS_uncall))
                                        {
                                            product_detail_goodsstatus="3";
                                        }
                                        else
                                        {
                                            //一旦有1个商品不是免传菜，后面的生产任务就不能变3
                                            b_orderUnCallFlag=true;
                                        }
                                    }




                                    //写零售生产单身
                                    String[] columns_productSale_detail = {"EID", "SHOPID", "BILLTYPE", "BILLNO", "OFNO", "OITEM",
                                            "PLUNO", "PLUNAME", "PLUBARCODE", "QTY", "SPECNAME", "UNITID",
                                            "UNITNAME", "FLAVORSTUFFDETAIL", "ISPACKAGE", "PGOODSDETAIL", "GOODSSTATUS"
                                            , "REPASTTYPE", "MEMO", "ISURGE", "FINALCATEGORY", "REFUNDQTY","ATTRNAME","PACKAGEMITEM","ISSTUFF","DETAILITEM"};
                                    //
                                    DataValue[] insValue_productSale_detail = new DataValue[]{
                                            new DataValue(eId_para, Types.VARCHAR),
                                            new DataValue(shopNo, Types.VARCHAR),
                                            new DataValue("ORDER", Types.VARCHAR),
                                            new DataValue(orderNo, Types.VARCHAR),
                                            new DataValue("", Types.VARCHAR),
                                            new DataValue(map_order_detail.get("ITEM").toString(), Types.VARCHAR),
                                            new DataValue(map_order_detail.get("PLUNO").toString(), Types.VARCHAR),
                                            new DataValue(map_order_detail.get("PLUNAME").toString(), Types.VARCHAR),
                                            new DataValue(map_order_detail.get("PLUBARCODE").toString(), Types.VARCHAR),
                                            new DataValue(map_order_detail.get("QTY").toString(), Types.FLOAT),
                                            new DataValue(map_order_detail.get("SPECNAME").toString(), Types.VARCHAR),
                                            new DataValue(map_order_detail.get("SUNIT").toString(), Types.VARCHAR),
                                            new DataValue(map_order_detail.get("SUNITNAME").toString(), Types.VARCHAR),
                                            new DataValue("", Types.VARCHAR),
                                            new DataValue(map_order_detail.get("PACKAGETYPE").toString().equals("2") ? "Y" : "N", Types.VARCHAR),
                                            new DataValue(pgoodsdetail.toString(), Types.VARCHAR),
                                            new DataValue(product_detail_goodsstatus, Types.VARCHAR),
                                            new DataValue("2", Types.VARCHAR),
                                            new DataValue(dMemo.toString(), Types.VARCHAR),
                                            new DataValue("N", Types.VARCHAR),
                                            new DataValue(map_order_detail.get("CATEGORY").toString(), Types.VARCHAR),
                                            new DataValue(0, Types.VARCHAR),
                                            new DataValue(map_order_detail.get("ATTRNAME").toString(), Types.VARCHAR),
                                            new DataValue(packageMitem, Types.VARCHAR),
                                            new DataValue(toppingtype.equals("3")?"Y":"N", Types.VARCHAR),
                                            new DataValue(toppingmitem, Types.VARCHAR),

                                    };
                                    InsBean ib_productSale_detail = new InsBean("dcp_product_detail", columns_productSale_detail);
                                    ib_productSale_detail.addValues(insValue_productSale_detail);
                                    this.addProcessData(new DataProcessBean(ib_productSale_detail));


                                    dMemo.setLength(0);
                                    dMemo = null;

                                }
                            }

                            //整单被预制菜处理了
                            if (!b_orderBeforeDishFlag)
                            {
                                productSale_status="2";

                                //非预制菜判断,预制菜只判断KDS_uncall就行
                                if (!b_orderUnCallFlag)
                                {
                                    productSale_status="3";
                                }
                            }
                            else
                            {
                                //预制菜不够扣，正常处理

                                if (!b_orderUnSideFlag)
                                {
                                    productSale_status="1";
                                }

                                if (!b_orderUnCookFlag)
                                {
                                    productSale_status="2";
                                }

                                if (!b_orderUnCallFlag)
                                {
                                    productSale_status="3";
                                }
                            }

                            //品种数
                            bdm_tot_cqty = new BigDecimal(temp_category.size());
                            temp_category.clear();
                            temp_category = null;

                            //
                            if (bHasProcessTaskDetail)
                            {
                                //写生产任务单头
                                String[] columns_processTask = {"EID", "SHOPID", "PROCESSTASKNO", "ORGANIZATIONNO", "CREATE_TIME", "CREATE_DATE",
                                        "CREATEBY", "STATUS", "TOT_CQTY", "TOT_AMT", "PROCESS_STATUS",
                                        "BDATE", "TOT_PQTY", "MEMO", "WAREHOUSE", "MATERIALWAREHOUSE"
                                        , "TOT_DISTRIAMT", "OFNO", "OTYPE", "CREATEDATETIME"};
                                //
                                DataValue[] insValue_processTask = new DataValue[]{
                                        new DataValue(eId_para, Types.VARCHAR),
                                        new DataValue(shopNo, Types.VARCHAR),
                                        new DataValue(processTaskNO, Types.VARCHAR),
                                        new DataValue(shopNo, Types.VARCHAR),
                                        new DataValue(new SimpleDateFormat("HHmmss").format(new Date()), Types.VARCHAR),
                                        new DataValue(new SimpleDateFormat("yyyyMMdd").format(new Date()), Types.VARCHAR),
                                        new DataValue(opNo, Types.VARCHAR),
                                        new DataValue("6", Types.VARCHAR),//5-新建  6-待加工  7-已完成
                                        new DataValue(bdm_tot_cqty, Types.VARCHAR),
                                        new DataValue(getData_Order.get(0).get("TOT_AMT").toString(), Types.FLOAT),
                                        new DataValue("N", Types.VARCHAR),
                                        new DataValue(bdate, Types.VARCHAR),
                                        new DataValue(getData_Order.get(0).get("TOT_QTY").toString(), Types.FLOAT),
                                        new DataValue(getData_Order.get(0).get("MEMO").toString(), Types.VARCHAR),
                                        new DataValue(req.getOut_cost_warehouse(), Types.VARCHAR),
                                        new DataValue(req.getOut_cost_warehouse(), Types.VARCHAR),
                                        new DataValue(getData_Order.get(0).get("TOT_AMT").toString(), Types.VARCHAR),
                                        new DataValue(orderNo, Types.VARCHAR),
                                        new DataValue("ORDER", Types.VARCHAR),
                                        new DataValue(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), Types.VARCHAR),
                                };
                                InsBean ib_processTask = new InsBean("dcp_processtask", columns_processTask);
                                ib_processTask.addValues(insValue_processTask);
                                this.addProcessData(new DataProcessBean(ib_processTask));
                            }

                            //控制上面但是必须有记录，不然保存单头没意义
                            if (this.pData.size() > 0)
                            {


                                //写零售生产单头
                                String[] columns_productSale = {"EID", "SHOPID", "BILLTYPE", "BILLNO", "OFNO", "TRNO",
                                        "TABLENO", "REPASTTYPE", "DINNERSIGN", "GUESTNUM", "PRODUCTSTATUS", "MEMO",
                                        "ISTAKEOUT", "CHANNELID", "APPTYPE", "LOADDOCTYPE", "WXOPENID"
                                        , "ORDERTIME", "ADULTCOUNT", "SHIPENDTIME","ISBOOK"};
                                //
                                DataValue[] insValue_productSale = new DataValue[]{
                                        new DataValue(eId_para, Types.VARCHAR),
                                        new DataValue(shopNo, Types.VARCHAR),
                                        new DataValue("ORDER", Types.VARCHAR),
                                        new DataValue(orderNo, Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue(getData_Order.get(0).get("ORDER_SN").toString(), Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue("2", Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue(getData_Order.get(0).get("MEALNUMBER").toString(), Types.FLOAT),
                                        new DataValue(productSale_status, Types.VARCHAR),
                                        new DataValue(getData_Order.get(0).get("MEMO").toString(), Types.VARCHAR),
                                        new DataValue("Y", Types.VARCHAR),
                                        new DataValue(channelId, Types.VARCHAR),
                                        new DataValue(loadDocType, Types.VARCHAR),
                                        new DataValue(loadDocType, Types.VARCHAR),
                                        new DataValue(getData_Order.get(0).get("OUTSELID").toString(), Types.VARCHAR),
                                        new DataValue(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), Types.VARCHAR),
                                        new DataValue(getData_Order.get(0).get("MEALNUMBER").toString(), Types.FLOAT),
                                        new DataValue(shipStartDateTime, Types.VARCHAR),
                                        new DataValue(getData_Order.get(0).get("ISBOOK").toString(), Types.VARCHAR),
                                };
                                InsBean ib_productSale = new InsBean("dcp_product_sale", columns_productSale);
                                ib_productSale.addValues(insValue_productSale);
                                this.addProcessData(new DataProcessBean(ib_productSale));

                                this.doExecuteDataToDB();
                            }
                        }
                    }
                }

            } catch (Exception e)
            {

            }
        }
        // endregion

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderReceivingAgreeOrRejectReq req) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderReceivingAgreeOrRejectReq req) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderReceivingAgreeOrRejectReq req) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrderReceivingAgreeOrRejectReq req) throws Exception
    {
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

        if (Check.Null(req.getRequest().getOpType()))
        {
            errCt++;
            errMsg.append("操作类型 opType不可为空值, ");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getOrderNo()))
        {
            errCt++;
            errMsg.append("订单单号orderNo不可为空值, ");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getLoadDocType()))
        {
            errCt++;
            errMsg.append("订单渠道类型loadDocType不可为空值, ");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getChannelId()))
        {
            errCt++;
            errMsg.append("订单渠道编码channelId不可为空值, ");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getShopId()))
        {
            errCt++;
            errMsg.append("门店编码shopId不可为空值, ");
            isFail = true;
        }


        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_OrderReceivingAgreeOrRejectReq> getRequestType()
    {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_OrderReceivingAgreeOrRejectReq>(){};
    }

    @Override
    protected DCP_OrderReceivingAgreeOrRejectRes getResponseType()
    {
        // TODO Auto-generated method stub
        return new DCP_OrderReceivingAgreeOrRejectRes();
    }


    private void deleteRedis(String redis_key,String hash_key) throws Exception
    {
        try
        {
            RedisPosPub delredis = new RedisPosPub();
            HelpTools.writelog_waimai(
                    "【删除门店Redis】开始删除  redis_key：" + redis_key + " hash_key：" + hash_key);
            delredis.DeleteHkey(redis_key, hash_key);
            HelpTools.writelog_waimai(
                    "【删除门店Redis】删除成功  redis_key：" + redis_key + " hash_key：" + hash_key);
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
    }

    private void WriteRedis(String redis_key, String hash_key, String hash_value) throws Exception
    {
        try
        {
            RedisPosPub redis = new RedisPosPub();
            HelpTools.writelog_waimai(
                    "【开始写缓存】" + " redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + hash_value);
            //redis.DeleteHkey(redis_key, hash_key);//
            boolean nret = redis.setHashMap(redis_key, hash_key, hash_value);
            if (nret)
            {
                HelpTools.writelog_waimai("【写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
            } else
            {
                HelpTools.writelog_waimai("【写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
            }
            // redis.Close();

        } catch (Exception e)
        {
            HelpTools.writelog_waimai(
                    "【开始写缓存】异常" + e.getMessage() + " redis_key:" + redis_key + " hash_key:" + hash_key);
        }
    }
}
