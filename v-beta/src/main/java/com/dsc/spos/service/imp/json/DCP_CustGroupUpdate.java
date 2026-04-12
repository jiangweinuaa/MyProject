package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CustGroupUpdateReq;
import com.dsc.spos.json.cust.res.DCP_CustGroupUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_CustGroupUpdate extends SPosAdvanceService<DCP_CustGroupUpdateReq, DCP_CustGroupUpdateRes> {
    @Override
    protected void processDUID(DCP_CustGroupUpdateReq req, DCP_CustGroupUpdateRes res) throws Exception {
        //同一标签编码或同一客户编号不可重复出现

        String lastModiTime = DateFormatUtils.getNowDateTime();

        ColumnDataValue dcp_custGroup = new ColumnDataValue();
        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("CUSTGROUPNO", DataValues.newString(req.getRequest().getCustGroupNo()));

        dcp_custGroup.add("CUSTGROUPNAME", DataValues.newString(req.getRequest().getCustGroupName()));
        dcp_custGroup.add("STATUS", DataValues.newInteger(req.getRequest().getStatus()));
        dcp_custGroup.add("MEMO", DataValues.newString(req.getRequest().getMemo()));
        dcp_custGroup.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
        dcp_custGroup.add("LASTMODITIME", DataValues.newDate(lastModiTime));

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_CUSTGROUP", condition, dcp_custGroup)));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_CUSTGROUP_DETAIL", condition)));

        for (DCP_CustGroupUpdateReq.GroupList groupList : req.getRequest().getGroupList()) {
            ColumnDataValue dcp_custGroup_detail = new ColumnDataValue();

            dcp_custGroup_detail.add("EID", DataValues.newString(req.geteId()));
            dcp_custGroup_detail.add("CUSTGROUPNO", DataValues.newString(req.getRequest().getCustGroupNo()));
            dcp_custGroup_detail.add("ATTRTYPE", DataValues.newString(groupList.getAttrType()));
            dcp_custGroup_detail.add("ATTRID", DataValues.newString(groupList.getAttrId()));
            dcp_custGroup_detail.add("STATUS", DataValues.newString(groupList.getStatus()));

            dcp_custGroup_detail.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_custGroup_detail.add("CREATETIME", DataValues.newDate(lastModiTime));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_CUSTGROUP_DETAIL", dcp_custGroup_detail)));
        }
        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CustGroupUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CustGroupUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CustGroupUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CustGroupUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CustGroupUpdateReq> getRequestType() {
        return new TypeToken<DCP_CustGroupUpdateReq>(){};
    }

    @Override
    protected DCP_CustGroupUpdateRes getResponseType() {
        return new DCP_CustGroupUpdateRes();
    }
}
