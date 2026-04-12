package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ROrderCreateReq;
import com.dsc.spos.json.cust.res.DCP_ROrderCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCP_ROrderCreate  extends SPosAdvanceService<DCP_ROrderCreateReq, DCP_ROrderCreateRes> {

    @Override
    protected void processDUID(DCP_ROrderCreateReq req, DCP_ROrderCreateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_ROrderCreateReq.levelRequest request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String billNo = this.getOrderNO(req, "DLXQ");

        ColumnDataValue mainColumns=new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(eId));
        mainColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));

        mainColumns.add("RORDERNO", DataValues.newString(billNo));
        mainColumns.add("BDATE", DataValues.newString(request.getBDate()));
        mainColumns.add("RDATE", DataValues.newString(request.getRDate()));
        mainColumns.add("RDAYS", DataValues.newString(request.getRDays()));
        mainColumns.add("EMPLOYEEID", DataValues.newString(request.getEmployeeId()));
        mainColumns.add("DEPARTID", DataValues.newString(request.getDepartId()));
        mainColumns.add("TOTCQTY", DataValues.newDecimal(request.getTotCqty()));
        mainColumns.add("TOTPQTY", DataValues.newDecimal(request.getTotPqty()));
        mainColumns.add("MEMO", DataValues.newString(request.getMemo()));
        mainColumns.add("STATUS", DataValues.newInteger(request.getStatus()));
        mainColumns.add("CREATEBY", DataValues.newString(req.getOpNO()));
        mainColumns.add("CREATEDEPTID", DataValues.newString(departmentNo));
        mainColumns.add("CREATETIME", DataValues.newDate(createTime));


        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib=new InsBean("DCP_RORDER",mainColumnNames);
        ib.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib));

        List<DCP_ROrderCreateReq.Detail> datas = request.getDetail();
        int dataItems=0;
        for (DCP_ROrderCreateReq.Detail data : datas){
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
        res.setROrderNo(billNo);
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ROrderCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ROrderCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ROrderCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ROrderCreateReq req) throws Exception {
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
    protected TypeToken<DCP_ROrderCreateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_ROrderCreateReq>(){};
    }

    @Override
    protected DCP_ROrderCreateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_ROrderCreateRes();
    }

}


