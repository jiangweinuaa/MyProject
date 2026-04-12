package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CustomerPriceAdjustQueryReq;
import com.dsc.spos.json.cust.res.DCP_CustomerPriceAdjustQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_CustomerPriceAdjustQuery extends SPosBasicService<DCP_CustomerPriceAdjustQueryReq, DCP_CustomerPriceAdjustQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_CustomerPriceAdjustQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CustomerPriceAdjustQueryReq> getRequestType() {
        return new TypeToken<DCP_CustomerPriceAdjustQueryReq>() {
        };
    }

    @Override
    protected DCP_CustomerPriceAdjustQueryRes getResponseType() {
        return new DCP_CustomerPriceAdjustQueryRes();
    }

    @Override
    protected DCP_CustomerPriceAdjustQueryRes processJson(DCP_CustomerPriceAdjustQueryReq req) throws Exception {
        DCP_CustomerPriceAdjustQueryRes res = this.getResponseType();
        res.setDatas(new ArrayList<>());

        List<Map<String, Object>> getQData = this.doQueryData(getQuerySql(req), null);
        int totalRecords = 0;    //总笔数
        int totalPages = 0;        //总页数
        if (getQData != null && !getQData.isEmpty()) {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            for (Map<String, Object> oneData : getQData) {

                DCP_CustomerPriceAdjustQueryRes.Datas oneEntity = res.new Datas();

                oneEntity.setCreateDeptId(StringUtils.toString(oneData.get("CREATEDEPTID"), ""));
                oneEntity.setEndDate(StringUtils.toString(oneData.get("ENDDATE"), ""));
                oneEntity.setCreateOpName(StringUtils.toString(oneData.get("CREATEOPNAME"), ""));
                oneEntity.setMemo(StringUtils.toString(oneData.get("MEMO"), ""));
                oneEntity.setOwnOpName(StringUtils.toString(oneData.get("OWNOPNAME"), ""));
                oneEntity.setTotCqty(StringUtils.toString(oneData.get("TOTCQTY"), ""));
                oneEntity.setModifyTime(StringUtils.toString(oneData.get("MODIFYTIME"), ""));
                oneEntity.setOwnDeptName(StringUtils.toString(oneData.get("OWNDEPTNAME"), ""));
                oneEntity.setDepartId(StringUtils.toString(oneData.get("DEPARTID"), ""));
                oneEntity.setCancelBy(StringUtils.toString(oneData.get("CANCELBY"), ""));
                oneEntity.setCancelByName(StringUtils.toString(oneData.get("CANCELBYNAME"), ""));
                oneEntity.setOwnOpId(StringUtils.toString(oneData.get("OWNOPID"), ""));
                oneEntity.setModifyByName(StringUtils.toString(oneData.get("MODIFYBYNAME"), ""));
                oneEntity.setBillNo(StringUtils.toString(oneData.get("BILLNO"), ""));
                oneEntity.setDepartName(StringUtils.toString(oneData.get("DEPARTNAME"), ""));
                oneEntity.setEmployeeName(StringUtils.toString(oneData.get("EMPLOYEENAME"), ""));
                oneEntity.setModifyBy(StringUtils.toString(oneData.get("MODIFYBY"), ""));
                oneEntity.setCreateOpId(StringUtils.toString(oneData.get("CREATEOPID"), ""));
                oneEntity.setTotCustQty(StringUtils.toString(oneData.get("CustQty"), "0"));
                oneEntity.setConfirmByName(StringUtils.toString(oneData.get("CONFIRMBYNAME"), ""));
                oneEntity.setConfirmBy(StringUtils.toString(oneData.get("CONFIRMBY"), ""));
                oneEntity.setEmployeeId(StringUtils.toString(oneData.get("EMPLOYEEID"), ""));
                oneEntity.setConfirmTime(StringUtils.toString(oneData.get("CONFIRMTIME"), ""));
                oneEntity.setBeginDate(StringUtils.toString(oneData.get("BEGINDATE"), ""));
                oneEntity.setOwnDeptId(StringUtils.toString(oneData.get("OWNDEPTID"), ""));
                oneEntity.setBDate(StringUtils.toString(oneData.get("BDATE"), ""));
                oneEntity.setCreateTime(StringUtils.toString(oneData.get("CREATETIME"), ""));
                oneEntity.setCancelTime(StringUtils.toString(oneData.get("CANCELTIME"), ""));
                oneEntity.setStatus(StringUtils.toString(oneData.get("STATUS"), ""));

                res.getDatas().add(oneEntity);
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
    protected String getQuerySql(DCP_CustomerPriceAdjustQueryReq req) throws Exception {

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        StringBuilder builder = new StringBuilder();

        builder.append(" SELECT * FROM( ")
                .append("  select count(*) over() num, row_number() over (order by a.BDATE DESC,a.BILLNO DESC) rn, ")
                .append("  a.*,ee0.NAME as CREATEOPNAME,ee1.NAME as OWNOPNAME,ee2.NAME as MODIFYBYNAME, ")
                .append("  ee3.NAME as CONFIRMBYNAME,ee4.NAME as CANCELBYNAME,dp0.DEPARTNAME as CREATEDEPTNAME, ")
                .append("  dp1.DEPARTNAME as OWNDEPTNAME,ee6.NAME AS EMPLOYEENAME,dp2.DEPARTNAME ")
                .append(" FROM DCP_CUSTPRICEADJUST a ")
                .append(" LEFT JOIN DCP_EMPLOYEE ee0 ON ee0.EID=a.EID and ee0.EMPLOYEENO=a.CREATEOPID")
                .append(" LEFT JOIN DCP_EMPLOYEE ee1 ON ee1.EID=a.EID and ee1.EMPLOYEENO=a.OWNOPID")
                .append(" LEFT JOIN DCP_EMPLOYEE ee2 ON ee2.EID=a.EID and ee2.EMPLOYEENO=a.MODIFYBY")
                .append(" LEFT JOIN DCP_EMPLOYEE ee3 ON ee3.EID=a.EID and ee3.EMPLOYEENO=a.CONFIRMBY")
                .append(" LEFT JOIN DCP_EMPLOYEE ee4 ON ee2.EID=a.EID and ee4.EMPLOYEENO=a.CANCELBY")
                .append(" LEFT JOIN DCP_EMPLOYEE ee6 ON ee6.EID=a.EID and ee6.EMPLOYEENO=a.EMPLOYEEID")
                .append(" LEFT JOIN DCP_DEPARTMENT_LANG dp0 on dp0.EID=a.EID AND dp0.DEPARTNO=a.CREATEDEPTID AND dp0.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_DEPARTMENT_LANG dp1 on dp1.EID=a.EID AND dp1.DEPARTNO=a.OWNDEPTID AND dp1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_DEPARTMENT_LANG dp2 on dp2.EID=a.EID AND dp2.DEPARTNO=a.DEPARTID AND dp2.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" WHERE a.EID='").append(req.geteId()).append("'")
        ;
        if (StringUtils.isNotEmpty(req.getRequest().getStatus())){
            builder.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getKeyTxt())){
            builder.append(" AND ( a.BILLNO like '%%").append(req.getRequest().getKeyTxt()).append("%%'")
                    .append(")");
        }

        builder.append(" ) a ");

        builder.append(" WHERE rn> ")
                .append(startRow)
                .append(" and rn<= ")
                .append(startRow + pageSize)
                .append(" ORDER BY rn ");


        return builder.toString();
    }
}
