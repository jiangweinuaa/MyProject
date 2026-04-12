package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_MaterialReplaceDeleteReq;
import com.dsc.spos.json.cust.res.DCP_MaterialReplaceDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_MaterialReplaceDelete extends SPosAdvanceService<DCP_MaterialReplaceDeleteReq, DCP_MaterialReplaceDeleteRes> {

    @Override
    protected void processDUID(DCP_MaterialReplaceDeleteReq req, DCP_MaterialReplaceDeleteRes res) throws Exception {

        String eId = req.geteId();

        List<DCP_MaterialReplaceDeleteReq.Datas> datas = req.getRequest().getDatas();
        for (DCP_MaterialReplaceDeleteReq.Datas data : datas) {

            DelBean db1 = new DelBean("MES_MATERIAL_REPLACE");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("MATERIAL_PLUNO", new DataValue(data.getMaterialPluNo(), Types.VARCHAR));
            db1.addCondition("MATERIAL_UNIT", new DataValue(data.getMaterialUnit(), Types.VARCHAR));
            db1.addCondition("REPLACETYPE", new DataValue(data.getReplaceType(), Types.VARCHAR));
            db1.addCondition("REPLACE_PLUNO", new DataValue(data.getReplacePluNo(), Types.VARCHAR));
            if(Check.NotNull(data.getOrganizationNo())) {
                db1.addCondition("ORGANIZATIONNO", new DataValue(data.getOrganizationNo(), Types.VARCHAR));
            }
            this.addProcessData(new DataProcessBean(db1));

        }


        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_MaterialReplaceDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_MaterialReplaceDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_MaterialReplaceDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_MaterialReplaceDeleteReq req) throws Exception {
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
    protected TypeToken<DCP_MaterialReplaceDeleteReq> getRequestType() {
        return new TypeToken<DCP_MaterialReplaceDeleteReq>() {
        };
    }

    @Override
    protected DCP_MaterialReplaceDeleteRes getResponseType() {
        return new DCP_MaterialReplaceDeleteRes();
    }
}