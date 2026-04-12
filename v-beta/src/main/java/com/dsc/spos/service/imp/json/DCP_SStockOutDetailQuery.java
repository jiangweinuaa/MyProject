package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_SStockOutDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_SStockOutDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.StringUtils;
import com.dsc.spos.utils.tax.TaxAmount2;
import com.dsc.spos.utils.tax.TaxAmountCalculation;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_SStockOutDetailQuery extends SPosBasicService<DCP_SStockOutDetailQueryReq, DCP_SStockOutDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_SStockOutDetailQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_SStockOutDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_SStockOutDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_SStockOutDetailQueryRes getResponseType() {
        return new DCP_SStockOutDetailQueryRes();
    }

    @Override
    protected DCP_SStockOutDetailQueryRes processJson(DCP_SStockOutDetailQueryReq req) throws Exception {
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        DCP_SStockOutDetailQueryRes res = this.getResponse();
        //查询资料
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        //查询图片
        String imageSql = this.getImageQuerySql(req);
        List<Map<String, Object>> getImageQData = this.doQueryData(imageSql, null);
        String detailSql = this.getDetailQuerySql(req);
        List<Map<String, Object>> getDetailQData = this.doQueryData(detailSql, null);
        String transferQuerySql = this.getTransferQuerySql(req);
        List<Map<String, Object>> getTransferQData = this.doQueryData(transferQuerySql, null);
        String lotsQuerySql = this.getBatchQuerySql(req);
        List<Map<String, Object>> getLotsQData = this.doQueryData(lotsQuerySql, null);
        res.setDatas(new ArrayList<>());
        if (getQData != null && !getQData.isEmpty()) {
            String isBatchManager = PosPub.getPARA_SMS(dao, eId, "", "IS_BatchNo");
            String isHttps = PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
            String httpStr = isHttps.equals("1") ? "https://" : "http://";
            String domainName = PosPub.getPARA_SMS(dao, eId, "", "DomainName");
            if (domainName.endsWith("/")) {
                domainName = httpStr + domainName + "resource/image/";
            } else {
                domainName = httpStr + domainName + "/resource/image/";
            }

            for (Map<String, Object> oneData : getQData) {
                DCP_SStockOutDetailQueryRes.Level1Elm oneLv1 = res.new Level1Elm();
                oneLv1.setDatas(new ArrayList<>());
                oneLv1.setImage_list(new ArrayList<>());
                oneLv1.setTransactionList(new ArrayList<>());
                oneLv1.setLotsList(new ArrayList<>());
                String sStockOutNo = oneData.get("SSTOCKOUTNO").toString();
                //设置响应
                oneLv1.setSStockOutNo(sStockOutNo);
                oneLv1.setBDate(oneData.get("BDATE").toString());
                oneLv1.setOfNo(oneData.get("OFNO").toString());
                oneLv1.setStatus(oneData.get("STATUS").toString());
                oneLv1.setMemo(oneData.get("MEMO").toString());
                oneLv1.setWarehouse(oneData.get("WAREHOUSE").toString());
                oneLv1.setWarehouseName(oneData.get("WAREHOUSE_NAME").toString());
                oneLv1.setTaxCode(oneData.get("TAXCODE").toString());
                oneLv1.setTaxName(oneData.get("TAXNAME").toString());
                oneLv1.setSupplier(oneData.get("SUPPLIER").toString());
                oneLv1.setAbbr(oneData.get("ABBR").toString());
                oneLv1.setStockOutAllowType(oneData.get("STOCKOUTALLOWTYPE").toString());
                oneLv1.setTotCqty(oneData.get("TOT_CQTY").toString());
                oneLv1.setTotPqty(oneData.get("TOT_PQTY").toString());
                oneLv1.setTotAmt(oneData.get("TOT_AMT").toString());
                oneLv1.setTotDistriAmt(oneData.get("TOT_DISTRIAMT").toString());
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
                oneLv1.setProcessERPNo(oneData.get("PROCESS_ERP_NO").toString());
                oneLv1.setEmployeeId(oneData.get("EMPLOYEEID").toString());
                oneLv1.setEmployeeName(oneData.get("EMPLOYEENAME").toString());
                oneLv1.setDepartId(oneData.get("DEPARTID").toString());
                oneLv1.setDepartName(oneData.get("DEPARTNAME").toString());
                oneLv1.setOrderOrgNo(oneData.get("ORDERORGNO").toString());
                oneLv1.setOrderOrgName(oneData.get("ORDERORGNAME").toString());
                oneLv1.setPayType(oneData.get("PAYTYPE").toString());
                oneLv1.setPayOrgNo(oneData.get("PAYORGNO").toString());
                oneLv1.setPayOrgName(oneData.get("PAYORGNAME").toString());
                oneLv1.setBillDateNo(oneData.get("BILLDATENO").toString());
                oneLv1.setBillDateDesc(oneData.get("BILLDATEDESC").toString());
                oneLv1.setPayDateNo(oneData.get("PAYDATENO").toString());
                oneLv1.setPayDateDesc(oneData.get("PAYDATEDESC").toString());
                oneLv1.setInvoiceCode(oneData.get("INVOICECODE").toString());
                oneLv1.setInvoiceName(oneData.get("INVOICENAME").toString());
                oneLv1.setCurrency(oneData.get("CURRENCY").toString());
                oneLv1.setCurrencyName(oneData.get("CURRENCYNAME").toString());
                oneLv1.setStockOutType(oneData.get("STOCKOUTTYPE").toString());
                oneLv1.setCustomer(oneData.get("CUSTOMER").toString());
                oneLv1.setCustomerName(oneData.get("CUSTOMERNAME").toString());
                oneLv1.setOType(oneData.get("OTYPE").toString());
                oneLv1.setTotDistriTaxAmt(oneData.get("TOTDISTRITAXAMT").toString());
                oneLv1.setTotDistriPreTaxAmt(oneData.get("TOTDISTRIPRETAXAMT").toString());
                oneLv1.setIsBatchManage(isBatchManager);
                oneLv1.setIsLocation(oneData.get("ISLOCATION").toString());
                oneLv1.setSupplierName(oneData.get("SUPPLIERNAME").toString());
                oneLv1.setOriginNo(oneData.get("ORIGINNO").toString());
                oneLv1.setPayee(oneData.get("PAYEE").toString());
                oneLv1.setPayeeName(oneData.get("PAYEENAME").toString());
                oneLv1.setPayer(oneData.get("PAYER").toString());
                oneLv1.setPayerName(oneData.get("PAYERNAME").toString());

                oneLv1.setCorp(oneData.get("CORP").toString());
                oneLv1.setCorpName(oneData.get("CORPNAME").toString());
                oneLv1.setBizOrgNo(oneData.get("BIZORGNO").toString());
                oneLv1.setBizOrgName(oneData.get("BIZORGNAME").toString());
                oneLv1.setBizCorp(oneData.get("BIZCORP").toString());
                oneLv1.setBizCorpName(oneData.get("BIZCORPNAME").toString());
                oneLv1.setTotAmt(oneData.get("TOT_AMT").toString());
                oneLv1.setTotPreTaxAmt(oneData.get("TOTDISTRIPRETAXAMT").toString());
                oneLv1.setTotTaxAmt(oneData.get("TOTDISTRITAXAMT").toString());

                oneLv1.setTaxPayerType(oneData.get("TAXPAYERTYPE").toString());
                oneLv1.setInputTaxCode(oneData.get("INPUTTAXCODE").toString());
                oneLv1.setInputTaxRate(oneData.get("INPUTTAXRATE").toString());
                oneLv1.setOutputTaxCode(oneData.get("OUTPUTTAXCODE").toString());
                oneLv1.setOutputTaxRate(oneData.get("OUTPUTTAXRATE").toString());

                //通知单号
                StringBuffer sJoinNoticeNo = new StringBuffer();
                StringBuffer sJoinOriginNo = new StringBuffer();
                List<String> ofNoList = getDetailQData.stream().map(x -> x.get("OFNO").toString()).distinct().collect(Collectors.toList());
                for (String ofNo : ofNoList) {
                    sJoinNoticeNo.append(ofNo + ",");
                }
                List<Map<String, Object>> getNoticeQData = new ArrayList<>();
                if (!Check.Null(sJoinNoticeNo.toString())) {
                    Map<String, String> map = new HashMap<>();
                    map.put("BILLNO", sJoinNoticeNo.toString());

                    MyCommon cm = new MyCommon();
                    String withNoticeNo = cm.getFormatSourceMultiColWith(map);
                    StringBuffer noticeSb = new StringBuffer();
                    noticeSb.append("select distinct a.billno,a.item,nvl(a.pqty,0) as pqty,nvl(a.STOCKOUTQTY,0) as stockoutqty,nvl(c.pqty,0) as otherpqty,c.sstockoutno,c.item as ssitem " +
                            " from DCP_STOCKOUTNOTICE_DETAIL a " +
                            " inner join (" + withNoticeNo + ") b on a.BILLNO=b.BILLNO " +
                            " left join dcp_sstockout_detail c on c.OFNO=a.billno and c.OITEM=a.item and c.sstockoutno!='" + sStockOutNo + "' " +
                            " left join dcp_sstockout d on d.eid=c.eid and d.organizationno=c.organizationno and c.sstockoutno=d.sstockoutno  " +
                            " where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "'");
                    getNoticeQData = this.doQueryData(noticeSb.toString(), null);
                }

                List<Map<String, Object>> getStockinQData = new ArrayList<>();
                if (!Check.Null(sJoinNoticeNo.toString())) {
                    Map<String, String> map = new HashMap<>();
                    map.put("SSTOCKINNO", sJoinNoticeNo.toString());

                    MyCommon cm = new MyCommon();
                    String withStockInNo = cm.getFormatSourceMultiColWith(map);
                    StringBuffer stockInSb = new StringBuffer();
                    stockInSb.append("select a.SSTOCKINNO as billno,a.item,nvl(a.pqty,0) as pqty,nvl(a.RETWQTY,0) as stockoutqty,nvl(c.pqty,0) as otherpqty,d.status,c.sstockoutno,c.item as ssitem " +
                            " from DCP_SSTOCKIN_DETAIL a " +
                            " inner join (" + withStockInNo + ") b on a.SSTOCKINNO=b.SSTOCKINNO " +
                            " left join dcp_sstockout_detail c on c.OFNO=a.SSTOCKINNO and c.OITEM=a.item and c.sstockoutno!='" + sStockOutNo + "' " +
                            " left join dcp_sstockout d on d.eid=c.eid and d.organizationno=c.organizationno and c.sstockoutno=d.sstockoutno  " +
                            " where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "'");
                    getStockinQData = this.doQueryData(stockInSb.toString(), null);
                }

                List<Map<String, Object>> getCustomerQData = new ArrayList<>();
                if (!Check.Null(sJoinNoticeNo.toString())) {
                    Map<String, String> map = new HashMap<>();
                    map.put("PORDERNO", sJoinNoticeNo.toString());

                    MyCommon cm = new MyCommon();
                    String withCustomerNo = cm.getFormatSourceMultiColWith(map);
                    StringBuffer customerSb = new StringBuffer();
                    customerSb.append("select a.porderno as billno,a.item,nvl(a.qty,0) as pqty,nvl(a.STOCKOUTQTY,0) as stockoutqty,nvl(a.STOCKOUTNOQTY,0) as STOCKOUTNOQTY,nvl(c.pqty,0) as otherpqty,d.status,c.sstockoutno,c.item as ssitem " +
                            " from DCP_CUSTOMERPORDER_DETAIL a " +
                            " inner join (" + withCustomerNo + ") b on a.PORDERNO=b.PORDERNO " +
                            " left join dcp_sstockout_detail c on c.OFNO=a.PORDERNO and c.OITEM=a.item and c.sstockoutno!='" + sStockOutNo + "' " +
                            " left join dcp_sstockout d on d.eid=c.eid and d.organizationno=c.organizationno and c.sstockoutno=d.sstockoutno  " +
                            " where a.eid='" + eId + "' ");
                    getCustomerQData = this.doQueryData(customerSb.toString(), null);
                }

                //查询明细
                for (Map<String, Object> oneDataDetail : getDetailQData) {
                    //过滤属于此单头的明细
                    DCP_SStockOutDetailQueryRes.SDetail oneLv2 = res.new SDetail();
                    String listImage = oneDataDetail.get("LISTIMAGE").toString();
                    if (!Check.Null(listImage)) {
                        listImage = domainName + listImage;
                    }

                    ///已退货数量计算，为空赋默认值0
                    String sStockIn_retWqty = oneDataDetail.get("SSTOCKIN_RETWQTY").toString();
                    if (!PosPub.isNumericType(sStockIn_retWqty)) {
                        sStockIn_retWqty = "0";
                    } else {
                        String sStockIn_unitRatio = oneDataDetail.get("SSTOCKIN_UNIT_RATIO").toString();
                        if (!PosPub.isNumericType(sStockIn_unitRatio)) {
                            sStockIn_unitRatio = "0";
                        }
                        //retWqty --> retPqty
                        BigDecimal sStockIn_retWqty_b = new BigDecimal(sStockIn_retWqty).divide(new BigDecimal(sStockIn_unitRatio), 6, RoundingMode.HALF_UP);
                        sStockIn_retWqty = sStockIn_retWqty_b.toPlainString();
                    }

                    //单身赋值
                    oneLv2.setItem(oneDataDetail.get("ITEM").toString());
                    oneLv2.setOItem(oneDataDetail.get("OITEM").toString());
                    oneLv2.setPluNo(oneDataDetail.get("PLUNO").toString());
                    oneLv2.setPluName(oneDataDetail.get("PLU_NAME").toString());
                    oneLv2.setFeatureNo(oneDataDetail.get("FEATURENO").toString());
                    oneLv2.setFeatureName(oneDataDetail.get("FEATURENAME").toString());
                    oneLv2.setBatchNo(oneDataDetail.get("BATCH_NO").toString());
                    oneLv2.setProdDate(oneDataDetail.get("PROD_DATE").toString());
                    oneLv2.setListImage(listImage);
                    oneLv2.setSpec(oneDataDetail.get("SPEC").toString());
                    if ("Y".equals(isBatchManager) && "Y".equals(oneDataDetail.get("ISBATCH").toString())) {
                        oneLv2.setIsBatch("Y");
                    } else {
                        oneLv2.setIsBatch("N");
                    }


                    oneLv2.setStockOutValidDay(oneDataDetail.get("STOCKOUTVALIDDAY").toString());
                    oneLv2.setShelfLife(oneDataDetail.get("SHELFLIFE").toString());
                    oneLv2.setWarehouse(oneDataDetail.get("WAREHOUSE").toString());   //同单头仓库
                    oneLv2.setPQty(oneDataDetail.get("PQTY").toString());
                    oneLv2.setPunit(oneDataDetail.get("PUNIT").toString());
                    oneLv2.setPunitName(oneDataDetail.get("PUNITNAME").toString());
                    oneLv2.setBaseQty(oneDataDetail.get("BASEQTY").toString());
                    oneLv2.setBaseUnit(oneDataDetail.get("BASEUNIT").toString());
                    oneLv2.setBaseUnitName(oneDataDetail.get("BASEUNITNAME").toString());
                    oneLv2.setUnitRatio(oneDataDetail.get("UNIT_RATIO").toString());
                    oneLv2.setPunitUdLength(oneDataDetail.get("UDLENGTH").toString());
                    oneLv2.setPrice(oneDataDetail.get("PRICE").toString());
                    oneLv2.setDistriPrice(oneDataDetail.get("DISTRIPRICE").toString());
                    oneLv2.setAmt(oneDataDetail.get("AMT").toString());
                    oneLv2.setDistriAmt(oneDataDetail.get("DISTRIAMT").toString());
                    oneLv2.setOqty(oneDataDetail.get("SSTOCKIN_PQTY").toString());
                    oneLv2.setRoutqty(sStockIn_retWqty);
                    oneLv2.setRoutunit(oneDataDetail.get("SSTOCKIN_PUNIT").toString());
                    oneLv2.setRoutunitName(oneDataDetail.get("SSTOCKIN_UNITNAME").toString());
                    oneLv2.setBaseUnitUdLength(oneDataDetail.get("BASEUNITUDLENGTH").toString());
                    oneLv2.setOType(oneDataDetail.get("OTYPE").toString());
                    oneLv2.setOfNo(oneDataDetail.get("OFNO").toString());
                    oneLv2.setCategory(oneDataDetail.get("CATEGORY").toString());
                    oneLv2.setCategoryName(oneDataDetail.get("CATEGORYNAME").toString());
                    oneLv2.setIsGift(oneDataDetail.get("ISGIFT").toString());
                    oneLv2.setLocation(oneDataDetail.get("LOCATION").toString());
                    oneLv2.setLocationName(oneDataDetail.get("LOCATIONNAME").toString());
                    oneLv2.setExpDate(oneDataDetail.get("EXPDATE").toString());
                    oneLv2.setPTemplateNo(oneDataDetail.get("PTEMPLATENO").toString());
                    oneLv2.setPurBasePrice(oneDataDetail.get("PURBASEPRICE").toString());
                    oneLv2.setMinRate(oneDataDetail.get("MINRATE").toString());
                    oneLv2.setMaxRate(oneDataDetail.get("MAXRATE").toString());
                    oneLv2.setOriginNo(oneDataDetail.get("ORIGINNO").toString());
                    oneLv2.setOriginItem(oneDataDetail.get("ORIGINITEM").toString());
                    oneLv2.setReturnQty(oneDataDetail.get("RETURNQTY").toString());
                    oneLv2.setTaxCode(oneDataDetail.get("TAXCODE").toString());
                    oneLv2.setTaxRate(oneDataDetail.get("TAXRATE").toString());
                    oneLv2.setInclTax(oneDataDetail.get("INCLTAX").toString());
                    oneLv2.setTaxCalType(oneDataDetail.get("TAXCALTYPE").toString());
                    oneLv2.setBsNo(StringUtils.toString(oneDataDetail.get("BSNO"), ""));
                    oneLv2.setBsName(StringUtils.toString(oneDataDetail.get("BSNAME"), ""));
                    oneLv2.setOofNo(oneDataDetail.get("OOFNO").toString());
                    oneLv2.setOoItem(oneDataDetail.get("OOITEM").toString());
                    oneLv2.setPurPrice(oneDataDetail.get("PURPRICE").toString());
                    oneLv2.setPurAmt(oneDataDetail.get("PURAMT").toString());
                    oneLv2.setRefPurPrice(oneDataDetail.get("REFPURPRICE").toString());
                    oneLv2.setRefCustPriceBaseUnit(oneDataDetail.get("BASEUNIT").toString());
                    oneLv2.setCustPrice(oneDataDetail.get("CUSTPRICE").toString());
                    oneLv2.setCustAmt(oneDataDetail.get("CUSTAMT").toString());
                    oneLv2.setRefCustPrice(oneDataDetail.get("REFCUSTPRICE").toString());
                    oneLv2.setRefCustPriceBaseUnit(oneDataDetail.get("BASEUNIT").toString());
                    oneLv2.setPreTaxAmt(oneDataDetail.get("PRETAXAMT").toString());

                    oneLv2.setCanStockOutQty("0");
                    //canstockqty
                    if (!Check.Null(oneLv2.getOfNo()) && !Check.Null(oneLv2.getOItem()) && getNoticeQData.size() > 0) {
                        List<Map<String, Object>> noticeList = getNoticeQData.stream().filter(x -> x.get("BILLNO").toString().equals(oneLv2.getOfNo()) && x.get("ITEM").toString().equals(oneLv2.getOItem())).collect(Collectors.toList());
                        if (noticeList.size() > 0) {
                            BigDecimal pqty = new BigDecimal(noticeList.get(0).get("PQTY").toString());
                            BigDecimal stockoutqty = new BigDecimal(noticeList.get(0).get("STOCKOUTQTY").toString());
                            BigDecimal otherqty = new BigDecimal(noticeList.get(0).get("OTHERPQTY").toString());
                            BigDecimal canstockqty = pqty.subtract(stockoutqty).subtract(otherqty);
                            oneLv2.setCanStockOutQty(canstockqty.toString());
                        }
                    }


                    if (!Check.Null(oneLv2.getOfNo()) && !Check.Null(oneLv2.getOItem()) && getStockinQData.size() > 0) {
                        List<Map<String, Object>> stockInList = getStockinQData.stream().filter(x -> x.get("BILLNO").toString().equals(oneLv2.getOfNo()) && x.get("ITEM").toString().equals(oneLv2.getOItem())).collect(Collectors.toList());
                        if (stockInList.size() > 0) {
                            BigDecimal pqty = new BigDecimal(stockInList.get(0).get("PQTY").toString());
                            BigDecimal stockoutqty = new BigDecimal(stockInList.get(0).get("STOCKOUTQTY").toString());
                            BigDecimal otherqty = new BigDecimal(stockInList.get(0).get("OTHERPQTY").toString());
                            BigDecimal canstockqty = pqty.subtract(stockoutqty).subtract(otherqty);
                            oneLv2.setCanStockOutQty(canstockqty.toString());
                        }
                    }

                    if (!Check.Null(oneLv2.getOfNo()) && !Check.Null(oneLv2.getOItem()) && getCustomerQData.size() > 0) {
                        List<Map<String, Object>> customerList = getCustomerQData.stream().filter(x -> x.get("BILLNO").toString().equals(oneLv2.getOfNo()) && x.get("ITEM").toString().equals(oneLv2.getOItem())).collect(Collectors.toList());
                        if (customerList.size() > 0) {
                            BigDecimal stockoutNoQty = new BigDecimal(customerList.get(0).get("STOCKOUTNOQTY").toString());
                            BigDecimal pqty = new BigDecimal(customerList.get(0).get("PQTY").toString());
                            BigDecimal stockoutqty = new BigDecimal(customerList.get(0).get("STOCKOUTQTY").toString());
                            BigDecimal otherqty = new BigDecimal(customerList.get(0).get("OTHERPQTY").toString());
                            BigDecimal canstockqty = pqty.subtract(stockoutqty).subtract(otherqty);
                            if (stockoutNoQty.compareTo(stockoutqty) > 0) {
                                canstockqty = pqty.subtract(stockoutNoQty).subtract(otherqty);
                            }

                            oneLv2.setCanStockOutQty(canstockqty.toString());
                        }
                    }

                    BigDecimal distriAmt = new BigDecimal(oneLv2.getDistriAmt());
                    BigDecimal taxRate = new BigDecimal(oneLv2.getTaxRate());
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

                if (getImageQData != null && !getImageQData.isEmpty()) {
                    for (Map<String, Object> oneDataImage : getImageQData) {
                        String image = oneDataImage.get("IMAGE").toString();
                        DCP_SStockOutDetailQueryRes.ImageList oneLv3 = res.new ImageList();
                        oneLv3.setImage(image);
                        //添加单身
                        oneLv1.getImage_list().add(oneLv3);
                    }
                }

                if (getTransferQData != null && !getTransferQData.isEmpty()) {
                    for (Map<String, Object> oneDataTransfer : getTransferQData) {
                        DCP_SStockOutDetailQueryRes.TransactionList transactionList = res.new TransactionList();
                        transactionList.setItem(oneDataTransfer.get("ITEM").toString());
                        transactionList.setPluNo(oneDataTransfer.get("PLUNO").toString());
                        transactionList.setPluName(oneDataTransfer.get("PLUNAME").toString());
                        transactionList.setFeatureNo(oneDataTransfer.get("FEATURENO").toString());
                        transactionList.setFeatureName(oneDataTransfer.get("FEATURENAME").toString());
                        transactionList.setTaxCode(oneDataTransfer.get("TAXCODE").toString());
                        transactionList.setTaxName(oneDataTransfer.get("TAXNAME").toString());
                        transactionList.setTaxRate(oneDataTransfer.get("TAXRATE").toString());
                        transactionList.setInclTax(oneDataTransfer.get("INCLTAX").toString());
                        transactionList.setPUnit(oneDataTransfer.get("PUNIT").toString());
                        transactionList.setPUnitName(oneDataTransfer.get("PUNITNAME").toString());
                        transactionList.setPQty(oneDataTransfer.get("PQTY").toString());
                        transactionList.setPrice(oneDataTransfer.get("PRICE").toString());
                        transactionList.setAmt(oneDataTransfer.get("AMT").toString());
                        transactionList.setPreTaxAmt(oneDataTransfer.get("PRETAXAMT").toString());
                        transactionList.setTaxAmt(oneDataTransfer.get("TAXAMT").toString());
                        transactionList.setTaxCalType(oneDataTransfer.get("TAXCALTYPE").toString());
                        oneLv1.getTransactionList().add(transactionList);
                    }
                }

                if (getLotsQData != null && !getLotsQData.isEmpty()) {
                    for (Map<String, Object> oneDataLots : getLotsQData) {
                        DCP_SStockOutDetailQueryRes.LotsList lotsList = res.new LotsList();

                        lotsList.setItem(oneDataLots.get("ITEM").toString());
                        lotsList.setItem2(oneDataLots.get("ITEM2").toString());
                        lotsList.setPluNo(oneDataLots.get("PLUNO").toString());
                        lotsList.setPluName(oneDataLots.get("PLUNAME").toString());
                        lotsList.setFeatureNo(oneDataLots.get("FEATURENO").toString());
                        lotsList.setFeatureName(oneDataLots.get("FEATURENAME").toString());
                        lotsList.setWareHouse(oneDataLots.get("WAREHOUSE").toString());
                        lotsList.setWareHouseName(oneDataLots.get("WAREHOUSENAME").toString());
                        lotsList.setLocation(oneDataLots.get("LOCATION").toString());
                        lotsList.setLocationName(oneDataLots.get("LOCATIONNAME").toString());
                        lotsList.setBatchNo(oneDataLots.get("BATCHNO").toString());
                        lotsList.setProdDate(oneDataLots.get("PRODDATE").toString());
                        lotsList.setExpDate(oneDataLots.get("EXPDATE").toString());
                        lotsList.setPUnit(oneDataLots.get("PUNIT").toString());
                        lotsList.setPUnitName(oneDataLots.get("PUNITNAME").toString());
                        lotsList.setPQty(oneDataLots.get("PQTY").toString());
                        lotsList.setBaseUnit(oneDataLots.get("BASEUNIT").toString());
                        lotsList.setBaseUnitName(oneDataLots.get("BASEUNITNAME").toString());
                        lotsList.setBaseQty(oneDataLots.get("BASEQTY").toString());
                        oneLv1.getLotsList().add(lotsList);

                    }
                }

                //添加单头
                res.getDatas().add(oneLv1);
            }
        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_SStockOutDetailQueryReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        String eId = req.geteId();
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String sStockOutNo = req.getRequest().getSStockOutNo();
        String langType = req.getLangType();

        sqlbuf.append(" "
                + " select a.eid,a.shopid,a.sstockoutno,a.ofno,a.warehouse,a.supplier,a.bdate,a.taxcode,a.memo,a.status,"
                + " a.tot_cqty,a.tot_pqty,a.tot_amt,a.tot_distriamt,a.TOTDISTRIPRETAXAMT,a.TOTDISTRITAXAMT,"
                + " a.createby,a.create_date,a.create_time,a.modifyby,a.modify_date,a.modify_time,"
                + " a.submitby,a.submit_date,a.submit_time,a.confirmby,a.confirm_date,a.confirm_time,"
                + " a.cancelby,a.cancel_date,a.cancel_time,a.accountby,a.account_date,a.account_time,"
                + " a.process_erp_no,a.process_status,a.update_time,"
                + " e.stockoutallowtype,f.abbr,f.supplier_name,"
                + " h.taxname,i.warehouse_name,j.ISLOCATION,"
                + " p1.op_name as createname,p2.op_name as modifyname,p3.op_name as submitname,"
                + " p4.op_name as confirmname,p5.op_name as cancelname,p6.op_name as accountname,"
                + " a.employeeid,e1.name as employeename,a.departid,d1.departname,a.orderorgno,o1.org_name as orderorgname,a.paytype,o2.org_name as payorgname,a.payorgno," +//bul.udlength as baseunitudlength,
                "   a.billdateno,dbl.name as billdatedesc,a.paydateno,dpl.name as paydatedesc,a.invoicecode,dil.invoice_name as invoicename,a.currency ,dcl.name as currencyname ,a.stockouttype,a.otype,a.customer,db.sname as customername,db1.sname as suppliername," +
                "  a.originno,a.payee,a.payer,j.sname as payeename,k.sname as payername,a.taxpayer_type as taxpayertype,a.input_taxcode as inputtaxcode,a.input_taxrate as inputtaxrate,a.output_taxcode as outputtaxcode,a.output_taxrate as outputtaxrate   " +
                "  ,a.CORP,a.BIZORGNO,a.BIZCORP,o3.ORG_NAME CORPNAME,o4.ORG_NAME BIZORGNAME,o5.ORG_NAME BIZCORPNAME "
                + " from dcp_sstockout a"
                + " left  join dcp_supplier e on a.eid=e.eid and a.supplier=e.supplier"
                + " left  join dcp_supplier_lang f on a.eid=f.eid and a.supplier=f.supplier and f.lang_type='" + langType + "'"
                + " left  join dcp_taxcategory_lang h on a.eid=h.eid and a.taxcode=h.taxcode and h.lang_type='" + langType + "'"
                + " left  join dcp_warehouse_lang i on a.eid=i.eid and a.organizationno=i.organizationno and a.warehouse=i.warehouse and i.lang_type='" + langType + "'"
                + " left  join dcp_warehouse j on a.eid=j.eid and a.organizationno=j.organizationno and a.warehouse=j.warehouse "
                + " left  join PLATFORM_STAFFS_LANG p1 on a.eid=p1.eid and a.createby=p1.opno and p1.lang_type='" + req.getLangType() + "' "
                + " left  join PLATFORM_STAFFS_LANG p2 on a.eid=p2.eid and a.modifyby=p2.opno and p2.lang_type='" + req.getLangType() + "' "
                + " left  join PLATFORM_STAFFS_LANG p3 on a.eid=p3.eid and a.submitby=p3.opno and p3.lang_type='" + req.getLangType() + "' "
                + " left  join PLATFORM_STAFFS_LANG p4 on a.eid=p4.eid and a.confirmby=p4.opno and p4.lang_type='" + req.getLangType() + "' "
                + " left  join PLATFORM_STAFFS_LANG p5 on a.eid=p5.eid and a.cancelby=p5.opno and p5.lang_type='" + req.getLangType() + "' "
                + " left  join PLATFORM_STAFFS_LANG p6 on a.eid=p6.eid and a.accountby=p6.opno and p6.lang_type='" + req.getLangType() + "' "
                + " left join DCP_EMPLOYEE e1 on e1.eid=a.eid and e1.employeeno=a.employeeid " +
                " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.departid and d1.lang_type='" + langType + "' " +
                " left join DCP_ORG_LANG o1 on o1.eid=a.eid and o1.organizationno =a.orderorgno and o1.lang_type='" + langType + "' " +
                " left join DCP_ORG_LANG o2 on o2.eid=a.eid and o2.organizationno =a.payorgno and o2.lang_type='" + langType + "' " +
                " left join DCP_ORG_LANG o3 on o3.eid=a.eid and o3.organizationno =a.CORP and o3.lang_type='" + langType + "' " +
                " left join DCP_ORG_LANG o4 on o4.eid=a.eid and o4.organizationno =a.BIZORGNO and o4.lang_type='" + langType + "' " +
                " left join DCP_ORG_LANG o5 on o5.eid=a.eid and o5.organizationno =a.BIZCORP and o5.lang_type='" + langType + "' " +
                " left join DCP_BILLDATE_LANG dbl on dbl.eid=a.eid and dbl.BILLDATENO=a.billdateno and dbl.lang_type='" + langType + "' " +
                " left join DCP_PAYDATE_LANG dpl on dpl.eid=a.eid and dpl.paydateno=a.paydateno and dpl.lang_type='" + langType + "' " +
                " left join DCP_INVOICETYPE_LANG dil on dil.eid=a.eid and dil.invoicecode =a.invoicecode and dil.lang_type='" + langType + "' " +
                " left join DCP_CURRENCY_LANG dcl on dcl.eid=a.eid and dcl.currency=a.currency and dcl.lang_type='" + langType + "' " +
                " left join dcp_bizpartner db on db.eid=a.eid and db.BIZPARTNERNO=a.customer " +
                " left join dcp_bizpartner db1 on db1.eid=a.eid and db1.BIZPARTNERNO=a.supplier " +
                " left join dcp_bizpartner j on j.eid=a.eid and j.bizpartnerno=a.payee " +
                " left join dcp_bizpartner k on k.eid=a.eid and k.bizpartnerno=a.payer " +
                " where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "' and a.sstockoutno='" + sStockOutNo + "'"
                + " ");

        return sqlbuf.toString();
    }

    private String getDetailQuerySql(DCP_SStockOutDetailQueryReq req) {
        StringBuffer sqlbuf = new StringBuffer();
        String eid = req.geteId();
        String langType = req.getLangType();
        String organizationNO = req.getOrganizationNO();
        String sStockOutNo = req.getRequest().getSStockOutNo();

        sqlbuf.append(" "
                + " select "
                + " b.item,b.oitem,b.pluno,b.featureno,b.batch_no,b.prod_date,"
                + " b.pqty,b.punit,b.baseqty,b.baseunit,b.unit_ratio,b.price,b.distriprice,b.amt,b.distriamt,"
                + " c.isbatch,c.stockinvalidday,c.stockoutvalidday,c.shelflife,"
                + " d.plu_name,"
                + " g.punit as sstockin_punit,g.retwqty as sstockin_retwqty,g.unit_ratio as sstockin_unit_ratio,g.pqty as sstockin_pqty,"
                + " gul.spec,image.listimage as listimage,fn.featurename,"
                + " u.udlength,u1.uname as punitname,u2.uname as baseunitname,u3.uname as sstockin_unitname,b.otype,b.ofno,h.category,h.category_name as categoryname," +
                "i.location ,i.locationname,b.pTemplateNo,b.isgift,b.EXPDATE,b.originno,b.originitem,j.PURBASEPRICE,j.minrate,j.maxrate ,'0' as canStockOutQty,b.RETURNQTY,b.taxCode,b.taxRate, b.inclTax, b.taxCalType,b.warehouse," +
                " bul.udlength as BASEUNITUDLENGTH  "
                + " ,k.BSNO,k.REASON_NAME BSNAME,b.oofno,b.ooitem,b.PURPRICE,b.PURAMT,b.REFPURPRICE,b.CUSTPRICE,b.CUSTAMT,b.REFCUSTPRICE,b.PRETAXAMT "
                + " from dcp_sstockout a"
                + " inner join dcp_sstockout_detail b on a.eid=b.eid and a.shopid=b.shopid and a.sstockoutno=b.sstockoutno"
                + " inner join dcp_goods c on a.eid=c.eid and b.pluno=c.pluno"
                + " left  join dcp_goods_lang d on a.eid=d.eid and b.pluno=d.pluno and d.lang_type='" + langType + "'"
                + " left  join dcp_sstockin_detail g on a.eid=g.eid and a.shopid=g.shopid and a.ofno=g.sstockinno and b.oitem=g.item"
                + " left  join dcp_goods_unit_lang gul on a.eid=gul.eid and d.pluno=gul.pluno and b.punit=gul.ounit and gul.lang_type='" + langType + "'"
                + " left  join dcp_goodsimage image on image.eid=a.eid and image.pluno=d.pluno and image.apptype='ALL'"
                + " left  join dcp_goods_feature_lang fn on a.eid=fn.eid and b.pluno=fn.pluno and b.featureno=fn.featureno and fn.lang_type='" + langType + "'"
                + " left  join dcp_unit u on a.eid = u.eid and b.punit=u.unit"
                + " left  join dcp_unit_lang u1 on a.eid=u1.eid and b.punit=u1.unit and u1.lang_type='" + langType + "'"
                + " left  join dcp_unit_lang u2 on a.eid=u2.eid and b.baseunit=u2.unit and u2.lang_type='" + langType + "'"
                + " left  join dcp_unit_lang u3 on a.eid=u3.eid and g.punit=u3.unit and u3.lang_type='" + langType + "'"
                + " left  join dcp_unit bul on a.eid=bul.eid and b.baseunit=bul.unit" +
                " left join dcp_category_lang h on h.eid=a.eid and h.category=b.category and h.lang_type='" + langType + "' " +
                " left join DCP_LOCATION i on i.eid=a.eid and i.organizationno=a.organizationno and i.location=b.location " +
                " left join DCP_PURCHASETEMPLATE_GOODS j on j.eid=a.eid and j.PURTEMPLATENO=b.ptemplateno and j.pluno=b.pluno " +
                " left join DCP_REASON_LANG k on k.eid=a.eid and k.bstype='17' and k.lang_type='" + langType + "' and k.bsno=b.bsno  "
                + " where a.eid='" + eid + "' and a.organizationno='" + organizationNO + "' and a.sstockoutno='" + sStockOutNo + "'");

        return sqlbuf.toString();
    }

    private String getImageQuerySql(DCP_SStockOutDetailQueryReq req) {
        StringBuffer sqlbuf = new StringBuffer();
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String sStockOutNo = req.getRequest().getSStockOutNo();

        sqlbuf.append("select * from dcp_sstockout_image a" +
                " where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "' " +
                " and a.sstockoutno='" + sStockOutNo + "' ");
        return sqlbuf.toString();
    }

    private String getTransferQuerySql(DCP_SStockOutDetailQueryReq req) {
        String eid = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String langType = req.getLangType();
        String sStockOutNo = req.getRequest().getSStockOutNo();
        StringBuffer sqlbuf = new StringBuffer();

        sqlbuf.append("select a.item,a.pluno,a.featureno,b.plu_name as pluname,c.featurename,d.taxcode,d.taxname,a.taxrate,a.INCLTAX," +
                " a.punit,e.uname as punitname,a.pqty,a.price,a.amt,a.preTaxAmt,a.taxamt,a.taxcaltype" +
                " from DCP_TRANSACTION a " +
                " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='" + langType + "' " +
                " left join DCP_GOODS_FEATURE_LANG c on a.eid=c.eid and a.pluno=c.pluno and a.featureno=c.featureno and c.lang_type='" + langType + "' " +
                " left join DCP_TAXCATEGORY_LANG d on d.eid=a.eid and a.taxcode=d.taxcode and d.lang_type='" + langType + "' and d.taxarea='CN' " +
                " left join dcp_unit_lang e on e.eid=a.eid and e.unit=a.punit and e.lang_type='" + langType + "' " +
                " where a.eid='" + eid + "' and a.organizationno='" + organizationNO + "' " +
                " and a.billno='" + sStockOutNo + "' ");

        return sqlbuf.toString();
    }

    private String getBatchQuerySql(DCP_SStockOutDetailQueryReq req) {
        StringBuffer sqlbuf = new StringBuffer();
        String eid = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String sStockOutNo = req.getRequest().getSStockOutNo();
        String langType = req.getLangType();
        sqlbuf.append("select a.item,a.item2,a.pluno,b.plu_name as pluname,c.featureno,c.featurename,d.warehouse,d.warehouse_name as warehousename,f.location,f.locationname,  " +
                " a.batchno,a.proddate,a.expdate,a.punit,e.uname as punitname," +
                " a.pqty,a.baseunit,a.baseqty,g.uname as baseunitname " +
                " from DCP_SSTOCKOUT_BATCH a" +
                " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='" + langType + "' " +
                " left join DCP_GOODS_FEATURE_LANG c on a.eid=c.eid and a.pluno=c.pluno and a.featureno=c.featureno and c.lang_type='" + langType + "' " +
                " left join DCP_WAREHOUSE_LANG d on d.eid=a.eid and d.organizationno=a.organizationno and a.warehouse=d.warehouse " +
                " left join DCP_LOCATION f on f.eid=a.eid and f.location=a.location and f.organizationno=a.organizationno and f.warehouse=a.warehouse " +
                " left join dcp_unit_lang e on e.eid=a.eid and e.unit=a.punit and e.lang_type='" + langType + "' " +
                " left join dcp_unit_lang g on g.eid=a.eid and g.unit=a.baseunit and g.lang_type='" + langType + "' " +
                " where a.eid='" + eid + "' and a.organizationno='" + organizationNO + "' and a.sstockoutno='" + sStockOutNo + "'" +
                " order by a.item2,a.item ");

        return sqlbuf.toString();
    }
}
