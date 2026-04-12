package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TopupSetDeleteReq;
import com.dsc.spos.json.cust.res.DCP_TopupSetDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_TopupSetDelete extends SPosAdvanceService<DCP_TopupSetDeleteReq, DCP_TopupSetDeleteRes> {
    @Override
    protected void processDUID(DCP_TopupSetDeleteReq req, DCP_TopupSetDeleteRes res) throws Exception {


//        1. 整单删除条件：法人+归属组织法人
//        2. 单笔删除条件：法人+归属组织法人+充值商品编码

        ColumnDataValue condition = new ColumnDataValue();
        condition.add("EID", req.geteId());
        condition.add("CORP", req.getRequest().getCorp());
        condition.add("TOPUPORG", req.getRequest().getTopupOrg());

        if (StringUtils.isNotEmpty(req.getRequest().getTopupProdID())) {
            condition.add("TOPUPPRODID", req.getRequest().getTopupProdID());
        }
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_TOPUPSET", condition)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_TopupSetDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_TopupSetDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_TopupSetDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_TopupSetDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_TopupSetDeleteReq> getRequestType() {
        return new TypeToken<DCP_TopupSetDeleteReq>() {
        };
    }

    @Override
    protected DCP_TopupSetDeleteRes getResponseType() {
        return new DCP_TopupSetDeleteRes();
    }
}
