package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockTaskNewCreateReq;
import com.dsc.spos.json.cust.res.DCP_StockTaskNewCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

public class DCP_StockTaskNewCreate extends SPosAdvanceService<DCP_StockTaskNewCreateReq, DCP_StockTaskNewCreateRes> {
    //private Logger loger = LogManager.getLogger(DCP_StockTaskNewCreate.class.getName());

    @Override
    protected boolean isVerifyFail(DCP_StockTaskNewCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_StockTaskNewCreateReq> getRequestType() {
        return new TypeToken<DCP_StockTaskNewCreateReq>(){};
    }

    @Override
    protected DCP_StockTaskNewCreateRes getResponseType() {
        return new DCP_StockTaskNewCreateRes();
    }

    @Override
    protected void processDUID(DCP_StockTaskNewCreateReq req,DCP_StockTaskNewCreateRes res) throws Exception {

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String shopId = req.getShopId();
        String employeeNo = req.getEmployeeNo();
        DCP_StockTaskNewCreateReq.LevelElm request = req.getRequest();

        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String bDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());
        String bTime =	new SimpleDateFormat("HHmmss").format(new Date());


        String billNo = this.getOrderNO(req, "PDRW");

        List<DCP_StockTaskNewCreateReq.Detail> detail = request.getDetail();
        List<DCP_StockTaskNewCreateReq.OrgList> orgList = request.getOrgList();

        if(CollUtil.isNotEmpty(detail)){
            String pluNoStr="'********',";
            for(DCP_StockTaskNewCreateReq.Detail d : detail){
                if(!Check.Null(d.getPluNo())){
                    pluNoStr+="'"+d.getPluNo()+"',";
                }
            }
            pluNoStr=pluNoStr.substring(0,pluNoStr.length()-1);
            String pluSql="select * from dcp_goods where eid='"+eId+"' and pluNo in ("+pluNoStr+") ";
            List<Map<String, Object>> list = this.doQueryData(pluSql, null);
            Map<String,Map> pluMap=new HashMap();
            for(Map m : list){
                pluMap.put(m.get("PLUNO").toString(),m);
            }

            for(DCP_StockTaskNewCreateReq.Detail d : detail){
                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID",eId, Types.VARCHAR);
                detailColumns.add("STOCKTASKNO",billNo,Types.VARCHAR);
                detailColumns.add("ITEM",d.getItem(), Types.VARCHAR);
                detailColumns.add("PLUNO",d.getPluNo(), Types.VARCHAR);
                detailColumns.add("FEATURENO",d.getFeatureNo(), Types.VARCHAR);

                if(Check.Null(d.getCategory())){
                    if(pluMap.containsKey(d.getPluNo())){
                        Map o = pluMap.get(d.getPluNo());
                        if(o.containsKey("CATEGORY")){
                            detailColumns.add("CATEGORY",o.get("CATEGORY"), Types.VARCHAR);
                        }
                    }
                }else{
                    detailColumns.add("CATEGORY",d.getCategory(), Types.VARCHAR);
                }

                detailColumns.add("PUNIT",d.getPUnit(), Types.VARCHAR);
                detailColumns.add("BASEUNIT",d.getBaseUnit(), Types.VARCHAR);
                detailColumns.add("UNIT_RATIO",d.getUnitRatio(), Types.VARCHAR);
                if(Check.Null(d.getSdPrice())){
                    if(pluMap.containsKey(d.getPluNo())){
                        Map o = pluMap.get(d.getPluNo());
                        if(o.containsKey("PRICE")){
                            detailColumns.add("SDPRICE",o.get("PRICE"), Types.VARCHAR);
                        }
                    }
                }else{
                    detailColumns.add("SDPRICE",d.getSdPrice(), Types.VARCHAR);

                }
                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib1=new InsBean("DCP_STOCKTASK_DETAIL",detailColumnNames);
                ib1.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(ib1));

            }

        }

        if(CollUtil.isNotEmpty(orgList)){
            int item=0;
            for (DCP_StockTaskNewCreateReq.OrgList o : orgList){
                item++;
                ColumnDataValue orgColumns=new ColumnDataValue();
                orgColumns.add("EID",eId, Types.VARCHAR);
                orgColumns.add("STOCKTASKNO",billNo,Types.VARCHAR);
                orgColumns.add("ITEM",item+"",Types.VARCHAR);

                orgColumns.add("ORGANIZATIONNO",o.getOrganizationNo(),Types.VARCHAR);
                orgColumns.add("WAREHOUSE",o.getWarehouse(),Types.VARCHAR);
                orgColumns.add("SUBTASKNO",billNo+"_"+item,Types.VARCHAR);


                String[] orgColumnNames = orgColumns.getColumns().toArray(new String[0]);
                DataValue[] orgDataValues = orgColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib1=new InsBean("DCP_STOCKTASK_ORG",orgColumnNames);
                ib1.addValues(orgDataValues);
                this.addProcessData(new DataProcessBean(ib1));

            }
        }

        ColumnDataValue mainColumns=new ColumnDataValue();
        mainColumns.add("SHOPID",shopId,Types.VARCHAR);
        mainColumns.add("EID",eId,Types.VARCHAR);
        mainColumns.add("ORGANIZATIONNO",req.getOrganizationNO(),Types.VARCHAR);
        mainColumns.add("STOCKTASKNO",billNo,Types.VARCHAR);
        mainColumns.add("BDATE",request.getBDate(),Types.VARCHAR);
        mainColumns.add("MEMO",request.getMemo(),Types.VARCHAR);
        //mainColumns.add("WAREHOUSE",request.getware,Types.VARCHAR);
        mainColumns.add("IS_BTAKE",request.getIsBTake(),Types.VARCHAR);
        mainColumns.add("STATUS","0",Types.VARCHAR);
        mainColumns.add("DOC_TYPE",request.getDocType(),Types.VARCHAR);
        mainColumns.add("TASKWAY",request.getTaskWay(),Types.VARCHAR);
        mainColumns.add("NOTGOODSMODE",request.getNotGoodsMode(),Types.VARCHAR);
        mainColumns.add("IS_ADJUST_STOCK",request.getIsAdjustStock(),Types.VARCHAR);
        mainColumns.add("CREATEBY",employeeNo,Types.VARCHAR);
        mainColumns.add("CREATE_DATE",bDate,Types.VARCHAR);
        mainColumns.add("CREATE_TIME",bTime,Types.VARCHAR);
        mainColumns.add("PARTITION_DATE",request.getSDate(),Types.VARCHAR);
        mainColumns.add("STOCKTASKID", PosPub.getGUID(false),Types.VARCHAR);//request.getStockTaskID()
        mainColumns.add("CREATETYPE",request.getCreateType(),Types.VARCHAR);
        mainColumns.add("SDATE",request.getSDate(),Types.VARCHAR);
        mainColumns.add("TOTSUBTASKQTY",orgList.size(),Types.VARCHAR);
        mainColumns.add("TOTCQTY",detail.size(),Types.VARCHAR);
        mainColumns.add("PTEMPLATENO",request.getPTemplateNo(),Types.VARCHAR);
        mainColumns.add("WAREHOUSETYPE",request.getWarehouseType(),Types.VARCHAR);
        mainColumns.add("EMPLOYEEID",request.getEmployeeId(),Types.VARCHAR);
        mainColumns.add("DEPARTID",request.getDepartId(),Types.VARCHAR);

        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib1=new InsBean("DCP_STOCKTASK",mainColumnNames);
        ib1.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib1));

        res.setStockTaskNo(billNo);
        this.doExecuteDataToDB();
    }


    @Override
    protected List<InsBean> prepareInsertData(DCP_StockTaskNewCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockTaskNewCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockTaskNewCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected String getQuerySql(DCP_StockTaskNewCreateReq req) throws Exception {
        return null;
    }

}
