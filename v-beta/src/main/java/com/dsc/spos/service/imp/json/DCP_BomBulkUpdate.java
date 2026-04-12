package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BomBulkUpdateReq;
import com.dsc.spos.json.cust.res.DCP_BomBulkUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DCP_BomBulkUpdate extends SPosAdvanceService<DCP_BomBulkUpdateReq, DCP_BomBulkUpdateRes> {

    @Override
    protected void processDUID(DCP_BomBulkUpdateReq req, DCP_BomBulkUpdateRes res) throws Exception {
        String eId = req.geteId();
        DCP_BomBulkUpdateReq.LevelRequest request = req.getRequest();

        String sJoinno = request.getBomList().stream()
                .map(DCP_BomBulkUpdateReq.BomList::getBomNo).distinct()
                .collect(Collectors.joining(","));
        Map<String, String> mapOrder=new HashMap<String, String>();
        mapOrder.put("BOMNO", sJoinno+",");
        MyCommon cm=new MyCommon();
        String withasSql_mono=cm.getFormatSourceMultiColWith(mapOrder);

        String bomSql="with p as ("+withasSql_mono+") " +
                " select a.* " +
                " from dcp_bom a" +
                " inner join p on a.bomno=p.bomno " +
                " where a.eid='"+req.geteId()+"' ";
        List<Map<String, Object>> bomList = this.doQueryData(bomSql, null);

        String bomMaterialSql="with p as ("+withasSql_mono+") " +
                " select a.* " +
                " from dcp_bom_material a" +
                " inner join p on a.bomno=p.bomno " +
                " where a.eid='"+req.geteId()+"' ";
        List<Map<String, Object>> bomMaterialList = this.doQueryData(bomMaterialSql, null);


        String bomRangeSql="with p as ("+withasSql_mono+") " +
                " select a.* " +
                " from dcp_bom_range a" +
                " inner join p on a.bomno=p.bomno " +
                " where a.eid='"+req.geteId()+"' ";
        List<Map<String, Object>> bomRangeList = this.doQueryData(bomRangeSql, null);

        //MES_BOM_SUBPROCESS_MATERIAL
        String bomSubProcessMaterialSql="with p as ("+withasSql_mono+") " +
                " select a.* " +
                " from MES_BOM_SUBPROCESS_MATERIAL a" +
                " inner join p on a.bomno=p.bomno " +
                " where a.eid='"+req.geteId()+"' ";
        List<Map<String, Object>> bomSubProcessMaterialList = this.doQueryData(bomSubProcessMaterialSql, null);


        Integer unitUDLength = PosPub.getUnitUDLength(dao, req.geteId(), req.getRequest().getNewMaterialUnit());

        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        //校验提前
        if("0".equals(request.getOperation())) {
            //1.operation入参为0时，materialUnit，newMaterialPluNo，newMaterialUnit，oldRawQty，newRawQty入参不可为空，
            if (Check.Null(request.getMaterialUnit()) || Check.Null(request.getNewMaterialPluNo()) || Check.Null(request.getNewMaterialUnit()) || Check.Null(request.getOldRawQty()) || Check.Null(request.getNewRawQty())) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "materialUnit，newMaterialPluNo，newMaterialUnit，oldRawQty，newRawQty入参不可为空!".toString());
            }
        }
        else if("1".equals(request.getOperation())){
            //按upd开头参数判断是否更新该字段，这些upd参数为Y时，
            // 对应参数不可为空

            if("Y".equals(request.getUpdBomProcess())){
                List<Map<String, Object>> checkRows = bomList.stream().filter(x -> !"N".equals(x.get("ISPROCESSENABLE").toString())).collect(Collectors.toList());
                if(checkRows.size()>0){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "变更配方中存在启用工序配方，不可按数量更新配方工序组成用量!".toString());
                }
            }

            if("Y".equals(request.getUpdMaterialUnit())&&Check.Null(request.getNewMaterialUnit())){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "newMaterialUnit入参不可为空!".toString());
            }

            if("Y".equals(request.getUpdByMaterialQty())&&Check.Null(request.getMaterialQty())){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "materialQty入参不可为空!".toString());
            }
            if("Y".equals(request.getUpdQty())&&Check.Null(request.getQty())){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "qty入参不可为空!".toString());
            }

            if("Y".equals(request.getUpdLoseRate())&&Check.Null(request.getLoseRate())){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "loseRate入参不可为空!".toString());
            }

            if("Y".equals(request.getUpdBDate())&&Check.Null(request.getMaterialBDate())){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "materialBDate入参不可为空!".toString());
            }

            if("Y".equals(request.getUpdEDate())&&Check.Null(request.getMaterialEDate())){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "materialEDate入参不可为空!".toString());
            }
            if("Y".equals(request.getUpdIsPick())&&Check.Null(request.getIsPick())){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "isPick入参不可为空!".toString());
            }

            if("Y".equals(request.getUpdIsBatch())&&Check.Null(request.getIsBatch())){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "isBatch入参不可为空!".toString());
            }
            if("Y".equals(request.getUpdPWGroupNo())&&Check.Null(request.getPWGroupNo())){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "isPick入参不可为空!".toString());
            }
            if("Y".equals(request.getUpdIsBuckle())&&Check.Null(request.getIsBuckle())){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "isBuckle入参不可为空!".toString());
            }
            if("Y".equals(request.getUpdKPGroupNo())&&Check.Null(request.getKWGroupNo())){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "kWGroupNo入参不可为空!".toString());
            }

            if("Y".equals(request.getUpdByRaw())&&Check.Null(request.getNewRawQty())){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "newRawQty入参不可为空!".toString());
            }
        }

        //写入_v
        for (DCP_BomBulkUpdateReq.BomList singleBom : request.getBomList()) {
            List<Map<String, Object>> singleBomList = bomList.stream().filter(x -> x.get("BOMNO").toString().equals(singleBom.getBomNo())).collect(Collectors.toList());
            List<Map<String, Object>> singleBomMaterialList = bomMaterialList.stream().filter(x -> x.get("BOMNO").toString().equals(singleBom.getBomNo())).collect(Collectors.toList());
            List<Map<String, Object>> singleBomRageList = bomRangeList.stream().filter(x -> x.get("BOMNO").toString().equals(singleBom.getBomNo())).collect(Collectors.toList());
            if (singleBomList.size() <= 0 || singleBomMaterialList.size() <= 0) {
                continue;
            }
            String oldVersionNum = singleBomList.get(0).get("VERSIONNUM").toString();

            //添加_v
            ColumnDataValue mainColumns = new ColumnDataValue();
            mainColumns.add("EID", DataValues.newString(eId));
            mainColumns.add("BOMNO", DataValues.newString(singleBom.getBomNo()));
            mainColumns.add("BOMTYPE", DataValues.newString(singleBomList.get(0).get("BOMTYPE").toString()));
            mainColumns.add("PLUNO", DataValues.newString(singleBomList.get(0).get("PLUNO").toString()));
            mainColumns.add("UNIT", DataValues.newString(singleBomList.get(0).get("UNIT").toString()));
            mainColumns.add("MULQTY", DataValues.newString(singleBomList.get(0).get("MULQTY").toString()));
            mainColumns.add("EFFDATE", new DataValue(singleBomList.get(0).get("EFFDATE").toString(), Types.DATE));

            mainColumns.add("MEMO", DataValues.newString(singleBomList.get(0).get("MEMO").toString()));
            mainColumns.add("STATUS", DataValues.newString(singleBomList.get(0).get("STATUS").toString()));
            mainColumns.add("RESTRICTSHOP", DataValues.newString(singleBomList.get(0).get("RESTRICTSHOP").toString()));
            mainColumns.add("CREATEOPID", DataValues.newString(singleBomList.get(0).get("CREATEOPID").toString()));
            mainColumns.add("CREATEOPNAME", DataValues.newString(singleBomList.get(0).get("CREATEOPNAME").toString()));
            mainColumns.add("CREATETIME", new DataValue(createTime, Types.DATE));
            mainColumns.add("LASTMODIOPID", DataValues.newString(singleBomList.get(0).get("LASTMODIOPID").toString()));
            mainColumns.add("LASTMODIOPNAME", DataValues.newString(singleBomList.get(0).get("LASTMODIOPNAME").toString()));
            mainColumns.add("LASTMODITIME", new DataValue(createTime, Types.DATE));
            mainColumns.add("CREATEDEPTID", DataValues.newString(singleBomList.get(0).get("CREATEDEPTID").toString()));
            mainColumns.add("PRODTYPE", DataValues.newString(singleBomList.get(0).get("PRODTYPE").toString()));
            mainColumns.add("BATCHQTY", DataValues.newInteger(singleBomList.get(0).get("BATCHQTY").toString()));
            mainColumns.add("VERSIONNUM", DataValues.newInteger(singleBomList.get(0).get("VERSIONNUM").toString()));
            mainColumns.add("CONTAINTYPE", DataValues.newString(singleBomList.get(0).get("CONTAINTYPE").toString()));
            mainColumns.add("PROCESSSTATUS", DataValues.newString(singleBomList.get(0).get("PROCESSSTATUS").toString()));
            mainColumns.add("FIXEDLOSSQTY", DataValues.newString(singleBomList.get(0).get("FIXEDLOSSQTY").toString()));
            mainColumns.add("ISPROCESSENABLE", DataValues.newString(singleBomList.get(0).get("ISPROCESSENABLE").toString()));
            mainColumns.add("INWGROUPNO", DataValues.newString(singleBomList.get(0).get("INWGROUPNO").toString()));

            mainColumns.add("REMAINTYPE", singleBomList.get(0).get("REMAINTYPE").toString(), Types.VARCHAR);
            mainColumns.add("MINQTY", singleBomList.get(0).get("MINQTY").toString(), Types.VARCHAR);
            mainColumns.add("ODDVALUE", singleBomList.get(0).get("ODDVALUE").toString(), Types.VARCHAR);
            mainColumns.add("PRODUCTEXCEED", singleBomList.get(0).get("PRODUCTEXCEED").toString(), Types.VARCHAR);
            mainColumns.add("PROCRATE", singleBomList.get(0).get("PROCRATE").toString(), Types.VARCHAR);
            mainColumns.add("DISPTYPE", singleBomList.get(0).get("DISPTYPE").toString(), Types.VARCHAR);
            mainColumns.add("SEMIWOTYPE", singleBomList.get(0).get("SEMIWOTYPE").toString(), Types.VARCHAR);
            mainColumns.add("SEMIWODEPTTYPE", singleBomList.get(0).get("SEMIWODEPTTYPE").toString(), Types.VARCHAR);
            mainColumns.add("FIXPREDAYS", singleBomList.get(0).get("FIXPREDAYS").toString(), Types.VARCHAR);
            mainColumns.add("SDLABORTIME", singleBomList.get(0).get("SDLABORTIME").toString(), Types.VARCHAR);
            mainColumns.add("SDMACHINETIME", singleBomList.get(0).get("SDMACHINETIME").toString(), Types.VARCHAR);
            mainColumns.add("STANDARDHOURS", singleBomList.get(0).get("STANDARDHOURS").toString(), Types.VARCHAR);


            String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
            DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);

            InsBean ib1v = new InsBean("DCP_BOM_V", mainColumnNames);
            ib1v.addValues(mainDataValues);
            this.addProcessData(new DataProcessBean(ib1v));

            for (Map<String, Object> singleBomMaterial : singleBomMaterialList) {

                ColumnDataValue materialColumns = new ColumnDataValue();
                materialColumns.add("EID", DataValues.newString(eId));
                materialColumns.add("BOMNO", DataValues.newString(singleBom.getBomNo()));
                materialColumns.add("MATERIAL_PLUNO", DataValues.newString(singleBomMaterial.get("MATERIAL_PLUNO").toString()));
                materialColumns.add("MATERIAL_UNIT", DataValues.newString(singleBomMaterial.get("MATERIAL_UNIT").toString()));
                materialColumns.add("MATERIAL_QTY", DataValues.newInteger(singleBomMaterial.get("MATERIAL_QTY").toString()));
                materialColumns.add("QTY", DataValues.newInteger(singleBomMaterial.get("QTY").toString()));
                materialColumns.add("LOSS_RATE", DataValues.newInteger(singleBomMaterial.get("LOSS_RATE").toString()));
                materialColumns.add("ISBUCKLE", DataValues.newString(singleBomMaterial.get("ISBUCKLE").toString()));
                materialColumns.add("ISREPLACE", DataValues.newString(singleBomMaterial.get("ISREPLACE").toString()));
                materialColumns.add("MATERIAL_BDATE", DataValues.newDate(singleBomMaterial.get("MATERIAL_BDATE").toString()));
                materialColumns.add("MATERIAL_EDATE", DataValues.newDate(singleBomMaterial.get("MATERIAL_EDATE").toString()));
                materialColumns.add("SORTID", DataValues.newInteger(singleBomMaterial.get("SORTID").toString()));
                //materialColumns.add("TRAN_TIME", new DataValue(createTime, Types.DATE));
                materialColumns.add("LASTMODITIME", new DataValue(createTime, Types.DATE));
                materialColumns.add("COSTRATE", DataValues.newInteger(singleBomMaterial.get("COSTRATE").toString()));
                materialColumns.add("ISPICK", DataValues.newString(singleBomMaterial.get("ISPICK").toString()));
                materialColumns.add("ISBATCH", DataValues.newString(singleBomMaterial.get("ISBATCH").toString()));
                materialColumns.add("PWGROUPNO", DataValues.newString(singleBomMaterial.get("PWGROUPNO").toString()));
                materialColumns.add("ISMIX", DataValues.newString(singleBomMaterial.get("ISMIX").toString()));
                materialColumns.add("MIXGROUP", DataValues.newString(singleBomMaterial.get("MIXGROUP").toString()));
                materialColumns.add("KWGROUPNO", DataValues.newString(singleBomMaterial.get("KWGROUPNO").toString()));
                materialColumns.add("VERSIONNUM", DataValues.newString(oldVersionNum));

                String[] materialColumnNames = materialColumns.getColumns().toArray(new String[0]);
                DataValue[] materialDataValues = materialColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib2 = new InsBean("DCP_BOM_MATERIAL_v", materialColumnNames);
                ib2.addValues(materialDataValues);
                this.addProcessData(new DataProcessBean(ib2));

            }

            for (Map<String, Object> singleBomRange : singleBomRageList) {
                ColumnDataValue rangeColumns = new ColumnDataValue();
                rangeColumns.add("EID", DataValues.newString(eId));
                rangeColumns.add("BOMNO", DataValues.newString(singleBomRange.get("BOMNO").toString()));
                rangeColumns.add("SHOPID", DataValues.newString(singleBomRange.get("SHOPID").toString()));
                rangeColumns.add("ORGANIZATIONNO", DataValues.newString(singleBomRange.get("ORGANIZATIONNO").toString()));
                rangeColumns.add("LASTMODITIME", new DataValue(createTime, Types.DATE));
                //rangeColumns.add("TRAN_TIME", DataValues.newString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
                rangeColumns.add("VERSIONNUM", DataValues.newString(oldVersionNum));
                String[] rangeColumnNames = rangeColumns.getColumns().toArray(new String[0]);
                DataValue[] rangeDataValues = rangeColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib2 = new InsBean("DCP_BOM_RANGE_V", rangeColumnNames);
                ib2.addValues(rangeDataValues);
                this.addProcessData(new DataProcessBean(ib2));
            }

            //更新当前版本加1
            int newVersionNum=Integer.valueOf(oldVersionNum)+1;

            UptBean ub1 = null;
            ub1 = new UptBean("DCP_BOM");
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("BOMNO", new DataValue(singleBom.getBomNo(), Types.VARCHAR));

            ub1.addUpdateValue("VERSIONNUM", new DataValue(newVersionNum, Types.VARCHAR));

            this.addProcessData(new DataProcessBean(ub1));

        }

        if("0".equals(request.getOperation())){

            for (DCP_BomBulkUpdateReq.BomList singleBom : request.getBomList()){
                List<Map<String, Object>> singleBomList = bomList.stream().filter(x -> x.get("BOMNO").toString().equals(singleBom.getBomNo())).collect(Collectors.toList());
                List<Map<String, Object>> singleBomMaterialList = bomMaterialList.stream().filter(x -> x.get("BOMNO").toString().equals(singleBom.getBomNo())).collect(Collectors.toList());
                if(singleBomList.size()<=0||singleBomMaterialList.size()<=0){
                    continue;
                }

                //按bomList和materialPluNo，materialUnit过滤DCP_BOM_MATERIAL表，
                //更新MATERIAL_PLUNO=入参newMaterialPluNo
                //MATERIAL_UNIT=入参newMaterialUnit
                //MATERIAL_QTY=MATERIAL_QTY×入参newRawQty/入参oldRawQty（按单位小数位数保留）

                //如入参updBomProcess为Y，则按bomList和materialPluNo，materialUnit过滤MES_BOM_SUBPROCESS_MATERIAL表，
                //更新MATERIAL_PLUNO=入参newMaterialPluNo
                //MATERIAL_UNIT=入参newMaterialUnit
                //MATERIAL_QTY=MATERIAL_QTY×入参newRawQty/入参oldRawQty（按单位小数位数保留）
                //更新后，按BOMNO分组汇总对比DCP_BOM_MATERIAL.MATERIAL_QTY和SUM(MES_BOM_SUBPROCESS_MATERIAL.MATERIAL_QTY)，如值不相等，则将误差部分处理到MES_BOM_SUBPROCESS_MATERIAL中其中一笔数据上
                //

                List<Map<String, Object>> filterMaterial = singleBomMaterialList.stream().filter(x -> x.get("MATERIAL_PLUNO").toString().equals(request.getMaterialPluNo()) ).collect(Collectors.toList());//&& x.get("MATERIAL_UNIT").toString().equals(request.getMaterialUnit())
                if(filterMaterial.size()>0){

                    BigDecimal oldMaterialQty = new BigDecimal(filterMaterial.get(0).get("MATERIAL_QTY").toString());
                    BigDecimal nowMaterialQty = oldMaterialQty.multiply(new BigDecimal(request.getNewRawQty())).divide(new BigDecimal(request.getOldRawQty()), unitUDLength, RoundingMode.HALF_UP);

                    UptBean ub1 = null;
                    ub1 = new UptBean("DCP_BOM_MATERIAL");
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("BOMNO", new DataValue(singleBom.getBomNo(), Types.VARCHAR));
                    ub1.addCondition("MATERIAL_PLUNO", new DataValue(request.getMaterialPluNo(), Types.VARCHAR));
                    //ub1.addCondition("MATERIAL_UNIT", new DataValue(request.getNewMaterialUnit(), Types.VARCHAR));


                    ub1.addUpdateValue("MATERIAL_PLUNO", new DataValue(request.getNewMaterialPluNo(), Types.VARCHAR));

                    if(Check.NotNull(request.getNewMaterialUnit())){
                        ub1.addUpdateValue("MATERIAL_UNIT", new DataValue(request.getNewMaterialUnit(), Types.VARCHAR));
                    }

                    ub1.addUpdateValue("MATERIAL_QTY", new DataValue(nowMaterialQty, Types.VARCHAR));

                    this.addProcessData(new DataProcessBean(ub1));

                    if("Y".equals(request.getUpdBomProcess())){
                        List<Map<String, Object>> processMaterialFilterRows = bomSubProcessMaterialList.stream()
                                .filter(x -> x.get("BOMNO").toString().equals(singleBom.getBomNo())
                                        && x.get("MATERIAL_PLUNO").toString().equals(request.getMaterialPluNo())
                                        ).collect(Collectors.toList());//&& x.get("MATERIAL_UNIT").toString().equals(request.getMaterialUnit())
                        BigDecimal allProcessMaterialQty=new BigDecimal(0);
                        if(processMaterialFilterRows.size()>0){
                            for (Map<String, Object> singleProcessMaterial : processMaterialFilterRows){
                                BigDecimal processMaterialQty = new BigDecimal(singleProcessMaterial.get("MATERIAL_QTY").toString());
                                BigDecimal nowProcessMaterialQty = processMaterialQty.multiply(new BigDecimal(request.getNewRawQty())).divide(new BigDecimal(request.getOldRawQty()), unitUDLength, RoundingMode.HALF_UP);
                                allProcessMaterialQty=allProcessMaterialQty.add(nowProcessMaterialQty);
                            }
                            BigDecimal subtract = nowMaterialQty.subtract(allProcessMaterialQty);
                            for (Map<String, Object> singleProcessMaterial : processMaterialFilterRows){
                                BigDecimal processMaterialQty = new BigDecimal(singleProcessMaterial.get("MATERIAL_QTY").toString());
                                BigDecimal nowProcessMaterialQty = processMaterialQty.multiply(new BigDecimal(request.getNewRawQty())).divide(new BigDecimal(request.getOldRawQty()), unitUDLength, RoundingMode.HALF_UP);
                                BigDecimal add = nowProcessMaterialQty.add(subtract);
                                subtract=subtract.subtract(subtract);
                                UptBean ub2 = null;
                                ub2 = new UptBean("MES_BOM_SUBPROCESS_MATERIAL");
                                ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub2.addCondition("BOMNO", new DataValue(singleBom.getBomNo(), Types.VARCHAR));
                                ub2.addCondition("PITEM", new DataValue(singleProcessMaterial.get("PITEM").toString(), Types.VARCHAR));
                                ub2.addCondition("SITEM", new DataValue(singleProcessMaterial.get("SITEM").toString(), Types.VARCHAR));
                                ub2.addCondition("ITEM", new DataValue(singleProcessMaterial.get("ITEM").toString(), Types.VARCHAR));


                                ub2.addUpdateValue("MATERIAL_PLUNO", new DataValue(request.getNewMaterialPluNo(), Types.VARCHAR));
                                ub2.addUpdateValue("MATERIAL_UNIT", new DataValue(request.getNewMaterialUnit(), Types.VARCHAR));
                                ub2.addUpdateValue("MATERIAL_QTY", new DataValue(add, Types.VARCHAR));

                                this.addProcessData(new DataProcessBean(ub2));
                            }
                        }

                    }

                }

            }

        }
        else if("1".equals(request.getOperation())){


            for (DCP_BomBulkUpdateReq.BomList singleBom : request.getBomList()){
                List<Map<String, Object>> singleBomList = bomList.stream().filter(x -> x.get("BOMNO").toString().equals(singleBom.getBomNo())).collect(Collectors.toList());
                List<Map<String, Object>> singleBomMaterialList = bomMaterialList.stream().filter(x -> x.get("BOMNO").toString().equals(singleBom.getBomNo())).collect(Collectors.toList());
                List<Map<String, Object>> singleBomRageList = bomRangeList.stream().filter(x -> x.get("BOMNO").toString().equals(singleBom.getBomNo())).collect(Collectors.toList());
                if(singleBomList.size()<=0||singleBomMaterialList.size()<=0){
                    continue;
                }
                List<Map<String, Object>> filterMaterial = singleBomMaterialList.stream().filter(x -> x.get("MATERIAL_PLUNO").toString().equals(request.getMaterialPluNo())).collect(Collectors.toList());// && x.get("MATERIAL_UNIT").toString().equals(request.getMaterialUnit())
                if(filterMaterial.size()>0) {

                    BigDecimal oldMaterialQty = new BigDecimal(filterMaterial.get(0).get("MATERIAL_QTY").toString());


                    UptBean ub1 = null;
                    ub1 = new UptBean("DCP_BOM_MATERIAL");
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("BOMNO", new DataValue(singleBom.getBomNo(), Types.VARCHAR));
                    ub1.addCondition("MATERIAL_PLUNO", new DataValue(request.getMaterialPluNo(), Types.VARCHAR));
                    //ub1.addCondition("MATERIAL_UNIT", new DataValue(request.getNewMaterialUnit(), Types.VARCHAR));
                    BigDecimal nowMaterialQty=BigDecimal.ZERO;
                    if ("Y".equals(request.getUpdMaterialUnit()) && Check.NotNull(request.getNewMaterialUnit())) {
                        ub1.addUpdateValue("MATERIAL_UNIT", new DataValue(request.getNewMaterialUnit(), Types.VARCHAR));
                    }
                    if ("Y".equals(request.getUpdByRaw()) && Check.NotNull(request.getNewRawQty())) {
                         nowMaterialQty = oldMaterialQty.multiply(new BigDecimal(request.getNewRawQty())).divide(new BigDecimal(request.getOldRawQty()), unitUDLength, RoundingMode.HALF_UP);

                        ub1.addUpdateValue("MATERIAL_QTY", new DataValue(nowMaterialQty, Types.VARCHAR));
                    }
                    else if ("Y".equals(request.getUpdByMaterialQty()) && Check.NotNull(request.getMaterialQty())) {
                        nowMaterialQty=new BigDecimal(request.getMaterialQty());
                        ub1.addUpdateValue("MATERIAL_QTY", new DataValue(request.getMaterialQty(), Types.VARCHAR));
                    }
                    if ("Y".equals(request.getUpdQty()) && Check.NotNull(request.getQty())) {
                        ub1.addUpdateValue("QTY", new DataValue(request.getQty(), Types.VARCHAR));
                    }

                    if ("Y".equals(request.getUpdLoseRate()) && Check.NotNull(request.getLoseRate())) {
                        ub1.addUpdateValue("LOSS_RATE", new DataValue(request.getLoseRate(), Types.VARCHAR));
                    }

                    if ("Y".equals(request.getUpdBDate()) && Check.NotNull(request.getMaterialBDate())) {
                        ub1.addUpdateValue("MATERIAL_BDATE", new DataValue(request.getMaterialBDate(), Types.DATE));
                    }

                    if ("Y".equals(request.getUpdEDate()) && Check.NotNull(request.getMaterialEDate())) {
                        ub1.addUpdateValue("MATERIAL_EDATE", new DataValue(request.getMaterialEDate(), Types.DATE));
                    }
                    if ("Y".equals(request.getUpdIsPick()) && Check.NotNull(request.getIsPick())) {
                        ub1.addUpdateValue("ISPICK", new DataValue(request.getIsPick(), Types.VARCHAR));
                    }

                    if ("Y".equals(request.getUpdIsBatch()) && Check.NotNull(request.getIsBatch())) {
                        ub1.addUpdateValue("ISBATCH", new DataValue(request.getIsBatch(), Types.VARCHAR));
                    }
                    if ("Y".equals(request.getUpdPWGroupNo()) && Check.NotNull(request.getPWGroupNo())) {
                        ub1.addUpdateValue("PWGROUPNO", new DataValue(request.getPWGroupNo(), Types.VARCHAR));
                    }
                    if ("Y".equals(request.getUpdIsBuckle()) && Check.NotNull(request.getIsBuckle())) {
                        ub1.addUpdateValue("ISBUCKLE", new DataValue(request.getIsBuckle(), Types.VARCHAR));
                    }
                    if ("Y".equals(request.getUpdKPGroupNo()) && Check.NotNull(request.getKWGroupNo())) {
                        ub1.addUpdateValue("KWGROUPNO", new DataValue(request.getKWGroupNo(), Types.VARCHAR));
                    }

                    this.addProcessData(new DataProcessBean(ub1));

                    if("Y".equals(request.getUpdBomProcess())){
                        List<Map<String, Object>> processMaterialFilterRows = bomSubProcessMaterialList.stream()
                                .filter(x -> x.get("BOMNO").toString().equals(singleBom.getBomNo())
                                        && x.get("MATERIAL_PLUNO").toString().equals(request.getMaterialPluNo())
                                       ).collect(Collectors.toList());
                        BigDecimal allProcessMaterialQty=new BigDecimal(0);
                        if(processMaterialFilterRows.size()>0){
                            for (Map<String, Object> singleProcessMaterial : processMaterialFilterRows){
                                BigDecimal processMaterialQty = new BigDecimal(singleProcessMaterial.get("MATERIAL_QTY").toString());
                                BigDecimal nowProcessMaterialQty = processMaterialQty.multiply(new BigDecimal(request.getNewRawQty())).divide(new BigDecimal(request.getOldRawQty()), unitUDLength, RoundingMode.HALF_UP);
                                allProcessMaterialQty=allProcessMaterialQty.add(nowProcessMaterialQty);
                            }
                            BigDecimal subtract = nowMaterialQty.subtract(allProcessMaterialQty);
                            for (Map<String, Object> singleProcessMaterial : processMaterialFilterRows){
                                BigDecimal processMaterialQty = new BigDecimal(singleProcessMaterial.get("MATERIAL_QTY").toString());
                                BigDecimal nowProcessMaterialQty = processMaterialQty.multiply(new BigDecimal(request.getNewRawQty())).divide(new BigDecimal(request.getOldRawQty()), unitUDLength, RoundingMode.HALF_UP);
                                BigDecimal add = nowProcessMaterialQty.add(subtract);
                                subtract=subtract.subtract(subtract);
                                UptBean ub2 = null;
                                ub2 = new UptBean("MES_BOM_SUBPROCESS_MATERIAL");
                                ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub2.addCondition("BOMNO", new DataValue(singleBom.getBomNo(), Types.VARCHAR));
                                ub2.addCondition("PITEM", new DataValue(singleProcessMaterial.get("PITEM").toString(), Types.VARCHAR));
                                ub2.addCondition("SITEM", new DataValue(singleProcessMaterial.get("SITEM").toString(), Types.VARCHAR));
                                ub2.addCondition("ITEM", new DataValue(singleProcessMaterial.get("ITEM").toString(), Types.VARCHAR));


                                ub2.addUpdateValue("MATERIAL_PLUNO", new DataValue(request.getNewMaterialPluNo(), Types.VARCHAR));
                                ub2.addUpdateValue("MATERIAL_UNIT", new DataValue(request.getNewMaterialUnit(), Types.VARCHAR));
                                ub2.addUpdateValue("MATERIAL_QTY", new DataValue(add, Types.VARCHAR));

                                this.addProcessData(new DataProcessBean(ub2));
                            }
                        }

                    }

                }
            }




        }



        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BomBulkUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BomBulkUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BomBulkUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_BomBulkUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_BomBulkUpdateReq> getRequestType() {
        return new TypeToken<DCP_BomBulkUpdateReq>() {
        };
    }

    @Override
    protected DCP_BomBulkUpdateRes getResponseType() {
        return new DCP_BomBulkUpdateRes();
    }
}

