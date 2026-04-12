package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CvsShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_CvsShopQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.ConvertUtils;
import com.google.common.base.Strings;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class DCP_CvsShopQuery extends SPosBasicService<DCP_CvsShopQueryReq, DCP_CvsShopQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_CvsShopQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected DCP_CvsShopQueryRes processJson(DCP_CvsShopQueryReq req) throws Exception {
        int startRownum = (req.getPageNumber() - 1) * req.getPageSize();
        int endRRownum = startRownum + req.getPageSize();
        startRownum += 1;
        String orgNo = req.getRequest().getOrgNo();
        String condition = orgNo == null || orgNo.isEmpty()? "" : "AND ORGNO LIKE '%%" + orgNo + "%%'";

        String querySql = getQuerySql(condition);
        querySql = querySql.replace(":EID", req.geteId());
        querySql = querySql.replace(":STARTROWNUM", String.valueOf(startRownum));
        querySql = querySql.replace(":ENDROWNUM", String.valueOf(endRRownum));
        querySql = querySql.replace(":LANGTYPE", String.valueOf(req.getLangType()));
        List<Map<String, Object>> searchResult = this.doQueryData(querySql, null);
        List<DCP_CvsShopQueryRes.Level1Elm> level1Elms = ConvertUtils.convertValue(searchResult, DCP_CvsShopQueryRes.Level1Elm.class);

        String countSql = getCountSql(condition);
        countSql = countSql.replace(":EID", req.geteId());
        countSql = countSql.replace(":LANGTYPE", String.valueOf(req.getLangType()));
        List<Map<String, Object>> searchCountResult = this.doQueryData(countSql, null);
        int totalRecords = Integer.parseInt(String.valueOf(searchCountResult.get(0).get("COUNT")));
        int totalPages = totalRecords / req.getPageSize();
        totalPages += ((totalRecords % req.getPageSize()) > 0 ? 1 : 0);

        DCP_CvsShopQueryRes response = getResponseType();
        response.setSuccess(true);
        response.setDatas(level1Elms);
        response.setPageNumber(req.getPageNumber());
        response.setPageSize(req.getPageSize());
        response.setTotalRecords(totalRecords);
        response.setTotalPages(totalPages);
        return response;
    }

    private String getBaseQuerySql(String condition) {
        String sql =
                "SELECT\n" +
                "*\n" +
                "FROM (\n" +
                "     SELECT\n" +
                "            ROWNUM RN,\n" +
                "            DCP_ORG.ORGANIZATIONNO ORGNO,\n" +
                "            ORG_NAME ORGNAME,\n" +
                "            FAMISHOPID,\n" +
                "            HILIFESHOPID,\n" +
                "            UNIMARTSHOPID,\n" +
                "            OKSHOPID,\n" +
                "            'Y' DISABLE\n" +
                "     FROM DCP_ORG\n" +
                "     LEFT JOIN DCP_ORG_CVS ON DCP_ORG.EID = DCP_ORG_CVS.EID AND DCP_ORG.ORGANIZATIONNO = DCP_ORG_CVS.ORGANIZATIONNO\n" +
                "     LEFT JOIN DCP_ORG_LANG ON DCP_ORG.EID = DCP_ORG_LANG.EID AND DCP_ORG.ORGANIZATIONNO = DCP_ORG_LANG.ORGANIZATIONNO AND DCP_ORG_LANG.LANG_TYPE = ':LANGTYPE'\n" +
                "     WHERE DCP_ORG.EID = ':EID'\n" +
                "     ORDER BY RN\n" +
                "    ) source\n" +
                "WHERE 1 = 1\n" +
                condition;

        return sql;
    }

    private String getQuerySql(String condition){
        String sql =
                getBaseQuerySql(condition) +
                "AND RN BETWEEN :STARTROWNUM AND :ENDROWNUM\n" +
                "ORDER BY ORGNO";

        return sql;
    }

    private String getCountSql(String condition) {
        String sql = "SELECT COUNT(1) COUNT FROM (\n" +
                getBaseQuerySql(condition)  + "\n" +
              ") t";

        return sql;
    }

    @Override
    protected TypeToken<DCP_CvsShopQueryReq> getRequestType() {
        return new TypeToken<DCP_CvsShopQueryReq>(){};
    }

    @Override
    protected DCP_CvsShopQueryRes getResponseType() {
        return new DCP_CvsShopQueryRes();
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_CvsShopQueryReq req) throws Exception {
        return null;
    }
}
