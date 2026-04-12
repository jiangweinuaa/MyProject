package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CustomerCategoryDiscUpdateReq;
import com.dsc.spos.json.cust.res.DCP_CustomerCategoryDiscUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_CustomerCategoryDiscUpdate extends SPosAdvanceService<DCP_CustomerCategoryDiscUpdateReq, DCP_CustomerCategoryDiscUpdateRes> {
    @Override
    protected boolean isVerifyFail(DCP_CustomerCategoryDiscUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CustomerCategoryDiscUpdateReq> getRequestType() {
        return new TypeToken<DCP_CustomerCategoryDiscUpdateReq>() {
        };
    }

    @Override
    protected DCP_CustomerCategoryDiscUpdateRes getResponseType() {
        return new DCP_CustomerCategoryDiscUpdateRes();
    }

    @Override
    protected void processDUID(DCP_CustomerCategoryDiscUpdateReq req, DCP_CustomerCategoryDiscUpdateRes res) throws Exception {

        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("CUSTOMERNO", DataValues.newString(req.getRequest().getCustomerNo()));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("dcp_customer_cate_disc", condition)));

        int item = 1;
        for (DCP_CustomerCategoryDiscUpdateReq.CateDiscList data : req.getRequest().getCateDiscList()) {
            ColumnDataValue dcp_customer_cate_disc = new ColumnDataValue();
            dcp_customer_cate_disc.add("EID", DataValues.newString(req.geteId()));
            dcp_customer_cate_disc.add("CUSTOMERNO", DataValues.newString(req.getRequest().getCustomerNo()));
            dcp_customer_cate_disc.add("ITEM", DataValues.newInteger(item++));
            dcp_customer_cate_disc.add("CATEGORYID", DataValues.newString(data.getCategory()));
            dcp_customer_cate_disc.add("CUSTOMERTYPE", DataValues.newString(req.getRequest().getCustomerType()));
            dcp_customer_cate_disc.add("DISCRATE",DataValues.newInteger(data.getDiscRate()));
            dcp_customer_cate_disc.add("STATUS",DataValues.newDecimal(100));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("dcp_customer_cate_disc", dcp_customer_cate_disc)));
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CustomerCategoryDiscUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CustomerCategoryDiscUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CustomerCategoryDiscUpdateReq req) throws Exception {
        return Collections.emptyList();
    }
}
