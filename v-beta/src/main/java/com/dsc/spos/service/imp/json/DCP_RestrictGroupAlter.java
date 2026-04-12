package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.req.DCP_PurTemplateCreateReq;
import com.dsc.spos.json.cust.req.DCP_RestrictGroupAlterReq;
import com.dsc.spos.json.cust.res.DCP_RestrictGroupAlterRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_RestrictGroupAlter  extends SPosAdvanceService<DCP_RestrictGroupAlterReq, DCP_RestrictGroupAlterRes> {

    @Override
    protected void processDUID(DCP_RestrictGroupAlterReq req, DCP_RestrictGroupAlterRes res) throws Exception {
        // TODO Auto-generated method stub
        //String sql = null;
       // try {
        DCP_RestrictGroupAlterReq.level1Elm request = req.getRequest();
        String oprType = request.getOprType();//I insert U update
        List<String> employees = getEmployees(req.geteId());
        List<String> departments = getDepartments(req.geteId());
        List<String> bizpartners = getBizpartners(req.geteId());

        for (DCP_RestrictGroupAlterReq.EmpList emp:request.getEmpList()){
            if(!employees.contains(emp.getEmpNo())){
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("员工"+emp.getEmpNo()+"不存在");
                return;
            }
        }

        for (DCP_RestrictGroupAlterReq.DeptList dept:request.getDeptList()){
            if(!departments.contains(dept.getDeptNo())){
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("部门"+dept.getDeptNo()+"不存在");
                return;
            }
        }

        if(CollUtil.isNotEmpty(request.getBizpartnerList())) {
            for (DCP_RestrictGroupAlterReq.BizpartnerList biz : request.getBizpartnerList()) {
                if (!bizpartners.contains(biz.getBizpartnerNo())) {
                    res.setSuccess(false);
                    res.setServiceStatus("200");
                    res.setServiceDescription("供应商" + biz.getBizpartnerNo() + "无效！");
                    return;
                }
            }
        }





        if(oprType.equals("I")){
                processOnCreate(req,res);
            }else{
                processOnUpdate(req,res);
            }
       // } catch (Exception e) {
            // TODO Auto-generated catch block
          //  res.setSuccess(false);
          //  res.setServiceStatus("200");
          //  res.setServiceDescription("服务执行失败");
       // }


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_RestrictGroupAlterReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_RestrictGroupAlterReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_RestrictGroupAlterReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_RestrictGroupAlterReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        //保存检查：
        //1、部门/人员至少设置一种，不可都为空！
        //2、限定供应商范围不可为空！
        //3、适用组织不可为空！
        //
        //数据校验：
        //1-检查输入部门/人员状态有效性！
        //2-检查输入供应商状态有效性！（查询DCP_BIZPARTNER.资料状态码）

        DCP_RestrictGroupAlterReq.level1Elm request = req.getRequest();
        if(Check.Null(request.getOprType())){
            errMsg.append("操作类型不能为空！");
            isFail = true;
        }
        if(Check.Null(request.getMemo())){
            //errMsg.append("memo不能为空！");
            //isFail = true;
        }

        if(Check.Null(request.getStatus())){
            errMsg.append("状态码不能为空！");
            isFail = true;
        }

        if(Check.Null(request.getGroupNo())){
            //errMsg.append("控制组编号不能为空！");
            //isFail = true;
        }

        if(Check.Null(request.getGroupNo())&&"U".equals(request.getOprType()))
        {
            errMsg.append("控制组编号不能为空！");
            isFail = true;
        }
        if(Check.Null(request.getGroupType()))
        {
            errMsg.append("控制组类型不能为空！");
            isFail = true;
        }
        if(request.getEmpList()==null){
            request.setEmpList(new ArrayList<DCP_RestrictGroupAlterReq.EmpList>());
        }
        if(request.getDeptList()==null){
            request.setDeptList(new ArrayList<DCP_RestrictGroupAlterReq.DeptList>());
        }
        if(request.getEmpList().size()<=0&&request.getDeptList().size()<=0){
            //errMsg.append("empList和deptList不能都为空！");
            //isFail = true;
        }

        if(!request.getGroupType().equals("3") ){
        if(request.getBizpartnerList()==null||request.getBizpartnerList().size()<=0){
            errMsg.append("限定交易对象不能为空！");
            isFail = true;
        }
        }
        if(request.getOrgList()==null||request.getOrgList().size()<=0){
            //errMsg.append("orgList不能为空！");
            //isFail = true;
        }

        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }



    @Override
    protected TypeToken<DCP_RestrictGroupAlterReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_RestrictGroupAlterReq>(){};
    }

    @Override
    protected DCP_RestrictGroupAlterRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_RestrictGroupAlterRes();
    }

    /**
     * 验证是否已存在
     * @param eId
     * @return
     */
    private String isRepeat(String eId, String groupType,String groupNo){
        String sql = "select groupNo from DCP_RESTRICTGROUP where EID = '"+eId+"' and GROUPTYPE = '"+groupType+"' and GROUPNO='"+groupNo+"'";
        return sql;
    }

    private  List<String> getEmployees(String eid) throws Exception{
        String sql="select employeeno from dcp_employee where eid='"+eid+"' ";
        List<Map<String, Object>> employees = this.doQueryData(sql, null);
        List<String> employeenos = employees.stream().map(var -> var.get("EMPLOYEENO").toString()).distinct().collect(Collectors.toList());

        return employeenos;
    }

    private String getMaxGroupNo(DCP_RestrictGroupAlterReq req) throws Exception{
        String billNo = "";
        StringBuffer sqlbuf = new StringBuffer();
        String sql = "";
        Date dt = new Date();
        // BJDH2020010100001 （报价单号 + 八位短日期 + 5位流水号）
        SimpleDateFormat matter = new SimpleDateFormat("yyyyMMdd");
        billNo = "CGKZ" + matter.format(dt);
        if("2".equals(req.getRequest().getGroupType())){
            billNo = "XSKZ" + matter.format(dt);
        }
        sqlbuf.append("" + "select billNO  from ( " + "select max(GROUPNO) as  billNO "
                + "  from DCP_RESTRICTGROUP " + " where eId='"+req.geteId()+"'  "
                + " and GROUPNO like '%%" + billNo + "%%' ");
        sqlbuf.append(" ) TBL ");
        sql = sqlbuf.toString();

        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if (getQData != null && getQData.isEmpty() == false) {

            billNo = (String) getQData.get(0).get("BILLNO");
            if (billNo != null && billNo.length() > 0) {
                long i;
                billNo = billNo.substring(5, billNo.length());
                i = Long.parseLong(billNo) + 1;
                billNo = i + "";
                billNo = "CGKZZ" + billNo;
            } else {
                billNo = "CGKZZ" + matter.format(dt) + "0001";
            }
        } else {
            billNo = "CGKZZ" + matter.format(dt) + "0001";
        }
        return billNo;
    }

    private String getGroupNO(JsonBasicReq req, String preFix) throws Exception  {
        String sql = null;
        String templateNo = null;
        String shopId = req.getShopId();
        String eId = req.geteId();

        StringBuffer sqlbuf = new StringBuffer("select F_DCP_GETBILLNO('"+eId+"','"+shopId+"','"+preFix+"') TEMPLATENO FROM dual");
        sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && getQData.isEmpty() == false)
        {
            templateNo = (String) getQData.get(0).get("TEMPLATENO");
        }
        else
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "取加工任务单号失败！");
        }
        return templateNo;
    }


    private List<String> getDepartments(String eid) throws Exception{
        String sql="select departno from dcp_department where eid='"+eid+"' ";
        List<Map<String, Object>> departmentList = this.doQueryData(sql, null);
        List<String> departmentnos = departmentList.stream().map(var -> var.get("DEPARTNO").toString()).distinct().collect(Collectors.toList());

        return departmentnos;
    }

    private List<String> getBizpartners(String eid) throws Exception{
        String sql="select bizpartnerno from dcp_bizpartner where eid='"+eid+"' and status='100' ";
        List<Map<String, Object>> bizpartnerList = this.doQueryData(sql, null);
        List<String> bizpartnernos = bizpartnerList.stream().map(var -> var.get("BIZPARTNERNO").toString()).distinct().collect(Collectors.toList());
        return bizpartnernos;
    }


    private void processOnCreate(DCP_RestrictGroupAlterReq req, DCP_RestrictGroupAlterRes res) throws Exception{
        String sql="";
        String eId = req.geteId();
        DCP_RestrictGroupAlterReq.level1Elm request = req.getRequest();

        String employeeNo = req.getEmployeeNo();
        String departmentNo=req.getDepartmentNo();
        String departmentName = req.getDepartmentName();
        String employeeName = req.getEmployeeName();
        String groupType = req.getRequest().getGroupType();
        String groupNo ="";
        if("2".equals(groupType)){
            groupNo= getGroupNO(req,"XSKZ");
        }else if("1".equals(groupType)){
            groupNo= getGroupNO(req,"CGKZ");
        }if("3".equals(groupType)){
            groupNo= getGroupNO(req,"CKKZ");
        }



        String status = req.getRequest().getStatus();
        String memo = req.getRequest().getMemo();


        String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        sql = this.isRepeat(eId, groupType,groupNo);
        List<Map<String, Object>> groupDatas = this.doQueryData(sql, null);
        if(groupDatas.isEmpty()) {
            String[] columns1 = {"EID", "GROUPTYPE", "GROUPNO", "MEMO", "STATUS",
                    "CREATEOPID", "CREATEDEPTID", "CREATETIME", "LASTMODIOPID", "LASTMODITIME"
            };
            DataValue[] insValue1 = null;
            insValue1 = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(groupType, Types.VARCHAR),
                    new DataValue(groupNo, Types.VARCHAR),
                    new DataValue(memo, Types.VARCHAR),
                    new DataValue(status, Types.VARCHAR),
                    new DataValue(employeeNo, Types.VARCHAR),
                    new DataValue(departmentNo, Types.VARCHAR),
                    new DataValue(nowTime, Types.DATE),
                    new DataValue(employeeNo, Types.VARCHAR),
                    new DataValue(nowTime, Types.DATE)

            };

            InsBean ib1 = new InsBean("DCP_RESTRICTGROUP", columns1);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1)); // 新增單頭


            List<DCP_RestrictGroupAlterReq.Name_lang> getLangDatas = req.getRequest().getName_lang();
            for (DCP_RestrictGroupAlterReq.Name_lang oneLv1 : getLangDatas) {

                String[] columnsName = {
                        "EID", "GROUPTYPE", "GROUPNO", "LANG_TYPE", "NAME"
                };

                String langType = oneLv1.getLangType();
                String desc = oneLv1.getName();

                DataValue[] insValueDetail = new DataValue[]
                        {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(groupType, Types.VARCHAR),
                                new DataValue(groupNo, Types.VARCHAR),
                                new DataValue(langType, Types.VARCHAR),
                                new DataValue(desc, Types.VARCHAR)
                        };
                InsBean ib2 = new InsBean("DCP_RESTRICTGROUP_LANG", columnsName);
                ib2.addValues(insValueDetail);
                this.addProcessData(new DataProcessBean(ib2));


            }

            List<DCP_RestrictGroupAlterReq.DeptList> deptList = req.getRequest().getDeptList();
            for (DCP_RestrictGroupAlterReq.DeptList oneLv1 : deptList) {
                String[] columnsName = {
                        "EID", "GROUPTYPE", "GROUPNO", "DEPARTMENTNO", "STATUS"
                };
                String deptNo = oneLv1.getDeptNo();
                String deptStatus = oneLv1.getStatus();
                DataValue[] insValueDetail = new DataValue[]
                        {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(groupType, Types.VARCHAR),
                                new DataValue(groupNo, Types.VARCHAR),
                                new DataValue(deptNo, Types.VARCHAR),
                                new DataValue(deptStatus, Types.VARCHAR)
                        };
                InsBean ib2 = new InsBean("DCP_RESTRICTGROUP_DEPT", columnsName);
                ib2.addValues(insValueDetail);
                this.addProcessData(new DataProcessBean(ib2));
            }

            List<DCP_RestrictGroupAlterReq.EmpList> empList = req.getRequest().getEmpList();
            for (DCP_RestrictGroupAlterReq.EmpList oneLv1 : empList) {
                String[] columnsName = {
                        "EID", "GROUPTYPE", "GROUPNO", "EMPLOYEENO", "STATUS"
                };
                String empNo = oneLv1.getEmpNo();
                String empStatus = oneLv1.getStatus();
                DataValue[] insValueDetail = new DataValue[]
                        {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(groupType, Types.VARCHAR),
                                new DataValue(groupNo, Types.VARCHAR),
                                new DataValue(empNo, Types.VARCHAR),
                                new DataValue(empStatus, Types.VARCHAR)
                        };
                InsBean ib2 = new InsBean("DCP_RESTRICTGROUP_EMP", columnsName);
                ib2.addValues(insValueDetail);
                this.addProcessData(new DataProcessBean(ib2));

            }
            if(CollUtil.isNotEmpty(request.getBizpartnerList())) {
                List<DCP_RestrictGroupAlterReq.BizpartnerList> bizpartnerList = req.getRequest().getBizpartnerList();

                for (DCP_RestrictGroupAlterReq.BizpartnerList oneLv1 : bizpartnerList) {
                    String[] columnsName = {
                            "EID", "GROUPTYPE", "GROUPNO", "BIZPARTNERNO", "STATUS"
                    };
                    String bizpartnerNo = oneLv1.getBizpartnerNo();
                    String bizpartnerStatus = oneLv1.getStatus();
                    DataValue[] insValueDetail = new DataValue[]
                            {
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(groupType, Types.VARCHAR),
                                    new DataValue(groupNo, Types.VARCHAR),
                                    new DataValue(bizpartnerNo, Types.VARCHAR),
                                    new DataValue(bizpartnerStatus, Types.VARCHAR)
                            };
                    InsBean ib2 = new InsBean("DCP_RESTRICTGROUP_BIZPARTNER", columnsName);
                    ib2.addValues(insValueDetail);
                    this.addProcessData(new DataProcessBean(ib2));
                }
            }
            List<DCP_RestrictGroupAlterReq.OrgList> orgList = req.getRequest().getOrgList();
            if(CollUtil.isNotEmpty(orgList)) {
                for (DCP_RestrictGroupAlterReq.OrgList oneLv1 : orgList) {
                    String[] columnsName = {
                            "EID", "GROUPTYPE", "GROUPNO", "ORGANIZATIONNO", "STATUS"
                    };
                    String orgNo = oneLv1.getOrgNo();
                    String orgStatus = oneLv1.getStatus();
                    DataValue[] insValueDetail = new DataValue[]
                            {
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(groupType, Types.VARCHAR),
                                    new DataValue(groupNo, Types.VARCHAR),
                                    new DataValue(orgNo, Types.VARCHAR),
                                    new DataValue(orgStatus, Types.VARCHAR)
                            };
                    InsBean ib2 = new InsBean("DCP_RESTRICTGROUP_ORG", columnsName);
                    ib2.addValues(insValueDetail);
                    this.addProcessData(new DataProcessBean(ib2));

                }
            }

            List<DCP_RestrictGroupAlterReq.WarehouseList> warehouseList = req.getRequest().getWarehouseList();
            if(CollUtil.isNotEmpty(warehouseList)) {
                for (DCP_RestrictGroupAlterReq.WarehouseList oneLv1 : warehouseList) {
                    String[] columnsName = {
                            "EID", "GROUPTYPE", "GROUPNO", "ORGANIZATIONNO", "WAREHOUSE", "STATUS"
                    };
                    String orgNo = oneLv1.getOrganizationNo();
                    String orgStatus = oneLv1.getStatus();
                    String warehouse = oneLv1.getWarehouse();

                    DataValue[] insValueDetail = new DataValue[]
                            {
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(groupType, Types.VARCHAR),
                                    new DataValue(groupNo, Types.VARCHAR),
                                    new DataValue(orgNo, Types.VARCHAR),
                                    new DataValue(warehouse, Types.VARCHAR),
                                    new DataValue(orgStatus, Types.VARCHAR)
                            };
                    InsBean ib2 = new InsBean("DCP_RESTRICTGROUP_WAREHOUSE", columnsName);
                    ib2.addValues(insValueDetail);
                    this.addProcessData(new DataProcessBean(ib2));

                }
            }
            this.doExecuteDataToDB();

            res.setGroupNo(groupNo);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        }else{
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败: 控制组已存在");
            return;
        }

    }

    private void processOnUpdate(DCP_RestrictGroupAlterReq req, DCP_RestrictGroupAlterRes res) throws Exception{
        String sql="";
        String eId = req.geteId();
        DCP_RestrictGroupAlterReq.level1Elm request = req.getRequest();

        String employeeNo = req.getEmployeeNo();
        String departmentNo=req.getDepartmentNo();
        String departmentName = req.getDepartmentName();
        String employeeName = req.getEmployeeName();
        String groupType = req.getRequest().getGroupType();
        String groupNo = req.getRequest().getGroupNo();
        String status = req.getRequest().getStatus();
        String memo = req.getRequest().getMemo();
        String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        UptBean ub1 = null;
        ub1 = new UptBean("DCP_RESTRICTGROUP");

        ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
        ub1.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
        ub1.addUpdateValue("LASTMODIOPID", new DataValue(employeeNo, Types.VARCHAR));
        ub1.addUpdateValue("LASTMODITIME", new DataValue(nowTime, Types.DATE));
        // condition
        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("GROUPTYPE", new DataValue(groupType, Types.VARCHAR));
        ub1.addCondition("GROUPNO", new DataValue(groupNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));

        DelBean db2 = new DelBean("DCP_RESTRICTGROUP_LANG");
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("GROUPTYPE", new DataValue(groupType, Types.VARCHAR));
        db2.addCondition("GROUPNO", new DataValue(groupNo, Types.VARCHAR));

        this.addProcessData(new DataProcessBean(db2));

        DelBean db3 = new DelBean("DCP_RESTRICTGROUP_DEPT");
        db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db3.addCondition("GROUPTYPE", new DataValue(groupType, Types.VARCHAR));
        db3.addCondition("GROUPNO", new DataValue(groupNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db3));

        DelBean db4 = new DelBean("DCP_RESTRICTGROUP_EMP");
        db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db4.addCondition("GROUPTYPE", new DataValue(groupType, Types.VARCHAR));
        db4.addCondition("GROUPNO", new DataValue(groupNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db4));

        DelBean db5 = new DelBean("DCP_RESTRICTGROUP_BIZPARTNER");
        db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db5.addCondition("GROUPTYPE", new DataValue(groupType, Types.VARCHAR));
        db5.addCondition("GROUPNO", new DataValue(groupNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db5));



        DelBean db7 = new DelBean("DCP_RESTRICTGROUP_ORG");
        db7.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db7.addCondition("GROUPTYPE", new DataValue(groupType, Types.VARCHAR));
        db7.addCondition("GROUPNO", new DataValue(groupNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db7));

        DelBean db8 = new DelBean("DCP_RESTRICTGROUP_WAREHOUSE");
        db8.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db8.addCondition("GROUPTYPE", new DataValue(groupType, Types.VARCHAR));
        db8.addCondition("GROUPNO", new DataValue(groupNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db8));




        List<DCP_RestrictGroupAlterReq.Name_lang> getLangDatas = req.getRequest().getName_lang();
        for (DCP_RestrictGroupAlterReq.Name_lang oneLv1 : getLangDatas) {

            String[] columnsName = {
                    "EID", "GROUPTYPE", "GROUPNO", "LANG_TYPE", "NAME"
            };

            String langType = oneLv1.getLangType();
            String desc = oneLv1.getName();

            DataValue[] insValueDetail = new DataValue[]
                    {
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(groupType, Types.VARCHAR),
                            new DataValue(groupNo, Types.VARCHAR),
                            new DataValue(langType, Types.VARCHAR),
                            new DataValue(desc, Types.VARCHAR)
                    };
            InsBean ib2 = new InsBean("DCP_RESTRICTGROUP_LANG", columnsName);
            ib2.addValues(insValueDetail);
            this.addProcessData(new DataProcessBean(ib2));


        }

        List<DCP_RestrictGroupAlterReq.DeptList> deptList = req.getRequest().getDeptList();
        for (DCP_RestrictGroupAlterReq.DeptList oneLv1 : deptList) {
            String[] columnsName = {
                    "EID", "GROUPTYPE", "GROUPNO", "DEPARTMENTNO", "STATUS"
            };
            String deptNo = oneLv1.getDeptNo();
            String deptStatus = oneLv1.getStatus();
            DataValue[] insValueDetail = new DataValue[]
                    {
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(groupType, Types.VARCHAR),
                            new DataValue(groupNo, Types.VARCHAR),
                            new DataValue(deptNo, Types.VARCHAR),
                            new DataValue(deptStatus, Types.VARCHAR)
                    };
            InsBean ib2 = new InsBean("DCP_RESTRICTGROUP_DEPT", columnsName);
            ib2.addValues(insValueDetail);
            this.addProcessData(new DataProcessBean(ib2));
        }

        List<DCP_RestrictGroupAlterReq.EmpList> empList = req.getRequest().getEmpList();
        for (DCP_RestrictGroupAlterReq.EmpList oneLv1 : empList) {
            String[] columnsName = {
                    "EID", "GROUPTYPE", "GROUPNO", "EMPLOYEENO", "STATUS"
            };
            String empNo = oneLv1.getEmpNo();
            String empStatus = oneLv1.getStatus();
            DataValue[] insValueDetail = new DataValue[]
                    {
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(groupType, Types.VARCHAR),
                            new DataValue(groupNo, Types.VARCHAR),
                            new DataValue(empNo, Types.VARCHAR),
                            new DataValue(empStatus, Types.VARCHAR)
                    };
            InsBean ib2 = new InsBean("DCP_RESTRICTGROUP_EMP", columnsName);
            ib2.addValues(insValueDetail);
            this.addProcessData(new DataProcessBean(ib2));

        }
        if(CollUtil.isNotEmpty(request.getBizpartnerList())) {
            List<DCP_RestrictGroupAlterReq.BizpartnerList> bizpartnerList = req.getRequest().getBizpartnerList();
            for (DCP_RestrictGroupAlterReq.BizpartnerList oneLv1 : bizpartnerList) {
                String[] columnsName = {
                        "EID", "GROUPTYPE", "GROUPNO", "BIZPARTNERNO", "STATUS"
                };
                String bizpartnerNo = oneLv1.getBizpartnerNo();
                String bizpartnerStatus = oneLv1.getStatus();
                DataValue[] insValueDetail = new DataValue[]
                        {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(groupType, Types.VARCHAR),
                                new DataValue(groupNo, Types.VARCHAR),
                                new DataValue(bizpartnerNo, Types.VARCHAR),
                                new DataValue(bizpartnerStatus, Types.VARCHAR)
                        };
                InsBean ib2 = new InsBean("DCP_RESTRICTGROUP_BIZPARTNER", columnsName);
                ib2.addValues(insValueDetail);
                this.addProcessData(new DataProcessBean(ib2));
            }
        }

        List<DCP_RestrictGroupAlterReq.OrgList> orgList = req.getRequest().getOrgList();
        if(CollUtil.isNotEmpty(orgList)) {
            for (DCP_RestrictGroupAlterReq.OrgList oneLv1 : orgList) {
                String[] columnsName = {
                        "EID", "GROUPTYPE", "GROUPNO", "ORGANIZATIONNO", "STATUS"
                };
                String orgNo = oneLv1.getOrgNo();
                String orgStatus = oneLv1.getStatus();
                DataValue[] insValueDetail = new DataValue[]
                        {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(groupType, Types.VARCHAR),
                                new DataValue(groupNo, Types.VARCHAR),
                                new DataValue(orgNo, Types.VARCHAR),
                                new DataValue(orgStatus, Types.VARCHAR)
                        };
                InsBean ib2 = new InsBean("DCP_RESTRICTGROUP_ORG", columnsName);
                ib2.addValues(insValueDetail);
                this.addProcessData(new DataProcessBean(ib2));

            }
        }

        List<DCP_RestrictGroupAlterReq.WarehouseList> warehouseList = req.getRequest().getWarehouseList();
        if(CollUtil.isNotEmpty(warehouseList)) {
            for (DCP_RestrictGroupAlterReq.WarehouseList oneLv1 : warehouseList) {
                String[] columnsName = {
                        "EID", "GROUPTYPE", "GROUPNO", "ORGANIZATIONNO", "WAREHOUSE", "STATUS"
                };
                String orgNo = oneLv1.getOrganizationNo();
                String orgStatus = oneLv1.getStatus();
                String warehouse = oneLv1.getWarehouse();

                DataValue[] insValueDetail = new DataValue[]
                        {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(groupType, Types.VARCHAR),
                                new DataValue(groupNo, Types.VARCHAR),
                                new DataValue(orgNo, Types.VARCHAR),
                                new DataValue(warehouse, Types.VARCHAR),
                                new DataValue(orgStatus, Types.VARCHAR)
                        };
                InsBean ib2 = new InsBean("DCP_RESTRICTGROUP_WAREHOUSE", columnsName);
                ib2.addValues(insValueDetail);
                this.addProcessData(new DataProcessBean(ib2));

            }
        }

        this.doExecuteDataToDB();

        res.setGroupNo(groupNo);
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

}
