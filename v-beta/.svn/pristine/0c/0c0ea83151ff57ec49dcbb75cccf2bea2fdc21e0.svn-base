package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PStockOutStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PStockOutStatusUpdateRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.webhook.WebHookService;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.bc.BcReq;
import com.dsc.spos.utils.bc.BcRes;
import com.google.gson.reflect.TypeToken;
import org.hibernate.validator.constraints.NotBlank;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_PStockOutStatusUpdate  extends SPosAdvanceService<DCP_PStockOutStatusUpdateReq, DCP_PStockOutStatusUpdateRes>
{
    @Override
    protected void processDUID(DCP_PStockOutStatusUpdateReq req, DCP_PStockOutStatusUpdateRes res) throws Exception
    {
        String isBatchNo=PosPub.getPARA_SMS(dao, req.geteId(), req.getShopId(), "Is_BatchNO");

        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String confirmBy = req.getEmployeeNo();
        String accountBy = req.getEmployeeNo();
        String submitBy = req.getEmployeeNo();
        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String confirmDate = df.format(cal.getTime());
        //String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        String completeDate = df.format(cal.getTime());
        String submitDate = df.format(cal.getTime());
        df = new SimpleDateFormat("HHmmss");
        String confirmTime = df.format(cal.getTime());
        String accountTime = df.format(cal.getTime());
        String submitTime = df.format(cal.getTime());
        String pStockInNO = req.getRequest().getPStockInNo();
        String companyId  = req.getBELFIRM();
        String docTypeR = req.getRequest().getDocType();
        String opType = req.getRequest().getOpType();

        if("2".equals(docTypeR)){
            String pSql="select * from dcp_pstockin a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.pstockinno='"+pStockInNO+"'";
            List<Map<String, Object>> getQData = this.doQueryData(pSql, null);
            if(CollUtil.isNotEmpty(getQData)) {
                String status = getQData.get(0).get("STATUS").toString();
                String bDate = getQData.get(0).get("BDATE").toString();
                String memo = getQData.get(0).get("MEMO").toString();
                String prodType = getQData.get(0).get("PRODTYPE").toString();
                String accountDate=getQData.get(0).get("ACCOUNT_DATE").toString();
                if(Check.Null(accountDate)){
                    accountDate=submitDate;//当前日期
                }
                if ("cancel".equals(opType)) {
                    if (!status.equals("0")) {
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非新建不可作废！");
                    }

                    UptBean ub1 = new UptBean("DCP_PSTOCKIN");
                    //add Value
                    ub1.addUpdateValue("status", new DataValue("3", Types.VARCHAR));
                    ub1.addUpdateValue("MODIFYBY", new DataValue(submitBy, Types.VARCHAR));
                    ub1.addUpdateValue("MODIFY_DATE", new DataValue(submitDate, Types.VARCHAR));
                    ub1.addUpdateValue("MODIFY_TIME", new DataValue(submitTime, Types.VARCHAR));
                    ub1.addUpdateValue("CANCELBY", new DataValue(submitBy, Types.VARCHAR));
                    ub1.addUpdateValue("CANCEL_DATE", new DataValue(submitDate, Types.VARCHAR));
                    ub1.addUpdateValue("CANCEL_TIME", new DataValue(submitTime, Types.VARCHAR));
                    ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    //condition
                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                    ub1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));

                    //更新
                    this.addProcessData(new DataProcessBean(ub1));

                }
                if ("post".equals(opType)) {
                    if (!status.equals("0")) {
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非新建不可过账！");
                    }

                    Map<String, Object> stockChangeVerifyMsg = PosPub.getStockChangeVerifyMsg(dao, req.geteId(), req.getOrganizationNO(), accountDate);
                    String check = stockChangeVerifyMsg.get("check").toString();
                    if("N".equals( check)){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, stockChangeVerifyMsg.get("errorMsg").toString());
                    }


                    UptBean ub1 = new UptBean("DCP_PSTOCKIN");
                    //add Value
                    ub1.addUpdateValue("status", new DataValue("2", Types.VARCHAR));
                    ub1.addUpdateValue("CONFIRMBY", new DataValue(submitBy, Types.VARCHAR));
                    ub1.addUpdateValue("CONFIRM_DATE", new DataValue(submitDate, Types.VARCHAR));
                    ub1.addUpdateValue("CONFIRM_TIME", new DataValue(submitTime, Types.VARCHAR));
                    ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    //condition
                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                    ub1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));

                    //更新
                    this.addProcessData(new DataProcessBean(ub1));

                    String detailSql="select a.*,b.ISCHECKSTOCK,b.islocation,c.isbatch " +
                            " from DCP_PSTOCKIN_detail a " +
                            " left join dcp_warehouse b on a.eid=b.eid and a.warehouse=b.warehouse " +
                            " left join dcp_goods c on a.pluno=c.pluno and a.eid=c.eid " +
                            " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.pstockinno='"+pStockInNO+"'";
                    List<Map<String, Object>> getDData = this.doQueryData(detailSql, null);
                    String batchSql = "select a.oitem,sum(a.pqty) as pqty ,sum(a.baseqty) as baseqty from DCP_PSTOCKOUT_BATCH a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.pstockinno='"+pStockInNO+"' group by a.oitem ";
                    List<Map<String, Object>> getBData = this.doQueryData(batchSql, null);
                    String batchNormalSql = "select a.* from DCP_PSTOCKOUT_BATCH a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.pstockinno='"+pStockInNO+"' order by a.item ";
                    List<Map<String, Object>> getBDataN = this.doQueryData(batchNormalSql, null);

                    String materialSql="select a.*,b.ISCHECKSTOCK,c.isbatch,d.PRODTYPE " +
                            " from DCP_PSTOCKIN_MATERIAL a " +
                            " left join dcp_pstockin_detail d on d.eid=a.eid and d.ORGANIZATIONNO=a.ORGANIZATIONNO and d.pstockinno=a.pstockinno and d.item=a.mitem " +
                            " left join dcp_warehouse b on a.eid=b.eid and a.warehouse=b.warehouse " +
                            " left join dcp_goods c on a.pluno=c.pluno and a.eid=c.eid " +
                            " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.pstockinno='"+pStockInNO+"'";
                    List<Map<String, Object>> getMData = this.doQueryData(materialSql, null);

                    int batchItem=0;
                    if(getBDataN.size()>0){
                        Optional<Integer> itemOp = getBDataN.stream().map(x -> Integer.valueOf(x.get("ITEM").toString())).collect(Collectors.toList()).stream().max(Comparator.comparingInt(x -> x));
                        batchItem = itemOp.get();
                    }

                    for (Map<String, Object> map : getDData){
                        String item = map.get("ITEM").toString();
                        String pluNo = map.get("PLUNO").toString();
                        String isCheckStock = map.get("ISCHECKSTOCK").toString();
                        String featureNo = map.get("FEATURENO").toString();
                        String pUnit = map.get("PUNIT").toString();
                        String pQty = map.get("PQTY").toString();
                        String baseUnit = map.get("BASEUNIT").toString();
                        String baseQty = map.get("BASEQTY").toString();
                        String unitRatio = map.get("UNIT_RATIO").toString();
                        String warehouse = map.get("WAREHOUSE").toString();
                        String batchNo = map.get("BATCH_NO").toString();
                        String location = map.get("LOCATION").toString();
                        String expDate = map.get("EXPDATE").toString();
                        String prodDate = map.get("PROD_DATE").toString();
                        String price = map.get("PRICE").toString();
                        String distriPrice = map.get("DISTRIPRICE").toString();
                        String amt = map.get("AMT").toString();
                        String distriAmt = map.get("DISTRIAMT").toString();
                        String isBatch = map.get("ISBATCH").toString();
                        String isLocation = map.get("ISLOCATION").toString();

                        if(isBatch.equals("Y")&&isBatchNo.equals("Y")){
                            if(Check.Null(batchNo)||" ".equals(batchNo)){
                                batchNo="DEFAULTBATCH";
                            }
                        }
                        if(Check.Null(batchNo)){
                            batchNo=" ";
                        }
                        if("Y".equals(isLocation)){
                            if(Check.Null(location)||" ".equals(location)){
                                location="DEFAULTLOCATION";
                            }
                        }
                        if(Check.Null(location)){
                            location=" ";
                        }

                        BcReq bcReq=new BcReq();
                        bcReq.setServiceType("PStockOutProcess");
                        bcReq.setDocType(docTypeR);
                        bcReq.setBillType("32");
                        bcReq.setProdType(map.get("PRODTYPE").toString());
                        bcReq.setDirection("-1");

                        BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                        String bType = bcMap.getBType();
                        String costCode = bcMap.getCostCode();
                        if(Check.Null(bType)||Check.Null(costCode)){
                            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                        }


                        List<Map<String, Object>> filterB = getBData.stream().filter(x -> x.get("OITEM").toString().equals(item)).collect(Collectors.toList());
                        List<Map<String, Object>> filterBn = getBDataN.stream().filter(x -> x.get("OITEM").toString().equals(item)).collect(Collectors.toList());

                        if (CollUtil.isNotEmpty(filterB)){
                            String batchPQty = filterB.get(0).get("PQTY").toString();
                            String batchBaseQty = filterB.get(0).get("BASEQTY").toString();

                            //String batchItem = filterBn.get(filterBn.size() - 1).get("ITEM").toString();
                            //batchItem=String.valueOf(Integer.parseInt(batchItem)+1);
                            BigDecimal lastPQty = new BigDecimal(pQty).subtract(new BigDecimal(batchPQty));
                            BigDecimal lastBaseQty = new BigDecimal(baseQty).subtract(new BigDecimal(batchBaseQty));

                            BigDecimal lastAmt = lastPQty.multiply(new BigDecimal(price));
                            BigDecimal lastDistriAmt = lastPQty.multiply(new BigDecimal(distriPrice));

                            if(lastPQty.compareTo(BigDecimal.ZERO)>0){
                                if("Y".equals(isCheckStock)){
                                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号"+pluNo+"库存不足！");
                                }else{
                                    batchItem++;
                                    //允许负库存
                                    ColumnDataValue mbColumns=new ColumnDataValue();
                                    mbColumns.add("EID", DataValues.newString(eId));
                                    mbColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                    mbColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                                    mbColumns.add("ITEM",DataValues.newString(batchItem));
                                    mbColumns.add("OITEM",DataValues.newString(item));
                                    mbColumns.add("PLUNO",DataValues.newString(pluNo));
                                    mbColumns.add("FEATURENO",DataValues.newString(featureNo));
                                    mbColumns.add("PUNIT",DataValues.newString(pUnit));
                                    mbColumns.add("PQTY",DataValues.newString(lastPQty));
                                    mbColumns.add("BASEUNIT",DataValues.newString(baseUnit));
                                    mbColumns.add("BASEQTY",DataValues.newString(lastBaseQty));
                                    mbColumns.add("UNIT_RATIO",DataValues.newString(unitRatio));

                                    mbColumns.add("WAREHOUSE",DataValues.newString(warehouse));
                                    mbColumns.add("LOCATION",DataValues.newString(Check.Null(location)?" ":location));
                                    mbColumns.add("BATCHNO",DataValues.newString(Check.Null(batchNo)?" ":batchNo));
                                    mbColumns.add("PRODDATE",DataValues.newString(prodDate));
                                    mbColumns.add("EXPDATE",DataValues.newString(expDate));

                                    String[] mbColumnNames = mbColumns.getColumns().toArray(new String[0]);
                                    DataValue[] mbDataValues = mbColumns.getDataValues().toArray(new DataValue[0]);
                                    InsBean ibmb=new InsBean("DCP_PSTOCKOUT_BATCH",mbColumnNames);
                                    ibmb.addValues(mbDataValues);
                                    this.addProcessData(new DataProcessBean(ibmb));

                                    String procedure = "SP_DCP_STOCKCHANGE_VX";
                                    Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                                    inputParameter.put(1, eId);                                            //--企业ID
                                    inputParameter.put(2, null);
                                    inputParameter.put(3, shopId);                                         //--组织
                                    inputParameter.put(4, bType);
                                    inputParameter.put(5, costCode);
                                    inputParameter.put(6, "32");                                //--单据类型
                                    inputParameter.put(7, pStockInNO);                                        //--单据号
                                    inputParameter.put(8, String.valueOf(batchItem));
                                    inputParameter.put(9, "0");
                                    inputParameter.put(10, "-1");                                   //--异动方向 1=加库存 -1=减库存
                                    inputParameter.put(11, bDate);             //--营业日期 yyyy-MM-dd
                                    inputParameter.put(12, pluNo);         //--品号
                                    inputParameter.put(13, featureNo);                                   //--特征码
                                    inputParameter.put(14, warehouse);    //--仓库
                                    inputParameter.put(15, Check.Null(batchNo)?" ":batchNo);     //--批号
                                    inputParameter.put(16, location);
                                    inputParameter.put(17, pUnit);        //--交易单位
                                    inputParameter.put(18, lastPQty);         //--交易数量
                                    inputParameter.put(19, baseUnit);     //--基准单位
                                    inputParameter.put(20, lastBaseQty);      //--基准数量
                                    inputParameter.put(21, unitRatio);   //--换算比例
                                    inputParameter.put(22, price);        //--零售价
                                    inputParameter.put(23, lastAmt.toString());          //--零售金额
                                    inputParameter.put(24, distriPrice);  //--进货价
                                    inputParameter.put(25, lastDistriAmt);    //--进货金额
                                    inputParameter.put(26, accountDate);                                   //--入账日期 yyyy-MM-dd
                                    inputParameter.put(27, prodDate);    //--批号的生产日期 yyyy-MM-dd
                                    inputParameter.put(28, bDate);            //--单据日期
                                    inputParameter.put(29, "拆解出库成品扣库存");             //--异动原因
                                    inputParameter.put(30, memo);             //--异动描述
                                    inputParameter.put(31, accountBy);          //--操作员

                                    ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                    this.addProcessData(new DataProcessBean(pdb));
                                }
                            }
                        }
                        else{
                            //
                            if("Y".equals(isCheckStock)){
                                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号"+pluNo+"库存不足！");
                            }
                            else{
                                batchItem++;
                                //允许负库存
                                ColumnDataValue mbColumns=new ColumnDataValue();
                                mbColumns.add("EID", DataValues.newString(eId));
                                mbColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                mbColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                                mbColumns.add("ITEM",DataValues.newString(batchItem));
                                mbColumns.add("OITEM",DataValues.newString(item));
                                mbColumns.add("PLUNO",DataValues.newString(pluNo));
                                mbColumns.add("FEATURENO",DataValues.newString(featureNo));
                                mbColumns.add("PUNIT",DataValues.newString(pUnit));
                                mbColumns.add("PQTY",DataValues.newString(pQty));
                                mbColumns.add("BASEUNIT",DataValues.newString(baseUnit));
                                mbColumns.add("BASEQTY",DataValues.newString(baseQty));
                                mbColumns.add("UNIT_RATIO",DataValues.newString(unitRatio));

                                mbColumns.add("WAREHOUSE",DataValues.newString(warehouse));
                                mbColumns.add("LOCATION",DataValues.newString(Check.Null(location)?" ":location));
                                mbColumns.add("BATCHNO",DataValues.newString(Check.Null(batchNo)?" ":batchNo));
                                mbColumns.add("PRODDATE",DataValues.newString(prodDate));
                                mbColumns.add("EXPDATE",DataValues.newString(expDate));

                                String[] mbColumnNames = mbColumns.getColumns().toArray(new String[0]);
                                DataValue[] mbDataValues = mbColumns.getDataValues().toArray(new DataValue[0]);
                                InsBean ibmb=new InsBean("DCP_PSTOCKOUT_BATCH",mbColumnNames);
                                ibmb.addValues(mbDataValues);
                                this.addProcessData(new DataProcessBean(ibmb));


                                String procedure = "SP_DCP_STOCKCHANGE_VX";
                                Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                                inputParameter.put(1, eId);                                            //--企业ID
                                inputParameter.put(2, null);
                                inputParameter.put(3, shopId);                                         //--组织
                                inputParameter.put(4, bType);
                                inputParameter.put(5, costCode);
                                inputParameter.put(6, "32");                                //--单据类型
                                inputParameter.put(7, pStockInNO);                                        //--单据号
                                inputParameter.put(8, batchItem);
                                inputParameter.put(9, "0");
                                inputParameter.put(10, "-1");                                   //--异动方向 1=加库存 -1=减库存
                                inputParameter.put(11, bDate);             //--营业日期 yyyy-MM-dd
                                inputParameter.put(12, pluNo);         //--品号
                                inputParameter.put(13, featureNo);                                   //--特征码
                                inputParameter.put(14, warehouse);    //--仓库
                                inputParameter.put(15, Check.Null(batchNo)?" ":batchNo);     //--批号
                                inputParameter.put(16, location);
                                inputParameter.put(17, pUnit);        //--交易单位
                                inputParameter.put(18, pQty);         //--交易数量
                                inputParameter.put(19, baseUnit);     //--基准单位
                                inputParameter.put(20, baseQty);      //--基准数量
                                inputParameter.put(21, unitRatio);   //--换算比例
                                inputParameter.put(22, price);        //--零售价
                                inputParameter.put(23, amt);          //--零售金额
                                inputParameter.put(24, distriPrice);  //--进货价
                                inputParameter.put(25, distriAmt);    //--进货金额
                                inputParameter.put(26, accountDate);                                   //--入账日期 yyyy-MM-dd
                                inputParameter.put(27, prodDate);    //--批号的生产日期 yyyy-MM-dd
                                inputParameter.put(28, bDate);            //--单据日期
                                inputParameter.put(29, "拆解出库成品扣库存");             //--异动原因
                                inputParameter.put(30, memo);             //--异动描述
                                inputParameter.put(31, accountBy);          //--操作员

                                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                this.addProcessData(new DataProcessBean(pdb));
                            }
                        }

                        //扣库存  上面临时添加的先扣 再根据表中的扣

                        if(CollUtil.isNotEmpty(filterBn)){
                            for (Map<String, Object> bn : filterBn){

                                String bBatchNo = bn.get("BATCHNO").toString();
                                String bLocation = bn.get("LOCATION").toString();
                                String bPQty = bn.get("PQTY").toString();
                                String bBaseQty = bn.get("BASEQTY").toString();
                                String bProdDate = bn.get("PRODDATE").toString();
                                BigDecimal bAmt = new BigDecimal(bPQty).multiply(new BigDecimal(price));
                                BigDecimal bDistriAmt =new BigDecimal(bPQty).multiply(new BigDecimal(distriPrice));

                                //if(isBatch.equals("Y")&&isBatchNo.equals("Y")){
                                //    if(Check.Null(bBatchNo)||" ".equals(bBatchNo)){
                                //        bBatchNo="DEFAULTBATCH";
                                //    }
                                //}
                                if("Y".equals(isLocation)){
                                    if(Check.Null(location)||" ".equals(location)){
                                        location="DEFAULTLOCATION";
                                    }
                                }

                                String procedure = "SP_DCP_STOCKCHANGE_VX";
                                Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                                inputParameter.put(1, eId);                                            //--企业ID
                                inputParameter.put(2, null);
                                inputParameter.put(3, shopId);                                         //--组织
                                inputParameter.put(4, bType);
                                inputParameter.put(5, costCode);
                                inputParameter.put(6, "32");                                //--单据类型
                                inputParameter.put(7, pStockInNO);                                        //--单据号
                                inputParameter.put(8, bn.get("ITEM").toString());
                                inputParameter.put(9, "0");
                                inputParameter.put(10, "-1");                                   //--异动方向 1=加库存 -1=减库存
                                inputParameter.put(11, bDate);             //--营业日期 yyyy-MM-dd
                                inputParameter.put(12, pluNo);         //--品号
                                inputParameter.put(13, featureNo);                                   //--特征码
                                inputParameter.put(14, warehouse);    //--仓库
                                inputParameter.put(15, Check.Null(bBatchNo)?" ":bBatchNo);     //--批号
                                inputParameter.put(16, Check.Null(bLocation)?" ":bLocation);
                                inputParameter.put(17, pUnit);        //--交易单位
                                inputParameter.put(18, bPQty);         //--交易数量
                                inputParameter.put(19, baseUnit);     //--基准单位
                                inputParameter.put(20, bBaseQty);      //--基准数量
                                inputParameter.put(21, unitRatio);   //--换算比例
                                inputParameter.put(22, price);        //--零售价
                                inputParameter.put(23, bAmt);          //--零售金额
                                inputParameter.put(24, distriPrice);  //--进货价
                                inputParameter.put(25, bDistriAmt);    //--进货金额
                                inputParameter.put(26, accountDate);                                   //--入账日期 yyyy-MM-dd
                                inputParameter.put(27, bProdDate);    //--批号的生产日期 yyyy-MM-dd
                                inputParameter.put(28, bDate);            //--单据日期
                                inputParameter.put(29, "拆解出库成品扣库存");             //--异动原因
                                inputParameter.put(30, memo);             //--异动描述
                                inputParameter.put(31, accountBy);          //--操作员

                                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                this.addProcessData(new DataProcessBean(pdb));
                            }
                        }
                    }

                    //原料加库存
                    for (Map<String, Object> md : getMData){
                        String pluNo = md.get("PLUNO").toString();
                        String featureNo = md.get("FEATURENO").toString();
                        String pUnit = md.get("PUNIT").toString();
                        String pQty = md.get("PQTY").toString();
                        String baseUnit = md.get("BASEUNIT").toString();
                        String baseQty = md.get("BASEQTY").toString();
                        String unitRatio = md.get("UNIT_RATIO").toString();
                        String warehouse = md.get("WAREHOUSE").toString();
                        String batchNo = md.get("BATCH_NO").toString();
                        String location = md.get("LOCATION").toString();
                        String prodDate = md.get("PROD_DATE").toString();
                        String price = md.get("PRICE").toString();
                        String distriPrice = md.get("DISTRIPRICE").toString();
                        String amt = md.get("AMT").toString();
                        String distriAmt = md.get("DISTRIAMT").toString();
                        String isBatch = md.get("ISBATCH").toString();
                        String materialItem = md.get("ITEM").toString();
                        if("Y".equals(isBatch)&&"Y".equals(isBatchNo)&&(Check.Null(batchNo)||" ".equals(batchNo))){
                            batchNo = PosPub.getBatchNo(dao, eId, organizationNO, "", pluNo, featureNo, prodDate, "", req.getEmployeeNo(), req.getEmployeeName(), "");

                            UptBean ubm = new UptBean("DCP_PSTOCKIN_MATERIAL");
                            //add Value
                            ubm.addUpdateValue("BATCH_NO", new DataValue(batchNo, Types.VARCHAR));
                           //condition
                            ubm.addCondition("ITEM", new DataValue(materialItem, Types.VARCHAR));
                            ubm.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                            ubm.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ubm.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                            ubm.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));

                            //更新
                            this.addProcessData(new DataProcessBean(ubm));
                        }

                        BcReq bcReq=new BcReq();
                        bcReq.setServiceType("PStockOutProcess");
                        bcReq.setDocType(docTypeR);
                        bcReq.setBillType("33");
                        bcReq.setProdType(md.get("PRODTYPE").toString());
                        bcReq.setDirection("1");

                        BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                        String bType = bcMap.getBType();
                        String costCode = bcMap.getCostCode();
                        if(Check.Null(bType)||Check.Null(costCode)){
                            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                        }

                        String procedure = "SP_DCP_STOCKCHANGE_VX";
                        Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                        inputParameter.put(1, eId);                                            //--企业ID
                        inputParameter.put(2, null);
                        inputParameter.put(3, shopId);                                         //--组织
                        inputParameter.put(4, bType);
                        inputParameter.put(5, costCode);
                        inputParameter.put(6, "33");                                //--单据类型
                        inputParameter.put(7, pStockInNO);                                        //--单据号
                        inputParameter.put(8, md.get("ITEM").toString());
                        inputParameter.put(9, "0");
                        inputParameter.put(10, "1");                                   //--异动方向 1=加库存 -1=减库存
                        inputParameter.put(11, bDate);             //--营业日期 yyyy-MM-dd
                        inputParameter.put(12, pluNo);         //--品号
                        inputParameter.put(13, featureNo);                                   //--特征码
                        inputParameter.put(14, warehouse);    //--仓库
                        inputParameter.put(15, Check.Null(batchNo)?" ":batchNo);     //--批号
                        inputParameter.put(16, location);
                        inputParameter.put(17, pUnit);        //--交易单位
                        inputParameter.put(18, pQty);         //--交易数量
                        inputParameter.put(19, baseUnit);     //--基准单位
                        inputParameter.put(20, baseQty);      //--基准数量
                        inputParameter.put(21, unitRatio);   //--换算比例
                        inputParameter.put(22, price);        //--零售价
                        inputParameter.put(23, amt);          //--零售金额
                        inputParameter.put(24, distriPrice);  //--进货价
                        inputParameter.put(25, distriAmt);    //--进货金额
                        inputParameter.put(26, accountDate);                                   //--入账日期 yyyy-MM-dd
                        inputParameter.put(27, prodDate);    //--批号的生产日期 yyyy-MM-dd
                        inputParameter.put(28, bDate);            //--单据日期
                        inputParameter.put(29, "拆解出库原料加库存");             //--异动原因
                        inputParameter.put(30, memo);             //--异动描述
                        inputParameter.put(31, accountBy);          //--操作员

                        ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                        this.addProcessData(new DataProcessBean(pdb));
                    }
                }

                this.doExecuteDataToDB();
            }
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PStockOutStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PStockOutStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PStockOutStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PStockOutStatusUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        String pStockInNO = req.getRequest().getPStockInNo();
        if(Check.Null(pStockInNO)){
            errMsg.append("完工入库单单号不可为空值, ");
            isFail = true;
        }
        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_PStockOutStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_PStockOutStatusUpdateReq>(){};
    }

    @Override
    protected DCP_PStockOutStatusUpdateRes getResponseType() {
        return new DCP_PStockOutStatusUpdateRes();
    }

    /**
     * 完工入库查询
     * @param req
     * @return
     **/
    protected String GetDCP_PSTOCKIN_SQL(DCP_PStockOutStatusUpdateReq req) {
        String sql=" "
                + " select a.pstockinno,a.bdate,a.otype,a.ofno,a.memo,a.load_doctype,a.load_docno,a.doc_type,a.account_date,"
                + " b.warehouse,b.item,b.oitem,b.pluno,b.punit,b.pqty,b.baseunit,b.unit_ratio,b.baseqty,b.batch_no,"
                + " b.prod_date,b.price,b.amt,b.scrap_qty,b.bsno,b.distriprice,b.distriamt,b.featureno,"
                + " c.warehouse as mat_warehouse,c.item as mat_item,c.mpluno as mat_mpluno,c.mitem as mat_mitem,"
                + " c.pluno as mat_pluno,c.punit mat_punit,c.pqty as mat_pqty,c.baseunit as mat_baseunit,"
                + " c.baseqty as mat_baseqty,c.unit_ratio as mat_unit_ratio,c.finalprodbaseqty as mat_finalprodbaseqty,"
                + " c.rawmaterialbaseqty as mat_rawmaterialbaseqty,c.price as mat_price,c.amt as mat_amt,"
                + " c.batch_no as mat_batch_no,c.prod_date as mat_prod_date,c.distriprice as mat_distriprice,"
                + " c.distriamt as mat_distriamt,c.isbuckle as mat_isbuckle, c.featureno as mat_featureno"
                + " from dcp_pstockin a"
                + " inner join dcp_pstockin_detail b on a.eid=b.eid and a.organizationno=b.organizationno "
                + "  and a.pstockinno=b.pstockinno and a.account_date=b.account_date "
                + " inner join dcp_pstockin_material c on a.eid=c.eid and a.organizationno=c.organizationno "
                + "  and a.pstockinno=c.pstockinno and a.account_date=c.account_date and b.item=c.mitem "
                + " where a.pstockinno='"+req.getRequest().getPStockInNo()+"' "
                + " and a.eid='"+req.geteId()+"' and a.shopid='"+req.getShopId()+"' and status='0' ";
        return sql;
    }
    protected String GetTW_PSTOCKIN_MaterialSql(DCP_PStockOutStatusUpdateReq req) {
        String sql="select a.PSTOCKINNO,a.BDATE,a.OTYPE,a.OFNO,a.MEMO,CREATEBY,CREATE_DATE,CREATE_TIME,ACCOUNTBY,"
                + " a.ACCOUNT_DATE,ACCOUNT_TIME,LOAD_DOCTYPE,LOAD_DOCNO,m.MITEM AS MITEM,m.ITEM,OITEM,m.pluno as PLUNO,m.mpluno as MPLUNO,"
                + " m.PUNIT,m.PQTY,m.BASEUNIT,m.UNIT_RATIO,m.BASEQTY,m.PRICE,m.AMT,b.SCRAP_QTY,BSNO,m.WAREHOUSE,"
                + " m.BATCH_NO,m.PROD_DATE,m.DISTRIPRICE,NVL(m.ISBUCKLE,'Y') as ISBUCKLE "
                + ",m.FINALPRODBASEQTY,m.RAWMATERIALBASEQTY  "
                + " from DCP_PSTOCKIN a inner join DCP_PSTOCKIN_detail b on a.EID=b.EID "
                + " and a.Organizationno=b.Organizationno and a.PSTOCKINNO=b.PSTOCKINNO and a.ACCOUNT_DATE=b.ACCOUNT_DATE "
                + " inner join DCP_PSTOCKIN_material m on a.EID=m.EID and a.Organizationno=m.Organizationno "
                + " and a.PSTOCKINNO=m.PSTOCKINNO and a.ACCOUNT_DATE=m.ACCOUNT_DATE "
                + " and b.item = m.mitem and b.pluno = m.mpluno "
                + " where a.PSTOCKINNO='"+req.getRequest().getPStockInNo()+"' and a.EID='"+req.geteId()+"' AND a.SHOPID='"+req.getShopId()+"' "
                //+ " and (m.ISBUCKLE='Y' or m.ISBUCKLE is null)
                + "";
        return sql;
    }


}
