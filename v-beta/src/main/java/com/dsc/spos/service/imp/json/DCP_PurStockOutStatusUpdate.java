package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PurStockOutStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PurStockOutStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_PurStockOutStatusUpdate  extends SPosAdvanceService<DCP_PurStockOutStatusUpdateReq, DCP_PurStockOutStatusUpdateRes> {

    private static final String TYPE_POST = "post";
    private static final String TYPE_UNPOST = "unpost";
    private static final String TYPE_CANCEL = "cancel";

    @Override
    protected boolean isVerifyFail(DCP_PurStockOutStatusUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        DCP_PurStockOutStatusUpdateReq.Request request = req.getRequest();

        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_PurStockOutStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_PurStockOutStatusUpdateReq>(){};
    }

    @Override
    protected DCP_PurStockOutStatusUpdateRes getResponseType() {
        return new DCP_PurStockOutStatusUpdateRes();
    }

    @Override
    public void processDUID(DCP_PurStockOutStatusUpdateReq req,DCP_PurStockOutStatusUpdateRes res) throws Exception {
        //枚举: cancel-作废 post-过账 unpost-取消过账
        String pStockOutNo = req.getRequest().getPStockOutNo();
        String opType = req.getRequest().getOpType();
        String eid = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        String companyId = req.getBELFIRM();
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


        String querySql = this.getQuerySql(req);
        List<Map<String, Object>> mainData = dao.executeQuerySQL(querySql, null);

        if(mainData.size()==0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据不存在");
        }
        String status = mainData.get(0).get("STATUS").toString();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String bdate = new SimpleDateFormat("yyyyMMdd").format(formatter.parse(mainData.get(0).get("BDATE").toString()));

        if(TYPE_CANCEL.equals(opType)){
            if(!"0".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非【新建】不可作废！");
            }
            UptBean ub2 = new UptBean("DCP_DITRIORDER");
            ub2.addUpdateValue("STATUS", DataValues.newString("2"));//2-已作废
            ub2.addUpdateValue("LASTMODIOPID", DataValues.newString(employeeNo));
            ub2.addUpdateValue("LASTMODITIME",DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("CANCELBY", DataValues.newString(employeeNo));
            ub2.addUpdateValue("CANCELTIME", DataValues.newDate(lastmoditime));


            ub2.addCondition("EID", DataValues.newString(eid));
            ub2.addCondition("BILLNO",DataValues.newString(pStockOutNo));
            this.addProcessData(new DataProcessBean(ub2));
        }

        if(TYPE_POST.equals(opType)){
            if(!"1".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非【审核】不可取消审核！");
            }



            UptBean ub2 = new UptBean("DCP_DITRIORDER");
            ub2.addUpdateValue("STATUS", DataValues.newString("0"));//0-新建
            ub2.addUpdateValue("LASTMODIOPID", DataValues.newString(employeeNo));
            ub2.addUpdateValue("LASTMODITIME",DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("CONFIRMBY", DataValues.newString(employeeNo));
            ub2.addUpdateValue("CONFIRMTIME", DataValues.newDate(null));

            ub2.addCondition("EID", DataValues.newString(eid));
            ub2.addCondition("ORGANIZATIONNO",DataValues.newString(organizationNO));
            ub2.addCondition("BILLNO",DataValues.newString(pStockOutNo));
            this.addProcessData(new DataProcessBean(ub2));
        }

        if(TYPE_UNPOST.equals(opType)){

            Calendar cal = Calendar.getInstance();//获得当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String bDate = df.format(cal.getTime());
            String createDate = df.format(cal.getTime());
            df = new SimpleDateFormat("HHmmss");
            String createTime = df.format(cal.getTime());

            if(!"0".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非【新建】不可审核！");
            }

            String employeeid = mainData.get(0).get("EMPLOYEEID").toString();
            String departid = mainData.get(0).get("DEPARTID").toString();
            String ownopid = mainData.get(0).get("OWNOPID").toString();
            String owndeptid = mainData.get(0).get("OWNDEPTID").toString();
            String createdeptid = mainData.get(0).get("CREATEDEPTID").toString();



            UptBean ub2 = new UptBean("DCP_DITRIORDER");
            ub2.addUpdateValue("STATUS", DataValues.newString("1"));
            ub2.addUpdateValue("LASTMODIOPID", DataValues.newString(employeeNo));
            ub2.addUpdateValue("LASTMODITIME",DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("CONFIRMBY", DataValues.newString(employeeNo));
            ub2.addUpdateValue("CONFIRMTIME", DataValues.newDate(lastmoditime));

            ub2.addCondition("EID", DataValues.newString(eid));
            ub2.addCondition("ORGANIZATIONNO",DataValues.newString(organizationNO));
            ub2.addCondition("BILLNO",DataValues.newString(pStockOutNo));
            this.addProcessData(new DataProcessBean(ub2));
        }

        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurStockOutStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurStockOutStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurStockOutStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected String getQuerySql(DCP_PurStockOutStatusUpdateReq req) throws Exception {
        StringBuilder sb=new StringBuilder();
        sb.append("select * from DCP_DITRIORDER a where a.eid='"+req.geteId()+"' " +
                " and a.ORGANIZATIONNO='"+req.getOrganizationNO()+"' " +
                " and a.BILLNO='"+req.getRequest().getPStockOutNo()+"' ");

        return sb.toString();
    }


}
