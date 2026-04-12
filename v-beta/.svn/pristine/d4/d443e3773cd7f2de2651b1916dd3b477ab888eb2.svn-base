package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PurStockInDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PurStockInDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;
import java.util.Collections;
import java.util.List;

/**
 * 接口已作废
 *
 */
@Deprecated
public class DCP_PurStockInDelete extends SPosAdvanceService<DCP_PurStockInDeleteReq, DCP_PurStockInDeleteRes> {

    @Override
    protected void processDUID(DCP_PurStockInDeleteReq req, DCP_PurStockInDeleteRes res) throws Exception {

        ColumnDataValue condition = new ColumnDataValue();
        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("BILLTYPE",DataValues.newString(req.getRequest().getBillType()));
        condition.add("PSTOCKINNO",DataValues.newString(req.getRequest().getBillType()));

        DelBean db1 = DataBeans.getDelBean("DCP_PURSTOCKIN", condition);
        this.addProcessData(new DataProcessBean(db1));
        DelBean db2 = DataBeans.getDelBean("DCP_PURSTOCKIN_DETAIL", condition);
        this.addProcessData(new DataProcessBean(db2));
        DelBean db3 = DataBeans.getDelBean("DCP_PURSTOCKIN_LOTS", condition);
        this.addProcessData(new DataProcessBean(db3));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurStockInDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurStockInDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurStockInDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_PurStockInDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PurStockInDeleteReq> getRequestType() {
        return null;
    }

    @Override
    protected DCP_PurStockInDeleteRes getResponseType() {
        return null;
    }
}
