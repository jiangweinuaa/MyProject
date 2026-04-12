package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CostLevelCreateReq;
import com.dsc.spos.json.cust.res.DCP_CostLevelCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_CostLevelCreate extends SPosAdvanceService<DCP_CostLevelCreateReq, DCP_CostLevelCreateRes> {

    @Override
    protected void processDUID(DCP_CostLevelCreateReq req, DCP_CostLevelCreateRes res) throws Exception {
        try {

            List<Map<String,Object>> repeat = doQueryData(String.format(" SELECT * FROM DCP_COSTLEVEL WHERE EID='%s' AND COSTGROUPINGID='%s' ",req.geteId(),req.getRequest().getCostGroupingId()) ,null);
            if (null!=repeat && !repeat.isEmpty()){
               throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400,req.getRequest().getCostGroupingId()+"已存在");
            }

            ColumnDataValue dcp_costLevel = new ColumnDataValue();
            dcp_costLevel.add("EID", DataValues.newString(req.geteId()));
            dcp_costLevel.add("COSTGROUPINGID", DataValues.newString(req.getRequest().getCostGroupingId()));
            dcp_costLevel.add("COSTGROUPINGID_NAME", DataValues.newString(req.getRequest().getCostGroupingId_Name()));
            dcp_costLevel.add("START_COSTLEVEL", DataValues.newDecimal(req.getRequest().getStart_CostLevel()));
            dcp_costLevel.add("END_COSTLEVEL", DataValues.newDecimal(req.getRequest().getEnd_CostLevel()));
            dcp_costLevel.add("STATUS", DataValues.newInteger(req.getRequest().getStatus()));
            dcp_costLevel.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_costLevel.add("CREATEOPNAME", DataValues.newString(req.getEmployeeName()));
            dcp_costLevel.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_COSTLEVEL", dcp_costLevel)));

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");

        } catch (Exception e) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CostLevelCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CostLevelCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CostLevelCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CostLevelCreateReq req) throws Exception {
        StringBuilder errorMsg = new StringBuilder();
        if (req.getRequest().getStart_CostLevel() > req.getRequest().getEnd_CostLevel() ||
                !Check.isInteger(req.getRequest().getStart_CostLevel()) ||
                !Check.isInteger(req.getRequest().getEnd_CostLevel())

        ) {
            errorMsg.append("Start_CostLevel和End_CostLevel为数字整数，且截止成本阶不得小于起始成本阶");
        }



        if (errorMsg.length() > 0) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errorMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_CostLevelCreateReq> getRequestType() {
        return new TypeToken<DCP_CostLevelCreateReq>() {

        };
    }

    @Override
    protected DCP_CostLevelCreateRes getResponseType() {
        return new DCP_CostLevelCreateRes();
    }
}
