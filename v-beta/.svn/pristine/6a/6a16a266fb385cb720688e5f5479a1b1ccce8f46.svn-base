package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CostAllocCreateReq;
import com.dsc.spos.json.cust.res.DCP_CostAllocCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_CostAllocCreate extends SPosAdvanceService<DCP_CostAllocCreateReq, DCP_CostAllocCreateRes> {
    @Override
    protected void processDUID(DCP_CostAllocCreateReq req, DCP_CostAllocCreateRes res) throws Exception {

        String lastModiTime = DateFormatUtils.getNowDateTime();

        String accSql = "select COSTCURRENTYEAR,COSTCURRENTMTH from DCP_ACOUNT_SETTING a where a.eid='" + req.geteId() + "' and a.ACCOUNTID='" + req.getRequest().getAccountID() + "' ";

        List<Map<String, Object>> accList = this.doQueryData(accSql, null);
        if (CollectionUtils.isEmpty(accList)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400,"帐套不存在或未启用！");
        }

        String currentYear = accList.get(0).get("COSTCURRENTYEAR").toString();
        String currentMth = accList.get(0).get("COSTCURRENTMTH").toString();
        if (currentMth.length()<2){
            currentMth = "0" + currentMth;
        }
        String currentYearMth = currentYear + currentMth;
        String year = String.valueOf(req.getRequest().getYear());
        String period = String.valueOf(req.getRequest().getPeriod());
        if (period.length()<2){
            period = "0" + period;
        }
        String nowYearMonth = year + period;
        if (nowYearMonth.compareTo(currentYearMth) < 0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400,"年度/期别不得小于现行成本年月!");
        }

        List<DCP_CostAllocCreateReq.AllocList> allocList = req.getRequest().getAllocList();

        List<Map<String, Object>> department = this.doQueryData(String.format("SELECT DEPARTNAME FROM DCP_DEPARTMENT_LANG WHERE EID='%s' and DEPARTNO='%s' and LANG_TYPE='%s'  ", req.geteId(), req.getRequest().getCostCenter(), req.getLangType()), null);

        for (DCP_CostAllocCreateReq.AllocList alloc : allocList) {
            ColumnDataValue dcp_costalloc = new ColumnDataValue();

            dcp_costalloc.add("EID", DataValues.newString(req.geteId()));
            dcp_costalloc.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
            dcp_costalloc.add("ALLOCTYPE", DataValues.newString(req.getRequest().getAllocType()));
            dcp_costalloc.add("YEAR", DataValues.newInteger(req.getRequest().getYear()));
            dcp_costalloc.add("PERIOD", DataValues.newInteger(req.getRequest().getPeriod()));
            dcp_costalloc.add("STATUS", DataValues.newInteger(req.getRequest().getStatus()));
            dcp_costalloc.add("ACCOUNT", DataValues.newString(req.getRequest().getAccount()));
            dcp_costalloc.add("COSTCENTERNO", DataValues.newString(req.getRequest().getCostCenter()));
            dcp_costalloc.add("COSTCENTER", DataValues.newString(department.get(0).get("DEPARTNAME")));


            dcp_costalloc.add("DEPTNO", DataValues.newString(alloc.getDeptNo()));
            dcp_costalloc.add("DEPT", DataValues.newString(alloc.getDept()));
            dcp_costalloc.add("ITEM", DataValues.newString(alloc.getItem()));

            dcp_costalloc.add("ORGANIZATIONNO", DataValues.newString(alloc.getOrganizationID()));
            dcp_costalloc.add("ORG_NAME", DataValues.newString(alloc.getOrg_Name()));
            dcp_costalloc.add("AMT", DataValues.newString(alloc.getAmt()));

            dcp_costalloc.add("ALLOCSOURCE", DataValues.newDecimal(alloc.getAllocSource()));
            dcp_costalloc.add("ALLOCFORMULA", DataValues.newDecimal(alloc.getAllocFormula()));
            dcp_costalloc.add("ALLOCWEIGHT", DataValues.newDecimal(alloc.getAllocWeight()));
            dcp_costalloc.add("CREATETIME", DataValues.newDate(lastModiTime));
            dcp_costalloc.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_costalloc.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("dcp_costalloc", dcp_costalloc)));

        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CostAllocCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CostAllocCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CostAllocCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CostAllocCreateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostAllocCreateReq> getRequestType() {
        return new TypeToken<DCP_CostAllocCreateReq>() {
        };
    }

    @Override
    protected DCP_CostAllocCreateRes getResponseType() {
        return new DCP_CostAllocCreateRes();
    }
}
