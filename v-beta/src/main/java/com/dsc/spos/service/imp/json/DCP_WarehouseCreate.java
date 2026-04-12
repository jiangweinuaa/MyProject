package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_WarehouseCreateReq;
import com.dsc.spos.json.cust.res.DCP_WarehouseCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_WarehouseCreate extends SPosAdvanceService<DCP_WarehouseCreateReq, DCP_WarehouseCreateRes> {

    @Override
    protected void processDUID(DCP_WarehouseCreateReq req, DCP_WarehouseCreateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        //String organizationNO = req.getOrganizationNO();
        if(Check.Null(req.getRequest().getOrganizationNo())){
            req.getRequest().setOrganizationNo(req.getOrganizationNO());
        }
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_WarehouseCreateReq.levelElm request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());

        String wSql="select * from dcp_warehouse where eid='"+eId+"' and organizationno='"+req.getRequest().getOrganizationNo()+"' " +
                " and warehouse='"+request.getWarehouseNo()+"' ";
        List<Map<String, Object>> wList = this.doQueryData(wSql, null);
        if(CollUtil.isNotEmpty(wList)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "编号重复".toString());
        }


        ColumnDataValue mainColumns=new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(eId));
        mainColumns.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrganizationNo()));
        mainColumns.add("WAREHOUSE", DataValues.newString(request.getWarehouseNo()));
        mainColumns.add("WAREHOUSE_TYPE", DataValues.newString(request.getWarehouseType()));
        mainColumns.add("IS_COST", DataValues.newString(request.getIsCost()));
        mainColumns.add("ISLOCATION", DataValues.newString(request.getIsLocation()));
        mainColumns.add("ISCHECKSTOCK", DataValues.newString(request.getIsCheckStock()));
        mainColumns.add("STATUS", DataValues.newString(request.getStatus()));
        mainColumns.add("CREATEBY", DataValues.newString(req.getOpNO()));
        mainColumns.add("CREATEDEPTID", DataValues.newString(departmentNo));
        mainColumns.add("CREATE_DTIME", DataValues.newDate(createTime));
        mainColumns.add("MODIFYBY", DataValues.newString(req.getOpNO()));
        mainColumns.add("MODIFY_DTIME", DataValues.newDate(createTime));


        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib=new InsBean("DCP_WAREHOUSE",mainColumnNames);
        ib.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib));

        List<DCP_WarehouseCreateReq.Lang> wareNamelang = request.getWareNamelang();

        if(CollUtil.isNotEmpty(wareNamelang)){
            for (DCP_WarehouseCreateReq.Lang detail : wareNamelang){
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
    protected List<InsBean> prepareInsertData(DCP_WarehouseCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WarehouseCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WarehouseCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_WarehouseCreateReq req) throws Exception {
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
    protected TypeToken<DCP_WarehouseCreateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_WarehouseCreateReq>(){};
    }

    @Override
    protected DCP_WarehouseCreateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_WarehouseCreateRes();
    }

}


