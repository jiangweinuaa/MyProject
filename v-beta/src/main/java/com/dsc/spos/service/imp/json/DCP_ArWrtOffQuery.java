package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ArWrtOffQueryReq;
import com.dsc.spos.json.cust.res.DCP_ArWrtOffQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_ArWrtOffQuery extends SPosBasicService<DCP_ArWrtOffQueryReq, DCP_ArWrtOffQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ArWrtOffQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ArWrtOffQueryReq> getRequestType() {
        return new TypeToken<DCP_ArWrtOffQueryReq>() {
        };
    }

    @Override
    protected DCP_ArWrtOffQueryRes getResponseType() {
        return new DCP_ArWrtOffQueryRes();
    }

    @Override
    protected DCP_ArWrtOffQueryRes processJson(DCP_ArWrtOffQueryReq req) throws Exception {
        DCP_ArWrtOffQueryRes res = this.getResponseType();
        List<Map<String, Object>> queryData = doQueryData(this.getQuerySql(req), null);
        int totalRecords;    //总笔数
        int totalPages;        //总页数

        if (CollectionUtils.isNotEmpty(queryData)) {
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

                DCP_ArWrtOffQueryRes.Datas oneData = res.new Datas();
                res.getDatas().add(oneData);
                oneData.setTaskId(oneMaster.get("TASKID").toString());
                oneData.setStatus(oneMaster.get("STATUS").toString());
                oneData.setAccountId(oneMaster.get("ACCOUNTID").toString());
                oneData.setAccountName(oneMaster.get("ACCOUNTNAME").toString());
                oneData.setArNo(oneMaster.get("ARNO").toString());
                oneData.setBizPartnerNo(oneMaster.get("BIZPARTNERNO").toString());
                oneData.setBizPartnerName(oneMaster.get("BIZPARTNERNAME").toString());
                oneData.setBDate(oneMaster.get("PDATE").toString());
                oneData.setFCYTATAmt(oneMaster.get("FCYTATAMT").toString());
                oneData.setGlNo(oneMaster.get("GLNO").toString());

            }
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
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
    protected String getQuerySql(DCP_ArWrtOffQueryReq req) throws Exception {
        //分页处理
        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        StringBuilder builder = new StringBuilder();

        builder.append(" SELECT * FROM( ");
        builder.append(" SELECT count(1) over () as num,row_number() over (order by a.arno desc) as rn ")
                .append(" ,a.* ")
                .append("  ")
                .append(" FROM DCP_ARWRTOFF  a ")
                .append(" LEFT JOIN DCP_ARBILL b on a.eid=b.eid and a.ARNO=b.ARNO and a.ACCOUNTID=b.ACCOUNTID ")
        ;

        builder.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (Check.isNotEmpty(req.getRequest().getAccountId())) {
            builder.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountId()).append("'");
        }


        if (Check.isNotEmpty(req.getRequest().getStatus())) {
            builder.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }

        if (Check.isNotEmpty(req.getRequest().getTaskId())) {
            builder.append(" AND b.TASKID='").append(req.getRequest().getTaskId()).append("'");
        }

        if (Check.isNotEmpty(req.getRequest().getBizPartnerNo())) {
            builder.append(" AND b.BIZPARTNERNO='").append(req.getRequest().getTaskId()).append("'");
        }

        builder.append(") temp  ");

        builder.append(" where  rn>").append(startRow).append(" and rn<=").append(startRow + pageSize).append("  ").append(" ");

        return builder.toString();
    }


}
