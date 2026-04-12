package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BomCreateReq;
import com.dsc.spos.json.cust.req.DCP_BomProcessInitReq;
import com.dsc.spos.json.cust.req.DCP_BomUpdateReq;
import com.dsc.spos.json.cust.res.DCP_BomUpdateRes;
import com.dsc.spos.json.cust.res.DCP_ReceivingStatusUpdateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_BomUpdate extends SPosAdvanceService<DCP_BomUpdateReq, DCP_BomUpdateRes> {
    @Override
    protected void processDUID(DCP_BomUpdateReq req, DCP_BomUpdateRes res) throws Exception {
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String eId = req.geteId();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_BomUpdateReq.LevelRequest request = req.getRequest();
        String bomType = request.getBomType();
        String restrictShop = request.getRestrictShop();
        String pluNo = request.getPluNo();

        String bomNo=request.getBomNo();
        if(Check.Null(request.getMulQty())){
            request.setMulQty("1");
        }
        if(Check.Null(request.getVersionNum())){
            request.setVersionNum("1");
        }else{
            //改成让前端传
            //request.setVersionNum(Integer.parseInt(request.getVersionNum())+1+"");
        }

        if(CollUtil.isNotEmpty(request.getMaterialList())) {
            for (DCP_BomUpdateReq.MaterialList materialRequest : request.getMaterialList()) {
                if(materialRequest.getMaterialPluNo().equals(request.getPluNo())){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "子件与主件不能相同");
                }
            }
        }

        //查询原来的versionNum
        String bomSql="select * from DCP_BOM a where a.eid='"+eId+"' and a.bomno='"+bomNo+"' ";
        List<Map<String, Object>> bomList = this.doQueryData( bomSql, null);
        if(CollUtil.isEmpty(bomList)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "BOM不存在");
        }

        //同一个品（主件/子件）不允许同时存在【原料】和【产出物】这两种关系。
        //if(!"0".equals(request.getBomType())) {//配方可以存在
        //    String valSql1 = "select * from dcp_bom where eid='" + req.geteId() + "' and bomno!='" + req.getRequest().getBomNo() + "' and bomtype='"+bomType+"' ";
         //   List<Map<String, Object>> valList1 = this.doQueryData(valSql1, null);
         //   String valSql2 = "select a.* from dcp_bom_material a " +
          //          " left join dcp_bom b on a.eid=b.eid and a.bomno=b.bomno  where a.eid='" + req.geteId() + "'  and a.bomno!='" + req.getRequest().getBomNo() + "' and b.bomtype='"+bomType+"' ";
         //   List<Map<String, Object>> valList2 = this.doQueryData(valSql2, null);

         //   List<Map<String, Object>> valList2Filter = valList2.stream().filter(x -> x.get("MATERIAL_PLUNO").equals(pluNo)).collect(Collectors.toList());
         //   if (valList2Filter.size() > 0) {
         //       throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号"+pluNo+":同一个品（主件/子件）不允许同时存在【原料】和【产出物】这两种关系");
         //   }

         //   if (CollUtil.isNotEmpty(request.getMaterialList())) {
         //       for (DCP_BomUpdateReq.MaterialList materialRequest : request.getMaterialList()) {
        //            List<Map<String, Object>> valList1Filter = valList1.stream().filter(x -> x.get("PLUNO").toString().equals(materialRequest.getMaterialPluNo())).collect(Collectors.toList());
        //            if (valList1Filter.size() > 0) {
        //                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号"+materialRequest.getMaterialPluNo()+":同一个品（主件/子件）不允许同时存在【原料】和【产出物】这两种关系");
         //           }
        //        }
       //     }
      //  }

        if(req.getRequest().getProdType().equals("0")){
            //当前成品
            //往下找原料
            //先把自己加上
            List<String> path =new ArrayList<>();
            path.add(pluNo);

            for (DCP_BomUpdateReq.MaterialList materialRequest : request.getMaterialList()){
                List<String> newPath = new ArrayList<>(path);
                newPath.add(materialRequest.getMaterialPluNo());
                traverseDown(req, materialRequest.getMaterialPluNo(),req.getRequest().getProdType(), newPath);
            }

            //往上找主件
            traverseUp(req, pluNo,req.getRequest().getProdType(), path);


        }
        else if(req.getRequest().getProdType().equals("1")){
            //当前原料
            List<String> path =new ArrayList<>();
            path.add(pluNo);
            traverseDown(req, pluNo,req.getRequest().getProdType(), path);

            for (DCP_BomUpdateReq.MaterialList materialRequest : request.getMaterialList()){
                List<String> newPath = new ArrayList<>(path);
                newPath.add(materialRequest.getMaterialPluNo());
                traverseUp(req, materialRequest.getMaterialPluNo(),req.getRequest().getProdType(), newPath);
            }

        }



        //拆解型BOM（prodType="1.子件"）保存增加子件数据校验：子件成本分摊比例不可为0%，且合计比例为100%
        if("1".equals(request.getProdType())){
            BigDecimal costRateAll=new BigDecimal(0);
            for (DCP_BomUpdateReq.MaterialList materialRequest : request.getMaterialList()){
                if(Check.Null(materialRequest.getCostRate())){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "子件成本分摊比例不可为0%");
                }
                BigDecimal costRateDecimal = new BigDecimal(materialRequest.getCostRate());
                if(costRateDecimal.compareTo(new BigDecimal("0"))==0){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "子件成本分摊比例不可为0%");
                }
                costRateAll=costRateAll.add(costRateDecimal);
            }
            if(costRateAll.compareTo(new BigDecimal("100"))!=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "子件成本分摊比例合计必须为100%");
            }
        }

        String oldVersionNum = bomList.get(0).get("VERSIONNUM").toString();


        DelBean db2 = new DelBean("DCP_BOM_MATERIAL");
        db2.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        DelBean db3 = new DelBean("DCP_BOM_RANGE");
        db3.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
        db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db3));

        DelBean db5 = new DelBean("DCP_BOM_MATERIAL_R");
        db5.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
        db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db5));

        //当isProcessEnable改为Y时，MES配方工序信息无需删除；当isProcessEnable改为N时，检查DCP_BOM.PROCESSSTATUS是否为Y，如果为Y，提示已维护配方工序信息，不可关闭启用工序；

        if(!"Y".equals(req.getRequest().getIsProcessEnable())){

            if("Y".equals(bomList.get(0).get("PROCESSSTATUS"))){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "已维护配方工序信息，不可关闭启用工序");
            }

            DelBean db6 = new DelBean("MES_BOM_PROCESS");
            db6.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
            db6.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db6.addCondition("PROCESSNO", new DataValue("#01", Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db6));

            DelBean db7 = new DelBean("MES_BOM_SUBPROCESS");
            db7.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
            db7.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db7.addCondition("PROCESSNO", new DataValue("#01", Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db7));

            DelBean db8 = new DelBean("MES_BOM_SUBPROCESS_MATERIAL");
            db8.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
            db8.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db8.addCondition("PROCESSNO", new DataValue("#01", Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db8));
        }

        DelBean db9 = new DelBean("DCP_BOM_COBYPRODUCT");
        db9.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
        db9.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db9));


        UptBean ub1 = null;
        ub1 = new UptBean("DCP_BOM");
        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));

        ub1.addUpdateValue("BOMTYPE", new DataValue(bomType, Types.VARCHAR));
        ub1.addUpdateValue("VERSIONNUM", new DataValue(request.getVersionNum(), Types.VARCHAR));
        ub1.addUpdateValue("PLUNO", new DataValue(pluNo, Types.VARCHAR));
        ub1.addUpdateValue("UNIT", new DataValue(request.getUnit(), Types.VARCHAR));
        ub1.addUpdateValue("MULQTY", new DataValue(request.getMulQty(), Types.VARCHAR));

        if(Check.NotNull(request.getEffDate())){
            Date effDate = new SimpleDateFormat("yyyyMMdd").parse(request.getEffDate());
            String formatDate = DateFormatUtils.format(effDate, "yyyy-MM-dd HH:mm:ss");
            ub1.addUpdateValue("EFFDATE", new DataValue(formatDate, Types.DATE));
        }
        ub1.addUpdateValue("MEMO", new DataValue(request.getMemo(), Types.VARCHAR));
        ub1.addUpdateValue("STATUS", new DataValue(request.getStatus(), Types.VARCHAR));
        ub1.addUpdateValue("BATCHQTY", new DataValue(request.getBatchQty(), Types.VARCHAR));
        ub1.addUpdateValue("REMAINTYPE", new DataValue(request.getRemainType(), Types.VARCHAR));
        ub1.addUpdateValue("CONTAINTYPE", new DataValue(request.getContainType(), Types.VARCHAR));
        ub1.addUpdateValue("PROCESSSTATUS", new DataValue(request.getProcessStatus(), Types.VARCHAR));
        ub1.addUpdateValue("RESTRICTSHOP", new DataValue(restrictShop, Types.VARCHAR));
        ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
        ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getEmployeeName(), Types.VARCHAR));
        ub1.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));
        ub1.addUpdateValue("PRODTYPE", new DataValue(request.getProdType(), Types.VARCHAR));
        ub1.addUpdateValue("PROCESS_STATUS",new DataValue("N", Types.VARCHAR));
        ub1.addUpdateValue("FIXEDLOSSQTY", new DataValue(request.getFixedLossQty(), Types.VARCHAR));
        ub1.addUpdateValue("ISPROCESSENABLE", new DataValue(request.getIsProcessEnable(), Types.VARCHAR));
        ub1.addUpdateValue("INWGROUPNO", new DataValue(request.getInWGroupNo(), Types.VARCHAR));

        ub1.addUpdateValue("MINQTY", new DataValue(request.getMinQty(), Types.VARCHAR));
        ub1.addUpdateValue("ODDVALUE", new DataValue(request.getOddValue(), Types.VARCHAR));
        ub1.addUpdateValue("PRODUCTEXCEED", new DataValue(request.getProductExceed(), Types.VARCHAR));
        ub1.addUpdateValue("PROCRATE", new DataValue(request.getProcRate(), Types.VARCHAR));
        ub1.addUpdateValue("DISPTYPE", new DataValue(request.getDispType(), Types.VARCHAR));
        ub1.addUpdateValue("SEMIWOTYPE", new DataValue(request.getSemiwoType(), Types.VARCHAR));
        ub1.addUpdateValue("SEMIWODEPTTYPE", new DataValue(request.getSemiwoDeptType(), Types.VARCHAR));
        ub1.addUpdateValue("FIXPREDAYS", new DataValue(request.getFixPreDays(), Types.VARCHAR));
        ub1.addUpdateValue("SDLABORTIME", new DataValue(request.getSdlaborTime(), Types.VARCHAR));
        ub1.addUpdateValue("SDMACHINETIME", new DataValue(request.getSdmachineTime(), Types.VARCHAR));
        ub1.addUpdateValue("STANDARDHOURS", new DataValue(request.getStandardHours(), Types.VARCHAR));
        ub1.addUpdateValue("ISCOBYPRODUCT",new DataValue(request.getIsCoByProduct(), Types.VARCHAR));


        this.addProcessData(new DataProcessBean(ub1)); // update

        if(!oldVersionNum.equals(request.getVersionNum())) {
            ColumnDataValue mainColumns = new ColumnDataValue();
            mainColumns.add("EID", DataValues.newString(eId));
            mainColumns.add("BOMNO", DataValues.newString(bomNo));
            mainColumns.add("BOMTYPE", DataValues.newString(bomType));
            mainColumns.add("PLUNO", DataValues.newString(pluNo));
            mainColumns.add("UNIT", DataValues.newString(request.getUnit()));
            mainColumns.add("MULQTY", DataValues.newString(request.getMulQty()));
            if (Check.NotNull(request.getEffDate())) {
                Date effDate = new SimpleDateFormat("yyyyMMdd").parse(request.getEffDate());
                String formatDate = DateFormatUtils.format(effDate, "yyyy-MM-dd HH:mm:ss");

                mainColumns.add("EFFDATE", new DataValue(formatDate, Types.DATE));
            }
            mainColumns.add("MEMO", DataValues.newString(request.getMemo()));
            mainColumns.add("STATUS", DataValues.newString(request.getStatus()));
            mainColumns.add("RESTRICTSHOP", DataValues.newString(restrictShop));
            mainColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));
            mainColumns.add("CREATEOPNAME", DataValues.newString(req.getEmployeeName()));
            mainColumns.add("CREATETIME", new DataValue(createTime, Types.DATE));
            mainColumns.add("LASTMODIOPID", DataValues.newString(req.getOpNO()));
            mainColumns.add("LASTMODIOPNAME", DataValues.newString(req.getEmployeeName()));
            mainColumns.add("LASTMODITIME", new DataValue(createTime, Types.DATE));
            mainColumns.add("CREATEDEPTID", DataValues.newString(departmentNo));
            mainColumns.add("PRODTYPE", DataValues.newString(request.getProdType()));
            mainColumns.add("BATCHQTY", DataValues.newInteger(request.getBatchQty()));
            mainColumns.add("VERSIONNUM", DataValues.newInteger(oldVersionNum));
            mainColumns.add("CONTAINTYPE", DataValues.newString(request.getContainType()));
            mainColumns.add("PROCESSSTATUS", DataValues.newString(request.getProcessStatus()));
            mainColumns.add("FIXEDLOSSQTY", DataValues.newString(request.getFixedLossQty()));
            mainColumns.add("ISPROCESSENABLE", DataValues.newString(request.getIsProcessEnable()));
            mainColumns.add("INWGROUPNO", DataValues.newString(request.getInWGroupNo()));

            mainColumns.add("REMAINTYPE",request.getRemainType(),Types.VARCHAR);
            mainColumns.add("MINQTY",request.getMinQty(),Types.VARCHAR);
            mainColumns.add("ODDVALUE",request.getOddValue(),Types.VARCHAR);
            mainColumns.add("PRODUCTEXCEED",request.getProductExceed(),Types.VARCHAR);
            mainColumns.add("PROCRATE",request.getProcRate(),Types.VARCHAR);
            mainColumns.add("DISPTYPE",request.getDispType(),Types.VARCHAR);
            mainColumns.add("SEMIWOTYPE",request.getSemiwoType(),Types.VARCHAR);
            mainColumns.add("SEMIWODEPTTYPE",request.getSemiwoDeptType(),Types.VARCHAR);
            mainColumns.add("FIXPREDAYS",request.getFixPreDays(),Types.VARCHAR);
            mainColumns.add("SDLABORTIME",request.getSdlaborTime(),Types.VARCHAR);
            mainColumns.add("SDMACHINETIME",request.getSdmachineTime(),Types.VARCHAR);
            mainColumns.add("STANDARDHOURS",request.getStandardHours(),Types.VARCHAR);
            mainColumns.add("ISCOBYPRODUCT",request.getIsCoByProduct(),Types.VARCHAR);


            String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
            DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);

            InsBean ib1v = new InsBean("DCP_BOM_V", mainColumnNames);
            ib1v.addValues(mainDataValues);
            this.addProcessData(new DataProcessBean(ib1v));
        }

        if(CollUtil.isNotEmpty(request.getMaterialList())){
            for (DCP_BomUpdateReq.MaterialList materialRequest : request.getMaterialList()) {

                ColumnDataValue materialColumns=new ColumnDataValue();
                materialColumns.add("EID", DataValues.newString(eId));
                materialColumns.add("BOMNO", DataValues.newString(bomNo));
                materialColumns.add("MATERIAL_PLUNO", DataValues.newString(materialRequest.getMaterialPluNo()));
                materialColumns.add("MATERIAL_UNIT", DataValues.newString(materialRequest.getMaterialUnit()));
                materialColumns.add("MATERIAL_QTY", DataValues.newInteger(materialRequest.getMaterialQty()));
                materialColumns.add("QTY", DataValues.newInteger(materialRequest.getQty()));
                materialColumns.add("LOSS_RATE", DataValues.newInteger(materialRequest.getLossRate()));
                materialColumns.add("ISBUCKLE", DataValues.newString(materialRequest.getIsBuckle()));
                materialColumns.add("ISREPLACE", DataValues.newString(materialRequest.getIsReplace()));
                //materialColumns.add("MATERIAL_BDATE", DataValues.newDate(materialRequest.getMaterialBDate()));
                //materialColumns.add("MATERIAL_EDATE", DataValues.newDate(materialRequest.getMaterialEDate()));

                if (!Check.Null(materialRequest.getMaterialBDate())) {
                    Date mbDate = new SimpleDateFormat("yyyy-MM-dd").parse(materialRequest.getMaterialBDate());
                    String formatDate =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(mbDate);
                    materialColumns.add("MATERIAL_BDATE",DataValues.newDate(formatDate));
                }
                if (!Check.Null(materialRequest.getMaterialEDate())) {
                    Date meDate = new SimpleDateFormat("yyyy-MM-dd").parse(materialRequest.getMaterialEDate());
                    String formatDate =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(meDate);
                    materialColumns.add("MATERIAL_EDATE",DataValues.newDate(formatDate));
                }

                materialColumns.add("SORTID", DataValues.newInteger(materialRequest.getSortId()));
                //materialColumns.add("TRAN_TIME", new DataValue(createTime, Types.DATE));
                materialColumns.add("LASTMODITIME", new DataValue(createTime, Types.DATE));
                materialColumns.add("COSTRATE", DataValues.newInteger(materialRequest.getCostRate()));
                materialColumns.add("ISPICK", DataValues.newString(materialRequest.getIsPick()));
                materialColumns.add("ISBATCH", DataValues.newString(materialRequest.getIsBatch()));
                materialColumns.add("PWGROUPNO", DataValues.newString(materialRequest.getPWGroupNo()));
                materialColumns.add("ISMIX", DataValues.newString(materialRequest.getIsMix()));
                materialColumns.add("MIXGROUP", DataValues.newString(materialRequest.getMixGroup()));
                materialColumns.add("KWGROUPNO", DataValues.newString(materialRequest.getKWGroupNo()));

                String[] materialColumnNames = materialColumns.getColumns().toArray(new String[0]);
                DataValue[] materialDataValues = materialColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib2=new InsBean("DCP_BOM_MATERIAL",materialColumnNames);
                ib2.addValues(materialDataValues);
                this.addProcessData(new DataProcessBean(ib2));
                if(!oldVersionNum.equals(request.getVersionNum())) {
                    materialColumns.add("VERSIONNUM", DataValues.newInteger(oldVersionNum));
                    String[] materialColumnNamesv = materialColumns.getColumns().toArray(new String[0]);
                    DataValue[] materialDataValuesv = materialColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean ib2v = new InsBean("DCP_BOM_MATERIAL_V", materialColumnNamesv);
                    ib2v.addValues(materialDataValuesv);
                    this.addProcessData(new DataProcessBean(ib2v));
                }

                List<DCP_BomUpdateReq.ReplaceList> replaceList = materialRequest.getReplaceList();
                if(CollUtil.isNotEmpty(replaceList)) {
                    for (DCP_BomUpdateReq.ReplaceList replaceRequest : replaceList) {
                        ColumnDataValue replaceColumns = new ColumnDataValue();
                        replaceColumns.add("EID", DataValues.newString(eId));
                        replaceColumns.add("BOMNO", DataValues.newString(bomNo));
                        replaceColumns.add("MATERIAL_PLUNO", DataValues.newString(materialRequest.getMaterialPluNo()));
                        replaceColumns.add("MATERIAL_QTY", DataValues.newString(materialRequest.getMaterialQty()));
                        replaceColumns.add("MATERIAL_UNIT", DataValues.newString(materialRequest.getMaterialUnit()));
                        replaceColumns.add("PRIORITY", DataValues.newString(replaceRequest.getPriority()));

                        replaceColumns.add("REPLACE_PLUNO", DataValues.newString(replaceRequest.getReplacePluNo()));
                        replaceColumns.add("REPLACE_QTY", DataValues.newString(replaceRequest.getReplaceQty()));
                        replaceColumns.add("REPLACE_UNIT", DataValues.newString(replaceRequest.getReplaceUnit()));
                        replaceColumns.add("REPLACE_BDATE", new DataValue(replaceRequest.getReplaceBDate(), Types.DATE));
                        replaceColumns.add("REPLACE_EDATE", new DataValue(replaceRequest.getReplaceEDate(), Types.DATE));
                        replaceColumns.add("LASTMODITIME", new DataValue(createTime, Types.DATE));

                        String[] replaceColumnNames = replaceColumns.getColumns().toArray(new String[0]);
                        DataValue[] replaceDataValues = replaceColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean ib3 = new InsBean("DCP_BOM_MATERIAL_R", replaceColumnNames);
                        ib3.addValues(replaceDataValues);
                        this.addProcessData(new DataProcessBean(ib3));
                    }
                }
            }
        }

        if(CollUtil.isNotEmpty(request.getRangeList())){
            for (DCP_BomUpdateReq.RangeList rangeRequest : request.getRangeList()) {
                ColumnDataValue rangeColumns=new ColumnDataValue();
                rangeColumns.add("EID", DataValues.newString(eId));
                rangeColumns.add("BOMNO", DataValues.newString(bomNo));
                rangeColumns.add("SHOPID", DataValues.newString(rangeRequest.getShopId()));
                rangeColumns.add("ORGANIZATIONNO", DataValues.newString(rangeRequest.getOrganizationNo()));
                rangeColumns.add("LASTMODITIME", new DataValue(createTime, Types.DATE));
                //rangeColumns.add("TRAN_TIME", DataValues.newString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));

                String[] rangeColumnNames = rangeColumns.getColumns().toArray(new String[0]);
                DataValue[] rangeDataValues = rangeColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib2=new InsBean("DCP_BOM_RANGE",rangeColumnNames);
                ib2.addValues(rangeDataValues);
                this.addProcessData(new DataProcessBean(ib2));
                if(!oldVersionNum.equals(request.getVersionNum())) {
                    rangeColumns.add("VERSIONNUM", DataValues.newString(oldVersionNum));
                    String[] rangeColumnNamesv = rangeColumns.getColumns().toArray(new String[0]);
                    DataValue[] rangeDataValuesv = rangeColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean ib2v = new InsBean("DCP_BOM_RANGE_V", rangeColumnNamesv);
                    ib2v.addValues(rangeDataValuesv);
                    this.addProcessData(new DataProcessBean(ib2v));
                }
            }
        }



        if(CollUtil.isNotEmpty(request.getCoByList())){
            for (DCP_BomUpdateReq.CoByList coByRequest : request.getCoByList()) {

                ColumnDataValue cobyColumns=new ColumnDataValue();
                cobyColumns.add("EID", DataValues.newString(eId));
                cobyColumns.add("BOMNO", DataValues.newString(bomNo));
                cobyColumns.add("PRODUCTTYPE", DataValues.newString(coByRequest.getProductType()));
                cobyColumns.add("PLUNO", DataValues.newString(coByRequest.getPluNo()));
                cobyColumns.add("COSTRATE", DataValues.newString(coByRequest.getCostRate()));
                cobyColumns.add("UNIT", DataValues.newString(coByRequest.getUnit()));

                cobyColumns.add("LASTMODITIME", new DataValue(createTime, Types.DATE));
                cobyColumns.add("TRAN_TIME", DataValues.newString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));

                String[] cobyColumnNames = cobyColumns.getColumns().toArray(new String[0]);
                DataValue[] cobyDataValues = cobyColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib2=new InsBean("DCP_BOM_COBYPRODUCT",cobyColumnNames);
                ib2.addValues(cobyDataValues);
                this.addProcessData(new DataProcessBean(ib2));

                if(!oldVersionNum.equals(request.getVersionNum())) {
                    cobyColumns.add("VERSIONNUM", DataValues.newString(oldVersionNum));
                    String[] cobyColumnNamesv = cobyColumns.getColumns().toArray(new String[0]);
                    DataValue[] cobyDataValuesv = cobyColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean ib2v = new InsBean("DCP_BOM_COBYPRODUCT_V", cobyColumnNamesv);
                    ib2v.addValues(cobyDataValuesv);
                    this.addProcessData(new DataProcessBean(ib2v));
                }
            }
        }



        this.doExecuteDataToDB();
        String bomAutoProcInit= PosPub.getPARA_SMS(dao, req.geteId(), "", "bomAutoProcInit");


        if(bomType.equals("0")&&"Y".equals(bomAutoProcInit)){

            //改成调用服务
            ParseJson pj = new ParseJson();
            DCP_BomProcessInitReq initReq=new DCP_BomProcessInitReq();
            initReq.setServiceId("DCP_BomProcessInit");
            initReq.setToken(req.getToken());
            DCP_BomProcessInitReq.LevelRequest request1 = initReq.new LevelRequest();
            request1.setBomList(new ArrayList<>());
            DCP_BomProcessInitReq.BomList bomList1 = initReq.new BomList();
            bomList1.setBomNo(bomNo);
            request1.getBomList().add(bomList1);
            initReq.setRequest(request1);

            String jsontemp= pj.beanToJson(initReq);

            //直接调用CRegisterDCP服务
            DispatchService ds = DispatchService.getInstance();
            String resXml = ds.callService(jsontemp, StaticInfo.dao);
            DCP_ReceivingStatusUpdateRes resserver=pj.jsonToBean(resXml, new TypeToken<DCP_ReceivingStatusUpdateRes>(){});
            if(resserver.isSuccess()==false)
            {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "DCP_BomProcessInit失败！");
            }



        }


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BomUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BomUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BomUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_BomUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_BomUpdateReq> getRequestType() {
        return new TypeToken<DCP_BomUpdateReq>() {

        };
    }

    @Override
    protected DCP_BomUpdateRes getResponseType() {
        return new DCP_BomUpdateRes();
    }

    @Override
    protected String getQuerySql(DCP_BomUpdateReq req) throws Exception {
        return null;
    }



    private Set<String> bomItems = new HashSet<>();
    private Set<String> materialItems = new HashSet<>();

    /**
     * 向下遍历原料（获取下级原料）
     * @param req 企业编号
     * @param pluno 当前品号
     * @param prodType 产出类型
     * @param path 当前路径
     * @throws Exception 循环或超限异常
     */
    private void traverseDown(DCP_BomUpdateReq req, String pluno, String prodType, List<String> path) throws Exception {
        if (path.size() > 10) {
            throw new Exception("配方阶层超出界限，请检查");
        }
        //这个只能判断成品 原料的

        if (materialItems.contains(pluno)) {
            return;
        }


        List<DCP_BomUpdateReq.BomItem> nextItems= getNextItems(req, pluno, prodType);

        for (DCP_BomUpdateReq.BomItem item : nextItems) {
            if (path.contains(item.getPluno())) {
                throw new Exception(item.getPluno() + "存在配方循环或组合关系循环，请检查");
            }
            List<String> newPath = new ArrayList<>(path);
            newPath.add(item.getPluno());
            traverseDown(req, item.getPluno(), item.getProdType(), newPath);
        }

        //向下找原料找到最后了

        materialItems.add(pluno);
    }

    /**
     * 向上遍历成品（追溯上级成品）
     * @param req 企业编号
     * @param pluno 当前品号
     * @param prodType 产出类型
     * @param path 当前路径
     * @throws Exception 循环或超限异常
     */
    private void traverseUp(DCP_BomUpdateReq req, String pluno, String prodType, List<String> path) throws Exception {
        if (path.size() > 10) {
            throw new Exception("配方阶层超出界限，请检查");
        }

        if(materialItems.contains(pluno)){
            throw new Exception(pluno + "存在配方循环或组合关系循环，请检查");
        }

        if (bomItems.contains(pluno)) {
            return;
        }

        List<DCP_BomUpdateReq.BomItem> nextItems = getPreviousItems(req, pluno, prodType);
        for (DCP_BomUpdateReq.BomItem item : nextItems) {
            if (path.contains(item.getPluno())) {
                throw new Exception(item.getPluno() + "存在配方循环或组合关系循环，请检查");
            }
            List<String> newPath = new ArrayList<>(path);
            newPath.add(item.getPluno());
            traverseUp(req, item.getPluno(), item.getProdType(), newPath);
        }

        //这边是一笔原料找到最后了

        bomItems.add(pluno);
    }

    /**
     * 获取下级原料
     * @param req 企业编号
     * @param pluno 当前品号
     * @param prodType 产出类型
     * @return 下级原料列表
     * @throws Exception 数据库异常
     */
    private List<DCP_BomUpdateReq.BomItem> getNextItems(DCP_BomUpdateReq req, String pluno, String prodType) throws Exception {
        List<DCP_BomUpdateReq.BomItem> items = new ArrayList<>();

        // prodType为0时，X为成品，X1,X2为原料，查询DCP_BOM_MATERIAL.MATERIAL_PLUNO
        // prodType为1时，X为原料，X1,X2为成品，查询DCP_BOM.PLUNO
        String query =" select b.material_pluno as pluno from dcp_bom a " +
                " inner join dcp_bom_material b on a.eid=b.eid and a.bomno=b.bomno " +
                " left join dcp_bom_range c on a.eid=c.eid and a.bomno=c.bomno " +
                " where a.eid='"+req.geteId()+"' and a.prodtype='0' and a.pluno='"+pluno+"' and (a.RESTRICTSHOP='0' or c.SHOPID='"+req.getOrganizationNO()+"') " +
                " union all (" +//子件
                " select a.pluno as pluno from dcp_bom a " +
                " inner join dcp_bom_material b on a.eid=b.eid and a.bomno=b.bomno " +
                " left join dcp_bom_range c on a.eid=c.eid and a.bomno=c.bomno " +
                " where a.eid='"+req.geteId()+"' and a.prodtype='1' and b.material_pluno='"+pluno+"' and (a.RESTRICTSHOP='0' or c.SHOPID='"+req.getOrganizationNO()+"') " +

                ")";
        List<Map<String, Object>> list = this.doQueryData(query, null);
        List<String> pluNoList = list.stream().map(x -> x.get("PLUNO").toString()).distinct().collect(Collectors.toList());
        for (String thisPluno : pluNoList) {
            DCP_BomUpdateReq.BomItem bomItem = req.new BomItem();
            bomItem.setPluno(thisPluno);

            items.add(bomItem);
        }
        return items;
    }

    /**
     * 获取上级成品
     * @param req 企业编号
     * @param pluno 当前品号
     * @param prodType 产出类型
     * @return 上级成品列表
     * @throws Exception 数据库异常
     */
    private List<DCP_BomUpdateReq.BomItem> getPreviousItems(DCP_BomUpdateReq req, String pluno, String prodType) throws Exception {
        List<DCP_BomUpdateReq.BomItem> items = new ArrayList<>();

        //往上找成品
        // prodType为0时，查询DCP_BOM.PLUNO（X为成品，查找包含X作为原料的配方）
        // prodType为1时，查询DCP_BOM_MATERIAL.MATERIAL_PLUNO（X为原料，查找X作为成品的配方）
        String query =" select b.material_pluno as pluno from dcp_bom a " +
                " inner join dcp_bom_material b on a.eid=b.eid and a.bomno=b.bomno " +
                " left join dcp_bom_range c on a.eid=c.eid and a.bomno=c.bomno " +
                " where a.eid='"+req.geteId()+"' and a.prodtype='1' and a.pluno='"+pluno+"' and (a.RESTRICTSHOP='0' or c.SHOPID='"+req.getOrganizationNO()+"') " +
                " union all (" +//子件
                " select a.pluno as pluno from dcp_bom a " +
                " inner join dcp_bom_material b on a.eid=b.eid and a.bomno=b.bomno " +
                " left join dcp_bom_range c on a.eid=c.eid and a.bomno=c.bomno " +
                " where a.eid='"+req.geteId()+"' and a.prodtype='0' and b.material_pluno='"+pluno+"' and (a.RESTRICTSHOP='0' or c.SHOPID='"+req.getOrganizationNO()+"') " +

                ")";
        List<Map<String, Object>> list = this.doQueryData(query, null);
        List<String> pluNoList = list.stream().map(x -> x.get("PLUNO").toString()).distinct().collect(Collectors.toList());
        for (String thisPluno : pluNoList) {
            DCP_BomUpdateReq.BomItem bomItem = req.new BomItem();
            bomItem.setPluno(thisPluno);

            items.add(bomItem);
        }
        return items;
    }



}
