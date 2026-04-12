package com.dsc.spos.service.imp.json;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockTakeProcessReq;
import com.dsc.spos.json.cust.req.DCP_StockTakeProcessReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_StockTakeProcessRes;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.webhook.WebHookService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.bc.BcReq;
import com.dsc.spos.utils.bc.BcRes;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：StockTakeProcess
 * 服务说明：库存盘点执行
 * @author panjing
 * @since  2016-10-08
 */
public class DCP_StockTakeProcess extends SPosAdvanceService<DCP_StockTakeProcessReq, DCP_StockTakeProcessRes> {
    @Override
    protected boolean isVerifyFail(DCP_StockTakeProcessReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        levelElm request = req.getRequest();
        String stockTakeNO = request.getStockTakeNo();
        String status = request.getStatus();
        String taskWay = request.getTaskWay();
        String notGoodsMode = request.getNotGoodsMode();
        String warehouse= request.getWarehouse();
        String docType = request.getDocType();
        String bDate = request.getbDate();
        
        if (Check.Null(stockTakeNO)) {
            errMsg.append("单据编号不可为空值, ");
            isFail = true;
        }
        if (Check.Null(status)) {
            errMsg.append("状态不可为空值, ");
            isFail = true;
        }
        if (Check.Null(taskWay)) {
            errMsg.append("盘点方式不可为空值, ");
            isFail = true;
        }
        if (Check.Null(docType)) {
            errMsg.append("盘点类型不可为空值, ");
            isFail = true;
        }
        if (Check.Null(notGoodsMode)) {
            errMsg.append("漏盘商品处理方式不可为空值, ");
            isFail = true;
        }
        if (Check.Null(warehouse)) {
            errMsg.append("盘点仓库不可为空值, ");
            isFail = true;
        }
        if (Check.Null(bDate)) {
            errMsg.append("盘点日期不可为空值, ");
            isFail = true;
        }
        
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_StockTakeProcessReq> getRequestType() {
        return new TypeToken<DCP_StockTakeProcessReq>(){};
    }
    
    @Override
    protected DCP_StockTakeProcessRes getResponseType() {
        return new DCP_StockTakeProcessRes();
    }
    
    @Override
    protected void processDUID(DCP_StockTakeProcessReq req,DCP_StockTakeProcessRes res) throws Exception {
        
        String eId = req.geteId();
        String shopId = req.getShopId();
        String companyId = req.getBELFIRM();
        levelElm request = req.getRequest();
        String taskway = request.getTaskWay() ;            ///盘点方式 1.营业中盘点  2.闭店盘点
        String notGoodsmode =request.getNotGoodsMode() ;   ///漏盘商品处理方式  1.库存变成零 2.库存不变
        String warehouse=request.getWarehouse();
        String stockTakeNo = request.getStockTakeNo();
        
        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
        String sysDate = dfDate.format(cal.getTime());
        String confirmBy = req.getEmployeeNo();
        String confirmDate = dfDate.format(cal.getTime());
        String confirmTime = dfTime.format(cal.getTime());
        String accountBy = req.getEmployeeNo();
        String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        String accountTime = dfTime.format(cal.getTime());
        String submitBy = req.getEmployeeNo();
        String submitDate = dfDate.format(cal.getTime());
        String submitTime = dfTime.format(cal.getTime());
        String completeDate = dfDate.format(cal.getTime());
        String bDate= request.getbDate();
        String stockSync_billNo="";

        String opType = request.getOpType();

        if("post".equals(opType)) {
            //状态为0
            String vsql="select * from dcp_stocktake where stocktakeno='"+stockTakeNo+"' and eid='"+eId+"' " +
                    " and organizationno='"+ req.getOrganizationNO()+"' and status='0' ";
            List<Map<String, Object>> list = this.doQueryData(vsql,null);
            if(CollUtil.isEmpty(list)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或状态不为0");
            }

            Map<String, Object> stockChangeVerifyMsg = PosPub.getStockChangeVerifyMsg(dao, req.geteId(), req.getOrganizationNO(), accountDate);
            if("N".equals( stockChangeVerifyMsg.get("check").toString())){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, stockChangeVerifyMsg.get("errorMsg").toString());
            }

            String employeeId = list.get(0).get("EMPLOYEEID").toString();
            String departId = list.get(0).get("DEPARTID").toString();

            String isBatchPara = PosPub.getPARA_SMS(dao, eId, "", "Is_BatchNO");
            if (Check.Null(isBatchPara) || !isBatchPara.equals("Y"))
                isBatchPara="N";

            if(isBatchPara.equals("Y")){
                String detailSql="select a.item,a.ISBATCHADD,a.BATCH_NO as BATCHNO,a.PLUNO,a.FEATURENO,a.PROD_DATE as PRODDATE,a.EXPDATE,nvl(a.PQTY,0) as PQTY from dcp_stocktake_detail a where a.eid='"+eId+"' and stocktakeno='"+stockTakeNo+"' ";
                List<Map<String, Object>> detailList = this.doQueryData(detailSql,null);
                if(CollUtil.isNotEmpty(detailList)){
                    for (Map<String, Object> map : detailList){
                        String isBatchAdd = map.get("ISBATCHADD").toString();
                        String batchNo = map.get("BATCHNO").toString();
                        String pluNo = map.get("PLUNO").toString();
                        String featureNo = map.get("FEATURENO").toString();
                        String prodDate = map.get("PRODDATE").toString();
                        String expDate = map.get("EXPDATE").toString();
                        String item = map.get("ITEM").toString();
                        BigDecimal pQty = new BigDecimal(map.get("PQTY").toString());
                        //if("Y".equals(isBatchAdd)) {
                            if (pQty.compareTo(BigDecimal.ZERO) < 0 && Check.Null(batchNo)) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品【" + pluNo + "】库存扣减必须指定批号！");
                            }
                            if(pQty.compareTo(BigDecimal.ZERO)>0){
                                if(Check.Null(batchNo)||" ".equals(batchNo)){
                                    String newBatchNo = PosPub.getBatchNo(dao, eId, shopId, "", pluNo, featureNo, prodDate,expDate, req.getEmployeeNo(), req.getEmployeeName(), "");
                                    UptBean ub1 = new UptBean("DCP_StockTake_DETAIL");
                                    ub1.addUpdateValue("BATCH_NO", new DataValue(newBatchNo, Types.VARCHAR));
                                     // condition
                                    ub1.addCondition("OrganizationNO", new DataValue(shopId, Types.VARCHAR));
                                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                                    ub1.addCondition("stockTakeNO", new DataValue(stockTakeNo, Types.VARCHAR));
                                    ub1.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                                    this.addProcessData(new DataProcessBean(ub1));

                                }else{
                                    String newBatchNo = PosPub.getBatchNo(dao, eId, shopId, "", pluNo, featureNo, prodDate,expDate, req.getEmployeeNo(), req.getEmployeeName(), batchNo);
                                }
                            }
                       // }



                    }
                }
                this.doExecuteDataToDB();//先更新单身的数据
            }

            //try {
            if (PosPub.compare_date(sysDate, request.getbDate()) == -1) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "当前系统日期小于盘点单的盘点日期,不允许确认！");
            }
            //闭店盘点检查未确认单据
            if (taskway.equals("2")) {
                String Is_Take_ConfirmCheck = PosPub.getPARA_SMS(dao, eId, shopId, "Is_Take_ConfirmCheck");
                if (!Check.Null(Is_Take_ConfirmCheck) && !Is_Take_ConfirmCheck.equals("N")) {
                    StringBuffer errorMessage = new StringBuffer();
                    boolean result = confirmCheck(eId, shopId, stockTakeNo, errorMessage, Is_Take_ConfirmCheck);
                    if (!result) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorMessage.toString());
                    }
                }
            }

            ///处理单价和金额小数位数  BY JZMA 20200401
            String amtLength = PosPub.getPARA_SMS(dao, eId, shopId, "amtLength");
            String distriAmtLength = PosPub.getPARA_SMS(dao, eId, shopId, "distriAmtLength");
            if (Check.Null(amtLength) || !PosPub.isNumeric(amtLength)) {
                amtLength = "2";
            }
            if (Check.Null(distriAmtLength) || !PosPub.isNumeric(distriAmtLength)) {
                distriAmtLength = "2";
            }
            int amtLengthInt = Integer.parseInt(amtLength);
            int distriAmtLengthInt = Integer.parseInt(distriAmtLength);


            String sql = " select a.stocktakeno,a.is_adjust_stock,a.ptemplateno,nvl(w.ISLOCATION,'') ISLOCATION,nvl(b.location,'') location from dcp_stocktake a"
                    + " inner join dcp_stocktake_detail b on a.eid=b.eid and a.shopid=b.shopid and a.stocktakeno=b.stocktakeno"
                    +" left join dcp_warehouse w on w.warehouse=a.warehouse and a.eid=w.eid "
                    + " left join dcp_substocktake c on a.eid=c.eid and a.shopid=c.shopid and a.stocktakeno=c.stocktakeno and (c.status='0' or c.importstatus='0')"
                    + " where a.eid='" + eId + "' and a.shopid='" + shopId + "' and a.stocktakeno='" + stockTakeNo + "' and a.status='0' and c.substocktakeno is null  ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (CollectionUtil.isEmpty(getQData)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点子任务未提交或单身资料不存在或单据已确认！");
            }

            String islocation = getQData.get(0).get("ISLOCATION").toString();
            if("Y".equals(islocation)){
                for (Map<String, Object> map : getQData){
                    String location = map.get("LOCATION").toString();
                    if(Check.Null(location)){
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点明细表库位不可为空！");
                    }
                }
            }

            BcReq bcReq=new BcReq();
            bcReq.setServiceType("StockTakeProcess");
            BcRes bcRes = PosPub.getBTypeAndCostCode(bcReq);
            String bType = bcRes.getBType();
            String costCode = bcRes.getCostCode();

            if(Check.Null(bType)||Check.Null(costCode)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "请先设置业务类型和成本码！");
            }

            //【ID1039808】【金贝儿3403】盘点单审核增加处理不异动库存情况 by jinzma 20240327
            String isAdjustStock = getQData.get(0).get("IS_ADJUST_STOCK").toString();  //是否调整库存Y/N/X Y转库存 N转销售 X不异动
            if (Check.Null(isAdjustStock) || isAdjustStock.equals("Y") || isAdjustStock.equals("X")) {
                //判断是否已产生了库存调整单，检查是否重复操作  如果是 则不执行以下逻辑
                sql = " select a.adjustno from dcp_adjust a"
                        + " where a.shopid ='" + shopId + "' and a.eid ='" + eId + "' and a.organizationno ='" + shopId + "' "
                        + " and a.ofno ='" + stockTakeNo + "' ";
                List<Map<String, Object>> getQData1 = this.doQueryData(sql, null);


                if (getQData1 == null || getQData1.isEmpty()) {
                    // 门店直接盈亏时  status=2   后续增加参数 如果ERP盈亏 则 stjavascirpt status=1
                    String status = "2";
                    UptBean ub1 = new UptBean("DCP_StockTake");
                    ub1.addUpdateValue("Status", new DataValue(status, Types.VARCHAR));
                    ub1.addUpdateValue("ACCOUNT_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                    ub1.addUpdateValue("ConfirmBy", new DataValue(confirmBy, Types.VARCHAR));
                    ub1.addUpdateValue("Confirm_Date", new DataValue(confirmDate, Types.VARCHAR));
                    ub1.addUpdateValue("Confirm_Time", new DataValue(confirmTime, Types.VARCHAR));
                    ub1.addUpdateValue("ACCOUNTBY", new DataValue(accountBy, Types.VARCHAR));
                    ub1.addUpdateValue("ACCOUNT_DATE", new DataValue(accountDate, Types.VARCHAR));
                    ub1.addUpdateValue("ACCOUNT_TIME", new DataValue(accountTime, Types.VARCHAR));
                    ub1.addUpdateValue("SUBMITBY", new DataValue(submitBy, Types.VARCHAR));
                    ub1.addUpdateValue("SUBMIT_DATE", new DataValue(submitDate, Types.VARCHAR));
                    ub1.addUpdateValue("SUBMIT_TIME", new DataValue(submitTime, Types.VARCHAR));
                    ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

                    // condition
                    ub1.addCondition("OrganizationNO", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("stockTakeNO", new DataValue(stockTakeNo, Types.VARCHAR));

                    //更新
                    this.addProcessData(new DataProcessBean(ub1));

                    //更新关联任务单   by jzma 20180823
                    if (!Check.Null(request.getOfNo())) {
                        UptBean ub2 = new UptBean("DCP_stocktask");
                        ub2.addUpdateValue("Status", new DataValue("7", Types.VARCHAR));
                        ub2.addUpdateValue("Complete_Date", new DataValue(completeDate, Types.VARCHAR));
                        // condition
                        ub2.addCondition("OrganizationNO", new DataValue(shopId, Types.VARCHAR));
                        ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ub2.addCondition("stockTaskNO", new DataValue(request.getOfNo(), Types.VARCHAR));
                        //更新
                        this.addProcessData(new DataProcessBean(ub2));
                    }

                    //2.产生库存调整单(盈亏数据)
                    String memo = "盘点盈亏产生";
                    String adjustno =this.getOrderNO(req,"KCTZ");// this.getAdjustNo(req);//库存调整单单号
                    stockSync_billNo = adjustno;
                    //DCP_ADJUST
                    String[] columnsAdjust = {
                            "SHOPID", "ADJUSTNO", "BDATE", "MEMO", "DOC_TYPE", "OTYPE", "OFNO", "STATUS",
                            "CREATEBY", "CREATE_DATE", "CREATE_TIME", "MODIFYBY", "MODIFY_DATE", "MODIFY_TIME",
                            "CONFIRMBY", "CONFIRM_DATE", "CONFIRM_TIME", "ACCOUNTBY", "ACCOUNT_DATE", "ACCOUNT_TIME",
                            "CANCELBY", "CANCEL_DATE", "CANCEL_TIME", "SUBMITBY", "SUBMIT_DATE", "SUBMIT_TIME",
                            "TOT_PQTY", "TOT_AMT", "TOT_CQTY", "LOAD_DOCTYPE", "LOAD_DOCNO",
                            "EID", "ORGANIZATIONNO", "WAREHOUSE", "TOT_DISTRIAMT", "UPDATE_TIME", "TRAN_TIME","EMPLOYEEID","DEPARTID"
                    };


                    int TOT_CQTY = 0;
                    BigDecimal sumPlQty = new BigDecimal(0);  //总盈亏数
                    BigDecimal sumPlAmt = new BigDecimal(0);  //总盈亏金额
                    BigDecimal sumDistriAmt = new BigDecimal(0);  //总盈亏进货价金额

                    String doctype = "2";  ///库存调整单的单据类型   2-库存盘点调整
                    String otype = "0";    ///库存调整单的来源类型   2-库存盘点调整
                    String sqlPL = GetPLNEW_SQL(req, warehouse, notGoodsmode);
                    List<Map<String, Object>> getPL = this.doQueryData(sqlPL, null);
                    //是否产生盘点盈亏
                    boolean ispl = false;
                    if (getPL != null && !getPL.isEmpty()) {
                        ///处理零售价和配送价
                        List<Map<String, Object>> plus = new ArrayList<>();
                        Map<String, Boolean> condition = new HashMap<>(); //查詢條件
                        condition.put("PLUNO", true);
                        List<Map<String, Object>> getQPlu = MapDistinct.getMap(getPL, condition);
                        for (Map<String, Object> onePlu : getQPlu) {
                            Map<String, Object> plu = new HashMap<>();
                            plu.put("PLUNO", onePlu.get("PLUNO").toString());
                            plu.put("PUNIT", onePlu.get("BASEUNIT").toString());
                            plu.put("BASEUNIT", onePlu.get("BASEUNIT").toString());
                            plu.put("UNITRATIO", "1");
                            plus.add(plu);
                        }
                        MyCommon mc = new MyCommon();
                        if (Check.Null(companyId)) {
                            sql = " select belfirm from dcp_org where eid='" + eId + "' and organizationno='" + shopId + "' ";
                            List<Map<String, Object>> getCompanyId = this.doQueryData(sql, null);
                            companyId = getCompanyId.get(0).get("BELFIRM").toString();
                        }
                        List<Map<String, Object>> getPluPrice = mc.getSalePrice_distriPrice(dao, eId, companyId, shopId, plus, companyId);

                        //【ID1026993】【潮品3.0】盘点单中有多个商品的实盘为负数时，请一次性提示出来，目前每次点击确定只提示一个 by jinzma 20220711
                        {
                            StringBuffer errorMessage = new StringBuffer();
                            for (Map<String, Object> par : getPL) {
                                BigDecimal pqty = new BigDecimal(par.get("PQTY").toString());
                                //盘点录入的数量不能为负数
                                if (pqty.compareTo(BigDecimal.ZERO) < 0) {
                                    errorMessage.append(par.get("PLUNO").toString() + " 商品的盘点数量为负数,不允许确认! <br/>");
                                }
                            }
                            if (errorMessage.length() > 0) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorMessage.toString());
                            }
                        }


                        int iItem = 0;
                        for (Map<String, Object> par : getPL) {
                            //这里更新一下盘点的账面库存数 WQTY 为实时的账面库存
                            //取得账面库存 和 盘点库存 以及来源项次
                            //					float wqty = Float.parseFloat( par.get("WQTY").toString() ) ;
                            //					float pqty = Float.parseFloat( par.get("PQTY").toString() ) ;
                            //					float ref_wqty = Float.parseFloat( par.get("REF_WQTY").toString() ) ;

                            BigDecimal baseQty = new BigDecimal(par.get("BASEQTY").toString());
                            BigDecimal pqty = new BigDecimal(par.get("PQTY").toString());
                            BigDecimal ref_baseqty = new BigDecimal(par.get("REF_BASEQTY").toString());

                            int oitem = Integer.parseInt(par.get("OITEM").toString());

                            ///标记这个商品的来源  0.从库存帐来的   1.从盘点单来的  注：根据不同来源取对应的账面数字段  BY JZMA 20180508
                            String stocktake = par.get("STOCKTAKE").toString();
                            ub1 = new UptBean("DCP_STOCKTAKE_DETAIL");
                            //wqty可能有小数，需要控制一下
                            //float zmwqty=Float.parseFloat( PosPub.GetdoubleScale(wqty, 4) );


                            //闭店盘点或营业中盘点：未录入盘点单的商品参考库存数=账面库存数      BY JZMA 20180502
                            if (taskway.equals("2") || (taskway.equals("1") && stocktake.equals("0"))) {
                                ub1.addUpdateValue("REF_BASEQTY", new DataValue(baseQty.toString(), Types.VARCHAR));
                                ref_baseqty = baseQty;
                            }
                            // condition
                            ub1.addCondition("OrganizationNO", new DataValue(shopId, Types.VARCHAR));
                            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                            ub1.addCondition("STOCKTAKENO", new DataValue(stockTakeNo, Types.VARCHAR));
                            ub1.addCondition("ITEM", new DataValue(oitem, Types.DECIMAL));

                            //更新
                            //这里先不加update 有可能会放开主要是怕锁表
                            this.addProcessData(new DataProcessBean(ub1));

                            //库存数==实盘数，直接退出
                            //					if(ref_wqty==pqty)
                            //					{
                            //						continue;
                            //					}

                            if (ref_baseqty.compareTo(pqty) == 0) {
                                continue;
                            }

                            ispl = true;

                            int insColCt = 0;
                            String price = "0";
                            String distriPrice = "0";
                            BigDecimal plamt = new BigDecimal("0");
                            BigDecimal pldistriAmt = new BigDecimal("0");
                            String plqty = "0";
                            String punit = "";
                            String pluNo = "";


                            String[] columnsAdjustDetail = {
                                    "SHOPID", "ADJUSTNO", "ITEM", "OITEM", "PLUNO", "PLU_BARCODE", "PUNIT", "PQTY", "BASEUNIT", "BASEQTY",
                                    "UNIT_RATIO", "PRICE", "AMT", "EID", "ORGANIZATIONNO", "WAREHOUSE", "BATCH_NO", "PROD_DATE",
                                    "DISTRIPRICE", "DISTRIAMT", "BDATE", "FEATURENO","CATEGORY","LOCATION","EXPDATE"
                            };
                            DataValue[] columnsVal = new DataValue[columnsAdjustDetail.length];
                            warehouse = par.get("WAREHOUSE").toString();
                            for (int i = 0; i < columnsVal.length; i++) {
                                String keyVal = null;
                                switch (i) {
                                    case 0:
                                        keyVal = shopId;
                                        break;
                                    case 1:
                                        keyVal = adjustno;
                                        TOT_CQTY += 1;
                                        break;
                                    case 2:
                                        iItem = iItem + 1;
                                        keyVal = iItem + "";
                                        break;
                                    case 3:
                                        keyVal = "0";
                                        break;
                                    case 4:
                                        keyVal = par.get("PLUNO").toString();
                                        pluNo = keyVal;
                                        break;
                                    case 5:
                                        keyVal = "";
                                        break;
                                    case 6:
                                        keyVal = par.get("BASEUNIT").toString();
                                        punit = keyVal;
                                        break;
                                    case 7:
                                        ///盘点计划单：营业中盘点差异数量=盘点数量-参考库存数 BY JZMA 2018/5/2
                                        if (taskway.equals("1")) {
                                            //keyVal = String.valueOf (Float.parseFloat( PosPub.GetdoubleScale(pqty-ref_wqty, 4) ))  ;
                                            keyVal = String.valueOf(pqty.subtract(ref_baseqty));
                                        } else {
                                            keyVal = par.get("PLQTY").toString();
                                        }
                                        plqty = keyVal;
                                        // 合计数量
                                        sumPlQty = sumPlQty.add(new BigDecimal(keyVal));
                                        break;
                                    case 8:
                                        keyVal = par.get("BASEUNIT").toString();
                                        break;
                                    case 9:
                                        ///盘点计划单：营业中盘点差异数量=盘点数量-参考库存数 BY JZMA 2018/5/2
                                        if (taskway.equals("1")) {
                                            //keyVal = String.valueOf (Float.parseFloat( PosPub.GetdoubleScale(pqty-ref_wqty, 4) ))  ;
                                            keyVal = String.valueOf(pqty.subtract(ref_baseqty));
                                        } else {
                                            keyVal = par.get("PLQTY").toString();
                                        }
                                        break;
                                    case 10:
                                        keyVal = "1";
                                        break;
                                    case 11:
                                        //商品取价
                                        price = "0";
                                        distriPrice = "0";
                                        Map<String, Object> condiV = new HashMap<>();
                                        condiV.put("PLUNO", pluNo);
                                        condiV.put("PUNIT", punit);
                                        List<Map<String, Object>> priceList = MapDistinct.getWhereMap(getPluPrice, condiV, false);
                                        if (priceList != null && priceList.size() > 0) {
                                            price = priceList.get(0).get("PRICE").toString();
                                            distriPrice = priceList.get(0).get("DISTRIPRICE").toString();
                                            BigDecimal price_b = new BigDecimal(price);
                                            BigDecimal distriPrice_b = new BigDecimal(distriPrice);
                                            plamt = price_b.multiply(new BigDecimal(plqty)).setScale(amtLengthInt, RoundingMode.HALF_UP);
                                            pldistriAmt = distriPrice_b.multiply(new BigDecimal(plqty)).setScale(distriAmtLengthInt, RoundingMode.HALF_UP);
                                        }
                                        keyVal = price;
                                        break;
                                    case 12:
                                        keyVal = plamt.toString();
                                        sumPlAmt = sumPlAmt.add(plamt);
                                        break;
                                    case 13:
                                        keyVal = eId;
                                        break;
                                    case 14:
                                        keyVal = shopId;
                                        break;
                                    case 15:
                                        keyVal = warehouse;
                                        break;
                                    case 16:
                                        keyVal = par.get("BATCHNO").toString();
                                        break;
                                    case 17:
                                        keyVal = par.get("PRODDATE").toString();
                                        break;
                                    case 18:
                                        keyVal = distriPrice;
                                        break;
                                    case 19:
                                        keyVal = pldistriAmt.toString();
                                        sumDistriAmt = sumDistriAmt.add(pldistriAmt);
                                        break;
                                    case 20:
                                        keyVal = bDate;
                                        break;
                                    case 21:
                                        keyVal = par.get("FEATURENO").toString();
                                        if (Check.Null(keyVal))
                                            keyVal = " ";
                                        break;

                                    case 22:
                                        keyVal = "";

                                        break;

                                    case 23:
                                        keyVal = par.get("LOCATION").toString();
                                        if (Check.Null(keyVal))
                                            keyVal = " ";
                                        break;
                                    case 24:
                                        keyVal = par.get("EXPDATE").toString();
                                        break;
                                    default:
                                        break;
                                }

                                if (keyVal != null) {
                                    insColCt++;
                                    if (i == 2 || i == 3) {
                                        columnsVal[i] = new DataValue(keyVal, Types.DECIMAL);
                                    } else if (i == 10 || i == 11 || i == 12) {
                                        columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
                                    } else {
                                        columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
                                    }
                                } else {
                                    columnsVal[i] = null;
                                }
                            }

                            String[] columns2 = new String[insColCt];
                            DataValue[] insValue2 = new DataValue[insColCt];

                            insColCt = 0;

                            for (int i = 0; i < columnsVal.length; i++) {
                                if (columnsVal[i] != null) {
                                    columns2[insColCt] = columnsAdjustDetail[i];
                                    insValue2[insColCt] = columnsVal[i];
                                    insColCt++;
                                    if (insColCt >= insValue2.length)
                                        break;
                                }
                            }

                            InsBean ib2 = new InsBean("DCP_ADJUST_DETAIL", columns2);
                            ib2.addValues(insValue2);
                            this.addProcessData(new DataProcessBean(ib2));

                            //*****************************************//

                            //3.加入库存流水账信息
                            if (!"X".equals(isAdjustStock)) {
                                String featureNo = par.get("FEATURENO").toString();
                                if (Check.Null(featureNo))
                                    featureNo = " ";

                                String procedure = "SP_DCP_STOCKCHANGE_VX";
                                Map<Integer, Object> inputParameter = new HashMap<>();
                                inputParameter.put(1, eId);                                  //--企业ID
                                inputParameter.put(2, null);
                                inputParameter.put(3, shopId);                               //--组织
                                inputParameter.put(4, bType);
                                inputParameter.put(5, costCode);
                                inputParameter.put(6, "10");                                 //--单据类型
                                inputParameter.put(7, adjustno);                                //--单据号
                                inputParameter.put(8, iItem);                                //--单据行号
                                inputParameter.put(9, "0");
                                inputParameter.put(10, "1");                                  //--异动方向 1=加库存 -1=减库存
                                inputParameter.put(11, bDate);                                //--营业日期 yyyy-MM-dd
                                inputParameter.put(12, par.get("PLUNO").toString());          //--品号
                                inputParameter.put(13, featureNo);                            //--特征码
                                inputParameter.put(14, warehouse);                           //--仓库
                                inputParameter.put(15, Check.Null(par.get("BATCHNO").toString())?" ":par.get("BATCHNO").toString());       //--批号

                                inputParameter.put(16, Check.Null(par.get("LOCATION").toString())?" ":par.get("LOCATION").toString());       //--库位

                                inputParameter.put(17, par.get("BASEUNIT").toString());      //--交易单位
                                inputParameter.put(18, plqty);                               //--交易数量
                                inputParameter.put(19, par.get("BASEUNIT").toString());      //--基准单位
                                inputParameter.put(20, plqty);                               //--基准数量
                                inputParameter.put(21, "1");                                 //--换算比例
                                inputParameter.put(22, price);                               //--零售价
                                inputParameter.put(23, plamt);                               //--零售金额
                                inputParameter.put(24, distriPrice);                         //--进货价
                                inputParameter.put(25, pldistriAmt);                         //--进货金额
                                inputParameter.put(26, accountDate);                         //--入账日期 yyyy-MM-dd
                                inputParameter.put(27,par.get("PRODDATE").toString());     //--批号的生产日期 yyyy-MM-dd
                                inputParameter.put(28, bDate);                               //--单据日期
                                inputParameter.put(29, "");                                  //--异动原因
                                inputParameter.put(30, memo);                                //--异动描述
                                inputParameter.put(31, req.getOpNO());                       //--操作员

                                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                this.addProcessData(new DataProcessBean(pdb));
                            }


                        }
                    }

                    //单身为0，不要插入单头记录
                    //if(getPL.size() > 0)

                    if (ispl) {
                        sumPlAmt = sumPlAmt.setScale(amtLengthInt, RoundingMode.HALF_UP);
                        sumDistriAmt = sumDistriAmt.setScale(distriAmtLengthInt, RoundingMode.HALF_UP);
                        DataValue[] insValue1 = new DataValue[]{
                                new DataValue(shopId, Types.VARCHAR),
                                new DataValue(adjustno, Types.VARCHAR),
                                new DataValue(accountDate, Types.VARCHAR),
                                new DataValue(memo, Types.VARCHAR),
                                new DataValue(doctype, Types.VARCHAR),
                                new DataValue(otype, Types.VARCHAR),
                                new DataValue(stockTakeNo, Types.VARCHAR),
                                new DataValue("2", Types.VARCHAR),//STATUS
                                new DataValue(confirmBy, Types.VARCHAR),
                                new DataValue(confirmDate, Types.VARCHAR),
                                new DataValue(confirmTime, Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue(confirmBy, Types.VARCHAR),
                                new DataValue(confirmDate, Types.VARCHAR),
                                new DataValue(confirmTime, Types.VARCHAR),
                                new DataValue(accountBy, Types.VARCHAR),
                                new DataValue(accountDate, Types.VARCHAR),
                                new DataValue(accountTime, Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue(submitBy, Types.VARCHAR),
                                new DataValue(submitDate, Types.VARCHAR),
                                new DataValue(submitTime, Types.VARCHAR),
                                new DataValue(sumPlQty.toString(), Types.VARCHAR), //TOT_PQTY
                                new DataValue(sumPlAmt.toString(), Types.VARCHAR), //TOT_AMT
                                new DataValue(TOT_CQTY, Types.VARCHAR), //TOT_CQTY 明细条数
                                new DataValue("", Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(shopId, Types.VARCHAR), //
                                new DataValue(warehouse, Types.VARCHAR),
                                new DataValue(sumDistriAmt.toString(), Types.VARCHAR),
                                new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                                new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                                new DataValue(employeeId, Types.VARCHAR), //
                                new DataValue(departId, Types.VARCHAR)
                        };

                        InsBean ib1 = new InsBean("DCP_ADJUST", columnsAdjust);
                        ib1.addValues(insValue1);
                        this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
                    }



                }
                else {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "已产生盘点调整单，请重新确认！ ");
                }

            }


            //【ID1030281】【大万3.0】车销业务场景下系统改造评估-----盘点差异生成销售单服务端 by jinzma 20221219
            if (isAdjustStock.equals("N")) {

                //判断是否已产生了销售单，检查是否重复操作  如果是 则不执行以下逻辑
                sql = " select a.saleno from dcp_sale a where a.eid='" + eId + "' and a.shopid='" + shopId + "' and a.ofno='" + stockTakeNo + "' ";
                List<Map<String, Object>> getQData1 = this.doQueryData(sql, null);
                if (getQData1 == null || getQData1.isEmpty()) {

                    //判断POS是否开店
                    //【ID1031672】【贝丽多V3.3.0.2】门店是每天中午闭店的，但是日结又不是随着中午闭店的，因为中午闭店门店不固定，导致盘点保存不了 by jinzma 20230307
                    // sql = " select bdate from dcp_date a where a.eid='"+eId+"' and a.shopid='"+shopId+"' and bdate='"+accountDate+"' ";
                    sql = " select bdate from dcp_date a where a.eid='" + eId + "' and a.shopid='" + shopId + "' ";

                    List<Map<String, Object>> getQData2 = this.doQueryData(sql, null);
                    if (CollectionUtil.isEmpty(getQData2)) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "门店闭店检查失败,dcp_date无当天数据,请重新确认! ");
                    }

                    //DCP_STOCKTAKE
                    UptBean ub1 = new UptBean("DCP_STOCKTAKE");
                    ub1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
                    ub1.addUpdateValue("ACCOUNT_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                    ub1.addUpdateValue("CONFIRMBY", new DataValue(confirmBy, Types.VARCHAR));
                    ub1.addUpdateValue("CONFIRM_DATE", new DataValue(confirmDate, Types.VARCHAR));
                    ub1.addUpdateValue("CONFIRM_TIME", new DataValue(confirmTime, Types.VARCHAR));
                    ub1.addUpdateValue("ACCOUNTBY", new DataValue(accountBy, Types.VARCHAR));
                    ub1.addUpdateValue("ACCOUNT_DATE", new DataValue(accountDate, Types.VARCHAR));
                    ub1.addUpdateValue("ACCOUNT_TIME", new DataValue(accountTime, Types.VARCHAR));
                    ub1.addUpdateValue("SUBMITBY", new DataValue(submitBy, Types.VARCHAR));
                    ub1.addUpdateValue("SUBMIT_DATE", new DataValue(submitDate, Types.VARCHAR));
                    ub1.addUpdateValue("SUBMIT_TIME", new DataValue(submitTime, Types.VARCHAR));
                    ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    ub1.addUpdateValue("PROCESS_STATUS", new DataValue("Y", Types.VARCHAR));  //转销售单不用再传ERP
                    // condition
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("STOCKTAKENO", new DataValue(stockTakeNo, Types.VARCHAR));

                    this.addProcessData(new DataProcessBean(ub1));

                    //更新盘点计划单   by jzma 20180823
                    if (!Check.Null(request.getOfNo())) {
                        UptBean ub2 = new UptBean("DCP_STOCKTASK");
                        ub2.addUpdateValue("STATUS", new DataValue("7", Types.VARCHAR));
                        ub2.addUpdateValue("COMPLETE_DATE", new DataValue(completeDate, Types.VARCHAR));
                        // condition
                        ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ub2.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                        ub2.addCondition("STOCKTASKNO", new DataValue(request.getOfNo(), Types.VARCHAR));
                        //更新
                        this.addProcessData(new DataProcessBean(ub2));
                    }


                    //2.产生销售单(盈亏数据)
                    //SALENO  销售单单号：“PD”+SHOPID+年月日时分+6位正整数；比如： //PD01 2022 12 12 170000 0001;
                    String saleNo = "PD" + eId + shopId + confirmDate + confirmTime + stockTakeNo.substring(12);
                    stockSync_billNo = saleNo;


                    //dcp_sale
                    String[] columns_sale = {
                            "EID", "SHOPID", "SALENO", "MACHINE", "BDATE", "TRNO", "FNO", "VER_NUM", "TYPE", "SQUADNO", "WORKNO",
                            "OPNO", "OSHOP", "OMACHINE", "OTYPE", "OFNO", "OTRNO", "CARDAMOUNT", "POINT_QTY", "REMAINPOINT", "MEALNUMBER",
                            "CHILDNUMBER", "ECSFLG", "GUESTNUM", "REPAST_TYPE", "DINNERTYPE", "TOUR_PEOPLENUM", "TOT_QTY", "TOT_OLDAMT",
                            "TOT_DISC", "SALEDISC", "ERASE_AMT", "TOT_AMT", "ORDERAMOUNT", "ISINVOICE", "SELLCREDIT", "PAY_AMT",
                            "TOT_CHANGED", "ISTAKEOUT", "PLATFORM_DISC", "SELLER_DISC", "ISBUFFER", "STATUS", "ISRETURN", "SDATE", "STIME",
                            "EVALUATE", "ISUPLOADED", "UPDATE_TIME", "TRAN_TIME", "ORDERRETURN", "COMPANYID", "CHANNELID", "APPTYPE",
                            "OCOMPANYID", "OCHANNELID", "OAPPTYPE", "TOT_AMT_MERRECEIVE", "TOT_AMT_CUSTPAYREAL", "TOT_DISC_MERRECEIVE",
                            "TOT_DISC_CUSTPAYREAL", "ISMERPAY", "SALEAMT", "SALEMODE", "TIME_STAMP", "ISPACKGOODS", "ISORDERTOSALEALL",
                            "UPLOADTOCLOUD", "PARTITION_DATE"
                    };

                    //dcp_sale_detail
                    String[] columns_sale_detail = {
                            "EID", "SHOPID", "SALENO", "ITEM", "OITEM", "DEALTYPE", "PLUNO", "PNAME", "PLUBARCODE", "UNIT",
                            "BASEUNIT", "QTY", "OLDPRICE", "PRICE2", "PRICE3", "RQTY", "OLDAMT", "DISC", "SALEDISC", "PAYDISC",
                            "PRICE", "ADDITIONALPRICE", "AMT", "COUNTERAMT", "PACKAGEMASTER", "ISPACKAGE", "PACKAGEAMT", "PACKAGEQTY",
                            "UPITEM", "SHAREAMT", "ISSTUFF", "DETAILITEM", "REPAST_TYPE", "PACKAGEAMOUNT", "PACKAGEPRICE", "PACKAGEFEE",
                            "STATUS", "BDATE", "SDATE", "STIME", "TRAN_TIME", "DISC_MERRECEIVE", "AMT_MERRECEIVE", "DISC_CUSTPAYREAL",
                            "AMT_CUSTPAYREAL", "REDEEMPOINT", "EXTRACHARGE", "RULEBILLNO", "RULESERIALNO", "PARTITION_DATE", "UNITRATIO",
                            "BASEQTY", "WAREHOUSE"
                    };

                    //dcp_sale_pay
                    String[] columns_sale_pay = {
                            "EID", "SHOPID", "SALENO", "PAYDOCTYPE", "PAYCODE", "PAYCODEERP", "PAYNAME", "PAY", "POS_PAY", "CHANGED", "EXTRA", "RETURNRATE",
                            "CARDAMTBEFORE", "REMAINAMT", "SENDPAY", "COUPONQTY", "DESCORE", "ISORDERPAY", "ISTURNOVER", "STATUS", "BDATE",
                            "SDATE", "STIME", "TRAN_TIME", "PAYTYPE", "PAYSHOP", "MERDISCOUNT", "MERRECEIVE", "THIRDDISCOUNT", "CUSTPAYREAL",
                            "COUPONMARKETPRICE", "COUPONPRICE", "CHARGEAMOUNT", "TIME_STAMP", "PARTITION_DATE", "FUNCNO"
                    };

                    //【ID1031376】【贝丽多V3.3.0.1】盘点差异生成现金的零售单 ，未记录在交班和闭店档中   by jinzma 20230224
                    // DCP_STATISTIC_INFO
                    String[] columns_statistic_info = {
                            "EID", "SHOPID", "MACHINE", "OPNO", "SQUADNO", "ORDERNO", "ITEM", "ISORDERPAY", "WORKNO",
                            "TYPE", "CARDNO", "CUSTOMERNO", "CHANGED", "EXTRA", "ISTURNOVER", "PAYCODE", "PAYNAME", "PAYTYPE",
                            "MERDISCOUNT", "THIRDDISCOUNT", "DIRECTION", "SENDPAY", "CHARGEAMOUNT", "AMT", "STATUS",
                            "SDATE", "STIME", "BDATE", "APPTYPE", "CHANNELID", "PAYCHANNELCODE"
                    };


                    String sqlPL = GetPLNEW_SQL(req, warehouse, notGoodsmode);
                    List<Map<String, Object>> getPL = this.doQueryData(sqlPL, null);
                    if (getPL != null && !getPL.isEmpty()) {

                        BigDecimal sumPlQty = new BigDecimal(0);          //总盈亏数
                        BigDecimal sumPlAmt = new BigDecimal(0);          //总盈亏金额
                        //BigDecimal sumDistriAmt=new BigDecimal(0);        //总盈亏进货价金额

                        boolean ispl = false; // getPL 存在盘点无差异的资料，这部分资料不产生销售单
                        //【ID1026993】【潮品3.0】盘点单中有多个商品的实盘为负数时，请一次性提示出来，目前每次点击确定只提示一个 by jinzma 20220711
                        {
                            StringBuffer errorMessage = new StringBuffer();
                            for (Map<String, Object> par : getPL) {
                                BigDecimal pqty = new BigDecimal(par.get("PQTY").toString());
                                //盘点录入的数量不能为负数
                                if (pqty.compareTo(BigDecimal.ZERO) < 0) {
                                    errorMessage.append(par.get("PLUNO").toString() + " 商品的盘点数量为负数,不允许确认! <br/>");
                                }
                            }
                            if (errorMessage.length() > 0) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorMessage.toString());
                            }
                        }

                        ///处理零售价和配送价
                        List<Map<String, Object>> plus = new ArrayList<>();
                        Map<String, Boolean> condition = new HashMap<>(); //查詢條件
                        condition.put("PLUNO", true);
                        List<Map<String, Object>> getQPlu = MapDistinct.getMap(getPL, condition);
                        for (Map<String, Object> onePlu : getQPlu) {
                            Map<String, Object> plu = new HashMap<>();
                            plu.put("PLUNO", onePlu.get("PLUNO").toString());
                            plu.put("PUNIT", onePlu.get("BASEUNIT").toString());
                            plu.put("BASEUNIT", onePlu.get("BASEUNIT").toString());
                            plu.put("UNITRATIO", "1");
                            plus.add(plu);
                        }
                        MyCommon mc = new MyCommon();
                        if (Check.Null(companyId)) {
                            sql = " select belfirm from dcp_org where eid='" + eId + "' and organizationno='" + shopId + "' ";
                            List<Map<String, Object>> getCompanyId = this.doQueryData(sql, null);
                            companyId = getCompanyId.get(0).get("BELFIRM").toString();
                        }
                        List<Map<String, Object>> getPluPrice = mc.getSalePrice_distriPrice(dao, eId, companyId, shopId, plus, companyId);

                        int item = 1;
                        for (Map<String, Object> par : getPL) {
                            BigDecimal baseQty = new BigDecimal(par.get("BASEQTY").toString());
                            BigDecimal pqty = new BigDecimal(par.get("PQTY").toString());
                            BigDecimal ref_baseqty = new BigDecimal(par.get("REF_BASEQTY").toString());
                            int oitem = Integer.parseInt(par.get("OITEM").toString()); //回写盘点单单身
                            ///标记这个商品的来源  0.从库存帐来的   1.从盘点单来的  注：根据不同来源取对应的账面数字段  BY JZMA 20180508
                            String stocktake = par.get("STOCKTAKE").toString();

                            //更新 DCP_STOCKTAKE_DETAIL
                            ub1 = new UptBean("DCP_STOCKTAKE_DETAIL");
                            //闭店盘点或营业中盘点：未录入盘点单的商品参考库存数=账面库存数      BY JZMA 20180502
                            if (taskway.equals("2") || (taskway.equals("1") && stocktake.equals("0"))) {
                                ub1.addUpdateValue("REF_BASEQTY", new DataValue(baseQty.toString(), Types.VARCHAR));
                                ref_baseqty = baseQty;
                            }
                            // condition
                            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub1.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                            ub1.addCondition("STOCKTAKENO", new DataValue(stockTakeNo, Types.VARCHAR));
                            ub1.addCondition("ITEM", new DataValue(oitem, Types.DECIMAL));

                            this.addProcessData(new DataProcessBean(ub1));

                            //没有差异的直接就出去
                            if (ref_baseqty.compareTo(pqty) == 0) {
                                continue;
                            }
                            ispl = true;

                            //用于单身计算
                            String pluNo = par.get("PLUNO").toString();
                            String punit = par.get("BASEUNIT").toString();
                            String plqty;
                            BigDecimal plamt = new BigDecimal("0");
                            BigDecimal pldistriAmt = new BigDecimal("0");

                            ///盘点计划单：营业中盘点差异数量=盘点数量-参考库存数 BY JZMA 2018/5/2
                            if (taskway.equals("1")) {
                                plqty = String.valueOf(pqty.subtract(ref_baseqty));
                            } else {
                                plqty = par.get("PLQTY").toString();
                            }
                            //库存差异数量为正时，销售为负，此处plqty变成负数用于销售单保存，plqty_stock用于库存调整
                            //String plqty_stock = plqty;
                            plqty = BigDecimal.ZERO.subtract(new BigDecimal(plqty)).toPlainString();

                            //商品取价
                            String price = "0";
                            String distriPrice = "0";
                            Map<String, Object> condiV = new HashMap<>();
                            condiV.put("PLUNO", pluNo);
                            condiV.put("PUNIT", punit);
                            List<Map<String, Object>> priceList = MapDistinct.getWhereMap(getPluPrice, condiV, false);
                            if (priceList != null && priceList.size() > 0) {
                                price = priceList.get(0).get("PRICE").toString();
                                distriPrice = priceList.get(0).get("DISTRIPRICE").toString();
                                BigDecimal price_b = new BigDecimal(price);
                                BigDecimal distriPrice_b = new BigDecimal(distriPrice);
                                plamt = price_b.multiply(new BigDecimal(plqty)).setScale(amtLengthInt, RoundingMode.HALF_UP);
                                pldistriAmt = distriPrice_b.multiply(new BigDecimal(plqty)).setScale(distriAmtLengthInt, RoundingMode.HALF_UP);
                            }

                            sumPlQty = sumPlQty.add(new BigDecimal(plqty));
                            sumPlAmt = sumPlAmt.add(plamt);

                            //插入dcp_sale_detail
                            DataValue[] insValue = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(shopId, Types.VARCHAR),
                                    new DataValue(saleNo, Types.VARCHAR),
                                    new DataValue(item, Types.VARCHAR),
                                    new DataValue("0", Types.VARCHAR),    // OITEM  来源项次 默认为0
                                    new DataValue("1", Types.VARCHAR),    //DEALTYPE  1销售2退货
                                    new DataValue(pluNo, Types.VARCHAR),        //pluno
                                    new DataValue(par.get("PLU_NAME").toString(), Types.VARCHAR),
                                    new DataValue(pluNo, Types.VARCHAR),        //PLUBARCODE 暂时给pluno
                                    new DataValue(par.get("BASEUNIT").toString(), Types.VARCHAR),   //UNIT   销售单位(和龙海确认可以是其他单位 20221221)
                                    new DataValue(par.get("BASEUNIT").toString(), Types.VARCHAR),   //BASEUNIT   基本单位
                                    new DataValue(plqty, Types.VARCHAR),                    // QTY   数量就是差异的数量，可正可负
                                    new DataValue(price, Types.VARCHAR),                    //OLDPRICE  就是销售单价
                                    new DataValue("0", Types.VARCHAR),               //PRICE2  默认0
                                    new DataValue("0", Types.VARCHAR),               //PRICE3 默认0
                                    new DataValue("0", Types.VARCHAR),               //RQTY    如果生成的是退单，默认0
                                    new DataValue(plamt, Types.VARCHAR),                   //OLDAMT   差异数量对应的金额
                                    new DataValue("0", Types.VARCHAR),              //DISC        默认0
                                    new DataValue("0", Types.VARCHAR),              //SALEDISC  默认0
                                    new DataValue("0", Types.VARCHAR),              //PAYDISC   默认0
                                    new DataValue(price, Types.VARCHAR),                  //PRICE     销售单价
                                    new DataValue("0", Types.VARCHAR),              //ADDITIONALPRICE  默认0
                                    new DataValue(plamt, Types.VARCHAR),                  //AMT     差异数量对应的金额
                                    new DataValue("0", Types.VARCHAR),              //COUNTERAMT  默认0
                                    new DataValue("N", Types.VARCHAR),              //PACKAGEMASTER 默认N
                                    new DataValue("N", Types.VARCHAR),              //ISPACKAGE  默认N
                                    new DataValue("0", Types.VARCHAR),              //PACKAGEAMT  默认0
                                    new DataValue("0", Types.VARCHAR),              //PACKAGEQTY  默认0
                                    new DataValue("0", Types.VARCHAR),              //UPITEM           默认0
                                    new DataValue("0", Types.VARCHAR),              //SHAREAMT   默认0
                                    new DataValue("N", Types.VARCHAR),              //ISSTUFF        默认N
                                    new DataValue("0", Types.VARCHAR),              //DETAILITEM   默认0
                                    new DataValue("0", Types.VARCHAR),              //REPAST_TYPE 默认0
                                    new DataValue("0", Types.VARCHAR),              //PACKAGEAMOUNT默认0
                                    new DataValue("0", Types.VARCHAR),              //PACKAGEPRICE  默认0
                                    new DataValue("0", Types.VARCHAR),              //PACKAGEFEE    默认0
                                    new DataValue("100", Types.VARCHAR),            //STATUS  默认100
                                    new DataValue(accountDate, Types.VARCHAR),            //BDATE  取当前营业日期 即入账日期
                                    new DataValue(submitDate, Types.VARCHAR),             //SDATE系统日期
                                    new DataValue(submitTime, Types.VARCHAR),             //STIME 系统时间
                                    new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),    //TRAN_TIME  时间戳
                                    new DataValue("0", Types.VARCHAR),              //DISC_MERRECEIVE  为0
                                    new DataValue(plamt, Types.VARCHAR),                  //AMT_MERRECEIVE   差异数量对应的金额
                                    new DataValue("0", Types.VARCHAR),              //DISC_CUSTPAYREAL 为0
                                    new DataValue(plamt, Types.VARCHAR),                  //AMT_CUSTPAYREAL   差异数量对应的金额
                                    new DataValue("0", Types.VARCHAR),              //REDEEMPOINT  为0
                                    new DataValue("0", Types.VARCHAR),              //EXTRACHARGE  为0
                                    new DataValue("0", Types.VARCHAR),              //RULEBILLNO  为0
                                    new DataValue("0", Types.VARCHAR),              //RULESERIALNO 为0
                                    new DataValue(accountDate, Types.NUMERIC),             //PARTITION_DATE
                                    new DataValue('1', Types.VARCHAR),              //UNITRATIO
                                    new DataValue(plqty, Types.VARCHAR),                  //BASEQTY
                                    new DataValue(par.get("WAREHOUSE").toString(), Types.VARCHAR),    //WAREHOUSE

                            };

                            InsBean ib = new InsBean("DCP_SALE_DETAIL", columns_sale_detail);
                            ib.addValues(insValue);
                            this.addProcessData(new DataProcessBean(ib));


                            //3.加入库存流水账信息
                            String featureNo = par.get("FEATURENO").toString();
                            if (Check.Null(featureNo)) {
                                featureNo = " ";
                            }

                            String procedure = "SP_DCP_STOCKCHANGE_VX";
                            Map<Integer, Object> inputParameter = new HashMap<>();
                            inputParameter.put(1, eId);                                  //--企业ID
                            inputParameter.put(2, null);
                            inputParameter.put(3, shopId);                               //--组织
                            inputParameter.put(4, bType);
                            inputParameter.put(5, costCode);
                            inputParameter.put(6, "20");                                 //--单据类型
                            inputParameter.put(7, saleNo);                                //--单据号
                            inputParameter.put(8, item);                                //--单据行号
                            inputParameter.put(9, "1");
                            inputParameter.put(10, "-1");                                  //--异动方向 1=加库存 -1=减库存
                            inputParameter.put(11, accountDate);                          //--营业日期 yyyy-MM-dd
                            inputParameter.put(12, par.get("PLUNO").toString());          //--品号
                            inputParameter.put(13, featureNo);                            //--特征码
                            inputParameter.put(14, par.get("WAREHOUSE").toString());     //--仓库
                            inputParameter.put(15, Check.Null(par.get("BATCHNO").toString())?" ":par.get("BATCHNO").toString());       //--批号
                            inputParameter.put(16, Check.Null(par.get("LOCATION").toString())?" ":par.get("LOCATION").toString());

                            inputParameter.put(17, par.get("BASEUNIT").toString());      //--交易单位
                            inputParameter.put(18, plqty);                               //--交易数量
                            inputParameter.put(19, par.get("BASEUNIT").toString());      //--基准单位
                            inputParameter.put(20, plqty);                               //--基准数量
                            inputParameter.put(21, "1");                                 //--换算比例
                            inputParameter.put(22, price);                               //--零售价
                            inputParameter.put(23, plamt);                               //--零售金额
                            inputParameter.put(24, distriPrice);                         //--进货价
                            inputParameter.put(25, pldistriAmt);                         //--进货金额
                            inputParameter.put(26, accountDate);                         //--入账日期 yyyy-MM-dd
                            inputParameter.put(27, par.get("PRODDATE").toString());     //--批号的生产日期 yyyy-MM-dd
                            inputParameter.put(28, bDate);                               //--单据日期
                            inputParameter.put(29, "");                                  //--异动原因
                            inputParameter.put(30, "盘点差异转销售");                      //--异动描述
                            inputParameter.put(31, req.getOpNO());                       //--操作员

                            ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                            this.addProcessData(new DataProcessBean(pdb));

                            item++;
                        }

                        if (ispl) {
                            //根据中台的参数，“盘点默认支付方式”InventoryDefaultPay  到款别表中查询  涉及DCP_PAYTYPE，DCP_PAYFUNC
                            String InventoryDefaultPay = PosPub.getPARA_SMS(dao, eId, shopId, "InventoryDefaultPay");
                            if (Check.Null(InventoryDefaultPay)) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点默认支付方式(参数)未配置,请重新确认! ");
                            }
                            sql = " select a.paycode,a.paycodeerp,a.payname,b.paytype,b.funcno from dcp_payment a "
                                    + " inner join dcp_paytype b on a.eid=b.eid and a.paycode=b.paycode "
                                    + " where a.eid='" + eId + "' and a.paycode='" + InventoryDefaultPay + "'";
                            List<Map<String, Object>> getPayQData = this.doQueryData(sql, null);
                            if (CollectionUtil.isEmpty(getPayQData)) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点默认支付方式在付款表中不存在,请重新确认! ");
                            }


                            sumPlAmt = sumPlAmt.setScale(amtLengthInt, RoundingMode.HALF_UP);
                            //sumDistriAmt = sumDistriAmt.setScale(distriAmtLengthInt, RoundingMode.HALF_UP);

                            //插入DCP_SALE_PAY
                            DataValue[] insValue1 = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(shopId, Types.VARCHAR),
                                    new DataValue(saleNo, Types.VARCHAR),
                                    new DataValue("4", Types.VARCHAR),     // PAYDOCTYPE   默认是4
                                    new DataValue(getPayQData.get(0).get("PAYCODE").toString(), Types.VARCHAR),     // PAYCODE
                                    new DataValue(getPayQData.get(0).get("PAYCODEERP").toString(), Types.VARCHAR),  //PAYCODEERP
                                    new DataValue(getPayQData.get(0).get("PAYNAME").toString(), Types.VARCHAR),     //PAYNAME
                                    new DataValue(sumPlAmt, Types.VARCHAR),          //PAY      所有商品盘点差异金额之和；
                                    new DataValue("0", Types.VARCHAR),        //POS_PAY      默认0
                                    new DataValue("0", Types.VARCHAR),        //CHANGED      默认0
                                    new DataValue("0", Types.VARCHAR),        //EXTRA        默认0
                                    new DataValue("0", Types.VARCHAR),        //RETURNRATE   默认0
                                    new DataValue("0", Types.VARCHAR),        //CARDAMTBEFORE  默认0
                                    new DataValue("0", Types.VARCHAR),        //REMAINAMT    默认0
                                    new DataValue("0", Types.VARCHAR),        //SENDPAY      默认0
                                    new DataValue("0", Types.VARCHAR),        //COUPONQTY    默认0
                                    new DataValue("0", Types.VARCHAR),        //DESCORE      默认0
                                    new DataValue("N", Types.VARCHAR),        //ISORDERPAY   默认N
                                    new DataValue("Y", Types.VARCHAR),        //ISTURNOVER   默认Y
                                    new DataValue("100", Types.VARCHAR),      //STATUS       默认100
                                    new DataValue(accountDate, Types.VARCHAR),      //BDATE        营业日期
                                    new DataValue(submitDate, Types.VARCHAR),       //SDATE        系统日期
                                    new DataValue(submitTime, Types.VARCHAR),       //STIME        系统时间
                                    new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR), //TRAN_TIME 时间标记
                                    new DataValue(getPayQData.get(0).get("PAYTYPE").toString(), Types.VARCHAR),   //PAYTYPE     支付方式编码
                                    new DataValue(shopId, Types.VARCHAR),          //PAYSHOP 当前门店
                                    new DataValue("0", Types.VARCHAR),      // MERDISCOUNT  默认0
                                    new DataValue(sumPlAmt, Types.VARCHAR),      //MERRECEIVE   == DCP_SALE_PAY.PAY
                                    new DataValue("0", Types.VARCHAR),      //THIRDDISCOUNT 默认0
                                    new DataValue(sumPlAmt, Types.VARCHAR),      //CUSTPAYREAL    == DCP_SALE_PAY.PAY
                                    new DataValue("0", Types.VARCHAR),      //COUPONMARKETPRICE 默认0
                                    new DataValue("0", Types.VARCHAR),      //COUPONPRICE   默认0
                                    new DataValue("0", Types.VARCHAR),      //CHARGEAMOUNT  默认0
                                    new DataValue(String.valueOf(System.currentTimeMillis()), Types.VARCHAR),    // TIME_STAMP  时间戳
                                    new DataValue(accountDate, Types.VARCHAR),             //PARTITION_DATE
                                    new DataValue(getPayQData.get(0).get("FUNCNO").toString(), Types.VARCHAR),   //FUNCNO 需要传值传DCP_PAYTYPE.FUNCNO
                            };
                            InsBean ib1 = new InsBean("DCP_SALE_PAY", columns_sale_pay);
                            ib1.addValues(insValue1);
                            this.addProcessData(new DataProcessBean(ib1));


                            sql = " select a.channelid from dcp_ecommerce a"
                                    + " inner join crm_channel b on a.eid=b.eid and a.channelid=b.channelid and b.status='100'"
                                    + " where a.eid='" + eId + "' and a.loaddoctype='POS'";
                            List<Map<String, Object>> getChannelidQData = this.doQueryData(sql, null);
                            if (CollectionUtil.isEmpty(getChannelidQData)) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "pos渠道类型未设置,请重新确认! ");
                            }

                            //插入DCP_SALE
                            //取trno 按照康总的要求必须给值 20230104
                            String trno = getTRNO(eId, shopId, accountDate, saleNo);
                            if (Check.Null(trno)) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取销售单最大流水号TRNO报错! ");
                            }
                            DataValue[] insValue2 = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(shopId, Types.VARCHAR),
                                    new DataValue(saleNo, Types.VARCHAR),
                                    new DataValue("", Types.VARCHAR),      //MACHINE    可以不填
                                    new DataValue(accountDate, Types.VARCHAR),   //BDATE     营业日期
                                    new DataValue(trno, Types.VARCHAR),         //TRNO        可以不填
                                    new DataValue("", Types.VARCHAR),     //FNO         可以不填
                                    new DataValue("3.0", Types.VARCHAR),  //VER_NUM  默认3.0
                                    new DataValue("0", Types.VARCHAR),    //TYPE    0-销售单   2-无单退货
                                    new DataValue("", Types.VARCHAR),     //SQUADNO   可以不填
                                    new DataValue("", Types.VARCHAR),     //WORKNO  可以不填
                                    new DataValue(req.getOpNO(), Types.VARCHAR), //OPNO     用户编号   登录中台或者移动门店的用户编号
                                    new DataValue(shopId, Types.VARCHAR),        //OSHOP   就是当前门店编码
                                    new DataValue("", Types.VARCHAR),      //OMACHINE  可以不填
                                    new DataValue("28", Types.VARCHAR),     // OTYPE   原单据类型， 增加枚举值：28盘点差异转销售
                                    new DataValue(stockTakeNo, Types.VARCHAR),    //OFNO    盘点单号
                                    new DataValue("", Types.VARCHAR),      //OTRNO     可以不填
                                    new DataValue("0", Types.VARCHAR),     //CARDAMOUNT 初始余额默认为0
                                    new DataValue("0", Types.VARCHAR),     //POINT_QTY    默认为0
                                    new DataValue("0", Types.VARCHAR),     //REMAINPOINT  默认为0
                                    new DataValue("0", Types.VARCHAR),     //MEALNUMBER  默认为0
                                    new DataValue("0", Types.VARCHAR),    //CHILDNUMBER  默认为0
                                    new DataValue("N", Types.VARCHAR),    //ECSFLG           默认为N
                                    new DataValue("0", Types.VARCHAR),    //GUESTNUM    默认0
                                    new DataValue("0", Types.VARCHAR),    //REPAST_TYPE  默认0
                                    new DataValue("0", Types.VARCHAR),    //DINNERTYPE    默认0
                                    new DataValue("0", Types.VARCHAR),    //TOUR_PEOPLENUM  默认0
                                    new DataValue(sumPlQty, Types.VARCHAR),     //TOT_QTY            盘点差异的数量
                                    new DataValue(sumPlAmt, Types.VARCHAR),     //TOT_OLDAMT    就是盘点差异对应零售总额
                                    new DataValue("0", Types.VARCHAR),    //TOT_DISC   默认0
                                    new DataValue("0", Types.VARCHAR),    //SALEDISC  默认0
                                    new DataValue("0", Types.VARCHAR),    //ERASE_AMT  默认0
                                    new DataValue(sumPlAmt, Types.VARCHAR),     //TOT_AMT           就是盘点差异对应零售总额
                                    new DataValue("0", Types.VARCHAR),    //ORDERAMOUNT   默认0
                                    new DataValue("N", Types.VARCHAR),    //ISINVOICE           默认N
                                    new DataValue("N", Types.VARCHAR),      //SELLCREDIT       默认N
                                    new DataValue(sumPlAmt, Types.VARCHAR),       //PAY_AMT           就是盘点差异对应零售总额
                                    new DataValue("0", Types.VARCHAR),     //TOT_CHANGED    默认0
                                    new DataValue("N", Types.VARCHAR),     //ISTAKEOUT          默认N
                                    new DataValue("0", Types.VARCHAR),     //PLATFORM_DISC  默认0
                                    new DataValue("0", Types.VARCHAR),     //SELLER_DISC   默认0
                                    new DataValue("N", Types.VARCHAR),     //ISBUFFER          默认N
                                    new DataValue("100", Types.VARCHAR),   //STATUS             默认100
                                    new DataValue("N", Types.VARCHAR),     //ISRETURN        正向销售单N，逆向销售单Y
                                    new DataValue(confirmDate, Types.VARCHAR),    //SDATE             系统日期
                                    new DataValue(confirmTime, Types.VARCHAR),    //STIME              系统时间
                                    new DataValue("0", Types.VARCHAR),     //EVALUATE       默认0
                                    new DataValue("N", Types.VARCHAR),     //ISUPLOADED  默认N
                                    new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),    // UPDATE_TIME   修改时间
                                    new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),    // TRAN_TIME       时间
                                    new DataValue("0", Types.VARCHAR),    //ORDERRETURN  默认0
                                    new DataValue(companyId, Types.VARCHAR),    //COMPANYID       公司编码
                                    new DataValue(getChannelidQData.get(0).get("CHANNELID").toString(), Types.VARCHAR),  //CHANNELID       取pos有效的渠道类型下面 第一个渠道编码
                                    new DataValue("POS", Types.VARCHAR),   //APPTYPE           默认POS
                                    new DataValue(companyId, Types.VARCHAR),    //OCOMPANYID   当前公司编码
                                    new DataValue(getChannelidQData.get(0).get("CHANNELID").toString(), Types.VARCHAR),    //OCHANNELID     取pos有效的渠道类型下面 第一个渠道编码
                                    new DataValue("POS", Types.VARCHAR),    //OAPPTYPE        默认POS
                                    new DataValue(sumPlAmt, Types.VARCHAR),    //TOT_AMT_MERRECEIVE     就是盘点差异对应零售总额
                                    new DataValue(sumPlAmt, Types.VARCHAR),    //TOT_AMT_CUSTPAYREAL    就是盘点差异对应零售总额
                                    new DataValue("0", Types.VARCHAR),    //TOT_DISC_MERRECEIVE   默认0
                                    new DataValue("0", Types.VARCHAR),    //TOT_DISC_CUSTPAYREAL  默认0
                                    new DataValue("N", Types.VARCHAR),    //ISMERPAY  默认N
                                    new DataValue(sumPlAmt, Types.VARCHAR),     //SALEAMT        就是盘点差异对应零售总额
                                    new DataValue("0", Types.VARCHAR),    //SALEMODE    默认0
                                    new DataValue(String.valueOf(System.currentTimeMillis()), Types.VARCHAR),    //TIME_STAMP  时间戳
                                    new DataValue("N", Types.VARCHAR),    //ISPACKGOODS  默认N
                                    new DataValue("0", Types.VARCHAR),    //ISORDERTOSALEALL  默认0
                                    new DataValue("N", Types.VARCHAR),    //UPLOADTOCLOUD   默认N
                                    new DataValue(accountDate, Types.VARCHAR),  //PARTITION_DATE
                            };

                            InsBean ib2 = new InsBean("DCP_SALE", columns_sale);
                            ib2.addValues(insValue2);
                            this.addProcessData(new DataProcessBean(ib2));


                            //【ID1031376】【贝丽多V3.3.0.1】盘点差异生成现金的零售单 ，未记录在交班和闭店档中 by jinzma 20230224
                            //插入DCP_STATISTIC_INFO
                            {
                                DataValue[] insValue3 = new DataValue[]{
                                        new DataValue(eId, Types.VARCHAR),             //EID             EID 必传
                                        new DataValue(shopId, Types.VARCHAR),          //SHOPID          SHOPID 必传
                                        new DataValue("", Types.VARCHAR),       //MACHINE         MACHINE  不传
                                        new DataValue(req.getOpNO(), Types.VARCHAR),   //OPNO            OPNO  传中台登录人的工号
                                        new DataValue("", Types.VARCHAR),       //SQUADNO         SQUADNO  不传
                                        new DataValue(saleNo, Types.VARCHAR),          //ORDERNO         ORDERNO  传销售单号
                                        new DataValue("1", Types.VARCHAR),      //ITEM            ITEM 项次传1
                                        new DataValue("N", Types.VARCHAR),      //ISORDERPAY      ISORDERPAY 是否定金传N
                                        new DataValue("", Types.VARCHAR),       //WORKNO          WORKNO  不传
                                        new DataValue("0", Types.VARCHAR),      //TYPE            TYPE  传0
                                        new DataValue("", Types.VARCHAR),       //CARDNO          CARDNO 不传
                                        new DataValue("", Types.VARCHAR),       //CUSTOMERNO      CUSTOMERNO 不传
                                        new DataValue("0", Types.VARCHAR),      //CHANGED         CHANGED 找零传0
                                        new DataValue("0", Types.VARCHAR),      //EXTRA           EXTRA 溢收 传0
                                        new DataValue("Y", Types.VARCHAR),      //ISTURNOVER      ISTURNOVER 是否纳入营收传Y
                                        new DataValue(getPayQData.get(0).get("PAYCODE").toString(), Types.VARCHAR),   //PAYCODE         PAYCODE 传销售单支付方式
                                        new DataValue(getPayQData.get(0).get("PAYNAME").toString(), Types.VARCHAR),   //PAYNAME         PAYNAME 传销售单支付方式名称
                                        new DataValue("", Types.VARCHAR),       //PAYTYPE         PAYTYPE 不传
                                        new DataValue("0", Types.VARCHAR),      //MERDISCOUNT     MERDISCOUNT 传0
                                        new DataValue("0", Types.VARCHAR),      //THIRDDISCOUNT   THIRDDISCOUNT 传0
                                        new DataValue("1", Types.VARCHAR),      //DIRECTION       DIRECTION  收款类型： 传1
                                        new DataValue("0", Types.VARCHAR),      //SENDPAY         SENDPAY 传0
                                        new DataValue("0", Types.VARCHAR),      //CHARGEAMOUNT    CHARGEAMOUNT 传0
                                        new DataValue(sumPlAmt, Types.VARCHAR),       //AMT             AMT  传销售单支付金额
                                        new DataValue("100", Types.VARCHAR),    //STATUS          STATUS 传100
                                        new DataValue(confirmDate, Types.VARCHAR),    //SDATE           SDATE  系统日期  必传
                                        new DataValue(confirmTime, Types.VARCHAR),    //STIME           STIME  系统时间  必传
                                        new DataValue(accountDate, Types.VARCHAR),    //BDATE           BDATE 传营业日期
                                        new DataValue("POS", Types.VARCHAR),   //APPTYPE         APPTYPE     渠道类型  取销售单里面的渠道
                                        new DataValue(getChannelidQData.get(0).get("CHANNELID").toString(), Types.VARCHAR),  //CHANNELID       CHANNELID  渠道编码 取销售单里面的渠道编码
                                        new DataValue("", Types.VARCHAR),      //PAYCHANNELCODE   PAYCHANNELCODE 不传
                                };

                                InsBean ib3 = new InsBean("DCP_STATISTIC_INFO", columns_statistic_info);
                                ib3.addValues(insValue3);
                                this.addProcessData(new DataProcessBean(ib3));

                            }

                        }
                    }

                }
                else {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "已产生盘点销售单，请重新确认！ ");
                }

            }



            this.doExecuteDataToDB();
        }
        if("cancel".equals(opType)){

            String stockTakeSql="select * from DCP_StockTake a where a.eid='"+eId+"' and a.organizationno='"+req.getOrganizationNO()+"' and a.stocktakeno='"+stockTakeNo+"'";
            List<Map<String, Object>> list = this.doQueryData(stockTakeSql, null);
            if(CollUtil.isNotEmpty(list)){
                String status = list.get(0).get("STATUS").toString();
                if(status!="2"){
                    UptBean ub1 = new UptBean("DCP_StockTake");
                    ub1.addUpdateValue("Status", new DataValue("3", Types.VARCHAR));
                    ub1.addUpdateValue("ACCOUNT_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                    ub1.addUpdateValue("ConfirmBy", new DataValue("", Types.VARCHAR));
                    ub1.addUpdateValue("Confirm_Date", new DataValue("", Types.VARCHAR));
                    ub1.addUpdateValue("Confirm_Time", new DataValue("", Types.VARCHAR));
                    ub1.addUpdateValue("ACCOUNTBY", new DataValue("", Types.VARCHAR));
                    ub1.addUpdateValue("ACCOUNT_DATE", new DataValue("", Types.VARCHAR));
                    ub1.addUpdateValue("ACCOUNT_TIME", new DataValue("", Types.VARCHAR));
                    ub1.addUpdateValue("SUBMITBY", new DataValue("", Types.VARCHAR));
                    ub1.addUpdateValue("SUBMIT_DATE", new DataValue("", Types.VARCHAR));
                    ub1.addUpdateValue("SUBMIT_TIME", new DataValue("", Types.VARCHAR));
                    ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    // condition
                    ub1.addCondition("OrganizationNO", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("stockTakeNO", new DataValue(stockTakeNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub1));

                }
            }


            this.doExecuteDataToDB();
        }
            
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
            
            //***********调用库存同步给三方，这是个异步，不会影响效能*****************
            //try {
            //    WebHookService.stockSync(eId,shopId,stockSync_billNo);
            //} catch (Exception ignored) { }
            

    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_StockTakeProcessReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockTakeProcessReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockTakeProcessReq req) throws Exception {
        return null;
    }
    
    /*
      返回库存调整单号
      @param req DCP_StockTakeProcessReq
      @return String 库存调整单号
      @throws Exception 抛异常
     */
    private String getAdjustNo(DCP_StockTakeProcessReq req) throws Exception {
		/*
		  库存调整单生成规则：
		  固定编码KCTZ+年月日+5位流水号
		  KCTZ2016120900001
		 */
        String adjustno;
        String shopId = req.getShopId();
        String organizationNO = req.getShopId();
        String eId = req.geteId();
        String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        String[] conditionValues = {shopId, eId,organizationNO }; // 查询条件
        String ajustnoHead="KCTZ" + bDate;
        String sql = " select ADJUSTNO from (select MAX(ADJUSTNO) ADJUSTNO from DCP_adjust "
                + " where SHOPID=? "
                + " and EID=? "
                + " and organizationno=? "
                + " and adjustno like '" + ajustnoHead + "%%')";
        
        List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
        if (getQData != null && !getQData.isEmpty()) {
            adjustno = (String) getQData.get(0).get("ADJUSTNO");
            if (adjustno != null && adjustno.length() > 0) {
                long i;
                adjustno = adjustno.substring(4);
                i = Long.parseLong(adjustno) + 1;
                adjustno = i + "";
                adjustno = "KCTZ" + adjustno;
            } else {
                //当天无单，从00001开始
                adjustno = "KCTZ" + bDate + "00001";
            }
        } else {
            //当天无单，从00001开始
            adjustno = "KCTZ" + bDate + "00001";
        }
        return adjustno;
    }
    
    /*
      查盈亏 新 参考wagas
      @param req DCP_StockTakeProcessReq
     * @return String 差异SQL
     * @throws Exception 抛异常
     */
    private String GetPLNEW_SQL(DCP_StockTakeProcessReq req,String Warehouse,String notGoodsmode ) throws Exception{
        StringBuffer sqlbuf = new StringBuffer();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String ofNO = req.getRequest().getOfNo();
        String docType = req.getRequest().getDocType();
        String stockTakeNO = req.getRequest().getStockTakeNo();
        String bDate = req.getRequest().getbDate();
        String taskWay = req.getRequest().getTaskWay();            ///1.营业中盘点  2.闭店盘点
        String notGoodsMode = req.getRequest().getNotGoodsMode();  ///1.库存变成零 2.库存不变
        String warehouse = req.getRequest().getWarehouse();
        String paraIsBatch = PosPub.getPARA_SMS(dao, eId, organizationNO, "Is_BatchNO");
        if (Check.Null(paraIsBatch) || !paraIsBatch.equals("Y")) {
            paraIsBatch = "N";
        }
        sqlbuf.append(""
                + " select distinct a.eid,a.organizationno,a.pluno,a.featureno,b.baseunit,a.ref_baseqty,a.baseqty,a.pqty,a.plqty,"
                + " a.oitem,a.warehouse,a.stocktake,a.batchno,a.proddate,c.plu_name,a.expdate,a.location "
                + " from ("
                + " select a.pluno,a.featureno,a.organizationno,a.eid,sum(a.baseqty) as baseqty,sum(a.pqty) as pqty,"
                + " sum(a.pqty)-sum(a.baseqty) as plqty,sum(a.ref_baseqty) as ref_baseqty ,max(a.oitem) as oitem,a.warehouse,"
                + " sum(stocktake) as stocktake,");
        ///启用批号
        if (paraIsBatch.equals("Y")) {
            sqlbuf.append( " trim(a.BATCHNO) as BATCHNO,max(a.proddate) as proddate,max(a.expdate) as expdate,trim(a.location) as location ");
        } else {
            sqlbuf.append( " N'' as batchno,N'' as proddate,N'' as expdate,N'' as location");
        }
        sqlbuf.append( " from (" );
        
        ///营业中盘点
        if (taskWay.equals("1")) {
            sqlbuf.append(" "
                    + " select a.pluno,a.featureno,a.qty as baseqty ,0 as pqty,0 as ref_baseqty, a.organizationno,a.eid ,0 as oitem,a.warehouse,"
                    + " '0' as stocktake,batchno,proddate,N'' as location,N'' as expdate from dcp_stock_day a "
                    + " where a.eid='"+ eId +"' and a.organizationno='"+organizationNO+"'  and warehouse='"+warehouse+"' "
                    + " union all"
                    + " select a.pluno,a.featureno,(a.baseqty*a.stocktype) as baseqty, 0 as pqty,0 as ref_wqty,a.organizationno,a.eid,0 as oitem,"
                    + " a.warehouse,'0' as stocktake,batchno,"
                    + " proddate,a.location as location,N'' as expdate from dcp_stock_detail a"
                    + " where a.eid='"+ eId +"' and a.organizationno='"+organizationNO+"' and warehouse='"+warehouse+"' and"
                    + " billtype in ('00','01','02','03','04','05','06','07','08','09','10','11','12','13','14','15','16','17','18','19','20','21','30','31','32','33','34','35','36','37','38','39','40','41','42') "
                    + " union all"
                    + " select a.pluno,a.featureno,0 as baseqty,a.baseqty as pqty,"
                    + " nvl(a.ref_baseqty,0) as ref_baseqty,a.organizationno,a.eid ,a.item as oitem,a.warehouse,'1' as stocktake,"
                    + " batch_no as batchno,prod_date as proddate,a.location,a.expdate"
                    + " from dcp_stocktake_detail a"
                    + " inner join dcp_stocktake b on a.eid=b.eid and a.shopid=b.shopid  and a.stocktakeno=b.stocktakeno"
                    + " where a.eid='"+ eId +"' and a.shopid='"+organizationNO+"' and b.stocktakeno='"+stockTakeNO+"' and b.warehouse='"+warehouse+"'"
                    + " ") ;
        }
        ///闭店盘点
        if (taskWay.equals("2")) {
            sqlbuf.append( " "
                    + " select a.pluno,a.featureno,a.qty as baseqty,0 as pqty,0 as ref_baseqty,a.organizationno,a.eid ,0 as oitem,a.warehouse,"
                    + " '0' as stocktake,batchno,proddate,N'' as location,N'' as expdate from dcp_stock_day_static a"
                    + " where a.eid='"+ eId +"' and a.organizationno='"+organizationNO+"'  and warehouse='"+warehouse+"' "
                    + " and edate in "
                    + " (select max(edate) from dcp_stock_day_static where eid='"+eId+"' and organizationno='"+organizationNO+"' and edate<='"+bDate+"') "
                    + " union all"
                    + " select a.pluno,a.featureno,(a.baseqty*a.stocktype) as baseqty,0 as pqty,0 as ref_baseqty,a.organizationno,"
                    + " a.eid,0 as oitem,a.warehouse,'0' as stocktake,batchno,"
                    + " proddate,a.location as location,N'' as expdate from dcp_stock_detail a"
                    + " where a.eid='"+ eId +"' and a.organizationno='"+organizationNO+"' and"
                    + " billtype in ('00','01','02','03','04','05','06','07','08','09','10','11','14','15','16','17','18','19','20','21','12','13','30','31','32','33','34','35','36','37','38','39','40','41','42')"
                    + " and a.accountdate<=to_date('"+ bDate +"','yyyymmdd') and warehouse='"+warehouse+"'"
                    + " union all"
                    + " select a.pluno,a.featureno,0 as baseqty,a.baseqty as pqty,"
                    + " nvl(a.ref_baseqty,0) as ref_baseqty , a.organizationno,a.eid ,a.item as oitem,a.warehouse,'1' as stocktake,"
                    + " batch_no as batchno,prod_date as proddate,a.location,a.expdate"
                    + " from dcp_stocktake_detail a"
                    + " inner join dcp_stocktake b on  a.eid=b.eid   and a.shopid=b.shopid  and a.stocktakeno=b.stocktakeno "
                    + " where  a.eid='"+ eId +"' and a.shopid='"+organizationNO+"' and b.stocktakeno='"+stockTakeNO+"'"
                    + " and b.warehouse='"+warehouse+"'"
                    + " ");
            
        }
        sqlbuf.append( " )a " );
        
        ///漏盘商品库存不变   （包括计划单全盘或抽盘、盘点模板、全盘、抽盘）
        if ( notGoodsMode.equals("2") || docType.equals("2") ) {
            sqlbuf.append(" inner join dcp_stocktake_detail b"
                    + " on a.eid=b.eid and a.organizationno=b.shopid and a.pluno=b.pluno "
                    + " and (a.featureno=b.featureno or (trim(a.featureno) is null and trim(b.featureno) is null))"
                    + " and b.stocktakeno='"+stockTakeNO+"'"
                    + " ");
            //启用批号
            if (paraIsBatch.equals("Y")) {
                sqlbuf.append(" and (trim(a.BATCHNO)=trim(b.batch_no) or (trim(a.BATCHNO) is null and trim(b.batch_no) is null))");

                sqlbuf.append(" and (trim(a.location)=trim(b.location) or (trim(a.location) is null and trim(b.location) is null))");

            }

        }
        ///漏盘商品库存变成零
        if (docType.equals("1") && notGoodsMode.equals("1") && !Check.Null(ofNO)) {
            sqlbuf.append(" "
                    + " inner join dcp_stocktask_list b"
                    + " on a.eid=b.eid and a.organizationno=b.organizationno and b.warehouse='"+warehouse+"' "
                    + " and stocktaskno='"+ofNO+"' and a.pluno=b.pluno");
        }
        ///全盘且漏盘商品库存变成零,不需要处理
        //////////////////////////////////
        //启用批号
        if (paraIsBatch.equals("Y")) {
            sqlbuf.append(" group by a.pluno,a.featureno,a.organizationno,a.eid,a.warehouse,trim(a.batchno),trim(a.location) )a ");
        } else {
            sqlbuf.append(" group by a.pluno,a.featureno,a.organizationno,a.eid,a.warehouse)a ");
        }
        sqlbuf.append(" inner join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno and b.status='100' ");
        sqlbuf.append(" left  join dcp_goods_lang c on a.eid=c.eid and a.pluno=c.pluno and c.lang_type='"+req.getLangType()+"' ");
        
        sqlbuf.append(" order by oitem ");
        return sqlbuf.toString();
    }
    
    private boolean confirmCheck(String eId,String shopId,String stockTakeNO, StringBuffer errorMessage,String Is_Take_ConfirmCheck ){
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String sql;
        String docNo;
        String docType;
        List<Map<String,Object>>getQData;
        try {
            // X-未确认（所有）+预计到货日单据
            if (Is_Take_ConfirmCheck.equals("X")){
                //收货通知单预计已经到货未收货-配送收货
                sql = " "
                        + " select a.receivingno,a.load_docno,a.doc_type,a.receiptdate from dcp_receiving a"
                        + " left join dcp_stockin b on a.eid=b.eid and a.shopid=b.shopid and a.receivingno=b.ofno"
                        + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.status='6' and a.doc_type in ('0','1')"
                        + " and (a.receiptdate<='"+sDate+"' or a.receiptdate is null)"
                        + " and b.stockinno is null"
                        + " order by a.doc_type,a.receivingno desc";
                
                getQData = this.doQueryData(sql,null);
                if (getQData != null && !getQData.isEmpty()) {
                    for (Map<String, Object> par : getQData) {
                        docNo   = par.get("LOAD_DOCNO").toString();
                        docType = par.get("DOC_TYPE").toString();
                        if (!Check.Null(docNo)){
                            if (docType.equals("0")) {
                                errorMessage.append("配送收货未收货,发货单号：" +docNo+"<br/>" );
                            }
                            if (docType.equals("1")) {
                                errorMessage.append("调拨收货未收货,发货单号：" +docNo+"<br/>"  );
                            }
                        }
                    }
                }
                
                // 2-统采直供通知
                sql= " select a.receivingno,a.load_docno,a.doc_type,a.receiptdate from dcp_receiving a"
                        + " left join dcp_sstockin b on a.eid=b.eid and a.shopid=b.shopid and a.receivingno=b.ofno"
                        + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.status='6' and a.doc_type='2'"
                        + " and (a.receiptdate <='"+sDate+"' or a.receiptdate is null)"
                        + " and b.sstockinno is null"
                        + " order by a.receivingno"
                        + " ";
                
                
                getQData = this.doQueryData(sql,null);
                if (getQData != null && !getQData.isEmpty()) {
                    for (Map<String, Object> par : getQData){
                        docNo = par.get("LOAD_DOCNO").toString();
                        if (!Check.Null(docNo) ) {
                            errorMessage.append("采购入库未收货,发货单号：" +docNo+"<br/>" ) ;
                        }
                    }
                }
            }
            
            //0 配送收货 1调拨收货 3其他入库
            sql= " select stockinno,doc_type from dcp_stockin "
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' "
                    + " order by doc_type,stockinno " ;
            getQData = this.doQueryData(sql,null);
            if (getQData != null && !getQData.isEmpty()) {
                for (Map<String, Object> par : getQData) {
                    docType = par.get("DOC_TYPE").toString();
                    docNo = par.get("STOCKINNO").toString();
                    if (!Check.Null(docNo) && !Check.Null(docType)) {
                        if (docType.equals("0")) {
                            errorMessage.append("配送收货单保存未确认,单号：" +docNo+"<br/>"  );
                        }
                        if (docType.equals("1")) {
                            errorMessage.append("调拨收货单保存未确认,单号：" +docNo+"<br/>"  );
                        }
                        if (docType.equals("3")) {
                            errorMessage.append("其他入库单保存未确认,单号：" +docNo+"<br/>" );
                        }
                    }
                }
            }
            
            //0-换季退货  1-调拨出库  2-次品退货 3-其他出库 4-移仓出库
            sql= " select stockoutno,doc_type,nvl(sourcemenu,0) as sourcemenu from dcp_stockout "
                    + " where eid='"+eId+"' and shopid='"+shopId+"' "
                    + " and status='0' "
                    + " order by doc_type,sourcemenu,stockoutno ";
            
            //以下注释,仅判断单据是否已提交,和调拨方式没关系 by jinzma 20210714
            /*if (transferStock.equals("2")) {
                sql= " select stockoutno,doc_type,status from dcp_stockout "
                        + " where eid='"+eId+"' and shopid='"+shopId+"' "
                        + " and ( status='0' or ( doc_type='1' and status='2') )  "
                        + " order by doc_type,status,stockoutno ";
            }*/
            getQData = this.doQueryData(sql,null);
            if (getQData != null && !getQData.isEmpty()) {
                for (Map<String, Object> par : getQData) {
                    docType = par.get("DOC_TYPE").toString();
                    docNo = par.get("STOCKOUTNO").toString();
                    String sourceMenu = par.get("SOURCEMENU").toString();
                    if (Check.Null(sourceMenu)){
                        sourceMenu = "0";      //0其他出库单，1试吃出库，2赠送出库
                    }
                    if (!Check.Null(docNo) && !Check.Null(docType)) {
                        if (docType.equals("0") || docType.equals("2")) {
                            errorMessage.append("退货出库单保存未确认,单号：" +docNo+"<br/>")  ;
                        }
                        if (docType.equals("1")) {
                            errorMessage.append("调拨出库单保存未确认,单号：" +docNo+"<br/>" ) ;
                        }
                        if (docType.equals("4")) {
                            errorMessage.append("移仓出库单保存未确认,单号：" +docNo+"<br/>" ) ;
                        }
                        if (docType.equals("3") && sourceMenu.equals("0")) {
                            errorMessage.append("其他出库单保存未确认,单号：" +docNo+"<br/>")  ;
                        }
                        if (docType.equals("3") && sourceMenu.equals("1")) {
                            errorMessage.append("试吃出库单保存未确认,单号：" +docNo+"<br/>")  ;
                        }
                        if (docType.equals("3") && sourceMenu.equals("2")) {
                            errorMessage.append("赠送出库单保存未确认,单号：" +docNo+"<br/>")  ;
                        }
                    }
                }
            }
            
            //0-完工入库 1-组合单 2-拆解单 3-转换合并单 4-转换拆解单
            sql = " select doc_type,pstockinno from dcp_pstockin  "
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' "
                    + " order by doc_type,pstockinno ";
            getQData = this.doQueryData(sql,null);
            if (getQData != null && !getQData.isEmpty()) {
                for (Map<String, Object> par : getQData) {
                    docType = par.get("DOC_TYPE").toString();
                    docNo = par.get("PSTOCKINNO").toString();
                    if (!Check.Null(docNo) && !Check.Null(docType)) {
                        if (docType.equals("0")) {
                            errorMessage.append("完工入库单保存未确认,单号：" +docNo+"<br/>" ) ;
                        }
                        if (docType.equals("1")) {
                            errorMessage.append("组合单保存未确认,单号：" +docNo+"<br/>" ) ;
                        }
                        if (docType.equals("2")) {
                            errorMessage.append("拆解单保存未确认,单号：" +docNo+"<br/>" ) ;
                        }
                        if (docType.equals("3")) {
                            errorMessage.append("转换合并单保存未确认,单号：" +docNo+"<br/>" ) ;
                        }
                        if (docType.equals("4")) {
                            errorMessage.append("转换拆解单保存未确认,单号：" +docNo+"<br/>" ) ;
                        }
                    }
                }
            }
            
            //采购入库  1.自采 2.统采 3.门店直供
            sql = " select sstockinno,doc_type from dcp_sstockin"
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' "
                    + " order by doc_type,sstockinno ";
            getQData = this.doQueryData(sql,null);
            if (getQData != null && !getQData.isEmpty()) {
                for (Map<String, Object> par : getQData) {
                    //docType = par.get("DOC_TYPE").toString();
                    docNo = par.get("SSTOCKINNO").toString();
                    if (!Check.Null(docNo)) {
                        errorMessage.append("采购入库单保存未确认,单号：" + docNo + "<br/>");
                    }
                }
            }
            
            //采购退货
            sql = " select sstockoutno from dcp_sstockout "
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' "
                    + " order by sstockoutno" ;
            getQData = this.doQueryData(sql,null);
            if (getQData != null && !getQData.isEmpty()) {
                for (Map<String, Object> par : getQData) {
                    docNo = par.get("SSTOCKOUTNO").toString();
                    if (!Check.Null(docNo)) {
                        errorMessage.append("采购退货单保存未确认,单号：" +docNo+"<br/>")  ;
                    }
                }
            }
            
            //报损单
            sql = " select lstockoutno from dcp_lstockout "
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' "
                    + " order by lstockoutno " ;
            getQData = this.doQueryData(sql,null);
            if (getQData != null && !getQData.isEmpty()) {
                for (Map<String, Object> par : getQData) {
                    docNo = par.get("LSTOCKOUTNO").toString();
                    if (!Check.Null(docNo)) {
                        errorMessage.append("报损单保存未确认,单号：" +docNo+"<br/>")  ;
                    }
                }
            }
            
            //拼胚单
            sql = " select pinpeino from dcp_pinpei"
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' "
                    + " order by pinpeino " ;
            getQData = this.doQueryData(sql,null);
            if (getQData != null && !getQData.isEmpty()) {
                for (Map<String, Object> par : getQData) {
                    docNo = par.get("PINPEINO").toString();
                    if (!Check.Null(docNo)) {
                        errorMessage.append("拼胚单保存未确认,单号：" +docNo+"<br/>")  ;
                    }
                }
            }
            
            //盘点单
            sql = " select * from ("
                    + " select a.stocktakeno,b.pluno,row_number() over (partition by a.stocktakeno order by b.pluno) rn from dcp_stocktake a "
                    + " inner join dcp_stocktake_detail b on a.eid=b.eid and a.shopid=b.shopid and a.stocktakeno=b.stocktakeno "
                    + " inner join dcp_stocktake_detail c on b.eid=c.eid and b.shopid=c.shopid and b.pluno=c.pluno and c.stocktakeno='"+stockTakeNO+"' "
                    + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.status='0' and a.stocktakeno<>'"+stockTakeNO+"' "
                    + " ) where rn<=10 ";
            getQData = this.doQueryData(sql,null);
            if (getQData != null && !getQData.isEmpty()) {
                for (Map<String, Object> par : getQData) {
                    docNo = par.get("STOCKTAKENO").toString();
                    String pluNo = par.get("PLUNO").toString();
                    if (!Check.Null(docNo)) {
                        errorMessage.append("有相同商品的盘点单保存未确认，单号："+docNo+ " 商品编号："+pluNo+"<br/>")  ;
                    }
                }
            }
            
            if (errorMessage.length()>0){
                return false;
            }else{
                return true;
            }
            
        }catch(Exception e) {
            errorMessage.append( e.getMessage());
            return false;
        }
    }
    
    /*
     * 取当天最大流水号,F_POS_TRNO函数 1个单号只能调一次，不然序号不连续
     * @return
     */
    private String getTRNO(String eId,String shopId,String bDate,String saleno) {
        RedisPosPub redis=new RedisPosPub();
        
        String trno="";
        try
        {
            //F_POS_TRNO 调一次加一次，只能控制1单只能调1次，不然序号不连续
            
            //先取缓存
            String key="SALE:"+eId+":"+shopId+":"+bDate;
            trno= redis.getHashMap(key,saleno);
            if (Check.Null(trno))
            {
                //取库
                String sql_trno = " select F_POS_TRNO('"+eId+"','"+shopId+"','"+bDate+"') TRNO FROM dual ";
                
                List<Map<String, Object>> getData_trno=dao.executeQuerySQL(sql_trno, null);
                if(getData_trno==null || getData_trno.isEmpty())
                {
                    trno="1";
                }
                else
                {
                    if (Check.Null(getData_trno.get(0).get("TRNO").toString()))
                    {
                        trno="1";
                    }
                    else
                    {
                        trno=getData_trno.get(0).get("TRNO").toString();
                    }
                }
                redis.setHashMap(key,saleno,trno);
            }
        }
        catch (Exception e)
        {
            try
            {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);
                
                pw.flush();
                pw.close();
                
                errors.flush();
                errors.close();
                
                //logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"计算销售单最大流水号TRNO报错1："+e.getMessage() + errors.toString() +"\r\n");
                
                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                //logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"计算销售单最大流水号TRNO报错2："+e1.getMessage() +"\r\n");
            }
            
            trno="";
        }
        finally
        {
            redis.Close();
        }
        return trno;
    }
    
    
    
}

