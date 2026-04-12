package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CustomerPriceAdjustCreateReq;
import com.dsc.spos.json.cust.res.DCP_CustomerPriceAdjustCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_CustomerPriceAdjustCreate extends SPosAdvanceService<DCP_CustomerPriceAdjustCreateReq, DCP_CustomerPriceAdjustCreateRes> {

    @Override
    protected void processDUID(DCP_CustomerPriceAdjustCreateReq req, DCP_CustomerPriceAdjustCreateRes res) throws Exception {

        ColumnDataValue dcp_custPriceAdjust = new ColumnDataValue();
        String billNO = getOrderNO(req, "KHTJ");
        String lastModiTime =DateFormatUtils.getNowDateTime();

        dcp_custPriceAdjust.add("EID", DataValues.newString(req.geteId()));
        dcp_custPriceAdjust.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
        dcp_custPriceAdjust.add("BILLNO", DataValues.newString(billNO));
        dcp_custPriceAdjust.add("BDATE", DataValues.newString(DateFormatUtils.getPlainDate(req.getRequest().getBDate())));
        dcp_custPriceAdjust.add("BEGINDATE", DataValues.newString(DateFormatUtils.getPlainDate(req.getRequest().getBeginDate())));
        dcp_custPriceAdjust.add("ENDDATE", DataValues.newString(DateFormatUtils.getPlainDate(req.getRequest().getEndDate())));
        dcp_custPriceAdjust.add("EMPLOYEEID", DataValues.newString(req.getRequest().getEmployeeId()));
        dcp_custPriceAdjust.add("DEPARTID", DataValues.newString(req.getRequest().getDepartId()));
        dcp_custPriceAdjust.add("MEMO", DataValues.newString(req.getRequest().getMemo()));

        if (StringUtils.isEmpty(req.getRequest().getStatus())){
            dcp_custPriceAdjust.add("STATUS", DataValues.newInteger(0));
        }else {
            dcp_custPriceAdjust.add("STATUS", DataValues.newInteger(req.getRequest().getStatus()));
        }


        dcp_custPriceAdjust.add("OWNOPID", DataValues.newString(req.getEmployeeNo()));
        dcp_custPriceAdjust.add("OWNDEPTID", DataValues.newString(req.getDepartmentNo()));
        dcp_custPriceAdjust.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
        dcp_custPriceAdjust.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
        dcp_custPriceAdjust.add("CREATETIME", DataValues.newDate(lastModiTime));
        int totCQty = 0;
        for (DCP_CustomerPriceAdjustCreateReq.CustList custList:req.getRequest().getCustList()){

            ColumnDataValue dcp_custPriceAdjust_range = new ColumnDataValue();

            dcp_custPriceAdjust_range.add("EID",DataValues.newString(req.geteId()));
            dcp_custPriceAdjust_range.add("ORGANIZATIONNO",DataValues.newString(req.getOrganizationNO()));
            dcp_custPriceAdjust_range.add("BILLNO",DataValues.newString(billNO));
            dcp_custPriceAdjust_range.add("ITEM",DataValues.newInteger(custList.getItem()));
            dcp_custPriceAdjust_range.add("CUSTOMERTYPE",DataValues.newString(custList.getCustomerType()));
            dcp_custPriceAdjust_range.add("CUSTOMERNO",DataValues.newString(custList.getCustomerNo()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("dcp_custPriceAdjust_range",dcp_custPriceAdjust_range)));

        }

        for (DCP_CustomerPriceAdjustCreateReq.Detail detail : req.getRequest().getDetail()){
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

        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("dcp_custPriceAdjust",dcp_custPriceAdjust)));
        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        res.setBillNo(billNO);
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CustomerPriceAdjustCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CustomerPriceAdjustCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CustomerPriceAdjustCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CustomerPriceAdjustCreateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CustomerPriceAdjustCreateReq> getRequestType() {
        return new TypeToken<DCP_CustomerPriceAdjustCreateReq>() {
        };
    }

    @Override
    protected DCP_CustomerPriceAdjustCreateRes getResponseType() {
        return new DCP_CustomerPriceAdjustCreateRes();
    }
}
