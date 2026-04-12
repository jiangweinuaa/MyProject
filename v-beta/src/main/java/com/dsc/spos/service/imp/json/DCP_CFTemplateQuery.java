package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CFTemplateQueryReq;
import com.dsc.spos.json.cust.res.DCP_CFTemplateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_CFTemplateQuery extends SPosBasicService<DCP_CFTemplateQueryReq, DCP_CFTemplateQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_CFTemplateQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CFTemplateQueryReq> getRequestType() {
        return new TypeToken<DCP_CFTemplateQueryReq>() {
        };
    }

    @Override
    protected DCP_CFTemplateQueryRes getResponseType() {
        return new DCP_CFTemplateQueryRes();
    }

    @Override
    protected DCP_CFTemplateQueryRes processJson(DCP_CFTemplateQueryReq req) throws Exception {
        DCP_CFTemplateQueryRes res = this.getResponseType();
        int totalRecords=0;
        int totalPages=0;
        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        if (CollectionUtils.isNotEmpty(qData)) {

            String num = qData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;


            res.setDatas(new ArrayList<>());

            for (Map<String, Object> data : qData) {
                DCP_CFTemplateQueryRes.Request oneReq = res.new Request();
                res.getDatas().add(oneReq);

                oneReq.setItem(data.get("ITEM").toString());
                oneReq.setCfCode(data.get("CFCODE").toString());
                oneReq.setCfName(data.get("CFNAME").toString());
                oneReq.setCfType(data.get("CFTYPE").toString());
                oneReq.setType(data.get("TYPE").toString());
                oneReq.setCompute(data.get("COMPUTE").toString());
                oneReq.setStatus(data.get("STATUS").toString());

                oneReq.setCreateOpId(data.get("CREATEOPID").toString());
                oneReq.setCreateOpName(data.get("CREATEOPNAME").toString());
                oneReq.setCreateTime(data.get("CREATETIME").toString());

                oneReq.setLastmodiopID(data.get("LASTMODIOPID").toString());
                oneReq.setLastmodiopName(data.get("LASTMODIOPNAME").toString());
                oneReq.setLastmodiTime(data.get("LASTMODITIME").toString());

                oneReq.setConfirmopID(data.get("CONFIRMOPID").toString());
                oneReq.setConfirmopName(data.get("CONFIRMOPNAME").toString());
                oneReq.setConfirmopTime(data.get("CONFIRMTIME").toString());

                oneReq.setCloseopID(data.get("CLOSEOPID").toString());
                oneReq.setCloseopName(data.get("CLOSEOPNAME").toString());
                oneReq.setCloseTime(data.get("CLOSETIME").toString());

                oneReq.setCancelopID(data.get("CANCELOPID").toString());
                oneReq.setCancelopName(data.get("CANCELOPNAME").toString());
                oneReq.setCancelTime(data.get("CANCELTIME").toString());

            }

        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);
        res.setSuccess(true);
        res.setServiceStatus("000");

        //res.setTotalRecords(totalRecords);

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_CFTemplateQueryReq req) throws Exception {

        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT * FROM( ");
        querySql.append("SELECT " +
                        " COUNT(*) OVER() NUM ,ROW_NUMBER() over(ORDER BY ITEM) rn, " +
                        " a.* " +
                        " ,ee1.NAME CREATEOPNAME,ee2.NAME LASTMODIOPNAME,ee3.NAME CLOSEOPNAME " +
                        " ,ee4.NAME CANCELOPNAME,ee5.NAME CONFIRMOPNAME,ee6.NAME SUBMITOPNAME  " +
                        " FROM DCP_CFTEMPLATE a ")
                .append(" LEFT JOIN DCP_EMPLOYEE ee1 ON ee1.EID=a.EID and ee1.EMPLOYEENO=a.CREATEOPID ")
                .append(" LEFT JOIN DCP_EMPLOYEE ee2 ON ee2.EID=a.EID and ee2.EMPLOYEENO=a.LASTMODIOPID ")
                .append(" LEFT JOIN DCP_EMPLOYEE ee3 ON ee3.EID=a.EID and ee3.EMPLOYEENO=a.CLOSEOPID ")
                .append(" LEFT JOIN DCP_EMPLOYEE ee4 ON ee2.EID=a.EID and ee4.EMPLOYEENO=a.CANCELOPID ")
                .append(" LEFT JOIN DCP_EMPLOYEE ee5 ON ee5.EID=a.EID and ee5.EMPLOYEENO=a.CONFIRMOPID ")
                .append(" LEFT JOIN DCP_EMPLOYEE ee6 ON ee6.EID=a.EID and ee6.EMPLOYEENO=a.SUBMITOPID ")
        ;
        querySql.append(" WHERE a.EID='").append(req.geteId()).append("' ");

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
            querySql.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("' ");
        }

        querySql.append(" ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" ");

        querySql.append(" ORDER BY rn ");

        return querySql.toString();
    }
}
