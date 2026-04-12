package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_RestrictGroupQueryReq;
import com.dsc.spos.json.cust.res.DCP_RestrictGroupQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_RestrictGroupQuery extends SPosBasicService<DCP_RestrictGroupQueryReq, DCP_RestrictGroupQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_RestrictGroupQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_RestrictGroupQueryReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(request.getGroupType())) {
            errMsg.append("控制组类型不能为空 ");
            isFail = true;
        }
        if (Check.Null(request.getStatus())) {
            //errMsg.append("状态码不能为空 ");
            //isFail = true;
        }

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_RestrictGroupQueryReq> getRequestType() {
        return new TypeToken<DCP_RestrictGroupQueryReq>(){};
    }

    @Override
    protected DCP_RestrictGroupQueryRes getResponseType() {
        return new DCP_RestrictGroupQueryRes();
    }

    @Override
    protected DCP_RestrictGroupQueryRes processJson(DCP_RestrictGroupQueryReq req) throws Exception {
        DCP_RestrictGroupQueryRes res = this.getResponseType();
        res.setDatas(new ArrayList<DCP_RestrictGroupQueryRes.level1Elm>());

        int totalRecords = 0; // 总笔数
        int totalPages = 0;
        //try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> datas = this.doQueryData(sql, null);
            if(!CollectionUtils.isEmpty(datas)){
                SimpleDateFormat df = new SimpleDateFormat("HH:mm");

                String num = datas.get(0).getOrDefault("NUM", "0").toString();
                totalRecords = Integer.parseInt(num);
                // 算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                StringBuffer sJoineid=new StringBuffer("");
                for (Map<String, Object> data : datas) {
                    sJoineid.append("'"+data.get("GROUPNO").toString()+"',");
                }
                String groupNos = sJoineid.toString();
                if(groupNos.length()>0){
                    groupNos=groupNos.substring(0,groupNos.length()-1);
                }
                String eid = req.geteId();
                String langType = req.getLangType();
                String groupType = req.getRequest().getGroupType();
                String langSql=this.getLangSql(eid,groupType,groupNos);
                List<Map<String, Object>> datas_lang = this.doQueryData(langSql, null);

                String deptSql = this.getDeptSql(eid, groupType, groupNos, langType);
                List<Map<String, Object>> datas_dept = this.doQueryData(deptSql, null);

                String empSql = this.getEmpSql(eid, groupType, groupNos);
                List<Map<String, Object>> datas_emp = this.doQueryData(empSql, null);

                String bizPartnerSql = this.getBizPartnerSql(eid, groupType, groupNos);
                List<Map<String, Object>> datas_bizPartner = this.doQueryData(bizPartnerSql, null);

                String orgSql = this.getOrgSql(eid, groupType, groupNos, langType);
                List<Map<String, Object>> datas_org = this.doQueryData(orgSql, null);

                String warehouseSql = this.getWarehouseSql(eid, groupType, groupNos, langType);
                List<Map<String, Object>> datas_warehouse = this.doQueryData(warehouseSql, null);


                for (Map<String, Object> data : datas) {

                    String groupno = data.get("GROUPNO").toString();
                    String groupname = data.get("GROUPNAME").toString();
                    String status = data.get("STATUS").toString();
                    String creatorid = data.get("CREATEOPID").toString();
                    String creatorname = data.get("CREATEOPNAME").toString();
                    String creatordeptid = data.get("CREATEDEPTID").toString();
                    String creatordeptname = data.get("CREATEDEPTNAME").toString();

                    String create_datetime = data.get("CREATETIME").toString();
                    String lastmodifyid = data.get("LASTMODIOPID").toString();
                    String lastmodifyname = data.get("LASTMODIOPNAME").toString();
                    String lastmodify_datetime = data.get("LASTMODITIME").toString();

                    String grouptype = data.get("GROUPTYPE").toString();
                    String memo = data.get("MEMO").toString();

                    DCP_RestrictGroupQueryRes.level1Elm lv1 = res.new level1Elm();
                    lv1.setGroupNo(groupno);
                    lv1.setGroupName(groupname);
                    lv1.setStatus(status);
                    lv1.setCreatorID(creatorid);
                    lv1.setCreatorName(creatorname);
                    lv1.setCreatorDeptID(creatordeptid);
                    lv1.setCreate_datetime(create_datetime);
                    lv1.setCreatorDeptName(creatordeptname);
                    lv1.setLastmodifyID(lastmodifyid);
                    lv1.setLastmodifyName(lastmodifyname);
                    lv1.setLastmodify_datetime(lastmodify_datetime);
                    lv1.setGroupType(grouptype);
                    lv1.setMemo(memo);


                    lv1.setLang_list(new ArrayList<>());
                    for (Map lang:datas_lang) {
                        String groupno1 = lang.get("GROUPNO").toString();
                        if(groupno1.equals(groupno)){
                            DCP_RestrictGroupQueryRes.Lang_list singleLang = res.new Lang_list();
                            singleLang.setLang_type(lang.get("LANG_TYPE").toString());
                            singleLang.setDesc(lang.get("NAME").toString());
                            lv1.getLang_list().add(singleLang);
                        }
                    }

                    lv1.setDeptList(new ArrayList<>());
                    for (Map dept:datas_dept) {
                        String groupno2 = dept.get("GROUPNO").toString();
                        if(groupno2.equals(groupno)){
                            DCP_RestrictGroupQueryRes.DeptList singleDept = res.new DeptList();
                            singleDept.setDeptNo(dept.get("DEPARTMENTNO").toString());
                            singleDept.setDeptName(dept.get("DEPARTNAME").toString());
                            singleDept.setStatus(dept.get("STATUS").toString());
                            lv1.getDeptList().add(singleDept);
                        }
                    }

                    lv1.setEmpList(new ArrayList<>());
                    for (Map emp:datas_emp) {
                        String groupno3 = emp.get("GROUPNO").toString();
                        if(groupno3.equals(groupno)){
                            DCP_RestrictGroupQueryRes.EmpList singleEmp = res.new EmpList();
                            singleEmp.setEmpNo(emp.get("EMPLOYEENO").toString());
                            singleEmp.setEmpName(emp.get("NAME").toString());
                            singleEmp.setStatus(emp.get("STATUS").toString());
                            lv1.getEmpList().add(singleEmp);
                        }
                    }

                    int effectiveBizCount=0;
                    lv1.setBizpartnerList(new ArrayList<>());
                    for (Map bizpartner:datas_bizPartner) {
                        String groupno4 = bizpartner.get("GROUPNO").toString();
                        if(groupno4.equals(groupno)){
                            DCP_RestrictGroupQueryRes.BizpartnerList singleBizpartner = res.new BizpartnerList();
                            singleBizpartner.setBizpartnerNo(bizpartner.get("BIZPARTNERNO").toString());
                            singleBizpartner.setBizpartnerName(bizpartner.get("SNAME").toString());
                            singleBizpartner.setStatus(bizpartner.get("STATUS").toString());
                            lv1.getBizpartnerList().add(singleBizpartner);
                            if("100".equals(singleBizpartner.getStatus())){
                                effectiveBizCount++;
                            }
                        }
                    }
                    lv1.setTotBizCqty(String.valueOf(effectiveBizCount));

                    lv1.setOrg_list(new ArrayList<>());
                    for (Map org:datas_org) {
                        String groupno5 = org.get("GROUPNO").toString();
                        if(groupno5.equals(groupno)){
                            DCP_RestrictGroupQueryRes.Org_list singleOrg = res.new Org_list();
                            singleOrg.setOrgNo(org.get("ORGANIZATIONNO").toString());
                            singleOrg.setOrgName(org.get("ORG_NAME").toString());
                            singleOrg.setStatus(org.get("STATUS").toString());
                            lv1.getOrg_list().add(singleOrg);
                        }
                    }

                    lv1.setWarehouseList(new ArrayList<>());
                    for (Map warehouse:datas_warehouse) {
                        String groupno5 = warehouse.get("GROUPNO").toString();
                        if(groupno5.equals(groupno)){
                            DCP_RestrictGroupQueryRes.WarehouseList singleWarehouse = res.new WarehouseList();
                            singleWarehouse.setOrganizationNo(warehouse.get("ORGANIZATIONNO").toString());
                            singleWarehouse.setOrganizationName(warehouse.get("ORG_NAME").toString());
                            singleWarehouse.setStatus(warehouse.get("STATUS").toString());
                            singleWarehouse.setWarehouse(warehouse.get("WAREHOUSE").toString());
                            singleWarehouse.setWarehouseName(warehouse.get("WAREHOUSE_NAME").toString());
                            lv1.getWarehouseList().add(singleWarehouse);
                        }
                    }

                    res.getDatas().add(lv1);
                }

                //Map<String, String> mapOrder=new HashMap<String, String>();
                //mapOrder.put("GROUPNO", sJoineid.toString());
                //MyCommon cm=new MyCommon();
                //String withasSql_mono=cm.getFormatSourceMultiColWith(mapOrder);

                //if (withasSql_mono.equals(""))
                //{
                //    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "入参转换成临时表with语句的方法处理失败！");
                //}

            }
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        //} catch (Exception e) {
         //   throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, e.getMessage());
        //}
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_RestrictGroupQueryReq req) throws Exception {
        String sql = "";
        String status = req.getRequest().getStatus();
        String groupType = req.getRequest().getGroupType();
        String keytxt = req.getRequest().getKeyTxt();
        String dept = req.getRequest().getDept();
        String bizpartnerNo = req.getRequest().getBizpartnerNo();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        // 計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" SELECT * FROM ( " +
                " SELECT count(a.GROUPNO) OVER () AS num, DENSE_RANK() OVER (ORDER BY GROUPNO) AS rn , a.GROUPNO,a.groupname,a.STATUS,a.CREATEOPID,a.CREATEDEPTID,a.CREATETIME,a.LASTMODIOPID,a.LASTMODITIME," +
                "  a.CREATEOPNAME, a.LASTMODIOPNAME, a.CREATEDEPTNAME,a.grouptype,a.memo "+
                " from (" +
                " select distinct a.GROUPNO,b.name as groupname,a.STATUS,a.CREATEOPID,a.CREATEDEPTID,a.CREATETIME,a.LASTMODIOPID,a.LASTMODITIME," +
                " e1.NAME AS CREATEOPNAME,e2.NAME AS LASTMODIOPNAME,f.DEPARTNAME as CREATEDEPTNAME,a.grouptype,a.memo "+
                " from DCP_RESTRICTGROUP a" +
                " left join DCP_RESTRICTGROUP_LANG b on a.eid=b.eid and a.grouptype=b.grouptype and a.groupno=b.groupno " +
                " left join DCP_RESTRICTGROUP_DEPT c on a.eid=c.eid and a.grouptype=c.grouptype and a.groupno=c.groupno " +
                " left join DCP_RESTRICTGROUP_BIZPARTNER d on a.eid=d.eid and a.grouptype=d.grouptype and a.groupno=d.groupno " +
                " left join DCP_DEPARTMENT_lang c1 on c.eid=c1.eid and c.DEPARTMENTNO=c1.DEPARTNO and c1.lang_type='"+req.getLangType()+"'" +
                " left join DCP_BIZPARTNER d1 on d.eid=d1.eid and d.BIZPARTNERNO=d1.BIZPARTNERNO  " +
                " left join DCP_EMPLOYEE e1 on e1.eid=a.eid and e1.employeeno=a.CREATEOPID " +
                " left join DCP_EMPLOYEE e2 on e2.eid=a.eid and e2.employeeno=a.LASTMODIOPID " +
                " LEFT JOIN DCP_DEPARTMENT_lang f on a.eid=f.eid and a.CREATEDEPTID =f.DEPARTNO and f.lang_type='"+req.getLangType()+"' " +
                 "       where a.eid = '" + req.geteId() + "'"+
                "");
        if(!Check.Null(status)){
            sqlbuf.append(" AND a.status = '"+status+"'");
        }

        if(!Check.Null(groupType)){
            sqlbuf.append(" AND a.grouptype = '"+groupType+"'");
        }

        if(!Check.Null(keytxt)){
            sqlbuf.append(" AND (a.groupno like  '%%"+keytxt+"%%' or b.name like  '%%"+keytxt+"%%' " +
                    " or d1.sname like  '%%"+keytxt+"%%' " +
                    " or d1.fname like  '%%"+keytxt+"%%' )");
        }
        if(!Check.Null(dept)){
            sqlbuf.append(" AND (c1.DEPARTNO like  '%%"+dept+"%%' or c1.DEPARTNAME like  '%%"+keytxt+"%%' ) ");
        }

        if(!Check.Null(bizpartnerNo)){
            sqlbuf.append(" AND (d1.BIZPARTNERNO like  '%%"+bizpartnerNo+"%%' or d1.sname like  '%%"+bizpartnerNo+"%%' or d1.fname like  '%%"+bizpartnerNo+"%%') ");
        }

        sqlbuf.append(" ORDER BY a.GROUPNO )  a");
        sqlbuf.append(" ) WHERE rn > " + startRow + " AND rn <= " + (startRow + pageSize) + " ");
        sql = sqlbuf.toString();
        return sql;
    }

    private String getLangSql(String eid,String groupType,String groupNos){
        String sql="select * from DCP_RESTRICTGROUP_LANG a where a.eid='"+eid+"' " +
                " and a.groupno in ("+groupNos+") and a.grouptype='"+groupType+"' ";
        return sql;
    }

    private String getDeptSql(String eid,String groupType,String groupNos,String langType){
        String sql="select a.groupno,a.DEPARTMENTNO,b.departname,a.status from DCP_RESTRICTGROUP_DEPT a " +
                " LEFT JOIN DCP_DEPARTMENT_lang b on a.eid=b.eid and a.DEPARTMENTNO =b.DEPARTNO and b.lang_type='"+langType+"' " +
                " where a.eid='"+eid+"' " +
                " and a.groupno in ("+groupNos+") and a.grouptype='"+groupType+"' ";
        return sql;
    }

    private String getBizPartnerSql(String eid,String groupType,String groupNos){
        String sql="select a.groupno,a.BIZPARTNERNO,b.sname,a.status from DCP_RESTRICTGROUP_BIZPARTNER a " +
                " left join DCP_BIZPARTNER b  on a.eid=b.eid and a.BIZPARTNERNO=b.BIZPARTNERNO  where a.eid='"+eid+"' " +
                " and a.groupno in ("+groupNos+") and a.grouptype='"+groupType+"' ";
        return sql;
    }

    private String getEmpSql(String eid,String groupType,String groupNos){
        String sql="select a.groupno,a.EMPLOYEENO,b.name ,a.status from DCP_RESTRICTGROUP_EMP a" +
                " left join dcp_employee b on a.eid=b.eid and a.EMPLOYEENO=b.EMPLOYEENO    where a.eid='"+eid+"' " +
                " and a.groupno in ("+groupNos+") and a.grouptype='"+groupType+"' ";
        return sql;
    }

    private String getOrgSql(String eid,String groupType,String groupNos,String langType){
        String sql="select a.groupno,a.ORGANIZATIONNO,b.org_name,a.status from DCP_RESTRICTGROUP_ORG a " +
                " left join dcp_org_lang b on a.eid=b.eid and a.ORGANIZATIONNO=b.ORGANIZATIONNO and b.lang_type='"+langType+"'" +
                " where a.eid='"+eid+"' " +
                " and a.groupno in ("+groupNos+") and a.grouptype='"+groupType+"' ";
        return sql;
    }

    private String getWarehouseSql(String eid,String groupType,String groupNos,String langType){
        String sql="select a.groupno,a.ORGANIZATIONNO,b.org_name,a.status,a.warehouse,c.warehouse_name " +
                " from DCP_RESTRICTGROUP_WAREHOUSE a " +
                " left join dcp_org_lang b on a.eid=b.eid and a.ORGANIZATIONNO=b.ORGANIZATIONNO and b.lang_type='"+langType+"'" +
                " left join dcp_warehouse_lang c on a.eid=c.eid and a.warehouse=c.warehouse  and c.lang_type='"+langType+"'" +

                " where a.eid='"+eid+"' " +
                " and a.groupno in ("+groupNos+") and a.grouptype='"+groupType+"' ";
        return sql;
    }

}
