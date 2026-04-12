package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_MStockOutQueryReq;
import com.dsc.spos.json.cust.res.DCP_MStockOutQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_MStockOutQuery extends SPosBasicService<DCP_MStockOutQueryReq, DCP_MStockOutQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_MStockOutQueryReq req) throws Exception {
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
    protected TypeToken<DCP_MStockOutQueryReq> getRequestType() {
        return new TypeToken<DCP_MStockOutQueryReq>(){};
    }

    @Override
    protected DCP_MStockOutQueryRes getResponseType() {
        return new DCP_MStockOutQueryRes();
    }

    @Override
    protected DCP_MStockOutQueryRes processJson(DCP_MStockOutQueryReq req) throws Exception {
        DCP_MStockOutQueryRes res = this.getResponse();
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
                DCP_MStockOutQueryRes.Level1Elm level1Elm = res.new Level1Elm();
                level1Elm.setMStockOutNo(row.get("MSTOCKOUTNO").toString());
                level1Elm.setDocType(row.get("DOC_TYPE").toString());
                level1Elm.setWarehouse(row.get("WAREHOUSE").toString());
                level1Elm.setWarehouseName(row.get("WAREHOUSENAME").toString());
                level1Elm.setBDate(row.get("BDATE").toString());
                level1Elm.setAccountDate(row.get("ACCOUNT_DATE").toString());
                level1Elm.setTotPQty(row.get("TOT_PQTY").toString());
                level1Elm.setTotCQty(row.get("TOT_CQTY").toString());
                level1Elm.setTotAmt(row.get("TOT_AMT").toString());
                level1Elm.setTotDistriAmt(row.get("TOT_DISTRIAMT").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setAdjustStatus(row.get("ADJUSTSTATUS").toString());
                level1Elm.setLoadDocType(row.get("LOAD_DOCTYPE").toString());
                level1Elm.setLoadDocNo(row.get("LOAD_DOCNO").toString());
                level1Elm.setAutoProcess(row.get("AUTOPROCESS").toString());
                level1Elm.setCreateBy(row.get("CREATEOPID").toString());
                level1Elm.setCreateByName(row.get("CREATEOPNAME").toString());
                level1Elm.setCreateTime(row.get("CREATETIME").toString());
                level1Elm.setModifyBy(row.get("LASTMODIOPID").toString());
                level1Elm.setModifyTime(row.get("LASTMODITIME").toString());
                level1Elm.setModifyByName(row.get("LASTMODIOPNAME").toString());
                level1Elm.setProcessStatus(row.get("PROCESS_STATUS").toString());
                level1Elm.setProcessErpNo(row.get("PROCESS_ERP_NO").toString());
                level1Elm.setProcessErpOrg(row.get("PROCESS_ERP_ORG").toString());
                level1Elm.setAccountBy(row.get("ACCOUNTOPID").toString());
                level1Elm.setAccountByName(row.get("ACCOUNTOPNAME").toString());
                level1Elm.setAccountTime(row.get("ACCOUNTTIME").toString());
                level1Elm.setOMStockOutNo(row.get("OMSTOCKOUTNO").toString());
                level1Elm.setEmployeeId(row.get("EMPLOYEEID").toString());
                level1Elm.setEmployeeName(row.get("EMPLOYEENAME").toString());
                level1Elm.setDepartId(row.get("DEPARTID").toString());
                level1Elm.setDepartName(row.get("DEPTNAME").toString());
                level1Elm.setOType(row.get("OTYPE").toString());
                level1Elm.setOfNo(row.get("OFNO").toString());
                level1Elm.setOOType(row.get("OOTYPE").toString());
                level1Elm.setOOfNo(row.get("OOFNO").toString());

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
    protected String getQuerySql(DCP_MStockOutQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();

        DCP_MStockOutQueryReq.levelRequest request = req.getRequest();
        String dateType = request.getDateType();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String keyTxt = request.getKeyTxt();
        String status = request.getStatus();
        String departId = request.getDepartId();

        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with mstockout as ("
                + " select a.mstockoutno from DCP_MSTOCKOUT a"
                + " where a.eid='"+eId+"' and a.organizationno='"+req.getOrganizationNO()+"' "
        );

        if("accountDate".equals(dateType)){
            sqlbuf.append(" and a.ACCOUNT_DATE between '"+beginDate+"' and '"+endDate+"' ");
        }else if ("bDate".equals(dateType)){
            sqlbuf.append(" and a.bdate between '"+beginDate+"' and '"+endDate+"' ");
        }

        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.mstockoutno like '%"+keyTxt+"%' "
                    + " ) ");
        }

        if (!Check.Null(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }

        if(Check.NotNull(departId)){
            sqlbuf.append(" and a.DEPARTID='"+departId+"' ");
        }


        sqlbuf.append(" group by a.mstockoutno");
        sqlbuf.append(" )");


        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.createtime desc) as rn,"
                + " a.*,c.WAREHOUSE_NAME as warehousename ,e1.op_name as createopname,e2.op_name as lastModiOpName,e3.op_name as confirmopname,e4.op_name as accountopname" +
                ",d1.departname as deptname,ee.name as employeename  "
                + " from DCP_MSTOCKOUT a"
                + " inner join mstockout b on a.mstockoutno=b.mstockoutno " +
                " left join dcp_warehouse_lang c on c.eid=a.eid and c.warehouse=a.warehouse and c.lang_type='"+req.getLangType()+"' " +
                " "
                + " left join PLATFORM_STAFFS_LANG e1 on e1.eid=a.eid and e1.opno=a.createOpId and e1.lang_type='"+req.getLangType()+"'"
                + " left join PLATFORM_STAFFS_LANG e2 on e2.eid=a.eid and e2.opno=a.lastModiOpId and e2.lang_type='"+req.getLangType()+"' "
                + " left join PLATFORM_STAFFS_LANG e3 on e3.eid=a.eid and e3.opno=a.CONFIRMOPID and e3.lang_type='"+req.getLangType()+"' "
                + " left join PLATFORM_STAFFS_LANG e4 on e4.eid=a.eid and e4.opno=a.ACCOUNTOPID and e4.lang_type='"+req.getLangType()+"'" +
                " left join dcp_employee ee on ee.eid=a.eid and ee.employeeno=a.employeeid  "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.DEPARTID and d1.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}


