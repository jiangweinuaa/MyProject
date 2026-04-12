package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_EmployeeAlterReq;
import com.dsc.spos.json.cust.res.DCP_EmployeeAlterRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_EmployeeAlter extends SPosAdvanceService<DCP_EmployeeAlterReq, DCP_EmployeeAlterRes> {

    @Override
    protected void processDUID(DCP_EmployeeAlterReq req, DCP_EmployeeAlterRes res) throws Exception {
        try {
            String oprType = req.getRequest().getOprType();

            String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            if (Constant.OPR_TYPE_I.equals(oprType)) {

                if (isEmployeeExist(req.geteId(),req.getRequest().getEmployeeNo())){
                    throw new RuntimeException( req.getRequest().getEmployeeNo()+ "已存在");
                }

                ColumnDataValue dataValue = new ColumnDataValue();
                dataValue.add("EID", DataValues.newString(req.geteId()));
                dataValue.add("EMPLOYEENO", DataValues.newString(req.getRequest().getEmployeeNo()));
                dataValue.add("NAME", DataValues.newString((req.getRequest().getName())));
                dataValue.add("NICKNAME", DataValues.newString(req.getRequest().getNickname()));
                dataValue.add("GENDER", DataValues.newString(req.getRequest().getGender()));
                dataValue.add("EMAIL", DataValues.newString(req.getRequest().getEmail()));
                dataValue.add("IDNUMBER", DataValues.newString(req.getRequest().getIDnumber()));
                dataValue.add("BIRTHDATE", DataValues.newDate(req.getRequest().getBirthdate()));
                dataValue.add("DEPARTMENTNO", DataValues.newString(req.getRequest().getDepartmentNo()));
                dataValue.add("STATUS", DataValues.newInteger(req.getRequest().getStatus()));
                dataValue.add("CREATEOPID", DataValues.newString(req.getOpNO()));
                dataValue.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
                dataValue.add("CREATETIME", DataValues.newDate(lastmoditime));
                dataValue.add("LASTMODIOPID", DataValues.newString(req.getOpNO()));
                dataValue.add("LASTMODITIME", DataValues.newDate(lastmoditime));

                InsBean ins1 = DataBeans.getInsBean("DCP_EMPLOYEE",dataValue);

                this.addProcessData(new DataProcessBean(ins1)); // 新增單頭

            } else if (Constant.OPR_TYPE_U.equals(oprType)) {

                ColumnDataValue condition = new ColumnDataValue();
                ColumnDataValue updateValue = new ColumnDataValue();

                condition.add("EID",DataValues.newString(req.geteId()));
                condition.add("EMPLOYEENO",DataValues.newString(req.getRequest().getEmployeeNo()));

                updateValue.add("NAME",DataValues.newString(req.getRequest().getName()));
                updateValue.add("NICKNAME",DataValues.newString(req.getRequest().getNickname()));
                updateValue.add("GENDER",DataValues.newString(req.getRequest().getGender()));
                updateValue.add("IDNUMBER",DataValues.newString(req.getRequest().getIDnumber()));
                updateValue.add("EMAIL",DataValues.newString(req.getRequest().getEmail()));
                updateValue.add("BIRTHDATE",DataValues.newDate(req.getRequest().getBirthdate()));
                updateValue.add("DEPARTMENTNO",DataValues.newString(req.getRequest().getDepartmentNo()));
                updateValue.add("STATUS",DataValues.newInteger(req.getRequest().getStatus()));
                updateValue.add("LASTMODIOPID",DataValues.newString(req.getOpNO()));
                updateValue.add("LASTMODITIME",DataValues.newDate(lastmoditime));

                UptBean uptBean1 = DataBeans.getUptBean("DCP_EMPLOYEE",condition,updateValue);
                this.addProcessData(new DataProcessBean(uptBean1));
            }

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:" + e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_EmployeeAlterReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_EmployeeAlterReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_EmployeeAlterReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_EmployeeAlterReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(req.getRequest().getOprType())) {
            errMsg.append("操作类型不能为空值 ");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getEmployeeNo())) {
            errMsg.append("员工编号不能为空值");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getName())) {
            errMsg.append("员工姓名不能为空值");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getGender())) {
            errMsg.append("性别不能为空值");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getDepartmentNo())) {
            errMsg.append("归属部门不能为空值");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getStatus())) {
            errMsg.append("状态码不能为空值 ");
            isFail = true;
        }

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_EmployeeAlterReq> getRequestType() {
        return new TypeToken<DCP_EmployeeAlterReq>() {
        };

    }

    @Override
    protected DCP_EmployeeAlterRes getResponseType() {
        return new DCP_EmployeeAlterRes();
    }

    private boolean isEmployeeExist(String... keys) {
        String sql = null;
        try {
            sql = " SELECT * FROM DCP_EMPLOYEE WHERE EID='%s' AND EMPLOYEENO='%s' ";
            sql = String.format(sql,keys);
            List<Map<String, Object>> DepartmentDatas = this.doQueryData(sql, null);
            if (DepartmentDatas != null && DepartmentDatas.size() > 0) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


}
