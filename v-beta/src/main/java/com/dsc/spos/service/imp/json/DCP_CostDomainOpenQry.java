package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CostDomainOpenQryReq;
import com.dsc.spos.json.cust.res.DCP_CostDomainOpenQryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_CostDomainOpenQry extends SPosBasicService<DCP_CostDomainOpenQryReq, DCP_CostDomainOpenQryRes> {
    @Override
    protected boolean isVerifyFail(DCP_CostDomainOpenQryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostDomainOpenQryReq> getRequestType() {
        return new TypeToken<DCP_CostDomainOpenQryReq>() {
        };
    }

    @Override
    protected DCP_CostDomainOpenQryRes getResponseType() {
        return new DCP_CostDomainOpenQryRes();
    }

    @Override
    protected DCP_CostDomainOpenQryRes processJson(DCP_CostDomainOpenQryReq req) throws Exception {
        DCP_CostDomainOpenQryRes res = this.getResponseType();
        List<Map<String, Object>> getQData = this.doQueryData(getQuerySql(req), null);
        int totalRecords = 0;    //总笔数
        int totalPages = 0;        //总页数

        res.setDatas(new ArrayList<>());
        if (getQData != null && !getQData.isEmpty()) {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            Map<String, Boolean> distinctMap = new HashMap<>();
            distinctMap.put("CORP", true);

            List<Map<String, Object>> distinctData = MapDistinct.getMap(getQData, distinctMap);
            for (Map<String, Object> oneDistinct : distinctData) {
                DCP_CostDomainOpenQryRes.Datas oneEntity = res.new Datas();
                oneEntity.setOrgList(new ArrayList<>());
                res.getDatas().add(oneEntity);

                oneEntity.setCorp(oneDistinct.get("CORP").toString());
                oneEntity.setCorpName(oneDistinct.get("CORPNAME").toString());

                Map<String, Object> condition = new HashMap<>();
                condition.put("CORP", oneDistinct.get("CORP"));

                List<Map<String, Object>> details = MapDistinct.getWhereMap(getQData, condition, true);
                for (Map<String, Object> oneDetail : details) {
                    DCP_CostDomainOpenQryRes.OrgList oneOrg = res.new OrgList();
                    oneEntity.getOrgList().add(oneOrg);

                    oneOrg.setOrgNo(oneDetail.get("ORGANIZATIONNO").toString());
                    oneOrg.setOrgName(oneDetail.get("ORGNAME").toString());
                }
            }

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
    protected String getQuerySql(DCP_CostDomainOpenQryReq req) throws Exception {

        //計算起啟位置
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        int startRow = (pageNumber - 1) * pageSize;

        StringBuilder querySql = new StringBuilder();
        querySql.append(" select * from (")
                .append("   select count(*) over() num, row_number() over (order by a.ORGANIZATIONNO) rn ")
                .append("       ,a.ORGANIZATIONNO CORP ,ol1.ORG_NAME CORPNAME  " +
                        "       ,b.ORGANIZATIONNO,ol2.ORG_NAME ORGNAME ")
                .append("   FROM DCP_ORG a ")
                .append("   LEFT JOIN DCP_ORG b on a.eid=b.EID and b.CORP=a.ORGANIZATIONNO ")
                .append("   LEFT JOIN DCP_ORG_LANG ol1 on ol1.eid=a.EID and ol1.ORGANIZATIONNO=a.ORGANIZATIONNO and ol1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append("   LEFT JOIN DCP_ORG_LANG ol2 on ol2.eid=b.EID and ol2.ORGANIZATIONNO=b.ORGANIZATIONNO and ol2.LANG_TYPE='").append(req.getLangType()).append("'")
        ;
        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        querySql.append(" AND a.IS_CORP='Y' ");
        querySql.append(" AND a.STATUS='100' ");

        if (StringUtils.isNotEmpty(req.getRequest().getOrgNo())) {
            querySql.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getOrgNo()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            querySql.append(" AND b.CORP='").append(req.getRequest().getCorp()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getKeyTxt())) {
            querySql.append(" AND ( ol1.ORG_NAME like '%%").append(req.getRequest().getKeyTxt()).append("%%'")
                    .append(")");
        }

        querySql.append(" ) where rn>" + startRow + " and rn<=" + (startRow + pageSize));
        return querySql.toString();
    }
}
