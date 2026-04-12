package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_RouteCreateReq;
import com.dsc.spos.json.cust.res.DCP_RouteCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_RouteCreate extends SPosAdvanceService<DCP_RouteCreateReq, DCP_RouteCreateRes> {

    @Override
    protected void processDUID(DCP_RouteCreateReq req, DCP_RouteCreateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_RouteCreateReq.levelRequest request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        String routeNo = req.getRequest().getRouteNo();

        String va1Sql="select * from mes_route a where a.eid='"+eId+"' and a.routeno='"+routeNo+"'";
        List<Map<String, Object>> vaList = this.doQueryData(va1Sql, null);
        if(CollUtil.isNotEmpty(vaList)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "编号重复！");
        }

        //机构路线唯一性：机构编号仅能存在于一条路线中，同一类型的机构编号不允许多条路线
        if(CollUtil.isEmpty(req.getRequest().getDetail())){
            MyCommon mc = new MyCommon();
            Map<String,String> map = new HashMap<>();
            String sJoinCode = "";
            String sJoinRouteType = "";
            for(DCP_RouteCreateReq.Detail s :req.getRequest().getDetail()) {
                sJoinCode += s.getCode()+",";
                sJoinRouteType += s.getRouteType()+",";
            }
            map.put("ROUTETYPE", sJoinRouteType);
            map.put("CODE", sJoinCode);
            String withRc = mc.getFormatSourceMultiColWith(map);
            if(Check.NotNull(withRc)){
                String valSql=" with p as ("+withRc+") " +
                        " select * from mes_route_detail a " +
                        " inner join p on p.code=a.code and a.routetype=p.routetype" +
                        " where a.eid='"+eId+"' ";
                List<Map<String, Object>> list = this.doQueryData(valSql, null);
                if(CollUtil.isNotEmpty(list)){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "机构路线唯一性：机构编号仅能存在于一条路线中，同一类型的机构编号不允许多条路线");
                }
            }
        }


        ColumnDataValue mainColumns=new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(eId));
        mainColumns.add("ROUTENO", DataValues.newString(routeNo));
        mainColumns.add("ROUTENAME", DataValues.newString(request.getRouteName()));
        mainColumns.add("STATUS", DataValues.newInteger(request.getStatus()));
        mainColumns.add("LASTMODITIME", DataValues.newDate(createTime));
        mainColumns.add("MEMO", DataValues.newString(request.getMemo()));
        mainColumns.add("CREATEOPID", DataValues.newString(employeeNo));
        mainColumns.add("CREATEDEPTID", DataValues.newString(departmentNo));
        mainColumns.add("CREATETIME", DataValues.newDate(createTime));
        mainColumns.add("LASTMODIOPID", DataValues.newString(employeeNo));
        mainColumns.add("LASTMODITIME", DataValues.newDate(createTime));


        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib=new InsBean("MES_ROUTE",mainColumnNames);
        ib.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib));

        List<DCP_RouteCreateReq.Detail> datas = request.getDetail();
        for (DCP_RouteCreateReq.Detail data : datas){
            ColumnDataValue detailColumns=new ColumnDataValue();
            detailColumns.add("EID", DataValues.newString(eId));
            detailColumns.add("ROUTENO", DataValues.newString(routeNo));
            detailColumns.add("ROUTETYPE", DataValues.newInteger(data.getRouteType()));
            detailColumns.add("CODE", DataValues.newString(data.getCode()));
            detailColumns.add("NAME", DataValues.newString(data.getName()));
            detailColumns.add("ADDRESS", DataValues.newString(data.getAddress()));
            detailColumns.add("STATUS", DataValues.newString(data.getStatus()));
            detailColumns.add("SORTING", DataValues.newString(data.getSorting()));

            String[] detailColumnNames =detailColumns.getColumns().toArray(new String[0]);
            DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
            InsBean ibDetail=new InsBean("MES_ROUTE_DETAIL",detailColumnNames);
            ibDetail.addValues(detailDataValues);
            this.addProcessData(new DataProcessBean(ibDetail));
        }


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_RouteCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_RouteCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_RouteCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_RouteCreateReq req) throws Exception {
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
    protected TypeToken<DCP_RouteCreateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_RouteCreateReq>(){};
    }

    @Override
    protected DCP_RouteCreateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_RouteCreateRes();
    }

}


