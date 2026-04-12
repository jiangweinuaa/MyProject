package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockTaskDeleteReq;
import com.dsc.spos.json.cust.res.DCP_StockTaskDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_StockTaskDelete extends SPosAdvanceService<DCP_StockTaskDeleteReq, DCP_StockTaskDeleteRes>
{
    @Override
    protected boolean isVerifyFail(DCP_StockTaskDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String stockTaskNO = req.getRequest().getStockTaskNo();
        if (Check.Null(stockTaskNO)) {
            isFail = true;
            errMsg.append("盘点任务单单号不可为空值, ");
        }

        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected void processDUID(DCP_StockTaskDeleteReq req, DCP_StockTaskDeleteRes res) throws Exception {
        // TODO Auto-generated method stub
        String eId = req.geteId();
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String stockTaskNO = req.getRequest().getStockTaskNo();

        String sql="select * from DCP_STOCKTASK where eid='"+eId+"' and organizationno='"+organizationNO+"' and stocktaskno='"+stockTaskNO+"'  ";
        List<Map<String, Object>> list = this.doQueryData(sql, null);
        if(CollUtil.isNotEmpty(list)){

            String status = list.get(0).get("STATUS").toString();
            if(!status.equals("0")){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态<>新建,不可更改！");
            }

            DelBean db1 = new DelBean("DCP_STOCKTASK");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("stockTaskNO", new DataValue(stockTaskNO, Types.VARCHAR));
            db1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            DelBean db2 = new DelBean("DCP_STOCKTASK_DETAIL");
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db2.addCondition("stockTaskNO", new DataValue(stockTaskNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));

            DelBean db3 = new DelBean("DCP_STOCKTASK_ORG");
            db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db3.addCondition("stockTaskNO", new DataValue(stockTaskNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db3));

        }else{
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "没有可删除的任务单！");

        }

        this.doExecuteDataToDB();

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockTaskDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockTaskDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockTaskDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected TypeToken<DCP_StockTaskDeleteReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_StockTaskDeleteReq>(){};
    }

    @Override
    protected DCP_StockTaskDeleteRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_StockTaskDeleteRes();
    }

}
