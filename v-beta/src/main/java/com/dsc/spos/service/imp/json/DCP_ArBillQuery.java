package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ArBillQueryReq;
import com.dsc.spos.json.cust.res.DCP_ArBillQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ArBillQuery extends SPosBasicService<DCP_ArBillQueryReq, DCP_ArBillQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ArBillQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ArBillQueryReq> getRequestType() {
        return new TypeToken<DCP_ArBillQueryReq>() {
        };
    }

    @Override
    protected DCP_ArBillQueryRes getResponseType() {
        return new DCP_ArBillQueryRes();
    }

    @Override
    protected DCP_ArBillQueryRes processJson(DCP_ArBillQueryReq req) throws Exception {
        DCP_ArBillQueryRes res = this.getResponseType();

        List<Map<String, Object>> queryData = doQueryData(this.getQuerySql(req), null);
        List<Map<String, Object>> totData = doQueryData(this.getQueryTotSql(req), null);
        int totalRecords = 0;    //总笔数
        int totalPages = 0;        //总页数

        res.setDatas(new ArrayList<>());
        if (CollectionUtils.isNotEmpty(queryData)) {
            String num = queryData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;


            Map<String, Boolean> distinct = new HashMap<>();
            distinct.put("EID", true);
            distinct.put("ACCOUNTID", true);
            distinct.put("TASKID", true);
            List<Map<String, Object>> masterData = MapDistinct.getMap(queryData, distinct);

            for (Map<String, Object> oneMaster : masterData) {

                DCP_ArBillQueryRes.Datas oneData = res.new Datas();
                res.getDatas().add(oneData);

                oneData.setAccountId(oneMaster.get("ACCOUNTID").toString());
                oneData.setAccountName(oneMaster.get("ACCOUNTNAME").toString());
                oneData.setTaskId(oneMaster.get("TASKID").toString());

                double totfCYTATAmt = 0;
                double totfCYRevAmt = 0;
                double totunPaidAmt = 0;
                Map<String, Object> condition = new HashMap<>();
                condition.put("EID", oneMaster.get("EID").toString());
                condition.put("TASKID", oneMaster.get("TASKID").toString());
                condition.put("ACCOUNTID", oneMaster.get("ACCOUNTID").toString());

                List<Map<String, Object>> detailData = MapDistinct.getWhereMap(queryData, condition, true);

                oneData.setArList(new ArrayList<>());
                for (Map<String, Object> oneDetail : detailData) {

                    DCP_ArBillQueryRes.ArList oneAr = res.new ArList();
                    oneData.getArList().add(oneAr);

                    oneAr.setTaskId(oneDetail.get("TASKID").toString());
                    oneAr.setAccountId(oneDetail.get("ACCOUNTID").toString());
                    oneAr.setAccountName(oneDetail.get("ACCOUNTNAME").toString());

                    oneAr.setBizPartnerNo(oneDetail.get("BIZPARTNERNO").toString());
                    oneAr.setBizPartnerName(oneDetail.get("BIZPARTNERNAME").toString());
                    oneAr.setArNo(oneDetail.get("ARNO").toString());
                    oneAr.setPDate(oneDetail.get("PDATE").toString());

                    totfCYTATAmt += Double.parseDouble(oneDetail.get("FCYTATAMT").toString());
                    totfCYRevAmt += Double.parseDouble(oneDetail.get("FCYREVAMT").toString());

                    double unpaidAmt = Double.parseDouble(oneDetail.get("FCYTATAMT").toString())
                            - Double.parseDouble(oneDetail.get("FCYREVAMT").toString());
                    totunPaidAmt += unpaidAmt;

                    oneAr.setFCYTATAmt(oneDetail.get("FCYTATAMT").toString());
                    oneAr.setFCYRevAmt(oneDetail.get("FCYREVAMT").toString());
                    oneAr.setUnPaidAmt(String.valueOf(unpaidAmt));
                    oneAr.setStatus(oneDetail.get("STATUS").toString());

                }

                oneData.setTotfCYTATAmt(String.valueOf(totfCYTATAmt));
                oneData.setTotfCYRevAmt(String.valueOf(totfCYRevAmt));
                oneData.setTotunPaidAmt(String.valueOf(totunPaidAmt));

                oneData.setTotList(new ArrayList<>());

                Map<String, Object> totCondition = new HashMap<>();
                totCondition.put("EID", oneMaster.get("EID").toString());
                totCondition.put("ACCOUNTID", oneMaster.get("ACCOUNTID").toString());
                List<Map<String, Object>> totFilter = MapDistinct.getWhereMap(queryData, totCondition, true);

                DCP_ArBillQueryRes.TotList oneTot = res.new TotList();
                oneData.getTotList().add(oneTot);

                BigDecimal ytdFcytatAmt = totFilter.stream().map(x -> {
                    BigDecimal fcytatAmt = new BigDecimal(Check.Null(x.get("FCYTATAMT").toString()) ? "0" : x.get("FCYTATAMT").toString());
                    if (x.get("TASKID").toString().equals("8") || x.get("TASKID").toString().equals("9")) {
                        fcytatAmt = fcytatAmt.multiply(BigDecimal.valueOf(-1));
                    }
                    return fcytatAmt;
                }).reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal ytdFcyrevAmt = totFilter.stream().map(x -> {
                    BigDecimal fcyrevAmt = new BigDecimal(Check.Null(x.get("FCYREVAMT").toString()) ? "0" : x.get("FCYREVAMT").toString());
                    if (x.get("TASKID").toString().equals("8") || x.get("TASKID").toString().equals("9")) {
                        fcyrevAmt = fcyrevAmt.multiply(BigDecimal.valueOf(-1));
                    }
                    return fcyrevAmt;
                }).reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal ytdUnpaidAmt = ytdFcytatAmt.subtract(ytdFcyrevAmt);

                BigDecimal totFCYATAmt = totFilter.stream().map(x -> {
                    BigDecimal fcytatAmt = new BigDecimal(Check.Null(x.get("FCYTATAMT").toString()) ? "0" : x.get("FCYTATAMT").toString());
                    if (x.get("TASKID").toString().equals("8") || x.get("TASKID").toString().equals("9")) {
                        fcytatAmt = fcytatAmt.multiply(BigDecimal.valueOf(-1));
                    }
                    return fcytatAmt;
                }).reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totFCYRevAmt = totFilter.stream().map(x -> {
                    BigDecimal fcyrevAmt = new BigDecimal(Check.Null(x.get("FCYREVAMT").toString()) ? "0" : x.get("FCYREVAMT").toString());
                    if (x.get("TASKID").toString().equals("8") || x.get("TASKID").toString().equals("9")) {
                        fcyrevAmt = fcyrevAmt.multiply(BigDecimal.valueOf(-1));
                    }
                    return fcyrevAmt;
                }).reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totUnPaidAmt = totFCYATAmt.subtract(totFCYRevAmt);

                oneTot.setAccountId(oneMaster.get("ACCOUNTID").toString());
                oneTot.setAccountName(oneMaster.get("ACCOUNTNAME").toString());

                oneTot.setTotFCYATAmt(totFCYATAmt.toString());
                oneTot.setTotFCYRevAmt(totFCYRevAmt.toString());
                oneTot.setTotUnPaidAmt(totUnPaidAmt.toString());
                oneTot.setYTDFCYATAmt(ytdFcytatAmt.toString());
                oneTot.setYTDFCYRevAmt(ytdFcyrevAmt.toString());
                oneTot.setYTDUnPaidAmt(ytdUnpaidAmt.toString());

                List<Map<String, Object>> confirmRows = totFilter.stream().filter(x -> x.get("STATUS").toString().equals("2")).collect(Collectors.toList());
                oneTot.setTotConfirm(confirmRows.size() + "");
                List<Map<String, Object>> insertRows = totFilter.stream().filter(x -> x.get("STATUS").toString().equals("1")).collect(Collectors.toList());
                oneTot.setTotInsert(insertRows.size() + "");

                List<Map<String, Object>> glNoFilter = confirmRows.stream().filter(x -> Check.Null(x.get("GLNO").toString())).collect(Collectors.toList());
                oneTot.setUngenVoucher(glNoFilter.size() + "");

//                List<Map<String, Object>> settFilterRows = totFilter.stream().filter(x -> x.get("ACCOUNTID").toString().equals(toList.getAccountId())).collect(Collectors.toList());
//                toList.setTotCreateAP(settFilterRows.size() + "");

                //没必要用循环
//                for (Map<String, Object> oneSum : detailData) {
//                    DCP_ArBillQueryRes.TotList oneTot = res.new TotList();
//                    oneData.getTotList().add(oneTot);
//
//                    oneTot.setAccountId(oneSum.get("ACCOUNTID").toString());
//                    oneTot.setTotFCYATAmt(oneSum.get("TOTFCYATAMT").toString());
//                    oneTot.setTotFCYRevAmt(oneSum.get("TOTFCYREVAMT").toString());
//                    oneTot.setTotUnPaidAmt(oneSum.get("TOTUNPAIDAMT").toString());
//                    oneTot.setYTDFCYATAmt(oneSum.get("YTDFCYATAMT").toString());
//                    oneTot.setYTDFCYRevAmt(oneSum.get("YTDFCYREVAMT").toString());
//                    oneTot.setYTDUnPaidAmt(oneSum.get("YTDUNPAIDAMT").toString());
//                    oneTot.setTotConfirm(oneSum.get("TOTCONFIRM").toString());
//                    oneTot.setTotInsert(oneSum.get("TOTINSERT").toString());
//                    oneTot.setUngenVoucher(oneSum.get("UNGENVOUCHER").toString());
//
//                }


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

    protected String getQueryTotSql(DCP_ArBillQueryReq req) {
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT *  ")
                .append(" FROM DCP_ARBILL a ")
        ;
        builder.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (Check.isNotEmpty(req.getRequest().getAccountId())) {
            builder.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountId()).append("'");
        }
        String nowDate = DateFormatUtils.getNowPlainDate();
        if (Check.isNotEmpty(req.getRequest().getEndDate())) {
            nowDate = DateFormatUtils.getPlainDate(req.getRequest().getEndDate());
        }
        String year = nowDate.substring(0, 4);
        builder.append(" AND a.PDATE like '").append(year).append("%%'");

        return builder.toString();
    }


    @Override
    protected String getQuerySql(DCP_ArBillQueryReq req) throws Exception {
        //分页处理
        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        StringBuilder builder = new StringBuilder();

        builder.append(" SELECT * FROM( ");
        builder.append(" SELECT count(1) over () as num,row_number() over (order by a.ARNO desc) as rn ")
                .append(" ,a.* ")
                .append(" ,das.ACCOUNT ACCOUNTNAME,c.SNAME BIZPARTNERNAME ")
                .append(" FROM DCP_ARBILL a ")
                .append(" LEFT JOIN DCP_ACOUNT_SETTING das on a.eid=das.eid and das.ACCOUNTID=a.ACCOUNTID ")
                .append(" left join dcp_bizpartner c on c.eid=a.eid and c.BIZPARTNERNO=a.BIZPARTNERNO ")
        ;

        builder.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getAccountId())) {
            builder.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountId()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            builder.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getTaskId())) {
            builder.append(" AND ( 1=2 ");
            for (String taskId : req.getRequest().getTaskId()) {
                builder.append(" OR a.TASKID='").append(taskId).append("'");
            }
            builder.append(")");
        }

//        if (StringUtils.isNotEmpty(req.getRequest().getTaskId())) {
//            builder.append(" AND a.TASKID='").append(req.getRequest().getTaskId()).append("'");
//        }

        if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())) {
            builder.append(" AND PDATE >= '").append(DateFormatUtils.getPlainDate(req.getRequest().getBeginDate())).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getEndDate())) {
            builder.append(" AND PDATE <= '").append(DateFormatUtils.getPlainDate(req.getRequest().getEndDate())).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getBizPartnerNo())) {
            builder.append(" AND a.BIZPARTNERNO='").append(req.getRequest().getBizPartnerNo()).append("'");
        }
//        if (StringUtils.isNotEmpty(req.getRequest().getIsPmtOffset())) {
//            builder.append(" AND a.BIZPARTNERNO='").append(req.getRequest().getIsPmtOffset()).append("'");
//        }

        builder.append(") temp  ");

        builder.append(" where  rn>").append(startRow).append(" and rn<=").append(startRow + pageSize).append("  ").append(" ");

        return builder.toString();
    }
}
