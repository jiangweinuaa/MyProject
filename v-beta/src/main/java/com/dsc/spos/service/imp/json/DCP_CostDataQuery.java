package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CostDataQueryReq;
import com.dsc.spos.json.cust.res.DCP_CostDataQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_CostDataQuery extends SPosBasicService<DCP_CostDataQueryReq, DCP_CostDataQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_CostDataQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostDataQueryReq> getRequestType() {
        return new TypeToken<DCP_CostDataQueryReq>() {
        };
    }

    @Override
    protected DCP_CostDataQueryRes getResponseType() {
        return new DCP_CostDataQueryRes();
    }

    @Override
    protected DCP_CostDataQueryRes processJson(DCP_CostDataQueryReq req) throws Exception {
        DCP_CostDataQueryRes res = this.getResponse();
        int totalRecords = 0;
        int totalPages = 0;
        //分两段sql 查
        //单头查询
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        String sqld = this.getQuerySql(req);
        List<Map<String, Object>> getQDataDetail = this.doQueryData(sqld, null);

        res.setDatas(new ArrayList<>());
        //and a.accountid='ALLCOA'

        if (getQDataDetail != null && !getQDataDetail.isEmpty()) {
            String num = getQDataDetail.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> map : getQDataDetail) {
                DCP_CostDataQueryRes.Datas oneData = res.new Datas();
                res.getDatas().add(oneData);

                oneData.setStatus(map.get("STATUS").toString());
                oneData.setAccountID(map.get("ACCOUNTID").toString());
                oneData.setAccount(map.get("ACCOUNT").toString());
                oneData.setYear(map.get("YEAR").toString());
                oneData.setPeriod(map.get("PERIOD").toString());
                oneData.setCostType(map.get("COSTTYPE").toString());
                oneData.setCostNo(map.get("COSTNO").toString());
                oneData.setGlNo(map.get("GLNO").toString());

            }


        }


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
    protected String getQuerySql(DCP_CostDataQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        sb.append(" SELECT * FROM( ")
                .append("SELECT row_number() OVER (ORDER BY a.COSTNO,a.ACCOUNTID) AS RN,COUNT(*) OVER ( ) NUM,A.*,")
                .append(" FROM DCP_COSTDATA a ")
                .append(" LEFT JOIN DCP_ACOUNT_SETTING b ON a.eid=b.eid and a.ACCOUNTID=b.ACCOUNTID ")
        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getAccountID())) {
            sb.append(" AND a.ACCOUNTID =").append(req.getRequest().getAccountID()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            sb.append(" AND a.STATUS = '").append(req.getRequest().getStatus()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getYear())) {
            sb.append(" AND a.YEAR = ").append(req.getRequest().getYear());
        }

        if (StringUtils.isNotEmpty(req.getRequest().getPeriod())) {
            sb.append(" AND a.PERIOD = ").append(req.getRequest().getPeriod());
        }


        sb.append("  )  a "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY ACCOUNTID ");


        return sb.toString();
    }
}
