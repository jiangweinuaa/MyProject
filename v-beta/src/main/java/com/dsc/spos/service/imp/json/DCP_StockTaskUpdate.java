package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockTaskUpdateReq;
import com.dsc.spos.json.cust.res.DCP_StockTaskUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

public class DCP_StockTaskUpdate extends SPosAdvanceService<DCP_StockTaskUpdateReq, DCP_StockTaskUpdateRes> {
    //private Logger loger = LogManager.getLogger(DCP_StockTaskNewCreate.class.getName());

    @Override
    protected boolean isVerifyFail(DCP_StockTaskUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_StockTaskUpdateReq> getRequestType() {
        return new TypeToken<DCP_StockTaskUpdateReq>(){};
    }

    @Override
    protected DCP_StockTaskUpdateRes getResponseType() {
        return new DCP_StockTaskUpdateRes();
    }

    @Override
    protected void processDUID(DCP_StockTaskUpdateReq req,DCP_StockTaskUpdateRes res) throws Exception {

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String shopId = req.getShopId();
        String employeeNo = req.getEmployeeNo();
        DCP_StockTaskUpdateReq.LevelElm request = req.getRequest();

        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String bDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());
        String bTime =	new SimpleDateFormat("HHmmss").format(new Date());
        String billNo = request.getStockTaskNo();

        DelBean db1 = new DelBean("DCP_STOCKTASK_DETAIL");
        db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
        db1.addCondition("STOCKTASKNO", new DataValue(billNo,Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        DelBean db2 = new DelBean("DCP_STOCKTASK_ORG");
        db2.addCondition("EID", new DataValue(eId,Types.VARCHAR));
        db2.addCondition("STOCKTASKNO", new DataValue(billNo,Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));


        List<DCP_StockTaskUpdateReq.Detail> detail = request.getDetail();
        List<DCP_StockTaskUpdateReq.OrgList> orgList = request.getOrgList();

        if(CollUtil.isNotEmpty(detail)){
            String pluNoStr="'********',";
            for(DCP_StockTaskUpdateReq.Detail d : detail){
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
            for(DCP_StockTaskUpdateReq.Detail d : detail){
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
            for (DCP_StockTaskUpdateReq.OrgList o : orgList){
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

        //修改
        UptBean ub1 = new UptBean("DCP_STOCKTASK");
        ub1.addUpdateValue("BDATE", new DataValue(request.getBDate(), Types.VARCHAR));
        ub1.addUpdateValue("MEMO", new DataValue(request.getMemo(), Types.VARCHAR));
        ub1.addUpdateValue("IS_BTAKE", new DataValue(request.getIsBTake(), Types.VARCHAR));
        ub1.addUpdateValue("DOC_TYPE", new DataValue(request.getDocType(), Types.VARCHAR));
        ub1.addUpdateValue("TASKWAY", new DataValue(request.getTaskWay(), Types.VARCHAR));
        ub1.addUpdateValue("NOTGOODSMODE", new DataValue(request.getNotGoodsMode(), Types.VARCHAR));
        ub1.addUpdateValue("IS_ADJUST_STOCK", new DataValue(request.getIsAdjustStock(), Types.VARCHAR));
        ub1.addUpdateValue("PARTITION_DATE", new DataValue(request.getSDate(), Types.VARCHAR));
        ub1.addUpdateValue("STOCKTASKID", new DataValue(request.getStockTaskID(), Types.VARCHAR));
        ub1.addUpdateValue("CREATETYPE", new DataValue(request.getCreateType(), Types.VARCHAR));
        ub1.addUpdateValue("SDATE", new DataValue(request.getSDate(), Types.VARCHAR));
        ub1.addUpdateValue("TOTSUBTASKQTY", new DataValue(orgList.size(), Types.VARCHAR));
        ub1.addUpdateValue("TOTCQTY", new DataValue(detail.size(), Types.VARCHAR));
        ub1.addUpdateValue("PTEMPLATENO", new DataValue(request.getPTemplateNo(), Types.VARCHAR));
        ub1.addUpdateValue("WAREHOUSETYPE", new DataValue(request.getWarehouseType(), Types.VARCHAR));
        ub1.addUpdateValue("EMPLOYEEID", new DataValue(request.getEmployeeId(), Types.VARCHAR));
        ub1.addUpdateValue("DEPARTID", new DataValue(request.getDepartId(), Types.VARCHAR));
        ub1.addUpdateValue("MODIFYBY", new DataValue(employeeNo, Types.VARCHAR));
        ub1.addUpdateValue("MODIFY_DATE", new DataValue(bDate, Types.VARCHAR));
        ub1.addUpdateValue("MODIFY_TIME", new DataValue(bTime, Types.VARCHAR));

        ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
        ub1.addCondition("STOCKTASKNO", new DataValue(billNo, Types.VARCHAR));

        //更新
        this.addProcessData(new DataProcessBean(ub1));

        this.doExecuteDataToDB();
    }


    @Override
    protected List<InsBean> prepareInsertData(DCP_StockTaskUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockTaskUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockTaskUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected String getQuerySql(DCP_StockTaskUpdateReq req) throws Exception {
        return null;
    }

}

