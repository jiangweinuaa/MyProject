package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ProductInQueryReq;
import com.dsc.spos.json.cust.res.DCP_ProductInQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ProductInQuery extends SPosBasicService<DCP_ProductInQueryReq, DCP_ProductInQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ProductInQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ProductInQueryReq> getRequestType() {
        return new TypeToken<DCP_ProductInQueryReq>(){};
    }

    @Override
    protected DCP_ProductInQueryRes getResponseType() {
        return new DCP_ProductInQueryRes();
    }

    @Override
    protected DCP_ProductInQueryRes processJson(DCP_ProductInQueryReq req) throws Exception {
        DCP_ProductInQueryRes res = this.getResponse();
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
                DCP_ProductInQueryRes.Level1Elm levelElm=res.new Level1Elm();
                levelElm.setBDate(row.get("BDATE").toString());
                levelElm.setProductInNo(row.get("PRODUCTIONNO").toString());
                levelElm.setPGroupNo(row.get("PGROUPNO").toString());
                levelElm.setPGroupName(row.get("PGROUPNAME").toString());
                levelElm.setCreateBy(row.get("CREATEBY").toString());
                levelElm.setCreateByName(row.get("CREATEBYNAME").toString());
                levelElm.setCreateTime(row.get("CREATETIME").toString());
                levelElm.setModifyBy(row.get("MODIFYBY").toString());
                levelElm.setModifyByName(row.get("MODIFYBYNAME").toString());
                levelElm.setModifyTime(row.get("MODIFYTIME").toString());
                levelElm.setConfirmBy(row.get("CONFIRMBY").toString());
                levelElm.setConfirmByName(row.get("CONFIRMBYNAME").toString());
                levelElm.setConfirmTime(row.get("CONFIRMTIME").toString());
                levelElm.setProcessStatus(row.get("PROCESSSTATUS").toString());
                levelElm.setProcessErpNo(row.get("PROCESSERPNO").toString());
                levelElm.setProcessErpOrg(row.get("PROCESSERPORG").toString());
                levelElm.setSourceReportNo(row.get("SOURCEREPORTNO").toString());
                levelElm.setReturnNo(row.get("RETURNNO").toString());
                levelElm.setReturnStatus(row.get("RETURNSTATUS").toString());
                levelElm.setDepartId(row.get("DEPARTID").toString());
                levelElm.setDepartName(row.get("DEPARTNAME").toString());
                res.getDatas().add(levelElm);

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
    protected String getQuerySql(DCP_ProductInQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        String keyTxt = req.getRequest().getKeyTxt();
        StringBuffer sqlbuf=new StringBuffer();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with productin as ("
                + " select a.PRODUCTIONNO from MES_PRODUCTIN a"
                + " where a.eid='"+eId+"'  and a.organizationno='"+ req.getOrganizationNO()+"'"
        );
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.PRODUCTIONNO like '%"+keyTxt+"%'  "
                    + " ) ");
        }
        if(Check.NotNull(beginDate)){
            sqlbuf.append(" and a.bdate>='"+beginDate+"' ");
        }

        if (!Check.Null(endDate)){
            sqlbuf.append(" and a.bdate<='"+endDate+"' ");
        }

        sqlbuf.append(" group by a.PRODUCTIONNO");
        sqlbuf.append(" )");


        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.createtime desc) as rn,"
                + " a.bdate,a.PRODUCTIONNO as productionno,a.pgroupno,c.pgroupname,a.CREATEOPID as createby,e1.name as createbyname,to_char(a.createtime,'yyyy-MM-dd HH:mm:ss') as createtime,a.lastModiOpId as modifyby,e2.name as modifybyname,to_char(a.LASTMODITIME,'yyyy-MM-dd HH:mm:ss') as modifyTime,"
                + " a.ACCOUNTOPID as confirmby,e3.name as confirmbyname,to_char(a.ACCOUNTTIME,'yyyy-MM-dd HH:mm:ss') as confirmtime,a.PROCESS_STATUS as processstatus,a.PROCESS_ERP_NO as processerpno,a.PROCESS_ERP_ORG as processerporg,a.SOURCEREPORTNO,a.RETURNNO,a.RETURNSTATUS,a.departId,d1.departname  "
                + " from MES_PRODUCTIN a"
                + " inner join productin b on a.PRODUCTIONNO=b.PRODUCTIONNO "
                + " left join MES_PRODUCT_GROUP c on c.eid=a.eid and c.pgroupno=a.pgroupno "
                + " left join dcp_employee e1 on e1.eid=a.eid and e1.employeeno=a.createOpId "
                + " left join dcp_employee e2 on e2.eid=a.eid and e2.employeeno=a.lastModiOpId "
                + " left join dcp_employee e3 on e3.eid=a.eid and e3.employeeno=a.ACCOUNTOPID "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.departId and d1.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.organizationno='"+req.getOrganizationNO()+"' "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}


