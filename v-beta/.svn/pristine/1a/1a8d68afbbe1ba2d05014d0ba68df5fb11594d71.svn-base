package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_RouteUpdateReq;
import com.dsc.spos.json.cust.res.DCP_RouteUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_RouteUpdate extends SPosAdvanceService<DCP_RouteUpdateReq, DCP_RouteUpdateRes> {

    @Override
    protected void processDUID(DCP_RouteUpdateReq req, DCP_RouteUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_RouteUpdateReq.levelRequest request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String routeNo =req.getRequest().getRouteNo();

        //机构路线唯一性：机构编号仅能存在于一条路线中，同一类型的机构编号不允许多条路线
        if(CollUtil.isEmpty(req.getRequest().getDetail())){
            MyCommon mc = new MyCommon();
            Map<String,String> map = new HashMap<>();
            String sJoinCode = "";
            String sJoinRouteType = "";
            for(DCP_RouteUpdateReq.Detail s :req.getRequest().getDetail()) {
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
                        " where a.eid='"+eId+"' and a.routeno!='"+routeNo+"' ";
                List<Map<String, Object>> list = this.doQueryData(valSql, null);
                if(CollUtil.isNotEmpty(list)){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "机构路线唯一性：机构编号仅能存在于一条路线中，同一类型的机构编号不允许多条路线");
                }
            }
        }



        DelBean db2 = new DelBean("MES_ROUTE_DETAIL");
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("ROUTENO", new DataValue(routeNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        UptBean ub1 = new UptBean("MES_ROUTE");
        ub1.addUpdateValue("ROUTENAME", new DataValue(request.getRouteName(), Types.VARCHAR));
        ub1.addUpdateValue("STATUS", new DataValue(request.getStatus(), Types.VARCHAR));
        ub1.addUpdateValue("MEMO", new DataValue(request.getMemo(), Types.VARCHAR));
        ub1.addUpdateValue("LASTMODIOPID", new DataValue(employeeNo, Types.VARCHAR));
        ub1.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));

        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("ROUTENO", new DataValue(routeNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));

        List<DCP_RouteUpdateReq.Detail> datas = request.getDetail();
        int dataItems=0;
        for (DCP_RouteUpdateReq.Detail data : datas){
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
    protected List<InsBean> prepareInsertData(DCP_RouteUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_RouteUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_RouteUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_RouteUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_RouteUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_RouteUpdateReq>(){};
    }

    @Override
    protected DCP_RouteUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_RouteUpdateRes();
    }

}


