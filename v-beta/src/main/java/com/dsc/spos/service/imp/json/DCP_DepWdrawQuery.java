package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_DepWdrawDeteilQueryReq;
import com.dsc.spos.json.cust.res.DCP_DepWdrawDeteilQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_DepWdrawQuery extends SPosBasicService<DCP_DepWdrawDeteilQueryReq, DCP_DepWdrawDeteilQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_DepWdrawDeteilQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_DepWdrawDeteilQueryReq> getRequestType() {
        return new TypeToken<DCP_DepWdrawDeteilQueryReq>() {
        };
    }

    @Override
    protected DCP_DepWdrawDeteilQueryRes getResponseType() {
        return new DCP_DepWdrawDeteilQueryRes();
    }

    @Override
    protected DCP_DepWdrawDeteilQueryRes processJson(DCP_DepWdrawDeteilQueryReq req) throws Exception {
        DCP_DepWdrawDeteilQueryRes res = this.getResponseType();
        int totalRecords = 0;
        int totalPages = 0;
        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        if (CollectionUtils.isNotEmpty(qData)) {
            //算總頁數
            String num = qData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            res.setDatas(new ArrayList<>());

            for (Map<String, Object> data : qData) {
                DCP_DepWdrawDeteilQueryRes.Datas oneReq = res.new Datas();
                res.getDatas().add(oneReq);

                oneReq.setItem(data.get("RN").toString());
                oneReq.setDepWdrawCode(data.get("DEPWDRAWCODE").toString());
                oneReq.setDepWdrawName(data.get("DEPWDRAWNAME").toString());
                oneReq.setDwType(data.get("DWTYPE").toString());
                oneReq.setCfCode(data.get("CFCODE").toString());
                oneReq.setCfName(data.get("CFNAME").toString());
                oneReq.setSubjectId(data.get("SUBJECTID").toString());
                oneReq.setSubjectName(data.get("SUBJECTNAME").toString());
                oneReq.setAcctSet(data.get("ACCTSET").toString());
                oneReq.setAccountID(data.get("ACCOUNTID").toString());
                oneReq.setAccount(data.get("ACCOUNT").toString());
                oneReq.setStatus(data.get("STATUS").toString());

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
    protected String getQuerySql(DCP_DepWdrawDeteilQueryReq req) throws Exception {

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        StringBuilder querySql = new StringBuilder();
        querySql.append("SELECT *  FROM (");

        querySql.append("SELECT " +
                " COUNT(*) OVER() NUM ," +
                " ROW_NUMBER() over(ORDER BY DEPWDRAWCODE) rn, " +
                " a.* " +
                " FROM DCP_DEPWDRAW a ");
        querySql.append(" WHERE a.EID='").append(req.geteId()).append("' ");

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            querySql.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("' ");
        }
        querySql.append("  )  a "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize) + ""
                + " ORDER BY rn ");
        return querySql.toString();
    }
}
