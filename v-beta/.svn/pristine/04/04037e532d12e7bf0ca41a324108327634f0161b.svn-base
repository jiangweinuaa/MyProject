package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataGenerateReq;
import com.dsc.spos.json.cust.req.DCP_StockOutCreateInBatchReq;
import com.dsc.spos.json.cust.req.DCP_StockOutUpdateReq;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataGenerateRes;
import com.dsc.spos.json.cust.res.DCP_StockOutCreateInBatchRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_StockOutCreateInBatch extends SPosAdvanceService<DCP_StockOutCreateInBatchReq, DCP_StockOutCreateInBatchRes> {

    @Override
    protected boolean isVerifyFail(DCP_StockOutCreateInBatchReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_StockOutCreateInBatchReq> getRequestType() {
        return new TypeToken<DCP_StockOutCreateInBatchReq>() {
        };
    }

    @Override
    protected DCP_StockOutCreateInBatchRes getResponseType() {
        return new DCP_StockOutCreateInBatchRes();
    }

    @Override
    public void processDUID(DCP_StockOutCreateInBatchReq req, DCP_StockOutCreateInBatchRes res) throws Exception {

        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        //String sysDate = formatter.format(new Date());

        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String createDate = df.format(cal.getTime());
        df = new SimpleDateFormat("HHmmss");
        String createTime = df.format(cal.getTime());

        DCP_StockOutCreateInBatchReq.LevelElm request = req.getRequest();
        List<DCP_StockOutCreateInBatchReq.DataList> dataList = request.getDataList();
        List<Map<String, String>> docList = dataList.stream().map(x -> {
            Map<String, String> docMap = new HashMap<>();
            docMap.put("templateNo", x.getTemplateNo());
            docMap.put("noticeNo", x.getNoticeNo());
            docMap.put("warehouse", x.getWarehouse());
            docMap.put("receiptOrg", x.getReceiptOrg());
            return docMap;
        }).distinct().collect(Collectors.toList());

        StringBuffer sJoinPluNo = new StringBuffer();
        for (DCP_StockOutCreateInBatchReq.DataList x : dataList) {
            sJoinPluNo.append(x.getPluNo() + ",");
        }
        Map<String, String> map = new HashMap<>();
        map.put("PLUNO", sJoinPluNo.toString());

        MyCommon cm = new MyCommon();
        String withPlu = cm.getFormatSourceMultiColWith(map);
        String sql = " select a.pluno,a.featureno,sum(a.qty) as baseqty "
                + " from dcp_stock a"
                + " inner join (" + withPlu + ") b on a.pluno=b.pluno "
                + " inner join dcp_warehouse c on a.eid=c.eid and a.organizationno=c.organizationno and a.warehouse=c.warehouse and c.warehouse_type<>'3'"
                + " where a.eid='" + req.geteId() + "' and a.organizationno='" + req.getOrganizationNO() + "'  "
                + " group by a.pluno,a.featureno";
        List<Map<String, Object>> getStock = this.doQueryData(sql, null);

        //查询组织
        StringBuffer orgSb = new StringBuffer();
        orgSb.append("select * from dcp_org a where a.eid='" + eId + "'");
        List<Map<String, Object>> orgList = this.doQueryData(orgSb.toString(), null);

        List<String> docNos=new ArrayList<>();

        List<DCP_InterSettleDataGenerateReq> interReqs=new ArrayList<>();

        for (Map<String, String> docMap : docList) {
            String templateNo = docMap.get("templateNo");
            String noticeNo = docMap.get("noticeNo");
            String warehouse = docMap.get("warehouse");
            String receiptOrg = docMap.get("receiptOrg");

            //查询通知单
            StringBuffer noticeSb = new StringBuffer();
            noticeSb.append("select a.eid,a.DELIVERORGNO,b.RETAILPRICE,b.item,b.RETAILPRICE,b.price,b.baseunit,b.poqty,b.sourcetype,b.sourcebillno,b.oitem,b.pQty-nvl(b.stockOutQty,0) as stockOutNoQty,nvl(b.PLUBARCODE,c.MAINBARCODE) as plubarcode " +
                    " from DCP_STOCKOUTNOTICE a " +
                    " left join DCP_STOCKOUTNOTICE_DETAIL b on a.eid=b.eid and a.organizationno=b.organizationno and a.billno=b.billno " +
                    " left join dcp_goods c on c.eid=a.eid and c.pluno=b.pluno " +
                    " where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "' and a.billno='" + noticeNo + "' ");
            List<Map<String, Object>> noticeDetails = this.doQueryData(noticeSb.toString(), null);
            if (CollUtil.isEmpty(noticeDetails)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "通知单不存在");
            }

            String deliverorgno = noticeDetails.get(0).get("DELIVERORGNO").toString();
            String orderNO = this.getOrderNO(req, deliverorgno,"PSCK");
            String corp="";
            String receiptCorp="";
            String orgSql1="select * from dcp_org where eid='"+req.geteId()+"' and organizationno='"+deliverorgno+"' ";
            String orgSql2="select * from dcp_org where eid='"+req.geteId()+"' and organizationno='"+receiptOrg+"' ";
            List<Map<String, Object>> orgList1 = this.doQueryData(orgSql1, null);
            if(CollUtil.isNotEmpty(orgList1)){
                corp = orgList1.get(0).get("CORP").toString();
            }
            List<Map<String, Object>> orgList2 = this.doQueryData(orgSql2, null);
            if(CollUtil.isNotEmpty(orgList2)){
                receiptCorp = orgList2.get(0).get("CORP").toString();
            }

            //if(!corp.equals(receiptCorp)){
                //单据类型(0退货出库 1调拨出库 3其他出库 4移仓出库 5配送出库)
                //配货出库
                DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                inReq.setServiceId("DCP_InterSettleDataGenerate");
                inReq.setToken(req.getToken());
                //DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                request1.setOrganizationNo(deliverorgno);
                request1.setBillNo(orderNO);
                request1.setSupplyOrgNo(deliverorgno);
                request1.setReturnSupplyPrice("Y");
                request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType10003.getType());
                request1.setDetail(new ArrayList<>());

            //}




            List<Map<String, Object>> orgFilterRows = orgList.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(deliverorgno)).collect(Collectors.toList());
            String invWarehouse = "";

            if (CollUtil.isNotEmpty(orgFilterRows)) {
                invWarehouse = orgFilterRows.get(0).get("INV_COST_WAREHOUSE").toString();

            }

            orgFilterRows = orgList.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(receiptOrg)).collect(Collectors.toList());
            String transferWarehouse = "";
            if (CollUtil.isNotEmpty(orgFilterRows)) {
                transferWarehouse = orgFilterRows.get(0).get("IN_COST_WAREHOUSE").toString();
            }


            BigDecimal totCqty = BigDecimal.ZERO;
            BigDecimal totPqty = BigDecimal.ZERO;
            BigDecimal totDistriAmt = BigDecimal.ZERO;
            BigDecimal totAmt = BigDecimal.ZERO;

            //单身
            List<DCP_StockOutCreateInBatchReq.DataList> detailList = dataList.stream().filter(x -> x.getTemplateNo().equals(templateNo) &&
                    x.getNoticeNo().equals(noticeNo) &&
                    x.getReceiptOrg().equals(receiptOrg) &&
                    x.getWarehouse().equals(warehouse)).collect(Collectors.toList());
            int detailItem = 0;
            int batchItem = 0;
            for (DCP_StockOutCreateInBatchReq.DataList detail : detailList) {

                List<Map<String, Object>> singleNoticeDetail = noticeDetails.stream().filter(x -> x.get("ITEM").toString().equals(detail.getNoticeItem())).collect(Collectors.toList());
                BigDecimal distriPrice = new BigDecimal(singleNoticeDetail.get(0).get("PRICE").toString());
                BigDecimal price = new BigDecimal(Check.Null(singleNoticeDetail.get(0).get("RETAILPRICE").toString()) ? "0" : singleNoticeDetail.get(0).get("RETAILPRICE").toString());
                BigDecimal amt = new BigDecimal(detail.getPQty()).multiply(price);
                BigDecimal distriAmt = new BigDecimal(detail.getPQty()).multiply(distriPrice);
                String baseunit = singleNoticeDetail.get(0).get("BASEUNIT").toString();
                if (Check.Null(detail.getPluBarcode())) {
                    detail.setPluBarcode(singleNoticeDetail.get(0).get("PLUBARCODE").toString());
                }

                String stockoutNoQty = singleNoticeDetail.get(0).get("STOCKOUTNOQTY").toString();

                Map<String, Object> baseMap = PosPub.getBaseQty(dao, req.geteId(),
                        detail.getPluNo(), detail.getPUnit(),
                        String.valueOf(detail.getPQty()));

                String baseQty = baseMap.get("baseQty").toString();
                String unitRatio = baseMap.get("unitRatio").toString();
                String poqty = singleNoticeDetail.get(0).get("POQTY").toString();
                String sourcetype = singleNoticeDetail.get(0).get("SOURCETYPE").toString();
                String sourcebillno = singleNoticeDetail.get(0).get("SOURCEBILLNO").toString();
                String oitem = singleNoticeDetail.get(0).get("OITEM").toString();

                List<Map<String, Object>> plunos = getStock.stream().filter(x -> x.get("PLUNO").toString().equals(detail.getPluNo()) && x.get("FEATURENO").toString().equals(detail.getFeatureNo())).distinct().collect(Collectors.toList());
                String stockQty = plunos.size() > 0 ? plunos.get(0).get("BASEQTY").toString() : "0";

                detailItem++;
                ColumnDataValue detailColumns = new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(eId));
                detailColumns.add("ORGANIZATIONNO", DataValues.newString(deliverorgno));
                detailColumns.add("SHOPID", DataValues.newString(deliverorgno));
                detailColumns.add("STOCKOUTNO", DataValues.newString(orderNO));
                detailColumns.add("ITEM", DataValues.newString(detailItem));
                detailColumns.add("OITEM", DataValues.newString(detail.getNoticeItem()));
                detailColumns.add("PLU_BARCODE", DataValues.newString(detail.getPluBarcode()));
                detailColumns.add("PLUNO", DataValues.newString(detail.getPluNo()));
                detailColumns.add("FEATURENO", DataValues.newString(Check.Null(detail.getFeatureNo()) ? " " : detail.getFeatureNo()));
                detailColumns.add("WAREHOUSE", DataValues.newString(detail.getWarehouse()));
                detailColumns.add("PUNIT", DataValues.newString(detail.getPUnit()));
                detailColumns.add("PQTY", DataValues.newString(detail.getPQty()));
                detailColumns.add("PRICE", DataValues.newString(price));
                detailColumns.add("AMT", DataValues.newString(amt));
                detailColumns.add("DISTRIPRICE", DataValues.newString(distriPrice));
                detailColumns.add("DISTRIAMT", DataValues.newString(distriAmt));
                detailColumns.add("BASEUNIT", DataValues.newString(baseunit));
                detailColumns.add("BASEQTY", DataValues.newString(baseQty));
                detailColumns.add("UNIT_RATIO", DataValues.newString(unitRatio));
                detailColumns.add("BDATE", DataValues.newString(createDate));
                detailColumns.add("STOCKQTY", DataValues.newString(stockQty));
                detailColumns.add("PARTITION_DATE", DataValues.newString(createDate));
                detailColumns.add("POQTY", DataValues.newString(poqty));
                detailColumns.add("OFNO", DataValues.newString(noticeNo));
                detailColumns.add("OOTYPE", DataValues.newString(sourcetype));
                detailColumns.add("OOFNO", DataValues.newString(sourcebillno));
                detailColumns.add("OOITEM", DataValues.newString(oitem));
                detailColumns.add("STOCKOUTNOQTY", DataValues.newString(stockoutNoQty));

                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib1 = new InsBean("DCP_STOCKOUT_DETAIL", detailColumnNames);
                ib1.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(ib1));

                //DCP_STOCKOUT_BATCH
                batchItem++;

                ColumnDataValue batchColumns = new ColumnDataValue();
                batchColumns.add("EID", DataValues.newString(eId));
                batchColumns.add("ORGANIZATIONNO", DataValues.newString(deliverorgno));
                batchColumns.add("SHOPID", DataValues.newString(deliverorgno));
                batchColumns.add("STOCKOUTNO", DataValues.newString(orderNO));
                batchColumns.add("ITEM", DataValues.newString(batchItem));
                batchColumns.add("ITEM2", DataValues.newString(detailItem));
                batchColumns.add("PLUNO", DataValues.newString(detail.getPluNo()));
                batchColumns.add("FEATURENO", DataValues.newString(Check.Null(detail.getFeatureNo()) ? " " : detail.getFeatureNo()));
                batchColumns.add("WAREHOUSE", DataValues.newString(detail.getWarehouse()));
                batchColumns.add("LOCATION", DataValues.newString(" "));
                batchColumns.add("BATCHNO", DataValues.newString(" "));
                batchColumns.add("PRODDATE", DataValues.newString(""));
                batchColumns.add("EXPDATE", DataValues.newString(""));
                batchColumns.add("PUNIT", DataValues.newString(detail.getPUnit()));
                batchColumns.add("PQTY", DataValues.newString(detail.getPQty()));
                batchColumns.add("BASEUNIT", DataValues.newString(baseunit));
                batchColumns.add("BASEQTY", DataValues.newString(baseQty));
                batchColumns.add("UNITRATIO", DataValues.newString(unitRatio));


                String[] batchColumnNames = batchColumns.getColumns().toArray(new String[0]);
                DataValue[] batchDataValues = batchColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib2 = new InsBean("DCP_STOCKOUT_BATCH", batchColumnNames);
                ib2.addValues(batchDataValues);
                this.addProcessData(new DataProcessBean(ib2));

                totPqty = totPqty.add(new BigDecimal(detail.getPQty()));
                totAmt = totAmt.add(amt);
                totDistriAmt = totDistriAmt.add(distriAmt);

                //更新通知单明细状态4
                UptBean ub1 = new UptBean("DCP_STOCKOUTNOTICE_DETAIL");
                //add Value
                ub1.addUpdateValue("STATUS", new DataValue("4", Types.VARCHAR));

                //condition
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("BILLNO", new DataValue(noticeNo, Types.VARCHAR));
                ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                ub1.addCondition("ITEM", new DataValue(detail.getNoticeItem(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));

                DCP_InterSettleDataGenerateReq.Detail inDetail = inReq.new Detail();
                inDetail.setReceiveOrgNo(receiptOrg);
                inDetail.setSourceBillNo(noticeNo);
                inDetail.setSourceItem(detail.getNoticeItem());
                inDetail.setItem(String.valueOf(detailItem));
                inDetail.setPluNo(detail.getPluNo());
                inDetail.setFeatureNo(detail.getFeatureNo());
                inDetail.setPUnit(detail.getPUnit());
                inDetail.setPQty(detail.getPQty());
                inDetail.setReceivePrice(String.valueOf(distriPrice));
                inDetail.setReceiveAmt(String.valueOf(distriAmt));
                inDetail.setSupplyPrice("");
                inDetail.setSupplyAmt("");
                request1.getDetail().add(inDetail);

            }
            inReq.setRequest(request1);
            if(!corp.equals(receiptCorp)){
                interReqs.add(inReq);
            }

            List<Map<String, String>> pluNos = detailList.stream().map(x -> {
                Map<String, String> singlMap = new HashMap();
                singlMap.put("pluNo", x.getPluNo().toString());
                singlMap.put("featureNo", x.getFeatureNo().toString());
                return singlMap;
            }).distinct().collect(Collectors.toList());
            totCqty = new BigDecimal(pluNos.size());

            //单头
            ColumnDataValue mainColumns = new ColumnDataValue();
            mainColumns.add("EID", DataValues.newString(eId));
            mainColumns.add("SHOPID", DataValues.newString(deliverorgno));
            mainColumns.add("ORGANIZATIONNO", DataValues.newString(deliverorgno));

            mainColumns.add("CORP", DataValues.newString(corp));
            mainColumns.add("RECEIPTCORP", DataValues.newString(receiptCorp));

            mainColumns.add("BDATE", DataValues.newString(createDate));
            mainColumns.add("STOCKOUTNO", DataValues.newString(orderNO));
            mainColumns.add("DOC_TYPE", DataValues.newString("5"));//doc_Type=5-配货出库
            mainColumns.add("OTYPE", DataValues.newString("3"));//3-配货通知
            mainColumns.add("OFNO", DataValues.newString(noticeNo));
            mainColumns.add("RECEIPT_ORG", DataValues.newString(receiptOrg));
            mainColumns.add("WAREHOUSE", DataValues.newString(warehouse));
            mainColumns.add("INVWAREHOUSE", DataValues.newString(invWarehouse));
            mainColumns.add("LOAD_DOCTYPE", DataValues.newString(""));
            mainColumns.add("LOAD_DOCNO", DataValues.newString(""));
            mainColumns.add("LOAD_DOCSHOP", DataValues.newString(""));
            mainColumns.add("TRANSFER_SHOP", DataValues.newString(receiptOrg));
            mainColumns.add("TRANSFER_WAREHOUSE", DataValues.newString(transferWarehouse));
            mainColumns.add("TOT_CQTY", DataValues.newString(totCqty));
            mainColumns.add("TOT_PQTY", DataValues.newString(totPqty));
            mainColumns.add("TOT_DISTRIAMT", DataValues.newString(totDistriAmt));
            mainColumns.add("TOT_AMT", DataValues.newString(totAmt));
            mainColumns.add("BSNO", DataValues.newString(""));
            mainColumns.add("DELIVERY_NO", DataValues.newString(""));
            mainColumns.add("CREATEBY", DataValues.newString(req.getOpNO()));
            mainColumns.add("CREATE_DATE", DataValues.newString(createDate));
            mainColumns.add("CREATE_TIME", DataValues.newString(createTime));
            mainColumns.add("PTEMPLATENO", DataValues.newString(templateNo));
            mainColumns.add("STATUS", DataValues.newString("0"));
            mainColumns.add("PARTITION_DATE", DataValues.newString(createDate));

            mainColumns.add("EMPLOYEEID", DataValues.newString(employeeNo));
            mainColumns.add("DEPARTID", DataValues.newString(departmentNo));
            mainColumns.add("RECEIPTDATE", DataValues.newString(createDate));
            String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
            DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
            InsBean ib1 = new InsBean("DCP_STOCKOUT", mainColumnNames);
            ib1.addValues(mainDataValues);
            this.addProcessData(new DataProcessBean(ib1));

            //更新通知单明细状态4
            UptBean ub1 = new UptBean("DCP_STOCKOUTNOTICE");
            //add Value
            ub1.addUpdateValue("STATUS", new DataValue("4", Types.VARCHAR));

            //condition
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("BILLNO", new DataValue(noticeNo, Types.VARCHAR));
            ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            docNos.add(orderNO);

        }


        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

        //循环调用内部交易
        if(interReqs.size()>0){
            for (DCP_InterSettleDataGenerateReq inreq : interReqs){
                ParseJson pj = new ParseJson();
                String jsontemp = pj.beanToJson(inreq);

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


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockOutCreateInBatchReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockOutCreateInBatchReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockOutCreateInBatchReq req) throws Exception {
        return null;
    }

    @Override
    protected String getQuerySql(DCP_StockOutCreateInBatchReq req) throws Exception {
        return null;
    }


}

