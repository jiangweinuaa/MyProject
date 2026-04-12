package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_FeeSetupSubjectDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_FeeSetupSubjectDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_FeeSetupSubjectDetailQuery extends SPosBasicService<DCP_FeeSetupSubjectDetailQueryReq, DCP_FeeSetupSubjectDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_FeeSetupSubjectDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_FeeSetupSubjectDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_FeeSetupSubjectDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_FeeSetupSubjectDetailQueryRes getResponseType() {
        return new DCP_FeeSetupSubjectDetailQueryRes();
    }

    @Override
    protected DCP_FeeSetupSubjectDetailQueryRes processJson(DCP_FeeSetupSubjectDetailQueryReq req) throws Exception {
        DCP_FeeSetupSubjectDetailQueryRes res = this.getResponseType();
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
                DCP_FeeSetupSubjectDetailQueryRes.Request oneData = res.new Request();
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
                oneData.setSetupList(new ArrayList<>());
                for (Map<String, Object> data2 : detail) {
                    DCP_FeeSetupSubjectDetailQueryRes.SetupList oneSet = res.new SetupList();
                    oneData.getSetupList().add(oneSet);

                    oneSet.setItem(data2.get("RN").toString());
                    oneSet.setAccountID(data2.get("ACCOUNTID").toString());
                    oneSet.setCoaRefID(data2.get("COAREFID").toString());
                    oneSet.setFee(data2.get("FEE").toString());
                    oneSet.setFeeName(data2.get("FEENAME").toString());
                    oneSet.setFeeNature(data2.get("FEENATURE").toString());
                    oneSet.setAccSubject(data2.get("ACCSUBJECT").toString());
                    oneSet.setAccSubjectName(data2.get("ACCSUBJECTNAME").toString());
                    oneSet.setRevSubject(data2.get("REVSUBJECT").toString());
                    oneSet.setRevSubjectName(data2.get("REVSUBJECTNAME").toString());
                    oneSet.setAdvSubject(data2.get("ADVSUBJECT").toString());
                    oneSet.setAdvSubjectName(data2.get("ADVSUBJECTNAME").toString());

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
    protected String getQuerySql(DCP_FeeSetupSubjectDetailQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.ACCOUNTID,a.COAREFID ) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" a.* ")
                .append(" ,b.ACCOUNT,c.FEE_NAME FEENAME ")
                .append(" ,e1.NAME CREATEBYNAME,e2.NAME MODIFYBYNAME,e3.NAME CONFIRMBYNAME,e4.NAME CANCELBYNAME ")
                .append(" ,ca1.SUBJECTNAME ACCSUBJECTNAME,ca2.SUBJECTNAME REVSUBJECTNAME,ca3.SUBJECTNAME ADVSUBJECTNAME ")
                .append(" FROM DCP_FEESETUPSUBJECT  a ")
                .append(" LEFT JOIN DCP_ACOUNT_SETTING b on a.eid=b.eid and a.ACCOUNTID=b.ACCOUNTID ")
                .append(" LEFT JOIN DCP_FEE c on a.eid=c.eid and a.FEE=c.FEE ")
                .append(" LEFT JOIN DCP_COA ca1 on a.eid=ca1.eid and a.ACCSUBJECT=ca1.SUBJECTID and a.ACCOUNTID=ca1.ACCOUNTID")
                .append(" LEFT JOIN DCP_COA ca2 on a.eid=ca2.eid and a.REVSUBJECT=ca2.SUBJECTID and a.ACCOUNTID=ca2.ACCOUNTID")
                .append(" LEFT JOIN DCP_COA ca3 on a.eid=ca3.eid and a.ADVSUBJECT=ca3.SUBJECTID and a.ACCOUNTID=ca3.ACCOUNTID")
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
