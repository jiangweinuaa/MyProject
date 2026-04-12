package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataGenerateReq;
import com.dsc.spos.json.cust.req.DCP_StockInCreateReq;
import com.dsc.spos.json.cust.req.DCP_StockInUpdateReq;
import com.dsc.spos.json.cust.req.DCP_StockInUpdateReq.levelElm;
import com.dsc.spos.json.cust.req.DCP_StockInUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataGenerateRes;
import com.dsc.spos.json.cust.res.DCP_StockInUpdateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.ValidateUtils;
import com.google.gson.reflect.TypeToken;

public class DCP_StockInUpdate extends SPosAdvanceService<DCP_StockInUpdateReq, DCP_StockInUpdateRes> {
    
    @Override
    protected boolean isVerifyFail(DCP_StockInUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        levelElm request = req.getRequest();
        List<level1Elm> datas = request.getDatas();
        
        //必传值不为空
        String bDate = request.getbDate();
        String stockInNO = request.getStockInNo();
        String status = request.getStatus();
        String docType = request.getDocType();
        String oType = request.getoType();
        String ofNO = request.getOfNo();
        String warehouse = request.getWarehouse();
        String totPqty = request.getTotPqty();
        String totAmt = request.getTotAmt();
        String totDistriAmt=request.getTotDistriAmt();
        String totCqty = request.getTotCqty();
        String invWarehouse = request.getInvWarehouse();
        String employeeId = request.getEmployeeId();
        String departId = request.getDepartId();

        if (Check.Null(totPqty)) {
            errMsg.append("合计录入数量不可为空值, ");
            isFail = true;
        }
        if (Check.Null(totAmt)) {
            errMsg.append("合计录入金额不可为空值, ");
            isFail = true;
        }
        if (Check.Null(totDistriAmt)) {
            errMsg.append("合计进货金额可为空值, ");
            isFail = true;
        }
        if (Check.Null(totCqty)) {
            errMsg.append("合计品种数量不可为空值, ");
            isFail = true;
        }
        
        if(Check.Null(bDate)){
            errMsg.append("营业日期不可为空值, ");
            isFail = true;
        }
        
        if(Check.Null(stockInNO)){
            errMsg.append("收货单号不可为空值, ");
            isFail = true;
        }
        
        if(Check.Null(status)){
            errMsg.append("状态不可为空值, ");
            isFail = true;
        }
        
        if(Check.Null(docType)){
            errMsg.append("单据类型不可为空值, ");
            isFail = true;
        }
        
        //其他入库单不需要判断来源单信息
        if(!docType.equals("3"))
        {
            if(Check.Null(oType))
            {
                errMsg.append("来源单据类型不可为空值, ");
                isFail = true;
            }
            
            if(Check.Null(ofNO))
            {
                errMsg.append("来源单据单号不可为空值, ");
                isFail = true;
            }
            if(Check.Null(employeeId)){
                errMsg.append("员工编号不可为空值, ");
                isFail = true;
            }
            if(Check.Null(departId)){
                errMsg.append("部门编号不可为空值, ");
                isFail = true;
            }
            //if(Check.Null(invWarehouse)){
            //    errMsg.append("在途仓库不可为空值, ");
            //    isFail = true;
            //}

        }
        
        if (Check.Null(warehouse)) {
            errMsg.append("仓库不可为空值, ");
            isFail = true;
        }
        
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        for(level1Elm par : datas){
            
            String baseUnit =par.getBaseUnit();
            String baseQty = par.getBaseQty();
            String unitRatio = par.getUnitRatio();
            String pluNo = par.getPluNo();
            String expDate = par.getExpDate();

            if (Check.Null(par.getItem()))
            {
                errMsg.append("项次不可为空值, ");
                isFail = true;
            }
            
            if (Check.Null(par.getoItem()))
            {
                errMsg.append("来源项次不可为空值, ");
                isFail = true;
            }
            
            if (Check.Null(pluNo))
            {
                errMsg.append("商品编码不可为空值, ");
                isFail = true;
            }
            
            if (Check.Null(par.getPunit()))
            {
                errMsg.append("商品"+pluNo+"单位不可为空值, ");
                isFail = true;
            }
            
            if (Check.Null(par.getPqty()))
            {
                errMsg.append("商品"+pluNo+"收货量不可为空值, ");
                isFail = true;
            }
            
            if (Check.Null(par.getWarehouse()))
            {
                errMsg.append("商品"+pluNo+"仓库不可为空值, ");
                isFail = true;
            }
            if (baseUnit == null) {
                errMsg.append("商品"+pluNo+"基本单位不可为空值, ");
                isFail = true;
            }
            if (baseQty == null) {
                errMsg.append("商品"+pluNo+"基本数量不可为空值, ");
                isFail = true;
            }
            if (unitRatio == null) {
                errMsg.append("商品"+pluNo+"单位转换率不可为空值, ");
                isFail = true;
            }
            //if(Check.Null(expDate)){
                //errMsg.append("有效日期不可为空值, ");
                //isFail = true;
            //}
            
            if (isFail)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
        }
        
        return false;
    }
    
    
    @Override
    protected void processDUID(DCP_StockInUpdateReq req, DCP_StockInUpdateRes res) throws Exception {
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        levelElm request = req.getRequest();
        String bDate = request.getbDate();
        String modifyBy = req.getOpNO();
        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String modifyDate = df.format(cal.getTime());
        df = new SimpleDateFormat("HHmmss");
        String modifyTime = df.format(cal.getTime());
        String memo = request.getMemo();
        String stockInNO = request.getStockInNo();
        String status = request.getStatus();
        String oType = request.getoType();
        String ofNO = request.getOfNo();
        String bsNO = request.getBsNo();
        String warehouse = request.getWarehouse();
        String docType = request.getDocType();
        
        String totPqty = request.getTotPqty();
        String totAmt = request.getTotAmt();
        String totDistriAmt=request.getTotDistriAmt();
        String totCqty = request.getTotCqty();

        String invWarehouse = request.getInvWarehouse();
        String employeeId = request.getEmployeeId();
        String departId = request.getDepartId();
        String ooType = request.getOoType();
        String oofNo = request.getOofNo();
        String transferWarehouse = request.getTransferWarehouse();

        //1.保存提示"有效日期不可为空！"，校验：商品启用效期管理DCP_GOODS.ISSHELFLIFECHECK=Y&商品启用批号管理DCP_GOODS.ISBATCH=Y&组织参数Is_BatchNO=Y时，生产日期、有效日期不可为空！
        String isBatchPara = PosPub.getPARA_SMS(dao, req.geteId(), "", "Is_BatchNO");
        if (Check.Null(isBatchPara) || !isBatchPara.equals("Y")){
            isBatchPara="N";
        }
        if("Y".equals(isBatchPara)) {
            MyCommon cm = new MyCommon();
            StringBuffer sJoinPluNo = new StringBuffer("");
            for (DCP_StockInUpdateReq.level1Elm level1Elm : request.getDatas()) {
                String pluNo = level1Elm.getPluNo();
                sJoinPluNo.append(pluNo + ",");
            }
            Map<String, String> mapPlu = new HashMap<String, String>();
            mapPlu.put("PLUNO", sJoinPluNo.toString());

            String withasSql_plu = "";
            withasSql_plu = cm.getFormatSourceMultiColWith(mapPlu);
            mapPlu = null;

            if (withasSql_plu.equals("")) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找品号失败!");
            }
            StringBuilder sb = new StringBuilder();

            sb.append("with p AS ( " + withasSql_plu + ") " +
                    " select * from dcp_goods a " +
                    " inner join p on p.pluno=a.pluno " +
                    "  ")
            ;
            List<Map<String, Object>> getPluData = this.doQueryData(sb.toString(), null);

            if (getPluData.size() == 0) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找品号失败!");
            }

            for (DCP_StockInUpdateReq.level1Elm level1Elm : request.getDatas()) {
                String pluNo = level1Elm.getPluNo();
                List<Map<String, Object>> plus = getPluData.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo)).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(plus)) {
                    String isshelflifecheck = plus.get(0).get("ISSHELFLIFECHECK").toString();
                    String isbatch = plus.get(0).get("ISBATCH").toString();
                    if("Y".equals(isshelflifecheck)&&"Y".equals(isbatch)){
                        if(Check.Null(level1Elm.getProdDate())||Check.Null(level1Elm.getExpDate())){
                            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号"+pluNo+"生产日期、有效日期不可为空！");
                        }
                    }
                }
            }
        }

        String corp=req.getRequest().getCorp();//收货法人
        String deliveryCorp=req.getRequest().getDeliveryCorp();//发货法人

        if(Check.Null(corp)){
            String orgSql1="select * from dcp_org where eid='"+req.geteId()+"' and organizationno='"+req.getOrganizationNO()+"' ";
            List<Map<String, Object>> orgList1 = this.doQueryData(orgSql1, null);
            if(CollUtil.isNotEmpty(orgList1)){
                corp = orgList1.get(0).get("CORP").toString();
            }
            req.getRequest().setCorp(corp);
        }

        if(Check.Null(deliveryCorp)){
            String orgSql2="select * from dcp_org where eid='"+req.geteId()+"' and organizationno='"+request.getTransferShop()+"' ";
            List<Map<String, Object>> orgList2 = this.doQueryData(orgSql2, null);
            if(CollUtil.isNotEmpty(orgList2)){
                deliveryCorp = orgList2.get(0).get("CORP").toString();
            }
            req.getRequest().setDeliveryCorp(deliveryCorp);
        }



        if("5".equals(ooType)){
            String outSql="select * from dcp_stockout_detail a where a.eid='"+eId+"' " +
                    " and a.organizationno='"+organizationNO+"' and a.stockoutno='"+oofNo+"' " ;
            List<Map<String, Object>> stockOutList = this.doQueryData(outSql, null);
            for (Map<String, Object> map : stockOutList){
                String item = map.get("ITEM").toString();
                List<DCP_StockInUpdateReq.level1Elm> collect =request.getDatas().stream()
                        .filter(x -> x.getOriginItem().equals(item) && x.getOriginNo().equals(oofNo)).collect(Collectors.toList());
                if(CollUtil.isEmpty(collect)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"收货明细与出库明细不一致，请检查！");
                }else{
                    BigDecimal pqtySum=new BigDecimal(0);
                    for (DCP_StockInUpdateReq.level1Elm level1Elm : collect){
                        pqtySum = pqtySum.add(new BigDecimal(level1Elm.getPqty()));
                    }
                    BigDecimal outPqty = new BigDecimal(map.get("PQTY").toString());
                    if(pqtySum.compareTo(outPqty)!=0){
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"收货明细与出库明细["+item+"]不一致，请检查！");
                    }
                }
            }

            //人员资料是否有效
            if(!Check.Null(employeeId)&&!ValidateUtils.isEmployeeExist(dao,eId,employeeId)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"人员资料无效，请检查！");
            }
            //入库部门归属组织是否属于当前组织！
            if(!Check.Null(departId)&&!ValidateUtils.isDepartExist(dao,eId,departId,organizationNO)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"入库部门归属组织不属于当前组织，请检查！");
            }

            //-收货仓库是否属于当前收货组织，且资料有效！
            if(!ValidateUtils.isWarehouseExist(dao,eId,warehouse,organizationNO)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"收货仓库不属于当前组织，请检查！");
            }

            //检查单价、数量、金额等式是否相等
            for (DCP_StockInUpdateReq.level1Elm level1Elm : request.getDatas()){
                BigDecimal price = new BigDecimal(level1Elm.getPrice());
                BigDecimal amt = new BigDecimal(level1Elm.getAmt());
                BigDecimal distriPrice = new BigDecimal(level1Elm.getDistriPrice());
                BigDecimal distriAmt = new BigDecimal(level1Elm.getDistriAmt());
                BigDecimal pQty = new BigDecimal(level1Elm.getPqty());
                BigDecimal checkAmt = price.multiply(pQty).setScale(2, BigDecimal.ROUND_HALF_UP);
                BigDecimal checkDistriAmt = distriPrice.multiply(pQty).setScale(2, BigDecimal.ROUND_HALF_UP);
                if(amt.compareTo(checkAmt)!=0){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"零售金额不等，请检查！");
                }
                if(distriAmt.compareTo(checkDistriAmt)!=0){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"进货金额不等，请检查！");
                }
            }
        }



        //增加判断，单据已经审核就不能修改，单据不存在也不能修改
        try
        {
            if (checkExist(req))
            {
                //删除原有单身DCP_stockin_detail
                DelBean db1 = new DelBean("DCP_STOCKIN_DETAIL");
                db1.addCondition("stockInNO", new DataValue(stockInNO, Types.VARCHAR));
                db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));
                
                
                //新增新的单身（多条记录）
                List<level1Elm> datas = request.getDatas();
                for (level1Elm par : datas) {
                    int insColCt = 0;
                    String[] columnsName = {
                            "STOCKINNO", "SHOPID", "item", "oItem", "pluNO",
                            "punit", "pqty", "BASEUNIT", "BASEQTY", "unit_Ratio",
                            "price", "amt", "EID", "organizationNO",
                            "PLU_BARCODE","RECEIVING_QTY", "POQTY","WAREHOUSE",
                            "PLU_MEMO","BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO",
                            "PACKINGNO", "OTYPE", "OFNO", "OOTYPE", "OOFNO", "OOITEM","EXPDATE","ORIGINNO","ORIGINITEM","TRANSFER_BATCHNO","MES_LOCATION","BSNO"
                    };
                    DataValue[] columnsVal = new DataValue[columnsName.length];
                    
                    for (int i = 0; i < columnsVal.length; i++) {
                        String keyVal = null;
                        switch (i) {
                            case 0:
                                keyVal = stockInNO;
                                break;
                            case 1:
                                keyVal = shopId;
                                break;
                            case 2:
                                keyVal = par.getItem(); //item
                                break;
                            case 3:
                                keyVal = par.getoItem(); //oItem
                                break;
                            case 4:
                                keyVal = par.getPluNo(); //pluNO
                                break;
                            case 5:
                                keyVal = par.getPunit(); //punit
                                break;
                            case 6:
                                keyVal = par.getPqty(); //pqty
                                break;
                            case 7:
                                keyVal = par.getBaseUnit();     //wunit
                                break;
                            case 8:
                                keyVal = par.getBaseQty();     //wqty
                                break;
                            case 9:
                                keyVal = par.getUnitRatio();     //unitRatio
                                break;
                            case 10:
                                keyVal = par.getPrice();    //price
                                break;
                            case 11:
                                keyVal = par.getAmt();    //amt
                                break;
                            case 12:
                                keyVal = eId;
                                break;
                            case 13:
                                keyVal = organizationNO;
                                break;
                            case 14:
                                keyVal = par.getPluBarcode();      //pluBarcode
                                break;
                            case 15:
                                keyVal = par.getReceivingQty();    //receivingqty
                                break;
                            case 16:
                                keyVal = par.getPoQty();//poQty
                                break;
                            case 17:
                                keyVal = par.getWarehouse();
                                break;
                            case 18:
                                if(par.getPluMemo()==null){
                                    keyVal = "";
                                }
                                else{
                                    keyVal = par.getPluMemo();
                                }
                                break;
                            case 19:
                                keyVal = par.getBatchNo();
                                break;
                            case 20:
                                keyVal = par.getProdDate();
                                break;
                            case 21:
                                if(par.getDistriPrice()==null || Check.Null(par.getDistriPrice()) ){
                                    keyVal = "0";
                                }
                                else {
                                    keyVal=par.getDistriPrice();
                                }
                                break;
                            case 22:
                                keyVal = par.getDistriAmt(); //
                                if (Check.Null(keyVal))
                                    keyVal="0";
                                break;
                            case 23:
                                keyVal = bDate;
                                break;
                            case 24:
                                keyVal = par.getFeatureNo();
                                if (Check.Null(keyVal))
                                    keyVal=" ";
                                break;
                            case 25:
                                keyVal = par.getPackingNo();
                                break;
                            case 26:
                                keyVal = par.getoType();   //"OTYPE"
                                break;
                            case 27:
                                keyVal = par.getOfNo();    //"OFNO"
                                break;
                            case 28:
                                keyVal = par.getOoType();  //"OOTYPE"
                                break;
                            case 29:
                                keyVal = par.getOofNo();  //"OOFNO"
                                break;
                            case 30:
                                keyVal =par.getOoItem();  //"OOITEM"
                                break;
                            case 31:
                                keyVal =par.getExpDate();  //""
                                break;
                            case 32:
                                keyVal =par.getOriginNo();  //""
                                break;
                            case 33:
                                keyVal =par.getOriginItem();  //""
                                break;
                            case 34:
                                keyVal =par.getTransferBatchNo();  //""
                                break;
                            case 35:
                                keyVal =par.getLocation();  //""
                                if(Check.Null(keyVal)){
                                    keyVal=" ";
                                }
                                break;

                            case 36:
                                keyVal =par.getBsNo();  //""
                                break;
                            default:
                                break;
                        }
                        
                        if (keyVal != null)
                        {
                            insColCt++;
                            if (i == 2 || i == 3){
                                columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
                            }else if (i == 6 || i == 8 || i == 9 || i == 10 || i == 11 || i == 15 || i == 16 || i == 21){
                                columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
                            }else{
                                columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
                            }
                        }
                        else
                        {
                            columnsVal[i] = null;
                        }
                    }
                    
                    String[] columns2  = new String[insColCt];
                    DataValue[] insValue2 = new DataValue[insColCt];
                    
                    insColCt = 0;
                    for (int i = 0; i < columnsVal.length; i++){
                        if (columnsVal[i] != null){
                            columns2[insColCt] = columnsName[i];
                            insValue2[insColCt] = columnsVal[i];
                            insColCt ++;
                            if (insColCt >= insValue2.length) break;
                        }
                    }
                    InsBean ib2 = new InsBean("DCP_STOCKIN_DETAIL", columns2);
                    ib2.addValues(insValue2);
                    this.addProcessData(new DataProcessBean(ib2));//增加单身
                    
                    // 更新关联通知单单身 by jinzma 20210422 【ID1017036】【3.0货郎】按商品行收货（DCP_StockInUpdate（收货入库单修改））
                    if (docType.equals("0") && !Check.Null(ofNO)) {
                        UptBean ub = new UptBean("DCP_RECEIVING_DETAIL");
                        // add value
                        ub.addUpdateValue("STOCKIN_QTY", new DataValue(par.getPqty(), Types.VARCHAR));
                        ub.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
                        // condition
                        ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ub.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                        ub.addCondition("RECEIVINGNO", new DataValue(ofNO, Types.VARCHAR));
                        ub.addCondition("ITEM", new DataValue(par.getoItem(), Types.VARCHAR));
                        ub.addCondition("PLUNO", new DataValue(par.getPluNo(), Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub));
                    }
                }
                
                UptBean ub1 = new UptBean("DCP_STOCKIN");
                //add Value
                ub1.addUpdateValue("MODIFY_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                ub1.addUpdateValue("bDate", new DataValue(bDate, Types.VARCHAR));
                ub1.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
                ub1.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
                ub1.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
                ub1.addUpdateValue("TOT_PQTY", new DataValue(totPqty, Types.VARCHAR));
                ub1.addUpdateValue("TOT_AMT", new DataValue(totAmt, Types.VARCHAR));
                ub1.addUpdateValue("TOT_CQTY", new DataValue(totCqty, Types.VARCHAR));
                ub1.addUpdateValue("TOT_DISTRIAMT", new DataValue(totDistriAmt, Types.VARCHAR));
                ub1.addUpdateValue("memo", new DataValue(memo, Types.VARCHAR));
                ub1.addUpdateValue("status", new DataValue(status, Types.VARCHAR));
                ub1.addUpdateValue("oType", new DataValue(oType, Types.VARCHAR));
                ub1.addUpdateValue("ofNO", new DataValue(ofNO, Types.VARCHAR));
                ub1.addUpdateValue("bsNO", new DataValue(bsNO, Types.VARCHAR));
                ub1.addUpdateValue("warehouse", new DataValue(warehouse, Types.VARCHAR));
                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                ub1.addUpdateValue("EMPLOYEEID", new DataValue(employeeId, Types.VARCHAR));
                ub1.addUpdateValue("DEPARTID", new DataValue(departId, Types.VARCHAR));
                ub1.addUpdateValue("INVWAREHOUSE", new DataValue(invWarehouse, Types.VARCHAR));
                ub1.addUpdateValue("OOTYPE", new DataValue(ooType, Types.VARCHAR));
                ub1.addUpdateValue("OOFNO", new DataValue(oofNo, Types.VARCHAR));
                ub1.addUpdateValue("TRANSFER_WAREHOUSE", new DataValue(transferWarehouse, Types.VARCHAR));

                ub1.addUpdateValue("CORP", new DataValue(corp, Types.VARCHAR));
                ub1.addUpdateValue("DELIVERYCORP", new DataValue(deliveryCorp, Types.VARCHAR));


                //condition
                ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("stockInNO", new DataValue(stockInNO, Types.VARCHAR));
                ub1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));
                
                this.doExecuteDataToDB();

                if(!req.getRequest().getCorp().equals(req.getRequest().getDeliveryCorp())){
                    if("0".equals(req.getRequest().getDocType())){
                        DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                        inReq.setServiceId("DCP_InterSettleDataGenerate");
                        inReq.setToken(req.getToken());
                        DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                        request1.setOrganizationNo(req.getOrganizationNO());
                        request1.setBillNo(stockInNO);
                        request1.setSupplyOrgNo(req.getRequest().getTransferShop());
                        request1.setReturnSupplyPrice("Y");
                        request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType10004.getType());
                        request1.setDetail(new ArrayList<>());
                        for (DCP_StockInUpdateReq.level1Elm par : req.getRequest().getDatas()) {
                            DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                            detail.setReceiveOrgNo(req.getOrganizationNO());
                            detail.setSourceBillNo(par.getOofNo());
                            detail.setSourceItem(par.getOoItem());
                            detail.setItem(String.valueOf(par.getItem()));
                            detail.setPluNo(par.getPluNo());
                            detail.setFeatureNo(par.getFeatureNo());
                            detail.setPUnit(par.getPunit());
                            detail.setPQty(String.valueOf(par.getPqty()));
                            detail.setReceivePrice(par.getDistriPrice());
                            detail.setReceiveAmt(par.getDistriAmt());
                            detail.setSupplyPrice("");
                            detail.setSupplyAmt("");
                            request1.getDetail().add(detail);
                        }
                        inReq.setRequest(request1);
                        ParseJson pj = new ParseJson();
                        String jsontemp = pj.beanToJson(inReq);

                        DispatchService ds = DispatchService.getInstance();
                        String resXml = ds.callService(jsontemp, StaticInfo.dao);
                        DCP_InterSettleDataGenerateRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_InterSettleDataGenerateRes>() {
                        });
                        if (resserver.isSuccess() == false) {
                            res.setSuccess(true);
                            res.setServiceStatus("000");
                            res.setServiceDescription("内部结算失败！");
                            return;
                            //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "内部结算失败！");
                        }
                    }

                    if("5".equals(req.getRequest().getDocType())){
                        DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                        inReq.setServiceId("DCP_InterSettleDataGenerate");
                        inReq.setToken(req.getToken());
                        DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                        request1.setOrganizationNo(req.getOrganizationNO());
                        request1.setBillNo(stockInNO);
                        request1.setSupplyOrgNo(req.getOrganizationNO());
                        request1.setReturnSupplyPrice("Y");
                        request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType_10004.getType());
                        request1.setDetail(new ArrayList<>());
                        for (DCP_StockInUpdateReq.level1Elm par : req.getRequest().getDatas()) {
                            DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                            detail.setReceiveOrgNo(req.getRequest().getTransferShop());
                            detail.setSourceBillNo(par.getOofNo());
                            detail.setSourceItem(par.getOoItem());
                            detail.setItem(String.valueOf(par.getItem()));
                            detail.setPluNo(par.getPluNo());
                            detail.setFeatureNo(par.getFeatureNo());
                            detail.setPUnit(par.getPunit());
                            detail.setPQty(String.valueOf(par.getPqty()));
                            detail.setReceivePrice(par.getDistriPrice());
                            detail.setReceiveAmt(par.getDistriAmt());
                            detail.setSupplyPrice("");
                            detail.setSupplyAmt("");
                            request1.getDetail().add(detail);
                        }
                        inReq.setRequest(request1);
                        ParseJson pj = new ParseJson();
                        String jsontemp = pj.beanToJson(inReq);

                        DispatchService ds = DispatchService.getInstance();
                        String resXml = ds.callService(jsontemp, StaticInfo.dao);
                        DCP_InterSettleDataGenerateRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_InterSettleDataGenerateRes>() {
                        });
                        if (resserver.isSuccess() == false) {
                            res.setSuccess(true);
                            res.setServiceStatus("000");
                            res.setServiceDescription("内部结算失败！");
                            return;
                            //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "内部结算失败！");
                        }
                    }

                    if("1".equals(req.getRequest().getDocType())){
                        DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                        inReq.setServiceId("DCP_InterSettleDataGenerate");
                        inReq.setToken(req.getToken());
                        DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                        request1.setOrganizationNo(req.getOrganizationNO());
                        request1.setBillNo(stockInNO);
                        request1.setSupplyOrgNo(null);
                        request1.setReturnSupplyPrice("Y");
                        request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType10006.getType());
                        request1.setDetail(new ArrayList<>());
                        for (DCP_StockInUpdateReq.level1Elm par : req.getRequest().getDatas()) {
                            DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                            detail.setReceiveOrgNo(req.getRequest().getTransferShop());
                            detail.setSourceBillNo(par.getOofNo());
                            detail.setSourceItem(par.getOoItem());
                            detail.setItem(String.valueOf(par.getItem()));
                            detail.setPluNo(par.getPluNo());
                            detail.setFeatureNo(par.getFeatureNo());
                            detail.setPUnit(par.getPunit());
                            detail.setPQty(String.valueOf(par.getPqty()));
                            detail.setReceivePrice(par.getDistriPrice());
                            detail.setReceiveAmt(par.getDistriAmt());
                            detail.setSupplyPrice("");
                            detail.setSupplyAmt("");
                            request1.getDetail().add(detail);

                            if(!req.getOrganizationNO().equals(req.getRequest().getTransferShop())){
                                detail = inReq.new Detail();
                                detail.setReceiveOrgNo(req.getOrganizationNO());
                                detail.setSourceBillNo(par.getOofNo());
                                detail.setSourceItem(par.getOoItem());
                                detail.setItem(String.valueOf(par.getItem()));
                                detail.setPluNo(par.getPluNo());
                                detail.setFeatureNo(par.getFeatureNo());
                                detail.setPUnit(par.getPunit());
                                detail.setPQty(String.valueOf(par.getPqty()));
                                detail.setReceivePrice(par.getDistriPrice());
                                detail.setReceiveAmt(par.getDistriAmt());
                                detail.setSupplyPrice("");
                                detail.setSupplyAmt("");
                                request1.getDetail().add(detail);
                            }

                        }
                        inReq.setRequest(request1);
                        ParseJson pj = new ParseJson();
                        String jsontemp = pj.beanToJson(inReq);

                        DispatchService ds = DispatchService.getInstance();
                        String resXml = ds.callService(jsontemp, StaticInfo.dao);
                        DCP_InterSettleDataGenerateRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_InterSettleDataGenerateRes>() {
                        });
                        if (resserver.isSuccess() == false) {
                            res.setSuccess(true);
                            res.setServiceStatus("000");
                            res.setServiceDescription("内部结算失败！");
                            return;
                            //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "内部结算失败！");
                        }
                    }

                }


                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
                
            }
            else
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已提交，请重新输入！");
            }
        }
        catch (Exception e)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_StockInUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockInUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockInUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected TypeToken<DCP_StockInUpdateReq> getRequestType() {
        return new TypeToken<DCP_StockInUpdateReq>(){};
    }
    
    @Override
    protected DCP_StockInUpdateRes getResponseType() {
        return new DCP_StockInUpdateRes();
    }
    
    private boolean checkExist(DCP_StockInUpdateReq req)  throws Exception  {
        boolean exist = false;
        String eId = req.geteId();
        String shopId = req.getShopId();
        levelElm request = req.getRequest();
        String stockInNO =request.getStockInNo();
        String[] conditionValues = { eId,shopId,stockInNO };
        String sql = " select * from DCP_StockIn  where EID=?  and SHOPID=? and StockinNo=? and  Status='0'  " ;
        List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
        if (getQData != null && !getQData.isEmpty()) {
            exist = true;
        }
        return exist;
    }
    
}
