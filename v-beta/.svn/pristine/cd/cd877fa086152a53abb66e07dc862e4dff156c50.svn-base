package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_UnitConvertEnableReq;
import com.dsc.spos.json.cust.req.DCP_UnitConvertEnableReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_UnitConvertEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 单位转换信息查询 2024-09-13
 *
 * @author 01029
 */
public class DCP_UnitConvertEnable extends SPosAdvanceService<DCP_UnitConvertEnableReq, DCP_UnitConvertEnableRes> {

    @Override
    protected void processDUID(DCP_UnitConvertEnableReq req, DCP_UnitConvertEnableRes res) throws Exception {
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
                String keyNo2 = par.getoUnit();
                String oPId = req.getEmployeeNo();
                UptBean up1 = new UptBean("dcp_unitconvert");
                up1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                up1.addCondition("UNIT", new DataValue(keyNo, Types.VARCHAR));
                up1.addCondition("OUNIT", new DataValue(keyNo2, Types.VARCHAR));
                up1.addUpdateValue("LASTMODIOPID", new DataValue(oPId, Types.VARCHAR));

                String lastmoditime = DateFormatUtils.getNowDateTime();
                up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
                if (status.equals("0")) {
                    up1.addCondition("STATUS", new DataValue("100", Types.VARCHAR));
                }

                up1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
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
    protected List<InsBean> prepareInsertData(DCP_UnitConvertEnableReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_UnitConvertEnableReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_UnitConvertEnableReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_UnitConvertEnableReq req) throws Exception {
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

            if (Check.Null(par.getoUnit())) {
                errMsg.append("换算前单位编码不可为空值, ");
                isFail = true;
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
            if (Check.Null(par.getUnit())) {
                errMsg.append("换算后单位编码不可为空值, ");
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
    protected TypeToken<DCP_UnitConvertEnableReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_UnitConvertEnableReq>() {
        };
    }

    @Override
    protected DCP_UnitConvertEnableRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_UnitConvertEnableRes();
    }

}
