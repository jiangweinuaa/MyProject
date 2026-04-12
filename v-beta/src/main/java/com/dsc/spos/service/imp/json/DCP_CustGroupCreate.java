package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CustGroupCreateReq;
import com.dsc.spos.json.cust.res.DCP_CustGroupCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_CustGroupCreate extends SPosAdvanceService<DCP_CustGroupCreateReq, DCP_CustGroupCreateRes> {

    @Override
    protected void processDUID(DCP_CustGroupCreateReq req, DCP_CustGroupCreateRes res) throws Exception {
        //同一标签编码或同一客户编号不可重复出现

        String lastModiTime = DateFormatUtils.getNowDateTime();
        String orderNo = getProcessTaskNO(req.geteId(), req.getShopId(), "KHZ");
        ColumnDataValue dcp_custGroup = new ColumnDataValue();

        dcp_custGroup.add("EID", DataValues.newString(req.geteId()));
        dcp_custGroup.add("CUSTGROUPNO", DataValues.newString(orderNo));
        dcp_custGroup.add("CUSTGROUPNAME", DataValues.newString(req.getRequest().getCustGroupName()));
        dcp_custGroup.add("STATUS", DataValues.newInteger(req.getRequest().getStatus()));
        dcp_custGroup.add("MEMO", DataValues.newString(req.getRequest().getMemo()));
        dcp_custGroup.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
        dcp_custGroup.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
        dcp_custGroup.add("CREATETIME", DataValues.newDate(lastModiTime));
        dcp_custGroup.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
        dcp_custGroup.add("LASTMODITIME", DataValues.newDate(lastModiTime));

        for (DCP_CustGroupCreateReq.GroupList groupList : req.getRequest().getGroupList()) {
            ColumnDataValue dcp_custGroup_detail = new ColumnDataValue();

            dcp_custGroup_detail.add("EID", DataValues.newString(req.geteId()));
            dcp_custGroup_detail.add("CUSTGROUPNO", DataValues.newString(orderNo));
            dcp_custGroup_detail.add("ATTRTYPE", DataValues.newString(groupList.getAttrType()));
            dcp_custGroup_detail.add("ATTRID", DataValues.newString(groupList.getAttrId()));
            dcp_custGroup_detail.add("STATUS", DataValues.newString(groupList.getStatus()));

            dcp_custGroup_detail.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_custGroup_detail.add("CREATETIME", DataValues.newDate(lastModiTime));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_CUSTGROUP_DETAIL", dcp_custGroup_detail)));
        }

        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_CUSTGROUP", dcp_custGroup)));
        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        res.setCustGroupNo(orderNo);

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CustGroupCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CustGroupCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CustGroupCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CustGroupCreateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CustGroupCreateReq> getRequestType() {
        return new TypeToken<DCP_CustGroupCreateReq>(){

        };
    }

    @Override
    protected DCP_CustGroupCreateRes getResponseType() {
        return new DCP_CustGroupCreateRes();
    }
}
