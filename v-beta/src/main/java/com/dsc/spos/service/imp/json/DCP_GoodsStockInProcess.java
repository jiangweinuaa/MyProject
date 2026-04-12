package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_GoodsStockInProcessReq;
import com.dsc.spos.json.cust.req.DCP_GoodsStockInProcessReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_StockInProcessReq;
import com.dsc.spos.json.cust.res.DCP_GoodsStockInProcessRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @apiNote 商品收货
 * @since 2021-04-23
 * @author jinzma
 */
public class DCP_GoodsStockInProcess extends SPosAdvanceService<DCP_GoodsStockInProcessReq,DCP_GoodsStockInProcessRes> {
    
    Logger logger = LogManager.getLogger(DCP_GoodsStockInProcess.class.getName());
    @Override
    protected void processDUID(DCP_GoodsStockInProcessReq req, DCP_GoodsStockInProcessRes res) throws Exception {
        DCP_GoodsStockInProcessRes.levelElm datas = res.new levelElm();
        String eId = req.geteId();
        String shopId = req.getShopId();
        List<level1Elm> pluList = req.getRequest().getPluList();
        StringBuffer errString = new StringBuffer();
        try {
            MyCommon mc = new MyCommon();
            Map<String,String> map = new HashMap<String, String>();
            StringBuilder sJoinReceivingNo = new StringBuilder();
            StringBuilder sJoinItem = new StringBuilder();
            StringBuilder sJoinPluNo = new StringBuilder();
            StringBuilder sJoinFeatureNo = new StringBuilder();
            StringBuilder sJoinPunit = new StringBuilder();
            StringBuilder sJoinBaseUnit = new StringBuilder();
            StringBuilder sJoinPqty = new StringBuilder();
            StringBuilder sJoinBaseQty = new StringBuilder();
            StringBuilder sJoinPrice = new StringBuilder();
            StringBuilder sJoinDistriPrice = new StringBuilder();
            StringBuilder sJoinAmt = new StringBuilder();
            StringBuilder sJoinDistriAmt = new StringBuilder();
            
            for(level1Elm par :pluList) {
                sJoinReceivingNo.append(par.getReceivingNo()).append(',');
                sJoinItem.append(par.getItem()).append(',');
                sJoinPluNo.append(par.getPluNo()).append(',');
                sJoinFeatureNo.append(par.getFeatureNo()).append(',');
                sJoinPunit.append(par.getPunit()).append(',');
                sJoinBaseUnit.append(par.getBaseUnit()).append(',');
                sJoinPqty.append(par.getPqty()).append(',');
                sJoinBaseQty.append(par.getBaseQty()).append(',');
                sJoinPrice.append(par.getPrice()).append(',');
                sJoinDistriPrice.append(par.getDistriPrice()).append(',');
                sJoinAmt.append(par.getAmt()).append(',');
                sJoinDistriAmt.append(par.getDistriAmt()).append(',');
            }
            map.put("REQ_RECEIVINGNO", sJoinReceivingNo.toString());
            map.put("REQ_ITEM", sJoinItem.toString());
            map.put("REQ_PLUNO", sJoinPluNo.toString());
            map.put("REQ_FEATURENO", sJoinFeatureNo.toString());
            map.put("REQ_PUNIT", sJoinPunit.toString());
            map.put("REQ_BASEUNIT", sJoinBaseUnit.toString());
            map.put("REQ_PQTY", sJoinPqty.toString());
            map.put("REQ_BASEQTY", sJoinBaseQty.toString());
            map.put("REQ_PRICE", sJoinPrice.toString());
            map.put("REQ_DISTRIPRICE", sJoinDistriPrice.toString());
            map.put("REQ_AMT", sJoinAmt.toString());
            map.put("REQ_DISTRIAMT", sJoinDistriAmt.toString());
            String withReceiving = mc.getFormatSourceMultiColWith(map);
            
            String sqlbuf = " with receivings as (" + withReceiving + ")"
                    + " select a.eid,a.shopid,a.receivingno,a.status,a.warehouse,a.memo,a.transfer_shop,"
                    + " a.Load_DocNo,a.Load_Doctype,a.ptemplateno,a.receiptdate,a.packingno as receivingpackingno, "
                    + " d.oitem as stockin_oitem,d.pluno as stockin_pluno,"
                    + " e.stockinno,e.status as stockinstatus,"
                    + " b.item,b.otype,b.pluno,b.punit,b.baseqty,b.unit_ratio,b.baseunit,b.oitem,b.poqty,b.pqty,b.ofno,b.proc_rate,"
                    + " b.price,b.amt,b.distriprice,b.distriamt,b.plu_memo,b.stockin_qty,b.batch_no,b.prod_date,b.featureno,"
                    + " b.packingno,b.status as detailstatus,"
                    + " c.req_receivingno,c.req_item,c.req_pluno,c.req_featureno,c.req_punit,c.req_baseunit,c.req_pqty,"
                    + " c.req_baseqty,c.req_price,c.req_distriprice,c.req_amt,c.req_distriamt"
                    + " from dcp_receiving a"
                    + " inner join dcp_receiving_detail b on a.eid=b.eid and a.shopid=b.shopid and a.receivingno=b.receivingno"
                    + " inner join receivings c on a.receivingno=c.req_receivingno and b.item=c.req_item"
                    + " left  join ("
                    + "   select a.eid,a.shopid,a.ofno,b.oitem,b.pluno from dcp_stockin a"
                    + "   inner join dcp_stockin_detail b on a.eid=b.eid and a.shopid=b.shopid and a.stockinno=b.stockinno"
                    + "   where a.eid='" + eId + "' and a.shopid='" + shopId + "' and a.doc_type='0'"
                    + " )d on a.eid=d.eid and a.shopid=d.shopid and a.receivingno=d.ofno and b.item=d.oitem"
                    + " left  join dcp_stockin e on a.eid=e.eid and a.shopid=e.shopid and a.receivingno=e.ofno and e.doc_type='0'"
                    + " where a.eid='" + eId + "' and a.shopid='" + shopId + "' and a.doc_type='0'"
                    + " order by b.receivingno,b.item"
                    + " ";
            List<Map<String, Object>> getQData = this.doQueryData(sqlbuf,null);
            if (getQData != null && !getQData.isEmpty()) {
                //待收货资料检查
                if (isErrorCheck(req,getQData,errString)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, errString.toString());
                }
                //获取入账日期
                String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
                
                //【ID1023280】【货郎】PDA商品收货,优化(自动确认) by jinzma 20220112
                //增加参数商品收货是否自动确认GoodsStockinAutoConfirm，默认为N 如果为Y，如果收货通知单明细都完成，自动确认收货
                String GoodsStockinAutoConfirm = PosPub.getPARA_SMS(dao,eId,shopId,"GoodsStockinAutoConfirm");
                if (Check.Null(GoodsStockinAutoConfirm)){
                    GoodsStockinAutoConfirm="N";
                }
                
                //获取收货通知单单头
                Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
                condition.put("RECEIVINGNO", true);
                //调用过滤函数
                List<Map<String, Object>> getQReceiving= MapDistinct.getMap(getQData,condition);
                for (Map<String, Object> oneData:getQReceiving){
                    String receivingNo = oneData.get("RECEIVINGNO").toString();
                    Map<String, Object> condiV=new HashMap<String, Object>();
                    condiV.put("RECEIVINGNO",receivingNo);
                    List<Map<String, Object>> receivingList= MapDistinct.getWhereMap(getQData, condiV, true);
                    String stockInNo = oneData.get("STOCKINNO").toString();
                    if (Check.Null(stockInNo)){
                        //未产生入库单，需要新增入库单和单身
                        if (!insertStockin(req,receivingList,receivingNo,accountDate,errString)){
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, errString.toString());
                        }
                    }else{
                        //已产生入库单，新增入库单单身并刷新单头数据
                        if (!updateStockin(req,receivingList,receivingNo,stockInNo,errString)){
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, errString.toString());
                        }
                    }
                    //每处理一张收货单就进行提交，避免收货单号连续产生和整单收货单号重叠
                    this.doExecuteDataToDB();
                    
                    //【ID1023280】【货郎】PDA商品收货,优化(自动确认) by jinzma 20220112
                    if (GoodsStockinAutoConfirm.equals("Y")){
                        stockinAutoConfirm(req,eId,shopId,receivingNo);
                    }
                    
                }
            }else{
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,"未查询到可收货的商品资料，请确认!");
            }
            
            this.doExecuteDataToDB();
            
            res.setDatas(datas);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            
        }catch(Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,e.getMessage());
        }
        
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_GoodsStockInProcessReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_GoodsStockInProcessReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_GoodsStockInProcessReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_GoodsStockInProcessReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String bDate = req.getRequest().getbDate();
        if (Check.Null(bDate)) {
            errMsg.append("单据日期不能为空,");
            isFail = true;
        }
        List<level1Elm> pluList = req.getRequest().getPluList();
        if (pluList==null || pluList.isEmpty()){
            errMsg.append("商品列表不能为空,");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        for (level1Elm plu:pluList){
            if (Check.Null(plu.getReceivingNo())) {
                errMsg.append("收货通知单单号不能为空,");
                isFail = true;
            }
            if (Check.Null(plu.getItem())) {
                errMsg.append("项次不能为空,");
                isFail = true;
            }
            if (Check.Null(plu.getPluNo())) {
                errMsg.append("品号不能为空,");
                isFail = true;
            }
            if (Check.Null(plu.getFeatureNo())) {
                errMsg.append("特征码不能为空,");
                isFail = true;
            }
            if (Check.Null(plu.getPunit())) {
                errMsg.append("配送单位不能为空,");
                isFail = true;
            }
            if (Check.Null(plu.getBaseUnit())) {
                errMsg.append("基准单位不能为空,");
                isFail = true;
            }
            if (Check.Null(plu.getUnitRatio())) {
                errMsg.append("单位换算率不能为空,");
                isFail = true;
            }
            if (Check.Null(plu.getPqty())) {
                errMsg.append("配送单位收货数量不能为空,");
                isFail = true;
            }
            if (Check.Null(plu.getReceivingQty())) {
                errMsg.append("配送单位配送数量不能为空,");
                isFail = true;
            }
            if (Check.Null(plu.getPoQty())) {
                errMsg.append("配送单位要货数量不能为空,");
                isFail = true;
            }
            if (Check.Null(plu.getBaseQty())) {
                errMsg.append("基准单位收货数量不能为空,");
                isFail = true;
            }
            if (Check.Null(plu.getPackingNo())) {
                errMsg.append("装箱单号不能为空,");
                isFail = true;
            }
            if (Check.Null(plu.getPrice())) {
                errMsg.append("零售价不能为空,");
                isFail = true;
            }
            if (Check.Null(plu.getAmt())) {
                errMsg.append("零售价合计不能为空,");
                isFail = true;
            }
            if (Check.Null(plu.getDistriPrice())) {
                errMsg.append("进货价不能为空,");
                isFail = true;
            }
            if (Check.Null(plu.getDistriAmt())) {
                errMsg.append("进货价合计不能为空,");
                isFail = true;
            }
            
            if (isFail) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "项次："+plu.getItem()+" "+errMsg);
            }
        }
        
        return isFail;
    }
    
    @Override
    protected TypeToken<DCP_GoodsStockInProcessReq> getRequestType() {
        return new TypeToken<DCP_GoodsStockInProcessReq>(){};
    }
    
    @Override
    protected DCP_GoodsStockInProcessRes getResponseType() {
        return new DCP_GoodsStockInProcessRes();
    }
    
    private boolean isErrorCheck(DCP_GoodsStockInProcessReq req,List<Map<String, Object>> getQData,StringBuffer errString) throws Exception{
        List<level1Elm> pluList = req.getRequest().getPluList();
        if (pluList.size()!=getQData.size()){
            errString.append("来源单号和项次未匹配到资料，请确认!");
            return true;
        }
        for (Map<String, Object> oneData:getQData){
            String receivingNo = oneData.get("RECEIVINGNO").toString();
            String status = oneData.get("STATUS").toString();
            String detailStatus = oneData.get("DETAILSTATUS").toString();
            String stockinOitem = oneData.get("STOCKIN_OITEM").toString();
            String stockinPluno = oneData.get("STOCKIN_PLUNO").toString();
            String stockinNo = oneData.get("STOCKINNO").toString();
            String stockinStatus = oneData.get("STOCKINSTATUS").toString();
            String pluNo = oneData.get("PLUNO").toString();
            String featureNo = oneData.get("FEATURENO").toString();
            String punit = oneData.get("PUNIT").toString();
            String baseUnit = oneData.get("BASEUNIT").toString();
            String req_pluNo = oneData.get("REQ_PLUNO").toString();
            String req_featureNo = oneData.get("REQ_FEATURENO").toString();
            String req_punit = oneData.get("REQ_PUNIT").toString();
            String req_baseunit = oneData.get("REQ_BASEUNIT").toString();
            
            //根据入参ofNo找收货通知单,单头status<>6 报错
            if (Check.Null(status) || !status.equals("6")){
                errString.append("收货通知单:"+receivingNo+"已确认!");
                return true;
            }
            //根据入参ofNo+ITEM找对应的收货通知单单身,单身status==100 报错
            if (!Check.Null(detailStatus) && detailStatus.equals("100")){
                errString.append("收货通知单:"+receivingNo+" 商品:"+pluNo+" 已完成收货!");
                return true;
            }
            //根据入参ofNo+ITEM找对应的入库单单身,找到则报错
            if (!Check.Null(stockinOitem)){
                errString.append("收货通知单:"+receivingNo+" 商品:"+pluNo+" 已完成收货!");
                return true;
            }
            //根据入库单状态非空且<>0,则报错
            if (!Check.Null(stockinStatus) && !stockinStatus.equals("0")){
                errString.append("收货通知单:"+receivingNo+" 商品:"+pluNo+" 对应的入库单:"+stockinNo+"已完成确认!");
                return true;
            }
            //入库单PLUNO<>收货通知单PLUNO,则报错
            if (!Check.Null(stockinPluno) && stockinPluno.equals(pluNo)){
                errString.append("收货通知单:"+receivingNo+" 商品:"+pluNo+" 对应的入库单商品不一致,请确认!");
                return true;
            }
            //前端请求资料<>收货通知单资料,则报错
            if(!req_pluNo.equals(pluNo) || !req_featureNo.equals(featureNo) || !req_punit.equals(punit) || !req_baseunit.equals(baseUnit)){
                errString.append("收货通知单:"+receivingNo+" 商品:"+pluNo+" 对应的前端请求商品不一致,请确认!");
                return true;
            }
        }
        return false;
    }
    
    private boolean insertStockin(DCP_GoodsStockInProcessReq req,List<Map<String, Object>> receivingList,String receivingNo,String accountDate,StringBuffer errString) throws Exception{
        String eId = req.geteId();
        String shopId = req.getShopId();
        String stockInNo = getStockInNo(eId,shopId,accountDate);
        String createBy = req.getOpNO();
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String sTime = new SimpleDateFormat("HHmmss").format(new Date());
        String warehouse = receivingList.get(0).get("WAREHOUSE").toString();
        String memo = receivingList.get(0).get("MEMO").toString();
        String transferShop = receivingList.get(0).get("TRANSFER_SHOP").toString();
        String loadDocType = receivingList.get(0).get("LOAD_DOCTYPE").toString();
        String loadDocNo = receivingList.get(0).get("LOAD_DOCNO").toString();
        String ptemplateNo = receivingList.get(0).get("PTEMPLATENO").toString();
        String receiptDate = receivingList.get(0).get("RECEIPTDATE").toString();
        String receivingPackingNo = receivingList.get(0).get("RECEIVINGPACKINGNO").toString();
        String bDate = req.getRequest().getbDate();
        
        if (Check.Null(warehouse)){
            warehouse = req.getIn_cost_warehouse();
        }
        if (Check.Null(warehouse)){
            errString.append("门店默认收货仓获取失败!");
            return false ;
        }
        
        try {
            //新增配送收货单单头和单身
            String[] stockInColumns = {"EID","SHOPID","ORGANIZATIONNO","STOCKINNO","BDATE","MEMO","STATUS",
                    "DOC_TYPE","OTYPE","OFNO","WAREHOUSE","PROCESS_STATUS","TRANSFER_SHOP",
                    "TOT_PQTY","TOT_CQTY","TOT_AMT","TOT_DISTRIAMT",
                    "LOAD_DOCTYPE","LOAD_DOCNO","PTEMPLATENO","RECEIPTDATE","PACKINGNO",
                    "CREATEBY","CREATE_DATE","CREATE_TIME","CREATE_CHATUSERID","UPDATE_TIME","TRAN_TIME"
            };
            String[] stockInDetailColumns = {
                    "EID","SHOPID","ORGANIZATIONNO","STOCKINNO","ITEM","OITEM","PLUNO","FEATURENO","BATCH_NO","PROD_DATE",
                    "WAREHOUSE","BDATE","PQTY","BASEQTY","PUNIT","BASEUNIT","RECEIVING_QTY","UNIT_RATIO",
                    "PRICE","DISTRIPRICE","AMT","DISTRIAMT","OFNO","OTYPE","OOTYPE","OOFNO","OOITEM","POQTY","PLU_MEMO",
                    "PACKINGNO",
            };
            BigDecimal totPqty = new BigDecimal("0");
            BigDecimal totAmt = new BigDecimal("0");
            BigDecimal totDistriAmt = new BigDecimal("0");
            int item=1;
            for (Map<String, Object> oneData : receivingList) {
                String oItem = oneData.get("ITEM").toString();
                String pluNo = oneData.get("PLUNO").toString();
                String featureNo = oneData.get("FEATURENO").toString();
                String batchNo = oneData.get("BATCH_NO").toString();
                String prodDate = oneData.get("PROD_DATE").toString();
                String pqty = oneData.get("REQ_PQTY").toString();
                String baseQty = oneData.get("REQ_BASEQTY").toString();
                String punit = oneData.get("PUNIT").toString();
                String baseUnit = oneData.get("BASEUNIT").toString();
                String receivingQty = oneData.get("PQTY").toString();
                String unitRatio = oneData.get("UNIT_RATIO").toString();
                String price = oneData.get("REQ_PRICE").toString();
                String distriPrice = oneData.get("REQ_DISTRIPRICE").toString();
                String amt = oneData.get("REQ_AMT").toString();
                String distriAmt = oneData.get("REQ_DISTRIAMT").toString();
                String oType = "0";
                String ooType = oneData.get("OTYPE").toString();
                String oofNo = oneData.get("OFNO").toString();
                String ooItem = oneData.get("OITEM").toString();
                String poQty = oneData.get("POQTY").toString();
                String pluMemo = oneData.get("PLU_MEMO").toString();
                String packingNo = oneData.get("PACKINGNO").toString();
                //金额计算
                totPqty = totPqty.add(new BigDecimal(pqty));
                totAmt = totAmt.add(new BigDecimal(amt));
                totDistriAmt = totDistriAmt.add(new BigDecimal(distriAmt));
                
                //新增配送收货单单身
                DataValue[] stockInDetailInsValue = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(stockInNo, Types.VARCHAR),
                        new DataValue(String.valueOf(item), Types.VARCHAR),
                        new DataValue(oItem, Types.VARCHAR),
                        new DataValue(pluNo, Types.VARCHAR),
                        new DataValue(featureNo, Types.VARCHAR),
                        new DataValue(batchNo, Types.VARCHAR),
                        new DataValue(prodDate, Types.VARCHAR),
                        new DataValue(warehouse, Types.VARCHAR),
                        new DataValue(bDate, Types.VARCHAR),
                        new DataValue(pqty, Types.VARCHAR),
                        new DataValue(baseQty, Types.VARCHAR),
                        new DataValue(punit, Types.VARCHAR),
                        new DataValue(baseUnit, Types.VARCHAR),
                        new DataValue(receivingQty, Types.VARCHAR),
                        new DataValue(unitRatio, Types.VARCHAR),
                        new DataValue(price, Types.VARCHAR),
                        new DataValue(distriPrice, Types.VARCHAR),
                        new DataValue(amt, Types.VARCHAR),
                        new DataValue(distriAmt, Types.VARCHAR),
                        new DataValue(receivingNo, Types.VARCHAR),
                        new DataValue(oType, Types.VARCHAR),
                        new DataValue(ooType, Types.VARCHAR),
                        new DataValue(oofNo, Types.VARCHAR),
                        new DataValue(ooItem, Types.VARCHAR),
                        new DataValue(poQty, Types.VARCHAR),
                        new DataValue(pluMemo, Types.VARCHAR),
                        new DataValue(packingNo, Types.VARCHAR),
                };
                InsBean stockInDetailIb = new InsBean("DCP_STOCKIN_DETAIL", stockInDetailColumns);
                stockInDetailIb.addValues(stockInDetailInsValue);
                this.addProcessData(new DataProcessBean(stockInDetailIb));
                
                // 更新关联通知单单身
                UptBean ub = new UptBean("DCP_RECEIVING_DETAIL");
                // add value
                ub.addUpdateValue("STOCKIN_QTY", new DataValue(pqty, Types.VARCHAR));
                ub.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
                // condition
                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                ub.addCondition("RECEIVINGNO", new DataValue(receivingNo, Types.VARCHAR));
                ub.addCondition("ITEM", new DataValue(oItem, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub));
                
                item++;
            }
            
            //新增配送收货单单身
            DataValue[] stockInInsValue = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue(stockInNo, Types.VARCHAR),
                    new DataValue(bDate, Types.VARCHAR),
                    new DataValue(memo, Types.VARCHAR),
                    new DataValue("0", Types.VARCHAR),
                    new DataValue("0", Types.VARCHAR),
                    new DataValue("0", Types.VARCHAR),
                    new DataValue(receivingNo, Types.VARCHAR),
                    new DataValue(warehouse, Types.VARCHAR),
                    new DataValue("N", Types.VARCHAR),
                    new DataValue(transferShop, Types.VARCHAR),
                    new DataValue(totPqty.toPlainString(), Types.VARCHAR),
                    new DataValue(String.valueOf(item-1), Types.VARCHAR),
                    new DataValue(totAmt.toPlainString(), Types.VARCHAR),
                    new DataValue(totDistriAmt.toPlainString(), Types.VARCHAR),
                    new DataValue(loadDocType, Types.VARCHAR),
                    new DataValue(loadDocNo, Types.VARCHAR),
                    new DataValue(ptemplateNo, Types.VARCHAR),
                    new DataValue(receiptDate, Types.VARCHAR),
                    new DataValue(receivingPackingNo, Types.VARCHAR),
                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(sDate, Types.VARCHAR),
                    new DataValue(sTime, Types.VARCHAR),
                    new DataValue(req.getChatUserId(), Types.VARCHAR),
                    new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                    new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
            };
            InsBean stockInIb = new InsBean("DCP_STOCKIN", stockInColumns);
            stockInIb.addValues(stockInInsValue);
            this.addProcessData(new DataProcessBean(stockInIb));
            
            return true;
        }catch (Exception e){
            errString.append(e.getMessage());
            return false ;
        }
    }
    
    private boolean updateStockin(DCP_GoodsStockInProcessReq req,List<Map<String, Object>> receivingList,String receivingNo,String stockInNo,StringBuffer errString) throws Exception{
        String eId = req.geteId();
        String shopId = req.getShopId();
        String modifyBy = req.getOpNO();
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String sTime = new SimpleDateFormat("HHmmss").format(new Date());
        try {
            String sql=" select a.warehouse,a.bdate,a.tot_pqty,a.tot_amt,a.tot_distriamt,max(b.item) item"
                    + " from dcp_stockin a"
                    + " inner join dcp_stockin_detail b on a.eid=b.eid and a.shopid=b.shopid and a.stockinno=b.stockinno"
                    + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.stockinno='"+stockInNo+"' and a.ofno='"+receivingNo+"' "
                    + " and a.doc_type='0' and status='0' "
                    + " group by a.warehouse,a.bdate,a.tot_pqty,a.tot_amt,a.tot_distriamt";
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (getQData != null && !getQData.isEmpty()) {
                int item = 1+Integer.parseInt(getQData.get(0).get("ITEM").toString());
                String warehouse = getQData.get(0).get("WAREHOUSE").toString();
                String bDate = getQData.get(0).get("BDATE").toString();
                BigDecimal totPqty = new BigDecimal(getQData.get(0).get("TOT_PQTY").toString());
                BigDecimal totAmt = new BigDecimal(getQData.get(0).get("TOT_AMT").toString());
                BigDecimal totDistriAmt = new BigDecimal(getQData.get(0).get("TOT_DISTRIAMT").toString());
                String[] stockInDetailColumns = {
                        "EID","SHOPID","ORGANIZATIONNO","STOCKINNO","ITEM","OITEM","PLUNO","FEATURENO","BATCH_NO","PROD_DATE",
                        "WAREHOUSE","BDATE","PQTY","BASEQTY","PUNIT","BASEUNIT","RECEIVING_QTY","UNIT_RATIO",
                        "PRICE","DISTRIPRICE","AMT","DISTRIAMT","OFNO","OTYPE","OOTYPE","OOFNO","OOITEM","POQTY","PLU_MEMO",
                        "PACKINGNO",
                };
                
                for (Map<String, Object> oneData : receivingList) {
                    String oItem = oneData.get("ITEM").toString();
                    String pluNo = oneData.get("PLUNO").toString();
                    String featureNo = oneData.get("FEATURENO").toString();
                    String batchNo = oneData.get("BATCH_NO").toString();
                    String prodDate = oneData.get("PROD_DATE").toString();
                    String pqty = oneData.get("REQ_PQTY").toString();
                    String baseQty = oneData.get("REQ_BASEQTY").toString();
                    String punit = oneData.get("PUNIT").toString();
                    String baseUnit = oneData.get("BASEUNIT").toString();
                    String receivingQty = oneData.get("PQTY").toString();
                    String unitRatio = oneData.get("UNIT_RATIO").toString();
                    String price = oneData.get("REQ_PRICE").toString();
                    String distriPrice = oneData.get("REQ_DISTRIPRICE").toString();
                    String amt = oneData.get("REQ_AMT").toString();
                    String distriAmt = oneData.get("REQ_DISTRIAMT").toString();
                    String oType = "0";
                    String ooType = oneData.get("OTYPE").toString();
                    String oofNo = oneData.get("OFNO").toString();
                    String ooItem = oneData.get("OITEM").toString();
                    String poQty = oneData.get("POQTY").toString();
                    String pluMemo = oneData.get("PLU_MEMO").toString();
                    String packingNo = oneData.get("PACKINGNO").toString();
                    
                    //金额计算
                    totPqty = totPqty.add(new BigDecimal(pqty));
                    totAmt = totAmt.add(new BigDecimal(amt));
                    totDistriAmt = totDistriAmt.add(new BigDecimal(distriAmt));
                    
                    //新增配送收货单单身
                    DataValue[] stockInDetailInsValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(stockInNo, Types.VARCHAR),
                            new DataValue(String.valueOf(item), Types.VARCHAR),
                            new DataValue(oItem, Types.VARCHAR),
                            new DataValue(pluNo, Types.VARCHAR),
                            new DataValue(featureNo, Types.VARCHAR),
                            new DataValue(batchNo, Types.VARCHAR),
                            new DataValue(prodDate, Types.VARCHAR),
                            new DataValue(warehouse, Types.VARCHAR),
                            new DataValue(bDate, Types.VARCHAR),
                            new DataValue(pqty, Types.VARCHAR),
                            new DataValue(baseQty, Types.VARCHAR),
                            new DataValue(punit, Types.VARCHAR),
                            new DataValue(baseUnit, Types.VARCHAR),
                            new DataValue(receivingQty, Types.VARCHAR),
                            new DataValue(unitRatio, Types.VARCHAR),
                            new DataValue(price, Types.VARCHAR),
                            new DataValue(distriPrice, Types.VARCHAR),
                            new DataValue(amt, Types.VARCHAR),
                            new DataValue(distriAmt, Types.VARCHAR),
                            new DataValue(receivingNo, Types.VARCHAR),
                            new DataValue(oType, Types.VARCHAR),
                            new DataValue(ooType, Types.VARCHAR),
                            new DataValue(oofNo, Types.VARCHAR),
                            new DataValue(ooItem, Types.VARCHAR),
                            new DataValue(poQty, Types.VARCHAR),
                            new DataValue(pluMemo, Types.VARCHAR),
                            new DataValue(packingNo, Types.VARCHAR),
                    };
                    InsBean stockInDetailIb = new InsBean("DCP_STOCKIN_DETAIL", stockInDetailColumns);
                    stockInDetailIb.addValues(stockInDetailInsValue);
                    this.addProcessData(new DataProcessBean(stockInDetailIb));
                    
                    // 更新关联通知单单身
                    UptBean ub = new UptBean("DCP_RECEIVING_DETAIL");
                    // add value
                    ub.addUpdateValue("STOCKIN_QTY", new DataValue(pqty, Types.VARCHAR));
                    ub.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
                    // condition
                    ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                    ub.addCondition("RECEIVINGNO", new DataValue(receivingNo, Types.VARCHAR));
                    ub.addCondition("ITEM", new DataValue(oItem, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub));
                    
                    item++;
                }
                
                // 更新收货单单头
                UptBean ub1 = new UptBean("DCP_STOCKIN");
                // add value
                ub1.addUpdateValue("MODIFY_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                ub1.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
                ub1.addUpdateValue("MODIFY_DATE", new DataValue(sDate, Types.VARCHAR));
                ub1.addUpdateValue("MODIFY_TIME", new DataValue(sTime, Types.VARCHAR));
                ub1.addUpdateValue("TOT_PQTY", new DataValue(totPqty, Types.VARCHAR));
                ub1.addUpdateValue("TOT_AMT", new DataValue(totAmt, Types.VARCHAR));
                ub1.addUpdateValue("TOT_CQTY", new DataValue(String.valueOf(item-1), Types.VARCHAR));
                ub1.addUpdateValue("TOT_DISTRIAMT", new DataValue(totDistriAmt, Types.VARCHAR));
                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                //condition
                ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("STOCKINNO", new DataValue(stockInNo, Types.VARCHAR));
                ub1.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                
                this.addProcessData(new DataProcessBean(ub1));
                
                return true;
            }else{
                errString.append("配送收货单:"+stockInNo+ "不存在或已确认");
                return false ;
            }
        }catch (Exception e){
            errString.append(e.getMessage());
            return false ;
        }
    }
    
    private String getStockInNo(String eId,String shopId,String accountDate) throws Exception {
        String stockInNo = "PSSH" + accountDate;
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
                stockInNo = "PSSH" + stockInNo;
            }else{
                stockInNo = "PSSH" + accountDate + "00001";
            }
        } else {
            stockInNo = "PSSH" + accountDate + "00001";
        }
        return stockInNo;
    }
    
    private void stockinAutoConfirm(DCP_GoodsStockInProcessReq req,String eId,String shopId,String receivingNo) throws Exception {
        //判断是否全部收货完成，完成后调自动收货
        try {
            boolean isFinish = true;
            String sql = ""
                    + " select b.item,b.pluno,b.featureno,b.stockin_qty,b.status,c.stockinno,c.load_docno from dcp_receiving a "
                    + " inner join dcp_receiving_detail b on a.eid=b.eid and a.shopid=b.shopid and a.receivingno=b.receivingno "
                    + " inner join dcp_stockin c on a.eid=c.eid and a.shopid=c.shopid and a.receivingno=c.ofno and c.doc_type='0' and c.status='0' "
                    + " where a.eid='" + eId + "' and a.shopid='" + shopId + "' and a.receivingno='" + receivingNo + "' and a.status='6' and a.doc_type='0' "
                    + " ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (getQData != null && !getQData.isEmpty()) {
                String stockInNo = getQData.get(0).get("STOCKINNO").toString();
                String loadDocNo = getQData.get(0).get("LOAD_DOCNO").toString();
                if (Check.Null(loadDocNo)) {
                    isFinish = false;              //loadDocNo必须有值，ERP单号
                }
                
                for (Map<String, Object> oneDate : getQData) {
                    String stockInQty = oneDate.get("STOCKIN_QTY").toString();
                    String status = oneDate.get("STATUS").toString();
                    if (Check.Null(stockInQty)) {
                        isFinish = false;              //未回写通知单入库数量
                        break;
                    }
                    if (Check.Null(status) || !status.equals("100")) {
                        isFinish = false;              //未回写通知单单身状态
                        break;
                    }
                }
                if (isFinish) {
                    //收货全部完成，调入库单确认服务
                    DCP_StockInProcessReq stockInProcessReq = new DCP_StockInProcessReq();
                    DCP_StockInProcessReq.levelElm stockInProcessReq_levelElm = stockInProcessReq.new levelElm();
                    stockInProcessReq_levelElm.setStockInNo(stockInNo);
                    stockInProcessReq_levelElm.setStatus("2");
                    stockInProcessReq_levelElm.setDocType("0");
                    stockInProcessReq_levelElm.setOfNo(receivingNo);
                    stockInProcessReq_levelElm.setLoadDocNo(loadDocNo);
                    
                    Map<String, Object> jsonMap = new HashMap<String, Object>();
                    
                    jsonMap.put("serviceId", "DCP_StockInProcess");
                    jsonMap.put("token", req.getToken());
                    jsonMap.put("request", stockInProcessReq_levelElm);
                    ParseJson pj = new ParseJson();
                    String json = pj.beanToJson(jsonMap);
                    logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"**********PDA商品收货自动确认,请求内容:"+ json+"******\r\n");
                    DispatchService ds = DispatchService.getInstance();
                    String res = ds.callService(json, dao);
                    logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"**********PDA商品收货自动确认,返回结果:"+ res+"******\r\n");
                }
            }
        }catch (Exception ignored){
        
        }
        
    }
}
