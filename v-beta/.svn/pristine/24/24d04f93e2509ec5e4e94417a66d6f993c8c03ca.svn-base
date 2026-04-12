package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockInRefundProcessReq;
import com.dsc.spos.json.cust.res.DCP_StockInRefundProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.webhook.WebHookService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.bc.BcReq;
import com.dsc.spos.utils.bc.BcRes;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

public class DCP_StockInRefundProcess extends SPosAdvanceService<DCP_StockInRefundProcessReq, DCP_StockInRefundProcessRes>
{


    @Override
    protected void processDUID(DCP_StockInRefundProcessReq req, DCP_StockInRefundProcessRes res) throws Exception
    {
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        DCP_StockInRefundProcessReq.levelElm request = req.getRequest();
        String loadDocNO = request.getLoadDocNo();
        String stockInNO = request.getStockInNo();
        String docType = request.getDocType();
        String ofNO = request.getOfNo();
        String status = request.getStatus();
        String corp="";
        String deliveryCorp="";

        //获取启用在途参数 Enable_InTransit
        String Enable_InTransit= PosPub.getPARA_SMS(dao, eId, "", "Enable_InTransit");
        if (Check.Null(Enable_InTransit)) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取在途仓参数:Enable_InTransit失败");
        }
        String transferShop = "";
        String opNo = req.getOpNO();
        String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String sTime = new SimpleDateFormat("HHmmss").format(new Date());


        //当传入的status=2时 处理以下逻辑
        if(status != null && status.equals("2")){
            //存在自动收货，所以要判断一下Doc_Type=0，要判断Load_DocNo是否有在数据库中存
            if(docType.equals("0")) {
                String exisstockin="select * from DCP_STOCKIN where EID='"+eId+"' and SHOPID='"+shopId+"'  "
                        + "and Load_DocNo='"+loadDocNO+"'  and status='2' ";
                List<Map<String, Object>> stakelist=this.doQueryData(exisstockin, null);
                if(stakelist !=null && !stakelist.isEmpty()) {
                    res.setSuccess(false);
                    res.setServiceStatus("000");
                    res.setServiceDescription("该单据已经收货！");
                    return;
                }
            }

            String exisstockin="select * from DCP_STOCKIN where EID='"+eId+"' and SHOPID='"+shopId+"'  "
                    + "and stockInNO='"+stockInNO+"'  and status='2' ";
            List<Map<String, Object>> stakelist=this.doQueryData(exisstockin, null);
            if(stakelist !=null && stakelist.isEmpty()==false) {
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
                return;
            }

            //查收货通知单并进行资料检核  BY JZMA 20191216
            if ( docType.equals("0") && !Check.Null(ofNO)) {
                String recevinhead="select a.*,b.pluno,b.status as detailstatus,c.status as dcp_goods_status from dcp_receiving a"
                        + " inner join dcp_receiving_detail b on a.eid=b.eid and a.shopid=b.shopid and a.receivingno=b.receivingno"
                        + " left  join dcp_goods c on a.eid=c.eid and b.pluno=c.pluno and c.status='100' "
                        + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.receivingno='"+ofNO+"' ";
                List<Map<String, Object>> listhead=this.doQueryData(recevinhead, null);
                if(listhead != null && listhead.isEmpty() == false) {
                    if (listhead.get(0).get("STATUS").toString().equals("8")) {
                        //by jinzma 2019/6/12 通知单被ERP撤销，入库单保存时增加通知单状态的管控 status<>'8' 
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "收货通知单:" +ofNO + " 被ERP撤销,请重新查询！" );
                    }
                    if (!loadDocNO.equals(listhead.get(0).get("LOAD_DOCNO").toString())) {
                        //by jinzma 2019/12/16 检查通知单来源单号和前端送入的来源单号是否一致，避免单身资料出现异常 
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "收货通知单来源单号:" +loadDocNO + " 检核异常,请重新查询！" );
                    } else {
                        // 确认是否所有商品都已收货 by jinzma 20210422【ID1017038】【3.0货郎】按商品行收货(DCP_StockInProcess（收货单确认）)
                        String receivingPlu ="";
                        int i=0;
                        for (Map<String, Object> oneReceivingQDate : listhead){
                            String detailPluNo = oneReceivingQDate.get("PLUNO").toString();
                            String detailStatus = oneReceivingQDate.get("DETAILSTATUS").toString();
                            String dcpGoodsStatus = oneReceivingQDate.get("DCP_GOODS_STATUS").toString();

                            // 【ID1017769】【大连大万3.0】总部发货后，门店没有收到  by jinzma 20210517 未关联到dcp_goods抛异常
                            if (Check.Null(dcpGoodsStatus)){
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "商品:"+detailPluNo+"在商品资料档中不存在,请确认");
                            }
                            if (detailStatus.equals("0")) {
                                receivingPlu = receivingPlu + detailPluNo + ",";
                                i++;
                                if (i >= 10) {
                                    break;
                                }
                            }
                        }
                        if (!Check.Null(receivingPlu)){
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "商品:"+receivingPlu+"未做收货确认，请先完成收货!" );
                        }


                        if (!Check.Null(loadDocNO)) {
                            //插入单据检查表 DCP_PLATFORM_BILLCHECK，避免单号重复
                            String[] columns = {"EID", "SHOPID", "BILLTYPE", "BILLNO"} ;
                            DataValue[] insValue = new DataValue[]
                                    {
                                            new DataValue(eId, Types.VARCHAR),
                                            new DataValue(shopId, Types.VARCHAR),
                                            new DataValue("receivingStockIn", Types.VARCHAR),
                                            new DataValue(loadDocNO, Types.VARCHAR),
                                    };
                            InsBean ib = new InsBean("DCP_PLATFORM_BILLCHECK", columns);
                            ib.addValues(insValue);
                            this.addProcessData(new DataProcessBean(ib));
                        }
                    }
                } else {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "收货通知单:" +ofNO + " 不存在,请重新查询！" );
                }
            }

            //更新主表
            UptBean ub1 = new UptBean("DCP_STOCKIN");
            //add Value
            ub1.addUpdateValue("status", new DataValue(status, Types.VARCHAR));
            ub1.addUpdateValue("ACCOUNT_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
            ub1.addUpdateValue("ConfirmBy", new DataValue(opNo, Types.VARCHAR));
            ub1.addUpdateValue("Confirm_Date", new DataValue(sDate, Types.VARCHAR));
            ub1.addUpdateValue("Confirm_Time", new DataValue(sTime, Types.VARCHAR));
            ub1.addUpdateValue("accountBy", new DataValue(opNo, Types.VARCHAR));
            ub1.addUpdateValue("account_Date", new DataValue(accountDate, Types.VARCHAR));
            ub1.addUpdateValue("account_Time", new DataValue(sTime, Types.VARCHAR));
            ub1.addUpdateValue("SUBMITBY", new DataValue(opNo, Types.VARCHAR));
            ub1.addUpdateValue("SUBMIT_DATE", new DataValue(sDate, Types.VARCHAR));
            ub1.addUpdateValue("SUBMIT_TIME", new DataValue(sTime, Types.VARCHAR));
            ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
            ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));


            //condition
            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("stockINNO", new DataValue(stockInNO, Types.VARCHAR));
            ub1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            String sqlTransferShop = null;
            String[] conditionValue_TransferShop = { eId, shopId, organizationNO,ofNO };
            sqlTransferShop = this.getQuerySql_TransferShop();
            List<Map<String, Object>> getQData_TransferShop = this.doQueryData(sqlTransferShop, conditionValue_TransferShop);
            if (getQData_TransferShop != null && getQData_TransferShop.isEmpty() == false) {
                transferShop = (String) getQData_TransferShop.get(0).get("TRANSFERSHOP");
            }
            if (docType.equals("1") && (transferShop.length() == 0) ){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "发货门店不可为空");
            }

            // IF 参数是否启用批号==N ，库存流水的批号和日期字段不给值 BY JZMA 20191024
            String isBatchPara = PosPub.getPARA_SMS(dao, eId, "", "Is_BatchNO");
            if (Check.Null(isBatchPara) || !isBatchPara.equals("Y")) isBatchPara="N";

            //加入库存流水账信息
            if(status.equals("2")) {

                Map<String, Object> stockChangeVerifyMsg = PosPub.getStockChangeVerifyMsg(dao, req.geteId(), req.getOrganizationNO(), accountDate);
                if("N".equals( stockChangeVerifyMsg.get("check").toString())){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, stockChangeVerifyMsg.get("errorMsg").toString());
                }

                String sql = this.GetDCP_StockIn_Sql(req);
                String[] condCountValues={};
                List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,condCountValues);

                //有差异
                boolean b_diff_detail=false;

                if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
                    //根据参数AutoStockinDiff处理收货自动产生差异
                    String AutoStockinDiff=PosPub.getPARA_SMS(dao, eId, "", "AutoStockinDiff");
                    //差异申诉用
                    BigDecimal totpqty=new BigDecimal(0);
                    BigDecimal totamt=new BigDecimal(0);
                    BigDecimal totcqty=new BigDecimal(0);
                    BigDecimal totdistriamt=new BigDecimal(0);

                    deliveryCorp = getQDataDetail.get(0).get("DELIVERYCORP").toString();
                    corp=getQDataDetail.get(0).get("CORP").toString();

                    String DifferenceNo="";
                    String bsno="";


                    //根据参数AutoStockinDiff处理收货自动产生差异
                    if (docType.equals("0")) {
                        if (AutoStockinDiff != null && AutoStockinDiff.equals("Y")) {
                            DifferenceNo = this.getDifferenceNo(req);

                            String bs_SQL = "select BSNO from dcp_reason where eid='" + eId + "' and bstype='2' and status='100' ";
                            List<Map<String, Object>> getQDataBS = this.doQueryData(bs_SQL, null);
                            if (getQDataBS != null && getQDataBS.size() > 0) {
                                bsno = getQDataBS.get(0).get("BSNO").toString();
                            } else {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "bstype=2的退货原因没记录，请添加差异申诉的退货原因");
                            }
                        }
                    }


                    for (Map<String, Object> oneData : getQDataDetail) {
                        //判断仓库不能为空或空格  BY JZMA 20191118
                        String stockWarehouse = oneData.get("WAREHOUSE_DETAIL").toString();
                        if (Check.Null(stockWarehouse)||Check.Null(stockWarehouse.trim())) {
                            this.pData.clear();
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "仓库不能为空或空格");
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
                            case "3":
                                stockDocType = "14";
                                break;
                            case "4":
                                stockDocType = "19";
                                break;
                        }
                        String featureNo=oneData.getOrDefault("FEATURENO", " ").toString();
                        if (Check.Null(featureNo)) {
                            featureNo = " ";
                        }

                        BcReq bcReq=new BcReq();
                        bcReq.setServiceType("StockInRefundProcess");
                        bcReq.setDocType(docType);
                        bcReq.setBillType(stockDocType);
                        bcReq.setSyneryDiff(!deliveryCorp.equals(corp));

                        BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                        String bType = bcMap.getBType();
                        String costCode = bcMap.getCostCode();
                        if(Check.Null(bType)||Check.Null(costCode)){
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                        }


                        //增加在库数
                        String procedure="SP_DCP_STOCKCHANGE_VX";
                        Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                        inputParameter.put(1,eId );                              //--企业ID
                        inputParameter.put(2,null );
                        inputParameter.put(3,shopId );                           //--组织
                        inputParameter.put(4, bType);
                        inputParameter.put(5, costCode);
                        inputParameter.put(6,stockDocType);                      //--单据类型
                        inputParameter.put(7,stockInNO);	                       //--单据号
                        inputParameter.put(8,oneData.get("ITEM").toString());    //--单据行号
                        inputParameter.put(9,"0");
                        inputParameter.put(10,"1");                               //--异动方向 1=加库存 -1=减库存
                        inputParameter.put(11,oneData.get("BDATE").toString());   //--营业日期 yyyy-MM-dd
                        inputParameter.put(12,oneData.get("PLUNO").toString());   //--品号
                        inputParameter.put(13,featureNo); //--特征码
                        inputParameter.put(14,stockWarehouse);            //--仓库
                        inputParameter.put(15,oneData.get("BATCH_NO").toString());     //--批号
                        inputParameter.put(16,oneData.get("LOCATION").toString());     //--批号
                        inputParameter.put(17,oneData.get("PUNIT").toString());        //--交易单位
                        inputParameter.put(18,oneData.get("PQTY").toString());         //--交易数量
                        inputParameter.put(19,oneData.get("BASEUNIT").toString());     //--基准单位
                        inputParameter.put(20,oneData.get("BASEQTY").toString());      //--基准数量
                        inputParameter.put(21,oneData.get("UNIT_RATIO").toString());   //--换算比例
                        inputParameter.put(22,oneData.get("PRICE").toString());        //--零售价
                        inputParameter.put(23,oneData.get("AMT").toString());          //--零售金额
                        inputParameter.put(24,oneData.getOrDefault("DISTRIPRICE", "0").toString());  //--进货价
                        inputParameter.put(25,oneData.getOrDefault("DISTRIAMT", "0").toString());    //--进货金额
                        inputParameter.put(26,accountDate );                           //--入账日期 yyyy-MM-dd
                        inputParameter.put(27,oneData.get("PROD_DATE").toString());    //--批号的生产日期 yyyy-MM-dd
                        inputParameter.put(28,oneData.get("BDATE").toString());        //--单据日期
                        inputParameter.put(29,"");                                     //--异动原因
                        inputParameter.put(30,"");                                     //--异动描述
                        inputParameter.put(31,req.getOpNO());                          //--操作员

                        ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                        this.addProcessData(new DataProcessBean(pdb));

                        //(调拨收货||移仓入库)&&启用在途
                        if((docType.equals("1")||docType.equals("4")) && Enable_InTransit.equals("Y")) {
                            //调拨收货不允许修改数量，不会调用此服务，移仓在出库的时候已经审核掉，所以这段代码不应该执行
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,"调拨收货或移仓入库调用服务错误，请联系前端排查");
                        }

                        //根据参数AutoStockinDiff处理收货自动产生差异 by knagzc
                        if (docType.equals("0")) {
                            if (AutoStockinDiff != null && AutoStockinDiff.equals("Y")) {
                                //
                                BigDecimal bdm_pqty = new BigDecimal(oneData.get("PQTY").toString());
                                BigDecimal bdm_receiving_qty = new BigDecimal(oneData.get("RECEIVING_QTY").toString());
                                BigDecimal bdm_diff = bdm_pqty.subtract(bdm_receiving_qty);

                                //只要有差异就计算
                                if (bdm_diff.compareTo(BigDecimal.ZERO) != 0) {

                                    b_diff_detail = true;

                                    BigDecimal bdm_amt = bdm_diff.multiply(new BigDecimal(oneData.get("PRICE").toString())).setScale(2, RoundingMode.HALF_UP);
                                    BigDecimal bdm_distriamt = bdm_diff.multiply(new BigDecimal(oneData.getOrDefault("DISTRIPRICE", "0").toString())).setScale(2, RoundingMode.HALF_UP);

                                    totpqty = totpqty.add(bdm_diff);
                                    totamt = totamt.add(bdm_amt);
                                    totcqty = totcqty.add(new BigDecimal(1));
                                    totdistriamt = totdistriamt.add(bdm_distriamt);


                                    int insColCt = 0;
                                    String[] columnsDifferenceDetail = {"SHOPID", "DIFFERENCENO", "ITEM", "OITEM", "PLUNO",
                                            "PUNIT", "PQTY", "BASEUNIT", "BASEQTY", "UNIT_RATIO", "BSNO", "PRICE", "AMT", "REQ_QTY",
                                            "EID", "ORGANIZATIONNO", "WAREHOUSE", "BATCH_NO", "PROD_DATE", "DISTRIPRICE",
                                            "DISTRIAMT", "LOAD_ITEM", "FEATURENO"
                                    };

                                    DataValue[] columnsVal = new DataValue[columnsDifferenceDetail.length];
                                    for (int i = 0; i < columnsVal.length; i++) {
                                        String keyVal = null;
                                        switch (i) {
                                            case 0:
                                                keyVal = shopId;
                                                break;
                                            case 1:
                                                keyVal = DifferenceNo;
                                                break;
                                            case 2:
                                                keyVal = oneData.get("ITEM").toString();
                                                break;
                                            case 3:
                                                keyVal = oneData.get("OITEM").toString();
                                                break;
                                            case 4:
                                                keyVal = oneData.get("PLUNO").toString();
                                                break;
                                            case 5:
                                                keyVal = oneData.get("PUNIT").toString();
                                                break;
                                            case 6:
                                                //keyVal = "0";    // pqty
                                                keyVal = bdm_diff.toPlainString();    //录入数量改取差异申请数
                                                break;
                                            case 7:
                                                keyVal = oneData.get("BASEUNIT").toString();
                                                break;
                                            case 8:
                                                //keyVal = "0";    // wqty
                                                keyVal = bdm_diff.multiply(new BigDecimal(oneData.get("UNIT_RATIO").toString())).toPlainString();    //改取前端基准数量
                                                break;
                                            case 9:
                                                keyVal = oneData.get("UNIT_RATIO").toString(); // unitRatio
                                                break;
                                            case 10:
                                                keyVal = bsno;//
                                                break;
                                            case 11:
                                                keyVal = oneData.get("PRICE").toString();
                                                break;
                                            case 12:
                                                keyVal = bdm_amt.toPlainString();
                                                break;
                                            case 13:
                                                keyVal = bdm_diff.toPlainString();
                                                break;
                                            case 14:
                                                keyVal = req.geteId();
                                                break;
                                            case 15:
                                                keyVal = req.getOrganizationNO();
                                                break;
                                            case 16:
                                                keyVal = stockWarehouse;
                                                break;
                                            case 17:
                                                keyVal = oneData.get("BATCH_NO").toString();
                                                break;
                                            case 18:
                                                keyVal = oneData.get("PROD_DATE").toString();
                                                break;
                                            case 19:
                                                keyVal = oneData.getOrDefault("DISTRIPRICE", "0").toString();
                                                break;
                                            case 20:
                                                keyVal = bdm_distriamt.toPlainString();
                                                break;
                                            case 21:
                                                keyVal = oneData.get("OITEM").toString();
                                                break;
                                            case 22:
                                                keyVal = featureNo;
                                                if (Check.Null(keyVal))
                                                    keyVal = " ";
                                                break;
                                            default:
                                                break;
                                        }

                                        if (keyVal != null) {
                                            insColCt++;
                                            if (i == 2 || i == 3) {
                                                columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
                                            } else if (i == 6 || i == 8 || i == 9 || i == 11 || i == 12 || i == 13 || i == 19) {
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
                                            columns2[insColCt] = columnsDifferenceDetail[i];
                                            insValue2[insColCt] = columnsVal[i];
                                            insColCt++;
                                            if (insColCt >= insValue2.length)
                                                break;
                                        }
                                    }

                                    InsBean ib2 = new InsBean("DCP_DIFFERENCE_DETAIL", columns2);
                                    ib2.addValues(insValue2);
                                    this.addProcessData(new DataProcessBean(ib2));

                                }
                            }
                        }
                    }

                    //根据参数AutoStockinDiff处理收货自动产生差异 by knagzc
                    if (docType.equals("0")) {
                        if (AutoStockinDiff != null && AutoStockinDiff.equals("Y") && b_diff_detail) {
                            Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
                            condition.put("STOCKINNO", true);

                            List<Map<String, Object>> header = MapDistinct.getMap(getQDataDetail, condition);
                            if (header != null && header.size() > 0) {
                                //DCP_DIFFERENCE
                                String[] columnsDifference = {
                                        "SHOPID", "ORGANIZATIONNO", "CREATEBY", "CREATE_DATE", "CREATE_TIME",
                                        "TOT_PQTY", "TOT_AMT", "TOT_CQTY", "EID", "DIFFERENCENO",
                                        "STATUS", "BDATE", "DIFFERENCE_ID", "OTYPE", "OFNO", "LOAD_DOCNO",
                                        "LOAD_DOCTYPE", "DOC_TYPE", "WAREHOUSE", "TRANSFER_SHOP", "TOT_DISTRIAMT",
                                        "SUBMITBY", "SUBMIT_DATE", "SUBMIT_TIME", "CONFIRMBY", "CONFIRM_DATE",
                                        "CONFIRM_TIME", "ACCOUNTBY", "ACCOUNT_DATE", "ACCOUNT_TIME",
                                        "UPDATE_TIME","TRAN_TIME"
                                };

                                DataValue[] insValue1 = new DataValue[]
                                        {
                                                new DataValue(shopId, Types.VARCHAR),
                                                new DataValue(organizationNO, Types.VARCHAR),
                                                new DataValue(req.getOpNO(), Types.VARCHAR),
                                                new DataValue(sDate, Types.VARCHAR),
                                                new DataValue(sTime, Types.VARCHAR),
                                                new DataValue(totpqty, Types.VARCHAR),
                                                new DataValue(totamt, Types.VARCHAR),
                                                new DataValue(totcqty, Types.VARCHAR),
                                                new DataValue(eId, Types.VARCHAR),
                                                new DataValue(DifferenceNo, Types.VARCHAR),
                                                new DataValue("1", Types.VARCHAR),//STATUS 0新建 1提交
                                                new DataValue(accountDate, Types.VARCHAR),
                                                new DataValue(PosPub.getGUID(false), Types.VARCHAR),
                                                new DataValue("0", Types.VARCHAR),
                                                new DataValue(stockInNO, Types.VARCHAR),
                                                new DataValue(request.getLoadDocNo(), Types.VARCHAR),
                                                new DataValue("0", Types.VARCHAR),
                                                new DataValue("3", Types.VARCHAR),
                                                new DataValue(header.get(0).get("WAREHOUSE_MAIN").toString(), Types.VARCHAR),
                                                new DataValue(header.get(0).get("TRANSFER_SHOP").toString(), Types.VARCHAR),
                                                new DataValue(totdistriamt, Types.VARCHAR),
                                                new DataValue(req.getOpNO(), Types.VARCHAR),
                                                new DataValue(sDate, Types.VARCHAR),
                                                new DataValue(sTime, Types.VARCHAR),
                                                new DataValue(req.getOpNO(), Types.VARCHAR),
                                                new DataValue(sDate, Types.VARCHAR),
                                                new DataValue(sTime, Types.VARCHAR),
                                                new DataValue(opNo, Types.VARCHAR),
                                                new DataValue(accountDate, Types.VARCHAR),
                                                new DataValue(sTime, Types.VARCHAR),
                                                new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                                                new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR)
                                        };

                                InsBean ib1 = new InsBean("DCP_DIFFERENCE", columnsDifference);
                                ib1.addValues(insValue1);
                                this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

                            }
                        }
                    }

                }
            }

            // 通过收货通知单单身的来源单号，回写要货单状态： 3-已完成
            if (docType.equals("0")) {
                //要货单回写的逻辑还要重新规划，之前2.0搞了很多SQL查询，实际又没回写
                /*******************2.0逻辑处理************************************************
                 String sql = this.getQuerySql_checkPOrder1();
                 String[] conditionValues_checkPOrder1 = { organizationNO, eId, shopId, organizationNO, eId, shopId, ofNO }; // 查詢條件
                 List<Map<String, Object>> getQData_checkPOrder1 = this.doQueryData(sql, conditionValues_checkPOrder1);

                 if (getQData_checkPOrder1 != null && getQData_checkPOrder1.isEmpty() == false) {
                 //List<Map<String, String>> jsonDatas = req.getDatas();
                 //for (Map<String, String> par : jsonDatas) {

                 sql = this.getQuerySql_updatePOrder1();
                 String[] conditionValues_updatePOrder1 = { organizationNO, eId, shopId, ofNO }; // 查詢條件
                 List<Map<String, Object>> getQData_updatePOrder1 = this.doQueryData(sql, conditionValues_updatePOrder1);

                 if (getQData_updatePOrder1 != null && getQData_updatePOrder1.isEmpty() == false) { // 有資料，取得詳細內容
                 for (Map<String, Object> oneData : getQData_updatePOrder1) {
                 String porderNO1 = oneData.get("OFNO").toString();
                 String detailStatus = "1";
                 String item = "";//DCP_Receiving_DETAIL.OITEM //par.get("OITEM");
                 String stockinQty = "0";//传入的pqty

                 UptBean ubPOrder1 = null;
                 ubPOrder1 = new UptBean("DCP_POrder_DETAIL");
                 // add value
                 ubPOrder1.addUpdateValue("DETAIL_STATUS", new DataValue(detailStatus, Types.VARCHAR));
                 ubPOrder1.addUpdateValue("STOCKIN_QTY", new DataValue(stockinQty, Types.DECIMAL));

                 // condition
                 ubPOrder1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                 ubPOrder1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                 ubPOrder1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                 ubPOrder1.addCondition("porderNO", new DataValue(porderNO1, Types.VARCHAR));
                 ubPOrder1.addCondition("Item", new DataValue(item, Types.VARCHAR));
                 this.addProcessData(new DataProcessBean(ubPOrder1));
                 }
                 }
                 //}
                 }

                 sql = this.getQuerySql_updatePOrder1();
                 String[] conditionValues_updatePOrder1 = { organizationNO, eId, shopId, ofNO }; // 查詢條件
                 List<Map<String, Object>> getQData_updatePOrder1 = this.doQueryData(sql, conditionValues_updatePOrder1);

                 if (getQData_updatePOrder1 != null && getQData_updatePOrder1.isEmpty() == false) { // 有資料，取得詳細內容
                 for (Map<String, Object> oneData : getQData_updatePOrder1) {
                 String porderNO1 = oneData.get("OFNO").toString();
                 sql = this.getQuerySql_updatePOrder2();
                 String[] conditionValues_updatePOrder2 = { organizationNO, eId, shopId, porderNO1 }; // 查詢條件
                 List<Map<String, Object>> getQData_updatePOrder2 = this.doQueryData(sql,
                 conditionValues_updatePOrder2);

                 if (getQData_updatePOrder2 == null || getQData_updatePOrder2.isEmpty() == true) {
                 String status2 = "3";

                 UptBean ub2 = null;
                 ub2 = new UptBean("DCP_POrder");
                 // add value
                 ub2.addUpdateValue("Status", new DataValue(status2, Types.VARCHAR));
                 ub2.addUpdateValue("Complete_Date", new DataValue(completeDate, Types.VARCHAR));

                 // condition
                 ub2.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                 ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                 ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                 ub2.addCondition("porderNO", new DataValue(porderNO1, Types.VARCHAR));
                 this.addProcessData(new DataProcessBean(ub2));
                 //this.doExecuteDataToDB();
                 }
                 }
                 }
                 */

            }

            if (docType.equals("1")) {     //tw_StockIn 0 配送收货 1调拨  3其他入库 4移仓收货  不可能走到的代码段

                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,"调拨收货调用服务错误，请联系前端排查");
                //				UptBean ub3 = null;
                //				ub3 = new UptBean("DCP_STOCKOUT");
                //				//String loadDocNO = "";//传入的loadDocNO
                //				// add value
                //				ub3.addUpdateValue("Status", new DataValue("3", Types.VARCHAR));
                //				// condition
                //				ub3.addCondition("OrganizationNO", new DataValue(transferShop, Types.VARCHAR));
                //				ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                //				ub3.addCondition("SHOPID", new DataValue(transferShop, Types.VARCHAR));
                //				ub3.addCondition("stockoutno", new DataValue(loadDocNO, Types.VARCHAR));
                //				this.addProcessData(new DataProcessBean(ub3));			
            }

            if (docType.equals("0")||docType.equals("2")) {  //tw_StockIn 0 配送收货 1调拨  3其他入库 4移仓收货  不可能走到的代码段
                // 更新关联收货通知单
                UptBean ub3 =  new UptBean("DCP_Receiving");
                // add value
                ub3.addUpdateValue("Status", new DataValue("7", Types.VARCHAR));
                ub3.addUpdateValue("Complete_Date", new DataValue(sDate, Types.VARCHAR));
                // condition
                ub3.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub3.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub3.addCondition("ReceivingNO", new DataValue(ofNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub3));


                // 更新关联通知单单身 by jinzma 20210422 【ID1017035】【3.0货郎】按商品行收货(DCP_StockInCreate（收货入库单新建))
                // 历史资料单身STATUS可能为空，所以要一并更新成100
                if (docType.equals("0") && !Check.Null(ofNO)) {
                    UptBean ub4 = new UptBean("DCP_RECEIVING_DETAIL");
                    // add value
                    ub4.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
                    // condition
                    ub4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub4.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub4.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                    ub4.addCondition("RECEIVINGNO", new DataValue(ofNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub4));
                }

            }

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            //***********调用库存同步给三方，这是个异步，不会影响效能*****************
            try
            {
                WebHookService.stockSync(eId, shopId, stockInNO);
            }
            catch (Exception e)
            {

            }

        }

        if(status!=null&&status.equals("3")){
            String exisstockin="select * from DCP_STOCKIN where EID='"+eId+"' " +
                    "and SHOPID='"+shopId+"'  "
                    + "and stockInNO='"+stockInNO+"'  ";
            List<Map<String, Object>> list = this.doQueryData(exisstockin, null);
            String stockInOriginNo="";
            if(list.size()>0){
                stockInOriginNo=list.get(0).get("STOCKINNO_ORIGIN").toString();
            }

            //作废
            //增加红冲作废处理：（等同于删除）入参status=3
            //1.清空字段值：原出库单红冲单号STOCKOUTNO_REFUND
            //2.更新单据状态，作废人、作废时间，修改人，修改时间
            UptBean ub1 = new UptBean("DCP_STOCKIN");
            //add Value
            ub1.addUpdateValue("status", new DataValue(status, Types.VARCHAR));
            //ub1.addUpdateValue("STOCKINNO_REFUND", new DataValue("", Types.VARCHAR));
            ub1.addUpdateValue("CANCELBY", new DataValue(opNo, Types.VARCHAR));
            ub1.addUpdateValue("CANCEL_DATE", new DataValue(sDate, Types.VARCHAR));
            ub1.addUpdateValue("CANCEL_TIME", new DataValue(sTime, Types.VARCHAR));

            ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
            ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));


            //condition
            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("stockINNO", new DataValue(stockInNO, Types.VARCHAR));
            ub1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));


            UptBean ub_DCP_STOCKIN = new UptBean("DCP_STOCKIN");
            ub_DCP_STOCKIN.addUpdateValue("STOCKINNO_REFUND", new DataValue("", Types.VARCHAR));
            // condition
            ub_DCP_STOCKIN.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub_DCP_STOCKIN.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            ub_DCP_STOCKIN.addCondition("STOCKINNO", new DataValue(stockInOriginNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub_DCP_STOCKIN));

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        }
        
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockInRefundProcessReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockInRefundProcessReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockInRefundProcessReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_StockInRefundProcessReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        DCP_StockInRefundProcessReq.levelElm request = req.getRequest();

        String stockInNO = request.getStockInNo();
        String docType = request.getDocType();
        String status = request.getStatus();
        String differenceID = request.getDifferenceID();

        if (Check.Null(stockInNO)) {
            errMsg.append("单号不可为空值, ");
            isFail = true;
        }

        if (Check.Null(docType)) {
            errMsg.append("单据类型不可为空值, ");
            isFail = true;
        }

        if (Check.Null(status)) {
            errMsg.append("状态不可为空值, ");
            isFail = true;
        }

        if(Check.Null(differenceID)&& docType.equals("1")){
            errMsg.append("差异ID不可为空值, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_StockInRefundProcessReq> getRequestType()
    {
        return new TypeToken<DCP_StockInRefundProcessReq>(){};
    }

    @Override
    protected DCP_StockInRefundProcessRes getResponseType()
    {
        return new DCP_StockInRefundProcessRes();
    }


    private String GetDCP_StockIn_Sql(DCP_StockInRefundProcessReq req) throws Exception {
        DCP_StockInRefundProcessReq.levelElm request = req.getRequest();
        String sql=""
                + " select a.STOCKINNO,a.TRANSFER_SHOP,a.BDATE,a.DOC_TYPE,a.OTYPE,a.OFNO,MEMO,CREATEBY,CREATE_DATE,CREATE_TIME,"
                + " ACCOUNTBY,ACCOUNT_DATE,ACCOUNT_TIME,LOAD_DOCTYPE,LOAD_DOCNO,ITEM,OITEM,PLUNO,PUNIT,PQTY,BASEUNIT,"
                + " UNIT_RATIO,BASEQTY,PRICE,AMT,a.WAREHOUSE AS WAREHOUSE_MAIN,b.WAREHOUSE AS WAREHOUSE_DETAIL, "
                + " BATCH_NO,PROD_DATE,DISTRIPRICE,DISTRIAMT,b.featureno,b.RECEIVING_QTY,a.deliverycorp,a.corp,b.MES_LOCATION as location "
                + " from DCP_STOCKIN a "
                + " inner join DCP_STOCKIN_DETAIL b on a.STOCKINNO=b.STOCKINNO and a.EID=b.EID and "
                + " a.Organizationno=b.Organizationno and a.BDATE=b.BDATE "
                + " where a.EID='"+req.geteId()+"'   "
                + " and a.Organizationno='"+req.getOrganizationNO()+"'  "
                + " and a.STOCKINNO='"+request.getStockInNo()+"'";

        return sql;
    }

    private String getQuerySql_TransferShop() throws Exception {
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer();

        sqlbuf.append(""
                              + "select transferShop "
                              + " from ( "
                              + " select transfer_shop as transferShop "
                              + " from DCP_receiving "
                              + " where EID=? and SHOPID=? and organizationno=? and receivingNo=? "
        );
        sqlbuf.append(" ) TBL ");


        sql = sqlbuf.toString();

        return sql;
    }

    private String getDifferenceNo(DCP_StockInRefundProcessReq req) throws Exception {
        String DifferenceNo="";
        String sql = "";
        String shopId = req.getShopId();
        String organizationNO = req.getShopId();
        String eId = req.geteId();
        String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        StringBuffer sqlbuf=new StringBuffer();

        String[] conditionValues = {shopId, eId,organizationNO }; // 查询条件
        String ajustnoHead="CYSS" + bDate;
        sqlbuf.append("select DIFFERENCENO from (select MAX(DIFFERENCENO) DIFFERENCENO from DCP_DIFFERENCE "
                              + " where SHOPID=? "
                              + " and EID=? "
                              + " and ORGANIZATIONNO=? "
                              + " and DIFFERENCENO like '"+ ajustnoHead+"%%')");
        sql=sqlbuf.toString();

        List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
        if (getQData != null && getQData.isEmpty() == false) {
            DifferenceNo = (String) getQData.get(0).get("DIFFERENCENO");

            if (DifferenceNo != null && DifferenceNo.length() > 0) {
                long i;
                DifferenceNo = DifferenceNo.substring(4);
                i = Long.parseLong(DifferenceNo) + 1;
                DifferenceNo = i + "";
                DifferenceNo = "CYSS" + DifferenceNo;
            } else {
                //当天无单，从00001开始
                DifferenceNo = "CYSS" + bDate + "00001";
            }
        } else {
            //当天无单，从00001开始
            DifferenceNo = "CYSS" + bDate + "00001";
        }

        return DifferenceNo;
    }


}
