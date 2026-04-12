package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CustGroupQueryReq;
import com.dsc.spos.json.cust.res.DCP_CustGroupQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_CustGroupQuery extends SPosBasicService<DCP_CustGroupQueryReq, DCP_CustGroupQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_CustGroupQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CustGroupQueryReq> getRequestType() {
        return new TypeToken<DCP_CustGroupQueryReq>() {
        };
    }

    @Override
    protected DCP_CustGroupQueryRes getResponseType() {
        return new DCP_CustGroupQueryRes();
    }

    @Override
    protected DCP_CustGroupQueryRes processJson(DCP_CustGroupQueryReq req) throws Exception {
        DCP_CustGroupQueryRes res = this.getResponseType();

        List<Map<String, Object>> queryData = doQueryData(this.getQuerySql(req), null);
        int totalRecords;	//总笔数
        int totalPages;		//总页数
        String num = queryData.get(0).get("NUM").toString();
        totalRecords=Integer.parseInt(num);
        totalPages = totalRecords / req.getPageSize();
        totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

        res.setDatas(new ArrayList<>());
        for (Map<String, Object> oneData : queryData) {
            DCP_CustGroupQueryRes.Datas datas = res.new Datas();

            datas.setCustGroupNo(oneData.get("CUSTGROUPNO").toString());
            datas.setCustGroupName(StringUtils.toString(oneData.get("CUSTGROUPNAME"), ""));

            datas.setLastModiOpId(StringUtils.toString(oneData.get("LASTMODIOPID"), ""));
            datas.setLastModiOpName(StringUtils.toString(oneData.get("LASTMODIOPNAME"), ""));
            datas.setCreateOpId(StringUtils.toString(oneData.get("CREATEOPID"), ""));
            datas.setCreateOpName(StringUtils.toString(oneData.get("CREATEOPNAME"), ""));
            datas.setCreateDeptId(StringUtils.toString(oneData.get("CREATEDEPTID"), ""));
            datas.setCreateDeptName(StringUtils.toString(oneData.get("CREATEDEPTNAME"), ""));
            datas.setMemo(StringUtils.toString(oneData.get("MEMO"), ""));
            datas.setLastModiTime(StringUtils.toString(oneData.get("LASTMODITIME"), ""));
            datas.setCreateTime(StringUtils.toString(oneData.get("CREATETIME"), ""));
            datas.setCustCount(StringUtils.toString(oneData.get("CUSTCOUNT"), "0"));
            datas.setStatus(oneData.get("STATUS").toString());

            res.getDatas().add(datas);
        }

        res.setSuccess(true);
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
    protected String getQuerySql(DCP_CustGroupQueryReq req) throws Exception {

        //分页处理
        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        String countCustSql =
                " SELECT a.EID,a.CUSTGROUPNO,COUNT(DISTINCT CUTNO) CustCount  FROM( " +
                        "  SELECT a.EID,a.CUSTGROUPNO,CASE WHEN a.ATTRTYPE='1' THEN c.ID ELSE A.ATTRID END CUTNO " +
                        "  FROM DCP_CUSTGROUP_DETAIL a " +
                        "  LEFT JOIN dcp_tagtype_detail c on a.EID=c.EID AND a.ATTRTYPE=1 and c.TAGGROUPTYPE='CUST' AND c.TAGNO=a.ATTRID )a" +
                        "  GROUP BY  a.EID,a.CUSTGROUPNO";

        StringBuilder builder = new StringBuilder();

        builder.append(" SELECT * FROM( ");

        builder.append(" SELECT count(1) over () as num,row_number() over (order by a.CUSTGROUPNO desc) as rn," +
                "  a.*,b.CustCount,em0.name as CREATEOPNAME,em1.name as LASTMODIOPNAME,dd0.DEPARTNAME as CREATEDEPTNAME " +
                " FROM DCP_CUSTGROUP a " +
                "  LEFT JOIN  (" + countCustSql + ") b on a.EID=B.EID AND a.CUSTGROUPNO=b.CUSTGROUPNO " +
                "  left join DCP_employee em0 on em0.eid=a.eid and em0.employeeno=a.CREATEOPID " +
                "  left join DCP_employee em1 on em1.eid=a.eid and em1.employeeno=a.LASTMODIOPID " +
                "  left join dcp_department_lang dd0 on dd0.eid=a.eid and dd0.departno=a.CREATEDEPTID and dd0.lang_type='" + req.getLangType() + "'" +
                "  where a.eid='" + req.geteId() + "'"
        );

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            builder.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getKeyTxt())) {
            builder.append(" AND (a.CUSTGROUPNO like '%%")
                    .append(req.getRequest().getKeyTxt())
                    .append("%%' OR")
                    .append(" a.CUSTGROUPNAME like '%% ")
                    .append(req.getRequest().getKeyTxt())
                    .append("%%')");
        }

        builder.append(") temp  ");

        builder.append(" where  rn>").append(startRow).append(" and rn<=").append(startRow + pageSize).append("  ").append(" ");

        return builder.toString();
    }
}
