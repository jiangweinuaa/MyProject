package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_DistriOrderQueryReq;
import com.dsc.spos.json.cust.res.DCP_DistriOrderQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_DistriOrderQuery extends SPosBasicService<DCP_DistriOrderQueryReq, DCP_DistriOrderQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_DistriOrderQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        if (req.getRequest()==null)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "request节点不存在！");
        }
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_DistriOrderQueryReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_DistriOrderQueryReq>(){};
    }

    @Override
    protected DCP_DistriOrderQueryRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_DistriOrderQueryRes();
    }

    @Override
    protected DCP_DistriOrderQueryRes processJson(DCP_DistriOrderQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        DCP_DistriOrderQueryRes res = this.getResponse();
        int totalRecords;		//总笔数
        int totalPages;
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<DCP_DistriOrderQueryRes.Level1Elm>());
        if (getQData != null && getQData.isEmpty() == false)
        {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            res.setDatas(new ArrayList<>());
            for (Map<String, Object> oneData : getQData)
            {
                DCP_DistriOrderQueryRes.Level1Elm level1Elm = res.new Level1Elm();
                level1Elm.setStatus(oneData.get("STATUS").toString());
                level1Elm.setBillNo(oneData.get("BILLNO").toString());
                level1Elm.setBDate(oneData.get("BDATE").toString());
                level1Elm.setRDate(oneData.get("RDATE").toString());
                level1Elm.setDemandOrgNo(oneData.get("DEMANDORGNO").toString());
                level1Elm.setDemandOrgName(oneData.get("DEMANDORGNAME").toString());
                level1Elm.setMemo(oneData.get("MEMO").toString());
                level1Elm.setEmployeeId(oneData.get("EMPLOYEEID").toString());
                level1Elm.setEmployeeName(oneData.get("EMPLOYEENAME").toString());
                level1Elm.setDepartId(oneData.get("DEPARTID").toString());
                level1Elm.setDepartName(oneData.get("DEPARTNAME").toString());
                level1Elm.setCreateOpId(oneData.get("CREATEOPID").toString());
                level1Elm.setCreateOpName(oneData.get("CREATEOPNAME").toString());
                level1Elm.setCreateDeptId(oneData.get("CREATEDEPTID").toString());
                level1Elm.setCreatDeptName(oneData.get("CREATDEPTNAME").toString());
                level1Elm.setCreateDateTime(oneData.get("CREATEDATETIME").toString());
                level1Elm.setLastModiOpId(oneData.get("LASTMODIOPID").toString());
                level1Elm.setLastModiOpName(oneData.get("LASTMODIOPNAME").toString());
                level1Elm.setLastModiDateTime(oneData.get("LASTMODIDATETIME").toString());
                level1Elm.setConfirmBy(oneData.get("CONFIRMBY").toString());
                level1Elm.setConfirmByName(oneData.get("CONFIRMBYNAME").toString());
                level1Elm.setConfirmDateTime(oneData.get("CONFIRMDATETIME").toString());
                level1Elm.setCancelBy(oneData.get("CANCELBY").toString());
                level1Elm.setCancelByName(oneData.get("CANCELBYNAME").toString());
                level1Elm.setCancelDateTime(oneData.get("CANCELDATETIME").toString());
                level1Elm.setTotCqty(oneData.get("TOTCQTY").toString());
                level1Elm.setTotPqty(oneData.get("TOTPQTY").toString());
                level1Elm.setTotOqty(oneData.get("TOTOQTY").toString());
                level1Elm.setTotAmt(oneData.get("TOTAMT").toString());
                level1Elm.setTotDistriAmt(oneData.get("TOTDISTRIAMT").toString());
                res.getDatas().add(level1Elm);

            }


        }
        else
        {
            totalRecords = 0;
            totalPages = 0;
        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO 自动生成的方法存根

    }

    @Override
    protected String getQuerySql(DCP_DistriOrderQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String langType = req.getLangType();
        DCP_DistriOrderQueryReq.LevelElm request = req.getRequest();

        //計算起啟位置
        int pageSize=req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        sqlbuf.append(" select * from (");
        sqlbuf.append(" select count(*) over () num,rownum rn,a.* from (");
        sqlbuf.append(" select a.status,a.billno,a.bdate ,a.rdate ,b.organizationno as demandOrgNo,b.org_name as demandOrgName,a.memo," +
                " a.EMPLOYEEID,em0.name as employeeName,a.DEPARTID,dd0.DEPARTNAME,em1.opno AS createOpId,em1.op_name as createOpName,dd1.DEPARTNO as createDeptId,dd1.DEPARTNAME as creatDeptName,a.CREATETIME as createDateTime," +
                "  a.lastModiOpId,em2.op_name as lastModiOpName,a.LASTMODITIME as lastModiDateTime,a.confirmBy,em3.op_name as confirmByName,a.cancelBy,em4.op_name as cancelByName,a.CONFIRMTIME as confirmDateTime,a.CANCELTIME as cancelDateTime, " +
                " a.totcqty,a.totpqty,a.totoqty,a.totamt,a.totdistriamt  " +
                " from DCP_DITRIORDER a " +
                " left join dcp_org_lang b on a.DEMANDORGNO=b.organizationno and a.eid=b.eid and b.lang_type='"+langType+"' " +
                " left join dcp_employee em0 on a.eid=em0.eid and a.EMPLOYEEID=em0.employeeno " +
                " left join DCP_DEPARTMENT_LANG dd0 on dd0.eid=a.eid and a.DEPARTID=dd0.DEPARTNO and dd0.lang_type='"+langType+"' " +
                " left join platform_staffs_lang em1 on a.eid=em1.eid and a.CREATEOPID=em1.opno and em1.lang_type='"+req.getOpNO()+"' " +
                " left join DCP_DEPARTMENT_LANG dd1 on dd1.eid=a.eid and a.CREATEDEPTID=dd1.DEPARTNO and dd1.lang_type='"+langType+"' " +
                " left join platform_staffs_lang em2 on a.eid=em2.eid and a.LASTMODIOPID=em2.opno and em2.lang_type='"+langType+"' " +
                " left join platform_staffs_lang em3 on a.eid=em3.eid and a.CONFIRMBY=em3.opno and em3.lang_type='"+langType+"' " +
                " left join platform_staffs_lang em4 on a.eid=em4.eid and a.CANCELBY=em4.opno and em4.lang_type='"+langType+"' " +
                " where a.eid='"+eId+"'  and a.organizationno='"+req.getOrganizationNO()+"' " +
                "");

        if(!Check.Null(request.getStatus())){
            sqlbuf.append(" and a.status='"+request.getStatus()+"' ");
        }
        if(!Check.Null(request.getBeginDate())){
            sqlbuf.append(" and a.bdate>='"+request.getBeginDate()+"' ");
        }
        if(!Check.Null(request.getEndDate())){
            sqlbuf.append(" and a.bdate<='"+request.getEndDate()+"' ");

        }
        if(!Check.Null(request.getKeytxt())){
            sqlbuf.append(" and a.billno like '%%"+request.getKeytxt()+"%%' ");
        }

        sqlbuf.append(" Order by a.bdate desc, a.billNo desc  "
                + " "
                + " ) a"
                + " ) where  rn>"+startRow+" and rn<=" + (startRow+pageSize) + "  "
                + " ");

        return sqlbuf.toString();
    }

    private List<String> getValidEmployees(DCP_DistriOrderQueryReq req,String modularNo,String range) throws Exception{

        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        //PLATFORM_STAFFS_ROLE
        String opNO = req.getOpNO();
        MyCommon cm=new MyCommon();

        String sqlPsr="select * from PLATFORM_STAFFS_ROLE where eid='"+req.geteId()+"' and OPNO='"+req.getOpNO()+"' and status='100' ";
        List<Map<String, Object>> getPsrData=this.doQueryData(sqlPsr, null);
        if(getPsrData.size()==0){
            return new ArrayList<>();
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
            return new ArrayList<>();
        }
        //0-全部 1-个人 2-所属部门 3-所属部门及下级部门 4-同级部门
        List<Map<String, Object>> powerrange = getPbData.stream().filter(x -> x.get(range).toString().equals("0")).collect(Collectors.toList());
        if(powerrange.size()>0){
            //全部权限
            String employeeSql=String.format("select * from dcp_employee where eid='%s'",req.geteId());
            List<Map<String, Object>> getEmployeeData=this.doQueryData(employeeSql, null);
            return getEmployeeData.stream().map(x -> x.get("EMPLOYEENO").toString()).collect(Collectors.toList());

        }
        //获取人员 排除全部了
        List<String> opENos=new ArrayList();

        //获取同部门人员
        String deSql="select * from dcp_department where eid='"+req.geteId()+"' and organizationno='"+req.getOrganizationNO()+"' " +
                " and status='100' ";
        List<Map<String, Object>> getAllDeData=this.doQueryData(deSql, null);

        //查找上级部门
        String upDepartNo=req.getUpDepartNo();

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
                sJoinDepartNo.append(departNo+",");
            }
            Map<String, String> mapDepartNo=new HashMap<String, String>();
            mapDepartNo.put("DEPARTNO", sJoinDepartNo.toString());

            String withasSql_departno="";
            withasSql_departno=cm.getFormatSourceMultiColWith(mapDepartNo);
            mapDepartNo=null;

            if (!withasSql_departno.equals("")) {
                return  opENos;
            }

            String sqlEmployee="with p AS ( " + withasSql_departno + ") " +
                    "select a.* from dcp_employee a " +
                    "inner join p on p.departno=a.DEPARTMENTNO " +
                    "where a.eid='"+req.geteId()+"' and a.status='100' ";
            List<Map<String, Object>> getEmployeeData=this.doQueryData(sqlEmployee, null);
            for (Map<String, Object> map:getEmployeeData){
                String employeeNo1=map.get("EMPLOYEENO").toString();
                if(!opENos.contains(employeeNo1)){
                    opENos.add(employeeNo1);
                }
            }



        }


        return opENos;
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

