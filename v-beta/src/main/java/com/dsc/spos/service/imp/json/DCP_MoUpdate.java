package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_MoUpdateReq;
import com.dsc.spos.json.cust.res.DCP_MoUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCP_MoUpdate extends SPosAdvanceService<DCP_MoUpdateReq, DCP_MoUpdateRes> {

    @Override
    protected void processDUID(DCP_MoUpdateReq req, DCP_MoUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_MoUpdateReq.level1Elm request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        String moNo = request.getMoNo();

        DelBean db2 = new DelBean("MES_MO_DETAIL");
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        db2.addCondition("MONO", new DataValue(moNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        UptBean ub1 = new UptBean("MES_MO");
        ub1.addUpdateValue("PGROUPNO", new DataValue(request.getPGroupNo(), Types.VARCHAR));
        ub1.addUpdateValue("BDATE", new DataValue(request.getBDate(), Types.VARCHAR));
        ub1.addUpdateValue("LOAD_DOCNO", new DataValue(request.getLoadDocNo(), Types.VARCHAR));
        ub1.addUpdateValue("PDATE", new DataValue(request.getPDate(), Types.VARCHAR));
        ub1.addUpdateValue("MEMO", new DataValue(request.getMemo(), Types.VARCHAR));
        ub1.addUpdateValue("STATUS", new DataValue(request.getStatus(), Types.VARCHAR));
        ub1.addUpdateValue("DEPARTID", new DataValue(request.getDepartId(), Types.VARCHAR));
        ub1.addUpdateValue("OTYPE", new DataValue(request.getOType(), Types.VARCHAR));
        ub1.addUpdateValue("OFNO", new DataValue(request.getOfNo(), Types.VARCHAR));
        ub1.addUpdateValue("SOURCEMONO", new DataValue(request.getSourceMoNo(), Types.VARCHAR));
        ub1.addUpdateValue("PRODTYPE", new DataValue(request.getProdType(), Types.VARCHAR));
        ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
        ub1.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));

        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("MONO", new DataValue(moNo, Types.VARCHAR));
        ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));


        List<DCP_MoUpdateReq.Datas> datas = request.getDatas();
        int dataItems=0;
        for (DCP_MoUpdateReq.Datas data : datas){
            ColumnDataValue detailColumns=new ColumnDataValue();
            detailColumns.add("EID", DataValues.newString(eId));
            detailColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
            detailColumns.add("MONO", DataValues.newString(moNo));
            detailColumns.add("ITEM", DataValues.newString(data.getItem()));
            detailColumns.add("OITEM", DataValues.newString(data.getOItem()));
            detailColumns.add("SOURCEMOITEM", DataValues.newString(data.getSourceMoItem()));
            detailColumns.add("PLUNO", DataValues.newString(data.getPluNo()));
            detailColumns.add("FEATURENO", DataValues.newString(data.getFeatureNo()));
            detailColumns.add("PUNIT", DataValues.newString(data.getPUnit()));
            detailColumns.add("PQTY", DataValues.newString(data.getPQty()));
            detailColumns.add("BEGINDATE", DataValues.newString(data.getBeginDate()));
            detailColumns.add("ENDDATE", DataValues.newString(data.getEndDate()));
            detailColumns.add("BOMNO", DataValues.newString(data.getBomNo()));
            detailColumns.add("VERSIONNUM", DataValues.newString(data.getVersionNum()));
            detailColumns.add("PICKSTATUS", DataValues.newString(data.getPickStatus()));
            detailColumns.add("DISPATCHQTY", DataValues.newString(data.getDispatchQty()));
            detailColumns.add("DISPATCHSTATUS", DataValues.newString(data.getDispatchStatus()));
            detailColumns.add("MULQTY", DataValues.newString(data.getMulQty()));
            detailColumns.add("MINQTY", DataValues.newString(data.getMinQty()));

            String[] detailColumnNames =detailColumns.getColumns().toArray(new String[0]);
            DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
            InsBean ibDetail=new InsBean("MES_MO_DETAIL",detailColumnNames);
            ibDetail.addValues(detailDataValues);
            this.addProcessData(new DataProcessBean(ibDetail));
        }


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_MoUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_MoUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_MoUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_MoUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_MoUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_MoUpdateReq>(){};
    }

    @Override
    protected DCP_MoUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_MoUpdateRes();
    }

}


