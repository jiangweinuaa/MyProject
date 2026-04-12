package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_WOReportDeleteReq;
import com.dsc.spos.json.cust.res.DCP_WOReportDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_WOReportDelete  extends SPosAdvanceService<DCP_WOReportDeleteReq, DCP_WOReportDeleteRes> {

    @Override
    protected void processDUID(DCP_WOReportDeleteReq req, DCP_WOReportDeleteRes res) throws Exception {

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();

        List<DCP_WOReportDeleteReq.Datas> datas = req.getRequest().getDatas();
        if(CollUtil.isNotEmpty(datas)){

            MyCommon cm=new MyCommon();

            String collect = datas.stream().map(x -> x.getReportNo()).collect(Collectors.joining(","))+",";

            Map<String, String> mapReport=new HashMap<String, String>();
            mapReport.put("REPORTNO", collect);

            String withasSql_reportNo="";
            withasSql_reportNo=cm.getFormatSourceMultiColWith(mapReport);
            mapReport=null;

            String sql=" with p as ("+withasSql_reportNo+") " +
                    " select * from DCP_WOREPORT a " +
                    " inner join p on p.reportno=a.reportno" +
                    " where a.eid='"+req.geteId()+"' " +
                    " and a.organizationno='"+req.getOrganizationNO()+"' " +
                    " and a.status<>'0' ";
            List<Map<String, Object>> list = this.doQueryData(sql,null);

            if(list.size()>0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "存在非新建单据，不可删除！".toString());
            }

            for (DCP_WOReportDeleteReq.Datas datas1 : datas){
                //TEMPLATEID
                DelBean db1 = new DelBean("DCP_WOREPORT");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                db1.addCondition("REPORTNO", new DataValue(datas1.getReportNo(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                DelBean db2 = new DelBean("DCP_WOREPORT_DETAIL");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                db2.addCondition("REPORTNO", new DataValue(datas1.getReportNo(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));


            }
            this.doExecuteDataToDB();

        }





    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_WOReportDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WOReportDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WOReportDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_WOReportDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_WOReportDeleteReq> getRequestType() {
        return new TypeToken<DCP_WOReportDeleteReq>() {
        };
    }

    @Override
    protected DCP_WOReportDeleteRes getResponseType() {
        return new DCP_WOReportDeleteRes();
    }
}

