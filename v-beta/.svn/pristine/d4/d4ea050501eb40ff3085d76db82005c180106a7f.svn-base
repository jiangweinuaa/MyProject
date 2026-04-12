package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CustomerPriceAdjustCreateReq;
import com.dsc.spos.json.cust.req.DCP_CustomerPriceAdjustUpdateReq;
import com.dsc.spos.json.cust.res.DCP_CustomerPriceAdjustUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_CustomerPriceAdjustUpdate extends SPosAdvanceService<DCP_CustomerPriceAdjustUpdateReq, DCP_CustomerPriceAdjustUpdateRes> {
    @Override
    protected void processDUID(DCP_CustomerPriceAdjustUpdateReq req, DCP_CustomerPriceAdjustUpdateRes res) throws Exception {

        String billNO = req.getRequest().getBillNo();
        String lastModiTime = DateFormatUtils.getNowDateTime();

        ColumnDataValue dcp_custPriceAdjust = new ColumnDataValue();
        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("BILLNO", DataValues.newString(billNO));

        dcp_custPriceAdjust.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
        dcp_custPriceAdjust.add("BDATE", DataValues.newString(DateFormatUtils.getPlainDate(req.getRequest().getBDate())));
        dcp_custPriceAdjust.add("BEGINDATE", DataValues.newString(DateFormatUtils.getPlainDate(req.getRequest().getBeginDate())));
        dcp_custPriceAdjust.add("ENDDATE", DataValues.newString(DateFormatUtils.getPlainDate(req.getRequest().getEndDate())));
        dcp_custPriceAdjust.add("EMPLOYEEID", DataValues.newString(req.getRequest().getEmployeeId()));
        dcp_custPriceAdjust.add("DEPARTID", DataValues.newString(req.getRequest().getDepartId()));
        dcp_custPriceAdjust.add("MEMO", DataValues.newString(req.getRequest().getMemo()));
        dcp_custPriceAdjust.add("STATUS", DataValues.newInteger(req.getRequest().getStatus()));

        dcp_custPriceAdjust.add("MODIFYBY", DataValues.newString(req.getEmployeeNo()));
        dcp_custPriceAdjust.add("MODIFYTIME", DataValues.newDate(lastModiTime));
        int totCQty = 0;
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("dcp_custPriceAdjust_range",condition)));

        for (DCP_CustomerPriceAdjustUpdateReq.CustList custList:req.getRequest().getCustList()){

            ColumnDataValue dcp_custPriceAdjust_range = new ColumnDataValue();

            dcp_custPriceAdjust_range.add("EID",DataValues.newString(req.geteId()));
            dcp_custPriceAdjust_range.add("ORGANIZATIONNO",DataValues.newString(req.getOrganizationNO()));
            dcp_custPriceAdjust_range.add("BILLNO",DataValues.newString(billNO));
            dcp_custPriceAdjust_range.add("ITEM",DataValues.newInteger(custList.getItem()));
            dcp_custPriceAdjust_range.add("CUSTOMERTYPE",DataValues.newString(custList.getCustomerType()));
            dcp_custPriceAdjust_range.add("CUSTOMERNO",DataValues.newString(custList.getCustomerNo()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("dcp_custPriceAdjust_range",dcp_custPriceAdjust_range)));

        }
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("dcp_custPriceAdjust_detail",condition)));

        for (DCP_CustomerPriceAdjustUpdateReq.Detail detail : req.getRequest().getDetail()){
            ColumnDataValue dcp_custPriceAdjust_detail = new ColumnDataValue();

            totCQty++;
            dcp_custPriceAdjust_detail.add("EID",DataValues.newString(req.geteId()));
            dcp_custPriceAdjust_detail.add("ORGANIZATIONNO",DataValues.newString(req.getOrganizationNO()));
            dcp_custPriceAdjust_detail.add("BILLNO",DataValues.newString(billNO));
            dcp_custPriceAdjust_detail.add("ITEM",DataValues.newInteger(detail.getItem()));
            dcp_custPriceAdjust_detail.add("PLUBARCODE",DataValues.newString(detail.getPluBarcode()));
            dcp_custPriceAdjust_detail.add("PLUNO",DataValues.newString(detail.getPluNo()));
            dcp_custPriceAdjust_detail.add("CATEGORY",DataValues.newString(detail.getCategory()));
            dcp_custPriceAdjust_detail.add("UNIT",DataValues.newString(detail.getUnit()));
            dcp_custPriceAdjust_detail.add("PRICE",DataValues.newDecimal(detail.getPrice()));
            dcp_custPriceAdjust_detail.add("RETAILPRICE",DataValues.newDecimal(detail.getRetailPrice()));
            dcp_custPriceAdjust_detail.add("DISCRATE",DataValues.newDecimal(detail.getDiscRate()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("dcp_custPriceAdjust_detail",dcp_custPriceAdjust_detail)));

        }
        dcp_custPriceAdjust.add("TOTCQTY", DataValues.newInteger(totCQty));

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("dcp_custPriceAdjust",condition, dcp_custPriceAdjust)));
        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CustomerPriceAdjustUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CustomerPriceAdjustUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CustomerPriceAdjustUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CustomerPriceAdjustUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CustomerPriceAdjustUpdateReq> getRequestType() {
        return new TypeToken<DCP_CustomerPriceAdjustUpdateReq>(){};
    }

    @Override
    protected DCP_CustomerPriceAdjustUpdateRes getResponseType() {
        return new DCP_CustomerPriceAdjustUpdateRes();
    }
}
