package com.dsc.spos.scheduler.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.dsc.spos.waimai.entity.orderStatusLog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ExpressOrderSendNotify extends InitJob  {

    Logger logger = LogManager.getLogger(ISV_ELMTokenRefresh.class.getName());
    static boolean bRun=false;//标记此服务是否正在执行中
    //String goodsLogFileName = "ExpressOrderDelayNotify";
    Map<String,Map<String,Object>> apiUserChannelInfo;
    public ExpressOrderSendNotify()
    {

    }

    public String doExe() throws Exception
    {
        String sReturnInfo="";
        try
        {
            //此服务是否正在执行中
            if (bRun)
            {
                logger.info("\r\n*********物流下单后配送中通知ExpressOrderSendNotify正在执行中,本次调用取消:************\r\n");

                sReturnInfo="定时传输任务-物流下单后配送中通知ExpressOrderSendNotify正在执行中！";
                return sReturnInfo;
            }

            bRun=true;
            logger.info("\r\n*********物流下单后配送中通知ExpressOrderSendNotify定时调用Start:************\r\n");
            this.Log("*********物流下单后配送中通知ExpressOrderSendNotify定时调用Start:************");

            boolean runTimeFlag = this.jobRunTimeFlag();
            if(!runTimeFlag)
            {
                sReturnInfo= "【同步任务ExpressOrderSendNotify】不在job设置的运行时间内！";
                this.Log(sReturnInfo+",物流下单后配送中通知ExpressOrderSendNotify定时调用定时调用End");
                return sReturnInfo;
            }

            String OCCATION = "#036";//订单配送提醒
            String sql1 = " select * from (" +
                    " select OCCATION,MSGEND,CAST('2' AS NVARCHAR2(1)) IDX from CRM_MSGCONTROL_DEF WHERE OCCATION='" +OCCATION+"'"+
                    " UNION ALL   " +
                    " select OCCATION,MSGEND,CAST('1' AS NVARCHAR2(1)) IDX from  CRM_MSGCONTROL  WHERE OCCATION='"+OCCATION+"') order by IDX ";

            this.Log("【同步任务ExpressOrderSendNotify】物流下单后配送中通知，查询消息发送场景是配置表sql:"+sql1);
            List<Map<String,Object>> getQDataMsgControl = this.doQueryData(sql1,null);
            if (getQDataMsgControl==null||getQDataMsgControl.isEmpty())
            {
                sReturnInfo= "【同步任务ExpressOrderSendNotify】查询消息发送场景配置表CRM_MSGCONTROL_DEF、CRM_MSGCONTROL没有数据！";
                this.Log(sReturnInfo+",物流下单后配送中通知ExpressOrderSendNotify定时调用定时调用End");
                return sReturnInfo;
            }
            String msgEnd = getQDataMsgControl.get(0).get("MSGEND").toString();
            if (msgEnd.trim().isEmpty())
            {
                msgEnd = "小哥火速配送中，请您耐心等待";
            }

            String msgcontrolkeySql = "SELECT * FROM CRM_MSGCONTROLKEY_DEF where OCCATION ='"+OCCATION+"'";
            this.Log("【同步任务ExpressOrderSendNotify】物流下单后配送中通知，查询消息发送微信模板键值配置表sql:"+msgcontrolkeySql);
            List<Map<String,Object>> getQDataMsgControlKey = this.doQueryData(msgcontrolkeySql,null);
            if (getQDataMsgControlKey==null||getQDataMsgControlKey.isEmpty())
            {
                sReturnInfo= "【同步任务ExpressOrderSendNotify】查询消息发送微信模板键值配置表CRM_MSGCONTROLKEY_DEF没有数据！";
                this.Log(sReturnInfo+",物流下单后配送中通知ExpressOrderSendNotify定时调用定时调用End");
                return sReturnInfo;
            }


            String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            //查询参数值
            StringBuffer sqlOrder= new StringBuffer("select EID,ORDERNO,LOADDOCTYPE,CHANNELID,MEMBERID,SHIPPINGSHOP,SHIPPINGSHOPNAME,DELIVERY_SENDTIME from dcp_order a where  a.billtype=1  and a.paystatus='3' and a.status<>'3' and a.status<>'12' and a.autodelivery='Y' and a.deliverystatus is not null");
            sqlOrder.append(" and a.shipdate>='"+sdate+"' ");
            sqlOrder.append(" and a.MEMBERID is not null ");//没有会员ID，无法获取openid
            sqlOrder.append(" and a.DELIVERY_SENDTIME is not null ");//物流状态变成开始配送中状态的时间 日期格式： 2020/11/1 16:36:40
            sqlOrder.append(" and a.DELIVERY_MINIMSG is null ");//发物流的订单开始配送时，是否已推送微信小程序消息( Y：已推送 为空或N未推送)

            this.Log("【同步任务ExpressOrderSendNotify】物流下单后配送中通知，查询语句:"+sqlOrder.toString());
            List<Map<String,Object>> getQData = this.doQueryData(sqlOrder.toString(),null);
            if (getQData==null||getQData.isEmpty())
            {
                sReturnInfo= "【同步任务ExpressOrderSendNotify】查询没有数据！";
                this.Log(sReturnInfo+",物流下单后配送中通知ExpressOrderSendNotify定时调用定时调用End");
                return sReturnInfo;
            }
            for(Map<String,Object> par : getQData)
            {
                try
                {
                    String eId = par.get("EID").toString();
                    String orderNo = par.get("ORDERNO").toString();
                    String loaddoctype = par.get("LOADDOCTYPE").toString();
                    String channelId = par.get("CHANNELID").toString();
                    String memberId = par.get("MEMBERID").toString();
                    String shippingShop = par.get("SHIPPINGSHOP").toString();
                    String shippingShopName = par.get("SHIPPINGSHOPNAME").toString();
                    String delivery_sendtime = par.get("DELIVERY_SENDTIME").toString();
                    //获取APPID，以及接口账号
                    Map<String,Object> apiUserChannelMap = this.getApiUserChannelInfo(eId,channelId,loaddoctype);
                    if (apiUserChannelMap==null)
                    {
                        this.Log("【同步任务ExpressOrderSendNotify】获取推送小程序消息appid等配置信息数据为空,订单号orderNo="+orderNo);
                        continue;
                    }
                    String memberUrl = PosPub.getCRM_INNER_URL(eId);
                    if (memberUrl==null||memberUrl.trim().isEmpty()) {
                        this.Log("【同步任务ExpressOrderSendNotify】参数CrmUrl值为空,会员接口地址未配置");
                        continue;
                    }
                    String appId = apiUserChannelMap.get("APPID").toString();
                    String userCode = apiUserChannelMap.get("USERCODE").toString();
                    String userKey = apiUserChannelMap.get("USERKEY").toString();
                    String appType = apiUserChannelMap.get("APPTYPE").toString();//crm_channel.apptype 2-支付宝，4-微信
                    this.Log("【同步任务ExpressOrderSendNotify】获取推送小程序消息appid等配置信息数据,APPID="+appId+",APPTYPE="+appType+"(2-支付宝,4-微信),接口账号userCode="+userCode+",userKey="+userKey+",订单号orderNo="+orderNo);
                    if (appId.trim().isEmpty()||appType.trim().isEmpty()||userCode.trim().isEmpty())
                    {
                        this.Log("【同步任务ExpressOrderSendNotify】appId||appType||userCode存在为空的,无法推送,订单号orderNo="+orderNo);
                        continue;
                    }
                    //获取OPENID
                    String sql_openid = " select * from CRM_MEMBERIDENDITY where EID='"+eId+"' and APPTYPE='"+appType+"' and APPID='"+appId+"' and MEMBERID='"+memberId+"'";
                    this.Log("【同步任务ExpressOrderSendNotify】获取推送小程序消息openid,查询sql="+sql_openid+",订单号orderNo="+orderNo);
                    List<Map<String, Object>> getQDataOpenId=this.doQueryData(sql_openid, null);
                    if (getQDataOpenId==null||getQDataOpenId.isEmpty())
                    {
                        this.Log("【同步任务ExpressOrderSendNotify】获取推送小程序消息openid为空,无法推送,订单号orderNo="+orderNo);
                        continue;
                    }
                    String openId = getQDataOpenId.get(0).get("OPENID").toString();
                    //开始推送，
                    com.alibaba.fastjson.JSONObject sendMsReq = new com.alibaba.fastjson.JSONObject();
                    sendMsReq.put("serviceId", "CRM_MiniSendMsg_Open");

                    com.alibaba.fastjson.JSONArray datalistArray = new com.alibaba.fastjson.JSONArray();
                    //订单号
                    com.alibaba.fastjson.JSONObject dataMsg1 = new JSONObject(new TreeMap<String, Object>());
                    dataMsg1.put("key","订单号");//标题  character_string2  getMsgKey.get("KEYWORD1").toString()
                    dataMsg1.put("value", orderNo);//内容
                    datalistArray.add(dataMsg1);

                    //出餐门店
                    JSONObject dataMsg2 = new JSONObject(new TreeMap<String, Object>());
                    dataMsg2.put("key", "出餐门店");//标题 thing1  getMsgKey.get("KEYWORD2").toString()
                    dataMsg2.put("value", shippingShopName);//内容
                    datalistArray.add(dataMsg2);

                    //配送状态
                    JSONObject dataMsg3 = new JSONObject(new TreeMap<String, Object>());
                    dataMsg3.put("key", "配送状态");//标题 thing12  getMsgKey.get("KEYWORD3").toString()
                    dataMsg3.put("value", "订单配送中");//内容
                    datalistArray.add(dataMsg3);

                    //送出时间
                    JSONObject dataMsg4 = new JSONObject(new TreeMap<String, Object>());
                    dataMsg4.put("key","出餐时间");//标题 thing4  getMsgKey.get("KEYWORD4").toString()
                    dataMsg4.put("value", delivery_sendtime);//内容
                    datalistArray.add(dataMsg4);

                    //备注
                    JSONObject dataMsg5 = new JSONObject(new TreeMap<String, Object>());
                    dataMsg5.put("key", "备注");//标题 thing5 getMsgKey.get("KEYWORD5").toString()
                    dataMsg5.put("value", msgEnd);//内容
                    datalistArray.add(dataMsg5);

                    JSONObject message = new JSONObject();
                    message.put("first", "");
                    message.put("remark", "");
                    message.put("page", "");
                    message.put("data", datalistArray);

                    JSONObject cpReq = new JSONObject();
                    cpReq.put("occation", OCCATION); // 消息发送场景  暂时只支持 微信取餐提醒
                    cpReq.put("appId", appId);
                    cpReq.put("openId", openId);
                    cpReq.put("orderNo", orderNo);
                    cpReq.put("message", message);

                    sendMsReq.put("request", cpReq);

                    String sendMsgReqStr = cpReq.toString();
                    String senMsgSign = PosPub.encodeMD5(sendMsgReqStr + userKey);

                    JSONObject sendMsgSignJson = new JSONObject();
                    sendMsgSignJson.put("sign", senMsgSign);
                    sendMsgSignJson.put("key", userCode);

                    sendMsReq.put("sign", sendMsgSignJson);

                    //********** 已经准备好 CRM_MiniSendMsg_Open 的json，开始调用 *************

                    this.Log("******调用CRM_MiniSendMsg_Open接口，请求地址url:"+memberUrl+",请求req:" + sendMsReq.toString()+",订单号orderNo="+orderNo);
                    String sendMsgResStr = HttpSend.Sendcom(sendMsReq.toString(), memberUrl);
                    this.Log("******调用CRM_MiniSendMsg_Open接口,返回res:" + sendMsgResStr+",订单号orderNo="+orderNo);
                    if (sendMsgResStr!=null&&!sendMsgResStr.trim().isEmpty())
                    {
                        JSONObject sendMsgResJson = new JSONObject();
                        sendMsgResJson = JSON.parseObject(sendMsgResStr);//String转json
                        String sendMsgSuccess = sendMsgResJson.get("success").toString(); // TRUE 或 FALSE
                        String nRet = "N";
                        if ("true".equalsIgnoreCase(sendMsgSuccess))
                        {
                            nRet = "Y";
                        }
                        List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                        UptBean ubecOrder=new UptBean("dcp_order");
                        ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ubecOrder.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                        ubecOrder.addUpdateValue("DELIVERY_MINIMSG", new DataValue(nRet, Types.VARCHAR));
                        lstData.add(new DataProcessBean(ubecOrder));
                        StaticInfo.dao.useTransactionProcessData(lstData);
                    }

                }
                catch (Exception e)
                {
                    continue;
                }
            }


        }
        catch (Exception e)
        {
            try
            {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);

                pw.flush();
                pw.close();

                errors.flush();
                errors.close();

                logger.error("\r\n******物流下单后配送中通知ExpressOrderSendNotify报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\n******物流下单后配送中通知ExpressOrderSendNotify报错信息" + e1.getMessage() + "******\r\n");
            }

            //
            sReturnInfo="物流下单后配送中通知ExpressOrderSendNotify错误信息:" + e.getMessage();
        }
        finally
        {
            bRun=false;//
            logger.info("\r\n*********物流下单后配送中通知ExpressOrderSendNotify定时调用End:************\r\n");
            this.Log("物流下单后配送中通知ExpressOrderSendNotify定时调用End:");
        }
        return sReturnInfo;
    }

    private void Log(String log)  {
        try
        {
            HelpTools.writelog_fileName(log, "ExpressOrderSendNotify");

        } catch (Exception e)
        {
            // TODO: handle exception
        }

    }

    /**
     * job运行时间，（如果没有设置，默认一直运行）
     * @return
     * @throws Exception
     */
    private boolean jobRunTimeFlag() throws Exception
    {
        boolean flag = true;
        String sdate = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String stime = new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime());

        // 先查 job 执行时间，然后再执行后续操作
        String getTimeSql = "select * from job_quartz_detail where job_name = 'ExpressOrderSendNotify'  and cnfflg = 'Y' ";
        List<Map<String, Object>> getTimeDatas = this.doQueryData(getTimeSql, null);
        if (getTimeDatas != null && !getTimeDatas.isEmpty())
        {
            boolean isTime = false;
            for (Map<String, Object> map : getTimeDatas)
            {
                String beginTime = map.get("BEGIN_TIME").toString();
                String endTime = map.get("END_TIME").toString();

                // 如果当前时间在 执行时间范围内， 就执行
                if (stime.compareTo(beginTime) >= 0 && stime.compareTo(endTime) < 0)
                {
                    isTime = true;
                    break;
                }
            }
            if (!isTime)
            {
                return false;
            }

        }
        else// 如果没设置执行时间，一直运行
        {
            return true;
        }
        return flag;
    }

    private String getDeliveryTypeName(String deliveryType) throws Exception
    {
        String deliveryTypeName = "";
        if (deliveryType!=null)
        {
            try
            {
                switch (deliveryType)
                {
                    case "1":
                        deliveryTypeName = "自配送";
                        break;
                    case "2":
                        deliveryTypeName = "顺丰同城";
                        break;
                    case "3":
                        deliveryTypeName = "百度物流";
                        break;
                    case "4":
                        deliveryTypeName = "达达配送";
                        break;
                    case "5":
                        deliveryTypeName = "人人配送";
                        break;
                    case "6":
                        deliveryTypeName = "闪送";
                        break;
                    case "20":
                        deliveryTypeName = "点我达";
                        break;
                    case "21":
                        deliveryTypeName = "管易云物流";
                        break;
                    case "23":
                        deliveryTypeName = "美团跑腿";
                        break;
                    case "24":
                        deliveryTypeName = "圆通物流";
                        break;
                    case "25":
                        deliveryTypeName = "商有云物流";
                        break;
                    case "KDN":
                        deliveryTypeName = "快递鸟聚合物流";
                        break;
                    case "28":
                        deliveryTypeName = "餐道配送";
                        break;
                    default:
                        deliveryTypeName = "";
                        break;

                }
            }
            catch (Exception e)
            {

            }

        }

        return deliveryTypeName;

    }

    /**
     * 获取渠道id对应的APPID以及接口账号等信息
     * @param eId
     * @param channelId
     * @param appType
     * @return
     */
    private Map<String,Object> getApiUserChannelInfo(String eId,String channelId,String appType)
    {
        String hashKey = eId+"||"+channelId+"||"+appType;
        try
        {
            if (apiUserChannelInfo!=null)
            {
                Map<String,Object> par = apiUserChannelInfo.get(hashKey);
                if (par!=null)
                {
                    return par;
                }
            }
            else
            {
                apiUserChannelInfo = new HashMap<>();
            }

            String sql_channel = "select A.APPID,A.APPNO, A.APPTYPE,B.USERCODE,B.USERKEY from "
                    + " crm_channel a inner join crm_apiuser b on a.eid=b.eid and a.channelid=b.channelid and a.appno=b.apptype"
                    + " where a.eid='"+eId+"' and a.channelid='"+channelId+"' and a.appno='"+appType+"'";
            this.Log("【同步任务ExpressOrderSendNotify】物流状态配送中推送小程序消息需要查询渠道编码对应的appid等配置信息数据sql="+sql_channel+",企业id="+eId+",渠道类型="+appType+",渠道id="+channelId);
            List<Map<String, Object>> getQDataChannel=this.doQueryData(sql_channel, null);
            if (getQDataChannel==null||getQDataChannel.isEmpty())
            {
                this.Log("【同步任务ExpressOrderSendNotify】物流状态配送中推送小程序消息需要查询渠道编码对应的appid等配置信息数据为空,企业id="+eId+",渠道类型="+appType+",渠道id="+channelId);
                return null;
            }
            else
            {
                Map<String,Object>	apiUserMap = getQDataChannel.get(0);
                apiUserChannelInfo.put(hashKey,apiUserMap);
                return apiUserMap;
            }
        }
        catch (Exception e)
        {
            return null;
        }

    }
}
