package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_InterSettBillQueryReq;
import com.dsc.spos.json.cust.res.DCP_InterSettBillQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_InterSettBillQuery extends SPosBasicService<DCP_InterSettBillQueryReq, DCP_InterSettBillQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_InterSettBillQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_InterSettBillQueryReq> getRequestType() {
        return new TypeToken<DCP_InterSettBillQueryReq>() {
        };
    }

    @Override
    protected DCP_InterSettBillQueryRes getResponseType() {
        return new DCP_InterSettBillQueryRes();
    }

    @Override
    protected DCP_InterSettBillQueryRes processJson(DCP_InterSettBillQueryReq req) throws Exception {
        DCP_InterSettBillQueryRes res = this.getResponseType();
        int totalRecords;                //总笔数
        int totalPages;
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);
        res.setDatas(res.new Datas());
        if (getData != null && !getData.isEmpty()) {
            //总页数
            String num = getData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            res.getDatas().setInterList(new ArrayList<>());
            for (Map<String, Object> data : getData) {
                DCP_InterSettBillQueryRes.InterList oneList = res.new InterList();
                res.getDatas().getInterList().add(oneList);

                oneList.setBDate(data.get("BDATE").toString());
                oneList.setSettAccPeriod(data.get("SETTACCPERIOD").toString());
                oneList.setBillNo(data.get("BILLNO").toString());
                oneList.setStatus(data.get("STATUS").toString());
                oneList.setCorp(data.get("CORP").toString());
                oneList.setCorpName(data.get("CORPNAME").toString());
                oneList.setArAmt(data.get("ARAMT").toString());
                oneList.setArPostedAmt(data.get("ARPOSTEDAMT").toString());
                oneList.setArNo(data.get("ARNO").toString());
                oneList.setApAmt(data.get("APAMT").toString());
                oneList.setApPostedAmt(data.get("APPOSTEDAMT").toString());
                oneList.setApNo(data.get("APNO").toString());
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
    protected String getQuerySql(DCP_InterSettBillQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.BILLNO DESC) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" a.*,o1.ORG_NAME CORPNAME,ar.FCYTATAMT ARAMT,ar.FCYREVAMT ARPOSTEDAMT " +
                        ",ap.FCYTATAMT APAMT,ap.FCYREVAMT APPOSTEDAMT ")
                .append(" FROM DCP_INTERSETTBILL a ")
                .append(" LEFT JOIN DCP_ARBILL ar on ar.eid=a.eid and ar.ARNO=a.ARNO ")
                .append(" LEFT JOIN DCP_APBILL ap on ap.eid=a.eid and ap.APNO=a.APNO ")
                .append(" LEFT JOIN DCP_ORG_LANG o1 ON a.eid=o1.eid and a.CORP=o1.ORGANIZATIONNO and o1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" INNER JOIN (" +
                        " SELECT EID,BILLNO,SUM(AMT) FROM DCP_INTERSETTBILLDETAIL a " +
                        " GROUP BY EID,BILLNO ) b on a.eid=b.eid and a.BILLNO=b.BILLNO ")
        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            sb.append(" AND a.CORP='").append(req.getRequest().getCorp()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getSettAccPeriod())) {
            sb.append(" AND a.SETTACCPERIOD='").append(req.getRequest().getSettAccPeriod()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            sb.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }

        sb.append("  ) a "
                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY rn ");

        return sb.toString();
    }
}
