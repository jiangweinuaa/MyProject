package com.dsc.spos.service.imp.json;


import com.dsc.spos.json.cust.req.DCP_PurReceiveDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_PurReceiveDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_PurReceiveDetailQuery extends SPosBasicService<DCP_PurReceiveDetailQueryReq, DCP_PurReceiveDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_PurReceiveDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PurReceiveDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_PurReceiveDetailQueryReq>() {

        };
    }

    @Override
    protected DCP_PurReceiveDetailQueryRes getResponseType() {
        return new DCP_PurReceiveDetailQueryRes();
    }

    @Override
    protected DCP_PurReceiveDetailQueryRes processJson(DCP_PurReceiveDetailQueryReq req) throws Exception {
        DCP_PurReceiveDetailQueryRes res = this.getResponseType();
        int totalRecords = 0;        //总笔数
        int totalPages = 0;

        res.setDatas(new ArrayList<>());

        List<Map<String, Object>> queryData = this.doQueryData(getQuerySql(req), null);
        List<Map<String, Object>> detailData = this.doQueryData(getQueryDetailSql(req), null);

        if (CollectionUtils.isNotEmpty(queryData) && CollectionUtils.isNotEmpty(detailData)) {
            String num = queryData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            //算總頁數
//            totalPages = totalRecords / req.getPageSize();
//            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            for (Map<String, Object> oneData : queryData) {
                DCP_PurReceiveDetailQueryRes.Data data = res.new Data();

                data.setStatus(oneData.get("STATUS").toString());
                data.setBillNo(oneData.get("BILLNO").toString());
                data.setOrgNo(oneData.get("ORGNO").toString());
                data.setOrgName(oneData.get("ORGNAME").toString());
                data.setBDate(oneData.get("BDATE").toString());
                data.setSupplier(oneData.get("SUPPLIER").toString());
                data.setSupplierName(StringUtils.toString(oneData.get("SUPPLIERNAME"), ""));
                data.setPayType(StringUtils.toString(oneData.get("PAYTYPE"), ""));
                data.setPayOrgNo(StringUtils.toString(oneData.get("PAYORGNO"), ""));
                data.setPayOrgName(StringUtils.toString(oneData.get("PAYORGNAME"), ""));
                data.setBillDateNo(StringUtils.toString(oneData.get("BILLDATENO"), ""));
                data.setBillDateDesc(StringUtils.toString(oneData.get("BILLDATEDESC"), ""));
                data.setPayDateNo(StringUtils.toString(oneData.get("PAYDATENO"), ""));
                data.setPayDateDesc(StringUtils.toString(oneData.get("PAYDATEDESC"), ""));
                data.setInvoiceCode(StringUtils.toString(oneData.get("INVOICECODE"), ""));
                data.setInvoiceName(StringUtils.toString(oneData.get("INVOICENAME"), ""));
                data.setCurrency(StringUtils.toString(oneData.get("CURRENCY"), ""));
                data.setCurrencyName(StringUtils.toString(oneData.get("CURRENCYNAME"), ""));
                data.setPurOrgNo(StringUtils.toString(oneData.get("PURORGNO"), ""));
                data.setPurOrgName(StringUtils.toString(oneData.get("PURORGNAME"), ""));
                data.setPurOrderNo(StringUtils.toString(oneData.get("PURORDERNO"), ""));
                data.setPayee(StringUtils.toString(oneData.get("PAYEE"), ""));
                data.setPayeeName(StringUtils.toString(oneData.get("PAYEENAME"), ""));
                data.setCorp(oneData.get("CORP").toString());
                data.setCorpName(oneData.get("CORPNAME").toString());
                data.setBizOrgNo(oneData.get("BIZORGNO").toString());
                data.setBizOrgName(oneData.get("BIZORGNAME").toString());
                data.setBizCorp(oneData.get("BIZCORP").toString());
                data.setBizCorpName(oneData.get("BIZCORPNAME").toString());
                data.setTotReceiveAmt(oneData.get("TOTRECEIVEAMT").toString());
                data.setTotSupAmt(oneData.get("TOTSUPAMT").toString());

                String querySql = String.format(" SELECT * FROM DCP_PURORDER WHERE EID='%s' AND PURORDERNO='%s' ", req.geteId(), StringUtils.toString(oneData.get("PURORDERNO"), ""));
                List<Map<String, Object>> purOrderInfo = this.doQueryData(querySql, new String[0]);
                if (CollectionUtils.isNotEmpty(purOrderInfo)) {
                    Map<String, Object> oneInfo = purOrderInfo.get(0);
                    data.setExpireDate(StringUtils.toString(oneInfo.get("EXPIREDATE"), ""));
                }

                data.setReceivingNo(StringUtils.toString(oneData.get("RECEIVINGNO"), ""));

                querySql = String.format(" SELECT a.RECEIPTDATE,a.RDATE,a.WAREHOUSE," +
                        " f.warehouse_name as WAREHOUSENAME,e.ISLOCATION,a.TOT_CQTY,b.POQTY, " +
                        " b.ITEM,b.RECEIVINGNO,nvl(b.STOCKIN_QTY,0) as STOCKIN_QTY,b.PQTY " +
                        " FROM DCP_RECEIVING a " +
                        " LEFT JOIN DCP_RECEIVING_DETAIL b ON a.RECEIVINGNO=b.RECEIVINGNO AND a.SHOPID=b.SHOPID and a.eid=b.eid and a.ORGANIZATIONNO=b.ORGANIZATIONNO " +
                        " LEFT JOIN DCP_WAREHOUSE e on e.EID=a.EID and a.WAREHOUSE=e.WAREHOUSE " +
                        " left join dcp_warehouse_lang f on f.eid=a.eid and f.warehouse=a.warehouse and f.lang_type='" + req.getLangType() + "' " +
                        " WHERE a.EID='%s' AND a.RECEIVINGNO='%s' ", req.geteId(), StringUtils.toString(oneData.get("RECEIVINGNO"), ""));
                List<Map<String, Object>> receivingInfo = this.doQueryData(querySql, null);
                if (CollectionUtils.isNotEmpty(receivingInfo)) {
                    Map<String, Object> oneInfo = receivingInfo.get(0);
                    data.setReceiptDate(StringUtils.toString(oneInfo.get("RECEIPTDATE"), ""));
                    //data.setWareHouse(StringUtils.toString(oneInfo.get("WAREHOUSE"), ""));
                    //data.setWareHouseName(StringUtils.toString(oneInfo.get("WAREHOUSENAME"), ""));
                    //data.setIsLocation(StringUtils.toString(oneInfo.get("ISLOCATION"), ""));

                }

                data.setWareHouse(StringUtils.toString(oneData.get("WAREHOUSE"), ""));
                data.setWareHouseName(StringUtils.toString(oneData.get("WAREHOUSENAME"), ""));
                data.setIsLocation(StringUtils.toString(oneData.get("ISLOCATION"), ""));


                data.setTotCqty(StringUtils.toString(oneData.get("TOTCQTY"), ""));
                data.setTotPqty(StringUtils.toString(oneData.get("TOTPQTY"), ""));

                data.setPurType(StringUtils.toString(oneData.get("PURTYPE"), ""));
                data.setEmployeeID(StringUtils.toString(oneData.get("EMPLOYEEID"), ""));
                data.setEmployeeName(StringUtils.toString(oneData.get("EMPLOYEENAME"), ""));
                data.setDepartID(StringUtils.toString(oneData.get("DEPARTID"), ""));
                data.setDepartName(StringUtils.toString(oneData.get("DEPARTNAME"), ""));
                data.setOwnOpId(StringUtils.toString(oneData.get("OWNOPID"), ""));
                data.setOwnOpName(StringUtils.toString(oneData.get("OWNOPNAME"), ""));
                data.setOwnDeptId(StringUtils.toString(oneData.get("OWNDEPTID"), ""));
                data.setOwnDeptName(StringUtils.toString(oneData.get("OWNDEPTNAME"), ""));
                data.setCreateBy(StringUtils.toString(oneData.get("CREATEOPID"), ""));
                data.setCreateByName(StringUtils.toString(oneData.get("CREATEBYNAME"), ""));
                data.setCreateDateTime(StringUtils.toString(oneData.get("CREATETIME"), ""));
                data.setModifyBy(StringUtils.toString(oneData.get("LASTMODIOPID"), ""));
                data.setModifyByName(StringUtils.toString(oneData.get("MODIFYBYNAME"), ""));
                data.setModifyDateTime(StringUtils.toString(oneData.get("LASTMODITIME"), ""));
                data.setConfirmBy(StringUtils.toString(oneData.get("CONFIRMBY"), ""));
                data.setConfirmByName(StringUtils.toString(oneData.get("CONFIRMBYNAME"), ""));
                data.setConfirmDateTime(StringUtils.toString(oneData.get("CONFIRMTIME"), ""));
                data.setCancelBy(StringUtils.toString(oneData.get("CANCELBY"), ""));
                data.setCancelByName(StringUtils.toString(oneData.get("CANCELBYNAME"), ""));
                data.setCancelDateTime(StringUtils.toString(oneData.get("CANCELTIME"), ""));
                data.setMemo(StringUtils.toString(oneData.get("MEMO"), ""));

                data.setTaxPayerType(oneData.get("TAXPAYER_TYPE").toString());
                data.setInputTaxCode(oneData.get("INPUT_TAXCODE").toString());
                data.setInputTaxRate(oneData.get("INPUT_TAXRATE").toString());
                data.setDetail(new ArrayList<>());


                double totPurAmt = 0.0;
                double totStockInQty = 0.0;
                double totCanStockInQty = 0.0;
                int totCanStockInCQty = 0;

                int qcStatus = 0;
                int dQcStatus = 0;

                if (CollectionUtils.isNotEmpty(detailData)) {
                    for (Map<String, Object> oneDetail : detailData) {

                        DCP_PurReceiveDetailQueryRes.Detail detail = res.new Detail();

                        Map<String, Object> condiV = Maps.newHashMap();
                        condiV.put("ITEM", oneDetail.get("RITEM"));
                        List<Map<String, Object>> detailReceive = MapDistinct.getWhereMap(receivingInfo, condiV, true);
                        if (CollectionUtils.isNotEmpty(detailReceive)) {
                            Map<String, Object> rDetail = detailReceive.get(0);

                            double canReceiveQty = Double.parseDouble(rDetail.get("PQTY").toString()) - Double.parseDouble(rDetail.get("STOCKIN_QTY").toString());
                            detail.setCanReceiveQty(StringUtils.toString(canReceiveQty, "0"));
                        }

                        detail.setItem(StringUtils.toString(oneDetail.get("ITEM"), ""));
                        detail.setReceivingNo(StringUtils.toString(oneDetail.get("RECEIVINGNO"), ""));
                        detail.setRItem(StringUtils.toString(oneDetail.get("RITEM"), ""));
                        detail.setPurOrderNo(StringUtils.toString(oneDetail.get("PURORDERNO"), ""));
                        detail.setPoItem(StringUtils.toString(oneDetail.get("POITEM"), ""));
                        detail.setPoItem2(StringUtils.toString(oneDetail.get("POITEM2"), ""));
                        detail.setPluNo(StringUtils.toString(oneDetail.get("PLUNO"), ""));
                        detail.setPluName(StringUtils.toString(oneDetail.get("PLUNAME"), ""));
                        detail.setPluBarcode(StringUtils.toString(oneDetail.get("PLUBARCODE"), ""));
                        detail.setSpec(StringUtils.toString(oneDetail.get("SPEC"), ""));
                        detail.setFeatureNo(StringUtils.toString(oneDetail.get("FEATURENO"), ""));
                        detail.setFeatureName(StringUtils.toString(oneDetail.get("FEATURENAME"), ""));
                        detail.setIsBatch(StringUtils.toString(oneDetail.get("ISBATCH"), ""));
                        detail.setCategory(StringUtils.toString(oneDetail.get("CATEGORY"), ""));
                        detail.setCategoryName(StringUtils.toString(oneDetail.get("CATEGORYNAME"), ""));
                        detail.setWareHouse(StringUtils.toString(oneDetail.get("WAREHOUSE"), ""));
                        detail.setWareHouseName(StringUtils.toString(oneDetail.get("WAREHOUSENAME"), ""));
                        detail.setPUnit(StringUtils.toString(oneDetail.get("PUNIT"), ""));
                        detail.setPUnitName(StringUtils.toString(oneDetail.get("PUNITNAME"), ""));
                        detail.setPQty(StringUtils.toString(oneDetail.get("PQTY"), ""));
                        detail.setBaseUnit(StringUtils.toString(oneDetail.get("BASEUNIT"), ""));
                        detail.setBaseUnitName(StringUtils.toString(oneDetail.get("BASEUNITNAME"), ""));
                        detail.setPurPrice(StringUtils.toString(oneDetail.get("PURPRICE"), ""));
                        detail.setIsGift(StringUtils.toString(oneDetail.get("ISGIFT"), ""));
                        detail.setReceivePrice(oneDetail.get("RECEIVEPRICE").toString());
                        detail.setReceiveAmt(oneDetail.get("RECEIVEAMT").toString());
                        detail.setSupPrice(oneDetail.get("SUPPRICE").toString());
                        detail.setSupAmt(oneDetail.get("SUPAMT").toString());
                        if (null == oneDetail.get("QCSTATUS")) {
                            qcStatus = 0;
                        } else {
                            dQcStatus = Integer.parseInt(oneDetail.get("QCSTATUS").toString());
                        }

                        if (1 == dQcStatus || qcStatus == 1) {
                            qcStatus = 1;
                        } else if (2 == dQcStatus ) {
                            qcStatus = 2;
                        } else {
                            qcStatus = 0;
                        }

                        detail.setQcStatus(StringUtils.toString(oneDetail.get("QCSTATUS"), "0"));
                        detail.setBatchNo(StringUtils.toString(oneDetail.get("BATCHNO"), ""));
                        detail.setProdDate(StringUtils.toString(oneDetail.get("PRODDATE"), ""));
                        detail.setExpDate(StringUtils.toString(oneDetail.get("EXPDATE"), ""));
                        double passQty = Double.parseDouble(StringUtils.toString(oneDetail.get("PASSQTY"), "0"));
                        double stockInQty = Double.parseDouble(StringUtils.toString(oneDetail.get("STOCKINQTY"), "0"));
                        totStockInQty += stockInQty;
                        totCanStockInQty += (passQty - stockInQty);
                        if (passQty - stockInQty > 0) {
                            totCanStockInCQty++;
                        }
                        detail.setPassQty(String.valueOf(passQty));
                        detail.setStockInQty(String.valueOf(stockInQty));
                        detail.setRejectQty(StringUtils.toString(oneDetail.get("REJECTQTY"), ""));
                        detail.setCanStockInQty(String.valueOf(passQty - stockInQty));
                        detail.setPurTemplateNo(StringUtils.toString(oneDetail.get("PURTEMPLATENO"), ""));
                        detail.setMemo(StringUtils.toString(oneDetail.get("MEMO"), ""));
                        detail.setShelfLife(StringUtils.toString(oneDetail.get("SHELFLIFE"), ""));
                        detail.setBaseQty(StringUtils.toString(oneDetail.get("BASEQTY"), ""));
                        detail.setUnitRatio(StringUtils.toString(oneDetail.get("UNITRATIO"), ""));
                        detail.setTaxCode(StringUtils.toString(oneDetail.get("TAXCODE"), ""));
                        detail.setTaxName(StringUtils.toString(oneDetail.get("TAXNAME"), ""));
                        detail.setTaxRate(StringUtils.toString(oneDetail.get("TAXRATE"), ""));
                        detail.setInclTax(StringUtils.toString(oneDetail.get("INCLTAX"), ""));
                        double puramt = Double.parseDouble(StringUtils.toString(oneDetail.get("PURAMT"), "0"));
                        detail.setPurAmt(String.valueOf(puramt));
                        detail.setTaxCalType(StringUtils.toString(oneDetail.get("TAXCALTYPE"), ""));
                        totPurAmt += puramt;

                        detail.setProcRate(oneDetail.get("PROCRATE").toString());


                        data.getDetail().add(detail);
                    }

                    data.setQcStatus(String.valueOf(qcStatus));

                    data.setTotStockInQty(String.valueOf(totStockInQty));
                    data.setTotPurAmt(String.valueOf(totPurAmt));
                    data.setTotCanStockInQty(String.valueOf(totCanStockInQty));
                    data.setTotCanStockInCqty(String.valueOf(totCanStockInCQty));

                }
                res.getDatas().add(data);
            }
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);


        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    protected String getQueryDetailSql(DCP_PurReceiveDetailQueryReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append("SELECT b.*, " +
                "  b.RECEIVINGITEM RITEM," +
                "  d.PLU_NAME PLUNAME," +
                "  c.MAINBARCODE PLUBARCODE," +
                "  gul.SPEC," +
                "  e.FEATURENAME," +
                "  c.ISBATCH," +
                "  c.CATEGORY," +
                "  g.CATEGORY_NAME CATEGORYNAME," +
                "  h.warehouse_name AS warehousename," +
                "  f.UNAME PUNITNAME," +
                "  i.UNAME BASEUNITNAME,c.SHELFLIFE " +
                "  FROM " +
                "  DCP_PURRECEIVE a" +
                "  INNER JOIN DCP_PURRECEIVE_DETAIL b ON a.EID = b.EID AND a.ORGANIZATIONNO = b.ORGANIZATIONNO AND a.BILLNO = b.BILLNO " +
                "  LEFT JOIN DCP_GOODS c ON b.EID = c.EID AND b.PLUNO = c.PLUNO " +
                "  LEFT JOIN DCP_GOODS_UNIT_LANG gul on gul.EID=b.EID and gul.PLUNO=b.PLUNO and gul.OUNIT=b.PUNIT and gul.LANG_TYPE='" + req.getLangType() + "'" +
                "  LEFT JOIN DCP_GOODS_LANG d ON b.EID = d.EID AND b.PLUNO = d.PLUNO AND d.LANG_TYPE = '" + req.getLangType() + "'" +
                "  LEFT JOIN DCP_GOODS_FEATURE_LANG e ON e.EID = b.EID AND e.PLUNO = b.PLUNO AND e.FEATURENO = b.FEATURENO AND e.LANG_TYPE = 'zh_CN'" +
                "  LEFT JOIN DCP_UNIT_LANG f ON f.EID = b.EID AND f.UNIT = b.PUNIT AND f.LANG_TYPE = '" + req.getLangType() + "'" +
                "  LEFT JOIN DCP_CATEGORY_LANG g ON g.EID = b.EID AND g.CATEGORY = c.CATEGORY AND g.LANG_TYPE = '" + req.getLangType() + "'" +
                "  LEFT JOIN DCP_WAREHOUSE_LANG h ON h.eid = b.eid AND h.warehouse = b.warehouse" +
                "  LEFT JOIN DCP_UNIT_LANG i ON i.EID = b.EID AND i.UNIT = b.BASEUNIT AND i.LANG_TYPE = 'zh_CN' " +
                "  WHERE 1=1"
        );

        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            querySql.append(" AND a.billNo='" + req.getRequest().getBillNo() + "'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getReceivingNo())) {
            querySql.append(" AND a.RECEIVINGNO='").append(req.getRequest().getReceivingNo()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            querySql.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }

//        if ("1".equals(req.getRequest().getSearchScope())) {
////            1.仅查询待入库量>0明细
//            querySql.append(" AND b.PASSQTY-b.STOCKINQTY>0");
//        }

        querySql.append(" ORDER BY ITEM ");

        return querySql.toString();
    }

    @Override
    protected String getQuerySql(DCP_PurReceiveDetailQueryReq req) throws Exception {

        StringBuilder querySql = new StringBuilder();
        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        querySql.append(" select * from (");

        querySql.append(
                " SELECT COUNT( DISTINCT temp.BILLNO ) OVER ( ) NUM," +
                        "      dense_rank ( ) over ( ORDER BY BDATE DESC,BILLNO DESC ) rn," +
                        "      temp.* " +
                        "    FROM" +
                        "      (" +
                        "        SELECT" +
                        "          a.*," +
                        "          c.organizationno AS orgno," +
                        "          c.org_name AS orgname," +
                        "          b.org_name PAYORGNAME," +
                        "          ol3.org_name PURORGNAME," +
                        "          em2.name AS employeename," +
                        "          dd2.DEPARTNAME AS departName," +
                        "          dd0.DEPARTNAME AS OWNDEPTNAME," +
                        "          em1.op_name AS createbyname," +
                        "          em6.op_name AS modifybyname," +
                        "          em3.op_name AS confirmbyname," +
                        "          em4.op_name AS cancelbyname," +
                        "          em0.name AS ownopname," +
                        "          f.NAME BILLDATEDESC," +
                        "          g.NAME PAYDATEDESC," +
                        "          h.NAME CURRENCYNAME," +
                        "          i.INVOICE_NAME INVOICENAME, " +
                        "          wl.warehouse_name WAREHOUSENAME,wl1.islocation, " +
                        "          e.SNAME SUPPLIERNAME,j.sname as paayeename,k.org_name as corpname,l.org_name as bizorgname,m.org_name as bizcorpname,j.sname as payeename    " +
                        "        FROM DCP_PURRECEIVE a " +
                        "        LEFT JOIN dcp_org_lang b ON b.eid = a.eid AND b.ORGANIZATIONNO = a.PAYORGNO AND b.lang_type ='" + req.getLangType() + "'" +
                        "        LEFT JOIN dcp_org_lang c ON c.eid = a.eid AND c.ORGANIZATIONNO = a.ORGANIZATIONNO AND c.lang_type = '" + req.getLangType() + "'" +
                        "        LEFT JOIN dcp_org_lang ol3 ON ol3.eid = a.eid AND ol3.ORGANIZATIONNO = a.PURORGNO AND ol3.lang_type = '" + req.getLangType() + "'" +
                        "        LEFT JOIN DCP_BIZPARTNER e ON e.eid = a.eid AND e.BIZPARTNERNO = a.SUPPLIER" +
                        "        LEFT JOIN DCP_BILLDATE_LANG f ON f.eid = a.eid AND f.BILLDATENO = a.BILLDATENO AND f.lang_type = '" + req.getLangType() + "'" +
                        "        LEFT JOIN DCP_PAYDATE_LANG g ON g.eid = a.eid AND g.PAYDATENO = a.PAYDATENO AND g.lang_type = '" + req.getLangType() + "'" +
                        "        LEFT JOIN DCP_CURRENCY_LANG h ON h.eid = a.eid AND h.CURRENCY = a.CURRENCY AND nation = 'CN' AND h.lang_type = '" + req.getLangType() + "'" +
                        "        LEFT JOIN DCP_INVOICETYPE_LANG i ON i.eid = a.eid AND i.INVOICECODE = a.INVOICECODE AND TAXAREA = 'CN' AND i.lang_type = '" + req.getLangType() + "'" +
                        "        LEFT JOIN DCP_employee em0 ON em0.eid = a.eid AND em0.employeeno = a.OWNOPID" +
                        "        LEFT JOIN platform_staffs_lang em1 ON em1.eid = a.eid AND em1.opno = a.CREATEOPID and em1.lang_type='"+req.getLangType()+"'" +
                        "        LEFT JOIN DCP_employee em2 ON em2.eid = a.eid AND em2.employeeno = a.EMPLOYEEID " +
                        "        LEFT JOIN platform_staffs_lang em3 ON em3.eid = a.eid AND em3.opno = a.CONFIRMBY and em3.lang_type='"+req.getLangType()+"'" +
                        "        LEFT JOIN platform_staffs_lang em4 ON em4.eid = a.eid AND em4.opno = a.CANCELBY and em4.lang_type='"+req.getLangType()+"'" +
                        "        LEFT JOIN platform_staffs_lang em6 ON em6.eid = a.eid AND em6.opno = a.LASTMODIOPID and em6.lang_type='"+req.getLangType()+"'" +
                        "        LEFT JOIN dcp_department_lang dd0 ON dd0.eid = a.eid AND dd0.departno = a.OWNDEPTID AND dd0.lang_type = '" + req.getLangType() + "'" +
                        "        LEFT JOIN dcp_department_lang dd2 ON dd2.eid = a.eid AND dd2.departno = a.DEPARTID AND dd2.lang_type = '" + req.getLangType() + "'" +
                        "        left join dcp_warehouse_lang wl on wl.eid=a.eid and wl.warehouse=a.warehouse and wl.lang_type='" + req.getLangType() + "' " +
                        "        left join dcp_warehouse wl1 on wl1.eid=a.eid and wl1.warehouse=a.warehouse  " +
                        " left join dcp_bizpartner j on j.eid=a.eid and j.bizpartnerno=a.payee " +
                        "        left join dcp_org_lang k on k.eid=a.eid and k.organizationno=a.corp and k.lang_type='"+req.getLangType()+"'" +
                        "        left join dcp_org_lang l on l.eid=a.eid and l.organizationno=a.bizorgno and l.lang_type='"+req.getLangType()+"'" +
                        "        left join dcp_org_lang m on m.eid=a.eid and m.organizationno=a.bizcorp and m.lang_type='"+req.getLangType()+"'" +
                        "   where a.eid='" + req.geteId() + "' and a.organizationno='" + req.getOrganizationNO() + "'  "

        );


        if (!Check.Null(req.getRequest().getBillNo())) {
            querySql.append(" and a.billno='" + req.getRequest().getBillNo() + "'");

        }

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            querySql.append("and a.status='" + req.getRequest().getStatus() + "' ");
        }

        if (!Check.Null(req.getRequest().getReceivingNo())) {
            querySql.append("and (a.receivingno like '%%" + req.getRequest().getReceivingNo() + "%%' " +

                    " ) ");
        }

        querySql.append(") temp  ");

        querySql.append("  "
                + " ) a ");


        return querySql.toString();
    }
}
