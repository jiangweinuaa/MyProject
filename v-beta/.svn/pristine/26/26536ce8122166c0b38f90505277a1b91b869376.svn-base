package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ROrderQueryReq;
import com.dsc.spos.json.cust.res.DCP_ROrderQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ROrderQuery  extends SPosBasicService<DCP_ROrderQueryReq, DCP_ROrderQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ROrderQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ROrderQueryReq> getRequestType() {
        return new TypeToken<DCP_ROrderQueryReq>(){};
    }

    @Override
    protected DCP_ROrderQueryRes getResponseType() {
        return new DCP_ROrderQueryRes();
    }

    @Override
    protected DCP_ROrderQueryRes processJson(DCP_ROrderQueryReq req) throws Exception {
        DCP_ROrderQueryRes res = this.getResponse();
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
                DCP_ROrderQueryRes.level1Elm level1Elm = res.new level1Elm();
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setBDate(row.get("BDATE").toString());
                level1Elm.setROrderNo(row.get("RORDERNO").toString());
                level1Elm.setRDate(row.get("RDATE").toString());
                level1Elm.setRDays(row.get("RDAYS").toString());
                level1Elm.setEmployeeId(row.get("EMPLOYEEID").toString());
                level1Elm.setEmployeeName(row.get("EMPLOYEENAME").toString());
                level1Elm.setDepartId(row.get("DEPARTID").toString());
                level1Elm.setDepartName(row.get("DEPARTNAME").toString());
                level1Elm.setTotCqty(row.get("TOTCQTY").toString());
                level1Elm.setTotPqty(row.get("TOTPQTY").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                level1Elm.setCreateBy(row.get("CREATEBY").toString());
                level1Elm.setCreateByName(row.get("CREATEBYNAME").toString());
                level1Elm.setCreateTime(row.get("CREATETIME").toString());
                level1Elm.setModifyBy(row.get("MODIFYBY").toString());
                level1Elm.setModifyByName(row.get("MODIFYBYNAME").toString());
                level1Elm.setModifyTime(row.get("MODIFYTIME").toString());
                level1Elm.setConfirmBy(row.get("CONFIRMBY").toString());
                level1Elm.setConfirmByName(row.get("CONFIRMBYNAME").toString());
                level1Elm.setConfirmTime(row.get("CONFIRMTIME").toString());
                level1Elm.setDepartId(row.get("DEPARTID").toString());
                level1Elm.setDepartName(row.get("DEPARTNAME").toString());

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
    protected String getQuerySql(DCP_ROrderQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        String keyTxt = req.getRequest().getKeyTxt();
        StringBuffer sqlbuf=new StringBuffer();


        String status = req.getRequest().getStatus();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with rorder as ("
                + " select a.rorderno from DCP_RORDER a"
                + " where a.eid='"+eId+"' and a.organizationno='"+ req.getOrganizationNO()+"' "
        );
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.rorderno like '%"+keyTxt+"%' "
                    + " ) ");
        }
        if(Check.NotNull(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }

        if (!Check.Null(beginDate)){
            sqlbuf.append(" and a.bdate>='"+beginDate+"' ");
        }

        if (!Check.Null(endDate)){
            sqlbuf.append(" and a.bdate<='"+endDate+"' ");
        }

        sqlbuf.append(" group by a.rorderno");
        sqlbuf.append(" )");

        //EMPLOYEEID
        //DEPARTID

        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.createtime desc) as rn,"
                + " a.bdate,a.RORDERNO,a.RDATE,a.RDAYS,a.TOTCQTY,a.TOTPQTY,a.memo,a.STATUS,"
                + " a.createby ,e1.op_name as createbyname,a.modifyby,e2.op_name as modifybyname,a.createTime,a.modifyTime,a.departId,d1.departname as departNAME,a.confirmby,a.confirmtime,e3.op_name as confirmbyname,"
                + " a.cancelby,e4.op_name as cancelbyname,a.closeby,e5.op_name as closebyname,a.canceltime,a.closetime,a.employeeid,e0.name as employeename  "
                + " from DCP_RORDER a"
                + " inner join rorder b on a.rorderno=b.rorderno "
                + " left join dcp_employee e0 on e0.eid=a.eid and e0.employeeno=a.EMPLOYEEID "
                + " left join platform_staffs_lang e1 on e1.eid=a.eid and e1.opno=a.createBy and e1.lang_type='"+req.getLangType()+"' "
                + " left join platform_staffs_lang e2 on e2.eid=a.eid and e2.opno=a.modifyby and e2.lang_type='"+req.getLangType()+"' "
                + " left join platform_staffs_lang e3 on e3.eid=a.eid and e3.opno=a.confirmby and e3.lang_type='"+req.getLangType()+"' "
                + " left join platform_staffs_lang e4 on e4.eid=a.eid and e4.opno=a.cancelby and e4.lang_type='"+req.getLangType()+"' "
                + " left join platform_staffs_lang e5 on e5.eid=a.eid and e5.opno=a.closeby and e5.lang_type='"+req.getLangType()+"' "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.departid and d1.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.organizationno='"+req.getOrganizationNO()+"' "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}



