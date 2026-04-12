package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CurInvCostAdjQueryReq;
import com.dsc.spos.json.cust.res.DCP_CurInvCostAdjQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_CurInvCostAdjQuery extends SPosBasicService<DCP_CurInvCostAdjQueryReq, DCP_CurInvCostAdjQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_CurInvCostAdjQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CurInvCostAdjQueryReq> getRequestType() {
        return new TypeToken<DCP_CurInvCostAdjQueryReq>() {
        };
    }

    @Override
    protected DCP_CurInvCostAdjQueryRes getResponseType() {
        return new DCP_CurInvCostAdjQueryRes();
    }

    @Override
    protected DCP_CurInvCostAdjQueryRes processJson(DCP_CurInvCostAdjQueryReq req) throws Exception {

        DCP_CurInvCostAdjQueryRes res = this.getResponseType();

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
                DCP_CurInvCostAdjQueryRes.Datas oneData = res.new Datas();

                oneData.setStatus(data.get("STATUS").toString());
                oneData.setAccountID(data.get("ACCOUNTID").toString());
                oneData.setAccount(data.get("ACCOUNT").toString());
                oneData.setYear(data.get("YEAR").toString());
                oneData.setPeriod(data.get("PERIOD").toString());
                oneData.setDataSource(data.get("DATASOURCE").toString());
                oneData.setReferenceNo(data.get("REFERENCENO").toString());

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
    protected String getQuerySql(DCP_CurInvCostAdjQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.REFERENCENO DESC) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" a.* ")
                .append(" ,b.ACCOUNT ")
                .append(" FROM DCP_CURINVCOSTADJ a ")
                .append(" LEFT JOIN DCP_ACOUNT_SETTING b on a.eid=b.eid and a.ACCOUNTID=b.ACCOUNTID ")

        ;

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getYear())) {
            sb.append(" and a.YEAR='").append(req.getRequest().getYear()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getPeriod())) {
            sb.append(" and a.PERIOD='").append(req.getRequest().getPeriod()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getAccountID())) {
            sb.append(" and a.ACCOUNTID='").append(req.getRequest().getAccountID()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getReferenceNo())) {
            sb.append(" and a.REFERENCENO='").append(req.getRequest().getReferenceNo()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getDataSource())) {
            sb.append(" and a.DATASOURCE='").append(req.getRequest().getDataSource()).append("'");
        }


        sb.append("  ) a "
                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY REFERENCENO ");

        return sb.toString();
    }
}
