package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_StockOutProcessReq;
import com.dsc.spos.json.cust.req.DCP_StockOutRevokeReq;
import com.dsc.spos.json.cust.res.DCP_StockOutProcessRes;
import com.dsc.spos.json.cust.res.DCP_StockOutRevokeRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_StockOutRevoke extends SPosAdvanceService<DCP_StockOutRevokeReq, DCP_StockOutRevokeRes> {

    @Override
    protected void processDUID(DCP_StockOutRevokeReq req, DCP_StockOutRevokeRes res) throws Exception {

//        1校验调拨出库/退配出库单的状态和该调拨/退配单对应的调入组织的收货通知单的状态：出库单状态status为2（待签收）且调入组织的收货通知单状态status为6（待收货）的出库单才可操作撤销
//        2撤销操作处理：产生出库单的红冲单，并自动完成红冲出库确认（单头备注：出库单号stockOutNo撤销生成)；
//        更新收货通知单状态=5.已撤销；出库单状态=5.已撤销
        String stockOutNo = req.getRequest().getStockOutNo();

        //查询出库单/退配出库单的状态
        List<Map<String, Object>> existsData = doQueryData(getQuerySql(req), null);
        if (null == existsData || existsData.isEmpty()) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无对应单据！" + stockOutNo);
        }

        Map<String, Object> oneData = existsData.get(0);

        String status = oneData.get("STATUS").toString();
        String rStatus = oneData.get("RSTATUS").toString();

        if (!"2".equals(status) || !"6".equals(rStatus)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "出库单状态status为2（待签收）且调入组织的收货通知单状态status为6（待收货）的出库单才可操作撤销!" +
                    " 单据状态为:" + status +
                    " 收货通知单状态为:" + rStatus);
        }

        String docType = oneData.get("DOC_TYPE").toString();
        String orgNo = oneData.get("ORGANIZATIONNO").toString();
        String newStockOutNo = "";
        if (docType.equals("1")) {
            newStockOutNo = getOrderNO(req, orgNo, "DBCK");
        } else if (docType.equals("0") || docType.equals("2")) {
            newStockOutNo = getOrderNO(req, orgNo, "THCK");
        } else if (docType.equals("3")) {
            newStockOutNo = getOrderNO(req, orgNo, "QTCK");
        } else if (docType.equals("4")) {
            newStockOutNo = getOrderNO(req, orgNo, "YCCK");
        }

        //创建红冲单 逻辑同DCP_StockOutRefundCreate
        ColumnDataValue dcp_stockout = new ColumnDataValue();

        dcp_stockout.append("EID", DataValues.newString(oneData.get("EID")));
        dcp_stockout.append("SHOPID", DataValues.newString(oneData.get("SHOPID")));
        dcp_stockout.append("ORGANIZATIONNO", DataValues.newString(oneData.get("ORGANIZATIONNO")));
        dcp_stockout.append("STOCKOUTNO", DataValues.newString(newStockOutNo));

        dcp_stockout.append("BDATE", DataValues.newDecimal(DateFormatUtils.getNowPlainDate()));

        dcp_stockout.append("TRANSFER_SHOP", DataValues.newString(oneData.get("TRANSFER_SHOP")));
        dcp_stockout.append("CONFIRM_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
        dcp_stockout.append("CONFIRM_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));
        dcp_stockout.append("CONFIRMBY", DataValues.newString(req.getEmployeeNo()));

        dcp_stockout.append("TOT_AMT", DataValues.newString(oneData.get("TOT_AMT")));
        dcp_stockout.append("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
        dcp_stockout.append("ACCOUNT_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
        dcp_stockout.append("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));
        dcp_stockout.append("ACCOUNT_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));
        dcp_stockout.append("CREATEBY", DataValues.newString(req.getEmployeeNo()));
        dcp_stockout.append("ACCOUNTBY", DataValues.newString(req.getEmployeeNo()));

        dcp_stockout.append("DOC_TYPE", DataValues.newString(oneData.get("DOC_TYPE")));
        dcp_stockout.append("OFNO", DataValues.newString(oneData.get("OFNO")));
        dcp_stockout.append("BSNO", DataValues.newString(oneData.get("BSNO")));
        dcp_stockout.append("TRANSFER_WAREHOUSE", DataValues.newString(oneData.get("TRANSFER_WAREHOUSE")));
        dcp_stockout.append("TOT_PQTY", DataValues.newString(oneData.get("TOT_PQTY")));
        dcp_stockout.append("TOT_DISTRIAMT", DataValues.newString(oneData.get("TOT_DISTRIAMT")));
        dcp_stockout.append("OTYPE", DataValues.newString(oneData.get("OTYPE")));
        dcp_stockout.append("WAREHOUSE", DataValues.newString(oneData.get("WAREHOUSE")));
        dcp_stockout.append("TOT_CQTY", DataValues.newString(oneData.get("TOT_CQTY")));
        dcp_stockout.append("RECEIPT_ORG", DataValues.newString(oneData.get("RECEIPT_ORG")));
        dcp_stockout.append("LOAD_DOCNO", DataValues.newString(oneData.get("LOAD_DOCNO")));
        dcp_stockout.append("MEMO", DataValues.newString("出库单号" + stockOutNo + "撤销生成"));
        dcp_stockout.append("PTEMPLATENO", DataValues.newString(oneData.get("PTEMPLATENO")));

        dcp_stockout.append("STATUS", DataValues.newString("0"));
//        if (docType.equals("4")) {
//            dcp_stockout.append("STATUS", DataValues.newString("4"));
//        } else {
//            dcp_stockout.append("STATUS", DataValues.newString("2"));
//        }

        dcp_stockout.append("SOURCEMENU", DataValues.newString(oneData.get("SOURCEMENU")));
        dcp_stockout.append("RDATE", DataValues.newString(oneData.get("RDATE")));
        dcp_stockout.append("PACKINGNO", DataValues.newString(oneData.get("PACKINGNO")));
        dcp_stockout.append("INVWAREHOUSE", DataValues.newString(oneData.get("INVWAREHOUSE")));
        dcp_stockout.append("RECEIPTDATE", DataValues.newString(oneData.get("RECEIPTDATE")));
        dcp_stockout.append("DELIVERYDATE", DataValues.newString(oneData.get("DELIVERYDATE")));
        dcp_stockout.append("ISTRANINCONFIRM", DataValues.newString(oneData.get("ISTRANINCONFIRM")));

        dcp_stockout.append("STOCKOUTNO_ORIGIN", DataValues.newString(stockOutNo));
//        dcp_stockout.append("STOCKOUTNO_REFUND", DataValues.newString(stockOutNo));
        dcp_stockout.append("EMPLOYEEID", DataValues.newString(req.getEmployeeNo()));
        dcp_stockout.append("DEPARTID", DataValues.newString(req.getDepartmentNo()));

        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_STOCKOUT", dcp_stockout)));

        //修改原始单据信息和状态
        ColumnDataValue oStockOutCondition = new ColumnDataValue();
        ColumnDataValue oStockOut = new ColumnDataValue();

        oStockOutCondition.append("EID", DataValues.newString(oneData.get("EID")));
        oStockOutCondition.append("STOCKOUTNO", DataValues.newString(oneData.get("STOCKOUTNO")));
        oStockOutCondition.append("SHOPID", DataValues.newString(oneData.get("SHOPID")));
        oStockOutCondition.append("ORGANIZATIONNO", DataValues.newString(oneData.get("ORGANIZATIONNO")));

        oStockOut.add("STATUS", DataValues.newString("5"));
        oStockOut.add("STOCKOUTNO_REFUND", DataValues.newString(newStockOutNo));
        if (StringUtils.isNotEmpty(req.getRequest().getReason())){
            oStockOut.add("REJECTREASON", DataValues.newString(req.getRequest().getReason()));
        }
        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_STOCKOUT", oStockOutCondition, oStockOut)));

        ColumnDataValue oReceivingCondition = new ColumnDataValue();
        ColumnDataValue oReceiving = new ColumnDataValue();

        oReceivingCondition.add("EID", DataValues.newString(oneData.get("EID").toString()));
        oReceivingCondition.add("RECEIVINGNO", DataValues.newString(oneData.get("RECEIVINGNO").toString()));

        oReceiving.add("STATUS", DataValues.newString("5"));
        if (StringUtils.isNotEmpty(req.getRequest().getReason())){
            oReceiving.add("REASON", DataValues.newString(req.getRequest().getReason()));
        }
        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_RECEIVING", oReceivingCondition, oReceiving)));


        Map<String, Boolean> condition = new HashMap<>();
        condition.put("ITEM", true);

        List<Map<String, Object>> detailData = MapDistinct.getMap(existsData, condition);
        for (Map<String, Object> data : detailData) {
            ColumnDataValue dcp_stockout_detail = new ColumnDataValue();

            dcp_stockout_detail.append("EID", DataValues.newString(data.get("EID")));
            dcp_stockout_detail.append("SHOPID", DataValues.newString(data.get("SHOPID")));
            dcp_stockout_detail.append("ORGANIZATIONNO", DataValues.newString(data.get("ORGANIZATIONNO")));
            dcp_stockout_detail.append("ITEM", DataValues.newString(data.get("ITEM")));
            dcp_stockout_detail.append("STOCKOUTNO", DataValues.newString(newStockOutNo));

            dcp_stockout_detail.append("OITEM", DataValues.newString(data.get("OITEM")));
            dcp_stockout_detail.append("PUNIT", DataValues.newString(data.get("PUNIT")));
            dcp_stockout_detail.append("WAREHOUSE", DataValues.newString(data.get("WAREHOUSE")));
            dcp_stockout_detail.append("PLUNO", DataValues.newString(data.get("PLUNO")));
            dcp_stockout_detail.append("BATCH_NO", DataValues.newString(data.get("BATCH_NO")));
            dcp_stockout_detail.append("PROD_DATE", DataValues.newString(data.get("PROD_DATE")));
            dcp_stockout_detail.append("PRICE", DataValues.newString(data.get("PRICE")));
            dcp_stockout_detail.append("DISTRIPRICE", DataValues.newString(data.get("DISTRIPRICE")));
            dcp_stockout_detail.append("UNIT_RATIO", DataValues.newString(data.get("UNIT_RATIO")));

            double pQty = Double.parseDouble(data.get("PQTY").toString()) * -1;
            double baseQty = Double.parseDouble(data.get("BASEQTY").toString()) * -1;
            double amt = Double.parseDouble(data.get("AMT").toString()) * -1;
            double distriAmt = Double.parseDouble(data.get("DISTRIAMT").toString()) * -1;

            dcp_stockout_detail.append("PQTY", DataValues.newString(pQty));
            dcp_stockout_detail.append("BASEQTY", DataValues.newString(baseQty));
            dcp_stockout_detail.append("BASEUNIT", DataValues.newString(data.get("BASEUNIT")));
            dcp_stockout_detail.append("PLU_BARCODE", DataValues.newString(data.get("PLU_BARCODE")));
            dcp_stockout_detail.append("AMT", DataValues.newString(amt));
            dcp_stockout_detail.append("DISTRIAMT", DataValues.newString(distriAmt));

            dcp_stockout_detail.append("RQTY", DataValues.newString(data.get("RQTY")));
            dcp_stockout_detail.append("BSNO", DataValues.newString(data.get("BSNO")));
            dcp_stockout_detail.append("PLU_MEMO", DataValues.newString(data.get("PLU_MEMO")));

            dcp_stockout_detail.append("BDATE", DataValues.newDecimal(DateFormatUtils.getNowPlainDate()));

            dcp_stockout_detail.append("FEATURENO", DataValues.newString(data.get("FEATURENO")));
            dcp_stockout_detail.append("STOCKQTY", DataValues.newString(data.get("STOCKQTY")));
            dcp_stockout_detail.append("POQTY", DataValues.newString(data.get("POQTY")));

            dcp_stockout_detail.append("MES_STATUS", DataValues.newString(data.get("MES_STATUS")));
            dcp_stockout_detail.append("MES_LOCATION", DataValues.newString(data.get("MES_LOCATION")));
            dcp_stockout_detail.append("PAKCINGNO", DataValues.newString(data.get("PAKCINGNO")));
            dcp_stockout_detail.append("EXPDATE", DataValues.newString(data.get("EXPDATE")));


            dcp_stockout_detail.append("OFNO", DataValues.newString(data.get("OFNO")));
            dcp_stockout_detail.append("OOTYPE", DataValues.newString(data.get("OOTYPE")));
            dcp_stockout_detail.append("OOFNO", DataValues.newString(data.get("OOFNO")));
            dcp_stockout_detail.append("OOITEM", DataValues.newString(data.get("OOITEM")));
            dcp_stockout_detail.append("OTYPE", DataValues.newString(data.get("OTYPE")));
            dcp_stockout_detail.append("RECEIVINGNO", DataValues.newString(data.get("RECEIVINGNO")));
            dcp_stockout_detail.append("RECEIVINGITEM", DataValues.newString(data.get("RECEIVINGITEM")));

            dcp_stockout_detail.append("PQTY_ORIGIN", DataValues.newString(data.get("PQTY")));
            dcp_stockout_detail.append("ITEM_ORIGIN", DataValues.newString(data.get("ITEM")));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_STOCKOUT_DETAIL", dcp_stockout_detail)));

            ColumnDataValue oCondition = new ColumnDataValue();
            ColumnDataValue oValue = new ColumnDataValue();

            oCondition.add("EID", DataValues.newString(data.get("EID")));
            oCondition.add("STOCKOUTNO", DataValues.newString(data.get("STOCKOUTNO")));
            oCondition.add("ITEM", DataValues.newString(data.get("ITEM")));
            oCondition.add("ORGANIZATIONNO", DataValues.newString(data.get("ORGANIZATIONNO")));
            oCondition.add("SHOPID", DataValues.newString(data.get("SHOPID")));

            oValue.add("PQTY_REFUND", DataValues.newDecimal(data.get("PQTY")));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_STOCKOUT_DETAIL", oCondition, oValue)));
        }

        for (Map<String, Object> data : existsData) {
            ColumnDataValue dcp_stockout_batch = new ColumnDataValue();

            dcp_stockout_batch.append("EID", DataValues.newString(data.get("EID")));
            dcp_stockout_batch.append("SHOPID", DataValues.newString(data.get("SHOPID")));
            dcp_stockout_batch.append("ORGANIZATIONNO", DataValues.newString(data.get("ORGANIZATIONNO")));
            dcp_stockout_batch.append("ITEM", DataValues.newString(data.get("DITEM")));
            dcp_stockout_batch.append("STOCKOUTNO", DataValues.newString(newStockOutNo));

            dcp_stockout_batch.append("ITEM2", DataValues.newString(data.get("DITEM2")));
            dcp_stockout_batch.append("PLUNO", DataValues.newString(data.get("DPLUNO")));
            dcp_stockout_batch.append("FEATURENO", DataValues.newString(data.get("DFEATURENO")));
            dcp_stockout_batch.append("WAREHOUSE", DataValues.newString(data.get("DWAREHOUSE")));
            dcp_stockout_batch.append("LOCATION", DataValues.newString(data.get("DLOCATION")));
            dcp_stockout_batch.append("BATCHNO", DataValues.newString(data.get("DBATCHNO")));
            dcp_stockout_batch.append("PRODDATE", DataValues.newString(data.get("DPRODDATE")));
            dcp_stockout_batch.append("EXPDATE", DataValues.newString(data.get("DEXPDATE")));
            dcp_stockout_batch.append("PUNIT", DataValues.newString(data.get("DPUNIT")));
            dcp_stockout_batch.append("PQTY", DataValues.newString(data.get("DPQTY")));
            dcp_stockout_batch.append("BASEUNIT", DataValues.newString(data.get("DBASEUNIT")));
            dcp_stockout_batch.append("BASEQTY", DataValues.newString(data.get("DBASEQTY")));
            dcp_stockout_batch.append("UNITRATIO", DataValues.newString(data.get("DUNITRATIO")));
            dcp_stockout_batch.append("RQTY", DataValues.newString(data.get("DRQTY")));

            dcp_stockout_batch.append("PQTY_ORIGIN", DataValues.newString(data.get("DOPQTY")));
            dcp_stockout_batch.append("ITEM_ORIGIN", DataValues.newString(data.get("DITEM")));


            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_STOCKOUT_BATCH", dcp_stockout_batch)));

            ColumnDataValue oCondition = new ColumnDataValue();
            ColumnDataValue oValue = new ColumnDataValue();

            oCondition.add("EID", DataValues.newString(data.get("EID")));
            oCondition.add("STOCKOUTNO", DataValues.newString(data.get("STOCKOUTNO")));
            oCondition.add("ITEM", DataValues.newString(data.get("DITEM")));
            oCondition.add("ORGANIZATIONNO", DataValues.newString(data.get("ORGANIZATIONNO")));
            oCondition.add("SHOPID", DataValues.newString(data.get("SHOPID")));

            oValue.add("PQTY_REFUND", DataValues.newDecimal(data.get("DPQTY")));


            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_STOCKOUT_BATCH", oCondition, oValue)));

        }


        /*

        for (Map<String, Object> data : existsData) {

            String stockDocType = "";
            if (docType.equals("4")) {
                stockDocType = "19";
            } else {
                stockDocType = "12";
            }

            String procedure = "SP_DCP_STOCKCHANGE_V3";
            List<Object> inputParameter = Lists.newArrayList();

            inputParameter.add(data.get("EID").toString());                                      //--企业ID
            inputParameter.add(data.get("ORGANIZATIONNO").toString());                           //--组织

            inputParameter.add(stockDocType);                                     //--单据类型
            inputParameter.add(newStockOutNo);       //--单据号
            inputParameter.add(data.get("DITEM").toString());           //--单据行号
            inputParameter.add("-1");                                     //--异动方向 1=加库存 -1=减库存
            inputParameter.add(data.get("BDATE").toString());          //--营业日期 yyyy-MM-dd
            inputParameter.add(data.get("DPLUNO").toString());          //--品号
            inputParameter.add(data.get("DFEATURENO").toString());                                //--特征码
            inputParameter.add(data.get("DWAREHOUSE").toString());                          //--仓库
            inputParameter.add(data.get("DBATCHNO").toString());      //--批号
            inputParameter.add(data.get("DLOCATION").toString());      //--批号
            inputParameter.add(data.get("DPUNIT").toString());         //--交易单位
            inputParameter.add(data.get("DPQTY").toString());          //--交易数量
            inputParameter.add(data.get("DBASEUNIT").toString());      //--基准单位
            inputParameter.add(data.get("DBASEQTY").toString());       //--基准数量
            inputParameter.add(data.get("DUNITRATIO").toString());    //--换算比例
            inputParameter.add(data.get("PRICE").toString());         //--零售价
            inputParameter.add(data.get("AMT").toString());           //--零售金额
            inputParameter.add(data.get("DISTRIPRICE").toString());   //--进货价
            inputParameter.add(data.get("DISTRIAMT").toString());     //--进货金额
            inputParameter.add(DateFormatUtils.getNowDate());                             //--入账日期 yyyy-MM-dd
            inputParameter.add(data.get("DPRODDATE").toString());     //--批号的生产日期 yyyy-MM-dd
            inputParameter.add(data.get("BDATE").toString());         //--单据日期
            inputParameter.add("");                                      //--异动原因
            inputParameter.add("");                                      //--异动描述
            inputParameter.add(req.getEmployeeNo());                                    //--操作员

            this.addProcessData(new DataProcessBean(DataBeans.getProcedureBean(procedure, inputParameter)));
        }

         */

        this.doExecuteDataToDB();

        ParseJson pj = new ParseJson();
        DCP_StockOutProcessReq outReq = new DCP_StockOutProcessReq();
        outReq.setServiceId("DCP_StockOutProcess");
        outReq.setToken(req.getToken());
        DCP_StockOutProcessReq.levelElm soRequest = outReq.new levelElm();

        soRequest.setStockOutNo(newStockOutNo);
        soRequest.setDocType(docType);
        soRequest.setStatus("2");//确认
        soRequest.setOrgNo(orgNo);

        outReq.setRequest(soRequest);

        String jsontemp = pj.beanToJson(outReq);

        DispatchService ds = DispatchService.getInstance();
        String resXml = ds.callService(jsontemp, StaticInfo.dao);
        DCP_StockOutProcessRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_StockOutProcessRes>() {
        });
        if (!resserver.isSuccess()) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "出库单审核失败！");
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }


    @Override
    protected List<InsBean> prepareInsertData(DCP_StockOutRevokeReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockOutRevokeReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockOutRevokeReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_StockOutRevokeReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_StockOutRevokeReq> getRequestType() {
        return new TypeToken<DCP_StockOutRevokeReq>() {
        };
    }

    @Override
    protected DCP_StockOutRevokeRes getResponseType() {
        return new DCP_StockOutRevokeRes();
    }


    @Override
    protected String getQuerySql(DCP_StockOutRevokeReq req) throws Exception {

        String querySql = " SELECT " +
                " a.STATUS,a.BDATE,a.TRANSFER_SHOP,a.DOC_TYPE,a.DELIVERY_NO,a.OTYPE " +
                " ,a.WAREHOUSE,a.TOT_CQTY,a.RECEIPT_ORG,a.LOAD_DOCNO,a.LOAD_DOCTYPE " +
                " ,a.PTEMPLATENO,a.SOURCEMENU,a.OOTYPE,a.OOFNO,a.RDATE,a.BDATE " +
                " ,a.DOC_TYPE,a.TOT_AMT*-1 TOT_AMT,a.PACKINGNO,a.INVWAREHOUSE,a.RECEIPTDATE" +
                " ,a.DELIVERYDATE ,a.TOT_PQTY *-1 TOT_PQTY,a.TOT_DISTRIAMT*-1 TOT_DISTRIAMT " +
                " ,b.STATUS as RSTATUS,b.RECEIVINGNO " +
                " ,d.ITEM DITEM,d.ITEM2 DITEM2,d.PLUNO DPLUNO,d.FEATURENO DFEATURENO,d.WAREHOUSE DWAREHOUSE" +
                " ,d.LOCATION DLOCATION,d.BATCHNO DBATCHNO,d.PQTY*-1 DPQTY,d.BASEUNIT DBASEUNIT " +
                " ,d.PRODDATE DPRODDATE,d.EXPDATE DEXPDATE,d.PUNIT DPUNIT,d.BASEQTY*-1 DBASEQTY" +
                " ,d.UNITRATIO DUNITRATIO,d.RQTY DRQTY,d.PQTY DOPQTY" +
                " ,c.*   " +
                " FROM DCP_STOCKOUT a " +
                " INNER JOIN DCP_STOCKOUT_DETAIL C ON a.EID=c.EID and a.SHOPID=c.SHOPID and a.STOCKOUTNO=c.STOCKOUTNO and a.ORGANIZATIONNO=c.ORGANIZATIONNO " +
                " INNER JOIN DCP_STOCKOUT_BATCH d on c.eid=d.eid and d.SHOPID=c.SHOPID and d.STOCKOUTNO=c.STOCKOUTNO and d.ORGANIZATIONNO=c.ORGANIZATIONNO AND d.ITEM2=c.ITEM " +
                " LEFT JOIN DCP_RECEIVING b ON a.STOCKOUTNO=b.LOAD_DOCNO and a.EID=b.EID  " +
                " WHERE a.EID='" + req.geteId() + "' and a.STOCKOUTNO='" + req.getRequest().getStockOutNo() + "'";

        return querySql;
    }


}
