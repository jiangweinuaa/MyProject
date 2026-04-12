package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_WarehouseRangeAddReq;
import com.dsc.spos.json.cust.res.DCP_WarehouseRangeAddRes;
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

public class DCP_WarehouseRangeAdd  extends SPosAdvanceService<DCP_WarehouseRangeAddReq, DCP_WarehouseRangeAddRes> {

    @Override
    protected void processDUID(DCP_WarehouseRangeAddReq req, DCP_WarehouseRangeAddRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_WarehouseRangeAddReq.LevelElm request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        if(Check.Null(request.getOrganizationNo())){
            request.setOrganizationNo(req.getOrganizationNO());
        }
        String organizationNO = request.getOrganizationNo();

        String wSql="select * from dcp_warehouse where eid='"+eId+"' and organizationno='"+organizationNO+"' " +
                " and warehouse='"+request.getWarehouseNo()+"' ";
        List<Map<String, Object>> wList = this.doQueryData(wSql, null);
        if(CollUtil.isEmpty(wList)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "仓库不存在".toString());
        }


        List<DCP_WarehouseRangeAddReq.RangeList> rangeList = request.getRangeList();

        if(CollUtil.isNotEmpty(rangeList)){
            for (DCP_WarehouseRangeAddReq.RangeList range : rangeList){

                String vSql="select * from DCP_WAREHOUSE_RANGE a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.warehouse='"+request.getWarehouseNo()+"'" +
                        " and a.type='"+range.getType()+"' and a.code='"+range.getCode()+"' ";
                List<Map<String, Object>> vList = this.doQueryData(vSql, null);
                if(CollUtil.isNotEmpty(vList)){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "仓库范围已存在".toString());
                }

                ColumnDataValue rangeColumns=new ColumnDataValue();
                rangeColumns.add("EID", DataValues.newString(eId));
                rangeColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                rangeColumns.add("WAREHOUSE", DataValues.newString(request.getWarehouseNo()));
                rangeColumns.add("TYPE", DataValues.newString(range.getType()));
                rangeColumns.add("CODE", DataValues.newString(range.getCode()));
                rangeColumns.add("STATUS", DataValues.newString(range.getStatus()));

                rangeColumns.add("CREATEOPID", DataValues.newString(employeeNo));
                rangeColumns.add("CREATEDEPTID", DataValues.newString(departmentNo));
                rangeColumns.add("CREATETIME", DataValues.newDate(createTime));
                rangeColumns.add("LASTMODIOPID", DataValues.newString(employeeNo));
                rangeColumns.add("LASTMODITIME", DataValues.newDate(createTime));


                String[] rangeColumnNames = rangeColumns.getColumns().toArray(new String[0]);
                DataValue[] rangeDataValues = rangeColumns.getDataValues().toArray(new DataValue[0]);
                InsBean rangeib=new InsBean("DCP_WAREHOUSE_RANGE",rangeColumnNames);
                rangeib.addValues(rangeDataValues);
                this.addProcessData(new DataProcessBean(rangeib));
            }
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_WarehouseRangeAddReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WarehouseRangeAddReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WarehouseRangeAddReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_WarehouseRangeAddReq req) throws Exception {
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
    protected TypeToken<DCP_WarehouseRangeAddReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_WarehouseRangeAddReq>(){};
    }

    @Override
    protected DCP_WarehouseRangeAddRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_WarehouseRangeAddRes();
    }

}


