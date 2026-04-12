package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PickGroupUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PickGroupUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_PickGroupUpdate extends SPosAdvanceService<DCP_PickGroupUpdateReq, DCP_PickGroupUpdateRes> {
    @Override
    protected void processDUID(DCP_PickGroupUpdateReq req, DCP_PickGroupUpdateRes res) throws Exception {


        String querySql = " SELECT * FROM DCP_PICKGROUP WHERE EID='%s' and ORGANIZATIONNO='%s' and PICKGROUPNO='%s' ";
        List<Map<String, Object>> qData = doQueryData(String.format(querySql,req.geteId(),req.getOrganizationNO(),req.getRequest().getPickGroupNo()),null);
        if (qData == null || qData.isEmpty()) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400,req.getOrganizationNO() + "不存在分区编码"+req.getRequest().getPickGroupNo());
        }

        ColumnDataValue dcp_pickGroup = new ColumnDataValue();
        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
        condition.add("PICKGROUPNO", DataValues.newString(req.getRequest().getPickGroupNo()));

        dcp_pickGroup.add("PICKGROUPNAME", DataValues.newString(req.getRequest().getPickGroupName()));
        dcp_pickGroup.add("WAREHOUSE", DataValues.newString(req.getRequest().getWarehouse()));
        dcp_pickGroup.add("WAREREGIONNO", DataValues.newString(req.getRequest().getWareRegionNo()));
        dcp_pickGroup.add("PICKTYPE", DataValues.newString(req.getRequest().getPickType()));
        dcp_pickGroup.add("PICKRANGETYPE", DataValues.newString(req.getRequest().getRangeType()));
        dcp_pickGroup.add("OBJECTRANGE", DataValues.newString(req.getRequest().getObjectRange()));
        dcp_pickGroup.add("STATUS", DataValues.newString(req.getRequest().getStatus()));

        dcp_pickGroup.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
        dcp_pickGroup.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PICKGROUP",condition, dcp_pickGroup)));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_PICKGROUP_RANGE",condition )));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_PICKGROUP_OBJECT",condition)));


        for (DCP_PickGroupUpdateReq.RangeList rangeList: req.getRequest().getRangeList()){
            ColumnDataValue dcp_pickGroupRange = new ColumnDataValue();

            dcp_pickGroupRange.add("EID",DataValues.newString(req.geteId()));
            dcp_pickGroupRange.add("ORGANIZATIONNO",DataValues.newString(req.getOrganizationNO()));
            dcp_pickGroupRange.add("PICKGROUPNO",DataValues.newString(req.getRequest().getPickGroupNo()));
            dcp_pickGroupRange.add("TYPE",DataValues.newString(rangeList.getType()));
            dcp_pickGroupRange.add("CODE",DataValues.newString(rangeList.getCode()));

            dcp_pickGroupRange.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_pickGroupRange.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_PICKGROUP_RANGE", dcp_pickGroupRange)));

        }

        if (null != req.getRequest().getObjectList()){
            for (DCP_PickGroupUpdateReq.ObjectList objectList : req.getRequest().getObjectList()) {

                ColumnDataValue dcp_pickGroup_object = new ColumnDataValue();

                dcp_pickGroup_object.add("EID",DataValues.newString(req.geteId()));
                dcp_pickGroup_object.add("ORGANIZATIONNO",DataValues.newString(req.getOrganizationNO()));
                dcp_pickGroup_object.add("PICKGROUPNO",DataValues.newString(req.getRequest().getPickGroupNo()));
                dcp_pickGroup_object.add("TYPE",DataValues.newString(objectList.getType()));
                dcp_pickGroup_object.add("CODE",DataValues.newString(objectList.getCode()));

                dcp_pickGroup_object.add("SORTID",DataValues.newString(objectList.getSortId()));
                dcp_pickGroup_object.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
                dcp_pickGroup_object.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_PICKGROUP_OBJECT",dcp_pickGroup_object)));

            }
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PickGroupUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PickGroupUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PickGroupUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_PickGroupUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PickGroupUpdateReq> getRequestType() {
        return new TypeToken<DCP_PickGroupUpdateReq>(){};
    }

    @Override
    protected DCP_PickGroupUpdateRes getResponseType() {
        return new DCP_PickGroupUpdateRes();
    }
}
