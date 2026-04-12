package com.dsc.spos.utils.transitStock;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.batchLocation.BatchLocationPlu;
import com.dsc.spos.utils.batchLocation.BatchLocationStockAlloc;
import com.dsc.spos.utils.batchLocation.WarehouseLocationPlu;
import com.dsc.spos.utils.bc.BcReq;
import com.dsc.spos.utils.bc.BcRes;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransitStockAdjust {
    //出库单确认
//    DCP_STOCKOUT单据类型出库确认后按出库明细DCP_STOCK_BATCH生成在途仓库存异动明细，包括：DOC_TYPE=0-退货出库/ 1-调拨出库/ 4-移仓出库 /5-配货出库
//    确认前检查单据在途仓有效性：
//            ● 出库单在途仓INVWAREHOUSE不可为空！（一阶段移仓作业没有在途仓，DCP_STOCKOUT.SOURCEMENU标识移仓作业还是移仓出库）
//            ● 出库单在途仓【所属组织】必须等于出库单【出货组织】，且为【已启用】状态！
//            2.收货通知单生成规则调整：按照DCP_STOCK_BATCHI明细1:1赋值生成收货通知明细（备注：库位固定空格）

    public List<DataProcessBean> stockOutWarehouse(
            String eid,
            String stockOutNo,
            String shopId,
            String orgNo,
            String opNo,
            String dept,
            String receivingNo,
            List<WarehouseLocationPlu> newAllocList
    ) throws Exception {

        DsmDAO dao = StaticInfo.dao;

        List<DataProcessBean> dbs = new ArrayList<>();
        String querySql = " SELECT a.PACKINGNO,a.PTEMPLATENO,a.RDATE,a.DOC_TYPE,a.INVWAREHOUSE,a.BDATE,b.PRICE,b.DISTRIPRICE, " +
                " a.RECEIPT_ORG,a.RECEIPTDATE,a.TRANSFER_WAREHOUSE,a.WAREHOUSE MWAREHOUSE,a.TOT_AMT,a.TOT_PQTY,a.TOT_DISTRIAMT,a.TOT_CQTY, " +
                " b.PLU_BARCODE,b.PLU_MEMO,C.*,nvl(NVL(c.UNITRATIO,b.UNIT_RATIO),1) UNIT_RATIO,ro.IN_COST_WAREHOUSE,b.bsno " +
                "  FROM DCP_STOCKOUT a " +
                " INNER JOIN DCP_STOCKOUT_DETAIL b ON a.EID=b.EID and a.SHOPID=b.SHOPID and a.STOCKOUTNO=b.STOCKOUTNO and a.ORGANIZATIONNO=b.ORGANIZATIONNO " +
                " INNER JOIN DCP_STOCKOUT_BATCH c ON b.eid=c.eid and b.ORGANIZATIONNO=c.ORGANIZATIONNO and c.SHOPID=b.SHOPID and b.STOCKOUTNO=c.STOCKOUTNO and c.ITEM2=b.ITEM  " +
                " LEFT JOIN DCP_ORG ro on ro.eid=a.eid and ro.ORGANIZATIONNO=a.RECEIPT_ORG  " +
                " WHERE a.EID = '" + eid + "' AND a.ORGANIZATIONNO = '" + orgNo + "' AND a.STOCKOUTNO='" + stockOutNo + "'  ";

        List<Map<String, Object>> qData = dao.executeQuerySQL(querySql, null);
        if (qData != null && !qData.isEmpty()) {

            //库位分配
            List<BatchLocationPlu> batchLocationPlus = new ArrayList<>();
            for (Map<String, Object> singleBatch : qData) {
                BatchLocationPlu onePlu = new BatchLocationPlu();
                onePlu.setId(Integer.parseInt(singleBatch.get("ITEM").toString()));
                onePlu.setWarehouse(singleBatch.get("WAREHOUSE").toString());
                onePlu.setPQty(singleBatch.get("PQTY").toString());
                onePlu.setPUnit(singleBatch.get("PUNIT").toString());
                onePlu.setPluNo(singleBatch.get("PLUNO").toString());
                onePlu.setFeatureNo(singleBatch.get("FEATURENO").toString());

                onePlu.setBatchNo(singleBatch.get("BATCHNO").toString());
                //String batchNo = singleBatch.get("TRANSFER_BATCHNO").toString();
                //if (StringUtils.isEmpty(batchNo)){
                //    batchNo = singleBatch.get("BATCHNO").toString();
                //}
                //onePlu.setBatchNo(batchNo);
                onePlu.setLocation(singleBatch.get("LOCATION").toString());

                batchLocationPlus.add(onePlu);
            }

            List<WarehouseLocationPlu> allocList = BatchLocationStockAlloc.batchLocationStockAlloc(batchLocationPlus);

            Map<String, Object> oneData = qData.get(0);
            String docType = oneData.get("DOC_TYPE").toString();
            String invWarehouse = oneData.get("INVWAREHOUSE").toString();
            String receiptOrg = oneData.get("RECEIPT_ORG").toString();
            String organizationNO = oneData.get("ORGANIZATIONNO").toString();
            String warehouse = oneData.get("TRANSFER_WAREHOUSE").toString();
            if (StringUtils.isEmpty(warehouse)) {
                warehouse = oneData.get("IN_COST_WAREHOUSE").toString();
            }

            if (StringUtils.isNotEmpty(invWarehouse)) {
                ColumnDataValue dcp_receiving = new ColumnDataValue();

                if (StringUtils.isNotEmpty(receivingNo) && !StringUtils.equals(receiptOrg, organizationNO)) {

                    dcp_receiving.add("EID", DataValues.newString(oneData.get("EID").toString()));
                    dcp_receiving.add("ORGANIZATIONNO", DataValues.newString(oneData.get("ORGANIZATIONNO").toString()));
                    dcp_receiving.add("SHOPID", DataValues.newString(oneData.get("SHOPID").toString()));
                    dcp_receiving.add("RECEIVINGNO", DataValues.newString(receivingNo));
                    dcp_receiving.add("BDATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));

                    String rDocType = "0";
                    switch (docType) {
                        case "5":
                            rDocType = "0";
                            break;
                        case "0":
                            rDocType = "5";
                            break;
                        case "1":
                            rDocType = "1";
                            break;
                        case "4":
                            rDocType = "7";
                            break;
                    }
                    dcp_receiving.add("DOC_TYPE", DataValues.newString(rDocType));
                    dcp_receiving.add("LOAD_DOCNO", DataValues.newString(oneData.get("STOCKOUTNO").toString()));
                    dcp_receiving.add("RECEIPTDATE", DataValues.newString(oneData.get("RECEIPTDATE").toString()));
                    dcp_receiving.add("TOT_CQTY", DataValues.newString(oneData.get("TOT_CQTY").toString()));
                    dcp_receiving.add("TOT_PQTY", DataValues.newString(oneData.get("TOT_PQTY").toString()));
                    dcp_receiving.add("TOT_AMT", DataValues.newString(oneData.get("TOT_AMT").toString()));
                    dcp_receiving.add("TOT_DISTRIAMT", DataValues.newString(oneData.get("TOT_DISTRIAMT").toString()));
                    dcp_receiving.add("WAREHOUSE", DataValues.newString(warehouse));
                    dcp_receiving.add("STATUS", DataValues.newString(6));
                    dcp_receiving.add("RDATE", DataValues.newString(oneData.get("RDATE").toString()));
                    dcp_receiving.add("RECEIPTDATE", DataValues.newString(oneData.get("RECEIPTDATE").toString()));
                    dcp_receiving.add("EMPLOYEEID", DataValues.newString(opNo));
                    dcp_receiving.add("CREATEBY", DataValues.newString(opNo));
                    dcp_receiving.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
                    dcp_receiving.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));
                    dcp_receiving.add("DEPARTID", DataValues.newString(dept));
                    dcp_receiving.add("TRANSFER_SHOP", DataValues.newString(oneData.get("ORGANIZATIONNO").toString()));
                    dcp_receiving.add("TRANSFER_WAREHOUSE", DataValues.newString(oneData.get("MWAREHOUSE").toString()));
                    dcp_receiving.add("PTEMPLATENO", DataValues.newString(oneData.get("PTEMPLATENO").toString()));
                    dcp_receiving.add("PACKINGNO", DataValues.newString(oneData.get("PACKINGNO").toString()));
                    dcp_receiving.add("OTYPE", DataValues.newString(oneData.get("DOC_TYPE").toString()));
                    dcp_receiving.add("OFNO", DataValues.newString(oneData.get("STOCKOUTNO").toString()));

                    if (StringUtils.isEmpty(receiptOrg)) {
                        dcp_receiving.add("RECEIPTORGNO", DataValues.newString(oneData.get("ORGANIZATIONNO").toString())); //收货组织为空时默认当前组织
                    } else {
                        dcp_receiving.add("RECEIPTORGNO", DataValues.newString(oneData.get("RECEIPT_ORG").toString()));
                    }

                    dcp_receiving.add("INVWAREHOUSE", DataValues.newString(oneData.get("INVWAREHOUSE").toString()));

                    dbs.add(new DataProcessBean(DataBeans.getInsBean("dcp_receiving", dcp_receiving)));
                }

                int item = 1;
                for (Map<String, Object> map : qData) {

                    //增加库位分配保持和新的 DCP_STOCKOUT_BATCH 表一致
                    String thisItem = map.get("ITEM").toString();
                    String newItem = map.get("ITEM").toString();
                    List<WarehouseLocationPlu> filterAlloc = allocList.stream().filter(x -> String.valueOf(x.getId()).equals(thisItem)).collect(Collectors.toList());

                    if ("4".equals(docType) || "5".equals(docType) ) {  //出库确认中只有4和5进行了分配，因此这边和出库确认保持一致
                        for (WarehouseLocationPlu warehouseLocation : filterAlloc) {

                            List<Object> inputParameter = Lists.newArrayList();

                            double amt = BigDecimalUtils.mul(Double.parseDouble(map.get("PRICE").toString()),
                                    Double.parseDouble(warehouseLocation.getAllocQty()), 2);
                            double distriAmt = BigDecimalUtils.mul(Double.parseDouble(map.get("DISTRIPRICE").toString()),
                                    Double.parseDouble(warehouseLocation.getAllocQty()), 2);
                            double baseQty = BigDecimalUtils.mul(Double.parseDouble(warehouseLocation.getAllocQty()), Double.parseDouble(map.get("UNIT_RATIO").toString()));

                            for (WarehouseLocationPlu newPlu : newAllocList) {
                                if (newPlu.equals(warehouseLocation)) {
                                    newItem = String.valueOf(newPlu.getNewId());
                                    break;
                                }

                            }

                            BcReq bcReq=new BcReq();
                            bcReq.setServiceType("StockOutProcess");
                            bcReq.setDocType("");//在途加 12
                            bcReq.setBillType("12");

                            BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                            String bType = bcMap.getBType();
                            String costCode = bcMap.getCostCode();
                            if(Check.Null(bType)||Check.Null(costCode)){
                                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                            }

                            inputParameter.add(eid);                                                     //P_EID		IN	VARCHAR2,	--企业ID
                            inputParameter.add(null);
                            inputParameter.add(orgNo);                                          //P_OrgID		IN	VARCHAR2,	--组织
                            inputParameter.add(bType);
                            inputParameter.add(costCode);
                            inputParameter.add("12");                                                                   //P_BillType	IN	VARCHAR2,	--单据类型
                            inputParameter.add(stockOutNo);                                                //P_BillNo	IN	VARCHAR2,	--单据号
                            inputParameter.add(newItem);                                                   //P_Item		IN	INTEGER,	--单据行号
                            inputParameter.add("0");
                            inputParameter.add(1);                                                                     //P_Direct	IN	INTEGER,	--异动方向 1=加库存 -1=减库存
                            inputParameter.add(map.get("BDATE").toString());                                                   //P_BDate		IN	VARCHAR2,	--营业日期 yyyy-MM-dd
                            inputParameter.add(map.get("PLUNO").toString());                                          //P_PluNo		IN	VARCHAR2,	--品号
                            inputParameter.add(StringUtils.toString(map.get("FEATURENO"), " "));          //P_FeatureNo	IN	VARCHAR2,	--特征码
                            inputParameter.add(map.get("INVWAREHOUSE").toString());                                              //P_WAREHOUSE	IN	VARCHAR2,	--仓库
                            inputParameter.add(StringUtils.toString(warehouseLocation.getBatchNo(), " "));        //P_BATCHNO	IN	VARCHAR2,	--批号
                            inputParameter.add(" ");       //P_LOCATION IN VARCHAR2,	--库位
                            inputParameter.add(map.get("PUNIT").toString());                                           //P_SUnit		IN	VARCHAR2,	--交易单位
                            inputParameter.add(warehouseLocation.getAllocQty());                                            //P_Qty		IN	NUMBER,		--交易数量
                            inputParameter.add(map.get("BASEUNIT").toString());                                                //P_BASEUNIT	IN	VARCHAR2,	--基准单位
                            inputParameter.add(baseQty);                                                 //P_BASEQTY	IN	NUMBER,		--基准数量
                            inputParameter.add(map.get("UNIT_RATIO").toString());                                              //P_UNITRATIO	IN	NUMBER,		--换算比例
                            inputParameter.add(map.get("PRICE").toString());                                                   //P_Price		IN	NUMBER,		--零售价
                            inputParameter.add(amt);                                                     //P_Amt		IN	NUMBER,		--零售金额
                            inputParameter.add(oneData.get("DISTRIPRICE"));                                             //P_DISTRIPRICE	IN	NUMBER,		--进货价
                            inputParameter.add(distriAmt);                                               //P_DISTRIAMT	IN	NUMBER,		--进货金额
                            inputParameter.add(DateFormatUtils.getDate(oneData.get("BDATE").toString()));               //P_ACCOUNTDATE	IN	VARCHAR2,	--入账日期 yyyy-MM-dd
                            inputParameter.add(DateFormatUtils.getDate(oneData.get("PRODDATE").toString()));            //P_PRODDATE	IN	VARCHAR2,	--批号的生产日期 yyyy-MM-dd
                            inputParameter.add(DateFormatUtils.getDate(oneData.get("BDATE").toString()));               //P_BillDate	IN	VARCHAR2,	--单据日期 yyyy-MM-dd
                            inputParameter.add("");                                           //P_ReasonID	IN 	VARCHAR2,	--异动原因
                            inputParameter.add("在途库存调整");                                                                     //P_Description	IN 	VARCHAR2,	--异动描述
                            inputParameter.add(opNo);                                                    //P_UserID	IN	VARCHAR2	--操作员

                            dbs.add(new DataProcessBean(DataBeans.getProcedureBean("SP_DCP_STOCKCHANGE_VX", inputParameter)));

                            if (StringUtils.isNotEmpty(receivingNo) && !StringUtils.equals(receiptOrg, organizationNO)) {
                                ColumnDataValue dcp_receiving_detail = new ColumnDataValue();
                                dcp_receiving_detail.add("EID", DataValues.newString(map.get("EID").toString()));
                                dcp_receiving_detail.add("ORGANIZATIONNO", DataValues.newString(map.get("ORGANIZATIONNO").toString()));
                                dcp_receiving_detail.add("SHOPID", DataValues.newString(map.get("SHOPID").toString()));
                                dcp_receiving_detail.add("RECEIVINGNO", DataValues.newString(receivingNo));

                                dcp_receiving_detail.add("BDATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
                                dcp_receiving_detail.add("ITEM", DataValues.newString(item++));
                                dcp_receiving_detail.add("PLU_BARCODE", DataValues.newString(map.get("PLU_BARCODE").toString()));
                                dcp_receiving_detail.add("PLUNO", DataValues.newString(map.get("PLUNO").toString()));
                                dcp_receiving_detail.add("FEATURENO", DataValues.newString(map.get("FEATURENO").toString()));
                                dcp_receiving_detail.add("PUNIT", DataValues.newString(map.get("PUNIT").toString()));
                                dcp_receiving_detail.add("PQTY", DataValues.newString(warehouseLocation.getAllocQty()));
                                dcp_receiving_detail.add("PRICE", DataValues.newString(map.get("PRICE").toString()));
                                dcp_receiving_detail.add("AMT", DataValues.newString(amt));
                                dcp_receiving_detail.add("DISTRIPRICE", DataValues.newString(map.get("DISTRIPRICE").toString()));
                                dcp_receiving_detail.add("DISTRIAMT", DataValues.newString(distriAmt));

                                dcp_receiving_detail.add("WAREHOUSE", DataValues.newString(warehouse));
                                dcp_receiving_detail.add("BASEQTY", DataValues.newString(baseQty));
                                dcp_receiving_detail.add("BASEUNIT", DataValues.newString(map.get("BASEUNIT").toString()));
                                dcp_receiving_detail.add("UNIT_RATIO", DataValues.newString(map.get("UNIT_RATIO").toString()));
                                dcp_receiving_detail.add("OTYPE", DataValues.newString(map.get("DOC_TYPE").toString()));
                                dcp_receiving_detail.add("OFNO", DataValues.newString(map.get("STOCKOUTNO").toString()));
                                dcp_receiving_detail.add("OITEM", DataValues.newString(newItem));
                                dcp_receiving_detail.add("PLU_MEMO", DataValues.newString(map.get("PLU_MEMO").toString()));
                                dcp_receiving_detail.add("BATCH_NO", DataValues.newString(warehouseLocation.getBatchNo()));
                                dcp_receiving_detail.add("PACKINGNO", DataValues.newString(map.get("PACKINGNO").toString()));
                                dcp_receiving_detail.add("STATUS", DataValues.newString(0));
                                dcp_receiving_detail.add("OITEM2", DataValues.newString(map.get("ITEM2").toString()));

                                dcp_receiving_detail.add("PROD_DATE", DataValues.newString(DateFormatUtils.getPlainDate(warehouseLocation.getProdDate())));
                                dcp_receiving_detail.add("EXPDATE", DataValues.newString(DateFormatUtils.getPlainDate(warehouseLocation.getValidDate())));
                                dcp_receiving_detail.add("BSNO", DataValues.newString(map.get("BSNO").toString()));

                                dbs.add(new DataProcessBean(DataBeans.getInsBean("dcp_receiving_detail", dcp_receiving_detail)));

                            }
                        }
                    } else {
                        List<Object> inputParameter = Lists.newArrayList();

                        double amt = BigDecimalUtils.mul(Double.parseDouble(map.get("PRICE").toString()), Double.parseDouble(map.get("PQTY").toString()), 2);
                        double distriAmt = BigDecimalUtils.mul(Double.parseDouble(map.get("DISTRIPRICE").toString()), Double.parseDouble(map.get("PQTY").toString()), 2);
                        double baseQty = BigDecimalUtils.mul(Double.parseDouble(map.get("PQTY").toString()), Double.parseDouble(map.get("UNIT_RATIO").toString()));

                        BcReq bcReq=new BcReq();
                        bcReq.setServiceType("StockOutProcess");
                        bcReq.setDocType("");//在途加 12
                        bcReq.setBillType("12");

                        BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                        String bType = bcMap.getBType();
                        String costCode = bcMap.getCostCode();
                        if(Check.Null(bType)||Check.Null(costCode)){
                            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                        }

                        inputParameter.add(eid);                                                     //P_EID		IN	VARCHAR2,	--企业ID
                        inputParameter.add(null);
                        inputParameter.add(orgNo);                                          //P_OrgID		IN	VARCHAR2,	--组织
                        inputParameter.add(bType);
                        inputParameter.add(costCode);
                        inputParameter.add("12");                                                                   //P_BillType	IN	VARCHAR2,	--单据类型
                        inputParameter.add(stockOutNo);                                                //P_BillNo	IN	VARCHAR2,	--单据号
                        inputParameter.add(newItem);                                                   //P_Item		IN	INTEGER,	--单据行号
                        inputParameter.add("0");
                        inputParameter.add(1);                                                                     //P_Direct	IN	INTEGER,	--异动方向 1=加库存 -1=减库存
                        inputParameter.add(map.get("BDATE").toString());                                                   //P_BDate		IN	VARCHAR2,	--营业日期 yyyy-MM-dd
                        inputParameter.add(map.get("PLUNO").toString());                                          //P_PluNo		IN	VARCHAR2,	--品号
                        inputParameter.add(StringUtils.toString(map.get("FEATURENO"), " "));          //P_FeatureNo	IN	VARCHAR2,	--特征码
                        inputParameter.add(map.get("INVWAREHOUSE").toString());                                              //P_WAREHOUSE	IN	VARCHAR2,	--仓库
                        inputParameter.add(StringUtils.toString(map.get("BATCHNO"), " "));        //P_BATCHNO	IN	VARCHAR2,	--批号
                        inputParameter.add(" ");       //P_LOCATION IN VARCHAR2,	--库位
                        inputParameter.add(map.get("PUNIT").toString());                                           //P_SUnit		IN	VARCHAR2,	--交易单位
                        inputParameter.add(map.get("PQTY").toString());                                            //P_Qty		IN	NUMBER,		--交易数量
                        inputParameter.add(map.get("BASEUNIT").toString());                                                //P_BASEUNIT	IN	VARCHAR2,	--基准单位
                        inputParameter.add(baseQty);                                                 //P_BASEQTY	IN	NUMBER,		--基准数量
                        inputParameter.add(map.get("UNIT_RATIO").toString());                                              //P_UNITRATIO	IN	NUMBER,		--换算比例
                        inputParameter.add(map.get("PRICE").toString());                                                   //P_Price		IN	NUMBER,		--零售价
                        inputParameter.add(amt);                                                     //P_Amt		IN	NUMBER,		--零售金额
                        inputParameter.add(oneData.get("DISTRIPRICE"));                                             //P_DISTRIPRICE	IN	NUMBER,		--进货价
                        inputParameter.add(distriAmt);                                               //P_DISTRIAMT	IN	NUMBER,		--进货金额
                        inputParameter.add(DateFormatUtils.getDate(oneData.get("BDATE").toString()));               //P_ACCOUNTDATE	IN	VARCHAR2,	--入账日期 yyyy-MM-dd
                        inputParameter.add(DateFormatUtils.getDate(oneData.get("PRODDATE").toString()));            //P_PRODDATE	IN	VARCHAR2,	--批号的生产日期 yyyy-MM-dd
                        inputParameter.add(DateFormatUtils.getDate(oneData.get("BDATE").toString()));               //P_BillDate	IN	VARCHAR2,	--单据日期 yyyy-MM-dd
                        inputParameter.add("");                                           //P_ReasonID	IN 	VARCHAR2,	--异动原因
                        inputParameter.add("在途库存调整");                                                                     //P_Description	IN 	VARCHAR2,	--异动描述
                        inputParameter.add(opNo);                                                    //P_UserID	IN	VARCHAR2	--操作员

                        dbs.add(new DataProcessBean(DataBeans.getProcedureBean("SP_DCP_STOCKCHANGE_VX", inputParameter)));

                        if (StringUtils.isNotEmpty(receivingNo) && !StringUtils.equals(receiptOrg, organizationNO)) {
                            ColumnDataValue dcp_receiving_detail = new ColumnDataValue();
                            dcp_receiving_detail.add("EID", DataValues.newString(map.get("EID").toString()));
                            dcp_receiving_detail.add("ORGANIZATIONNO", DataValues.newString(map.get("ORGANIZATIONNO").toString()));
                            dcp_receiving_detail.add("SHOPID", DataValues.newString(map.get("SHOPID").toString()));
                            dcp_receiving_detail.add("RECEIVINGNO", DataValues.newString(receivingNo));

                            dcp_receiving_detail.add("BDATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
                            dcp_receiving_detail.add("ITEM", DataValues.newString(item++));
                            dcp_receiving_detail.add("PLU_BARCODE", DataValues.newString(map.get("PLU_BARCODE").toString()));
                            dcp_receiving_detail.add("PLUNO", DataValues.newString(map.get("PLUNO").toString()));
                            dcp_receiving_detail.add("FEATURENO", DataValues.newString(map.get("FEATURENO").toString()));
                            dcp_receiving_detail.add("PUNIT", DataValues.newString(map.get("PUNIT").toString()));
                            dcp_receiving_detail.add("PQTY", DataValues.newString(map.get("PQTY").toString()));
                            dcp_receiving_detail.add("PRICE", DataValues.newString(map.get("PRICE").toString()));
                            dcp_receiving_detail.add("AMT", DataValues.newString(amt));
                            dcp_receiving_detail.add("DISTRIPRICE", DataValues.newString(map.get("DISTRIPRICE").toString()));
                            dcp_receiving_detail.add("DISTRIAMT", DataValues.newString(distriAmt));

                            dcp_receiving_detail.add("WAREHOUSE", DataValues.newString(warehouse));
                            dcp_receiving_detail.add("BASEQTY", DataValues.newString(baseQty));
                            dcp_receiving_detail.add("BASEUNIT", DataValues.newString(map.get("BASEUNIT").toString()));
                            dcp_receiving_detail.add("UNIT_RATIO", DataValues.newString(map.get("UNIT_RATIO").toString()));
                            dcp_receiving_detail.add("OTYPE", DataValues.newString(map.get("DOC_TYPE").toString()));
                            dcp_receiving_detail.add("OFNO", DataValues.newString(map.get("STOCKOUTNO").toString()));
                            dcp_receiving_detail.add("OITEM", DataValues.newString(newItem));
                            dcp_receiving_detail.add("PLU_MEMO", DataValues.newString(map.get("PLU_MEMO").toString()));
                            dcp_receiving_detail.add("BATCH_NO", DataValues.newString(map.get("BATCHNO").toString()));
                            dcp_receiving_detail.add("PACKINGNO", DataValues.newString(map.get("PACKINGNO").toString()));
                            dcp_receiving_detail.add("STATUS", DataValues.newString(0));
                            dcp_receiving_detail.add("OITEM2", DataValues.newString(map.get("ITEM2").toString()));
                            dcp_receiving_detail.add("PROD_DATE", DataValues.newString(map.get("PRODDATE").toString()));
                            dcp_receiving_detail.add("EXPDATE", DataValues.newString(map.get("EXPDATE").toString()));
                            dcp_receiving_detail.add("BSNO", DataValues.newString(map.get("BSNO").toString()));
                            dbs.add(new DataProcessBean(DataBeans.getInsBean("dcp_receiving_detail", dcp_receiving_detail)));

                        }
                    }

                }

            }
        }

        return dbs;
    }


    //入库单确认，核销在途库存
//    ● +收货仓库存
//    ● -在途仓库存（在途仓库存核销）
    public List<DataProcessBean> stockInWarehouse(
            String eid,
            String stockInNo,
            String shopId,
            String orgNo,
            String opNo,
            Map<Integer, String> newBatchNo
    ) throws Exception {

        DsmDAO dao = StaticInfo.dao;

        List<DataProcessBean> dbs = new ArrayList<>();
        /*
        String querySql = " SELECT d.*,a.DOC_TYPE,b.PRICE,b.DISTRIPRICE " +
                "  ,a.INVWAREHOUSE,a.BDATE,a.ACCOUNT_DATE,NVL(uc1.UNIT_RATIO,1) UNIT_RATIO " +
                "  ,c.ORGANIZATIONNO AS OOORGANIZATIONNO,DQTY " +
                " FROM DCP_STOCKOUT_BATCH d  " +
                " LEFT JOIN DCP_STOCKOUT_DETAIL c ON d.eid=c.eid and d.ORGANIZATIONNO=c.ORGANIZATIONNO and c.SHOPID=d.SHOPID and d.STOCKOUTNO=c.STOCKOUTNO and d.ITEM2=c.ITEM " +
                " LEFT JOIN ( " +
                "            SELECT DISTINCT eid,SHOPID,STOCKINNO,ORGANIZATIONNO,oofno,OOITEM,PRICE,DISTRIPRICE,SUM(PQTY) DQTY " +
                "            FROM DCP_STOCKIN_DETAIL GROUP BY eid,SHOPID,STOCKINNO,ORGANIZATIONNO,oofno,OOITEM,PRICE,DISTRIPRICE ) b on b.eid=c.eid and b.oofno=c.STOCKOUTNO and b.OOITEM=d.ITEM " +
                " LEFT JOIN DCP_STOCKIN a ON a.EID=b.EID and a.SHOPID=b.SHOPID and a.STOCKINNO=b.STOCKINNO and a.ORGANIZATIONNO=b.ORGANIZATIONNO " +
                " LEFT JOIN DCP_UNITCONVERT uc1 on uc1.eid=d.eid and uc1.OUNIT=d.PUNIT AND uc1.UNIT=d.BASEUNIT " +
                " LEFT JOIN DCP_ORG ro on ro.eid=a.eid and ro.ORGANIZATIONNO=a.ORGANIZATIONNO  " +
                " WHERE a.EID = '" + eid + "' AND a.ORGANIZATIONNO = '" + orgNo + "' AND a.STOCKINNO='" + stockInNo + "'  ";
        */
        String querySql = " SELECT b.*,a.DOC_TYPE,b.PRICE,b.DISTRIPRICE " +
                "  ,a.INVWAREHOUSE,a.BDATE,a.ACCOUNT_DATE " +
                "  ,c.ORGANIZATIONNO AS OOORGANIZATIONNO,d.PQTY OQTY,d.BATCHNO,a.DELIVERYCORP,a.corp,a.DOC_TYPE,a.TRANSFER_SHOP " +
                "  FROM DCP_STOCKIN a  " +
                "  INNER JOIN DCP_STOCKIN_DETAIL b ON a.EID=b.EID and a.SHOPID=b.SHOPID and a.STOCKINNO=b.STOCKINNO and a.ORGANIZATIONNO=b.ORGANIZATIONNO " +
                "  LEFT JOIN DCP_STOCKOUT_BATCH d on b.eid = d.eid and b.oofno = d.STOCKOUTNO and b.OOITEM = d.ITEM " +
                "  LEFT JOIN DCP_STOCKOUT_DETAIL c ON d.eid=c.eid and d.ORGANIZATIONNO=c.ORGANIZATIONNO and c.SHOPID=d.SHOPID and d.STOCKOUTNO=c.STOCKOUTNO and d.ITEM2=c.ITEM" +
                "  LEFT JOIN DCP_ORG ro on ro.eid=a.eid and ro.ORGANIZATIONNO=a.ORGANIZATIONNO  " +
                " WHERE a.EID = '" + eid + "' AND a.ORGANIZATIONNO = '" + orgNo + "' AND a.STOCKINNO='" + stockInNo + "'  ";
        List<Map<String, Object>> qData = dao.executeQuerySQL(querySql, null);

        //获取内部结算数据
        String interSql="select * from DCP_INTERSETTLE_DETAIL a where a.eid='"+eid+"' " +
                " and a.organizationno='"+orgNo+"' and a.billno='"+stockInNo+"' ";
        List<Map<String, Object>> interData = dao.executeQuerySQL(interSql, null);

        if (qData != null && !qData.isEmpty()) {

            String deliveryCorp = qData.get(0).get("DELIVERYCORP").toString();
            String corp = qData.get(0).get("CORP").toString();
            String doc_type = qData.get(0).get("DOC_TYPE").toString();


            Map<String, Boolean> condition = new HashMap<>();
            condition.put("OOFNO", true);
            condition.put("OOITEM", true);
            List<Map<String, Object>> oData = MapDistinct.getMap(qData, condition);

            //调拨入库 后面加btype  costcode
            for (Map<String, Object> oneData : oData) {
                String invWarehouse = oneData.get("INVWAREHOUSE").toString();

                String dbTBType="";//调拨入库在途仓库存btype
                if("1".equals(doc_type)) {
                    List<Map<String, Object>> filterRows = interData.stream().filter(x -> x.get("ITEM").toString().equals(oneData.get("ITEM").toString())
                            && x.get("SUPPLYORGNO").toString().equals(oneData.get("TRANSFER_SHOP").toString())).collect(Collectors.toList());
                    if (filterRows.size() > 0) {
                        dbTBType = filterRows.get(0).get("BTYPE").toString();
                    }
                }

                if (StringUtils.isNotEmpty(invWarehouse)) {
                    Map<String, Object> cond = new HashMap<>();
                    cond.put("OOFNO", oneData.get("OOFNO"));
                    cond.put("OOITEM", oneData.get("OOITEM"));
                    List<Map<String, Object>> whereList = MapDistinct.getWhereMap(qData, cond, true);

                    String ooOrganizationNo = oneData.get("OOORGANIZATIONNO").toString();
                    double pQty = Double.parseDouble(oneData.get("OQTY").toString()); //发货量
                    double nowQty = 0, defaultQty = 0;
                    double sumQty = 0;     //实际出库总量
                    double sumPQty = pQty; //实际发货总量
                    String batchNo = oneData.get("BATCHNO").toString();

                    if (StringUtils.isBlank(batchNo) || StringUtils.isEmpty(batchNo)) {
                        for (Map<String, Object> map : whereList) {
                            double qty = Double.parseDouble(map.get("PQTY").toString());  //签收量
                            List<Object> inputParameter = Lists.newArrayList();
                            nowQty = qty;

                            BcReq bcReq=new BcReq();
                            bcReq.setServiceType("StockInProcess");
                            bcReq.setDocType(doc_type);
                            bcReq.setBillType("13");
                            bcReq.setSyneryDiff(!deliveryCorp.equals(corp));
                            bcReq.setDbTBType(dbTBType);
                            BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                            String bType = bcMap.getBType();
                            String costCode = bcMap.getCostCode();
                            if(Check.Null(bType)||Check.Null(costCode)){
                                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                            }

                            double baseQty = BigDecimalUtils.mul(nowQty, Double.parseDouble(map.get("UNIT_RATIO").toString()));
                            double amt = BigDecimalUtils.mul(Double.parseDouble(map.get("PRICE").toString()), nowQty, 2);
                            double distriAmt = BigDecimalUtils.mul(Double.parseDouble(map.get("DISTRIPRICE").toString()), nowQty, 2);
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

                            String stockDistriPrice=map.get("DISTRIPRICE").toString();
                            String stockDistriAmt=String.valueOf(distriAmt);
                            String stockPreTaxAmt="0";
                            String stockTaxAmt="0";
                            String stockTaxCode="";
                            String stockTaxRate="0";

                            if(!corp.equals(deliveryCorp)&& "S".equals(costCode)){
                                List<Map<String, Object>> filterRows = interData.stream().filter(x -> x.get("ITEM").toString().equals(oneData.get("ITEM").toString())
                                ).collect(Collectors.toList());
                                if("1".equals(doc_type)) {
                                    filterRows = interData.stream().filter(x -> x.get("ITEM").toString().equals(oneData.get("ITEM").toString())
                                            && x.get("SUPPLYORGNO").toString().equals(oneData.get("TRANSFER_SHOP").toString())).collect(Collectors.toList());
                                }

                                if(filterRows.size()<=0){
                                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "内部结算明细不存在！");
                                }

                                stockDistriPrice = filterRows.get(0).get("RECEIVEPRICE").toString();
                                stockDistriAmt = filterRows.get(0).get("RECEIVEAMT").toString();
                                stockTaxAmt = filterRows.get(0).get("TAXAMT").toString();
                                stockTaxCode = filterRows.get(0).get("TAXCODE").toString();
                                stockTaxRate = filterRows.get(0).get("TAXRATE").toString();
                                stockPreTaxAmt = filterRows.get(0).get("PRETAXAMT").toString();


                                if (Check.Null(stockDistriAmt) || Check.Null(stockTaxAmt) || Check.Null(stockPreTaxAmt)) {
                                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "税额、金额、未税金额不可为空！");
                                }
                                int i = new BigDecimal(stockPreTaxAmt).add(new BigDecimal(stockTaxAmt)).compareTo(new BigDecimal(stockDistriAmt));
                                if (i != 0) {
                                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "金额、未税金额、税额不一致！");
                                }
                            }




                            inputParameter.add(eid);                                                     //P_EID		IN	VARCHAR2,	--企业ID
                            inputParameter.add(null);
                            if (StringUtils.isEmpty(ooOrganizationNo)) {
                                inputParameter.add(orgNo);  //在途组织给原组织                                        //P_OrgID		IN	VARCHAR2,	--组织
                            } else {
                                inputParameter.add(ooOrganizationNo);  //在途组织给原组织                                        //P_OrgID		IN	VARCHAR2,	--组织
                            }


                            inputParameter.add( bType);
                            inputParameter.add(costCode);
                            inputParameter.add("13");                                                                   //P_BillType	IN	VARCHAR2,	--单据类型
                            inputParameter.add(stockInNo);                                                //P_BillNo	IN	VARCHAR2,	--单据号
                            inputParameter.add(map.get("ITEM").toString());                                                   //P_Item		IN	INTEGER,	--单据行号
                            inputParameter.add("0");
                            inputParameter.add(-1);                                                                     //P_Direct	IN	INTEGER,	--异动方向 1=加库存 -1=减库存
                            inputParameter.add(map.get("BDATE").toString());                                                   //P_BDate		IN	VARCHAR2,	--营业日期 yyyy-MM-dd
                            inputParameter.add(map.get("PLUNO").toString());                                          //P_PluNo		IN	VARCHAR2,	--品号
                            inputParameter.add(StringUtils.toString(map.get("FEATURENO"), " "));          //P_FeatureNo	IN	VARCHAR2,	--特征码
                            inputParameter.add(map.get("INVWAREHOUSE").toString());                                              //P_WAREHOUSE	IN	VARCHAR2,	--仓库
                            inputParameter.add(StringUtils.toString(map.get("BATCHNO"), " "));        //P_BATCHNO	IN	VARCHAR2,	--批号
                            inputParameter.add(" ");       //P_LOCATION IN VARCHAR2,	--库位
                            inputParameter.add(map.get("PUNIT").toString());                                           //P_SUnit		IN	VARCHAR2,	--交易单位
                            inputParameter.add(nowQty);                                            //P_Qty		IN	NUMBER,		--交易数量
                            inputParameter.add(map.get("BASEUNIT").toString());                                                //P_BASEUNIT	IN	VARCHAR2,	--基准单位
                            inputParameter.add(baseQty);                                                 //P_BASEQTY	IN	NUMBER,		--基准数量
                            inputParameter.add(map.get("UNIT_RATIO").toString());                                              //P_UNITRATIO	IN	NUMBER,		--换算比例
                            inputParameter.add(map.get("PRICE").toString());                                                   //P_Price		IN	NUMBER,		--零售价
                            inputParameter.add(amt);                                                     //P_Amt		IN	NUMBER,		--零售金额
                            inputParameter.add(stockDistriPrice);                                             //P_DISTRIPRICE	IN	NUMBER,		--进货价
                            inputParameter.add(stockDistriAmt);                                               //P_DISTRIAMT	IN	NUMBER,		--进货金额

                            inputParameter.add(stockPreTaxAmt);
                            inputParameter.add(stockTaxAmt);
                            inputParameter.add(stockTaxCode);
                            inputParameter.add(stockTaxRate);

                            inputParameter.add(DateFormatUtils.getDate(oneData.get("ACCOUNT_DATE").toString()));               //P_ACCOUNTDATE	IN	VARCHAR2,	--入账日期 yyyy-MM-dd
                            inputParameter.add(DateFormatUtils.getDate(oneData.get("PROD_DATE").toString()));            //P_PRODDATE	IN	VARCHAR2,	--批号的生产日期 yyyy-MM-dd
                            inputParameter.add(DateFormatUtils.getDate(oneData.get("BDATE").toString()));               //P_BillDate	IN	VARCHAR2,	--单据日期 yyyy-MM-dd
                            inputParameter.add("");                                           //P_ReasonID	IN 	VARCHAR2,	--异动原因
                            inputParameter.add("在途库存调整");                                                                     //P_Description	IN 	VARCHAR2,	--异动描述
                            inputParameter.add(opNo);                                                    //P_UserID	IN	VARCHAR2	--操作员

                            dbs.add(new DataProcessBean(DataBeans.getProcedureBean("SP_DCP_STOCKCHANGE_V35", inputParameter)));
                        }

                    } else {
                        for (Map<String, Object> map : whereList) {
                            List<Object> inputParameter = Lists.newArrayList();
                            double qty = Double.parseDouble(map.get("PQTY").toString());  //签收量

                            nowQty = Math.min(pQty, qty);
                            sumQty = sumQty + qty;
                            pQty = pQty - qty; //实际出库量

                            double baseQty = BigDecimalUtils.mul(nowQty, Double.parseDouble(map.get("UNIT_RATIO").toString()));
                            double amt = BigDecimalUtils.mul(Double.parseDouble(map.get("PRICE").toString()), nowQty, 2);
                            double distriAmt = BigDecimalUtils.mul(Double.parseDouble(map.get("DISTRIPRICE").toString()), nowQty, 2);
                            BcReq bcReq=new BcReq();
                            bcReq.setServiceType("StockInProcess");
                            bcReq.setDocType(doc_type);
                            bcReq.setBillType("13");
                            bcReq.setSyneryDiff(!deliveryCorp.equals(corp));
                            bcReq.setDbTBType(dbTBType);

                            BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                            String bType = bcMap.getBType();
                            String costCode = bcMap.getCostCode();
                            if(Check.Null(bType)||Check.Null(costCode)){
                                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                            }

                            inputParameter.add(eid);                                                     //P_EID		IN	VARCHAR2,	--企业ID
                            inputParameter.add(null);
                            if (StringUtils.isEmpty(ooOrganizationNo)) {
                                inputParameter.add(orgNo);  //在途组织给原组织                                        //P_OrgID		IN	VARCHAR2,	--组织
                            } else {
                                inputParameter.add(ooOrganizationNo);  //在途组织给原组织                                        //P_OrgID		IN	VARCHAR2,	--组织
                            }
                            inputParameter.add(bType);
                            inputParameter.add(costCode);
                            inputParameter.add("13");                                                                   //P_BillType	IN	VARCHAR2,	--单据类型
                            inputParameter.add(stockInNo);                                                //P_BillNo	IN	VARCHAR2,	--单据号
                            inputParameter.add(map.get("ITEM").toString());                                                   //P_Item		IN	INTEGER,	--单据行号
                            inputParameter.add("");
                            inputParameter.add(-1);                                                                     //P_Direct	IN	INTEGER,	--异动方向 1=加库存 -1=减库存
                            inputParameter.add(map.get("BDATE").toString());                                                   //P_BDate		IN	VARCHAR2,	--营业日期 yyyy-MM-dd
                            inputParameter.add(map.get("PLUNO").toString());                                          //P_PluNo		IN	VARCHAR2,	--品号
                            inputParameter.add(StringUtils.toString(map.get("FEATURENO"), " "));          //P_FeatureNo	IN	VARCHAR2,	--特征码
                            inputParameter.add(map.get("INVWAREHOUSE").toString());                                              //P_WAREHOUSE	IN	VARCHAR2,	--仓库
                            inputParameter.add(StringUtils.toString(map.get("BATCHNO"), " "));        //P_BATCHNO	IN	VARCHAR2,	--批号
                            inputParameter.add(" ");       //P_LOCATION IN VARCHAR2,	--库位
                            inputParameter.add(map.get("PUNIT").toString());                                           //P_SUnit		IN	VARCHAR2,	--交易单位
                            inputParameter.add(nowQty);                                            //P_Qty		IN	NUMBER,		--交易数量
                            inputParameter.add(map.get("BASEUNIT").toString());                                                //P_BASEUNIT	IN	VARCHAR2,	--基准单位
                            inputParameter.add(baseQty);                                                 //P_BASEQTY	IN	NUMBER,		--基准数量
                            inputParameter.add(map.get("UNIT_RATIO").toString());                                              //P_UNITRATIO	IN	NUMBER,		--换算比例
                            inputParameter.add(map.get("PRICE").toString());                                                   //P_Price		IN	NUMBER,		--零售价
                            inputParameter.add(amt);                                                     //P_Amt		IN	NUMBER,		--零售金额
                            inputParameter.add(map.get("DISTRIPRICE").toString());                                             //P_DISTRIPRICE	IN	NUMBER,		--进货价
                            inputParameter.add(distriAmt);                                               //P_DISTRIAMT	IN	NUMBER,		--进货金额
                            inputParameter.add(DateFormatUtils.getDate(oneData.get("ACCOUNT_DATE").toString()));               //P_ACCOUNTDATE	IN	VARCHAR2,	--入账日期 yyyy-MM-dd
                            inputParameter.add(DateFormatUtils.getDate(oneData.get("PROD_DATE").toString()));            //P_PRODDATE	IN	VARCHAR2,	--批号的生产日期 yyyy-MM-dd
                            inputParameter.add(DateFormatUtils.getDate(oneData.get("BDATE").toString()));               //P_BillDate	IN	VARCHAR2,	--单据日期 yyyy-MM-dd
                            inputParameter.add("");                                           //P_ReasonID	IN 	VARCHAR2,	--异动原因
                            inputParameter.add("在途库存调整");                                                                     //P_Description	IN 	VARCHAR2,	--异动描述
                            inputParameter.add(opNo);                                                    //P_UserID	IN	VARCHAR2	--操作员

                            dbs.add(new DataProcessBean(DataBeans.getProcedureBean("SP_DCP_STOCKCHANGE_VX", inputParameter)));

                        }

                        if (sumQty - sumPQty > 0 && StringUtils.isNotEmpty(batchNo)) {  //批号不为空时，将签收量-发货量

                            defaultQty = sumQty - sumPQty; //签收量 - 发货量
                            List<Object> inputParameter2 = Lists.newArrayList();

                            double baseQty = BigDecimalUtils.mul(defaultQty, Double.parseDouble(oneData.get("UNIT_RATIO").toString()));
                            double amt = BigDecimalUtils.mul(Double.parseDouble(oneData.get("PRICE").toString()), defaultQty, 2);
                            double distriAmt = BigDecimalUtils.mul(Double.parseDouble(oneData.get("DISTRIPRICE").toString()), defaultQty, 2);

                            BcReq bcReq=new BcReq();
                            bcReq.setServiceType("StockInProcess");
                            bcReq.setDocType(doc_type);
                            bcReq.setBillType("13");
                            bcReq.setSyneryDiff(!deliveryCorp.equals(corp));
                            bcReq.setDbTBType(dbTBType);
                            BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                            String bType = bcMap.getBType();
                            String costCode = bcMap.getCostCode();
                            if(Check.Null(bType)||Check.Null(costCode)){
                                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                            }

                            inputParameter2.add(eid);                                                     //P_EID		IN	VARCHAR2,	--企业ID
                            inputParameter2.add(null);
                            if (StringUtils.isEmpty(ooOrganizationNo)) {
                                inputParameter2.add(orgNo);  //在途组织给原组织                                        //P_OrgID		IN	VARCHAR2,	--组织
                            } else {
                                inputParameter2.add(ooOrganizationNo);  //在途组织给原组织                                        //P_OrgID		IN	VARCHAR2,	--组织
                            }
                            inputParameter2.add(bType);
                            inputParameter2.add(costCode);
                            inputParameter2.add("13");                                                                   //P_BillType	IN	VARCHAR2,	--单据类型
                            inputParameter2.add(stockInNo);                                                //P_BillNo	IN	VARCHAR2,	--单据号
                            inputParameter2.add(oneData.get("ITEM").toString());                                                   //P_Item		IN	INTEGER,	--单据行号
                            inputParameter2.add("0");
                            inputParameter2.add(-1);                                                                     //P_Direct	IN	INTEGER,	--异动方向 1=加库存 -1=减库存
                            inputParameter2.add(oneData.get("BDATE").toString());                                                   //P_BDate		IN	VARCHAR2,	--营业日期 yyyy-MM-dd
                            inputParameter2.add(oneData.get("PLUNO").toString());                                          //P_PluNo		IN	VARCHAR2,	--品号
                            inputParameter2.add(StringUtils.toString(oneData.get("FEATURENO"), " "));          //P_FeatureNo	IN	VARCHAR2,	--特征码
                            inputParameter2.add(oneData.get("INVWAREHOUSE").toString());                                              //P_WAREHOUSE	IN	VARCHAR2,	--仓库
                            inputParameter2.add("DEFAULTBATCH");        //P_BATCHNO	IN	VARCHAR2,	--批号
                            inputParameter2.add(" ");
                            //P_LOCATION IN VARCHAR2,	--库位
                            inputParameter2.add(oneData.get("PUNIT").toString());                                           //P_SUnit		IN	VARCHAR2,	--交易单位
                            inputParameter2.add(defaultQty);                                            //P_Qty		IN	NUMBER,		--交易数量
                            inputParameter2.add(oneData.get("BASEUNIT").toString());                                                //P_BASEUNIT	IN	VARCHAR2,	--基准单位
                            inputParameter2.add(baseQty);                                                 //P_BASEQTY	IN	NUMBER,		--基准数量
                            inputParameter2.add(oneData.get("UNIT_RATIO").toString());                                              //P_UNITRATIO	IN	NUMBER,		--换算比例
                            inputParameter2.add(oneData.get("PRICE").toString());                                                   //P_Price		IN	NUMBER,		--零售价
                            inputParameter2.add(amt);                                                     //P_Amt		IN	NUMBER,		--零售金额
                            inputParameter2.add(oneData.get("DISTRIPRICE").toString());                                             //P_DISTRIPRICE	IN	NUMBER,		--进货价
                            inputParameter2.add(distriAmt);                                               //P_DISTRIAMT	IN	NUMBER,		--进货金额
                            inputParameter2.add(DateFormatUtils.getDate(oneData.get("ACCOUNT_DATE").toString()));               //P_ACCOUNTDATE	IN	VARCHAR2,	--入账日期 yyyy-MM-dd
                            inputParameter2.add(DateFormatUtils.getDate(oneData.get("PROD_DATE").toString()));            //P_PRODDATE	IN	VARCHAR2,	--批号的生产日期 yyyy-MM-dd
                            inputParameter2.add(DateFormatUtils.getDate(oneData.get("BDATE").toString()));               //P_BillDate	IN	VARCHAR2,	--单据日期 yyyy-MM-dd
                            inputParameter2.add("");                                           //P_ReasonID	IN 	VARCHAR2,	--异动原因
                            inputParameter2.add("在途库存差异调整");                                                                     //P_Description	IN 	VARCHAR2,	--异动描述
                            inputParameter2.add(opNo);                                                    //P_UserID	IN	VARCHAR2	--操作员

                            dbs.add(new DataProcessBean(DataBeans.getProcedureBean("SP_DCP_STOCKCHANGE_VX", inputParameter2)));

                        }
                    }
                }
            }
        }



        /*
        if (qData != null && !qData.isEmpty()) {

            Map<String, Object> oneData = qData.get(0);
            String docType = oneData.get("DOC_TYPE").toString();
            String invWarehouse = oneData.get("INVWAREHOUSE").toString();
            String organizationNO = oneData.get("ORGANIZATIONNO").toString();
            String ooOrganizationNo = oneData.get("OOORGANIZATIONNO").toString();
            if (StringUtils.isNotEmpty(invWarehouse)) {

                for (Map<String, Object> map : qData) {

                    List<Object> inputParameter = Lists.newArrayList();
                    int item = Integer.parseInt(map.get("ITEM").toString());

                    double qty = Double.parseDouble(map.get("DQTY").toString());  //签收量
                    double pQty = Double.parseDouble(map.get("PQTY").toString()); //发货量
                    double nowQty = 0, defaultQty = 0;
                    String batchNo = map.get("BATCHNO").toString();

                    if (StringUtils.isEmpty(batchNo) || StringUtils.isBlank(batchNo)) {
                        nowQty = Double.parseDouble(map.get("DQTY").toString());
                    } else {
                        if (qty <= pQty) { //签收量<=发货量，核销量=签收量；
                            nowQty = qty;
                        } else {
                            nowQty = pQty;
                            defaultQty = qty - pQty;
                        }

                    }
                    double baseQty = BigDecimalUtils.mul(nowQty, Double.parseDouble(map.get("UNIT_RATIO").toString()));
                    double amt = BigDecimalUtils.mul(Double.parseDouble(map.get("PRICE").toString()), nowQty, 2);
                    double distriAmt = BigDecimalUtils.mul(Double.parseDouble(map.get("DISTRIPRICE").toString()), nowQty, 2);

                    inputParameter.add(eid);                                                     //P_EID		IN	VARCHAR2,	--企业ID
                    if (StringUtils.isEmpty(ooOrganizationNo)) {
                        inputParameter.add(orgNo);  //在途组织给原组织                                        //P_OrgID		IN	VARCHAR2,	--组织
                    } else {
                        inputParameter.add(ooOrganizationNo);  //在途组织给原组织                                        //P_OrgID		IN	VARCHAR2,	--组织
                    }
                    inputParameter.add("13");                                                                   //P_BillType	IN	VARCHAR2,	--单据类型
                    inputParameter.add(stockInNo);                                                //P_BillNo	IN	VARCHAR2,	--单据号
                    inputParameter.add(map.get("ITEM2").toString());                                                   //P_Item		IN	INTEGER,	--单据行号
                    inputParameter.add(-1);                                                                     //P_Direct	IN	INTEGER,	--异动方向 1=加库存 -1=减库存
                    inputParameter.add(map.get("BDATE").toString());                                                   //P_BDate		IN	VARCHAR2,	--营业日期 yyyy-MM-dd
                    inputParameter.add(map.get("PLUNO").toString());                                          //P_PluNo		IN	VARCHAR2,	--品号
                    inputParameter.add(StringUtils.toString(map.get("FEATURENO"), " "));          //P_FeatureNo	IN	VARCHAR2,	--特征码
                    inputParameter.add(map.get("INVWAREHOUSE").toString());                                              //P_WAREHOUSE	IN	VARCHAR2,	--仓库
                    inputParameter.add(StringUtils.toString(map.get("BATCHNO"), " "));        //P_BATCHNO	IN	VARCHAR2,	--批号
                    inputParameter.add(" ");       //P_LOCATION IN VARCHAR2,	--库位
                    inputParameter.add(map.get("PUNIT").toString());                                           //P_SUnit		IN	VARCHAR2,	--交易单位
                    inputParameter.add(nowQty);                                            //P_Qty		IN	NUMBER,		--交易数量
                    inputParameter.add(map.get("BASEUNIT").toString());                                                //P_BASEUNIT	IN	VARCHAR2,	--基准单位
                    inputParameter.add(baseQty);                                                 //P_BASEQTY	IN	NUMBER,		--基准数量
                    inputParameter.add(map.get("UNIT_RATIO").toString());                                              //P_UNITRATIO	IN	NUMBER,		--换算比例
                    inputParameter.add(map.get("PRICE").toString());                                                   //P_Price		IN	NUMBER,		--零售价
                    inputParameter.add(amt);                                                     //P_Amt		IN	NUMBER,		--零售金额
                    inputParameter.add(map.get("DISTRIPRICE").toString());                                             //P_DISTRIPRICE	IN	NUMBER,		--进货价
                    inputParameter.add(distriAmt);                                               //P_DISTRIAMT	IN	NUMBER,		--进货金额
                    inputParameter.add(DateFormatUtils.getDate(oneData.get("ACCOUNT_DATE").toString()));               //P_ACCOUNTDATE	IN	VARCHAR2,	--入账日期 yyyy-MM-dd
                    inputParameter.add(DateFormatUtils.getDate(oneData.get("PRODDATE").toString()));            //P_PRODDATE	IN	VARCHAR2,	--批号的生产日期 yyyy-MM-dd
                    inputParameter.add(DateFormatUtils.getDate(oneData.get("BDATE").toString()));               //P_BillDate	IN	VARCHAR2,	--单据日期 yyyy-MM-dd
                    inputParameter.add("");                                           //P_ReasonID	IN 	VARCHAR2,	--异动原因
                    inputParameter.add("在途库存调整");                                                                     //P_Description	IN 	VARCHAR2,	--异动描述
                    inputParameter.add(opNo);                                                    //P_UserID	IN	VARCHAR2	--操作员

                    dbs.add(new DataProcessBean(DataBeans.getProcedureBean("SP_DCP_STOCKCHANGE_V3", inputParameter)));

                    if (defaultQty > 0) {
                        List<Object> inputParameter2 = Lists.newArrayList();

                        baseQty = BigDecimalUtils.mul(defaultQty, Double.parseDouble(map.get("UNIT_RATIO").toString()));
                        amt = BigDecimalUtils.mul(Double.parseDouble(map.get("PRICE").toString()), defaultQty, 2);
                        distriAmt = BigDecimalUtils.mul(Double.parseDouble(map.get("DISTRIPRICE").toString()), defaultQty, 2);

                        inputParameter2.add(eid);                                                     //P_EID		IN	VARCHAR2,	--企业ID
                        if (StringUtils.isEmpty(ooOrganizationNo)) {
                            inputParameter2.add(orgNo);  //在途组织给原组织                                        //P_OrgID		IN	VARCHAR2,	--组织
                        } else {
                            inputParameter2.add(ooOrganizationNo);  //在途组织给原组织                                        //P_OrgID		IN	VARCHAR2,	--组织
                        }
                        inputParameter2.add("13");                                                                   //P_BillType	IN	VARCHAR2,	--单据类型
                        inputParameter2.add(stockInNo);                                                //P_BillNo	IN	VARCHAR2,	--单据号
                        inputParameter2.add(map.get("ITEM2").toString());                                                   //P_Item		IN	INTEGER,	--单据行号
                        inputParameter2.add(-1);                                                                     //P_Direct	IN	INTEGER,	--异动方向 1=加库存 -1=减库存
                        inputParameter2.add(map.get("BDATE").toString());                                                   //P_BDate		IN	VARCHAR2,	--营业日期 yyyy-MM-dd
                        inputParameter2.add(map.get("PLUNO").toString());                                          //P_PluNo		IN	VARCHAR2,	--品号
                        inputParameter2.add(StringUtils.toString(map.get("FEATURENO"), " "));          //P_FeatureNo	IN	VARCHAR2,	--特征码
                        inputParameter2.add(map.get("INVWAREHOUSE").toString());                                              //P_WAREHOUSE	IN	VARCHAR2,	--仓库
                        inputParameter2.add("DEFAULTBATCH");        //P_BATCHNO	IN	VARCHAR2,	--批号
                        inputParameter2.add(" ");
                        //P_LOCATION IN VARCHAR2,	--库位
                        inputParameter2.add(map.get("PUNIT").toString());                                           //P_SUnit		IN	VARCHAR2,	--交易单位
                        inputParameter2.add(defaultQty);                                            //P_Qty		IN	NUMBER,		--交易数量
                        inputParameter2.add(map.get("BASEUNIT").toString());                                                //P_BASEUNIT	IN	VARCHAR2,	--基准单位
                        inputParameter2.add(baseQty);                                                 //P_BASEQTY	IN	NUMBER,		--基准数量
                        inputParameter2.add(map.get("UNIT_RATIO").toString());                                              //P_UNITRATIO	IN	NUMBER,		--换算比例
                        inputParameter2.add(map.get("PRICE").toString());                                                   //P_Price		IN	NUMBER,		--零售价
                        inputParameter2.add(amt);                                                     //P_Amt		IN	NUMBER,		--零售金额
                        inputParameter2.add(map.get("DISTRIPRICE").toString());                                             //P_DISTRIPRICE	IN	NUMBER,		--进货价
                        inputParameter2.add(distriAmt);                                               //P_DISTRIAMT	IN	NUMBER,		--进货金额
                        inputParameter2.add(DateFormatUtils.getDate(oneData.get("ACCOUNT_DATE").toString()));               //P_ACCOUNTDATE	IN	VARCHAR2,	--入账日期 yyyy-MM-dd
                        inputParameter2.add(DateFormatUtils.getDate(oneData.get("PRODDATE").toString()));            //P_PRODDATE	IN	VARCHAR2,	--批号的生产日期 yyyy-MM-dd
                        inputParameter2.add(DateFormatUtils.getDate(oneData.get("BDATE").toString()));               //P_BillDate	IN	VARCHAR2,	--单据日期 yyyy-MM-dd
                        inputParameter2.add("");                                           //P_ReasonID	IN 	VARCHAR2,	--异动原因
                        inputParameter2.add("在途库存差异调整");                                                                     //P_Description	IN 	VARCHAR2,	--异动描述
                        inputParameter2.add(opNo);                                                    //P_UserID	IN	VARCHAR2	--操作员

                        dbs.add(new DataProcessBean(DataBeans.getProcedureBean("SP_DCP_STOCKCHANGE_V3", inputParameter2)));

                    }

                }
            }

        }

         */

        return dbs;
    }

}
