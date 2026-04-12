package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataGenerateReq;
import com.dsc.spos.json.cust.req.DCP_ReceivingCreateReq;
import com.dsc.spos.json.cust.req.DCP_ReceivingUpdateReq;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataGenerateRes;
import com.dsc.spos.json.cust.res.DCP_ReceivingUpdateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ReceivingUpdate extends SPosAdvanceService<DCP_ReceivingUpdateReq, DCP_ReceivingUpdateRes> {
    @Override
    protected void processDUID(DCP_ReceivingUpdateReq req, DCP_ReceivingUpdateRes res) throws Exception {
        try {

            if(Check.Null(req.getRequest().getCorp())){
                String orgSql1="select * from dcp_org where eid='"+req.geteId()+"' and organizationno='"+req.getOrganizationNO()+"' ";
                List<Map<String, Object>> orgList1 = this.doQueryData(orgSql1, null);
                if(CollUtil.isNotEmpty(orgList1)){
                    req.getRequest().setCorp( orgList1.get(0).get("CORP").toString());
                }
            }
            if(Check.Null(req.getRequest().getReceiptCorp())){
                String orgSql1="select * from dcp_org where eid='"+req.geteId()+"' and organizationno='"+req.getRequest().getReceiptOrgNo()+"' ";
                List<Map<String, Object>> orgList1 = this.doQueryData(orgSql1, null);
                if(CollUtil.isNotEmpty(orgList1)){
                    req.getRequest().setReceiptCorp( orgList1.get(0).get("CORP").toString());
                }
            }

            if(Check.NotNull(req.getRequest().getOfNo())&&("3".equals(req.getRequest().getDocType())||"4".equals(req.getRequest().getDocType()))){
                String prSql="select * from dcp_purorder_delivery a where a.eid='"+req.geteId()+"' and a.purorderno='"+req.getRequest().getOfNo()+"' ";
                List<Map<String, Object>> prList = this.doQueryData(prSql, null);
                for (DCP_ReceivingUpdateReq.Receipt detail : req.getRequest().getDataList()){
                    List<Map<String, Object>> prFilterRows = prList.stream().filter(x -> x.get("ITEM").toString().equals(detail.getOItem())).collect(Collectors.toList());
                    if(Check.Null(detail.getPurPrice())){
                        if(prFilterRows.size()>0){
                            BigDecimal purPrice =new BigDecimal(prFilterRows.get(0).get("PURPRICE").toString());
                            detail.setPurPrice(purPrice.toString());
                            BigDecimal purAmt = purPrice.multiply(new BigDecimal(detail.getPQty()));
                            detail.setPurAmt(purAmt.toString());

                        }
                    }
                    if(Check.Null(detail.getDistriPrice())||new BigDecimal(detail.getDistriPrice()).compareTo(BigDecimal.ZERO)==0){
                        if(prFilterRows.size()>0){
                            BigDecimal distriPrice =new BigDecimal(prFilterRows.get(0).get("RECEIVEPRICE").toString());
                            detail.setDistriPrice(distriPrice.toString());
                            BigDecimal distriAmt = distriPrice.multiply(new BigDecimal(detail.getPQty()));
                            detail.setDistriAmt(distriAmt.toString());
                        }
                    }


                    if(Check.Null(detail.getSupPrice())||new BigDecimal(detail.getSupPrice()).compareTo(BigDecimal.ZERO)==0){
                        if(prFilterRows.size()>0){
                            BigDecimal supplyPrice =new BigDecimal(prFilterRows.get(0).get("SUPPRICE").toString());
                            detail.setSupPrice(supplyPrice.toString());
                            BigDecimal supplyAmt = supplyPrice.multiply(new BigDecimal(detail.getPQty()));
                            detail.setSupAmt(supplyAmt.toString());
                        }
                    }

                    if(Check.Null(detail.getCategory())){
                        String goodSql="select * from dcp_goods a where a.eid='"+req.geteId()+"' and a.pluno='"+detail.getPluNo()+"' ";
                        List<Map<String, Object>> goodList = this.doQueryData(goodSql, null);
                        if(goodList.size()>0){
                            detail.setCategory(goodList.get(0).get("CATEGORY").toString());
                        }
                    }
                }
            }



            if(Check.Null(req.getRequest().getTotPurAmt())) {
                BigDecimal totPurAmt=new BigDecimal(0);
                for (DCP_ReceivingUpdateReq.Receipt detail : req.getRequest().getDataList()) {
                    totPurAmt=totPurAmt.add(new BigDecimal(Check.Null(detail.getPurAmt())?"0":detail.getPurAmt()));
                }
                req.getRequest().setTotPurAmt(totPurAmt.toString());
            }


            if ("3".equals(req.getRequest().getDocType())) {
                String querySql = " SELECT b.EID,b.PLUNO,b.FEATURENO,c.BASEUNIT,d.UNIT_RATIO, " +
                        "   b.PURUNIT FROM DCP_PURORDER a " +
                        " LEFT JOIN DCP_PURORDER_DELIVERY b ON a.EID=b.EID AND a.PURORDERNO=b.PURORDERNO " +
                        " LEFT JOIN DCP_GOODS c ON c.EID=a.EID AND c.PLUNO=b.PLUNO  " +
                        " LEFT JOIN DCP_UNITCONVERT d ON d.EID=a.EID AND d.UNIT=b.PURUNIT AND d.OUNIT=c.BASEUNIT " +
                        " WHERE a.EID='%s' AND a.PURORDERNO='%s' ";
                querySql = String.format(querySql, req.geteId(), req.getRequest().getOfNo());
                List<Map<String, Object>> data = doQueryData(querySql, null);

                if (CollectionUtils.isEmpty(data)) {
                    res.setSuccess(false);
                    res.setServiceStatus("000");
                    res.setServiceDescription(req.getRequest().getLoadDocNo() + "不存在采购订单中！");
                    return;
                }
            }

            if("3".equals(req.getRequest().getDocType())||"4".equals(req.getRequest().getDocType())){
                if(Check.NotNull(req.getRequest().getOfNo())){
                    String sql="select * from dcp_purorder a where a.eid='"+req.geteId()+"' and a.purorderno='"+req.getRequest().getOfNo()+"'";
                    List<Map<String, Object>> purOrderData = this.doQueryData(sql, null);
                    if(purOrderData.size()>0){
                        //payType,payOrgNo,payDateNo,billDateNo,invoiceCode,currency,payee
                        req.getRequest().setPayType(purOrderData.get(0).get("PAYTYPE").toString());
                        req.getRequest().setPayOrgNo(purOrderData.get(0).get("PAYORGNO").toString());
                        req.getRequest().setPayDateNo(purOrderData.get(0).get("PAYDATENO").toString());
                        req.getRequest().setBillDateNo(purOrderData.get(0).get("BILLDATENO").toString());
                        req.getRequest().setInvoiceCode(purOrderData.get(0).get("INVOICECODE").toString());
                        req.getRequest().setCurrency(purOrderData.get(0).get("CURRENCY").toString());
                        req.getRequest().setPayee(purOrderData.get(0).get("PAYEE").toString());
                    }
                }else{
                    String sql="select * from dcp_bizpartner a where a.eid='"+req.geteId()+"' and a.bizpartnerno='"+req.getRequest().getSupplierNo()+"'";
                    List<Map<String, Object>> bizData = this.doQueryData(sql, null);
                    if(bizData.size()>0){
                        //payType,payOrgNo,payDateNo,billDateNo,invoiceCode,currency,payee
                        req.getRequest().setPayType(bizData.get(0).get("PAYTYPE").toString());
                        req.getRequest().setPayOrgNo(bizData.get(0).get("PAYORGNO").toString());
                        req.getRequest().setPayDateNo(bizData.get(0).get("PAYDATENO").toString());
                        req.getRequest().setBillDateNo(bizData.get(0).get("BILLDATENO").toString());
                        req.getRequest().setInvoiceCode(bizData.get(0).get("INVOICECODE").toString());
                        req.getRequest().setCurrency(bizData.get(0).get("CURRENCY").toString());
                        req.getRequest().setPayee(bizData.get(0).get("PAYEE").toString());
                    }
                }
            }
            else if("6".equals(req.getRequest().getDocType())){
                if(Check.NotNull(req.getRequest().getOfNo())){
                    String sql="select * from DCP_CUSTOMERPORDER a where a.eid='"+req.geteId()+"' and a.porderno='"+req.getRequest().getOfNo()+"'";
                    List<Map<String, Object>> custOrderData = this.doQueryData(sql, null);
                    if(custOrderData.size()>0){
                        req.getRequest().setPayType(custOrderData.get(0).get("PAYTYPE").toString());
                        req.getRequest().setPayOrgNo(custOrderData.get(0).get("PAYORGNO").toString());
                        req.getRequest().setPayDateNo(custOrderData.get(0).get("PAYDATENO").toString());
                        req.getRequest().setBillDateNo(custOrderData.get(0).get("BILLDATENO").toString());
                        req.getRequest().setInvoiceCode(custOrderData.get(0).get("INVOICECODE").toString());
                        req.getRequest().setCurrency(custOrderData.get(0).get("CURRENCY").toString());
                        req.getRequest().setPayer(custOrderData.get(0).get("PAYER").toString());
                    }
                }else{
                    String sql="select * from dcp_bizpartner a where a.eid='"+req.geteId()+"' and a.bizpartnerno='"+req.getRequest().getCustomer()+"'";
                    List<Map<String, Object>> bizData = this.doQueryData(sql, null);
                    if(bizData.size()>0){
                        req.getRequest().setPayType(bizData.get(0).get("PAYTYPE").toString());
                        req.getRequest().setPayOrgNo(bizData.get(0).get("PAYORGNO").toString());
                        req.getRequest().setPayDateNo(bizData.get(0).get("PAYDATENO").toString());
                        req.getRequest().setBillDateNo(bizData.get(0).get("BILLDATENO").toString());
                        req.getRequest().setInvoiceCode(bizData.get(0).get("INVOICECODE").toString());
                        req.getRequest().setCurrency(bizData.get(0).get("CURRENCY").toString());
                        req.getRequest().setPayer(bizData.get(0).get("PAYER").toString());
                    }
                }
            }

            if(Check.Null(req.getRequest().getReceiptAddress())){
                String orgNo="select * from dcp_org where eid='"+req.geteId()+"' and organizationno='"+req.getRequest().getReceiptOrgNo()+"' ";
                List<Map<String, Object>> orgData = this.doQueryData(orgNo, null);
                if(CollUtil.isNotEmpty(orgData)){
                    req.getRequest().setReceiptAddress(orgData.get(0).get("ADDRESS").toString());
                }
            }


            String nowDateTime = DateFormatUtils.getNowDateTime();

            ColumnDataValue dcp_receiving = new ColumnDataValue();
            ColumnDataValue condition = new ColumnDataValue();

            condition.add("EID", DataValues.newString(req.geteId()));
            condition.add("SHOPID", DataValues.newString(req.getRequest().getReceiptOrgNo()));
            condition.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrgNo()));
            condition.add("RECEIVINGNO", DataValues.newString(req.getRequest().getReceivingNo()));

            dcp_receiving.add("BDATE", DataValues.newString(DateFormatUtils.getPlainDate(req.getRequest().getBDate())));
            dcp_receiving.add("DOC_TYPE", DataValues.newString(req.getRequest().getDocType()));
            dcp_receiving.add("LOAD_DOCTYPE", DataValues.newString(req.getRequest().getLoadDocType()));
            dcp_receiving.add("LOAD_DOCNO", DataValues.newString(req.getRequest().getLoadDocNo()));
            dcp_receiving.add("STATUS", DataValues.newInteger(0));

            dcp_receiving.add("MEMO", DataValues.newString(req.getRequest().getMemo()));
            dcp_receiving.add("SUPPLIER", DataValues.newString(req.getRequest().getSupplierNo()));
            dcp_receiving.add("RECEIPTDATE", DataValues.newString(DateFormatUtils.getPlainDate(req.getRequest().getReceiptDate())));
            dcp_receiving.add("WAREHOUSE", DataValues.newString(req.getRequest().getWareHouse()));
            dcp_receiving.add("DELIVERY_NO", DataValues.newString(req.getRequest().getDeliveryNo()));
            dcp_receiving.add("RDATE", DataValues.newString(DateFormatUtils.getPlainDate(req.getRequest().getReceiptDate())));
            dcp_receiving.add("MODIFYBY", DataValues.newString(req.getOpNO()));
            dcp_receiving.add("MODIFY_DATE", DataValues.newString(DateFormatUtils.getPlainDate(nowDateTime)));
            dcp_receiving.add("MODIFY_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));
            dcp_receiving.add("RECEIPTORGNO", DataValues.newString(req.getRequest().getReceiptOrgNo()));
            dcp_receiving.add("RECEIPTADDRESS", DataValues.newString(req.getRequest().getReceiptAddress()));

            dcp_receiving.add("EMPLOYEEID", DataValues.newString(req.getRequest().getEmployeeID()));
            dcp_receiving.add("DEPARTID", DataValues.newString(req.getRequest().getDepartID()));
            dcp_receiving.add("OTYPE", DataValues.newString(req.getRequest().getOType()));
            dcp_receiving.add("OFNO", DataValues.newString(req.getRequest().getOfNo()));

            dcp_receiving.add("PAYTYPE", DataValues.newString(req.getRequest().getPayType()));
            dcp_receiving.add("PAYORGNO", DataValues.newString(req.getRequest().getPayOrgNo()));
            dcp_receiving.add("PAYDATENO", DataValues.newString(req.getRequest().getPayDateNo()));
            dcp_receiving.add("BILLDATENO", DataValues.newString(req.getRequest().getBillDateNo()));
            dcp_receiving.add("INVOICECODE", DataValues.newString(req.getRequest().getInvoiceCode()));
            dcp_receiving.add("CURRENCY", DataValues.newString(req.getRequest().getCurrency()));
            dcp_receiving.add("PAYEE", DataValues.newString(req.getRequest().getPayee()));
            dcp_receiving.add("PAYER", DataValues.newString(req.getRequest().getPayer()));
            dcp_receiving.add("CUSTOMER", DataValues.newString(req.getRequest().getCustomer()));
            dcp_receiving.add("CORP", DataValues.newString(req.getRequest().getCorp()));
            dcp_receiving.add("RECEIPTCORP", DataValues.newString(req.getRequest().getReceiptCorp()));
            dcp_receiving.add("TOTPURAMT", DataValues.newString(req.getRequest().getTotPurAmt()));


            double totPqty = 0;
            double totAmt = 0;
            double totDistriAmt = 0;
            double totCqty = 0;
            for (DCP_ReceivingUpdateReq.Receipt detail : req.getRequest().getDataList()) {
                totPqty += StringUtils.isEmpty(detail.getPQty()) ? 0 : Double.parseDouble(detail.getPQty());
                totAmt += StringUtils.isEmpty(detail.getAmt()) ? 0 : Double.parseDouble(detail.getAmt());
                totCqty += 1;
                totDistriAmt += StringUtils.isEmpty(detail.getDistriAmt()) ? 0 : Double.parseDouble(detail.getDistriAmt());

            }

            if (StringUtils.isEmpty(req.getRequest().getTotPqty())) {
                dcp_receiving.add("TOT_PQTY", DataValues.newDecimal(totPqty));
            } else {
                dcp_receiving.add("TOT_PQTY", DataValues.newDecimal(req.getRequest().getTotPqty()));
            }

            if (StringUtils.isEmpty(req.getRequest().getTotAmt())) {
                dcp_receiving.add("TOT_AMT", DataValues.newDecimal(totAmt));
            } else {
                dcp_receiving.add("TOT_AMT", DataValues.newDecimal(req.getRequest().getTotAmt()));
            }
            if (StringUtils.isEmpty(req.getRequest().getTotCqty())) {
                dcp_receiving.add("TOT_CQTY", DataValues.newDecimal(totCqty));
            } else {
                dcp_receiving.add("TOT_CQTY", DataValues.newDecimal(req.getRequest().getTotCqty()));
            }
            if (StringUtils.isEmpty(req.getRequest().getTotDistriAmt())) {
                dcp_receiving.add("TOT_DISTRIAMT", DataValues.newDecimal(totDistriAmt));
            } else {
                dcp_receiving.add("TOT_DISTRIAMT", DataValues.newDecimal(req.getRequest().getTotDistriAmt()));
            }


            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("dcp_receiving", condition, dcp_receiving)));


            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("dcp_receiving_detail", condition)));

            List<Map<String, Object>> getUnitData = doQueryData(getGoodsUnitConvert(req), null);

            for (DCP_ReceivingUpdateReq.Receipt detail : req.getRequest().getDataList()) {
                Map<String, Object> pluCondition = Maps.newConcurrentMap();

                pluCondition.put("EID", req.geteId());
                pluCondition.put("PLUNO", detail.getPluNo());
                pluCondition.put("OUNIT", detail.getPUnit());
                if(Check.Null(detail.getOType())){
                    detail.setOType(req.getRequest().getOType());
                }
                if(Check.Null(detail.getOfNo())){
                    detail.setOfNo(req.getRequest().getOfNo());
                }


                List<Map<String, Object>> pluList = MapDistinct.getWhereMap(getUnitData, pluCondition, false);
                Map<String, Object> pluInfo = pluList.get(0);

                totPqty += StringUtils.isEmpty(detail.getPQty()) ? 0 : Double.parseDouble(detail.getPQty());
                totAmt += StringUtils.isEmpty(detail.getAmt()) ? 0 : Double.parseDouble(detail.getAmt());
                totCqty += 1;
                totDistriAmt += StringUtils.isEmpty(detail.getDistriAmt()) ? 0 : Double.parseDouble(detail.getDistriAmt());

                ColumnDataValue dcp_receiving_detail = new ColumnDataValue();
                dcp_receiving_detail.add("EID", DataValues.newString(req.geteId()));
                dcp_receiving_detail.add("RECEIVINGNO", DataValues.newString(req.getRequest().getReceivingNo()));
                dcp_receiving_detail.add("SHOPID", DataValues.newString(req.getRequest().getReceiptOrgNo()));
                dcp_receiving_detail.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrgNo()));

                dcp_receiving_detail.add("ITEM", DataValues.newInteger(detail.getItem()));

                dcp_receiving_detail.add("OTYPE", DataValues.newString(detail.getOType()));
                dcp_receiving_detail.add("OFNO", DataValues.newString(detail.getOfNo()));
                dcp_receiving_detail.add("OITEM", DataValues.newInteger(detail.getOItem()));
                dcp_receiving_detail.add("OITEM2", DataValues.newString(detail.getOItem2()));

                dcp_receiving_detail.add("PLUNO", DataValues.newString(detail.getPluNo()));
                dcp_receiving_detail.add("PUNIT", DataValues.newString(detail.getPUnit()));
                dcp_receiving_detail.add("PQTY", DataValues.newDecimal(detail.getPQty()));

                if (StringUtils.isEmpty(detail.getBaseUnit())) {
                    dcp_receiving_detail.add("BASEUNIT", DataValues.newString(pluInfo.get("BASEUNIT")));
                    double unitRatio = Double.parseDouble(pluInfo.get("UNITRATIO").toString());
                    dcp_receiving_detail.add("UNIT_RATIO", DataValues.newDecimal(unitRatio));
                    dcp_receiving_detail.add("BASEQTY", DataValues.newDecimal(BigDecimalUtils.mul(unitRatio, Double.parseDouble(detail.getPQty()))));

                } else {
                    dcp_receiving_detail.add("BASEUNIT", DataValues.newString(detail.getBaseUnit()));
                    dcp_receiving_detail.add("UNIT_RATIO", DataValues.newDecimal(detail.getUnitRatio()));
                    dcp_receiving_detail.add("BASEQTY", DataValues.newDecimal(detail.getBaseQty()));

                }

                dcp_receiving_detail.add("PRICE", DataValues.newString(detail.getPrice()));
                dcp_receiving_detail.add("AMT", DataValues.newString(detail.getAmt()));

                dcp_receiving_detail.add("POQTY", DataValues.newString(detail.getPQty()));
                dcp_receiving_detail.add("PROC_RATE", DataValues.newDecimal(0));
                dcp_receiving_detail.add("WAREHOUSE", DataValues.newString(req.getRequest().getWareHouse()));
                dcp_receiving_detail.add("PLU_MEMO", DataValues.newString(detail.getMemo()));
                dcp_receiving_detail.add("BATCH_NO", DataValues.newString(detail.getBatchNo()));
                dcp_receiving_detail.add("PROD_DATE", DataValues.newString(detail.getProdDate()));
                dcp_receiving_detail.add("DISTRIPRICE", DataValues.newDecimal(detail.getDistriPrice()));
                dcp_receiving_detail.add("DISTRIAMT", DataValues.newDecimal(detail.getDistriAmt()));
                dcp_receiving_detail.add("BDATE", DataValues.newDecimal(DateFormatUtils.getTimestamp()));
                dcp_receiving_detail.add("FEATURENO", DataValues.newString(detail.getFeatureNo()));
                dcp_receiving_detail.add("PACKINGNO", DataValues.newString(""));
                dcp_receiving_detail.add("STATUS", DataValues.newInteger(0));

                dcp_receiving_detail.add("PLU_BARCODE", DataValues.newString(detail.getPluBarcode()));
                dcp_receiving_detail.add("TAXCODE", DataValues.newString(detail.getTaxCode()));
                dcp_receiving_detail.add("TAXRATE", DataValues.newString(detail.getTaxRate()));
                dcp_receiving_detail.add("INCLTAX", DataValues.newString(detail.getInclTax()));
                dcp_receiving_detail.add("ISGIFT", DataValues.newDecimal(detail.getIsGift()));
                dcp_receiving_detail.add("TAXCALTYPE", DataValues.newDecimal(detail.getTaxCalType()));

                dcp_receiving_detail.add("PURPRICE", DataValues.newDecimal(detail.getPurPrice()));
                dcp_receiving_detail.add("PURAMT", DataValues.newDecimal(detail.getPurAmt()));
                dcp_receiving_detail.add("CATEGORY", DataValues.newDecimal(detail.getCategory()));
                dcp_receiving_detail.add("SUPPRICE",DataValues.newString(detail.getSupPrice()));
                dcp_receiving_detail.add("SUPAMT",DataValues.newString(detail.getSupAmt()));



                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("dcp_receiving_detail", dcp_receiving_detail)));
            }


            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");

            if("3".equals(req.getRequest().getDocType())||"4".equals(req.getRequest().getDocType())){
                if(req.getRequest().getCorp().equals(req.getRequest().getReceiptCorp())){
                    DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                    inReq.setServiceId("DCP_InterSettleDataGenerate");
                    inReq.setToken(req.getToken());
                    DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                    request1.setOrganizationNo(req.getOrganizationNO());
                    request1.setBillNo(req.getRequest().getReceivingNo());
                    request1.setSupplyOrgNo(req.getOrganizationNO());
                    request1.setReturnSupplyPrice("Y");
                    request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType_11001.getType());
                    request1.setDetail(new ArrayList<>());
                    for (DCP_ReceivingUpdateReq.Receipt par : req.getRequest().getDataList()) {
                        DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                        detail.setReceiveOrgNo(req.getRequest().getReceiptOrgNo());
                        detail.setSourceBillNo("");
                        detail.setSourceItem("");

                        detail.setSourceBillNo(par.getOfNo());
                        detail.setSourceItem(par.getOItem2());

                        detail.setItem(String.valueOf(par.getItem()));
                        detail.setPluNo(par.getPluNo());
                        detail.setFeatureNo(par.getFeatureNo());
                        detail.setPUnit(par.getPUnit());
                        detail.setPQty(String.valueOf(par.getPQty()));
                        detail.setReceivePrice(String.valueOf(par.getDistriPrice()));
                        detail.setReceiveAmt(String.valueOf(par.getDistriAmt()));
                        detail.setSupplyPrice(String.valueOf(par.getSupPrice()));
                        detail.setSupplyAmt(String.valueOf(par.getSupAmt()));
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
                    }else{
                        /**
                        List<DCP_InterSettleDataGenerateRes.SupPriceDetail> supPriceDetail = resserver.getSupPriceDetail();
                        if(supPriceDetail.size()>0){
                            for(DCP_InterSettleDataGenerateRes.SupPriceDetail supd:supPriceDetail){
                                String item = supd.getItem();
                                String receivePrice = supd.getReceivePrice();
                                String supplyPrice = supd.getSupplyPrice();

                                List<DCP_ReceivingUpdateReq.Receipt> collect = req.getRequest().getDataList().stream().filter(x -> x.getItem().equals(item)).collect(Collectors.toList());
                                if(CollUtil.isNotEmpty(collect)){
                                    BigDecimal pQty = new BigDecimal(collect.get(0).getPQty());
                                    BigDecimal supplyAmt = pQty.multiply(new BigDecimal(supplyPrice));
                                    UptBean ub1 = new UptBean("DCP_RECEIVING_DETAIL");
                                    ub1.addUpdateValue("SUPPRICE",DataValues.newString(supplyPrice));
                                    ub1.addUpdateValue("SUPAMT",DataValues.newString(supplyAmt));

                                    ub1.addCondition("EID", DataValues.newString(req.geteId()));
                                    ub1.addCondition("RECEIVINGNO",DataValues.newString(req.getRequest().getReceivingNo()));
                                    ub1.addCondition("ITEM",DataValues.newString(item));
                                    this.addProcessData(new DataProcessBean(ub1));
                                }

                            }

                            this.doExecuteDataToDB();
                        }
**/
                    }
                }
            }

            if("6".equals(req.getRequest().getDocType())){
                if(req.getRequest().getCorp().equals(req.getRequest().getReceiptCorp())){
                    DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                    inReq.setServiceId("DCP_InterSettleDataGenerate");
                    inReq.setToken(req.getToken());
                    DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                    request1.setOrganizationNo(req.getOrganizationNO());
                    request1.setBillNo(req.getRequest().getReceivingNo());
                    request1.setSupplyOrgNo(req.getRequest().getReceiptOrgNo());
                    request1.setReturnSupplyPrice("Y");
                    request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType12001.getType());
                    request1.setDetail(new ArrayList<>());
                    for (DCP_ReceivingUpdateReq.Receipt par : req.getRequest().getDataList()) {
                        DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                        detail.setReceiveOrgNo(req.getOrganizationNO());
                        detail.setSourceBillNo("");
                        detail.setSourceItem("");

                        detail.setSourceBillNo(par.getOfNo());
                        detail.setSourceItem(par.getOItem2());

                        detail.setItem(String.valueOf(par.getItem()));
                        detail.setPluNo(par.getPluNo());
                        detail.setFeatureNo(par.getFeatureNo());
                        detail.setPUnit(par.getPUnit());
                        detail.setPQty(String.valueOf(par.getPQty()));
                        detail.setReceivePrice(String.valueOf(par.getDistriPrice()));
                        detail.setReceiveAmt(String.valueOf(par.getDistriAmt()));
                        detail.setSupplyPrice(String.valueOf(par.getSupPrice()));
                        detail.setSupplyAmt(String.valueOf(par.getSupAmt()));
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
                    }else{
                        /**
                         List<DCP_InterSettleDataGenerateRes.SupPriceDetail> supPriceDetail = resserver.getSupPriceDetail();
                         if(supPriceDetail.size()>0){
                         for(DCP_InterSettleDataGenerateRes.SupPriceDetail supd:supPriceDetail){
                         String item = supd.getItem();
                         String receivePrice = supd.getReceivePrice();
                         String supplyPrice = supd.getSupplyPrice();

                         List<DCP_ReceivingUpdateReq.Receipt> collect = req.getRequest().getDataList().stream().filter(x -> x.getItem().equals(item)).collect(Collectors.toList());
                         if(CollUtil.isNotEmpty(collect)){
                         BigDecimal pQty = new BigDecimal(collect.get(0).getPQty());
                         BigDecimal supplyAmt = pQty.multiply(new BigDecimal(supplyPrice));
                         UptBean ub1 = new UptBean("DCP_RECEIVING_DETAIL");
                         ub1.addUpdateValue("SUPPRICE",DataValues.newString(supplyPrice));
                         ub1.addUpdateValue("SUPAMT",DataValues.newString(supplyAmt));

                         ub1.addCondition("EID", DataValues.newString(req.geteId()));
                         ub1.addCondition("RECEIVINGNO",DataValues.newString(req.getRequest().getReceivingNo()));
                         ub1.addCondition("ITEM",DataValues.newString(item));
                         this.addProcessData(new DataProcessBean(ub1));
                         }

                         }

                         this.doExecuteDataToDB();
                         }
                         **/
                    }
                }
            }


        } catch (Exception e) {
            //插入失败
            res.setSuccess(false);
            res.setServiceStatus("000");
            res.setServiceDescription(e.getClass().getName() + e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReceivingUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReceivingUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReceivingUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReceivingUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ReceivingUpdateReq> getRequestType() {
        return new TypeToken<DCP_ReceivingUpdateReq>() {

        };
    }

    private String getGoodsUnitConvert(DCP_ReceivingUpdateReq req) throws Exception {

        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT a.EID,a.PLUNO,b.OUNIT,a.BASEUNIT,b.UNITRATIO  ")
                .append(" FROM DCP_GOODS a")
                .append(" LEFT JOIN DCP_GOODS_UNIT b on a.eid=b.eid and a.PLUNO=b.PLUNO and a.BASEUNIT=b.UNIT ")
        ;
        sb.append(" WHERE 1=2 ");
        for (DCP_ReceivingUpdateReq.Receipt onePlu : req.getRequest().getDataList()) {
            sb.append(" OR( a.PLUNO='").append(onePlu.getPluNo()).append("' and b.OUNIT='").append(onePlu.getPUnit()).append("')");
        }

        return sb.toString();

    }

    @Override
    protected DCP_ReceivingUpdateRes getResponseType() {
        return new DCP_ReceivingUpdateRes();
    }
}
