package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PurchaseApplyDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PurchaseApplyDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_PurchaseApplyDelete extends SPosAdvanceService<DCP_PurchaseApplyDeleteReq, DCP_PurchaseApplyDeleteRes> {

    @Override
    protected void processDUID(DCP_PurchaseApplyDeleteReq req, DCP_PurchaseApplyDeleteRes res) throws Exception {

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String billNo = req.getRequest().getBillNo();
        String sql="select * from DCP_PURCHASEAPPLY a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.billno='"+billNo+"'";
        List<Map<String, Object>> list = this.doQueryData(sql,null);
        if(list.size()>0){
            String status=list.get(0).get("STATUS").toString();
            if("0".equals(status)){
                DelBean db1 = new DelBean("DCP_PURCHASEAPPLY");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                db1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                DelBean db2 = new DelBean("DCP_PURCHASEAPPLY_DETAIL");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                db2.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));
            }
        }



        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurchaseApplyDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurchaseApplyDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurchaseApplyDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PurchaseApplyDeleteReq req) throws Exception {
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
    protected TypeToken<DCP_PurchaseApplyDeleteReq> getRequestType() {
        return new TypeToken<DCP_PurchaseApplyDeleteReq>() {
        };
    }

    @Override
    protected DCP_PurchaseApplyDeleteRes getResponseType() {
        return new DCP_PurchaseApplyDeleteRes();
    }
}

