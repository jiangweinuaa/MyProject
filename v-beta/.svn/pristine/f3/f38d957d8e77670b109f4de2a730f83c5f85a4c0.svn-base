package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_WOReportQueryReq;
import com.dsc.spos.json.cust.res.DCP_WOReportQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_WOReportQuery extends SPosBasicService<DCP_WOReportQueryReq, DCP_WOReportQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_WOReportQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        }

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_WOReportQueryReq> getRequestType() {
        return new TypeToken<DCP_WOReportQueryReq>(){};
    }

    @Override
    protected DCP_WOReportQueryRes getResponseType() {
        return new DCP_WOReportQueryRes();
    }

    @Override
    protected DCP_WOReportQueryRes processJson(DCP_WOReportQueryReq req) throws Exception {
        DCP_WOReportQueryRes res = this.getResponse();
        int totalRecords=0;
        int totalPages=0;
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {
            //算總頁數
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> row : getQData){

                DCP_WOReportQueryRes.DatasDTO datasDTO = res.new DatasDTO();
                datasDTO.setReportNo(row.get("REPORTNO").toString());
                datasDTO.setBDate(row.get("BDATE").toString());
                datasDTO.setAccountDate(row.get("ACCOUNTDATE").toString());
                datasDTO.setMemo(row.get("MEMO").toString());
                datasDTO.setStatus(row.get("STATUS").toString());
                datasDTO.setCreateBy(row.get("CREATEBY").toString());
                datasDTO.setCreateByName(row.get("CREATEBYNAME").toString());
                datasDTO.setCreateTime(row.get("CREATETIME").toString());
                datasDTO.setModifyBy(row.get("MODIFYBY").toString());
                datasDTO.setModifyByName(row.get("MODIFYBYNAME").toString());
                datasDTO.setModifyTime(row.get("MODIFYTIME").toString());
                datasDTO.setAccountBy(row.get("ACCOUNTBY").toString());
                datasDTO.setAccountByName(row.get("ACCOUNTBYNAME").toString());
                datasDTO.setAccountTime(row.get("ACCOUNTTIME").toString());
                datasDTO.setCancelBy(row.get("CANCELBY").toString());
                datasDTO.setCancelByName(row.get("CANCELBYNAME").toString());
                datasDTO.setCancelTime(row.get("CANCELTIME").toString());
                datasDTO.setProcessStatus(row.get("PROCESSSTATUS").toString());
                datasDTO.setProcessErpNo(row.get("PROCESSERPNO").toString());
                datasDTO.setProcessErpOrg(row.get("PROCESSERPORG").toString());
                datasDTO.setEmployeeId(row.get("EMPLOYEEID").toString());
                datasDTO.setEmployeeName(row.get("EMPLOYEENAME").toString());
                datasDTO.setDepartId(row.get("DEPARTID").toString());
                datasDTO.setDepartName(row.get("DEPARTNAME").toString());

                res.getDatas().add(datasDTO);
            }

        }

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
    protected String getQuerySql(DCP_WOReportQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();

        StringBuffer sqlbuf=new StringBuffer();

        String dateType = req.getRequest().getDateType();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();
        String departId = req.getRequest().getDepartId();

        String status = req.getRequest().getStatus();
        String keyTxt = req.getRequest().getKeyTxt();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with report as ("
                + " select a.reportno from DCP_WOREPORT a"
                + " where a.eid='"+eId+"' "
        );
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.reportno like '%"+keyTxt+"%'"
                    + " ) ");
        }

        if("bDate".equals(dateType)){
            if(Check.NotNull(beginDate)){
                sqlbuf.append(" and a.bdate >= '"+beginDate+"' ");
            }
            if(Check.NotNull(endDate)){
                sqlbuf.append(" and a.bdate <= '"+endDate+"' ");
            }
        }
        else if("accountDate".equals(dateType))
        {
            if(Check.NotNull(beginDate)){
                sqlbuf.append(" and a.accountdate >= '"+beginDate+"' ");
            }
            if(Check.NotNull(endDate)){
                sqlbuf.append(" and a.accountdate <= '"+endDate+"' ");
            }
        }
        if(Check.NotNull(departId)){
            sqlbuf.append(" and a.departid='"+departId+"' ");
        }
        if (!Check.Null(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }

        sqlbuf.append(" group by a.reportno");
        sqlbuf.append(" )");


        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.createtime desc) as rn,"
                + " a.*,e1.opno as createby,e2.opno as modifyby,e3.opno as accountby,e4.opno as cancelby,e1.op_name as createbyname,e2.op_name as modifybyname,e3.op_name as accountbyname,e4.op_name as cancelbyname,d1.departname," +
                "e5.name as employeename  "
                + " from DCP_WOREPORT a"
                + " inner join report b on a.reportno=b.reportno "
                + " left join PLATFORM_STAFFS_LANG e1 on e1.eid=a.eid and e1.opno=a.CREATEOPID and e1.lang_type='"+req.getLangType()+"'"
                + " left join PLATFORM_STAFFS_LANG e2 on e2.eid=a.eid and e2.opno=a.LASTMODIOPID and e2.lang_type='"+req.getLangType()+"' "
                + " left join PLATFORM_STAFFS_LANG e3 on e3.eid=a.eid and e3.opno=a.ACCOUNTOPID and e1.lang_type='"+req.getLangType()+"'"
                + " left join PLATFORM_STAFFS_LANG e4 on e4.eid=a.eid and e4.opno=a.CANCELOPID and e2.lang_type='"+req.getLangType()+"' " +
                " left join dcp_employee e5 on e5.eid=a.eid and e5.employeeno =a.employeeid "

                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.DEPARTID and d1.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}


