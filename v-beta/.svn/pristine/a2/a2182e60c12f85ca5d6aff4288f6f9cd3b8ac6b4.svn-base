package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BomCreateReq;
import com.dsc.spos.json.cust.req.DCP_BomProcessInitReq;
import com.dsc.spos.json.cust.res.DCP_BomProcessInitRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_BomProcessInit extends SPosAdvanceService<DCP_BomProcessInitReq, DCP_BomProcessInitRes> {

    @Override
    protected void processDUID(DCP_BomProcessInitReq req, DCP_BomProcessInitRes res) throws Exception {
        // TODO Auto-generated method stub
        List<DCP_BomProcessInitReq.BomList> bomList = req.getRequest().getBomList();
        String eid = req.geteId();
        String employeeNo = req.getEmployeeNo();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


        String sJoinno = bomList.stream()
                .map(DCP_BomProcessInitReq.BomList::getBomNo).distinct()
                .collect(Collectors.joining(","))+",";
        Map<String, String> mapOrder=new HashMap<String, String>();
        mapOrder.put("BOMNO", sJoinno);
        MyCommon cm=new MyCommon();
        String withasSql_mono=cm.getFormatSourceMultiColWith(mapOrder);

        String sql="with p as ("+withasSql_mono+") " +
                " select a.bomno,a.pluno,a.ISPROCESSENABLE,a.PROCESSSTATUS,a.INWGROUPNO,d.plu_name as pluname,a.unit,a.batchqty," +
                " b.MATERIAL_PLUNO,b.MATERIAL_UNIT,b.MATERIAL_QTY,b.QTY,b.ISBATCH,b.PWGROUPNO,b.ISMIX,b.MIXGROUP,b.KWGROUPNO,b.SORTID,B.ISBUCKLE " +
                " from dcp_bom a" +
                " inner join p on a.bomno=p.bomno " +
                " left join dcp_bom_material b on a.eid=b.eid and a.bomno=b.bomno " +
                " left join dcp_goods_lang d on d.eid=a.eid and d.pluno=a.pluno and d.lang_type='"+req.getLangType()+"' " +
                " where a.eid='"+req.geteId()+"' ";
        List<Map<String, Object>> list = this.doQueryData(sql, null);

        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        for (DCP_BomProcessInitReq.BomList bomListItem : bomList){
            String bomNo = bomListItem.getBomNo();
            List<Map<String, Object>> bomFilterRows = list.stream().filter(x -> x.get("BOMNO").toString().equals(bomNo)).collect(Collectors.toList());
            if(bomFilterRows.size()<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, bomNo+"不存在！");
            }
            String isProcessEnable = bomFilterRows.get(0).get("ISPROCESSENABLE").toString();
            String processStatus = bomFilterRows.get(0).get("PROCESSSTATUS").toString();
            if(Check.Null(isProcessEnable)||"N".equals(isProcessEnable)||("Y".equals(isProcessEnable)&&!"Y".equals(processStatus))){

            }else{
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, bomNo+"配方工序已维护，不可初始化！");
            }

            //先删数据
            DelBean db1 = new DelBean("MES_BOM_PROCESS");
            db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            db1.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            DelBean db2 = new DelBean("MES_BOM_SUBPROCESS");
            db2.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            db2.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));

            DelBean db3 = new DelBean("MES_BOM_SUBPROCESS_MATERIAL");
            db3.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            db3.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db3));


            //表MES_BOM_PROCESS
            ColumnDataValue processColumns=new ColumnDataValue();
            processColumns.add("EID", DataValues.newString(req.geteId()));
            processColumns.add("BOMNO", DataValues.newString(bomNo));
            processColumns.add("PLUNO", DataValues.newString(bomFilterRows.get(0).get("PLUNO").toString()));
            processColumns.add("PITEM", DataValues.newString("1"));
            processColumns.add("PROCESSNO", DataValues.newString("#01"));
            processColumns.add("WGROUPNO", DataValues.newString(bomFilterRows.get(0).get("INWGROUPNO").toString()));
            processColumns.add("ARTIFACTTYPE", DataValues.newString("2"));
            processColumns.add("ARTIFACTNO", DataValues.newString(bomFilterRows.get(0).get("PLUNO").toString()));
            processColumns.add("ARTIFACTNAME", DataValues.newString(bomFilterRows.get(0).get("PLUNAME").toString()));
            processColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));
            processColumns.add("CREATEOPNAME", DataValues.newString(req.getEmployeeName()));
            processColumns.add("CREATETIME", DataValues.newDate(lastmoditime));
            processColumns.add("LASTMODIOPID", DataValues.newString(req.getOpNO()));
            processColumns.add("LASTMODIOPNAME", DataValues.newString(req.getEmployeeName()));
            processColumns.add("LASTMODITIME", DataValues.newDate(lastmoditime));
            processColumns.add("STATUS", DataValues.newString("100"));

            processColumns.add("PQTY", DataValues.newString(bomFilterRows.get(0).get("QTY").toString()));
            processColumns.add("PUNIT", DataValues.newString(bomFilterRows.get(0).get("UNIT").toString()));
            processColumns.add("SORTID", DataValues.newString("1"));


            String[] processColumnNames = processColumns.getColumns().toArray(new String[0]);
            DataValue[] processDataValues = processColumns.getDataValues().toArray(new DataValue[0]);
            InsBean ibProcess=new InsBean("MES_BOM_PROCESS",processColumnNames);
            ibProcess.addValues(processDataValues);
            this.addProcessData(new DataProcessBean(ibProcess));

            ColumnDataValue subProcessColumns=new ColumnDataValue();
            subProcessColumns.add("EID", DataValues.newString(req.geteId()));
            subProcessColumns.add("BOMNO", DataValues.newString(bomNo));
            subProcessColumns.add("PLUNO", DataValues.newString(bomFilterRows.get(0).get("PLUNO").toString()));
            subProcessColumns.add("PROCESSNO", DataValues.newString("#01"));
            subProcessColumns.add("PITEM", DataValues.newString("1"));
            subProcessColumns.add("SITEM", DataValues.newString("1"));
            subProcessColumns.add("SNAME", DataValues.newString("生产"));
            subProcessColumns.add("STATUS", DataValues.newString("100"));
            String[] subProcessColumnNames = subProcessColumns.getColumns().toArray(new String[0]);
            DataValue[] subProcessDataValues = subProcessColumns.getDataValues().toArray(new DataValue[0]);
            InsBean ibsubProcess=new InsBean("MES_BOM_SUBPROCESS",subProcessColumnNames);
            ibsubProcess.addValues(subProcessDataValues);
            this.addProcessData(new DataProcessBean(ibsubProcess));


            int materialItem=0;
            for (Map<String, Object> materialRequest : bomFilterRows){
                materialItem++;
                ColumnDataValue processMaterialColumns=new ColumnDataValue();
                processMaterialColumns.add("EID", DataValues.newString(req.geteId()));
                processMaterialColumns.add("BOMNO", DataValues.newString(bomNo));
                processMaterialColumns.add("PROCESSNO", DataValues.newString("#01"));
                processMaterialColumns.add("PLUNO", DataValues.newString(materialRequest.get("PLUNO").toString()));
                processMaterialColumns.add("PITEM", DataValues.newString("1"));
                processMaterialColumns.add("SITEM", DataValues.newString("1"));
                processMaterialColumns.add("ITEM", DataValues.newString(materialItem));
                processMaterialColumns.add("MATERIAL_TYPE", DataValues.newString("0"));
                processMaterialColumns.add("MATERIAL_PLUNO", DataValues.newString(materialRequest.get("MATERIAL_PLUNO").toString()));
                processMaterialColumns.add("MATERIAL_UNIT", DataValues.newString(materialRequest.get("MATERIAL_UNIT").toString()));
                processMaterialColumns.add("MATERIAL_QTY", DataValues.newString(materialRequest.get("MATERIAL_QTY").toString()));
                processMaterialColumns.add("STATUS", DataValues.newString("100"));

                processMaterialColumns.add("QTY", DataValues.newString(materialRequest.get("QTY").toString()));
                //ISBATCH PWGROUPNO ISMIX MIXGROUP ISBUCKLE KWGROUPNO  SORTID
                processMaterialColumns.add("ISBATCH", DataValues.newString(materialRequest.get("ISBATCH").toString()));
                processMaterialColumns.add("PWGROUPNO", DataValues.newString(materialRequest.get("PWGROUPNO").toString()));
                processMaterialColumns.add("ISMIX", DataValues.newString(materialRequest.get("ISMIX").toString()));
                processMaterialColumns.add("MIXGROUP", DataValues.newString(materialRequest.get("MIXGROUP").toString()));
                processMaterialColumns.add("ISBUCKLE", DataValues.newString(materialRequest.get("ISBUCKLE").toString()));
                processMaterialColumns.add("KWGROUPNO", DataValues.newString(materialRequest.get("KWGROUPNO").toString()));
                processMaterialColumns.add("SORTID", DataValues.newString(materialRequest.get("SORTID").toString()));


                String[] processMaterialColumnNames = processMaterialColumns.getColumns().toArray(new String[0]);
                DataValue[] processMaterialDataValues = processMaterialColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ibProcessMaterial=new InsBean("MES_BOM_SUBPROCESS_MATERIAL",processMaterialColumnNames);
                ibProcessMaterial.addValues(processMaterialDataValues);
                this.addProcessData(new DataProcessBean(ibProcessMaterial));
            }

        }


        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BomProcessInitReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BomProcessInitReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BomProcessInitReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_BomProcessInitReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_BomProcessInitReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_BomProcessInitReq>() {
        };
    }

    @Override
    protected DCP_BomProcessInitRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_BomProcessInitRes();
    }


}
