package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataGenerateReq;
import com.dsc.spos.json.cust.req.DCP_StockInCreateReq;
import com.dsc.spos.json.cust.req.DCP_StockInRefundCreateReq;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataGenerateRes;
import com.dsc.spos.json.cust.res.DCP_StockInRefundCreateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

public class DCP_StockInRefundCreate extends SPosAdvanceService<DCP_StockInRefundCreateReq, DCP_StockInRefundCreateRes>
{


    @Override
    protected void processDUID(DCP_StockInRefundCreateReq req, DCP_StockInRefundCreateRes res) throws Exception
    {
        String stockInNO;

        try {
            if (!checkGuid(req)) {
                DCP_StockInRefundCreateReq.levelElm request = req.getRequest();

                String shopId = req.getShopId();
                String organizationNO = req.getOrganizationNO();
                String eId = req.geteId();
                String bDate = request.getBDate();
                String memo = request.getMemo();
                String status = request.getStatus();
                String docType = request.getDocType();
                String oType = request.getOType();
                String ofNO = request.getOfNo();
                String pTemplateNO = request.getPTemplateNo();
                String createBy = req.getOpNO();
                String confirmBy = req.getOpNO();
                String accountBy = req.getOpNO();
                String submitBy  = req.getOpNO();
                Calendar cal = Calendar.getInstance();// 获得当前时间
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                String createDate = df.format(cal.getTime());
                String confirmDate = df.format(cal.getTime());
                String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
                String completeDate = df.format(cal.getTime());
                String submitDate  = df.format(cal.getTime());
                df = new SimpleDateFormat("HHmmss");
                String createTime = df.format(cal.getTime());
                String confirmTime = df.format(cal.getTime());
                String accountTime = df.format(cal.getTime());
                String submitTime = df.format(cal.getTime());
                String loadDocType = request.getLoadDocType();
                String loadDocNO = request.getLoadDocNo();
                String stockInID = request.getStockInID();
                String receiptDate = request.getReceiptDate();
                stockInNO = getStockInNoNew(req);
                String transferShop = request.getTransferShop();
                String bsNo = request.getBsNo();
                String warehouse = request.getWarehouse();
                String totPqty = request.getTotPqty();
                String totAmt = request.getTotAmt();
                String totDistriAmt=request.getTotDistriAmt();
                String totCqty = request.getTotCqty();
                String packingNo = request.getPackingNo();
                if (Check.Null(packingNo)) packingNo="";

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


                //获取启用在途参数 Enable_InTransit
                String Enable_InTransit = PosPub.getPARA_SMS(dao, eId, "", "Enable_InTransit");
                if (Check.Null(Enable_InTransit))
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"获取在途仓参数:Enable_InTransit失败");

                String sql;
                boolean isExist_B1;

                if (docType.equals("0")||docType.equals("1")||docType.equals("2"))   //增加调拨入库的判断docType==1 BY JZMA 20190107
                {
                    sql = this.getQuerySql_checkStockIn();
                    String[] conditionValues_checkStockIn = { shopId, eId, ofNO }; // 查詢條件
                    List<Map<String, Object>> getQData_checkStockIn = this.doQueryData(sql, conditionValues_checkStockIn);
                    if (getQData_checkStockIn == null || getQData_checkStockIn.isEmpty())
                    { // 有資料，取得詳細內容
                        isExist_B1 = true;
                    } else {
                        isExist_B1= false;
                        if (docType.equals("0")) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "收货通知单已转入库单或ERP来源单号重复,请重新查询！" );
                        }
                    }
                } else {
                    isExist_B1 = true;
                }

                if (isExist_B1)
                {
                    //查收货通知单并进行资料检核  BY JZMA 20191216
                    if ( docType.equals("0") && !Check.Null(ofNO))
                    {
                        String recevinhead="select * from DCP_RECEIVING where EID='"+eId+"' and SHOPID='"+shopId+"' and RECEIVINGNO='"+ofNO+"' ";
                        List<Map<String, Object>> listhead=this.doQueryData(recevinhead, null);
                        if(listhead != null && !listhead.isEmpty())
                        {
                            if (listhead.get(0).get("STATUS").toString().equals("8"))
                            {
                                //by jinzma 2019/6/12 通知单被ERP撤销，入库单保存时增加通知单状态的管控 status<>'8'
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "收货通知单:" +ofNO + " 被ERP撤销,请重新查询！" );
                            }

                            if (listhead.get(0).get("STATUS").toString().equals("7"))
                            {
                                //by jinzma 2019/6/12 通知单被ERP撤销，入库单保存时增加通知单状态的管控 status<>'8'
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "收货通知单:" +ofNO + " 已转入库单,请重新查询！" );
                            }

                            if (!loadDocNO.equals(listhead.get(0).get("LOAD_DOCNO").toString()))
                            {
                                //by jinzma 2019/12/16 检查通知单来源单号和前端送入的来源单号是否一致，避免单身资料出现异常
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "收货通知单来源单号:" +loadDocNO + " 检核异常,请重新查询！" );
                            }

                            //查一下收货通知单取得RECEIPTDATE字段，存到DCP_stockin里面
                            //String RECEIPTDATE="";
                            if(Check.Null(receiptDate))
                            {
                                receiptDate=listhead.get(0).get("RECEIPTDATE").toString();
                            }
                        }
                        else
                        {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "收货通知单:" +ofNO + " 不存在,请重新查询！" );
                        }
                    }


                    String[] columns1 = { "SHOPID", "OrganizationNO", "EID", "stockInNO", "BDate", "MEMO", "Status",
                            "DOC_TYPE", "oType", "ofNO","pTemplateNO", "CreateBy", "Create_Date", "Create_time", "ConfirmBy",
                            "Confirm_Date", "Confirm_time", "accountBy", "account_Date", "account_time", "TOT_PQTY",
                            "TOT_AMT", "TOT_CQTY", "LOAD_DOCTYPE", "LOAD_DOCNO", "transfer_shop", "stockIn_ID", "bsNO", "warehouse", "RECEIPTDATE",
                            "SUBMITBY","SUBMIT_DATE","SUBMIT_TIME","TOT_DISTRIAMT","PACKINGNO","CREATE_CHATUSERID","ACCOUNT_CHATUSERID",
                            "UPDATE_TIME","TRAN_TIME","STOCKINNO_ORIGIN","EMPLOYEEID","DEPARTID","CORP","DELIVERYCORP"
                    };
                    DataValue[] insValue1;

                    // 新增單身 (多筆)
                    List<DCP_StockInRefundCreateReq.level1Elm> jsonDatas = request.getDatas();
                    for (DCP_StockInRefundCreateReq.level1Elm par : jsonDatas)
                    {
                        int insColCt = 0;
                        String[] columnsName = { "stockInNO", "SHOPID", "item", "oItem", "pluNO",
                                "receiving_qty", "punit", "pqty", "BASEUNIT", "BASEQTY", "unit_Ratio",
                                "PLU_BARCODE", "price", "amt", "EID", "organizationNO",
                                "oType", "ofNO", "ooType", "oofNO", "ooItem", "WAREHOUSE","poqty",
                                "PLU_MEMO","BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","BDATE",
                                "FEATURENO","PACKINGNO","ITEM_ORIGIN","PQTY_ORIGIN","MES_LOCATION","EXPDATE"
                        };

                        DataValue[] columnsVal = new DataValue[columnsName.length];

                        for (int i = 0; i < columnsVal.length; i++)
                        {
                            String keyVal = null;
                            switch (i) {
                                case 0:
                                    keyVal = stockInNO;
                                    break;
                                case 1:
                                    keyVal = shopId;
                                    break;
                                case 2:
                                    keyVal = par.getItem(); // item
                                    break;
                                case 3:
                                    keyVal = par.getOItem(); // oItem
                                    break;
                                case 4:
                                    keyVal = par.getPluNo(); // pluNO
                                    break;
                                case 5:
                                    keyVal = par.getReceivingQty(); // receiving_qty
                                    break;
                                case 6:
                                    keyVal = par.getPunit(); // punit
                                    break;
                                case 7:
                                    keyVal = par.getPqty(); // pqty
                                    break;
                                case 8:
                                    keyVal = par.getBaseUnit(); // wunit
                                    break;
                                case 9:
                                    keyVal = par.getBaseQty(); // wqty
                                    break;
                                case 10:
                                    keyVal = par.getUnitRatio(); // unitRatio
                                    break;
                                case 11:
                                    keyVal = par.getPluBarcode(); // pluBarcode
                                    break;
                                case 12:
                                    keyVal = par.getPrice(); // price
                                    if(par.getPrice()==null || par.getPrice().isEmpty()){
                                        keyVal = "0";
                                    }
                                    break;
                                case 13:
                                    keyVal = par.getAmt(); // amt
                                    break;
                                case 14:
                                    keyVal = eId;
                                    break;
                                case 15:
                                    keyVal = organizationNO;
                                    break;
                                case 16:
                                    keyVal = par.getOType();
                                    break;
                                case 17:
                                    keyVal = par.getOfNo();
                                    break;
                                case 18:
                                    keyVal = par.getOoType();
                                    break;
                                case 19:
                                    keyVal = par.getOofNo();
                                    break;
                                case 20:
                                    keyVal = par.getOoItem();
                                    break;
                                case 21:
                                    keyVal = par.getWarehouse();
                                    break;
                                case 22:
                                    keyVal = par.getPoQty();
                                    break;
                                case 23:
                                    if(par.getPluMemo()==null){
                                        keyVal = "";
                                    }
                                    else{
                                        keyVal = par.getPluMemo();
                                    }
                                    break;
                                case 24:
                                    keyVal = par.getBatchNo();
                                    break;
                                case 25:
                                    keyVal = par.getProdDate();
                                    break;
                                case 26:
                                    if(par.getDistriPrice()==null || Check.Null(par.getDistriPrice()) ){
                                        keyVal = "0";
                                    }
                                    else {
                                        keyVal=par.getDistriPrice();
                                    }
                                    break;
                                case 27:
                                    keyVal = par.getDistriAmt(); //
                                    if (Check.Null(keyVal)) keyVal="0";
                                    break;
                                case 28:
                                    keyVal = bDate;
                                    break;
                                case 29:
                                    keyVal = par.getFeatureNo();
                                    if (Check.Null(keyVal))
                                        keyVal=" ";
                                    break;
                                case 30:
                                    keyVal = par.getPackingNo();
                                    break;
                                case 31:
                                    keyVal = par.getItem_origin();
                                    break;
                                case 32:
                                    keyVal = par.getPqty_origin();
                                    break;

                                case 33:
                                    keyVal = par.getLocation();
                                    if(Check.Null(keyVal)){
                                        keyVal=" ";
                                    }
                                    break;

                                case 34:
                                    keyVal = par.getExpDate();
                                    break;
                                default:
                                    break;
                            }

                            if (keyVal != null) {
                                insColCt++;
                                if (i == 2 || i == 3) {
                                    columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
                                } else if (i == 7 || i == 9 || i == 10 || i == 12 || i == 13 || i == 26 ) {
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
                                if (insColCt >= insValue2.length)
                                    break;
                            }
                        }

                        InsBean ib2 = new InsBean("DCP_STOCKIN_DETAIL", columns2);
                        ib2.addValues(insValue2);
                        this.addProcessData(new DataProcessBean(ib2));

                        //更新原单的明细已红冲数量
                        UptBean ub_DCP_STOCKIN_DETAIL = new UptBean("DCP_STOCKIN_DETAIL");
                        ub_DCP_STOCKIN_DETAIL.addUpdateValue("PQTY_REFUND", new DataValue(par.getPqty(), Types.VARCHAR));
                        // condition
                        ub_DCP_STOCKIN_DETAIL.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub_DCP_STOCKIN_DETAIL.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ub_DCP_STOCKIN_DETAIL.addCondition("STOCKINNO", new DataValue(req.getRequest().getStockInNo_origin(), Types.VARCHAR));
                        ub_DCP_STOCKIN_DETAIL.addCondition("ITEM", new DataValue(par.getItem_origin(), Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub_DCP_STOCKIN_DETAIL));


                        // 更新关联通知单单身 by jinzma 20210422 【ID1017035】【3.0货郎】按商品行收货(DCP_StockInCreate（收货入库单新建))
                        if (docType.equals("0") && !Check.Null(ofNO)) {

                            UptBean ub4 = new UptBean("DCP_RECEIVING_DETAIL");
                            // add value
                            ub4.addUpdateValue("STOCKIN_QTY", new DataValue(par.getPqty(), Types.VARCHAR));
                            ub4.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
                            // condition
                            ub4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub4.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                            ub4.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                            ub4.addCondition("RECEIVINGNO", new DataValue(ofNO, Types.VARCHAR));
                            ub4.addCondition("ITEM", new DataValue(par.getOItem(), Types.VARCHAR));
                            ub4.addCondition("PLUNO", new DataValue(par.getPluNo(), Types.VARCHAR));

                            this.addProcessData(new DataProcessBean(ub4));
                        }
                    }

                    String[] conditionValue_TransferShop = { eId, shopId, organizationNO,ofNO };
                    sql = this.getQuerySql_TransferShop();
                    List<Map<String, Object>> getQData_TransferShop = this.doQueryData(sql, conditionValue_TransferShop);
                    if (getQData_TransferShop != null && !getQData_TransferShop.isEmpty()) {
                        transferShop = (String) getQData_TransferShop.get(0).get("TRANSFERSHOP");
                    }
                    if (docType.equals("1") && (transferShop.length() == 0) ){
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "发货门店不可为空");
                    }
                    if(docType.equals("0")){
                        transferShop=request.getTransferShop();
                    }

                    if(docType.equals("3")) //其它入库单,把来源单号赋值成单号,目的用ofno做唯一索引,避免配送收货,调拨收货,自动收货操作单据重复
                    {
                        if(Check.Null(ofNO) || ofNO.isEmpty() )
                        {
                            ofNO= stockInNO;
                        }
                    }
                    insValue1 = new DataValue[]
                            {
                                    new DataValue(shopId, Types.VARCHAR),
                                    new DataValue(organizationNO, Types.VARCHAR),
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(stockInNO, Types.VARCHAR),
                                    new DataValue(bDate, Types.VARCHAR),
                                    new DataValue(memo, Types.VARCHAR),
                                    new DataValue(status, Types.VARCHAR),
                                    new DataValue(docType, Types.VARCHAR),
                                    new DataValue(oType, Types.VARCHAR),
                                    new DataValue(ofNO, Types.VARCHAR),
                                    new DataValue(pTemplateNO, Types.VARCHAR),
                                    new DataValue(createBy, Types.VARCHAR),
                                    new DataValue(createDate, Types.VARCHAR),
                                    new DataValue(createTime, Types.VARCHAR),
                                    new DataValue(confirmBy, Types.VARCHAR),
                                    new DataValue(confirmDate, Types.VARCHAR),
                                    new DataValue(confirmTime, Types.VARCHAR),
                                    new DataValue(accountBy, Types.VARCHAR),
                                    new DataValue(accountDate, Types.VARCHAR),
                                    new DataValue(accountTime, Types.VARCHAR),
                                    new DataValue(totPqty, Types.VARCHAR),
                                    new DataValue(totAmt, Types.VARCHAR),
                                    new DataValue(totCqty, Types.VARCHAR),
                                    new DataValue(loadDocType, Types.VARCHAR),
                                    new DataValue(loadDocNO, Types.VARCHAR),
                                    new DataValue(transferShop, Types.VARCHAR),
                                    new DataValue(stockInID, Types.VARCHAR),
                                    new DataValue(bsNo, Types.VARCHAR),
                                    new DataValue(warehouse, Types.VARCHAR),
                                    new DataValue(receiptDate, Types.VARCHAR),
                                    new DataValue(submitBy, Types.VARCHAR),
                                    new DataValue(submitDate, Types.VARCHAR),
                                    new DataValue(submitTime, Types.VARCHAR),
                                    new DataValue(totDistriAmt, Types.VARCHAR),
                                    new DataValue(packingNo, Types.VARCHAR),
                                    new DataValue(req.getChatUserId(), Types.VARCHAR),
                                    new DataValue(req.getChatUserId(), Types.VARCHAR),
                                    new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                                    new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                                    new DataValue(req.getRequest().getStockInNo_origin(), Types.VARCHAR),
                                    new DataValue(req.getRequest().getEmployeeId(), Types.VARCHAR),
                                    new DataValue(req.getRequest().getDepartId(), Types.VARCHAR),
                                    new DataValue(corp, Types.VARCHAR),
                                    new DataValue(deliveryCorp, Types.VARCHAR),

                            };

                    InsBean ib1 = new InsBean("DCP_STOCKIN", columns1);
                    ib1.addValues(insValue1);
                    this.addProcessData(new DataProcessBean(ib1)); // 新增單頭


                    //更新原单的退单号
                    UptBean ub_DCP_STOCKIN = new UptBean("DCP_STOCKIN");
                    ub_DCP_STOCKIN.addUpdateValue("STOCKINNO_REFUND", new DataValue(stockInNO, Types.VARCHAR));
                    // condition
                    ub_DCP_STOCKIN.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub_DCP_STOCKIN.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub_DCP_STOCKIN.addCondition("STOCKINNO", new DataValue(req.getRequest().getStockInNo_origin(), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub_DCP_STOCKIN));


                    //加入库存流水账
                    if(status.equals("2"))
                    {
                        List<DCP_StockInRefundCreateReq.level1Elm> datas = request.getDatas();
                        if (datas != null && !datas.isEmpty())
                        {

                            String inv_cost_warehouse = "";	 //调出门店的在途仓
                            String sqlWarehouse="select inv_cost_warehouse from DCP_ORG "
                                    + "where organizationNO='"+transferShop+"' AND ORG_FORM='2' "
                                    + "and EID='"+req.geteId()+"' ";
                            List<Map<String, Object>> getQDataWarehouse = this.doQueryData(sqlWarehouse, null);
                            if (getQDataWarehouse != null && !getQDataWarehouse.isEmpty())
                            {
                                inv_cost_warehouse = getQDataWarehouse.get(0).get("INV_COST_WAREHOUSE").toString();
                            }


                            // 新增调拨扣库存方式	  1.调出门店发货后扣库存       2.调入门店收货后扣库存
                            //此处应该取调拨出的门店参数  by jinzma 20230105
                            //String Transfer_Stock= PosPub.getPARA_SMS(dao, eId, shopId, "Transfer_Stock");
                            String Transfer_Stock= PosPub.getPARA_SMS(dao, eId, transferShop, "Transfer_Stock");

                            //获取调出门店的仓库
                            sql = " select WAREHOUSE from DCP_stockout "
                                    + " where EID='"+eId+"' and SHOPID='"+transferShop+"' and STOCKOUTNO ='"+loadDocNO+"' ";
                            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                            String outWareHouse = getQData.get(0).get("WAREHOUSE").toString();

                            for (DCP_StockInRefundCreateReq.level1Elm oneData : datas)
                            {
                                //判断仓库不能为空或空格  BY JZMA 20191118
                                String stockWarehouse=oneData.getWarehouse();
                                if (Check.Null(stockWarehouse)||Check.Null(stockWarehouse.trim()))
                                {
                                    this.pData.clear();
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "仓库不能为空或空格");
                                }
                                //单据类型处理
                                String stockDocType="";
                                switch (docType) {
                                    case "0":
                                        stockDocType = "01";
                                        break;
                                    case "1":
                                        stockDocType = "02";
                                        break;
                                    case "4":
                                        stockDocType = "19";
                                        break;
                                }

                                //增加在库数
                                String featureNo=oneData.getFeatureNo();  //特征码为空给空格
                                if (Check.Null(featureNo))
                                    featureNo = " ";
                                String procedure="SP_DCP_StockChange";
                                Map<Integer,Object> inputParameter = new HashMap<>();
                                inputParameter.put(1,eId );                        //--企业ID
                                inputParameter.put(2,shopId );                     //--组织
                                inputParameter.put(3,stockDocType);                //--单据类型
                                inputParameter.put(4,stockInNO);	                 //--单据号
                                inputParameter.put(5,oneData.getItem());           //--单据行号
                                inputParameter.put(6,"1");                         //--异动方向 1=加库存 -1=减库存
                                inputParameter.put(7,bDate );                      //--营业日期 yyyy-MM-dd
                                inputParameter.put(8,oneData.getPluNo());          //--品号
                                inputParameter.put(9,featureNo);                   //--特征码
                                inputParameter.put(10,stockWarehouse );            //--仓库
                                inputParameter.put(11,oneData.getBatchNo());       //--批号
                                inputParameter.put(12,oneData.getPunit());         //--交易单位
                                inputParameter.put(13,oneData.getPqty() );         //--交易数量
                                inputParameter.put(14,oneData.getBaseUnit());      //--基准单位
                                inputParameter.put(15,oneData.getBaseQty() );      //--基准数量
                                inputParameter.put(16,oneData.getUnitRatio() );    //--换算比例
                                inputParameter.put(17,oneData.getPrice() );        //--零售价
                                inputParameter.put(18,oneData.getAmt() );          //--零售金额
                                inputParameter.put(19,oneData.getDistriPrice() );  //--进货价
                                inputParameter.put(20,oneData.getDistriAmt() );    //--进货金额
                                inputParameter.put(21,accountDate );               //--入账日期 yyyy-MM-dd
                                inputParameter.put(22,oneData.getProdDate() );     //--批号的生产日期 yyyy-MM-dd
                                inputParameter.put(23,bDate);                      //--单据日期
                                inputParameter.put(24,"");                         //--异动原因
                                inputParameter.put(25,"");                         //--异动描述
                                inputParameter.put(26,req.getOpNO());              //--操作员

                                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                this.addProcessData(new DataProcessBean(pdb));

                                //(调拨入||移仓入) && 启用在途
                                if((docType.equals("1") || docType.equals("4")) && Enable_InTransit.equals("Y"))
                                {
                                    if (Check.Null(inv_cost_warehouse))  //调出门店的在途仓未设置
                                    {
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调出门店：" + transferShop +" 在途仓未设置 ");
                                    }

                                    //减少在库数
                                    Map<Integer,Object> inputParameter1 = new HashMap<>();
                                    inputParameter1.put(1,eId );                        //--企业ID
                                    inputParameter1.put(2,transferShop );               //--组织
                                    inputParameter1.put(3,"13");                        //--单据类型
                                    inputParameter1.put(4,loadDocNO);	                 //--单据号
                                    inputParameter1.put(5,oneData.getItem());           //--单据行号
                                    inputParameter1.put(6,"-1");                         //--异动方向 1=加库存 -1=减库存
                                    inputParameter1.put(7,bDate );                      //--营业日期 yyyy-MM-dd
                                    inputParameter1.put(8,oneData.getPluNo());          //--品号
                                    inputParameter1.put(9,featureNo);                   //--特征码
                                    inputParameter1.put(10,inv_cost_warehouse );        //--仓库
                                    inputParameter1.put(11,oneData.getBatchNo());       //--批号
                                    inputParameter1.put(12,oneData.getPunit());         //--交易单位
                                    inputParameter1.put(13,oneData.getPqty() );         //--交易数量
                                    inputParameter1.put(14,oneData.getBaseUnit());      //--基准单位
                                    inputParameter1.put(15,oneData.getBaseQty() );      //--基准数量
                                    inputParameter1.put(16,oneData.getUnitRatio() );    //--换算比例
                                    inputParameter1.put(17,oneData.getPrice() );        //--零售价
                                    inputParameter1.put(18,oneData.getAmt() );          //--零售金额
                                    inputParameter1.put(19,oneData.getDistriPrice() );  //--进货价
                                    inputParameter1.put(20,oneData.getDistriAmt());     //--进货金额
                                    inputParameter1.put(21,accountDate );               //--入账日期 yyyy-MM-dd
                                    inputParameter1.put(22,oneData.getProdDate());      //--批号的生产日期 yyyy-MM-dd
                                    inputParameter1.put(23,bDate);                      //--单据日期
                                    inputParameter1.put(24,"");                         //--异动原因
                                    inputParameter1.put(25,"");                         //--异动描述
                                    inputParameter1.put(26,req.getOpNO());              //--操作员

                                    ProcedureBean pdb1 = new ProcedureBean(procedure, inputParameter1);
                                    this.addProcessData(new DataProcessBean(pdb1));

                                }
                                else
                                {
                                    //单据类型为调拨且未启用在途
                                    if (docType.equals("1") && Enable_InTransit.equals("N"))
                                    {
                                        // 新增调拨扣库存方式	  1.调出门店发货后扣库存       2.调入门店收货后扣库存
                                        if (!Check.Null(Transfer_Stock) && Transfer_Stock.equals("2"))
                                        {
                                            //减少调出门店的在库数

                                            Map<Integer,Object> inputParameter1 = new HashMap<>();

                                            inputParameter1.put(1,eId);                         //--企业ID
                                            inputParameter1.put(2,transferShop);                //--组织
                                            inputParameter1.put(3,"04");                        //--单据类型
                                            inputParameter1.put(4,loadDocNO);	                 //--单据号
                                            inputParameter1.put(5,oneData.getItem());           //--单据行号
                                            inputParameter1.put(6,"-1");                        //--异动方向 1=加库存 -1=减库存
                                            inputParameter1.put(7,bDate);                       //--营业日期 yyyy-MM-dd
                                            inputParameter1.put(8,oneData.getPluNo());          //--品号
                                            inputParameter1.put(9,featureNo);                   //--特征码
                                            inputParameter1.put(10,outWareHouse);               //--仓库
                                            inputParameter1.put(11,oneData.getBatchNo());       //--批号
                                            inputParameter1.put(12,oneData.getPunit());         //--交易单位
                                            inputParameter1.put(13,oneData.getPqty());          //--交易数量
                                            inputParameter1.put(14,oneData.getBaseUnit());      //--基准单位
                                            inputParameter1.put(15,oneData.getBaseQty());       //--基准数量
                                            inputParameter1.put(16,oneData.getUnitRatio());     //--换算比例
                                            inputParameter1.put(17,oneData.getPrice());         //--零售价
                                            inputParameter1.put(18,oneData.getAmt());           //--零售金额
                                            inputParameter1.put(19,oneData.getDistriPrice());   //--进货价
                                            inputParameter1.put(20,oneData.getDistriAmt());     //--进货金额
                                            inputParameter1.put(21,accountDate);                //--入账日期 yyyy-MM-dd
                                            inputParameter1.put(22,oneData.getProdDate());      //--批号的生产日期 yyyy-MM-dd
                                            inputParameter1.put(23,bDate);                      //--单据日期
                                            inputParameter1.put(24,"");                         //--异动原因
                                            inputParameter1.put(25,"");                         //--异动描述
                                            inputParameter1.put(26,req.getOpNO());              //--操作员

                                            ProcedureBean pdb1 = new ProcedureBean(procedure, inputParameter1);
                                            this.addProcessData(new DataProcessBean(pdb1));

                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                // 更新关联要货单
                if (status.equals("2"))
                {
                    if (docType.equals("0"))
                    {
                        sql = this.getQuerySql_checkPOrder1();
                        String[] conditionValues_checkPOrder1 = { organizationNO, eId, shopId, organizationNO, eId,shopId, ofNO }; // 查詢條件

                        List<Map<String, Object>> getQData_checkPOrder1 = this.doQueryData(sql, conditionValues_checkPOrder1);

                        if (getQData_checkPOrder1 != null && !getQData_checkPOrder1.isEmpty()) {
                            List<DCP_StockInRefundCreateReq.level1Elm> jsonDatas = request.getDatas();
                            for (DCP_StockInRefundCreateReq.level1Elm par : jsonDatas) {

                                sql = this.getQuerySql_updatePOrder1();
                                String[] conditionValues_updatePOrder1 = { organizationNO, eId, shopId, ofNO }; // 查詢條件
                                List<Map<String, Object>> getQData_updatePOrder1 = this.doQueryData(sql,
                                                                                                    conditionValues_updatePOrder1);

                                if (getQData_updatePOrder1 != null && !getQData_updatePOrder1.isEmpty()) { // 有資料，取得詳細內容
                                    for (Map<String, Object> oneData : getQData_updatePOrder1) {
                                        String porderNO1 = oneData.get("OFNO").toString();
                                        String pqty = par.getPqty();
                                        String detailStatus = "1";
                                        String oItem = par.getOItem();

                                        UptBean ub1 = new UptBean("DCP_POrder_DETAIL");
                                        // add value
                                        ub1.addUpdateValue("DETAIL_STATUS", new DataValue(detailStatus, Types.VARCHAR));
                                        ub1.addUpdateValue("STOCKIN_QTY", new DataValue(pqty, Types.DECIMAL));

                                        // condition
                                        ub1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                                        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                        ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                                        ub1.addCondition("porderNO", new DataValue(porderNO1, Types.VARCHAR));
                                        ub1.addCondition("Item", new DataValue(oItem, Types.VARCHAR));
                                        this.addProcessData(new DataProcessBean(ub1));
                                    }
                                }
                            }
                        }

                        sql = this.getQuerySql_updatePOrder1();
                        String[] conditionValues_updatePOrder1 = { organizationNO, eId, shopId, ofNO }; // 查詢條件
                        List<Map<String, Object>> getQData_updatePOrder1 = this.doQueryData(sql, conditionValues_updatePOrder1);

                        if (getQData_updatePOrder1 != null && !getQData_updatePOrder1.isEmpty()) { // 有資料，取得詳細內容
                            for (Map<String, Object> oneData : getQData_updatePOrder1) {
                                String porderNO1 = oneData.get("OFNO").toString();
                                sql = this.getQuerySql_updatePOrder2();
                                String[] conditionValues_updatePOrder2 = { organizationNO, eId, shopId, porderNO1 }; // 查詢條件
                                List<Map<String, Object>> getQData_updatePOrder2 = this.doQueryData(sql,
                                                                                                    conditionValues_updatePOrder2);

                                if (getQData_updatePOrder2 == null || getQData_updatePOrder2.isEmpty()) {
                                    String status2 = "3";

                                    UptBean ub2 = new UptBean("DCP_POrder");
                                    // add value
                                    ub2.addUpdateValue("Status", new DataValue(status2, Types.VARCHAR));
                                    ub2.addUpdateValue("Complete_Date", new DataValue(completeDate, Types.VARCHAR));

                                    // condition
                                    ub2.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                                    ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                    ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                                    ub2.addCondition("porderNO", new DataValue(porderNO1, Types.VARCHAR));
                                    this.addProcessData(new DataProcessBean(ub2));
                                }
                            }
                        }
                    }

                    if (docType.equals("1")||docType.equals("4")) {
                        UptBean ub3 = new UptBean("DCP_STOCKOUT");
                        // add value
                        ub3.addUpdateValue("Status", new DataValue("3", Types.VARCHAR));
                        ub3.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                        ub3.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));


                        // condition
                        ub3.addCondition("OrganizationNO", new DataValue(transferShop, Types.VARCHAR));
                        ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub3.addCondition("SHOPID", new DataValue(transferShop, Types.VARCHAR));
                        ub3.addCondition("stockoutno", new DataValue(loadDocNO, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub3));
                    }

                    if (docType.equals("0")||docType.equals("1")||docType.equals("4")){
                        // 更新关联通知单
                        UptBean ub3 = new UptBean("DCP_Receiving");
                        // add value
                        ub3.addUpdateValue("Status", new DataValue("7", Types.VARCHAR));
                        ub3.addUpdateValue("Complete_Date", new DataValue(completeDate, Types.VARCHAR));
                        // condition
                        ub3.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                        ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub3.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ub3.addCondition("ReceivingNO", new DataValue(ofNO, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub3));

                        // 更新关联通知单单身 by jinzma 20210422 【ID1017035】【3.0货郎】按商品行收货(DCP_StockInCreate（收货入库单新建))
                        // 以下代码没用，因为单身不允许删除，上面已经修改了status状态，这段再修改状态是多余的
//                        if (docType.equals("0") && !Check.Null(ofNO)) {
//                            UptBean ub4 = new UptBean("DCP_RECEIVING_DETAIL");
//                            // add value
//                            ub4.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
//                            // condition
//                            ub4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
//                            ub4.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
//                            ub4.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
//                            ub4.addCondition("RECEIVINGNO", new DataValue(ofNO, Types.VARCHAR));
//                            this.addProcessData(new DataProcessBean(ub4));
//                        }
                    }
                }

                if (docType.equals("1")&& loadDocType.equals("9") ){
                    //更新订单中心订单：
                    //确认完调拨单之后, 要回写订单的状态,根据 LOAD_DOCNO==订单单号 AND ORGANIZATIONNO==订单门店  AND 企业编号==订单企业编号
                    UptBean ub1 = new UptBean("DCP_ORDER");
                    String shipType;
                    String orderNO;
                    String orderLoadDocType;
                    //删除shippingshop的判断，因为可能先产生调拨单，后修改订单配送门店，导致异常  BY JZMA 20200117
                    String sqlorder = " select orderno,SHIPTYPE,a.LOAD_DOCTYPE as LOAD_DOCTYPE from dcp_order a inner join DCP_stockout b  "
                            + "on a.EID=b.EID and a.machshop = b.SHOPID and a.orderno=b.load_docno "
                            +	" where a.EID='"+eId+"' and b.stockoutno='"+loadDocNO+"' " 	;	 //and a.shippingshop='"+SHOPID+"'
                    List<Map<String, Object>> slorder = this.doQueryData(sqlorder, null);
                    if (slorder !=null && !slorder.isEmpty())
                    {
                        if(!Check.Null(slorder.get(0).get("ORDERNO").toString()))
                        {
                            orderNO=slorder.get(0).get("ORDERNO").toString();
                            shipType=slorder.get(0).get("SHIPTYPE").toString() ;
                            // 陶大爷说订单唯一判断要增加LOAD_DOCTYPE比较保险   BY JZMA 20200119
                            orderLoadDocType = slorder.get(0).get("LOAD_DOCTYPE").toString() ;

                            // 根据配送方式判断, 给status 赋值
                            if(shipType.equals("2")){ //商户配送
                                // add value
                                ub1.addUpdateValue("STATUS", new DataValue("9", Types.VARCHAR));//待配送
                                // add condition
                                ub1.addCondition("ORDERNO", new DataValue(orderNO, Types.VARCHAR));
                                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub1.addCondition("MACHSHOP", new DataValue(shopId, Types.VARCHAR));
                                ub1.addCondition("LOAD_DOCTYPE", new DataValue(orderLoadDocType, Types.VARCHAR));
                            }
                            if(shipType.equals("3")){ //顾客自提
                                // add value
                                ub1.addUpdateValue("Status", new DataValue("8", Types.VARCHAR));//待提货
                                // add condition
                                ub1.addCondition("ORDERNO", new DataValue(orderNO, Types.VARCHAR));
                                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub1.addCondition("MACHSHOP", new DataValue(shopId, Types.VARCHAR));
                                ub1.addCondition("LOAD_DOCTYPE", new DataValue(orderLoadDocType, Types.VARCHAR));
                            }
                            ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                            ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                            this.addProcessData(new DataProcessBean(ub1));
                        }
                    }
                    else
                    {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "订单："+loadDocNO+"不存在，无法回写状态，请确认 ");
                    }

                }
            }
            else
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据已存在，请重新确认！ ");
            }
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
                    for (DCP_StockInRefundCreateReq.level1Elm par : req.getRequest().getDatas()) {
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
                    for (DCP_StockInRefundCreateReq.level1Elm par : req.getRequest().getDatas()) {
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
                    for (DCP_StockInRefundCreateReq.level1Elm par : req.getRequest().getDatas()) {
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



            res.setDatas(new ArrayList<>());
            DCP_StockInRefundCreateRes.level1Elm oneLv1 = res.new level1Elm();
            oneLv1.setStockInNo(stockInNO);

            res.getDatas().add(oneLv1);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        }
        catch(Exception e)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockInRefundCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockInRefundCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockInRefundCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_StockInRefundCreateReq req) throws Exception
    {
        boolean isFail = false;

        StringBuffer errMsg = new StringBuffer();
        DCP_StockInRefundCreateReq.levelElm request = req.getRequest();
        List<DCP_StockInRefundCreateReq.level1Elm> jsonDatas = request.getDatas();

        if(jsonDatas==null || jsonDatas.size()==0) {
            errMsg.append("明细记录不可为空值, ");
            isFail = true;
        }

        //必传值不为空
        String bDate = request.getBDate();
        String status = request.getStatus();
        String docType = request.getDocType();
        String oType = request.getOType();
        String ofNO = request.getOfNo();
        String warehouse = request.getWarehouse();
        String totPqty = request.getTotPqty();
        String totAmt = request.getTotAmt();
        String totDistriAmt=request.getTotDistriAmt();
        String totCqty = request.getTotCqty();

        //必传值可以为空
        String memo = request.getMemo();
        String loadDocType = request.getLoadDocType();
        String loadDocNO = request.getLoadDocNo();

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


        if (Check.Null(warehouse)) {
            errMsg.append("仓库不可为空值, ");
            isFail = true;
        }

        if (memo == null) {
            errMsg.append("备注不可为空值, ");
            isFail = true;
        }

        if (loadDocType == null) {
            errMsg.append("转入来源单据类型不可为空值, ");
            isFail = true;
        }

        if (loadDocNO == null) {
            errMsg.append("转入来源单据单号不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getStockInNo_origin()))
        {
            errMsg.append("stockInNo_origin不可为空值, ");
            isFail = true;
        }
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for (DCP_StockInRefundCreateReq.level1Elm par : jsonDatas) {
            //必传值不为空
            String item = par.getItem();
            String oItem = par.getOItem();
            String pluNo = par.getPluNo();
            String punit = par.getPunit();
            String pqty = par.getPqty();
            String warehouseD = par.getWarehouse();
            String baseUnit =par.getBaseUnit();
            String baseQty = par.getBaseQty();
            String unitRatio = par.getUnitRatio();

            //必传值可以为空
            String price = par.getPrice();
            String amt = par.getAmt();

            if (Check.Null(item)) {
                errMsg.append("项次不可为空值, ");
                isFail = true;
            }

            if (Check.Null(oItem)) {
                errMsg.append("来源项次不可为空值, ");
                isFail = true;
            }

            if (Check.Null(pluNo)) {
                errMsg.append("商品编码不可为空值, ");
                isFail = true;
            }

            if (Check.Null(punit)) {
                errMsg.append("商品"+pluNo+"单位不可为空值, ");
                isFail = true;
            }

            if (Check.Null(pqty)) {
                errMsg.append("商品"+pluNo+"数量不可为空值, ");
                isFail = true;
            }

            if (Check.Null(warehouseD)) {
                errMsg.append("商品"+pluNo+"仓库不可为空值, ");
                isFail = true;
            }

            if (price == null) {
                errMsg.append("商品"+pluNo+"单价不可为空值, ");
                isFail = true;
            }

            if (amt == null) {
                errMsg.append("商品"+pluNo+"金额不可为空值, ");
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
            if (Check.Null(par.getItem_origin()))
            {
                errMsg.append("item_origin不可为空值, ");
                isFail = true;
            }
            if (Check.Null(par.getPqty_origin()))
            {
                errMsg.append("pqty_origin不可为空值, ");
                isFail = true;
            }

            if (isFail){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }

        }
        return false;
    }

    @Override
    protected TypeToken<DCP_StockInRefundCreateReq> getRequestType()
    {
        return new TypeToken<DCP_StockInRefundCreateReq>(){};
    }

    @Override
    protected DCP_StockInRefundCreateRes getResponseType()
    {
        return new DCP_StockInRefundCreateRes();
    }

    private String getQuerySql_TransferShop() {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(""
                              + " select transfer_shop as transferShop "
                              + " from DCP_receiving  "
                              + " where EID=?  and  SHOPID=?  and organizationno=? and receivingNo = ?"
        );

        return sqlbuf.toString();
    }

    private String getStockInNoNew(DCP_StockInRefundCreateReq req) throws Exception{
        DCP_StockInRefundCreateReq.levelElm request = req.getRequest();
        String stockInNO = "";
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String docType = request.getDocType();
        String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);


        if (docType.equals("1")) {
            stockInNO = this.getOrderNO(req, "DBSH");
        }

        if (docType.equals("0") || docType.equals("2") ) {
            stockInNO = this.getOrderNO(req, "PSSH");
        }

        if (docType.equals("3") ) {
            stockInNO = this.getOrderNO(req, "QTRK");
        }

        if (docType.equals("4") ) {
            stockInNO = this.getOrderNO(req, "YCSH");
        }
        return stockInNO;
    }
    private String getStockInNO(DCP_StockInRefundCreateReq req) throws Exception {
        /*
         * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
         * 注意固定编码：如果doctype=0 则固定编码PSSH  如果doctype=1 则固定码为DBSH
         */
        DCP_StockInRefundCreateReq.levelElm request = req.getRequest();
        String stockInNO = null;
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String docType = request.getDocType();
        String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        String[] conditionValues = { organizationNO, eId, shopId, docType }; // 查询要货单号

        StringBuffer sqlbuf = new StringBuffer();

        if (docType.equals("1")) {
            stockInNO = "DBSH" + bDate;//matter.format(dt);
        }

        if (docType.equals("0") || docType.equals("2") ) {
            stockInNO = "PSSH" + bDate;//matter.format(dt);
        }

        if (docType.equals("3") ) {
            stockInNO = "QTRK" + bDate;//matter.format(dt);
        }

        if (docType.equals("4") ) {
            stockInNO = "YCSH" + bDate;//matter.format(dt);
        }

        sqlbuf.append("" + "select stockInNO  from ( " + "select max(stockInNO) as  stockInNO "
                              + "  from DCP_STOCKIN " + " where OrganizationNO = ? " + " and EID = ? " + " and SHOPID = ? "
                              + " and stockInNO like '%%" + stockInNO + "%%' and doc_Type=?"
        ); // 假資料
        sqlbuf.append(" ) TBL ");
        String sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);

        if (getQData != null && !getQData.isEmpty()) {
            stockInNO = (String) getQData.get(0).get("STOCKINNO");
            if (stockInNO != null && stockInNO.length() > 0) {
                long i;
                stockInNO = stockInNO.substring(4);
                i = Long.parseLong(stockInNO) + 1;
                stockInNO = i + "";
                if (docType.equals("0")) {
                    stockInNO = "PSSH" + stockInNO;
                }
                if (docType.equals("1")) {
                    stockInNO = "DBSH" + stockInNO;
                }
                if (docType.equals("3")){
                    stockInNO = "QTRK" + stockInNO;
                }
                if (docType.equals("4")){
                    stockInNO = "YCSH" + stockInNO;
                }
            } else {
                if (docType.equals("0")) {
                    stockInNO = "PSSH" + bDate + "00001";
                }
                if (docType.equals("1")) {
                    stockInNO = "DBSH" + bDate + "00001";
                }
                if (docType.equals("3")){
                    stockInNO = "QTRK" + bDate + "00001";
                }
                if (docType.equals("4")){
                    stockInNO = "YCSH" + bDate + "00001";
                }
            }
        } else {
            if (docType.equals("0")) {
                stockInNO = "PSSH" + bDate + "00001";
            }
            if (docType.equals("1")) {
                stockInNO = "DBSH" + bDate + "00001";
            }
            if (docType.equals("3")){
                stockInNO = "QTRK" + bDate + "00001";
            }
            if (docType.equals("4")){
                stockInNO = "YCSH" + bDate + "00001";
            }
        }

        return stockInNO;
    }
    private String getQuerySql_checkStockIn() {
        StringBuffer sqlbuf = new StringBuffer();

        sqlbuf.append(""
                              + "SELECT * "
                              + " FROM DCP_STOCKIN "
                              + " WHERE  SHOPID=? and EID=? and  OFNO=?  "
        );

        return sqlbuf.toString();
    }
    private String getQuerySql_checkPOrder1() {

        StringBuffer sqlbuf = new StringBuffer();

        sqlbuf.append(""
                              + "SELECT * "
                              + " FROM DCP_POrder "
                              + " WHERE  OrganizationNO=? AND EID=? AND SHOPID=? "
                              + " AND  POrderNO in ("
                              + " select DISTINCT OFNO from DCP_RECEIVING_DETAIL "
                              + " where OrganizationNO=? AND EID=? AND SHOPID=? AND ReceivingNO=? )"
        );

        return sqlbuf.toString();
    }
    private String getQuerySql_updatePOrder1() {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(""
                              + "SELECT OFNO "
                              + " from ( "
                              + " select DISTINCT OFNO from DCP_Receiving_DETAIL "
                              + " where OrganizationNO=?  AND EID=?   AND SHOPID=?  AND ReceivingNO=? "
        );

        sqlbuf.append(" ) TBL ");

        return sqlbuf.toString();
    }
    private String getQuerySql_updatePOrder2() {

        StringBuffer sqlbuf = new StringBuffer();

        sqlbuf.append(""
                              + "SELECT * "
                              + " from DCP_POrder_detail "
                              + " where OrganizationNO=? AND EID = ? AND SHOPID = ? AND DETAIL_STATUS='0' AND POrderNO = ?"
        );

        return sqlbuf.toString();
    }

    private boolean checkGuid(DCP_StockInRefundCreateReq req) throws Exception {

        DCP_StockInRefundCreateReq.levelElm request = req.getRequest();
        String guid = request.getStockInID();
        boolean existGuid;

        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(""
                              + "select StockIn_ID "
                              + " from DCP_StockIn "
                              + " where StockIn_ID = '"+guid+"' "
        );
        String sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && !getQData.isEmpty()) {
            existGuid = true;
        } else {
            existGuid =  false;
        }

        return existGuid;
    }
    
    
}
