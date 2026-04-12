package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ExportDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ExportDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ExportDetailQuery extends SPosBasicService<DCP_ExportDetailQueryReq, DCP_ExportDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ExportDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ExportDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ExportDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_ExportDetailQueryRes getResponseType() {
        return new DCP_ExportDetailQueryRes();
    }

    @Override
    protected DCP_ExportDetailQueryRes processJson(DCP_ExportDetailQueryReq req) throws Exception {
        DCP_ExportDetailQueryRes res = this.getResponseType();
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
                DCP_ExportDetailQueryRes.Datas oneData = res.new Datas();

                oneData.setAccountID(data.get("ACCOUNTID").toString());
                oneData.setAccount(data.get("ACCOUNT").toString());
                oneData.setYear(data.get("YEAR").toString());
                oneData.setPeriod(data.get("PERIOD").toString());
                oneData.setMainTaskId(data.get("MAINTASKID").toString());
                oneData.setExpStateInfo(data.get("IMPSTATEINFO").toString());
                oneData.setStatus(data.get("STATUS").toString());

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
    protected String getQuerySql(DCP_ExportDetailQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.MAINTASKID DESC) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" a.*,b.ACCOUNT ")
                .append(" FROM DCP_EXPORTDETAIL a ")
                .append(" LEFT JOIN DCP_ACOUNT_SETTING b on a.eid=b.eid and a.ACCOUNTID=b.ACCOUNTID ")

        ;

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getAccountId())) {
            sb.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountId()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getCerateOpId())) {
            sb.append(" AND a.CREATEOPID='").append(req.getRequest().getCerateOpId()).append("'");
        }

        sb.append("  ) a "
                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY MAINTASKID ");


        return sb.toString();
    }
}
