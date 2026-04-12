package com.dsc.spos.service.imp.json;


import com.dsc.spos.json.cust.req.DCP_SStockInDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_SStockInDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.StringUtils;
import com.dsc.spos.utils.tax.TaxAmount;
import com.dsc.spos.utils.tax.TaxAmount2;
import com.dsc.spos.utils.tax.TaxAmountCalculation;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DCP_SStockInDetailQuery extends SPosBasicService<DCP_SStockInDetailQueryReq, DCP_SStockInDetailQueryRes> {


    @Override
    protected boolean isVerifyFail(DCP_SStockInDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_SStockInDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_SStockInDetailQueryReq>() {

        };
    }

    @Override
    protected DCP_SStockInDetailQueryRes getResponseType() {
        return new DCP_SStockInDetailQueryRes();
    }

    @Override
    protected DCP_SStockInDetailQueryRes processJson(DCP_SStockInDetailQueryReq req) throws Exception {
        //try {

            double totDistriPreTaxAmt = 0;
            double totDistriTaxAmt = 0;
            String isBatchSStockIn = PosPub.getPARA_SMS(dao, req.geteId(), "", "isBatchSStockIn");

            if (Check.Null(isBatchSStockIn)) {
                isBatchSStockIn = "N";
            }
            String sql = this.getQuerySql(req);
            //查询资料
            DCP_SStockInDetailQueryRes res = this.getResponseType();
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            List<Map<String, Object>> getTransaction = this.doQueryData(getQueryTransactionSql(req), null);

            int totalRecords;    //总笔数
            int totalPages;        //总页数
            res.setDatas(new ArrayList<>());
            if (getQData != null && !getQData.isEmpty()) {
                //算總頁數
//                String num = getQData.get(0).get("NUM").toString();
                totalRecords = getQData.size();
//                totalPages = totalRecords / (req.getPageSize() == 0 ? 1 : req.getPageSize());
//                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                // 拼接返回图片路径  by jinzma 20210705
                String isHttps = PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr = isHttps.equals("1") ? "https://" : "http://";
                String domainName = PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                if (domainName.endsWith("/")) {
                    domainName = httpStr + domainName + "resource/image/";
                } else {
                    domainName = httpStr + domainName + "/resource/image/";
                }
                //单头主键字段
                Map<String, Boolean> condition = new HashMap<>(); //查询条件
                condition.put("SSTOCKINNO", true);
                //调用过滤函数
                List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQData, condition);
                for (Map<String, Object> oneData : getQHeader) {
                    DCP_SStockInDetailQueryRes.Datas oneLv1 = res.new Datas();
                    oneLv1.setDatas(new ArrayList<>());
                    oneLv1.setTransaction(new ArrayList<>());

                    //取出第一层
                    String sStockInNO = oneData.get("SSTOCKINNO").toString();
                    // 进货允收方式  0.不管控  1.依进货效期管控
                    String stockInAllowType = oneData.get("STOCKINALLOWTYPE").toString();
                    Map<String, Object> conditionDetail = new HashMap<>();
                    conditionDetail.put("SSTOCKINNO", sStockInNO);
                    List<Map<String, Object>> getDetail = MapDistinct.getWhereMap(getQData, conditionDetail, true);

                    Map<String, Object> conditionTransaction = new HashMap<>();
                    conditionDetail.put("BILLNO", sStockInNO);
                    List<Map<String, Object>> getDTransaction = MapDistinct.getWhereMap(getTransaction, conditionTransaction, true);


                    for (Map<String, Object> oneData2 : getDetail) {
                        //过滤属于此单头的明细

                        DCP_SStockInDetailQueryRes.Detail oneLv2 = res.new Detail();

                        String listImage = oneData2.get("LISTIMAGE").toString();
                        if (!Check.Null(listImage)) {
                            listImage = domainName + listImage;
                        }
                        String shelfLife = oneData2.get("SHELFLIFE").toString();
                        if (Check.Null(shelfLife)) {
                            shelfLife = "0";
                        }
                        String stockInValidDay = oneData2.get("STOCKINVALIDDAY").toString();
                        if (Check.Null(stockInValidDay)) {
                            stockInValidDay = "0";
                        }
                        //供应商进货允收方式  0.不管控  1.依进货效期管控
                        if (Check.Null(stockInAllowType) || stockInAllowType.equals("0")) {
                            stockInValidDay = "0"; //进货效期==0 不管控
                        }

                        String unitRatio = oneData2.get("UNIT_RATIO").toString();
                        if (Check.Null(unitRatio)||Double.parseDouble(unitRatio) == 0) {
                            unitRatio = "1";
                        }
                        String pqty = oneData2.get("PQTY").toString();
                        String baseQty = oneData2.get("BASEQTY").toString();
                        String stockInQty = oneData2.get("STOCKIN_QTY").toString();
                        if (Check.Null(stockInQty)) {
                            stockInQty = "0";
                        }
                        //采购收货单的入库数量=发货数量 - 减去已收货数量  BY JZMA 2019-7-9
                        if (Check.Null(sStockInNO) && PosPub.isNumericType(stockInQty)) {
                            BigDecimal stockInQty_B = new BigDecimal(stockInQty);
                            BigDecimal pqty_B = new BigDecimal(pqty);
                            pqty_B = pqty_B.subtract(stockInQty_B);
                            if (pqty_B.compareTo(BigDecimal.ZERO) < 0) {
                                pqty_B = new BigDecimal("0");
                            }
                            pqty = pqty_B.toString();
                            BigDecimal baseQty_B = pqty_B.multiply(new BigDecimal(unitRatio));
                            baseQty = baseQty_B.toString();
                        }

                        String procRate = oneData2.get("PROC_RATE").toString();
                        if (Check.Null(procRate)) {
                            procRate = "0";
                        }
                        String retWqty = oneData2.get("RETWQTY").toString();
                        if (Check.Null(retWqty)) {
                            retWqty = "0";
                        }
                        BigDecimal retWqty_b = new BigDecimal(retWqty).divide(new BigDecimal(unitRatio), 7, RoundingMode.HALF_UP);

                        //单身赋值
                        oneLv2.setItem(oneData2.get("ITEM").toString());
                        oneLv2.setOItem(oneData2.get("OITEM").toString());
                        oneLv2.setPluNo(oneData2.get("PLUNO").toString());
                        oneLv2.setPluName(oneData2.get("PLU_NAME").toString());
                        oneLv2.setPunit(oneData2.get("PUNIT").toString());
                        oneLv2.setPunitName(oneData2.get("PUNITNAME").toString());
                        oneLv2.setPqty(pqty);
                        oneLv2.setReceivingQty(oneData2.get("RECEIVING_QTY").toString());
                        oneLv2.setPrice(oneData2.get("PRICE").toString());
                        oneLv2.setAmt(oneData2.get("AMT").toString());
                        oneLv2.setPoQty(oneData2.get("POQTY").toString());
                        oneLv2.setWarehouse(oneData2.get("WAREHOUSE_DETAIL").toString());
                        oneLv2.setRoutqty(retWqty_b.toPlainString());
                        oneLv2.setRoutunit(oneData2.get("PUNIT").toString());
                        oneLv2.setRoutunitName(oneData2.get("PUNITNAME").toString());
                        oneLv2.setSpec(oneData2.get("SPEC").toString());
                        oneLv2.setListImage(listImage);
                        oneLv2.setPluMemo(oneData2.get("PLU_MEMO").toString());
                        oneLv2.setStockInQty(oneData2.get("STOCKIN_QTY").toString());
                        oneLv2.setBatchNo(oneData2.get("BATCH_NO").toString());
                        oneLv2.setProdDate(oneData2.get("PROD_DATE").toString());
                        oneLv2.setDistriPrice(oneData2.get("DISTRIPRICE").toString());

                        oneLv2.setDistriAmt(oneData2.get("DISTRIAMT").toString());
                        oneLv2.setUnitRatio(unitRatio);
                        oneLv2.setIsBatch(StringUtils.toString(oneData2.get("ISBATCH"), ""));

                        oneLv2.setBaseQty(baseQty);
                        oneLv2.setBaseUnit(oneData2.get("BASEUNIT").toString());
                        oneLv2.setBaseUnitName(oneData2.get("BASEUNITNAME").toString());
                        oneLv2.setStockInValidDay(stockInValidDay);
                        oneLv2.setProcRate(procRate);
                        oneLv2.setShelfLife(shelfLife);
                        oneLv2.setPunitUdLength(oneData2.get("PUNITUDLENGTH").toString());
                        oneLv2.setFeatureNo(oneData2.get("FEATURENO").toString());

                        oneLv2.setFeatureName(oneData2.get("FEATURENAME").toString());
                        oneLv2.setBaseUnitUdLength(oneData2.get("BASEUNITUDLENGTH").toString());
                        oneLv2.setReceivingNo(StringUtils.toString(oneData2.get("DETAILRECEIVINGNO"), ""));
                        oneLv2.setRItem(StringUtils.toString(oneData2.get("RECEIVINGITEM"), ""));
                        oneLv2.setOType(StringUtils.toString(oneData2.get("DOTYPE"), ""));

                        oneLv2.setOfNo(StringUtils.toString(oneData2.get("DOFNO"), ""));
                        oneLv2.setPTemplateNo(oneData.get("DETAILPTEMPLATENO").toString());
                        oneLv2.setCategory(StringUtils.toString(oneData2.get("CATEGORY"), ""));
                        oneLv2.setCategoryName(StringUtils.toString(oneData2.get("CATEGORY_NAME"), ""));
                        oneLv2.setIsGift(StringUtils.toString(oneData2.get("ISGIFT"), ""));
                        oneLv2.setLocation(StringUtils.toString(oneData2.get("LOCATION"), ""));
                        oneLv2.setIsLocation(StringUtils.toString(oneData2.get("DETAILISLOCATION"), ""));
                        oneLv2.setLocationName(StringUtils.toString(oneData2.get("LOCATIONNAME"), ""));
                        oneLv2.setExpDate(StringUtils.toString(oneData2.get("EXP_DATE"), ""));
                        oneLv2.setMaxRate(StringUtils.toString(oneData2.get("MAXRATE"), ""));
                        oneLv2.setMinRate(StringUtils.toString(oneData2.get("MINRATE"), ""));
//                        double canStockInQty = 0;
//                        oneLv2.setCanStockInQty(String.valueOf(canStockInQty));
                        oneLv2.setCanStockInQty(oneData2.get("CANSTOCKINQTY").toString());
                        oneLv2.setOriginNo(StringUtils.toString(oneData2.get("DETAILORGIGINNO"), ""));
                        oneLv2.setOriginItem(StringUtils.toString(oneData2.get("ORIGINITEM"), ""));
                        oneLv2.setTaxCalType(StringUtils.toString(oneData2.get("TAXCALTYPE"), ""));
                        oneLv2.setRetWqty(oneData2.get("RETWQTY").toString());
                        oneLv2.setOoType(oneData2.get("DETAILOOTYPE").toString());
                        oneLv2.setOoItem(oneData2.get("OOITEM").toString());
                        oneLv2.setOofNo(oneData2.get("DETAILOOFNO").toString());
                        oneLv2.setTaxCode(oneData2.get("DETAILTAXCODE").toString());
                        oneLv2.setTaxName(oneData2.get("DETAILTAXNAME").toString());
                        oneLv2.setTaxRate(oneData2.get("DETAILTAXRATE").toString());
                        oneLv2.setInclTax(oneData2.get("INCLTAX").toString());
                        oneLv2.setPurPrice(oneData2.get("PURPRICE").toString());
                        oneLv2.setPurAmt(oneData2.get("PURAMT").toString());

                        oneLv2.setCustPrice(oneData2.get("CUSTPRICE").toString());
                        oneLv2.setRefCustPrice(oneData2.get("REFCUSTPRICE").toString());
                        oneLv2.setSdPurPrice(oneData2.get("SDPURPRICE").toString());

                        oneLv2.setRefCustPriceBaseUnit("0");
                        if(Check.NotNull(oneLv2.getRefCustPrice())){
                            BigDecimal divide = new BigDecimal(oneLv2.getRefCustPrice()).divide(new BigDecimal(unitRatio), 6, RoundingMode.HALF_UP);
                            oneLv2.setRefCustPriceBaseUnit(divide.toPlainString());
                        }
                        oneLv2.setRefDistriPriceBaseUnit("0");
                        oneLv2.setRefPurPricebaseUnit("0");

                        if(Check.NotNull(oneLv2.getDistriPrice())){
                            BigDecimal divide = new BigDecimal(oneLv2.getDistriPrice()).divide(new BigDecimal(unitRatio), 6, RoundingMode.HALF_UP);
                            oneLv2.setRefDistriPriceBaseUnit(divide.toPlainString());
                        }
                        if(Check.NotNull(oneLv2.getPurPrice())){
                            BigDecimal divide = new BigDecimal(oneLv2.getPurPrice()).divide(new BigDecimal(unitRatio), 6, RoundingMode.HALF_UP);
                            oneLv2.setRefPurPricebaseUnit(divide.toPlainString());
                        }


                        BigDecimal distriAmt = new BigDecimal(Check.Null(oneLv2.getDistriAmt())?"0":oneLv2.getDistriAmt());
                        BigDecimal taxRate = new BigDecimal(Check.Null(oneLv2.getTaxRate())?"0":oneLv2.getTaxRate());
                        TaxAmount2 taxAmount = TaxAmountCalculation.calculateAmount(
                                oneLv2.getInclTax(),
                                distriAmt,
                                taxRate,
                                oneLv2.getTaxCalType(),
                                2
                        );
                        oneLv2.setTaxAmt(taxAmount.getTaxAmount().toString());


                        oneLv1.getDatas().add(oneLv2);

                    }


                    for (Map<String, Object> oneData2 : getDTransaction) {
                        DCP_SStockInDetailQueryRes.Transaction transaction = res.new Transaction();
                        //添加transaction
                        transaction.setAmt(StringUtils.toString(oneData2.get("AMT"), ""));
                        transaction.setItem(StringUtils.toString(oneData2.get("ITEM"), ""));
                        transaction.setFeatureName(StringUtils.toString(oneData2.get("FEATURENAME"), ""));
                        transaction.setPUnit(StringUtils.toString(oneData2.get("PUNIT"), ""));
                        transaction.setPluNo(StringUtils.toString(oneData2.get("PLUNO"), ""));
                        transaction.setPluName(StringUtils.toString(oneData2.get("PLU_NAME"), ""));
                        transaction.setPUnitName(StringUtils.toString(oneData2.get("UNAME"), ""));
                        transaction.setTaxCode(StringUtils.toString(oneData2.get("TAXCODE"), ""));
                        transaction.setTaxName(StringUtils.toString(oneData2.get("TAXNAME"), ""));
                        transaction.setTaxRate(StringUtils.toString(oneData2.get("TAXRATE"), ""));
                        transaction.setPreTaxAmt(StringUtils.toString(oneData2.get("PRETAXAMT"), ""));
                        transaction.setPQty(StringUtils.toString(oneData2.get("PQTY"), ""));
                        transaction.setInclTax(StringUtils.toString(oneData2.get("INCLTAX"), ""));
                        transaction.setPrice(StringUtils.toString(oneData2.get("PRICE"), ""));
                        transaction.setFeatureNo(StringUtils.toString(oneData2.get("FEATURENO"), ""));
                        transaction.setTaxAmt(StringUtils.toString(oneData2.get("TAXAMT"), ""));
                        transaction.setTaxCalType(StringUtils.toString(oneData2.get("TAXCALTYPE"), ""));
                        totDistriTaxAmt += Double.parseDouble(StringUtils.toString(oneData2.get("TAXAMT"), "0"));
                        totDistriPreTaxAmt += Double.parseDouble(StringUtils.toString(oneData2.get("PRETAXAMT"), "0"));

                        oneLv1.getTransaction().add(transaction);

                    }

                    oneLv1.setSStockInNo(sStockInNO);
                    oneLv1.setDocType(oneData.get("DOC_TYPE").toString());
                    oneLv1.setProcessERPNo(oneData.get("PROCESS_ERP_NO").toString());
                    oneLv1.setBDate(oneData.get("BDATE").toString());
                    oneLv1.setMemo(oneData.get("MEMO").toString());
                    oneLv1.setStatus(oneData.get("STATUS").toString());
                    oneLv1.setSupplier(oneData.get("SUPPLIER").toString());
                    oneLv1.setSupplierName(oneData.get("SUPPLIER_NAME").toString());
                    oneLv1.setOType(oneData.get("OTYPE").toString());
                    oneLv1.setOfNo(oneData.get("OFNO").toString());
                    oneLv1.setPTemplateNo(oneData.get("PTEMPLATENO").toString());
                    oneLv1.setPTemplateName(oneData.get("PTEMPLATE_NAME").toString());
                    oneLv1.setLoadDocType(oneData.get("LOAD_DOCTYPE").toString());
                    oneLv1.setLoadDocNo(oneData.get("LOAD_DOCNO").toString());
                    oneLv1.setLoadReceiptNo(oneData.get("LOAD_RECEIPTNO").toString());
                    oneLv1.setWarehouse(oneData.get("WAREHOUSE_MAIN").toString());
                    oneLv1.setWarehouseName(oneData.get("WAREHOUSE_MAIN_NAME").toString());
                    oneLv1.setCreateBy(oneData.get("CREATEBY").toString());
                    oneLv1.setCreateByName(oneData.get("CREATENAME").toString());
                    oneLv1.setCreateDate(oneData.get("CREATE_DATE").toString());
                    oneLv1.setCreateTime(oneData.get("CREATE_TIME").toString());
                    oneLv1.setModifyBy(oneData.get("MODIFYBY").toString());
                    oneLv1.setModifyByName(oneData.get("MODIFYNAME").toString());
                    oneLv1.setModifyDate(oneData.get("MODIFY_DATE").toString());
                    oneLv1.setModifyTime(oneData.get("MODIFY_TIME").toString());
                    oneLv1.setSubmitBy(oneData.get("SUBMITBY").toString());
                    oneLv1.setSubmitByName(oneData.get("SUBMITNAME").toString());
                    oneLv1.setSubmitDate(oneData.get("SUBMIT_DATE").toString());
                    oneLv1.setSubmitTime(oneData.get("SUBMIT_TIME").toString());
                    oneLv1.setConfirmBy(oneData.get("CONFIRMBY").toString());
                    oneLv1.setConfirmByName(oneData.get("CONFIRMNAME").toString());
                    oneLv1.setConfirmDate(oneData.get("CONFIRM_DATE").toString());
                    oneLv1.setConfirmTime(oneData.get("CONFIRM_TIME").toString());
                    oneLv1.setCancelBy(oneData.get("CANCELBY").toString());
                    oneLv1.setCancelByName(oneData.get("CANCELNAME").toString());
                    oneLv1.setCancelDate(oneData.get("CANCEL_DATE").toString());
                    oneLv1.setCancelTime(oneData.get("CANCEL_TIME").toString());
                    oneLv1.setAccountBy(oneData.get("ACCOUNTBY").toString());
                    oneLv1.setAccountByName(oneData.get("ACCOUNTNAME").toString());
                    oneLv1.setAccountDate(oneData.get("ACCOUNT_DATE").toString());
                    oneLv1.setAccountTime(oneData.get("ACCOUNT_TIME").toString());
                    oneLv1.setUpdate_time(oneData.get("UPDATE_TIME").toString());
                    oneLv1.setProcess_status(oneData.get("PROCESS_STATUS").toString());
                    oneLv1.setTotPqty(oneData.get("TOT_PQTY").toString());
                    oneLv1.setTotAmt(oneData.get("TOT_AMT").toString());
                    oneLv1.setTotCqty(oneData.get("TOT_CQTY").toString());
                    oneLv1.setTotDistriAmt(oneData.get("TOT_DISTRIAMT").toString());
                    oneLv1.setDeliveryNo(oneData.get("DELIVERY_NO").toString());
                    oneLv1.setTaxCode(oneData.get("TAXCODE").toString());
                    oneLv1.setTaxName(oneData.get("TAXNAME").toString());
                    oneLv1.setBuyerNo(oneData.get("BUYERNO").toString());
                    oneLv1.setBuyerName(oneData.get("BUYERNAME").toString());
                    oneLv1.setRDate(oneData.get("RECEIVING_RDATE").toString());
                    oneLv1.setIsBatchSStockIn(isBatchSStockIn);
                    oneLv1.setReceiptDate(oneData.get("RECEIPTDATE").toString());
                    oneLv1.setEmployeeId(StringUtils.toString(oneData.get("EMPLOYEEID"), ""));
                    oneLv1.setEmployeeName(StringUtils.toString(oneData.get("EMPLOYEENAME"), ""));
                    oneLv1.setDepartId(StringUtils.toString(oneData.get("DEPARTID"), ""));
                    oneLv1.setDepartName(StringUtils.toString(oneData.get("DEPARTNAME"), ""));
                    oneLv1.setPayType(StringUtils.toString(oneData.get("PAYTYPE"), ""));
                    oneLv1.setPayOrgNo(StringUtils.toString(oneData.get("PAYORGNO"), ""));
                    oneLv1.setBillDateNo(StringUtils.toString(oneData.get("BILLDATENO"), ""));
                    oneLv1.setBillDateDesc(StringUtils.toString(oneData.get("BILLDATENAME"), ""));
                    oneLv1.setPayDateNo(StringUtils.toString(oneData.get("PAYDATENO"), ""));
                    oneLv1.setPayDateDesc(StringUtils.toString(oneData.get("PAYDATENAME"), ""));
                    oneLv1.setInvoiceCode(StringUtils.toString(oneData.get("INVOICECODE"), ""));
                    oneLv1.setInvoiceName(StringUtils.toString(oneData.get("INVOICE_NAME"), ""));
                    oneLv1.setCurrency(StringUtils.toString(oneData.get("CURRENCY"), ""));
                    oneLv1.setCurrencyName(StringUtils.toString(oneData.get("CURRENCYNAME"), ""));
                    oneLv1.setStockInType(StringUtils.toString(oneData.get("STOCKINTYPE"), ""));
                    oneLv1.setCustomer(StringUtils.toString(oneData.get("CUSTOMER"), ""));
                    oneLv1.setCustomerName(StringUtils.toString(oneData.get("CUSTOMERNAME"), ""));
                    oneLv1.setAccountDateTime(oneData.get("ACCOUNTDATETIME").toString());

//                    oneLv1.setTotDistriTaxAmt(String.valueOf(totDistriTaxAmt));
//                    oneLv1.setTotDistriPreTaxAmt(String.valueOf(totDistriPreTaxAmt));
                    oneLv1.setTotDistriTaxAmt(StringUtils.toString(oneData.get("TOTDISTRITAXAMT"), ""));
                    oneLv1.setTotDistriPreTaxAmt(StringUtils.toString(oneData.get("TOTDISTRIPRETAXAMT"), ""));

                    oneLv1.setTotDistriAmt(StringUtils.toString(oneData.get("TOT_DISTRIAMT"), ""));
                    oneLv1.setOrderOrgNo(StringUtils.toString(oneData.get("ORDERORGNO"), ""));
                    oneLv1.setOrderOrgName(StringUtils.toString(oneData.get("ORDERORGNAME"), ""));
//                    oneLv1.setOrderNo(StringUtils.toString(oneData.get("ORGIGINNO"),""));
                    oneLv1.setExpireDate(StringUtils.toString(oneData.get("EXPIRDATE"), ""));
                    oneLv1.setOoType(StringUtils.toString(oneData.get("OOTYPE"), ""));
                    oneLv1.setOofNo(StringUtils.toString(oneData.get("OOFNO"), ""));
                    oneLv1.setOriginNo(StringUtils.toString(oneData.get("ORGIGINNO"), ""));
                    oneLv1.setReturnType(StringUtils.toString(oneData.get("RETURNTYPE"), ""));
                    oneLv1.setIsLocation(StringUtils.toString(oneData.get("ISLOCATION"), ""));
                    oneLv1.setPayOrgName(StringUtils.toString(oneData.get("PAYORGNAME"), ""));
                    oneLv1.setReceivingNo(StringUtils.toString(oneData.get("RECEIVINGNO"), ""));
                    oneLv1.setPayee(StringUtils.toString(oneData.get("PAYEE"), ""));
                    oneLv1.setPayeeName(StringUtils.toString(oneData.get("PAYEENAME"), ""));
                    oneLv1.setPayer(StringUtils.toString(oneData.get("PAYER"), ""));
                    oneLv1.setPayerName(StringUtils.toString(oneData.get("PAYERNAME"), ""));
                    oneLv1.setCorp(oneData.get("CORP").toString());
                    oneLv1.setCorpName(oneData.get("CORPNAME").toString());
                    oneLv1.setBizOrgNo(oneData.get("BIZORGNO").toString());
                    oneLv1.setBizOrgName(oneData.get("BIZORGNAME").toString());
                    oneLv1.setBizCorp(oneData.get("BIZCORP").toString());
                    oneLv1.setBizCorpName(oneData.get("BIZCORPNAME").toString());
                    oneLv1.setTotAmount(oneData.get("TOTAMOUNT").toString());
                    oneLv1.setTotPreTaxAmt(oneData.get("TOTPRETAXAMT").toString());
                    oneLv1.setTotTaxAmt(oneData.get("TOTTAXAMT").toString());

                    oneLv1.setPurType(oneData.get("PURTYPE").toString());
                    oneLv1.setOrganizationNo(oneData.get("ORGANIZATIONNO").toString());
                    oneLv1.setOrganizationName(oneData.get("ORGANIZATIONNAME").toString());
                    oneLv1.setTaxPayerType(oneData.get("TAXPAYER_TYPE").toString());
                    oneLv1.setInputTaxCode(oneData.get("INPUT_TAXCODE").toString());
                    oneLv1.setInputTaxRate(oneData.get("INPUT_TAXRATE").toString());
                    oneLv1.setOutputTaxCode(oneData.get("OUTPUT_TAXCODE").toString());
                    oneLv1.setOutputTaxRate(oneData.get("OUTPUT_TAXRATE").toString());


                    res.getDatas().add(oneLv1);
                }
            } else {
                totalRecords = 0;
                totalPages = 0;
            }
            res.setSuccess(true);
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
//            res.setTotalPages(totalPages);

            return res;

        //} catch (Exception e) {
        //    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, e.getMessage());
        //}
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    protected String getQueryTransactionSql(DCP_SStockInDetailQueryReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT distinct b.*,c.PLU_NAME,tl.TAXNAME,g1.UNAME,fl.FEATURENAME FROM dcp_sstockin a")
                .append(" INNER JOIN dcp_transaction b ON a.EID=b.EID and a.ORGANIZATIONNO=b.ORGANIZATIONNO and a.SSTOCKINNO=b.BILLNO ")
                .append(" left join dcp_goods_lang c on b.eid=c.eid and b.pluno=c.pluno and c.lang_type='").append(req.getLangType()).append("'")
                .append(" left join dcp_taxcategory_lang tl on b.eid=tl.eid and b.taxcode=tl.taxcode and tl.lang_type='").append(req.getLangType()).append("'")
                .append(" left join dcp_unit_lang g1 on b.eid=g1.eid and b.punit=g1.unit and g1.lang_type='").append(req.getLangType()).append("'")
                .append(" left join dcp_goods_feature_lang fl on b.eid=fl.eid and b.PLUNO=fl.PLUNO and b.FEATURENO=fl.FEATURENO and fl.lang_type='").append(req.getLangType()).append("'")
                .append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getStockInType())) {
            querySql.append(" AND a.STOCKINTYPE='").append(req.getRequest().getStockInType()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getSStockInNo())) {
            querySql.append(" AND a.SSTOCKINNO='").append(req.getRequest().getSStockInNo()).append("'");
        }

        querySql.append(" ORDER BY b.ITEM ASC ");

        return querySql.toString();
    }

    @Override
    protected String getQuerySql(DCP_SStockInDetailQueryReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT distinct a.*  ")
                .append(" ,b.ITEM,b.OITEM,b.PQTY,b.PRICE,b.BASEQTY,b.UNIT_RATIO,b.PROC_RATE ")
                .append(" ,b.POQTY,b.PLUNO,b.RECEIVING_QTY,b.AMT,b.RETWQTY,b.PLU_MEMO,b.STOCKIN_QTY ")
                .append(" ,b.DISTRIPRICE,b.BATCH_NO,b.PROD_DATE,b.DISTRIAMT,b.BDATE,b.FEATURENO ")
                .append(" ,b.PARTITION_DATE,b.RECEIVINGNO DETAILRECEIVINGNO,b.RECEIVINGITEM,b.OTYPE AS DOTYPE ,b.OFNO AS DOFNO ")
                .append(" ,b.PTEMPLATENO DETAILPTEMPLATENO,b.CATEGORY,b.ISGIFT as ISGIFT,b.LOCATION,b.EXP_DATE,b.ORIGINNO DETAILORGIGINNO ")
                .append(" ,b.ORIGINITEM,b.TAXCODE DETAILTAXCODE,b.TAXRATE DETAILTAXRATE,b.INCLTAX,b.OOTYPE DETAILOOTYPE,b.OOFNO DETAILOOFNO,b.OOITEM ")
                .append(" ,b.TAXCALTYPE,b.WAREHOUSE as WAREHOUSE_DETAIL,wl2.WAREHOUSE_NAME as WAREHOUSE_DETAIL_NAME ")
                .append(" ,a.WAREHOUSE as WAREHOUSE_MAIN,wl1.WAREHOUSE_NAME as WAREHOUSE_MAIN_NAME ")
                .append(" ,b.PUNIT,g1.uname as punitname,u1.udlength as punitudlength ")
                .append(" ,b.BASEUNIT,g2.uname as baseunitname,u2.udlength as baseunitudlength ")
                .append(" ,gl.PLU_NAME,gul.spec,b.PLU_MEMO,dc.LOCATIONNAME ")
                .append(" ,b1.op_NAME as createname,b2.op_NAME as modifyname,b3.op_NAME as submitname ")
                .append(" ,b4.op_NAME as confirmname,b5.op_NAME as cancelname,b6.op_NAME as accountname ")
                .append(" ,b7.NAME as EMPLOYEENAME,image.listimage,tl.TAXNAME DETAILTAXNAME,f.PTEMPLATE_NAME,tl2.TAXNAME ")
                .append(" ,dg.isbatch,dg.shelflife,dg.stockinvalidday,dg.stockoutvalidday ")
                .append(" ,bz1.sname SUPPLIER_NAME,fn.FEATURENAME,dd0.DEPARTNAME,'0' STOCKINALLOWTYPE ")
                .append(" ,c.DELIVERY_NO,c.rdate as receiving_rdate,bl1.name as BILLDATENAME,bl2.name PAYDATENAME ")
                .append(" ,it1.INVOICE_NAME,h.NAME CURRENCYNAME,bz2.sname CUSTOMERNAME,ol3.ORG_NAME ORGANIZATIONNAME ")
                .append(" ,cl1.category_name,ol2.ORG_NAME PAYORGNAME,w1.ISLOCATION,w2.ISLOCATION DETAILISLOCATION,j.sname as payeename,k.sname as payername,b.purprice,b.puramt,l.org_name as corpname ,m.org_name as bizorgname,n.org_name as bizcorpname ")
                .append(" ,o.org_name as orderorgname,(nvl(b.Pqty,0)-nvl(b.STOCKIN_QTY,0)) as canStockInQty,b.custprice,b.refcustprice,b.SDPURPRICE ")
                .append(" from dcp_sstockin a ")
                .append(" LEFT JOIN DCP_SSTOCKIN_DETAIL b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.ORGANIZATIONNO=b.ORGANIZATIONNO and a.SSTOCKINNO=b.SSTOCKINNO  ")
                .append(" left join dcp_goods_lang gl on b.eid=gl.eid and b.pluno=gl.pluno and gl.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT join dcp_goods dg on b.eid=dg.eid and b.pluno=dg.pluno ")
                .append(" left join dcp_goods_unit_lang gul on b.eid=gul.eid and b.pluno=gul.pluno and b.punit=gul.ounit and gul.lang_type='").append(req.getLangType()).append("'")
                .append(" left join dcp_receiving_detail d on b.eid=d.eid and b.receivingno=d.receivingno and b.RECEIVINGITEM=d.item  ")
                .append(" left join dcp_receiving c on d.eid=c.eid and d.shopid=c.shopid and d.receivingno=c.receivingno")
                .append(" left join dcp_ptemplate f on a.eid=f.eid and a.ptemplateno=f.ptemplateno and f.doc_type='3' ")
                .append(" left join dcp_unit u1 on b.eid=u1.eid and b.punit=u1.unit")
                .append(" left join dcp_unit u2 on b.eid=u2.eid and b.baseunit=u2.unit")
                .append(" left join dcp_unit_lang g1 on b.eid=g1.eid and b.punit=g1.unit and g1.lang_type='").append(req.getLangType()).append("'")
                .append(" left join dcp_unit_lang g2 on b.eid=g2.eid and b.baseunit=g2.unit and g2.lang_type='").append(req.getLangType()).append("'")
                .append(" left join dcp_taxcategory_lang tl on b.eid=tl.eid and b.taxcode=tl.taxcode and tl.lang_type='").append(req.getLangType()).append("'")
                .append(" left join dcp_taxcategory_lang tl2 on a.eid=tl2.eid and a.taxcode=tl2.taxcode and tl2.lang_type='").append(req.getLangType()).append("'")
                .append(" left join dcp_goods_feature_lang fn on b.eid=fn.eid and b.pluno=fn.pluno and b.featureno=fn.featureno and fn.lang_type='").append(req.getLangType()).append("'")
                .append(" left join dcp_warehouse w1 on a.eid=w1.eid and a.warehouse=w1.warehouse ")
                .append(" left join dcp_warehouse w2 on b.eid=w2.eid and b.warehouse=w2.warehouse ")
                .append(" left join dcp_warehouse_lang wl1 on a.eid=wl1.eid and a.warehouse=wl1.warehouse and wl1.lang_type='").append(req.getLangType()).append("'")
                .append(" left join dcp_warehouse_lang wl2 on b.eid=wl2.eid and b.warehouse=wl2.warehouse and wl2.lang_type='").append(req.getLangType()).append("'")
                .append(" left join dcp_goodsimage image on image.eid=b.eid and image.pluno=b.pluno and image.apptype='ALL'")
                .append(" left join PLATFORM_STAFFS_LANG b1 on a.eid=b1.eid and a.createby=b1.opno  and b1.lang_type='"+req.getLangType()+"' ")
                .append(" left join PLATFORM_STAFFS_LANG b2 on a.eid=b2.eid and a.modifyby=b2.opno and b2.lang_type='"+req.getLangType()+"' ")
                .append(" left join PLATFORM_STAFFS_LANG b3 on a.eid=b3.eid and a.submitby=b3.opno and b3.lang_type='"+req.getLangType()+"' ")
                .append(" left join PLATFORM_STAFFS_LANG b4 on a.eid=b4.eid and a.confirmby=b4.opno and b4.lang_type='"+req.getLangType()+"' ")
                .append(" left join PLATFORM_STAFFS_LANG b5 on a.eid=b5.eid and a.cancelby=b5.opno and b5.lang_type='"+req.getLangType()+"' ")
                .append(" left join PLATFORM_STAFFS_LANG b6 on a.eid=b6.eid and a.accountby=b6.opno and b6.lang_type='"+req.getLangType()+"' ")
                .append(" left join DCP_employee b7 on a.eid=b7.eid and a.EMPLOYEEID=b7.employeeno ")
                .append(" left join dcp_department_lang dd0 on dd0.eid=a.eid and dd0.departno=a.DEPARTID and dd0.lang_type='").append(req.getLangType()).append("' ")
//                .append(" left join dcp_department_lang dd2 on dd2.eid=a.eid and dd2.departno=a.DEPARTID and dd2.lang_type='").append(req.getLangType()).append("' ")
                .append(" left join dcp_bizpartner bz1 on a.eid=bz1.eid and a.supplier=bz1.BIZPARTNERNO ")
                .append(" left join dcp_bizpartner bz2 on a.eid=bz2.eid and a.CUSTOMER=bz2.BIZPARTNERNO ")
                .append(" left join DCP_BILLDATE_LANG bl1 on bl1.eid=a.eid and bl1.billdateno=a.billdateno and bl1.lang_type='").append(req.getLangType()).append("'")
                .append(" left join DCP_PAYDATE_LANG bl2 on bl2.eid=a.eid and bl2.paydateno=a.PAYDATENO and bl2.lang_type='").append(req.getLangType()).append("'")
                .append(" left join DCP_INVOICETYPE_LANG it1 on it1.eid =a.eid and it1.INVOICECODE=a.INVOICECODE and it1.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_CURRENCY_LANG h ON h.eid = a.eid AND h.CURRENCY = a.CURRENCY AND nation = 'CN' AND h.lang_type = '").append(req.getLangType()).append("'")
                .append(" LEFT JOIN dcp_org_lang ol3 ON ol3.eid = a.eid AND ol3.ORGANIZATIONNO = a.ORGANIZATIONNO AND ol3.lang_type = '").append(req.getLangType()).append("'")
                .append(" LEFT JOIN dcp_org_lang ol2 ON ol2.eid = a.eid AND ol2.ORGANIZATIONNO = a.PAYORGNO AND ol2.lang_type = '").append(req.getLangType()).append("'")
                .append(" LEFT JOIN dcp_category_lang cl1 ON b.eid = cl1.eid AND b.category = cl1.category AND cl1.lang_type = '").append(req.getLangType()).append("'")
                .append(" LEFT JOIN dcp_location dc on dc.eid=b.eid and dc.warehouse=b.warehouse and dc.location=b.location ")
                .append(" left join dcp_bizpartner j on j.eid=a.eid and j.bizpartnerno=a.payee ")
                .append(" left join dcp_bizpartner k on k.eid=a.eid and k.bizpartnerno=a.payer ")

                .append(" left join dcp_org_lang l on l.eid=a.eid and l.organizationno=a.corp and l.lang_type='"+req.getLangType()+"'")
                .append(" left join dcp_org_lang m on m.eid=a.eid and m.organizationno=a.bizorgno and m.lang_type='"+req.getLangType()+"' ")
                .append(" left join dcp_org_lang n on n.eid=a.eid and n.organizationno=a.bizcorp and n.lang_type='"+req.getLangType()+"' ")
                .append(" left join dcp_org_lang o on o.eid=a.eid and o.organizationno=a.ORDERORGNO and o.lang_type='"+req.getLangType()+"' ")


                .append(" WHERE a.EID='").append(req.geteId()).append("'");


        if (StringUtils.isNotEmpty(req.getRequest().getStockInType())) {
            querySql.append(" AND a.STOCKINTYPE='").append(req.getRequest().getStockInType()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getSStockInNo())) {
            querySql.append(" AND a.SSTOCKINNO='").append(req.getRequest().getSStockInNo()).append("'");
        }
        querySql.append(" ORDER BY a.SSTOCKINNO ASC,b.ITEM ASC ");

        return querySql.toString();
    }


    protected String getQueryDocSql(DCP_SStockInDetailQueryReq req, String isBatchSStockIn) {
        //查询条件
        String eId = req.geteId();
        String shopId = req.getShopId();
        String orderNo = req.getRequest().getSStockInNo();
        String orderType = req.getRequest().getStockInType();


        StringBuffer sqlbuf = new StringBuffer();
        String langType = req.getLangType();
        //计算起始位置
        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * pageSize);

        sqlbuf.append(" with doc as("
                + " select * from ("
                + " select count(*) over () as num,row_number() over (order by a.docno desc) as rn,a.*"
                + " from (");

        sqlbuf.append(""

                + " select a.sstockinno as docno,'sstockin' as doctype from dcp_sstockin a"
                + " where a.eid='" + eId);

        if (!Check.Null(orderNo)) {
            sqlbuf.append(" and a.SSTOCKINNO='" + orderNo + "'");
        }


        sqlbuf.append(" )a");
        sqlbuf.append(" ) ) ");
        sqlbuf.append(" "
                + " select a.num,a.eid,a.shopid,a.supplier,a.docno,a.memo,a.status,a.ptemplateno,a.bdate,"
                + " a.doc_type,a.load_doctype,a.load_docno,a.load_receiptno,a.delivery_no,a.receiving_rdate,"
                + " a.tot_cqty,a.tot_pqty,a.tot_amt,a.tot_distriamt,"
                + " a.warehouse_main,i.warehouse_name as warehouse_main_name,"
                + " a.update_time,a.process_erp_no,a.sstockinno,a.otype,a.ofno,a.buyerno,a.buyername,"
                + " a.process_status,a.taxcode,a.taxname,"
                + " a.createby,a.create_time,a.create_date,b1.NAME as createname,"
                + " a.modifyby,a.modify_date,a.modify_time,b2.NAME as modifyname,"
                + " a.submitby,a.submit_date,a.submit_time,b3.NAME as submitname,"
                + " a.confirmby,a.confirm_date,a.confirm_time,b4.NAME as confirmname,"
                + " a.cancelby,a.cancel_date,a.cancel_time,b5.NAME as cancelname,"
                + " a.EMPLOYEEID,b7.NAME as EMPLOYEENAME,"
                + " a.accountby,a.account_date,a.account_time,b6.NAME as accountname,"
                + " a.item,a.pluno,a.featureno,a.pqty,a.baseqty,a.punit,a.baseunit,a.unit_ratio,"
                + " a.price,a.distriprice,a.amt,a.distriamt,"
                + " a.receiving_qty,a.reitem,a.oitem,a.plu_memo,a.poqty,"
                + " a.warehouse_detail,a.stockin_qty,a.proc_rate,a.batch_no,a.prod_date,a.retwqty,a.receiptdate,"
                + " b.isbatch,b.shelflife,b.stockinvalidday,b.stockoutvalidday,"
                + " c.plu_name,fn.featurename,gul.spec,image.listimage,"
                + " '0' stockinallowtype,'0' stockoutallowtype,"
                + " '' abbr,d.sname supplier_name,"
                + " f.ptemplate_name,"
                + " g1.uname as punitname,g2.uname as baseunitname,"
                + " h.udlength as punitudlength,bul.udlength as baseunitudlength "
                + " from ("
                + " select doc.num,doc.doctype as sorttype,a.eid,a.shopid,a.supplier,a.sstockinno as docno,"
                + " a.memo,a.status,a.ptemplateno,a.bdate,"
                + " a.doc_type,a.load_doctype,a.load_docno,a.load_receiptno,c.delivery_no,c.rdate as receiving_rdate,"
                + " a.tot_cqty,a.tot_pqty,a.tot_amt,a.tot_distriamt,"
                + " a.warehouse as warehouse_main,a.update_time,"
                + " a.process_erp_no,a.sstockinno,"
                + " a.buyerno,a.buyername,a.otype,a.ofno,"
                + " a.process_status,a.taxcode,e.taxname as taxname,"
                + " a.createby,a.create_time,a.create_date,"
                + " a.modifyby,a.modify_date,a.modify_time,"
                + " a.submitby,a.submit_date,a.submit_time,"
                + " a.confirmby,a.confirm_date,a.confirm_time,"
                + " a.cancelby,a.cancel_date,a.cancel_time,"
                + " a.accountby,a.account_date,a.account_time,"
                + " b.item,b.pluno,b.featureno,b.pqty,b.baseqty,b.punit,b.baseunit,b.unit_ratio,"
                + " b.price,b.distriprice,b.amt,b.distriamt,"
                + " b.receiving_qty,b.oitem as reitem,"
                + " b.oitem,b.plu_memo,b.poqty,"
                + " b.warehouse as warehouse_detail,b.stockin_qty,d.proc_rate,"
                + " b.batch_no,b.prod_date,"
                + " b.retwqty,c.receiptdate,a.EMPLOYEEID"
                + " from dcp_sstockin a"
                + " inner join dcp_sstockin_detail b on a.eid=b.eid and a.shopid=b.shopid and a.sstockinno=b.sstockinno"
                + " inner join doc on a.sstockinno=doc.docno and doc.doctype='sstockin'"
                + " left  join dcp_receiving c on a.eid=c.eid and a.shopid=c.shopid and a.ofno=c.receivingno"
                + " left  join dcp_receiving_detail d on a.eid=d.eid and a.shopid=d.shopid and c.receivingno=d.receivingno and b.oitem=d.item"
                + " left  join dcp_taxcategory_lang e on a.eid=e.eid and a.taxcode=e.taxcode and e.lang_type='" + langType + "'"
                + " where a.eid='" + eId + "' and a.shopid='" + shopId + "'"
                + " ) a"
                + " inner join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno"
                + " left  join dcp_goods_lang c on a.eid=c.eid and a.pluno=c.pluno and c.lang_type='" + langType + "'"
                + " left  join dcp_bizpartner d on a.eid=d.eid and a.supplier=d.BIZPARTNERNO"
//                + " left  join dcp_supplier_lang e on a.eid=e.eid and a.supplier=e.supplier and e.lang_type='"+langType+"'"
                + " left  join dcp_ptemplate f on a.eid=f.eid and a.ptemplateno=f.ptemplateno and f.doc_type='3'"
                + " left  join dcp_unit_lang g1 on a.eid=g1.eid and a.punit=g1.unit and g1.lang_type='" + langType + "'"
                + " left  join dcp_unit_lang g2 on a.eid=g2.eid and a.baseunit=g2.unit and g2.lang_type='" + langType + "'"
                + " left  join dcp_unit h on a.eid=h.eid and a.punit=h.unit"
                + " left  join dcp_unit bul on a.eid=bul.eid and a.baseunit=bul.unit"
                + " left  join dcp_warehouse_lang i on a.eid=i.eid and a.shopid=i.organizationno and a.warehouse_main=i.warehouse and i.lang_type='" + langType + "'"
                + " left  join dcp_goods_feature_lang fn on a.eid=fn.eid and a.pluno=fn.pluno and a.featureno=fn.featureno and fn.lang_type='" + langType + "'"
                + " left  join dcp_goods_unit_lang gul on a.eid=gul.eid and a.pluno=gul.pluno and a.punit=gul.ounit and gul.lang_type='" + langType + "'"
                + " left  join dcp_goodsimage image on image.eid=a.eid and image.pluno=a.pluno and image.apptype='ALL'"
                + " left  join DCP_employee b1 on a.eid=b1.eid and a.createby=b1.employeeno "
                + " left  join DCP_employee b2 on a.eid=b2.eid and a.modifyby=b2.employeeno "
                + " left  join DCP_employee b3 on a.eid=b3.eid and a.submitby=b3.employeeno "
                + " left  join DCP_employee b4 on a.eid=b4.eid and a.confirmby=b4.employeeno "
                + " left  join DCP_employee b5 on a.eid=b5.eid and a.cancelby=b5.employeeno "
                + " left  join DCP_employee b6 on a.eid=b6.eid and a.accountby=b6.employeeno "
                + " left  join DCP_employee b7 on a.eid=b7.eid and a.EMPLOYEEID=b6.employeeno "
                + " left  join DCP_TRANSACTION dt on a.eid=dt.eid and a.docno=dt.BILLNO and a.ITEM=dt.ITEM "
                + " order by a.sorttype,a.status,a.docno desc,a.item"
                + "");

        return sqlbuf.toString();
    }

}
