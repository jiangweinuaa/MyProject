package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ExpenseQueryReq;
import com.dsc.spos.json.cust.res.DCP_ExpenseQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ExpenseQuery extends SPosBasicService<DCP_ExpenseQueryReq, DCP_ExpenseQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ExpenseQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ExpenseQueryReq> getRequestType() {
        return new TypeToken<DCP_ExpenseQueryReq>() {
        };
    }

    @Override
    protected DCP_ExpenseQueryRes getResponseType() {
        return new DCP_ExpenseQueryRes();
    }

    @Override
    protected DCP_ExpenseQueryRes processJson(DCP_ExpenseQueryReq req) throws Exception {
        DCP_ExpenseQueryRes res = this.getResponseType();
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
                DCP_ExpenseQueryRes.Datas oneData = res.new Datas();

                oneData.setBdate(data.get("BDATE").toString());
                oneData.setDoc_Type(data.get("DOC_TYPE").toString());
                oneData.setCorp(data.get("CORP").toString());
                oneData.setCorpName(data.get("CORPNAME").toString());
                oneData.setSupplierNo(data.get("SETTLEDBY").toString());
                oneData.setSupplierName(data.get("SETTLEDBYNAME").toString());
                oneData.setTot_Amt(data.get("TOT_AMT").toString());
                oneData.setBfeeNo(data.get("BFEENO").toString());
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
    protected String getQuerySql(DCP_ExpenseQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.BFEENO DESC) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" a.* ")
                .append(",b.SNAME SETTLEDBYNAME,ol1.ORG_NAME CORPNAME ")
                .append(" FROM DCP_EXPSHEET a ")
                .append(" LEFT JOIN DCP_BIZPARTNER b on a.EID=b.EID and a.SETTLEDBY=b.BIZPARTNERNO ")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 on ol1.EID=a.EID and ol1.ORGANIZATIONNO=a.CORP and ol1.LANG_TYPE='").append(req.getLangType()).append("'")
        ;

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            sb.append(" and a.CORP='").append(req.getRequest().getCorp()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getBdate())) {
            sb.append(" and a.BDATE='").append(DateFormatUtils.getPlainDate(req.getRequest().getBdate())).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())){
            sb.append(" and a.BDATE>='").append(DateFormatUtils.getPlainDate(req.getRequest().getBeginDate())).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getEndDate())){
            sb.append(" and a.BDATE<='").append(DateFormatUtils.getPlainDate(req.getRequest().getEndDate())).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getDoc_Type())) {
            sb.append(" and a.DOC_TYPE='").append(req.getRequest().getDoc_Type()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getSupplierNo())) {
            sb.append(" and a.SETTLEDBY='").append(req.getRequest().getSupplierNo()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            sb.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }

        sb.append("  ) a "
                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY BFEENO ");

        return sb.toString();
    }
}
