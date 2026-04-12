package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_AcctSetClosingQryReq;
import com.dsc.spos.json.cust.res.DCP_AcctSetClosingQryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_AcctSetClosingQry extends SPosBasicService<DCP_AcctSetClosingQryReq, DCP_AcctSetClosingQryRes> {
    @Override
    protected boolean isVerifyFail(DCP_AcctSetClosingQryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_AcctSetClosingQryReq> getRequestType() {
        return new TypeToken<DCP_AcctSetClosingQryReq>() {
        };
    }

    @Override
    protected DCP_AcctSetClosingQryRes getResponseType() {
        return new DCP_AcctSetClosingQryRes();
    }

    @Override
    protected DCP_AcctSetClosingQryRes processJson(DCP_AcctSetClosingQryReq req) throws Exception {
        DCP_AcctSetClosingQryRes res = this.getResponseType();
        int totalRecords;                //总笔数
        int totalPages;
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);
        res.setDatas(new ArrayList<>());
        if (getData != null && !getData.isEmpty()) {
            //总页数
            String num = getData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> data : getData) {
                DCP_AcctSetClosingQryRes.Datas oneData = res.new Datas();
                res.getDatas().add(oneData);

                oneData.setStatus(data.get("STATUS").toString());
                oneData.setAccountID(data.get("ACCOUNTID").toString());
                oneData.setAccount(data.get("ACCOUNT").toString());
                oneData.setAcctType(data.get("ACCTTYPE").toString());
                oneData.setCorp(data.get("CORP").toString());
                oneData.setCorpName(data.get("CORPNAME").toString());
                oneData.setCostCalculation(data.get("COST_CALCULATION").toString());
                oneData.setCostDomain(data.get("COST_DOMAIN").toString());
                oneData.setClosingDate(data.get("CLOSINGDATE").toString());
                oneData.setInvClosingDate(data.get("INVCLOSINGDATE").toString());
                oneData.setFundCloseDate(data.get("FUNDCLOSEDATE").toString());
                oneData.setCostClosingDate(data.get("COSTCLOSINGDATE").toString());
                oneData.setAssetClosingDate(data.get("ASSETCLOSINGDATE").toString());
                oneData.setApClosingDate(data.get("APCLOSINGDATE").toString());
                oneData.setArClosingDate(data.get("ARCLOSINGDATE").toString());

            }
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
        }
        res.setSuccess(true);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_AcctSetClosingQryReq req) throws Exception {

        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        sb.append(" SELECT * FROM( ")
                .append("SELECT row_number() OVER (ORDER BY a.ACCOUNTID ) AS RN,COUNT(*) OVER ( ) NUM,A.*,")
                .append(" em1.name AS CREATEOPNAME,em2.name AS LASTMODIOPNAME,dd0.DEPARTNAME AS CREATEDEPTNAME, " +
                        " c.NAME CURRENCYNAME,o.COST_DOMAIN,o.COST_CALCULATION,ol1.ORG_NAME CORPNAME ")
                .append(" FROM DCP_ACOUNT_SETTING a ")
//                .append(" LEFT JOIN DCP_COSTDOMAIN b on a.eid=b.eid and a.CORP=b.CORP ")
                .append(" LEFT JOIN DCP_ORG o ON o.eid = a.eid AND o.ORGANIZATIONNO=a.CORP ")
                .append(" LEFT JOIN DCP_employee em1 ON em1.eid = a.eid AND em1.employeeno = a.CREATEOPID ")
                .append(" LEFT JOIN DCP_employee em2 ON em2.eid = a.eid AND em2.employeeno = a.LASTMODIOPID ")
                .append(" LEFT JOIN dcp_department_lang dd0 ON dd0.eid = a.eid AND dd0.departno = a.CREATEDEPTID AND dd0.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_CURRENCY_LANG c ON c.eid = a.eid AND c.CURRENCY = a.CURRENCY and nation='CN' AND c.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 ON ol1.eid = a.eid AND ol1.ORGANIZATIONNO=a.CORP AND ol1.lang_type='").append(req.getLangType()).append("'")
        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");

        sb.append(" AND a.ACCTTYPE='1' ");

        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            sb.append(" AND a.CORP = '").append(req.getRequest().getCorp()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            sb.append(" AND a.STATUS ='").append(req.getRequest().getStatus()).append("'");
        }

        sb.append("  )  a "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY ACCOUNTID ");


        return sb.toString();
    }
}
