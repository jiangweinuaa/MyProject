package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ArPerdQueryReq;
import com.dsc.spos.json.cust.res.DCP_ArPerdQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_ArPerdQuery extends SPosBasicService<DCP_ArPerdQueryReq, DCP_ArPerdQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ArPerdQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ArPerdQueryReq> getRequestType() {
        return new TypeToken<DCP_ArPerdQueryReq>() {
        };
    }

    @Override
    protected DCP_ArPerdQueryRes getResponseType() {
        return new DCP_ArPerdQueryRes();
    }

    @Override
    protected DCP_ArPerdQueryRes processJson(DCP_ArPerdQueryReq req) throws Exception {
        DCP_ArPerdQueryRes res = this.getResponseType();

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
            distinct.put("ARNO", true);
            List<Map<String, Object>> masterData = MapDistinct.getMap(queryData, distinct);

            for (Map<String, Object> oneMaster : masterData) {

                DCP_ArPerdQueryRes.Datas oneData = res.new Datas();
                res.getDatas().add(oneData);

                oneData.setAccountId(oneMaster.get("ACCOUNTID").toString());
                oneData.setApType(oneMaster.get("ARTYPE").toString());
                oneData.setCorp(oneMaster.get("CORP").toString());
                oneData.setPDate(oneMaster.get("PDATE").toString());
                oneData.setOrganizationNo(oneMaster.get("ORGANIZATIONNO").toString());
                oneData.setBizPartnerNo(oneMaster.get("BIZPARTNERNO").toString());
                oneData.setReceiver(oneMaster.get("RECEIVER").toString());
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
                condition.put("ARNO", oneMaster.get("ARNO").toString());

                List<Map<String, Object>> detailData = MapDistinct.getWhereMap(queryData, condition, true);

                oneData.setArPerdList(new ArrayList<>());
                for (Map<String, Object> oneDetail : detailData) {
                    DCP_ArPerdQueryRes.ArPerdList oneList = res.new ArPerdList();
                    oneData.getArPerdList().add(oneList);

                    oneList.setAccountID(oneDetail.get("ACCOUNTID").toString());
                    oneList.setCorp(oneDetail.get("CORP").toString());
//                oneList.setSourceOrg(oneDetail.get("CORP").toString());
                    oneList.setOrganizationNo(oneDetail.get("ORGANIZATIONNO").toString());
                    oneList.setArNo(oneDetail.get("ARNO").toString());
                    oneList.setItem(oneDetail.get("ITEM").toString());
                    oneList.setInstPmtSeq(oneDetail.get("INSTPMTSEQ").toString());
                    oneList.setPayType(oneDetail.get("PAYTYPE").toString());
                    oneList.setPayDueDate(oneDetail.get("PAYDUEDATE").toString());
                    oneList.setBillDueDate(oneDetail.get("BILLDUEDATE").toString());
                    oneList.setDirection(oneDetail.get("DIRECTION").toString());
                    oneList.setFCYReqAmt(oneDetail.get("FCYTATAMT").toString());
                    oneList.setCurrency(oneDetail.get("CURRENCY").toString());
                    oneList.setExRate(oneDetail.get("EXRATE").toString());
                    oneList.setFCYRevsedRate(oneDetail.get("FCYREVSEDRATE").toString());
                    oneList.setFCYTATAmt(oneDetail.get("FCYTATAMT").toString());
                    oneList.setFCYPmtRevAmt(oneDetail.get("FCYPMTREVAMT").toString());
                    oneList.setRevalAdjNum(oneDetail.get("REVALADJNUM").toString());
                    oneList.setLCYTATAmt(oneDetail.get("LCYTATAMT").toString());
                    oneList.setLCYPmtRevAmt(oneDetail.get("LCYPMTREVAMT").toString());
                    oneList.setPayDateNo(oneDetail.get("PAYDATENO").toString());
                    oneList.setPmtCategory(oneDetail.get("PMTCATEGORY").toString());
                    oneList.setPoNo(oneDetail.get("PONO").toString());
                    oneList.setArSubjectId(oneDetail.get("ARSUBJECTID").toString());


                }

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
    protected String getQuerySql(DCP_ArPerdQueryReq req) throws Exception {

        //分页处理
        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        StringBuilder builder = new StringBuilder();

        builder.append(" SELECT * FROM( ");
        builder.append(" SELECT count(1) over () as num,row_number() over (order by a.CUSTGROUPNO desc) as rn,")
                .append(" ,a.* ")
                .append(" ,b.TASKID,b.ARTYPE ")
                .append(" FROM DCP_ARPERD a ")
                .append(" LEFT JOIN DCP_ARBILL b on a.EID=b.EID and a.ARNO=b.ARNO ")
        ;

        builder.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getAccountId())) {
            builder.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountId()).append("'");
        }

        if (StringUtils.isEmpty(req.getRequest().getArNo())) {
            builder.append(" AND a.ARNO='").append(req.getRequest().getArNo()).append("'");
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
