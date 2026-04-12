package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_WarehouseUpdateReq;
import com.dsc.spos.json.cust.res.DCP_WarehouseUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_WarehouseUpdate extends SPosAdvanceService<DCP_WarehouseUpdateReq, DCP_WarehouseUpdateRes> {

    @Override
    protected void processDUID(DCP_WarehouseUpdateReq req, DCP_WarehouseUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        //String organizationNO = req.getOrganizationNO();
        if(Check.Null(req.getRequest().getOrganizationNo())){
            req.getRequest().setOrganizationNo(req.getOrganizationNO());
        }
        DCP_WarehouseUpdateReq.levelElm request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());

        DelBean db2 = new DelBean("DCP_WAREHOUSE_LANG");
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
        db2.addCondition("WAREHOUSE", new DataValue(request.getWarehouseNo(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        UptBean ub1 = new UptBean("DCP_WAREHOUSE");
        ub1.addUpdateValue("WAREHOUSE_TYPE", new DataValue(request.getWarehouseType(), Types.VARCHAR));
        ub1.addUpdateValue("IS_COST", new DataValue(request.getIsCost(), Types.VARCHAR));
        ub1.addUpdateValue("ISLOCATION", new DataValue(request.getIsLocation(), Types.VARCHAR));
        ub1.addUpdateValue("ISCHECKSTOCK", new DataValue(request.getIsCheckStock(), Types.VARCHAR));
        ub1.addUpdateValue("STATUS", new DataValue(request.getStatus(), Types.VARCHAR));
        ub1.addUpdateValue("MODIFYBY", new DataValue(req.getOpNO(), Types.VARCHAR));
        ub1.addUpdateValue("MODIFY_DTIME", new DataValue(createTime, Types.DATE));
        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
        ub1.addCondition("WAREHOUSE", new DataValue(request.getWarehouseNo(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));

        List<DCP_WarehouseUpdateReq.Lang> wareNamelang = request.getWareNamelang();

        if(CollUtil.isNotEmpty(wareNamelang)){
            for (DCP_WarehouseUpdateReq.Lang detail : wareNamelang){
                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(eId));
                detailColumns.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrganizationNo()));
                detailColumns.add("LANG_TYPE", DataValues.newString(detail.getLangType()));
                detailColumns.add("WAREHOUSE", DataValues.newString(request.getWarehouseNo()));
                detailColumns.add("WAREHOUSE_NAME", DataValues.newString(detail.getName()));
                detailColumns.add("STATUS", DataValues.newDecimal("100"));

                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailib=new InsBean("DCP_WAREHOUSE_LANG",detailColumnNames);
                detailib.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailib));
            }
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_WarehouseUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WarehouseUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WarehouseUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_WarehouseUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");


        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_WarehouseUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_WarehouseUpdateReq>(){};
    }

    @Override
    protected DCP_WarehouseUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_WarehouseUpdateRes();
    }

}


