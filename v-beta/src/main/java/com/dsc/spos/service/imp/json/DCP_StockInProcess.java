package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DifferenceProcessReq;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataProcessReq;
import com.dsc.spos.json.cust.req.DCP_StockInProcessReq;
import com.dsc.spos.json.cust.req.DCP_StockInProcessReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_DifferenceProcessRes;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataProcessRes;
import com.dsc.spos.json.cust.res.DCP_StockInProcessRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.utils.ServiceAgentUtils;
import com.dsc.spos.service.webhook.WebHookService;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.bc.BcReq;
import com.dsc.spos.utils.bc.BcRes;
import com.dsc.spos.utils.transitStock.TransitStockAdjust;
import com.google.gson.reflect.TypeToken;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_StockInProcess extends SPosAdvanceService<DCP_StockInProcessReq, DCP_StockInProcessRes> {


    @Override
    protected boolean isVerifyFail(DCP_StockInProcessReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        levelElm request = req.getRequest();

        String docType = request.getDocType();
        String differenceID = request.getDifferenceID();

        if (Check.Null(differenceID) && docType.equals("1")) {
            errMsg.append("差异ID不可为空值, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }


    @Override
    protected void processDUID(DCP_StockInProcessReq req, DCP_StockInProcessRes res) throws Exception {
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        if (StringUtils.isNotEmpty(req.getRequest().getOrgNo())) {
            organizationNO = req.getRequest().getOrgNo();
        }
        String eId = req.geteId();
        levelElm request = req.getRequest();
        String loadDocNO = request.getLoadDocNo();
        String stockInNO = request.getStockInNo();
        String docType = request.getDocType();
        String ofNO = request.getOfNo();
        if (Check.Null(ofNO)) {
            ofNO = "";
        }
        String oType = "";
        String status = request.getStatus();

        String createTime = new SimpleDateFormat("HHmmss").format(new Date());
        String bDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        //获取启用在途参数 Enable_InTransit
        String Enable_InTransit = PosPub.getPARA_SMS(dao, eId, "", "Enable_InTransit");
        if (Check.Null(Enable_InTransit)) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取在途仓参数:Enable_InTransit失败");
        }
        String transferShop = "";
        String opNo = req.getEmployeeNo();
        String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String sTime = new SimpleDateFormat("HHmmss").format(new Date());

        String corp="";
        String deliveryCorp="";

        //1. 【单据状态】非“0-新建”不可过账！
        //2. 【扣账日期】不可为空，且不可小于库存关账日期！（成本域组织参数：成本计算前必须冻结库存不可异动）
        String stransferShop = "";
        String stockInSql = "select * from dcp_stockin a where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "' " +
                " and a.stockinno='" + stockInNO + "' ";
        List<Map<String, Object>> stockInList = this.doQueryData(stockInSql, null);
        if (CollUtil.isNotEmpty(stockInList)) {
            String stockInStatus = stockInList.get(0).get("STATUS").toString();
            stransferShop = stockInList.get(0).get("TRANSFER_SHOP").toString();
            corp=stockInList.get(0).get("CORP").toString();
            deliveryCorp=stockInList.get(0).get("DELIVERYCORP").toString();
            docType=stockInList.get(0).get("DOC_TYPE").toString();
            if (Check.Null(ofNO)) {
                ofNO = stockInList.get(0).get("OFNO").toString();
            }

            oType = stockInList.get(0).get("OTYPE").toString();
            if (!"0".equals(stockInStatus)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据状态不为[新建]不可过账");
            }

            String accSql="select a.*,to_char(a.INVCLOSINGDATE,'yyyyMMdd') as INVCLOSINGDATES  from DCP_ACOUNT_SETTING a where a.eid='"+eId+"' and a.corp='"+req.getCorp()+"'  and a.ACCTTYPE='1' and a.status='100' ";
            List<Map<String, Object>> accList = this.doQueryData(accSql, null);
            if(accList.size()<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "找不到对应的账套");
            }

            String invClosingDate = accList.get(0).get("INVCLOSINGDATES").toString();

            //String account_date = stockInList.get(0).get("ACCOUNT_DATE").toString();
            if (accountDate.compareTo(invClosingDate) < 0) {
                 throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "扣账日期不可小于库存关账日期");
            }
        }else {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"当前组织"+organizationNO + "无对应单据:"+stockInNO);
        }


        ////tw_StockIn 0 配送收货 1调拨  3其他入库 4移仓收货
        //当传入的status=2时 处理以下逻辑
        if (status != null && status.equals("2")) {
            //存在自动收货，所以要判断一下Doc_Type=0，要判断Load_DocNo是否有在数据库中存
            if (docType.equals("0")) {
                String exisstockin = "select * from DCP_STOCKIN where EID='" + eId + "' and SHOPID='" + shopId + "'  "
                        + "and Load_DocNo='" + loadDocNO + "'  and status='2' ";
                List<Map<String, Object>> stakelist = this.doQueryData(exisstockin, null);
                if (stakelist != null && !stakelist.isEmpty()) {
                    res.setSuccess(false);
                    res.setServiceStatus("000");
                    res.setServiceDescription("该单据已经收货！");
                    return;
                }
            }

            String exisstockin = "select * from DCP_STOCKIN where EID='" + eId + "' and SHOPID='" + shopId + "'  "
                    + "and stockInNO='" + stockInNO + "'  and status='2' ";
            List<Map<String, Object>> stakelist = this.doQueryData(exisstockin, null);
            if (stakelist != null && stakelist.isEmpty() == false) {
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
                return;
            }

            //查收货通知单并进行资料检核  BY JZMA 20191216
            if (docType.equals("0") && !Check.Null(ofNO) && ("1".equals(oType) || "4".equals(oType))) {
                String recevinhead = "select a.*,b.pluno,b.status as detailstatus,c.status as dcp_goods_status from dcp_receiving a"
                        + " inner join dcp_receiving_detail b on a.eid=b.eid and a.shopid=b.shopid and a.receivingno=b.receivingno"
                        + " left  join dcp_goods c on a.eid=c.eid and b.pluno=c.pluno and c.status='100' "
                        + " where a.eid='" + eId + "' " +//and a.shopid='"+stransferShop+"'
                        " and a.receivingno='" + ofNO + "' ";
                List<Map<String, Object>> listhead = this.doQueryData(recevinhead, null);
                if (listhead != null && listhead.isEmpty() == false) {
                    if (listhead.get(0).get("STATUS").toString().equals("8")) {
                        //by jinzma 2019/6/12 通知单被ERP撤销，入库单保存时增加通知单状态的管控 status<>'8' 
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "收货通知单:" + ofNO + " 被ERP撤销,请重新查询！");
                    }
                    if (Check.NotNull(loadDocNO) && !loadDocNO.equals(listhead.get(0).get("LOAD_DOCNO").toString())) {
                        //by jinzma 2019/12/16 检查通知单来源单号和前端送入的来源单号是否一致，避免单身资料出现异常 
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "收货通知单来源单号:" + loadDocNO + " 检核异常,请重新查询！");
                    } else {
                        // 确认是否所有商品都已收货 by jinzma 20210422【ID1017038】【3.0货郎】按商品行收货(DCP_StockInProcess（收货单确认）)
                        String receivingPlu = "";
                        int i = 0;
                        for (Map<String, Object> oneReceivingQDate : listhead) {
                            String detailPluNo = oneReceivingQDate.get("PLUNO").toString();
                            String detailStatus = oneReceivingQDate.get("DETAILSTATUS").toString();
                            String dcpGoodsStatus = oneReceivingQDate.get("DCP_GOODS_STATUS").toString();

                            // 【ID1017769】【大连大万3.0】总部发货后，门店没有收到  by jinzma 20210517 未关联到dcp_goods抛异常
                            if (Check.Null(dcpGoodsStatus)) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "商品:" + detailPluNo + "在商品资料档中不存在,请确认");
                            }
                            if (detailStatus.equals("0")) {
                                receivingPlu = receivingPlu + detailPluNo + ",";
                                i++;
                                if (i >= 10) {
                                    break;
                                }
                            }
                        }
                        if (!Check.Null(receivingPlu)) {
                            //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "商品:"+receivingPlu+"未做收货确认，请先完成收货!" );
                        }


                        if (!Check.Null(loadDocNO)) {
                            //插入单据检查表 DCP_PLATFORM_BILLCHECK，避免单号重复
                            String[] columns = {"EID", "SHOPID", "BILLTYPE", "BILLNO"};
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
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "收货通知单:" + ofNO + " 不存在,请重新查询！");
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
//            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("stockINNO", new DataValue(stockInNO, Types.VARCHAR));
            ub1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            String sqlTransferShop = null;
            String[] conditionValue_TransferShop = {eId, ofNO};
            sqlTransferShop = this.getQuerySql_TransferShop();
            List<Map<String, Object>> getQData_TransferShop = this.doQueryData(sqlTransferShop, conditionValue_TransferShop);
            if (getQData_TransferShop != null && getQData_TransferShop.isEmpty() == false) {
                transferShop = (String) getQData_TransferShop.get(0).get("TRANSFERSHOP");
            }
            if (docType.equals("1") && (transferShop.length() == 0)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "发货门店不可为空");
            }

            // IF 参数是否启用批号==N ，库存流水的批号和日期字段不给值 BY JZMA 20191024
            String isBatchPara = PosPub.getPARA_SMS(dao, eId, req.getOrganizationNO(), "Is_BatchNO");
            if (Check.Null(isBatchPara) || !isBatchPara.equals("Y")) isBatchPara = "N";

            String warehouse_pstockin = "";
            //加入库存流水账信息
            if (status.equals("2")) {

                Map<String, Object> stockChangeVerifyMsg = PosPub.getStockChangeVerifyMsg(dao, req.geteId(), req.getOrganizationNO(), accountDate);
                if("N".equals( stockChangeVerifyMsg.get("check").toString())){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, stockChangeVerifyMsg.get("errorMsg").toString());
                }

                //获取内部结算数据 不同法人才有
                String interSql="select * from DCP_INTERSETTLE_DETAIL a where a.eid='"+eId+"' " +
                        " and a.organizationno='"+organizationNO+"' and a.billno='"+stockInNO+"' ";
                List<Map<String, Object>> interData = dao.executeQuerySQL(interSql, null);


                String sql = this.GetDCP_StockIn_Sql(req);
                String[] condCountValues = {};
                List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, condCountValues);

                //有差异
                boolean b_diff_detail = false;

                if (getQDataDetail != null && !getQDataDetail.isEmpty()) {
                    //根据参数AutoStockinDiff处理收货自动产生差异
                    String AutoStockinDiff = PosPub.getPARA_SMS(dao, eId, "", "AutoStockinDiff");
                    //差异申诉用
                    BigDecimal totpqty = new BigDecimal(0);
                    BigDecimal totamt = new BigDecimal(0);
                    BigDecimal totcqty = new BigDecimal(0);
                    BigDecimal totdistriamt = new BigDecimal(0);

                    String DifferenceNo = "";
                    String bsno = "";


                    //根据参数AutoStockinDiff处理收货自动产生差异
                    if (docType.equals("0") ) {
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

                    Map<Integer, String> newBatchNo = new HashMap<>();
                    for (Map<String, Object> oneData : getQDataDetail) {
                        //判断仓库不能为空或空格  BY JZMA 20191118
                        String stockWarehouse = oneData.get("WAREHOUSE_DETAIL").toString();
                        if (Check.Null(stockWarehouse) || Check.Null(stockWarehouse.trim())) {
                            this.pData.clear();
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "仓库不能为空或空格");
                        }
                        warehouse_pstockin = stockWarehouse;
                        //单据类型处理
                        String stockDocType = "";
                        String supplierId="";//调拨入库/退配入库/配送入库，supplierId=发货组织编码；其他入库=当前组织编码
                        switch (docType) {
                            case "0":
                                stockDocType = "01";
                                supplierId=transferShop;
                                break;
                            case "1":
                                stockDocType = "02";
                                supplierId=transferShop;
                                break;
                            case "3":
                                stockDocType = "14";
                                supplierId=req.getOrganizationNO();
                                break;
                            case "4"://移仓
                                stockDocType = "19";
                                break;
                            case "5":
                                stockDocType = "42";
                                supplierId=transferShop;
                                break;
                        }
                        String featureNo = oneData.getOrDefault("FEATURENO", " ").toString();
                        if (Check.Null(featureNo)) {
                            featureNo = " ";
                        }
                        String batchNo = oneData.get("BATCH_NO").toString();
                        String item = oneData.get("ITEM").toString();
                        String isBatch = oneData.get("ISBATCH").toString();
                        String batchRules = oneData.get("BATCHRULES").toString();
                        String pluNo = oneData.get("PLUNO").toString();
                        String prodDate = oneData.get("PROD_DATE").toString();
                        String expDate = oneData.get("EXPDATE").toString();
                        if ("Y".equals(isBatchPara) && "Y".equals(isBatch) && Check.Null(batchNo)) {

                            //移仓不断批号
                            batchNo = PosPub.getBatchNo(dao, eId, organizationNO, batchRules, pluNo, featureNo
                                    , prodDate, expDate, req.getOpNO(), req.getOpName(), "",true,stockDocType,stockInNO,"2",supplierId);
                            newBatchNo.put(Integer.parseInt(item), batchNo);
                            //更新上去
                            UptBean ubrd = new UptBean("DCP_STOCKIN_DETAIL");
                            //add Value
                            ubrd.addUpdateValue("BATCH_NO", new DataValue(batchNo, Types.VARCHAR));

                            //condition
                            ubrd.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                            ubrd.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ubrd.addCondition("STOCKINNO", new DataValue(stockInNO, Types.VARCHAR));
                            ubrd.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                            this.addProcessData(new DataProcessBean(ubrd));
                        }

                        //给个空格异动库存
                        if (Check.Null(batchNo)) {
                            batchNo = " ";
                        }

                        BcReq bcReq=new BcReq();
                        bcReq.setServiceType("StockInProcess");
                        bcReq.setDocType(docType);
                        bcReq.setBillType(stockDocType);
                        bcReq.setSyneryDiff(!corp.equals(deliveryCorp));

                        BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                        String bType = bcMap.getBType();
                        String costCode = bcMap.getCostCode();
                        if(Check.Null(bType)||Check.Null(costCode)){
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                        }

                        //不同法人 要传税种
                        //docType=0.配送入库/1.调拨入库/5.退配入库
                        //0 明细  1 明细+在途 5在途
                        //调拨入库有两笔  要transferShop

                        //进货价 = 内部采购进货价DCP_INTERSETTLE_DETAIL.RECEIVEPRICE,
                        //进货金额（含税）=内部采购进货金额DCP_INTERSETTLE_DETAIL.RECEIVEAMT,
                        //未税金额 = 内部采购未税金额DCP_INTERSETTLE_DETAIL.PRETAXAMT,
                        //税额 = 内部采购未税金额DCP_INTERSETTLE_DETAIL.TAXAMT,
                        //税别编码 = 内部采购税别编码DCP_INTERSETTLE_DETAIL.TAXCODE,
                        //税率% = 内部采购税率DCP_INTERSETTLE_DETAIL.TAXRATE;

                        String stockDistriPrice=oneData.getOrDefault("DISTRIPRICE", "0").toString();
                        String stockDistriAmt=oneData.getOrDefault("DISTRIAMT", "0").toString();
                        String stockPreTaxAmt="0";
                        String stockTaxAmt="0";
                        String stockTaxCode="";
                        String stockTaxRate="0";

                        if(!corp.equals(deliveryCorp)&& "S".equals(costCode)){
                            List<Map<String, Object>> filterRows = interData.stream().filter(x -> x.get("ITEM").toString().equals(oneData.get("ITEM").toString())
                                    ).collect(Collectors.toList());
                            if("1".equals(docType)) {
                                filterRows = interData.stream().filter(x -> x.get("ITEM").toString().equals(oneData.get("ITEM").toString())
                                        && x.get("SUPPLYORGNO").toString().equals(oneData.get("TRANSFER_SHOP").toString())).collect(Collectors.toList());
                            }

                            if(filterRows.size()<=0){
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "内部结算明细不存在！");
                            }

                            stockDistriPrice = filterRows.get(0).get("RECEIVEPRICE").toString();
                            stockDistriAmt = filterRows.get(0).get("RECEIVEAMT").toString();
                            stockTaxAmt = filterRows.get(0).get("TAXAMT").toString();
                            stockTaxCode = filterRows.get(0).get("TAXCODE").toString();
                            stockTaxRate = filterRows.get(0).get("TAXRATE").toString();
                            stockPreTaxAmt = filterRows.get(0).get("PRETAXAMT").toString();


                            if (Check.Null(stockDistriAmt) || Check.Null(stockTaxAmt) || Check.Null(stockPreTaxAmt)) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "税额、金额、未税金额不可为空！");
                            }
                            int i = new BigDecimal(stockPreTaxAmt).add(new BigDecimal(stockTaxAmt)).compareTo(new BigDecimal(stockDistriAmt));
                            if (i != 0) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "金额、未税金额、税额不一致！");
                            }
                        }



                        //增加在库数
                        String procedure = "SP_DCP_STOCKCHANGE_V35";
                        Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                        inputParameter.put(1, eId);                              //--企业ID
                        inputParameter.put(2, null);
                        inputParameter.put(3, organizationNO);                           //--组织
                        inputParameter.put(4, bType);
                        inputParameter.put(5, costCode);
                        inputParameter.put(6, stockDocType);                      //--单据类型
                        inputParameter.put(7, stockInNO);                           //--单据号
                        inputParameter.put(8, oneData.get("ITEM").toString());    //--单据行号
                        inputParameter.put(9, "0");
                        inputParameter.put(10, "1");                               //--异动方向 1=加库存 -1=减库存
                        inputParameter.put(11, oneData.get("BDATE").toString());   //--营业日期 yyyy-MM-dd
                        inputParameter.put(12, oneData.get("PLUNO").toString());   //--品号
                        inputParameter.put(13, featureNo); //--特征码
                        inputParameter.put(14, stockWarehouse);            //--仓库
                        inputParameter.put(15, batchNo);     //--批号
                        inputParameter.put(16, oneData.get("MES_LOCATION").toString());     //--location
                        inputParameter.put(17, oneData.get("PUNIT").toString());        //--交易单位
                        inputParameter.put(18, oneData.get("PQTY").toString());         //--交易数量
                        inputParameter.put(19, oneData.get("BASEUNIT").toString());     //--基准单位
                        inputParameter.put(20, oneData.get("BASEQTY").toString());      //--基准数量
                        inputParameter.put(21, oneData.get("UNIT_RATIO").toString());   //--换算比例
                        inputParameter.put(22, oneData.get("PRICE").toString());        //--零售价
                        inputParameter.put(23, oneData.get("AMT").toString());          //--零售金额
                        inputParameter.put(24, stockDistriPrice);  //--进货价
                        inputParameter.put(25, stockDistriAmt);    //--进货金额

                        inputParameter.put(26,stockPreTaxAmt);
                        inputParameter.put(27,stockTaxAmt);
                        inputParameter.put(28,stockTaxCode);
                        inputParameter.put(29,stockTaxRate);

                        inputParameter.put(30, accountDate);                           //--入账日期 yyyy-MM-dd
                        inputParameter.put(31, oneData.get("PROD_DATE").toString());    //--批号的生产日期 yyyy-MM-dd
                        inputParameter.put(32, oneData.get("BDATE").toString());        //--单据日期
                        inputParameter.put(33, "");                                     //--异动原因
                        inputParameter.put(34, "");                                     //--异动描述
                        inputParameter.put(35, req.getOpNO());                          //--操作员

                        ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                        this.addProcessData(new DataProcessBean(pdb));

                        //(调拨收货||移仓入库)&&启用在途
                        if ((docType.equals("1") || docType.equals("4")) && Enable_InTransit.equals("Y")) {
                            //调拨收货不允许修改数量，不会调用此服务，移仓在出库的时候已经审核掉，所以这段代码不应该执行
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "调拨收货或移仓入库调用服务错误，请联系前端排查");
                        }

                        //根据参数AutoStockinDiff处理收货自动产生差异 by knagzc
                        if (docType.equals("0") && !"7".equals(oType)) {
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
                                            "DISTRIAMT", "LOAD_ITEM", "FEATURENO", "TRANSFER_BATCHNO"
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
//                                                keyVal = req.getOrganizationNO(); //20250821 自动审批时公司别切换
                                                keyVal = organizationNO;
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
                                            case 23:
                                                keyVal = oneData.get("TRANSFER_BATCHNO ").toString();
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
                    if (docType.equals("0") && !"7".equals(oType)) {
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
                                        "UPDATE_TIME", "TRAN_TIME"
                                };

                                DataValue[] insValue1 = new DataValue[]
                                        {
                                                new DataValue(shopId, Types.VARCHAR),
                                                new DataValue(organizationNO, Types.VARCHAR),
                                                new DataValue(req.getEmployeeNo(), Types.VARCHAR),
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
                                                new DataValue(req.getEmployeeNo(), Types.VARCHAR),
                                                new DataValue(sDate, Types.VARCHAR),
                                                new DataValue(sTime, Types.VARCHAR),
                                                new DataValue(req.getEmployeeNo(), Types.VARCHAR),
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

                    //在途仓库存异动
                    List<DataProcessBean> newDatabeans = new ArrayList<>();
                    newDatabeans = new TransitStockAdjust().stockInWarehouse(eId,
                            stockInNO,
                            shopId,
                            organizationNO,
                            opNo, newBatchNo);

                    if (!newDatabeans.isEmpty()) {
                        for (DataProcessBean bean : newDatabeans) {
                            addProcessData(bean);
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

            //if (docType.equals("1")) {     //tw_StockIn 0 配送收货 1调拨  3其他入库 4移仓收货  不可能走到的代码段

                //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "调拨收货调用服务错误，请联系前端排查");
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
            //}

            if (docType.equals("0") || docType.equals("2")) {
                //tw_StockIn 0 配送收货 1调拨  3其他入库 4移仓收货  不可能走到的代码段
                // 更新关联收货通知单
                UptBean ub3 = new UptBean("DCP_Receiving");
                // add value
                ub3.addUpdateValue("Status", new DataValue("7", Types.VARCHAR));
                ub3.addUpdateValue("Complete_Date", new DataValue(sDate, Types.VARCHAR));
                // condition
//                ub3.addCondition("OrganizationNO", new DataValue(stransferShop, Types.VARCHAR));
                ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                //ub3.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
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
                    //ub4.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
//                    ub4.addCondition("ORGANIZATIONNO", new DataValue(stransferShop, Types.VARCHAR));
                    ub4.addCondition("RECEIVINGNO", new DataValue(ofNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub4));
                }

            }

            //20240417009 【詹记3.0】轻加工--服务端，轻加工商品收货自动完工入库主单单号20240410010
            if (docType.equals("0") && "2".equals(status)) {
                // 0 不开启自动入库  1 开启后自动入库（但是产生库存流水）
                String ReAutoCompletion = PosPub.getPARA_SMS(dao, eId, organizationNO, "ReAutoCompletion");
                PosPub.writelog("DCP_StockInProcess配送收货单确认，詹记需求，轻加工商品收货自动完工入库参数ReAutoCompletion=" + ReAutoCompletion + ",企业eId=" + eId + ",门店organizationNO=" + organizationNO + ",配送收货单号stockInNO=" + stockInNO + ",收货通知单号ReceivingNo=" + ofNO);
                if ("1".equals(ReAutoCompletion)) {
                    String sql_halfToFinish = getHalfToFinishPluSql(eId, organizationNO, stockInNO);
                    PosPub.writelog("DCP_StockInProcess配送收货单确认，詹记需求，轻加工商品收货自动完工入库,查询资料sql:" + sql_halfToFinish + ",企业eId=" + eId + ",门店organizationNO=" + organizationNO + ",配送收货单号stockInNO=" + stockInNO + ",收货通知单号ReceivingNo=" + ofNO);
                    List<Map<String, Object>> getQData_halfToFinish = this.doQueryData(sql_halfToFinish, null);
                    if (getQData_halfToFinish == null || getQData_halfToFinish.isEmpty()) {
                        PosPub.writelog("DCP_StockInProcess配送收货单确认，詹记需求，轻加工商品收货自动完工入库,查询无数据，无需处理，企业eId=" + eId + ",门店organizationNO=" + organizationNO + ",配送收货单号stockInNO=" + stockInNO + ",收货通知单号ReceivingNo=" + ofNO);
                    } else {
                        PosPub.writelog("DCP_StockInProcess配送收货单确认，詹记需求，轻加工商品收货自动完工入库,查询轻加工商品完成，组装sql语句开始，企业eId=" + eId + ",门店organizationNO=" + organizationNO + ",配送收货单号stockInNO=" + stockInNO + ",收货通知单号ReceivingNo=" + ofNO);
                        String pStockInNO = this.getPStockInNO(eId, organizationNO, accountDate);
                        List<DataProcessBean> DPB = PosPub.getPStockInSql(eId, organizationNO, warehouse_pstockin, pStockInNO, accountDate, opNo, ofNO, getQData_halfToFinish);
                        if (DPB.isEmpty() || DPB.size() < 3) {
                            PosPub.writelog("DCP_StockInProcess配送收货单确认，詹记需求，轻加工商品收货自动完工入库,查询轻加工商品完成，组装sql语句结束，【组装异常】，企业eId=" + eId + ",门店organizationNO=" + organizationNO + ",配送收货单号stockInNO=" + stockInNO + ",收货通知单号ReceivingNo=" + ofNO);
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "轻加工商品收货时自动完工入库异常！");
                        }
                        PosPub.writelog("DCP_StockInProcess配送收货单确认，詹记需求，轻加工商品收货自动完工入库,查询轻加工商品完成，组装sql语句结束，【组装成功】，企业eId=" + eId + ",门店organizationNO=" + organizationNO + ",配送收货单号stockInNO=" + stockInNO + ",收货通知单号ReceivingNo=" + ofNO + ",对应完工入库单号pStockInNO=" + pStockInNO);
                        for (DataProcessBean dpb : DPB) {
                            this.addProcessData(dpb);
                        }
                    }

                }
            }


//            if (docType.equals("5"))
            {
                //docType：5-退配收货(新增)
                //1、退配入库确认更新来源单号签收量：
                //● 根据来源单号OFNO+来源单项次OITEM汇总收货量PQTY更新来源通知单【签收量STOCKIN_QTY】：关联表DCP_RECEIVING_DETAIL，关联条件：根据来源单号OFNO+来源单项次OITEM=RECEIVINGNO+ITEM，
                String sql1 = "select a.ofno,a.oitem,sum(a.pqty) pqty from DCP_STOCKIN_DETAIL a where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "' and a.stockinno='" + stockInNO + "' " +
                        " group by a.ofno,a.oitem ";
                List<Map<String, Object>> groupList1 = this.doQueryData(sql1, null);
                for (Map<String, Object> map : groupList1) {
                    String ofno = map.get("OFNO").toString();
                    String oitem = map.get("OITEM").toString();
                    String pqty = map.get("PQTY").toString();

                    UptBean ubrd = new UptBean("DCP_RECEIVING_DETAIL");
                    //add Value
                    ubrd.addUpdateValue("STOCKIN_QTY", new DataValue(pqty, Types.VARCHAR, DataValue.DataExpression.UpdateSelf));

                    //condition
                    ubrd.addCondition("ITEM", new DataValue(oitem, Types.VARCHAR));
                    ubrd.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ubrd.addCondition("RECEIVINGNO", new DataValue(ofno, Types.VARCHAR));
//                    ubrd.addCondition("organizationNO", new DataValue(stransferShop, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ubrd));
                }

                //● 根据上层来源单号OOFNO+上层来源单项次OITEM汇总收货量PQTY来源出库单【签收量RQTY】,关联表DCP_STOCKOUT_DETAIL，更新条件：
                //  ○ 当上层来源项次OOITEM不为空时，根据根据上层来源单号OOFNO+来源单项次OOITEM关联更新DCP_STOCKOUT_DETAIL.RQTY
                //  ○ 当上层来源项次OOITEM为空时（由于N：1汇总产生收货通知明细而导致无明确的OITEM)，根据【品号+特征码+批号】条件匹配DCP_STOCKOUT_DETAIL，用汇总收货数量依项次从小到大排序回冲签收量；
                List<String> stockOutNoList = new ArrayList();
                String sql2 = "select a.oofno,a.ooitem,sum(a.pqty) pqty from DCP_STOCKIN_DETAIL a where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "' and a.stockinno='" + stockInNO + "'  " +
                        " group by a.oofno,a.ooitem ";
                List<Map<String, Object>> groupList2 = this.doQueryData(sql2, null);
                for (Map<String, Object> map : groupList2) {
                    String oofno = map.get("OOFNO").toString();
                    String oitem = map.get("OOITEM").toString();
                    String pqty = map.get("PQTY").toString();
                    if (Check.Null(oitem)) {
                        continue;
                    }

                    if (!stockOutNoList.contains(oofno) && Check.NotNull(oofno)) {
                        stockOutNoList.add(oofno);
                    }
                    UptBean ubrd = new UptBean("DCP_STOCKOUT_DETAIL");
                    //add Value
                    ubrd.addUpdateValue("RQTY", new DataValue(pqty, Types.VARCHAR, DataValue.DataExpression.UpdateSelf));

                    //condition
                    ubrd.addCondition("ITEM", new DataValue(oitem, Types.VARCHAR));
                    ubrd.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ubrd.addCondition("STOCKOUTNO", new DataValue(oofno, Types.VARCHAR));
//                    ubrd.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ubrd));
                }

                String sql3 = "select a.oofno,a.PLUNO,a.FEATURENO,a.BATCH_NO ,sum(a.pqty) pqty,a.oitem from DCP_STOCKIN_DETAIL a where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "' and a.stockinno='" + stockInNO + "'  " +
                        " group by a.oofno,a.PLUNO,a.FEATURENO,a.BATCH_NO,a.oitem ";
                List<Map<String, Object>> groupList3 = this.doQueryData(sql3, null);
                for (Map<String, Object> map : groupList3) {
                    String pluno = map.get("PLUNO").toString();
                    String featureno = map.get("FEATURENO").toString();
                    String batchno = map.get("BATCH_NO").toString();
                    String pqty = map.get("PQTY").toString();
                    String oofno = map.get("OOFNO").toString();
                    String oitem = map.get("OITEM").toString();
                    if (Check.NotNull(oitem)) {
                        continue;
                    }

                    if (!stockOutNoList.contains(oofno) && Check.NotNull(oofno)) {
                        stockOutNoList.add(oofno);
                    }
                    UptBean ubrd = new UptBean("DCP_STOCKOUT_DETAIL");
                    //add Value
                    ubrd.addUpdateValue("RQTY", new DataValue(pqty, Types.VARCHAR, DataValue.DataExpression.UpdateSelf));

                    //condition
                    ubrd.addCondition("PLUNO", new DataValue(pluno, Types.VARCHAR));
                    ubrd.addCondition("FEATURENO", new DataValue(featureno, Types.VARCHAR));
                    ubrd.addCondition("BATCHNO", new DataValue(batchno, Types.VARCHAR));
                    ubrd.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ubrd.addCondition("STOCKOUTNO", new DataValue(oofno, Types.VARCHAR));
//                    ubrd.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ubrd));
                }


                //2、退配入库确认更新关联出库单状态：
                //● 所有行签收量=出货量，则更新单据状态=3-已签收；
                //● 存在行签收量<>出货量，则更新单据状态=4-部分签收；（没有区分大于或小于）
                //3、来源退配出库单行签收量<>出货量的明细，产生退货组织退货差异申诉单且自动提交并确认单据：

                boolean isAddDiffer = false;
                BigDecimal totCqty = new BigDecimal(0);
                BigDecimal totDistriAmt = new BigDecimal(0);
                int cyitem = 0;
                List detailsFilters = new ArrayList();
                for (String stockOutNo : stockOutNoList) {

                    //查询明细
                    String outSql = "select * from dcp_stockout_detail a where a.eid='" + eId + "'  and a.stockoutno='" + stockOutNo + "' ";
                    List<Map<String, Object>> stockOutDetailList = this.doQueryData(outSql, null);
                    for (Map<String, Object> stockOutDetail : stockOutDetailList) {
                        String item = stockOutDetail.get("ITEM").toString();
                        String pluno = stockOutDetail.get("PLUNO").toString();
                        String featureno = stockOutDetail.get("FEATURENO").toString();
                        String batchno = stockOutDetail.get("BATCH_NO").toString();
                        BigDecimal pqty = new BigDecimal(stockOutDetail.get("PQTY").toString());
                        BigDecimal rqty = new BigDecimal(Check.Null(stockOutDetail.get("RQTY").toString()) ? "0" : stockOutDetail.get("RQTY").toString());
                        List<Map<String, Object>> collect1 = groupList2.stream().filter(x -> x.get("OOFNO").toString().equals(stockOutNo) && x.get("OOITEM").toString().equals(item)).collect(Collectors.toList());
                        for (Map<String, Object> map : collect1) {
                            String pqty1 = map.get("PQTY").toString();
                            rqty = rqty.add(new BigDecimal(pqty1));
                        }

                        List<Map<String, Object>> collect2 = groupList3.stream().filter(x -> x.get("OOFNO").toString().equals(stockOutNo) && x.get("PLUNO").toString().equals(pluno) && x.get("FEATURENO").toString().equals(featureno) && x.get("BATCH_NO").toString().equals(batchno) && Check.Null(x.get("OITEM").toString())).collect(Collectors.toList());
                        for (Map<String, Object> map : collect2) {
                            String pqty1 = map.get("PQTY").toString();
                            rqty = rqty.add(new BigDecimal(pqty1));
                        }

                        if (pqty.compareTo(rqty) != 0) {
                            UptBean ubrd = new UptBean("DCP_STOCKOUT_DETAIL");
                            //add Value
                            ubrd.addUpdateValue("STATUS", new DataValue("4", Types.VARCHAR, DataValue.DataExpression.UpdateSelf));

                            //condition
                            ubrd.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                            ubrd.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ubrd.addCondition("STOCKOUTNO", new DataValue(stockOutNo, Types.VARCHAR));
//                            ubrd.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                            this.addProcessData(new DataProcessBean(ub1));
                        } else {
                            UptBean ubrd = new UptBean("DCP_STOCKOUT_DETAIL");
                            //add Value
                            ubrd.addUpdateValue("STATUS", new DataValue("3", Types.VARCHAR, DataValue.DataExpression.UpdateSelf));

                            //condition
                            ubrd.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                            ubrd.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ubrd.addCondition("STOCKOUTNO", new DataValue(stockOutNo, Types.VARCHAR));
//                            ubrd.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                            this.addProcessData(new DataProcessBean(ub1));
                        }
                    }
                }

            }

//            if (docType.equals("4"))
            {
                //移仓入库
                //更新通知单  或者更新出库单
                UptBean ub3 = new UptBean("DCP_Receiving");
                // add value
                ub3.addUpdateValue("Status", new DataValue("7", Types.VARCHAR));
                ub3.addUpdateValue("Complete_Date", new DataValue(sDate, Types.VARCHAR));
                // condition
                ub3.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub3.addCondition("ReceivingNO", new DataValue(ofNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub3));

                String sql1 = "select a.ofno,a.oitem,sum(a.pqty) pqty from DCP_STOCKIN_DETAIL a where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "' and a.stockinno='" + stockInNO + "' " +
                        " group by a.ofno,a.oitem ";
                List<Map<String, Object>> groupList1 = this.doQueryData(sql1, null);
                for (Map<String, Object> map : groupList1) {
                    String ofno = map.get("OFNO").toString();
                    String oitem = map.get("OITEM").toString();
                    String pqty = map.get("PQTY").toString();

                    UptBean ubrd = new UptBean("DCP_RECEIVING_DETAIL");
                    //add Value
                    ubrd.addUpdateValue("STOCKIN_QTY", new DataValue(pqty, Types.VARCHAR, DataValue.DataExpression.UpdateSelf));

                    //condition
                    ubrd.addCondition("ITEM", new DataValue(oitem, Types.VARCHAR));
                    ubrd.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ubrd.addCondition("RECEIVINGNO", new DataValue(ofno, Types.VARCHAR));
                    ubrd.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ubrd));
                }


                //移仓出入库一次性
                List<String> stockOutNoList = new ArrayList();
                String sql2 = "select a.oofno,b.item2,sum(a.pqty + NVL(b.RQTY,0)) pqty " +
                        " from DCP_STOCKIN_DETAIL a " +
                        " left join DCP_STOCKOUT_BATCH b on a.eid=b.eid and a.oofno=b.STOCKOUTNO and a.ooitem=b.ITEM " +
                        " where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "' and a.stockinno='" + stockInNO + "'  " +
                        " group by a.oofno,b.item2 ";
                List<Map<String, Object>> groupList2 = this.doQueryData(sql2, null);
                for (Map<String, Object> map : groupList2) {
                    String oofno = map.get("OOFNO").toString();
                    String ooitem = map.get("ITEM2").toString();
                    String pqty = map.get("PQTY").toString();

                    if (!stockOutNoList.contains(oofno)) {
                        stockOutNoList.add(oofno);
                    }
                    UptBean ubrd = new UptBean("DCP_STOCKOUT_DETAIL");
                    //add Value
                    ubrd.addUpdateValue("RQTY", new DataValue(pqty, Types.VARCHAR));

                    //condition
                    ubrd.addCondition("ITEM", new DataValue(ooitem, Types.VARCHAR));
                    ubrd.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ubrd.addCondition("STOCKOUTNO", new DataValue(oofno, Types.VARCHAR));
//                    ubrd.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ubrd));
                }

                for (String stockOutNo : stockOutNoList) {
                    UptBean ubr = new UptBean("DCP_STOCKOUT");
                    //add Value
                    ubr.addUpdateValue("STATUS", new DataValue("3", Types.VARCHAR));

                    //condition
                    ubr.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ubr.addCondition("STOCKOUTNO", new DataValue(stockOutNo, Types.VARCHAR));
//                    ubr.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ubr));

                }

            }

            String sql = " select a.oofno,a.OOITEM,SUM(a.PQTY + nvl(RQTY，0)) QTY  " +
                    " from DCP_STOCKIN_DETAIL a" +
                    " left join DCP_STOCKOUT_BATCH c on a.eid=c.eid and a.oofno=c.STOCKOUTNO and a.ooitem=c.ITEM " +
                    " where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "' and a.stockinno='" + stockInNO + "'  " +
                    " group by a.oofno,a.OOITEM ";
            List<Map<String, Object>> groupList = this.doQueryData(sql, null);

            if (CollectionUtils.isNotEmpty(groupList)) {
                for (Map<String, Object> map : groupList) {
                    String oofno = map.get("OOFNO").toString();
                    String ooitem = map.get("OOITEM").toString();

                    String pqty = map.get("QTY").toString();

                    ColumnDataValue dcp_stockout_batch = new ColumnDataValue();
                    ColumnDataValue dcp_stockout_batch_condition = new ColumnDataValue();

                    dcp_stockout_batch.add("RQTY", DataValues.newString(pqty));

                    dcp_stockout_batch_condition.add("STOCKOUTNO", DataValues.newString(oofno));
                    dcp_stockout_batch_condition.add("EID", DataValues.newString(eId));
                    dcp_stockout_batch_condition.add("ITEM", DataValues.newString(ooitem));

                    this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_STOCKOUT_BATCH", dcp_stockout_batch_condition, dcp_stockout_batch)));

                }
            }


            //生成差异  退配收货 配货入库
            String differNo = "";
            String differDocType = "0";
            if ((docType.equals("5") || docType.equals("0")) && !"7".equals(oType)) {

                List<OriOrder> stockOutList = new ArrayList<>();
                String sql2 = "select a.oofno,a.ooitem,sum(a.pqty) pqty from DCP_STOCKIN_DETAIL a where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "' and a.stockinno='" + stockInNO + "'  " +
                        " group by a.oofno,a.ooitem ";

                List<Map<String, Object>> groupList2 = this.doQueryData(sql2, null);
                for (Map<String, Object> map : groupList2) {
                    String oofNo = map.get("OOFNO").toString();
                    String ooItem = map.get("OOITEM").toString();
                    if (StringUtils.isEmpty(oofNo) || StringUtils.isEmpty(ooItem)) {
                        continue;
                    }

                    OriOrder order = new OriOrder();
                    order.setOfNo(oofNo);
                    order.setOItem(ooItem);

                    if (!stockOutList.contains(order) ) {
                        stockOutList.add(order);
                    }
                }

//                List<String> stockOutNoList = new ArrayList();
//                String sql2 = "select a.oofno,a.ooitem,sum(a.pqty) pqty from DCP_STOCKIN_DETAIL a where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "' and a.stockinno='" + stockInNO + "'  " +
//                        " group by a.oofno,a.ooitem ";
//                List<Map<String, Object>> groupList2 = this.doQueryData(sql2, null);
//                for (Map<String, Object> map : groupList2) {
//                    String oofno = map.get("OOFNO").toString();
//                    String oitem = map.get("OOITEM").toString();
//                    if (Check.Null(oitem)) {
//                        continue;
//                    }
//
//                    if (!stockOutNoList.contains(oofno) && Check.NotNull(oofno)) {
//                        stockOutNoList.add(oofno);
//                    }
//                }
//
//                String sql3 = "select a.oofno,a.PLUNO,a.FEATURENO,a.BATCH_NO ,sum(a.pqty) pqty,a.oitem from DCP_STOCKIN_DETAIL a where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "' and a.stockinno='" + stockInNO + "'  " +
//                        " group by a.oofno,a.PLUNO,a.FEATURENO,a.BATCH_NO,a.oitem ";
//                List<Map<String, Object>> groupList3 = this.doQueryData(sql3, null);
//                for (Map<String, Object> map : groupList3) {
//                    String oofno = map.get("OOFNO").toString();
//                    String oitem = map.get("OITEM").toString();
//                    if (Check.NotNull(oitem)) {
//                        continue;
//                    }
//
//                    if (!stockOutNoList.contains(oofno) && Check.NotNull(oofno)) {
//                        stockOutNoList.add(oofno);
//                    }
//                }

                boolean isAddDiffer = false;
                BigDecimal totCqty = new BigDecimal(0);
                BigDecimal totPqty = new BigDecimal(0);
                BigDecimal totDistriAmt = new BigDecimal(0);
                BigDecimal totAmt = new BigDecimal(0);
                int cyitem = 0;
                List<String> detailsFilters = new ArrayList<>();

                String inSql = " SELECT a.* FROM DCP_STOCKIN_DETAIL a where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "' and a.stockinno='" + stockInNO + "'  ";
                List<Map<String, Object>> stockInDetailList = this.doQueryData(inSql, null);

                StringBuilder outSql = new StringBuilder(" SELECT a.*,b.TRANSFER_BATCHNO,b.DISTRIPRICE,b.PRICE FROM DCP_STOCKOUT_BATCH a INNER JOIN DCP_STOCKOUT_DETAIL b ON a.EID=b.EID and a.SHOPID=b.SHOPID and a.STOCKOUTNO=b.STOCKOUTNO and a.ORGANIZATIONNO =b.ORGANIZATIONNO and a.ITEM2=b.ITEM ");
                outSql.append(" WHERE a.EID='").append(eId).append("' ");

                outSql.append(" AND (1=2 ");
                for (OriOrder order : stockOutList) {
                    outSql.append(" OR (a.STOCKOUTNO='").append(order.getOfNo()).append("' AND a.ITEM = '").append(order.getOItem()).append("') ");
                }
                outSql.append(")");
                List<Map<String, Object>> stockOutDetailList = this.doQueryData(outSql.toString(), null);

                for (Map<String, Object> map : stockInDetailList) {

                    String ooItem = map.get("OOITEM").toString();
                    String oofNo = map.get("OOFNO").toString();
                    String pluno = map.get("PLUNO").toString();
                    String featureno = map.get("FEATURENO").toString();

                    String item = map.get("ITEM").toString();

                    Map<String, Object> cond = new HashMap<>();
                    cond.put("STOCKOUTNO", oofNo);
                    cond.put("ITEM", ooItem);
                    List<Map<String, Object>> detailData = MapDistinct.getWhereMap(stockOutDetailList, cond, false);

                    Map<String,Object> stockOutDetail = detailData.get(0);

                    BigDecimal pqty = new BigDecimal(stockOutDetail.get("PQTY").toString());
                    BigDecimal inPqty = new BigDecimal(map.get("PQTY").toString());
//                    BigDecimal rqty = new BigDecimal(Check.Null(stockOutDetail.get("RQTY").toString()) ? "0" : stockOutDetail.get("RQTY").toString());
                    BigDecimal differQty = inPqty.subtract(pqty);

                    String batchNo =   StringUtils.toString(map.get("BATCH_NO").toString()," ");
                    String prod_date = map.get("PROD_DATE").toString();
                    String transfer_batchno = map.get("TRANSFER_BATCHNO").toString();

                    if (differQty.compareTo(BigDecimal.ZERO) != 0) {
                        isAddDiffer = true;

                        totPqty = totPqty.add(differQty);
                        cyitem++;
                        if (Check.Null(differNo)) {
                            differNo = this.getOrderNO(req, "CYSS");
                        }

                        if (!detailsFilters.contains(pluno + featureno)) {
                            detailsFilters.add(pluno + featureno);
                        }
                        BigDecimal price = new BigDecimal(map.get("PRICE").toString());
                        BigDecimal unit_ratio = new BigDecimal(map.get("UNIT_RATIO").toString());
                        String base_unit = map.get("BASEUNIT").toString();
                        String detailWarehouse = map.get("WAREHOUSE").toString();
                        String punit = map.get("PUNIT").toString();
                        BigDecimal distriprice = new BigDecimal(map.get("DISTRIPRICE").toString());

                        ColumnDataValue diffDColumns = new ColumnDataValue();
                        diffDColumns.add("DIFFERENCENO", differNo, Types.VARCHAR);
                        diffDColumns.add("SHOPID", organizationNO, Types.VARCHAR);
                        diffDColumns.add("ITEM", cyitem, Types.VARCHAR);
                        diffDColumns.add("EID", eId, Types.VARCHAR);
                        diffDColumns.add("ORGANIZATIONNO", organizationNO, Types.VARCHAR);
                        diffDColumns.add("PRICE", price.toString(), Types.VARCHAR);
                        diffDColumns.add("UNIT_RATIO", unit_ratio.toString(), Types.VARCHAR);
                        diffDColumns.add("BASEUNIT", base_unit, Types.VARCHAR);
                        diffDColumns.add("PLUNO", pluno, Types.VARCHAR);
                        diffDColumns.add("WAREHOUSE", detailWarehouse, Types.VARCHAR);
                        diffDColumns.add("PUNIT", punit, Types.VARCHAR);
                        diffDColumns.add("OITEM", item, Types.VARCHAR);
                        diffDColumns.add("TRANSFER_BATCHNO", transfer_batchno, Types.VARCHAR);

                        BigDecimal amt = differQty.multiply(price);
                        totAmt = totAmt.add(amt);

                        diffDColumns.add("AMT", amt.toString(), Types.VARCHAR);
                        diffDColumns.add("PQTY", differQty.toString(), Types.VARCHAR);
                        diffDColumns.add("REQ_QTY", differQty.toString(), Types.VARCHAR);

                        BigDecimal baseQty = differQty.multiply(unit_ratio);
                        diffDColumns.add("BASEQTY", baseQty, Types.VARCHAR);
                        diffDColumns.add("DISTRIPRICE", distriprice.toString(), Types.VARCHAR);
                        diffDColumns.add("BATCH_NO", batchNo, Types.VARCHAR);
                        diffDColumns.add("PROD_DATE", prod_date, Types.VARCHAR);
                        BigDecimal distriAmt = differQty.multiply(distriprice);
                        diffDColumns.add("DISTRIAMT", distriAmt.toString(), Types.VARCHAR);
                        diffDColumns.add("FEATURENO", featureno, Types.VARCHAR);

                        totDistriAmt = totDistriAmt.add(distriAmt);

                        String[] setColumnNames = diffDColumns.getColumns().toArray(new String[0]);
                        DataValue[] setDataValues = diffDColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean ib1 = new InsBean("DCP_DIFFERENCE_DETAIL", setColumnNames);
                        ib1.addValues(setDataValues);
                        this.addProcessData(new DataProcessBean(ib1));

                    }
                }

                /*
                for (String stockOutNo : stockOutNoList) {

                    //查询明细
                    String outSql = "select a.*  from DCP_STOCKOUT_DETAIL a " +
                            " where a.eid='" + eId + "'  and a.stockoutno='" + stockOutNo + "' ";
                    List<Map<String, Object>> stockOutDetailList = this.doQueryData(outSql, null);
                    String insql = "select a.oofno,a.ooitem,a.TRANSFER_BATCHNO,EXPDATE,BATCH_NO,PROD_DATE from DCP_STOCKIN_DETAIL a where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "' and a.stockinno='" + stockInNO + "'  ";
                    List<Map<String, Object>> stockInDetailList = this.doQueryData(insql, null);
                    for (Map<String, Object> stockOutDetail : stockOutDetailList) {
                        String item = stockOutDetail.get("ITEM").toString();
                        String pluno = stockOutDetail.get("PLUNO").toString();
                        String featureno = stockOutDetail.get("FEATURENO").toString();

                        Map<String, Object> cond = new HashMap<>();
                        cond.put("OOITEM", item);
                        List<Map<String, Object>> detailData = MapDistinct.getWhereMap(stockInDetailList, cond, false);
                        String batchno = stockOutDetail.get("BATCH_NO").toString();
                        String prod_date = stockOutDetail.get("PROD_DATE").toString();
//                        String expDate = stockOutDetail.get("EXPDATE").toString();
                        String transfer_batchno = stockOutDetail.get("TRANSFER_BATCHNO").toString();

                        BigDecimal pqty = new BigDecimal(stockOutDetail.get("PQTY").toString());
                        BigDecimal rqty = new BigDecimal(Check.Null(stockOutDetail.get("RQTY").toString()) ? "0" : stockOutDetail.get("RQTY").toString());
                        List<Map<String, Object>> collect1 = groupList2.stream().filter(x -> x.get("OOFNO").toString().equals(stockOutNo) && x.get("OOITEM").toString().equals(item)).collect(Collectors.toList());
                        for (Map<String, Object> map : collect1) {
                            String pqty1 = map.get("PQTY").toString();
                            rqty = rqty.add(new BigDecimal(pqty1));
                        }


                        List<Map<String, Object>> collect2 = groupList3.stream().filter(x -> x.get("OOFNO").toString().equals(stockOutNo) && x.get("PLUNO").toString().equals(pluno) && x.get("FEATURENO").toString().equals(featureno) &&
                                x.get("BATCH_NO").toString().equals(batchno) && Check.Null(x.get("OITEM").toString())).collect(Collectors.toList());
                        for (Map<String, Object> map : collect2) {
                            String pqty1 = map.get("PQTY").toString();
                            rqty = rqty.add(new BigDecimal(pqty1));
                        }

                        if (CollectionUtils.isNotEmpty(detailData)) {

                            prod_date = detailData.get(0).get("PROD_DATE").toString();
                            transfer_batchno = detailData.get(0).get("TRANSFER_BATCHNO").toString();
//                            expDate = detailData.get(0).get("EXPDATE").toString();
                        }

                        BigDecimal differQty = rqty.subtract(pqty);
                        if (differQty.compareTo(BigDecimal.ZERO) != 0) {
                            isAddDiffer = true;
                        }

                        String oItem = detailData.get(0).get("ITEM").toString();

                        if (differQty.compareTo(BigDecimal.ZERO) != 0) {
                            totPqty = totPqty.add(differQty);
                            cyitem++;
                            if (Check.Null(differNo)) {
                                differNo = this.getOrderNO(req, "CYSS");
                            }

                            if (!detailsFilters.contains(pluno + featureno)) {
                                detailsFilters.add(pluno + featureno);
                            }
                            BigDecimal price = new BigDecimal(stockOutDetail.get("PRICE").toString());
                            BigDecimal unit_ratio = new BigDecimal(stockOutDetail.get("UNIT_RATIO").toString());
                            String base_unit = stockOutDetail.get("BASEUNIT").toString();
                            String detailWarehouse = stockOutDetail.get("WAREHOUSE").toString();
                            String punit = stockOutDetail.get("PUNIT").toString();
                            BigDecimal distriprice = new BigDecimal(stockOutDetail.get("DISTRIPRICE").toString());

                            ColumnDataValue diffDColumns = new ColumnDataValue();
                            diffDColumns.add("DIFFERENCENO", differNo, Types.VARCHAR);
                            diffDColumns.add("SHOPID", organizationNO, Types.VARCHAR);
                            diffDColumns.add("ITEM", cyitem, Types.VARCHAR);
                            diffDColumns.add("EID", eId, Types.VARCHAR);
                            diffDColumns.add("ORGANIZATIONNO", organizationNO, Types.VARCHAR);
                            diffDColumns.add("PRICE", price.toString(), Types.VARCHAR);
                            diffDColumns.add("UNIT_RATIO", unit_ratio.toString(), Types.VARCHAR);
                            diffDColumns.add("BASEUNIT", base_unit, Types.VARCHAR);
                            diffDColumns.add("PLUNO", pluno, Types.VARCHAR);
                            diffDColumns.add("WAREHOUSE", detailWarehouse, Types.VARCHAR);
                            diffDColumns.add("PUNIT", punit, Types.VARCHAR);
                            diffDColumns.add("OITEM", oItem, Types.VARCHAR);
                            diffDColumns.add("TRANSFER_BATCHNO", transfer_batchno, Types.VARCHAR);

                            BigDecimal amt = differQty.multiply(price);
                            totAmt = totAmt.add(amt);

                            diffDColumns.add("AMT", amt.toString(), Types.VARCHAR);
                            diffDColumns.add("PQTY", differQty.toString(), Types.VARCHAR);
                            diffDColumns.add("REQ_QTY", differQty.toString(), Types.VARCHAR);

                            BigDecimal baseQty = differQty.multiply(unit_ratio);
                            diffDColumns.add("BASEQTY", baseQty, Types.VARCHAR);
                            diffDColumns.add("DISTRIPRICE", distriprice.toString(), Types.VARCHAR);
                            diffDColumns.add("BATCH_NO", batchno, Types.VARCHAR);
                            diffDColumns.add("PROD_DATE", prod_date, Types.VARCHAR);
                            BigDecimal distriAmt = differQty.multiply(distriprice);
                            diffDColumns.add("DISTRIAMT", distriAmt.toString(), Types.VARCHAR);
                            diffDColumns.add("FEATURENO", featureno, Types.VARCHAR);

                            totDistriAmt = totDistriAmt.add(distriAmt);

                            String[] setColumnNames = diffDColumns.getColumns().toArray(new String[0]);
                            DataValue[] setDataValues = diffDColumns.getDataValues().toArray(new DataValue[0]);
                            InsBean ib1 = new InsBean("DCP_DIFFERENCE_DETAIL", setColumnNames);
                            ib1.addValues(setDataValues);
                            this.addProcessData(new DataProcessBean(ib1));
                        }
                    }
                }
                */

                if (isAddDiffer) {
                    totCqty = new BigDecimal(detailsFilters.size());

                    Map<String, Object> stringObjectMap = stockInList.get(0);
                    String warehouse = stringObjectMap.get("WAREHOUSE").toString();
                    String transfer_shop = stringObjectMap.get("TRANSFER_SHOP").toString();
                    String transfer_warehouse = stringObjectMap.get("TRANSFER_WAREHOUSE").toString();
                    String invWarehouse = stringObjectMap.get("INVWAREHOUSE").toString();
                    String load_docno = stringObjectMap.get("LOAD_DOCNO").toString();
                    String load_docType = stringObjectMap.get("LOAD_DOCTYPE").toString();
                    if ("5".equals(docType)) {//退配
                        differDocType = "4";
                    }
                    if ("0".equals(docType)) {//配送
                        differDocType = "3";
                    }
                    ColumnDataValue diffColumns = new ColumnDataValue();
                    diffColumns.add("DIFFERENCENO", differNo, Types.VARCHAR);
                    diffColumns.add("EID", eId, Types.VARCHAR);
                    diffColumns.add("SHOPID", organizationNO, Types.VARCHAR);
                    diffColumns.add("ORGANIZATIONNO", organizationNO, Types.VARCHAR);
                    diffColumns.add("DOC_TYPE", differDocType, Types.VARCHAR);//退配入库
                    diffColumns.add("TOT_CQTY", totCqty.toString(), Types.VARCHAR);
                    diffColumns.add("TOT_PQTY", totPqty.toString(), Types.VARCHAR);
                    diffColumns.add("WAREHOUSE", warehouse, Types.VARCHAR);
                    diffColumns.add("CREATEBY", req.getEmployeeNo(), Types.VARCHAR);
                    diffColumns.add("OFNO", stockInNO, Types.VARCHAR);
                    diffColumns.add("LOAD_DOCNO", load_docno, Types.VARCHAR);
                    diffColumns.add("OTYPE", docType, Types.VARCHAR);
                    diffColumns.add("LOAD_DOCTYPE", load_docType, Types.VARCHAR);
                    diffColumns.add("createType", "0", Types.VARCHAR);
                    diffColumns.add("CREATE_DATE", bDate, Types.VARCHAR);
                    diffColumns.add("CREATE_TIME", createTime, Types.VARCHAR);
                    diffColumns.add("BDATE", bDate, Types.VARCHAR);
                    diffColumns.add("TRANSFER_SHOP", transfer_shop, Types.VARCHAR);
                    diffColumns.add("TOT_DISTRIAMT", totDistriAmt.toString(), Types.VARCHAR);
                    diffColumns.add("TOT_AMT", totAmt.toString(), Types.VARCHAR);
                    diffColumns.add("STATUS", "0", Types.VARCHAR);

                    diffColumns.add("TRANSFER_WAREHOUSE", transfer_warehouse, Types.VARCHAR);
                    diffColumns.add("INVWAREHOUSE", invWarehouse, Types.VARCHAR);

                    String[] differColumnNames = diffColumns.getColumns().toArray(new String[0]);
                    DataValue[] differDataValues = diffColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean ib1 = new InsBean("DCP_DIFFERENCE", differColumnNames);
                    ib1.addValues(differDataValues);
                    this.addProcessData(new DataProcessBean(ib1));
                }
            }

            this.doExecuteDataToDB();

            if (!Check.Null(differNo)) {
                ParseJson pj = new ParseJson();
                //审核差异单
                DCP_DifferenceProcessReq differReq = new DCP_DifferenceProcessReq();
                differReq.setServiceId("DCP_DifferenceProcess");
                differReq.setToken(req.getToken());
                DCP_DifferenceProcessReq.levelElm levelElm = differReq.new levelElm();
                levelElm.setStatus("1");
                levelElm.setDifferenceNo(differNo);
                levelElm.setDocType(differDocType);
                differReq.setRequest(levelElm);

                String jsontemp = pj.beanToJson(differReq);

                DispatchService ds = DispatchService.getInstance();
                String resXml = ds.callService(jsontemp, StaticInfo.dao);
                DCP_DifferenceProcessRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_DifferenceProcessRes>() {
                });
                if (resserver.isSuccess() == false) {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "差异单审核失败！");
                }
            }

            DCP_InterSettleDataProcessReq isdpr = new DCP_InterSettleDataProcessReq();
            isdpr.setServiceId("DCP_InterSettleDataProcess");
            isdpr.setToken(req.getToken());
            isdpr.setRequest(isdpr.new Request());
            isdpr.getRequest().setBillNo(req.getRequest().getStockInNo());
            isdpr.getRequest().setOprType("confirm");
            ServiceAgentUtils<DCP_InterSettleDataProcessReq, DCP_InterSettleDataProcessRes> serviceAgent = new ServiceAgentUtils<>();
            if (!serviceAgent.agentServiceSuccess(isdpr, new TypeToken<DCP_InterSettleDataProcessRes>() {
            })) {
                res.setServiceDescription("调用内部交易结算数据审核/反审核失败");
            }

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            //***********调用库存同步给三方，这是个异步，不会影响效能*****************
            try {
                WebHookService.stockSync(eId, shopId, stockInNO);
            } catch (Exception e) {

            }

        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockInProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockInProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockInProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected TypeToken<DCP_StockInProcessReq> getRequestType() {
        return new TypeToken<DCP_StockInProcessReq>() {
        };
    }

    @Override
    protected DCP_StockInProcessRes getResponseType() {
        return new DCP_StockInProcessRes();
    }

    private String GetDCP_StockIn_Sql(DCP_StockInProcessReq req) throws Exception {
        levelElm request = req.getRequest();
        String organizationNo = req.getOrganizationNO();
        //自动审批时组织切换
        if (StringUtils.isNotEmpty(request.getOrgNo())) {
            organizationNo = request.getOrgNo();
        }
        String sql = ""
                + " select a.STOCKINNO,a.TRANSFER_SHOP,a.BDATE,a.DOC_TYPE,a.OTYPE,a.OFNO,a.MEMO,CREATEBY,CREATE_DATE,CREATE_TIME,"
                + " ACCOUNTBY,ACCOUNT_DATE,ACCOUNT_TIME,LOAD_DOCTYPE,LOAD_DOCNO,b.ITEM,b.OITEM,b.PLUNO,b.PUNIT,b.PQTY,b.BASEUNIT,"
                + " b.UNIT_RATIO,b.BASEQTY,b.PRICE,b.AMT,a.WAREHOUSE AS WAREHOUSE_MAIN,b.WAREHOUSE AS WAREHOUSE_DETAIL, "
                + " b.BATCH_NO,b.PROD_DATE,b.DISTRIPRICE,b.DISTRIAMT,b.featureno,b.RECEIVING_QTY,b.MES_LOCATION,c.isbatch,c.BATCHRULES,b.EXPDATE,a.TRANSFER_SHOP "
                + " from DCP_STOCKIN a "
                + " inner join DCP_STOCKIN_DETAIL b on a.STOCKINNO=b.STOCKINNO and a.EID=b.EID and " +
                " a.Organizationno=b.Organizationno and a.BDATE=b.BDATE " +
                " left join dcp_goods c on c.eid=a.eid and c.pluno=b.pluno "
                + " where a.EID='" + req.geteId() + "'   "
                + " and a.Organizationno='" + organizationNo + "'  "
                + " and a.STOCKINNO='" + request.getStockInNo() + "'";

        return sql;
    }

    private String getHalfToFinishPluSql(String eId, String shop, String stockInNo) throws Exception {
        String sql = " select A.PLUNO,A.FEATURENO,ITEM,OITEM,PUNIT,PQTY,BASEUNIT,UNIT_RATIO,BASEQTY,PRICE,AMT,WAREHOUSE,BATCH_NO,PROD_DATE,DISTRIPRICE,DISTRIAMT from DCP_STOCKIN_DETAIL A"
                + " inner join DCP_LIGHTPROGOODS B on A.EID=B.EID AND A.ORGANIZATIONNO=B.SHOPNO AND A.PLUNO=B.PLUNO  AND A.FEATURENO=B.FEATURENO "
                + " WHERE A.EID='" + eId + "' AND A.Organizationno='" + shop + "' AND A.STOCKINNO='" + stockInNo + "'";

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
                + " where EID=? and receivingNo=? "
        );
        sqlbuf.append(" ) TBL ");


        sql = sqlbuf.toString();

        return sql;
    }

    private String getDifferenceNo(DCP_StockInProcessReq req) throws Exception {
        String DifferenceNo = "";
        String sql = "";
        String shopId = req.getShopId();
        String organizationNO = req.getShopId();
        String eId = req.geteId();
        String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        StringBuffer sqlbuf = new StringBuffer();

        String[] conditionValues = {shopId, eId, organizationNO}; // 查询条件
        String ajustnoHead = "CYSS" + bDate;
        sqlbuf.append("select DIFFERENCENO from (select MAX(DIFFERENCENO) DIFFERENCENO from DCP_DIFFERENCE "
                + " where SHOPID=? "
                + " and EID=? "
                + " and ORGANIZATIONNO=? "
                + " and DIFFERENCENO like '" + ajustnoHead + "%%')");
        sql = sqlbuf.toString();

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

    private String getPStockInNO(String eId, String shopId, String bDate) throws Exception {
        String pStockInNO = "";
        StringBuffer sqlbuf = new StringBuffer();
        pStockInNO = "WGRK" + bDate;
        sqlbuf.append(""
                + " select max(pstockinno) as pstockinno "
                + " from dcp_pstockin where eid = '" + eId + "' and shopid = '" + shopId + "' "
                + " and pstockinno like '%%" + pStockInNO + "%%' "); // 假資料

        List<Map<String, Object>> getQData = this.doQueryData(sqlbuf.toString(), null);
        if (getQData != null && !getQData.isEmpty()) {
            pStockInNO = getQData.get(0).get("PSTOCKINNO").toString();
            if (pStockInNO != null && pStockInNO.length() > 0) {
                long i;
                pStockInNO = pStockInNO.substring(4);
                i = Long.parseLong(pStockInNO) + 1;
                pStockInNO = i + "";
                pStockInNO = "WGRK" + pStockInNO;

            } else {
                pStockInNO = "WGRK" + bDate + "00001";
            }

        } else {
            pStockInNO = "WGRK" + bDate + "00001";
        }

        return pStockInNO;

    }


    @Getter
    @Setter
    @EqualsAndHashCode
    class OriOrder{
        private String ofNo;
        private String oItem;
    }

}
