package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ShopSettBillQueryReq;
import com.dsc.spos.json.cust.res.DCP_PurInvReconQueryRes;
import com.dsc.spos.json.cust.res.DCP_ShopSettBillQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_ShopSettBillQuery extends SPosBasicService<DCP_ShopSettBillQueryReq, DCP_ShopSettBillQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ShopSettBillQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ShopSettBillQueryReq> getRequestType() {
        return new TypeToken<DCP_ShopSettBillQueryReq>() {
        };
    }

    @Override
    protected DCP_ShopSettBillQueryRes getResponseType() {
        return new DCP_ShopSettBillQueryRes();
    }

    @Override
    protected DCP_ShopSettBillQueryRes processJson(DCP_ShopSettBillQueryReq req) throws Exception {
        DCP_ShopSettBillQueryRes res = getResponseType();

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

            Map<String, Boolean> distinct = new HashMap<>();
            distinct.put("EID", true);
            distinct.put("PURINVNO", true);
            List<Map<String, Object>> distinctData = MapDistinct.getMap(getData, distinct);

            for (Map<String, Object> data : distinctData) {
                DCP_ShopSettBillQueryRes.Datas oneData = res.new Datas();
                res.getDatas().add(oneData);

                oneData.setStatus(data.get("STATUS").toString());
                oneData.setBDate(data.get("BDATE").toString());
                oneData.setReconNo(data.get("RECONNO").toString());
                oneData.setShopId(data.get("SHOPID").toString());
                oneData.setSourceType(data.get("SOURCETYPE").toString());
                oneData.setBizPartnerNo(data.get("BIZPARTNERNO").toString());
                oneData.setTot_Amt(data.get("TOT_AMT").toString());
                oneData.setPayAmt(data.get("PAYAMT").toString());
                oneData.setArNo(data.get("ARNO").toString());
                oneData.setArNo2(data.get("ARNO2").toString());

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
    protected String getQuerySql(DCP_ShopSettBillQueryReq req) throws Exception {
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
                .append(" FROM DCP_SHOPSETTBILL a ")
        ;

        sb.append(" WHERE a.EID='").append(req.geteId()).append("' ");

        if (StringUtils.isNotEmpty(req.getRequest().getShopId())) {
            sb.append(" AND a.SHOPID='").append(req.getRequest().getShopId()).append("' ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())) {
            sb.append(" AND to_char(a.BDATE,'YYYYMMDD') >= ").append(req.getRequest().getBeginDate()).append(" ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getEndDate())) {
            sb.append(" AND to_char(a.BDATE,'YYYYMMDD') <= ").append(req.getRequest().getEndDate()).append(" ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getSourceType())) {
            sb.append(" AND a.SOURCETYPE='").append(req.getRequest().getSourceType()).append("' ");
        }

        sb.append("  ) a "
                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY CORP ");

        return sb.toString();
    }
}
