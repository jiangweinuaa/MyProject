package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_OrgOpenAccQryReq;
import com.dsc.spos.json.cust.res.DCP_OrgOpenAccQryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_OrgOpenAccQry extends SPosBasicService<DCP_OrgOpenAccQryReq, DCP_OrgOpenAccQryRes> {
    @Override
    protected boolean isVerifyFail(DCP_OrgOpenAccQryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_OrgOpenAccQryReq> getRequestType() {
        return new TypeToken<DCP_OrgOpenAccQryReq>() {
        };
    }

    @Override
    protected DCP_OrgOpenAccQryRes getResponseType() {
        return new DCP_OrgOpenAccQryRes();
    }

    @Override
    protected DCP_OrgOpenAccQryRes processJson(DCP_OrgOpenAccQryReq req) throws Exception {
        DCP_OrgOpenAccQryRes res = this.getResponseType();
        res.setDatas(new ArrayList<>());
        int totalRecords = 0;                                //总笔数
        int totalPages = 0;

        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        if (CollectionUtils.isNotEmpty(qData)) {
            String num = qData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

            // 计算页数
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> oneData : qData) {

                DCP_OrgOpenAccQryRes.Datas orgData = res.new Datas();

                orgData.setOrgNo(oneData.get("ORGANIZATIONNO").toString());
                orgData.setStatus(oneData.get("STATUS").toString());
                orgData.setIsCorp(oneData.get("IS_CORP").toString());
                orgData.setCorp(oneData.get("CORP").toString());
                orgData.setSName(oneData.get("SNAME").toString());
                orgData.setFullName(oneData.get("ORG_NAME").toString());
                orgData.setCorpName(oneData.get("CORPNAME").toString());

                res.getDatas().add(orgData);
            }
        }

        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_OrgOpenAccQryReq req) throws Exception {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //分页起始位置
        int startRow = (pageNumber - 1) * pageSize;

        StringBuilder sqlbuf = new StringBuilder();
        sqlbuf.append(" SELECT * FROM ( ");
        sqlbuf.append(" SELECT COUNT(DISTINCT a.ORGANIZATIONNO) OVER() NUM ," +
                " ROW_NUMBER() over(ORDER BY a.ORGANIZATIONNO) rn,  " +
                " a.ORGANIZATIONNO,a.STATUS,a.IS_CORP,a.CORP,ol1.ORG_NAME,ol1.SNAME,o2.org_name as corpname " +
                " FROM DCP_ORG a " +
                " INNER JOIN ( SELECT DISTINCT EID,CORP FROM DCP_ACOUNT_SETTING) b  on a.eid= b.EID and a.CORP=b.CORP " +
                " INNER JOIN DCP_ORG_LANG ol1 on a.eid=ol1.eid and a.ORGANIZATIONNO=ol1.ORGANIZATIONNO AND ol1.LANG_TYPE='").append(req.getLangType()).append("'");
        sqlbuf.append(" left join dcp_org_lang o2 on o2.eid=a.eid and o2.organizationno=a.corp and o2.lang_type='"+req.getLangType()+"' ");
        sqlbuf.append(" WHERE a.EID='").append(req.geteId()).append("'");
//        sqlbuf.append(" AND b.LANG_TYPE='").append(req.getLangType()).append("'");
//        sqlbuf.append(" AND a.ORGANIZATIONNO NOT IN( SELECT CORP FROM DCP_ACOUNT_SETTING ) ");

        if (StringUtils.isNotEmpty(req.getRequest().getIsCorp())) {
            sqlbuf.append(" AND a.IS_CORP='").append(req.getRequest().getIsCorp()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            sqlbuf.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            sqlbuf.append(" AND b.CORP='").append(req.getRequest().getCorp()).append("'");
        }

        sqlbuf.append("  ) a  WHERE RN > " + startRow + " AND rn <= " + (startRow + pageSize) + " ORDER BY ORGANIZATIONNO DESC ");

        return sqlbuf.toString();
    }
}
