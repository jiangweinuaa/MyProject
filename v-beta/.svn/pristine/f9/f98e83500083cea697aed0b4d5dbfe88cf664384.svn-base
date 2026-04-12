package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PurInvReconDiffUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PurInvReconDiffUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_PurInvReconDiffUpdate extends SPosAdvanceService<DCP_PurInvReconDiffUpdateReq, DCP_PurInvReconDiffUpdateRes> {
    @Override
    protected void processDUID(DCP_PurInvReconDiffUpdateReq req, DCP_PurInvReconDiffUpdateRes res) throws Exception {
//        1.发票明细录入,2.差异分摊至单价(依金额比例),3.差异分摊至单价(依数量比例),4.差异金额转其他加减项
        String diff = req.getRequest().getDiff();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurInvReconDiffUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurInvReconDiffUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurInvReconDiffUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_PurInvReconDiffUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PurInvReconDiffUpdateReq> getRequestType() {
        return new TypeToken<DCP_PurInvReconDiffUpdateReq>() {
        };
    }

    @Override
    protected DCP_PurInvReconDiffUpdateRes getResponseType() {
        return new DCP_PurInvReconDiffUpdateRes();
    }

    @Override
    protected String getQuerySql(DCP_PurInvReconDiffUpdateReq req) throws Exception {
        return super.getQuerySql(req);
    }
}
