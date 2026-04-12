package com.dsc.spos.service.imp.json;


import com.dsc.spos.json.cust.req.DCP_PurReceiveQueryReq;
import com.dsc.spos.json.cust.res.DCP_PurReceiveQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_PurReceiveQuery extends SPosBasicService<DCP_PurReceiveQueryReq, DCP_PurReceiveQueryRes> {

    private Logger logger = LogManager.getLogger(DCP_PurReceiveQuery.class.getName());

    @Override
    protected boolean isVerifyFail(DCP_PurReceiveQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PurReceiveQueryReq> getRequestType() {
        return new TypeToken<DCP_PurReceiveQueryReq>() {

        };
    }

    @Override
    protected DCP_PurReceiveQueryRes getResponseType() {
        return new DCP_PurReceiveQueryRes();
    }

    @Override
    protected DCP_PurReceiveQueryRes processJson(DCP_PurReceiveQueryReq req) throws Exception {
        DCP_PurReceiveQueryRes res = this.getResponseType();

        int totalRecords = 0;        //总笔数
        int totalPages = 0;

        res.setDatas(new ArrayList<>());
        String sql = getQuerySql(req);
        logger.info(sql);
        List<Map<String, Object>> queryData = this.doQueryData(sql, null);

        if (CollectionUtils.isNotEmpty(queryData)) {
            String num = queryData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> oneData : queryData) {
                DCP_PurReceiveQueryRes.Data data = res.new Data();
                data.setStatus(oneData.get("STATUS").toString());
                data.setBillNo(oneData.get("BILLNO").toString());
                data.setOrgNo(StringUtils.toString(oneData.get("ORGNO"), ""));
                data.setOrgName(StringUtils.toString(oneData.get("ORGNAME"), ""));

                data.setBDate(oneData.get("BDATE").toString());

                data.setSupplier(StringUtils.toString(oneData.get("SUPPLIER"), ""));
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
                String querySql = String.format(" SELECT * FROM DCP_PURORDER WHERE EID='%s' AND PURORDERNO='%s' ", req.geteId(), StringUtils.toString(oneData.get("PURORDERNO"), ""));
                List<Map<String, Object>> purOrderInfo = this.doQueryData(querySql, new String[0]);
                if (CollectionUtils.isNotEmpty(purOrderInfo)) {
                    Map<String, Object> oneInfo = purOrderInfo.get(0);
                    data.setExpireDate(StringUtils.toString(oneInfo.get("EXPIREDATE"), ""));
                }


                data.setReceivingNo(StringUtils.toString(oneData.get("RECEIVINGNO"), ""));

                querySql = String.format(" SELECT a.RDATE,a.WAREHOUSE,f.warehouse_name as WAREHOUSENAME,e.ISLOCATION,a.TOT_CQTY,b.POQTY,a.RECEIPTDATE " +
                        " FROM DCP_RECEIVING a " +
                        " LEFT JOIN DCP_RECEIVING_DETAIL b ON a.RECEIVINGNO=b.RECEIVINGNO AND a.SHOPID=b.SHOPID and a.eid=b.eid and a.ORGANIZATIONNO=b.ORGANIZATIONNO " +
                        " LEFT JOIN DCP_WAREHOUSE e on e.EID=a.EID and a.WAREHOUSE=e.WAREHOUSE " +
                        " left join dcp_warehouse_lang f on f.eid=a.eid and f.warehouse=a.warehouse and f.lang_type='" + req.getLangType() + "' " +
                        " WHERE a.EID='%s' AND a.RECEIVINGNO='%s' ", req.geteId(), StringUtils.toString(oneData.get("RECEIVINGNO"), ""));
                List<Map<String, Object>> receivingInfo = this.doQueryData(querySql, null);
                if (CollectionUtils.isNotEmpty(receivingInfo)) {
                    Map<String, Object> oneInfo = receivingInfo.get(0);

                    data.setReceiptDate(StringUtils.toString(oneInfo.get("RECEIPTDATE"), ""));
                    data.setIsLocation(StringUtils.toString(oneInfo.get("ISLOCATION"), ""));

                }

                data.setTotCanStockInQty(StringUtils.toString(oneData.get("TOTCANSTOCKINQTY"), ""));
                data.setTotCanStockInCqty(StringUtils.toString(oneData.get("TOTCANSTOCKINCQTY"), ""));
                data.setTotPassQty(StringUtils.toString(oneData.get("TOTPASSQTY"), ""));
                data.setTotStockInQty(StringUtils.toString(oneData.get("TOTSTOCKINQTY"), "0"));

                data.setWareHouse(StringUtils.toString(oneData.get("WAREHOUSE"), ""));
                data.setWareHouseName(StringUtils.toString(oneData.get("WAREHOUSENAME"), ""));

                data.setTotCqty(StringUtils.toString(oneData.get("TOTCQTY"), ""));
                data.setTotPqty(StringUtils.toString(oneData.get("TOTPQTY"), ""));

                data.setTotPurAmt(StringUtils.toString(oneData.get("TOTPURAMT"), ""));

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
                data.setAccountBy(StringUtils.toString(oneData.get("ACCOUNTBY"), ""));
                data.setAccountByName(StringUtils.toString(oneData.get("ACCOUNTBYNAME"), ""));
                data.setAccountDateTime(StringUtils.toString(oneData.get("ACCOUNTDATE"), ""));
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

                data.setQcStatus(StringUtils.toString(oneData.get("QCSTATUS"), "0"));

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

    @Override
    protected String getQuerySql(DCP_PurReceiveQueryReq req) throws Exception {

        StringBuilder querySql = new StringBuilder();
        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        String totSql = " SELECT a.EID,a.ORGANIZATIONNO,a.BILLNO,SUM(PASSQTY) TOTPASSQTY,SUM(STOCKINQTY) TOTSTOCKINQTY" +
                ",CASE WHEN EXISTS (SELECT 1 FROM DCP_PURRECEIVE_DETAIL b WHERE QCStatus = 1 and a.EID=b.EID and a.BILLNO=b.BILLNO and a.ORGANIZATIONNO=b.ORGANIZATIONNO ) THEN 1 " +
                "      WHEN EXISTS (SELECT 1 FROM DCP_PURRECEIVE_DETAIL c WHERE QCStatus = 2 and a.EID=c.EID and a.BILLNO=c.BILLNO and a.ORGANIZATIONNO=c.ORGANIZATIONNO) THEN 2 " +
                "        ELSE 0 " +
                "        END AS QCSTATUS " +
                " FROM DCP_PURRECEIVE_DETAIL a " +
                " GROUP BY a.EID,a.ORGANIZATIONNO,a.BILLNO ";

        String tsSql= " SELECT EID, ORGANIZATIONNO, BILLNO, COUNT(*) TOTCANSTOCKINCQTY FROM DCP_PURRECEIVE_DETAIL WHERE  PASSQTY - STOCKINQTY > 0 GROUP by EID, ORGANIZATIONNO, BILLNO ";

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
                        "          wl.warehouse_name WAREHOUSENAME, " +
                        "          e.SNAME SUPPLIERNAME,   " +
                        "          ds.QCSTATUS, " +
                        "          ds.TOTPASSQTY,ds.TOTSTOCKINQTY,NVL(ts.TOTCANSTOCKINCQTY,0) TOTCANSTOCKINCQTY, " +
                        "          ds.TOTPASSQTY-ds.TOTSTOCKINQTY as totCanStockInQty,j.sname as payeename,k.org_name as corpname,l.org_name as bizorgname,m.org_name as bizcorpname " +
                        "        FROM DCP_PURRECEIVE a " +
                        " LEFT JOIN ( " + totSql + ") ds ON a.EID=ds.EID and a.ORGANIZATIONNO=ds.ORGANIZATIONNO and a.BILLNO=ds.BILLNO " +
                        " LEFT JOIN ( " + tsSql + ") ts ON a.EID=ts.EID and a.ORGANIZATIONNO=ts.ORGANIZATIONNO and a.BILLNO=ts.BILLNO " +
                        "        LEFT JOIN dcp_org_lang b ON b.eid = a.eid AND b.ORGANIZATIONNO = a.PAYORGNO AND b.lang_type ='" + req.getLangType() + "'" +
                        "        LEFT JOIN dcp_org_lang c ON c.eid = a.eid AND c.ORGANIZATIONNO = a.ORGANIZATIONNO AND c.lang_type = '" + req.getLangType() + "'" +
                        "        LEFT JOIN dcp_org_lang ol3 ON ol3.eid = a.eid AND ol3.ORGANIZATIONNO = a.PURORGNO AND ol3.lang_type = '" + req.getLangType() + "'" +
                        "        LEFT JOIN DCP_BIZPARTNER e ON e.eid = a.eid AND e.BIZPARTNERNO = a.SUPPLIER" +
                        "        LEFT JOIN DCP_BILLDATE_LANG f ON f.eid = a.eid AND f.BILLDATENO = a.BILLDATENO AND f.lang_type = '" + req.getLangType() + "'" +
                        "        LEFT JOIN DCP_BILLDATE_LANG g ON g.eid = a.eid AND g.BILLDATENO = a.PAYDATENO AND g.lang_type = '" + req.getLangType() + "'" +
                        "        LEFT JOIN DCP_CURRENCY_LANG h ON h.eid = a.eid AND h.CURRENCY = a.CURRENCY AND nation = 'CN' AND h.lang_type = '" + req.getLangType() + "'" +
                        "        LEFT JOIN DCP_INVOICETYPE_LANG i ON i.eid = a.eid AND i.INVOICECODE = a.INVOICECODE AND TAXAREA = 'CN' AND i.lang_type = '" + req.getLangType() + "'" +
                        "        LEFT JOIN DCP_employee em0 ON em0.eid = a.eid AND em0.employeeno = a.OWNOPID" +
                        "        LEFT JOIN PLATFORM_STAFFS_LANG em1 ON em1.eid = a.eid AND em1.opno = a.CREATEOPID and em1.lang_type='"+req.getLangType()+"'" +
                        "        LEFT JOIN DCP_employee em2 ON em2.eid = a.eid AND em2.employeeno = a.EMPLOYEEID" +
                        "        LEFT JOIN PLATFORM_STAFFS_LANG em3 ON em3.eid = a.eid AND em3.opno = a.CONFIRMBY and em3.lang_type='"+req.getLangType()+"'" +
                        "        LEFT JOIN PLATFORM_STAFFS_LANG em4 ON em4.eid = a.eid AND em4.opno = a.CANCELBY and em4.lang_type='"+req.getLangType()+"'" +
                        "        LEFT JOIN PLATFORM_STAFFS_LANG em6 ON em6.eid = a.eid AND em6.opno = a.LASTMODIOPID and em6.lang_type='"+req.getLangType()+"'" +
                        "        LEFT JOIN dcp_department_lang dd0 ON dd0.eid = a.eid AND dd0.departno = a.OWNDEPTID AND dd0.lang_type = '" + req.getLangType() + "'" +
                        "        LEFT JOIN dcp_department_lang dd2 ON dd2.eid = a.eid AND dd2.departno = a.DEPARTID AND dd2.lang_type = '" + req.getLangType() + "'" +
                        "        left join dcp_warehouse_lang wl on wl.eid=a.eid and wl.warehouse=a.warehouse and wl.lang_type='" + req.getLangType() + "' " +
                        "        left join dcp_org_lang k on k.eid=a.eid and k.organizationno=a.corp and k.lang_type='"+req.getLangType()+"'" +
                        "        left join dcp_org_lang l on l.eid=a.eid and l.organizationno=a.bizorgno and l.lang_type='"+req.getLangType()+"'" +
                        "        left join dcp_org_lang m on m.eid=a.eid and m.organizationno=a.bizcorp and m.lang_type='"+req.getLangType()+"'" +
                        " left join dcp_bizpartner j on j.eid=a.eid and j.bizpartnerno=a.payee " +
                        "   where a.eid='" + req.geteId() + "' and a.organizationno='" + req.getOrganizationNO() + "'  "

        );


//        if (StringUtils.isNotEmpty(req.getRequest().getReceiptOrgNo())){
//            querySql.append("and a.status='" + req.getRequest().getStatus() + "' ");
//        }

        if ("1".equals(req.getRequest().getSearchScope())) {
            querySql.append(" and a.status='1' ");
            //querySql.append(" and ds.TOTPASSQTY >0 ");
            querySql.append("and (ds.TOTPASSQTY-ds.TOTSTOCKINQTY)>0");
            querySql.append(" and not exists( SELECT * FROM DCP_SSTOCKIN WHERE STATUS ='0' and OFNO=a.BILLNO ) ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            querySql.append("and a.status='" + req.getRequest().getStatus() + "' ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getDateType())) {
//            bdate：单据日期,receiptDate
            if (StringUtils.equals("bdate", req.getRequest().getDateType().toLowerCase())) {
                if (!Check.Null(req.getRequest().getBeginDate())) {
                    querySql.append("and a.bdate>='" + req.getRequest().getBeginDate() + "' ");
                }
                if (!Check.Null(req.getRequest().getEndDate())) {
                    querySql.append("and a.bdate<='" + req.getRequest().getEndDate() + "' ");
                }
            } else if (StringUtils.equals("receiptdate", req.getRequest().getDateType().toLowerCase())) {
                if (!Check.Null(req.getRequest().getBeginDate())) {
                    querySql.append("and a.receiptDate>='" + req.getRequest().getBeginDate() + "' ");
                }
                if (!Check.Null(req.getRequest().getEndDate())) {
                    querySql.append("and a.receiptDate<='" + req.getRequest().getEndDate() + "' ");
                }
            }
        }

        if (!Check.Null(req.getRequest().getKeyTxt())) {
            querySql.append("and (a.receivingno like '%" + req.getRequest().getKeyTxt() + "%%' " +
                    " or a.purorderno like '%%" + req.getRequest().getKeyTxt() + "%%' " +
                    " or a.BillNo like '%%" + req.getRequest().getKeyTxt() + "%%' " +
                    " or a.supplier like '%%" + req.getRequest().getKeyTxt() + "%%' " +
                    " or e.sname like '%%" + req.getRequest().getKeyTxt() + "%%' " +
                    " ) ");
        }

        querySql.append(") temp ORDER BY BDATE DESC,BILLNO DESC ");

        querySql.append("  "
                + " ) a where  rn>" + startRow + " and rn<=" + (startRow + pageSize) + "  "
                + " ");

        return querySql.toString();
    }

}
