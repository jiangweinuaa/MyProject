package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_FeeSetupSubjectQueryReq;
import com.dsc.spos.json.cust.res.DCP_FeeSetupSubjectQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_FeeSetupSubjectQuery extends SPosBasicService<DCP_FeeSetupSubjectQueryReq, DCP_FeeSetupSubjectQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_FeeSetupSubjectQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_FeeSetupSubjectQueryReq> getRequestType() {
        return new TypeToken<DCP_FeeSetupSubjectQueryReq>() {
        };
    }

    @Override
    protected DCP_FeeSetupSubjectQueryRes getResponseType() {
        return new DCP_FeeSetupSubjectQueryRes();
    }

    @Override
    protected DCP_FeeSetupSubjectQueryRes processJson(DCP_FeeSetupSubjectQueryReq req) throws Exception {
        DCP_FeeSetupSubjectQueryRes res = this.getResponseType();
        int totalRecords;                //总笔数
        int totalPages;
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);
        res.setRequest(new ArrayList<>());
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
                DCP_FeeSetupSubjectQueryRes.Request oneData = res.new Request();
                res.getRequest().add(oneData);

                oneData.setAccountId(data.get("ACCOUNTID").toString());
                oneData.setAccount(data.get("ACCOUNT").toString());
                oneData.setCoaRefID(data.get("COAREFID").toString());
                oneData.setStatus(data.get("STATUS").toString());

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
    protected String getQuerySql(DCP_FeeSetupSubjectQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.ACCOUNTID,a.COAREFID ) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" a.* ")
                .append(" ,b.ACCOUNT ")
                .append(" FROM ( SELECT DISTINCT EID,ACCOUNTID,COAREFID,STATUS FROM DCP_FEESETUPSUBJECT ) a ")
                .append(" LEFT JOIN DCP_ACOUNT_SETTING b on a.eid=b.eid and a.ACCOUNTID=b.ACCOUNTID ")
        ;

        sb.append(" WHERE a.EID='").append(req.geteId()).append("' ");
        if (StringUtils.isNotEmpty(req.getRequest().getAccountId())) {
            sb.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountId()).append("' ");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            sb.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("' ");
        }

        sb.append("  ) a "
                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY rn ");

        return sb.toString();
    }
}
