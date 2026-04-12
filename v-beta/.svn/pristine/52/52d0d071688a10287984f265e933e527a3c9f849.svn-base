package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_BottomLevelQueryReq;
import com.dsc.spos.json.cust.res.DCP_BottomLevelQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_BottomLevelQuery extends SPosBasicService<DCP_BottomLevelQueryReq, DCP_BottomLevelQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_BottomLevelQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_BottomLevelQueryReq> getRequestType() {
        return new TypeToken<DCP_BottomLevelQueryReq>(){};
    }

    @Override
    protected DCP_BottomLevelQueryRes getResponseType() {
        return new DCP_BottomLevelQueryRes();
    }

    @Override
    protected DCP_BottomLevelQueryRes processJson(DCP_BottomLevelQueryReq req) throws Exception {
        DCP_BottomLevelQueryRes res = this.getResponseType();
        int totalRecords;                //总笔数
        int totalPages;
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);

        if (getData != null && !getData.isEmpty()) {
            //总页数
            String num = getData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            Map<String, Object> oneData = getData.get(0);
            DCP_BottomLevelQueryRes.Datas datas = res.new Datas();
            datas.setCorp(StringUtils.toString(oneData.get("CORP"),""));
            datas.setAccountID(StringUtils.toString(oneData.get("ACCOUNTID"),""));
            datas.setAccount(StringUtils.toString(oneData.get("ACCOUNT"),""));
            datas.setStatus(StringUtils.toString(oneData.get("STATUS"),""));

            res.setDatas(datas);

            res.getDatas().setBottomList(new ArrayList<>());
            for (Map<String, Object> data : getData) {
                DCP_BottomLevelQueryRes.BottomList bottomList = res.new BottomList();
                bottomList.setBottoLevel(StringUtils.toString(data.get("BOTTOMLEVEL"),""));
                bottomList.setItem(StringUtils.toString(data.get("ITEM"),""));
                bottomList.setPluNo(StringUtils.toString(data.get("PLUNO"),""));


                res.getDatas().getBottomList().add(bottomList);
            }


            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_BottomLevelQueryReq req) throws Exception {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.EID,ACCOUNTID,ITEM DESC) AS RN,COUNT(*) OVER ( ) NUM,A.* ")
                .append(" FROM DCP_BOTTOMLEVEL a ");

        sb.append("  )  a "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY a.EID,ACCOUNTID,ITEM ");
        return sb.toString();
    }
}
