package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PriceAdjustDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PriceAdjustDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class DCP_PriceAdjustDelete extends SPosAdvanceService<DCP_PriceAdjustDeleteReq, DCP_PriceAdjustDeleteRes> {
    @Override
    protected void processDUID(DCP_PriceAdjustDeleteReq req, DCP_PriceAdjustDeleteRes res) throws Exception {

        try {
            String query = " SELECT * FROM DCP_PRICEADJUST WHERE EID='%s' AND BILLNO='%s' ";
            query = String.format(query, req.geteId(), req.getRequest().getBillNo());
            List<Map<String, Object>> data = this.doQueryData(query, null);

            if (null != data && !data.isEmpty()) {
                Map<String, Object> bill = data.get(0);

                if (!StringUtils.equals("0",bill.get("STATUS").toString())) {
                    res.setSuccess(false);
                    res.setServiceStatus("200");
                    res.setServiceDescription("单据状态非【0-新建】不可更改！");
                    return;
                }
            }

            ColumnDataValue condition = new ColumnDataValue();

            condition.add("EID", DataValues.newString(req.geteId()));
            condition.add("BILLNO", DataValues.newString(req.getRequest().getBillNo()));

            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_PRICEADJUST", condition)));
            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_PRICEADJUST_DETAIL", condition)));
            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_PRICEADJUST_PRICE", condition)));

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:" + e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PriceAdjustDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PriceAdjustDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PriceAdjustDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PriceAdjustDeleteReq req) throws Exception {

        return false;
    }

    @Override
    protected TypeToken<DCP_PriceAdjustDeleteReq> getRequestType() {
        return new TypeToken<DCP_PriceAdjustDeleteReq>() {

        };
    }

    @Override
    protected DCP_PriceAdjustDeleteRes getResponseType() {
        return new DCP_PriceAdjustDeleteRes();
    }
}
