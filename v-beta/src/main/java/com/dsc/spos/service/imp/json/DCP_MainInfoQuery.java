package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.json.cust.req.DCP_MainInfoQueryReq;
import com.dsc.spos.json.cust.res.DCP_MainInfoQueryRes;
import com.dsc.spos.json.cust.res.DCP_MainInfoQueryRes.*;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.*;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.google.gson.reflect.TypeToken;
import com.spreada.utils.chinese.ZHConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DCP_MainInfoQuery extends SPosBasicService<DCP_MainInfoQueryReq, DCP_MainInfoQueryRes> {
    
    Logger logger = LogManager.getLogger(DCP_MainInfoQuery.class.getName());
    
    @Override
    protected boolean isVerifyFail(DCP_MainInfoQueryReq req) throws Exception {
        return false;
    }
    
    @Override
    protected TypeToken<DCP_MainInfoQueryReq> getRequestType() {
        return new TypeToken<DCP_MainInfoQueryReq>() {};
    }
    
    @Override
    protected DCP_MainInfoQueryRes getResponseType() {
        return new DCP_MainInfoQueryRes();
    }
    
    @Override
    protected DCP_MainInfoQueryRes processJson(DCP_MainInfoQueryReq req) throws Exception {
        DCP_MainInfoQueryRes res = this.getResponse();
        // 2.wsDown 3.wsUp  5.monitor 6.eDate 7.job 8.DiskSpace 9.machine
        // (作废）0.etlDown 1.etlUp 4.monitorSpace
        String[] getTypes = req.getRequest().getGetType();
        boolean isWsDown = false;
        boolean isWsUp = false;
        boolean isMonitor = false;
        boolean isEDate = false;
        boolean isJob = false;
        boolean isDiskSpace = false;
        boolean isMachine = false;
        
        res.setEtlDown(new ArrayList<>());
        res.setEtlUp(new ArrayList<>());
        res.setWsDown(new ArrayList<>());
        res.setWsUp(new ArrayList<>());
        res.setMonitorSpace(new ArrayList<>());
        res.setMonitor(new ArrayList<>());
        res.seteDate(new ArrayList<>());
        res.setJob(new ArrayList<>());
        res.setDiskSpace(new ArrayList<>());
        res.setMachine(new ArrayList<>());
        
        try {
            if ((getTypes == null) || getTypes.length == 0) {
                isWsDown = true;
                isWsUp = true;
                isMonitor = true;
                isEDate = true;
                isJob = true;
                isDiskSpace = true;
                isMachine = true;
            } else {
                for (String getType : getTypes) {
                    switch (getType) {
                        case "2":
                            isWsDown = true;
                            break;
                        case "3":
                            isWsUp = true;
                            break;
                        case "5":
                            isMonitor = true;
                            break;
                        case "6":
                            isEDate = true;
                            break;
                        case "7":
                            isJob = true;
                            break;
                        case "8":
                            isDiskSpace = true;
                            break;
                        case "9":
                            isMachine = true;
                            break;
                    }
                }
            }
            
            if (isWsDown) {
                StringBuilder errorMessage = new StringBuilder();
                List<level1Elm_WsDown> wsDown = new ArrayList<>();
                boolean isFail = getWsDown(wsDown, req, errorMessage);
                if (!isFail) {
                    if (!wsDown.isEmpty()) {
                        res.setWsDown(wsDown);
                    }
                } else {
                    logger.error("\r\n" + "首页信息WS(ERP->云中台)查询异常：" + errorMessage + "\r\n");
                }
            }
            
            if (isWsUp) {
                StringBuilder errorMessage = new StringBuilder();
                List<level1Elm_WsUp> wsUp = new ArrayList<>();
                boolean isFail = getWsUp(wsUp, req, errorMessage);
                if (!isFail) {
                    if (!wsUp.isEmpty()) {
                        res.setWsUp(wsUp);
                    }
                } else {
                    logger.error("\r\n" + "首页信息WS(云中台->ERP)查询异常：" + errorMessage + "\r\n");
                }
                
            }
            
            if (isMonitor) {
                StringBuilder errorMessage = new StringBuilder();
                List<level1Elm_Monitor> monitor = new ArrayList<>();
                boolean isFail = getMonitor(monitor, req, errorMessage);
                if (!isFail) {
                    if (!monitor.isEmpty()) {
                        res.setMonitor(monitor);
                    }
                } else {
                    logger.error("\r\n" + "首页信息监控服务查询异常：" + errorMessage + "\r\n");
                }
            }
            
            if (isEDate) {
                StringBuilder errorMessage = new StringBuilder();
                List<level1Elm_EDate> eDate = new ArrayList<>();
                boolean isFail = getEDate(eDate, req, errorMessage);
                if (!isFail) {
                    if (!eDate.isEmpty()) {
                        res.seteDate(eDate);
                    }
                } else {
                    logger.error("\r\n" + "首页信息日结状况查询异常：" + errorMessage + "\r\n");
                }
            }
            
            if (isJob) {
                StringBuilder errorMessage = new StringBuilder();
                List<level1Elm_Job> job = new ArrayList<>();
                boolean isFail = getJob(job, req, errorMessage);
                if (!isFail) {
                    if (!job.isEmpty()) {
                        res.setJob(job);
                    }
                } else {
                    logger.error("\r\n" + "首页信息JOB查询异常：" + errorMessage + "\r\n");
                }
            }
            
            if (isDiskSpace) {
                StringBuilder errorMessage = new StringBuilder();
                List<level1Elm_diskSpace> diskSpace = new ArrayList<>();
                boolean isFail = getDiskSpace(diskSpace, req, errorMessage);
                if (!isFail) {
                    if (!diskSpace.isEmpty()) {
                        res.setDiskSpace(diskSpace);
                    }
                } else {
                    logger.error("\r\n" + "首页磁盘空间查询异常：" + errorMessage + "\r\n");
                }
            }
            
            if (isMachine) {
                StringBuilder errorMessage = new StringBuilder();
                List<level1Elm_machine> machine = new ArrayList<>();
                boolean isFail = getMachine(machine, req, errorMessage);
                if (!isFail) {
                    if (!machine.isEmpty()) {
                        res.setMachine(machine);
                    }
                } else {
                    logger.error("\r\n" + "首页机台版本查询异常：" + errorMessage + "\r\n");
                }
            }
            
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
            return res;
        }
        
        catch (Exception e) {
            // throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,
            // e.getMessage());
            // 此服务返回给前端都是成功，发生异常时只记录日志
            logger.error("\r\n" + "首页信息查询异常：" + e.getMessage() + "\r\n");
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
            return res;
        }
        
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }
    
    @Override
    protected String getQuerySql(DCP_MainInfoQueryReq req) throws Exception {
        return null;
    }
    
    private boolean getWsDown(List<level1Elm_WsDown> wsDown, DCP_MainInfoQueryReq req, StringBuilder errorMessage) {
        boolean isFail = false;
        String eId = req.geteId();
        String shopId = req.getShopId();
        String langType = req.getLangType();
        Calendar cal = Calendar.getInstance();// 获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String sDate = df.format(cal.getTime());
        df = new SimpleDateFormat("HHmmss");
        String sTime = df.format(cal.getTime());
        try {
            String sql =" "
                    + " SELECT EID,SERVICE_NAME,SUM(transQty) AS transQty,SUM(notTransQty) AS notTransQty FROM ( "
                    + " select EID,SERVICE_NAME,count(*) AS transQty,0 AS notTransQty from DCP_wslog "
                    + " where (EID='"+eId+"' or EID=' ') and service_type='2' and process_status='Y' "
                    + " and (modify_date='"+sDate+"' or (modify_date='"+PosPub.GetStringDate(sDate,-1)+"' and modify_time>='"+sTime+"')) "
                    + " group by EID,SERVICE_NAME "
                    + " union all "
                    + " select EID,SERVICE_NAME,0 AS transQty ,count(*) AS notTransQty  from DCP_wslog "
                    + " where (EID='"+eId+"' or EID=' ') and service_type='2' and process_status='N' "
                    + " group by EID,SERVICE_NAME ) GROUP BY EID,SERVICE_NAME  ORDER BY SERVICE_NAME ";
            
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (getQData !=null && !getQData.isEmpty()) {
                int item = 1 ;
                for ( Map<String, Object> oneDate:getQData) {
                    level1Elm_WsDown oneLv1 = new DCP_MainInfoQueryRes().new level1Elm_WsDown();
                    String serviceName = oneDate.get("SERVICE_NAME").toString();
                    if (Check.Null(serviceName)) {
                        serviceName = " ";
                    }
                    String transQty = oneDate.get("TRANSQTY").toString();
                    String notTransQty = oneDate.get("NOTTRANSQTY").toString();
                    String dataType="";
                    if (Check.Null(transQty)){
                        transQty = "0";
                    }
                    if (Check.Null(notTransQty)){
                        notTransQty = "0";
                    }
                    String status="0";      //0.成功 1.失败
                    switch (serviceName) {
                        case "pos.receiving.create":
                            dataType = "门店收货通知单新增";
                            if (langType.equals("zh_TW")) {
                                dataType = "門店收貨通知單新增";
                            }
                            break;
                        case "pos.counting.create":
                            dataType = "门店盘点计划单新增";
                            if (langType.equals("zh_TW")) {
                                dataType = "門店盤點計畫單新增";
                            }
                            break;
                        case "pos.return.update":
                            dataType = "门店退货出库单更新";
                            if (langType.equals("zh_TW")) {
                                dataType = "門店退貨出庫單更新";
                            }
                            break;
                        case "pos.day.end.check":
                            dataType = "门店日结检核";
                            if (langType.equals("zh_TW")){
                                dataType = "門店日結檢核";
                            }
                            break;
                        case "pos.adjust.create":
                            dataType = "门店库存调整单新增";
                            if (langType.equals("zh_TW")) {
                                dataType = "門店庫存調整單新增";
                            }
                            break;
                        case "pos.up.error.log.get":
                            dataType = "门店上传异常查询";
                            if (langType.equals("zh_TW")){
                                dataType = "門店上傳異常查詢";
                            }
                            break;
                        case "pos.scrap.update":
                            dataType = "门店报损单更新";
                            if (langType.equals("zh_TW")){
                                dataType = "門店報損單更新";
                            }
                            break;
                        case "pos.reject.create":
                            dataType = "门店单据驳回新增";
                            if (langType.equals("zh_TW")){
                                dataType = "門店單據駁回新增";
                            }
                            break;
                        case "pos.orgorder.update":
                            dataType = "节日订单审核";
                            if (langType.equals("zh_TW")){
                                dataType = "節日訂單審核";
                            }
                            break;
                        case "pos.undo.create":
                            dataType = "门店单据撤销";
                            if (langType.equals("zh_TW")){
                                dataType = "門店單據撤銷";
                            }
                            break;
                        case "pos.orderstatus.update":
                            dataType = "总部订单状态变更";
                            if (langType.equals("zh_TW")){
                                dataType = "總部訂單狀態變更";
                            }
                            break;
                        case "pos.orgorder.modify":
                            dataType = "云中台总部订单修改";
                            if (langType.equals("zh_TW")){
                                dataType = "雲中臺總部訂單修改";
                            }
                            break;
                        case "pos.requisition.update":
                            dataType = "要货单审核/反审核";
                            if (langType.equals("zh_TW")){
                                dataType = "要貨單審核/反審核";
                            }
                            break;
                        case "pos.fee.update":
                            dataType = "费用单状态更新";
                            if (langType.equals("zh_TW")) {
                                dataType = "費用單狀態更新";
                            }
                            break;
                        case "pos.requisition.create":
                            dataType = "要货单新增";
                            break;
                        case "pos.requisition.ecsflg":
                            dataType = "要货单结案";
                            break;
                        case " ":
                            dataType = "接口名称未能解析";
                            if (langType.equals("zh_TW")){
                                dataType = "接口名稱未能解析";
                            }
                            break;
                    }
                    
                    BigDecimal notTransQty_B = new BigDecimal(notTransQty);
                    if (notTransQty_B.compareTo(BigDecimal.ZERO) != 0) {
                        status ="1";
                    }
                    
                    oneLv1.setItem(String.valueOf(item));
                    oneLv1.setDataType(dataType);
                    oneLv1.setServiceName(serviceName);
                    oneLv1.setTransQty(transQty);
                    oneLv1.setNotTransQty(notTransQty);
                    oneLv1.setStatus(status);
                    wsDown.add(oneLv1);
                    item++;
                }
            }
            
            
            sql =" delete from DCP_wslog "
                    + " where (EID='"+eId+"' or EID=' ') and process_status='Y' and service_type='2' "
                    + " and modify_date< '"+PosPub.GetStringDate(sDate,-60)+"' " ;
            ExecBean exec = new ExecBean(sql);
            List<DataProcessBean> data  = new ArrayList<>();
            data.add(new DataProcessBean(exec));
            this.dao.useTransactionProcessData(data)	;
            
        } catch (Exception e) {
            errorMessage.append(e.getMessage());
            return true;
        }
        
        return false;
        
    }
    
    private boolean getWsUp(List<level1Elm_WsUp> wsUp, DCP_MainInfoQueryReq req, StringBuilder errorMessage) {
        boolean isFail = false;
        String eId = req.geteId();
        StringBuffer sqlbuf = new StringBuffer();
        
        try {
            Calendar cal = Calendar.getInstance();// 获得当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String sDate = df.format(cal.getTime());
            df = new SimpleDateFormat("HHmmss");
            String sTime = df.format(cal.getTime());
            
            //节点订单和JOB保持一致，只取>=昨天一天的
            Calendar yesterDay = Calendar.getInstance();
            yesterDay.add(Calendar.DATE, -1);
            String curdate=new SimpleDateFormat("yyyyMMdd").format(yesterDay.getTime());
            String waimaiDocTypeCon = "'"+orderLoadDocType.ELEME+"','"+orderLoadDocType.MEITUAN+"','"+orderLoadDocType.JDDJ+"','"+orderLoadDocType.MTSG+"','"+orderLoadDocType.DYWM+"'";
            String paraItem = "IsUploadWaimai";//是否上传外卖
            String isUploadWaimai = "Y";//默认都是Y
            String sql_para = " select ITEMVALUE from platform_basesettemp where status='100' and eid='"+eId+"' and  item='IsUploadWaimai' ";
            List<Map<String, Object>> getUploadPara = this.doQueryData(sql_para.toString(), null);
            if (getUploadPara!=null&&!getUploadPara.isEmpty())
            {
                if ("N".equals(getUploadPara.get(0).getOrDefault("ITEMVALUE","").toString()))
                {
                    isUploadWaimai = "N";
                }
            }
            
            sqlbuf.append(" select * from (  "
                    + " select service_name,sum(transQty) as transQty ,sum(notTransQty) as notTransQty from ( "
                    + " select 'RequisitionCreate' as service_name,count(*) as transQty,0 as notTransQty from DCP_porder "
                    + " where EID='" + eId + "' and status='2' and process_status='Y'  "
                    + " and (confirm_date='" + sDate + "' or (confirm_date='" + PosPub.GetStringDate(sDate, -1) + "' "
                    + " and confirm_time>='" + sTime + "')) "
                    + " union all "
                    + " select 'RequisitionCreate' as service_name,0 as transQty,count(*) notTransQty from DCP_porder "
                    + " where EID='" + eId + "' and status='2' and process_status='N' "
                    + " union all "
                    + " select 'DifferenceCreate' as service_name,count(*) as transQty,0 as notTransQty from DCP_difference "
                    + " where EID='" + eId + "' and status='1' and process_status='Y' "
                    + " and (confirm_date='" + sDate + "' or (confirm_date='" + PosPub.GetStringDate(sDate, -1) + "' "
                    + " and confirm_time>='" + sTime + "')) "
                    + " union all  "
                    + " select 'DifferenceCreate' as service_name,0 as transQty,count(*) notTransQty from DCP_difference "
                    + " where EID='" + eId + "' and status='1' and process_status='N' "
                    + " union all "
                    + " select 'ReturnCreate' as service_name,count(*) as transQty,0 as notTransQty from DCP_StockOut "
                    + " where EID='" + eId + "' and status='2' and process_status='Y' and doc_type in ('0','2')  "
                    + " and (confirm_date='" + sDate + "' or (confirm_date='" + PosPub.GetStringDate(sDate, -1) + "'"
                    + " and confirm_time>='" + sTime + "')) "
                    + " union all "
                    + " select 'ReturnCreate' as service_name,0 as transQty,count(*) notTransQty from DCP_StockOut "
                    + " where EID='"+ eId + "' and status='2' and doc_type in ('0','2') and process_status='N' "
                    + " union all "
                    + " select 'TransferCreate' as service_name,count(*) as transQty,0 as notTransQty from DCP_StockOut "
                    + " where EID='" + eId + "' and status>='2' and process_status='Y' and doc_type in ('1','4') "
                    + " and (confirm_date='" + sDate + "' or (confirm_date='" + PosPub.GetStringDate(sDate, -1) + "' "
                    + " and confirm_time>='" + sTime + "')) "
                    + " union all "
                    + " select 'TransferCreate' as service_name,0 as transQty,count(*) notTransQty from DCP_StockOut "
                    + " where EID='" + eId + "' and status>='2' and doc_type in ('1','4') and process_status='N' "
                    + " union all "
                    + " select 'TransferUpdate' as service_name,count(*) as transQty,0 as notTransQty from DCP_StockIn "
                    + " where EID='" + eId + "' and status='2' and process_status='Y' and doc_type in ('0','1','4')   "
                    + " and (confirm_date='" + sDate + "' or (confirm_date='" + PosPub.GetStringDate(sDate, -1) + "' "
                    + " and confirm_time>='" + sTime + "')) "
                    + " union all "
                    + " select 'TransferUpdate' as service_name,0 as transQty,count(*) notTransQty from DCP_StockIn "
                    + " where EID='" + eId + "' and status='2' and doc_type in ('0','1','4') and process_status='N' "
                    + " union all "
                    + " select 'IntegrateCountingCreate' as service_name,count(*) as transQty,0 as notTransQty from DCP_STOCKTAKE "
                    + " where EID='" + eId + "' and status='2' and process_status='Y' "
                    + " and (confirm_date='" + sDate + "' or (confirm_date='" + PosPub.GetStringDate(sDate, -1) + "' "
                    + " and confirm_time>='" + sTime + "')) "
                    + " union all "
                    + " select 'IntegrateCountingCreate' as service_name,0 as transQty,count(*) notTransQty from DCP_STOCKTAKE "
                    + " where EID='" + eId + "' and status='2' and process_status='N' "
                    + " union all "
                    + " select 'CompletionProcess' as service_name,count(*) as transQty,0 as notTransQty from DCP_PStockIn "
                    + " where EID='" + eId + "' and status='2' and process_status='Y' and doc_type in ('0','1','3')  "
                    + " and (confirm_date='" + sDate + "' or (confirm_date='" + PosPub.GetStringDate(sDate, -1) + "' "
                    + " and confirm_time>='" + sTime + "')) "
                    + " union all "
                    + " select 'CompletionProcess' as service_name,0 as transQty,count(*) notTransQty from DCP_PStockIn "
                    + " where EID='" + eId + "' and status='2' and doc_type in ('0','1','3') and process_status='N' "
                    + " union all "
                    + " select 'DisassemblyProcess' as service_name,count(*) as transQty,0 as notTransQty from DCP_PStockIn "
                    + " where EID='" + eId + "' and status='2' and process_status='Y' and doc_type in ('2','4')  "
                    + " and (confirm_date='" + sDate + "' or (confirm_date='" + PosPub.GetStringDate(sDate, -1) + "' "
                    + " and confirm_time>='" + sTime + "')) "
                    + " union all "
                    + " select 'DisassemblyProcess' as service_name,0 as transQty,count(*) notTransQty from DCP_PStockIn "
                    + " where EID='" + eId + "' and status='2' and doc_type in ('2','4') and process_status='N' "
                    + " union all "
                    + " select 'InventoryAdjustCreateLoss' as service_name,count(*) as transQty,0 as notTransQty from DCP_LStockOut "
                    + " where EID='"+ eId + "' and status='2' and process_status='Y'  "
                    + " and (confirm_date='" + sDate + "' or (confirm_date='" + PosPub.GetStringDate(sDate, -1) + "' "
                    + " and confirm_time>='" + sTime + "')) "
                    + " union all "
                    + " select 'InventoryAdjustCreateLoss' as service_name,0 as transQty,count(*) notTransQty from DCP_LStockOut "
                    + " where EID='" + eId + "' and status='2' and process_status='N' "
                    + " union all "
                    + " select 'InventoryAdjustCreateOtherIn' as service_name,count(*) as transQty,0 as notTransQty from DCP_StockIn "
                    + " where EID='" + eId + "' and status='2' and process_status='Y' and doc_type in ('3')  "
                    + " and (confirm_date='" + sDate + "' or (confirm_date='" + PosPub.GetStringDate(sDate, -1) + "' "
                    + " and confirm_time>='" + sTime + "')) "
                    + " union all "
                    + " select 'InventoryAdjustCreateOtherIn' as service_name,0 as transQty,count(*) notTransQty from DCP_StockIn "
                    + " where EID='" + eId + "' and status='2' and doc_type in ('3') and process_status='N' "
                    + " union all "
                    + " select 'InventoryAdjustCreateOtherOut' as service_name,count(*) as transQty,0 as notTransQty from DCP_StockOut "
                    + " where EID='" + eId + "' and status='2' and process_status='Y' and doc_type in ('3')  "
                    + " and (confirm_date='" + sDate + "' or (confirm_date='" + PosPub.GetStringDate(sDate, -1) + "' "
                    + " and confirm_time>='" + sTime + "')) "
                    + " union all "
                    + " select 'InventoryAdjustCreateOtherOut' as service_name,0 as transQty,count(*) notTransQty from DCP_StockOut "
                    + " where EID='" + eId + "' and status='2' and doc_type in ('3') and process_status='N' "
                    + " union all "
                    + " select 'ReceiptUpdate' as service_name,count(*) as transQty,0 as notTransQty from DCP_SStockIn "
                    + " where EID='" + eId + "' and status='2' and process_status='Y' "
                    + " and (confirm_date='" + sDate + "' or (confirm_date='" + PosPub.GetStringDate(sDate, -1) + "' "
                    + " and confirm_time>='" + sTime + "')) "
                    + " union all "
                    + " select 'ReceiptUpdate' as service_name,0 as transQty,count(*) notTransQty from DCP_SStockIn "
                    + " where EID='" + eId + "' and status='2' and process_status='N' "
                    + " union all "
                    + " select 'PurchaseReturnCreate' as service_name,count(*) as transQty,0 as notTransQty from DCP_SStockOut "
                    + " where EID='" + eId + "' and status='2' and process_status='Y' "
                    + " and (confirm_date='" + sDate + "' or (confirm_date='" + PosPub.GetStringDate(sDate, -1) + "' "
                    + " and confirm_time>='" + sTime + "')) "
                    + " union all "
                    + " select 'PurchaseReturnCreate' as service_name,0 as transQty,count(*) notTransQty from DCP_SStockOut "
                    + " where EID='" + eId + "' and status='2' and process_status='N' "
                    + " union all "
                    + " select 'FeeCreate' as service_name,count(*) as transQty,0 as notTransQty from DCP_Bfee "
                    + " where EID='" + eId + "' and status='2' and process_status='Y' "
                    + " and (confirm_date='" + sDate + "' or (confirm_date='" + PosPub.GetStringDate(sDate, -1) + "' "
                    + " and confirm_time>='" + sTime + "')) "
                    + " union all "
                    + " select 'FeeCreate' as service_name,0 as transQty,count(*) notTransQty from DCP_Bfee "
                    + " where EID='" + eId + "' and status='2' and process_status='N' "
                    + " union all "
                    + " select 'ReceiptEcsflg' as service_name,count(*) as transQty,0 as notTransQty from DCP_receiving "
                    + " where EID='" + eId + "' and status='7' and Doc_Type='2' and process_status='Y' "
                    + " and (confirm_date='" + sDate + "' or (confirm_date='" + PosPub.GetStringDate(sDate, -1) + "' "
                    + " and confirm_time>='" + sTime + "')) "
                    + " union all "
                    + " select 'ReceiptEcsflg' as service_name,0 as transQty,count(*) as notTransQty from DCP_receiving "
                    + " where EID='" + eId + "' and status='7' and Doc_Type='2' and process_status='N' "
                    + " union all "
                    + " select 'HolidayShoporderCreate_V3' as service_name,count(*) as transQty,0 as notTransQty from dcp_order a"
                    + " left join dcp_orderbasicsetting b on a.eid=b.eid and b.settingno='orderTransferErp' and b.status='100' "
                    + " left join dcp_ordertransfererpset c on a.eid=c.eid and a.shop=c.shop "
                    + " where a.EID='"+ eId + "' and a.process_status='Y' AND a.partition_date ='"+ sDate + "' and " //and a.BDATE ='"+ sDate + "'
                    + " (b.settingvalue='N' or b.settingvalue is null or (b.settingvalue='Y' and c.shop is not null)) "
                    + " union all "
                    + " select 'HolidayShoporderCreate_V3' as service_name,0 as transQty,count(*) as notTransQty from dcp_order a"
                    + " left join dcp_orderbasicsetting b on a.eid=b.eid and b.settingno='orderTransferErp' and b.status='100' "
                    + " left join dcp_ordertransfererpset c on a.eid=c.eid and a.shop=c.shop "
                    + " where a.EID='"+ eId + "' and a.process_status='N' AND a.partition_date >='"+ curdate + "' and " //and a.BDATE >='"+ curdate + "'
                    + " (b.settingvalue='N' or b.settingvalue is null or (b.settingvalue='Y' and c.shop is not null)) "
                    + " and A.DOWNGRADED<>'Y' ");
            if ("Y".equals(isUploadWaimai))
            {
                sqlbuf.append(" and (A.Loaddoctype not in ("+waimaiDocTypeCon+") or (A.Loaddoctype in ("+waimaiDocTypeCon+") and A.shop<>' ' and A.shop<>'' and A.Shop is not null))");
            }
            else
            {
                sqlbuf.append(" and (A.Loaddoctype not in ("+waimaiDocTypeCon+"))");
            }

            sqlbuf.append( " union all "
                    + " select 'OrderPayRefundCreate_V3' as service_name,count(*) as transQty,0 as notTransQty from DCP_ORDER_PAY A"
                    + " inner join DCP_ORDER b on a.EID = b.EID and a.SOURCEBILLTYPE = 'Order' and a.SOURCEBILLNO = b.ORDERNO and a.partition_date=b.partition_date"
                    + " left join dcp_orderbasicsetting c on b.eid=c.eid and c.settingno='orderTransferErp' and c.status='100' "
                    + " left join dcp_ordertransfererpset d on b.eid=d.eid and b.shop=d.shop "
                    + " where (a.process_status = 'Y') and a.SOURCEBILLTYPE = 'Order' "
                    + " and a.USETYPE != 'final' and a.partition_date ='"+ sDate +"'"  //AND B.BDATE ='"+ sDate +"'
                    + " and (c.settingvalue='N' or c.settingvalue is null or (c.settingvalue='Y' and d.shop is not null)) "
                    + " union all "
                    + " select 'OrderPayRefundCreate_V3' as service_name,0 as transQty,count(*) as notTransQty from DCP_ORDER_PAY A"
                    + " inner join DCP_ORDER b on a.EID = b.EID and a.SOURCEBILLTYPE = 'Order' and a.SOURCEBILLNO = b.ORDERNO and a.partition_date=b.partition_date"
                    + " left join dcp_orderbasicsetting c on b.eid=c.eid and c.settingno='orderTransferErp' and c.status='100' "
                    + " left join dcp_ordertransfererpset d on b.eid=d.eid and b.shop=d.shop "
                    + " where (a.process_status ='N') and a.SOURCEBILLTYPE = 'Order' "
                    + " and a.USETYPE != 'final' and a.partition_date >='"+ curdate +"'"  //AND B.BDATE >='"+ curdate +"'
                    + " and (c.settingvalue='N' or c.settingvalue is null or (c.settingvalue='Y' and d.shop is not null)) "
                    + " and b.DOWNGRADED<>'Y' and  b.process_status='Y' ");
            if ("Y".equals(isUploadWaimai))
            {
                sqlbuf.append(" and (b.Loaddoctype not in ("+waimaiDocTypeCon+") or (b.Loaddoctype in ("+waimaiDocTypeCon+") and b.shop<>' ' and b.shop<>'' and b.Shop is not null))");
            }
            else
            {
                sqlbuf.append(" and (b.Loaddoctype not in ("+waimaiDocTypeCon+"))");
            }
            sqlbuf.append("  )group by service_name  )a  inner join job_quartz b on a.service_name =b.job_name and b.status='100' ");
            
            List<Map<String, Object>> getQData = this.doQueryData(sqlbuf.toString(), null);
            
            String sql = " "
                    + " select service_name from DCP_wslog "
                    + " where EID='" + eId + "' and service_type='1' "
                    + " and process_status='N' "
                    + " and (modify_date='" + sDate + "' or (modify_date='" + PosPub.GetStringDate(sDate, -1) + "' "
                    + " and modify_time>='" + sTime + "'))"
                    + " group by service_name ";
            List<Map<String, Object>> getErrorQData = this.doQueryData(sql, null);
            boolean isError = true;
            if (getErrorQData == null || getErrorQData.isEmpty()) {
                isError = false;
            }
            
            int i = 1;
            for (Map<String, Object> oneDate : getQData) {
                level1Elm_WsUp oneLv1 = new DCP_MainInfoQueryRes().new level1Elm_WsUp();
                String dataType = oneDate.get("JOB_DISCRETION").toString();
                String jobName = oneDate.get("JOB_NAME").toString();
                if(req.getLangType().equals("zh_TW")) {
                    dataType =ZHConverter.convert(dataType,0);
                }
                String serviceName = oneDate.get("SERVICE_NAME").toString();
                String transQty = oneDate.get("TRANSQTY").toString();
                String notTransQty = oneDate.get("NOTTRANSQTY").toString();
                BigDecimal notTransQty_B = new BigDecimal(notTransQty);
                String status = "0"; // 0.成功 1.失败
                // 取出有异常的服务
                if (isError) {
                    switch (serviceName) {
                        case "RequisitionCreate":
                            serviceName = "requisition.create";
                            break;
                        case "DifferenceCreate":
                            serviceName = "difference.create";
                            break;
                        case "ReturnCreate":
                            serviceName = "return.create";
                            break;
                        case "TransferCreate":
                            serviceName = "transfer.create";
                            break;
                        case "TransferUpdate":
                            serviceName = "transfer.update";
                            break;
                        case "IntegrateCountingCreate":
                            serviceName = "integrate.counting.create";
                            break;
                        case "CompletionProcess":
                            serviceName = "completion.process";
                            break;
                        case "DisassemblyProcess":
                            serviceName = "disassembly.process";
                            break;
                        case "InventoryAdjustCreateLoss":
                            serviceName = "inventory.adjust.create";
                            break;
                        case "InventoryAdjustCreateOtherIn":
                            serviceName = "inventory.adjust.create";
                            break;
                        case "InventoryAdjustCreateOtherOut":
                            serviceName = "inventory.adjust.create";
                            break;
                        case "ReceiptUpdate":
                            serviceName = "receipt.update";
                            break;
                        case "PurchaseReturnCreate":
                            serviceName = "purchase.return.create";
                            break;
                        case "FeeCreate":
                            serviceName = "fee.create";
                            break;
                        case "ReceiptEcsflg":
                            serviceName = "receipt.ecsflg";
                            break;
                        case "HolidayShoporderCreate_V3":
                            serviceName = "holidayorder.create";
                            break;
                        case "OrderPayRefundCreate_V3":
                            serviceName = "orderpayrefund.create";
                            break;
                    }
                    
                    for (Map<String, Object> oneErrorDate : getErrorQData) {
                        String errorServiceName = oneErrorDate.get("SERVICE_NAME").toString();
                        if (errorServiceName.equals(serviceName) && notTransQty_B.compareTo(BigDecimal.ZERO) > 0) {
                            status = "1";
                        }
                    }
                }
                
                oneLv1.setItem(String.valueOf(i));
                oneLv1.setDataType(dataType);
                oneLv1.setJobName(jobName);
                oneLv1.setTransQty(transQty);
                oneLv1.setNotTransQty(notTransQty);
                oneLv1.setStatus(status);
                
                wsUp.add(oneLv1);
                i++;
            }
            
            
            //删除历史资料，嘉华一直都没有点ERP--》云中台，几十万笔历史资料冗余，查询变慢
            sql =" delete from DCP_wslog "
                    + " where (EID='"+eId+"' or EID=' ') and process_status='Y' and service_type='2' "
                    + " and modify_date< '"+PosPub.GetStringDate(sDate,-60)+"' " ;
            ExecBean exec = new ExecBean(sql);
            List<DataProcessBean> data  = new ArrayList<>();
            data.add(new DataProcessBean(exec));
            this.dao.useTransactionProcessData(data)	;
            
            
        } catch (Exception e) {
            errorMessage.append(e.getMessage());
            return true;
        }
        return false;
    }
    
    private boolean getMonitor(List<level1Elm_Monitor> monitor, DCP_MainInfoQueryReq req, StringBuilder errorMessage) {
        boolean isFail = false;
        String eId = req.geteId();
        String langType = req.getLangType();
        try {
            String YCUrl = PosPub.getPARA_SMS(dao, eId, "", "YCUrl");
            String YCKey = PosPub.getPARA_SMS(dao, eId, "", "YCKey");
            String YCSignKey = PosPub.getPARA_SMS(dao, eId, "", "YCSignKey");
            
            //// 3.0 参数调整 原参数：monitorReportUrl  sysReportURL
            String monitorReportUrl = PosPub.getPARA_SMS(dao, eId, "", "monitorReportUrl");
            String monitorSmsUrl = PosPub.getPARA_SMS(dao, eId, "", "monitorSmsUrl");
            
            if (Check.Null(monitorReportUrl) || monitorReportUrl.contains("IP")){
                monitorReportUrl="";
            }
            if (Check.Null(monitorSmsUrl) || monitorSmsUrl.contains("IP")){
                monitorSmsUrl="";
            }
            
            
            // 创建一个线程组
            ExecutorService exe = Executors.newCachedThreadPool(); // newFixedThreadPool(1);
            
            // 1.会员和支付**************************************************************************************
            if (YCUrl.trim().equals("") || YCKey.trim().equals("") || YCSignKey.trim().equals("")){
                errorMessage.append("会员接口地址参数未设置[YCUrl、YCKey、YCSignKey]!");
            } else {
                Callable callable = new thread_Monitor_HY(langType, YCUrl.trim(), YCKey.trim(), YCSignKey.trim(), monitor, errorMessage);
                Future future = exe.submit(callable);
                // future.get() 返回执行结果
            }
            
            // 3.云中台******************************************************************************************
            level1Elm_Monitor lvYZT_Monitor = new DCP_MainInfoQueryRes().new level1Elm_Monitor();
            lvYZT_Monitor.setItem("4");
            String lvYZT_ServiceName = "云中台服务";
            if (langType.equals("zh_TW")){
                lvYZT_ServiceName = "雲中台服務";
            }
            lvYZT_Monitor.setServiceID("DCP");
            lvYZT_Monitor.setServiceName(lvYZT_ServiceName);
            lvYZT_Monitor.setStatus("0");
            lvYZT_Monitor.setConcurrencyQty(StaticInfo.lUsercount + "");
            lvYZT_Monitor.setErrorMsg("");
            
            monitor.add(lvYZT_Monitor);
            
            // 4.报表服务器******************************************************************************************
            if (monitorReportUrl.trim().equals("")) {
                errorMessage.append("首页监控-报表服务地址参数未设置[monitorReportUrl]!");
            } else {
                Callable callable = new thread_Monitor_Report(langType, monitorReportUrl.trim(), monitor, errorMessage);
                Future future = exe.submit(callable);
                // future.get() 返回执行结果
            }
            
            // 5.供应商管理******************************************************************************************
            if (monitorSmsUrl.trim().equals("")) {
                errorMessage.append("首页监控-供应商管理服务地址参数未设置[monitorSmsUrl]!");
            } else {
                Callable callable = new thread_Monitor_SMS(langType, monitorSmsUrl.trim(), monitor, errorMessage);
                Future future = exe.submit(callable);
                // future.get() 返回执行结果
            }
            
            // 关闭线程池
            exe.shutdown();
            while (true) {
                if (exe.isTerminated()) {
                    break;
                }
            }
            
        } catch (Exception e) {
            isFail = true;
            errorMessage.append(e.getMessage());
        }
        
        return isFail;
    }
    
    private boolean getEDate(List<level1Elm_EDate> eDate, DCP_MainInfoQueryReq req, StringBuilder errorMessage) {
        boolean isFail = false;
        StringBuffer sqlbuf = new StringBuffer();
        String eId = req.geteId();
        String langType = req.getLangType();
        try {
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String sTime = new SimpleDateFormat("HHmmss").format(new Date());
            
            // 获取每天换日时间
            String shopEDateTime = "";
            
            //【ID1022996】【冠生园3.0】门店没有中午闭店功能 by jinzma 20220105
            String para_ShopEDateTimeCustomize = PosPub.getPARA_SMS(dao, eId,"", "ShopEDateTimeCustomize"); //是否启用门店自定义日结
            if (Check.Null(para_ShopEDateTimeCustomize)){
                para_ShopEDateTimeCustomize = "N";
            }
            if (!para_ShopEDateTimeCustomize.equals("Y")){
                shopEDateTime = PosPub.getPARA_SMS(dao, eId, "", "ShopEDateTime");
            }
            
            sqlbuf.append(" "
                    + " select a.organizationno,b.org_name,a.edate from ( "
                    + "   select organizationno,max(edate)as edate from DCP_stock_day a "
                    + "   where EID='" + eId + "' group by organizationno "
                    + " ) a "
                    + " left join DCP_ORG_lang b "
                    + " on b.EID='" + eId + "' and a.organizationno=b.organizationno and b.lang_type='" + langType + "' and b.status='100' "
                    + " inner join DCP_ORG c on c.EID='" + eId + "' and  a.organizationno=c.organizationno and c.status='100' and c.org_form='2' "
                    + " order by organizationno ");
            List<Map<String, Object>> getQData = this.doQueryData(sqlbuf.toString(), null);
            if (getQData != null && !getQData.isEmpty()) {
                int i = 1;
                // 查询日结JOB异常日志 BY JZMA 20191016
                String sql = " select EID,SHOPID,ERROR_MSG from DCP_JOBLOG where job_name='ShopEDate' and EID='" + eId + "'  ";
                List<Map<String, Object>> getQDataJob = this.doQueryData(sql, null);
                for (Map<String, Object> oneData : getQData) {
                    level1Elm_EDate oneLv1 = new DCP_MainInfoQueryRes().new level1Elm_EDate();
                    oneLv1.setItem(String.valueOf(i));
                    oneLv1.setShopId(oneData.get("ORGANIZATIONNO").toString());
                    oneLv1.setShopName(oneData.get("ORG_NAME").toString());
                    oneLv1.seteDate(oneData.get("EDATE").toString());
                    
                    //【ID1022996】【冠生园3.0】门店没有中午闭店功能 by jinzma 20220105
                    if (para_ShopEDateTimeCustomize.equals("Y")){
                        shopEDateTime = PosPub.getPARA_SMS(dao, eId, oneData.get("ORGANIZATIONNO").toString(), "ShopEDateTime");
                    }
                    if (Check.Null(shopEDateTime)) {
                        shopEDateTime = "020000";   //日结时间默认2点
                    }
                    
                    String rightEdate;
                    // 系统时间>换日时间
                    if (PosPub.compare_time(sTime, shopEDateTime) == 1) {
                        rightEdate = PosPub.GetStringDate(sDate, -1); // 正确日结日期=系统日期-1天
                    } else {
                        rightEdate = PosPub.GetStringDate(sDate, -2); // 正确日结日期=系统日期-2天
                    }
                    
                    //【ID1025197】【潮品3.0】所有门店日结不成功  by jinzma 20220413
					/* 门店自定义日结统一在2点执行，如果自动日结时间>2点，门店日结时就会少结1天，举例如下
					   例： 系统在 2022-04-13 2:00 执行自动日结
                            1、系统判断的日结日期 = 20220412
                            2、门店入账日期因为没有小于当前时间，计算出来的入账日期是20220412
                            3、因为日结日期==入账日期，所以日结会终止，并写入一笔异常
					 */
                    if (para_ShopEDateTimeCustomize.equals("Y") && PosPub.compare_time(shopEDateTime,"020000") == 1){
                        rightEdate = PosPub.GetStringDate(sDate, -2); // 因开启门店自定义日结，所以正确日结日期=系统日期-2天
                    }
                    
                    // 系统日结日期<正确日结日期提示异常
                    String errorMsg = "";
                    if (PosPub.compare_date(oneData.get("EDATE").toString(), rightEdate) == -1) {
                        oneLv1.setStatus("1");
                        // 获取日结JOB异常日志 BY JZMA 20191016
                        if (getQDataJob != null && !getQDataJob.isEmpty()) {
                            String shopId = oneData.get("ORGANIZATIONNO").toString();
                            for (Map<String, Object> oneDataJob : getQDataJob) {
                                String job_shop = oneDataJob.get("SHOPID").toString();
                                if (shopId.equals(job_shop)) {
                                    errorMsg = oneDataJob.get("ERROR_MSG").toString();
                                    break;
                                }
                            }
                        }
                    } else {
                        oneLv1.setStatus("0");
                    }
                    
                    oneLv1.setErrorMsg(errorMsg);
                    eDate.add(oneLv1);
                    i++;
                }
            }
            
        } catch (Exception e) {
            errorMessage.append(e.getMessage());
            return true;
        }
        return false;
    }
    
    private boolean getJob(List<level1Elm_Job> job, DCP_MainInfoQueryReq req, StringBuilder errorMessage) {
        boolean isFail = false;
        StringBuffer sqlbuf = new StringBuffer();
        String eId = req.geteId();
        try {
            sqlbuf.append(
                    " select a.job_name,a.job_discretion,b.EID,b.create_date,b.create_time,b.modify_date,b.modify_time,b.error_msg "
                            + " from job_quartz a left join DCP_joblog b "
                            + " on a.job_name=b.job_name and (b.EID=' ' or  b.EID='" + eId
                            + "') and b.job_name<>'ShopEDate' " // 日结异常在右侧门店日结里面单独显示
                            + " where  a.status='100' and a.MAINTYPE='1'  " + " order by a.job_name ");
            List<Map<String, Object>> getQData = this.doQueryData(sqlbuf.toString(), null);
            if (getQData != null && !getQData.isEmpty()) {
                int i = 1;
                for (Map<String, Object> oneData : getQData) {
                    level1Elm_Job oneLv1 = new DCP_MainInfoQueryRes().new level1Elm_Job();
                    oneLv1.setItem(String.valueOf(i));
                    oneLv1.setJobName(oneData.get("JOB_NAME").toString());
                    oneLv1.setJobDiscretion(oneData.get("JOB_DISCRETION").toString());
                    String status = "0"; // 0.成功 1.失败
                    String errorMsg = "";
                    if (!Check.Null(oneData.get("CREATE_DATE").toString())) {
                        status = "1";
                        errorMsg = oneData.get("ERROR_MSG").toString();
                    }
                    oneLv1.setStatus(status);
                    oneLv1.setErrorMsg(errorMsg);
                    job.add(oneLv1);
                    i++;
                }
            }
        } catch (Exception e) {
            errorMessage.append(e.getMessage());
            return true;
        }
        return false;
    }
    
    private boolean getDiskSpace(List<level1Elm_diskSpace> diskSpace, DCP_MainInfoQueryReq req,
                                 StringBuilder errorMessage) {
        boolean isFail = false;
        String langType = req.getLangType();
        String serverName = "中台服务器";
        if (langType.equals("zh_TW")){
            serverName = "中臺服務器";
        }
        
        try {
            File[] files = File.listRoots();
            for (int i = 0; i < files.length; i++) {
                level1Elm_diskSpace oneLv1 = new DCP_MainInfoQueryRes().new level1Elm_diskSpace();
                long totalSpace = files[i].getTotalSpace() / (1024 * 1024);
                long freeSpace = files[i].getFreeSpace() / (1024 * 1024);
                
                BigDecimal totalSpace_B = new BigDecimal(totalSpace);
                BigDecimal freeSpace_B = new BigDecimal(freeSpace);
                
                totalSpace_B = totalSpace_B.divide(new BigDecimal("1024"),0, RoundingMode.HALF_UP);
                freeSpace_B = freeSpace_B.divide(new BigDecimal("1024"),0, RoundingMode.HALF_UP);
                BigDecimal usedSpace_B = totalSpace_B.subtract(freeSpace_B);
                
                // 磁盘总容量为0的不统计(类似CD等)
                if (totalSpace_B.compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }
                
                String drive = files[i].toString();
                drive = drive.substring(0, 1);
                
                // 硬盘空间取小数位两位
                totalSpace_B = totalSpace_B.setScale(2, RoundingMode.HALF_UP);
                freeSpace_B = freeSpace_B.setScale(2, RoundingMode.HALF_UP);
                usedSpace_B = usedSpace_B.setScale(2, RoundingMode.HALF_UP);
                
                oneLv1.setItem(String.valueOf(i + 1));
                oneLv1.setServerName(serverName);
                oneLv1.setDrive(drive);
                oneLv1.setTotSpace(totalSpace_B.toString());
                oneLv1.setUsedSpace(usedSpace_B.toString());
                oneLv1.setFreeSpace(freeSpace_B.toString());
                
                diskSpace.add(oneLv1);
            }
            
        } catch (Exception e) {
            errorMessage.append(e.getMessage());
            return true;
        }
        return false;
    }
    
    private boolean getMachine(List<level1Elm_machine> machine, DCP_MainInfoQueryReq req, StringBuilder errorMessage) {
        boolean isFail = false;
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf = new StringBuffer();
        try {
            sqlbuf.append(" select a.SHOPID,b.org_name,a.machine,a.machinename,a.vernum,a.rdate from platform_machine a "
                    + " left join  DCP_ORG_lang b on a.EID=b.EID and a.SHOPID=b.organizationno "
                    + " and b.lang_type='"+ langType + "' "
                    + " where a.EID='" + eId + "' and b.lang_type='" + langType + "'  ");
            
            List<Map<String, Object>> getQData = this.doQueryData(sqlbuf.toString(), null);
            
            Map<String, Boolean> condition = new HashMap<>(); // 查询条件
            condition.put("SHOPID", true);
            // 调用过滤函数
            List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQData, condition);
            if (getQHeader != null && !getQHeader.isEmpty()) {
                int i = 1;
                for (Map<String, Object> oneData : getQHeader) {
                    level1Elm_machine oneLv1 = new DCP_MainInfoQueryRes().new level1Elm_machine();
                    oneLv1.setMachine_detail(new ArrayList<>());
                    oneLv1.setItem(String.valueOf(i));
                    oneLv1.setShopId(oneData.get("SHOPID").toString());
                    oneLv1.setShopName(oneData.get("ORG_NAME").toString());
                    
                    String getShopNO = oneData.get("SHOPID").toString();
                    
                    for (Map<String, Object> oneData_detail : getQData) {
                        level2Elm_machineDetail oneLv2 = new DCP_MainInfoQueryRes().new level2Elm_machineDetail();
                        if (getShopNO.equals(oneData_detail.get("SHOPID").toString())) {
                            oneLv2.setMachineNo(oneData_detail.get("MACHINE").toString());
                            oneLv2.setMachineName(oneData_detail.get("MACHINENAME").toString());
                            oneLv2.setVerNum(oneData_detail.get("VERNUM").toString());
                            oneLv2.setrDate(oneData_detail.get("RDATE").toString());
                            oneLv1.getMachine_detail().add(oneLv2);
                        }
                    }
                    machine.add(oneLv1);
                    i++;
                }
            }
        } catch (Exception e) {
            errorMessage.append(e.getMessage());
            return true;
        }
        return false;
    }
    
    private class thread_Monitor_HY implements Callable {
        String langType;
        String YCUrl;
        String YCKey;
        String YCSignKey;
        List<level1Elm_Monitor> monitor;
        StringBuilder errorMessage;
        
        thread_Monitor_HY(String langType, String YCUrl, String YCKey, String YCSignKey,
                          List<level1Elm_Monitor> monitor, StringBuilder errorMessage) {
            this.langType = langType;
            this.YCUrl = YCUrl;
            this.YCKey = YCKey;
            this.YCSignKey = YCSignKey;
            this.monitor = monitor;
            this.errorMessage = errorMessage;
        }
        
        public Object call() throws Exception {
            
            boolean isFail = false;
            try {
                JSONObject payReq = new JSONObject();
                JSONObject reqheader = new JSONObject();
                JSONObject signheader = new JSONObject();
                JSONObject requestheader = new JSONObject();
                String req_sign = reqheader + YCSignKey;
                EncryptUtils eu = new EncryptUtils();
                req_sign = eu.encodeMD5(req_sign);
                
                //
                signheader.put("key", YCKey);//
                signheader.put("sign", req_sign);// md5
                payReq.put("serviceId", "main.monitor.get"); // 之前是
                // MainErrorInfoGetDCP
                payReq.put("lang_type", langType);
                payReq.put("sign", signheader);
                payReq.put("request", requestheader);
                
                String str = payReq.toString();
                String resbody = HttpSend.MainInfoSendcom(str, YCUrl);
                // logger.info("\r\n******首页监控-请求会员服务返回参数： "+ "\r\n" + resbody +
                // "******\r\n");
                
                JSONObject jsonres = new JSONObject(resbody);
                String serviceDescription = jsonres.get("serviceDescription").toString();
                String serviceStatus = jsonres.get("serviceStatus").toString();
                
                level1Elm_Monitor lvHY_Monitor = new DCP_MainInfoQueryRes().new level1Elm_Monitor();
                if (jsonres.get("success").toString().equals("true")) {
                    JSONArray std_data_res = jsonres.getJSONArray("datas");
                    String service_name = std_data_res.getJSONObject(0).getString("service_name");// 服务名称
                    String status = std_data_res.getJSONObject(0).getString("status");// 状态
                    String concurrency_qty = std_data_res.getJSONObject(0).getString("concurrency_qty");// 并发数
                    String error_msg = std_data_res.getJSONObject(0).getString("error_msg");// 错误描述
                    if (!PosPub.isNumeric(concurrency_qty)) {
                        concurrency_qty = "0";
                    }
                    
                    // by jinzma 20190614 会员返回的服务名如果为空，给默认值
                    if (Check.Null(service_name) || service_name.trim().equals("")) {
                        service_name = "会员服务";
                        if (langType.equals("zh_TW"))
                            service_name = "會員服務";
                    }
                    
                    lvHY_Monitor.setItem("1");
                    lvHY_Monitor.setServiceID("CRM");
                    lvHY_Monitor.setServiceName(service_name);
                    lvHY_Monitor.setStatus(status);
                    lvHY_Monitor.setConcurrencyQty(concurrency_qty);
                    lvHY_Monitor.setErrorMsg(error_msg);
                    
                    monitor.add(lvHY_Monitor);
                } else {
                    // 写数据库 BY JZMA 2019/5/30 注销
                    errorMessage.append("首页监控-会员服务>>>返回错误信息:" + serviceStatus + "," + serviceDescription);
                }
                
                // 支付服务接口地址计算
                // 会员服务:http://eliutong2.digiwin.com.cn/crmService/sposWeb/services/jaxrs/openapi/member
                // 移动支付:http://eliutong2.digiwin.com.cn/payService/sposWeb/services/jaxrs/openapi/member
                String sPayServiceURL = YCUrl.replace("crmService", "payService");
                resbody = HttpSend.MainInfoSendcom(str, sPayServiceURL);
                
                // logger.info("\r\n******首页监控-请求移动支付服务返回参数： "+ "\r\n" + resbody
                // + "******\r\n");
                jsonres = new JSONObject(resbody);
                serviceDescription = jsonres.get("serviceDescription").toString();
                serviceStatus = jsonres.get("serviceStatus").toString();
                level1Elm_Monitor lvPAY_Monitor = new DCP_MainInfoQueryRes().new level1Elm_Monitor();
                
                if (jsonres.get("success").toString().equals("true")) {
                    JSONArray std_data_res = jsonres.getJSONArray("datas");
                    String service_name = std_data_res.getJSONObject(0).getString("service_name");// 服务名称
                    String status = std_data_res.getJSONObject(0).getString("status");// 状态
                    String concurrency_qty = std_data_res.getJSONObject(0).getString("concurrency_qty");// 并发数
                    String error_msg = std_data_res.getJSONObject(0).getString("error_msg");// 错误描述
                    
                    if (!PosPub.isNumeric(concurrency_qty)) {
                        concurrency_qty = "0";
                    }
                    
                    // by jinzma 20190614 会员支付返回的服务名如果为空，给默认值
                    if (Check.Null(service_name) || service_name.trim().equals("")) {
                        service_name = "支付服务";
                        if (langType.equals("zh_TW")){
                            service_name = "支付服務";
                        }
                    }
                    lvPAY_Monitor.setItem("2");
                    lvPAY_Monitor.setServiceID("PAY");
                    lvPAY_Monitor.setServiceName(service_name);
                    lvPAY_Monitor.setStatus(status);
                    lvPAY_Monitor.setConcurrencyQty(concurrency_qty);
                    lvPAY_Monitor.setErrorMsg(error_msg);
                    
                    monitor.add(lvPAY_Monitor);
                } else {
                    // 写数据库 BY JZMA 2019/5/30 注销
                    errorMessage.append("首页监控-支付服务>>>返回错误信息:" + serviceStatus + "," + serviceDescription);
                }
            } catch (Exception e) {
                StringWriter errors = new StringWriter();
                PrintWriter pw = new PrintWriter(errors);
                e.printStackTrace(pw);
                
                pw.flush();
                pw.close();
                
                errors.flush();
                errors.close();
                
                isFail = true;
                errorMessage.append("首页监控-会员和支付服务：" + e.getMessage());
                
            }
            return isFail;
        }
    }
    private class thread_Monitor_Report implements Callable {
        String langType;
        String monitorReportUrl;
        List<level1Elm_Monitor> monitor;
        StringBuilder errorMessage;
        
        thread_Monitor_Report(String langType, String monitorReportUrl, List<level1Elm_Monitor> monitor,
                              StringBuilder errorMessage) {
            this.langType = langType;
            this.monitorReportUrl = monitorReportUrl;
            this.monitor = monitor;
            this.errorMessage = errorMessage;
        }
        
        public Object call() {
            boolean isFail = false;
            level1Elm_Monitor lvReport_Monitor = new DCP_MainInfoQueryRes().new level1Elm_Monitor();
            String responseStr = "";
            try {
                lvReport_Monitor.setItem("5");
                String lvReport_ServiceName = "报表服务";
                if (langType.equals("zh_TW")){
                    lvReport_ServiceName = "報表服務";
                }
                lvReport_Monitor.setServiceID("REPORT");
                lvReport_Monitor.setServiceName(lvReport_ServiceName);
                lvReport_Monitor.setConcurrencyQty(" ");
                
                // 检测
                responseStr = HttpSend.SendNCRRestfulStatus(monitorReportUrl, "报表服务");
            } catch (Exception ignored) {
            
            } finally {
                if (responseStr.equals("")) {
                    lvReport_Monitor.setStatus("1");// 0.成功 1.失败
                    lvReport_Monitor.setErrorMsg("请检查中台参数:监控-报表服务地址设置是否正确");
                    
                } else {
                    lvReport_Monitor.setStatus("0");
                    lvReport_Monitor.setErrorMsg("");
                }
                monitor.add(lvReport_Monitor);
                
            }
            return isFail;
        }
    }
    private class thread_Monitor_SMS implements Callable {
        String langType;
        String monitorSmsUrl;
        List<level1Elm_Monitor> monitor;
        StringBuilder errorMessage;
        
        thread_Monitor_SMS(String langType, String monitorSmsUrl, List<level1Elm_Monitor> monitor,
                           StringBuilder errorMessage) {
            this.langType = langType;
            this.monitorSmsUrl = monitorSmsUrl;
            this.monitor = monitor;
            this.errorMessage = errorMessage;
        }
        
        public Object call() throws Exception {
            boolean isFail = false;
            String err;
            try {
                JSONObject smsReq = new JSONObject();
                smsReq.put("serviceId", "DCP_MainErrorInfoQuery"); // 正确应该是main.monitor.get
                // 供应商特殊沿用之前错误的名
                smsReq.put("lang_type", langType);
                
                String str = smsReq.toString();
                String resbody = HttpSend.MainInfoSendcom(str, monitorSmsUrl);
                
                logger.info("\r\n******首页监控-供应商管理请求返回参数：  " + "\r\n" + resbody + "******\r\n");
                
                JSONObject jsonres = new JSONObject(resbody);
                
                String serviceDescription = jsonres.get("serviceDescription").toString();
                String serviceStatus = jsonres.get("serviceStatus").toString();
                
                if (jsonres.get("success").toString().equals("true")) {
                    JSONArray std_data_res = jsonres.getJSONArray("datas");
                    
                    for (int i = 0; i < std_data_res.length(); i++) {
                        level1Elm_Monitor lvSms_Monitor = new DCP_MainInfoQueryRes().new level1Elm_Monitor();
                        
                        String service_name = std_data_res.getJSONObject(i).getString("service_name");// 服务名称
                        String status = std_data_res.getJSONObject(i).getString("status");// 状态
                        String concurrency_qty = std_data_res.getJSONObject(i).getString("concurrency_qty");// 并发数
                        String error_msg = std_data_res.getJSONObject(i).getString("error_msg");// 错误描述
                        
                        if (langType.equals("zh_TW")){
                            service_name = "供應商服務";
                        }
                        
                        lvSms_Monitor.setItem("6");
                        lvSms_Monitor.setServiceID("SMS");
                        lvSms_Monitor.setServiceName(service_name);
                        lvSms_Monitor.setStatus(status);
                        lvSms_Monitor.setConcurrencyQty(concurrency_qty);
                        lvSms_Monitor.setErrorMsg(error_msg);
                        //
                        monitor.add(lvSms_Monitor);
                    }
                } else {
                    // 写数据库 BY JZMA 2019/5/30 注销
                    errorMessage.append("首页监控-供应商管理请求>>>返回错误信息:" + serviceStatus + "," + serviceDescription);
                }
            } catch (Exception e) {
                StringWriter errors = new StringWriter();
                PrintWriter pw = new PrintWriter(errors);
                e.printStackTrace(pw);
                
                pw.flush();
                pw.close();
                
                errors.flush();
                errors.close();
                
                //
                if (e.getMessage() == null) {
                    String[] sSplitStrings = errors.toString().split("\r\n");
                    err = sSplitStrings[0];
                } else {
                    err = e.getMessage();
                }
                errorMessage.append("首页监控-供应商管理：" + err);
                isFail = true;
                
            }
            return isFail;
        }
    }
    
}
