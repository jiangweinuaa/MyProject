package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProdScheduleDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ProdScheduleDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_ProdScheduleDelete extends SPosAdvanceService<DCP_ProdScheduleDeleteReq, DCP_ProdScheduleDeleteRes> {

    @Override
    protected void processDUID(DCP_ProdScheduleDeleteReq req, DCP_ProdScheduleDeleteRes res) throws Exception {

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String billNo = req.getRequest().getBillNo();

        String valSql="select * from DCP_PRODSCHEDULE a where a.eid='"+eId+"' " +
                " and a.organizationno='"+organizationNO+"' and a.status='0' and a.billno='"+billNo+"'" ;
        List<Map<String, Object>> rows = this.doQueryData(valSql, null);

        if(rows.size()>0){
            //TEMPLATEID
            DelBean db1 = new DelBean("DCP_PRODSCHEDULE");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
            db1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            DelBean db2 = new DelBean("DCP_PRODSCHEDULE_DETAIL");
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db2.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
            db2.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));

            DelBean db3 = new DelBean("DCP_PRODSCHEDULE_GEN");
            db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db3.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
            db3.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db3));

            DelBean db4 = new DelBean("DCP_PRODSCHEDULE_SOURCE");
            db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db4.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
            db4.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db4));

            this.doExecuteDataToDB();
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProdScheduleDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProdScheduleDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProdScheduleDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ProdScheduleDeleteReq req) throws Exception {
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
    protected TypeToken<DCP_ProdScheduleDeleteReq> getRequestType() {
        return new TypeToken<DCP_ProdScheduleDeleteReq>() {
        };
    }

    @Override
    protected DCP_ProdScheduleDeleteRes getResponseType() {
        return new DCP_ProdScheduleDeleteRes();
    }
}

