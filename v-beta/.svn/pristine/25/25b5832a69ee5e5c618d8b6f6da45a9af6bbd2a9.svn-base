package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_WMSPGoodsDeleteReq;
import com.dsc.spos.json.cust.req.DCP_WMSPGoodsDeleteReq.levelId;
import com.dsc.spos.json.cust.req.DCP_WMSPGoodsDeleteReq.levelRequest;
import com.dsc.spos.json.cust.res.DCP_WMSPGoodsDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public  class DCP_WMSPGoodsDelete extends SPosAdvanceService<DCP_WMSPGoodsDeleteReq, DCP_WMSPGoodsDeleteRes> {
    @Override
    protected void processDUID(DCP_WMSPGoodsDeleteReq req, DCP_WMSPGoodsDeleteRes res) throws Exception {
        String eId = req.geteId();
        levelRequest request = req.getRequest();

        List<levelId> idList = request.getIdList();
        for (levelId elm : idList)
        {
            DelBean db1 = new DelBean("DCP_WMSPGOODS");
            db1.addCondition("ID", new DataValue(elm.getId(), Types.VARCHAR));
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));
            DelBean db2 = new DelBean("DCP_WMSPGOODS_SPEC");
            db2.addCondition("ID", new DataValue(elm.getId(), Types.VARCHAR));
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));
            DelBean db3 = new DelBean("DCP_WMSPGOODS_ATTR");
            db3.addCondition("ID", new DataValue(elm.getId(), Types.VARCHAR));
            db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db3));
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_WMSPGoodsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WMSPGoodsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WMSPGoodsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_WMSPGoodsDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("request不能为空值,");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        levelRequest request = req.getRequest();
        List<levelId> idList = request.getIdList();
        if (idList==null||idList.isEmpty())
        {
            errMsg.append("删除列表不能为空值,");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        for (levelId par : idList)
        {
            if(Check.Null(par.getId())){
                errMsg.append("商品ID不能为空值,");
                isFail = true;

            }
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_WMSPGoodsDeleteReq> getRequestType() {
        return new TypeToken<DCP_WMSPGoodsDeleteReq>(){};
    }

    @Override
    protected DCP_WMSPGoodsDeleteRes getResponseType() {
        return new DCP_WMSPGoodsDeleteRes();
    }


}
