package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ArPrePayDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ArPrePayDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ArPrePayDetailQuery extends SPosBasicService<DCP_ArPrePayDetailQueryReq, DCP_ArPrePayDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ArPrePayDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ArPrePayDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ArPrePayDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_ArPrePayDetailQueryRes getResponseType() {
        return new DCP_ArPrePayDetailQueryRes();
    }

    @Override
    protected DCP_ArPrePayDetailQueryRes processJson(DCP_ArPrePayDetailQueryReq req) throws Exception {
        DCP_ArPrePayDetailQueryRes res = this.getResponseType();
        List<Map<String, Object>> queryData = doQueryData(this.getQuerySql(req), null);
//        int totalRecords;    //总笔数
//        int totalPages;        //总页数
        String num = queryData.get(0).get("NUM").toString();
        req.setPageSize(Integer.parseInt(num));
//        totalRecords = Integer.parseInt(num);
//        totalPages = totalRecords / req.getPageSize();
//        totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

        res.setDatas(new ArrayList<>());

        for (Map<String, Object> oneMaster : queryData) {

            DCP_ArPrePayDetailQueryRes.Datas oneData = res.new Datas();
            res.getDatas().add(oneData);

            oneData.setAccountId(oneMaster.get("ACCOUNTID").toString());
            oneData.setArType(oneMaster.get("ARTYPE").toString());
            oneData.setCorp(oneMaster.get("CORP").toString());
            oneData.setPDate(oneMaster.get("PDATE").toString());
            oneData.setOrganizationNo(oneMaster.get("ORGANIZATIONNO").toString());
            oneData.setAccEmployeeNo(oneMaster.get("ACCEMPLOYEENO").toString());
            oneData.setBizPartnerNo(oneMaster.get("BIZPARTNERNO").toString());
            oneData.setReceiver(oneMaster.get("RECEIVER").toString());
            oneData.setTaskId(oneMaster.get("TASKID").toString());
            oneData.setPayDateNo(oneMaster.get("PAYDATENO").toString());
            oneData.setPayDueDate(oneMaster.get("PAYDUEDATE").toString());
            oneData.setTaxCode(oneMaster.get("TAXCODE").toString());
            oneData.setTaxRate(oneMaster.get("TAXRATE").toString());
            oneData.setInclTax(oneMaster.get("INCLTAX").toString());
//            oneData.setApplicant(oneMaster.get("INCLTAX").toString());
            oneData.setEmployeeNo(oneMaster.get("EMPLOYEENO").toString());
            oneData.setDepartId(oneMaster.get("DEPARTNO").toString());
            oneData.setSourceType(oneMaster.get("SOURCETYPE").toString());
            oneData.setSourceNo(oneMaster.get("SOURCENO").toString());
            oneData.setPendOffsetNo(oneMaster.get("PENDOFFSETNO").toString());
            oneData.setFeeSubjectId(oneMaster.get("REVSUBJECT").toString());
            oneData.setArSubjectId(oneMaster.get("ARSUBJECTID").toString());
            oneData.setGlNo(oneMaster.get("GLNO").toString());
            oneData.setGrpPmtNo(oneMaster.get("GRPPMTNO").toString());
            oneData.setMemo(oneMaster.get("MEMO").toString());
//            oneData.setPayList(oneMaster.get("MEMO").toString());
            oneData.setCurrency(oneMaster.get("CURRENCY").toString());
            oneData.setExRate(oneMaster.get("EXRATE").toString());
            oneData.setFCYBTAmt(oneMaster.get("FCYBTAMT").toString());
            oneData.setFCYTAmt(oneMaster.get("FCYTAMT").toString());
            oneData.setFCYRevAmt(oneMaster.get("FCYREVAMT").toString());
            oneData.setFCYTATAmt(oneMaster.get("FCYTATAMT").toString());
            oneData.setLCYBTAmt(oneMaster.get("LCYBTAMT").toString());
            oneData.setLCYTAmt(oneMaster.get("LCYTAMT").toString());
            oneData.setLCYRevAmt(oneMaster.get("LCYREVAMT").toString());
            oneData.setLCYTATAmt(oneMaster.get("LCYTATAMT").toString());
            oneData.setLCYTATAmt(oneMaster.get("LCYTATAMT").toString());
//            oneData.setFCYPmtAmt(oneMaster.get("LCYTATAMT").toString());
//            oneData.setLCYPmtAmt(oneMaster.get("LCYTATAMT").toString());
//            oneData.setFCYPmtRevAmt(oneMaster.get("LCYTATAMT").toString());
//            oneData.setLCYPmtRevAmt(oneMaster.get("LCYTATAMT").toString());
            oneData.setStatus(oneMaster.get("STATUS").toString());
            oneData.setCreateBy(oneMaster.get("CREATEBY").toString());
            oneData.setCreate_Date(oneMaster.get("CREATE_DATE").toString());
            oneData.setCreate_Time(oneMaster.get("CREATE_TIME").toString());
            oneData.setModifyBy(oneMaster.get("MODIFYBY").toString());
            oneData.setModify_Date(oneMaster.get("MODIFY_DATE").toString());
            oneData.setModify_Time(oneMaster.get("MODIFY_TIME").toString());
            oneData.setConfirmBy(oneMaster.get("CONFIRMBY").toString());
            oneData.setConfirm_Date(oneMaster.get("CONFIRM_DATE").toString());
            oneData.setCancel_Time(oneMaster.get("CONFIRM_TIME").toString());
            oneData.setCancelBy(oneMaster.get("CANCELBY").toString());
            oneData.setCancel_Date(oneMaster.get("CANCEL_DATE").toString());
            oneData.setCancel_Time(oneMaster.get("CANCEL_TIME").toString());


        }

        res.setSuccess(true);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
//        res.setTotalRecords(totalRecords);
//        res.setTotalPages(totalPages);


        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ArPrePayDetailQueryReq req) throws Exception {

        //分页处理
//        int pageSize = req.getPageSize();
//        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
//        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
//        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        StringBuilder builder = new StringBuilder();

        builder.append(" SELECT * FROM( ");
        builder.append(" SELECT count(1) over () as num,row_number() over (order by a.ARNO) as rn ")
                .append(" ,b.* ")
//
                .append(" FROM DCP_ARPERD a ")
                .append(" LEFT JOIN DCP_ARBILL b on a.EID=b.EID and a.ARNO=b.ARNO ")
                .append(" LEFT JOIN DCP_BIZPARTNER bp1 on bp1.eid=b.eid and bp1.BIZPARTNERNO=b.BIZPARTNERNO ")
//                .append(" LEFT JOIN DCP_ARBILLDETAIL c on b.eid=c.eid and b.ARNO=c.ARNO and b.ACCOUNTID=c.ACCOUNTID ")
        ;

        builder.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (Check.isNotEmpty(req.getRequest().getAccountId())) {
            builder.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountId()).append("'");
        }

        if (Check.isNotEmpty(req.getRequest().getArNo())) {
            builder.append(" AND b.ARNO='").append(req.getRequest().getArNo()).append("'");
        }

        if (Check.isNotEmpty(req.getRequest().getStatus())) {
            builder.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }

        if (Check.isNotEmpty(req.getRequest().getTaskId())) {
            builder.append(" AND b.TASKID='").append(req.getRequest().getTaskId()).append("'");
        }

        builder.append(") temp  ");

//        builder.append(" where  rn>").append(startRow).append(" and rn<=").append(startRow + pageSize).append("  ").append(" ");

        return builder.toString();
    }
}
