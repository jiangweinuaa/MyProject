package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ROrderUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ROrderUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCP_ROrderUpdate  extends SPosAdvanceService<DCP_ROrderUpdateReq, DCP_ROrderUpdateRes> {

    @Override
    protected void processDUID(DCP_ROrderUpdateReq req, DCP_ROrderUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_ROrderUpdateReq.levelRequest request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String billNo = request.getROrderNo();

        DelBean db2 = new DelBean("DCP_RORDER_DETAIL");
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        db2.addCondition("RORDERNO", new DataValue(billNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));


        UptBean ub1 = new UptBean("DCP_RORDER");
        ub1.addUpdateValue("BDATE", new DataValue(request.getBDate(), Types.VARCHAR));
        ub1.addUpdateValue("RDATE", new DataValue(request.getRDate(), Types.VARCHAR));
        ub1.addUpdateValue("RDAYS", new DataValue(request.getRDays(), Types.VARCHAR));
        ub1.addUpdateValue("EMPLOYEEID", new DataValue(employeeNo, Types.VARCHAR));
        ub1.addUpdateValue("DEPARTID", new DataValue(departmentNo, Types.VARCHAR));
        ub1.addUpdateValue("TOTCQTY", new DataValue(request.getTotCqty(), Types.VARCHAR));
        ub1.addUpdateValue("TOTPQTY", new DataValue(request.getTotPqty(), Types.VARCHAR));
        ub1.addUpdateValue("MEMO", new DataValue(request.getMemo(), Types.VARCHAR));
        ub1.addUpdateValue("STATUS", new DataValue(request.getStatus(), Types.VARCHAR));
        ub1.addUpdateValue("MODIFYBY", new DataValue(req.getOpNO(), Types.VARCHAR));
        ub1.addUpdateValue("MODIFYTIME", new DataValue(createTime, Types.DATE));

        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("RORDERNO", new DataValue(billNo, Types.VARCHAR));
        ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));

        List<DCP_ROrderUpdateReq.Detail> datas = request.getDetail();
        int dataItems=0;
        for (DCP_ROrderUpdateReq.Detail data : datas){
            ColumnDataValue detailColumns=new ColumnDataValue();
            detailColumns.add("EID", DataValues.newString(eId));
            detailColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
            detailColumns.add("RORDERNO", DataValues.newString(billNo));
            detailColumns.add("ITEM", DataValues.newString(data.getItem()));
            detailColumns.add("PLUBARCODE", DataValues.newString(data.getPluBarcode()));
            detailColumns.add("PLUNO", DataValues.newString(data.getPluNo()));
            detailColumns.add("FEATURENO", DataValues.newString(data.getFeatureNo()));
            detailColumns.add("PUNIT", DataValues.newString(data.getPUnit()));
            detailColumns.add("PQTY", DataValues.newString(data.getPQty()));
            detailColumns.add("FORECASTQTY", DataValues.newString(data.getForecastQty()));
            detailColumns.add("DAILYQTY", DataValues.newString(data.getAvgDeliverQty()));
            detailColumns.add("DAILYPQTY", DataValues.newString(data.getDailyPqty()));
            detailColumns.add("MINQTY", DataValues.newString(data.getMinQty()));
            detailColumns.add("MULQTY", DataValues.newString(data.getMulQty()));
            detailColumns.add("SAFEQTY", DataValues.newString(data.getSafeQty()));
            detailColumns.add("STOCKQTY", DataValues.newString(data.getStockQty()));
            detailColumns.add("BASEUNIT", DataValues.newString(data.getBaseUnit()));
            detailColumns.add("BASEQTY", DataValues.newString(data.getBaseQty()));
            detailColumns.add("UNITRATIO", DataValues.newString(data.getUnitRatio()));
            detailColumns.add("STATUS", DataValues.newString(data.getStatus()));
            detailColumns.add("MEMO", DataValues.newString(data.getMemo()));

            String[] detailColumnNames =detailColumns.getColumns().toArray(new String[0]);
            DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
            InsBean ibDetail=new InsBean("DCP_RORDER_DETAIL",detailColumnNames);
            ibDetail.addValues(detailDataValues);
            this.addProcessData(new DataProcessBean(ibDetail));
        }


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ROrderUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ROrderUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ROrderUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ROrderUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_ROrderUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_ROrderUpdateReq>(){};
    }

    @Override
    protected DCP_ROrderUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_ROrderUpdateRes();
    }

}


