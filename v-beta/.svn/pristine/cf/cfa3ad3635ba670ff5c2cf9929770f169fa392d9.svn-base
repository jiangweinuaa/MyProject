package com.dsc.spos.waimai;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrderShipping_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderShipping_OpenRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.imp.json.DCP_OrderShipping_Open;
import com.dsc.spos.thirdpart.youzan.YouZanCallBackServiceV3;
import com.dsc.spos.thirdpart.youzan.YouZanUtilsV3;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.waimai.entity.orderStatusLog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

public class ShangyouService extends SWaimaiBasicService
{

    Logger logger = LogManager.getLogger(ShangyouService.class.getName());

    @Override
    public String execute(String json) throws Exception
    {
        JSONObject resjsobject= JSONObject.parseObject(json);

        //接口必传type:
        //    10 物流状态
        //    30 新订单
        //    31 订单状态变更
        //    35 点餐新订单
        //    11 骑手位置同步
        //订单状态变更
        String type=resjsobject.containsKey("type")?resjsobject.getString("type"):"";//类型
        JSONObject resData= resjsobject.getJSONObject("data");
        if (type.equals("31")) //订单状态变更
        {
            if (resData != null)
            {
                String thirdShopId=resjsobject.containsKey("thirdShopId")?resjsobject.getString("thirdShopId"):"";//DCP门店号
                String mengYouNo=resData.getString("mengYouNo");//商有订单号
                String orderNo=resData.getString("orderNo");//DCP订单号
                String status=resData.getString("status");//订单状态：1、未生效   2、未处理  3、已接单\r\n4、已出餐  5、已完成  6、无效订单  7、退单中\r\n 8、退单成功  9、退单失败
                String channel=resData.getString("channel");//
                Long finishedTime=resData.getLong("finishedTime");//
                Long orderCancelTime=resData.getLong("orderCancelTime");//
                String cancelOrderDescription=resData.getString("cancelOrderDescription");//
                String refundDescription=resData.getString("refundDescription");//
                String refundTotal=resData.getString("refundTotal");//
            }
        }
        else if (type.equals("10")) //物流状态
        {
            if (resData != null)
            {
                String thirdShopId=resjsobject.containsKey("thirdShopId")?resjsobject.getString("thirdShopId"):"";//DCP门店号
                String businessOrderNo=resData.getString("businessOrderNo");//商有订单号
                String order_no=resData.getString("thirdOutOrderNo");//DCP订单号
                String express_no=resData.getString("orderNo");//物流单号
                String riderName=resData.getString("riderName");//
                String riderPhone=resData.getString("riderPhone");//
                String distributionStatus=resData.getString("distributionStatus");//
                String express_code=resData.getString("channel");//
                Long orderStartTime=resData.getLong("orderStartTime");//
                Long pushTime=resData.getLong("pushTime");//
                String remark=resData.getString("remark");//


                String sqlOrder="select * from dcp_order where ORDERNO='"+order_no+"' and OUTDOCORDERNO='"+businessOrderNo+"' ";

                List<Map<String, Object>> getQData=this.doQueryData(sqlOrder, null);
                if (getQData != null && getQData.isEmpty() == false)
                {
                    for (Map<String, Object> map : getQData)
                    {

                        String order_eId=map.get("EID").toString();
                        String order_shopno=map.get("SHOP").toString();
                        String order_load_doctype=map.get("LOADDOCTYPE").toString();
                        String order_chanelid=map.get("CHANNELID").toString();
                        String machShopId=map.get("MACHSHOP").toString();
                        String machShopName=map.get("MACHSHOPNAME").toString();
                        String memo=map.get("MEMO").toString();
                        String shippingId=map.get("SHIPPINGSHOP").toString();
                        String shippingName=map.get("SHIPPINGSHOPNAME").toString();
                        String shopId=map.get("SHOP").toString();
                        String shopName=map.get("SHOPNAME").toString();
                        String outDocorderno=map.get("OUTDOCORDERNO").toString();
                        String deliverystatus=map.get("DELIVERYSTATUS").toString();
                        String status=map.get("STATUS").toString();

                        String express_name="";//快递公司名称
                        switch (express_code)
                        {
                            case "5":
                                express_name="顺丰专送";
                                break;

                            case "8":
                                express_name="美团专送";
                                break;

                            case "9":
                                express_name="达达专送";
                                break;

                            case "10":
                                express_name="蜂鸟专送";
                                break;

                            case "13":
                                express_name="蜂鸟众包";
                                break;

                            case "14":
                                express_name="闪送";
                                break;

                            case "15":
                                express_name="顺丰众包";
                                break;

                            case "16":
                                express_name="达达众包";
                                break;

                            default:
                                express_name="商有新增的配送商啦";
                                break;
                        }

                        String distributionStatusName="";

                        //执行
                        List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();

                        String newDeliverystatus="";
                        if (distributionStatus.equals("1"))
                        {

                            distributionStatusName="已下单";
                            newDeliverystatus="0";
                            //更新订单商有云管家物流单信息
                            UptBean ubecOrder=new UptBean("DCP_ORDER");
                            ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));
                            ubecOrder.addCondition("OUTDOCORDERNO", new DataValue(businessOrderNo, Types.VARCHAR));

                            ubecOrder.addUpdateValue("DELIVERYNO", new DataValue(express_no, Types.VARCHAR));//物流单号
                            ubecOrder.addUpdateValue("SUBDELIVERYCOMPANYNO", new DataValue(express_code, Types.VARCHAR));//物流公司代码
                            ubecOrder.addUpdateValue("SUBDELIVERYCOMPANYNAME", new DataValue(express_name, Types.VARCHAR));//物流公司名称
                            ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue(newDeliverystatus, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8已分配骑手
                            ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            lstData.add(new DataProcessBean(ubecOrder));

                            //写订单日志
                            String memoLog="订单状态-->物流已产生发货单("+express_code+express_name+",物流单号="+express_no+")";
                            String LogStatus="0";
                            orderStatusLog oslog=new orderStatusLog();
                            oslog.setCallback_status("N");
                            oslog.setChannelId(order_chanelid);
                            oslog.setDisplay("1");
                            oslog.seteId(order_eId);
                            oslog.setLoadDocBillType(order_load_doctype);
                            oslog.setLoadDocOrderNo(order_no);
                            oslog.setLoadDocType(order_load_doctype);
                            oslog.setMachShopName(machShopName);
                            oslog.setMachShopNo(machShopId);
                            oslog.setMemo(memoLog);
                            oslog.setNeed_callback("N");
                            oslog.setNeed_notify("N");
                            oslog.setNotify_status("N");
                            oslog.setOpName("admin");
                            oslog.setOpNo("admin");
                            oslog.setOrderNo(order_no);
                            oslog.setShippingShopName(shippingName);
                            oslog.setShippingShopNo(shippingId);
                            oslog.setShopName(shopName);
                            oslog.setShopNo(shopId);
                            oslog.setStatus(status);//LogStatus不能是0和1，否则里面display重新赋值0啦
                            //
                            String statusType="2";
                            StringBuilder statusTypeName=new StringBuilder();
                            String statusName= HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
                            oslog.setStatusName(statusName);
                            oslog.setStatusType(statusType);
                            oslog.setStatusTypeName(statusTypeName.toString());
                            oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                            InsBean ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
                            lstData.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));


                            //更新微商城状态
                            Calendar cal = Calendar.getInstance();
                            String updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
                            JSONObject js=new JSONObject();
                            js.put("serviceId", "DCP_OrderStatusUpdate");
                            js.put("orderNo", order_no);
                            js.put("statusType", "2");//状态类型 1=交易状态变更 2=物流状态变更 3=其他 4= 退单状态变更 5=推送状态变更 6=开票状态变更
                            js.put("status", "1");//物流状态：0=未配送 1=配送中 2=已配送 3=确认收货
                            js.put("description", "物流已产生发货单("+express_code+express_name+",物流单号="+express_no+")");
                            js.put("oprId", "admin");
                            js.put("orgType", "2");
                            js.put("orgId", order_shopno);
                            js.put("updateTime", updateTime);

                            //物流信息
                            JSONArray deliverInfo = new JSONArray();
                            JSONObject body = new JSONObject();
                            body.put("expressType", "25");
                            body.put("expressTypeName", express_name);
                            body.put("expressBillNo", express_no);
                            deliverInfo.add(body);
                            js.put("deliverInfo", deliverInfo);

                            String request = js.toString();
                            String result = HttpSend.MicroMarkSend(request, order_eId, "OrderStatusUpdate", order_chanelid);
                            body=null;
                            deliverInfo=null;
                            js=null;

                            StaticInfo.dao.useTransactionProcessData(lstData);
                        }
                        else  if (distributionStatus.equals("3"))
                        {
                        	newDeliverystatus="7";
                            distributionStatusName="下单失败";
                            //不用重复写
                            if (deliverystatus.equals("7")) continue;
                            //更新订单商有云管家物流单信息
                            UptBean ubecOrder=new UptBean("DCP_ORDER");
                            ubecOrder.addCondition("EID", new DataValue(order_eId, Types.VARCHAR));
                            ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));
                            ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue(newDeliverystatus, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8已分配骑手
                            ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            lstData.add(new DataProcessBean(ubecOrder));

                            //写订单日志
                            String memoLog="订单状态-->物流:" + distributionStatusName;
                            String LogStatus="0";
                            orderStatusLog oslog=new orderStatusLog();
                            oslog.setCallback_status("N");
                            oslog.setChannelId(order_chanelid);
                            oslog.setDisplay("1");
                            oslog.seteId(order_eId);
                            oslog.setLoadDocBillType(order_load_doctype);
                            oslog.setLoadDocOrderNo(order_no);
                            oslog.setLoadDocType(order_load_doctype);
                            oslog.setMachShopName(machShopName);
                            oslog.setMachShopNo(machShopId);
                            oslog.setMemo(memoLog);
                            oslog.setNeed_callback("N");
                            oslog.setNeed_notify("N");
                            oslog.setNotify_status("N");
                            oslog.setOpName("admin");
                            oslog.setOpNo("admin");
                            oslog.setOrderNo(order_no);
                            oslog.setShippingShopName(shippingName);
                            oslog.setShippingShopNo(shippingId);
                            oslog.setShopName(shopName);
                            oslog.setShopNo(shopId);
                            oslog.setStatus(status);//LogStatus不能是0和1，否则里面display重新赋值0啦
                            //
                            String statusType="2";
                            StringBuilder statusTypeName=new StringBuilder();
                            String statusName= HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
                            oslog.setStatusName(statusName);
                            oslog.setStatusType(statusType);
                            oslog.setStatusTypeName(statusTypeName.toString());
                            oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                            InsBean ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
                            lstData.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));

                            StaticInfo.dao.useTransactionProcessData(lstData);

                        }
                        else  if (distributionStatus.equals("5"))
                        {
                            distributionStatusName="配送异常";
                            newDeliverystatus="4";
                            //不用重复写
                            if (deliverystatus.equals("4")) continue;
                            //更新订单商有云管家物流单信息
                            UptBean ubecOrder=new UptBean("DCP_ORDER");
                            ubecOrder.addCondition("EID", new DataValue(order_eId, Types.VARCHAR));
                            ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));
                            ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue(newDeliverystatus, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8已分配骑手
                            ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            lstData.add(new DataProcessBean(ubecOrder));

                            //写订单日志
                            String memoLog="订单状态-->物流:" + distributionStatusName;
                            String LogStatus="0";
                            orderStatusLog oslog=new orderStatusLog();
                            oslog.setCallback_status("N");
                            oslog.setChannelId(order_chanelid);
                            oslog.setDisplay("1");
                            oslog.seteId(order_eId);
                            oslog.setLoadDocBillType(order_load_doctype);
                            oslog.setLoadDocOrderNo(order_no);
                            oslog.setLoadDocType(order_load_doctype);
                            oslog.setMachShopName(machShopName);
                            oslog.setMachShopNo(machShopId);
                            oslog.setMemo(memoLog);
                            oslog.setNeed_callback("N");
                            oslog.setNeed_notify("N");
                            oslog.setNotify_status("N");
                            oslog.setOpName("admin");
                            oslog.setOpNo("admin");
                            oslog.setOrderNo(order_no);
                            oslog.setShippingShopName(shippingName);
                            oslog.setShippingShopNo(shippingId);
                            oslog.setShopName(shopName);
                            oslog.setShopNo(shopId);
                            oslog.setStatus(status);//LogStatus不能是0和1，否则里面display重新赋值0啦
                            //
                            String statusType="2";
                            StringBuilder statusTypeName=new StringBuilder();
                            String statusName= HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
                            oslog.setStatusName(statusName);
                            oslog.setStatusType(statusType);
                            oslog.setStatusTypeName(statusTypeName.toString());
                            oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                            InsBean ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
                            lstData.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));

                            StaticInfo.dao.useTransactionProcessData(lstData);
                        }
                        else  if (distributionStatus.equals("7"))
                        {
                            distributionStatusName="已取消";
                            newDeliverystatus="5";
                            //不用重复写
                            if (deliverystatus.equals("5")) continue;
                            //更新订单商有云管家物流单信息
                            UptBean ubecOrder=new UptBean("DCP_ORDER");
                            ubecOrder.addCondition("EID", new DataValue(order_eId, Types.VARCHAR));
                            ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));
                            ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue(newDeliverystatus, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8已分配骑手
                            ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            lstData.add(new DataProcessBean(ubecOrder));

                            //写订单日志
                            String memoLog="订单状态-->物流:" + distributionStatusName;
                            String LogStatus="0";
                            orderStatusLog oslog=new orderStatusLog();
                            oslog.setCallback_status("N");
                            oslog.setChannelId(order_chanelid);
                            oslog.setDisplay("1");
                            oslog.seteId(order_eId);
                            oslog.setLoadDocBillType(order_load_doctype);
                            oslog.setLoadDocOrderNo(order_no);
                            oslog.setLoadDocType(order_load_doctype);
                            oslog.setMachShopName(machShopName);
                            oslog.setMachShopNo(machShopId);
                            oslog.setMemo(memoLog);
                            oslog.setNeed_callback("N");
                            oslog.setNeed_notify("N");
                            oslog.setNotify_status("N");
                            oslog.setOpName("admin");
                            oslog.setOpNo("admin");
                            oslog.setOrderNo(order_no);
                            oslog.setShippingShopName(shippingName);
                            oslog.setShippingShopNo(shippingId);
                            oslog.setShopName(shopName);
                            oslog.setShopNo(shopId);
                            oslog.setStatus(status);//LogStatus不能是0和1，否则里面display重新赋值0啦
                            //
                            String statusType="2";
                            StringBuilder statusTypeName=new StringBuilder();
                            String statusName= HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
                            oslog.setStatusName(statusName);
                            oslog.setStatusType(statusType);
                            oslog.setStatusTypeName(statusTypeName.toString());
                            oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                            InsBean ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
                            lstData.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));

                            StaticInfo.dao.useTransactionProcessData(lstData);
                        }
                        else  if (distributionStatus.equals("15"))
                        {
                            distributionStatusName="已分配骑手";
                            newDeliverystatus="8";
                            //不用重复写
                            if (deliverystatus.equals("8")) continue;

                            //更新订单商有云管家物流单信息
                            UptBean ubecOrder=new UptBean("DCP_ORDER");
                            ubecOrder.addCondition("EID", new DataValue(order_eId, Types.VARCHAR));
                            ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));

                            ubecOrder.addUpdateValue("DELNAME", new DataValue(riderName, Types.VARCHAR));//
                            ubecOrder.addUpdateValue("DELTELEPHONE", new DataValue(riderPhone, Types.VARCHAR));//
                            ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue(newDeliverystatus, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8已分配骑手
                            ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            lstData.add(new DataProcessBean(ubecOrder));

                            //写订单日志
                            String memoLog="订单状态-->物流:" + distributionStatusName+",骑手:"+ riderName+"("+riderPhone+")";
                            String LogStatus="0";
                            orderStatusLog oslog=new orderStatusLog();
                            oslog.setCallback_status("N");
                            oslog.setChannelId(order_chanelid);
                            oslog.setDisplay("1");
                            oslog.seteId(order_eId);
                            oslog.setLoadDocBillType(order_load_doctype);
                            oslog.setLoadDocOrderNo(order_no);
                            oslog.setLoadDocType(order_load_doctype);
                            oslog.setMachShopName(machShopName);
                            oslog.setMachShopNo(machShopId);
                            oslog.setMemo(memoLog);
                            oslog.setNeed_callback("N");
                            oslog.setNeed_notify("N");
                            oslog.setNotify_status("N");
                            oslog.setOpName("admin");
                            oslog.setOpNo("admin");
                            oslog.setOrderNo(order_no);
                            oslog.setShippingShopName(shippingName);
                            oslog.setShippingShopNo(shippingId);
                            oslog.setShopName(shopName);
                            oslog.setShopNo(shopId);
                            oslog.setStatus(status);//LogStatus不能是0和1，否则里面display重新赋值0啦
                            //
                            String statusType="2";
                            StringBuilder statusTypeName=new StringBuilder();
                            String statusName= HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
                            oslog.setStatusName(statusName);
                            oslog.setStatusType(statusType);
                            oslog.setStatusTypeName(statusTypeName.toString());
                            oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                            InsBean ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
                            lstData.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));

                            StaticInfo.dao.useTransactionProcessData(lstData);
                        }
                        else  if (distributionStatus.equals("25"))
                        {
                            distributionStatusName="骑手已到店";
                            newDeliverystatus="6";
                            //不用重复写
                            if (deliverystatus.equals("6")) continue;
                            //更新订单商有云管家物流单信息
                            UptBean ubecOrder=new UptBean("DCP_ORDER");
                            ubecOrder.addCondition("EID", new DataValue(order_eId, Types.VARCHAR));
                            ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));
                            ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue(newDeliverystatus, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8已分配骑手
                            ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            lstData.add(new DataProcessBean(ubecOrder));

                            //写订单日志
                            String memoLog="订单状态-->物流:" + distributionStatusName;
                            String LogStatus="0";
                            orderStatusLog oslog=new orderStatusLog();
                            oslog.setCallback_status("N");
                            oslog.setChannelId(order_chanelid);
                            oslog.setDisplay("1");
                            oslog.seteId(order_eId);
                            oslog.setLoadDocBillType(order_load_doctype);
                            oslog.setLoadDocOrderNo(order_no);
                            oslog.setLoadDocType(order_load_doctype);
                            oslog.setMachShopName(machShopName);
                            oslog.setMachShopNo(machShopId);
                            oslog.setMemo(memoLog);
                            oslog.setNeed_callback("N");
                            oslog.setNeed_notify("N");
                            oslog.setNotify_status("N");
                            oslog.setOpName("admin");
                            oslog.setOpNo("admin");
                            oslog.setOrderNo(order_no);
                            oslog.setShippingShopName(shippingName);
                            oslog.setShippingShopNo(shippingId);
                            oslog.setShopName(shopName);
                            oslog.setShopNo(shopId);
                            oslog.setStatus(status);//LogStatus不能是0和1，否则里面display重新赋值0啦
                            //
                            String statusType="2";
                            StringBuilder statusTypeName=new StringBuilder();
                            String statusName= HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
                            oslog.setStatusName(statusName);
                            oslog.setStatusType(statusType);
                            oslog.setStatusTypeName(statusTypeName.toString());
                            oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                            InsBean ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
                            lstData.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));

                            StaticInfo.dao.useTransactionProcessData(lstData);
                        }
                        else  if (distributionStatus.equals("30"))//骑手取货，可以做订转销
                        {
                            distributionStatusName="骑手已取货（配送中）";
                            newDeliverystatus="2";
                            //不用重复写
                            if (deliverystatus.equals("2")) continue;
                            //更新订单商有云管家物流单信息
                            UptBean ubecOrder=new UptBean("DCP_ORDER");
                            ubecOrder.addCondition("EID", new DataValue(order_eId, Types.VARCHAR));
                            ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));
                            ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue(newDeliverystatus, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8已分配骑手
                            ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            lstData.add(new DataProcessBean(ubecOrder));


                            //调用订转销
                            try
                            {
                                DCP_OrderShipping_OpenReq dcp_OrderShipping_OpenReq=new DCP_OrderShipping_OpenReq();
                                dcp_OrderShipping_OpenReq.setRequest(dcp_OrderShipping_OpenReq.new levelRequest());
                                dcp_OrderShipping_OpenReq.setServiceId("DCP_OrderShipping_Open");
                                dcp_OrderShipping_OpenReq.getRequest().seteId(order_eId);
                                dcp_OrderShipping_OpenReq.getRequest().setOpShopId(order_shopno);
                                dcp_OrderShipping_OpenReq.getRequest().setOpType("1");
                                dcp_OrderShipping_OpenReq.getRequest().setOpShopId(order_shopno);
                                dcp_OrderShipping_OpenReq.getRequest().setOpNo("admin");
                                dcp_OrderShipping_OpenReq.getRequest().setOrderList(new String[]{order_no});

                                //调用目标服务
                                DCP_OrderShipping_Open dcp_OrderShipping_Open=new DCP_OrderShipping_Open();
                                DCP_OrderShipping_OpenRes dcp_OrderShipping_OpenRes=new DCP_OrderShipping_OpenRes();
                                dcp_OrderShipping_Open.setDao(StaticInfo.dao);
                                dcp_OrderShipping_Open.processDUID(dcp_OrderShipping_OpenReq,dcp_OrderShipping_OpenRes);

                                dcp_OrderShipping_Open=null;
                                dcp_OrderShipping_OpenRes=null;
                            }
                            catch (Exception e)
                            {
                                logger.error("\r\n***********商有云管家推送物流状态，调用订转销异常:EID=" + order_eId + "SHOPID=" +order_shopno + "\r\n orderno=" + order_no + "\r\n"+ e.getMessage());
                            }

                            //写订单日志
                            String memoLog="订单状态-->物流:" + distributionStatusName;
                            String LogStatus="0";
                            orderStatusLog oslog=new orderStatusLog();
                            oslog.setCallback_status("N");
                            oslog.setChannelId(order_chanelid);
                            oslog.setDisplay("1");
                            oslog.seteId(order_eId);
                            oslog.setLoadDocBillType(order_load_doctype);
                            oslog.setLoadDocOrderNo(order_no);
                            oslog.setLoadDocType(order_load_doctype);
                            oslog.setMachShopName(machShopName);
                            oslog.setMachShopNo(machShopId);
                            oslog.setMemo(memoLog);
                            oslog.setNeed_callback("N");
                            oslog.setNeed_notify("N");
                            oslog.setNotify_status("N");
                            oslog.setOpName("admin");
                            oslog.setOpNo("admin");
                            oslog.setOrderNo(order_no);
                            oslog.setShippingShopName(shippingName);
                            oslog.setShippingShopNo(shippingId);
                            oslog.setShopName(shopName);
                            oslog.setShopNo(shopId);
                            oslog.setStatus(status);//LogStatus不能是0和1，否则里面display重新赋值0啦
                            //
                            String statusType="2";
                            StringBuilder statusTypeName=new StringBuilder();
                            String statusName= HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
                            oslog.setStatusName(statusName);
                            oslog.setStatusType(statusType);
                            oslog.setStatusTypeName(statusTypeName.toString());
                            oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                            InsBean ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
                            lstData.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));

                            StaticInfo.dao.useTransactionProcessData(lstData);
                            
                        }
                        else  if (distributionStatus.equals("35"))//配送完成，可以更新订单完成，11完成
                        {
                            distributionStatusName="配送完成";
                            newDeliverystatus="3";
                            //不用重复写
                            if (deliverystatus.equals("3")) continue;
                            //更新订单商有云管家物流单信息
                            UptBean ubecOrder=new UptBean("DCP_ORDER");
                            ubecOrder.addCondition("EID", new DataValue(order_eId, Types.VARCHAR));
                            ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));
                            ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue(newDeliverystatus, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8已分配骑手
                            ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            lstData.add(new DataProcessBean(ubecOrder));


                            //自提3和同城配送6的单子订转销后直接完成，这里不需要
//                                            UptBean ubecOrder=new UptBean("DCP_ORDER");
//                                            ubecOrder.addCondition("EID", new DataValue(order_eId, Types.VARCHAR));
//                                            ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));
//
//                                            ubecOrder.addUpdateValue("STATUS", new DataValue("11", Types.VARCHAR));
//                                            lstData.add(new DataProcessBean(ubecOrder));

                            //写订单日志
                            String memoLog="订单状态-->物流:" + distributionStatusName;
                            String LogStatus="0";
                            orderStatusLog oslog=new orderStatusLog();
                            oslog.setCallback_status("N");
                            oslog.setChannelId(order_chanelid);
                            oslog.setDisplay("1");
                            oslog.seteId(order_eId);
                            oslog.setLoadDocBillType(order_load_doctype);
                            oslog.setLoadDocOrderNo(order_no);
                            oslog.setLoadDocType(order_load_doctype);
                            oslog.setMachShopName(machShopName);
                            oslog.setMachShopNo(machShopId);
                            oslog.setMemo(memoLog);
                            oslog.setNeed_callback("N");
                            oslog.setNeed_notify("N");
                            oslog.setNotify_status("N");
                            oslog.setOpName("admin");
                            oslog.setOpNo("admin");
                            oslog.setOrderNo(order_no);
                            oslog.setShippingShopName(shippingName);
                            oslog.setShippingShopNo(shippingId);
                            oslog.setShopName(shopName);
                            oslog.setShopNo(shopId);
                            oslog.setStatus(status);//LogStatus不能是0和1，否则里面display重新赋值0啦
                            //
                            String statusType="2";
                            StringBuilder statusTypeName=new StringBuilder();
                            String statusName= HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
                            oslog.setStatusName(statusName);
                            oslog.setStatusType(statusType);
                            oslog.setStatusTypeName(statusTypeName.toString());
                            oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                            InsBean ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
                            lstData.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));

                            StaticInfo.dao.useTransactionProcessData(lstData);
                        }
                        
                        YouZanUtilsV3.getInstance().Log(com.alibaba.fastjson.JSON.toJSONString(resjsobject));
                        Map<String, Object> otherMap = new HashMap<String, Object>();
                        otherMap.put("DISTRIBUTIONSTATUS", distributionStatus);//商有状态
                        //-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8已分配骑手
                        otherMap.put("DELIVERYSTATUS", newDeliverystatus);//物流状态
                        otherMap.put("DELIVERYSTATUSNAME", distributionStatusName);//状态描述
                        otherMap.put("DELNAME", riderName);//配送员
                        otherMap.put("DELTELEPHONE", riderPhone);//配送员手机号
                        otherMap.put("STATUS", status);//订单状态
                        
                        otherMap.put("SHOP", shopId);//
                        otherMap.put("SHOPNAME", shopName);//
                        otherMap.put("MACHSHOP", machShopId);//
                        otherMap.put("MACHSHOPNAME", machShopName);//
                        otherMap.put("SHIPPINGSHOP", shippingId);//
                        otherMap.put("SHIPPINGSHOPNAME", shippingName);//
                        otherMap.put("CHANNELID", order_chanelid);//
                        otherMap.put("LOADDOCTYPE", order_load_doctype);//
                        YouZanCallBackServiceV3.getInstance().updateDeliveryInfo(order_eId, order_no, null, null, otherMap);
                        

                    }
                }

            }
        }

        return null;
    }

    @Override
    protected void processDUID(String req, Map<String, Object> res) throws Exception
    {



    }

    @Override
    protected List<InsBean> prepareInsertData(String req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(String req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(String req) throws Exception
    {
        return null;
    }


}
