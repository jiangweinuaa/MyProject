package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CustomerPOrderUpdateReq;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataGenerateReq;
import com.dsc.spos.json.cust.res.DCP_CustomerPOrderUpdateRes;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataGenerateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.tax.TaxAmount;
import com.dsc.spos.utils.tax.TaxAmountCalculation;
import com.dsc.spos.utils.tax.TaxUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DCP_CustomerPOrderUpdate extends SPosAdvanceService<DCP_CustomerPOrderUpdateReq, DCP_CustomerPOrderUpdateRes> {
    @Override
    protected boolean isVerifyFail(DCP_CustomerPOrderUpdateReq req) throws Exception {

        return false;
    }

    @Override
    protected TypeToken<DCP_CustomerPOrderUpdateReq> getRequestType() {
        return new TypeToken<DCP_CustomerPOrderUpdateReq>() {
        };
    }

    @Override
    protected DCP_CustomerPOrderUpdateRes getResponseType() {
        return new DCP_CustomerPOrderUpdateRes();
    }

    @Override
    protected void processDUID(DCP_CustomerPOrderUpdateReq req, DCP_CustomerPOrderUpdateRes res) throws Exception {
        String lastModiTime = DateFormatUtils.getNowDateTime();

        try {
            ColumnDataValue condition = new ColumnDataValue();

            double totAmt = 0;
            double totPQty = 0;
            double totCQty = 0;
            double totTaxAmt = 0;
            double totPreTaxAmt = 0;
            double totDiscAmt = 0;

            condition.append("EID", DataValues.newString(req.geteId()));
            condition.append("SHOPNO", DataValues.newString(req.getShopId()));
            condition.append("PORDERNO", DataValues.newString(req.getRequest().getPOrderNo()));

            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_CUSTOMERPORDER_DETAIL", condition)));
            int amountDigit = 2;
            if (Check.NotNull(req.getRequest().getCurrency())) {
                String currencySql = "select * from DCP_CURRENCY where eid='" + req.geteId() + "' and" +
                        " CURRENCY='" + req.getRequest().getCurrency() + "' ";
                List<Map<String, Object>> currencies = this.doQueryData(currencySql, null);
                if (currencies.size() <= 0) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "币种:" + req.getRequest().getCurrency() + "不存在!");
                }
                amountDigit = Integer.parseInt(currencies.get(0).get("AMOUNTDIGIT").toString());
            }

            if (Check.Null(req.getRequest().getPayer())) {
                String bizSql = "select * from DCP_BIZPARTNER where eid='" + req.geteId() + "' and bizpartnerno='" + req.getRequest().getCustomerNo() + "'  ";
                List<Map<String, Object>> bizList = this.doQueryData(bizSql, null);
                if (!bizList.isEmpty()) {
                    req.getRequest().setPayer(bizList.get(0).get("PAYER").toString());
                }
            }

            String orgNo = req.getShopId();
            if (StringUtils.isNotEmpty(req.getRequest().getShopNo())) {
                orgNo = req.getRequest().getShopNo();
            }
            String corp = PosPub.getCorpByOrgNo(orgNo).get(orgNo);
            String taxpayer_type = "";

            String querySql = " SELECT TAXPAYER_TYPE,OUTPUTTAX FROM DCP_ORG WHERE EID='" + req.geteId() + "' AND ORGANIZATIONNO='" + corp + "'";
            List<Map<String, Object>> getData = dao.executeQuerySQL(querySql, null);
            if (CollectionUtils.isEmpty(getData)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "请先设置法人:" + corp + "的纳税人性质！");
            }
            taxpayer_type = getData.get(0).get("TAXPAYER_TYPE").toString();

            String taxCode = "";
            TaxUtils taxUtils = new TaxUtils();
            String taxCalType = "1";
            String inclTax = "N";
            String taxRate = "0";
            TaxUtils.Tax tax = null;

            int i = 1;
            for (DCP_CustomerPOrderUpdateReq.Datas oneData : req.getRequest().getDatas()) {

                String preAmt = "";
                String taxAmt = "";

                if (StringUtils.isEmpty(taxCode) || !StringUtils.equals(taxCode, oneData.getTaxCode())) {
                    taxCode = oneData.getTaxCode();
                    if (StringUtils.isEmpty(taxCode)) {
                        tax = taxUtils.getTax(req.geteId(), corp, oneData.getPluNo());
                    } else {
                        tax = taxUtils.getTax(req.geteId(), taxCode);
                    }
                }
                if (tax != null && StringUtils.isEmpty(tax.getTaxCode())) {
//                （小规模纳税人）提示：组织所属法人未设定销项税别；
//                （一般纳税人）提示：商品xx未设定销项税别
                    if ("1".equals(taxpayer_type)) {  //一般纳税人
                        throw new DispatchService.SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品" + oneData.getPluNo() + "未设定销项税别");
                    } else {
                        throw new DispatchService.SPosCodeException(CODE_EXCEPTION_TYPE.E400, "组织所属法人未设定销项税别");
                    }
                }

                if (tax != null) {
                    taxCalType = tax.getTaxCalType();
                    inclTax = tax.getInclTax();
                    taxRate = String.valueOf(tax.getTaxRate());
                }

                TaxAmount taxAmount = TaxAmountCalculation.calculateAmount(
                        "Y".equals(inclTax),
                        Double.parseDouble(oneData.getAmt()),
                        Double.parseDouble(taxRate),
                        taxCalType,
                        amountDigit
                );
                preAmt = oneData.getPreTaxAmt();
                taxAmt = oneData.getTaxAmt();
                if (StringUtils.isEmpty(preAmt)) {
                    preAmt = String.valueOf(taxAmount.getPreAmount());
                }

                if (StringUtils.isEmpty(taxAmt)) {
                    taxAmt = String.valueOf(taxAmount.getTaxAmount());
                }

                ColumnDataValue dcp_customerporder_detail = new ColumnDataValue();

                totAmt += Double.parseDouble(StringUtils.toString(oneData.getAmt(), "0"));
                totPQty += Double.parseDouble(StringUtils.toString(oneData.getQty(), "0"));
                totTaxAmt += Double.parseDouble(StringUtils.toString(taxAmt, "0"));
                totPreTaxAmt += Double.parseDouble(StringUtils.toString(preAmt, "0"));
                totDiscAmt += Double.parseDouble(StringUtils.toString(oneData.getDiscAmt(), "0"));
                totCQty++;

                dcp_customerporder_detail.append("EID", DataValues.newString(req.geteId()));
                dcp_customerporder_detail.append("SHOPNO", DataValues.newString(req.getShopId()));
                dcp_customerporder_detail.append("PORDERNO", DataValues.newString(req.getRequest().getPOrderNo()));
                dcp_customerporder_detail.append("ITEM", DataValues.newString(oneData.getItem()));
                dcp_customerporder_detail.append("PLUNO", DataValues.newString(oneData.getPluNo()));
                dcp_customerporder_detail.append("FEATURENO", DataValues.newString(oneData.getFeatureNo()));
                dcp_customerporder_detail.append("QTY", DataValues.newDecimal(oneData.getQty()));
                dcp_customerporder_detail.append("PRICE", DataValues.newDecimal(oneData.getPrice()));
                dcp_customerporder_detail.append("OPRICE", DataValues.newDecimal(oneData.getOPrice()));
                dcp_customerporder_detail.append("DISCRATE", DataValues.newDecimal(oneData.getDiscRate()));
                dcp_customerporder_detail.append("AMT", DataValues.newDecimal(oneData.getAmt()));
                dcp_customerporder_detail.append("UNIT", DataValues.newString(oneData.getUnit()));
                dcp_customerporder_detail.append("ISGIFT", DataValues.newString(oneData.getIsGift()));
                dcp_customerporder_detail.append("PLUBARCODE", DataValues.newString(oneData.getPluBarcode()));
                dcp_customerporder_detail.append("CATEGORY", DataValues.newString(oneData.getCategory()));
                dcp_customerporder_detail.append("TAXCODE", DataValues.newString(oneData.getTaxCode()));
                dcp_customerporder_detail.append("TAXRATE", DataValues.newDecimal(oneData.getTaxRate()));
                dcp_customerporder_detail.append("INCLTAX", DataValues.newString(oneData.getInclTax()));
                dcp_customerporder_detail.append("TAXCALTYPE", DataValues.newString(oneData.getTaxCalType()));

                dcp_customerporder_detail.append("PRETAXAMT", DataValues.newDecimal(preAmt));
                dcp_customerporder_detail.append("TAXAMT", DataValues.newDecimal(taxAmt));

                dcp_customerporder_detail.append("DISCAMT", DataValues.newDecimal(oneData.getDiscAmt()));
                dcp_customerporder_detail.append("RETAILPRICE", DataValues.newDecimal(oneData.getRetailPrice()));
                dcp_customerporder_detail.append("RETAILAMT", DataValues.newDecimal(oneData.getRetailAmt()));
                dcp_customerporder_detail.append("DELIVERORGNO", DataValues.newString(oneData.getDeliverOrgNo()));
                dcp_customerporder_detail.append("DELIVERWAREHOUSE", DataValues.newString(oneData.getDeliverWarehouse()));
                dcp_customerporder_detail.append("STATUS", DataValues.newString(0));
                dcp_customerporder_detail.append("BASEUNIT", DataValues.newString(oneData.getBaseUnit()));
                dcp_customerporder_detail.append("BASEQTY", DataValues.newDecimal(oneData.getBaseQty()));
                dcp_customerporder_detail.append("UNITRATIO", DataValues.newDecimal(oneData.getUnitRatio()));
//                dcp_customerporder_detail.append("STOCKOUTNOQTY", DataValues.newDecimal(oneData.getUnitRatio()));
//                dcp_customerporder_detail.append("STOCKOUTQTY", DataValues.newDecimal(oneData.getUnitRatio()));
//                dcp_customerporder_detail.append("RETURNQTY", DataValues.newDecimal(oneData.getUnitRatio()));
                dcp_customerporder_detail.append("DELIVERYORGNO", DataValues.newString(oneData.getDeliverOrgNo()));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_CUSTOMERPORDER_DETAIL", dcp_customerporder_detail)));

            }
            ColumnDataValue dcp_customerporder = new ColumnDataValue();

            dcp_customerporder.append("CUSTOMERNO", DataValues.newString(req.getRequest().getCustomerNo()));
            dcp_customerporder.append("CUSTOMERNAME", DataValues.newString(req.getRequest().getCustomerName()));
            dcp_customerporder.append("TELEPHONE", DataValues.newString(req.getRequest().getTelephone()));
            dcp_customerporder.append("ADDRESS", DataValues.newString(req.getRequest().getAddress()));
            dcp_customerporder.append("TEMPLATENO", DataValues.newString(req.getRequest().getTemplateNo()));
            dcp_customerporder.append("RDATE", DataValues.newString(DateFormatUtils.getPlainDate(req.getRequest().getRDate())));
            dcp_customerporder.append("BDATE", DataValues.newString(DateFormatUtils.getPlainDate(req.getRequest().getBDate())));
            dcp_customerporder.append("MEMO", DataValues.newString(req.getRequest().getMemo()));
            dcp_customerporder.append("STATUS", DataValues.newString(0));
            dcp_customerporder.append("SALESMANNO", DataValues.newString(req.getRequest().getSalesManNo()));
            dcp_customerporder.append("SALESMANNAME", DataValues.newString(req.getRequest().getSalesManName()));

            dcp_customerporder.append("PROCESS_STATUS", DataValues.newString("N"));
            dcp_customerporder.append("SALEDEPARTID", DataValues.newString(req.getRequest().getSalesDepartId()));
            dcp_customerporder.append("CONTACT", DataValues.newString(req.getRequest().getContact()));
            dcp_customerporder.append("DELIVERORGNO", DataValues.newString(req.getRequest().getDeliverOrgNo()));
            dcp_customerporder.append("DELIVERWAREHOUSE", DataValues.newString(req.getRequest().getDeliverWarehouse()));
            dcp_customerporder.append("PAYTYPE", DataValues.newString(req.getRequest().getPayType()));
            dcp_customerporder.append("PAYORGNO", DataValues.newString(req.getRequest().getPayOrgNo()));
            dcp_customerporder.append("BILLDATENO", DataValues.newString(req.getRequest().getBillDateNo()));
            dcp_customerporder.append("PAYDATENO", DataValues.newString(req.getRequest().getPayDateNo()));
            dcp_customerporder.append("INVOICECODE", DataValues.newString(req.getRequest().getInvoiceCode()));
            dcp_customerporder.append("CURRENCY", DataValues.newString(req.getRequest().getCurrency()));
            dcp_customerporder.append("EMPLOYEEID", DataValues.newString(req.getRequest().getEmployeeId()));
            dcp_customerporder.append("DEPARTID", DataValues.newString(req.getRequest().getDepartId()));

            dcp_customerporder.append("DISCRATE", DataValues.newDecimal(req.getRequest().getDiscRate()));
            dcp_customerporder.append("PAYER", DataValues.newDecimal(req.getRequest().getPayer()));

            dcp_customerporder.append("TOTDISCAMT", DataValues.newDecimal(totDiscAmt));
            dcp_customerporder.append("TOTPRETAXAMT", DataValues.newDecimal(totPreTaxAmt));
            dcp_customerporder.append("TOTTAXAMT", DataValues.newDecimal(totTaxAmt));
            dcp_customerporder.append("TOT_AMT", DataValues.newDecimal(totAmt));
            dcp_customerporder.append("TOT_PQTY", DataValues.newDecimal(totPQty));
            dcp_customerporder.append("TOT_CQTY", DataValues.newDecimal(totCQty));

            dcp_customerporder.append("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_customerporder.append("LASTMODIOPNAME", DataValues.newString(req.getEmployeeName()));
            dcp_customerporder.append("LASTMODITIME", DataValues.newDate(lastModiTime));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_CUSTOMERPORDER", condition, dcp_customerporder))); // 新增單頭

        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

        //内部交易
        Map<String, String> corpData = PosPub.getCorpByOrgNo(req.getOrganizationNO(), req.getRequest().getDeliverOrgNo());
        if (!StringUtils.equals(corpData.get(req.getOrganizationNO()), corpData.get(req.getRequest().getDeliverOrgNo()))) {

            DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
            inReq.setServiceId("DCP_InterSettleDataGenerate");
            inReq.setToken(req.getToken());

            DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
            request1.setOrganizationNo(req.getOrganizationNO());
            request1.setBillNo(req.getRequest().getPOrderNo());
            request1.setSupplyOrgNo(req.getRequest().getDeliverOrgNo());
            request1.setReturnSupplyPrice("N");
            request1.setBillType("12000");
            request1.setDetail(new ArrayList<>());
            //int item=0;
            for (DCP_CustomerPOrderUpdateReq.Datas par : req.getRequest().getDatas()) {
                //item++;
                DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                detail.setReceiveOrgNo(req.getOrganizationNO());
                detail.setSourceBillNo("");
                detail.setSourceItem("");
                detail.setItem(String.valueOf(par.getItem()));
                detail.setPluNo(par.getPluNo());
                detail.setFeatureNo(par.getFeatureNo());
                detail.setPUnit(par.getUnit());
                detail.setPQty(String.valueOf(par.getQty()));
                detail.setReceivePrice(String.valueOf(par.getRetailPrice()));//todo
                detail.setReceiveAmt(String.valueOf(par.getRetailAmt()));//todo
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

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CustomerPOrderUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CustomerPOrderUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CustomerPOrderUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected String getQuerySql(DCP_CustomerPOrderUpdateReq req) throws Exception {
        return null;
    }

//    private List<Map<String, Object>> getTaxCategory(String eId, String taxCode) throws Exception {
//        String getTaxSql = " SELECT TAXCALTYPE,TAXRATE/100 TAXRATE,INCLTAX FROM DCP_TAXCATEGORY WHERE EID='" + eId + "' and TAXCODE = '" + taxCode + "'";
//        List<Map<String, Object>> getTax = doQueryData(getTaxSql, null);
//        return getTax;
//    }

}
