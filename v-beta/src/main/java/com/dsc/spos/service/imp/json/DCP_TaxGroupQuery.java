package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_TaxGroupQueryReq;
import com.dsc.spos.json.cust.res.DCP_TaxGroupQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_TaxGroupQuery extends SPosBasicService<DCP_TaxGroupQueryReq, DCP_TaxGroupQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_TaxGroupQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_TaxGroupQueryReq> getRequestType() {
        return new TypeToken<DCP_TaxGroupQueryReq>() {
        };
    }

    @Override
    protected DCP_TaxGroupQueryRes getResponseType() {
        return new DCP_TaxGroupQueryRes();
    }

    @Override
    protected DCP_TaxGroupQueryRes processJson(DCP_TaxGroupQueryReq req) throws Exception {
        DCP_TaxGroupQueryRes res = this.getResponseType();

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


            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            for (Map<String, Object> data : getData) {
                DCP_TaxGroupQueryRes.Datas oneData = res.new Datas();
                oneData.setTaxGroupNo(data.get("TAXGROUPNO").toString());
                oneData.setTaxGroupName(StringUtils.toString(data.get("TAXGROUPNAME"), ""));
                oneData.setTaxCode(data.get("TAXCODE").toString());
                oneData.setTaxName(StringUtils.toString(data.get("TAXNAME"), ""));
                oneData.setTaxRate(StringUtils.toString(data.get("TAXRATE"), ""));
                oneData.setInclTax(StringUtils.toString(data.get("INCLTAX"), ""));
                oneData.setGoodsQty(StringUtils.toString(data.get("QTY"), ""));
                oneData.setMemo(StringUtils.toString(data.get("MEMO"), ""));
                oneData.setStatus(StringUtils.toString(data.get("STATUS"), ""));
                oneData.setCreateTime(StringUtils.toString(data.get("CREATETIME"), ""));
                oneData.setCreateOpId(StringUtils.toString(data.get("CREATEOPID"), ""));
                oneData.setCreateOpName(StringUtils.toString(data.get("CREATEOPNAME"), ""));
                oneData.setLastModiOpId(StringUtils.toString(data.get("LASTMODIOPID"), ""));
                oneData.setLastModiOpName(StringUtils.toString(data.get("LASTMODIOPNAME"), ""));
                oneData.setLastModiTime(StringUtils.toString(data.get("LASTMODITIME"), ""));
                oneData.setCreateDeptId(StringUtils.toString(data.get("CREATEDEPTID"), ""));
                oneData.setCreateDeptName(StringUtils.toString(data.get("CREATEDEPTNAME"), ""));

                res.getDatas().add(oneData);
            }
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
    protected String getQuerySql(DCP_TaxGroupQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        sb.append(" SELECT * FROM( ");

        sb.append("SELECT row_number() OVER (ORDER BY a.TAXGROUPNO DESC) AS RN,COUNT(*) OVER ( ) NUM,A.*,NVL(B.QTY,0) QTY,D.TAXNAME,C.TAXRATE,C.INCLTAX," +
                " em1.name AS CREATEOPNAME,em2.name AS LASTMODIOPNAME,dd0.DEPARTNAME AS CREATEDEPTNAME" +
                " FROM DCP_TAXGROUP A " +
                " LEFT JOIN (" +
                "  SELECT A.EID,A.TAXCODE,COUNT(DISTINCT ATTRID) QTY" +
                "  FROM(" +
                "  SELECT A.EID,A.TAXCODE,a.TAXGROUPNO,b.ATTRTYPE,CASE WHEN b.ATTRTYPE='1' THEN c.PLUNO ELSE b.ATTRID END ATTRID" +
                "  FROM DCP_TAXGROUP a" +
                "  INNER JOIN DCP_TAXGROUP_DETAIL b on a.EID=b.EID and A.TAXGROUPNO=b.TAXGROUPNO" +
                "  LEFT JOIN DCP_GOODS c ON b.EID=c.EID and c.CATEGORY=b.ATTRID AND b.ATTRTYPE='1' and c.STATUS='100' " +
                "  LEFT JOIN DCP_GOODS d ON b.EID=d.EID and b.ATTRID=d.PLUNO AND b.ATTRTYPE='2' and d.STATUS='100' " +
//                "  WHERE c.STATUS='100' and d.STATUS='100'" +
                "  WHERE  c.PLUNO is not  null or d.PLUNO is not null " +
                "  ) A GROUP BY A.EID,A.TAXCODE " +
                ") B ON A.EID=B.EID AND A.TAXCODE=B.TAXCODE " +
                " LEFT JOIN DCP_TAXCATEGORY C ON C.EID=A.EID AND C.TAXCODE=A.TAXCODE " +
                " LEFT JOIN DCP_TAXCATEGORY_LANG d ON d.EID=A.EID AND d.TAXCODE=A.TAXCODE and d.LANG_TYPE='" +req.getLangType()+"'"+
                " LEFT JOIN DCP_employee em1 ON em1.eid = a.eid AND em1.employeeno = a.CREATEOPID" +
                " LEFT JOIN DCP_employee em2 ON em2.eid = a.eid AND em2.employeeno = a.LASTMODIOPID" +
                " LEFT JOIN dcp_department_lang dd0 ON dd0.eid = a.eid AND dd0.departno = a.CREATEDEPTID AND dd0.lang_type='" + req.getLangType() + "'" +
                " WHERE a.eid='"+req.geteId()+"'");

        if (StringUtils.isNotEmpty(req.getRequest().getTaxCode())) {
            sb.append(" AND A.TAXCODE='").append(req.getRequest().getTaxCode()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            sb.append(" AND A.STATUS='").append(req.getRequest().getStatus()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getKeyTxt())) {
//            税分类编号/税分类名称/税别编号/税别名称模糊搜索
            sb.append(" AND (A.TAXGROUPNO like '%%").append(req.getRequest().getKeyTxt()).append("%%'")
                    .append(" OR A.TAXGROUPNAME like '%%").append(req.getRequest().getKeyTxt()).append("%%'")
                    .append(" OR A.TAXCODE like '%%").append(req.getRequest().getKeyTxt()).append("%%'")
                    .append(" OR D.TAXNAME like '%%").append(req.getRequest().getKeyTxt()).append("%%'")
                    .append(")");
        }
        sb.append("  )  a "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY TAXGROUPNO ");


        return sb.toString();
    }
}
