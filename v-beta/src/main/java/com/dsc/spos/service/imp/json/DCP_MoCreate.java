package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_MoCreateReq;
import com.dsc.spos.json.cust.res.DCP_MoCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCP_MoCreate extends SPosAdvanceService<DCP_MoCreateReq, DCP_MoCreateRes> {

    @Override
    protected void processDUID(DCP_MoCreateReq req, DCP_MoCreateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_MoCreateReq.level1Elm request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String moNo = this.getNormalNO(req, "MONO");

        ColumnDataValue mainColumns=new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(eId));
        mainColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));

        mainColumns.add("MONO", DataValues.newString(moNo));
        mainColumns.add("PGROUPNO", DataValues.newString(request.getPGroupNo()));
        mainColumns.add("BDATE", DataValues.newString(request.getBDate()));
        mainColumns.add("PDATE", DataValues.newString(request.getPDate()));
        mainColumns.add("LOAD_DOCNO", DataValues.newString(request.getLoadDocNo()));
        mainColumns.add("MEMO", DataValues.newString(request.getMemo()));
        mainColumns.add("STATUS", DataValues.newString(request.getStatus()));
        mainColumns.add("CREATEOPID", DataValues.newString(employeeNo));
        mainColumns.add("CREATETIME", DataValues.newDate(createTime));
        mainColumns.add("LASTMODIOPID", DataValues.newString(employeeNo));
        mainColumns.add("LASTMODITIME", DataValues.newDate(createTime));
        mainColumns.add("DEPARTID", DataValues.newString(request.getDepartId()));
        mainColumns.add("OTYPE", DataValues.newString(request.getOType()));
        mainColumns.add("OFNO", DataValues.newString(request.getOfNo()));
        mainColumns.add("SOURCEMONO", DataValues.newString(request.getSourceMoNo()));
        mainColumns.add("PRODTYPE", DataValues.newString(request.getProdType()));

        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib=new InsBean("MES_MO",mainColumnNames);
        ib.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib));

        List<DCP_MoCreateReq.Datas> datas = request.getDatas();
        int dataItems=0;
        for (DCP_MoCreateReq.Datas data : datas){
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
        res.setMoNo(moNo);
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_MoCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_MoCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_MoCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_MoCreateReq req) throws Exception {
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
    protected TypeToken<DCP_MoCreateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_MoCreateReq>(){};
    }

    @Override
    protected DCP_MoCreateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_MoCreateRes();
    }

}


