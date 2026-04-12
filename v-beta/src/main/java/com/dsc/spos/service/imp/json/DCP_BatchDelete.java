package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BatchDeleteReq;
import com.dsc.spos.json.cust.res.DCP_BatchDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_BatchDelete  extends SPosAdvanceService<DCP_BatchDeleteReq, DCP_BatchDeleteRes> {

    @Override
    protected void processDUID(DCP_BatchDeleteReq req, DCP_BatchDeleteRes res) throws Exception {

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        if(Check.Null(req.getRequest().getFeatureNo())){
            req.getRequest().setFeatureNo(" ");
        }

        //● 批号存在库存交易记录，不允许删除批号！
        //● 当前组织不属于批号归属组织不允许删除！
        String batchStockSql="select * from MES_BATCH_STOCK_DETAIL a" +
                " where a.eid='"+eId+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                " and a.pluno='"+req.getRequest().getPluNo()+"' and a.featureno='"+req.getRequest().getFeatureNo()+"'" +
                " and a.batchno='"+req.getRequest().getBatchNo()+"' ";
        List<Map<String, Object>> batchStockList = this.doQueryData(batchStockSql, null);
        if(batchStockList.size()>0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "批号存在库存交易记录，不允许删除批号！");
        }

        //TEMPLATEID
        DelBean db1 = new DelBean("MES_BATCH");
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));

        db1.addCondition("PLUNO", new DataValue(req.getRequest().getPluNo(), Types.VARCHAR));
        db1.addCondition("FEATURENO", new DataValue(req.getRequest().getFeatureNo(), Types.VARCHAR));
        db1.addCondition("BATCHBO", new DataValue(req.getRequest().getBatchNo(), Types.VARCHAR));

        this.addProcessData(new DataProcessBean(db1));



        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BatchDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BatchDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BatchDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_BatchDeleteReq req) throws Exception {
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
    protected TypeToken<DCP_BatchDeleteReq> getRequestType() {
        return new TypeToken<DCP_BatchDeleteReq>() {
        };
    }

    @Override
    protected DCP_BatchDeleteRes getResponseType() {
        return new DCP_BatchDeleteRes();
    }
}

