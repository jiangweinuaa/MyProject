package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockTaskBatchCreateReq;
import com.dsc.spos.json.cust.req.DCP_StockTaskStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_StockTaskBatchCreateRes;
import com.dsc.spos.json.cust.res.DCP_StockTaskStatusUpdateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class DCP_StockTaskBatchCreate extends SPosAdvanceService<DCP_StockTaskBatchCreateReq, DCP_StockTaskBatchCreateRes>
{
    @Override
    protected boolean isVerifyFail(DCP_StockTaskBatchCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();


        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected void processDUID(DCP_StockTaskBatchCreateReq req, DCP_StockTaskBatchCreateRes res) throws Exception {
        // TODO Auto-generated method stub
        String eId = req.geteId();
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();

        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
        String bDate = df.format(cal.getTime());
        String bTime =	new SimpleDateFormat("HHmmss").format(cal.getTime());


        if(Check.Null(req.getRequest().getBeginDate())){
            req.getRequest().setBeginDate(bDate);
        }
        if(Check.Null(req.getRequest().getEndDate())){
            req.getRequest().setEndDate(bDate);
        }
        String warehouseType = req.getRequest().getWarehouseType();
        String confirmType = req.getRequest().getConfirmType();



        String beginDateStr = req.getRequest().getBeginDate();
        String endDateStr = req.getRequest().getEndDate();

        Date beginDate = new SimpleDateFormat("yyyyMMdd").parse(beginDateStr);
        Date endDate = new SimpleDateFormat("yyyyMMdd").parse(endDateStr);

        List<String> taskNos=new ArrayList<>();
        while (beginDate.compareTo(endDate) <= 0) {
            StringBuffer sqlbuf = new StringBuffer();
            sqlbuf.append(" select ptemplate.* from (");
            sqlbuf.append(" "
                + " select distinct a.ptemplateno,a.shoptype,a.IS_BTAKE,a.TASKWAY,a.IS_ADJUST_STOCK,a.RANGEWAY " +
                    " from DCP_ptemplate a"
                + " where a.status='100' and (a.hqporder='N' or a.hqporder is null) and a.eid= '"+ req.geteId() +"' "
                + " and (a.shoptype is null or a.shoptype='2')"
                + " and a.DOC_TYPE='1'  ");
            //sqlbuf.append(" and a.ptemplateno='PDMB2025021800001' ");

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
                    String nowBDate = df.format(beginDate);

                    if(Check.Null(pTemplateNo)){
                        continue;
                    }

                    String sql = "select * from DCP_STOCKTASK where bdate='"+nowBDate+"' and ptemplateno='"+pTemplateNo+"' and status<>'8'";
                    List<Map<String, Object>> taskList = this.doQueryData(sql, null);
                    if(CollUtil.isEmpty(taskList)){

                        //查询明细
                        String tempDetailSql="select a.pluno,b.category,a.punit,b.baseunit,b.price,nvl(c.featureno,' ') as featureno " +
                                " from DCP_PTEMPLATE_DETAIL a" +
                                " left join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno" +
                                " left join dcp_goods_feature c on c.eid=b.eid and c.pluno=b.pluno and c.status='100'" +
                                " where a.eid='"+eId+"' and a.ptemplateno='"+pTemplateNo+"' and a.doc_type='1' and a.status='100' and b.status='100' ";

                        if("1".equals(rangeWay)){
                            //按品类
                             tempDetailSql="select b.pluno,b.category,b.cunit as  punit,b.baseunit,b.price,nvl(c.featureno,' ') as featureno  " +
                                    " from DCP_PTEMPLATE_DETAIL a" +
                                    " left join dcp_goods b on a.eid=b.eid and a.pluno=b.CATEGORY" +
                                     " left join dcp_goods_feature c on c.eid=b.eid and c.pluno=b.pluno and c.status='100'" +
                                    " where a.eid='"+eId+"' and a.ptemplateno='"+pTemplateNo+"' and a.doc_type='1' and a.status='100' and b.status='100' ";

                        }

                        List<Map<String, Object>> detailList = this.doQueryData(tempDetailSql, null);

                        if(CollUtil.isEmpty(detailList)){
                            continue;
                        }


                        String billNo = this.getOrderNO(req, "PDRW");
                        if(!taskNos.contains(billNo)){
                            taskNos.add(billNo);
                        }
                        BigDecimal totCQty=new BigDecimal(0);
                        BigDecimal totOrg=new BigDecimal(0);
                        int detailItem=0;
                        for (Map<String, Object> detailMap : detailList){
                            String pluNo = detailMap.get("PLUNO").toString();
                            String category = detailMap.get("CATEGORY").toString();
                            String punit = detailMap.get("PUNIT").toString();
                            String baseUnit = detailMap.get("BASEUNIT").toString();
                            String price = detailMap.get("PRICE").toString();
                            String featureNo = detailMap.get("FEATURENO").toString();

                            Map<String, Object> baseMap = PosPub.getBaseQty(dao, eId, pluNo, punit, "1");
                            String unitRatio = baseMap.get("unitRatio").toString();

                            detailItem++;
                            ColumnDataValue detailColumns=new ColumnDataValue();
                            detailColumns.add("EID",eId, Types.VARCHAR);
                            detailColumns.add("STOCKTASKNO",billNo,Types.VARCHAR);
                            detailColumns.add("ITEM",detailItem, Types.VARCHAR);
                            detailColumns.add("PLUNO",pluNo, Types.VARCHAR);
                            detailColumns.add("FEATURENO",featureNo, Types.VARCHAR);
                            detailColumns.add("CATEGORY",category, Types.VARCHAR);
                            detailColumns.add("PUNIT",punit, Types.VARCHAR);
                            detailColumns.add("BASEUNIT",baseUnit, Types.VARCHAR);
                            detailColumns.add("UNIT_RATIO",unitRatio, Types.VARCHAR);
                            detailColumns.add("SDPRICE",price, Types.VARCHAR);

                            String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                            DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                            InsBean ib1=new InsBean("DCP_STOCKTASK_DETAIL",detailColumnNames);
                            ib1.addValues(detailDataValues);
                            this.addProcessData(new DataProcessBean(ib1));

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
                                String orgNo = orgMap.get("ORGANIZATIONNO").toString();
                                String warehouse = orgMap.get("WAREHOUSE").toString();

                                orgItem++;
                                ColumnDataValue orgColumns=new ColumnDataValue();
                                orgColumns.add("EID",eId, Types.VARCHAR);
                                orgColumns.add("STOCKTASKNO",billNo,Types.VARCHAR);
                                orgColumns.add("ITEM",orgItem+"",Types.VARCHAR);

                                orgColumns.add("ORGANIZATIONNO",orgNo,Types.VARCHAR);
                                orgColumns.add("WAREHOUSE",warehouse,Types.VARCHAR);
                                orgColumns.add("SUBTASKNO",billNo+"_"+orgItem,Types.VARCHAR);


                                String[] orgColumnNames = orgColumns.getColumns().toArray(new String[0]);
                                DataValue[] orgDataValues = orgColumns.getDataValues().toArray(new DataValue[0]);
                                InsBean ib1=new InsBean("DCP_STOCKTASK_ORG",orgColumnNames);
                                ib1.addValues(orgDataValues);
                                this.addProcessData(new DataProcessBean(ib1));

                            }
                        totOrg=new BigDecimal(orgList.size());

                        ColumnDataValue mainColumns=new ColumnDataValue();
                        mainColumns.add("SHOPID",shopId,Types.VARCHAR);
                        mainColumns.add("EID",eId,Types.VARCHAR);
                        mainColumns.add("ORGANIZATIONNO",req.getOrganizationNO(),Types.VARCHAR);
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
                        mainColumns.add("EMPLOYEEID",employeeNo,Types.VARCHAR);
                        mainColumns.add("DEPARTID",departmentNo,Types.VARCHAR);

                        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
                        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean ib1=new InsBean("DCP_STOCKTASK",mainColumnNames);
                        ib1.addValues(mainDataValues);
                        this.addProcessData(new DataProcessBean(ib1));

                    }
                }
            }


            beginDate=addOneDay(beginDate);
        }

        this.doExecuteDataToDB();

        if("1".equals(confirmType)){
            for (String no : taskNos){
                ParseJson pj = new ParseJson();
                DCP_StockTaskStatusUpdateReq updateReq=new DCP_StockTaskStatusUpdateReq();
                updateReq.setServiceId("DCP_StockTaskStatusUpdate");
                updateReq.setToken(req.getToken());
                DCP_StockTaskStatusUpdateReq.LevelElm request = updateReq.new LevelElm();
                request.setOprType("confirm");
                request.setStockTaskNo(no);
                updateReq.setRequest(request);

                String jsontemp= pj.beanToJson(updateReq);

                //直接调用CRegisterDCP服务
                DispatchService ds = DispatchService.getInstance();
                String resXml = ds.callService(jsontemp, StaticInfo.dao);
                DCP_StockTaskStatusUpdateRes resserver=pj.jsonToBean(resXml, new TypeToken<DCP_StockTaskStatusUpdateRes>(){});
                if(resserver.isSuccess()==false)
                {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, no+"任务单审核失败！");
                }
            }
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockTaskBatchCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockTaskBatchCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockTaskBatchCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected TypeToken<DCP_StockTaskBatchCreateReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_StockTaskBatchCreateReq>(){};
    }

    @Override
    protected DCP_StockTaskBatchCreateRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_StockTaskBatchCreateRes();
    }

    private String getTemplateSql(DCP_StockTaskBatchCreateReq req) throws Exception{
        StringBuffer sqlbuf = new StringBuffer();
        String beginDateStr = req.getRequest().getBeginDate();
        String endDateStr = req.getRequest().getEndDate();

        Date beginDate = new SimpleDateFormat("yyyyMMdd").parse(beginDateStr);
        Date endDate = new SimpleDateFormat("yyyyMMdd").parse(endDateStr);


        sqlbuf.append(" select ptemplate.* from (");
        sqlbuf.append(" "
                + " select distinct a.ptemplateno from DCP_ptemplate a"
                + " where a.status='100' and (a.hqporder='N' or a.hqporder is null) and a.eid= '"+ req.geteId() +"' "
                + " and (a.shoptype is null or a.shoptype='2')"
                + " ");

        sqlbuf.append(" and ((a.time_type='0' ) ");
        while (beginDate.compareTo(endDate) <= 0) {
            String weekOfDay = this.getWeekDay(beginDate);
            String day = this.getDay(beginDate);
            String doubleDay = "1";    //单日
            if(Integer.parseInt(day) % 2==0)
                doubleDay = "2";//双日

            sqlbuf.append(" or ("
                    + "  (a.time_type='1' and a.time_value like '%"+doubleDay+"%')"
                    + " or (a.time_type='2' and a.time_value like '%"+weekOfDay+"%')"
                    + " or (a.time_type='3' and ';'||a.time_value||';' like '%%;"+String.valueOf(Integer.valueOf(day))+";%%')"
                    + " or (a.time_type='3' and a.time_value like '%%"+day+"%%')" +

                    ")");

            beginDate=addOneDay(beginDate);
        }
        sqlbuf.append(" ) ");


        sqlbuf.append(" ) ptemplate");



        return sqlbuf.toString();
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



}

