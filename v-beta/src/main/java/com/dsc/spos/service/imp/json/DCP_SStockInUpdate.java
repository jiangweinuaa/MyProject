package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataGenerateReq;
import com.dsc.spos.json.cust.req.DCP_SStockInUpdateReq;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataGenerateRes;
import com.dsc.spos.json.cust.res.DCP_SStockInUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.utils.ServiceAgentUtils;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.tax.TaxAmount;
import com.dsc.spos.utils.tax.TaxAmountCalculation;
import com.dsc.spos.utils.tax.TaxUtils;
import com.dsc.spos.utils.tax.Transaction;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 服務函數：SStockInUpdate
 * 說明：自采入库单修改
 * 服务说明：自采入库单修改
 *
 * @author JZMA
 * @since 2018-11-21
 */
public class DCP_SStockInUpdate extends SPosAdvanceService<DCP_SStockInUpdateReq, DCP_SStockInUpdateRes> {

    @Override
    protected boolean isVerifyFail(DCP_SStockInUpdateReq req) throws Exception {
        boolean isFail = false;
        DCP_SStockInUpdateReq.Request request = req.getRequest();
        StringBuilder errorMsg = new StringBuilder();

        if (Check.Null(request.getPayOrgNo())) {
            request.setPayOrgNo(req.getOrganizationNO());
        }
        String stockInType = request.getStockInType();
        String oType = request.getOType();

        if ("1".equals(oType)) {
            if (StringUtils.isEmpty(request.getReceivingNo())) {
                isFail = true;
                errorMsg.append("收货通知单号不可为空!");
            }
        }

        if ("4".equals(stockInType)) {
            if (StringUtils.isEmpty(request.getCustomer())) {
                isFail = true;
                errorMsg.append("客户编码不可为空!");
            }
        } else {
            if (StringUtils.isEmpty(request.getSupplier())) {
                isFail = true;
                errorMsg.append("供应商编号不可为空!");
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorMsg.toString());
        }
        return isFail;
    }

    @Override
    protected void processDUID(DCP_SStockInUpdateReq req, DCP_SStockInUpdateRes res) throws Exception {
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String modifyBy = req.getOpNO();
        String modifyDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String modifyTime = new SimpleDateFormat("HHmmss").format(new Date());

        //try {
        DCP_SStockInUpdateReq.Request request = req.getRequest();
        String sStockInNO = request.getSStockInNo();
        String bDate = request.getBDate();
        String memo = request.getMemo();
        String supplier = request.getSupplier();
        String pTemplateNO = request.getPTemplateNo();
        String docType = request.getDocType();
        String oType = request.getOType();
        String ofNO = request.getOfNo();
        String receivingNo = request.getReceivingNo();
        String loadDocType = request.getLoadDocType();
        String loadDocNO = request.getLoadDocNo();
        String warehouse = request.getWarehouse();
        //String taxCode = request.getTaxCode();
        double totPqty = 0;
        double totAmt = 0;
        double totDistriAmt = 0;
        double totDistriPreTaxAmt = 0;
        double totDistriTaxAmt = 0;
        String totCqty = String.valueOf(request.getTotCqty());

        //组织法人的进项税 和销项税
        req.getRequest().setTaxPayerType(req.getTaxPayerType());
        req.getRequest().setInputTaxCode(req.getInputTaxCode());
        req.getRequest().setInputTaxRate(req.getInputTaxRate());
        req.getRequest().setOutputTaxCode(req.getOutputTaxCode());
        req.getRequest().setOutputTaxRate(req.getOutputTaxRate());


        //前端未给值时，默认为当天  BY JZMA 20200427
        if (Check.Null(bDate)) {
            bDate = modifyDate;
        }

        if (!Check.Null(receivingNo)) {
            String receving = "select * from DCP_RECEIVING where status<>'8' and  EID='" + eId + "' and RECEIVINGNO='" + receivingNo + "' ";
            List<Map<String, Object>> listhead = this.doQueryData(receving, null);
            if (listhead == null || listhead.isEmpty()) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "收货通知单:" + ofNO + " 被ERP撤销,请重新查询！");
            }
        }

        if (Check.NotNull(req.getRequest().getOriginNo())) {
            if (Check.Null(req.getRequest().getPayee())) {
                //采购订单
                String prSql = "select * from dcp_purorder a where a.eid='" + req.geteId() + "' and a.PURORDERNO='" + req.getRequest().getOriginNo() + "'";
                List<Map<String, Object>> list = this.doQueryData(prSql, null);
                if (CollUtil.isNotEmpty(list)) {
                    req.getRequest().setPayee(list.get(0).get("PAYEE").toString());
                }
            }
            if (Check.Null(req.getRequest().getPayer())) {
                //大客订单
                String prSql = "select * from DCP_CUSTOMERPORDER a where a.eid='" + req.geteId() + "' and a.PORDERNO='" + req.getRequest().getOriginNo() + "'";
                List<Map<String, Object>> list = this.doQueryData(prSql, null);
                if (CollUtil.isNotEmpty(list)) {
                    req.getRequest().setPayee(list.get(0).get("PAYER").toString());
                }
            }

            if("4".equals(req.getRequest().getStockInType())){
                String prdSql="select * from DCP_CUSTOMERPORDER_DETAIL a " +
                        " where a.eid='"+eId+"' and a.PORDERNO='"+req.getRequest().getOriginNo()+"' ";
                List<Map<String, Object>> list = this.doQueryData(prdSql, null);
                if (CollUtil.isNotEmpty(list)) {
                    req.getRequest().getDatas().forEach(x->{
                        if(Check.NotNull(x.getOriginNo())&&Check.NotNull(x.getOriginItem())){
                            List<Map<String, Object>> filterRows = list.stream().filter(y -> y.get("ITEM").toString().equals(x.getOriginItem())).collect(Collectors.toList());
                            if(filterRows.size()>0){
                                x.setDistriPrice(Double.valueOf(filterRows.get(0).get("RECEIVEPRICE").toString()));
                                BigDecimal multiply = new BigDecimal(x.getDistriPrice()).multiply(new BigDecimal(x.getPqty()));
                                x.setDistriAmt(multiply.toString());
                            }
                        }

                    });
                }
            }
            else{
                String prdSql="select * from dcp_purorder_delivery a " +
                        " where a.eid='"+eId+"' and a.purorderno='"+req.getRequest().getOriginNo()+"' ";
                List<Map<String, Object>> list = this.doQueryData(prdSql, null);
                if (CollUtil.isNotEmpty(list)) {
                    req.getRequest().getDatas().forEach(x->{
                        if(Check.NotNull(x.getOriginNo())&&Check.NotNull(x.getOriginItem())){
                            List<Map<String, Object>> filterRows = list.stream().filter(y -> y.get("ITEM").toString().equals(x.getOriginItem())).collect(Collectors.toList());
                            if(filterRows.size()>0){
                                x.setDistriPrice(Double.valueOf(filterRows.get(0).get("RECEIVEPRICE").toString()));
                                BigDecimal multiply = new BigDecimal(x.getDistriPrice()).multiply(new BigDecimal(x.getPqty()));
                                x.setDistriAmt(multiply.toString());
                            }
                        }

                    });
                }
            }


        }

        if (Check.Null(req.getRequest().getPayee())) {
            String bizSql = "select * from DCP_BIZPARTNER where eid='" + eId + "' and bizpartnerno='" + req.getRequest().getSupplier() + "'  ";
            List<Map<String, Object>> bizList = this.doQueryData(bizSql, null);
            if (bizList.size() > 0) {
                req.getRequest().setPayee(bizList.get(0).get("PAYEE").toString());
            }
        }
        if (Check.Null(req.getRequest().getPayer())) {
            String bizSql = "select * from DCP_BIZPARTNER where eid='" + req.geteId() + "' and bizpartnerno='" + req.getRequest().getCustomer() + "'  ";
            List<Map<String, Object>> bizList = this.doQueryData(bizSql, null);
            if (bizList.size() > 0) {
                req.getRequest().setPayer(bizList.get(0).get("PAYER").toString());
            }
        }

        String pSql = "select a.CORP,a.BIZORGNO,a.BIZCORP,b.*" +
                " from DCP_PURRECEIVE a" +
                " left join DCP_PURRECEIVE_DETAIL b on a.eid=b.eid and a.billno=b.billno" +
                " where a.eid='" + req.geteId() + "' and a.billno='" + req.getRequest().getOfNo() + "'";
        List<Map<String, Object>> purrOrder = this.doQueryData(pSql, null);

        String receivingSql = "select a.RECEIPTCORP,a.CORP,a.organizationno,b.*" +
                " from DCP_RECEIVING a" +
                " left join dcp_receiving_detail b on a.eid=b.eid and a.organizationno=b.organizationno and a.receivingno=b.receivingno" +
                " where a.eid='" + req.geteId() + "' and a.receivingno='" + req.getRequest().getReceivingNo() + "'";
        List<Map<String, Object>> receivingList = this.doQueryData(receivingSql, null);

        //采购模板
        String templateSql = "select a.*,b.unitratio" +
                " from DCP_PURCHASETEMPLATE_GOODS a" +
                " left join dcp_goods_unit b on a.eid=b.eid and a.pluno=b.pluno and a.purunit=b.ounit where a.eid='" + req.geteId() + "' and a.PURTEMPLATENO='" + req.getRequest().getPTemplateNo() + "' ";
        List<Map<String, Object>> templateList = this.doQueryData(templateSql, null);

        req.getRequest().setCorp(req.getCorp());
        if (Check.Null(req.getRequest().getBizOrgNo())) {
            //● 业务归属组织bizOrgNo：入参值为空时取值：
            // 有来源单优先取来源单号记录的业务组织，无来源单取收货组织organizationNo;
            //  ○ 采购业务-采购组织：来源单号记录的业务归属组织：采购收货单BIZORGNO，收货通知单ORGANIZATIONNO;
            //  ○ 销售业务-销售组织：来源单号记录的业务归属组织：收货通知单ORGANIZATIONNO

            if (CollUtil.isNotEmpty(purrOrder)) {
                req.getRequest().setBizOrgNo(purrOrder.get(0).get("BIZORGNO").toString());
            } else if (Check.NotNull(req.getRequest().getReceivingNo())) {
                if (CollUtil.isNotEmpty(receivingList)) {
                    req.getRequest().setBizOrgNo(receivingList.get(0).get("ORGANIZATIONNO").toString());
                }
            } else {
                req.getRequest().setBizOrgNo(req.getOrganizationNO());
            }

        }

        if (Check.Null(req.getRequest().getBizCorp())) {
            //业务归属法人bizCorp：有来源单取来源单号记录的业务归属法人：
            // 采购收货单BIZORGCORP/收货通知单COR， 无来源单取bizOrgNo所属法人；
            if (CollUtil.isNotEmpty(purrOrder)) {
                req.getRequest().setBizCorp(purrOrder.get(0).get("BIZCORP").toString());
            } else if (Check.NotNull(req.getRequest().getReceivingNo())) {
                if (CollUtil.isNotEmpty(receivingList)) {
                    req.getRequest().setBizCorp(receivingList.get(0).get("CORP").toString());
                }
            } else {
                if (Check.NotNull(req.getRequest().getBizOrgNo())) {
                    String orgSql = "select * from dcp_org where eid='" + req.geteId() + "' and organizationno='" + req.getRequest().getBizOrgNo() + "' ";
                    List<Map<String, Object>> orgList = this.doQueryData(orgSql, null);
                    if (CollUtil.isNotEmpty(orgList)) {
                        req.getRequest().setBizCorp(orgList.get(0).get("CORP").toString());
                    }
                }
            }
        }

        //入库进项税 只有采购有
        if (!"4".equals(req.getRequest().getStockInType())) {
            String taxSql = "select * from DCP_TAXCATEGORY a where a.eid='" + req.geteId() + "' ";
            List<Map<String, Object>> getTax = this.doQueryData(taxSql, null);

            if(Check.Null(req.getRequest().getTaxCode())){
                String querySupplier = "select a.taxcode,c.taxrate,c.taxcaltype,c.incltax " +
                        " from DCP_BIZPARTNER a " +
                        " inner join DCP_TAXCATEGORY c on c.eid=a.eid and c.taxcode=a.taxcode " +
                        " where a.eid='" + req.geteId() + "' " +
                        " and a.bizpartnerno='" + req.getRequest().getSupplier() + "' ";
                List<Map<String, Object>> supplierList = this.doQueryData(querySupplier, null);

                req.getRequest().setTaxCode(supplierList.get(0).get("TAXCODE").toString());
            }

            req.getRequest().getDatas().forEach(x->{
                if(Check.Null(x.getOfNo())){
                    x.setTaxCode("");
                    x.setTaxRate("0");
                    x.setTaxCalType("");
                    x.setInclTax("");
                }
            });
            MyCommon cm = new MyCommon();
            List<DCP_SStockInUpdateReq.Datas> taxFilterList = req.getRequest().getDatas().stream().filter(x ->  Check.Null(x.getTaxCode())).collect(Collectors.toList());
            if (taxFilterList.size() > 0) {
                String ptemplateSql = "select distinct a.PURTEMPLATENO,b.TAXCODE,b.pluno,c.taxrate,c.taxcaltype,c.incltax " +
                        " from DCP_PURCHASETEMPLATE a " +
                        " inner join DCP_PURCHASETEMPLATE_GOODS b on a.eid=b.eid and a.PURTEMPLATENO=b.PURTEMPLATENO " +
                        " inner join DCP_TAXCATEGORY c on c.eid=a.eid and c.taxcode=b.taxcode " +
                        " where a.eid='" + req.geteId() + "' and a.status='100' and a.SUPPLIERNO='" + req.getRequest().getSupplier() + "' " +
                        //" and a.PURTYPE='" + req.getRequest().getPurType() + "' " +
                        "";
                List<Map<String, Object>> ptemplateList = this.doQueryData(ptemplateSql, null);



                String pluNos = taxFilterList.stream().map(x -> x.getPluNo()).collect(Collectors.joining(",")) + ",";
                Map<String, String> mapPluNo = new HashMap<String, String>();
                mapPluNo.put("PLUNO", pluNos.toString());

                String withasSql_pluno = "";
                withasSql_pluno = cm.getFormatSourceMultiColWith(mapPluNo);
                mapPluNo = null;

                String pluSql = "with p as (" + withasSql_pluno + ") " +
                        " select a.*,b.taxrate as inputtaxrate,b.taxcaltype as inputtaxcaltype,b.incltax as inputincltax" +
                        " from dcp_goods a " +
                        " inner join p on a.pluno=p.pluno " +
                        " left join dcp_taxcategory b on a.eid=b.eid and a.inputtaxcode=b.taxcode " +
                        " where a.eid='" + req.geteId() + "' ";
                List<Map<String, Object>> pluList = this.doQueryData(pluSql, null);


                req.getRequest().getDatas().forEach(x -> {
                    if (Check.Null(x.getTaxCode())) {
                        List<Map<String, Object>> pluFilters = ptemplateList.stream().filter(y -> y.get("PLUNO").toString().equals(x.getPluNo())).collect(Collectors.toList());

                        //如果模板编号不为空 加上模板编号
                        if(Check.NotNull(x.getPTemplateNo())){
                             pluFilters = ptemplateList.stream().filter(y -> y.get("PLUNO").toString().equals(x.getPluNo())&&y.get("PURTEMPLATENO").toString().equals(x.getPTemplateNo())).collect(Collectors.toList());
                        }

                        if (pluFilters.size() > 0) {
                            x.setTaxCode(pluFilters.get(0).get("TAXCODE").toString());
                            x.setTaxRate(pluFilters.get(0).get("TAXRATE").toString());
                            x.setInclTax(pluFilters.get(0).get("INCLTAX").toString());
                            x.setTaxCalType(pluFilters.get(0).get("TAXCALTYPE").toString());
                        }

                        /**
                        if (Check.Null(x.getTaxCode()) && supplierList.size() > 0) {
                            x.setTaxCode(supplierList.get(0).get("TAXCODE").toString());
                            x.setTaxRate(supplierList.get(0).get("TAXRATE").toString());
                            x.setInclTax(supplierList.get(0).get("INCLTAX").toString());
                            x.setTaxCalType(supplierList.get(0).get("TAXCALTYPE").toString());
                        }
                         **/

                        if(Check.Null(x.getTaxCode())&&Check.NotNull(req.getRequest().getTaxCode())){
                            List<Map<String, Object>> taxcodes = getTax.stream().filter(y -> y.get("TAXCODE").toString().equals(req.getRequest().getTaxCode())).collect(Collectors.toList());

                            x.setTaxCode(taxcodes.get(0).get("TAXCODE").toString());
                            x.setTaxRate(taxcodes.get(0).get("TAXRATE").toString());
                            x.setInclTax(taxcodes.get(0).get("INCLTAX").toString());
                            x.setTaxCalType(taxcodes.get(0).get("TAXCALTYPE").toString());
                        }


                        if (Check.Null(x.getTaxCode())) {
                            List<Map<String, Object>> giftPluFilterRows = pluList.stream().filter(y -> y.get("PLUNO").toString().equals(x.getPluNo())).collect(Collectors.toList());
                            if (giftPluFilterRows.size() > 0) {
                                x.setTaxCode(giftPluFilterRows.get(0).get("INPUTTAXCODE").toString());
                                x.setTaxRate(giftPluFilterRows.get(0).get("INPUTTAXRATE").toString());
                                x.setInclTax(giftPluFilterRows.get(0).get("INPUTINCLTAX").toString());
                                x.setTaxCalType(giftPluFilterRows.get(0).get("INPUTTAXCALTYPE").toString());
                            }

                        }

                    }
                });

            }

            if (("2").equals(req.getRequest().getTaxPayerType())) {

                req.getRequest().getDatas().forEach(x -> {
                    if(Check.Null(x.getOfNo())){
                        x.setTaxCode(req.getInputTaxCode());
                        List<Map<String, Object>> taxcodes = getTax.stream().filter(y -> y.get("TAXCODE").toString().equals(x.getTaxCode())).collect(Collectors.toList());
                        if (taxcodes.size() > 0) {
                            x.setTaxRate(taxcodes.get(0).get("TAXRATE").toString());
                            x.setTaxCalType(taxcodes.get(0).get("TAXCALTYPE").toString());
                            x.setInclTax(taxcodes.get(0).get("INCLTAX").toString());
                        }
                    }
                });
            }

        }

        //custprice refcustPrice
        if("4".equals(req.getRequest().getStockInType())){
            //custprice  refCustPrice

            //参考大客价/
            //  b. 价格取值来源：
            //    ⅰ. 1️⃣按销退通知：来源通知单销退价custPrice；
            //    ⅱ. 2️⃣按销货单：来源销货单销售价custPrice，
            //    更换单位需换算：基准单位销售价refCustPriceBaseUnit * unitRatio单位换算率；
            //    ⅲ. 3️⃣ 无源销退：最新客户销售价custPrice
            if("2".equals(oType)){
                //销退通知
                String stSql="select b.*,c.unitratio from DCP_STOCKOUTNOTICE a" +
                        " left join  DCP_STOCKOUTNOTICE_DETAIL b on a.eid=b.eid and a.sstockoutno=b.sstockoutno " +
                        " left join dcp_goods_unit c on a.eid=c.eid and a.pluno=c.pluno and b.punit=c.ounit" +
                        " where a.eid='"+eId+"'  " +
                        " and a.billno='"+req.getRequest().getOfNo()+"'  " +
                        "  ";
                List<Map<String, Object>> stList = this.doQueryData(stSql, null);
                req.getRequest().getDatas().forEach(x->{
                    List<Map<String, Object>> sstFilter = stList.stream().filter(y -> y.get("ITEM").toString().equals(x.getOItem())).collect(Collectors.toList());
                    if(sstFilter.size()>0){
                        String thisPrice = sstFilter.get(0).get("CUSTPRICE").toString();
                        //String unitRatio = sstFilter.get(0).get("UNITRATIO").toString();
                        //BigDecimal multiply = new BigDecimal(thisPrice).divide(new BigDecimal(unitRatio), 6, RoundingMode.HALF_UP).multiply(new BigDecimal(x.getUnitRatio()));
                        x.setRefCustPrice(thisPrice);
                        if(Check.Null(x.getCustPrice())){
                            x.setCustPrice(thisPrice);
                        }
                    }
                });


            }
            else if("4".equals(oType)){
                //销货单

                String ssSql="select b.*,c.unitratio from DCP_SSTOCKOUT a" +
                        " left join  DCP_SSTOCKOUT_detail b on a.eid=b.eid and a.sstockoutno=b.sstockoutno " +
                        " left join dcp_goods_unit c on a.eid=c.eid and a.pluno=c.pluno and b.punit=c.ounit" +

                        " where a.eid='"+eId+"' and a.STOCKOUTTYPE='2' " +
                        " and a.customer='"+req.getRequest().getCustomer()+"' and a.sstockoutno='"+req.getRequest().getOfNo()+"'  " +
                        "  ";
                List<Map<String, Object>> ssList = this.doQueryData(ssSql, null);
                req.getRequest().getDatas().forEach(x->{
                    List<Map<String, Object>> sstFilter = ssList.stream().filter(y -> y.get("ITEM").toString().equals(x.getOItem())).collect(Collectors.toList());
                    if(sstFilter.size()>0){
                        String thisPrice = sstFilter.get(0).get("CUSTPRICE").toString();
                        String unitRatio = sstFilter.get(0).get("UNITRATIO").toString();
                        BigDecimal multiply = new BigDecimal(thisPrice).divide(new BigDecimal(unitRatio), 6, RoundingMode.HALF_UP).multiply(new BigDecimal(x.getUnitRatio()));
                        x.setRefCustPrice(multiply.toString());

                        if(Check.Null(x.getCustPrice())){
                            x.setCustPrice(thisPrice);
                        }

                    }
                });

            }
            else if(Check.Null(oType)){
                //无源
                String cpSql="select a.*,b.UNITRATIO from DCP_CUSTOMER_PRICE a " +
                        " left join dcp_goods_unit b on a.eid=b.eid and a.pluno=b.pluno and a.unit=b.ounit" +
                        " where a.eid='"+eId+"' and a.customerno='"+req.getRequest().getCustomer()+"' and a.status='100' and to_char(sysdate,'yyyyMMdd')<=a.ENDDATE  order by a.lastmoditime ";
                List<Map<String, Object>> cpList = this.doQueryData(cpSql, null);
                req.getRequest().getDatas().forEach(x->{
                    List<Map<String, Object>> cpFilters = cpList.stream().filter(y -> y.get("PLUNO").toString().equals(x.getPluNo())).collect(Collectors.toList());
                    if(cpFilters.size()>0){
                        //String thisUnit = cpFilters.get(0).get("UNIT").toString();
                        String thisPrice = cpFilters.get(0).get("PRICE").toString();
                        //String unitRatio = cpFilters.get(0).get("UNITRATIO").toString();
                        //BigDecimal multiply = new BigDecimal(thisPrice).divide(new BigDecimal(unitRatio), 6, RoundingMode.HALF_UP).multiply(new BigDecimal(x.getUnitRatio()));
                        x.setRefCustPrice(thisPrice);
                        if(Check.Null(x.getCustPrice())){
                            x.setCustPrice(thisPrice);
                        }
                    }

                });


            }


        }



        String sql = "select * from DCP_SSTOCKIN "
                + " where EID='" + eId + "' and organizationNO='" + organizationNO + "' and sStockInNO='" + sStockInNO + "' and status='0' ";
        List<Map<String, Object>> getQDataKey = this.doQueryData(sql, null);

        //查询下list
        String detailSql = "select a.* from dcp_sstockin_detail a where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "'" +
                " and a.sstockinno='" + sStockInNO + "' ";
        List<Map<String, Object>> detailList = this.doQueryData(detailSql, null);

        if (getQDataKey != null && !getQDataKey.isEmpty()) {
            String keyDate = getQDataKey.get(0).get("BDATE").toString();

            //删除原有单身
            DelBean db1 = new DelBean("DCP_SSTOCKIN_DETAIL");
            db1.addCondition("sStockInNO", new DataValue(sStockInNO, Types.VARCHAR));
            db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            DelBean db2 = new DelBean("DCP_TRANSACTION");
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db2.addCondition("BILLNO", new DataValue(sStockInNO, Types.VARCHAR));
            db2.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));
            double amt = 0;
            double distriAmt = 0;
            String currency = request.getCurrency();
            String payType = request.getPayType();
            String payOrgNo = request.getPayOrgNo();
            String billDateNo = request.getBillDateNo();
            String payDateNo = request.getPayDateNo();
            String invoiceCode = request.getInvoiceCode();

            String querySql = " SELECT a.PAYTYPE,a.PAYORGNO,a.BILLDATENO,a.PAYDATENO,a.INVOICECODE,a.CURRENCY " +
                    " FROM DCP_SSTOCKIN a " +
                    " INNER JOIN DCP_RECEIVING b ON a.EID=b.EID and a.RECEIVINGNO=b.RECEIVINGNO  " +
                    " WHERE b.EID='" + eId + "' AND b.RECEIVINGNO='" + request.getReceivingNo() + "'";
            List<Map<String, Object>> payOrder = this.doQueryData(querySql, null);
            if (null != payOrder && !payOrder.isEmpty()) {

                if (StringUtils.isEmpty(request.getPayType())) {
                    payType = StringUtils.toString(payOrder.get(0).get("PAYTYPE"), "");
                }
                if (StringUtils.isEmpty(request.getPayOrgNo())) {
                    payOrgNo = StringUtils.toString(payOrder.get(0).get("PAYORGNO"), "");
                }
                if (StringUtils.isEmpty(request.getBillDateNo())) {
                    billDateNo = StringUtils.toString(payOrder.get(0).get("BILLDATENO"), "");
                }
                if (StringUtils.isEmpty(request.getPayDateNo())) {
                    payDateNo = StringUtils.toString(payOrder.get(0).get("PAYDATENO"), "");
                }
                if (StringUtils.isEmpty(request.getInvoiceCode())) {
                    invoiceCode = StringUtils.toString(payOrder.get(0).get("INVOICECODE"), "");
                }
                if (StringUtils.isEmpty(request.getCurrency())) {
                    currency = StringUtils.toString(payOrder.get(0).get("CURRENCY"), "");
                }
            }
            if (StringUtils.isEmpty(payOrgNo)) {
                payOrgNo = organizationNO;
            }

            List<Transaction> transactions = new ArrayList<>();

            ColumnDataValue dcp_sstockin = new ColumnDataValue();
            ColumnDataValue conditon = new ColumnDataValue();

            conditon.add("EID", DataValues.newString(eId));
            conditon.add("sStockInNO", DataValues.newString(sStockInNO));
            conditon.add("organizationNO", DataValues.newString(organizationNO));

            dcp_sstockin.add("SHOPID", DataValues.newString(shopId));

            dcp_sstockin.add("PAYTYPE", DataValues.newString(payType));
            dcp_sstockin.add("PAYORGNO", DataValues.newString(payOrgNo));
            dcp_sstockin.add("BILLDATENO", DataValues.newString(billDateNo));
            dcp_sstockin.add("PAYDATENO", DataValues.newString(payDateNo));
            dcp_sstockin.add("INVOICECODE", DataValues.newString(invoiceCode));
            dcp_sstockin.add("CURRENCY", DataValues.newString(currency));

            dcp_sstockin.add("RECEIVINGNO", DataValues.newString(req.getRequest().getReceivingNo()));
            dcp_sstockin.add("STOCKINTYPE", DataValues.newString(req.getRequest().getStockInType()));
            dcp_sstockin.add("CUSTOMER", DataValues.newString(req.getRequest().getCustomer()));
            dcp_sstockin.add("OOTYPE", DataValues.newString(req.getRequest().getOoType()));
            dcp_sstockin.add("OOFNO", DataValues.newString(req.getRequest().getOofNo()));
            dcp_sstockin.add("ORGIGINNO", DataValues.newString(req.getRequest().getOriginNo()));
            dcp_sstockin.add("RETURNTYPE", DataValues.newString(req.getRequest().getReturnType()));

            dcp_sstockin.add("BDATE", DataValues.newString(bDate));
            dcp_sstockin.add("PAYEE", DataValues.newString(req.getRequest().getPayee()));
            dcp_sstockin.add("PURTYPE", DataValues.newString(req.getRequest().getPurType()));
            dcp_sstockin.add("PAYER", DataValues.newString(req.getRequest().getPayer()));
            dcp_sstockin.add("EMPLOYEEID", DataValues.newString(req.getRequest().getEmployeeId()));
            dcp_sstockin.add("DEPARTID", DataValues.newString(req.getRequest().getDepartId()));

            dcp_sstockin.add("MEMO", DataValues.newString(memo));
            dcp_sstockin.add("STATUS", DataValues.newString(req.getRequest().getStatus()));
            dcp_sstockin.add("SUPPLIER", DataValues.newString(supplier));
            dcp_sstockin.add("pTemplateNO", DataValues.newString(pTemplateNO));
            dcp_sstockin.add("doc_Type", DataValues.newString(docType));
            dcp_sstockin.add("oType", DataValues.newString(oType));
            dcp_sstockin.add("ofNO", DataValues.newString(ofNO));
            dcp_sstockin.add("load_DocType", DataValues.newString(loadDocType));
            dcp_sstockin.add("load_DocNO", DataValues.newString(loadDocNO));
            dcp_sstockin.add("load_ReceiptNO", DataValues.newString(req.getRequest().getLoadReceiptNo()));
            dcp_sstockin.add("WAREHOUSE", DataValues.newString(warehouse));
            dcp_sstockin.add("TAXCODE", DataValues.newString(req.getRequest().getTaxCode()));
            dcp_sstockin.add("BUYERNO", DataValues.newString(req.getRequest().getBuyerNo()));
            dcp_sstockin.add("BUYERNAME", DataValues.newString(req.getRequest().getBuyerName()));
            dcp_sstockin.add("CREATE_CHATUSERID", DataValues.newString(req.getChatUserId()));
            dcp_sstockin.add("UPDATE_TIME", DataValues.newString(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime())));
            dcp_sstockin.add("TRAN_TIME", DataValues.newString(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime())));

            dcp_sstockin.add("MODIFYBY", DataValues.newString(modifyBy));
            dcp_sstockin.add("MODIFY_DATE", DataValues.newString(modifyDate));
            dcp_sstockin.add("MODIFY_TIME", DataValues.newString(modifyTime));
            dcp_sstockin.add("CORP", DataValues.newString(req.getRequest().getCorp()));
            dcp_sstockin.add("BIZORGNO", DataValues.newString(req.getRequest().getBizOrgNo()));
            dcp_sstockin.add("BIZCORP", DataValues.newString(req.getRequest().getBizCorp()));
            dcp_sstockin.add("TAXPAYER_TYPE", DataValues.newString(req.getRequest().getTaxPayerType()));
            dcp_sstockin.add("INPUT_TAXCODE", DataValues.newString(req.getRequest().getInputTaxCode()));
            dcp_sstockin.add("INPUT_TAXRATE", DataValues.newString(req.getRequest().getInputTaxRate()));
            dcp_sstockin.add("OUTPUT_TAXCODE", DataValues.newString(req.getRequest().getOutputTaxCode()));
            dcp_sstockin.add("OUTPUT_TAXRATE", DataValues.newString(req.getRequest().getOutputTaxRate()));


            //新增新的单身（多条记录）
            List<DCP_SStockInUpdateReq.Datas> datas = request.getDatas();
            //查询商品
            String pluStr = datas.stream().map(x -> "'" + x.getPluNo() + "'").distinct().collect(Collectors.joining(","));
            String pluSql = "select * from dcp_goods where eid='" + req.geteId() + "' and pluno in (" + pluStr + ")";
            List<Map<String, Object>> pluList = this.doQueryData(pluSql, null);

            //获取currency信息
            int amountDigit = 2;
            if (Check.NotNull(request.getCurrency())) {
                String currencySql = "select * from DCP_CURRENCY where eid='" + eId + "' and" +
                        " CURRENCY='" + currency + "' ";
                List<Map<String, Object>> currencies = this.doQueryData(currencySql, null);
                if (currencies.size() <= 0) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "币种:" + request.getCurrency() + "不存在!");
                }
                amountDigit = Integer.parseInt(currencies.get(0).get("AMOUNTDIGIT").toString());
            }

            String isBatchPara = PosPub.getPARA_SMS(dao, eId, req.getShopId(), "Is_BatchNO");
            if (Check.Null(isBatchPara) || !isBatchPara.equals("Y")) {
                isBatchPara = "N";
            }
            TaxUtils taxUtils = new TaxUtils();
            TaxUtils.Tax tax = null;
            //String taxCalType = "1";
            //String taxRate = "0";
            //String inclTax = "N";
            for (DCP_SStockInUpdateReq.Datas par : datas) {

                if (Check.Null(par.getPurPrice())) {
                    List<Map<String, Object>> purrFilterRows = purrOrder.stream().filter(x -> x.get("ITEM").toString().equals(par.getRItem())).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(purrFilterRows)) {
                        par.setPurPrice(purrFilterRows.get(0).get("PURPRICE").toString());
                    } else {
                        List<Map<String, Object>> receivingFilterRows = receivingList.stream().filter(x -> x.get("ITEM").toString().equals(par.getRItem())).collect(Collectors.toList());
                        if (CollUtil.isNotEmpty(receivingFilterRows)) {
                            par.setPurPrice(receivingFilterRows.get(0).get("PURPRICE").toString());
                        } else {
                            //取采购模板最新采购价  挪下面去了
                        }
                    }

                    if (Check.Null(par.getPurPrice())) {
                        //取采购模板最新采购价
                        List<Map<String, Object>> templateFilterRows = templateList.stream().filter(x -> x.get("PLUNO").toString().equals(par.getPluNo())).collect(Collectors.toList());
                        if (CollUtil.isNotEmpty(templateFilterRows)) {
                            BigDecimal purBasePrice = new BigDecimal(templateFilterRows.get(0).get("PURBASEPRICE").toString());
                            BigDecimal unitratio = new BigDecimal(templateFilterRows.get(0).get("UNITRATIO").toString());
                            BigDecimal divide = purBasePrice.multiply(unitratio).divide(new BigDecimal(par.getUnitRatio()), 2, RoundingMode.HALF_UP);
                            par.setPurPrice(divide.toString());
                        } else {
                            par.setPurPrice("0");
                        }
                    }
                }

                if(Check.Null(par.getSdPurPrice())){
                    List<Map<String, Object>> tempFilter = templateList.stream().filter(x -> x.get("PLUNO").toString().equals(par.getPluNo())).collect(Collectors.toList());
                    if(CollUtil.isNotEmpty(templateList)){
                        par.setSdPurPrice(tempFilter.get(0).get("PURBASEPRICE").toString());
                    }
                }
                if(Check.Null(par.getSdPurPrice())){
                    List<Map<String, Object>> pluFilter = pluList.stream().filter(x -> x.get("PLUNO").toString().equals(par.getPluNo())).collect(Collectors.toList());
                    if(CollUtil.isNotEmpty(pluFilter)){
                        par.setSdPurPrice(pluFilter.get(0).get("PURPRICE").toString());
                    }
                }

                if (Check.NotNull(par.getPurPrice())) {
                    BigDecimal multiply = new BigDecimal(par.getPurPrice()).multiply(new BigDecimal(par.getPqty())).setScale(2, RoundingMode.HALF_UP);
                    par.setPurAmt(multiply.toString());
                } else {
                    par.setPurAmt("0");
                }


                List<Map<String, Object>> pluFilter = pluList.stream().filter(x -> x.get("PLUNO").toString().equals(par.getPluNo())).collect(Collectors.toList());
                if (pluFilter.size() <= 0) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品" + par.getPluNo() + "不存在！");
                }
                int shelflife = Check.Null(pluFilter.get(0).get("SHELFLIFE").toString()) ? 0 : Integer.valueOf(pluFilter.get(0).get("SHELFLIFE").toString());
                String isBatch = pluFilter.get(0).get("ISBATCH").toString();

                if ("Y".equals(isBatchPara) && ("Y".equals(isBatch) || isBatch.equals("T"))) {

                    if (shelflife != 0 && Check.Null(par.getProdDate()) && Check.Null(par.getExpDate())) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品" + par.getPluNo() + "生产日期和失效日期不可全为空！");
                    }

                    if (shelflife != 0) {//两个日期互算
                        if (Check.Null(par.getExpDate())) {
                            par.setExpDate(DateFormatUtils.format(DateFormatUtils.addDay(DateFormatUtils.parseDate(par.getProdDate()),
                                    shelflife), DateFormatUtils.PLAIN_DATE_FORMAT));
                        }

                        if (Check.Null(par.getProdDate())) {
                            par.setProdDate(DateFormatUtils.format(DateFormatUtils.addDay(DateFormatUtils.parseDate(par.getExpDate()),
                                    -shelflife), DateFormatUtils.PLAIN_DATE_FORMAT));
                        }
                    }
                    //批号为空 重新生成不存mes_batch  生产日期为空的就不生成了
                    if (Check.NotNull(par.getBatchNo()) && Check.NotNull(par.getProdDate())) {
                        //批号不为空的时候如何判断批号需要重新生成
                        //用品号 特征码 生产日期  失效日期不参与生成不比较
                        List<Map<String, Object>> filterRows = detailList.stream().filter(x -> x.get("PLUNO").toString().equals(par.getPluNo())
                                && x.get("FEATURENO").toString().equals(par.getFeatureNo())
                                && x.get("PROD_DATE").toString().equals(par.getProdDate())
                        ).collect(Collectors.toList());
                        if (filterRows.size() <= 0) {
                            //用当前生产日期找不到数据了  要么品号新增了  要么生产日期改了
                            //重新生成一个 这个品号占位置  不存mes_batch
                            String batchNo = PosPub.getBatchNo(dao, req.geteId(), req.getShopId(), "", par.getPluNo(), par.getFeatureNo(), par.getProdDate(), par.getExpDate(), req.getOpNO(), req.getOpName(), par.getBatchNo(), false);
                            par.setBatchNo(batchNo);
                        }
                    } else if (Check.Null(par.getBatchNo()) && Check.NotNull(par.getProdDate())) {
                        String batchNo = PosPub.getBatchNo(dao, req.geteId(), req.getShopId(), "", par.getPluNo(), par.getFeatureNo(), par.getProdDate(), par.getExpDate(), req.getOpNO(), req.getOpName(), par.getBatchNo(), false);
                        par.setBatchNo(batchNo);
                    }
                }


                ColumnDataValue dcp_sstockin_detail = new ColumnDataValue();

                amt = BigDecimalUtils.mul(par.getPrice(), Double.parseDouble(par.getPqty()));
                distriAmt = BigDecimalUtils.mul(par.getDistriPrice(), Double.parseDouble(par.getPqty()));


                /**
                if(Check.Null(par.getTaxCode())){
                    if("4".equals(req.getRequest().getStockInType())){
                        tax = taxUtils.getTaxWithType(eId,
                                organizationNO,
                                request.getSupplier(),
                                par.getPTemplateNo(),
                                par.getPluNo(),"X");
                    }
                    else{
                        tax = taxUtils.getTaxWithType(eId,
                                organizationNO,
                                request.getSupplier(),
                                par.getPTemplateNo(),
                                par.getPluNo(),"J");
                    }
                    par.setTaxCode(tax.getTaxCode());
                    par.setTaxCalType(tax.getTaxCalType());
                    par.setTaxRate(String.valueOf(tax.getTaxRate()));
                    par.setInclTax(tax.getInclTax());
                }
**/

                //if (StringUtils.isEmpty(taxCode) || !StringUtils.equals(taxCode, par.getTaxCode())) {
                //    taxCode = par.getTaxCode();
                //}

                //if (StringUtils.isNotEmpty(par.getTaxCalType())) {
                 //   taxCalType = par.getTaxCalType();
                //}

                //if (StringUtils.isNotEmpty(par.getTaxRate())) {
                //    taxRate = par.getTaxRate();
                //}

                //if (StringUtils.isNotEmpty(par.getInclTax())) {
                 //   inclTax = par.getInclTax();
                //}

                totPqty += Double.parseDouble(par.getPqty());
                totAmt += amt;
                totDistriAmt += distriAmt;

                dcp_sstockin_detail.add("SSTOCKINNO", DataValues.newString(sStockInNO));
                dcp_sstockin_detail.add("SHOPID", DataValues.newString(shopId));
                dcp_sstockin_detail.add("item", DataValues.newInteger(par.getItem()));
                dcp_sstockin_detail.add("oItem", DataValues.newString(par.getOItem()));
                dcp_sstockin_detail.add("pluNO", DataValues.newString(par.getPluNo()));
                dcp_sstockin_detail.add("punit", DataValues.newString(par.getPunit()));
                dcp_sstockin_detail.add("pqty", DataValues.newDecimal(par.getPqty()));
                dcp_sstockin_detail.add("BASEUNIT", DataValues.newString(par.getBaseUnit()));

                if (StringUtils.isEmpty(par.getBaseQty())) {
                    double baseQty = BigDecimalUtils.mul(par.getUnitRatio(), Double.parseDouble(par.getPqty()));
                    dcp_sstockin_detail.add("BASEQTY", DataValues.newDecimal(baseQty));
                } else {
                    dcp_sstockin_detail.add("BASEQTY", DataValues.newDecimal(par.getBaseQty()));
                }

                dcp_sstockin_detail.add("unit_Ratio", DataValues.newDecimal(par.getUnitRatio()));
                dcp_sstockin_detail.add("price", DataValues.newDecimal(par.getPrice()));

                dcp_sstockin_detail.add("EID", DataValues.newString(eId));
                dcp_sstockin_detail.add("organizationNO", DataValues.newString(organizationNO));
                dcp_sstockin_detail.add("poQty", DataValues.newDecimal(par.getPqty()));
                dcp_sstockin_detail.add("receiving_Qty", DataValues.newDecimal(par.getReceivingQty()));
                dcp_sstockin_detail.add("WAREHOUSE", DataValues.newString(par.getWarehouse()));
                dcp_sstockin_detail.add("PLU_MEMO", DataValues.newString(par.getPluMemo()));
//                    dcp_sstockin_detail.add("STOCKIN_QTY", DataValues.newDecimal(par.getst()));
                //if (!StringUtils.isEmpty(par.getBatchNo())) {
                //   dcp_sstockin_detail.add("BATCH_NO", DataValues.newString(par.getBatchNo()));
                //} else {
                //    String prodDate = par.getProdDate();
                //    if (StringUtils.isEmpty(prodDate)) {
                //        prodDate = DateFormatUtils.getNowDate();
                //    }
                //    dcp_sstockin_detail.add("BATCH_NO", DataValues.newString(PosPub.getBatchNo(dao, req.geteId(), req.getShopId(), "", par.getPluNo(), par.getFeatureNo(), prodDate, par.getExpDate(), req.getOpNO(), req.getOpName(), par.getBatchNo())));
                //}
                dcp_sstockin_detail.add("BATCH_NO", DataValues.newString(par.getBatchNo()));

                dcp_sstockin_detail.add("PROD_DATE", DataValues.newString(DateFormatUtils.getPlainDate(par.getProdDate())));
                dcp_sstockin_detail.add("DISTRIPRICE", DataValues.newDecimal(par.getDistriPrice()));
                dcp_sstockin_detail.add("AMT", DataValues.newDecimal(amt));
                dcp_sstockin_detail.add("DISTRIAMT", DataValues.newDecimal(distriAmt));

                dcp_sstockin_detail.add("BDATE", DataValues.newString(bDate));
                dcp_sstockin_detail.add("FEATURENO", DataValues.newString(par.getFeatureNo()));
                dcp_sstockin_detail.add("TAXCALTYPE", DataValues.newString(par.getTaxCalType()));

                dcp_sstockin_detail.add("FEATURENO", DataValues.newString(par.getFeatureNo()));
                dcp_sstockin_detail.add("TAXCALTYPE", DataValues.newString(par.getTaxCalType()));
                dcp_sstockin_detail.add("RECEIVINGNO", DataValues.newString(par.getReceivingNo()));
                dcp_sstockin_detail.add("RECEIVINGITEM", DataValues.newString(par.getRItem()));
                dcp_sstockin_detail.add("OTYPE", DataValues.newString(par.getOType()));
                dcp_sstockin_detail.add("OFNO", DataValues.newString(par.getOfNo()));
                dcp_sstockin_detail.add("PTEMPLATENO", DataValues.newString(par.getPTemplateNo()));
                dcp_sstockin_detail.add("CATEGORY", DataValues.newString(par.getCategory()));
                dcp_sstockin_detail.add("ISGIFT", DataValues.newString(par.getIsGift()));
                dcp_sstockin_detail.add("LOCATION", DataValues.newString(par.getLocation()));
                dcp_sstockin_detail.add("EXP_DATE", DataValues.newString(DateFormatUtils.getPlainDate(par.getExpDate())));
                dcp_sstockin_detail.add("ORIGINNO", DataValues.newString(par.getOriginNo()));
                dcp_sstockin_detail.add("ORIGINITEM", DataValues.newString(par.getOriginItem()));
                dcp_sstockin_detail.add("TAXCODE", DataValues.newString(par.getTaxCode()));
                dcp_sstockin_detail.add("TAXRATE", DataValues.newString(par.getTaxRate()));
                dcp_sstockin_detail.add("INCLTAX", DataValues.newString(par.getInclTax()));
                dcp_sstockin_detail.add("OOTYPE", DataValues.newString(par.getOoType()));
                dcp_sstockin_detail.add("OOFNO", DataValues.newString(par.getOofNo()));
                dcp_sstockin_detail.add("OOITEM", DataValues.newString(par.getOoItem()));
                dcp_sstockin_detail.add("TAXCALTYPE", DataValues.newString(par.getTaxCalType()));
                dcp_sstockin_detail.add("PURPRICE", DataValues.newString(par.getPurPrice()));
                dcp_sstockin_detail.add("PURAMT", DataValues.newString(par.getPurAmt()));

                dcp_sstockin_detail.add("CUSTPRICE", DataValues.newString(par.getCustPrice()));
                dcp_sstockin_detail.add("REFCUSTPRICE", DataValues.newString(par.getRefCustPrice()));
                dcp_sstockin_detail.add("SDPURPRICE", DataValues.newString(par.getSdPurPrice()));


                Transaction transaction = new Transaction();
                transaction.setPluNo(par.getPluNo());
                transaction.setFeatureNo(par.getFeatureNo());
                transaction.setUnit(par.getPunit());
                transaction.setTaxCode(par.getTaxCode());
                transaction.setTaxRate(par.getTaxRate());
                transaction.setInclTax(par.getInclTax());
                transaction.setDistriPrice(Double.parseDouble(par.getPurPrice()));
//                    transaction.setPrice(oneData.getPrice());


                if ("4".equals(req.getRequest().getStockInType())) {
                    transaction.setDistriPrice(Double.parseDouble(par.getCustPrice()));
                }

                transaction.setTaxcalType(par.getTaxCalType());

                double qty = Double.parseDouble(par.getPqty());
                //BigDecimal amount = new BigDecimal(par.getPrice()).multiply(new BigDecimal(par.getPqty()));
                BigDecimal distriAmout = new BigDecimal(transaction.getDistriPrice()).multiply(new BigDecimal(par.getPqty()));

                if (transactions.contains(transaction)) {
                    transaction = transactions.get(transactions.indexOf(transaction));

                    transaction.setQty(transaction.getQty() + qty);
                    transaction.setDistriAmount(transaction.getDistriAmount().add(distriAmout));
//                        transaction.setAmount(transaction.getAmount().add(amount));
                } else {
                    transaction.setQty(qty);
//                        transaction.setAmount(amount);
                    transaction.setDistriAmount(distriAmout);

                    transactions.add(transaction);
                }


                TaxAmount taxAmount = TaxAmountCalculation.calculateAmount(
                        "Y".equals(par.getInclTax()),
                        Double.parseDouble(distriAmout.toString()),
                        Double.parseDouble(par.getTaxRate()),
                        par.getTaxCalType(),
                        amountDigit
                );
                dcp_sstockin_detail.add("TAXAMT", DataValues.newString(taxAmount.getTaxAmount()));
                dcp_sstockin_detail.add("PRETAXAMT", DataValues.newString(taxAmount.getPreAmount()));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_SSTOCKIN_DETAIL", dcp_sstockin_detail)));

            }


            int i = 1;
            double totAmount = 0;
            for (Transaction transaction : transactions) {
                ColumnDataValue dcp_transaction = new ColumnDataValue();
                dcp_transaction.add("EID", DataValues.newString(eId));
                dcp_transaction.add("COMPANY", DataValues.newString(payOrgNo));
                dcp_transaction.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                dcp_transaction.add("BTYPE", DataValues.newString("1"));
                dcp_transaction.add("BILLNO", DataValues.newString(sStockInNO));
                dcp_transaction.add("ITEM", DataValues.newString(i++));
                dcp_transaction.add("PLUNO", DataValues.newString(transaction.getPluNo()));
                dcp_transaction.add("FEATURENO", DataValues.newString(transaction.getFeatureNo()));
                dcp_transaction.add("TAXCODE", DataValues.newString(transaction.getTaxCode()));
                dcp_transaction.add("TAXRATE", DataValues.newString(transaction.getTaxRate()));
                dcp_transaction.add("INCLTAX", DataValues.newString(transaction.getInclTax()));
                dcp_transaction.add("PUNIT", DataValues.newString(transaction.getUnit()));
                dcp_transaction.add("TAXCALTYPE", DataValues.newString(transaction.getTaxcalType()));

                double qty = transaction.getQty();
                double price = transaction.getDistriPrice();
                dcp_transaction.add("PQTY", DataValues.newString(qty));
                dcp_transaction.add("PRICE", DataValues.newString(price));


                TaxAmount taxAmount = TaxAmountCalculation.calculateAmount(
                        "Y".equals(transaction.getInclTax()),
                        transaction.getDistriAmount().doubleValue(),
                        Double.parseDouble(transaction.getTaxRate()),
                        transaction.getTaxcalType(),
                        amountDigit
                );

                dcp_transaction.add("AMT", DataValues.newString(taxAmount.getAmount()));
                dcp_transaction.add("PRETAXAMT", DataValues.newString(taxAmount.getPreAmount()));
                dcp_transaction.add("TAXAMT", DataValues.newString(taxAmount.getTaxAmount()));

                totDistriPreTaxAmt += taxAmount.getPreAmount();
                totDistriTaxAmt += taxAmount.getTaxAmount();
                totAmount += taxAmount.getAmount();

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_TRANSACTION", dcp_transaction)));
            }


            dcp_sstockin.add("TOT_PQTY", DataValues.newString(totPqty));
            dcp_sstockin.add("TOT_AMT", DataValues.newString(totAmt));
            dcp_sstockin.add("TOT_CQTY", DataValues.newString(totCqty));
            dcp_sstockin.add("TOT_DISTRIAMT", DataValues.newString(totDistriAmt));

            dcp_sstockin.add("TOTDISTRIPRETAXAMT", DataValues.newString(totDistriPreTaxAmt));
            dcp_sstockin.add("TOTDISTRITAXAMT", DataValues.newString(totDistriTaxAmt));

            //含税金额  税额 税前金额
            dcp_sstockin.add("TOTAMOUNT", DataValues.newString(totAmount));
            dcp_sstockin.add("TOTTAXAMT", DataValues.newString(totDistriTaxAmt));
            dcp_sstockin.add("TOTPRETAXAMT", DataValues.newString(totDistriPreTaxAmt));


            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_SSTOCKIN", conditon, dcp_sstockin)));


            this.doExecuteDataToDB();

            if (!req.getRequest().getCorp().equals(req.getRequest().getBizCorp())) {
                if ("1".equals(req.getRequest().getStockInType()) || "2".equals(req.getRequest().getStockInType()) || "3".equals(req.getRequest().getStockInType())) {

                    DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                    inReq.setServiceId("DCP_InterSettleDataGenerate");
                    inReq.setToken(req.getToken());
                    DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                    request1.setOrganizationNo(req.getOrganizationNO());
                    request1.setBillNo(req.getRequest().getSStockInNo());
                    request1.setSupplyOrgNo(req.getRequest().getBizOrgNo());
                    request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType11002.getType());
                    request1.setReturnSupplyPrice("Y");
                    request1.setDetail(new ArrayList<>());
                    for (DCP_SStockInUpdateReq.Datas par : datas) {
                        DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                        detail.setReceiveOrgNo(req.getOrganizationNO());
                        detail.setSourceBillNo(req.getRequest().getReceivingNo());
                        detail.setSourceItem(par.getOItem());
                        detail.setItem(String.valueOf(par.getItem()));
                        detail.setPluNo(par.getPluNo());
                        detail.setFeatureNo(par.getFeatureNo());
                        detail.setPUnit(par.getPunit());
                        detail.setPQty(String.valueOf(par.getPqty()));
                        detail.setReceivePrice(String.valueOf(par.getDistriPrice()));
                        detail.setReceiveAmt(String.valueOf(par.getDistriAmt()));
                        detail.setSupplyPrice("");
                        detail.setSupplyAmt("");
                        request1.getDetail().add(detail);
                    }
                    inReq.setRequest(request1);
                    ServiceAgentUtils<DCP_InterSettleDataGenerateReq, DCP_InterSettleDataGenerateRes> agentUtils = new ServiceAgentUtils<>();
                    if (!agentUtils.agentServiceSuccess(inReq, new TypeToken<DCP_InterSettleDataGenerateRes>() {
                    })) {
                        res.setSuccess(true);
                        res.setServiceStatus("000");
                        res.setServiceDescription("内部交易结算执行失败");
                    }
                } else if ("4".equals(req.getRequest().getStockInType())) {
                    DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                    inReq.setServiceId("DCP_InterSettleDataGenerate");
                    inReq.setToken(req.getToken());
                    DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                    request1.setOrganizationNo(req.getOrganizationNO());
                    request1.setBillNo(req.getRequest().getSStockInNo());
                    request1.setSupplyOrgNo(req.getOrganizationNO());
                    request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType12002.getType());
                    request1.setReturnSupplyPrice("Y");
                    request1.setDetail(new ArrayList<>());
                    for (DCP_SStockInUpdateReq.Datas par : datas) {
                        DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                        detail.setReceiveOrgNo(req.getRequest().getBizOrgNo());
                        detail.setSourceBillNo(req.getRequest().getReceivingNo());
                        detail.setSourceItem(par.getOItem());
                        detail.setItem(String.valueOf(par.getItem()));
                        detail.setPluNo(par.getPluNo());
                        detail.setFeatureNo(par.getFeatureNo());
                        detail.setPUnit(par.getPunit());
                        detail.setPQty(String.valueOf(par.getPqty()));
                        detail.setReceivePrice(String.valueOf(par.getDistriPrice()));
                        detail.setReceiveAmt(String.valueOf(par.getDistriAmt()));
                        detail.setSupplyPrice("");
                        detail.setSupplyAmt("");
                        request1.getDetail().add(detail);
                    }
                    inReq.setRequest(request1);
                    ServiceAgentUtils<DCP_InterSettleDataGenerateReq, DCP_InterSettleDataGenerateRes> agentUtils = new ServiceAgentUtils<>();
                    if (!agentUtils.agentServiceSuccess(inReq, new TypeToken<DCP_InterSettleDataGenerateRes>() {
                    })) {
                        res.setSuccess(true);
                        res.setServiceStatus("000");
                        res.setServiceDescription("内部交易结算执行失败");
                    }
                }
            }


            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } else {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
        }
        //} catch (Exception e) {
        //    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        //}
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_SStockInUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SStockInUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SStockInUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected TypeToken<DCP_SStockInUpdateReq> getRequestType() {
        return new TypeToken<DCP_SStockInUpdateReq>() {
        };
    }

    @Override
    protected DCP_SStockInUpdateRes getResponseType() {
        return new DCP_SStockInUpdateRes();
    }

//    private List<Map<String, Object>> getTaxCategory(String eId, String taxCode) throws Exception {
//        String getTaxSql = " SELECT TAXCALTYPE,TAXRATE TAXRATE,INCLTAX FROM DCP_TAXCATEGORY WHERE EID='" + eId + "' and TAXCODE = '" + taxCode + "'";
//        List<Map<String, Object>> getTax = doQueryData(getTaxSql, null);
//        return getTax;
//    }

}

