package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_EmpOpenQryReq;
import com.dsc.spos.json.cust.res.DCP_EmpOpenQryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_EmpOpenQry extends SPosBasicService<DCP_EmpOpenQryReq, DCP_EmpOpenQryRes> {
    @Override
    protected boolean isVerifyFail(DCP_EmpOpenQryReq req) throws Exception {
        boolean isFail = false;
        DCP_EmpOpenQryReq.levelElm request = req.getRequest();
        StringBuffer errMsg = new StringBuffer("");
        if (request == null) {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_EmpOpenQryReq> getRequestType() {
        return new TypeToken<DCP_EmpOpenQryReq>() {
        };
    }

    @Override
    protected DCP_EmpOpenQryRes getResponseType() {
        return new DCP_EmpOpenQryRes();
    }

    @Override
    protected DCP_EmpOpenQryRes processJson(DCP_EmpOpenQryReq req) throws Exception {
        DCP_EmpOpenQryRes res = null;
        res = this.getResponse();

        int totalRecords = 0;                                //总笔数
        int totalPages = 0;

        res.setDatas(new ArrayList<DCP_EmpOpenQryRes.level1Elm>());
        try {
            String sql ="";
            sql = this.getQuerySql(req);
            List<Map<String, Object>> getEmployees = this.doQueryData(sql, null);
            if(!CollectionUtils.isEmpty(getEmployees)){

                String num = getEmployees.get(0).get("NUM").toString();
                totalRecords = Integer.parseInt(num);

                // 计算页数
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                for (Map<String, Object> getEmployee : getEmployees) {
                    DCP_EmpOpenQryRes.level1Elm level1Elm = res.new level1Elm();
                    String employeeno = getEmployee.get("EMPLOYEENO").toString();
                    String name = getEmployee.get("NAME").toString();
                    String departNo = getEmployee.get("DEPARTMENTNO").toString();
                    String departName = getEmployee.get("DEPARTNAME").toString();
                    level1Elm.setEmpNo(employeeno);
                    level1Elm.setEmpName(name);
                    level1Elm.setDeptNo(departNo);
                    level1Elm.setDeptName(departName);
                    level1Elm.setBelOrgNo(getEmployee.get("BELORGNO").toString());
                    level1Elm.setBelOrgName(getEmployee.get("BELORGNAME").toString());

                    res.getDatas().add(level1Elm);
                }
            }

            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败!"+e.getMessage());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_EmpOpenQryReq req) throws Exception {
        String langType = req.getLangType();
        String eid = req.geteId();
        String keyTxt = req.getRequest().getKeyTxt();
        //String status = req.getRequest().getStatus();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //分页起始位置
        int startRow = (pageNumber - 1) * pageSize;

        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");


        sqlbuf.append("SELECT * FROM ( " +
                " SELECT COUNT(DISTINCT EMPLOYEENO) OVER () AS num, DENSE_RANK() OVER (ORDER BY EMPLOYEENO) AS rn , a.* FROM ( " +
                " SELECT a.EMPLOYEENO, a.NAME, a.DEPARTMENTNO, b.DEPARTNAME,c.organizationno as belorgno,d.org_name as belorgname " +
                " FROM DCP_EMPLOYEE a " +
                " LEFT JOIN DCP_DEPARTMENT_lang b ON a.EID = b.EID AND a.DEPARTMENTNO = b.DEPARTNO AND b.LANG_TYPE = '"+langType+"' " +
                " left join dcp_department c on c.eid=a.eid and c.departno=a.departmentno" +
                " left join dcp_org_lang d on d.eid=c.eid and d.organizationno=c.organizationno and d.lang_type='"+langType+"' " +
                " WHERE a.EID = '"+eid+"' and a.status='100'  " +
                " ");

        //if(!Check.Null(status)){
        //    sqlbuf.append(" AND a.status = '"+status+"'");

        //}
        if(!Check.Null(keyTxt)){
            sqlbuf.append(" AND (a.EMPLOYEENO LIKE '%%"+keyTxt+"%%' " +
                    " or a.NAME LIKE '%%"+keyTxt+"%%' " +
                    " or a.DEPARTMENTNO LIKE '%%"+keyTxt+"%%' " +
                    " or b.DEPARTNAME LIKE '%%"+keyTxt+"%%')");
        }
        sqlbuf.append("  ) a ) WHERE RN > " + startRow + " AND rn <= " + (startRow + pageSize) + " ORDER BY employeeno DESC ");

        sql = sqlbuf.toString();
        return sql;
    }

}
