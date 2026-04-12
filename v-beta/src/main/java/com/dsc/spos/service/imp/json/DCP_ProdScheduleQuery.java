package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ProdScheduleQueryReq;
import com.dsc.spos.json.cust.res.DCP_ProdScheduleQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ProdScheduleQuery extends SPosBasicService<DCP_ProdScheduleQueryReq, DCP_ProdScheduleQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ProdScheduleQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ProdScheduleQueryReq> getRequestType() {
        return new TypeToken<DCP_ProdScheduleQueryReq>(){};
    }

    @Override
    protected DCP_ProdScheduleQueryRes getResponseType() {
        return new DCP_ProdScheduleQueryRes();
    }

    @Override
    protected DCP_ProdScheduleQueryRes processJson(DCP_ProdScheduleQueryReq req) throws Exception {
        DCP_ProdScheduleQueryRes res = this.getResponse();
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
                DCP_ProdScheduleQueryRes.Level1Elm level1Elm = res.new Level1Elm();
                level1Elm.setBDate(row.get("BDATE").toString());
                level1Elm.setBillNo(row.get("BILLNO").toString());
                level1Elm.setBeginDate(row.get("BEGINDATE").toString());
                level1Elm.setEndDate(row.get("ENDATE").toString());
                level1Elm.setSemiWOGenType(row.get("SEMIWOGENTYPE").toString());
                level1Elm.setEmployeeId(row.get("EMPLOYEEID").toString());
                level1Elm.setEmployeeName(row.get("EMPLOYEENAME").toString());
                level1Elm.setDepartId(row.get("DEPARTID").toString());
                level1Elm.setDepartName(row.get("DEPARTNAME").toString());
                level1Elm.setTotCqty(row.get("TOTCQTY").toString());
                level1Elm.setTotPqty(row.get("TOTPQTY").toString());
                level1Elm.setTotWOQty(row.get("TOTWOQTY").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setCreateBy(row.get("CREATEBY").toString());
                level1Elm.setCreateByName(row.get("CREATEBYNAME").toString());
                level1Elm.setCreateTime(row.get("CREATETIME").toString());
                level1Elm.setCreateDeptId(row.get("CREATEDEPTID").toString());
                level1Elm.setCreateDeptName(row.get("CREATEDEPTNAME").toString());
                level1Elm.setModifyBy(row.get("MODIFYBY").toString());
                level1Elm.setModifyByName(row.get("MODIFYBYNAME").toString());
                level1Elm.setModifyTime(row.get("MODIFYTIME").toString());
                level1Elm.setConfirmBy(row.get("CONFIRMBY").toString());
                level1Elm.setConfirmByName(row.get("CONFIRMBYNAME").toString());
                level1Elm.setConfirmTime(row.get("CONFIRMTIME").toString());
                level1Elm.setCancelBy(row.get("CANCELBY").toString());
                level1Elm.setCancelByName(row.get("CANCELBYNAME").toString());
                level1Elm.setCancelTime(row.get("CANCELTIME").toString());
                level1Elm.setCloseBy(row.get("CLOSEBY").toString());
                level1Elm.setCloseByName(row.get("CLOSEBYNAME").toString());
                level1Elm.setCloseTime(row.get("CLOSETIME").toString());
                res.getDatas().add(level1Elm);

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
    protected String getQuerySql(DCP_ProdScheduleQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        String keyTxt = req.getRequest().getKeyTxt();
        String organizationNO = req.getOrganizationNO();
        StringBuffer sqlbuf=new StringBuffer();
        String status = req.getRequest().getStatus();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with schdule as ("
                + " select a.billno from DCP_PRODSCHEDULE a"
                + " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' "
        );
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.billno like '%"+keyTxt+"%' "
                    + " ) ");
        }

        if (!Check.Null(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }
        if(Check.NotNull(beginDate)){
            sqlbuf.append(" and a.bdate>='"+beginDate+"' ");
        }
        if(Check.NotNull(endDate)){
            sqlbuf.append(" and a.bdate<='"+endDate+"' ");
        }

        sqlbuf.append(" group by a.billno");
        sqlbuf.append(" )");


        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.createtime desc) as rn,"
                + " a.billno,a.bdate,a.begindate,a.endate,a.status,a.semiWOGenType,a.totCqty,a.totpqty,a.totWOQty,a.memo,"
                + " a.CREATEBY ,e1.name as CREATEBYNAME,a.MODIFYBY,e2.name as MODIFYBYNAME,TO_CHAR(a.CREATETIME,'yyyy-MM-dd HH:mm:ss') as createtime,to_char(a.MODIFYTIME,'yyyy-MM-dd HH:mm:ss') as MODIFYTIME,a.CREATEDEPTID,d1.departname as createdeptname,  "
                + " a.confirmby,e3.name as confirmbyname,a.cancelby,e4.name as cancelbyname,a.closeby,e5.name as closebyname,to_char(a.confirmtime,'yyyy-MM-dd HH:mm:ss') as confirmtime,to_char(a.CANCELTIME,'yyyy-MM-dd HH:mm:ss') as CANCELTIME,to_char(a.closetime,'yyyy-MM-dd HH:mm:ss') as closetime, "
                + " a.employeeid,e.name as employeename,a.departid,d.departname "
                + " from DCP_PRODSCHEDULE a"
                + " inner join schdule b on a.billno=b.billno "
                + " left join dcp_employee e on e.eid=a.eid and e.employeeno=a.employeeid "
                + " left join dcp_employee e1 on e1.eid=a.eid and e1.employeeno=a.CREATEBY "
                + " left join dcp_employee e2 on e2.eid=a.eid and e2.employeeno=a.MODIFYBY "
                + " left join dcp_employee e3 on e3.eid=a.eid and e3.employeeno=a.CONFIRMBY "
                + " left join dcp_employee e4 on e4.eid=a.eid and e4.employeeno=a.CANCELBY "
                + " left join dcp_employee e5 on e5.eid=a.eid and e5.employeeno=a.CLOSEBY "
                + " left join dcp_department_lang d on d.eid=a.eid and d.departno=a.departid and d.lang_type='"+langType+"'"
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.CREATEDEPTID and d1.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}


