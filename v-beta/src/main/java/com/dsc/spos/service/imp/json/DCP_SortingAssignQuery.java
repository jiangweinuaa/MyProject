package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_SortingAssignQueryReq;
import com.dsc.spos.json.cust.res.DCP_SortingAssignQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_SortingAssignQuery extends SPosBasicService<DCP_SortingAssignQueryReq, DCP_SortingAssignQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_SortingAssignQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_SortingAssignQueryReq> getRequestType() {
        return new TypeToken<DCP_SortingAssignQueryReq>() {
        };
    }

    @Override
    protected DCP_SortingAssignQueryRes getResponseType() {
        return new DCP_SortingAssignQueryRes();
    }

    @Override
    protected DCP_SortingAssignQueryRes processJson(DCP_SortingAssignQueryReq req) throws Exception {
        DCP_SortingAssignQueryRes res = this.getResponseType();


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

            for (Map<String, Object> data : getData) {
                DCP_SortingAssignQueryRes.Datas oneData = res.new Datas();

                oneData.setBillNo(StringUtils.toString(data.get("BILLNO"), ""));
                oneData.setOType(StringUtils.toString(data.get("OTYPE"), ""));
                oneData.setBDate(StringUtils.toString(data.get("BDATE"), ""));
                oneData.setWarehouse(StringUtils.toString(data.get("WAREHOUSE"), ""));
                oneData.setWarehouseName(StringUtils.toString(data.get("WAREHOUSE_NAME"), ""));
                oneData.setEmployeeId(StringUtils.toString(data.get("EMPLOYEEID"), ""));
                oneData.setEmployeeName(StringUtils.toString(data.get("EMPLOYEENAME"), ""));
                oneData.setDepartId(StringUtils.toString(data.get("DEPARTID"), ""));
                oneData.setDepartName(StringUtils.toString(data.get("DEPARTNAME"), ""));
                oneData.setStatus(StringUtils.toString(data.get("STATUS"), ""));
                oneData.setTotOrgCnt(StringUtils.toString(data.get("TOTORGCNT"), ""));
                oneData.setTotCqty(StringUtils.toString(data.get("TOTCQTY"), ""));
                oneData.setTotPqty(StringUtils.toString(data.get("TOTPQTY"), ""));
                oneData.setCreateOpId(StringUtils.toString(data.get("CREATEOPID"), ""));
                oneData.setCreateOpName(StringUtils.toString(data.get("CREATEOPNAME"), ""));
                oneData.setCreateDeptId(StringUtils.toString(data.get("CREATEDEPTID"), ""));
                oneData.setCreateDeptName(StringUtils.toString(data.get("CREATEDEPTNAME"), ""));
                oneData.setCreateTime(StringUtils.toString(data.get("CREATETIME"), ""));
                oneData.setLastModiOpId(StringUtils.toString(data.get("LASTMODIOPID"), ""));
                oneData.setLastModiOpName(StringUtils.toString(data.get("LASTMODIOPNAME"), ""));
                oneData.setLastModiTime(StringUtils.toString(data.get("LASTMODITIME"), ""));
                oneData.setConfirmBy(StringUtils.toString(data.get("CONFIRMBY"), ""));
                oneData.setConfirmByName(StringUtils.toString(data.get("CONFIRMBYNAME"), ""));
                oneData.setConfirmTime(StringUtils.toString(data.get("CONFIRMTIME"), ""));
                oneData.setCloseBy(StringUtils.toString(data.get("CLOSEBY"), ""));
                oneData.setCloseByName(StringUtils.toString(data.get("CLOSEBYNAME"), ""));
                oneData.setCloseTime(StringUtils.toString(data.get("CLOSETIME"), ""));
                oneData.setCancelBy(StringUtils.toString(data.get("CANCELBY"), ""));
                oneData.setCancelByName(StringUtils.toString(data.get("CANCELBYNAME"), ""));
                oneData.setCancelTime(StringUtils.toString(data.get("CANCELTIME"), ""));


                res.getDatas().add(oneData);
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
    protected String getQuerySql(DCP_SortingAssignQueryReq req) throws Exception {

        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.BILLNO DESC) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" a.* ")
                .append(" ,em1.name AS CREATEOPNAME,em2.name AS LASTMODIOPNAME,em2.name AS EMPLOYEENAME ")
                .append(" ,em4.name AS CONFIRMBYNAME,em5.name AS CANCELBYNAME,em6.name AS CLOSEBYNAME ")
                .append(" ,dd0.DEPARTNAME AS DEPARTNAME,dd1.DEPARTNAME AS CREATEDEPTNAME,wl1.WAREHOUSE_NAME  ")
                .append(" FROM DCP_SORTINGASSIGN a")
                .append(" left join dcp_warehouse_lang wl1 on wl1.eid=a.eid and wl1.warehouse=a.warehouse and wl1.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_employee em1 ON em1.eid = a.eid AND em1.employeeno = a.CREATEOPID ")
                .append(" LEFT JOIN DCP_employee em2 ON em2.eid = a.eid AND em2.employeeno = a.LASTMODIOPID ")
                .append(" LEFT JOIN DCP_employee em3 ON em3.eid = a.eid AND em3.employeeno = a.EMPLOYEEID ")
                .append(" LEFT JOIN DCP_employee em4 ON em4.eid = a.eid AND em4.employeeno = a.CONFIRMBY ")
                .append(" LEFT JOIN DCP_employee em5 ON em5.eid = a.eid AND em5.employeeno = a.CANCELBY ")
                .append(" LEFT JOIN DCP_employee em6 ON em6.eid = a.eid AND em6.employeeno = a.CLOSEBY ")
                .append(" LEFT JOIN dcp_department_lang dd0 ON dd0.eid = a.eid AND dd0.departno = a.DEPARTID AND dd0.lang_type='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN dcp_department_lang dd1 ON dd0.eid = a.eid AND dd1.departno = a.CREATEDEPTID AND dd1.lang_type='").append(req.getLangType()).append("'")
        ;


        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getKeyTxt())) {
            String keyTxt = req.getRequest().getKeyTxt();
            sb.append(" AND (a.BILLNO like '%%").append(keyTxt).append("%%'")
                    .append(")");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getWarehouse())){
            sb.append(" AND a.WAREHOUSE='").append(req.getRequest().getWarehouse());
        }
        if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())){
            sb.append(" AND a.BDATE>='").append(DateFormatUtils.getPlainDate(req.getRequest().getBeginDate())).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getEndDate())){
            sb.append(" AND a.BDATE<='").append(DateFormatUtils.getPlainDate(req.getRequest().getEndDate())).append("'");
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getStatus())) {

            sb.append(" AND ( 1=2 ");
            for (String s : req.getRequest().getStatus()) {
                sb.append(" OR a.STATUS='").append(s).append("'");
            }
            sb.append(" )");
        }
        sb.append("  ) a "
                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY BILLNO ");
        return sb.toString();
    }
}
