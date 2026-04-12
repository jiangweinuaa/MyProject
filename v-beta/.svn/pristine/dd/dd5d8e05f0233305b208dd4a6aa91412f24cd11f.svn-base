package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_TaxSubjectDeleteReq;
import com.dsc.spos.json.cust.res.DCP_TaxSubjectDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_TaxSubjectDelete  extends SPosAdvanceService<DCP_TaxSubjectDeleteReq, DCP_TaxSubjectDeleteRes> {

    @Override
    protected void processDUID(DCP_TaxSubjectDeleteReq req, DCP_TaxSubjectDeleteRes res) throws Exception {

        String eId = req.geteId();
        List<DCP_TaxSubjectDeleteReq.taxCodeList> taxCodeList = req.getRequest().getTaxCodeList();

        if(CollUtil.isNotEmpty(taxCodeList)) {
            for (DCP_TaxSubjectDeleteReq.taxCodeList taxCodeList1 : taxCodeList) {
                DelBean db1 = new DelBean("DCP_TAXSUBJECT");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("ACCOUNTID", new DataValue(req.getRequest().getAccountId(), Types.VARCHAR));
                db1.addCondition("COAREFID", new DataValue(req.getRequest().getCoaRefID(), Types.VARCHAR));

                db1.addCondition("TAXCODE", new DataValue(taxCodeList1.getTaxCode(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));
            }
        }else{
            DelBean db1 = new DelBean("DCP_TAXSUBJECT");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("ACCOUNTID", new DataValue(req.getRequest().getAccountId(), Types.VARCHAR));
            db1.addCondition("COAREFID", new DataValue(req.getRequest().getCoaRefID(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));
        }



        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_TaxSubjectDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_TaxSubjectDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_TaxSubjectDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_TaxSubjectDeleteReq req) throws Exception {
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
    protected TypeToken<DCP_TaxSubjectDeleteReq> getRequestType() {
        return new TypeToken<DCP_TaxSubjectDeleteReq>() {
        };
    }

    @Override
    protected DCP_TaxSubjectDeleteRes getResponseType() {
        return new DCP_TaxSubjectDeleteRes();
    }
}

