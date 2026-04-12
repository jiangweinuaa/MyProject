package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_MoConfirmReq;
import com.dsc.spos.json.cust.res.DCP_MoConfirmRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_MoConfirm extends SPosAdvanceService<DCP_MoConfirmReq, DCP_MoConfirmRes>
{

    @Override
    protected void processDUID(DCP_MoConfirmReq req, DCP_MoConfirmRes res) throws Exception
    {

        String eId = req.geteId();
        String moNo = req.getRequest().getMoNo();
        String organizationNO = req.getOrganizationNO();
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());

        String vaSql="select * from mes_mo a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.mono='"+moNo+"'";
        List<Map<String, Object>> list = this.doQueryData(vaSql, null);
        if(list.size()>0){
            String status = list.get(0).get("STATUS").toString();
            if(status.equals("0")){
                UptBean ub1 = new UptBean("MES_MO");
                ub1.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));
                ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
                ub1.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));
                ub1.addUpdateValue("ACCOUNTOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
                ub1.addUpdateValue("ACCOUNTTIME", new DataValue(createTime, Types.DATE));
                ub1.addUpdateValue("ACCOUNTOPNAME", new DataValue(req.getEmployeeName(), Types.VARCHAR));

                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("MONO", new DataValue(moNo, Types.VARCHAR));
                ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));
            }else {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "该工单已确认，请勿重复确认");
            }
        }


        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_MoConfirmReq req) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_MoConfirmReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_MoConfirmReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_MoConfirmReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_MoConfirmReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_MoConfirmReq>(){};
    }

    @Override
    protected DCP_MoConfirmRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_MoConfirmRes();
    }

}

