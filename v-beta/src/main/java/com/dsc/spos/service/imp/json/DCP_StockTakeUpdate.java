package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockTakeUpdateReq;
import com.dsc.spos.json.cust.req.DCP_StockTakeUpdateReq.levelElm;
import com.dsc.spos.json.cust.req.DCP_StockTakeUpdateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_StockTakeUpdateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_StockTakeUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;

/**
 * 服務函數：StockTakeUpdateDCP
 * 服务说明：库存盘点修改
 * @author JZMA
 * @since  2018-11-21
 */
@SuppressWarnings("ALL")
public class DCP_StockTakeUpdate extends SPosAdvanceService<DCP_StockTakeUpdateReq, DCP_StockTakeUpdateRes> {
    @Override
    protected boolean isVerifyFail(DCP_StockTakeUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        List<level1Elm> jsonDatas = req.getRequest().getDatas();
        //必传值不为空
        String stockTakeNO = req.getRequest().getStockTakeNo();
        String bDate = req.getRequest().getbDate();
        String status = req.getRequest().getStatus();
        String docType = req.getRequest().getDocType();
        String warehouse = req.getRequest().getWarehouse();
        String taskWay = req.getRequest().getTaskWay();
        String notGoodsMode = req.getRequest().getNotGoodsMode();
        String totPqty = req.getRequest().getTotPqty();
        String totAmt = req.getRequest().getTotAmt();
        String totDistriAmt=req.getRequest().getTotDistriAmt();
        String totCqty = req.getRequest().getTotCqty();
        
        if (Check.Null(bDate)) {
            errMsg.append("营业日期不可为空值, ");
            isFail = true;
        }
        if (Check.Null(status)) {
            errMsg.append("状态不可为空值, ");
            isFail = true;
        }
        if (Check.Null(docType)) {
            errMsg.append("单据类型不可为空值, ");
            isFail = true;
        }
        if (Check.Null(stockTakeNO)) {
            errMsg.append("单号不可为空值, ");
            isFail = true;
        }
        if (Check.Null(taskWay)) {
            errMsg.append("盘点方式不可为空值, ");
            isFail = true;
        }
        if (Check.Null(notGoodsMode)) {
            errMsg.append("漏盘商品处理方式不可为空值, ");
            isFail = true;
        }
        if (Check.Null(warehouse)) {
            errMsg.append("仓库不可为空值, ");
            isFail = true;
        }
        if (Check.Null(totPqty)) {
            errMsg.append("合计录入数量不可为空值, ");
            isFail = true;
        }
        if (Check.Null(totAmt)) {
            errMsg.append("合计录入数量不可为空值, ");
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
        
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        //【ID1025351】 by jinzma 20220420
        // 【深圳乐沙儿3.0】门店2028的库存盘点单KCPD2022041900014商品210200207的盘点差异量4个，但没有生成库存调整单
        if (docType.equals("1") && notGoodsMode.equals("1")){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "抽盘不支持未盘商品库存变成零, ");
        }
        
        //PDA盘点允许单身为空  by jinzma 20210301
        if (jsonDatas !=null && jsonDatas.isEmpty() == false) {
            for (level1Elm par : jsonDatas) {
                //必传值不为空
                String item = par.getItem();
                String oItem = par.getoItem();
                String pluNo = par.getPluNo();
                String pqty = par.getPqty();
                String punit = par.getPunit();
                String price = par.getPrice();
                String amt = par.getAmt();
                String refBaseQty = par.getRefBaseQty();
                String baseUnit = par.getBaseUnit();
                String baseQty = par.getBaseQty();
                String unitRatio = par.getUnitRatio();
                
                if (Check.Null(item)) {
                    errMsg.append("商品" + pluNo + "项次不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(oItem)) {
                    errMsg.append("商品" + pluNo + "来源项次不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(pluNo)) {
                    errMsg.append("商品编码不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(pqty)) {
                    errMsg.append("商品" + pluNo + "盘点数量不可为空值, ");
                    isFail = true;
                }
                if (punit == null) {
                    errMsg.append("商品" + pluNo + "盘点单位不可为空值, ");
                    isFail = true;
                }
                if (price == null) {
                    errMsg.append("商品" + pluNo + "单价不可为空值, ");
                    isFail = true;
                }
                if (amt == null) {
                    errMsg.append("商品" + pluNo + "金额不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(refBaseQty) && taskWay.equals("1")) {
                    errMsg.append("商品" + pluNo + "实时数不可为空值, ");
                    isFail = true;
                }
                if (baseUnit == null) {
                    errMsg.append("商品" + pluNo + "基本单位不可为空值, ");
                    isFail = true;
                }
                if (baseQty == null) {
                    errMsg.append("商品" + pluNo + "基本数量不可为空值, ");
                    isFail = true;
                }
                if (unitRatio == null) {
                    errMsg.append("商品" + pluNo + "单位转换率不可为空值, ");
                    isFail = true;
                }
                
                if (isFail) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }
            }
        }
        return isFail;
    }
    
    @Override
    protected TypeToken<DCP_StockTakeUpdateReq> getRequestType() {
        return new TypeToken<DCP_StockTakeUpdateReq>(){};
    }
    
    @Override
    protected DCP_StockTakeUpdateRes getResponseType() {
        return new DCP_StockTakeUpdateRes();
    }
    
    @Override
    protected void processDUID(DCP_StockTakeUpdateReq req,DCP_StockTakeUpdateRes res) throws Exception {
        levelElm request = req.getRequest();
        String stockTakeNO = request.getStockTakeNo();
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String bDate = request.getbDate();
        String docType = request.getDocType();
        String memo = request.getMemo();
        String status = request.getStatus();
        String pTemplateNO = request.getpTemplateNo();
        String warehouse = request.getWarehouse();
        String taskWay = request.getTaskWay();
        String notGoodsMode = request.getNotGoodsMode();


        //【ID1039808】【金贝儿3403】盘点单审核增加处理不异动库存情况 by jinzma 20240327
        //【ID1030281】【大万3.0】车销业务场景下系统改造评估-----盘点差异生成销售单服务端 by jinzma 20221219
        String isAdjustStock = request.getIsAdjustStock();      //是否调整库存Y/N/X Y转库存 N转销售 X不异动
        if (Check.Null(isAdjustStock)) {
            isAdjustStock = "Y";
        }
        String modifyBy = req.getEmployeeNo();
        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String modifyDate = df.format(cal.getTime());
        df = new SimpleDateFormat("HHmmss");
        String modifyTime = df.format(cal.getTime());
        String totPqty = request.getTotPqty();
        String totAmt =request.getTotAmt();
        String totDistriAmt = request.getTotDistriAmt();
        String totCqty = request.getTotCqty();
        
        try {
            //校验此盘点单是否存在
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (getQData != null && getQData.isEmpty() == false) {
                String keyDate=getQData.get(0).get("BDATE").toString();
                //删除原来单身
                DelBean db1 = new DelBean("DCP_STOCKTAKE_DETAIL");
                db1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db1.addCondition("STOCKTAKENO", new DataValue(stockTakeNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));
                
                DelBean db2 = new DelBean("DCP_STOCKTAKE_DETAIL_UNIT");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db2.addCondition("STOCKTAKENO", new DataValue(stockTakeNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));
                
                //新增單身 (多筆)
                List<level1Elm> jsonDatas = req.getRequest().getDatas();
                //PDA盘点允许单身为空  by jinzma 20210301
                if (jsonDatas!=null) {
                    for (level1Elm par : jsonDatas) {
                        int insColCt = 0;
                        String[] columnsName = {
                                "STOCKTAKENO", "SHOPID", "ITEM", "OITEM", "PLUNO",
                                "PRICE", "AMT", "EID", "ORGANIZATIONNO",
                                "PUNIT", "PQTY", "BASEUNIT", "UNIT_RATIO", "BASEQTY", "REF_BASEQTY", "WAREHOUSE", "MEMO",
                                "BATCH_NO", "PROD_DATE", "DISTRIPRICE", "DISTRIAMT", "BDATE", "FEATURENO","LOCATION","EXPDATE","ISBATCHADD"
                        };
                        DataValue[] columnsVal = new DataValue[columnsName.length];
                        
                        for (int i = 0; i < columnsVal.length; i++) {
                            String keyVal = null;
                            switch (i) {
                                case 0:
                                    keyVal = stockTakeNO;
                                    break;
                                case 1:
                                    keyVal = shopId;
                                    break;
                                case 2:
                                    keyVal = par.getItem(); //item
                                    break;
                                case 3:
                                    keyVal = par.getoItem();
                                    if (Check.Null(keyVal))
                                        keyVal = "0";
                                    break;
                                case 4:
                                    keyVal = par.getPluNo(); //pluNO
                                    break;
                                case 5:
                                    keyVal = par.getPrice();   //price
                                    if (Check.Null(keyVal))
                                        keyVal = "0";
                                    break;
                                case 6:
                                    keyVal = par.getAmt();    //amt
                                    if (Check.Null(keyVal))
                                        keyVal = "0";
                                    break;
                                case 7:
                                    keyVal = eId;
                                    break;
                                case 8:
                                    keyVal = organizationNO;
                                    break;
                                case 9:
                                    keyVal = par.getPunit(); //punit
                                    break;
                                case 10:
                                    keyVal = par.getPqty(); //pqty
                                    break;
                                case 11:
                                    keyVal = par.getBaseUnit();
                                    break;
                                case 12:
                                    keyVal = par.getUnitRatio();
                                    break;
                                case 13:
                                    keyVal = par.getBaseQty();
                                    break;
                                case 14:
                                    keyVal = par.getRefBaseQty();
                                    break;
                                case 15:
                                    keyVal = par.getWarehouse();
                                    break;
                                case 16:
                                    keyVal = par.getMemo();
                                    break;
                                case 17:
                                    keyVal = par.getBatchNo();
                                    break;
                                case 18:
                                    keyVal = par.getProdDate();
                                    break;
                                case 19:
                                    keyVal = par.getDistriPrice();
                                    if (Check.Null(keyVal))
                                        keyVal = "0";
                                    break;
                                case 20:
                                    keyVal = par.getDistriAmt();
                                    if (Check.Null(keyVal))
                                        keyVal = "0";
                                    break;
                                case 21:
                                    keyVal = bDate;
                                    break;
                                case 22:
                                    keyVal = par.getFeatureNo();
                                    if (Check.Null(keyVal))
                                        keyVal = " ";
                                    break;
                                case 23:
                                    keyVal = par.getLocation();
                                    if (Check.Null(keyVal))
                                        keyVal = " ";
                                    break;
                                case 24:
                                    keyVal = par.getExpDate();
                                    break;
                                case 25:
                                    keyVal = par.getIsBatchAdd();
                                    break;
                                default:
                                    break;
                            }
                            
                            if (keyVal != null) {
                                insColCt++;
                                if (i == 2 || i == 3) {
                                    columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
                                } else if (i == 5 || i == 6 || i == 10 || i == 12 || i == 13) {
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
                        // 依照傳入參數組譯要insert的欄位與數值；
                        insColCt = 0;
                        for (int i = 0; i < columnsVal.length; i++) {
                            if (columnsVal[i] != null) {
                                columns2[insColCt] = columnsName[i];
                                insValue2[insColCt] = columnsVal[i];
                                insColCt++;
                                if (insColCt >= insValue2.length) break;
                            }
                        }
                        InsBean ib2 = new InsBean("DCP_STOCKTAKE_DETAIL", columns2);
                        ib2.addValues(insValue2);
                        this.addProcessData(new DataProcessBean(ib2));
                        
                        //【ID1019116】【霸王餐饮】茶颜悦色的个案功能移植到3.0-盘点时候支持多单位盘点（后端服务）
                        List<level2Elm> unitList = par.getUnitList();
                        if (unitList!=null) {
                            String[] columns = {
                                    "EID","ORGANIZATIONNO","SHOPID","STOCKTAKENO","ITEM","OITEM","PQTY","PUNIT","UNIT_RATIO"
                            };
                            int item=1;
                            for (level2Elm unit:unitList){
    
                                //【ID1026070】【詹记3.0】门店多单位盘点，偶发性出现录入盘点单，保存时整单数量为0。录入数量，保存数量串单位，导致库存混乱。
                                //增加盘点录入数为空检查  by jinzma 20220520
                                if (Check.Null(unit.getPqty())){
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品:"+par.getPluNo()+" 多单位盘点录入数量不能为空");
                                }
                                
                                DataValue[]	insValue = new DataValue[] {
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(shopId, Types.VARCHAR),
                                        new DataValue(shopId, Types.VARCHAR),
                                        new DataValue(stockTakeNO, Types.VARCHAR),
                                        new DataValue(item, Types.VARCHAR),
                                        new DataValue(par.getItem(), Types.VARCHAR),
                                        new DataValue(unit.getPqty(), Types.VARCHAR),
                                        new DataValue(unit.getPunit(), Types.VARCHAR),
                                        new DataValue(unit.getUnitRatio(), Types.VARCHAR),
                                };
                                
                                InsBean ib = new InsBean("DCP_STOCKTAKE_DETAIL_UNIT", columns);
                                ib.addValues(insValue);
                                this.addProcessData(new DataProcessBean(ib));
                                item++;
                            }
                        }
                    }
                }
                
                UptBean ub1 = new UptBean("DCP_STOCKTAKE");
                ub1.addUpdateValue("MODIFY_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                ub1.addUpdateValue("TASKWAY", new DataValue(taskWay, Types.VARCHAR));
                ub1.addUpdateValue("NOTGOODSMODE", new DataValue(notGoodsMode, Types.VARCHAR));
                ub1.addUpdateValue("PTEMPLATENO", new DataValue(pTemplateNO, Types.VARCHAR));
                ub1.addUpdateValue("IS_ADJUST_STOCK", new DataValue(isAdjustStock, Types.VARCHAR));
                ub1.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
                ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
                ub1.addUpdateValue("DOC_TYPE", new DataValue(docType, Types.VARCHAR));
                ub1.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
                ub1.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
                ub1.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
                ub1.addUpdateValue("TOT_PQTY", new DataValue(totPqty, Types.VARCHAR));
                ub1.addUpdateValue("TOT_AMT", new DataValue(totAmt, Types.VARCHAR));
                ub1.addUpdateValue("TOT_CQTY", new DataValue(totCqty, Types.VARCHAR));
                ub1.addUpdateValue("TOT_DISTRIAMT", new DataValue(totDistriAmt, Types.VARCHAR));
                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                ub1.addUpdateValue("WAREHOUSE", new DataValue(warehouse, Types.VARCHAR));
                //ub1.addUpdateValue("SUBSTOCKIMPORT", new DataValue(, Types.VARCHAR));  //红艳确认SUBSTOCKIMPORT字段不修改 20210302
                ub1.addUpdateValue("BDATE", new DataValue(bDate, Types.VARCHAR));
                ub1.addUpdateValue("EMPLOYEEID", new DataValue(req.getRequest().getEmployeeId(), Types.VARCHAR));
                ub1.addUpdateValue("DEPARTID", new DataValue(req.getRequest().getDepartId(), Types.VARCHAR));
                ub1.addUpdateValue("IS_BTAKE",new DataValue(req.getRequest().getIsBTake(),Types.VARCHAR));
                // condition
                ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub1.addCondition("STOCKTAKENO", new DataValue(stockTakeNO, Types.VARCHAR));
                
                //更新
                this.addProcessData(new DataProcessBean(ub1));
                
                this.doExecuteDataToDB();
                
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
                
            }else{
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "单据不存在或已确认！");
            }
        }catch(Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
        
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_StockTakeUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockTakeUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockTakeUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected String getQuerySql(DCP_StockTakeUpdateReq req) throws Exception {
        String sql = ""
                + "select stockTakeNO,BDATE "
                + " from DCP_stockTake "
                + " where OrganizationNO ='"+req.getOrganizationNO()+"' "
                + " and EID ='"+req.geteId()+"' "
                + " and SHOPID ='"+req.getShopId()+"' "
                + " and stockTakeNO ='"+req.getRequest().getStockTakeNo()+"'"
                + " and status='0' ";
        return sql;
    }
    
}
