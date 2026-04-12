package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_FeeCreateReq;
import com.dsc.spos.json.cust.res.DCP_FeeCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class DCP_FeeCreate extends SPosAdvanceService<DCP_FeeCreateReq, DCP_FeeCreateRes> {

    @Override
    protected void processDUID(DCP_FeeCreateReq req, DCP_FeeCreateRes res) throws Exception {
        // TODO Auto-generated method stub
        String sql = null;
        try {
            String eId = req.geteId();
            String fee = req.getRequest().getFee();

            //新增团务费用项 BY JZMA 20190211
            String isTourGroup = req.getRequest().getIsTourGroup();
            if (Check.Null(isTourGroup) || !isTourGroup.equals("Y")) {
                isTourGroup = "N";
            }
            sql = this.isRepeat(eId, fee);
            List<Map<String, Object>> feeDatas = this.doQueryData(sql, null);

            if (feeDatas.isEmpty()) {

                ColumnDataValue dcp_fee = new ColumnDataValue();

                dcp_fee.add("EID", DataValues.newString(req.geteId()));
                dcp_fee.add("FEE", DataValues.newString(req.getRequest().getFee()));
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

                dcp_fee.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
                dcp_fee.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
                dcp_fee.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_FEE", dcp_fee)));

                List<DCP_FeeCreateReq.Datas> datas = req.getRequest().getDatas();
                if (datas != null) {
                    for (DCP_FeeCreateReq.Datas par : datas) {

                        ColumnDataValue dcp_fee_lang = new ColumnDataValue();

                        dcp_fee_lang.add("EID", DataValues.newString(req.geteId()));
                        dcp_fee_lang.add("FEE", DataValues.newString(req.getRequest().getFee()));
                        dcp_fee_lang.add("FEE_NAME", DataValues.newString(par.getFeeName()));
                        dcp_fee_lang.add("LANG_TYPE", DataValues.newString(par.getLangType()));
                        dcp_fee_lang.add("STATUS", DataValues.newInteger(par.getStatus()));

                        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_FEE_LANG", dcp_fee_lang)));

                    }
                }
                //添加原因吗信息

                this.doExecuteDataToDB();
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");

            } else {
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("服务执行失败: 费用单号：" + fee + " 已存在！");
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_FeeCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_FeeCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_FeeCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_FeeCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;


        return isFail;
    }

    @Override
    protected TypeToken<DCP_FeeCreateReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_FeeCreateReq>() {
        };
    }

    @Override
    protected DCP_FeeCreateRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_FeeCreateRes();
    }


    private String isRepeat(String eId, String fee) {
        String sql = "select fee from DCP_FEE "
                + " where fee = '" + fee + "' "
                + " and EID = '" + eId + "'";
        return sql;
    }

}
