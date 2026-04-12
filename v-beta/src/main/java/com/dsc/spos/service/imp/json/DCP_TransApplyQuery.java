package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_TransApplyQueryReq;
import com.dsc.spos.json.cust.res.DCP_TransApplyQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_TransApplyQuery extends SPosBasicService<DCP_TransApplyQueryReq, DCP_TransApplyQueryRes> {


    @Override
    protected boolean isVerifyFail(DCP_TransApplyQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    protected TypeToken<DCP_TransApplyQueryReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_TransApplyQueryReq>(){};
    }

    @Override
    protected DCP_TransApplyQueryRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_TransApplyQueryRes();
    }

    @Override
    protected DCP_TransApplyQueryRes processJson(DCP_TransApplyQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        String sql=null;
        DCP_TransApplyQueryRes res = null;
        res = this.getResponse();
        int totalRecords;								//总笔数
        int totalPages;

        String langType = req.getLangType();

        sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());
        if (getQData != null && getQData.isEmpty() == false)
        {
            Map<String, Object> oneData_Count = getQData.get(0);
            String num = oneData_Count.get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> singleRow : getQData){
                DCP_TransApplyQueryRes.level1Elm level1Elm = res.new level1Elm();
                level1Elm.setStatus(singleRow.get("STATUS").toString());
                level1Elm.setBDate(singleRow.get("BDATE").toString());
                level1Elm.setBillNo(singleRow.get("BILLNO").toString());
                level1Elm.setTransType(singleRow.get("TRANSTYPE").toString());
                level1Elm.setTransOutOrgNo(singleRow.get("TRANSOUTORGNO").toString());
                level1Elm.setTransOutOrgName(singleRow.get("TRANSOUTORGNAME").toString());
                level1Elm.setTransOutWarehouse(singleRow.get("TRANSOUTWAREHOUSE").toString());
                level1Elm.setTransOutWarehouseName(singleRow.get("TRANSOUTWAREHOUSENAME").toString());
                level1Elm.setTransInOrgNo(singleRow.get("TRANSINORGNO").toString());
                level1Elm.setTransInOrgName(singleRow.get("TRANSINORGNAME").toString());
                level1Elm.setTransInWarehouse(singleRow.get("TRANSINWAREHOUSE").toString());
                level1Elm.setTransInWarehouseName(singleRow.get("TRANSINWAREHOUSENAME").toString());
                level1Elm.setIsTranInConfirm(singleRow.get("ISTRANINCONFIRM").toString());
                level1Elm.setRDate(singleRow.get("RDATE").toString());
                level1Elm.setPTemplateNo(singleRow.get("PTEMPLATENO").toString());
                level1Elm.setPTemplateName(singleRow.get("PTEMPLATENAME").toString());
                level1Elm.setReason(singleRow.get("REASON").toString());
                level1Elm.setTotCqty(singleRow.get("TOTCQTY").toString());
                level1Elm.setTotPoQty(singleRow.get("TOTPOQTY").toString());
                level1Elm.setTotPqty(singleRow.get("TOTPQTY").toString());
                level1Elm.setTotAmt(singleRow.get("TOTAMT").toString());
                level1Elm.setTotDistriAmt(singleRow.get("TOTDISTRIAMT").toString());
                level1Elm.setEmployeeId(singleRow.get("EMPLOYEEID").toString());
                level1Elm.setEmployeeName(singleRow.get("EMPLOYEENAME").toString());
                level1Elm.setDepartId(singleRow.get("DEPARTID").toString());
                level1Elm.setDepartName(singleRow.get("DEPARTNAME").toString());
                level1Elm.setMemo(singleRow.get("MEMO").toString());
                level1Elm.setCreateBy(singleRow.get("CREATEBY").toString());
                level1Elm.setCreateByName(singleRow.get("CREATEBYNAME").toString());
                level1Elm.setCreateDeptId(singleRow.get("CREATEDEPTID").toString());
                level1Elm.setCreateDeptName(singleRow.get("CREATEDEPTNAME").toString());
                level1Elm.setCreateTime(singleRow.get("CREATETIME").toString());
                level1Elm.setModifyBy(singleRow.get("MODIFYBY").toString());
                level1Elm.setModifyByName(singleRow.get("MODIFYBYNAME").toString());
                level1Elm.setModifyTime(singleRow.get("MODIFYTIME").toString());
                level1Elm.setSubmitBy(singleRow.get("SUBMITBY").toString());
                level1Elm.setSubmitByName(singleRow.get("SUBMITBYNAME").toString());
                level1Elm.setSubmitTime(singleRow.get("SUBMITTIME").toString());
                level1Elm.setConfirmBy(singleRow.get("CONFIRMBY").toString());
                level1Elm.setConfirmByName(singleRow.get("CONFIRMBYNAME").toString());
                level1Elm.setConfirmTime(singleRow.get("CONFIRMTIME").toString());
                level1Elm.setCancelBy(singleRow.get("CANCELBY").toString());
                level1Elm.setCancelByName(singleRow.get("CANCELBYNAME").toString());
                level1Elm.setCancelTime(singleRow.get("CANCELTIME").toString());
                level1Elm.setCloseBy(singleRow.get("CLOSEBY").toString());
                level1Elm.setCloseByName(singleRow.get("CLOSEBYNAME").toString());
                level1Elm.setCloseTime(singleRow.get("CLOSETIME").toString());
                level1Elm.setApplyType(singleRow.get("APPLYTYPE").toString());

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

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

        return res;


    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO 自动生成的方法存根

    }

    @Override
    protected String getQuerySql(DCP_TransApplyQueryReq req) throws Exception {
        String sql=null;
        StringBuffer sqlbuf=new StringBuffer("");
        String eId = req.geteId();
        DCP_TransApplyQueryReq.LevelElm request = req.getRequest();
        String keyTxt = request.getKeyTxt();
        String status = request.getStatus();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String getType = request.getGetType();
        String transType = request.getTransType();
        String applyType = request.getApplyType();

        String langType = req.getLangType();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow=(pageNumber-1) * pageSize;
        sqlbuf.append("SELECT * FROM ( " +
                " SELECT count(DISTINCT a.BILLNO) OVER () AS num , DENSE_RANK() OVER (ORDER BY  a.CREATETIME DESC, a.BILLNO) AS rn, " +
                " a.status,a.bdate,a.billno,a.transType,a.transOutOrgNo,o1.org_name as transOutOrgName, a.transInOrgNo,o2.org_name as transInOrgName,a.transOutWarehouse,w1.warehouse_name as transOutWarehouseName,a.transInWarehouse,w2.warehouse_name as transInWarehousename," +
                " a.isTranInConfirm,a.rDate,b.ptemplateno,b.ptemplate_name as ptemplatename,a.reason,a.totcqty, a.totpoqty,a.totpqty,a.totamt,a.totdistriamt,a.employeeid,e1.name as employeename,a.memo,a.departid,d1.departname,a.createBy,e2.op_name as createByname,a.createDeptId,d2.departname as createDeptName,a.createtime ," +
                " a.modifyBy,e3.op_name as modifyByname,a.modifyTime, a.submitBy,e4.op_name as submitByname,a.submitTime ,a.confirmBy,e5.op_name as confirmByname,a.confirmtime,a.cancelBy,e6.op_name as cancelByname,a.canceltime,a.closeBy,e7.op_name as closeByname,a.closetime,a.applytype " +
                " FROM DCP_TRANSAPPLY  a" +
                " LEFT JOIN DCP_ORG_LANG o1 ON a.EID = o1.EID AND a.TRANSOUTORGNO = o1.ORGANIZATIONNO " +
                " LEFT JOIN DCP_ORG_LANG o2 ON a.EID = o2.EID AND a.TRANSINORGNO = o2.ORGANIZATIONNO " +
                " LEFT JOIN DCP_WAREHOUSE_LANG w1 on a.eid=w1.eid and a.organizationno=w1.organizationno and a.transOutwarehouse=w1.warehouse "+
                " LEFT JOIN DCP_WAREHOUSE_LANG w2 on a.eid=w2.eid and a.organizationno=w2.organizationno and a.transInwarehouse=w2.warehouse "+
                " LEFT JOIN DCP_PTEMPLATE b ON a.EID = b.EID AND a.ptemplateno = b.ptemplateno " +
                " LEFT JOIN DCP_EMPLOYEE e1 ON a.EID  = e1.EID  AND a.employeeid  = e1.employeeno  " +
                " left join DCP_DEPARTMENT_LANG d1 on a.eid=d1.eid and a.departid=d1.departno and d1.lang_type='"+langType+"'"+
                " LEFT JOIN platform_staffs_lang e2 ON a.EID  = e2.EID  AND a.createBy  = e2.opno and e2.lang_type='"+langType+"'  " +
                " left join DCP_DEPARTMENT_LANG d2 on a.eid=d2.eid and a.createDeptId=d2.departno and d2.lang_type='"+langType+"'"+
                " LEFT JOIN platform_staffs_lang e3 ON a.EID  = e3.EID  AND a.modifyBy  = e3.opno and e3.lang_type='"+langType+"' " +
                " LEFT JOIN platform_staffs_lang e4 ON a.EID  = e4.EID  AND a.submitBy  = e4.opno and e4.lang_type='"+langType+"' " +
                " LEFT JOIN platform_staffs_lang e5 ON a.EID  = e5.EID  AND a.confirmBy  = e5.opno and e5.lang_type='"+langType+"' " +
                " LEFT JOIN platform_staffs_lang e6 ON a.EID  = e6.EID  AND a.cancelBy  = e6.opno  and e6.lang_type='"+langType+"' " +
                " LEFT JOIN platform_staffs_lang e7 ON a.EID  = e7.EID  AND a.closeBy  = e7.opno  and e7.lang_type='"+langType+"' " +
                " where a.EID = '"+eId+"'  " +
                " ");
        sqlbuf.append(" and a.bdate>='"+beginDate+"' and a.bdate <='"+endDate+"' ");
        if(!Check.Null(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }
        if(!Check.Null(transType)){
            sqlbuf.append(" and a.transtype='"+transType+"' ");
        }
        if(Check.NotNull(applyType)){
            sqlbuf.append(" and a.applytype='"+applyType+"' ");
        }

        if(Check.Null(getType)||"0".equals(getType)){
            sqlbuf.append(" and a.organizationno='"+ req.getOrganizationNO()+"' ");
        }else if("1".equals(getType)){
            sqlbuf.append(" and a.APPROVEORGNO='"+ req.getOrganizationNO()+"' ");
        }

        if(!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.billno like '%%"+keyTxt+"%%'" +
                    " or a.transoutorgno like '%%"+keyTxt+"%%'" +
                    " or a.transinorgno like '%%"+keyTxt+"%%' ) ");
        }

        sqlbuf.append(" order by a.bdate desc,a.billno desc ");


        sqlbuf.append(" ) WHERE rn > "+startRow+" AND rn <= "+(startRow+pageSize)+"");

        sql=sqlbuf.toString();
        return sql;
    }

}
