package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataGenerateReq;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataGenerateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.tax.TaxAmount;
import com.dsc.spos.utils.tax.TaxAmountCalculation;
import com.dsc.spos.utils.tax.TaxUtils;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DCP_InterSettleDataGenerate extends SPosAdvanceService<DCP_InterSettleDataGenerateReq, DCP_InterSettleDataGenerateRes> {

    @Data
    @EqualsAndHashCode(of = {"supplyOrgNo", "receiptOrgNo"})
    private class OrgantionRelationship {
        private String supplyOrgNo;
        private String receiptOrgNo;

        //这两个通过set回写
        private String supplyCorp;
        private String receiptCorp;

        //配置信息写入ort
        List<Map<String, Object>> interSettSetting;

    }

    @Getter
    public enum BillType {

//        单据类型billType:
//        ● 正向业务：1
//        调拨类00：10000要货申请、10002配货通知、10003配货出库、10004配货入库；
//        采购类10：11000采购订单、11001采购收货通知、11002采购入库；
//        销售类20：12000大客订单、12001销货通知、12002销货出库；
//
//        ● 逆向业务：-1
//        调拨类00：-10003退配出库、-10004退配入库
//        采购类10：-11001采退通知、-11002采退出库
//        销售类20：-12001销退通知、-12002销退入库

        //1.采购 2.销售 3.调拨
        BillType10000("3", "10000", "要货申请"),
        BillType10002("3", "10002", "配货通知"),
        BillType10003("3", "10003", "配货出库"),
        BillType10004("3", "10004", "配货入库"),
        BillType10005("3", "10005", "调拨出库"),
        BillType10006("3", "10006", "调拨入库"),
        BillType11000("1", "11000", "采购订单"),
        BillType11001("1", "11001", "采购收货通知"),
        BillType11002("1", "11002", "采购入库"),
        BillType12000("2", "12000", "大客订单"),
        BillType12001("2", "12001", "销货通知"),
        BillType12002("2", "12002", "销货出库"),
        BillType_10003("3", "-10003", "退配出库"),
        BillType_10004("3", "-10004", "退配入库"),
        BillType_11001("1", "-11001", "采退通知"),
        BillType_11002("1", "-11002", "采退出库"),
        BillType_12001("2", "-12001", "销退通知"),
        BillType_12002("2", "-12002", "销退入库");

        final String dataType;
        final String type;
        final String name;

        BillType(String dataType, String type, String name) {
            this.dataType = dataType;
            this.name = name;
            this.type = type;
        }

        public static BillType getBillType(String type) {
            for (BillType billType : BillType.values()) {
                if (billType.type.equals(type)) {
                    return billType;
                }
            }
            return null;
        }

    }

    @Getter
    public enum BType {
        B_TYPE_63("63", "跨法人调拨-采购入库"),
        B_TYPE_64("64", "跨法人调拨-采退出库"),
        B_TYPE_65("65", "跨法人调拨-销售出库"),
        B_TYPE_66("66", "跨法人调拨-销退入库");

        BType(String bType, String name) {
            this.type = bType;
            this.name = name;
        }

        final String type;
        final String name;
    }

    private int getDirection(String billType) {
        BillType bt = BillType.getBillType(billType);
        int dir = 0; //调拨会有两种，这个方法就没什么用了
        if (bt != null && (bt != BillType.BillType10006 && bt != BillType.BillType10005)) {  //调拨出入库特殊处理
//        if (bt != null && !"3".equals(bt.getBType())) { //不是调拨类单据
            if (StringUtils.startsWith(bt.getType(), "-")) {
                dir = -1;
            } else {
                dir = 1;
            }
        }
        return dir;
    }

    private String billTypeToBType(String billType, int dir, boolean supplyAngle) {

        String bType;
//        ● 正向流程+供方视角：销售出库；
//        ● 逆向流程+供方视角：销退入库；
//        ● 正向流程+需方视角：采购入库；
//        ● 逆向流程+需方视角：采退出库

        if (supplyAngle) {
            if (dir > 0) {
                bType = BType.B_TYPE_65.getType();
            } else {
                bType = BType.B_TYPE_66.getType();
            }
        } else {
            if (dir > 0) {
                bType = BType.B_TYPE_63.getType();
            } else {
                bType = BType.B_TYPE_64.getType();
            }
        }

        return bType;
    }

    private void getPrice4Response(DCP_InterSettleDataGenerateReq req, DCP_InterSettleDataGenerateRes res, List<ResponsePrice> priceList) {

        if ("Y".equals(req.getRequest().getReturnSupplyPrice())) {

            List<DCP_InterSettleDataGenerateReq.Detail> detail =
                    req.getRequest().getDetail();
            if (CollectionUtils.isEmpty(res.getSupPriceDetail())) {
                res.setSupPriceDetail(new ArrayList<>());
            }
            for (DCP_InterSettleDataGenerateReq.Detail thisDetail : detail) {
                DCP_InterSettleDataGenerateRes.SupPriceDetail onePrice = res.new SupPriceDetail();
                res.getSupPriceDetail().add(onePrice);
                ResponsePrice search = new ResponsePrice();
                search.setItem(thisDetail.getItem());
                search.setPluNo(thisDetail.getPluNo());
                search.setReceiveOrgNo(thisDetail.getReceiveOrgNo());
                if (priceList.contains(search)) {
                    search = priceList.get(priceList.indexOf(search));

                    onePrice.setSupplyPrice(search.getSupplyPrice());
                    onePrice.setSupplyOrgNo(search.getSupplyOrg());
                    onePrice.setReceivePrice(search.getReceiptPrice());

                } else {
                    onePrice.setSupplyPrice(thisDetail.getSupplyPrice());
                    onePrice.setSupplyOrgNo(req.getRequest().getSupplyOrgNo());
                    onePrice.setReceivePrice(thisDetail.getReceivePrice());

                }

                onePrice.setItem(thisDetail.getItem());
                onePrice.setPluNo(thisDetail.getPluNo());
                onePrice.setFeatureNo(thisDetail.getFeatureNo());
                onePrice.setReceiveOrgNo(thisDetail.getReceiveOrgNo());
                onePrice.setPUnit(thisDetail.getPUnit());

            }
        }
    }

    @Override
    protected void processDUID(DCP_InterSettleDataGenerateReq req, DCP_InterSettleDataGenerateRes res) throws Exception {
        //获取结算关系
        List<OrgantionRelationship> ortList = new ArrayList<>();
        BillType eBillType = BillType.getBillType(req.getRequest().getBillType());
        if (null == eBillType){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据类型错误！");
        }
        if (BillType.BillType10006.equals(eBillType) && Check.isEmpty(req.getRequest().getSupplyOrgNo())) {
            List<Map<String, Object>> qSource = doQueryData(getQueryType10006SourceSql(req), null);

            if (CollectionUtils.isEmpty(qSource)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "没有对应的来源单据！");
            }

            req.getRequest().setSupplyOrgNo(qSource.get(0).get("ORGANIZATIONNO").toString());

            for (Map<String, Object> oneSource : qSource) {
                List<DCP_InterSettleDataGenerateReq.Detail> details = req.getRequest().getDetail();
                String item = oneSource.get("ITEM").toString();
//                List<DCP_InterSettleDataGenerateReq.Detail> searchDetail = details.stream().filter(x -> item.equals(x.getItem())).collect(Collectors.toList());

                for (DCP_InterSettleDataGenerateReq.Detail oneDetail : details) {
                    if (item.equals(oneDetail.getItem())) {
                        oneDetail.setSupplyPrice(oneSource.get("PRICE").toString());
                        double supplyAmt = BigDecimalUtils.mul(Double.parseDouble(oneSource.get("PRICE").toString()), Double.parseDouble(oneDetail.getPQty()));
                        oneDetail.setSupplyAmt(String.valueOf(supplyAmt));
                    }
                }
            }
        }


        for (DCP_InterSettleDataGenerateReq.Detail detail : req.getRequest().getDetail()) {
            OrgantionRelationship ort = new OrgantionRelationship();

            if (StringUtils.isNotEmpty(req.getRequest().getSupplyOrgNo())
                    && StringUtils.isNotEmpty(detail.getReceiveOrgNo())
                    && !req.getRequest().getSupplyOrgNo().equals(detail.getReceiveOrgNo())
            ) {
                ort.setSupplyOrgNo(req.getRequest().getSupplyOrgNo());
                ort.setReceiptOrgNo(detail.getReceiveOrgNo());

                if (!ortList.contains(ort)) {
                    ortList.add(ort);
                }
            }
        }

        for (OrgantionRelationship ort : ortList) {
            Map<String, String> corpData = PosPub.getCorpByOrgNo(ort.getSupplyOrgNo(), ort.getReceiptOrgNo());
            if (!corpData.get(ort.getSupplyOrgNo())
                    .equals(corpData.get(ort.getReceiptOrgNo()))
            ) {
                ort.setSupplyCorp(corpData.get(ort.getSupplyOrgNo()));
                ort.setReceiptCorp(corpData.get(ort.getReceiptOrgNo()));

                List<Map<String, Object>> interSettSetData = doQueryData(getQueryInterSettSetting(req, ort.getReceiptCorp(), ort.getSupplyCorp()), null);

                ort.setInterSettSetting(interSettSetData);
            }
        }

        List<ResponsePrice> responsePriceList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(ortList)) {

            //删除单据
            String billType = req.getRequest().getBillType();
            String billNo = req.getRequest().getBillNo();
            ColumnDataValue condition = new ColumnDataValue();
            condition.add("EID", req.geteId());
            condition.add("BILLNO", billNo);
            int dir = getDirection(billType);
            boolean supplyAngle = req.getRequest().getOrganizationNo().equals(req.getRequest().getSupplyOrgNo());
            String bType = billTypeToBType(billType, dir, supplyAngle);
//            if (dir != 0) {
//                condition.add("BTYPE", bType);
//            }
            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_DETAIL", condition)));
            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_ROUTE", condition)));

            List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
            String organizationNo = req.getRequest().getOrganizationNo();
            Map<String, String> corpData = PosPub.getCorpByOrgNo(organizationNo);
            String corp = corpData.get(organizationNo);

            //重新生成
            String bDate = DateFormatUtils.getPlainDate(qData.get(0).get("BDATE").toString());
            String accountDate = DateFormatUtils.getPlainDate(qData.get(0).get("BDATE").toString());

            TaxUtils taxUtils = new TaxUtils();

            List<OrgPluPrice> orgPluPriceList = getOrgPluPriceList(
                    req,
                    ortList
            );
            Map<String, Object> interSet;

            //写入表
            for (DCP_InterSettleDataGenerateReq.Detail detail : req.getRequest().getDetail()) {


                Map<String, Object> searcher = new HashMap<>();
                searcher.put("ITEM", detail.getItem());
                Map<String, Object> oneData = MapDistinct.getWhereMap(qData, searcher, false).get(0);

                OrgantionRelationship ort = new OrgantionRelationship();
                ort.setSupplyOrgNo(req.getRequest().getSupplyOrgNo());
                ort.setReceiptOrgNo(detail.getReceiveOrgNo());

                List<Map<String, Object>> orgInterSettSet = null;

                TaxUtils.InterTax interTax = taxUtils.getInterTax(req.geteId(), corp);

                if (ortList.contains(ort)) {
                    ort = ortList.get(ortList.indexOf(ort));
                    orgInterSettSet = ort.getInterSettSetting();

                }

                ColumnDataValue dcp_interSettle_detail = new ColumnDataValue();
                dcp_interSettle_detail.add("EID", req.geteId());
                dcp_interSettle_detail.add("ORGANIZATIONNO", req.getRequest().getOrganizationNo());
                dcp_interSettle_detail.add("CORP", corp);
                dcp_interSettle_detail.add("BDATE", bDate);
                dcp_interSettle_detail.add("BILLNO", req.getRequest().getBillNo());
                dcp_interSettle_detail.add("ITEM", oneData.get("ITEM").toString());

                dcp_interSettle_detail.add("DATATYPE", DataValues.newString(eBillType.getDataType()));
                dcp_interSettle_detail.add("RECEIVEORGNO", ort.getReceiptOrgNo());
                dcp_interSettle_detail.add("RECEIVECORP", ort.getReceiptCorp());
                dcp_interSettle_detail.add("SUPPLYORGNO", ort.getSupplyOrgNo());
                dcp_interSettle_detail.add("SUPPLYCORP", ort.getSupplyCorp());

                dcp_interSettle_detail.add("PLUNO", oneData.get("PLUNO").toString());
                dcp_interSettle_detail.add("FEATURENO", oneData.get("FEATURENO").toString());
                dcp_interSettle_detail.add("PUNIT", oneData.get("PUNIT").toString());
                dcp_interSettle_detail.add("PQTY", oneData.get("PQTY").toString());

                double pQty = Double.parseDouble(oneData.get("PQTY").toString());

                //RECEIVEPRICE 取值优先级
                OrgPluPrice orgRevPluPrice = new OrgPluPrice();
                orgRevPluPrice.setPluNo(oneData.get("PLUNO").toString());
                orgRevPluPrice.setSupplyOrg(ort.getSupplyCorp());
                orgRevPluPrice.setDemandOrg(ort.getReceiptOrgNo());

                double receivePrice = 0;
                double receiveAmt = 0;
                double supplierPrice = 0;
                double supplierAmt = 0;

                if (!orgPluPriceList.contains(orgRevPluPrice)) {
                    orgRevPluPrice.setDemandOrg(ort.getReceiptCorp());
                }

                if (CollectionUtils.isNotEmpty(orgInterSettSet)) {
                    interSet = orgInterSettSet.get(0);
                    orgRevPluPrice.setSupplyOrg(interSet.get("SUPPLYOBJECT").toString());
                    interSet = orgInterSettSet.get(orgInterSettSet.size() - 1);
                    orgRevPluPrice.setDemandOrg(interSet.get("DEMANDOBJECT1").toString());
                }

                if (orgPluPriceList.contains(orgRevPluPrice)) {
                    orgRevPluPrice = orgPluPriceList.get(orgPluPriceList.indexOf(orgRevPluPrice));
                    receivePrice = Double.parseDouble(orgRevPluPrice.getDistriPrice());
                } else {
                    receivePrice = Double.parseDouble(detail.getReceivePrice());
                }
                receiveAmt = receivePrice * pQty;
                dcp_interSettle_detail.add("RECEIVEPRICE", DataValues.newString(receivePrice));
                dcp_interSettle_detail.add("RECEIVEAMT", DataValues.newDecimal(receiveAmt));

                OrgPluPrice orgSupPluPrice = new OrgPluPrice();
                //SUPPLYPRICE 取值优先级
                orgSupPluPrice.setPluNo(oneData.get("PLUNO").toString());
                orgSupPluPrice.setSupplyOrg(ort.getSupplyCorp());

                if (CollectionUtils.isNotEmpty(orgInterSettSet)) {
                    String supplyCorp = ort.getSupplyCorp();
                    String supplyOrg = ort.getSupplyOrgNo();
                    interSet = orgInterSettSet.stream().filter(x -> StringUtils.equals(supplyCorp, x.get("SUPPLYOBJECT").toString())
                            || StringUtils.equals(supplyOrg, x.get("SUPPLYOBJECT").toString())
                    ).collect(Collectors.toList()).get(0);
                    orgSupPluPrice.setDemandOrg(interSet.get("DEMANDOBJECT1").toString());
                }

                if (!orgPluPriceList.contains(orgSupPluPrice)) {
                    orgSupPluPrice.setSupplyOrg(ort.getSupplyCorp());
                    orgSupPluPrice.setDemandOrg(ort.getReceiptCorp());
                }

                if (!orgPluPriceList.contains(orgSupPluPrice)) {
                    orgSupPluPrice.setSupplyOrg(ort.getSupplyCorp());
                    orgSupPluPrice.setDemandOrg(ort.getReceiptOrgNo());
                }

                if (orgPluPriceList.contains(orgSupPluPrice)) {
                    orgSupPluPrice = orgPluPriceList.get(orgPluPriceList.indexOf(orgSupPluPrice));
                    supplierPrice = Double.parseDouble(orgSupPluPrice.getDistriPrice());

                } else {
                    dcp_interSettle_detail.add("SUPPLYPRICE", detail.getSupplyPrice());
                    dcp_interSettle_detail.add("SUPPLYAMT", detail.getSupplyAmt());
                    supplierPrice = Double.parseDouble(detail.getSupplyPrice());

                }
                supplierAmt = supplierPrice * pQty;
                dcp_interSettle_detail.add("SUPPLYPRICE", Double.toString(supplierPrice));
                dcp_interSettle_detail.add("SUPPLYAMT", DataValues.newDecimal(supplierAmt));

                ResponsePrice responsePrice = new ResponsePrice();
                responsePrice.setPluNo(detail.getPluNo());
                responsePrice.setItem(detail.getItem());
                responsePrice.setReceiveOrgNo(detail.getReceiveOrgNo());
                responsePrice.setSupplyPrice(String.valueOf(supplierPrice));
                responsePrice.setReceiptPrice(String.valueOf(receivePrice));
                responsePrice.setSupplyOrg(ort.getSupplyOrgNo());
                responsePriceList.add(responsePrice);

                TaxUtils.Tax tax;
                if (Check.isNotEmpty(interTax.getTaxPayerType())) {
                    if (TaxUtils.TaxPayerType.TYPE_GENERAL.getType().equals(interTax.getTaxPayerType())) {
                        tax = taxUtils.getTax(req.geteId(), req.getRequest().getSupplyOrgNo(), detail.getPluNo());
                    } else {
                        tax = interTax.getInPutTax();
                    }
                } else {
                    tax = taxUtils.getTax(req.geteId(), req.getRequest().getSupplyOrgNo(), detail.getPluNo());
                }

                dcp_interSettle_detail.add("TAXCODE", tax.getTaxCode());
                dcp_interSettle_detail.add("TAXRATE", DataValues.newDecimal(tax.getTaxRate()));

                double amt = 0;
                double preTaxAmt = 0;
                double taxAmt = 0;

                // 当业务类型属于“采购”时，用进货金额RECEIVEAMT计算未税金额、税额；
                if (bType.equals("63") || bType.equals("64")) {
                    amt = receiveAmt;
                }
                //当业务类型属于“销售”时，用供货金额SUPPLYAMT计算未税金额、税额；
                else {
                    amt = supplierAmt;
                }
                TaxAmount taxAmount = TaxAmountCalculation.calculateAmount("Y".equals(tax.getInclTax()), amt, tax.getTaxRate(), "1", 2);
                preTaxAmt = taxAmount.getPreAmount();

                taxAmt = taxAmount.getTaxAmount();
                dcp_interSettle_detail.add("PRETAXAMT", DataValues.newString(preTaxAmt));
                dcp_interSettle_detail.add("TAXAMT", DataValues.newString(taxAmt));


                dcp_interSettle_detail.add("DIRECTION", String.valueOf(getDirection(req)));

                dcp_interSettle_detail.add("BTYPE", bType);


                dcp_interSettle_detail.add("SOURCEBILLNO", detail.getSourceBillNo());
                dcp_interSettle_detail.add("SOURCEITEM", detail.getSourceItem());

                if (Check.isNotEmpty(interTax.getTaxPayerType())) {
                    dcp_interSettle_detail.add("TAXPAYER_TYPE", interTax.getTaxPayerType());
                    dcp_interSettle_detail.add("INPUT_TAXCODE", interTax.getInPutTax().getTaxCode());
                    dcp_interSettle_detail.add("INPUT_TAXRATE", DataValues.newString(interTax.getInPutTax().getTaxRate()));
                    dcp_interSettle_detail.add("OUTPUT_TAXCODE", interTax.getOutPutTax().getTaxCode());
                    dcp_interSettle_detail.add("OUTPUT_TAXRATE", DataValues.newString(interTax.getOutPutTax().getTaxRate()));
                }
                dcp_interSettle_detail.add("IS_SETTLEMENT", getSettlement(req));

                //无配置
                if (CollectionUtils.isEmpty(orgInterSettSet)) {
                    dcp_interSettle_detail.add("PROCESSNO", ort.getSupplyCorp() + "-" + ort.getReceiptCorp());
                    dcp_interSettle_detail.add("VERSIONNUM", "0");
                    if (StringUtils.equals(corp, ort.getReceiptCorp())) {
                        dcp_interSettle_detail.add("SETTLECORP", ort.getSupplyCorp());
                    } else {
                        dcp_interSettle_detail.add("SETTLECORP", ort.getReceiptCorp());
                    }

                } else {

                    //1.组织所属法人=需求法人时，用需求对象找供货对象，结算对象=供货对象supplyobject
                    if (StringUtils.equals(corp, ort.getReceiptCorp())) {
                        String receptCorp = ort.getReceiptCorp();
                        String receptOrg = ort.getReceiptOrgNo();

                        List<Map<String, Object>> filter = ort.getInterSettSetting().stream().filter(x -> StringUtils.equals(x.get("DEMANDOBJECT1").toString(), receptCorp)
                                || StringUtils.equals(x.get("DEMANDOBJECT1").toString(), receptOrg)
                        ).collect(Collectors.toList());
                        interSet = filter.get(0);
                        dcp_interSettle_detail.add("SETTLECORP", interSet.get("SUPPLYOBJECT").toString());
                    }
                    //2.组织所属法人=供货法人时，根据供货对象找需求对象，结算对象=demandObject1
                    else {

                        String supplyCorp = ort.getSupplyCorp();
                        String supplyOrg = ort.getSupplyOrgNo();


                        List<Map<String, Object>> filter = ort.getInterSettSetting().stream().filter(x -> StringUtils.equals(x.get("DEMANDOBJECT1").toString(), supplyCorp)
                                || StringUtils.equals(x.get("SUPPLYOBJECT").toString(), supplyOrg)
                        ).collect(Collectors.toList());
                        interSet = filter.get(0);
                        dcp_interSettle_detail.add("SETTLECORP", interSet.get("DEMANDOBJECT1").toString());
                    }

                    dcp_interSettle_detail.add("PROCESSNO", ort.getInterSettSetting().get(0).get("PROCESSNO").toString());
                    dcp_interSettle_detail.add("VERSIONNUM", ort.getInterSettSetting().get(0).get("VERSIONNUM").toString());
                }

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_INTERSETTLE_DETAIL", dcp_interSettle_detail)));

            }


            addInsertInterRoute(req, corp, bType, bDate, accountDate, ortList);

            this.doExecuteDataToDB();
        }

        //获取价格信息
        getPrice4Response(req, res, responsePriceList);

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    private String getSettlement(DCP_InterSettleDataGenerateReq req) {
        String isSettlement = "N";
//        需生成底稿=Y的单据类型包括：采购入库、采退出库、销货出库、销退入库、配送入库、退配入库、调拨入库）
        BillType nowBillType = BillType.getBillType(req.getRequest().getBillType());
        if (nowBillType != null) {
            switch (nowBillType) {
                case BillType11002:
                case BillType_11002:
                case BillType12002:
                case BillType_12002:
                case BillType10004:
                case BillType_10004:
                case BillType10006:
//                case BillType10005:
//                case BillType10003:
                    isSettlement = "Y";
                    break;
            }
        }
        return isSettlement;
    }


    //通过结算配置写入结算路径
    private void addInsertInterRoute(DCP_InterSettleDataGenerateReq req,
                                     String corp,
                                     String bType,
                                     String bDate,
                                     String accountDate,
                                     List<OrgantionRelationship> ortList
    ) throws Exception {
        //获取最大结算顺序
        int maxFSortId = 0;
        for (OrgantionRelationship ort : ortList) {
            if (CollectionUtils.isNotEmpty(ort.getInterSettSetting())) {
                maxFSortId = maxFSortId + ort.getInterSettSetting().size();
            }
        }
        //获取方向
        int direction = getDirection(req);
        int fSortId = 0;
        if (direction > 0) {
            fSortId++;
        } else {
            fSortId = maxFSortId + 1;
            fSortId--;
        }

        //写结算路径
        for (OrgantionRelationship ort : ortList) {

            if (CollectionUtils.isNotEmpty(ort.getInterSettSetting())) {
                int sortId = 1;
                for (Map<String, Object> oneSet : ort.getInterSettSetting()) {
                    ColumnDataValue dcp_interSettle_route = new ColumnDataValue();
                    dcp_interSettle_route.add("EID", req.geteId());
                    dcp_interSettle_route.add("ORGANIZATIONNO", req.getRequest().getOrganizationNo());
                    dcp_interSettle_route.add("CORP", corp);
                    dcp_interSettle_route.add("BDATE", bDate);
                    dcp_interSettle_route.add("ACCOUNTDATE", accountDate);
                    dcp_interSettle_route.add("BILLNO", req.getRequest().getBillNo());
                    dcp_interSettle_route.add("PROCESSNO", oneSet.get("PROCESSNO").toString());
                    dcp_interSettle_route.add("VERSIONNUM", oneSet.get("VERSIONNUM").toString());
                    dcp_interSettle_route.add("DIRECTION", DataValues.newDecimal(direction));
                    dcp_interSettle_route.add("PSORTID", DataValues.newDecimal(fSortId));
                    dcp_interSettle_route.add("SUPPLYCORP", ort.getSupplyCorp());
                    dcp_interSettle_route.add("RECEIVECORP", ort.getReceiptCorp());
                    dcp_interSettle_route.add("BTYPE", bType);
                    dcp_interSettle_route.add("SORTID", DataValues.newDecimal(sortId++));

                    if (direction > 0) {
                        dcp_interSettle_route.add("STARTPOINT", oneSet.get("SUPPLYOBJECT").toString());
                        dcp_interSettle_route.add("ENDPOINT", oneSet.get("DEMANDOBJECT1").toString());
                    } else {
                        dcp_interSettle_route.add("STARTPOINT", oneSet.get("DEMANDOBJECT1").toString());
                        dcp_interSettle_route.add("ENDPOINT", oneSet.get("SUPPLYOBJECT").toString());
                    }

                    this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_INTERSETTLE_ROUTE", dcp_interSettle_route)));

                }
            } else {
                int sortId = 1;
                ColumnDataValue dcp_interSettle_route = new ColumnDataValue();
                dcp_interSettle_route.add("EID", req.geteId());
                dcp_interSettle_route.add("ORGANIZATIONNO", req.getRequest().getOrganizationNo());
                dcp_interSettle_route.add("CORP", corp);
                dcp_interSettle_route.add("BDATE", bDate);
                dcp_interSettle_route.add("ACCOUNTDATE", accountDate);
                dcp_interSettle_route.add("BILLNO", req.getRequest().getBillNo());
//                3.未匹配到结算关系：新编虚拟流程编号，编码规则：SUPPLYCORP-RECEIVECORP；版本号=0
                dcp_interSettle_route.add("PROCESSNO", ort.getSupplyCorp() + "-" + ort.getReceiptCorp());
                dcp_interSettle_route.add("VERSIONNUM", "0");
                dcp_interSettle_route.add("DIRECTION", DataValues.newDecimal(direction));
                dcp_interSettle_route.add("PSORTID", DataValues.newDecimal(fSortId));
                dcp_interSettle_route.add("SUPPLYCORP", ort.getSupplyCorp());
                dcp_interSettle_route.add("RECEIVECORP", ort.getReceiptCorp());
                dcp_interSettle_route.add("BTYPE", bType);
                dcp_interSettle_route.add("SORTID", DataValues.newDecimal(sortId++));

                if (direction > 0) {
                    dcp_interSettle_route.add("STARTPOINT", ort.getSupplyCorp());
                    dcp_interSettle_route.add("ENDPOINT", ort.getReceiptCorp());
                } else {
                    dcp_interSettle_route.add("STARTPOINT", ort.getReceiptCorp());
                    dcp_interSettle_route.add("ENDPOINT", ort.getSupplyCorp());
                }

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_INTERSETTLE_ROUTE", dcp_interSettle_route)));

            }

        }


    }


    @Override
    protected List<InsBean> prepareInsertData(DCP_InterSettleDataGenerateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_InterSettleDataGenerateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_InterSettleDataGenerateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_InterSettleDataGenerateReq req) throws Exception {

        if (!BillType.BillType10006.getType().equals(req.getRequest().getBillType())) {
            if (Check.isEmpty(req.getRequest().getSupplyOrgNo())) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "供货组织不可为空");
            }
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_InterSettleDataGenerateReq> getRequestType() {
        return new TypeToken<DCP_InterSettleDataGenerateReq>() {
        };
    }

    @Override
    protected DCP_InterSettleDataGenerateRes getResponseType() {
        return new DCP_InterSettleDataGenerateRes();
    }


    private int getDirection(DCP_InterSettleDataGenerateReq req) throws Exception {

        int direction = 1;
        String billType = req.getRequest().getBillType();
        if (billType.contains("-")) {
            direction = -1;
        }

        return direction;

    }

    @Override
    protected String getQuerySql(DCP_InterSettleDataGenerateReq req) throws Exception {
        String billType = req.getRequest().getBillType();

        if (billType.equals(BillType.BillType10000.getType())) {
            return getQueryType10000Sql(req);
        } else if (billType.equals(BillType.BillType10002.getType())) {
            return getQueryType10002Sql(req);
        } else if (billType.equals(BillType.BillType10003.getType())) {
            return getQueryType10003Sql(req);
        } else if (billType.equals(BillType.BillType10004.getType())) {
            return getQueryType10004Sql(req);
        } else if (billType.equals(BillType.BillType10005.getType())) {
            return getQueryType10005Sql(req);
        } else if (billType.equals(BillType.BillType10006.getType())) {
            return getQueryType10006Sql(req);
        } else if (billType.equals(BillType.BillType11000.getType())) {
            return getQueryType11000Sql(req);
        } else if (billType.equals(BillType.BillType11001.getType())) {
            return getQueryType11001Sql(req);
        } else if (billType.equals(BillType.BillType11002.getType())) {
            return getQueryType11002Sql(req);
        } else if (billType.equals(BillType.BillType12000.getType())) {
            return getQueryType12000Sql(req);
        } else if (billType.equals(BillType.BillType12001.getType())) {
            return getQueryType12001Sql(req);
        } else if (billType.equals(BillType.BillType12002.getType())) {
            return getQueryType12002Sql(req);
        } else if (billType.equals(BillType.BillType_11002.getType())) {
            return getQueryType_11002Sql(req);
        } else {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "传入类型未对接！请联系开发人员！");
        }

    }

    private String getQueryType_11002Sql(DCP_InterSettleDataGenerateReq req) {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT b.PLUNO,b.PUNIT,b.PQTY,b.ITEM,b.FEATURENO,a.BDATE ")
                .append(" FROM DCP_SSTOCKOUT a ")
                .append(" INNER JOIN DCP_SSTOCKOUT_BATCH b on a.eid=b.eid and a.SSTOCKOUTNO = b.SSTOCKOUTNO and a.ORGANIZATIONNO=b.ORGANIZATIONNO ")
        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            sb.append(" AND a.SSTOCKOUTNO='").append(req.getRequest().getBillNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getOrganizationNo())) {
            sb.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getOrganizationNo()).append("'");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getDetail())) {
            sb.append(" AND (1=2 ");
            for (DCP_InterSettleDataGenerateReq.Detail oneDetail : req.getRequest().getDetail()) {
                sb.append(" or b.ITEM=").append(oneDetail.getItem());
            }
            sb.append(")");
        }

        return sb.toString();

    }


    private String getQuerySourceTicketSql(DCP_InterSettleDataGenerateReq req) throws Exception {
        return "";
    }


    private String getQueryInterSettSettingVersion(DCP_InterSettleDataGenerateReq req, String demandObject, String supplyObject, String processNo, String version) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT b.* FROM DCP_INTERSETTSETTING_V a ")
                .append(" INNER JOIN DCP_INTERSETTSETDETAIL_V b on a.eid=b.eid and a.PROCESSNO=b.PROCESSNO ");

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");

        //1.采购 2.销售 3.调拨
        //判断业务类型
        BillType billType = BillType.getBillType(req.getRequest().getBillType());
        if (null != billType) {
            sb.append(" AND a.BTYPE='").append(billType.getDataType()).append("'");
        }

        //组织
        if (StringUtils.isNotEmpty(req.getRequest().getOrganizationNo())) {
            sb.append(" AND a.DEMANDOBJECT='").append(demandObject).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getSupplyOrgNo())) {
            sb.append(" AND a.SUPPLYOBJECT='").append(supplyObject).append("'");
        }

        sb.append(" ORDER BY b.PROCESSNO,b.ITEM ");//排序

        return sb.toString();
    }

    private String getQueryInterSettSetting(DCP_InterSettleDataGenerateReq req, String demandObject, String supplyObject) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT b.* FROM DCP_INTERSETTSETTING a ")
                .append(" INNER JOIN DCP_INTERSETTSETDETAIL b on a.eid=b.eid and a.PROCESSNO=b.PROCESSNO ");

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");

        //1.采购 2.销售 3.调拨
        //判断业务类型
        BillType billType = BillType.getBillType(req.getRequest().getBillType());
        if (null != billType) {
            sb.append(" AND a.BTYPE='").append(billType.getDataType()).append("'");
        }

        //组织
        if (StringUtils.isNotEmpty(demandObject)) {
            sb.append(" AND a.DEMANDOBJECT='").append(demandObject).append("'");
        }

        if (StringUtils.isNotEmpty(supplyObject)) {
            sb.append(" AND a.SUPPLYOBJECT='").append(supplyObject).append("'");
        }
        sb.append(" ORDER BY b.PROCESSNO,b.ITEM ");//排序

        return sb.toString();
    }

    private String getQueryType11002Sql(DCP_InterSettleDataGenerateReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT b.PLUNO,b.PUNIT,b.PQTY,b.ITEM,b.FEATURENO,a.BDATE ")
                .append(" FROM DCP_SSTOCKIN a ")
                .append(" INNER JOIN DCP_SSTOCKIN_DETAIL b on a.eid=b.eid and a.SSTOCKINNO = b.SSTOCKINNO  and a.ORGANIZATIONNO=b.ORGANIZATIONNO ")

        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            sb.append(" AND a.SSTOCKINNO='").append(req.getRequest().getBillNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getOrganizationNo())) {
            sb.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getOrganizationNo()).append("'");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getDetail())) {
            sb.append(" AND (1=2 ");
            for (DCP_InterSettleDataGenerateReq.Detail oneDetail : req.getRequest().getDetail()) {
                sb.append(" or b.ITEM=").append(oneDetail.getItem());
            }
            sb.append(")");
        }

        return sb.toString();
    }

    private String getQueryType11001Sql(DCP_InterSettleDataGenerateReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT b.PLUNO,b.PUNIT,b.PQTY,b.ITEM,b.FEATURENO,a.BDATE ")
                .append(" FROM DCP_RECEIVING a ")
                .append(" INNER JOIN DCP_RECEIVING_DETAIL b on a.eid=b.eid and a.PURORDERNO = b.PURORDERNO  and a.ORGANIZATIONNO=b.ORGANIZATIONNO ")

        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            sb.append(" AND a.RECEIVINGNO='").append(req.getRequest().getBillNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getOrganizationNo())) {
            sb.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getOrganizationNo()).append("'");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getDetail())) {
            sb.append(" AND (1=2 ");
            for (DCP_InterSettleDataGenerateReq.Detail oneDetail : req.getRequest().getDetail()) {
                sb.append(" or b.ITEM=").append(oneDetail.getItem());
            }
            sb.append(")");
        }

        return sb.toString();
    }

    private String getQueryType12002Sql(DCP_InterSettleDataGenerateReq req) {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT b.PLUNO,b.PUNIT,b.PQTY,b.ITEM,b.FEATURENO,a.BDATE ")
                .append(" FROM DCP_SSTOCKOUT a ")
                .append(" INNER JOIN DCP_SSTOCKOUT_BATCH b on a.eid=b.eid and a.SSTOCKOUTNO = b.SSTOCKOUTNO and a.ORGANIZATIONNO=b.ORGANIZATIONNO ")
        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            sb.append(" AND a.SSTOCKOUTNO='").append(req.getRequest().getBillNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getOrganizationNo())) {
            sb.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getOrganizationNo()).append("'");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getDetail())) {
            sb.append(" AND (1=2 ");
            for (DCP_InterSettleDataGenerateReq.Detail oneDetail : req.getRequest().getDetail()) {
                sb.append(" or b.ITEM=").append(oneDetail.getItem());
            }
            sb.append(")");
        }

        return sb.toString();

    }


    private String getQueryType12001Sql(DCP_InterSettleDataGenerateReq req) {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT b.PLUNO,b.PUNIT,b.PQTY,b.ITEM,b.FEATURENO,a.BDATE ")
                .append(" FROM DCP_STOCKOUTNOTICE a ")
                .append(" INNER JOIN DCP_STOCKOUTNOTICE_DETAIL b on a.eid=b.eid and a.BILLNO = b.BILLNO and a.ORGANIZATIONNO=b.ORGANIZATIONNO ")
        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            sb.append(" AND a.BILLNO='").append(req.getRequest().getBillNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getOrganizationNo())) {
            sb.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getOrganizationNo()).append("'");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getDetail())) {
            sb.append(" AND (1=2 ");
            for (DCP_InterSettleDataGenerateReq.Detail oneDetail : req.getRequest().getDetail()) {
                sb.append(" or b.ITEM=").append(oneDetail.getItem());
            }
            sb.append(")");
        }

        return sb.toString();

    }

    private String getQueryType12000Sql(DCP_InterSettleDataGenerateReq req) {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT b.PLUNO,b.UNIT PUNIT,b.QTY PQTY,b.ITEM,b.FEATURENO,a.BDATE ")
                .append(" FROM DCP_CUSTOMERPORDER a ")
                .append(" INNER JOIN DCP_CUSTOMERPORDER_DETAIL b on a.eid=b.eid and a.PORDERNO = b.PORDERNO and a.SHOPNO=b.SHOPNO  ")
        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            sb.append(" AND a.PORDERNO='").append(req.getRequest().getBillNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getOrganizationNo())) {
            sb.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getOrganizationNo()).append("'");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getDetail())) {
            sb.append(" AND (1=2 ");
            for (DCP_InterSettleDataGenerateReq.Detail oneDetail : req.getRequest().getDetail()) {
                sb.append(" or b.ITEM=").append(oneDetail.getItem());
            }
            sb.append(")");
        }

        return sb.toString();

    }


    private String getQueryType11000Sql(DCP_InterSettleDataGenerateReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT b.PLUNO,b.PURUNIT PUNIT,b.PURQTY PQTY,b.ITEM,b.FEATURENO,a.BDATE ")
                .append(" FROM DCP_PURORDER a ")
                .append(" INNER JOIN DCP_PURORDER_DETAIL b on a.eid=b.eid and a.PURORDERNO = b.PURORDERNO  and a.ORGANIZATIONNO=b.ORGANIZATIONNO ")
        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            sb.append(" AND a.PURORDERNO='").append(req.getRequest().getBillNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getOrganizationNo())) {
            sb.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getOrganizationNo()).append("'");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getDetail())) {
            sb.append(" AND (1=2 ");
            for (DCP_InterSettleDataGenerateReq.Detail oneDetail : req.getRequest().getDetail()) {
                sb.append(" or b.ITEM=").append(oneDetail.getItem());
            }
            sb.append(")");
        }

        return sb.toString();
    }

    private String getQueryType10005Sql(DCP_InterSettleDataGenerateReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT b.PLUNO,b.PUNIT,b.PQTY,b.ITEM,b.FEATURENO,a.BDATE ")
                .append(" FROM DCP_STOCKOUT a ")
                .append(" INNER JOIN DCP_STOCKOUT_DETAIL b on a.eid=b.eid and a.STOCKOUTNO = b.STOCKOUTNO  and a.ORGANIZATIONNO=b.ORGANIZATIONNO and a.SHOPID=b.SHOPID ")

        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            sb.append(" AND a.STOCKOUTNO='").append(req.getRequest().getBillNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getOrganizationNo())) {
            sb.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getOrganizationNo()).append("'");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getDetail())) {
            sb.append(" AND (1=2 ");
            for (DCP_InterSettleDataGenerateReq.Detail oneDetail : req.getRequest().getDetail()) {
                sb.append(" or b.ITEM=").append(oneDetail.getItem());
            }
            sb.append(")");
        }

        return sb.toString();
    }

    private String getQueryType10006SourceSql(DCP_InterSettleDataGenerateReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT c.ORGANIZATIONNO,c.SHOPID,b.ITEM,b.OITEM,c.PRICE,b.PQTY ")
                .append(" FROM DCP_STOCKIN a ")
                .append(" INNER JOIN DCP_STOCKIN_DETAIL b on a.eid=b.eid and a.STOCKINNO = b.STOCKINNO  and a.ORGANIZATIONNO=b.ORGANIZATIONNO and a.SHOPID=b.SHOPID")
                .append(" LEFT JOIN DCP_RECEIVING_DETAIL c on c.eid = b.eid and a.OFNO = c.RECEIVINGNO and b.OITEM = c.ITEM ")
                .append(" LEFT JOIN DCP_RECEIVING d on c.eid=d.eid and c.RECEIVINGNO=d.RECEIVINGNO ")

        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            sb.append(" AND a.STOCKINNO='").append(req.getRequest().getBillNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getOrganizationNo())) {
            sb.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getOrganizationNo()).append("'");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getDetail())) {
            sb.append(" AND (1=2 ");
            for (DCP_InterSettleDataGenerateReq.Detail oneDetail : req.getRequest().getDetail()) {
                sb.append(" or b.ITEM=").append(oneDetail.getItem());
            }
            sb.append(")");
        }

        return sb.toString();
    }

    //调拨入库
    private String getQueryType10006Sql(DCP_InterSettleDataGenerateReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT b.PLUNO,b.PUNIT,b.PQTY,b.ITEM,b.FEATURENO,a.BDATE ")
                .append(" FROM DCP_STOCKIN a ")
                .append(" INNER JOIN DCP_STOCKIN_DETAIL b on a.eid=b.eid and a.STOCKINNO = b.STOCKINNO  and a.ORGANIZATIONNO=b.ORGANIZATIONNO and a.SHOPID=b.SHOPID")

        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            sb.append(" AND a.STOCKINNO='").append(req.getRequest().getBillNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getOrganizationNo())) {
            sb.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getOrganizationNo()).append("'");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getDetail())) {
            sb.append(" AND (1=2 ");
            for (DCP_InterSettleDataGenerateReq.Detail oneDetail : req.getRequest().getDetail()) {
                sb.append(" or b.ITEM=").append(oneDetail.getItem());
            }
            sb.append(")");
        }

        return sb.toString();
    }

    //配货入库
    private String getQueryType10004Sql(DCP_InterSettleDataGenerateReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT b.PLUNO,b.PUNIT,b.PQTY,b.ITEM,b.FEATURENO,a.BDATE ")
                .append(" FROM DCP_STOCKIN a ")
                .append(" INNER JOIN DCP_STOCKIN_DETAIL b on a.eid=b.eid and a.STOCKINNO = b.STOCKINNO  and a.ORGANIZATIONNO=b.ORGANIZATIONNO and a.SHOPID=b.SHOPID ")

        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            sb.append(" AND a.STOCKINNO='").append(req.getRequest().getBillNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getOrganizationNo())) {
            sb.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getOrganizationNo()).append("'");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getDetail())) {
            sb.append(" AND (1=2 ");
            for (DCP_InterSettleDataGenerateReq.Detail oneDetail : req.getRequest().getDetail()) {
                sb.append(" or b.ITEM=").append(oneDetail.getItem());
            }
            sb.append(")");
        }

        return sb.toString();
    }


    //配货出库
    private String getQueryType10003Sql(DCP_InterSettleDataGenerateReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT b.PLUNO,b.PUNIT,b.PQTY,b.ITEM,b.FEATURENO,a.BDATE ")
                .append(" FROM DCP_STOCKOUT a ")
                .append(" INNER JOIN DCP_STOCKOUT_DETAIL b on a.eid=b.eid and a.STOCKOUTNO = b.STOCKOUTNO  and a.ORGANIZATIONNO=b.ORGANIZATIONNO and a.SHOPID=b.SHOPID ")

        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            sb.append(" AND a.STOCKOUTNO='").append(req.getRequest().getBillNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getOrganizationNo())) {
            sb.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getOrganizationNo()).append("'");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getDetail())) {
            sb.append(" AND (1=2 ");
            for (DCP_InterSettleDataGenerateReq.Detail oneDetail : req.getRequest().getDetail()) {
                sb.append(" or b.ITEM=").append(oneDetail.getItem());
            }
            sb.append(")");
        }

        return sb.toString();
    }

    //配货通知
    private String getQueryType10002Sql(DCP_InterSettleDataGenerateReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT b.PLUNO,b.PUNIT,b.PQTY,b.ITEM,b.FEATURENO,a.BDATE ")
                .append(" FROM DCP_STOCKOUTNOTICE a ")
                .append(" INNER JOIN DCP_STOCKOUTNOTICE_DETAIL b on a.eid=b.eid and a.BILLNO = b.BILLNO  and a.ORGANIZATIONNO=b.ORGANIZATIONNO ")

        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            sb.append(" AND a.BILLNO='").append(req.getRequest().getBillNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getOrganizationNo())) {
            sb.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getOrganizationNo()).append("'");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getDetail())) {
            sb.append(" AND (1=2 ");
            for (DCP_InterSettleDataGenerateReq.Detail oneDetail : req.getRequest().getDetail()) {
                sb.append(" or b.ITEM=").append(oneDetail.getItem());
            }
            sb.append(")");
        }

        return sb.toString();
    }

    //要货申请
    private String getQueryType10000Sql(DCP_InterSettleDataGenerateReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT b.PLUNO,b.PUNIT,b.PQTY,b.ITEM,b.FEATURENO,a.BDATE ")
                .append(" FROM DCP_PORDER a ")
                .append(" INNER JOIN DCP_PORDER_DETAIL b on a.eid=b.eid and a.PORDERNO = b.PORDERNO and a.SHOPID=b.SHOPID and a.ORGANIZATIONNO=b.ORGANIZATIONNO ")
        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            sb.append(" AND a.PORDERNO='").append(req.getRequest().getBillNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getOrganizationNo())) {
            sb.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getOrganizationNo()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getSupplyOrgNo())) {
            sb.append(" AND a.RECEIPT_ORG='").append(req.getRequest().getSupplyOrgNo()).append("'");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getDetail())) {
            sb.append(" AND (1=2 ");
            for (DCP_InterSettleDataGenerateReq.Detail oneDetail : req.getRequest().getDetail()) {
                sb.append(" or b.ITEM=").append(oneDetail.getItem());
            }
            sb.append(")");
        }

        return sb.toString();
    }

    //根据内部结算配置详情获取对应组织的零售价和供货价
    private List<OrgPluPrice> getOrgPluPriceList(DCP_InterSettleDataGenerateReq req, List<OrgantionRelationship> ortList) throws Exception {
        List<OrgPluPrice> orgPluPriceList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(ortList)) {
            MyCommon mc = new MyCommon();
            for (OrgantionRelationship ort : ortList) {
                String supplyOrg = ort.getSupplyCorp();
                String supplyCorp = ort.getSupplyCorp();
                String receiveOrg = ort.getReceiptOrgNo();
                String receiveCorp = ort.getReceiptCorp();


                //获取价格信息
                List<Map<String, Object>> pricePlus = new ArrayList<>();
                for (DCP_InterSettleDataGenerateReq.Detail oneDetail : req.getRequest().getDetail()) {
                    if (StringUtils.isEmpty(oneDetail.getSupplyPrice())) {
                        Map<String, Object> onePlu = new HashMap<>();
                        onePlu.put("PLUNO", oneDetail.getPluNo());
                        onePlu.put("PUNIT", oneDetail.getPUnit());
                        onePlu.put("SUPPLIERID", supplyCorp);
                        pricePlus.add(onePlu);
                    }
                }
                //获取销售价格
                List<Map<String, Object>> getPrice = mc.getSalePrice_distriPrice(dao, req.geteId(), receiveOrg,
                        receiveOrg,
                        pricePlus, receiveOrg);

                for (Map<String, Object> oneDetail : getPrice) {
                    OrgPluPrice orgPluPrice = new OrgPluPrice();

                    orgPluPrice.setSupplyOrg(supplyOrg);
                    orgPluPrice.setDemandOrg(receiveOrg);
                    orgPluPrice.setPUnit(oneDetail.get("PUNIT").toString());
                    orgPluPrice.setPluNo(oneDetail.get("PLUNO").toString());
                    orgPluPrice.setSalePrice(oneDetail.get("PRICE").toString());
                    orgPluPrice.setDistriPrice(oneDetail.get("DISTRIPRICE").toString());

                    if (!orgPluPriceList.contains(orgPluPrice)) {
                        orgPluPriceList.add(orgPluPrice);
                    }

                }

                //获取销售价格
                getPrice = mc.getSalePrice_distriPrice(dao, req.geteId(), receiveCorp,
                        receiveCorp,
                        pricePlus, receiveCorp);

                for (Map<String, Object> oneDetail : getPrice) {
                    OrgPluPrice orgPluPrice = new OrgPluPrice();

                    orgPluPrice.setSupplyOrg(supplyOrg);
                    orgPluPrice.setDemandOrg(receiveCorp);
                    orgPluPrice.setPluNo(oneDetail.get("PLUNO").toString());
                    orgPluPrice.setSalePrice(oneDetail.get("PRICE").toString());
                    orgPluPrice.setPUnit(oneDetail.get("PUNIT").toString());
                    orgPluPrice.setDistriPrice(oneDetail.get("DISTRIPRICE").toString());

                    if (!orgPluPriceList.contains(orgPluPrice)) {
                        orgPluPriceList.add(orgPluPrice);
                    }

                }

                if (CollectionUtils.isNotEmpty(ort.getInterSettSetting())) {

                    Map<String, Object> interSet = ort.getInterSettSetting().stream().filter(x -> StringUtils.equals(supplyCorp, x.get("SUPPLYOBJECT").toString())
                            || StringUtils.equals(supplyOrg, x.get("SUPPLYOBJECT").toString())
                    ).collect(Collectors.toList()).get(0);

                    String demandOrg = interSet.get("DEMANDOBJECT1").toString();
                    //获取销售价格
                    getPrice = mc.getSalePrice_distriPrice(dao, req.geteId(), demandOrg,
                            demandOrg,
                            pricePlus, demandOrg);

                    for (Map<String, Object> oneDetail : getPrice) {
                        OrgPluPrice orgPluPrice = new OrgPluPrice();

                        orgPluPrice.setSupplyOrg(supplyOrg);
                        orgPluPrice.setDemandOrg(demandOrg);
                        orgPluPrice.setPluNo(oneDetail.get("PLUNO").toString());
                        orgPluPrice.setSalePrice(oneDetail.get("PRICE").toString());
                        orgPluPrice.setPUnit(oneDetail.get("PUNIT").toString());
                        orgPluPrice.setDistriPrice(oneDetail.get("DISTRIPRICE").toString());

                        if (!orgPluPriceList.contains(orgPluPrice)) {
                            orgPluPriceList.add(orgPluPrice);
                        }
                    }

                }


            }

        }


        return orgPluPriceList;
    }

    @Data
    @EqualsAndHashCode(of = {"pluNo", "item", "receiveOrgNo"})
    private class ResponsePrice {
        private String pluNo;
        private String item;
        private String receiveOrgNo;
        private String supplyOrg;

        private String supplyPrice;
        private String receiptPrice;
    }

    @Data
    @EqualsAndHashCode(of = {"supplyOrg", "demandOrg", "pluNo"})
    private class OrgPluPrice {
        private String supplyOrg;
        private String demandOrg;
        private String pluNo;
        private String pUnit;

        private String salePrice;  //零售价
        private String distriPrice;  //供货价
    }


}
