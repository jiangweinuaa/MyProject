package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CostDomainQueryReq;
import com.dsc.spos.json.cust.res.DCP_CostDomainQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_CostDomainQuery extends SPosBasicService<DCP_CostDomainQueryReq, DCP_CostDomainQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_CostDomainQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostDomainQueryReq> getRequestType() {
        return new TypeToken<DCP_CostDomainQueryReq>() {
        };
    }

    @Override
    protected DCP_CostDomainQueryRes getResponseType() {
        return new DCP_CostDomainQueryRes();
    }

    @Override
    protected DCP_CostDomainQueryRes processJson(DCP_CostDomainQueryReq req) throws Exception {
        DCP_CostDomainQueryRes res = this.getResponseType();

        res.setDatas(new ArrayList<>());

        List<Map<String, Object>> getQData = this.doQueryData(getQuerySql(req), null);
        int totalRecords = 0;    //总笔数
        int totalPages = 0;        //总页数
        if (getQData != null && !getQData.isEmpty()) {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            Map<String, Boolean> distinctMap = new HashMap<>();
            distinctMap.put("EID", true);
            distinctMap.put("CORP", true);
            distinctMap.put("COSTDOMAINTYPE", true);
            distinctMap.put("COST_CALCULATION", true);

            List<Map<String, Object>> distinctData = MapDistinct.getMap(getQData, distinctMap);

            for (Map<String, Object> oneDistinct : distinctData) {
                DCP_CostDomainQueryRes.Datas oneEntity = res.new Datas();
                oneEntity.setDatas(new ArrayList<>());
                res.getDatas().add(oneEntity);

                oneEntity.setCorp(oneDistinct.get("CORP").toString());
                oneEntity.setCorpName(oneDistinct.get("CORP_NAME").toString());
                oneEntity.setCostDomainTYPE(oneDistinct.get("COSTDOMAINTYPE").toString());
                oneEntity.setCost_Calculation(oneDistinct.get("COST_CALCULATION").toString());

                Map<String, Object> condition = new HashMap<>();

                condition.put("EID", oneDistinct.get("EID"));
                condition.put("CORP", oneDistinct.get("CORP"));
                condition.put("COSTDOMAINTYPE", oneDistinct.get("COSTDOMAINTYPE"));
                condition.put("COST_CALCULATION", oneDistinct.get("COST_CALCULATION"));

                List<Map<String, Object>> details = MapDistinct.getWhereMap(getQData, condition, true);

                for (Map<String, Object> oneDetail : details) {

                    DCP_CostDomainQueryRes.Detail detail = res.new Detail();
                    oneEntity.getDatas().add(detail);

                    detail.setCostDomainid(oneDetail.get("COSTDOMAINID").toString());
                    detail.setCostDomain(oneDetail.get("COSTDOMAIN").toString());
                    detail.setStatus(oneDetail.get("STATUS").toString());
                    detail.setMemo(oneDetail.get("MEMO").toString());

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
    protected String getQuerySql(DCP_CostDomainQueryReq req) throws Exception {
        String eId = req.geteId();

        String costDomain = req.getRequest().getCostDomain();
        String gorp = req.getRequest().getGorp();
        String keyTxt = req.getRequest().getKeyTxt();

        //計算起啟位置
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        int startRow = (pageNumber - 1) * pageSize;

        StringBuilder sqlBuff = new StringBuilder();

        sqlBuff.append(" select * from ("
                + " select count(*) over() num, row_number() over (order by a.COSTDOMAINID) rn,"
                + " a.*,b.ORG_NAME CORP_NAME FROM DCP_COSTDOMAIN a "
                + " LEFT JOIN DCP_ORG_LANG b on a.eid=b.eid and a.CORP=b.ORGANIZATIONNO and b.LANG_TYPE='" + req.getLangType() + "'"
                + " WHERE a.EID='" + eId + "' ");

        if (keyTxt != null && !keyTxt.isEmpty()) {
            sqlBuff.append(" AND (a.COSTDOMAIN like '%%" + keyTxt + "%%' or a.COSTDOMAINID like '%%" + keyTxt + "%%' or a.STATUS like '%%" + keyTxt + "%%'  ) ");
        }
        if (costDomain != null && !costDomain.isEmpty()) {
            sqlBuff.append(" AND a.COSTDOMAINID = '" + costDomain + "' ");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getStatus())){
            sqlBuff.append(" AND a.STATUS='" + req.getRequest().getStatus() + "' ");
        }

        if (gorp != null && !gorp.isEmpty()) {
            sqlBuff.append(" AND a.CORP = '" + gorp + "' ");
        }
        sqlBuff.append(" ) where rn>" + startRow + " and rn<=" + (startRow + pageSize));
        return sqlBuff.toString();
    }
}