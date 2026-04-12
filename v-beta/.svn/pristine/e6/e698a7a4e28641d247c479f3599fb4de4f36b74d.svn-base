package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProcessTaskCloseReq;
import com.dsc.spos.json.cust.res.DCP_ProcessTaskCloseRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ProcessTaskClose  extends SPosAdvanceService<DCP_ProcessTaskCloseReq, DCP_ProcessTaskCloseRes> {

    @Override
    protected void processDUID(DCP_ProcessTaskCloseReq req, DCP_ProcessTaskCloseRes res) throws Exception {

        String eId = req.geteId();
        String close = req.getRequest().getClose();

        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String bDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String bTime = new SimpleDateFormat("HHmmss").format(new Date());


        List<DCP_ProcessTaskCloseReq.ProcessTaskList> processTaskList = req.getRequest().getProcessTaskList();


        MyCommon cm=new MyCommon();
        StringBuffer sJoinProcessTaskNo=new StringBuffer("");
        for(DCP_ProcessTaskCloseReq.ProcessTaskList pro:processTaskList){
            sJoinProcessTaskNo.append(pro.getProcessTaskNo()+",");
        }
        Map<String, String> mapProcessTaskNo=new HashMap<String, String>();
        mapProcessTaskNo.put("PROCESSTASKNO", sJoinProcessTaskNo.toString());

        String withasSql_processTaskNo="";
        withasSql_processTaskNo=cm.getFormatSourceMultiColWith(mapProcessTaskNo);
        mapProcessTaskNo=null;


        //入参close=0，执行结案流程
        //1.根据单号列表查询任务单，查询时过滤单头STATUS为6或8，且单身GOODSSTATUS为0或1，且单身DISPATCHSTATUS为0的数据，如查询结果不存在，则提示“单据不存在或已完成”，存在则执行结案
        //2.更新单头STATUS为9，更新CANCELBY，CANCEL_DATE，CANCEL_TIME，UPDATE_TIME,将单身GOODSSTATUS为0或1的数据更新成3，DISPATCHSTATUS为0的数据更新成2
        //

        if("0".equals(close)){
            String sql="with p as ("+withasSql_processTaskNo+")" +
                    "select b.* from DCP_PROCESSTASK a" +
                    " inner join p on p.processtaskno=a.processtaskno " +
                    " left join DCP_PROCESSTASK_DETAIL b on a.eid=b.eid and a.organizationno=b.organizationno and a.processTaskno=b.processTaskno " +
                    " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"'" +
                    " and a.status in ('6','8') and b.GOODSSTATUS in ('0','1') and b.DISPATCHSTATUS='0' ";
            List<Map<String, Object>> list=this.executeQuerySQL_BindSQL(sql,null);

            //将单身GOODSSTATUS为0或1的数据更新成3，DISPATCHSTATUS为0的数据更新成2
            List<Map<String, Object>> filterRows1 = list.stream().filter(x -> x.get("GOODSSTATUS").toString().equals("0") || x.get("GOODSSTATUS").toString().equals("1")).collect(Collectors.toList());
            for (Map<String, Object> map:filterRows1){
                UptBean ub1 = new UptBean("DCP_PROCESSTASK_DETAIL");
                ub1.addUpdateValue("GOODSSTATUS", new DataValue("3", Types.VARCHAR));

                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("PROCESSTASKNO", new DataValue(map.get("PROCESSTASKNO").toString(), Types.VARCHAR));
                ub1.addCondition("SEQ", new DataValue(map.get("SEQ").toString(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));
            }
            List<Map<String, Object>> filterRows2 = list.stream().filter(x -> x.get("DISPATCHSTATUS").toString().equals("0") ).collect(Collectors.toList());
            for (Map<String, Object> map:filterRows2){
                UptBean ub1 = new UptBean("DCP_PROCESSTASK_DETAIL");
                ub1.addUpdateValue("DISPATCHSTATUS", new DataValue("2", Types.VARCHAR));

                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("PROCESSTASKNO", new DataValue(map.get("PROCESSTASKNO").toString(), Types.VARCHAR));
                ub1.addCondition("SEQ", new DataValue(map.get("SEQ").toString(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));
            }

            List<String> nos =  list.stream().map(x -> x.get("PROCESSTASKNO").toString()).distinct().collect(Collectors.toList());
            for (String no:nos){
                UptBean ub1 = new UptBean("DCP_PROCESSTASK");
                ub1.addUpdateValue("STTATUS", new DataValue("9", Types.VARCHAR));
                //更新CANCELBY，CANCEL_DATE，CANCEL_TIME，UPDATE_TIME
                ub1.addUpdateValue("CANCELBY", new DataValue(req.getOpNO(), Types.VARCHAR));
                ub1.addUpdateValue("CANCEL_DATE", new DataValue(bDate, Types.VARCHAR));
                ub1.addUpdateValue("CANCEL_TIME", new DataValue(bTime, Types.VARCHAR));
                ub1.addUpdateValue("UPDATE_TIME", new DataValue(lastmoditime, Types.VARCHAR));
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("PROCESSTASKNO", new DataValue(no, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));
            }

        }
        else if("1".equals(close)){
            //入参close=1，执行反结案流程
            //1.根据单号列表查询任务单，查询时过滤单头STATUS为9，且单身GOODSSTATUS为3，且单身DISPATCHSTATUS为2的数据，如查询结果不存在，则提示“单据不存在或状态未结案”，存在则执行反结案
            //2.单身PSTOCKIN_QTY=0则将GOODSSTATUS为3的数据更新成0，
            // 单身PSTOCKIN_QTY>0且PSTOCKIN_QTY<PQTY则将GOODSSTATUS为3的数据更新成1，
            // 单身DISPATCHQTY<PQTY则将DISPATCHSTATUS为2的数据更新成0；
            //更新单头STATUS为6，清空CANCELBY，CANCEL_DATE，CANCEL_TIME，更新UPDATE_TIME
            String sql="with p as ("+withasSql_processTaskNo+")" +
                    "select b.* from DCP_PROCESSTASK a" +
                    " inner join p on p.processtaskno=a.processtaskno " +
                    " left join DCP_PROCESSTASK_DETAIL b on a.eid=b.eid and a.organizationno=b.organizationno and a.processTaskno=b.processTaskno " +
                    " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"'" +
                    " and a.status='9' and b.GOODSSTATUS ='3' and b.DISPATCHSTATUS='2' ";
            List<Map<String, Object>> list=this.executeQuerySQL_BindSQL(sql,null);

            list.forEach(x->{
                String processTaskNo = x.get("PROCESSTASKNO").toString();
                String item = x.get("ITEM").toString();
                BigDecimal pstockin_qty = new BigDecimal(x.get("PSTOCKIN_QTY").toString());
                BigDecimal pQty = new BigDecimal(x.get("PQTY").toString());
                BigDecimal dispatchQty = new BigDecimal(x.get("DISPATCHQTY").toString());
                if(pstockin_qty.compareTo(BigDecimal.ZERO)==0) {
                    UptBean ub1 = new UptBean("DCP_PROCESSTASK_DETAIL");
                    ub1.addUpdateValue("GOODSSTATUS", new DataValue("0", Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("PROCESSTASKNO", new DataValue(processTaskNo, Types.VARCHAR));
                    ub1.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                    ub1.addCondition("GOODSSTATUS", new DataValue("3", Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub1));
                }
                else if(pstockin_qty.compareTo(BigDecimal.ZERO)>0 && pstockin_qty.compareTo(pQty)<0) {
                    UptBean ub1 = new UptBean("DCP_PROCESSTASK_DETAIL");
                    ub1.addUpdateValue("GOODSSTATUS", new DataValue("1", Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("PROCESSTASKNO", new DataValue(processTaskNo, Types.VARCHAR));
                    ub1.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                    ub1.addCondition("GOODSSTATUS", new DataValue("3", Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub1));
                }
                if(dispatchQty.compareTo(pQty)<0) {
                    UptBean ub1 = new UptBean("DCP_PROCESSTASK_DETAIL");
                    ub1.addUpdateValue("DISPATCHSTATUS", new DataValue("0", Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("PROCESSTASKNO", new DataValue(processTaskNo, Types.VARCHAR));
                    ub1.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                    ub1.addCondition("DISPATCHSTATUS", new DataValue("2", Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub1));
                }

            });


            List<String> nos =  list.stream().map(x -> x.get("PROCESSTASKNO").toString()).distinct().collect(Collectors.toList());
            for (String no:nos){
                UptBean ub1 = new UptBean("DCP_PROCESSTASK");
                ub1.addUpdateValue("STTATUS", new DataValue("6", Types.VARCHAR));
                //更新CANCELBY，CANCEL_DATE，CANCEL_TIME，UPDATE_TIME
                ub1.addUpdateValue("CANCELBY", new DataValue("", Types.VARCHAR));
                ub1.addUpdateValue("CANCEL_DATE", new DataValue("", Types.VARCHAR));
                ub1.addUpdateValue("CANCEL_TIME", new DataValue("", Types.VARCHAR));
                ub1.addUpdateValue("UPDATE_TIME", new DataValue(lastmoditime, Types.VARCHAR));
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("PROCESSTASKNO", new DataValue(no, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));
            }
        }


        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProcessTaskCloseReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProcessTaskCloseReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProcessTaskCloseReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ProcessTaskCloseReq req) throws Exception {
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
    protected TypeToken<DCP_ProcessTaskCloseReq> getRequestType() {
        return new TypeToken<DCP_ProcessTaskCloseReq>() {
        };
    }

    @Override
    protected DCP_ProcessTaskCloseRes getResponseType() {
        return new DCP_ProcessTaskCloseRes();
    }
}

