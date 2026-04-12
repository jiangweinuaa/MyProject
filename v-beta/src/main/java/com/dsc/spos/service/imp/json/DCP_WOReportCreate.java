package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_WOReportCreateReq;
import com.dsc.spos.json.cust.res.DCP_WOReportCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_WOReportCreate extends SPosAdvanceService<DCP_WOReportCreateReq, DCP_WOReportCreateRes> {

    @Override
    protected void processDUID(DCP_WOReportCreateReq req, DCP_WOReportCreateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_WOReportCreateReq.LevelRequest request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());

        String orderNO = this.getOrderNO(req, "GDBG");

        ColumnDataValue mainColumns=new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(eId));
        mainColumns.add("SHOPID", DataValues.newString(req.getShopId()));
        mainColumns.add("REPORTNO", DataValues.newString(orderNO));
        mainColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
        mainColumns.add("BDATE", DataValues.newDate(request.getBDate()));
        mainColumns.add("MEMO", DataValues.newString(request.getMemo()));
        mainColumns.add("STATUS", DataValues.newInteger(0));
        mainColumns.add("EMPLOYEEID", DataValues.newString(request.getEmployeeId()));
        mainColumns.add("DEPARTID", DataValues.newString(request.getDepartId()));
        mainColumns.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
        mainColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));
        mainColumns.add("CREATETIME", DataValues.newDate(createTime));
        mainColumns.add("LASTMODIOPID", DataValues.newString(req.getOpNO()));
        mainColumns.add("LASTMODITIME", DataValues.newDate(createTime));


        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib=new InsBean("DCP_WOREPORT",mainColumnNames);
        ib.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib));

        if(CollUtil.isNotEmpty(request.getDatas())){
            for (DCP_WOReportCreateReq.LevelElm2 data : request.getDatas()){

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


        DCP_WOReportCreateRes.Datas datas = res.new Datas();
        datas.setReportNo(orderNO);

        this.doExecuteDataToDB();
        res.setDatas(datas);
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_WOReportCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WOReportCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WOReportCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_WOReportCreateReq req) throws Exception {
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
    protected TypeToken<DCP_WOReportCreateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_WOReportCreateReq>(){};
    }

    @Override
    protected DCP_WOReportCreateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_WOReportCreateRes();
    }

}


