package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_LocationDeleteReq;
import com.dsc.spos.json.cust.res.DCP_LocationDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_LocationDelete  extends SPosAdvanceService<DCP_LocationDeleteReq, DCP_LocationDeleteRes> {

    @Override
    protected void processDUID(DCP_LocationDeleteReq req, DCP_LocationDeleteRes res) throws Exception {

        String eId = req.geteId();
        String orgNo = req.getRequest().getOrgNo();
        String wareHouse = req.getRequest().getWareHouse();
        List<DCP_LocationDeleteReq.LocationList> locationList = req.getRequest().getLocationList();
        for (DCP_LocationDeleteReq.LocationList location : locationList){

            //TEMPLATEID
            DelBean db1 = new DelBean("DCP_LOCATION");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("ORGANIZATIONNO", new DataValue(orgNo, Types.VARCHAR));
            db1.addCondition("WAREHOUSE", new DataValue(wareHouse, Types.VARCHAR));
            db1.addCondition("LOCATION", new DataValue(location.getLocation(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

        }


        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_LocationDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_LocationDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_LocationDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_LocationDeleteReq req) throws Exception {
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
    protected TypeToken<DCP_LocationDeleteReq> getRequestType() {
        return new TypeToken<DCP_LocationDeleteReq>() {
        };
    }

    @Override
    protected DCP_LocationDeleteRes getResponseType() {
        return new DCP_LocationDeleteRes();
    }
}

