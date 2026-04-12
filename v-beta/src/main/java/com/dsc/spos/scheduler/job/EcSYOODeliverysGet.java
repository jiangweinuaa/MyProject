package com.dsc.spos.scheduler.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderShipping_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderShipping_OpenRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.imp.json.DCP_OrderShipping_Open;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.ec.shangyou;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 查询商有云管家的物流信息，更新云中台订单
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class EcSYOODeliverysGet extends InitJob
{

    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pShop="";
    String pOrganizationNO="";
    String pBillNo="";

    Logger logger = LogManager.getLogger(EcSYOODeliverysGet.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public  EcSYOODeliverysGet()
    {

    }

    public  EcSYOODeliverysGet(String eId,String shopId,String organizationNO, String billNo)
    {
        pEId=eId;
        pShop=shopId;
        pOrganizationNO=organizationNO;
        pBillNo=billNo;
    }


    public String doExe()
    {
        //返回信息
        String sReturnInfo="";
        //此服务是否正在执行中
        if (bRun && pEId.equals(""))
        {
            logger.info("\r\n*********商有云管家订单物流信息查询EcSYOODeliverysGet正在执行中,本次调用取消:************\r\n");

            sReturnInfo="定时传输任务-商有云管家订单物流信息查询EcSYOODeliverysGet正在执行中！";
            return sReturnInfo;
        }

        bRun=true;//

        logger.info("\r\n*********商有云管家订单物流信息查询EcSYOODeliverysGet定时调用Start:************\r\n");

        try
        {
            //查询25商有云管家物流类型,没有物流单号的单据
            //3已拒单,不查
            //11已完成，不查
            //12整单已退单，不查
            //为避免追踪日志重复写，这里不判断物流状态字段，下面有跳过的处理
            //shipdate配送日期一周内，避免单据过多
            String sDate=new SimpleDateFormat("yyyyMMdd").format(new Date());
            sDate=PosPub.GetStringDate(sDate,-7);

            StringBuffer sqlOrder=new StringBuffer("select * from dcp_order a where a.deliverytype=25 and a.autodelivery='Y' and OUTDOCORDERNO is not null and a.status<>'3' and a.status<>'11' and a.status<>'12' and shipdate>='"+sDate+"' ");

            //根据条件
            if (pBillNo.equals("")==false)
            {
                sqlOrder.append(" and a.eid='"+pEId+"' and a.orderno='"+pBillNo+"' and a.SHOP='"+pShop+"' ");
            }

            //取25商有云管家同城配送物流
            String sqlLG = " select * from dcp_outsaleset where DELIVERYTYPE='25' and status='100' " ;
            List<Map<String, Object>> getLGData=this.doQueryData(sqlLG, null);
            if (getLGData != null && getLGData.isEmpty() == false)
            {
                //
                List<Map<String, Object>> getQData=this.doQueryData(sqlOrder.toString(), null);
                if (getQData != null && getQData.isEmpty() == false)
                {
                    for (Map<String, Object> map : getQData)
                    {
                        String order_eId=map.get("EID").toString();
                        String order_shopno=map.get("SHOP").toString();
                        String order_load_doctype=map.get("LOADDOCTYPE").toString();
                        String order_no=map.get("ORDERNO").toString();
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

                        try
                        {

                            //API地址目前没有字段存，固定的
                            String apiUrl=getLGData.get(0).get("APIURL").toString();//
                            String authToken=getLGData.get(0).get("APPSECRET").toString();
                            String signKey=getLGData.get(0).get("APPSIGNKEY").toString();

                            if (apiUrl.equals("") || authToken.equals("")|| signKey.equals(""))
                            {
                                logger.error("\r\n*********商有云管家订单物流信息查询EcSYOODeliverysGet EID="+order_eId+"物流资料API关键字段有空值************\r\n");
                                continue;
                            }
                            else
                            {
                                shangyou sy=new shangyou();
                                //查询物流详情，根据商有云管家订单号，没配送之前data对象是null要注意解析
                                String resbody= sy.logisticsOrderdetail(apiUrl,authToken,signKey,Long.parseLong(order_shopno),outDocorderno,2);

                                JSONObject resjsobject= JSONObject.parseObject(resbody);
                                //
                                String errorCode=resjsobject.containsKey("errorCode")?resjsobject.getString("errorCode"):"";//错误代码
                                String errorDesc=resjsobject.containsKey("errorMsg")?resjsobject.getString("errorMsg"):"";//错误原因

                                //成功000
                                if (errorCode.equals("000"))
                                {
                                    JSONObject resData= resjsobject.getJSONObject("data");
                                    if (resData != null)
                                    {
                                        //执行
                                        List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();

                                        String express_no=resData.getString("orderNo");//快递单号
                                        String express_code=resData.getString("channel");//快递公司代码
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
                                        String riderPhone=resData.getString("riderPhone");//未分配前为null,骑手电话
                                        String riderName=resData.getString("riderName");//未分配前为null，骑手名
                                        String distributionStatus=resData.getString("distributionStatus");//配送状态
                                        String distributionStatusName="";

                                        //处理已有状态不在重复写了
                                        String sql_orderStatusLog="select * from dcp_order_statuslog where eid='"+order_eId+"' and orderno='"+order_no+"'";
                                        List<Map<String, Object>> getOrderLogData=this.doQueryData(sql_orderStatusLog, null);

                                        if (distributionStatus.equals("1"))
                                        {

                                            String memoLog="订单状态-->物流已产生发货单("+express_code+express_name+",物流单号="+express_no+")";
                                            //不用重复写
                                            Map<String, Object> conditionValues = new HashMap<String, Object>();
                                            conditionValues = new HashMap<String, Object>();
                                            conditionValues.put("MEMO", memoLog);
                                            List<Map<String, Object>> getExistLog=MapDistinct.getWhereMap(getOrderLogData,conditionValues,false);
                                            if (getExistLog!=null && getExistLog.size()>0) continue;

                                            distributionStatusName="已下单";

                                            //更新订单商有云管家物流单信息
                                            UptBean ubecOrder=new UptBean("DCP_ORDER");
                                            ubecOrder.addCondition("EID", new DataValue(order_eId, Types.VARCHAR));
                                            ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));

                                            ubecOrder.addUpdateValue("DELIVERYNO", new DataValue(express_no, Types.VARCHAR));//物流单号
                                            ubecOrder.addUpdateValue("SUBDELIVERYCOMPANYNO", new DataValue(express_code, Types.VARCHAR));//物流公司代码
                                            ubecOrder.addUpdateValue("SUBDELIVERYCOMPANYNAME", new DataValue(express_name, Types.VARCHAR));//物流公司名称
                                            ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue(0, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8已分配骑手
											ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
											ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                                            lstData.add(new DataProcessBean(ubecOrder));

                                            //写订单日志，
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
                                            String result = HttpSend.MicroMarkSend(request, order_eId, "OrderStatusUpdate",order_chanelid);
                                            body=null;
                                            deliverInfo=null;
                                            js=null;

                                            StaticInfo.dao.useTransactionProcessData(lstData);
                                        }
                                        else  if (distributionStatus.equals("3"))
                                        {
                                            distributionStatusName="下单失败";

                                            String memoLog="订单状态-->物流:" + distributionStatusName;

                                            //不用重复写
                                            Map<String, Object> conditionValues = new HashMap<String, Object>();
                                            conditionValues = new HashMap<String, Object>();
                                            conditionValues.put("MEMO", memoLog);
                                            List<Map<String, Object>> getExistLog=MapDistinct.getWhereMap(getOrderLogData,conditionValues,false);
                                            if (getExistLog!=null && getExistLog.size()>0) continue;

                                            //更新订单商有云管家物流单信息
                                            UptBean ubecOrder=new UptBean("DCP_ORDER");
                                            ubecOrder.addCondition("EID", new DataValue(order_eId, Types.VARCHAR));
                                            ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));
                                            ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue(7, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8已分配骑手
											ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
											ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                                            lstData.add(new DataProcessBean(ubecOrder));

                                            //写订单日志
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

                                            String memoLog="订单状态-->物流:" + distributionStatusName;

                                            //不用重复写
                                            Map<String, Object> conditionValues = new HashMap<String, Object>();
                                            conditionValues = new HashMap<String, Object>();
                                            conditionValues.put("MEMO", memoLog);
                                            List<Map<String, Object>> getExistLog=MapDistinct.getWhereMap(getOrderLogData,conditionValues,false);
                                            if (getExistLog!=null && getExistLog.size()>0) continue;

                                            //更新订单商有云管家物流单信息
                                            UptBean ubecOrder=new UptBean("DCP_ORDER");
                                            ubecOrder.addCondition("EID", new DataValue(order_eId, Types.VARCHAR));
                                            ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));
                                            ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue(4, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8已分配骑手
											ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
											ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                                            lstData.add(new DataProcessBean(ubecOrder));

                                            //写订单日志
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

                                            String memoLog="订单状态-->物流:" + distributionStatusName;

                                            //不用重复写
                                            Map<String, Object> conditionValues = new HashMap<String, Object>();
                                            conditionValues = new HashMap<String, Object>();
                                            conditionValues.put("MEMO", memoLog);
                                            List<Map<String, Object>> getExistLog=MapDistinct.getWhereMap(getOrderLogData,conditionValues,false);
                                            if (getExistLog!=null && getExistLog.size()>0) continue;

                                            //更新订单商有云管家物流单信息
                                            UptBean ubecOrder=new UptBean("DCP_ORDER");
                                            ubecOrder.addCondition("EID", new DataValue(order_eId, Types.VARCHAR));
                                            ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));
                                            ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue(5, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8已分配骑手
											ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
											ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                                            lstData.add(new DataProcessBean(ubecOrder));

                                            //写订单日志
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

                                            String memoLog="订单状态-->物流:" + distributionStatusName+",骑手:"+ riderName+"("+riderPhone+")";

                                            //不用重复写
                                            Map<String, Object> conditionValues = new HashMap<String, Object>();
                                            conditionValues = new HashMap<String, Object>();
                                            conditionValues.put("MEMO", memoLog);
                                            List<Map<String, Object>> getExistLog=MapDistinct.getWhereMap(getOrderLogData,conditionValues,false);
                                            if (getExistLog!=null && getExistLog.size()>0) continue;

                                            //更新订单商有云管家物流单信息
                                            UptBean ubecOrder=new UptBean("DCP_ORDER");
                                            ubecOrder.addCondition("EID", new DataValue(order_eId, Types.VARCHAR));
                                            ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));

                                            ubecOrder.addUpdateValue("DELNAME", new DataValue(riderName, Types.VARCHAR));//
                                            ubecOrder.addUpdateValue("DELTELEPHONE", new DataValue(riderPhone, Types.VARCHAR));//
                                            ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue(8, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8已分配骑手
											ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
											ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                                            lstData.add(new DataProcessBean(ubecOrder));

                                            //写订单日志
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

                                            String memoLog="订单状态-->物流:" + distributionStatusName;

                                            //不用重复写
                                            Map<String, Object> conditionValues = new HashMap<String, Object>();
                                            conditionValues = new HashMap<String, Object>();
                                            conditionValues.put("MEMO", memoLog);
                                            List<Map<String, Object>> getExistLog=MapDistinct.getWhereMap(getOrderLogData,conditionValues,false);
                                            if (getExistLog!=null && getExistLog.size()>0) continue;

                                            //更新订单商有云管家物流单信息
                                            UptBean ubecOrder=new UptBean("DCP_ORDER");
                                            ubecOrder.addCondition("EID", new DataValue(order_eId, Types.VARCHAR));
                                            ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));
                                            ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue(6, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8已分配骑手
											ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
											ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                                            lstData.add(new DataProcessBean(ubecOrder));

                                            //写订单日志
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

                                            String memoLog="订单状态-->物流:" + distributionStatusName;

                                            //不用重复写
                                            Map<String, Object> conditionValues = new HashMap<String, Object>();
                                            conditionValues = new HashMap<String, Object>();
                                            conditionValues.put("MEMO", memoLog);
                                            List<Map<String, Object>> getExistLog=MapDistinct.getWhereMap(getOrderLogData,conditionValues,false);
                                            if (getExistLog!=null && getExistLog.size()>0) continue;

                                            //更新订单商有云管家物流单信息
                                            UptBean ubecOrder=new UptBean("DCP_ORDER");
                                            ubecOrder.addCondition("EID", new DataValue(order_eId, Types.VARCHAR));
                                            ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));
                                            ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue(2, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8已分配骑手
											ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
											ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                                            lstData.add(new DataProcessBean(ubecOrder));


                                            //调用订转销
                                            String json="";
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
                                                logger.error("\r\n***********商有云管家EcSYOODeliverysGet调用订转销异常:EID=" + order_eId + "SHOPID=" +order_shopno + "\r\n orderno=" + order_no + "\r\n"+ e.getMessage());
                                            }

                                            //写订单日志
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

                                            String memoLog="订单状态-->物流:" + distributionStatusName;

                                            //不用重复写
                                            Map<String, Object> conditionValues = new HashMap<String, Object>();
                                            conditionValues = new HashMap<String, Object>();
                                            conditionValues.put("MEMO", memoLog);
                                            List<Map<String, Object>> getExistLog=MapDistinct.getWhereMap(getOrderLogData,conditionValues,false);
                                            if (getExistLog!=null && getExistLog.size()>0) continue;

                                            //更新订单商有云管家物流单信息
                                            UptBean ubecOrder=new UptBean("DCP_ORDER");
                                            ubecOrder.addCondition("EID", new DataValue(order_eId, Types.VARCHAR));
                                            ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));
                                            ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue(3, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8已分配骑手
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
                                    }
                                }
                                else
                                {
                                    logger.error("\r\n*********商有云管家订单物流信息查询EcSYOODeliverysGet EID="+order_eId+",SHOP="+order_shopno+",LOADDOCTYPE="+order_load_doctype+",ORDERNO="+order_no+"调用商有云管家接口返回错误：errorCode="+errorCode+",errorDesc="+errorDesc+"************\r\n");
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            logger.error("\r\n*********商有云管家订单物流信息查询EcSYOODeliverysGet EID="+order_eId+",SHOP="+order_shopno+",LOADDOCTYPE="+order_load_doctype+",ORDERNO="+order_no+"发生异常错误："+e.getMessage()+"************\r\n");
                        }
                    }
                }
                else
                {
                    logger.info("\r\n*********商有云管家订单物流信息查询EcSYOODeliverysGet定时调用没有需要处理的订单************\r\n");
                }
            }
            else
            {
                logger.info("\r\n*********商有云管家订单物流信息查询EcSYOODeliverysGet定时调用dcp_outsaleset表中没有管易物流配置:************\r\n");
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

                logger.error("\r\n******商有云管家订单物流信息查询EcSYOODeliverysGet报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\n******商有云管家订单物流信息查询EcSYOODeliverysGet报错信息" + e1.getMessage() + "******\r\n");
            }

            //
            sReturnInfo="商有云管家订单物流信息查询EcSYOODeliverysGet错误信息:" + e.getMessage();
        }
        finally
        {
            bRun=false;//
            logger.info("\r\n*********商有云管家订单物流信息查询EcSYOODeliverysGet定时调用End:************\r\n");
        }
        return sReturnInfo;
    }


}
