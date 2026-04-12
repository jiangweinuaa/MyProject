package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_SStockInQueryReq;
import com.dsc.spos.json.cust.req.DCP_SStockInQueryReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_SStockInQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_SStockInQuery extends SPosBasicService<DCP_SStockInQueryReq, DCP_SStockInQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_SStockInQueryReq req) throws Exception {
        boolean isFail = false;

        return false;
    }

    @Override
    protected TypeToken<DCP_SStockInQueryReq> getRequestType() {
        return new TypeToken<DCP_SStockInQueryReq>() {
        };
    }

    @Override
    protected DCP_SStockInQueryRes getResponseType() {
        return new DCP_SStockInQueryRes();
    }

    @Override
    protected DCP_SStockInQueryRes processJson(DCP_SStockInQueryReq req) throws Exception {
        try {
            //新增采购入库可分批收货参数   BY JZMA 2019-7-1
            String isBatchSStockIn = PosPub.getPARA_SMS(dao, req.geteId(), "", "isBatchSStockIn");
            if (Check.Null(isBatchSStockIn)) {
                isBatchSStockIn = "N";
            }
            if(Check.Null(req.getRequest().getDateType())){
                req.getRequest().setDateType("bDate");
            }

//            String sql = this.getQueryDocSql(req, isBatchSStockIn);
            String sql = this.getQuerySql(req);
            //查询资料
            DCP_SStockInQueryRes res = this.getResponse();
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            int totalRecords;    //总笔数
            int totalPages;        //总页数
            res.setDatas(new ArrayList<>());
            if (getQData != null && !getQData.isEmpty()) {
                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords = Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                //单头主键字段
                Map<String, Boolean> condition = new HashMap<>(); //查询条件
                condition.put("SSTOCKINNO", true);
                //调用过滤函数
                List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQData, condition);
                for (Map<String, Object> oneData : getQHeader) {
                    DCP_SStockInQueryRes.level1Elm oneLv1 = res.new level1Elm();
//                    oneLv1.setDatas(new ArrayList<>());
                    //取出第一层
                    String sStockInNO = oneData.get("SSTOCKINNO").toString();


//                    for (Map<String, Object> oneData2 : getQData) {
//                        //过滤属于此单头的明细
//                        if (docNo.equals(oneData2.get("DOCNO").toString())) {
//                            DCP_SStockInQueryRes.level2Elm oneLv2 = res.new level2Elm();
//                            String listImage = oneData2.get("LISTIMAGE").toString();
//                            if (!Check.Null(listImage)) {
//                                listImage = domainName + listImage;
//                            }
//                            String shelfLife = oneData2.get("SHELFLIFE").toString();
//                            if (Check.Null(shelfLife)) {
//                                shelfLife = "0";
//                            }
//                            String stockInValidDay = oneData2.get("STOCKINVALIDDAY").toString();
//                            if (Check.Null(stockInValidDay)) {
//                                stockInValidDay = "0";
//                            }
//                            //供应商进货允收方式  0.不管控  1.依进货效期管控
//                            if (Check.Null(stockInAllowType) || stockInAllowType.equals("0")) {
//                                stockInValidDay = "0"; //进货效期==0 不管控
//                            }
//
//                            String unitRatio = oneData2.get("UNIT_RATIO").toString();
//                            if (Double.parseDouble(unitRatio) == 0) {
//                                unitRatio = "1";
//                            }
//                            String pqty = oneData2.get("PQTY").toString();
//                            String baseQty = oneData2.get("BASEQTY").toString();
//                            String stockInQty = oneData2.get("STOCKIN_QTY").toString();
//                            if (Check.Null(stockInQty)) {
//                                stockInQty = "0";
//                            }
//                            //采购收货单的入库数量=发货数量 - 减去已收货数量  BY JZMA 2019-7-9
//                            if (Check.Null(sStockInNO) && PosPub.isNumericType(stockInQty)) {
//                                BigDecimal stockInQty_B = new BigDecimal(stockInQty);
//                                BigDecimal pqty_B = new BigDecimal(pqty);
//                                pqty_B = pqty_B.subtract(stockInQty_B);
//                                if (pqty_B.compareTo(BigDecimal.ZERO) < 0) {
//                                    pqty_B = new BigDecimal("0");
//                                }
//                                pqty = pqty_B.toString();
//                                BigDecimal baseQty_B = pqty_B.multiply(new BigDecimal(unitRatio));
//                                baseQty = baseQty_B.toString();
//                            }
//
//                            String procRate = oneData2.get("PROC_RATE").toString();
//                            if (Check.Null(procRate)) {
//                                procRate = "0";
//                            }
//                            String retWqty = oneData2.get("RETWQTY").toString();
//                            if (Check.Null(retWqty)) {
//                                retWqty = "0";
//                            }
//                            BigDecimal retWqty_b = new BigDecimal(retWqty).divide(new BigDecimal(unitRatio), 7, RoundingMode.HALF_UP);
//
//                            //单身赋值
//                            oneLv2.setItem(oneData2.get("ITEM").toString());
//                            oneLv2.setPluNo(oneData2.get("PLUNO").toString());
//                            oneLv2.setPluName(oneData2.get("PLU_NAME").toString());
//                            oneLv2.setFeatureNo(oneData2.get("FEATURENO").toString());
//                            oneLv2.setFeatureName(oneData2.get("FEATURENAME").toString());
//                            oneLv2.setSpec(oneData2.get("SPEC").toString());
//                            oneLv2.setListImage(listImage);
//                            oneLv2.setPunit(oneData2.get("PUNIT").toString());
//                            oneLv2.setRoutunit(oneData2.get("PUNIT").toString());
//                            oneLv2.setPunitName(oneData2.get("PUNITNAME").toString());
//                            oneLv2.setRoutunitName(oneData2.get("PUNITNAME").toString());
//                            oneLv2.setPqty(pqty);
//                            oneLv2.setRoutqty(retWqty_b.toPlainString());
//                            oneLv2.setPrice(oneData2.get("PRICE").toString());
//                            oneLv2.setAmt(oneData2.get("AMT").toString());
//                            oneLv2.setDistriPrice(oneData2.get("DISTRIPRICE").toString());
//                            oneLv2.setDistriAmt(oneData2.get("DISTRIAMT").toString());
//                            oneLv2.setPoQty(oneData2.get("POQTY").toString());
//                            oneLv2.setReceivingQty(oneData2.get("RECEIVING_QTY").toString());
//                            oneLv2.setWarehouse(oneData2.get("WAREHOUSE_DETAIL").toString());
//                            oneLv2.setOItem(oneData2.get("OITEM").toString());
//                            oneLv2.setPluMemo(oneData2.get("PLU_MEMO").toString());
//                            oneLv2.setStockInQty(stockInQty);
//                            oneLv2.setBatchNo(oneData2.get("BATCH_NO").toString());
//                            oneLv2.setProdDate(oneData2.get("PROD_DATE").toString());
//                            oneLv2.setIsBatch(oneData2.get("ISBATCH").toString());
//                            oneLv2.setBaseUnit(oneData2.get("BASEUNIT").toString());
//                            oneLv2.setBaseUnitName(oneData2.get("BASEUNITNAME").toString());
//                            oneLv2.setUnitRatio(unitRatio);
//                            oneLv2.setStockInValidDay(stockInValidDay);
//                            oneLv2.setShelfLife(shelfLife);
//                            oneLv2.setProcRate(procRate);
//                            oneLv2.setPunitUdLength(oneData2.get("PUNITUDLENGTH").toString());
//                            oneLv2.setBaseQty(baseQty);
//                            oneLv2.setBaseUnitUdLength(oneData2.get("BASEUNITUDLENGTH").toString());
//                            //添加单身
//                            oneLv1.getDatas().add(oneLv2);
//                        }
//                    }
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
                    oneLv1.setRDate(oneData.get("RDATE").toString());
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
                    oneLv1.setInvoiceName(StringUtils.toString(oneData.get("INVOICENAME"), ""));
                    oneLv1.setCurrency(StringUtils.toString(oneData.get("CURRENCY"), ""));
                    oneLv1.setCurrencyName(StringUtils.toString(oneData.get("CURRENCYNAME"), ""));
                    oneLv1.setStockInType(StringUtils.toString(oneData.get("STOCKINTYPE"), ""));
                    oneLv1.setCustomer(StringUtils.toString(oneData.get("CUSTOMER"), ""));
                    oneLv1.setCustomerName(StringUtils.toString(oneData.get("CUSTOMERNAME"), ""));
                    oneLv1.setTotDistriTaxAmt(StringUtils.toString(oneData.get("TOTDISTRITAXAMT"), ""));
                    oneLv1.setTotDistriPreTaxAmt(StringUtils.toString(oneData.get("TOTDISTRIPRETAXAMT"), ""));
                    oneLv1.setTotDistriAmt(StringUtils.toString(oneData.get("TOT_DISTRIAMT"), ""));
                    oneLv1.setOrderOrgNo(StringUtils.toString(oneData.get("ORDERNOORG"), ""));
                    oneLv1.setOrderOrgName(StringUtils.toString(oneData.get("ORDERNOORGNAME"), ""));
                    oneLv1.setOrderNo(StringUtils.toString(oneData.get("ORGIGINNO"), ""));
                    oneLv1.setExpireDate(StringUtils.toString(oneData.get("EXPIREDATE"), ""));
                    oneLv1.setOoType(StringUtils.toString(oneData.get("OOTYPE"), ""));
                    oneLv1.setOofNo(StringUtils.toString(oneData.get("OOFNO"), ""));
                    oneLv1.setOriginNo(StringUtils.toString(oneData.get("ORGIGINNO"), ""));
                    oneLv1.setReturnType(StringUtils.toString(oneData.get("RETURNTYPE"), ""));
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
                    oneLv1.setTotAmount(oneData.get("TOT_AMT").toString());
                    oneLv1.setTotPreTaxAmt(oneData.get("TOTPRETAXAMT").toString());
                    oneLv1.setTotTaxAmt(oneData.get("TOTTAXAMT").toString());

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

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            return res;

        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_SStockInQueryReq req) throws Exception {

        StringBuilder querySql = new StringBuilder();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();
        String supplier = req.getRequest().getSupplier();
        String status = req.getRequest().getStatus();
        String keyTxt = req.getRequest().getKeyTxt();
        //计算起始位置
        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * pageSize);
        querySql.append(" SELECT * FROM( ");

        querySql.append("SELECT row_number() OVER (order by a.BDATE DESC, a.SSTOCKINNO DESC) AS RN,COUNT(*) OVER ( ) NUM,A.*  ")
                .append(" ,a.WAREHOUSE as WAREHOUSE_MAIN, wl1.WAREHOUSE_NAME as WAREHOUSE_MAIN_NAME " +
                        " ,b1.op_NAME as createname  " +
                        " ,b2.op_NAME as modifyname  " +
                        " ,b3.op_NAME as submitname  " +
                        " ,b4.op_NAME as confirmname " +
                        " ,b5.op_NAME as cancelname " +
                        " ,b6.op_NAME as accountname " +
                        " ,b7.NAME as EMPLOYEENAME " +
                        " ,f.PTEMPLATE_NAME, " +
                        "  bz1.sname                                         SUPPLIER_NAME, " +
                        "  dd0.DEPARTNAME, " +
                        "  bl1.name                                       as BILLDATENAME, " +
                        "  bl2.name                                          PAYDATENAME, " +
                        "  it1.INVOICE_NAME, " +
                        "  h.NAME                                            CURRENCYNAME, " +
                        "  bz2.sname                                         CUSTOMERNAME, " +
                        "  ol2.ORG_NAME                                      PAYORGNAME, " +
                        "  w1.ISLOCATION," +
                        "  c.DELIVERY_NO,tl2.TAXNAME,c.RDATE " +
                        " ,po.EXPIREDATE,h.sname as payeename,i.sname as payername,j.org_name as corpname,k.org_name as bizorgname,l.org_name as bizcorpname ")
                .append(" from dcp_sstockin a ")
                .append(" left join dcp_ptemplate f on a.eid=f.eid and a.ptemplateno=f.ptemplateno and f.doc_type='3' ")
                .append(" left join dcp_warehouse w1 on a.eid=w1.eid and a.warehouse=w1.warehouse ")
                .append(" left join dcp_warehouse_lang wl1 on a.eid=wl1.eid and a.warehouse=wl1.warehouse and wl1.lang_type='").append(req.getLangType()).append("'")
                .append(" left join dcp_receiving c on c.eid=a.eid and c.shopid=a.shopid and c.receivingno=a.receivingno ")
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
                .append(" left join dcp_taxcategory_lang tl2 on a.eid=tl2.eid and a.taxcode=tl2.taxcode and tl2.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_CURRENCY_LANG h ON h.eid = a.eid AND h.CURRENCY = a.CURRENCY AND nation = 'CN' AND h.lang_type = '").append(req.getLangType()).append("'")
                .append(" LEFT JOIN dcp_org_lang ol3 ON ol3.eid = a.eid AND ol3.ORGANIZATIONNO = a.ORGANIZATIONNO AND ol3.lang_type = '").append(req.getLangType()).append("'")
                .append(" LEFT JOIN dcp_org_lang ol2 ON ol2.eid = a.eid AND ol2.ORGANIZATIONNO = a.PAYORGNO AND ol2.lang_type = '").append(req.getLangType()).append("'")
                .append(" left join dcp_receiving re on re.eid=a.eid and a.RECEIVINGNO=re.RECEIVINGNO   ")
                .append(" LEFT JOIN DCP_PURORDER po ON re.EID=po.EID and re.ofNo=po.purorderno")
                .append(" left join dcp_bizpartner h on h.eid=a.eid and h.bizpartnerno=a.payee ")
                .append(" left join dcp_bizpartner i on i.eid=a.eid and i.bizpartnerno=a.payer ")
                .append(" left join dcp_org_lang j on j.eid=a.eid and j.organizationno=a.corp and j.lang_type='"+req.getLangType()+"'")
                .append(" left join dcp_org_lang k on k.eid=a.eid and k.organizationno=a.bizorgno and k.lang_type='"+req.getLangType()+"' ")
                .append(" left join dcp_org_lang l on l.eid=a.eid and l.organizationno=a.bizcorp and l.lang_type='"+req.getLangType()+"' ")
                .append(" WHERE a.EID='").append(req.geteId()).append("'");

        querySql.append(" and a.ORGANIZATIONNO='").append(req.getOrganizationNO()).append("'");
        if (StringUtils.isNotEmpty(status)) {
            querySql.append(" and a.status='").append(status).append("'");
        }

        if (!CollectionUtils.isEmpty(req.getRequest().getStockInType())) {
            querySql.append(" and ( 1=2  ");
            for (String s : req.getRequest().getStockInType()) {
                querySql.append(" OR a.stockintype='").append(s).append("'");
            }
            querySql.append(" ) ");
        }

        if (!Check.Null(supplier)) {
            querySql.append(" and a.supplier='").append(supplier).append("'");
        }

        //bDate单据日期
        //receiptDate预计到货日
        //accountDate记账日期
        if("bDate".equals(req.getRequest().getDateType())){
            if (StringUtils.isNotEmpty(beginDate)) {
                querySql.append(" and a.BDATE >=").append(beginDate);
            }

            if (StringUtils.isNotEmpty(endDate)) {
                querySql.append(" and a.BDATE <=").append(endDate);
            }
        }else if("receiptDate".equals(req.getRequest().getDateType()))
        {
            if (StringUtils.isNotEmpty(beginDate)) {
                querySql.append(" and a.RECEIPTDATE >=").append(beginDate);
            }

            if (StringUtils.isNotEmpty(endDate)) {
                querySql.append(" and a.RECEIPTDATE <=").append(endDate);
            }
        }else if ("accountDate".equals(req.getRequest().getDateType())){
            if (StringUtils.isNotEmpty(beginDate)) {
                querySql.append(" and a.account_Date >=").append(beginDate);
            }

            if (StringUtils.isNotEmpty(endDate)) {
                querySql.append(" and a.account_Date <=").append(endDate);
            }
        }



        if (StringUtils.isNotEmpty(keyTxt)) {
//            单号sStockInNo/来源单号ofNo/业务订单号originNo
//            /供应商编号supplier/供应商名称/客户编号customer/客户名称模糊搜索
            querySql.append(" and ( a.SSTOCKINNO like '%%").append(keyTxt).append("%%'  ")
                    .append(" or a.OFNO like '%%").append(keyTxt).append("%%'  ")
                    .append(" or a.ORGIGINNO like '%%").append(keyTxt).append("%%'  ")
                    .append(" or a.SUPPLIER like '%%").append(keyTxt).append("%%'  ")
                    .append(" or bz1.sname like '%%").append(keyTxt).append("%%'  ")
                    .append(" or a.CUSTOMER like '%%").append(keyTxt).append("%%'  ")
                    .append(" or bz2.sname like '%%").append(keyTxt).append("%%'  ")
                    .append(") ");
        }

        querySql.append("  )  a ")
                .append("    WHERE   rn> ").append(startRow).append(" and rn<= ").append(startRow + pageSize);

        querySql.append(" ORDER BY a.BDATE DESC, a.SSTOCKINNO DESC");

        return querySql.toString();


    }

    protected String getQueryDocSql(DCP_SStockInQueryReq req, String isBatchSStockIn) {
        //查询条件
        String eId = req.geteId();
        String shopId = req.getShopId();
        //2018-11-09 添加beginDate 和 endDate
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();
        String supplier = req.getRequest().getSupplier();
        String status = req.getRequest().getStatus();
        String keyTxt = req.getRequest().getKeyTxt();
//        String docType = req.getRequest().getDocType();  //采购入库单： 1.自采 2.统采 3.门店直供
        StringBuffer sqlbuf = new StringBuffer();
        String langType = req.getLangType();
        //计算起始位置
        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * pageSize);


        sqlbuf.append(""
                + " with doc as("
                + " select * from ("
                + " select count(*) over () as num,row_number() over (order by a.docno desc) as rn,a.*"
                + " from (");

        sqlbuf.append(" select a.sstockinno as docno,'sstockin' as doctype from dcp_sstockin a"
                + " where a.eid='" + eId + "' and a.shopid='" + shopId + "' and a.bdate>='" + beginDate + "' and a.bdate<='" + endDate + "'"
                + " ");
        if (!Check.Null(status)) {
            sqlbuf.append(" and a.status='" + status + "'");
        }

        if (!CollectionUtils.isEmpty(req.getRequest().getStockInType())) {
            sqlbuf.append(" and ( 1=2  ");
            for (String s : req.getRequest().getStockInType()) {
                sqlbuf.append(" OR a.stockintype='" + s + "'");
            }
            sqlbuf.append(" ) ");
        }

        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" "
                    + " and (a.tot_amt like '%" + keyTxt + "%' or a.tot_distriamt like '%" + keyTxt + "%'"
                    + " or a.tot_pqty like '%" + keyTxt + "%' or a.sstockinno like '%" + keyTxt + "%'"
                    + " or a.memo like '%" + keyTxt + "%' or a.supplier like '%" + keyTxt + "%'"
                    + " or a.process_erp_no like '%" + keyTxt + "%' or a.load_docno like '%" + keyTxt + "%'"
                    + " or a.load_receiptno like '%" + keyTxt + "%')"
                    + " ");
        }
        if (!Check.Null(supplier)) {
            sqlbuf.append(" and a.supplier='" + supplier + "'");
        }
        sqlbuf.append(" )a");
        sqlbuf.append(" ) where rn>" + startRow + " and rn<=" + (startRow + pageSize) + ")");
        sqlbuf.append(" "
                + " select a.num,a.eid,a.shopid,a.supplier,a.docno,a.memo,a.status,a.ptemplateno,a.bdate,"
                + " a.doc_type,a.load_doctype,a.load_docno,a.load_receiptno,a.delivery_no,a.receiving_rdate,"
                + " a.tot_cqty,a.tot_pqty,a.tot_amt,a.tot_distriamt,"
                + " a.warehouse_main,i.warehouse_name as warehouse_main_name,"
                + " a.update_time,a.process_erp_no,a.sstockinno,a.otype,a.ofno,a.buyerno,a.buyername,"
                + " a.process_status,a.taxcode,a.taxname,"
                + " a.createby,a.create_time,a.create_date,b1.op_name as createname,"
                + " a.modifyby,a.modify_date,a.modify_time,b2.op_name as modifyname,"
                + " a.submitby,a.submit_date,a.submit_time,b3.op_name as submitname,"
                + " a.confirmby,a.confirm_date,a.confirm_time,b4.op_name as confirmname,"
                + " a.cancelby,a.cancel_date,a.cancel_time,b5.op_name as cancelname,"
                + " a.accountby,a.account_date,a.account_time,b6.op_name as accountname,"
                + " a.item,a.pluno,a.featureno,a.pqty,a.baseqty,a.punit,a.baseunit,a.unit_ratio,"
                + " a.price,a.distriprice,a.amt,a.distriamt,"
                + " a.receiving_qty,a.reitem,a.oitem,a.plu_memo,a.poqty,"
                + " a.warehouse_detail,a.stockin_qty,a.proc_rate,a.batch_no,a.prod_date,a.retwqty,a.receiptdate,"
                + " b.isbatch,b.shelflife,b.stockinvalidday,b.stockoutvalidday,"
                + " c.plu_name,fn.featurename,gul.spec,image.listimage,"
                + " '0' stockinallowtype,'0' stockoutallowtype,"
                + " ' ' abbr,d.SNAME supplier_name,"
                + " f.ptemplate_name,"
                + " g1.uname as punitname,g2.uname as baseunitname,"
                + " h.udlength as punitudlength,bul.udlength as baseunitudlength, "
                + " a.EMPLOYEEID,b7.op_name as EMPLOYEENAME, "
                + " a.CUSTOMER,a.DEPARTID,a.PAYTYPE,a.PAYORGNO, "
                + " a.BILLDATENO,a.PAYDATENO,a.INVOICECODE,a.CURRENCY,a.RECEIVINGNO, "
                + " a.STOCKINTYPE,a.OOTYPE,a.OOFNO,a.ORGIGINNO,a.RETURNTYPE "
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
                + " b.retwqty,c.receiptdate,"
                + " a.EMPLOYEEID,a.CUSTOMER,a.DEPARTID,a.PAYTYPE,a.PAYORGNO, "
                + " a.BILLDATENO,a.PAYDATENO,a.INVOICECODE,a.CURRENCY,a.RECEIVINGNO, "
                + " a.STOCKINTYPE,a.OOTYPE,a.OOFNO,a.ORGIGINNO,a.RETURNTYPE "
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
                + " left  join DCP_BIZPARTNER d on a.eid=d.eid and a.supplier=d.BIZPARTNERNO"
                + " left  join dcp_ptemplate f on a.eid=f.eid and a.ptemplateno=f.ptemplateno and f.doc_type='3'"
                + " left  join dcp_unit_lang g1 on a.eid=g1.eid and a.punit=g1.unit and g1.lang_type='" + langType + "'"
                + " left  join dcp_unit_lang g2 on a.eid=g2.eid and a.baseunit=g2.unit and g2.lang_type='" + langType + "'"
                + " left  join dcp_unit h on a.eid=h.eid and a.punit=h.unit"
                + " left  join dcp_unit bul on a.eid=bul.eid and a.baseunit=bul.unit"
                + " left  join dcp_warehouse_lang i on a.eid=i.eid and a.shopid=i.organizationno and a.warehouse_main=i.warehouse and i.lang_type='" + langType + "'"
                + " left  join dcp_goods_feature_lang fn on a.eid=fn.eid and a.pluno=fn.pluno and a.featureno=fn.featureno and fn.lang_type='" + langType + "'"
                + " left  join dcp_goods_unit_lang gul on a.eid=gul.eid and a.pluno=gul.pluno and a.punit=gul.ounit and gul.lang_type='" + langType + "'"
                + " left  join dcp_goodsimage image on image.eid=a.eid and image.pluno=a.pluno and image.apptype='ALL'"
                + " left  join platform_staffs_lang b1 on a.eid=b1.eid and a.createby=b1.opno and b1.lang_type='" + langType + "'"
                + " left  join platform_staffs_lang b2 on a.eid=b2.eid and a.modifyby=b2.opno and b2.lang_type='" + langType + "'"
                + " left  join platform_staffs_lang b3 on a.eid=b3.eid and a.submitby=b3.opno and b3.lang_type='" + langType + "'"
                + " left  join platform_staffs_lang b4 on a.eid=b4.eid and a.confirmby=b4.opno and b4.lang_type='" + langType + "'"
                + " left  join platform_staffs_lang b5 on a.eid=b5.eid and a.cancelby=b5.opno and b5.lang_type='" + langType + "'"
                + " left  join platform_staffs_lang b6 on a.eid=b6.eid and a.accountby=b6.opno and b6.lang_type='" + langType + "'"
                + " left  join platform_staffs_lang b7 on a.eid=b7.eid and a.EMPLOYEEID=b7.opno and b7.lang_type='" + langType + "'"
                + " order by a.sorttype,a.status,a.docno desc,a.item"
                + "");

        return sqlbuf.toString();
    }


    protected String getQueryOldSql(DCP_SStockInQueryReq req) {
        StringBuffer sqlbuf = new StringBuffer();
        String eId = req.geteId();
        String shopId = req.getShopId();
        levelElm request = req.getRequest();
//        String docType = request.getDocType();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String status = request.getStatus();
        String keyTxt = request.getKeyTxt();
        String supplier = request.getSupplier();

        sqlbuf.append(""
                + "SELECT num "
                + " from ("
                + " SELECT count(*) as num  from ("
                + " SELECT distinct dno FROM  ( "
                + " select a.RECEIVINGNO as dno  from DCP_receiving a "
                + " INNER JOIN DCP_RECEIVING_DETAIL b ON A.EID=b.EID  AND A.SHOPID=b.SHOPID AND A.RECEIVINGNO=b.RECEIVINGNO and a.BDATE=b.BDATE "
                + " where  a.SHOPID='" + shopId + "' and a.EID='" + eId + "'"
                + " AND A.RECEIVINGNO NOT IN ("
                + " SELECT OFNO FROM DCP_sstockin WHERE  SHOPID='" + shopId + "' and EID='" + eId + "' and ofno is not null) "
        );

        //2018-11-09 添加 日期查询条件

        //待收货的不加日期判断，全部都要显示 BY JZMA 20200327
        //sqlbuf.append(" and a.BDATE between "+beginDate +" and "+endDate+" ");

        sqlbuf.append(" and A.doc_type='2' ");
        if (status != null && status.length() > 0) {
            sqlbuf.append(" and a.STATUS='" + status + "'");
        } else {
            sqlbuf.append(" AND a.status <> '8'  "); //by jzma 20190610 过滤已作废的收货通知单 (8-已作废)
        }

        if (keyTxt != null && keyTxt.length() > 0) {
            sqlbuf.append(" and (a.TOT_AMT LIKE '%%" + keyTxt + "%%' OR a.TOT_DISTRIAMT LIKE '%%" + keyTxt + "%%' "
                    + " OR a.TOT_PQTY LIKE '%%" + keyTxt + "%%' OR a.RECEIVINGNO LIKE '%%" + keyTxt + "%%' "
                    + " OR a.MEMO LIKE '%%" + keyTxt + "%%' OR a.SUPPLIER LIKE '%%" + keyTxt + "%%' "
                    + " OR a.LOAD_DOCNO like '%%" + keyTxt + "%%' OR a.LOAD_ReceiptNO like '%%" + keyTxt + "%%'  )");
        }
        if (supplier != null && supplier.length() > 0) {
            sqlbuf.append(" and a.SUPPLIER='" + supplier + "'");
        }

        sqlbuf.append(" union all "
                + " select a.Sstockinno as dno  "
                + " FROM DCP_SSTOCKIN A "
                + " INNER JOIN DCP_SSTOCKIN_DETAIL b on a.EID=b.EID and a.sstockinno=b.sstockinno and a.BDATE=b.BDATE " +
                " INNER JOIN DCP_GOODS c ON a.EID = c.EID AND b.pluno = c.pluno "
        );

        //2018-11-09 添加 日期查询条件
        sqlbuf.append(" WHERE a.BDATE between " + beginDate + " and " + endDate + " ");

        if (status != null && status.length() > 0) {
            sqlbuf.append(" and a.STATUS='" + status + "'");
        } else {
            sqlbuf.append(" AND a.status <> '8'  ");  //by jzma 20190610 过滤已作废的收货通知单 (8-已作废)
        }

//        if (docType != null && docType.length() > 0) {
//            sqlbuf.append(" and A.doc_type>'" + docType + "'");
//        }

        if (keyTxt != null && keyTxt.length() > 0) {
            sqlbuf.append(" and (a.TOT_AMT LIKE '%%" + keyTxt + "%%' OR a.TOT_DISTRIAMT LIKE '%%" + keyTxt + "%%' "
                    + " OR a.TOT_PQTY LIKE '%%" + keyTxt + "%%' OR a.SSTOCKINNO LIKE '%%" + keyTxt + "%%' "
                    + " OR a.MEMO LIKE '%%" + keyTxt + "%%' OR a.SUPPLIER LIKE '%%" + keyTxt + "%%' "
                    + " or a.process_ERP_No like '%%" + keyTxt + "%%' OR a.LOAD_DOCNO like '%%" + keyTxt + "%%' "
                    + " OR a.LOAD_ReceiptNO like '%%" + keyTxt + "%%'  )");
        }
        if (supplier != null && supplier.length() > 0) {
            sqlbuf.append(" and a.SUPPLIER='" + supplier + "'");
        }
        sqlbuf.append(" and a.SHOPID='" + shopId + "'");
        sqlbuf.append(" and a.EID='" + eId + "'");
        sqlbuf.append(" )))  ");

        return sqlbuf.toString();
    }

}
