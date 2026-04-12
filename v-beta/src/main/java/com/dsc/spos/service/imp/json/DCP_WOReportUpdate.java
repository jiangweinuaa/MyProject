package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_WOReportUpdateReq;
import com.dsc.spos.json.cust.res.DCP_WOReportUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCP_WOReportUpdate extends SPosAdvanceService<DCP_WOReportUpdateReq, DCP_WOReportUpdateRes> {

    @Override
    protected void processDUID(DCP_WOReportUpdateReq req, DCP_WOReportUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_WOReportUpdateReq.LevelRequest request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());

        String orderNO = req.getRequest().getReportNo();

        DelBean db2 = new DelBean("DCP_WOREPORT_DETAIL");
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
        db2.addCondition("REPORTNO", new DataValue(orderNO, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        UptBean ub1 = new UptBean("DCP_WOREPORT");
        ub1.addUpdateValue("BDATE", DataValues.newDate(request.getBDate()));
        ub1.addUpdateValue("MEMO", DataValues.newString(request.getMemo()));
        ub1.addUpdateValue("STATUS", DataValues.newInteger(0));
        ub1.addUpdateValue("EMPLOYEEID", DataValues.newString(request.getEmployeeId()));
        ub1.addUpdateValue("DEPARTID", DataValues.newString(request.getDepartId()));

        ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
        ub1.addUpdateValue("LASTMODITIME", DataValues.newDate(createTime));
        ub1.addCondition("EID", DataValues.newString(eId));
        ub1.addCondition("SHOPID", DataValues.newString(req.getShopId()));
        ub1.addCondition("REPORTNO", DataValues.newString(orderNO));
        ub1.addCondition("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
        this.addProcessData(new DataProcessBean(ub1));



        if(CollUtil.isNotEmpty(request.getDatas())){
            for (DCP_WOReportUpdateReq.LevelElm2 data : request.getDatas()){

                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(eId));
                detailColumns.add("SHOPID", DataValues.newString(req.getShopId()));
                detailColumns.add("REPORTNO", DataValues.newString(orderNO));
                detailColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                detailColumns.add("ITEM", DataValues.newInteger(data.getItem()));
                detailColumns.add("OFNO", DataValues.newString(data.getOfNo()));
                detailColumns.add("OITEM", DataValues.newInteger(data.getOItem()));
                detailColumns.add("PLUNO", DataValues.newString(data.getPluNo()));
                detailColumns.add("FEATURENO", DataValues.newString(data.getFeatureNo()));
                detailColumns.add("PQTY", DataValues.newString(data.getPQty()));
                detailColumns.add("PUNIT", DataValues.newString(data.getPUnit()));
                detailColumns.add("EQUIPNO", DataValues.newString(data.getEquipNo()));
                detailColumns.add("EQTY", DataValues.newString(data.getEQty()));
                detailColumns.add("LABORTIME", DataValues.newString(data.getLaborTime()));
                detailColumns.add("MACHINETIME", DataValues.newString(data.getMachineTime()));
                detailColumns.add("MEMO", DataValues.newString(data.getMemo()));

                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailib=new InsBean("DCP_WOREPORT_DETAIL",detailColumnNames);
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
    protected List<InsBean> prepareInsertData(DCP_WOReportUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WOReportUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WOReportUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_WOReportUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_WOReportUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_WOReportUpdateReq>(){};
    }

    @Override
    protected DCP_WOReportUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_WOReportUpdateRes();
    }

}


