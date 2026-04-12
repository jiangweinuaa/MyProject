package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PurInvReconQueryReq;
import com.dsc.spos.json.cust.res.DCP_PurInvReconQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_PurInvReconQuery extends SPosBasicService<DCP_PurInvReconQueryReq, DCP_PurInvReconQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_PurInvReconQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PurInvReconQueryReq> getRequestType() {
        return new TypeToken<DCP_PurInvReconQueryReq>() {
        };
    }

    @Override
    protected DCP_PurInvReconQueryRes getResponseType() {
        return new DCP_PurInvReconQueryRes();
    }

    @Override
    protected DCP_PurInvReconQueryRes processJson(DCP_PurInvReconQueryReq req) throws Exception {
        DCP_PurInvReconQueryRes res = this.getResponseType();

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

//            Map<String, Boolean> distinct = new HashMap<>();
//            distinct.put("EID", true);
//            distinct.put("PURINVNO", true);
//            distinct.put("CORP", true);
//            List<Map<String, Object>> distinctData = MapDistinct.getMap(getData, distinct);

            for (Map<String, Object> data : getData) {
                DCP_PurInvReconQueryRes.Datas oneData = res.new Datas();
                res.getDatas().add(oneData);

                oneData.setStatus(data.get("STATUS").toString());
                oneData.setBDate(data.get("BDATE").toString());
                oneData.setPurInvNo(data.get("PURINVNO").toString());
                oneData.setCorp(data.get("CORP").toString());
                oneData.setCorpName(data.get("CORPNAME").toString());
                oneData.setBizPartnerNo(data.get("BIZPARTNERNO").toString());
                oneData.setBizPartnerName(data.get("BIZPARTNERNAME").toString());
                oneData.setInvoiceCode(data.get("INVOICECODE").toString());
                oneData.setTotInvFCYTAmt(data.get("INVFCYTAMT").toString());
                oneData.setTotInvFCYBTAmt(data.get("INVFCYBTAMT").toString());
                oneData.setTotInvFCYATAmt(data.get("INVFCYATAMT").toString());
                oneData.setApNo(data.get("APNO").toString());

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
    protected String getQuerySql(DCP_PurInvReconQueryReq req) throws Exception {

        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
//        1：详情；2：简查
        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.CORP ) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" a.* ")
                .append(" ,ol1.ORG_NAME CORPNAME,bz1.SNAME BIZPARTNERNAME  ")
                .append(" FROM DCP_PURINVRECON a ")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 on ol1.EID=a.EID and ol1.ORGANIZATIONNO=a.CORP AND ol1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_BIZPARTNER bz1 on bz1.EID=a.EID and bz1.BIZPARTNERNO=a.BIZPARTNERNO ")
        ;

        sb.append(" WHERE a.EID='").append(req.geteId()).append("' ");

        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            sb.append(" AND a.CORP='").append(req.getRequest().getCorp()).append("' ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())) {
            sb.append(" AND to_char(a.BDATE,'YYYYMMDD') >= ").append(req.getRequest().getBeginDate()).append(" ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getEndDate())) {
            sb.append(" AND to_char(a.BDATE,'YYYYMMDD') <= ").append(req.getRequest().getEndDate()).append(" ");
        }


        sb.append("  ) a "
                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY rn ");

        return sb.toString();
    }
}
