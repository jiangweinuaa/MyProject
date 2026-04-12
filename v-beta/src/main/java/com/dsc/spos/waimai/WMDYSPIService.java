package com.dsc.spos.waimai;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.waimai.entity.*;
import com.google.gson.reflect.TypeToken;
import microsoft.exchange.webservices.data.core.IFileAttachmentContentHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

public class WMDYSPIService extends SWaimaiBasicService {
    private String response = "";
    private String messageId ="";
    public WMDYSPIService()
    {
        //this.messageId = UUID.randomUUID().toString();
    }
    public WMDYSPIService(String msgId)
    {
        if (msgId==null||msgId.isEmpty())
        {
            //this.messageId = UUID.randomUUID().toString();
        }
        else
        {
            this.messageId = msgId;
        }

    }
    @Override
    public String execute(String json) throws Exception {
        if (json == null || json.length() == 0) {
            return null;
        }
        Map<String, Object> res = new HashMap<String, Object>();
        this.processDUID(json, res);

        return response;
    }

    @Override
    protected void processDUID(String req, Map<String, Object> res) throws Exception {
        try
        {
            HelpTools.writelog_waimai("【收到抖音外卖用户申请退款消息logid】"+messageId+",【消息内容】"+ req);
            String loadDocType = orderLoadDocType.DYWM;
            JSONObject obj = new JSONObject(req);
            String order_id = obj.optString("order_id");
            String after_sale_id = obj.optString("after_sale_id","");
            JSONObject reason_obj = obj.optJSONObject("refund_reason");
            String reason_desc = "";//用户填写的退款原因
            if (reason_obj!=null)
            {
                reason_desc = reason_obj.optString("desc","");
            }
            JSONArray show_reason_array = reason_obj.optJSONArray("show_reason");
            String show_reason_msg = "";
            if (show_reason_array!=null&&show_reason_array.length()>0)
            {
                for (int i =0 ; i<show_reason_array.length();i++)
                {
                    String msg = show_reason_array.getJSONObject(i).optString("msg","");
                    if (msg==null||msg.trim().isEmpty())
                    {
                        continue;
                    }
                    if (show_reason_msg.isEmpty())
                    {
                        show_reason_msg = msg;
                    }
                    else
                    {
                        show_reason_msg = show_reason_msg+","+msg;
                    }

                }
            }

            if (reason_desc.isEmpty())
            {
                reason_desc = show_reason_msg;
            }

            String status = "2";
            String refundStatus = "2";
            order orderDB = HelpTools.GetOrderInfoByOrderNO(StaticInfo.dao,"",  loadDocType, order_id);
            if(orderDB==null)
            {
                HelpTools.writelog_waimai("【DYWM订单申请退款查询本地订单】异常！单号="+order_id);
                return;
            }
            try
            {
                //JSONObject jsonObj = new JSONObject(orderDBJson);


                String eId = orderDB.geteId();
                String shopNo = orderDB.getShopNo();
                String status_db =  orderDB.getStatus();//数据库里面订单状态
                status = status_db;
                String refundStatus_db =  orderDB.getRefundStatus();
                if ("3".equals(status_db)||"12".equals(status_db))
                {
                    HelpTools.writelog_waimai("【DYWM订单申请退款查询本地订单】数据库订单状态status="+status_db+"已取消/退单状态，无需处理了，单号="+order_id);
                    return;
                }
                String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;
                String hash_key = order_id;

                orderDB.setStatus(status);
                orderDB.setRefundStatus(refundStatus);
                orderDB.setRefundReason(reason_desc);
                //orderDB.setRefundAmt(refundMoney);// 部分退单 的退款金额

                ParseJson pj = new ParseJson();
                String Response_json = pj.beanToJson(orderDB);

                //更新缓存
                if (shopNo!=null&&!shopNo.trim().isEmpty())
                {
                    try
                    {
                        boolean IsUpdateRedis = true;//是否更新缓存。
                        RedisPosPub redis = new RedisPosPub();
                        boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
                        if (isexistHashkey)
                        {
                            //如果存在看下缓存里面状态是不是 已经是退单成功状态
                            String redis_order = redis.getHashMap(redis_key, hash_key);
                            try
                            {
                                JSONObject redis_order_obj = new JSONObject(redis_order);
                                String status_redis = redis_order_obj.optString("status");
                                String refundStatus_redis = redis_order_obj.optString("refundStatus");
                                if (refundStatus.equals("2")) {
                                    if (status_redis.equals("12"))//缓存里面已经是退成功状态
                                    {
                                        IsUpdateRedis = false;
                                    }
                                } else if (refundStatus.equals("7")) {
                                    if (refundStatus_redis.equals("10"))////缓存里面已经是退成功状态
                                    {
                                        IsUpdateRedis = false;
                                    }
                                }

                            }
                            catch (Exception e) {
                            }


                            if(IsUpdateRedis)
                            {
                                redis.DeleteHkey(redis_key, hash_key);
                                HelpTools.writelog_waimai(
                                        "【DYWM删除存在hash_key的缓存】成功！" + "redis_key:" + redis_key + " hash_key:" + hash_key);
                            }

                        }

                        //这里对比下数据库状态
                        //MT可能先推送退单成功状态，再推送申请退单状态
                        if (refundStatus.equals("2"))
                        {
                            if (status_db.equals("12")) {
                                IsUpdateRedis = false;
                            }
                        }
                        else if (refundStatus.equals("7"))
                        {
                            if (refundStatus_db.equals("10")) {
                                IsUpdateRedis = false;
                            }
                        }

                        if(IsUpdateRedis)
                        {

                            HelpTools.writelog_waimai("【DYWM订单申请退款开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key
                                    + " hash_value:" + Response_json);
                            boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                            if (nret) {
                                HelpTools.writelog_waimai(
                                        "【DYWM订单申请退款写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                            } else {
                                HelpTools.writelog_waimai(
                                        "【DYWM订单申请退款写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                            }

                        }
                        else
                        {
                            HelpTools.writelog_waimai("【DYWM订单申请退款开始写缓存】【无需写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key
                                    + " hash_value:" + Response_json+" 数据库中订单status="+status_db+" refundStatus="+refundStatus_db);

                        }

                    }
                    catch (Exception e)
                    {
                        HelpTools.writelog_waimai("更新缓存中订单退款消息异常！" + e.getMessage());
                    }
                }

                if ("3".equals(status_db)||"12".equals(status_db))
                {
                    HelpTools.writelog_waimai("【DYWM订单申请退款查询本地订单】数据库订单状态status="+status_db+"已取消/退单状态，无需处理了，单号="+order_id);
                    return;
                }
                UptBean ub1 = null;
                ub1 = new UptBean("DCP_ORDER");
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("ORDERNO", new DataValue(order_id, Types.VARCHAR));

                ub1.addUpdateValue("AFTER_SALE_ID", new DataValue(after_sale_id,Types.VARCHAR));
                ub1.addUpdateValue("REFUNDSTATUS", new DataValue(refundStatus,Types.VARCHAR));
                if(refundStatus.equals("2")||refundStatus.equals("7"))
                {
                    HelpTools.writelog_fileName("【DYWM】申请退款更新数据，单号orderNo="+order_id+" 退货原因refundReason="+reason_desc, "refunReasonLog");
                    HelpTools.writelog_waimai("【DYWM】申请退款更新数据，单号orderNo="+order_id+" 退货原因refundReason="+reason_desc);
                    if (reason_desc != null && reason_desc.length() > 255)
                    {
                        reason_desc = reason_desc.substring(0, 255);
                    }
                    ub1.addUpdateValue("REFUNDREASON", new DataValue(reason_desc,Types.VARCHAR));
                }
                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));

                this.doExecuteDataToDB();
                HelpTools.writelog_waimai("【DYWM订单更新数据库成功】"+" 订单号orderNO:"+order_id+" 订单状态status="+status+" 退单状态refundStatus="+refundStatus);


                //region写订单日志
                try
                {
                    List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                    orderStatusLog onelv1 = new orderStatusLog();
                    onelv1.setLoadDocType(loadDocType);
                    onelv1.setChannelId(orderDB.getChannelId());
                    onelv1.setLoadDocBillType(orderDB.getLoadDocBillType());
                    onelv1.setLoadDocOrderNo(orderDB.getLoadDocOrderNo());
                    onelv1.seteId(eId);
                    String opNO = "";
                    String o_opName = "抖音用户";

                    onelv1.setOpName(o_opName);
                    onelv1.setOpNo(opNO);
                    onelv1.setShopNo(shopNo);
                    onelv1.setOrderNo(order_id);
                    onelv1.setMachShopNo(orderDB.getMachShopNo());
                    onelv1.setShippingShopNo(orderDB.getShippingShopNo());
                    String statusType = "3";
                    String updateStaus = refundStatus;
                    onelv1.setStatusType(statusType);
                    onelv1.setStatus(updateStaus);
                    StringBuilder statusTypeNameObj = new StringBuilder();
                    String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
                    String statusTypeName = statusTypeNameObj.toString();
                    onelv1.setStatusTypeName(statusTypeName);
                    onelv1.setStatusName(statusName);

                    String memo = "";
                    memo += statusName;
                    if (!reason_desc.isEmpty())
                    {
                        memo += "<br>"+reason_desc;
                    }
                    memo +="<br>抖音申请售后单ID:"+after_sale_id;
                    onelv1.setMemo(memo);
                    onelv1.setDisplay("1");

                    String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    onelv1.setUpdate_time(updateDatetime);
                    orderStatusLogList.add(onelv1);


                    StringBuilder errorStatusLogMessage = new StringBuilder();
                    boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                    if (nRet) {
                        HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNo:" + order_id);
                    } else {
                        HelpTools.writelog_waimai(
                                "【写表tv_orderStatuslog异常】" + errorStatusLogMessage.toString() + " 订单号orderNO:" + order_id);
                    }

                }
                catch (Exception e)
                {

                }
                //endregion

                return ;
            }
            catch (Exception e)
            {
                HelpTools.writelog_waimai("【DYWM订单申请退款】异常："+e.getMessage());
            }
        }
        catch (Exception e)
        {
            HelpTools.writelog_waimai("【处理抖音外卖用户申请退款消息异常】" + e.getMessage());
            return;
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(String req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(String req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(String req) throws Exception {
        return null;
    }

}
