package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProductGroupCreateReq;
import com.dsc.spos.json.cust.res.DCP_ProductGroupCreateRes;
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

public class DCP_ProductGroupCreate extends SPosAdvanceService<DCP_ProductGroupCreateReq, DCP_ProductGroupCreateRes> {

    @Override
    protected void processDUID(DCP_ProductGroupCreateReq req, DCP_ProductGroupCreateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_ProductGroupCreateReq.LevelRequest request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());

        String valSql="select * from MES_PRODUCT_GROUP a where a.eid='"+eId+"' and a.pgroupno='"+ request.getPGroupNo()+"'";
        List<Map<String, Object>> valList = this.doQueryData(valSql, null);

        if(CollUtil.isNotEmpty(valList)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500,"编号已存在");
        }

        ColumnDataValue mainColumns=new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(eId));
        mainColumns.add("PGROUPNO", DataValues.newString(request.getPGroupNo()));
        mainColumns.add("PGROUPNAME", DataValues.newString(request.getPGroupName()));
        mainColumns.add("DEPARTID", DataValues.newString(request.getDepartId()));
        mainColumns.add("MEMO", DataValues.newString(request.getMemo()));
        mainColumns.add("STATUS", DataValues.newString(request.getStatus()));
        mainColumns.add("CREATEOPID", DataValues.newString(employeeNo));
        mainColumns.add("CREATEOPNAME", DataValues.newString(req.getEmployeeName()));
        mainColumns.add("CREATETIME", DataValues.newDate(createTime));


        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib=new InsBean("MES_PRODUCT_GROUP",mainColumnNames);
        ib.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib));


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProductGroupCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProductGroupCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProductGroupCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ProductGroupCreateReq req) throws Exception {
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
    protected TypeToken<DCP_ProductGroupCreateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_ProductGroupCreateReq>(){};
    }

    @Override
    protected DCP_ProductGroupCreateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_ProductGroupCreateRes();
    }

}


