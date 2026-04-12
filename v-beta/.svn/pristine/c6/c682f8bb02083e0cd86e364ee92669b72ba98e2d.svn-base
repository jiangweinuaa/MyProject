package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockTakeCreateReq;
import com.dsc.spos.json.cust.req.DCP_StockTakeCreateReq.levelElm;
import com.dsc.spos.json.cust.req.DCP_StockTakeCreateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_StockTakeCreateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_StockTakeCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;

/**
 * 服務函數：StockTakeCreate
 *    說明：库存盘点新建
 * 服务说明：库存盘点新建
 * @author JZMA
 * @since  2018-11-21
 */
public class DCP_StockTakeCreate extends SPosAdvanceService<DCP_StockTakeCreateReq, DCP_StockTakeCreateRes> {
    @Override
    protected boolean isVerifyFail(DCP_StockTakeCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        List<level1Elm> jsonDatas = req.getRequest().getDatas();
        
        //必传值不为空
        String bDate = req.getRequest().getbDate();
        String status = req.getRequest().getStatus();
        String docType = req.getRequest().getDocType();
        String stockTakeID = req.getRequest().getStockTakeID();
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
        if (Check.Null(stockTakeID)) {
            errMsg.append("单据ID不可为空值, ");
            isFail = true;
        }
        if (Check.Null(warehouse)) {
            errMsg.append("仓库不可为空值, ");
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
        if (jsonDatas != null && !jsonDatas.isEmpty()) {
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
        return false;
    }
    
    @Override
    protected TypeToken<DCP_StockTakeCreateReq> getRequestType() {
        return new TypeToken<DCP_StockTakeCreateReq>(){};
    }
    
    @Override
    protected DCP_StockTakeCreateRes getResponseType() {
        return new DCP_StockTakeCreateRes();
    }
    
    @Override
    protected void processDUID(DCP_StockTakeCreateReq req,DCP_StockTakeCreateRes res) throws Exception {
        
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String pTemplateNO = req.getRequest().getpTemplateNo();
        String bDate = req.getRequest().getbDate();//单据日期
        String createBy = req.getEmployeeNo();
        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
        String createDate = df.format(cal.getTime());
        df = new SimpleDateFormat("HHmmss");
        String createTime = df.format(cal.getTime());
        
        //try {
            //新增模板只能选择一次判断  BY JZMA 20200401
            String Is_StockPTemplate_SelectOne = PosPub.getPARA_SMS(dao, eId, shopId, "Is_StockPTemplate_SelectOne");
            if (Check.Null(Is_StockPTemplate_SelectOne) || !Is_StockPTemplate_SelectOne.equals("N")) {
                String sql =" select * from DCP_stocktake "
                        + " where EID='"+eId+"' and SHOPID='"+shopId+"' and bdate='"+bDate+"' and ptemplateno='"+pTemplateNO+"' " ;
                List<Map<String, Object>> getQData = this.doQueryData(sql,null);
                if (getQData != null && !getQData.isEmpty()) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点模板每天只能选一次，模板："+pTemplateNO+" 当天已存在");
                }
            }
            
            if (!checkGuid(req)) {
                levelElm request = req.getRequest();
                String memo = request.getMemo();
                String status = request.getStatus();
                String docType = request.getDocType();
                String oType = request.getoType();
                String ofNO = request.getOfNo();
                String isBTake = request.getIsBTake();
                String taskWay = request.getTaskWay();
                String notGoodsMode = request.getNotGoodsMode();

                String isAdjustStock = request.getIsAdjustStock();     //是否调整库存Y/N/X Y转库存 N转销售 X不异动
                //【ID1030281】【大万3.0】车销业务场景下系统改造评估-----盘点差异生成销售单服务端 by jinzma 20221219
                //【ID1039808】【金贝儿3403】盘点单审核增加处理不异动库存情况 by jinzma 20240327
                if (Check.Null(isAdjustStock)) {
                    isAdjustStock = "Y";
                }
                String loadDocType = request.getLoadDocType();
                String loadDocNO = request.getLoadDocNo();
                String stockTakeID = request.getStockTakeID();
                String stockTakeNo =this.getOrderNO(req,"KCPD"); //getstockTakeNO(req);
                String totPqty = request.getTotPqty();
                String totAmt =request.getTotAmt();
                String totDistriAmt = request.getTotDistriAmt();
                String totCqty = request.getTotCqty();
                String warehouse = request.getWarehouse();
                
                String[] columns1 = {
                        "SHOPID", "ORGANIZATIONNO","EID","STOCKTAKENO","BDATE","MEMO","STATUS",
                        "DOC_TYPE","OTYPE","OFNO","CREATEBY","CREATE_DATE","CREATE_TIME", "TOT_PQTY",
                        "TOT_AMT", "TOT_CQTY", "LOAD_DOCTYPE", "LOAD_DOCNO","IS_BTAKE" ,"STOCKTAKE_ID",
                        "PTEMPLATENO", "WAREHOUSE", "TASKWAY", "NOTGOODSMODE","TOT_DISTRIAMT","IS_ADJUST_STOCK",
                        "SUBSTOCKIMPORT","CREATE_CHATUSERID","EMPLOYEEID","DEPARTID"
                };
                
                //新增單身 (多筆)
                List<level1Elm> jsonDatas = request.getDatas();
                
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
                                    keyVal = stockTakeNo;
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
                                        new DataValue(stockTakeNo, Types.VARCHAR),
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
                
                DataValue[] insValue1 = new DataValue[]{
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(organizationNO, Types.VARCHAR),
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(stockTakeNo, Types.VARCHAR),
                        new DataValue(bDate, Types.VARCHAR),
                        new DataValue(memo, Types.VARCHAR),
                        new DataValue(status, Types.VARCHAR),
                        new DataValue(docType, Types.VARCHAR),
                        new DataValue(oType, Types.VARCHAR),
                        new DataValue(ofNO, Types.VARCHAR),
                        new DataValue(createBy, Types.VARCHAR),
                        new DataValue(createDate, Types.VARCHAR),
                        new DataValue(createTime, Types.VARCHAR),
                        new DataValue(totPqty, Types.VARCHAR),
                        new DataValue(totAmt, Types.VARCHAR),
                        new DataValue(totCqty, Types.VARCHAR),
                        new DataValue(loadDocType, Types.VARCHAR),
                        new DataValue(loadDocNO, Types.VARCHAR),
                        new DataValue(isBTake, Types.VARCHAR),
                        new DataValue(stockTakeID, Types.VARCHAR),
                        new DataValue(pTemplateNO, Types.VARCHAR),
                        new DataValue(warehouse, Types.VARCHAR),
                        new DataValue(taskWay, Types.VARCHAR),
                        new DataValue(notGoodsMode, Types.VARCHAR),
                        new DataValue(totDistriAmt, Types.VARCHAR),
                        new DataValue(isAdjustStock, Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),   //盘点子任务导入状态（0初复均未导入；1初盘导入；2初复盘均导入）
                        new DataValue(req.getChatUserId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getEmployeeId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getDepartId(), Types.VARCHAR),
                };
                
                InsBean ib1 = new InsBean("DCP_STOCKTAKE", columns1);
                ib1.addValues(insValue1);
                this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
                
                this.doExecuteDataToDB();
                
                //【ID1022160】【霸王3.0】库存盘点问题  by jinzma 20211119
                DCP_StockTakeCreateRes.level1Elm datas = res.new level1Elm();
                datas.setStockTakeNo(stockTakeNo);
                res.setDatas(datas);
                
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
            }
            else{
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据已存在，请重新确认！ ");
            }
        //}
        //catch (Exception e){
        //    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        //}
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_StockTakeCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockTakeCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockTakeCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected String getQuerySql(DCP_StockTakeCreateReq req) throws Exception {
        return null;
    }
    
    private String getstockTakeNO(DCP_StockTakeCreateReq req) throws Exception {
        /*
         * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
         * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
         */
        String stockTakeNO = null;
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        StringBuffer sqlbuf = new StringBuffer();
        String bDate= PosPub.getAccountDate_SMS(dao, eId, shopId);
        
        String[] conditionValues = { organizationNO, eId, shopId }; // 查询盘点单号
        stockTakeNO = "KCPD" + bDate;
        sqlbuf.append("" + "select stockTakeNO  from ( " + "select max(stockTakeNO) as  stockTakeNO "
                + "  from DCP_stockTake " + " where OrganizationNO = ? " + " and EID = ? " + " and SHOPID = ? "
                + " and stockTakeNO like '%%" + stockTakeNO + "%%' "); // 假資料
        sqlbuf.append(" ) TBL ");
        
        List<Map<String, Object>> getQData = this.doQueryData(sqlbuf.toString(), conditionValues);
        
        if (getQData != null && !getQData.isEmpty()) {
            stockTakeNO = (String) getQData.get(0).get("STOCKTAKENO");
            if (stockTakeNO != null && stockTakeNO.length() > 0) {
                long i;
                stockTakeNO = stockTakeNO.substring(4);
                i = Long.parseLong(stockTakeNO) + 1;
                stockTakeNO = i + "";
                stockTakeNO = "KCPD" + stockTakeNO;
            }else{
                stockTakeNO = "KCPD" + bDate + "00001";
            }
        }else{
            stockTakeNO = "KCPD" + bDate + "00001";
        }
        
        return stockTakeNO;
    }
    
    private boolean checkGuid(DCP_StockTakeCreateReq req) throws Exception {
        boolean existGuid;
        String guid = req.getRequest().getStockTakeID();
        String sql = "select STOCKTAKE_ID from DCP_STOCKTAKE where STOCKTAKE_ID = '"+guid+"' " ;
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        
        if (getQData != null && !getQData.isEmpty()) {
            existGuid = true;
        }else{
            existGuid =  false;
        }
        return existGuid;
    }
    
}
