package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SettleDataCreateReq;
import com.dsc.spos.json.cust.res.DCP_SettleDataCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class DCP_SettleDataCreate extends SPosAdvanceService<DCP_SettleDataCreateReq,DCP_SettleDataCreateRes> {
    @Override
    protected void processDUID(DCP_SettleDataCreateReq req, DCP_SettleDataCreateRes res) throws Exception {

        if (CollectionUtils.isNotEmpty(req.getRequest().getSettleList())){

            int i = 0;
            for (DCP_SettleDataCreateReq.SettleList oneSettle:req.getRequest().getSettleList()){
                ColumnDataValue dcp_settledata = new ColumnDataValue();

                dcp_settledata.add("EID", DataValues.newString(req.geteId()));
                dcp_settledata.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrganizationNo()));
                dcp_settledata.add("BILLNO", DataValues.newString(req.getRequest().getBillNo()));
                dcp_settledata.add("ITEM", DataValues.newString(oneSettle.getItem()));

                dcp_settledata.add("BTYPE", DataValues.newString(req.getRequest().getBType()));
                dcp_settledata.add("BDATE", DataValues.newString(DateFormatUtils.getPlainDate(req.getRequest().getBdate())));
                dcp_settledata.add("BIZTYPE", DataValues.newString(req.getRequest().getBizType()));
                dcp_settledata.add("BIZPARTNERNO", DataValues.newString(oneSettle.getBizPartnerNo()));
                dcp_settledata.add("PAYORGNO", DataValues.newString(oneSettle.getPayOrgNo()));
                dcp_settledata.add("BILLDATENO", DataValues.newString(oneSettle.getBillDateNo()));
                dcp_settledata.add("PAYDATENO", DataValues.newString(oneSettle.getPayDate()));
                dcp_settledata.add("INVOICECODE", DataValues.newString(oneSettle.getInvoiceCode()));
                dcp_settledata.add("BILLDATE", DataValues.newString(oneSettle.getBillDate()));
                dcp_settledata.add("PAYDATE", DataValues.newString(oneSettle.getPayDate()));
                dcp_settledata.add("YEAR", DataValues.newString(oneSettle.getYear()));
                dcp_settledata.add("MONTH", DataValues.newString(oneSettle.getMonth()));
                dcp_settledata.add("CURRENCY", DataValues.newString(oneSettle.getCurrency()));
                dcp_settledata.add("TAXCODE", DataValues.newString(oneSettle.getTaxCode()));
                dcp_settledata.add("TAXRATE", DataValues.newString(oneSettle.getTaxRate()));
                dcp_settledata.add("DIRECTION", DataValues.newString(oneSettle.getDirection()));
                dcp_settledata.add("PRETAXAMT", DataValues.newString(oneSettle.getPreTaxAmt()));
                dcp_settledata.add("BILLAMT", DataValues.newString(oneSettle.getBillAmt()));
                dcp_settledata.add("TAXAMT", DataValues.newString(oneSettle.getTaxAmt()));
                dcp_settledata.add("UNSETTLEAMT", DataValues.newString(oneSettle.getUnSettlAmt()));
                dcp_settledata.add("SETTLEAMT", DataValues.newString(oneSettle.getSettleAmt()));
                dcp_settledata.add("PAIDAMT", DataValues.newString(oneSettle.getPaidAmt()));
                dcp_settledata.add("BILLQTY", DataValues.newString(oneSettle.getBillQty()));
                dcp_settledata.add("BILLPRICE", DataValues.newString(oneSettle.getBillPrice()));
                dcp_settledata.add("PRICEUNIT", DataValues.newString(oneSettle.getPriceUnit()));
                dcp_settledata.add("DEPARTID", DataValues.newString(oneSettle.getDepartId()));
                dcp_settledata.add("CATEGORY", DataValues.newString(oneSettle.getCateGory()));
                dcp_settledata.add("PLUNO", DataValues.newString(oneSettle.getPluNo()));
                dcp_settledata.add("FEATURENO", DataValues.newString(oneSettle.getFeatureNo()));
                dcp_settledata.add("STATUS", DataValues.newString(oneSettle.getStatus()));
                dcp_settledata.add("FEE", DataValues.newString(oneSettle.getFee()));
                dcp_settledata.add("UNPAIDAMT", DataValues.newString(oneSettle.getUnPaidAmt()));
                dcp_settledata.add("APQTY", DataValues.newString(oneSettle.getApQty()));
                dcp_settledata.add("APAMT", DataValues.newString(oneSettle.getApAmt()));
                dcp_settledata.add("UNAPAMT", DataValues.newString(oneSettle.getUnApAmt()));


                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_SETTLEDATA",dcp_settledata)));

            }
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_SettleDataCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SettleDataCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SettleDataCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_SettleDataCreateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_SettleDataCreateReq> getRequestType() {
        return new TypeToken<DCP_SettleDataCreateReq>(){};
    }

    @Override
    protected DCP_SettleDataCreateRes getResponseType() {
        return new DCP_SettleDataCreateRes();
    }
}
