package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockOutCancelReq;
import com.dsc.spos.json.cust.res.DCP_StockOutCancelRes;
import com.dsc.spos.json.cust.res.DCP_StockOutCancelRes.level1Elm;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * @apiNote 调拨出库撤销
 * @since 2021-04-14
 * @author jinzma
 */
public class DCP_StockOutCancel extends SPosAdvanceService<DCP_StockOutCancelReq, DCP_StockOutCancelRes> {
    @Override
    protected void processDUID(DCP_StockOutCancelReq req, DCP_StockOutCancelRes res) throws Exception {
        String eId=req.geteId();
        String shopId=req.getShopId();
        String stockOutNo = req.getRequest().getStockOutNo();
        level1Elm datas = res.new level1Elm();
        StringBuffer errString=new StringBuffer();
        try {
            //查询调拨出库单
            String sql=""
                    + " select a.eid,a.shopid,a.stockoutno,a.warehouse,a.transfer_shop,a.transfer_warehouse,"
                    + " a.load_doctype,a.load_docno,"
                    + " a.tot_pqty,a.tot_cqty,a.tot_amt,a.tot_distriamt,a.deliveryby,"
                    + " b.item,b.pluno,b.featureno,b.batch_no,b.prod_date,b.baseunit,b.baseqty,b.punit,b.pqty,b.unit_ratio,"
                    + " b.price,b.distriprice,b.amt,b.distriamt,"
                    + " a.status,c.status as receivingstatus,c.receivingno,d.stockinno "
                    + " from dcp_stockout a"
                    + " inner join dcp_stockout_detail b on a.eid=b.eid and a.shopid=b.shopid and a.stockoutno=b.stockoutno"
                    + " left  join dcp_receiving c on c.eid=a.eid and c.shopid=a.transfer_shop and c.load_docno='"+stockOutNo+"'"
                    + " left  join dcp_stockin d   on d.eid=a.eid and d.shopid=c.shopid and d.ofno=c.receivingno"
                    + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.stockoutno='"+stockOutNo+"'"
                    + " ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (getQData == null || getQData.isEmpty()) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "出库单:"+stockOutNo+"单据不存在");
            }
            String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);

            /* 判断是否可以撤销 条件：出库单状态=2且入库通知单状态=6且未产生入库单且非订单来源 */
            if (!isCancel(eId,shopId,stockOutNo,getQData,errString)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, errString.toString());
            }

            //完成调拨出的所有流程
            if (!stockOutProcess(req,getQData,accountDate,errString)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, errString.toString());
            }

            //完成调拨入的所有流程
            if (!stockInProcess(req,getQData,accountDate,errString)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, errString.toString());
            }

            this.doExecuteDataToDB();

            res.setDatas(datas);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        }catch (Exception e){
            //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
            
            //【ID1032555】【乐沙儿3.3.0.3】在门店管理出库单据点确定时，存在负库存时的提示返回error executing work，
            // 需要能够返回SP_DCP_StockChange返回的报错，提示门店  by jinzma 20230526
            String description=e.getMessage();
            try {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);
                
                pw.flush();
                pw.close();
                
                errors.flush();
                errors.close();
                
                description = errors.toString();
                
                if (description.indexOf("品号")>0 && description.indexOf("库存出库")>0) {
                    description = description.substring(description.indexOf("品号"), description.indexOf("库存出库") + 4);
                }
                
            } catch (Exception ignored) {
            
            }
            
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, description);
            
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockOutCancelReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockOutCancelReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockOutCancelReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_StockOutCancelReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String stockOutNo = req.getRequest().getStockOutNo();
        if (Check.Null(stockOutNo)) {
            errMsg.append("调拨出库单号不能为空值!");
            isFail = true;
        }
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;

    }

    @Override
    protected TypeToken<DCP_StockOutCancelReq> getRequestType() {
        return new TypeToken<DCP_StockOutCancelReq>(){};
    }

    @Override
    protected DCP_StockOutCancelRes getResponseType() {
        return new DCP_StockOutCancelRes();
    }

    private boolean isCancel(String eId,String shopId,String stockOutNo, List<Map<String, Object>> getQData,StringBuffer errString) throws Exception{
        String status = getQData.get(0).get("STATUS").toString();
        String receivingStatus = getQData.get(0).get("RECEIVINGSTATUS").toString();
        String stockInNo = getQData.get(0).get("STOCKINNO").toString();

        /* 判断是否可以撤销 条件：出库单状态=2且入库通知单状态=6且未产生入库单且非订单来源 */
        if (Check.Null(status) || !status.equals("2")){
            errString.append("出库单:"+stockOutNo+"已完成调拨入库,无法撤销");
            return false;
        }
        if (Check.Null(receivingStatus) || !receivingStatus.equals("6")){
            errString.append("出库单:"+stockOutNo+"已完成调拨入库,无法撤销");
            return false;
        }
        if (!Check.Null(stockInNo)){
            errString.append("出库单:"+stockOutNo+"已完成调拨入库,无法撤销");
            return false;
        }

        return true;
    }

    private boolean stockOutProcess(DCP_StockOutCancelReq req,List<Map<String, Object>> getQData,String accountDate,StringBuffer errString) throws Exception{
        try {
            String createBy = req.getOpNO();
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String sTime = new SimpleDateFormat("HHmmss").format(new Date());
            String eId = getQData.get(0).get("EID").toString();
            String shopId = getQData.get(0).get("SHOPID").toString();
            String warehouse = getQData.get(0).get("WAREHOUSE").toString();
            String transferShop = getQData.get(0).get("TRANSFER_SHOP").toString();
            String transferWarehouse = getQData.get(0).get("TRANSFER_WAREHOUSE").toString();
            String loadDocType = getQData.get(0).get("LOAD_DOCTYPE").toString();
            String loadDocNo = getQData.get(0).get("LOAD_DOCNO").toString();
            String receivingNo = getQData.get(0).get("RECEIVINGNO").toString();
            String totPqty = getQData.get(0).get("TOT_PQTY").toString();
            String totCqty = getQData.get(0).get("TOT_CQTY").toString();
            String totAmt = getQData.get(0).get("TOT_AMT").toString();
            String totDistriAmt = getQData.get(0).get("TOT_DISTRIAMT").toString();
            String deliveryBy = getQData.get(0).get("DELIVERYBY").toString();

            String Enable_InTransit = PosPub.getPARA_SMS(dao, eId, "", "Enable_InTransit");  //启用在途
            if (Check.Null(Enable_InTransit)){
                Enable_InTransit="N";
            }
            String Transfer_Stock= PosPub.getPARA_SMS(dao, eId, shopId, "Transfer_Stock");  //1.调出门店发货后扣库存 2.调入门店收货后扣库存
            if (Check.Null(Transfer_Stock)){
                Transfer_Stock="1";
            }
            String stockInNo = getStockInNo(eId,transferShop,accountDate);
            String stockOutNo = req.getRequest().getStockOutNo();

            //新增入库单单身
            String[] detailColumns = {"EID","SHOPID","ORGANIZATIONNO","STOCKINNO","ITEM","OITEM",
                    "PLUNO","FEATURENO","BATCH_NO","PROD_DATE","WAREHOUSE","BDATE",
                    "BASEQTY","PQTY","BASEUNIT","PUNIT","RECEIVING_QTY","UNIT_RATIO",
                    "PRICE","DISTRIPRICE","AMT","DISTRIAMT","OFNO","OTYPE"
            };
            for (Map<String, Object> oneData : getQData) {
                DataValue[] insValues = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(transferShop, Types.VARCHAR),
                        new DataValue(transferShop, Types.VARCHAR),
                        new DataValue(stockInNo, Types.VARCHAR),
                        new DataValue(oneData.get("ITEM").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("ITEM").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("PLUNO").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("FEATURENO").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("BATCH_NO").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("PROD_DATE").toString(), Types.VARCHAR),
                        new DataValue(transferWarehouse, Types.VARCHAR),
                        new DataValue(sDate, Types.VARCHAR),
                        new DataValue(oneData.get("BASEQTY").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("PQTY").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("BASEUNIT").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("PUNIT").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("PQTY").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("UNIT_RATIO").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("PRICE").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("DISTRIPRICE").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("AMT").toString(), Types.VARCHAR),
                        new DataValue(oneData.get("DISTRIAMT").toString(), Types.VARCHAR),
                        new DataValue(receivingNo, Types.VARCHAR),
                        new DataValue("1", Types.VARCHAR)
                };
                InsBean ib = new InsBean("DCP_STOCKIN_DETAIL", detailColumns);
                ib.addValues(insValues);
                this.addProcessData(new DataProcessBean(ib));
            }

            //新增入库单单头
            String[] columns = {"EID","SHOPID","ORGANIZATIONNO","STOCKINNO","BDATE","MEMO","STATUS",
                    "DOC_TYPE","OTYPE","OFNO","WAREHOUSE","LOAD_DOCNO","PROCESS_STATUS",
                    "TRANSFER_SHOP","TRANSFER_WAREHOUSE",
                    "CREATEBY","CREATE_DATE","CREATE_TIME","CONFIRMBY","CONFIRM_DATE","CONFIRM_TIME",
                    "ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME","SUBMITBY","SUBMIT_DATE","SUBMIT_TIME",
                    "TOT_PQTY","TOT_CQTY","TOT_AMT","TOT_DISTRIAMT","CREATE_CHATUSERID","ACCOUNT_CHATUSERID",
                    "UPDATE_TIME","TRAN_TIME","DELIVERYBY"
            };
            DataValue[] insValues = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(transferShop, Types.VARCHAR),
                    new DataValue(transferShop, Types.VARCHAR),
                    new DataValue(stockInNo, Types.VARCHAR),
                    new DataValue(sDate, Types.VARCHAR),
                    new DataValue("调拨单撤销", Types.VARCHAR),
                    new DataValue("2", Types.VARCHAR),
                    new DataValue("1", Types.VARCHAR),
                    new DataValue("1", Types.VARCHAR),
                    new DataValue(receivingNo, Types.VARCHAR),
                    new DataValue(transferWarehouse, Types.VARCHAR),
                    new DataValue(stockOutNo, Types.VARCHAR),
                    new DataValue("N", Types.VARCHAR),
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue(warehouse, Types.VARCHAR),
                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(sDate, Types.VARCHAR),
                    new DataValue(sTime, Types.VARCHAR),
                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(sDate, Types.VARCHAR),
                    new DataValue(sTime, Types.VARCHAR),
                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(accountDate, Types.VARCHAR),
                    new DataValue(sTime, Types.VARCHAR),
                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(sDate, Types.VARCHAR),
                    new DataValue(sTime, Types.VARCHAR),
                    new DataValue(totPqty, Types.VARCHAR),
                    new DataValue(totCqty, Types.VARCHAR),
                    new DataValue(totAmt, Types.VARCHAR),
                    new DataValue(totDistriAmt, Types.VARCHAR),
                    new DataValue(req.getChatUserId(), Types.VARCHAR),
                    new DataValue(req.getChatUserId(), Types.VARCHAR),
                    new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
					new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                    //【ID1037888】[红房子3.0]订单和调拨单记录配送员，配送员电话，统计出来后用于计算配送人员工资，
                    // 之前易成用的是（易成用的要货发货单功能）--服务端  by jinzma 20231215
                    new DataValue(deliveryBy, Types.VARCHAR),
            };
            InsBean ib = new InsBean("DCP_STOCKIN", columns);
            ib.addValues(insValues);
            this.addProcessData(new DataProcessBean(ib));

            //在途仓判断
            String inv_cost_warehouse = req.getInv_cost_warehouse();     //调出门店的在途仓
            if (Enable_InTransit.equals("Y")) {
                if (Check.Null(inv_cost_warehouse)) {
                    errString.append("门店:" + shopId + "在途仓未设置");
                    return false;
                } else {
                    if (inv_cost_warehouse.equals(warehouse)) {
                        errString.append("门店:" + shopId + "启用在途，但在途仓和出货仓设置一致，不能出库");
                        return false;
                    }
                }
            }

            //库存流水处理
            for (Map<String, Object> oneData : getQData) {
                //增加在库数
                String procedure="SP_DCP_StockChange";
                Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                inputParameter.put(1,eId);                                         //--企业ID
                inputParameter.put(2,transferShop);                                //--组织
                inputParameter.put(3,"02");                                        //--单据类型
                inputParameter.put(4,stockInNo);	                               //--单据号
                inputParameter.put(5,oneData.get("ITEM").toString());              //--单据行号
                inputParameter.put(6,"1");                                         //--异动方向 1=加库存 -1=减库存
                inputParameter.put(7,sDate);                                       //--营业日期 yyyy-MM-dd
                inputParameter.put(8,oneData.get("PLUNO").toString());             //--品号
                inputParameter.put(9,oneData.get("FEATURENO").toString());         //--特征码
                inputParameter.put(10,transferWarehouse);                          //--仓库
                inputParameter.put(11,oneData.get("BATCH_NO").toString());         //--批号
                inputParameter.put(12,oneData.get("PUNIT").toString());            //--交易单位
                inputParameter.put(13,oneData.get("PQTY").toString());             //--交易数量
                inputParameter.put(14,oneData.get("BASEUNIT").toString());         //--基准单位
                inputParameter.put(15,oneData.get("BASEQTY").toString());          //--基准数量
                inputParameter.put(16,oneData.get("UNIT_RATIO").toString());       //--换算比例
                inputParameter.put(17,oneData.get("PRICE").toString());            //--零售价
                inputParameter.put(18,oneData.get("AMT").toString());              //--零售金额
                inputParameter.put(19,oneData.get("DISTRIPRICE").toString());      //--进货价
                inputParameter.put(20,oneData.get("DISTRIAMT").toString());        //--进货金额
                inputParameter.put(21,accountDate);                                //--入账日期 yyyy-MM-dd
                inputParameter.put(22,oneData.get("PROD_DATE").toString());        //--批号的生产日期 yyyy-MM-dd
                inputParameter.put(23,sDate);                                      //--单据日期
                inputParameter.put(24,"");                                         //--异动原因
                inputParameter.put(25,"");                                         //--异动描述
                inputParameter.put(26,createBy);                                   //--操作员
                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                this.addProcessData(new DataProcessBean(pdb));

                //启用在途，扣发货门店在途
                if (Enable_InTransit.equals("Y")){
                    //减少在库数
                    Map<Integer,Object> inputParameter1 = new HashMap<Integer, Object>();
                    inputParameter1.put(1,eId );                                     //--企业ID
                    inputParameter1.put(2,shopId );                                  //--组织
                    inputParameter1.put(3,"13");                                     //--单据类型
                    inputParameter1.put(4,stockOutNo);	                             //--单据号
                    inputParameter1.put(5,oneData.get("ITEM").toString());           //--单据行号
                    inputParameter1.put(6,"-1");                                     //--异动方向 1=加库存 -1=减库存
                    inputParameter1.put(7,sDate );                                   //--营业日期 yyyy-MM-dd
                    inputParameter1.put(8,oneData.get("PLUNO").toString());          //--品号
                    inputParameter1.put(9,oneData.get("FEATURENO").toString());      //--特征码
                    inputParameter1.put(10,inv_cost_warehouse );                     //--仓库
                    inputParameter1.put(11,oneData.get("BATCH_NO").toString());      //--批号
                    inputParameter1.put(12,oneData.get("PUNIT").toString());         //--交易单位
                    inputParameter1.put(13,oneData.get("PQTY").toString());          //--交易数量
                    inputParameter1.put(14,oneData.get("BASEUNIT").toString());      //--基准单位
                    inputParameter1.put(15,oneData.get("BASEQTY").toString());       //--基准数量
                    inputParameter1.put(16,oneData.get("UNIT_RATIO").toString());    //--换算比例
                    inputParameter1.put(17,oneData.get("PRICE").toString());         //--零售价
                    inputParameter1.put(18,oneData.get("AMT").toString());           //--零售金额
                    inputParameter1.put(19,oneData.get("DISTRIPRICE").toString());   //--进货价
                    inputParameter1.put(20,oneData.get("DISTRIAMT").toString());     //--进货金额
                    inputParameter1.put(21,accountDate );                            //--入账日期 yyyy-MM-dd
                    inputParameter1.put(22,oneData.get("PROD_DATE").toString());     //--批号的生产日期 yyyy-MM-dd
                    inputParameter1.put(23,sDate);                                   //--单据日期
                    inputParameter1.put(24,"");                                      //--异动原因
                    inputParameter1.put(25,"");                                      //--异动描述
                    inputParameter1.put(26,createBy);                                //--操作员

                    ProcedureBean pdb1 = new ProcedureBean(procedure, inputParameter1);
                    this.addProcessData(new DataProcessBean(pdb1));
                }else{
                    if (Transfer_Stock.equals("2")){
                        //减少调出门店的在库数
                        Map<Integer,Object> inputParameter1 = new HashMap<Integer, Object>();
                        inputParameter1.put(1,eId);                                      //--企业ID
                        inputParameter1.put(2,shopId);                                   //--组织
                        inputParameter1.put(3,"04");                                     //--单据类型
                        inputParameter1.put(4,stockOutNo);	                             //--单据号
                        inputParameter1.put(5,oneData.get("ITEM").toString());           //--单据行号
                        inputParameter1.put(6,"-1");                                     //--异动方向 1=加库存 -1=减库存
                        inputParameter1.put(7,sDate );                                   //--营业日期 yyyy-MM-dd
                        inputParameter1.put(8,oneData.get("PLUNO").toString());          //--品号
                        inputParameter1.put(9,oneData.get("FEATURENO").toString());      //--特征码
                        inputParameter1.put(10,warehouse);                               //--仓库
                        inputParameter1.put(11,oneData.get("BATCH_NO").toString());      //--批号
                        inputParameter1.put(12,oneData.get("PUNIT").toString());         //--交易单位
                        inputParameter1.put(13,oneData.get("PQTY").toString());          //--交易数量
                        inputParameter1.put(14,oneData.get("BASEUNIT").toString());      //--基准单位
                        inputParameter1.put(15,oneData.get("BASEQTY").toString());       //--基准数量
                        inputParameter1.put(16,oneData.get("UNIT_RATIO").toString());    //--换算比例
                        inputParameter1.put(17,oneData.get("PRICE").toString());         //--零售价
                        inputParameter1.put(18,oneData.get("AMT").toString());           //--零售金额
                        inputParameter1.put(19,oneData.get("DISTRIPRICE").toString());   //--进货价
                        inputParameter1.put(20,oneData.get("DISTRIAMT").toString());     //--进货金额
                        inputParameter1.put(21,accountDate );                            //--入账日期 yyyy-MM-dd
                        inputParameter1.put(22,oneData.get("PROD_DATE").toString());     //--批号的生产日期 yyyy-MM-dd
                        inputParameter1.put(23,sDate);                                   //--单据日期
                        inputParameter1.put(24,"");                                      //--异动原因
                        inputParameter1.put(25,"");                                      //--异动描述
                        inputParameter1.put(26,createBy);                                //--操作员

                        ProcedureBean pdb1 = new ProcedureBean(procedure, inputParameter1);
                        this.addProcessData(new DataProcessBean(pdb1));
                    }
                }
            }

            //变更出库单单据状态
            UptBean ub1 = new UptBean("DCP_STOCKOUT");
            // add value
            ub1.addUpdateValue("STATUS", new DataValue("3", Types.VARCHAR));
            ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
            ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

            // condition
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            ub1.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
            ub1.addCondition("STOCKOUTNO", new DataValue(stockOutNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            //变更通知单单据状态
            UptBean ub2 = new UptBean("DCP_RECEIVING");
            // add value
            ub2.addUpdateValue("STATUS", new DataValue("7", Types.VARCHAR));
            ub2.addUpdateValue("COMPLETE_DATE", new DataValue(sDate, Types.VARCHAR));
            ub2.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
            ub2.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

            // condition
            ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub2.addCondition("ORGANIZATIONNO", new DataValue(transferShop, Types.VARCHAR));
            ub2.addCondition("SHOPID", new DataValue(transferShop, Types.VARCHAR));
            ub2.addCondition("RECEIVINGNO", new DataValue(receivingNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub2));

            if (!Check.Null(loadDocType) && !Check.Null(loadDocNo) && loadDocType.equals("1")){
                UptBean ub3 = new UptBean("DCP_ORDER");
                // add value
                ub3.addUpdateValue("PRODUCTSTATUS", new DataValue("6", Types.VARCHAR));
                ub3.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                ub3.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));

                // add condition
                ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub3.addCondition("ORDERNO", new DataValue(loadDocNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub3));
            }
            return true;

        }catch (Exception e){
            errString.append(e.getMessage());
            return false;
        }
    }

    private boolean stockInProcess(DCP_StockOutCancelReq req,List<Map<String, Object>> getQData,String accountDate,StringBuffer errString) throws Exception{
        try {
            String createBy = req.getOpNO();
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String sTime = new SimpleDateFormat("HHmmss").format(new Date());
            String eId = getQData.get(0).get("EID").toString();
            ////调拨出和调拨入shopId始终代表调拨撤销发起门店 by jinzma 20210409
            String shopId = getQData.get(0).get("SHOPID").toString();
            String warehouse = getQData.get(0).get("WAREHOUSE").toString();
            String transferShop = getQData.get(0).get("TRANSFER_SHOP").toString();
            String transferWarehouse  = getQData.get(0).get("TRANSFER_WAREHOUSE").toString();
            String totPqty = getQData.get(0).get("TOT_PQTY").toString();
            String totCqty = getQData.get(0).get("TOT_CQTY").toString();
            String totAmt = getQData.get(0).get("TOT_AMT").toString();
            String totDistriAmt = getQData.get(0).get("TOT_DISTRIAMT").toString();
            String stockOutNo = getStockOutNo(eId,transferShop,accountDate);
            String receivingNo = getReceivingNo(eId,shopId,accountDate);
            String stockInNo = getStockInNo(eId,shopId,accountDate);
            String deliveryBy = getQData.get(0).get("DELIVERYBY").toString();

            //新增调拨出库单单头和单身
            String[] stockOutColumns = {
                    "EID","ORGANIZATIONNO","SHOPID","STOCKOUTNO","DOC_TYPE","WAREHOUSE","TRANSFER_SHOP","TRANSFER_WAREHOUSE",
                    "BDATE","MEMO","STATUS","TOT_PQTY","TOT_CQTY","TOT_AMT","TOT_DISTRIAMT","PROCESS_STATUS",
                    "CREATEBY","CREATE_DATE","CREATE_TIME","ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME",
                    "CONFIRMBY","CONFIRM_DATE","CONFIRM_TIME","SUBMITBY","SUBMIT_DATE","SUBMIT_TIME",
                    "CREATE_CHATUSERID","ACCOUNT_CHATUSERID","UPDATE_TIME","TRAN_TIME","DELIVERYBY"
            };
            String[] stockOutDetailColumns = {
                    "EID","ORGANIZATIONNO","SHOPID","STOCKOUTNO","ITEM","PLUNO","FEATURENO","BATCH_NO","PROD_DATE","WAREHOUSE",
                    "PQTY","BASEQTY","PUNIT","BASEUNIT","UNIT_RATIO","PRICE","DISTRIPRICE","AMT","DISTRIAMT","BDATE"
            };
            //新增收货通知单单头和单身
            String[] receivingColumns = {
                    "EID","ORGANIZATIONNO","SHOPID","RECEIVINGNO","DOC_TYPE","WAREHOUSE","TRANSFER_SHOP",
                    "BDATE","MEMO","STATUS","LOAD_DOCTYPE","LOAD_DOCNO","COMPLETE_DATE","PROCESS_STATUS",
                    "TOT_CQTY","TOT_PQTY","TOT_AMT","TOT_DISTRIAMT",
                    "CREATEBY","CREATE_DATE","CREATE_TIME","SUBMITBY","SUBMIT_DATE","SUBMIT_TIME",
                    "CONFIRMBY","CONFIRM_DATE","CONFIRM_TIME","ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME",
                    "UPDATE_TIME","TRAN_TIME","DELIVERYBY"
            };
            String[] receivingDetailColumns = {
                    "EID","ORGANIZATIONNO","SHOPID","RECEIVINGNO","WAREHOUSE","ITEM","OITEM","PLUNO","FEATURENO",
                    "BATCH_NO","PROD_DATE","PQTY","BASEQTY","PUNIT","BASEUNIT","UNIT_RATIO",
                    "PRICE","DISTRIPRICE","AMT","DISTRIAMT","OTYPE","OFNO","BDATE","STATUS"
            };
            //新增调拨入库单单头和单身
            String[] stockInColumns = {"EID","SHOPID","ORGANIZATIONNO","STOCKINNO","BDATE","MEMO","STATUS",
                    "DOC_TYPE","OTYPE","OFNO","WAREHOUSE","LOAD_DOCNO","PROCESS_STATUS","TRANSFER_SHOP","TRANSFER_WAREHOUSE",
                    "TOT_PQTY","TOT_CQTY","TOT_AMT","TOT_DISTRIAMT",
                    "CREATEBY","CREATE_DATE","CREATE_TIME","CONFIRMBY","CONFIRM_DATE","CONFIRM_TIME",
                    "ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME","SUBMITBY","SUBMIT_DATE","SUBMIT_TIME",
                    "CREATE_CHATUSERID","ACCOUNT_CHATUSERID","UPDATE_TIME","TRAN_TIME","DELIVERYBY"
            };
            String[] stockInDetailColumns = {
                    "EID","SHOPID","ORGANIZATIONNO","STOCKINNO","ITEM","OITEM","PLUNO","FEATURENO","BATCH_NO","PROD_DATE",
                    "WAREHOUSE","BDATE","PQTY","BASEQTY","PUNIT","BASEUNIT","RECEIVING_QTY","UNIT_RATIO",
                    "PRICE","DISTRIPRICE","AMT","DISTRIAMT","OFNO","OTYPE"
            };

            for (Map<String, Object> oneData : getQData) {
                String item = oneData.get("ITEM").toString();
                String pluNo = oneData.get("PLUNO").toString();
                String featureNo = oneData.get("FEATURENO").toString();
                String batchNo = oneData.get("BATCH_NO").toString();
                String prodDate = oneData.get("PROD_DATE").toString();
                String pqty = oneData.get("PQTY").toString();
                String baseQty = oneData.get("BASEQTY").toString();
                String punit = oneData.get("PUNIT").toString();
                String baseUnit = oneData.get("BASEUNIT").toString();
                String unitRatio = oneData.get("UNIT_RATIO").toString();
                String price = oneData.get("PRICE").toString();
                String distriPrice = oneData.get("DISTRIPRICE").toString();
                String amt = oneData.get("AMT").toString();
                String distriAmt = oneData.get("DISTRIAMT").toString();

                //新增调拨出库单单身
                DataValue[] stockOutDetailInsValue = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(transferShop, Types.VARCHAR),
                        new DataValue(transferShop, Types.VARCHAR),
                        new DataValue(stockOutNo, Types.VARCHAR),
                        new DataValue(item, Types.VARCHAR),
                        new DataValue(pluNo, Types.VARCHAR),
                        new DataValue(featureNo, Types.VARCHAR),
                        new DataValue(batchNo, Types.VARCHAR),
                        new DataValue(prodDate, Types.VARCHAR),
                        new DataValue(transferWarehouse, Types.VARCHAR),
                        new DataValue(pqty, Types.VARCHAR),
                        new DataValue(baseQty, Types.VARCHAR),
                        new DataValue(punit, Types.VARCHAR),
                        new DataValue(baseUnit, Types.VARCHAR),
                        new DataValue(unitRatio, Types.VARCHAR),
                        new DataValue(price, Types.VARCHAR),
                        new DataValue(distriPrice, Types.VARCHAR),
                        new DataValue(amt, Types.VARCHAR),
                        new DataValue(distriAmt, Types.VARCHAR),
                        new DataValue(sDate, Types.VARCHAR),
                };
                InsBean stockOutDetailIb = new InsBean("DCP_STOCKOUT_DETAIL", stockOutDetailColumns);
                stockOutDetailIb.addValues(stockOutDetailInsValue);
                this.addProcessData(new DataProcessBean(stockOutDetailIb));

                //新增收货通知单单身
                DataValue[] receivingDetailInsValue = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(receivingNo, Types.VARCHAR),
                        new DataValue(warehouse, Types.VARCHAR),
                        new DataValue(item, Types.VARCHAR),
                        new DataValue(item, Types.VARCHAR),
                        new DataValue(pluNo, Types.VARCHAR),
                        new DataValue(featureNo, Types.VARCHAR),
                        new DataValue(batchNo, Types.VARCHAR),
                        new DataValue(prodDate, Types.VARCHAR),
                        new DataValue(pqty, Types.VARCHAR),
                        new DataValue(baseQty, Types.VARCHAR),
                        new DataValue(punit, Types.VARCHAR),
                        new DataValue(baseUnit, Types.VARCHAR),
                        new DataValue(unitRatio, Types.VARCHAR),
                        new DataValue(price, Types.VARCHAR),
                        new DataValue(distriPrice, Types.VARCHAR),
                        new DataValue(amt, Types.VARCHAR),
                        new DataValue(distriAmt, Types.VARCHAR),
                        new DataValue("1", Types.VARCHAR),
                        new DataValue(stockOutNo, Types.VARCHAR),
                        new DataValue(sDate, Types.VARCHAR),
                        new DataValue("100", Types.VARCHAR),
                };
                InsBean receivingDetailIb = new InsBean("DCP_RECEIVING_DETAIL", receivingDetailColumns);
                receivingDetailIb.addValues(receivingDetailInsValue);
                this.addProcessData(new DataProcessBean(receivingDetailIb));

                //新增调拨入库单单身
                DataValue[] stockInDetailInsValue = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(stockInNo, Types.VARCHAR),
                        new DataValue(item, Types.VARCHAR),
                        new DataValue(item, Types.VARCHAR),
                        new DataValue(pluNo, Types.VARCHAR),
                        new DataValue(featureNo, Types.VARCHAR),
                        new DataValue(batchNo, Types.VARCHAR),
                        new DataValue(prodDate, Types.VARCHAR),
                        new DataValue(warehouse, Types.VARCHAR),
                        new DataValue(sDate, Types.VARCHAR),
                        new DataValue(pqty, Types.VARCHAR),
                        new DataValue(baseQty, Types.VARCHAR),
                        new DataValue(punit, Types.VARCHAR),
                        new DataValue(baseUnit, Types.VARCHAR),
                        new DataValue(pqty, Types.VARCHAR),
                        new DataValue(unitRatio, Types.VARCHAR),
                        new DataValue(price, Types.VARCHAR),
                        new DataValue(distriPrice, Types.VARCHAR),
                        new DataValue(amt, Types.VARCHAR),
                        new DataValue(distriAmt, Types.VARCHAR),
                        new DataValue(receivingNo, Types.VARCHAR),
                        new DataValue("1", Types.VARCHAR),
                };
                InsBean stockInDetailIb = new InsBean("DCP_STOCKIN_DETAIL", stockInDetailColumns);
                stockInDetailIb.addValues(stockInDetailInsValue);
                this.addProcessData(new DataProcessBean(stockInDetailIb));

                //扣调拨出库存
                String procedure="SP_DCP_StockChange";
                Map<Integer,Object> inputParameterOut = new HashMap<Integer, Object>();
                inputParameterOut.put(1,eId);                 //--企业ID
                inputParameterOut.put(2,transferShop);        //--组织
                inputParameterOut.put(3,"04");                //--单据类型 04调拨出库
                inputParameterOut.put(4,stockOutNo);	      //--单据号
                inputParameterOut.put(5,item);                //--单据行号
                inputParameterOut.put(6,"-1");                //--异动方向 1=加库存 -1=减库存
                inputParameterOut.put(7,sDate);               //--营业日期 yyyy-MM-dd
                inputParameterOut.put(8,pluNo);               //--品号
                inputParameterOut.put(9,featureNo);           //--特征码
                inputParameterOut.put(10,transferWarehouse);  //--仓库
                inputParameterOut.put(11,batchNo);            //--批号
                inputParameterOut.put(12,punit);              //--交易单位
                inputParameterOut.put(13,pqty);               //--交易数量
                inputParameterOut.put(14,baseUnit);           //--基准单位
                inputParameterOut.put(15,baseQty);            //--基准数量
                inputParameterOut.put(16,unitRatio);          //--换算比例
                inputParameterOut.put(17,price);              //--零售价
                inputParameterOut.put(18,amt);                //--零售金额
                inputParameterOut.put(19,distriPrice);        //--进货价
                inputParameterOut.put(20,distriAmt);          //--进货金额
                inputParameterOut.put(21,accountDate);        //--入账日期 yyyy-MM-dd
                inputParameterOut.put(22,prodDate);           //--批号的生产日期 yyyy-MM-dd
                inputParameterOut.put(23,sDate);              //--单据日期
                inputParameterOut.put(24,"");                 //--异动原因
                inputParameterOut.put(25,"");                 //--异动描述
                inputParameterOut.put(26,createBy);           //--操作员
                ProcedureBean pdbOut = new ProcedureBean(procedure, inputParameterOut);
                this.addProcessData(new DataProcessBean(pdbOut));

                //加调拨入库存
                Map<Integer,Object> inputParameterInput = new HashMap<Integer, Object>();
                inputParameterInput.put(1,eId );                //--企业ID
                inputParameterInput.put(2,shopId );             //--组织
                inputParameterInput.put(3,"02");                //--单据类型  02调拨收货
                inputParameterInput.put(4,stockInNo);	        //--单据号
                inputParameterInput.put(5,item);                //--单据行号
                inputParameterInput.put(6,"1");                 //--异动方向 1=加库存 -1=减库存
                inputParameterInput.put(7,sDate );              //--营业日期 yyyy-MM-dd
                inputParameterInput.put(8,pluNo);               //--品号
                inputParameterInput.put(9,featureNo);           //--特征码
                inputParameterInput.put(10,warehouse );         //--仓库
                inputParameterInput.put(11,batchNo);            //--批号
                inputParameterInput.put(12,punit);              //--交易单位
                inputParameterInput.put(13,pqty);               //--交易数量
                inputParameterInput.put(14,baseUnit);           //--基准单位
                inputParameterInput.put(15,baseQty);            //--基准数量
                inputParameterInput.put(16,unitRatio);          //--换算比例
                inputParameterInput.put(17,price);              //--零售价
                inputParameterInput.put(18,amt);                //--零售金额
                inputParameterInput.put(19,distriPrice);        //--进货价
                inputParameterInput.put(20,distriAmt);          //--进货金额
                inputParameterInput.put(21,accountDate );       //--入账日期 yyyy-MM-dd
                inputParameterInput.put(22,prodDate);           //--批号的生产日期 yyyy-MM-dd
                inputParameterInput.put(23,sDate);              //--单据日期
                inputParameterInput.put(24,"");                 //--异动原因
                inputParameterInput.put(25,"");                 //--异动描述
                inputParameterInput.put(26,createBy);           //--操作员
                ProcedureBean pdbIn = new ProcedureBean(procedure, inputParameterInput);
                this.addProcessData(new DataProcessBean(pdbIn));
            }

            //新增调拨出库单单头
            DataValue[] stockOutInsValue = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(transferShop, Types.VARCHAR),
                    new DataValue(transferShop, Types.VARCHAR),
                    new DataValue(stockOutNo, Types.VARCHAR),
                    new DataValue("1", Types.VARCHAR),
                    new DataValue(transferWarehouse, Types.VARCHAR),
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue(warehouse, Types.VARCHAR),
                    new DataValue(sDate, Types.VARCHAR),
                    new DataValue("调拨单撤销", Types.VARCHAR),
                    new DataValue("3", Types.VARCHAR),
                    new DataValue(totPqty, Types.VARCHAR),
                    new DataValue(totCqty, Types.VARCHAR),
                    new DataValue(totAmt, Types.VARCHAR),
                    new DataValue(totDistriAmt, Types.VARCHAR),
                    new DataValue("N", Types.VARCHAR),
                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(sDate, Types.VARCHAR),
                    new DataValue(sTime, Types.VARCHAR),
                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(accountDate, Types.VARCHAR),
                    new DataValue(sTime, Types.VARCHAR),
                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(sDate, Types.VARCHAR),
                    new DataValue(sTime, Types.VARCHAR),
                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(sDate, Types.VARCHAR),
                    new DataValue(sTime, Types.VARCHAR),
                    new DataValue(req.getChatUserId(), Types.VARCHAR),
                    new DataValue(req.getChatUserId(), Types.VARCHAR),
					new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
					new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                    //【ID1037888】[红房子3.0]订单和调拨单记录配送员，配送员电话，统计出来后用于计算配送人员工资，
                    // 之前易成用的是（易成用的要货发货单功能）--服务端  by jinzma 20231215
                    new DataValue(deliveryBy, Types.VARCHAR),
            
            };
            InsBean stockOutIb = new InsBean("DCP_STOCKOUT", stockOutColumns);
            stockOutIb.addValues(stockOutInsValue);
            this.addProcessData(new DataProcessBean(stockOutIb));

            //新增收货通知单单头
            DataValue[] receivingInsValue = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue(receivingNo, Types.VARCHAR),
                    new DataValue("1", Types.VARCHAR),
                    new DataValue(warehouse, Types.VARCHAR),
                    new DataValue(transferShop, Types.VARCHAR),
                    new DataValue(sDate, Types.VARCHAR),
                    new DataValue("调拨单撤销", Types.VARCHAR),
                    new DataValue("7", Types.VARCHAR),
                    new DataValue("", Types.VARCHAR),
                    new DataValue(stockOutNo, Types.VARCHAR),
                    new DataValue(sDate, Types.VARCHAR),
                    new DataValue("", Types.VARCHAR),
                    new DataValue(totCqty, Types.VARCHAR),
                    new DataValue(totPqty, Types.VARCHAR),
                    new DataValue(totAmt, Types.VARCHAR),
                    new DataValue(totDistriAmt, Types.VARCHAR),
                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(sDate, Types.VARCHAR),
                    new DataValue(sTime, Types.VARCHAR),
                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(sDate, Types.VARCHAR),
                    new DataValue(sTime, Types.VARCHAR),
                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(sDate, Types.VARCHAR),
                    new DataValue(sTime, Types.VARCHAR),
                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(accountDate, Types.VARCHAR),
                    new DataValue(sTime, Types.VARCHAR),
					new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
					new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                    //【ID1037888】[红房子3.0]订单和调拨单记录配送员，配送员电话，统计出来后用于计算配送人员工资，
                    // 之前易成用的是（易成用的要货发货单功能）--服务端  by jinzma 20231215
                    new DataValue(deliveryBy, Types.VARCHAR),
            };
            InsBean receivingIb = new InsBean("DCP_RECEIVING", receivingColumns);
            receivingIb.addValues(receivingInsValue);
            this.addProcessData(new DataProcessBean(receivingIb));

            //新增调拨入库单单头
            DataValue[] stockInInsValue = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue(stockInNo, Types.VARCHAR),
                    new DataValue(sDate, Types.VARCHAR),
                    new DataValue("调拨单撤销", Types.VARCHAR),
                    new DataValue("2", Types.VARCHAR),
                    new DataValue("1", Types.VARCHAR),
                    new DataValue("1", Types.VARCHAR),
                    new DataValue(receivingNo, Types.VARCHAR),
                    new DataValue(warehouse, Types.VARCHAR),
                    new DataValue(stockOutNo, Types.VARCHAR),
                    new DataValue("N", Types.VARCHAR),
                    new DataValue(transferShop, Types.VARCHAR),
                    new DataValue(transferWarehouse, Types.VARCHAR),
                    new DataValue(totPqty, Types.VARCHAR),
                    new DataValue(totCqty, Types.VARCHAR),
                    new DataValue(totAmt, Types.VARCHAR),
                    new DataValue(totDistriAmt, Types.VARCHAR),
                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(sDate, Types.VARCHAR),
                    new DataValue(sTime, Types.VARCHAR),
                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(sDate, Types.VARCHAR),
                    new DataValue(sTime, Types.VARCHAR),
                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(accountDate, Types.VARCHAR),
                    new DataValue(sTime, Types.VARCHAR),
                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(sDate, Types.VARCHAR),
                    new DataValue(sTime, Types.VARCHAR),
                    new DataValue(req.getChatUserId(), Types.VARCHAR),
                    new DataValue(req.getChatUserId(), Types.VARCHAR),
                    new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
					new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                    //【ID1037888】[红房子3.0]订单和调拨单记录配送员，配送员电话，统计出来后用于计算配送人员工资，
                    // 之前易成用的是（易成用的要货发货单功能）--服务端  by jinzma 20231215
                    new DataValue(deliveryBy, Types.VARCHAR),
            };
            InsBean stockInIb = new InsBean("DCP_STOCKIN", stockInColumns);
            stockInIb.addValues(stockInInsValue);
            this.addProcessData(new DataProcessBean(stockInIb));

            return true;

        }catch (Exception e){
            errString.append(e.getMessage());
            return false;
        }
    }

    private String getStockInNo(String eId,String shopId,String accountDate) throws Exception {
        String stockInNo = "DBSH" + accountDate;
        String sql=""
                + " select max(stockinno) as stockinno from dcp_stockin"
                + " where eid='"+eId+"' and shopid='"+shopId+"' and organizationno='"+shopId+"' and stockinno like '%"+stockInNo+"%'"
                + " ";
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        if (getQData != null && !getQData.isEmpty()) {
            stockInNo = getQData.get(0).get("STOCKINNO").toString();
            if (!Check.Null(stockInNo)){
                stockInNo = stockInNo.substring(4);
                long i = Long.parseLong(stockInNo) + 1;
                stockInNo = i + "";
                stockInNo = "DBSH" + stockInNo;
            }else{
                stockInNo = "DBSH" + accountDate + "00001";
            }
        } else {
            stockInNo = "DBSH" + accountDate + "00001";
        }
        return stockInNo;
    }

    private String getStockOutNo(String eId,String shopId,String accountDate) throws Exception {
        String stockOutNo = "DBCK" + accountDate;
        String sql=""
                + " select max(stockoutno) as stockoutno from dcp_stockout"
                + " where eid='"+eId+"' and shopid='"+shopId+"' and organizationno='"+shopId+"' and stockoutno like '%"+stockOutNo+"%'"
                + " ";
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        if (getQData != null && !getQData.isEmpty()) {
            stockOutNo = getQData.get(0).get("STOCKOUTNO").toString();
            if (!Check.Null(stockOutNo)){
                stockOutNo = stockOutNo.substring(4);
                long i = Long.parseLong(stockOutNo) + 1;
                stockOutNo = i + "";
                stockOutNo = "DBCK" + stockOutNo;
            }else{
                stockOutNo = "DBCK" + accountDate + "00001";
            }
        } else {
            stockOutNo = "DBCK" + accountDate + "00001";
        }
        return stockOutNo;
    }

    private String getReceivingNo(String eId,String shopId,String accountDate) throws Exception {
        String receivingNo = "SHTZ" + accountDate;
        String sql=""
                + " select max(receivingno) as receivingno from dcp_receiving"
                + " where eid='"+eId+"' and shopid='"+shopId+"' and organizationno='"+shopId+"' and receivingno like '%"+receivingNo+"%'"
                + " ";
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        if (getQData != null && !getQData.isEmpty()) {
            receivingNo = getQData.get(0).get("RECEIVINGNO").toString();
            if (!Check.Null(receivingNo)){
                receivingNo = receivingNo.substring(4);
                long i = Long.parseLong(receivingNo) + 1;
                receivingNo = i + "";
                receivingNo = "SHTZ" + receivingNo;
            }else{
                receivingNo = "SHTZ" + accountDate + "00001";
            }
        } else {
            receivingNo = "SHTZ" + accountDate + "00001";
        }
        return receivingNo;
    }

}
