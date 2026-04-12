package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PrintSettingReq;
import com.dsc.spos.json.cust.res.DCP_PrintSettingRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_PrintSetting extends SPosAdvanceService<DCP_PrintSettingReq, DCP_PrintSettingRes> {

    @Override
    protected void processDUID(DCP_PrintSettingReq req, DCP_PrintSettingRes res) throws Exception {

        String eId = req.geteId();
        String modularNo = req.getRequest().getModularNo();

        if(Check.NotNull(req.getRequest().getDefualtReportNo())){
            //清空默认值
            UptBean ub2 = new UptBean("DCP_MODULAR_PRINT");
            ub2.addUpdateValue("ISDEFAULT", new DataValue("N", Types.VARCHAR));

            ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub2.addCondition("MODULARNO", new DataValue(modularNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub2));
        }

        UptBean ub1 = new UptBean("DCP_MODULAR_PRINT");
        ub1.addUpdateValue("ISDEFAULT", new DataValue("Y", Types.VARCHAR));
        ub1.addUpdateValue("PRINTTYPE", new DataValue(req.getRequest().getPrintType(), Types.VARCHAR));

        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("MODULARNO", new DataValue(modularNo, Types.VARCHAR));
        ub1.addCondition("PRINTNO", new DataValue(req.getRequest().getDefualtReportNo(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));


        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PrintSettingReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PrintSettingReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PrintSettingReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PrintSettingReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_PrintSettingReq> getRequestType() {
        return new TypeToken<DCP_PrintSettingReq>() {
        };
    }

    @Override
    protected DCP_PrintSettingRes getResponseType() {
        return new DCP_PrintSettingRes();
    }
}

