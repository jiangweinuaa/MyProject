package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ApBillEstQueryReq;
import com.dsc.spos.json.cust.res.DCP_ApBillEstQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_ApBillEstQuery extends SPosBasicService<DCP_ApBillEstQueryReq, DCP_ApBillEstQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ApBillEstQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ApBillEstQueryReq> getRequestType() {
        return new TypeToken<DCP_ApBillEstQueryReq>() {
        };
    }

    @Override
    protected DCP_ApBillEstQueryRes getResponseType() {
        return new DCP_ApBillEstQueryRes();
    }

    @Override
    protected DCP_ApBillEstQueryRes processJson(DCP_ApBillEstQueryReq req) throws Exception {
        DCP_ApBillEstQueryRes res = this.getResponseType();

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
        distinct.put("ARNO", true);
        List<Map<String, Object>> masterData = MapDistinct.getMap(queryData, distinct);

        for (Map<String, Object> oneMaster : masterData) {

            DCP_ApBillEstQueryRes.Datas oneData = res.new Datas();
            res.getDatas().add(oneData);

            oneData.setAccountId(oneMaster.get("ACCOUNTID").toString());
            oneData.setApType(oneMaster.get("APTYPE").toString());
            oneData.setCorp(oneMaster.get("CORP").toString());
            oneData.setTaskId(oneMaster.get("TASKID").toString());
            oneData.setStatus(oneMaster.get("STATUS").toString());

            oneData.setCreateBy(oneMaster.get("CREATEBY").toString());
            oneData.setCreate_Date(oneMaster.get("CREATE_DATE").toString());
            oneData.setCreate_Time(oneMaster.get("CREATE_TIME").toString());

            oneData.setModifyBy(oneMaster.get("MODIFYBY").toString());
            oneData.setModify_Date(oneMaster.get("MODIFY_DATE").toString());
            oneData.setModify_Time(oneMaster.get("MODIFY_TIME").toString());

            oneData.setConfirmBy(oneMaster.get("CONFIRMBY").toString());
            oneData.setConfirm_Date(oneMaster.get("CONFIRM_DATE").toString());
            oneData.setConfirm_Time(oneMaster.get("CONFIRM_TIME").toString());

            oneData.setCancelBy(oneMaster.get("CANCELBY").toString());
            oneData.setCancel_Date(oneMaster.get("CANCEL_DATE").toString());
            oneData.setCancel_Time(oneMaster.get("CANCEL_TIME").toString());


            Map<String, Object> condition = new HashMap<>();
            condition.put("EID", oneMaster.get("EID").toString());
            condition.put("APNO", oneMaster.get("APNO").toString());
            condition.put("ACCOUNTID", oneMaster.get("ACCOUNTID").toString());

            List<Map<String, Object>> detailData = MapDistinct.getWhereMap(queryData, condition, true);

            oneData.setEstList(new ArrayList<>());
            for (Map<String, Object> oneDetail : detailData) {
                DCP_ApBillEstQueryRes.EstList oneList = res.new EstList();
                oneData.getEstList().add(oneList);

                oneList.setAccountID(oneDetail.get("ACCOUNTID").toString());
                oneList.setSourceOrg(oneDetail.get("SOURCEORG").toString());
                oneList.setItem(oneDetail.get("ITEM").toString());
                oneList.setWrtOffType(oneDetail.get("WRTOFFTYPE").toString());
                oneList.setEstBillNo(oneDetail.get("ESTBILLNO").toString());
                oneList.setPeriod(oneDetail.get("PERIOD").toString());
                oneList.setWrtOffAPSubject(oneDetail.get("WRTOFFAPSUBJECT").toString());
                oneList.setExRate(oneDetail.get("EXRATE").toString());
                oneList.setFCYBTAmt(oneDetail.get("FCYBTAMT").toString());
                oneList.setFCYTAmt(oneDetail.get("FCYTAMT").toString());
                oneList.setFCYTATAmt(oneDetail.get("FCYTATAMT").toString());
                oneList.setLCYBTAmt(oneDetail.get("LCYBTAMT").toString());
                oneList.setLCYTAmt(oneDetail.get("LCYTAMT").toString());
                oneList.setLCYTATAmt(oneDetail.get("LCYTATAMT").toString());
                oneList.setLCYPrcDiffAmt(oneDetail.get("LCYPRCDIFFAMT").toString());
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
    protected String getQuerySql(DCP_ApBillEstQueryReq req) throws Exception {
        //分页处理
        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        StringBuilder builder = new StringBuilder();

        builder.append(" SELECT * FROM( ");
        builder.append(" SELECT count(1) over () as num,row_number() over (order by a.CUSTGROUPNO desc) as rn,")
                .append(" ,a.* ")
                .append(" ,b.TASKID,b.APTYPE ")
                .append(" FROM DCP_APBILLESTDTL a ")
                .append(" LEFT JOIN DCP_APBILL  b on a.EID=b.EID and a.APNO=b.APNO ")
        ;

        builder.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getAccountId())) {
            builder.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountId()).append("'");
        }

        if (StringUtils.isEmpty(req.getRequest().getApNo())) {
            builder.append(" AND a.APNO='").append(req.getRequest().getApNo()).append("'");
        }
        if (StringUtils.isEmpty(req.getRequest().getBizPartnerNo())) {
            builder.append(" AND b.BIZPARTNERNO='").append(req.getRequest().getBizPartnerNo()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            builder.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getTaskId())) {
            builder.append(" AND b.TASKID='").append(req.getRequest().getTaskId()).append("'");
        }

        builder.append(") temp  ");

        builder.append(" where  rn>").append(startRow).append(" and rn<=").append(startRow + pageSize).append("  ").append(" ");

        return builder.toString();
    }
}
