package com.dsc.spos.service.imp.json;


import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PurStockInUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PurStockInUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

/**
 * 接口已作废
 *
 */
@Deprecated
public class DCP_PurStockInUpdate  extends SPosAdvanceService<DCP_PurStockInUpdateReq, DCP_PurStockInUpdateRes> {

    @Override
    protected void processDUID(DCP_PurStockInUpdateReq req, DCP_PurStockInUpdateRes res) throws Exception {

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurStockInUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurStockInUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurStockInUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_PurStockInUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PurStockInUpdateReq> getRequestType() {
        return new TypeToken<DCP_PurStockInUpdateReq>(){

        };
    }

    @Override
    protected DCP_PurStockInUpdateRes getResponseType() {
        return new DCP_PurStockInUpdateRes();
    }
}
