package com.dsc.spos.scheduler.job;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_AutoStockTaskCreate extends InitJob
{


    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pShop="";
    String pOrganizationNO="";
    String pBillNo="";

    Logger logger = LogManager.getLogger(DCP_AutoStockTaskCreate.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public DCP_AutoStockTaskCreate()
    {

    }

    public DCP_AutoStockTaskCreate(String eId, String shopId, String organizationNO, String billNo)
    {
        pEId=eId;
        pShop=shopId;
        pOrganizationNO=organizationNO;
        pBillNo=billNo;
    }


    public String doExe()
    {
        //返回信息
        String sReturnInfo = "";
        //此服务是否正在执行中
        if (bRun && pEId.equals(""))
        {
            logger.debug("\r\n*********盘点任务自动生成DCP_AutoStockTaskCreate正在执行中,本次调用取消:************\r\n");

            sReturnInfo = "定时生成任务-盘点任务自动生成DCP_AutoStockTaskCreate正在执行中！";
            return sReturnInfo;
        }

        bRun = true;//

        logger.debug("\r\n*********盘点任务自动生成DCP_AutoStockTaskCreate定时调用Start:************\r\n");

        try
        {



            Calendar cal = Calendar.getInstance();//获得当前时间
            SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
            String bDate = df.format(cal.getTime());
            String bTime =	new SimpleDateFormat("HHmmss").format(cal.getTime());

            String warehouseType = "1,2";
            String confirmType ="1";

            Date beginDate = new Date();
            Date endDate = new Date();

            List<String> taskNos=new ArrayList<>();
            List<DataProcessBean> dataProcess = new ArrayList<DataProcessBean>();
            List<Map<String,String>> taskIns=new ArrayList<>();
            while (beginDate.compareTo(endDate) <= 0) {
                StringBuffer sqlbuf = new StringBuffer();
                sqlbuf.append(" select ptemplate.* from (");
                sqlbuf.append(" "
                        + " select distinct a.eid,a.ptemplateno,a.shoptype,a.IS_BTAKE,a.TASKWAY,a.RANGEWAY,a.IS_ADJUST_STOCK from DCP_ptemplate a"
                        + " where a.status='100' and (a.hqporder='N' or a.hqporder is null)  "
                        + " and (a.shoptype is null or a.shoptype='2')"
                        + " ");

                sqlbuf.append(" and ( ");

                String weekOfDay = this.getWeekDay(beginDate);
                String day = this.getDay(beginDate);
                String doubleDay = "1";    //单日
                if(Integer.parseInt(day) % 2==0)
                    doubleDay = "2";//双日

                sqlbuf.append("  ("
                        + "  (a.time_type='1' and a.time_value like '%"+doubleDay+"%')"
                        + " or (a.time_type='2' and a.time_value like '%"+weekOfDay+"%')"
                        + " or (a.time_type='3' and ';'||a.time_value||';' like '%%;"+String.valueOf(Integer.valueOf(day))+";%%')"
                        + " or (a.time_type='3' and a.time_value like '%%"+day+"%%')" +

                        ")");
                sqlbuf.append(" ) ");


                sqlbuf.append(" ) ptemplate");

                List<Map<String, Object>> templateList = this.doQueryData(sqlbuf.toString(), null);

                if(CollUtil.isNotEmpty(templateList)){

                    for(Map<String, Object> templateMap : templateList){
                        //盘点日期+模板编号已存在盘点任务单且状态<>"已作废"，跳过无需生成盘点任务！
                        String pTemplateNo = templateMap.get("PTEMPLATENO").toString();
                        String shopType = templateMap.get("SHOPTYPE").toString();
                        String isBtake = templateMap.get("IS_BTAKE").toString();
                        String taskWay = templateMap.get("TASKWAY").toString();
                        String rangeWay = templateMap.get("RANGEWAY").toString();
                        String is_adjust_stock = templateMap.get("IS_ADJUST_STOCK").toString();
                        String eId = templateMap.get("EID").toString();
                        String nowBDate = df.format(beginDate);

                        String employeeNo = PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "JobOpId");
                        String orgNo = PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "JobOpOrgId");
                        String isStockMultipleUnit = PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "IsStockMultipleUnit");
                        if(Check.Null(pTemplateNo)){
                            continue;
                        }
                        String sql = "select * from DCP_STOCKTASK where eid='"+eId+"' and bdate='"+nowBDate+"' and ptemplateno='"+pTemplateNo+"' and status<>'8'";
                        List<Map<String, Object>> taskList = this.doQueryData(sql, null);
                        if(CollUtil.isEmpty(taskList)){

                            //查询明细
                            String tempDetailSql="select a.pluno,b.category,a.punit,b.baseunit,b.price from DCP_PTEMPLATE_DETAIL a" +
                                    " left join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno" +
                                    " where a.eid='"+eId+"' and a.ptemplateno='"+pTemplateNo+"' and a.doc_type='1' ";
                            if("1".equals(rangeWay)){
                                //按品类
                                tempDetailSql="select b.pluno,b.category,a.punit,b.baseunit,b.price " +
                                        " from DCP_PTEMPLATE_DETAIL a" +
                                        " left join dcp_goods b on a.eid=b.eid and a.pluno=b.CATEGORY" +
                                        " where a.eid='"+eId+"' and a.ptemplateno='"+pTemplateNo+"' and a.doc_type='1' and a.status='100' and b.status='100' ";

                            }
                            List<Map<String, Object>> detailList = this.doQueryData(tempDetailSql, null);


                            String billNo = this.getOrderNO(eId,orgNo, "PDRW");
                            if(!taskNos.contains(billNo)){
                                taskNos.add(billNo);
                            }

                            Map singleInfo=new HashMap();
                            singleInfo.put("TASKNO",billNo);
                            singleInfo.put("EID",eId);
                            singleInfo.put("ORGNO",orgNo);
                            taskIns.add(singleInfo);

                            BigDecimal totCQty=new BigDecimal(0);
                            BigDecimal totOrg=new BigDecimal(0);
                            int detailItem=0;
                            for (Map<String, Object> detailMap : detailList){
                                String pluNo = detailMap.get("PLUNO").toString();
                                String category = detailMap.get("CATEGORY").toString();
                                String punit = detailMap.get("PUNIT").toString();
                                String baseUnit = detailMap.get("BASEUNIT").toString();
                                String price = detailMap.get("PRICE").toString();

                                Map<String, Object> baseMap = PosPub.getBaseQty(StaticInfo.dao, eId, pluNo, punit, "1");
                                String unitRatio = baseMap.get("unitRatio").toString();

                                detailItem++;
                                ColumnDataValue detailColumns=new ColumnDataValue();
                                detailColumns.add("EID",eId, Types.VARCHAR);
                                detailColumns.add("STOCKTASKNO",billNo,Types.VARCHAR);
                                detailColumns.add("ITEM",detailItem, Types.VARCHAR);
                                detailColumns.add("PLUNO",pluNo, Types.VARCHAR);
                                detailColumns.add("FEATURENO"," ", Types.VARCHAR);
                                detailColumns.add("CATEGORY",category, Types.VARCHAR);
                                detailColumns.add("PUNIT",punit, Types.VARCHAR);
                                detailColumns.add("BASEUNIT",baseUnit, Types.VARCHAR);
                                detailColumns.add("UNIT_RATIO",unitRatio, Types.VARCHAR);
                                detailColumns.add("SDPRICE",price, Types.VARCHAR);

                                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                                InsBean ib1=new InsBean("DCP_STOCKTASK_DETAIL",detailColumnNames);
                                ib1.addValues(detailDataValues);
                                dataProcess.add(new DataProcessBean(ib1));

                            }
                            totCQty=new BigDecimal(detailList.size());
                            //适用门店类型：1-全部门店2-指定门店
                            String extraSql=" and 1=1 ";
                            if(Check.Null(warehouseType)){
                                String[] split = warehouseType.split(",");
                                if(split.length>0){
                                    String typeStr="";
                                    for (String s : split) {
                                        typeStr+="'"+s+"',";
                                    }
                                    typeStr=typeStr.substring(0,typeStr.length()-1);
                                    extraSql=" and b.warehouse_type in ("+typeStr+")";
                                }
                            }



                            StringBuffer orgSqlsb=new StringBuffer("select a.organizationno,b.warehouse  " +
                                    " from dcp_org a " +
                                    " left join dcp_warehouse b on a.eid=b.eid and a.organizationno=b.organizationno and b.status='100' "
                            );

                            orgSqlsb.append( " where a.status='100'  and a.eid='"+eId+"' ");
                            orgSqlsb.append(extraSql);
                            if(shopType.equals("2")){
                                orgSqlsb=new StringBuffer("select ps.ORGANIZATIONNO , b.warehouse " +
                                        " from DCP_PTEMPLATE_SHOP ps" +
                                        " left join dcp_warehouse b on ps.eid = b.eid and ps.organizationno = b.organizationno and b.STATUS=100" +
                                        " where ps.eid = '99' and ps.ptemplateno = '"+pTemplateNo+"' and ps.warehouse =' '" +
                                        " " +extraSql +
                                        " union" +
                                        " select ps.ORGANIZATIONNO ,ps.warehouse" +
                                        " from DCP_PTEMPLATE_SHOP ps" +
                                        " left join dcp_warehouse b on ps.eid = b.eid and ps.organizationno = b.organizationno and ps.warehouse=b.warehouse and b.STATUS=100\n" +
                                        " where ps.eid = '99' and ps.ptemplateno = '"+pTemplateNo+"' and ps.warehouse <>' '" +
                                        " "+extraSql);
                            }

                            List<Map<String, Object>> orgList = this.doQueryData(orgSqlsb.toString(), null);
                            int orgItem=0;
                            for (Map<String, Object> orgMap : orgList){
                                String orgNoD = orgMap.get("ORGANIZATIONNO").toString();
                                String warehouse = orgMap.get("WAREHOUSE").toString();

                                orgItem++;
                                ColumnDataValue orgColumns=new ColumnDataValue();
                                orgColumns.add("EID",eId, Types.VARCHAR);
                                orgColumns.add("STOCKTASKNO",billNo,Types.VARCHAR);
                                orgColumns.add("ITEM",orgItem+"",Types.VARCHAR);

                                orgColumns.add("ORGANIZATIONNO",orgNoD,Types.VARCHAR);
                                orgColumns.add("WAREHOUSE",warehouse,Types.VARCHAR);
                                orgColumns.add("SUBTASKNO",billNo+"_"+orgItem,Types.VARCHAR);


                                String[] orgColumnNames = orgColumns.getColumns().toArray(new String[0]);
                                DataValue[] orgDataValues = orgColumns.getDataValues().toArray(new DataValue[0]);
                                InsBean ib1=new InsBean("DCP_STOCKTASK_ORG",orgColumnNames);
                                ib1.addValues(orgDataValues);
                                dataProcess.add(new DataProcessBean(ib1));

                            }
                            totOrg=new BigDecimal(orgList.size());

                            ColumnDataValue mainColumns=new ColumnDataValue();
                            mainColumns.add("SHOPID",orgNo,Types.VARCHAR);
                            mainColumns.add("EID",eId,Types.VARCHAR);
                            mainColumns.add("ORGANIZATIONNO",orgNo,Types.VARCHAR);
                            mainColumns.add("STOCKTASKNO",billNo,Types.VARCHAR);
                            mainColumns.add("BDATE",nowBDate,Types.VARCHAR);
                            mainColumns.add("MEMO","",Types.VARCHAR);
                            //mainColumns.add("WAREHOUSE",request.getware,Types.VARCHAR);
                            mainColumns.add("IS_BTAKE",isBtake,Types.VARCHAR);
                            mainColumns.add("STATUS","0",Types.VARCHAR);
                            mainColumns.add("DOC_TYPE","2",Types.VARCHAR);
                            mainColumns.add("TASKWAY",taskWay,Types.VARCHAR);
                            mainColumns.add("NOTGOODSMODE","2",Types.VARCHAR);
                            mainColumns.add("IS_ADJUST_STOCK",is_adjust_stock,Types.VARCHAR);
                            mainColumns.add("CREATEBY",employeeNo,Types.VARCHAR);
                            mainColumns.add("CREATE_DATE",bDate,Types.VARCHAR);
                            mainColumns.add("CREATE_TIME",bTime,Types.VARCHAR);
                            mainColumns.add("PARTITION_DATE",nowBDate,Types.VARCHAR);
                            mainColumns.add("STOCKTASKID", PosPub.getGUID(false),Types.VARCHAR);
                            mainColumns.add("CREATETYPE","2",Types.VARCHAR);
                            mainColumns.add("SDATE",nowBDate,Types.VARCHAR);
                            mainColumns.add("TOTSUBTASKQTY",totOrg,Types.VARCHAR);
                            mainColumns.add("TOTCQTY",totCQty,Types.VARCHAR);
                            mainColumns.add("PTEMPLATENO",pTemplateNo,Types.VARCHAR);
                            mainColumns.add("WAREHOUSETYPE",warehouseType,Types.VARCHAR);
                            mainColumns.add("EMPLOYEEID","",Types.VARCHAR);
                            mainColumns.add("DEPARTID","",Types.VARCHAR);

                            String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
                            DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
                            InsBean ib1=new InsBean("DCP_STOCKTASK",mainColumnNames);
                            ib1.addValues(mainDataValues);
                            dataProcess.add(new DataProcessBean(ib1));


                            StaticInfo.dao.useTransactionProcessData(dataProcess);


                        }
                    }
                }

                beginDate=addOneDay(beginDate);
            }

            StaticInfo.dao.useTransactionProcessData(dataProcess);
            List<DataProcessBean> dataProcess1 = new ArrayList<DataProcessBean>();
            if(taskIns.size()>0){
                for (Map<String,String> info : taskIns){
                    String stockTaskNo = info.get("TASKNO").toString();
                    String eId = info.get("EID").toString();
                    String taskOrgNo = info.get("ORGNO").toString();

                    String employeeNo = PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "JobOpId");
                    //String orgNo = PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "JobOpOrgId");
                    String isStockMultipleUnit = PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "IsStockMultipleUnit");


                    String taskOrgSql="select * from dcp_org where organizationno='"+taskOrgNo+"' and eid='"+eId+"'";
                    List<Map<String, Object>> taskOrgList = this.doQueryData(taskOrgSql, null);
                    String belfirm="";
                    for (Map<String, Object> taskOrgMap : taskOrgList){
                        belfirm=taskOrgMap.get("BELFIRM").toString();
                    }

                    String sql = "select * from DCP_STOCKTASK a where a.eid='"+eId+"' " +
                            " and a.ORGANIZATIONNO='"+taskOrgNo+"' " +
                            " and a.STOCKTASKNO='"+stockTaskNo+"' ";
                    List<Map<String, Object>> list = this.doQueryData(sql, null);
                    if (CollUtil.isEmpty(list)){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E404, "任务单不存在");
                    }
                    Map<String, Object> singleData = list.get(0);
                    String status = singleData.get("STATUS").toString();
                    String bTaskDate = singleData.get("BDATE").toString();
                    String pTemplateNo = singleData.get("PTEMPLATENO").toString();
                    String shopId = singleData.get("SHOPID").toString();
                    String docType = singleData.get("DOC_TYPE").toString();
                    String memo = singleData.get("MEMO").toString();
                    String totCqty = singleData.get("TOTCQTY").toString();
                    String isBtake = singleData.get("IS_BTAKE").toString();
                    String taskWay = singleData.get("TASKWAY").toString();
                    String notGoodsMode = singleData.get("NOTGOODSMODE").toString();
                    String isAdjustStock = singleData.get("IS_ADJUST_STOCK").toString();

                    String validSql1="select a.organizationno,a.warehouse from DCP_STOCKTAKE a where a.bdate='"+bDate+"' and a.PTEMPLATENO='"+pTemplateNo+"'";
                    List<Map<String, Object>> list1 = this.doQueryData(validSql1, null);
                    String orgSql=" select * from DCP_STOCKTASK_ORG a where a.eid='"+eId+"' and a.stocktaskno='"+stockTaskNo+"' ";
                    List<Map<String, Object>> orgList = this.doQueryData(orgSql, null);

                    String detailSql="select * from DCP_STOCKTASK_DETAIL a where a.eid='"+eId+"' and a.stocktaskno='"+stockTaskNo+"'";
                    List<Map<String, Object>> detailList = this.doQueryData(detailSql, null);

                    String detailUnitSql="select b.ounit,b.qty,b.oqty from DCP_STOCKTASK_DETAIL a " +
                            " inner join DCP_GOODS_UNIT b on a.eid=b.eid and a.pluno=b.pluno where a.eid='"+eId+"'  and a.stocktaskno='"+stockTaskNo+"'";
                    List<Map<String, Object>> unitList = this.doQueryData(detailUnitSql, null);

                    if(CollUtil.isEmpty(orgList)){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "未找到盘点任务范围");
                    }
                    String priceLength = PosPub.getPARA_SMS(StaticInfo.dao, eId, taskOrgNo, "priceLength");
                    String distriPriceLength = PosPub.getPARA_SMS(StaticInfo.dao, eId, taskOrgNo, "distriPriceLength");
                    if (Check.Null(priceLength)||!PosPub.isNumeric(priceLength)) {
                        priceLength="2";
                    }
                    if (Check.Null(distriPriceLength)||!PosPub.isNumeric(distriPriceLength)) {
                        distriPriceLength="2";
                    }
                    int priceLengthInt = Integer.parseInt(priceLength);
                    int distriPriceLengthInt = Integer.parseInt(distriPriceLength);


                    //处理成品零售价和配送价
                    List<Map<String, Object>> plus = new ArrayList<>();
                    Map<String, Boolean> condition = new HashMap<>(); //查詢條件
                    condition.put("PLUNO", true);
                    List<Map<String, Object>> getQPlu=MapDistinct.getMap(detailList, condition);
                    for (Map<String, Object> onePlu :getQPlu ) {
                        Map<String, Object> plu = new HashMap<>();
                        plu.put("PLUNO", onePlu.get("PLUNO").toString());
                        plu.put("PUNIT", onePlu.get("PUNIT").toString());
                        plu.put("BASEUNIT", onePlu.get("BASEUNIT").toString());
                        plu.put("UNITRATIO", onePlu.get("UNIT_RATIO").toString());
                        plus.add(plu);
                    }

                    MyCommon mc = new MyCommon();
                    List<Map<String, Object>> getPluPrice = mc.getSalePrice_distriPrice(StaticInfo.dao,eId, belfirm, taskOrgNo,plus,belfirm);


                    for (Map<String, Object> map : orgList){
                        String orgNo = map.get("ORGANIZATIONNO").toString();
                        String warehouse = map.get("WAREHOUSE").toString();
                        String stockTakeNo = map.get("STOCKTAKENO").toString();
                        if(!Check.Null(stockTakeNo)){
                            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "已生成盘点单");
                        }

                        List<Map<String, Object>> collect = list1.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(orgNo) & x.get("WAREHOUSE").toString().equals(warehouse)).collect(Collectors.toList());
                        if(CollUtil.isNotEmpty(collect)){
                            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "已生成盘点单");
                        }

                    }

                    for (Map<String, Object> map : orgList){
                        String orgNo = map.get("ORGANIZATIONNO").toString();
                        String warehouse = map.get("WAREHOUSE").toString();
                        String subTaskNo = map.get("SUBTASKNO").toString();
                        String item = map.get("ITEM").toString();
                        //每一条subtaskNo生成一条盘点单
                        String stockTakeNo = this.getOrderNO(eId,orgNo, "KCPD");

                        String batchSql="select * from MES_BATCH_STOCK_DETAIL a where a.eid='"+eId+"' " +
                                " and a.organizationno='"+orgNo+"' and a.warehouse='"+warehouse+"' ";
                        List<Map<String, Object>> batchList = this.doQueryData(batchSql, null);


                        if(CollUtil.isNotEmpty(detailList)){
                            int detailItem=0;
                            for (Map<String, Object> detail : detailList){
                                String pluNo = detail.get("PLUNO").toString();
                                String baseUnit = detail.get("BASEUNIT").toString();
                                String oItem = detail.get("ITEM").toString();
                                String pUnit = detail.get("PUNIT").toString();
                                String unitRatio = detail.get("UNIT_RATIO").toString();
                                String featureNo = detail.get("FEATURENO").toString();
                                if(Check.Null(featureNo)){
                                    featureNo=" ";
                                }
                                List<Map<String, Object>> batchPlu = batchList.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo)).collect(Collectors.toList());
                                String batchNo=" ";
                                String prodDate="";
                                String refQty="0";
                                String location=" ";
                                if(CollUtil.isNotEmpty(batchPlu)){
                                    batchNo = batchPlu.get(0).get("BATCHNO").toString();
                                    prodDate = batchPlu.get(0).get("PRODDATE").toString();
                                    refQty=batchPlu.get(0).get("QTY").toString();
                                    location=batchPlu.get(0).get("LOCATION").toString();
                                }

                                String price="0";
                                String distriPrice="0";
                                Map<String, Object> condiV= new HashMap<>();
                                condiV.put("PLUNO",pluNo);
                                condiV.put("PUNIT",pUnit);
                                List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPluPrice, condiV, false);
                                if(priceList!=null && priceList.size()>0 ) {
                                    price=priceList.get(0).get("PRICE").toString();
                                    distriPrice=priceList.get(0).get("DISTRIPRICE").toString();
                                    BigDecimal price_b=new BigDecimal(price);
                                    price_b=price_b.setScale(priceLengthInt, RoundingMode.HALF_UP);
                                    price=price_b.toPlainString();
                                    BigDecimal distriPrice_b=new BigDecimal(distriPrice);
                                    distriPrice_b=distriPrice_b.setScale(distriPriceLengthInt, RoundingMode.HALF_UP);
                                    distriPrice=distriPrice_b.toPlainString();
                                }



                                detailItem++;
                                ColumnDataValue detailColumns=new ColumnDataValue();
                                detailColumns.add("STOCKTAKENO",stockTakeNo, Types.VARCHAR);
                                detailColumns.add("SHOPID",orgNo,Types.VARCHAR);

                                detailColumns.add("ITEM",detailItem,Types.VARCHAR);
                                detailColumns.add("EID",eId,Types.VARCHAR);
                                detailColumns.add("ORGANIZATIONNO",orgNo,Types.VARCHAR);
                                detailColumns.add("WAREHOUSE",warehouse,Types.VARCHAR);
                                detailColumns.add("PLUNO",pluNo,Types.VARCHAR);
                                detailColumns.add("BATCH_NO",batchNo,Types.VARCHAR);
                                detailColumns.add("PROD_DATE",prodDate,Types.VARCHAR);
                                detailColumns.add("BASEUNIT",baseUnit,Types.VARCHAR);
                                detailColumns.add("OITEM",oItem,Types.VARCHAR);
                                detailColumns.add("PQTY",0,Types.VARCHAR);
                                detailColumns.add("REF_BASEQTY",refQty,Types.VARCHAR);
                                detailColumns.add("PRICE",price,Types.VARCHAR);
                                detailColumns.add("DISTRIPRICE",distriPrice,Types.VARCHAR);
                                detailColumns.add("BASEQTY",0,Types.VARCHAR);
                                detailColumns.add("AMT",0,Types.VARCHAR);
                                detailColumns.add("DISTRIAMT",0,Types.VARCHAR);
                                detailColumns.add("PUNIT",pUnit,Types.VARCHAR);
                                detailColumns.add("UNIT_RATIO",unitRatio,Types.VARCHAR);
                                detailColumns.add("MEMO","",Types.VARCHAR);
                                detailColumns.add("BDATE",bDate,Types.VARCHAR);
                                detailColumns.add("FEATURENO",featureNo,Types.VARCHAR);
                                detailColumns.add("FQTY",0,Types.VARCHAR);
                                detailColumns.add("FBASEQTY",0,Types.VARCHAR);
                                detailColumns.add("RQTY",0,Types.VARCHAR);
                                detailColumns.add("RBASEQTY",0,Types.VARCHAR);
                                detailColumns.add("PARTITION_DATE",bDate,Types.VARCHAR);
                                detailColumns.add("LOCATION",location,Types.VARCHAR);

                                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                                InsBean ib1=new InsBean("DCP_STOCKTAKE_DETAIL",detailColumnNames);
                                ib1.addValues(detailDataValues);
                                dataProcess1.add(new DataProcessBean(ib1));

                                if("Y".equals(isStockMultipleUnit)&&CollUtil.isNotEmpty(unitList)){
                                    int unitItem=0;
                                    for (Map<String, Object> unit : unitList){

                                        String unitQty = unit.get("QTY").toString();
                                        String unitOqty = unit.get("OQTY").toString();
                                        String unitUnitRatio="0";
                                        if(!Check.Null(unitOqty)){
                                            unitUnitRatio=new BigDecimal(unitQty).divide(new BigDecimal(unitOqty),2, RoundingMode.HALF_UP).toString();
                                        }
                                        unitItem++;
                                        ColumnDataValue unitColumns=new ColumnDataValue();
                                        unitColumns.add("STOCKTAKENO",stockTakeNo, Types.VARCHAR);
                                        unitColumns.add("SHOPID",orgNo,Types.VARCHAR);
                                        unitColumns.add("ITEM",unitItem,Types.VARCHAR);
                                        unitColumns.add("EID",eId,Types.VARCHAR);
                                        unitColumns.add("ORGANIZATIONNO",orgNo,Types.VARCHAR);

                                        unitColumns.add("OITEM",detailItem,Types.VARCHAR);
                                        unitColumns.add("PQTY",0,Types.VARCHAR);
                                        unitColumns.add("PUNIT",unit.get("OUNIT").toString(),Types.VARCHAR);
                                        unitColumns.add("PARTITION_DATE",bDate,Types.VARCHAR);
                                        unitColumns.add("UNIT_RATIO",unitUnitRatio,Types.VARCHAR);

                                        String[] unitColumnNames = unitColumns.getColumns().toArray(new String[0]);
                                        DataValue[] unitDataValues = unitColumns.getDataValues().toArray(new DataValue[0]);
                                        InsBean ib2=new InsBean("DCP_STOCKTAKE_DETAIL_UNIT",unitColumnNames);
                                        ib2.addValues(unitDataValues);
                                        dataProcess1.add(new DataProcessBean(ib2));
                                    }
                                }

                            }
                        }

                        ColumnDataValue mainColumns=new ColumnDataValue();
                        mainColumns.add("SHOPID",orgNo,Types.VARCHAR);
                        mainColumns.add("STOCKTAKENO",stockTakeNo,Types.VARCHAR);
                        mainColumns.add("STOCKTAKE_ID",PosPub.getGUID(false),Types.VARCHAR);
                        mainColumns.add("EID",stockTakeNo,Types.VARCHAR);
                        mainColumns.add("ORGANIZATIONNO",orgNo,Types.VARCHAR);
                        mainColumns.add("CREATEBY",employeeNo,Types.VARCHAR);
                        mainColumns.add("CREATE_DATE",bDate,Types.VARCHAR);
                        mainColumns.add("CREATE_TIME",bTime,Types.VARCHAR);
                        mainColumns.add("OTYPE","1",Types.VARCHAR);
                        mainColumns.add("DOC_TYPE",docType,Types.VARCHAR);
                        mainColumns.add("MEMO",memo,Types.VARCHAR);
                        //mainColumns.add("LOAD_DOCTYPE",stockTakeNo,Types.VARCHAR);
                        //mainColumns.add("LOAD_DOCNO",stockTakeNo,Types.VARCHAR);
                        mainColumns.add("TOT_CQTY",totCqty,Types.VARCHAR);
                        mainColumns.add("BDATE",bDate,Types.VARCHAR);
                        mainColumns.add("IS_BTAKE",isBtake,Types.VARCHAR);
                        mainColumns.add("TOT_PQTY","0",Types.VARCHAR);
                        mainColumns.add("TOT_AMT","0",Types.VARCHAR);
                        mainColumns.add("TOT_DISTRIAMT","0",Types.VARCHAR);
                        mainColumns.add("OFNO",subTaskNo,Types.VARCHAR);
                        mainColumns.add("PTEMPLATENO",pTemplateNo,Types.VARCHAR);
                        mainColumns.add("WAREHOUSE",warehouse,Types.VARCHAR);

                        mainColumns.add("TASKWAY",taskWay,Types.VARCHAR);
                        mainColumns.add("NOTGOODSMODE",notGoodsMode,Types.VARCHAR);
                        mainColumns.add("IS_ADJUST_STOCK",isAdjustStock,Types.VARCHAR);
                        mainColumns.add("STATUS","0",Types.VARCHAR);
                        mainColumns.add("PARTITION_DATE",bDate,Types.VARCHAR);


                        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
                        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean ib1=new InsBean("DCP_STOCKTAKE",mainColumnNames);
                        ib1.addValues(mainDataValues);
                        dataProcess1.add(new DataProcessBean(ib1));


                        UptBean ub2 = new UptBean("DCP_STOCKTASK_ORG");
                        ub2.addUpdateValue("STOCKTAKENO", DataValues.newString(stockTakeNo));

                        ub2.addCondition("EID", DataValues.newString(eId));
                        ub2.addCondition("STOCKTASKNO",DataValues.newString(stockTaskNo));
                        ub2.addCondition("ITEM",DataValues.newString(item));
                        dataProcess1.add(new DataProcessBean(ub2));
                    }

                    UptBean ub2 = new UptBean("DCP_STOCKTASK");
                    ub2.addUpdateValue("STATUS", DataValues.newString("6"));
                    ub2.addUpdateValue("MODIFYBY", DataValues.newString(employeeNo));
                    ub2.addUpdateValue("MODIFY_DATE",DataValues.newString(bDate));
                    ub2.addUpdateValue("MODIFY_TIME",DataValues.newString(bTime));
                    ub2.addUpdateValue("CONFIRMBY", DataValues.newString(employeeNo));
                    ub2.addUpdateValue("CONFIRM_DATE", DataValues.newString(bDate));
                    ub2.addUpdateValue("CONFIRM_TIME", DataValues.newString(bTime));

                    ub2.addCondition("EID", DataValues.newString(eId));
                    ub2.addCondition("STOCKTASKNO",DataValues.newString(stockTaskNo));
                    dataProcess1.add(new DataProcessBean(ub2));


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

                logger.error("\r\n******盘点任务自动生成DCP_AutoStockTaskCreate报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\n******盘点任务自动生成DCP_AutoStockTaskCreate报错信息" + e.getMessage() + "******\r\n");
            }

            //
            sReturnInfo="错误信息:" + e.getMessage();

        }
        finally
        {
            bRun=false;//
            logger.debug("\r\n*********盘点任务自动生成DCP_AutoStockTaskCreate定时调用End:************\r\n");
        }
        return sReturnInfo;
    }

    protected String getWeekDay(Date date) throws Exception{
        String weekOfDay="";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String weekDay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        switch (weekDay) {
            case "1":
                weekOfDay="周日";
                break;
            case "2":
                weekOfDay="周一";
                break;
            case "3":
                weekOfDay="周二";
                break;
            case "4":
                weekOfDay="周三";
                break;
            case "5":
                weekOfDay="周四";
                break;
            case "6":
                weekOfDay="周五";
                break;
            case "7":
                weekOfDay="周六";
                break;
            default:
                break;
        }
        return weekOfDay;

    }
    public  Date addOneDay(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Date.from(localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    protected String getDay(Date date) throws Exception{
        String day="";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        return day;

    }

    private String getOrderNO(String eId,String shopId, String preFix) throws Exception  {
        String billCode="";
        String sql = null;
        String templateNo = null;

        StringBuffer sqlbufOrg = new StringBuffer("select billcode FROM dcp_org where eid='"+eId+"' and organizationno='"+shopId+"' ");
        sql = sqlbufOrg.toString();
        List<Map<String, Object>> getQDataOrg = this.doQueryData(sql, null);
        if(getQDataOrg!=null&&getQDataOrg.isEmpty()==false){
            billCode = (String) getQDataOrg.get(0).get("BILLCODE");
        }

        StringBuffer sqlbuf = new StringBuffer("select F_DCP_GETBILLNO('"+eId+"','"+shopId+"','"+billCode+"-"+preFix+"') TEMPLATENO FROM dual");
        sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && getQData.isEmpty() == false)
        {
            templateNo = (String) getQData.get(0).get("TEMPLATENO");
        }
        else
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "取加工任务单号失败！");
        }
        return templateNo;
    }





}
