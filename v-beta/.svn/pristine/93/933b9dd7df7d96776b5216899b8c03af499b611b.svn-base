package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_WarehouseRangeEnableReq;
import com.dsc.spos.json.cust.res.DCP_WarehouseRangeEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCP_WarehouseRangeEnable extends SPosAdvanceService<DCP_WarehouseRangeEnableReq, DCP_WarehouseRangeEnableRes>
{

    @Override
    protected void processDUID(DCP_WarehouseRangeEnableReq req, DCP_WarehouseRangeEnableRes res) throws Exception
    {
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String employeeNo = req.getEmployeeNo();
        List<DCP_WarehouseRangeEnableReq.RangeList> rangeList = req.getRequest().getRangeList();

        if(CollUtil.isNotEmpty(rangeList)){
            for(DCP_WarehouseRangeEnableReq.RangeList range:rangeList){
                UptBean ub1 = new UptBean("DCP_WAREHOUSE_RANGE");
                if("1".equals(req.getRequest().getOpType())) {
                    ub1.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
                }
                if("2".equals(req.getRequest().getOpType())) {
                    ub1.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
                }
                ub1.addUpdateValue("LASTMODIOPID", new DataValue(employeeNo, Types.VARCHAR));
                ub1.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));

                //condition
                ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                ub1.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
                ub1.addCondition("WAREHOUSE", new DataValue(req.getRequest().getWarehouseNo(), Types.VARCHAR));
                ub1.addCondition("TYPE", new DataValue(range.getType(), Types.VARCHAR));
                ub1.addCondition("CODE", new DataValue(range.getCode(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));
            }
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_WarehouseRangeEnableReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WarehouseRangeEnableReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WarehouseRangeEnableReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_WarehouseRangeEnableReq req) throws Exception
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
    protected TypeToken<DCP_WarehouseRangeEnableReq> getRequestType()
    {
        return new TypeToken<DCP_WarehouseRangeEnableReq>(){};
    }

    @Override
    protected DCP_WarehouseRangeEnableRes getResponseType()
    {
        return new DCP_WarehouseRangeEnableRes();
    }


}
