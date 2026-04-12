package com.dsc.spos.service.imp.json;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import java.util.Calendar;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockTaskCreateReq;
import com.dsc.spos.json.cust.res.DCP_StockTaskCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * StockTaskCreateDCP 專用的 response json
 *   說明：收货通知单新增
 * 服务说明：收货通知单新增
 * @author chensong
 * @since  2016-11-22
 */
public class DCP_StockTaskCreate extends SPosAdvanceService<DCP_StockTaskCreateReq, DCP_StockTaskCreateRes> {
    private Logger loger = LogManager.getLogger(DCP_StockTaskCreate.class.getName());

    @Override
    protected boolean isVerifyFail(DCP_StockTaskCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        List<Map<String, String>> jsonDatas_shop = req.getStocktaskshop();
        List<Map<String, String>> jsonDatas_range = req.getStocktaskrange();
        //		List<Map<String, String>> jsonDatas_list = req.getStocktasklist();

        //必传值不为空
        String Bdate = req.getBdate();
        String docType = req.getDocType();
        String loadDocNO =req.getLoadDocNO();
        //    String btake =req.getBtake();
        //    String taskWay =req.getTaskWay();
        //    String notgoodsMode =req.getNotgoodsMode();

        
        if (Check.Null(Bdate)) {
            errMsg.append("盘点日期不可为空值, ");
            isFail = true;
        }

        if (Check.Null(docType)) {
            errMsg.append("盘点类型不可为空值, ");
            isFail = true;
        }

        if (Check.Null(loadDocNO)) {
            errMsg.append("资料来源单号不可为空值, ");
            isFail = true;
        }

        //		if (Check.Null(btake)) {
        //			errCt++;
        //			errMsg.append("是否盲盘不可为空值, ");
        //			isFail = true;
        //		}

        //		if (Check.Null(taskWay)) {
        //			errCt++;
        //			errMsg.append("盘点方式不可为空值, ");
        //			isFail = true;
        //		}

        //		if (Check.Null(notgoodsMode)) {
        //			errCt++;
        //			errMsg.append("未盘商品处理方式不可为空值, ");
        //			isFail = true;
        //		}

        if (docType.equals("1")) {
            //查询默认仓库
            String sqlWarehouse="select ORGANIZATIONNO,in_cost_warehouse from DCP_ORG where EID='"+req.geteId()+"' AND ORG_FORM='2' ";
            List<Map<String, Object>> getQData = this.doQueryData(sqlWarehouse,null);
            if (getQData == null || getQData.isEmpty())
            {
                errMsg.append("门店默认收货成本仓库无信息, ");
                isFail = true;
            }

            for (Map<String, String> par : jsonDatas_shop) {
                String shopId = par.get("shopId");
                String warehouse = par.get("warehouse");

                //ERP仓库为空，门店取默认收货成本仓库
                if (Check.Null(warehouse)) {
                    Map<String, Object> map=new HashMap<>();
                    map.put("shopId", shopId);
                    List<Map<String, Object>> getQFind=MapDistinct.getWhereMap(getQData, map, false);
                    if (getQFind == null || getQFind.isEmpty()) {
                        errMsg.append("门店默认收货成本仓库不可为空值, ");
                        isFail = true;
                    } else {
                        warehouse=getQFind.get(0).get("IN_COST_WAREHOUSE").toString();
                        par.put("warehouse", warehouse);
                    }
                }

                if (Check.Null(shopId)) {
                    errMsg.append("门店不可为空值, ");
                    isFail = true;
                }

                if (Check.Null(warehouse)) {
                    errMsg.append("仓库不可为空值, ");
                    isFail = true;
                }

            }
            for (Map<String, String> par : jsonDatas_range) {
                String groupNO = par.get("groupNO");
                String attrNO = par.get("attrNO");
                String valueNO = par.get("valueNO");

                if (Check.Null(groupNO)) {
                    errMsg.append("组别不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(attrNO)) {
                    errMsg.append("属性类型不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(valueNO)) {
                    errMsg.append("属性编码不可为空值, ");
                    isFail = true;
                }
            }
            //			for (Map<String, String> par : jsonDatas_list) {
            //				String pluNO = par.get("pluNO");
            //				String price = par.get("price");
            //				if (Check.Null(pluNO)) {
            //					errCt++;
            //					errMsg.append("商品编码不可为空值, ");
            //					isFail = true;
            //				}
            //				if (Check.Null(price)) {
            //					errCt++;
            //					errMsg.append("商品单价不可为空值, ");
            //					isFail = true;
            //				}
            //			}
        }

        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_StockTaskCreateReq> getRequestType() {
        return new TypeToken<DCP_StockTaskCreateReq>(){};
    }

    @Override
    protected DCP_StockTaskCreateRes getResponseType() {
        return new DCP_StockTaskCreateRes();
    }

    @Override
    protected void processDUID(DCP_StockTaskCreateReq req,DCP_StockTaskCreateRes res) throws Exception {
        try{
            String eId = req.geteId();
            String warehouse ="";
            //		String Bdate = req.getBdate();
            String docType = req.getDocType();
            String loadDocNO = req.getLoadDocNO();
            String loadDocType = req.getLoadDocType();
            String remark = req.getRemark();
            String btake =req.getBtake();
            String stockTaskNO = "";
            String shopId = "";
            String taskWay =req.getTaskWay();
            String notgoodsMode =req.getNotgoodsMode();

            //【ID1039808】【金贝儿3403】盘点单审核增加处理不异动库存情况 by jinzma 20240327
            String isAdjustStock = req.getIsAdjustStock();  //是否调整库存Y/N/X Y转库存 N转销售 X不异动
            if (Check.Null(isAdjustStock)) {
                isAdjustStock="Y";
            }

            int c = 0;
            String operation_type=req.getOperation_type()==null?"0":req.getOperation_type();
            if(operation_type.equals("2")) {
                //这种类型代表失效盘点单
                //最好是先查询一下是否存在此盘点单
                String existTask="select * from DCP_STOCKTASK where EID='"+eId+"'  and LOAD_DOCNO='"+loadDocNO+"' ";
                List<Map<String, Object>> stasklist=this.doQueryData(existTask, null);
                if(stasklist==null||stasklist.isEmpty()) {
                    res.setSuccess(false);
                    res.setServiceStatus("000");
                    res.setServiceDescription("服务执行失败，门店没有该盘点计划");
                    return;
                }

                String existTake="select * from DCP_STOCKTAKE where EID='"+eId+"'  and LOAD_DOCNO='"+loadDocNO+"' ";
                List<Map<String, Object>> stakelist=this.doQueryData(existTake, null);
                if(stakelist!=null&&!stakelist.isEmpty()) {
                    res.setSuccess(false);
                    res.setServiceStatus("000");
                    res.setServiceDescription("服务执行失败，门店已有盘点单据产生了此盘点计划");
                    return;
                }

                UptBean up=new UptBean("DCP_STOCKTASK");
                up.addUpdateValue("STATUS", new DataValue("8", Types.VARCHAR));
                up.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                up.addCondition("LOAD_DOCNO", new DataValue(loadDocNO, Types.VARCHAR));


                //更新
                this.addProcessData(new DataProcessBean(up));


                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
                return;

            }
            List<Map<String, String>> jsonDatas_Shop = req.getStocktaskshop();
            String[] newStockTaskNO = new String[jsonDatas_Shop.size()] ;

            //插入盘点任务主表DCP_STOCKTASK  (插入之前需做一个判断 IF NOT EXISTS (select * from DCP_STOCKTASK WHERE EID=传入的对应的enterprise_no值  AND LOAD_DOCNO=传入的对应load_doc_no值) 则插入  否则不做处理)
            String sql = " SELECT stockTaskNO,STATUS FROM DCP_STOCKTASK WHERE  EID='"+eId+"' and LOAD_DOCNO='"+loadDocNO+"' ";
            List<Map<String, Object>> getQData_checkStockTask = this.doQueryData(sql,null);
            if (getQData_checkStockTask != null && !getQData_checkStockTask.isEmpty()) {
                //如果status为8是作废状态
                String status= getQData_checkStockTask.get(0).get("STATUS").toString();
                if(status.equals("8")) {
                    //删除单头(DCP_STOCKTASK)
                    String delSql1 = "delete from DCP_STOCKTASK where load_docno = '"+loadDocNO+"' "
                            + "and EID = '"+eId+"'" ;

                    //删除单身(DCP_Stocktask_Range)
                    String delSql2 = "delete from DCP_Stocktask_Range where SHOPID||stocktaskno in "
                            + "(select SHOPID||stocktaskno from DCP_STOCKTASK "
                            + " where load_docno = '"+loadDocNO+"' "
                            + " and EID = '"+eId+"')" ;

                    //删除单身(DCP_stocktask_list)
                    String delSql3 = "delete from DCP_stocktask_list   where SHOPID||stocktaskno in "
                            + "(select SHOPID||stocktaskno from DCP_STOCKTASK "
                            + " where load_docno = '"+loadDocNO+"' "
                            + "and EID = '"+eId+"')" ;


                    ExecBean ec1 = new ExecBean(delSql1);
                    ExecBean ec2 = new ExecBean(delSql2);
                    ExecBean ec3 = new ExecBean(delSql3);

                    //				ec3.setExecsql(delSql3);
                    //				ec2.setExecsql(delSql2);
                    //				ec1.setExecsql(delSql1);


                    this.addProcessData(new DataProcessBean(ec3));
                    this.addProcessData(new DataProcessBean(ec2));
                    this.addProcessData(new DataProcessBean(ec1));
                    this.doExecuteDataToDB();

                    getQData_checkStockTask= null;

                }

            }


            if (getQData_checkStockTask == null || getQData_checkStockTask.isEmpty()) { //无资料
                for (Map<String, String> par_Shop : jsonDatas_Shop) {
                    shopId = par_Shop.get("shopId");
                    warehouse=par_Shop.get("warehouse");
                    stockTaskNO = getStockTaskNO(req,shopId);
                    newStockTaskNO[c] = stockTaskNO;
                    c++;
                    String status = "6";//单头	STATUS			创建时，默认给值6，
                    String createBy ="";

                    Calendar cal = Calendar.getInstance();//获得当前时间
                    SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
                    String bDate_req = req.getBdate();
                    String bDate = "";
                    if (bDate_req != null && bDate_req.length()>0){
                        bDate_req=bDate_req.replace("-", "");
                        bDate = bDate_req;
                        //bDate = bDate_req.substring(0,4)+bDate_req.substring(5,7)+bDate_req.substring(8,10);
                    }
                    //System.out.println(bDate);

                    if (btake == null || !btake.equals("N")){
                        btake = "Y";
                    }

                    if (taskWay == null || !taskWay.equals("1")){
                        taskWay = "2";
                    }

                    if (notgoodsMode == null || !notgoodsMode.equals("1")){
                        notgoodsMode = "2";
                    }


                    String createDate = df.format(cal.getTime());
                    //				String sysDate = df.format(cal.getTime());

                    df=new SimpleDateFormat("HHmmss");
                    String createTime = df.format(cal.getTime());


                    String[] columns1 = {
                            "STOCKTASKNO", "SHOPID", "BDATE", "MEMO", "DOC_TYPE", "STATUS",
                            "CREATEBY", "CREATE_DATE", "CREATE_TIME", "LOAD_DOCTYPE","LOAD_DOCNO",
                            "EID", "ORGANIZATIONNO", "IS_BTAKE","WAREHOUSE","TASKWAY",
                            "NOTGOODSMODE","SUBMITSTATUS","IS_ADJUST_STOCK" };
                    DataValue[] insValue1 = null;

                    if (docType.equals("1")){//全盘时没有以下两个单身
                        // 新增單身 (多筆)
                        List<Map<String, String>> jsonDatas_Range = req.getStocktaskrange();
                        for (Map<String, String> par_Range : jsonDatas_Range) {
                            int insColCt = 0;
                            String[] columnsName = { "STOCKTASKNO", "SHOPID", "ORGANIZATIONNO", "EID",
                                    "GROUPNO","ATTRNO", "VALUENO","WAREHOUSE","BDATE"};

                            DataValue[] columnsVal = new DataValue[columnsName.length];

                            for (int i = 0; i < columnsVal.length; i++) {
                                String keyVal = null;
                                switch (i) {
                                    case 0:
                                        keyVal = stockTaskNO;
                                        break;
                                    case 1:
                                        keyVal = shopId;
                                        break;
                                    case 2:
                                        keyVal = shopId;
                                        break;
                                    case 3:
                                        keyVal = eId;
                                        break;
                                    case 4:
                                        if(par_Range.containsKey("groupNO"))
                                        {
                                            keyVal = par_Range.get("groupNO");  //   groupNO
                                            break;
                                        }else
                                        {
                                            loger.error("\r\n 缺少字段:group_no(groupNO) \r\n");
                                            this.pData.clear();//清理数据
                                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "缺少字段:group_no(groupNO)");
                                        }
                                    case 5:
                                        if(par_Range.containsKey("attrNO"))
                                        {
                                            keyVal = par_Range.get("attrNO");  //   attrNO
                                            break;
                                        }else
                                        {
                                            loger.error("\r\n 缺少字段:property_type(attrNO) \r\n");
                                            this.pData.clear();//清理数据
                                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "缺少字段:property_type(attrNO)");
                                        }
                                    case 6:
                                        if(par_Range.containsKey("valueNO"))
                                        {
                                            keyVal = par_Range.get("valueNO");  //   valueNO
                                            break;
                                        }else
                                        {
                                            loger.error("\r\n 缺少字段:property_value(valueNO) \r\n");
                                            this.pData.clear();//清理数据
                                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "缺少字段:property_value(valueNO)");
                                        }
                                    case 7:
                                        keyVal = warehouse;  //   WAREHOUSE
                                        break;
                                    case 8:
                                        keyVal = bDate;
                                        break;
                                    default:
                                        break;
                                }

                                if (keyVal != null) {
                                    insColCt++;
                                    columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
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
                                    if (insColCt >= insValue2.length)
                                        break;
                                }
                            }

                            InsBean ib2 = new InsBean("DCP_stocktask_range", columns2);
                            ib2.addValues(insValue2);
                            this.addProcessData(new DataProcessBean(ib2));
                        }

                        // 新增單身 (多筆)
                        List<Map<String, String>> jsonDatas_List = req.getStocktasklist();
                        if (jsonDatas_List != null )
                        {
                            for (Map<String, String> par_List : jsonDatas_List)
                            {
                                int insColCt = 0;
                                String[] columnsName = { "STOCKTASKNO", "SHOPID", "ORGANIZATIONNO",
                                        "EID", "PLUNO","PRICE","WAREHOUSE","BDATE"};

                                DataValue[] columnsVal = new DataValue[columnsName.length];

                                for (int i = 0; i < columnsVal.length; i++)
                                {
                                    String keyVal = null;
                                    switch (i)
                                    {
                                        case 0:
                                            keyVal = stockTaskNO;
                                            break;
                                        case 1:
                                            keyVal = shopId;
                                            break;
                                        case 2:
                                            keyVal = shopId;
                                            break;
                                        case 3:
                                            keyVal = eId;
                                            break;
                                        case 4:
                                            if(par_List.containsKey("pluNO"))
                                            {
                                                keyVal = par_List.get("pluNO");  //   pluNO
                                                break;
                                            }else
                                            {
                                                loger.error("\r\n 缺少字段:item_no(pluNO) \r\n");
                                                this.pData.clear();//清理数据
                                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "缺少字段:item_no(pluNO)");

                                            }
                                        case 5:
                                            if(par_List.containsKey("price"))
                                            {
                                                keyVal = par_List.get("price");  //   price
                                                if(par_List.get("price")==null || par_List.get("price").toString().isEmpty())
                                                {
                                                    keyVal = "0";
                                                }
                                                break;
                                            }else
                                            {
                                                loger.error("\r\n 缺少字段:price(price) \r\n");
                                                this.pData.clear();//清理数据
                                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "缺少字段:price(price)");
                                            }
                                        case 6:
                                            keyVal = warehouse;
                                            break;
                                        case 7:
                                            keyVal = bDate;
                                            break;
                                        default:
                                            break;
                                    }

                                    if (keyVal != null)
                                    {
                                        insColCt++;
                                        if (i == 5)
                                        {
                                            columnsVal[i] = new DataValue(keyVal, Types.DECIMAL);
                                        }
                                        else
                                        {
                                            columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
                                        }
                                    }
                                    else
                                    {
                                        columnsVal[i] = null;
                                    }
                                }
                                String[] columns2 = new String[insColCt];
                                DataValue[] insValue2 = new DataValue[insColCt];
                                // 依照傳入參數組譯要insert的欄位與數值；
                                insColCt = 0;

                                for (int i = 0; i < columnsVal.length; i++)
                                {
                                    if (columnsVal[i] != null)
                                    {
                                        columns2[insColCt] = columnsName[i];
                                        insValue2[insColCt] = columnsVal[i];
                                        insColCt++;
                                        if (insColCt >= insValue2.length)
                                            break;
                                    }
                                }

                                InsBean ib2 = new InsBean("DCP_stocktask_list", columns2);
                                ib2.addValues(insValue2);
                                this.addProcessData(new DataProcessBean(ib2));
                            }
                        }
                    }

                    insValue1 = new DataValue[] { new DataValue(stockTaskNO, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(bDate, Types.VARCHAR),
                            new DataValue(remark, Types.VARCHAR),
                            new DataValue(docType, Types.VARCHAR),
                            new DataValue(status, Types.VARCHAR),
                            new DataValue(createBy, Types.VARCHAR),
                            new DataValue(createDate, Types.VARCHAR),
                            new DataValue(createTime, Types.VARCHAR),
                            new DataValue(loadDocType, Types.VARCHAR),
                            new DataValue(loadDocNO, Types.VARCHAR),
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(btake, Types.VARCHAR),
                            new DataValue(warehouse, Types.VARCHAR),
                            new DataValue(taskWay, Types.VARCHAR),
                            new DataValue(notgoodsMode, Types.VARCHAR),
                            new DataValue("1", Types.VARCHAR),
                            new DataValue(isAdjustStock, Types.VARCHAR),
                    };

                    InsBean ib1 = new InsBean("DCP_stocktask", columns1);
                    ib1.addValues(insValue1);
                    this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

                    this.doExecuteDataToDB();
                }
            }

            res.setDatas(new ArrayList<DCP_StockTaskCreateRes.level2Elm>());
            DCP_StockTaskCreateRes.level2Elm oneLv2 = res.new level2Elm();
            oneLv2.setStockTaskNO(newStockTaskNO);
            res.getDatas().add(oneLv2);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            PrintWriter pw=new PrintWriter(errors);
            e.printStackTrace(pw);

            pw.flush();
            pw.close();

            errors.flush();
            errors.close();

            loger.error("\r\n*********盘点计划单失败,"+ errors +"*************\r\n");
            pw=null;
            errors=null;

            //插入失败
            res.setDatas(new ArrayList<DCP_StockTaskCreateRes.level2Elm>());
            DCP_StockTaskCreateRes.level2Elm oneLv2 = res.new level2Elm();
            oneLv2.setStockTaskNO(null);
            res.getDatas().add(oneLv2);
            res.setSuccess(false);
            res.setServiceStatus("000");
            res.setServiceDescription(e.getMessage());
        }
    }


    @Override
    protected List<InsBean> prepareInsertData(DCP_StockTaskCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockTaskCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockTaskCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected String getQuerySql(DCP_StockTaskCreateReq req) throws Exception {
        return null;
    }

    protected String getQuerySql_GetNO() throws Exception {
        StringBuffer sqlbuf = new StringBuffer("");
        Date dt = new Date();
        SimpleDateFormat matter = new SimpleDateFormat("yyyyMMdd");

        String stockTaskNO = "";

        stockTaskNO = "PDRW" + matter.format(dt);
        
        sqlbuf.append("" + "select stockTaskNO  from ( " + "select max(stockTaskNO) as  stockTaskNO "
                + "  from DCP_StockTask " + " where OrganizationNO = ? " + " and EID = ? " + " and SHOPID = ? "
                + " and stockTaskNO like '%%" + stockTaskNO + "%%' "); // 假資料
        sqlbuf.append(" ) TBL ");

 

        return sqlbuf.toString();
    }
    
    private String getStockTaskNO(DCP_StockTaskCreateReq req,String shopId) throws Exception {

        /*
         * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
         * 注意固定编码：YHSQ
         */
        String sql = null;
        String stockTaskNO = null;

        String organizationNO = shopId;
        String eId = req.geteId();

        String[] conditionValues = { organizationNO, eId, shopId }; // 查询要货单号

        sql = this.getQuerySql_GetNO();
        List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);

        if (getQData != null && !getQData.isEmpty()) {

            stockTaskNO = (String) getQData.get(0).get("STOCKTASKNO");

            if (stockTaskNO != null && stockTaskNO.length() > 0) {
                long i;
                stockTaskNO = stockTaskNO.substring(4, stockTaskNO.length());
                i = Long.parseLong(stockTaskNO) + 1;
                stockTaskNO = i + "";
                stockTaskNO = "PDRW" + stockTaskNO;

            } else {
                Date dt = new Date();
                SimpleDateFormat matter = new SimpleDateFormat("yyyyMMdd");
                stockTaskNO = "PDRW" + matter.format(dt) + "00001";
            }
        } else {
            Date dt = new Date();
            SimpleDateFormat matter = new SimpleDateFormat("yyyyMMdd");
            stockTaskNO = "SHTZ" + matter.format(dt) + "00001";
        }
        return stockTaskNO;
    }

}
