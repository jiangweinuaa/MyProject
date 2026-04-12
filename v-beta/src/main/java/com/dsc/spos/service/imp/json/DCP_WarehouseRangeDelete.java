package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_WarehouseRangeDeleteReq;
import com.dsc.spos.json.cust.res.DCP_WarehouseRangeDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_WarehouseRangeDelete extends SPosAdvanceService<DCP_WarehouseRangeDeleteReq, DCP_WarehouseRangeDeleteRes>
{

    @Override
    protected void processDUID(DCP_WarehouseRangeDeleteReq req, DCP_WarehouseRangeDeleteRes res) throws Exception
    {
        List<DCP_WarehouseRangeDeleteReq.RangeList> rangeList = req.getRequest().getRangeList();

        if(CollUtil.isNotEmpty(rangeList)){
            for(DCP_WarehouseRangeDeleteReq.RangeList range:rangeList){
                DelBean db = new DelBean("DCP_WAREHOUSE_RANGE");
                db.addCondition("TYPE",  new DataValue(range.getType(), Types.VARCHAR));
                db.addCondition("CODE", new DataValue(range.getCode(), Types.VARCHAR));
                db.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
                db.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                db.addCondition("WAREHOUSE", new DataValue(req.getRequest().getWarehouseNo(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db));
            }
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_WarehouseRangeDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WarehouseRangeDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WarehouseRangeDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_WarehouseRangeDeleteReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_WarehouseRangeDeleteReq> getRequestType()
    {
        return new TypeToken<DCP_WarehouseRangeDeleteReq>(){};
    }

    @Override
    protected DCP_WarehouseRangeDeleteRes getResponseType()
    {
        return new DCP_WarehouseRangeDeleteRes();
    }


}
