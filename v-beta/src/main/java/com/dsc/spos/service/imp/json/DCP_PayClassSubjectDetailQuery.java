package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PayClassSubjectDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_PayClassSubjectDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_PayClassSubjectDetailQuery extends SPosBasicService<DCP_PayClassSubjectDetailQueryReq, DCP_PayClassSubjectDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_PayClassSubjectDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PayClassSubjectDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_PayClassSubjectDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_PayClassSubjectDetailQueryRes getResponseType() {
        return new DCP_PayClassSubjectDetailQueryRes();
    }

    @Override
    protected DCP_PayClassSubjectDetailQueryRes processJson(DCP_PayClassSubjectDetailQueryReq req) throws Exception {
        DCP_PayClassSubjectDetailQueryRes res = this.getResponseType();
        int totalRecords;                //总笔数
        int totalPages;
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);
        res.setRequest(new ArrayList<>());
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
            distinct.put("ACCOUNTID", true);
            distinct.put("COAREFID", true);
            List<Map<String, Object>> distinctData = MapDistinct.getMap(getData, distinct);

            for (Map<String, Object> data : distinctData) {
                DCP_PayClassSubjectDetailQueryRes.Request oneData = res.new Request();
                res.getRequest().add(oneData);

                oneData.setAccountId(data.get("ACCOUNTID").toString());
                oneData.setAccount(data.get("ACCOUNT").toString());
                oneData.setCoaRefID(data.get("COAREFID").toString());
                oneData.setStatus(data.get("STATUS").toString());

                Map<String, Object> condition = new HashMap<>();
                condition.put("EID", data.get("EID").toString());
                condition.put("ACCOUNTID", data.get("ACCOUNTID").toString());
                condition.put("COAREFID", data.get("COAREFID").toString());

                List<Map<String, Object>> detail = MapDistinct.getWhereMap(getData, condition, true);
                oneData.setClassList(new ArrayList<>());
                for (Map<String, Object> data2 : detail) {
                    DCP_PayClassSubjectDetailQueryRes.ClassList oneClass = res.new ClassList();
                    oneData.getClassList().add(oneClass);

                    oneClass.setItem(data2.get("RN").toString());
                    oneClass.setClassNo(data2.get("CLASSNO").toString());
                    oneClass.setClassName(data2.get("CLASSNAME").toString());
                    oneClass.setDebitSubject(data2.get("DEBITSUBJECT").toString());
                    oneClass.setDebitSubjectName(data2.get("DEBITSUBJECTNAME").toString());
                    oneClass.setPaySubject(data2.get("PAYSUBJECT").toString());
                    oneClass.setPaySubjectName(data2.get("PAYSUBJECTNAME").toString());
                    oneClass.setRevSubject(data2.get("REVSUBJECT").toString());
                    oneClass.setRevSubjectName(data2.get("REVSUBJECTNAME").toString());
                    oneClass.setAdvSubject(data2.get("ADVSUBJECT").toString());
                    oneClass.setAdvSubjectName(data2.get("ADVSUBJECTNAME").toString());


                }

                oneData.setCreateBy(data.get("CREATEBY").toString());
                oneData.setCreateByName(data.get("CREATEBYNAME").toString());
                oneData.setCreate_Date(data.get("CREATE_DATE").toString());
                oneData.setCreate_Time(data.get("CREATE_TIME").toString());
                oneData.setModifyBy(data.get("MODIFYBY").toString());
                oneData.setModifyByName(data.get("MODIFYBYNAME").toString());
                oneData.setModify_Date(data.get("MODIFY_DATE").toString());
                oneData.setModify_Time(data.get("MODIFY_TIME").toString());
                oneData.setConfirmBy(data.get("CONFIRMBY").toString());
                oneData.setConfirmByName(data.get("CONFIRMBYNAME").toString());
                oneData.setConfirm_Date(data.get("CONFIRM_DATE").toString());
                oneData.setConfirm_Time(data.get("CONFIRM_TIME").toString());
                oneData.setCancelBy(data.get("CANCELBY").toString());
                oneData.setCancelByName(data.get("CANCELBYNAME").toString());
                oneData.setCancel_Date(data.get("CANCEL_DATE").toString());
                oneData.setCancel_Time(data.get("CANCEL_TIME").toString());

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
    protected String getQuerySql(DCP_PayClassSubjectDetailQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.COAREFID ) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" a.* ")
                .append(" ,b.ACCOUNT ")
                .append(" ,e1.NAME CREATEBYNAME,e2.NAME MODIFYBYNAME,e3.NAME CONFIRMBYNAME,e4.NAME CANCELBYNAME ")
                .append(" ,pl1.PAYNAME CLASSNAME,ca1.SUBJECTNAME DEBITSUBJECTNAME,ca2.SUBJECTNAME PAYSUBJECTNAME,ca3.SUBJECTNAME REVSUBJECTNAME,ca4.SUBJECTNAME ADVSUBJECTNAME  ")
                .append(" FROM DCP_PAYClASSSUBJECT  a ")
                .append(" LEFT JOIN DCP_ACOUNT_SETTING b on a.eid=b.eid and a.ACCOUNTID=b.ACCOUNTID AND b.ACCTTYPE='1'")
                .append(" LEFT JOIN DCP_COA ca1 on a.eid=ca1.eid and a.DEBITSUBJECT=ca1.SUBJECTID and a.ACCOUNTID=ca1.ACCOUNTID")
                .append(" LEFT JOIN DCP_COA ca2 on a.eid=ca2.eid and a.PAYSUBJECT=ca2.SUBJECTID and a.ACCOUNTID=ca2.ACCOUNTID")
                .append(" LEFT JOIN DCP_COA ca3 on a.eid=ca3.eid and a.REVSUBJECT=ca3.SUBJECTID and a.ACCOUNTID=ca3.ACCOUNTID")
                .append(" LEFT JOIN DCP_COA ca4 on a.eid=ca4.eid and a.ADVSUBJECT=ca4.SUBJECTID and a.ACCOUNTID=ca4.ACCOUNTID")
                .append(" left join DCP_PAYTYPE_LANG pl1 on pl1.eid=a.eid and pl1.PAYTYPE=a.CLASSNO AND pl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" left join dcp_employee e1 on e1.eid=a.eid and e1.employeeno=a.CREATEBY ")
                .append(" left join dcp_employee e2 on e2.eid=a.eid and e2.employeeno=a.MODIFYBY ")
                .append(" left join dcp_employee e3 on e3.eid=a.eid and e3.employeeno=a.CONFIRMBY ")
                .append(" left join dcp_employee e4 on e4.eid=a.eid and e4.employeeno=a.CANCELBY ")

        ;

        sb.append(" WHERE a.EID='").append(req.geteId()).append("' ");
        if (StringUtils.isNotEmpty(req.getRequest().getAccountId())) {
            sb.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountId()).append("' ");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getCoaRefID())) {
            sb.append(" AND a.COAREFID='").append(req.getRequest().getCoaRefID()).append("' ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            sb.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("' ");
        }

        sb.append("  ) a "
                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY rn ");

        return sb.toString();
    }
}
