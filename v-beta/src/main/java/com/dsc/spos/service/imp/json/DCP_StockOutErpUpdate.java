package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockOutErpUpdateReq;
import com.dsc.spos.json.cust.req.DCP_StockOutErpUpdateReq.Return_Detail;
import com.dsc.spos.json.cust.res.DCP_StockOutErpUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.dao.DataValue.DataExpression;

/**
 * 服务说明：门店退货出库单更新
 * @author jinzma
 * @since  2020-07-22
 */
public class DCP_StockOutErpUpdate extends SPosAdvanceService<DCP_StockOutErpUpdateReq, DCP_StockOutErpUpdateRes> {
    @Override
    protected boolean isVerifyFail(DCP_StockOutErpUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String stockOutNO = req.getStockOutNO();
        String opType = req.getOpType();
        
        if (Check.Null(stockOutNO)) {
            errMsg.append("前端单号(front_no)不可为空值, ");
            isFail = true;
        }
        
        if (Check.Null(opType)) {
            errMsg.append("操作类型(operation_type)不可为空值, ");
            isFail = true;
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        for (Return_Detail par:req.getReturn_detail()) {
            String pluNo = par.getItem_no();
            //String punit = par.getPacking_unit();
            String pqty = par.getPacking_qty();
            //String baseUnit = par.getBase_unit();
            String baseQty = par.getBase_qty();
            //String price = par.getPrice();
            //String amt = par.getAmount();
            //String unitRatio = par.getUnit_ratio();
            //String distriPrice = par.getDistri_price();
            //String distriAmt = par.getDistri_amount();
            
            if (Check.Null(pluNo)) {
                errMsg.append("商品编号(item_no)不可为空值, ");
                isFail = true;
            }
            //			if (Check.Null(punit)) {
            //				errMsg.append("包装单位(packing_unit)不可为空值, ");
            //				isFail = true;
            //			}
            if (Check.Null(pqty)) {
                errMsg.append("包装数量(packing_qty)不可为空值, ");
                isFail = true;
            }
            //			if (Check.Null(baseUnit)) {
            //				errMsg.append("基本单位(base_unit)不可为空值, ");
            //				isFail = true;
            //			}
            if (Check.Null(baseQty)) {
                errMsg.append("基本单位数量(base_qty)不可为空值, ");
                isFail = true;
            }
            //			if (Check.Null(price)) {
            //				errMsg.append("零售价(price)不可为空值, ");
            //				isFail = true;
            //			}
            //			if (Check.Null(amt)) {
            //				errMsg.append("零售金额(amount)不可为空值, ");
            //				isFail = true;
            //			}
            //			if (Check.Null(unitRatio)) {
            //				errMsg.append("单位转换率(unit_ratio)不可为空值, ");
            //				isFail = true;
            //			}
            //			if (Check.Null(distriPrice)) {
            //				errMsg.append("进货单价(distri_price)不可为空值, ");
            //				isFail = true;
            //			}
            //			if (Check.Null(distriAmt)) {
            //				errMsg.append("进货金额(distri_amount)不可为空值, ");
            //				isFail = true;
            //			}
            
            if (isFail) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_StockOutErpUpdateReq> getRequestType() {
        return new TypeToken<DCP_StockOutErpUpdateReq>(){};
    }
    
    @Override
    protected DCP_StockOutErpUpdateRes getResponseType() {
        return new DCP_StockOutErpUpdateRes();
    }
    
    @Override
    protected void processDUID(DCP_StockOutErpUpdateReq req,DCP_StockOutErpUpdateRes res) throws Exception {
        try {
            String eId = req.geteId();
            String shopId = req.getShopId();
            String stockOutNO = req.getStockOutNO();
            String opType = req.getOpType();             //2.整单收货过账   3.部分收货  4.整单驳回
            String rejectReason = req.getReject_reason();
            
            String sDate=new SimpleDateFormat("yyyyMMdd").format(new Date());
            String sTime=new SimpleDateFormat("HHmmss").format(new Date());
            
            //判断下，是不是已经更新过了，防止ERP 重复调用  //STATUS=3 4 5 就直接返回报错
            String sql = this.getDCP_StockOut_Sql(req);
            List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,null);
            if (getQDataDetail != null && !getQDataDetail.isEmpty()) {
                //0-新建  1-待过账  2-待签收  3-已签收 4-部分签收 5-已驳回
                String status = getQDataDetail.get(0).get("STATUS").toString();
                if (status.equals("2")){
                    //修改退货单状态
                    String docStatus="";
                    switch (opType) {
                        case "2":            //2.整单收货过账
                            docStatus="3";     //3-已签收
                            break;
                        case "3":            //3.部分收货
                            docStatus="4";     //4-部分签收
                            break;
                        case "4":            //4.整单驳回
                            docStatus="5";     //5-已驳回
                            break;
                    }
                    UptBean ub1 = new UptBean("DCP_STOCKOUT");
                    ub1.addUpdateValue("STATUS", new DataValue(docStatus, Types.VARCHAR));
                    ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    //【ID1036513】【浙江意诺V9203】‘个案评估’ 退货出库，驳回增加原因，并可以让门店知道这个驳回原因，需要有报表查询---erp下发 by jinzma
                    ub1.addUpdateValue("REJECTREASON", new DataValue(rejectReason, Types.VARCHAR));
                    
                    // condition
                    ub1.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("STOCKOUTNO", new DataValue(stockOutNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub1));
                    
                    //IF参数是否启用批号==N ，库存流水的批号和日期字段不给值 BY JZMA 20191024
                    //					String isBatchPara = PosPub.getPARA_SMS(dao, eId, "", "Is_BatchNO");
                    //					if (Check.Null(isBatchPara) || !isBatchPara.equals("Y"))
                    //						isBatchPara="N";
                    
                    //获取启用在途参数 Enable_InTransit
                    String Enable_InTransit=PosPub.getPARA_SMS(dao, eId, "", "Enable_InTransit");
                    if (Check.Null(Enable_InTransit)|| !Enable_InTransit.equals("Y")) {
                        Enable_InTransit = "N";
                    }
                    
                    //获取在途仓
                    String inv_cost_warehouse = "";
                    if (Enable_InTransit.equals("Y")) {
                        String sql_inv_cost_warehouse = " select inv_cost_warehouse from dcp_org"
                                + " where status='100' and  eid='"+eId+"' and organizationno='"+shopId+"'";
                        List<Map<String, Object>> getQDatawarehouse=this.doQueryData(sql_inv_cost_warehouse,null);
                        if (getQDatawarehouse!=null && !getQDatawarehouse.isEmpty()) {
                            inv_cost_warehouse = getQDatawarehouse.get(0).getOrDefault("INV_COST_WAREHOUSE","").toString();
                        }
                    }
                    
                    String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
                    
                    if(Enable_InTransit.equals("Y") && !Check.Null(inv_cost_warehouse)) {  //启用在途时且在途仓不为空，先扣减在途
                        for (Map<String, Object> oneData : getQDataDetail) {
                            //减少在库数
                            String featureNo = oneData.get("FEATURENO").toString();  //特征码为空给空格
                            if (Check.Null(featureNo)) {
                                featureNo = " ";
                            }
                            String procedure="SP_DCP_StockChange";
                            Map<Integer,Object> inputParameter = new HashMap<>();
                            inputParameter.put(1,eId);                                      //--企业ID
                            inputParameter.put(2,shopId);                                   //--组织
                            inputParameter.put(3,"13");                                     //--单据类型
                            inputParameter.put(4,stockOutNO);	                              //--单据号
                            inputParameter.put(5,oneData.get("ITEM").toString());           //--单据行号
                            inputParameter.put(6,"-1");                                     //--异动方向 1=加库存 -1=减库存
                            inputParameter.put(7,oneData.get("BDATE").toString());          //--营业日期 yyyy-MM-dd
                            inputParameter.put(8,oneData.get("PLUNO").toString());          //--品号
                            inputParameter.put(9,featureNo);                                //--特征码
                            inputParameter.put(10,inv_cost_warehouse);                      //--仓库
                            inputParameter.put(11,oneData.get("BATCH_NO").toString());      //--批号
                            inputParameter.put(12,oneData.get("PUNIT").toString());         //--交易单位
                            inputParameter.put(13,oneData.get("PQTY").toString());          //--交易数量
                            inputParameter.put(14,oneData.get("BASEUNIT").toString());      //--基准单位
                            inputParameter.put(15,oneData.get("BASEQTY").toString());       //--基准数量
                            inputParameter.put(16,oneData.get("UNIT_RATIO").toString());    //--换算比例
                            inputParameter.put(17,oneData.get("PRICE").toString());         //--零售价
                            inputParameter.put(18,oneData.get("AMT").toString());           //--零售金额
                            inputParameter.put(19,oneData.get("DISTRIPRICE").toString());   //--进货价
                            inputParameter.put(20,oneData.get("DISTRIAMT").toString());     //--进货金额
                            inputParameter.put(21,accountDate);                             //--入账日期 yyyy-MM-dd
                            inputParameter.put(22,oneData.get("PROD_DATE").toString());     //--批号的生产日期 yyyy-MM-dd
                            inputParameter.put(23,oneData.get("BDATE").toString());         //--单据日期
                            inputParameter.put(24,"");                                      //--异动原因
                            inputParameter.put(25,oneData.get("MEMO").toString());          //--异动描述
                            inputParameter.put(26,"admin");                                 //--操作员
                            
                            ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                            this.addProcessData(new DataProcessBean(pdb));
                        }
                    }
                    
                    // 操作类型 : 3.部分收货  4.整单驳回
                    if (opType.equals("3") || opType.equals("4")) {
                        ///处理单价和金额小数位数  BY JZMA 20200401
                        String amtLength = PosPub.getPARA_SMS(dao, eId, shopId, "amtLength");
                        String distriAmtLength = PosPub.getPARA_SMS(dao, eId, shopId, "distriAmtLength");
                        if (Check.Null(amtLength)||!PosPub.isNumeric(amtLength)) {
                            amtLength="2";
                        }
                        if (Check.Null(distriAmtLength)||!PosPub.isNumeric(distriAmtLength)) {
                            distriAmtLength="2";
                        }
                        int amtLengthInt = Integer.parseInt(amtLength);
                        int distriAmtLengthInt = Integer.parseInt(distriAmtLength);
                        
                        //取得库存调整单的单号
                        String adjustno=getAdjustNo(req);
                        
                        //控制调整单的项次
                        int i=1;
                        BigDecimal totPqty= new BigDecimal("0");
                        BigDecimal totAmt= new BigDecimal("0");
                        BigDecimal totDistriAmt = new BigDecimal(0);
                        
                        String cost_warehousestock = getQDataDetail.get(0).get("WAREHOUSE").toString();
                        for (Map<String, Object> oneData : getQDataDetail) {
                            //这里需要查询一下返回项次的数量
                            //更新项次的签收数量
                            String item=oneData.get("ITEM").toString();
                            String pluNo=oneData.get("PLUNO").toString();
                            String pqty=oneData.get("PQTY").toString();
                            String baseQty=oneData.get("BASEQTY").toString();
                            String price=oneData.get("PRICE").toString();
                            String distriPrice=oneData.get("DISTRIPRICE").toString();
                            String punit=oneData.get("PUNIT").toString();
                            String baseUnit=oneData.get("BASEUNIT").toString();
                            String unitRatio=oneData.get("UNIT_RATIO").toString();
                            String batchNo=oneData.get("BATCH_NO").toString();
                            String prodDate=oneData.get("PROD_DATE").toString();
                            String featureNo=oneData.get("FEATURENO").toString();
                            if (Check.Null(featureNo)) {
                                featureNo = " ";
                            }
                            String oItem = oneData.get("OITEM").toString();
                            String ofNo = oneData.get("OFNO").toString();
                            
                            //签收数量
                            String packing_qty="0";
                            String base_qty="0";
                            //String amount="0";
                            //String distri_amount="0";
                            
                            //3.部分收货,查询对应的原单项次
                            if (opType.equals("3")){
                                boolean errorPlu= true;
                                for (Return_Detail par : req.getReturn_detail()) {
                                    if(par.getSeq()==Integer.parseInt(item) && pluNo.equals(par.getItem_no()) ) {
                                        packing_qty=par.getPacking_qty();
                                        base_qty=par.getBase_qty();
                                        //amount=par.getAmount();
                                        //distri_amount = par.getDistri_amount();
                                        errorPlu= false;
                                        break;
                                    }
                                }
                                if (errorPlu) {
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "退货单更新明细和原单不一致");
                                }
                            }
                            
                            BigDecimal adjust_pqty= new BigDecimal(pqty).subtract(new BigDecimal(packing_qty));
                            BigDecimal adjust_baseQty= new BigDecimal(baseQty).subtract(new BigDecimal(base_qty));
                            
                            //需要更新一下原单的签收数量
                            ub1 = new UptBean("DCP_STOCKOUT_DETAIL");
                            ub1.addUpdateValue("RQTY", new DataValue(packing_qty, Types.VARCHAR));
                            ub1.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                            ub1.addCondition("STOCKOUTNO", new DataValue(stockOutNO, Types.VARCHAR));
                            ub1.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                            this.addProcessData(new DataProcessBean(ub1));
                            
                            //【ID1027835】【大万食品】门店退货出库单更新传输报错 by jinzma 20220804
                            if (!Check.Null(ofNo)) {
                                //【ID1023788】【霸王3.0】驳回门店仓退单据可退量被占用 by jinzma 20220215 更新入库单已退数量
                                UptBean ub2 = new UptBean("DCP_STOCKIN_DETAIL");
                                if (adjust_baseQty.compareTo(new BigDecimal("0")) == -1){
                                    adjust_baseQty = adjust_baseQty.abs();
                                    ub2.addUpdateValue("RETWQTY", new DataValue(adjust_baseQty, Types.VARCHAR, DataExpression.UpdateSelf));   //UpdateSelf
                                }else{
                                    ub2.addUpdateValue("RETWQTY", new DataValue(adjust_baseQty, Types.VARCHAR, DataExpression.SubSelf));   //UpdateSelf
                                }
                                ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub2.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                                ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                                ub2.addCondition("STOCKINNO", new DataValue(ofNo, Types.VARCHAR));
                                ub2.addCondition("ITEM", new DataValue(oItem, Types.VARCHAR));
                                this.addProcessData(new DataProcessBean(ub2));
                            }
                            
                            if(adjust_pqty.compareTo(BigDecimal.ZERO) != 0 && adjust_baseQty.compareTo(BigDecimal.ZERO) !=0) {
                                String[] columnsAdjustDetail = {
                                        "SHOPID","ADJUSTNO","ITEM","OITEM","PLUNO","PLU_BARCODE","PUNIT","PQTY",
                                        "BASEUNIT","BASEQTY","UNIT_RATIO",
                                        "PRICE","AMT","EID","ORGANIZATIONNO","WAREHOUSE",
                                        "BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO"
                                };
                                
                                BigDecimal amt = adjust_pqty.multiply(new BigDecimal(price)).setScale(amtLengthInt, RoundingMode.HALF_UP);
                                BigDecimal distriAmt = adjust_pqty.multiply(new BigDecimal(distriPrice)).setScale(distriAmtLengthInt, RoundingMode.HALF_UP);
                                totAmt = totAmt.add(amt);
                                totDistriAmt=totDistriAmt.add(distriAmt);
                                totPqty = totPqty.add(adjust_pqty);
                                
                                DataValue[] insValue1 = new DataValue[]{
                                        new DataValue(shopId, Types.VARCHAR),
                                        new DataValue(adjustno, Types.VARCHAR),
                                        new DataValue(i, Types.VARCHAR),
                                        new DataValue(item, Types.VARCHAR),
                                        new DataValue(pluNo, Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue(punit, Types.VARCHAR),
                                        new DataValue(adjust_pqty.toPlainString(), Types.VARCHAR),
                                        new DataValue(baseUnit, Types.VARCHAR),
                                        new DataValue(adjust_baseQty.toPlainString(), Types.VARCHAR),
                                        new DataValue(unitRatio, Types.VARCHAR),
                                        new DataValue(price, Types.VARCHAR),
                                        new DataValue(amt.toPlainString(), Types.VARCHAR),
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(shopId, Types.VARCHAR),
                                        new DataValue(cost_warehousestock, Types.VARCHAR),
                                        new DataValue(batchNo, Types.VARCHAR),
                                        new DataValue(prodDate, Types.VARCHAR),
                                        new DataValue(distriPrice, Types.VARCHAR),
                                        new DataValue(distriAmt.toPlainString(), Types.VARCHAR),
                                        new DataValue(sDate, Types.VARCHAR),
                                        new DataValue(featureNo, Types.VARCHAR),
                                };
                                InsBean ib1 = new InsBean("DCP_ADJUST_DETAIL", columnsAdjustDetail);
                                ib1.addValues(insValue1);
                                this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
                                
                                //写库存流水
                                String procedure="SP_DCP_StockChange";
                                Map<Integer,Object> inputParameter = new HashMap<>();
                                inputParameter.put(1,eId);                              //--企业ID
                                inputParameter.put(2,shopId);                           //--组织
                                inputParameter.put(3,"17");                             //--单据类型
                                inputParameter.put(4,adjustno);	                        //--单据号
                                inputParameter.put(5,i);                                //--单据行号
                                inputParameter.put(6,"1");                              //--异动方向 1=加库存 -1=减库存
                                inputParameter.put(7,sDate);                            //--营业日期 yyyy-MM-dd
                                inputParameter.put(8,pluNo);                            //--品号
                                inputParameter.put(9,featureNo);                        //--特征码
                                inputParameter.put(10,cost_warehousestock);             //--仓库
                                inputParameter.put(11,batchNo);                         //--批号
                                inputParameter.put(12,punit);                           //--交易单位
                                inputParameter.put(13,adjust_pqty.toPlainString());     //--交易数量
                                inputParameter.put(14,baseUnit);                        //--基准单位
                                inputParameter.put(15,adjust_baseQty.toPlainString());  //--基准数量
                                inputParameter.put(16,unitRatio);                       //--换算比例
                                inputParameter.put(17,price);                           //--零售价
                                inputParameter.put(18,amt.toPlainString());             //--零售金额
                                inputParameter.put(19,distriPrice);                     //--进货价
                                inputParameter.put(20,distriAmt.toPlainString());       //--进货金额
                                inputParameter.put(21,accountDate);                     //--入账日期 yyyy-MM-dd
                                inputParameter.put(22,prodDate);                        //--批号的生产日期 yyyy-MM-dd
                                inputParameter.put(23,sDate);                           //--单据日期
                                inputParameter.put(24,"");                              //--异动原因
                                inputParameter.put(25,"退货差异调整");                   //--异动描述
                                inputParameter.put(26,"admin");                              //--操作员
                                
                                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                this.addProcessData(new DataProcessBean(pdb));
                                
                                i++;
                            }
                            
                        }
                        //单身有资料才写单头
                        if (i>1) {
                            
                            //插入一张库存调整单据的单头
                            String[] columnsAdjust = {
                                    "SHOPID","ADJUSTNO","BDATE","MEMO","DOC_TYPE","OTYPE","OFNO","STATUS",
                                    "CREATEBY","CREATE_DATE","CREATE_TIME","MODIFYBY","MODIFY_DATE","MODIFY_TIME",
                                    "CONFIRMBY","CONFIRM_DATE","CONFIRM_TIME","ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME",
                                    "CANCELBY","CANCEL_DATE","CANCEL_TIME",
                                    "TOT_PQTY","TOT_AMT","TOT_CQTY","LOAD_DOCTYPE","LOAD_DOCNO","EID","ORGANIZATIONNO",
                                    "WAREHOUSE","PROCESS_STATUS","TOT_DISTRIAMT","UPDATE_TIME","TRAN_TIME"
                            };
                            
                            DataValue[] insValue1 = new DataValue[] {
                                    new DataValue(req.getShopId(), Types.VARCHAR),
                                    new DataValue(adjustno, Types.VARCHAR),
                                    new DataValue(sDate, Types.VARCHAR),
                                    new DataValue("退货库存调整", Types.VARCHAR),
                                    new DataValue("6", Types.VARCHAR),
                                    new DataValue("6", Types.VARCHAR),
                                    new DataValue(stockOutNO, Types.VARCHAR),
                                    new DataValue("2", Types.VARCHAR),//STATUS
                                    new DataValue("admin", Types.VARCHAR),
                                    new DataValue(sDate, Types.VARCHAR),
                                    new DataValue(sTime, Types.VARCHAR),
                                    new DataValue("admin", Types.VARCHAR),
                                    new DataValue(sDate, Types.VARCHAR),
                                    new DataValue(sTime, Types.VARCHAR),
                                    new DataValue("admin", Types.VARCHAR),
                                    new DataValue(sDate, Types.VARCHAR),
                                    new DataValue(sTime, Types.VARCHAR),
                                    new DataValue("admin", Types.VARCHAR),
                                    new DataValue(accountDate, Types.VARCHAR),
                                    new DataValue(sTime, Types.VARCHAR),
                                    new DataValue("", Types.VARCHAR),
                                    new DataValue("", Types.VARCHAR),
                                    new DataValue("", Types.VARCHAR),
                                    new DataValue(totPqty.toPlainString(), Types.VARCHAR), //TOT_PQTY
                                    new DataValue(totAmt.toPlainString(), Types.VARCHAR), //TOT_AMT  --
                                    new DataValue(i-1, Types.VARCHAR), //TOT_CQTY 明细条数
                                    new DataValue("", Types.VARCHAR),
                                    new DataValue("", Types.VARCHAR),
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(shopId, Types.VARCHAR), //organizationno
                                    new DataValue(cost_warehousestock, Types.VARCHAR),
                                    new DataValue("Y", Types.VARCHAR),
                                    new DataValue(totDistriAmt.toPlainString(), Types.VARCHAR),
									new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
									new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                            };
                            InsBean  ib1 = new InsBean("DCP_ADJUST", columnsAdjust);
                            ib1.addValues(insValue1);
                            this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
                            
                        }
                    }
                    res.setServiceDescription("服务执行成功");
                }
                else {
                    //以下注释，避免ERP不停地循环调用 by jinzma 20210707
                    //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"前端单号(退货单)：" + stockOutNO+" 已完成退货出库单更新，请检查！");
                    res.setServiceDescription("ERP重复调用");
                }
                
                this.doExecuteDataToDB();
                
                res.setDoc_no(stockOutNO);
                res.setOrg_no(shopId);
                res.setSuccess(true);
                res.setServiceStatus("000");
                
            } else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"前端单号(退货单)：" + stockOutNO+" 不存在，请检查！");
            }
        }catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_StockOutErpUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockOutErpUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockOutErpUpdateReq req) throws Exception {
        return null;
    }
    
    private String getDCP_StockOut_Sql(DCP_StockOutErpUpdateReq req) {
        String eId = req.geteId();
        String shopId = req.getShopId();
        String stockOutNO = req.getStockOutNO();
        String sql =" "
                + " select a.status,a.stockoutno,a.bdate,a.memo,a.delivery_no,a.load_doctype,a.load_docno,"
                + " a.createby,a.create_date,a.create_time,"
                + " a.accountby,a.account_date,a.account_time,a.ofno,"
                + " b.item,b.pluno,b.featureno,b.batch_no,b.prod_date,b.warehouse,"
                + " b.punit,b.pqty,b.baseunit,b.baseqty,b.unit_ratio,"
                + " b.price,b.amt,b.distriprice,b.distriamt,b.oitem"
                + " from dcp_stockout a"
                + " inner join dcp_stockout_detail b on a.eid=b.eid and a.organizationno=b.organizationno"
                + " and a.stockoutno=b.stockoutno "
                + " where a.eid='"+eId+"' "
                + " and a.shopid='"+shopId+"' "
                + " and a.stockoutno='"+stockOutNO+"' "
                + " and a.status in ('2','3','4','5')"
                + " ";
        return sql;
    }
    
    /**
     * 返回库存调整单号
     * @param req DCP_StockOutErpUpdateReq
     * @return String 库存调整单号
     * @throws Exception 抛异常
     */
    private String getAdjustNo(DCP_StockOutErpUpdateReq req) throws Exception {
        /*
         * 库存调整单生成规则：
         * 固定编码KCTZ+年月日+5位流水号
         * KCTZ2016120900001
         */
        String eId = req.geteId();
        String shopId = req.getShopId();
        String ajustnoHead="KCTZ" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        String adjustno;
        String sql = " select max(adjustno) adjustno from dcp_adjust "
                + " where shopid='"+shopId+"' "
                + " and eid='"+eId+"' "
                + " and organizationno='"+shopId+"' "
                + " and adjustno like '"+ ajustnoHead+"%%'";
        
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if (getQData != null && !getQData.isEmpty()) {
            adjustno = getQData.get(0).get("ADJUSTNO").toString();
            if (adjustno != null && adjustno.length() > 0) {
                long i;
                adjustno = adjustno.substring(4);
                i = Long.parseLong(adjustno) + 1;
                adjustno = i + "";
                adjustno = "KCTZ" + adjustno;
            } else {
                //当天无单，从00001开始
                adjustno = ajustnoHead + "00001";
            }
        } else {
            //当天无单，从00001开始
            adjustno = ajustnoHead + "00001";
        }
        
        return adjustno;
    }
    
}

