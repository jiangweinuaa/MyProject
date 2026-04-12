package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CustGroupDeleteReq;
import com.dsc.spos.json.cust.res.DCP_CustGroupDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_CustGroupDelete extends SPosAdvanceService<DCP_CustGroupDeleteReq, DCP_CustGroupDeleteRes> {
    @Override
    protected void processDUID(DCP_CustGroupDeleteReq req, DCP_CustGroupDeleteRes res) throws Exception {


        List<Map<String, Object>> data = doQueryData(String.format("SELECT STATUS FROM DCP_CUSTGROUP WHERE EID='%s' and CUSTGROUPNO='%s' ", req.geteId(), req.getRequest().getCustGroupNo()), null);

        if (!StringUtils.equals("-1", data.get(0).get("STATUS").toString())) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "客户组状态非“-1.未启用”不可删除！");
        }

        ColumnDataValue condition = new ColumnDataValue();
        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("CUSTGROUPNO", DataValues.newString(req.getRequest().getCustGroupNo()));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_CUSTGROUP", condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_CUSTGROUP_DETAIL", condition)));

        condition = new ColumnDataValue();
        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("CUSTOMERNO", DataValues.newString(req.getRequest().getCustGroupNo()));
        condition.add("CUSTOMERTYPE", DataValues.newString("1"));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("dcp_customer_cate_disc", condition)));


        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CustGroupDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CustGroupDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CustGroupDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CustGroupDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CustGroupDeleteReq> getRequestType() {
        return new TypeToken<DCP_CustGroupDeleteReq>() {
        };
    }

    @Override
    protected DCP_CustGroupDeleteRes getResponseType() {
        return new DCP_CustGroupDeleteRes();
    }
}
