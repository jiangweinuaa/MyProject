package com.dsc.spos.scheduler.job;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.dsc.spos.waimai.entity.orderStatusLog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
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
public class ExpressOrderDelayNotify extends InitJob  {

    Logger logger = LogManager.getLogger(ISV_ELMTokenRefresh.class.getName());
    static boolean bRun=false;//标记此服务是否正在执行中
    //String goodsLogFileName = "ExpressOrderDelayNotify";

    public ExpressOrderDelayNotify()
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
                logger.info("\r\n*********物流下单后未及时接单通知ExpressOrderDelayNotify正在执行中,本次调用取消:************\r\n");

                sReturnInfo="定时传输任务-物流下单后未及时接单通知ExpressOrderDelayNotify正在执行中！";
                return sReturnInfo;
            }

            bRun=true;
            logger.info("\r\n*********物流下单后未及时接单通知ExpressOrderDelayNotify定时调用Start:************\r\n");
            this.Log("*********物流下单后未及时接单通知ExpressOrderDelayNotify定时调用Start:************");

            boolean runTimeFlag = this.jobRunTimeFlag();
            if(!runTimeFlag)
            {
                sReturnInfo= "【同步任务ExpressOrderDelayNotify】不在job设置的运行时间内！";
                this.Log(sReturnInfo+",物流下单后未及时接单通知ExpressOrderDelayNotify定时调用定时调用End");
                return sReturnInfo;
            }
            //查询参数值
            String sql_para = "select * from platform_basesettemp where item='OtherDeliveryDelayOrderTime'";
            this.Log("【同步任务ExpressOrderDelayNotify】物流下单后未及时接单通知，查询参数ExpressOrderDelayNotify语句:"+sql_para);
            List<Map<String,Object>> getParaList = this.doQueryData(sql_para,null);
            if (getParaList==null||getParaList.isEmpty())
            {
                sReturnInfo= "【同步任务ExpressOrderDelayNotify】参数OtherDeliveryDelayOrderTime未设置！";
                this.Log(sReturnInfo+",物流下单后未及时接单通知ExpressOrderDelayNotify定时调用定时调用End");
                return sReturnInfo;
            }
            for(Map<String,Object> par : getParaList)
            {
                try
                {
                    String eId = par.get("EID").toString();
                    String itemvalue = par.get("ITEMVALUE").toString();
                    if (itemvalue==null||itemvalue.isEmpty())
                    {
                        this.Log("【同步任务ExpressOrderDelayNotify】参数OtherDeliveryDelayOrderTime为空,企业EID="+eId);
                        continue;
                    }
                    int timeSpwn = 0;
                    try
                    {
                        timeSpwn = Integer.parseInt(itemvalue);
                    }
                    catch (Exception e)
                    {
                        this.Log("【同步任务ExpressOrderDelayNotify】获取参数值异常:"+e.getMessage()+",参数OtherDeliveryDelayOrderTime="+itemvalue+",企业EID="+eId);
                        continue;
                    }


                    Calendar cal =Calendar.getInstance();
                    String sdate = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
                    //向前倒退
                    cal.add(Calendar.SECOND, -timeSpwn);
                    String sdate_pre = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
                    String spwn_con = "to_date('"+sdate_pre+"','yyyy-mm-dd hh24:mi:ss')";

                    StringBuffer sqlOrder= new StringBuffer("");
                    sqlOrder.append(" select * from dcp_order a ");
                    sqlOrder.append(" where a.eid='"+eId+"' ");
                    sqlOrder.append(" and  a.billtype=1  and a.paystatus='3' and a.status<>'3' and a.status<>'12' and a.autodelivery='Y'  ");
                    sqlOrder.append(" and a.shipdate='"+sdate+"' and a.deliverytype is not null ");
                    sqlOrder.append(" and (a.deliverystatus='-1' or a.deliverystatus='0') ");//-1物流预下单 0物流已下单
                    sqlOrder.append(" and (a.DELIVERY_ISPUSHCACHE is null or a.DELIVERY_ISPUSHCACHE='N') ");//
                    sqlOrder.append(" and a.DELIVERY_CREATETIME is not null and  a.DELIVERY_CREATETIME<"+spwn_con);//超过多久物流没接单提醒
                    String sql = sqlOrder.toString();
                    this.Log("【同步任务ExpressOrderDelayNotify】物流下单后超过["+timeSpwn+"]秒未接单。查询sql="+sql);
                    List<Map<String, Object>> getQData=this.doQueryData(sql, null);
                    if (getQData != null && getQData.isEmpty() == false)
                    {
                        RedisPosPub redis = new RedisPosPub();
                        for (Map<String, Object> map_order : getQData)
                        {
                            try
                            {
                                //String eId=map_order.get("EID").toString();
                                String orderNo = map_order.get("ORDERNO").toString();
                                String channelId = map_order.get("CHANNELID").toString();
                                String deliveryType = map_order.get("DELIVERYTYPE").toString();
                                String machShopId = map_order.get("MACHSHOP").toString();
                                String machShopName = map_order.get("MACHSHOPNAME").toString();
                                String memo = map_order.get("MEMO").toString();
                                String shippingId = map_order.get("SHIPPINGSHOP").toString();
                                String shippingName = map_order.get("SHIPPINGSHOPNAME").toString();
                                String shopId = map_order.get("SHOP").toString();
                                String shopName = map_order.get("SHOPNAME").toString();
                                String loadDocType = map_order.get("LOADDOCTYPE").toString();
                                String loadDocTypeName = "";
                                String channelIdName = "";
                                if (orderLoadDocType.POS.equals(loadDocType)) {
                                    loadDocTypeName = "POS";
                                    channelIdName = "POS";
                                } else if (orderLoadDocType.POSANDROID.equals(loadDocType)) {
                                    loadDocTypeName = "安卓POS";
                                    channelIdName = "安卓POS";
                                } else if (orderLoadDocType.WAIMAI.equals(loadDocType)) {
                                    loadDocTypeName = "自营外卖";
                                    channelIdName = "自营外卖";
                                } else if (orderLoadDocType.MINI.equals(loadDocType)) {
                                    loadDocTypeName = "小程序商城";
                                    channelIdName = "小程序商城";
                                } else if (orderLoadDocType.WECHAT.equals(loadDocType)) {
                                    loadDocTypeName = "微信手机商城";
                                    channelIdName = "微信手机商城";
                                } else {
                                    loadDocTypeName = loadDocType;
                                    channelIdName = channelId;
                                }

                                String shipDate = map_order.get("SHIPDATE").toString();
                                String shipStartTime = map_order.get("SHIPSTARTTIME").toString();
                                String shipEndTime = map_order.get("SHIPENDTIME").toString();
                                String deliveryTypeName = map_order.get("SUBDELIVERYCOMPANYNAME").toString();
                                if (deliveryTypeName.isEmpty())
                                {
                                    deliveryTypeName = getDeliveryTypeName(deliveryType);
                                }
                                JSONObject jsonOrder = new JSONObject();
                                jsonOrder.put("shopNo",shippingId);//配送门店号
                                jsonOrder.put("shopName",shippingName);
                                jsonOrder.put("orderNo",orderNo);
                                jsonOrder.put("loadDocType",loadDocType);
                                jsonOrder.put("loadDocTypeName",loadDocTypeName);
                                jsonOrder.put("channelId",channelId);
                                jsonOrder.put("channelIdName",channelIdName);
                                jsonOrder.put("shipDate",shipDate);
                                jsonOrder.put("shipStartTime",shipStartTime);
                                jsonOrder.put("shipEndTime",shipEndTime);
                                jsonOrder.put("deliveryType",deliveryType);
                                jsonOrder.put("deliveryTypeName",deliveryTypeName);
                                JSONArray goodsArray =  new JSONArray();


                                String sqlOrderDetail="select * from dcp_order_detail where eid='"+eId+"' and orderno='"+orderNo+"' order by item ";
                                List<Map<String, Object>> getDetailDatas=this.doQueryData(sqlOrderDetail, null);
                                if (getDetailDatas!=null&&!getDetailDatas.isEmpty())
                                {
                                    for (Map<String, Object> mapGoods : getDetailDatas)
                                    {
                                        JSONObject jsonGoods = new JSONObject();
                                        String item = mapGoods.getOrDefault("ITEM", "").toString();
                                        String pluName = mapGoods.getOrDefault("PLUNAME", "").toString();
                                        String pluNo = mapGoods.getOrDefault("PLUNO", "").toString();
                                        String pluBarcode = mapGoods.getOrDefault("PLUBARCODE", "").toString();
                                        String sUnit = mapGoods.getOrDefault("SUNIT", "").toString();
                                        String sUnitName = mapGoods.getOrDefault("SUNITNAME", "").toString();
                                        String specName = mapGoods.getOrDefault("SPECNAME", "").toString();
                                        String qty = mapGoods.getOrDefault("QTY", "1").toString();
                                        //String price = mapGoods.getOrDefault("PRICE", "0").toString();
                                        String amt = mapGoods.getOrDefault("AMT", "0").toString();
                                        jsonGoods.put("item",item);
                                        jsonGoods.put("pluBarcode",pluBarcode);
                                        jsonGoods.put("pluNo",pluNo);
                                        jsonGoods.put("pluName",pluName);
                                        jsonGoods.put("sUnit",sUnit);
                                        jsonGoods.put("sUnitName",sUnitName);
                                        jsonGoods.put("specName",specName);
                                        jsonGoods.put("qty",qty);
                                        jsonGoods.put("amt",amt);

                                        goodsArray.put(jsonGoods);
                                    }
                                }

                                jsonOrder.put("goodsList",goodsArray);

                                String redis_key = orderRedisKeyInfo.redis_OrderNotify+ ":" +  eId + ":" + shippingId;//写配送门店缓存
                                String hash_key = orderNo;
                                String hash_value = jsonOrder.toString();
                                this.Log("【通知POS消息写缓存】开始"+ ",redis_key=" + redis_key + ",hash_key=" + hash_key + ",hash_value:" + hash_value+ ",单号orderNo:"+orderNo);
                                boolean nret = redis.setHashMap(redis_key,hash_key,hash_value);
                                if (nret)
                                {
                                    this.Log("【通知POS消息写缓存】成功"+ ",redis_key=" + redis_key + ",hash_key=" + hash_key + ",单号orderNo:"+orderNo);
                                    List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                                    UptBean ubecOrder=new UptBean("dcp_order");
                                    ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                    ubecOrder.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                                    ubecOrder.addUpdateValue("DELIVERY_ISPUSHCACHE", new DataValue("Y", Types.VARCHAR));//物流未接单是否推送缓存标记 Y：已推送 为空或N未推送
                                    lstData.add(new DataProcessBean(ubecOrder));
                                    StaticInfo.dao.useTransactionProcessData(lstData);

                                    //写订单日志
                                    List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                                    String LogStatus="99";
                                    orderStatusLog oslog=new orderStatusLog();
                                    oslog.setCallback_status("N");
                                    oslog.setChannelId(channelId);
                                    oslog.setDisplay("0");
                                    oslog.seteId(eId);
                                    oslog.setLoadDocType(loadDocType);
                                    oslog.setMachShopName(machShopName);
                                    oslog.setMachShopNo(machShopId);
                                    oslog.setMemo("物流下单后超过["+timeSpwn+"]秒未接单已通知POS");
                                    oslog.setNeed_callback("N");
                                    oslog.setNeed_notify("N");
                                    oslog.setNotify_status("N");
                                    oslog.setOpName("定时任务");
                                    oslog.setOpNo("JOB");
                                    oslog.setOrderNo(orderNo);
                                    oslog.setShippingShopName(shippingName);
                                    oslog.setShippingShopNo(shippingId);
                                    oslog.setShopName(shopName);
                                    oslog.setShopNo(shopId);
                                    oslog.setStatus(LogStatus);
                                    //
                                    String statusType="99";
                                    StringBuilder statusTypeName=new StringBuilder();
                                    String statusName="其他";
                                    oslog.setStatusName(statusName);
                                    oslog.setStatusType(statusType);
                                    oslog.setStatusTypeName("其他状态");
                                    oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                    orderStatusLogList.add(oslog);
                                    StringBuilder errorMessage = new StringBuilder();
                                    HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);

                                }
                                else
                                {
                                    this.Log("【通知POS消息写缓存】失败"+ ",redis_key:" + redis_key + ",hash_key:" + hash_key + ",单号orderNo:"+orderNo);
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
                                    this.Log("ExpressOrderDelayNotify定时任务报错信息:"+ e.getMessage()+"\r\n异常原因:" + errors.toString());
                                    logger.error("\r\n******物流下单后未及时接单通知ExpressOrderDelayNotify报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                                    pw=null;
                                    errors=null;
                                }
                                catch (IOException e1)
                                {
                                    logger.error("\r\n******物流下单后未及时接单通知ExpressOrderDelayNotify报错信息" + e1.getMessage() + "******\r\n");
                                    this.Log("ExpressOrderDelayNotify定时任务报错信息:"+ e1.getMessage());
                                }
                            }

                        }

                    }
                    else
                    {
                        logger.info("\r\n*********物流下单后未及时接单通知ExpressOrderDelayNotify無記錄SQL: "+sqlOrder+"************\r\n");
                        this.Log("物流下单后未及时接单通知ExpressOrderDelayNotify查询没有数据。");
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

                logger.error("\r\n******物流下单后未及时接单通知ExpressOrderDelayNotify报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\n******物流下单后未及时接单通知ExpressOrderDelayNotify报错信息" + e1.getMessage() + "******\r\n");
            }

            //
            sReturnInfo="物流下单后未及时接单通知ExpressOrderDelayNotify错误信息:" + e.getMessage();
        }
        finally
        {
            bRun=false;//
            logger.info("\r\n*********物流下单后未及时接单通知ExpressOrderDelayNotify定时调用End:************\r\n");
            this.Log("物流下单后未及时接单通知ExpressOrderDelayNotify定时调用End:");
        }
        return sReturnInfo;
    }

    private void Log(String log)  {
        try
        {
            HelpTools.writelog_fileName(log, "ExpressOrderDelayNotify");

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
        String getTimeSql = "select * from job_quartz_detail where job_name = 'ExpressOrderDelayNotify'  and cnfflg = 'Y' ";
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
}
