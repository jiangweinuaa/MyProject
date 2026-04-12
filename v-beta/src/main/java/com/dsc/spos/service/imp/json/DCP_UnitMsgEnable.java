package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_UnitMsgEnableReq;
import com.dsc.spos.json.cust.req.DCP_UnitMsgEnableReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_UnitMsgEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_UnitMsgEnable extends SPosAdvanceService<DCP_UnitMsgEnableReq, DCP_UnitMsgEnableRes> {

    @Override
    protected void processDUID(DCP_UnitMsgEnableReq req, DCP_UnitMsgEnableRes res) throws Exception {
        // TODO Auto-generated method stub
        try {
            String status = "100";//状态：-1未启用100已启用 0已禁用

            if (req.getRequest().getOprType().equals("1"))//操作类型：1-启用2-禁用
            {
                status = "100";
            } else {
                status = "0";
            }

            for (level1Elm par : req.getRequest().getUnitList()) {
                String keyNo = par.getUnit();
                UptBean up1 = new UptBean("DCP_UNIT");
                up1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                up1.addCondition("UNIT", new DataValue(keyNo, Types.VARCHAR));
                if (status.equals("0")) {
                    up1.addCondition("STATUS", new DataValue("100", Types.VARCHAR));
                }

                up1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
                up1.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
                up1.addUpdateValue("LASTMODIOPNAME", DataValues.newString(req.getEmployeeName()));
                up1.addUpdateValue("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

                this.addProcessData(new DataProcessBean(up1));

            }

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");


        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常：" + e.getMessage());

        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_UnitMsgEnableReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_UnitMsgEnableReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_UnitMsgEnableReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_UnitMsgEnableReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (req.getRequest() == null) {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(req.getRequest().getOprType())) {
            errMsg.append("操作类型不可为空值, ");
            isFail = true;
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        List<level1Elm> stausList = req.getRequest().getUnitList();

        if (stausList == null || stausList.isEmpty()) {
            errMsg.append("编码不可为空, ");
            isFail = true;
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        for (level1Elm par : stausList) {
            if (Check.Null(par.getUnit())) {
                errMsg.append("编码不可为空值, ");
                isFail = true;
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
        }


        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_UnitMsgEnableReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_UnitMsgEnableReq>() {
        };
    }

    @Override
    protected DCP_UnitMsgEnableRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_UnitMsgEnableRes();
    }

}
