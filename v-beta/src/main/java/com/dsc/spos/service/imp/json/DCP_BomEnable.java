package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BomEnableReq;
import com.dsc.spos.json.cust.res.DCP_BomEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_BomEnable extends SPosAdvanceService<DCP_BomEnableReq, DCP_BomEnableRes> {

    @Override
    protected void processDUID(DCP_BomEnableReq req, DCP_BomEnableRes res) throws Exception {
        // TODO Auto-generated method stub
        List<DCP_BomEnableReq.BomList> bomList = req.getRequest().getBomList();
        String eid = req.geteId();
        String employeeNo = req.getEmployeeNo();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        if(CollUtil.isEmpty(bomList)){
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            return;
        }

        //启用处理：opType="1.启用"
        //1.启用前检查：状态=“100.已启用”时不可启用！
        //2.传入参数opType='1.启用'，更新配方状态status=100.已启用，记录修改人，修改时间
        //
        //
        //禁用处理：opType="2.禁用"
        //1.禁用前检查：状态=“0.已禁用”时不可禁用！
        //2.传入参数opType='2.禁用'，更新配方状态status=0.禁用，记录修改人，修改时间
        MyCommon cm=new MyCommon();
        StringBuffer sJoinBomNo=new StringBuffer("");
        for(DCP_BomEnableReq.BomList bom:bomList){
            sJoinBomNo.append(bom.getBomNo()+",");
        }
        Map<String, String> mapBomNo=new HashMap<String, String>();
        mapBomNo.put("BOMNO", sJoinBomNo.toString());

        String withasSql_bomno="";
        withasSql_bomno=cm.getFormatSourceMultiColWith(mapBomNo);
        mapBomNo=null;



        String opType = req.getRequest().getOpType();
        if(opType.equals("1")){
            //启用
            //String sqlBom="with p AS ( " + withasSql_bomno + ") " +
                //    "select a.* from dcp_bom a " +
                 //   "inner join p on p.bomno=a.bomno " +
                 //   "where a.eid='"+req.geteId()+"' and a.status='100' ";
            //List<Map<String, Object>> list = this.doQueryData(sqlBom, null);
            //if(CollUtil.isNotEmpty(list)){
               // for(Map<String, Object> map:list){
               //    String bomno = map.get("BOMNO").toString();
                //    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "BOMNO"+bomno+"已启用不可启用！");
                //}
            //}

            for(DCP_BomEnableReq.BomList bom:bomList){
                String bomNo = bom.getBomNo();

                String sqlBom="select a.* from dcp_bom a " +
                        "where a.eid='"+req.geteId()+"' and a.status='100' and a.bomno='"+bomNo+"' ";
                List<Map<String, Object>> list = this.doQueryData(sqlBom, null);
                if(CollUtil.isNotEmpty(list)){
                    continue;
                   //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "BOMNO"+bomNo+"已启用不可启用！");
                }

                UptBean ub1 = null;
                ub1 = new UptBean("DCP_BOM");
                ub1.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                ub1.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));

                ub1.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
                ub1.addUpdateValue("LASTMODIOPID", new DataValue(employeeNo, Types.VARCHAR));
                ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getEmployeeName(), Types.VARCHAR));
                ub1.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));

                this.addProcessData(new DataProcessBean(ub1));

                //this.doExecuteDataToDB();
            }



        }
        if(opType.equals("2")){
            //禁用
            //String sqlBom="with p AS ( " + withasSql_bomno + ") " +
            //        "select a.* from dcp_bom a " +
            //        "inner join p on p.bomno=a.bomno " +
             //       "where a.eid='"+req.geteId()+"' and a.status='0' ";
            //List<Map<String, Object>> list = this.doQueryData(sqlBom, null);
            //if(CollUtil.isNotEmpty(list)){
             //   for(Map<String, Object> map:list){
              //      String bomno = map.get("BOMNO").toString();
              //      throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "BOMNO"+bomno+"已禁用不可禁用！");
             //   }
            //}

            for(DCP_BomEnableReq.BomList bom:bomList){
                String bomNo = bom.getBomNo();
                String sqlBom="select a.* from dcp_bom a " +
                        "where a.eid='"+req.geteId()+"' and a.status='0' and a.bomno='"+bomNo+"' ";
                List<Map<String, Object>> list = this.doQueryData(sqlBom, null);
                if(CollUtil.isNotEmpty(list)){
                    continue;
                    //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "BOMNO"+bomNo+"已禁用不可禁用！");
                }
                UptBean ub1 = null;
                ub1 = new UptBean("DCP_BOM");
                ub1.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                ub1.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));

                ub1.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
                ub1.addUpdateValue("LASTMODIOPID", new DataValue(employeeNo, Types.VARCHAR));
                ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getEmployeeName(), Types.VARCHAR));
                ub1.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));

                this.addProcessData(new DataProcessBean(ub1));

                //this.doExecuteDataToDB();
            }
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BomEnableReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BomEnableReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BomEnableReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_BomEnableReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_BomEnableReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_BomEnableReq>() {
        };
    }

    @Override
    protected DCP_BomEnableRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_BomEnableRes();
    }


}
