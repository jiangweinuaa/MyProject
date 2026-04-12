package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BomDeleteReq;
import com.dsc.spos.json.cust.res.DCP_BomDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_BomDelete extends SPosAdvanceService<DCP_BomDeleteReq, DCP_BomDeleteRes> {

    @Override
    protected void processDUID(DCP_BomDeleteReq req, DCP_BomDeleteRes res) throws Exception {
        // TODO Auto-generated method stub
        List<DCP_BomDeleteReq.BomList> bomList = req.getRequest().getBomList();
        //1.主件配方状态<>"-1.未启用"不可删除！
        //2.检查子件商品存在配方，且配方状态<>"-1.未启用"时，不可删除！（注意：多阶BOM要展到最低阶检查）

        if (CollUtil.isNotEmpty(bomList)) {
            String eid = req.geteId();
            String allBomSql = "select * from dcp_bom a where a.eid='" + eid + "'";
            List<Map<String, Object>> allBomList = this.doQueryData(allBomSql, null);
            for (DCP_BomDeleteReq.BomList p : bomList){
                String bomNo = p.getBomNo();
                List<Map<String, Object>> singleBoms = allBomList.stream().filter(x -> x.get("BOMNO").toString().equals(bomNo)).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(singleBoms)){
                    String status = singleBoms.get(0).get("STATUS").toString();
                    if(!status.equals("-1")){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "BOMNO:"+bomNo+"主件配方状态不等于未启用不可删除！");
                    }
                }
            }

            //String allBomMaterialSql="select * from DCP_BOM_MATERIAL where eid='"+eid+"'";
            //List<Map<String, Object>> allBomMaterialList = this.doQueryData(allBomMaterialSql, null);
            //List<String> allBomNos=new ArrayList();
            //for (DCP_BomDeleteReq.BomList p : bomList){
            //    String bomNo = p.getBomNo();
            //    if(!allBomNos.contains(bomNo)){
             //       allBomNos.add(bomNo);
            //    }
            //    this.addChildrenBomNo(bomNo,allBomNos,allBomMaterialList,allBomList);
            //}

            //for (String singleBomNo:allBomNos){
            //    List<Map<String, Object>> singleBoms = allBomList.stream().filter(x -> x.get("BOMNO").toString().equals(singleBomNo)).collect(Collectors.toList());
            //    if (CollUtil.isNotEmpty(singleBoms)){
            //        String status = singleBoms.get(0).get("STATUS").toString();
            //        if(!status.equals("-1")){
            //            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "BOMNO:"+singleBomNo+"主件配方状态不等于未启用不可删除！");
            //        }
            //    }
            //}

            //校验好后删数据
            for(DCP_BomDeleteReq.BomList p : bomList){
                String bomNo = p.getBomNo();
                DelBean db1 = new DelBean("DCP_BOM");
                db1.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
                db1.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                DelBean db2 = new DelBean("DCP_BOM_MATERIAL");
                db2.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
                db2.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                DelBean db3 = new DelBean("DCP_BOM_RANGE");
                db3.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
                db3.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db3));


                DelBean db4 = new DelBean("DCP_BOM_V");
                db4.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
                db4.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db4));

                DelBean db5 = new DelBean("DCP_BOM_MATERIAL_V");
                db5.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
                db5.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db5));

                DelBean db6 = new DelBean("DCP_BOM_RANGE_V");
                db6.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
                db6.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db6));

                DelBean db7 = new DelBean("DCP_BOM_MATERIAL_R");
                db7.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
                db7.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db7));

                //MES_BOM_PROCESS、MES_BOM_SUBPROCESS、MES_BOM_SUBPROCESS_MATERIAL表数据

                DelBean db8 = new DelBean("MES_BOM_PROCESS");
                db8.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
                db8.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db8));

                DelBean db9 = new DelBean("MES_BOM_SUBPROCESS");
                db9.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
                db9.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db9));

                DelBean db10 = new DelBean("MES_BOM_SUBPROCESS_MATERIAL");
                db10.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
                db10.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db10));

                DelBean db11 = new DelBean("DCP_BOM_COBYPRODUCT");
                db11.addCondition("BOMNO", new DataValue(bomNo, Types.VARCHAR));
                db11.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db11));

            }
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BomDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BomDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BomDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_BomDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_BomDeleteReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_BomDeleteReq>() {
        };
    }

    @Override
    protected DCP_BomDeleteRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_BomDeleteRes();
    }

    private void addChildrenBomNo(String bomNo,List<String> bomNos,List<Map<String, Object>> allBomMaterialList, List<Map<String, Object>> allBomList ){
        List<Map<String, Object>> materialList = allBomMaterialList.stream().filter(x -> x.get("BOMNO").toString().equals(bomNo)).collect(Collectors.toList());
        if(CollUtil.isNotEmpty(materialList)){
            for(Map m:materialList){
                String material_pluno = m.get("MATERIAL_PLUNO").toString();
                //判断有没有bom
                List<Map<String, Object>> bomList = allBomList.stream().filter(x -> x.get("PLUNO").toString().equals(material_pluno)).collect(Collectors.toList());
                if(CollUtil.isNotEmpty(bomList)){
                    String childrenBomNo = bomList.get(0).get("BOMNO").toString();
                    if(!bomNos.contains(childrenBomNo)){
                        bomNos.add(childrenBomNo);
                        addChildrenBomNo(childrenBomNo,bomNos,allBomMaterialList,allBomList);
                    }
                }
            }
        }

    }

}
