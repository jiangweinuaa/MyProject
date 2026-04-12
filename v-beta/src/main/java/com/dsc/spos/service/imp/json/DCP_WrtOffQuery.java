package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_WrtOffQueryReq;
import com.dsc.spos.json.cust.res.DCP_WrtOffQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_WrtOffQuery extends SPosBasicService<DCP_WrtOffQueryReq, DCP_WrtOffQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_WrtOffQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_WrtOffQueryReq> getRequestType() {
        return new TypeToken<DCP_WrtOffQueryReq>() {
        };
    }

    @Override
    protected DCP_WrtOffQueryRes getResponseType() {
        return new DCP_WrtOffQueryRes();
    }

    @Override
    protected DCP_WrtOffQueryRes processJson(DCP_WrtOffQueryReq req) throws Exception {
        DCP_WrtOffQueryRes res = this.getResponseType();

        List<Map<String, Object>> queryData = doQueryData(this.getQuerySql(req), null);
        int totalRecords;    //总笔数
        int totalPages;        //总页数
        String num = queryData.get(0).get("NUM").toString();
        totalRecords = Integer.parseInt(num);
        totalPages = totalRecords / req.getPageSize();
        totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

        res.setDatas(new ArrayList<>());
        Map<String, Boolean> distinct = new HashMap<>();
        distinct.put("EID", true);
        distinct.put("ACCOUNTID", true);

        List<Map<String, Object>> masterData = MapDistinct.getMap(queryData, distinct);

        for (Map<String, Object> oneMaster : masterData) {

            DCP_WrtOffQueryRes.Datas oneData = res.new Datas();
            oneData.setAccountId(oneMaster.get("ACCOUNTID").toString());
            oneData.setApList(new ArrayList<>());

            Map<String, Object> condition = new HashMap<>();
            condition.put("EID", oneMaster.get("EID").toString());
            condition.put("ACCOUNTID", oneMaster.get("ACCOUNTID").toString());

            List<Map<String, Object>> detailData = MapDistinct.getWhereMap(queryData, condition, true);
            for (Map<String, Object> oneDetail : detailData) {
                DCP_WrtOffQueryRes.ApList oneApList = res.new ApList();
                oneData.getApList().add(oneApList);

                oneApList.setTaskId(oneDetail.get("TASKID").toString());
                oneApList.setApNo(oneDetail.get("APNO").toString());
                oneApList.setPDate(oneDetail.get("CREATE_DATE").toString());
                oneApList.setBizPartnerNo(oneDetail.get("BIZPARTNERNO").toString());
                oneApList.setApSubjectId(oneDetail.get("APSUBJECTID").toString());
//                oneApList.setUnPaidAmt(oneDetail.get("UNPAIDAMT").toString());
                oneApList.setStatus(oneDetail.get("STATUS").toString());
                oneApList.setMemo(oneDetail.get("MEMO").toString());

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
    protected String getQuerySql(DCP_WrtOffQueryReq req) throws Exception {
        //分页处理
        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT * FROM( ");
        builder.append(" SELECT count(*) over () as num" +
                        " ,row_number() over (order by a.WRTOFFNO,a.ITEM ) as rn ")
                .append(" ,a.*  ")
                .append(" ,b.BIZPARTNERNO,b.APSUBJECTID ")
                .append(" FROM DCP_ARBILLWRTOFF a ")
                .append(" LEFT JOIN DCP_APBILL b on a.eid=b.eid and a.APNO=b.APNO ")
        ;

        builder.append(" WHERE a,EID='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getAccountId())) {
            builder.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountId()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getWrtOffType())) {
            builder.append(" AND a.WRTOFFNO='").append(req.getRequest().getWrtOffType()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            builder.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getBizPartnerNo())){
            builder.append(" AND b.BIZPARTNERNO='").append(req.getRequest().getBizPartnerNo()).append("'");
        }

        builder.append(") temp  ");
        builder.append(" where  rn>").append(startRow).append(" and rn<=").append(startRow + pageSize).append("  order by rn");

        return builder.toString();
    }
}
