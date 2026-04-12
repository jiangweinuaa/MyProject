package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_VendorAdjDeleteReq;
import com.dsc.spos.json.cust.res.DCP_VendorAdjDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_VendorAdjDelete extends SPosAdvanceService<DCP_VendorAdjDeleteReq, DCP_VendorAdjDeleteRes> {

    @Override
    protected void processDUID(DCP_VendorAdjDeleteReq req, DCP_VendorAdjDeleteRes res) throws Exception {

        String eId = req.geteId();
        String organizationNo = req.getRequest().getOrganizationNo();
        String adjustNO = req.getRequest().getAdjustNO();

        String validSql="select * from DCP_VENDORADJ a where a.eid='"+eId+"'" +
                " and a.organizationNo='"+organizationNo+"' and a.adjustno='"+adjustNO+"' ";
        List<Map<String, Object>> validData = this.doQueryData(validSql, null);

        if(validData.size()>0){
            String status = validData.get(0).get("STATUS").toString();
            if(Check.Null(status)||"0".equals(status)){//新建可删
                DelBean db1 = new DelBean("DCP_VENDORADJ");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
                db1.addCondition("ADJUSTNO", new DataValue(adjustNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                DelBean db2 = new DelBean("DCP_VENDORADJ_DETAIL");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
                db2.addCondition("ADJUSTNO", new DataValue(adjustNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));
            }
        }


        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_VendorAdjDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_VendorAdjDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_VendorAdjDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_VendorAdjDeleteReq req) throws Exception {
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
    protected TypeToken<DCP_VendorAdjDeleteReq> getRequestType() {
        return new TypeToken<DCP_VendorAdjDeleteReq>() {
        };
    }

    @Override
    protected DCP_VendorAdjDeleteRes getResponseType() {
        return new DCP_VendorAdjDeleteRes();
    }
}

