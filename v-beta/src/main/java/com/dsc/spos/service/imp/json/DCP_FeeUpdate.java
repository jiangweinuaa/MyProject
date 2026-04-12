package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_FeeUpdateReq;
import com.dsc.spos.json.cust.res.DCP_FeeUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.util.List;

public class DCP_FeeUpdate extends SPosAdvanceService<DCP_FeeUpdateReq, DCP_FeeUpdateRes> {

    @Override
    protected void processDUID(DCP_FeeUpdateReq req, DCP_FeeUpdateRes res) throws Exception {
        // TODO Auto-generated method stub
        try {
            String eId = req.geteId();
            String fee = req.getRequest().getFee();
            String feeName = req.getRequest().getFeeName();
            String feeType = req.getRequest().getFeeType();
            String status = req.getRequest().getStatus();
            //新增团务费用项 BY JZMA 20190211
            String isTourGroup = req.getRequest().getIsTourGroup();
            if (Check.Null(isTourGroup) || !isTourGroup.equals("Y")) {
                isTourGroup = "N";
            }

            List<DCP_FeeUpdateReq.Datas> datas = req.getRequest().getDatas();

            DelBean db2 = new DelBean("DCP_FEE_LANG");
            db2.addCondition("FEE", new DataValue(fee, Types.VARCHAR));
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));

            if (datas != null && !datas.isEmpty()) {

                for (DCP_FeeUpdateReq.Datas par : datas) {
                    ColumnDataValue dcp_fee_lang = new ColumnDataValue();

                    dcp_fee_lang.add("EID", DataValues.newString(req.geteId()));
                    dcp_fee_lang.add("FEE", DataValues.newString(req.getRequest().getFee()));
                    dcp_fee_lang.add("FEE_NAME", DataValues.newString(par.getFeeName()));
                    dcp_fee_lang.add("LANG_TYPE", DataValues.newString(par.getLangType()));
                    dcp_fee_lang.add("STATUS", DataValues.newInteger(par.getStatus()));

                    this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_FEE_LANG", dcp_fee_lang)));
                }
            }

            ColumnDataValue dcp_fee = new ColumnDataValue();

            dcp_fee.add("FEE_NAME", DataValues.newString(req.getRequest().getFeeName()));
            dcp_fee.add("FEE_TYPE", DataValues.newString(req.getRequest().getFeeType()));
            dcp_fee.add("ISTOURGROUP", DataValues.newString(isTourGroup));

            dcp_fee.add("STATUS", DataValues.newInteger(req.getRequest().getStatus()));

            dcp_fee.add("FEENATURE", DataValues.newDecimal(req.getRequest().getFeeNature()));
            dcp_fee.add("TAXCODE", DataValues.newString(req.getRequest().getTaxCode()));
            dcp_fee.add("ACCOUNTINGPOLICY", DataValues.newDecimal(req.getRequest().getAccountingPolicy()));
            dcp_fee.add("PRICECATEGORY", DataValues.newDecimal(req.getRequest().getPriceCategory()));
            dcp_fee.add("FEEALLOCATION", DataValues.newDecimal(req.getRequest().getFeeAllocation()));
            dcp_fee.add("UPDATE_TIME", DataValues.newString(DateFormatUtils.getTimestamp()));
            dcp_fee.add("TRAN_TIME", DataValues.newString(DateFormatUtils.getTimestamp()));
            dcp_fee.add("ISINVOICEISSUED", DataValues.newString(req.getRequest().getIsInvoiceIssued()));
            dcp_fee.add("INSETTLEMENT", DataValues.newString(req.getRequest().getInSettlement()));

            dcp_fee.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_fee.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            //condition
            ColumnDataValue condition = new ColumnDataValue();
            condition.add("EID", DataValues.newString(eId));
            condition.add("FEE", DataValues.newString(fee));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_FEE", condition,dcp_fee)));

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_FeeUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_FeeUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_FeeUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_FeeUpdateReq req) throws Exception {
        boolean isFail = false;

        return isFail;
    }

    @Override
    protected TypeToken<DCP_FeeUpdateReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_FeeUpdateReq>() {
        };
    }

    @Override
    protected DCP_FeeUpdateRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_FeeUpdateRes();
    }

}
