package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PurOrderDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PurOrderDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_PurOrderDelete extends SPosAdvanceService<DCP_PurOrderDeleteReq, DCP_PurOrderDeleteRes> {

    @Override
    protected boolean isVerifyFail(DCP_PurOrderDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        //必传值不为空
        String no = req.getRequest().getPurOrderNo();
        String eid = req.geteId();


        if(Check.Null(no)){
            errMsg.append("purOrderNo不能为空值 ");
            isFail = true;
        }


        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    /**
     * 查询多语言信息
     */
    @Override
    protected String getQuerySql(DCP_PurOrderDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return "";
    }

    private boolean CheckPurOrder(String eid, String no,String organizationno) throws Exception {
        String sql="select * from DCP_PURORDER a" +
                " where a.PURORDERNO='"+no+"' and a.status='0' and a.eid='"+eid+"' and a.organizationno='"+organizationno+"' ";
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);

        return getQData.size()>0;
    }

    @Override
    protected void processDUID(DCP_PurOrderDeleteReq req, DCP_PurOrderDeleteRes res) throws Exception {
        // TODO Auto-generated method stub
        String sql = "";
        try
        {
            String no = req.getRequest().getPurOrderNo();
            String eId = req.geteId();
            String organizationNO = req.getOrganizationNO();

            if(!CheckPurOrder(eId,no,organizationNO)){
                res.setSuccess(false);
                res.setServiceStatus("000");
                res.setServiceDescription("单据状态非[新建]不可删除！");
                return;
            }

            //数据权限校验
            if(!canDeleteDoc(req,"SCM0402","DELETE_RANGE")){//SCM0402固定
                res.setSuccess(false);
                res.setServiceStatus("000");
                res.setServiceDescription("没有删除权限！");
                return;
            }


            DelBean db1 = new DelBean("DCP_PURORDER");
            db1.addCondition("PURORDERNO", new DataValue(no, Types.VARCHAR));
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            DelBean db2 = new DelBean("DCP_PURORDER_DETAIL");
            db2.addCondition("PURORDERNO", new DataValue(no, Types.VARCHAR));
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db2.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));

            DelBean db3 = new DelBean("DCP_PURORDER_DELIVERY");
            db3.addCondition("PURORDERNO", new DataValue(no, Types.VARCHAR));
            db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db3.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db3));

            DelBean db4 = new DelBean("DCP_PURORDER_PAY");
            db4.addCondition("PURORDERNO", new DataValue(no, Types.VARCHAR));
            db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db4.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db4));

            DelBean db5 = new DelBean("DCP_PURORDER_SOURCE");
            db5.addCondition("PURORDERNO", new DataValue(no, Types.VARCHAR));
            db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db5.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db5));

            String psSql=" select * from DCP_PURORDER_SOURCE a where a.eid='"+eId+"' " +
                    " and a.organizationno='"+organizationNO+"' and a.purorderno='"+no+"' ";
            List<Map<String, Object>> getPsData=this.doQueryData(psSql, null);
            for (Map<String, Object> row : getPsData){
                //更新底稿 DISTRISTATUS
                UptBean ub1 = new UptBean("DCP_DEMAND");
                ub1.addUpdateValue("DISTRISTATUS", new DataValue("00", Types.VARCHAR));

                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("ORDERNO", new DataValue(row.get("SOURCEBILLNO").toString(), Types.VARCHAR));
                ub1.addCondition("ITEM", new DataValue(row.get("OITEM").toString(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));
            }

            //删除内部交易
            ColumnDataValue condition = new ColumnDataValue();
            condition.add("EID", req.geteId());
            condition.add("BILLNO", req.getRequest().getPurOrderNo());
            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_DETAIL", condition)));
            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_ROUTE", condition)));



            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        }
        catch (Exception e)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurOrderDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurOrderDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurOrderDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    protected TypeToken<DCP_PurOrderDeleteReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_PurOrderDeleteReq>(){};
    }
    @Override
    protected DCP_PurOrderDeleteRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_PurOrderDeleteRes();
    }

    private boolean canDeleteDoc(DCP_PurOrderDeleteReq req,String modularNo,String range ) throws Exception{

        String purOrderNo = req.getRequest().getPurOrderNo();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        //PLATFORM_STAFFS_ROLE
        String opNO = req.getOpNO();
        MyCommon cm=new MyCommon();

        String sqlPsr="select * from PLATFORM_STAFFS_ROLE where eid='"+req.geteId()+"' and OPNO='"+req.getOpNO()+"' and status='100' ";
        List<Map<String, Object>> getPsrData=this.doQueryData(sqlPsr, null);
        if(getPsrData.size()==0){
            return false;
        }
        List<String> opGroups = getPsrData.stream().map(x -> x.get("OPGROUP").toString()).distinct().collect(Collectors.toList());
        String opGroupStr="";
        for(String opGroup:opGroups){
            opGroupStr+="'"+opGroup+"',";
        }
        opGroupStr=opGroupStr.substring(0,opGroupStr.length()-1);
        String sqlPb="select * from PLATFORM_BILLPOWER where eid='"+req.geteId()+"' and MODULARNO='"+modularNo+"' and OPGROUP in ("+opGroupStr+") and status='100' ";
        List<Map<String, Object>> getPbData=this.doQueryData(sqlPb, null);
        if(getPbData.size()==0){
            return false;
        }
        //0-全部 1-个人 2-所属部门 3-所属部门及下级部门 4-同级部门
        List<Map<String, Object>> powerrange = getPbData.stream().filter(x -> x.get(range).toString().equals("0")).collect(Collectors.toList());
        if(powerrange.size()>0){
            return true;
        }
        //获取人员 排除全部了
        List<String> opENos=new ArrayList();

        //获取同部门人员
        String deSql="select * from dcp_department where eid='"+req.geteId()+"' and organizationno='"+req.getOrganizationNO()+"' " +
                " and status='100' ";
        List<Map<String, Object>> getAllDeData=this.doQueryData(deSql, null);

        //查找上级部门
        String upDepartNo="";
        String upDSql="select a.departno from dcp_department a " +
                " inner join dcp_department b on a.eid=b.eid and a.organizationno=b.organizationno and a.departno=b.updepartno " +
                " where b.eid='"+req.geteId()+"' and b.organizationno='"+req.getOrganizationNO()+"' " +
                " and b.departno='"+departmentNo+"' ";
        List<Map<String, Object>> getUpDeData=this.doQueryData(upDSql, null);
        if(getUpDeData.size()>0){
            upDepartNo=getUpDeData.get(0).get("DEPARTNO").toString();
        }

        List<String> departmentList=new ArrayList();
        for (Map<String, Object> map:getPbData){
            String powerRange=map.get(range).toString();
            //if(powerRange.equals("1")&&!opNos.contains(employeeNo)){
              //  opNos.add(employeeNo);
            //}
            if(!opENos.contains(employeeNo)){
                opENos.add(employeeNo);
            }

            //234 要加所属部门
            if(!"1".equals(powerRange)){
                departmentList.add(departmentNo);
            }

            if("3".equals(powerRange)){
                //所属部门及下级部门
                addChildDepartment(departmentList,getAllDeData,departmentNo);
            }

            if("4".equals(powerRange)){
                //同级部门及下级部门
                if(upDepartNo.length()>0){
                    addChildDepartment(departmentList,getAllDeData,upDepartNo);
                }
            }
        }

        if(departmentList.size()>0){
            StringBuffer sJoinDepartNo=new StringBuffer("");
            for(String departNo:departmentList){
                sJoinDepartNo.append("'"+departNo+"',");
            }
            Map<String, String> mapDepartNo=new HashMap<String, String>();
            mapDepartNo.put("DEPARTNO", sJoinDepartNo.toString());

            String withasSql_departno="";
            withasSql_departno=cm.getFormatSourceMultiColWith(mapDepartNo);
            mapDepartNo=null;

            if (!withasSql_departno.equals("")) {

            }else {

                String sqlEmployee = "with p AS ( " + withasSql_departno + ") " +
                        "select a.* from dcp_employee a " +
                        "inner join p on p.departno=a.DEPARTMENTNO " +
                        "where a.eid='" + req.geteId() + "' and a.status='100' ";
                List<Map<String, Object>> getEmployeeData = this.doQueryData(sqlEmployee, null);
                for (Map<String, Object> map : getEmployeeData) {
                    String employeeNo1 = map.get("EMPLOYEENO").toString();
                    if (!opENos.contains(employeeNo1)) {
                        opENos.add(employeeNo1);
                    }
                }
            }



        }

        if(opENos.size()>0){
            StringBuffer sJoinEmployeeNo=new StringBuffer("");
            for(String employeeNo1:opENos){
                sJoinEmployeeNo.append(employeeNo1+",");
            }
            Map<String, String> mapEmployeeNo=new HashMap<String, String>();
            mapEmployeeNo.put("EMPLOYEENO", sJoinEmployeeNo.toString());

            String withasSql_employeeno="";
            withasSql_employeeno=cm.getFormatSourceMultiColWith(mapEmployeeNo);
            mapEmployeeNo=null;

            if (withasSql_employeeno.equals("")) {
                return  false;
            }

            String purOrderSql="with p AS ( " + withasSql_employeeno + ") " +
                    " select * from dcp_purorder a " +
                    " inner join p on p.EMPLOYEENO=a.OWNOPID " +
                    " where a.purorderno='"+purOrderNo+"' and a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' "
                    ;
            List<Map<String, Object>> getPurOrderData=this.doQueryData(purOrderSql, null);
            if(getPurOrderData.size()>0){
                return true;
            }

        }

        return false;
    }

    private void addChildDepartment(List departmentNos,List<Map<String, Object>> getAllDeData,String upDepartmentNo) throws Exception{
        for (Map<String, Object> map:getAllDeData){
            String departno = map.get("DEPARTNO").toString();
            String updepartno = map.get("UPDEPARTNO").toString();
            if(updepartno.equals(upDepartmentNo)&&!departmentNos.contains(departno)){
                departmentNos.add(departno);
                addChildDepartment(departmentNos,getAllDeData,departno);
            }
        }
    }

}
