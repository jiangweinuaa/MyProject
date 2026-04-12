package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_MaterialReplaceEnableReq;
import com.dsc.spos.json.cust.res.DCP_MaterialReplaceEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_MaterialReplaceEnable  extends SPosAdvanceService<DCP_MaterialReplaceEnableReq, DCP_MaterialReplaceEnableRes> {

    @Override
    protected void processDUID(DCP_MaterialReplaceEnableReq req, DCP_MaterialReplaceEnableRes res) throws Exception {

        String eId = req.geteId();

        List<DCP_MaterialReplaceEnableReq.Datas> datas = req.getRequest().getDatas();
        for (DCP_MaterialReplaceEnableReq.Datas data : datas) {

            UptBean ub1 = new UptBean("MES_MATERIAL_REPLACE");
            ub1.addUpdateValue("STATUS", new DataValue(req.getRequest().getOpType(), Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("MATERIAL_PLUNO", new DataValue(data.getMaterialPluNo(), Types.VARCHAR));
            ub1.addCondition("MATERIAL_UNIT", new DataValue(data.getMaterialUnit(), Types.VARCHAR));
            ub1.addCondition("REPLACETYPE", new DataValue(data.getReplaceType(), Types.VARCHAR));
            ub1.addCondition("REPLACE_PLUNO", new DataValue(data.getReplacePluNo(), Types.VARCHAR));
            if("0".equals(data.getReplaceType())){
                ub1.addCondition("ORGANIZATIONNO", new DataValue("ALL", Types.VARCHAR));
            }else{
                ub1.addCondition("ORGANIZATIONNO", new DataValue(data.getOrganizationNo(), Types.VARCHAR));
            }

            this.addProcessData(new DataProcessBean(ub1));

        }


        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_MaterialReplaceEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_MaterialReplaceEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_MaterialReplaceEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_MaterialReplaceEnableReq req) throws Exception {
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
    protected TypeToken<DCP_MaterialReplaceEnableReq> getRequestType() {
        return new TypeToken<DCP_MaterialReplaceEnableReq>() {
        };
    }

    @Override
    protected DCP_MaterialReplaceEnableRes getResponseType() {
        return new DCP_MaterialReplaceEnableRes();
    }
}