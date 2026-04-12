package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_EmployeeQueryReq;
import com.dsc.spos.json.cust.res.DCP_EmployeeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_EmployeeQuery extends SPosBasicService<DCP_EmployeeQueryReq, DCP_EmployeeQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_EmployeeQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_EmployeeQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_EmployeeQueryReq>() {
        };
    }

    @Override
    protected DCP_EmployeeQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_EmployeeQueryRes();
    }

    @Override
    protected DCP_EmployeeQueryRes processJson(DCP_EmployeeQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String sql;

        DCP_EmployeeQueryRes res = this.getResponse();
        int totalRecords;                //总笔数
        int totalPages;
        String cur_langType = req.getLangType();
        //查询原因码信息
        sql = this.getQuerySql(req);

        String[] conditionValues1 = {}; //查詢條件

        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, conditionValues1);


        if (getQDataDetail != null && !getQDataDetail.isEmpty()) {
            //总页数
            String num = getQDataDetail.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            //单头主键字段
            Map<String, Boolean> condition = new HashMap<>(); //查詢條件
            condition.put("EID", true);
            condition.put("EMPLOYEENO", true);
            //调用过滤函数
            List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQDataDetail, condition);

            res.setDatas(new ArrayList<>());

            for (Map<String, Object> oneData : getQHeader) {
                DCP_EmployeeQueryRes.EmployeeInfo oneLv1 = new DCP_EmployeeQueryRes.EmployeeInfo();
                String employeeNo = String.valueOf(oneData.get("EMPLOYEENO"));

                oneLv1.setEmployeeNo(employeeNo);
                oneLv1.setStatus(String.valueOf(oneData.get("STATUS")));
                oneLv1.setCreate_datetime(StringUtils.toString(oneData.get("CREATETIME"),""));
                oneLv1.setLastmodify_datetime(StringUtils.toString(oneData.get("LASTMODITIME"),""));
                oneLv1.setCreatorID(StringUtils.toString(oneData.get("CREATEOPID"),""));
                oneLv1.setCreatorName(StringUtils.toString(oneData.get("CREATEOPNAME"),""));
                oneLv1.setCreatorDeptID(StringUtils.toString(oneData.get("CREATEDEPID"),""));
                oneLv1.setCreatorDeptName(StringUtils.toString(oneData.get("CREATEDEPTNAME"),""));
                oneLv1.setLastmodifyID(StringUtils.toString(oneData.get("LASTMODIOPID"),""));
                oneLv1.setLastmodifyName(StringUtils.toString(oneData.get("LASTMODIOPNAME"),""));

                oneLv1.setStatus(String.valueOf(oneData.get("STATUS")));
                oneLv1.setNickname(String.valueOf(oneData.get("NICKNAME")));
                oneLv1.setName(String.valueOf(oneData.get("NAME")));
                oneLv1.setEmail(String.valueOf(oneData.get("EMAIL")));
                oneLv1.setDeptNo(String.valueOf(oneData.get("DEPARTMENTNO")));
                oneLv1.setDeptName(StringUtils.toString(oneData.get("DEPARTMENTNAME"),""));
                oneLv1.setBirthdate(String.valueOf(oneData.get("BIRTHDATE")));
                oneLv1.setGender(String.valueOf(oneData.get("GENDER")));
                oneLv1.setIDnumber(String.valueOf(oneData.get("IDNUMBER")));

                oneLv1.setBelOrgNo(StringUtils.toString(oneData.get("ORGANIZATIONNO"),""));
                oneLv1.setBelOrgName(StringUtils.toString(oneData.get("ORG_NAME"),""));


                res.getDatas().add(oneLv1);
            }
        } else {
            res.setDatas(new ArrayList<>());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_EmployeeQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String sql = null;

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;


        String langType = req.getLangType();
        String status = null;

        String keyTxt = null;// req.getKeyTxt();
        if (req.getRequest() != null) {
            status = req.getRequest().getStatus();
            keyTxt = req.getRequest().getKeytxt();
        }

        String eId = req.geteId();


        StringBuilder builder = new StringBuilder("");

        builder.append(" SELECT * FROM( " +
                " SELECT COUNT(DISTINCT EMPLOYEENO ) OVER() NUM ,dense_rank() over(ORDER BY a.EMPLOYEENO) rn," +
                " EID,EMPLOYEENO,NAME,NICKNAME,GENDER,IDNUMBER, BIRTHDATE,DEPARTMENTNO,DEPARTMENTNAME," +
                " EMAIL,STATUS,CREATETIME,LASTMODITIME,CREATEOPID,CREATEOPNAME,LASTMODIOPID,LASTMODIOPNAME," +
                " CREATEDEPTID,CREATEDEPTNAME,ORGANIZATIONNO,ORG_NAME,LANG_TYPE" +
                " FROM ( " +
                "   SELECT a.EID,a.EMPLOYEENO,a.NAME,a.NICKNAME,a.GENDER,a.IDNUMBER,to_char(a.BIRTHDATE,'yyyy-MM-dd') BIRTHDATE,a.DEPARTMENTNO, " +
                "   c.DEPARTNAME AS DEPARTMENTNAME,a.EMAIL,a.STATUS,a.CREATETIME,a.LASTMODITIME, " +
                "   a.CREATEOPID,e.OPNAME AS CREATEOPNAME,a.LASTMODIOPID,f.OPNAME AS LASTMODIOPNAME, " +
                "   a.CREATEDEPTID,g.DEPARTNAME AS CREATEDEPTNAME,d.ORGANIZATIONNO,h.ORG_NAME,c.LANG_TYPE  " +
                "   FROM DCP_EMPLOYEE a " +
                "   LEFT JOIN DCP_DEPARTMENT d on a.EID=d.EID AND d.DEPARTNO=a.DEPARTMENTNO  "+
                "   LEFT JOIN DCP_DEPARTMENT_LANG c on d.EID=c.EID AND c.DEPARTNO=d.DEPARTNO AND c.LANG_TYPE='" + req.getLangType() + "'" +
                "   LEFT JOIN PLATFORM_STAFFS e ON e.EID=a.EID and e.OPNO=a.CREATEOPID " +
                "   LEFT JOIN PLATFORM_STAFFS f ON f.EID=a.EID and f.OPNO=a.LASTMODIOPID " +
                "   LEFT JOIN DCP_DEPARTMENT_LANG g on g.EID=a.EID AND g.DEPARTNO=a.CREATEDEPTID AND g.LANG_TYPE='" + req.getLangType() + "'" +
                "   LEFT JOIN DCP_ORG_LANG h on h.ORGANIZATIONNO=d.ORGANIZATIONNO AND h.LANG_TYPE='" + req.getLangType() + "'" +
                " )a WHERE EID='" + eId + "'"
        );

        if (StringUtils.isNotEmpty(keyTxt))
            builder.append(" AND ( NAME like '%%" + keyTxt + "%%' or EMPLOYEENO like '%%" + keyTxt + "%%' ) "
                    + " AND LANG_TYPE = '" + langType + "' ");

        if (StringUtils.isNotEmpty(status))
            builder.append(" AND (status= ").append(status).append(")");

        builder.append("  )  a "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize) + ""
                + " ORDER BY EMPLOYEENO ");


        return builder.toString();
    }


}
