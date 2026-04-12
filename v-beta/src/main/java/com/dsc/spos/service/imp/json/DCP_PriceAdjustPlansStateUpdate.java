package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PriceAdjustPlansStateUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PriceAdjustPlansStateUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class DCP_PriceAdjustPlansStateUpdate extends SPosAdvanceService<DCP_PriceAdjustPlansStateUpdateReq, DCP_PriceAdjustPlansStateUpdateRes> {

    //        terminate-终止 execute-执行
    private static final String TYPE_TERMINATE = "terminate";
    private static final String TYPE_EXECUTE = "execute";


    @Override
    protected void processDUID(DCP_PriceAdjustPlansStateUpdateReq req, DCP_PriceAdjustPlansStateUpdateRes res) throws Exception {

        try {

            String lastModifyTime = DateFormatUtils.getNowDateTime();

            for (DCP_PriceAdjustPlansStateUpdateReq.Bill bill : req.getRequest().getBillList()) {

                ColumnDataValue condition = new ColumnDataValue();
                condition.add("EID", DataValues.newString(req.geteId()));
                condition.add("BILLNO", DataValues.newString(bill.getBillNo()));
                condition.add("ITEM", DataValues.newString(bill.getItem()));

                ColumnDataValue dcpPriceAdjustPlan = new ColumnDataValue();
                if (TYPE_TERMINATE.equals(req.getRequest().getOprType())){
                    dcpPriceAdjustPlan.add("STATUS",DataValues.newInteger(3));
                }else if (TYPE_EXECUTE.equals(req.getRequest().getOprType())){
                    dcpPriceAdjustPlan.add("STATUS",DataValues.newInteger(1));
                }
                dcpPriceAdjustPlan.add("CONFIRMBY",DataValues.newString(req.getEmployeeNo()));
                dcpPriceAdjustPlan.add("CONFIRMTIME",DataValues.newDate(lastModifyTime));

                this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PRICEADJUSTPLAN",condition, dcpPriceAdjustPlan)));

            }

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
    protected List<InsBean> prepareInsertData(DCP_PriceAdjustPlansStateUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PriceAdjustPlansStateUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PriceAdjustPlansStateUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PriceAdjustPlansStateUpdateReq req) throws Exception {

        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (null == req.getRequest().getBillList() || req.getRequest().getBillList().isEmpty()) {
            errMsg.append("单据列表不可为空");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (StringUtils.isEmpty(req.getRequest().getOprType())) {
            errMsg.append("操作类型不可为空");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        req.getRequest().setOprType(req.getRequest().getOprType().toLowerCase()); //小写转换

        return false;
    }

    @Override
    protected TypeToken<DCP_PriceAdjustPlansStateUpdateReq> getRequestType() {
        return new TypeToken<DCP_PriceAdjustPlansStateUpdateReq>() {

        };
    }

    @Override
    protected DCP_PriceAdjustPlansStateUpdateRes getResponseType() {
        return new DCP_PriceAdjustPlansStateUpdateRes();
    }
}
