package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.ec.shangyou;
import com.dsc.spos.waimai.candao.candaoService;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.kdniao.kdnQGService;
import com.dsc.spos.waimai.kdniao.kdnTCService;
import com.dsc.spos.waimai.sftc.sftcService;
import com.dsc.spos.waimai.shansong.SHANSONGService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.thirdpart.PAOTUIService;
import com.dsc.spos.thirdpart.ThirdpartConstants;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.dada.DadaService;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.dsc.spos.waimai.yto.ytoService;

/**
 *
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ExpressOrderCancel extends InitJob {

    String pCompanyNo="";
    String pShop="";
    String pOrganizationNO="";
    String pBillNo="";

    public  String loggerInfoTitle = "自动取消快递单ExpressOrderCancel";

    public Logger logger = LogManager.getLogger(ExpressOrderCancel.class.getName());

    static boolean bRun = false;// 标记此服务是否正在执行中

    public ExpressOrderCancel()
    {

    }

    public ExpressOrderCancel(String companyNo,String shop,String organizationNO, String billNo)
    {
        pCompanyNo=companyNo;
        pShop=shop;
        pOrganizationNO=organizationNO;
        pBillNo=billNo;
    }
    public String doExe()
    {
        //测试用 begin
//		ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath*:dsmServiceModule.xml");
//        MySpringContext mySpringContext = new MySpringContext();
//        mySpringContext.setApplicationContext(classPathXmlApplicationContext);
//        DsmDAO dsmDao=(DsmDAO) mySpringContext.getContext().getBean("sposDao");
//        StaticInfo.dao=dsmDao;
        //测试用 end

        //返回信息
        String sReturnInfo="";
        //此服务是否正在执行中
        if (bRun && pCompanyNo.equals(""))
        {
            logger.info("\r\n*********"+loggerInfoTitle+"正在执行中,本次调用取消:************\r\n");

            sReturnInfo="定时传输任务-"+loggerInfoTitle+"正在执行中！";
            return sReturnInfo;
        }

        bRun=true;//

        logger.info("\r\n*********"+loggerInfoTitle+"定时调用Start:************\r\n");

        try
        {
            //12-已退单状态
            String sqlOrder="select * from dcp_order a where (a.status='12' or a.status='3') and a.autodelivery='Y' and (a.deliveryno is not null or ( (deliveryno is null or deliveryno='') and a.deliverystatus='0'  or a.deliverystatus='-1') ) "
                    + " and a.deliverystatus is not null and a.deliverystatus<>'3' and a.deliverystatus<>'4' and a.deliverystatus<>'5' and a.billtype=1";
            //如单据未定转销，会写一笔billtype=-1的资料
            //已做定转销，只会有一笔billtype=1的资料
            //二者关系   billtype=-1的资料，其REFUNDSOURCEBILLNO=(billtype=1资料的ORDERNO)

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, -3);
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
            String dateNow1=sdf1.format(cal.getTime());
            sqlOrder+=" and substr(a.update_time,0,14)>'"+dateNow1+"'";
            //根据条件
            if (pBillNo.equals("")==false)
            {
                sqlOrder+=" and a.eid='"+pCompanyNo+"' and a.orderno='"+pBillNo+"' and a.shop='"+pShop+"' ";
            }

            List<Map<String, Object>> getQData=this.doQueryData(sqlOrder, null);
            this.Log("ExpressOrderCancel定时任务。查询sql="+sqlOrder.toString());
            if (getQData != null && getQData.isEmpty() == false)
            {
                String sqlOutSaleset="select * from dcp_outsaleset where status='100' ";
                List<Map<String, Object>> getQData_OutSaleset=this.doQueryData(sqlOutSaleset, null);
                if (getQData_OutSaleset != null && getQData_OutSaleset.isEmpty() == false)
                {
                    for (Map<String, Object> map_order : getQData)
                    {
                        String eId = map_order.get("EID").toString();
                        String channelId = map_order.get("CHANNELID").toString();
                        String deliveryType = map_order.get("DELIVERYTYPE").toString();
                        String machShopId = map_order.get("MACHSHOP").toString();
                        String machShopName = map_order.get("MACHSHOPNAME").toString();
                        String shippingId = map_order.get("SHIPPINGSHOP").toString();
                        String shippingName = map_order.get("SHIPPINGSHOPNAME").toString();
                        String shopId = map_order.get("SHOP").toString();
                        String shopName = map_order.get("SHOPNAME").toString();
                        String loadDocType = map_order.get("LOADDOCTYPE").toString();
                        String orderNo_DB = map_order.get("ORDERNO").toString();
                        boolean invokeCancelRes = false;//调用三方取消接口，是否成功

                        //美团跑腿
                        if(ThirdpartConstants.pt_deliveryType.equals(deliveryType)){
                            List<Map<String, Object>> outSalesetList=getQData_OutSaleset.stream().filter(g->(g.get("DELIVERYTYPE")==null?"":g.get("DELIVERYTYPE").toString()).equals(ThirdpartConstants.pt_deliveryType)).collect(Collectors.toList());
                            if(outSalesetList==null||outSalesetList.size()==0){
                                continue;
                            }
                            //Map<String, Object> outSalesetMap=outSalesetList.get(0);
                            Map<String, Object> outSalesetMap=this.getDeliverySetByShippingShop(eId,deliveryType,shippingId);
                            if(outSalesetMap==null||outSalesetMap.isEmpty())
                            {
                                this.Log("ExpressOrderCancel定时任务。查询配送门店对应的物流设置参数为空，请检查物流参数设置，配送门店:"+shippingId+",物流类型:"+deliveryType);
                                continue;
                            }
                            //单号
                            String orderNo=map_order.get("ORDERNO").toString();

                            PAOTUIService pts=new PAOTUIService();
                            JSONObject json1=pts.cancelOrder(outSalesetMap, map_order);
                            String code=json1.getString("code");
                            Boolean result=false;
                            if("0".equals(code)){
                                JSONObject dataJson=json1.getJSONObject("data");
                                result=true;
                            }
                            if(result){
//								String id=resjsobject.getString("id");//订单ID
//								String code=resjsobject.getString("code");//订单单据编号
//								String created=resjsobject.getString("created");//订单创建时间		

                                //执行
                                List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                                UptBean ubecOrder=new UptBean("DCP_ORDER");
                                ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ubecOrder.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                                ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue("5", Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
                                ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
								ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

                                lstData.add(new DataProcessBean(ubecOrder));

                                //写订单日志
                                String LogStatus="5";
                                orderStatusLog oslog=new orderStatusLog();
                                oslog.setCallback_status("N");
                                oslog.setChannelId(channelId);
                                oslog.setDisplay("1");
                                oslog.seteId(eId);
                                oslog.setLoadDocType(loadDocType);
                                oslog.setMachShopName(machShopName);
                                oslog.setMachShopNo(machShopId);
                                oslog.setMemo("取消物流成功");
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
                                String statusType="2";
                                StringBuilder statusTypeName=new StringBuilder();
                                String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
                                oslog.setStatusName(statusName);
                                oslog.setStatusType(statusType);
                                oslog.setStatusTypeName(statusTypeName.toString());
                                oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                InsBean	ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
                                lstData.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));


                                StaticInfo.dao.useTransactionProcessData(lstData);
                                invokeCancelRes = true;
                            }else{
                                //执行
                                List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                                String memo1="取消物流失败";
                                String logSql="select * from DCP_ORDER_STATUSLOG where eid='"+eId+"' and orderno='"+orderNo+"' and memo='"+memo1+"'";
                                List<Map<String, Object>> logMaps=StaticInfo.dao.executeQuerySQL(logSql, null);
                                if(logMaps==null||logMaps.size()==0){

                                    //写订单日志
                                    String LogStatus="996";
                                    orderStatusLog oslog=new orderStatusLog();
                                    oslog.setCallback_status("N");
                                    oslog.setChannelId(channelId);
                                    oslog.setDisplay("1");
                                    oslog.seteId(eId);
                                    oslog.setLoadDocType(ThirdpartConstants.pt_deliveryType);
                                    oslog.setMachShopName(machShopName);
                                    oslog.setMachShopNo(machShopId);
                                    oslog.setMemo(memo1);
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
                                    String statusType="996";
                                    String statusName= "取消物流失败";
                                    oslog.setStatusName(statusName);
                                    oslog.setStatusType(statusType);
                                    oslog.setStatusTypeName("配送状态");
                                    oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                    InsBean	ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
                                    lstData.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));

                                    StaticInfo.dao.useTransactionProcessData(lstData);
                                }
                            }

                        }
                        else if (ThirdpartConstants.dada_deliveryType.equals(deliveryType))
                        {

                            List<Map<String, Object>> outSalesetList=getQData_OutSaleset.stream().filter(g->(g.get("DELIVERYTYPE")==null?"":g.get("DELIVERYTYPE").toString()).equals(ThirdpartConstants.dada_deliveryType)).collect(Collectors.toList());
                            if(outSalesetList==null||outSalesetList.size()==0){
                                continue;
                            }
                            //Map<String, Object> outSalesetMap=outSalesetList.get(0);
                            Map<String, Object> outSalesetMap=this.getDeliverySetByShippingShop(eId,deliveryType,shippingId);
                            if(outSalesetMap==null||outSalesetMap.isEmpty())
                            {
                                this.Log("ExpressOrderCancel定时任务。查询配送门店对应的物流设置参数为空，请检查物流参数设置，配送门店:"+shippingId+",物流类型:"+deliveryType);
                                continue;
                            }
                            //单号
                            String orderNo=map_order.get("ORDERNO").toString();

                            DadaService pts=new DadaService();
                            JSONObject json1=pts.cancelOrder(outSalesetMap, map_order);
                            if(json1 ==null)
                            {
                                continue;
                            }
                            String code=json1.getString("code");

                            if("0".equals(code))
                            {
                                List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                                UptBean ubecOrder=new UptBean("DCP_ORDER");
                                ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ubecOrder.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                                ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue("5", Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
                                ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
								ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

                                lstData.add(new DataProcessBean(ubecOrder));
                                StaticInfo.dao.useTransactionProcessData(lstData);

                                invokeCancelRes = true;
                                //写订单日志
                                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                                String LogStatus="5";
                                orderStatusLog oslog=new orderStatusLog();
                                oslog.setCallback_status("N");
                                oslog.setChannelId(channelId);
                                oslog.setDisplay("1");
                                oslog.seteId(eId);
                                oslog.setLoadDocType(loadDocType);
                                oslog.setMachShopName(machShopName);
                                oslog.setMachShopNo(machShopId);
                                oslog.setMemo("取消物流成功");
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
                                String statusType="2";
                                StringBuilder statusTypeName=new StringBuilder();
                                String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
                                oslog.setStatusName(statusName);
                                oslog.setStatusType(statusType);
                                oslog.setStatusTypeName(statusTypeName.toString());
                                oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                orderStatusLogList.add(oslog);
                                StringBuilder errorMessage = new StringBuilder();
                                boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);

                            }
                            else
                            {
                                String memo1="取消物流失败";
                                //写订单日志
                                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                                String LogStatus="996";
                                orderStatusLog oslog=new orderStatusLog();
                                oslog.setCallback_status("N");
                                oslog.setChannelId(channelId);
                                oslog.setDisplay("1");
                                oslog.seteId(eId);
                                oslog.setLoadDocType(loadDocType);
                                oslog.setMachShopName(machShopName);
                                oslog.setMachShopNo(machShopId);
                                oslog.setMemo(memo1);
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
                                String statusType="996";
                                String statusName= "取消物流失败";
                                oslog.setStatusName(statusName);
                                oslog.setStatusType(statusType);
                                oslog.setStatusTypeName("配送状态");
                                oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                orderStatusLogList.add(oslog);
                                StringBuilder errorMessage = new StringBuilder();
                                boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);


                            }




                        }
                        else if (ThirdpartConstants.yto_deliveryType.equals(deliveryType))
                        {

                            List<Map<String, Object>> outSalesetList=getQData_OutSaleset.stream().filter(g->(g.get("DELIVERYTYPE")==null?"":g.get("DELIVERYTYPE").toString()).equals(ThirdpartConstants.yto_deliveryType)).collect(Collectors.toList());
                            if(outSalesetList==null||outSalesetList.size()==0){
                                continue;
                            }

                            /*****************根据配送门店ID，先查询指定门店对应的物流配置***********************************/
                            //Map<String, Object> outSalesetMap=outSalesetList.get(0);
                            Map<String, Object> outSalesetMap=this.getDeliverySetByShippingShop(eId,deliveryType,shippingId);
                            if(outSalesetMap==null||outSalesetMap.isEmpty())
                            {
                                this.Log("ExpressOrderCancel定时任务。查询配送门店对应的物流设置参数为空，请检查物流参数设置，配送门店:"+shippingId+",物流类型:"+deliveryType);
                                continue;
                            }
                            //单号
                            String orderNo=map_order.get("ORDERNO").toString();

                            ytoService yto=new ytoService();
                            String res=yto.generateKOrderCancel(outSalesetMap, map_order);
                            if(res ==null||res.isEmpty())
                            {
                                continue;
                            }
                            JSONObject json1 = JSONObject.parseObject(res);
                            String code= "";

                            if(json1.containsKey("logisticsNo"))
                            {
                                code = "0";
                            }

                            String reason = "";
                            if(json1.containsKey("reason"))
                            {
                                reason = json1.get("reason").toString();
                            }


                            if("0".equals(code))
                            {
                                List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                                UptBean ubecOrder=new UptBean("DCP_ORDER");
                                ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ubecOrder.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                                ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue("5", Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
                                ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                                ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                                lstData.add(new DataProcessBean(ubecOrder));
                                StaticInfo.dao.useTransactionProcessData(lstData);

                                invokeCancelRes = true;
                                //写订单日志
                                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                                String LogStatus="5";
                                orderStatusLog oslog=new orderStatusLog();
                                oslog.setCallback_status("N");
                                oslog.setChannelId(channelId);
                                oslog.setDisplay("1");
                                oslog.seteId(eId);
                                oslog.setLoadDocType(loadDocType);
                                oslog.setMachShopName(machShopName);
                                oslog.setMachShopNo(machShopId);
                                oslog.setMemo("取消物流成功");
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
                                String statusType="2";
                                StringBuilder statusTypeName=new StringBuilder();
                                String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
                                oslog.setStatusName(statusName);
                                oslog.setStatusType(statusType);
                                oslog.setStatusTypeName(statusTypeName.toString());
                                oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                orderStatusLogList.add(oslog);
                                StringBuilder errorMessage = new StringBuilder();
                                boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);

                            }
                            else
                            {
                                String memo1="取消物流失败";
                                //写订单日志
                                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                                String LogStatus="996";
                                orderStatusLog oslog=new orderStatusLog();
                                oslog.setCallback_status("N");
                                oslog.setChannelId(channelId);
                                oslog.setDisplay("1");
                                oslog.seteId(eId);
                                oslog.setLoadDocType(loadDocType);
                                oslog.setMachShopName(machShopName);
                                oslog.setMachShopNo(machShopId);
                                oslog.setMemo(memo1);
                                if(!reason.isEmpty())
                                {
                                    oslog.setMemo(memo1+":"+reason);
                                }
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
                                String statusType="996";
                                String statusName= "取消物流失败";
                                oslog.setStatusName(statusName);
                                oslog.setStatusType(statusType);
                                oslog.setStatusTypeName("配送状态");
                                oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                orderStatusLogList.add(oslog);
                                StringBuilder errorMessage = new StringBuilder();
                                boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);


                            }




                        }
                        else if (ThirdpartConstants.sy_deliveryType.equals(deliveryType)) //商有物流
                        {
                            Map<String, Object> outSalesetMap=this.getDeliverySetByShippingShop(eId,deliveryType,shippingId);
                            if(outSalesetMap==null||outSalesetMap.isEmpty())
                            {
                                this.Log("ExpressOrderCancel定时任务。查询配送门店对应的物流设置参数为空，请检查物流参数设置，配送门店:"+shippingId+",物流类型:"+deliveryType);
                                continue;
                            }
                            if (outSalesetMap != null && outSalesetMap.isEmpty() == false)
                            {
                                String apiUrl=outSalesetMap.get("APIURL").toString();//http://steward-qa.syoo.cn 测试环境
                                String authToken= outSalesetMap.get("APPSECRET").toString();
                                String signKey=outSalesetMap.get("APPSIGNKEY").toString();
                                //
                                String order_companyno=eId;
                                String order_shopno=map_order.get("SHOP").toString();
                                String order_load_doctype=map_order.get("LOADDOCTYPE").toString();
                                String order_no=map_order.get("ORDERNO").toString();
                                String sy_Orderno=map_order.get("OUTDOCORDERNO").toString();

                                shangyou sy=new shangyou();
                                try
                                {
                                    boolean sy_OK=false;

                                    //取消商有订单
                                    String resbody=sy.orderValid(apiUrl,authToken,signKey,Long.parseLong(order_shopno),order_no,"商家同意取消订单");

                                    JSONObject resjsobject= JSONObject.parseObject(resbody);
                                    //
                                    String errorCode=resjsobject.containsKey("errorCode")?resjsobject.getString("errorCode"):"";//错误代码
                                    String errorDesc=resjsobject.containsKey("errorMsg")?resjsobject.getString("errorMsg"):"";//错误原因

                                    //成功000
                                    if (errorCode.equals("000"))
                                    {
                                        sy_OK=true;
                                    }
                                    else
                                    {
                                        logger.info("\r\n*********"+loggerInfoTitle+"取消商有订orderValid接口返回失败："+resbody+"************\r\n");
                                    }

                                    //取消商有物流
                                    resbody=sy.logisticsCancelorder(apiUrl,authToken,signKey,Long.parseLong(order_shopno),sy_Orderno,2);

                                    resjsobject= JSONObject.parseObject(resbody);
                                    //
                                    errorCode=resjsobject.containsKey("errorCode")?resjsobject.getString("errorCode"):"";//错误代码
                                    errorDesc=resjsobject.containsKey("errorMsg")?resjsobject.getString("errorMsg"):"";//错误原因
                                    //成功000
                                    if (errorCode.equals("000"))
                                    {
                                        sy_OK=true;
                                    }
                                    else
                                    {
                                        logger.info("\r\n*********"+loggerInfoTitle+"取消商有物流logisticsCancelorder接口返回失败："+resbody+"************\r\n");
                                    }

                                    if (sy_OK)
                                    {
                                        //执行
                                        List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                                        UptBean ubecOrder=new UptBean("dcp_order");
                                        ubecOrder.addCondition("EID", new DataValue(order_companyno, Types.VARCHAR));
                                        ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));

                                        ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue("5", Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
                                        lstData.add(new DataProcessBean(ubecOrder));

                                        //写订单日志
                                        String LogStatus="-1";
                                        orderStatusLog oslog=new orderStatusLog();
                                        oslog.setCallback_status("N");
                                        oslog.setChannelId(channelId);
                                        oslog.setDisplay("1");
                                        oslog.seteId(eId);
                                        oslog.setLoadDocBillType(order_load_doctype);
                                        oslog.setLoadDocOrderNo(order_no);
                                        oslog.setLoadDocType(order_load_doctype);
                                        oslog.setMachShopName(machShopName);
                                        oslog.setMachShopNo(machShopId);
                                        oslog.setMemo("取消商有订单、取消商有物流");
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
                                        oslog.setStatus(LogStatus);
                                        //
                                        String statusType="2";
                                        StringBuilder statusTypeName=new StringBuilder();
                                        String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
                                        oslog.setStatusName(statusName);
                                        oslog.setStatusType(statusType);
                                        oslog.setStatusTypeName(statusTypeName.toString());
                                        oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                        InsBean	ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
                                        lstData.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));


                                        StaticInfo.dao.useTransactionProcessData(lstData);
                                        invokeCancelRes = true;
                                    }

                                }
                                catch (Exception e)
                                {
                                    logger.info("\r\n*********"+loggerInfoTitle+"取消商有订单/取消商有物流配送有异常************\r\n");
                                }
                            }
                            else
                            {
                                logger.info("\r\n*********"+loggerInfoTitle+"物流廠商接口表dcp_outsaleset商有云管家物流未設置資料:************\r\n");

                                continue;
                            }

                        }
                        else if (ThirdpartConstants.sftc_deliveryType.equals(deliveryType))
                        {
                            List<Map<String, Object>> outSalesetList=getQData_OutSaleset.stream().filter(g->(g.get("DELIVERYTYPE")==null?"":g.get("DELIVERYTYPE").toString()).equals(ThirdpartConstants.sftc_deliveryType)).collect(Collectors.toList());
                            if(outSalesetList==null||outSalesetList.size()==0){
                                continue;
                            }
                            //Map<String, Object> outSalesetMap=outSalesetList.get(0);
                            Map<String, Object> outSalesetMap=this.getDeliverySetByShippingShop(eId,deliveryType,shippingId);
                            if(outSalesetMap==null||outSalesetMap.isEmpty())
                            {
                                this.Log("ExpressOrderCancel定时任务。查询配送门店对应的物流设置参数为空，请检查物流参数设置，配送门店:"+shippingId+",物流类型:"+deliveryType);
                                continue;
                            }

                            //单号
                            String orderNo=map_order.get("ORDERNO").toString();
                            sftcService sftc = new sftcService();
                            String res = sftc.sftcOrderCancel(outSalesetMap,map_order);
                            if(res ==null||res.isEmpty())
                            {
                                continue;
                            }
                            JSONObject json1 = JSONObject.parseObject(res);
                            String code=json1.get("error_code").toString();
                            if ("0".equals(code))
                            {
                                List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                                UptBean ubecOrder=new UptBean("DCP_ORDER");
                                ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ubecOrder.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                                ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue("5", Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
                                ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                                ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                                lstData.add(new DataProcessBean(ubecOrder));
                                StaticInfo.dao.useTransactionProcessData(lstData);
                                invokeCancelRes = true;

                                //写订单日志
                                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                                String LogStatus="5";
                                orderStatusLog oslog=new orderStatusLog();
                                oslog.setCallback_status("N");
                                oslog.setChannelId(channelId);
                                oslog.setDisplay("1");
                                oslog.seteId(eId);
                                oslog.setLoadDocType(loadDocType);
                                oslog.setMachShopName(machShopName);
                                oslog.setMachShopNo(machShopId);
                                oslog.setMemo("取消物流成功");
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
                                String statusType="2";
                                StringBuilder statusTypeName=new StringBuilder();
                                String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
                                oslog.setStatusName(statusName);
                                oslog.setStatusType(statusType);
                                oslog.setStatusTypeName(statusTypeName.toString());
                                oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                orderStatusLogList.add(oslog);
                                StringBuilder errorMessage = new StringBuilder();
                                boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);

                            }
                            else
                            {
                                String error_msg = "";
                                if (json1.containsKey("error_msg"))
                                {
                                    error_msg = json1.get("error_msg").toString();
                                }

                                String memo1="取消物流失败";
                                //写订单日志
                                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                                String LogStatus="996";
                                orderStatusLog oslog=new orderStatusLog();
                                oslog.setCallback_status("N");
                                oslog.setChannelId(channelId);
                                oslog.setDisplay("1");
                                oslog.seteId(eId);
                                oslog.setLoadDocType(loadDocType);
                                oslog.setMachShopName(machShopName);
                                oslog.setMachShopNo(machShopId);
                                oslog.setMemo(memo1);
                                if(!error_msg.isEmpty())
                                {
                                    oslog.setMemo(memo1+":"+error_msg);
                                }
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
                                String statusType="996";
                                String statusName= "取消物流失败";
                                oslog.setStatusName(statusName);
                                oslog.setStatusType(statusType);
                                oslog.setStatusTypeName("配送状态");
                                oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                orderStatusLogList.add(oslog);
                                StringBuilder errorMessage = new StringBuilder();
                                boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);

                            }


                        }
                        // 闪送跑腿
                        else if(ThirdpartConstants.ss_deliveryType.equals(deliveryType)){
                            List<Map<String, Object>> outSalesetList=getQData_OutSaleset.stream().filter(g->(g.get("DELIVERYTYPE")==null?"":g.get("DELIVERYTYPE").toString()).equals(ThirdpartConstants.ss_deliveryType)).collect(Collectors.toList());
                            if(outSalesetList==null||outSalesetList.size()==0){
                                continue;
                            }
                            //Map<String, Object> outSalesetMap=outSalesetList.get(0);
                            Map<String, Object> outSalesetMap=this.getDeliverySetByShippingShop(eId,deliveryType,shippingId);
                            if(outSalesetMap==null||outSalesetMap.isEmpty())
                            {
                                this.Log("ExpressOrderCancel定时任务。查询配送门店对应的物流设置参数为空，请检查物流参数设置，配送门店:"+shippingId+",物流类型:"+deliveryType);
                                continue;
                            }

                            //单号
                            String orderNo=map_order.get("ORDERNO").toString();
                            SHANSONGService shansongService  = new SHANSONGService();
                            String res =  shansongService.ssOrderCancel(outSalesetMap,map_order);
                            if(res ==null||res.isEmpty())
                            {
                                continue;
                            }
                            JSONObject json1 = JSONObject.parseObject(res);
                            String code=json1.get("status").toString();
                            if("200".equals(code)){
                                List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                                UptBean ubecOrder=new UptBean("DCP_ORDER");
                                ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ubecOrder.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                                ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue("5", Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
                                ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                                ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                                lstData.add(new DataProcessBean(ubecOrder));
                                StaticInfo.dao.useTransactionProcessData(lstData);
                                invokeCancelRes = true;

                                //写订单日志
                                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                                String LogStatus="5";
                                orderStatusLog oslog=new orderStatusLog();
                                oslog.setCallback_status("N");
                                oslog.setChannelId(channelId);
                                oslog.setDisplay("1");
                                oslog.seteId(eId);
                                oslog.setLoadDocType(loadDocType);
                                oslog.setMachShopName(machShopName);
                                oslog.setMachShopNo(machShopId);
                                oslog.setMemo("取消物流成功");
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
                                String statusType="2";
                                StringBuilder statusTypeName=new StringBuilder();
                                String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
                                oslog.setStatusName(statusName);
                                oslog.setStatusType(statusType);
                                oslog.setStatusTypeName(statusTypeName.toString());
                                oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                orderStatusLogList.add(oslog);
                                StringBuilder errorMessage = new StringBuilder();
                                boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
                            }else {
                                String error_msg = "";
                                if (json1.containsKey("msg"))
                                {
                                    error_msg = json1.get("msg").toString();
                                }

                                String memo1="取消物流失败";
                                //写订单日志
                                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                                String LogStatus="996";
                                orderStatusLog oslog=new orderStatusLog();
                                oslog.setCallback_status("N");
                                oslog.setChannelId(channelId);
                                oslog.setDisplay("1");
                                oslog.seteId(eId);
                                oslog.setLoadDocType(loadDocType);
                                oslog.setMachShopName(machShopName);
                                oslog.setMachShopNo(machShopId);
                                oslog.setMemo(memo1);
                                if(!error_msg.isEmpty())
                                {
                                    oslog.setMemo(memo1+":"+error_msg);
                                }
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
                                String statusType="996";
                                String statusName= "取消物流失败";
                                oslog.setStatusName(statusName);
                                oslog.setStatusType(statusType);
                                oslog.setStatusTypeName("配送状态");
                                oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                orderStatusLogList.add(oslog);
                                StringBuilder errorMessage = new StringBuilder();
                                boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
                            }
                        }
                        //快递鸟聚合物流
                        else if (ThirdpartConstants.kdn_deliveryType.equals(deliveryType))
                        {

                            List<Map<String, Object>> outSalesetList=getQData_OutSaleset.stream().filter(g->(g.get("DELIVERYTYPE")==null?"":g.get("DELIVERYTYPE").toString()).equals(ThirdpartConstants.kdn_deliveryType)).collect(Collectors.toList());
                            if(outSalesetList==null||outSalesetList.size()==0){
                                continue;
                            }

                            //单号
                            String orderNo=map_order.get("ORDERNO").toString();
                            String ref_deliveryNo=map_order.get("REF_DELIVERYNO").toString();
                            //快递鸟同城物流，只能用快递鸟单号取消
                            String out_deliveryNo=map_order.get("OUTDOCORDERNO").toString();//快递鸟的物流单号


                            String orderLoadDoctype=map_order.get("LOADDOCTYPE").toString();

                            String shipType = map_order.get("SHIPTYPE").toString();//配送方式( 1.订单来源渠道配送 2.全国快递 3.顾客自提 5ERP总部配送 6同城配送)
                            String shipMode = "";//kdn 物流产品类型 0全国快递，1同城快递
                            if ("2".equals(shipType))
                            {
                                shipMode = "0";
                            }
                            else if ("6".equals(shipType))
                            {
                                shipMode = "1";
                                //this.Log("自动发快递ExpressOrderCreate开始调用kdn快递鸟物流,配送方式shipType="+shipType+"(同城快递)还未实现,订单号orderNo="+orderNo);
                                //continue;
                            }
                            else
                            {
                                continue;
                            }

                            /*****************根据配送门店ID，先查询指定门店对应的物流配置***********************************/
                            //Map<String, Object> outSalesetMap=outSalesetList.get(0);
                            Map<String, Object> outSalesetMap=this.getKDNDeliverySetByShippingShop(eId,deliveryType,shippingId,shipMode);
                            if(outSalesetMap==null||outSalesetMap.isEmpty())
                            {
                                this.Log("ExpressOrderCancel定时任务。查询配送门店对应的物流设置参数为空，请检查物流参数设置，配送门店:"+shippingId+",物流类型:"+deliveryType);
                                continue;
                            }

                            String res= "";
                            boolean resultSuccess = false;
                            StringBuffer apiErrorMessage = new StringBuffer("");
                            if ("0".equals(shipMode))
                            {
                                kdnQGService kdn = null;
                                String EBusinessID = outSalesetMap.get("APPSIGNKEY").toString();
                                if ("350238".equals(EBusinessID))
                                {
                                    kdn = new kdnQGService(true);
                                }
                                else
                                {
                                    kdn = new kdnQGService();
                                }
                                res = kdn.kdnOrderCancel(ref_deliveryNo,outSalesetMap,orderNo,"","",apiErrorMessage);
                                if (res==null||res.isEmpty())
                                {
                                    //后面统一写异常的订单历程
                                }
                                else
                                {
                                    JSONObject jsonRes = JSONObject.parseObject(res);
                                    String Success = jsonRes.get("Success").toString();
                                    String Reason = jsonRes.getOrDefault("Reason","")==null?"":jsonRes.getOrDefault("Reason","").toString();
                                    if (!"true".equalsIgnoreCase(Success))
                                    {
                                        //后面统一写异常的订单历程
                                        apiErrorMessage.append("取消接口返回异常:"+Reason);
                                    }
                                    else
                                    {
                                        resultSuccess = true;
                                    }
                                }
                            }
                            else
                            {
                                kdnTCService kdn = null;
                                String EBusinessID = outSalesetMap.get("APPSIGNKEY").toString();
                                if ("1663339".equals(EBusinessID))
                                {
                                    kdn = new kdnTCService(true);
                                }
                                else
                                {
                                    kdn = new kdnTCService();
                                }
                                res = kdn.kdnOrderCancel(out_deliveryNo,outSalesetMap,orderNo,"","",apiErrorMessage);
                                if (res==null||res.isEmpty())
                                {
                                    //后面统一写异常的订单历程
                                }
                                else
                                {
                                    JSONObject jsonRes = JSONObject.parseObject(res);
                                    String resultCode = jsonRes.get("resultCode").toString();
                                    String reason = jsonRes.getOrDefault("reason","")==null?"":jsonRes.getOrDefault("reason","").toString();
                                    if (!"100".equals(resultCode))
                                    {
                                        //后面统一写异常的订单历程
                                        apiErrorMessage.append("取消接口返回异常:"+reason);
                                    }
                                    else
                                    {
                                        resultSuccess = true;
                                    }
                                }
                            }



                            if(resultSuccess)
                            {
                                invokeCancelRes = true;
                                List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                                UptBean ubecOrder=new UptBean("DCP_ORDER");
                                ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ubecOrder.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                                ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue("5", Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
                                ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                                ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                                lstData.add(new DataProcessBean(ubecOrder));
                                StaticInfo.dao.useTransactionProcessData(lstData);

                                //写订单日志
                                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                                String LogStatus="5";
                                orderStatusLog oslog=new orderStatusLog();
                                oslog.setCallback_status("N");
                                oslog.setChannelId(channelId);
                                oslog.setDisplay("1");
                                oslog.seteId(eId);
                                oslog.setLoadDocType(loadDocType);
                                oslog.setMachShopName(machShopName);
                                oslog.setMachShopNo(machShopId);
                                oslog.setMemo("取消物流成功");
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
                                String statusType="2";
                                StringBuilder statusTypeName=new StringBuilder();
                                String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
                                oslog.setStatusName(statusName);
                                oslog.setStatusType(statusType);
                                oslog.setStatusTypeName(statusTypeName.toString());
                                oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                orderStatusLogList.add(oslog);
                                StringBuilder errorMessage = new StringBuilder();
                                boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);

                            }
                            else
                            {
                                String memo1="取消物流失败";
                                //写订单日志
                                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                                String LogStatus="996";
                                orderStatusLog oslog=new orderStatusLog();
                                oslog.setCallback_status("N");
                                oslog.setChannelId(channelId);
                                oslog.setDisplay("1");
                                oslog.seteId(eId);
                                oslog.setLoadDocType(loadDocType);
                                oslog.setMachShopName(machShopName);
                                oslog.setMachShopNo(machShopId);
                                oslog.setMemo(memo1);
                                String errorMesStr = apiErrorMessage.toString();
                                if(!errorMesStr.isEmpty())
                                {
                                    oslog.setMemo(memo1+"<br>"+errorMesStr);
                                }
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
                                String statusType="996";
                                String statusName= "取消物流失败";
                                oslog.setStatusName(statusName);
                                oslog.setStatusType(statusType);
                                oslog.setStatusTypeName("配送状态");
                                oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                orderStatusLogList.add(oslog);
                                StringBuilder errorMessage = new StringBuilder();
                                boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);


                            }




                        }
                        //餐道物流
                        else if (ThirdpartConstants.cangdao_deliveryType.equals(deliveryType))
                        {

                            List<Map<String, Object>> outSalesetList=getQData_OutSaleset.stream().filter(g->(g.get("DELIVERYTYPE")==null?"":g.get("DELIVERYTYPE").toString()).equals(ThirdpartConstants.cangdao_deliveryType)).collect(Collectors.toList());
                            if(outSalesetList==null||outSalesetList.size()==0){
                                continue;
                            }

                            //单号
                            String orderNo=map_order.get("ORDERNO").toString();
                            String ref_deliveryNo=map_order.get("REF_DELIVERYNO").toString(); //商家物流单号

                            String out_deliveryNo=map_order.get("OUTDOCORDERNO").toString();//餐道返回的物流单号


                            String orderLoadDoctype=map_order.get("LOADDOCTYPE").toString();

                            /*****************根据配送门店ID，先查询指定门店对应的物流配置***********************************/
                            //Map<String, Object> outSalesetMap=outSalesetList.get(0);
                            Map<String, Object> outSalesetMap=this.getDeliverySetByShippingShop(eId,deliveryType,shippingId);
                            if(outSalesetMap==null||outSalesetMap.isEmpty())
                            {
                                this.Log("ExpressOrderCancel定时任务。查询配送门店对应的物流设置参数为空，请检查物流参数设置，配送门店:"+shippingId+",物流类型:"+deliveryType);
                                continue;
                            }

                            String res= "";
                            boolean resultSuccess = false;
                            StringBuffer apiErrorMessage = new StringBuffer("");

                            String accessKey = outSalesetMap.get("APPSIGNKEY").toString();//用户ID
                            String isTest = outSalesetMap.getOrDefault("ISTEST","").toString();
                            candaoService candao= null;
                            if ("Y".equals(isTest))
                            {
                                candao = new candaoService(true);
                            }
                            else
                            {
                                candao = new candaoService();
                            }
                            res = candao.candaoOrderCancel(ref_deliveryNo,outSalesetMap,map_order,apiErrorMessage);
                            if (res==null||res.isEmpty())
                            {
                                //后面统一写异常的订单历程
                            }
                            else
                            {
                                JSONObject jsonRes = JSONObject.parseObject(res);
                                String resultCode = jsonRes.get("status").toString();
                                String reason = jsonRes.getOrDefault("msg","")==null?"":jsonRes.getOrDefault("reason","").toString();
                                if (!"1".equals(resultCode))
                                {
                                    //后面统一写异常的订单历程
                                    apiErrorMessage.append("取消接口返回异常:"+reason);
                                }
                                else
                                {
                                    resultSuccess = true;
                                }
                            }


                            if(resultSuccess)
                            {
                                invokeCancelRes = true;
                                List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
                                UptBean ubecOrder=new UptBean("DCP_ORDER");
                                ubecOrder.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ubecOrder.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                                ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue("5", Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
                                ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                                ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                                lstData.add(new DataProcessBean(ubecOrder));
                                StaticInfo.dao.useTransactionProcessData(lstData);

                                //写订单日志
                                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                                String LogStatus="5";
                                orderStatusLog oslog=new orderStatusLog();
                                oslog.setCallback_status("N");
                                oslog.setChannelId(channelId);
                                oslog.setDisplay("1");
                                oslog.seteId(eId);
                                oslog.setLoadDocType(loadDocType);
                                oslog.setMachShopName(machShopName);
                                oslog.setMachShopNo(machShopId);
                                oslog.setMemo("取消物流成功");
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
                                String statusType="2";
                                StringBuilder statusTypeName=new StringBuilder();
                                String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
                                oslog.setStatusName(statusName);
                                oslog.setStatusType(statusType);
                                oslog.setStatusTypeName(statusTypeName.toString());
                                oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                orderStatusLogList.add(oslog);
                                StringBuilder errorMessage = new StringBuilder();
                                boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);

                            }
                            else
                            {
                                String memo1="取消物流失败";
                                //写订单日志
                                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                                String LogStatus="996";
                                orderStatusLog oslog=new orderStatusLog();
                                oslog.setCallback_status("N");
                                oslog.setChannelId(channelId);
                                oslog.setDisplay("1");
                                oslog.seteId(eId);
                                oslog.setLoadDocType(loadDocType);
                                oslog.setMachShopName(machShopName);
                                oslog.setMachShopNo(machShopId);
                                oslog.setMemo(memo1);
                                String errorMesStr = apiErrorMessage.toString();
                                if(!errorMesStr.isEmpty())
                                {
                                    oslog.setMemo(memo1+"<br>"+errorMesStr);
                                }
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
                                String statusType="996";
                                String statusName= "取消物流失败";
                                oslog.setStatusName(statusName);
                                oslog.setStatusType(statusType);
                                oslog.setStatusTypeName("配送状态");
                                oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                orderStatusLogList.add(oslog);
                                StringBuilder errorMessage = new StringBuilder();
                                boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);


                            }




                        }
                        else{ //其他物流
                            this.Log("【物流取消定时任务ExpressOrderCancel】物流类型deliveryType="+deliveryType+",暂未对接,订单号orderNo="+orderNo_DB);
                            continue;
                        }

                        if (invokeCancelRes)
                        {
                            if (orderLoadDocType.WECHAT.equals(loadDocType)||orderLoadDocType.MINI.equals(loadDocType)||orderLoadDocType.WECHAT.equals(loadDocType))
                            {
                                try
                                {
                                    org.json.JSONObject js=new org.json.JSONObject();
                                    js.put("serviceId", "OrderStatusUpdate");
                                    js.put("orderNo", orderNo_DB);
                                    js.put("statusType", "2");//状态类型 1=交易状态变更 2=物流状态变更 3=其他 4= 退单状态变更 5=推送状态变更 6=开票状态变更
                                    js.put("status", "4");//交易状态 0=未配送 1=配送中 2=已配送 3=确认收货 4=已取消 5=已下单6=已接单
                                    //delstatus中台物流状态 -1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单
                                    js.put("description", "物流已取消");
                                    js.put("oprId", "admin");
                                    js.put("orgType", "2");
                                    js.put("orgId", shippingId);
                                    js.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                                    org.json.JSONArray deliverInfo = new org.json.JSONArray();
                                    org.json.JSONObject body = new org.json.JSONObject();
                                    //body.put("expressType", deltype);//快递鸟 商城对应的字段是INT，暂时注释
                                    if (!"KDN".equals(deliveryType))
                                    {
                                        try
                                        {
                                            int deltype_i = Integer.parseInt(deliveryType);
                                            body.put("expressType", deliveryType);
                                        } catch (Exception e) {
                                        }
                                    }
                                    body.put("expressTypeCode", "");//真正的物流公司编码
                                    body.put("expressTypeName", "");//真正的物流公司名称
                                    body.put("expressBillNo", "");
                                    body.put("deliverPerson", "");
                                    body.put("deliverPhone", "");
                                    body.put("remark", "物流已取消");
                                    deliverInfo.put(body);

                                    js.put("deliverInfo", deliverInfo);

                                    String req_crm = js.toString();
                                    this.Log("【物流取消定时任务ExpressOrderCancel】物流取消成功,通知商城接口请求req:"+req_crm+",对应的订单单号orderNO="+orderNo_DB);
                                    String result_crm = HttpSend.MicroMarkSend(req_crm, eId, "OrderStatusUpdate",channelId);
                                    this.Log("【物流取消定时任务ExpressOrderCancel】物流取消成功,通知商城接口返回res:"+result_crm+",对应的订单单号orderNO="+orderNo_DB);

                                }
                                catch (Exception e)
                                {

                                }
                            }

                        }
                    }
                }
                else
                {
                    logger.info("\r\n*********"+loggerInfoTitle+"物流廠商接口表dcp_outsaleset未設置資料:************\r\n");

                    return "";
                }
            }
            else
            {
                logger.info("\r\n*********"+loggerInfoTitle+"無記錄SQL: "+sqlOrder+"************\r\n");
                this.Log(loggerInfoTitle+",查询无数据！");
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

                logger.error("\r\n******"+loggerInfoTitle+"报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\n******"+loggerInfoTitle+"报错信息" + e1.getMessage() + "******\r\n");
            }

            //
            sReturnInfo=""+loggerInfoTitle+"错误信息:" + e.getMessage();
        }
        finally
        {
            bRun=false;//
            logger.info("\r\n*********"+loggerInfoTitle+"定时调用End:************\r\n");
            this.Log(loggerInfoTitle+"定时调用End:");
        }
        return sReturnInfo;

    }

    public void Log(String log)  {
        try
        {
            HelpTools.writelog_fileName(log, "ExpressOrderCancel");

        } catch (Exception e)
        {
            // TODO: handle exception
        }

    }

    /**
     * 根据物流类型以及配送门店，获取相应的物流类型设置的参数（先查询配送门店为指定生效门店的物流，再查询默认生效全部门店的物流）
     * @param eId 企业ID
     * @param deliveryType 物流类型
     * @param shopId 配送门店ID
     * @return
     * @throws Exception
     */
    private Map<String, Object> getDeliverySetByShippingShop(String eId,String deliveryType,String shopId) throws  Exception
    {
        Map<String, Object> resultMap = null;
        try {
            StringBuffer sqlBuffer = new StringBuffer("");
            sqlBuffer.append(" select * from (");
            //先查询适用于指定门店的参数
            sqlBuffer.append(" select  CAST('2' AS NVARCHAR2(1)) IDX, A.* from dcp_outsaleset A "
                    + " LEFT JOIN dcp_outsaleset_shop B  on A.EID=B.EID AND A.APPID=B.APPID AND A.DELIVERYTYPE=B.DELIVERYTYPE"
                    + " WHERE A.EID='" + eId + "' AND A.DELIVERYTYPE='" + deliveryType + "'  and A.SHOPTYPE='2' and A.STATUS='100' AND B.SHOPID='" + shopId + "' ");

            sqlBuffer.append(" UNION ALL ");
            //查询下适用全部门店
            sqlBuffer.append(" select  CAST('1' AS NVARCHAR2(1)) IDX, A.* from dcp_outsaleset A "
                    + " WHERE A.EID='" + eId + "' AND A.DELIVERYTYPE='" + deliveryType + "'  and A.SHOPTYPE='1' and A.STATUS='100' ");

            sqlBuffer.append(") A ORDER BY A.IDX DESC");
            String sql = sqlBuffer.toString();
            this.Log("ExpressOrderCancel定时任务。【物流配置是否指定门店】查询物流设置参数sql=" + sql+",企业eId=" +eId+",物流类型deliveryType="+deliveryType+",配送门店shopId="+shopId);
            List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
            if (getDatas != null && getDatas.isEmpty() == false) {
                resultMap = getDatas.get(0);
                this.Log("ExpressOrderCancel定时任务。【物流配置是否指定门店】查询物流设置参数内容=" + resultMap.toString());
            }
            else
            {
                this.Log("ExpressOrderCancel定时任务。【物流配置是否指定门店】查询物流设置参数为空！企业eId=" +eId+",物流类型deliveryType="+deliveryType+",配送门店shopId="+shopId);
            }
        }

        catch (Exception e)
        {
            this.Log("ExpressOrderCancel定时任务。【物流配置是否指定门店】查询物流设置参数，异常:"+e.getMessage());
        }

        return resultMap;

    }


    /**
     * kdn快递鸟物流参数(全国快递和同城配送不一致)
     * @param eId
     * @param deliveryType
     * @param shopId
     * @param shipMode
     * @return
     * @throws Exception
     */
    public Map<String, Object> getKDNDeliverySetByShippingShop(String eId,String deliveryType,String shopId,String shipMode) throws  Exception
    {
        Map<String, Object> resultMap = null;
        if (!"1".equals(shipMode))
        {
            shipMode = "0";//0全国快递，1同城快递
        }
        try {
            StringBuffer sqlBuffer = new StringBuffer("");
            sqlBuffer.append(" select * from (");
            //先查询适用于指定门店的参数
            sqlBuffer.append(" select  CAST('2' AS NVARCHAR2(1)) IDX, A.* from dcp_outsaleset A "
                    + " LEFT JOIN dcp_outsaleset_shop B  on A.EID=B.EID AND A.APPID=B.APPID AND A.DELIVERYTYPE=B.DELIVERYTYPE"
                    + " WHERE A.STATUS='100' AND A.EID='" + eId + "' AND A.DELIVERYTYPE='" + deliveryType + "'  and A.SHOPTYPE='2'  AND B.SHOPID='" + shopId + "' AND SHIPMODE='"+shipMode+"' ");

            sqlBuffer.append(" UNION ALL ");
            //查询下适用全部门店
            sqlBuffer.append(" select  CAST('1' AS NVARCHAR2(1)) IDX, A.* from dcp_outsaleset A "
                    + " WHERE A.STATUS='100' AND  A.EID='" + eId + "' AND A.DELIVERYTYPE='" + deliveryType + "'  and A.SHOPTYPE='1' AND SHIPMODE='"+shipMode+"' ");

            sqlBuffer.append(") A ORDER BY A.IDX DESC");
            String sql = sqlBuffer.toString();
            this.Log("ExpressOrderCreate定时任务。【快递鸟物流类型】查询物流设置参数sql=" + sql+",企业eId=" +eId+",物流类型deliveryType="+deliveryType+",配送门店shopId="+shopId+",配送方式(0全国；1同城)shipMode="+shipMode);
            List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
            if (getDatas != null && getDatas.isEmpty() == false) {
                resultMap = getDatas.get(0);
                this.Log("ExpressOrderCreate定时任务。【快递鸟物流类型】查询物流设置参数内容=" + resultMap.toString());
            }
            else
            {
                this.Log("ExpressOrderCreate定时任务。【圆通物流类型】查询物流设置参数为空！企业eId=" +eId+",物流类型deliveryType="+deliveryType+",配送门店shopId="+shopId+",配送方式(0全国；1同城)shipMode="+shipMode);
            }
        }

        catch (Exception e)
        {
            this.Log("ExpressOrderCreate定时任务。【圆通物流类型】查询物流设置参数，异常:"+e.getMessage());
        }

        return resultMap;
    }

}
