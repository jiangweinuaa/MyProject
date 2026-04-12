package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BatchToMStockOutReq;
import com.dsc.spos.json.cust.req.DCP_BatchingDocProcessReq;
import com.dsc.spos.json.cust.res.DCP_BatchToMStockOutRes;
import com.dsc.spos.json.cust.res.DCP_BatchingDocProcessRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.bc.BcReq;
import com.dsc.spos.utils.bc.BcRes;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DCP_BatchingDocProcess extends SPosAdvanceService<DCP_BatchingDocProcessReq, DCP_BatchingDocProcessRes> {

    @Override
    protected void processDUID(DCP_BatchingDocProcessReq req, DCP_BatchingDocProcessRes res) throws Exception {

        String eId = req.geteId();
        String batchNo = req.getRequest().getBatchNo();
        String accountDate = req.getRequest().getAccountDate();
        String organizationNO = req.getOrganizationNO();
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String lastModifiDate =	new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        if(Check.Null(accountDate)){
            accountDate=lastModifiDate;
        }

        Map<String, Object> stockChangeVerifyMsg = PosPub.getStockChangeVerifyMsg(dao, req.geteId(), req.getOrganizationNO(), accountDate);
        if("N".equals( stockChangeVerifyMsg.get("check").toString())){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, stockChangeVerifyMsg.get("errorMsg").toString());
        }

        String sql="select * from MES_BATCHING a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' " +
                " and a.batchno='"+batchNo+"' ";
        List<Map<String, Object>> list = this.doQueryData(sql, null);
        if(list.size()<=0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据不存在");
        }
        String status = list.get(0).get("STATUS").toString();
        String ooType = list.get(0).get("OOTYPE").toString();
        String docType = list.get(0).get("DOC_TYPE").toString();

        if(!"0".equals(status)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非新建不可过账！");
        }

        String sql1="select a.*,b.unitratio" +
                " from MES_BATCHING_DETAIL a " +
                " left join dcp_goods_unit b on a.eid=b.eid and a.pluno=b.pluno and a.mpunit=b.ounit" +
                "  where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' " +
                " and a.batchno='"+batchNo+"' ";
        List<Map<String, Object>> listDetail = this.doQueryData(sql1, null);

        String sql2="select a.*,b.unitratio " +
                " from MES_BATCHING_DETAIL_MO a" +
                " left join dcp_goods_unit b on a.eid=b.eid and a.punit=b.ounit and a.pluno=b.pluno" +
                " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' " +
                " and a.batchno='"+batchNo+"' ";
        List<Map<String, Object>> listMo = this.doQueryData(sql2, null);

        for (Map<String, Object> detailMap : listDetail){
            BigDecimal pQty = new BigDecimal(detailMap.get("PQTY").toString());
            List<Map<String, Object>> collect = listMo.stream().filter(x -> x.get("OITEM").toString().equals(detailMap.get("ITEM").toString())).collect(Collectors.toList());
            if(collect.size()>0){
                List<BigDecimal> shareQtys = collect.stream().map(x -> new BigDecimal(x.get("SHAREPQTY").toString())).collect(Collectors.toList());
                BigDecimal shareQty = shareQtys.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                if(pQty.compareTo(shareQty)!=0){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "配料单明细批次数量与配料单明细的批次数量不一致！");
                }
            }
        }


        UptBean ub2 = new UptBean("MES_BATCHING");
        ub2.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));
        ub2.addUpdateValue("ACCOUNTOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
        ub2.addUpdateValue("ACCOUNTOPNAME", new DataValue(req.getOpName(), Types.VARCHAR));
        ub2.addUpdateValue("ACCOUNTTIME", new DataValue(accountDate, Types.DATE));
        ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub2.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        ub2.addCondition("BATCHNO", new DataValue(batchNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub2));
        List<Map<String, Object>> detailFRows=listDetail;
        if("0".equals(ooType)){
             detailFRows = listDetail.stream().filter(x -> x.get("ISBUCKLE").toString().equals("Y")).collect(Collectors.toList());
        }
        for (Map<String, Object> detailMap : detailFRows){
            List<Map<String, Object>> collect = listMo.stream().filter(x -> x.get("OITEM").toString().equals(detailMap.get("ITEM").toString())).collect(Collectors.toList());
            if(collect.size()>0){
                for (Map<String, Object> moMap : collect){
                    String fromWarehouse = moMap.get("FROMWAREHOUSE").toString();
                    String toWarehouse = moMap.get("TOWAREHOUSE").toString();
                    BigDecimal shareQty = new BigDecimal(moMap.get("SHAREPQTY").toString());
                    if("1".equals(docType)){
                        shareQty=shareQty.multiply(new BigDecimal("-1"));
                    }

                    String baseUnit = moMap.get("BASEUNIT").toString();
                    String baseQty = moMap.get("BASEQTY").toString();
                    String batch = moMap.get("BATCH").toString();
                    String pluno = moMap.get("PLUNO").toString();
                    String pUnit = moMap.get("PUNIT").toString();
                    String unitRatio = moMap.get("UNITRATIO").toString();

                    String prodDuctDate="";
                    if(Check.NotNull(batch)){
                        prodDuctDate = this.getProdDateFromMesBatch(req, pluno, "", batch);
                    }
                    int direction=1;

                    BcReq bcReq=new BcReq();
                    bcReq.setServiceType("BatchingDocProcess");
                    bcReq.setBillType("39");
                    BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                    if(Check.Null(bcMap.getBType())||Check.Null(bcMap.getCostCode())){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                    }
                    //异动库存 39.配料出库
                    String procedure="SP_DCP_STOCKCHANGE_VX";
                    Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                    inputParameter.put(1,req.geteId());                                       //--企业ID
                    inputParameter.put(2,null);
                    inputParameter.put(3,req.getOrganizationNO());                                    //--组织
                    inputParameter.put(4,bcMap.getBType());
                    inputParameter.put(5,bcMap.getCostCode());
                    inputParameter.put(6,"39");            //--单据类型39
                    inputParameter.put(7,batchNo);         //--单据号
                    inputParameter.put(8,moMap.get("ITEM").toString());    //--单据行号
                    inputParameter.put(9,"0");
                    inputParameter.put(10,"-1");            //--异动方向 1=加库存 -1=减库存
                    inputParameter.put(11,accountDate);           //--营业日期 yyyyMMdd
                    inputParameter.put(12,pluno);   //--品号
                    inputParameter.put(13," ");              //--特征码
                    inputParameter.put(14,fromWarehouse);   //--仓库
                    inputParameter.put(15,batch);              //--批号
                    inputParameter.put(16,"");              //--库位
                    inputParameter.put(17,pUnit);    //--交易单位
                    inputParameter.put(18,shareQty);       //--交易数量
                    inputParameter.put(19,baseUnit);        //--基准单位
                    inputParameter.put(20,baseQty);         //--基准数量
                    inputParameter.put(21,unitRatio);       //--换算比例
                    inputParameter.put(22,0);          		//--零售价
                    inputParameter.put(23,0);               //--零售金额
                    inputParameter.put(24,"0");             //--进货价
                    inputParameter.put(25,"0");             //--进货金额
                    inputParameter.put(26,accountDate);           //--入账日期 yyyy-MM-dd
                    inputParameter.put(27,prodDuctDate);              //--批号的生产日期 yyyy-MM-dd
                    inputParameter.put(28,accountDate);           //--单据日期yyyy-MM-dd
                    inputParameter.put(29,"配料提交-减库存");    //--异动原因
                    inputParameter.put(30,"配料提交-减库存");    //--异动描述
                    inputParameter.put(31,req.getOpNO());    //--操作员

                    ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                    this.addProcessData(new DataProcessBean(pdb));

                    bcReq=new BcReq();
                    bcReq.setServiceType("BatchingDocProcess");
                    bcReq.setBillType("40");
                    bcMap = PosPub.getBTypeAndCostCode(bcReq);
                    if(Check.Null(bcMap.getBType())||Check.Null(bcMap.getCostCode())){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                    }

                    //异动库存 40.配料入库
                    String procedure2="SP_DCP_STOCKCHANGE_VX";
                    Map<Integer,Object> inputParameter2 = new HashMap<Integer, Object>();
                    inputParameter2.put(1,req.geteId());                                       //--企业ID
                    inputParameter2.put(2,null);
                    inputParameter2.put(3,req.getOrganizationNO());                                    //--组织
                    inputParameter2.put(4,bcMap.getBType());
                    inputParameter2.put(5,bcMap.getCostCode());
                    inputParameter2.put(6,"40");            //--单据类型40
                    inputParameter2.put(7,batchNo);         //--单据号
                    inputParameter2.put(8,moMap.get("ITEM").toString());    //--单据行号
                    inputParameter2.put(9,"0");
                    inputParameter2.put(10,"1");            //--异动方向 1=加库存 -1=减库存
                    inputParameter2.put(11,accountDate);           //--营业日期 yyyyMMdd
                    inputParameter2.put(12,pluno);   //--品号
                    inputParameter2.put(13," ");             //--特征码
                    inputParameter2.put(14,toWarehouse);    //--仓库
                    inputParameter2.put(15,batch);             //--批号
                    inputParameter2.put(16,"");              //--库位 map_batch.get("LOCATION").toString()
                    inputParameter2.put(17,pluno);    //--交易单位
                    inputParameter2.put(18,shareQty);       //--交易数量
                    inputParameter2.put(19,baseUnit);        //--基准单位
                    inputParameter2.put(20,baseQty);         //--基准数量
                    inputParameter2.put(21,unitRatio);       //--换算比例
                    inputParameter2.put(22,0);               //--零售价
                    inputParameter2.put(23,0);               //--零售金额
                    inputParameter2.put(24,"0");             //--进货价
                    inputParameter2.put(25,"0");             //--进货金额
                    inputParameter2.put(26,accountDate);           //--入账日期 yyyy-MM-dd
                    inputParameter2.put(27,prodDuctDate);              //--批号的生产日期 yyyy-MM-dd
                    inputParameter2.put(28,accountDate);           //--单据日期yyyy-MM-dd
                    inputParameter2.put(29,"配料提交-加库存");    //--异动原因
                    inputParameter2.put(30,"配料提交-加库存");    //--异动描述
                    inputParameter2.put(31,req.getOpNO());    //--操作员

                    ProcedureBean pdb2 = new ProcedureBean(procedure2, inputParameter2);
                    this.addProcessData(new DataProcessBean(pdb2));

                }
            }
            else{
                String fromWarehouse = detailMap.get("FROMWAREHOUSE").toString();
                String toWarehouse = detailMap.get("TOWAREHOUSE").toString();
                BigDecimal shareQty = new BigDecimal(detailMap.get("PQTY").toString());
                if("1".equals(docType)){
                    shareQty=shareQty.multiply(new BigDecimal("-1"));
                }

                String baseUnit = detailMap.get("BASEUNIT").toString();
                String baseQty = detailMap.get("BASEQTY").toString();
                String batch = "";
                String pluno = detailMap.get("MPLUNO").toString();
                String pUnit = detailMap.get("MPUNIT").toString();
                String unitRatio = detailMap.get("UNITRATIO").toString();

                String prodDuctDate="";
                if(Check.NotNull(batch)){
                    prodDuctDate = this.getProdDateFromMesBatch(req, pluno, "", batch);
                }
                int direction=1;

                BcReq bcReq=new BcReq();
                bcReq.setServiceType("BatchingDocProcess");
                bcReq.setBillType("39");
                BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                if(Check.Null(bcMap.getBType())||Check.Null(bcMap.getCostCode())){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                }

                //异动库存 39.配料出库
                String procedure="SP_DCP_STOCKCHANGE_VX";
                Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                inputParameter.put(1,req.geteId());                                       //--企业ID
                inputParameter.put(2,null);
                inputParameter.put(3,req.getOrganizationNO());                                    //--组织
                inputParameter.put(4,bcMap.getBType());
                inputParameter.put(5,bcMap.getCostCode());
                inputParameter.put(6,"39");            //--单据类型39
                inputParameter.put(7,batchNo);         //--单据号
                inputParameter.put(8,detailMap.get("ITEM").toString());    //--单据行号
                inputParameter.put(9,"0");
                inputParameter.put(10,"-1");            //--异动方向 1=加库存 -1=减库存
                inputParameter.put(11,accountDate);           //--营业日期 yyyyMMdd
                inputParameter.put(12,pluno);   //--品号
                inputParameter.put(13," ");              //--特征码
                inputParameter.put(14,fromWarehouse);   //--仓库
                inputParameter.put(15,batch);              //--批号
                inputParameter.put(16,"");              //--库位
                inputParameter.put(17,pUnit);    //--交易单位
                inputParameter.put(18,shareQty);       //--交易数量
                inputParameter.put(19,baseUnit);        //--基准单位
                inputParameter.put(20,baseQty);         //--基准数量
                inputParameter.put(21,unitRatio);       //--换算比例
                inputParameter.put(22,0);          		//--零售价
                inputParameter.put(23,0);               //--零售金额
                inputParameter.put(24,"0");             //--进货价
                inputParameter.put(25,"0");             //--进货金额
                inputParameter.put(26,accountDate);           //--入账日期 yyyy-MM-dd
                inputParameter.put(27,prodDuctDate);              //--批号的生产日期 yyyy-MM-dd
                inputParameter.put(28,accountDate);           //--单据日期yyyy-MM-dd
                inputParameter.put(29,"配料提交-减库存");    //--异动原因
                inputParameter.put(30,"配料提交-减库存");    //--异动描述
                inputParameter.put(31,req.getOpNO());    //--操作员

                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                this.addProcessData(new DataProcessBean(pdb));

                bcReq=new BcReq();
                bcReq.setServiceType("BatchingDocProcess");
                bcReq.setBillType("40");
                bcMap = PosPub.getBTypeAndCostCode(bcReq);
                if(Check.Null(bcMap.getBType())||Check.Null(bcMap.getCostCode())){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                }

                //异动库存 40.配料入库
                String procedure2="SP_DCP_STOCKCHANGE_VX";
                Map<Integer,Object> inputParameter2 = new HashMap<Integer, Object>();
                inputParameter2.put(1,req.geteId());                                       //--企业ID
                inputParameter2.put(2,null);
                inputParameter2.put(3,req.getOrganizationNO());                                    //--组织
                inputParameter2.put(4,bcMap.getBType());
                inputParameter2.put(5,bcMap.getCostCode());
                inputParameter2.put(6,"40");            //--单据类型40
                inputParameter2.put(7,batchNo);         //--单据号
                inputParameter2.put(8,detailMap.get("ITEM").toString());    //--单据行号
                inputParameter2.put(9,"0");
                inputParameter2.put(10,"1");            //--异动方向 1=加库存 -1=减库存
                inputParameter2.put(11,accountDate);           //--营业日期 yyyyMMdd
                inputParameter2.put(12,pluno);   //--品号
                inputParameter2.put(13," ");             //--特征码
                inputParameter2.put(14,toWarehouse);    //--仓库
                inputParameter2.put(15,batch);             //--批号
                inputParameter2.put(16,"");              //--库位 map_batch.get("LOCATION").toString()
                inputParameter2.put(17,pluno);    //--交易单位
                inputParameter2.put(18,shareQty);       //--交易数量
                inputParameter2.put(19,baseUnit);        //--基准单位
                inputParameter2.put(20,baseQty);         //--基准数量
                inputParameter2.put(21,unitRatio);       //--换算比例
                inputParameter2.put(22,0);               //--零售价
                inputParameter2.put(23,0);               //--零售金额
                inputParameter2.put(24,"0");             //--进货价
                inputParameter2.put(25,"0");             //--进货金额
                inputParameter2.put(26,accountDate);           //--入账日期 yyyy-MM-dd
                inputParameter2.put(27,prodDuctDate);              //--批号的生产日期 yyyy-MM-dd
                inputParameter2.put(28,accountDate);           //--单据日期yyyy-MM-dd
                inputParameter2.put(29,"配料提交-加库存");    //--异动原因
                inputParameter2.put(30,"配料提交-加库存");    //--异动描述
                inputParameter2.put(31,req.getOpNO());    //--操作员

                ProcedureBean pdb2 = new ProcedureBean(procedure2, inputParameter2);
                this.addProcessData(new DataProcessBean(pdb2));

            }
        }


        //5.oOType为0，且docType为0时，
        // 根据oOfNo和oOItem关联
        // 更新DCP_PROCESSTASK_DETAIL.BATCHQTY=MES_BATCHING_DETAIL.BATCHQTY(叠加)
        if("0".equals(ooType)&&"0".equals(docType)){
            for (Map<String, Object> map : listDetail){
                UptBean ub6 = new UptBean("DCP_PROCESSTASK_DETAIL");
                ub6.addUpdateValue("BATCHQTY", new DataValue(Check.Null(map.get("BATCHQTY").toString())?"0":map.get("BATCHQTY").toString(), Types.DECIMAL, DataValue.DataExpression.UpdateSelf));//其他出库

                ub6.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
                ub6.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub6.addCondition("ITEM", new DataValue(map.get("OOITEM").toString(), Types.VARCHAR));
                ub6.addCondition("PROCESSTASKNO", new DataValue(map.get("OOFNO").toString(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub6));
            }
        }
        //oOType为1，且docType为0时，
        // 根据oOfNo和pItem，sItem，zItem，materialPluNo关联
        // 更新MES_BATCHTASK_MATERIAL.BATCHQTY=MES_BATCHING_DETAIL.PQTY(叠加)
        //更新MES_BATCHTASK_MATERIAL.BATCHCOPIES=配料单明细对应原料的最大benCopies
        if("1".equals(ooType)&&"0".equals(docType)){

            for (Map<String, Object> map : listDetail){
                UptBean ub6 = null;
                ub6 = new UptBean("MES_BATCHTASK_MATERIAL");
                ub6.addUpdateValue("BATCHQTY",new DataValue(map.get("PQTY").toString(), Types.DECIMAL, DataValue.DataExpression.UpdateSelf));

                //condition
                ub6.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                ub6.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
                ub6.addCondition("BATCHTASKNO", new DataValue(map.get("BATCHTASKNO").toString(), Types.VARCHAR));
                ub6.addCondition("PITEM", new DataValue(map.get("PITEM").toString(), Types.INTEGER));
                ub6.addCondition("SITEM", new DataValue(map.get("SITEM").toString(), Types.INTEGER));
                ub6.addCondition("ZITEM", new DataValue(map.get("ZITEM").toString(), Types.INTEGER));

                this.addProcessData(new DataProcessBean(ub6));
            }

            //listDetail按BENCOPIES从小到大排序
            listDetail.sort(new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    return new BigDecimal(o1.get("BENCOPIES").toString()).compareTo(new BigDecimal(o2.get("BENCOPIES").toString()));
                }
            });
            for (Map<String, Object> map : listDetail){
                UptBean ub6 = null;
                ub6 = new UptBean("MES_BATCHTASK_MATERIAL");
                ub6.addUpdateValue("BATCHCOPIES",new DataValue(map.get("BENCOPIES").toString(), Types.DECIMAL));

                //condition
                ub6.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                ub6.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
                ub6.addCondition("BATCHTASKNO", new DataValue(map.get("BATCHTASKNO").toString(), Types.VARCHAR));
                ub6.addCondition("PITEM", new DataValue(map.get("PITEM").toString(), Types.INTEGER));
                ub6.addCondition("SITEM", new DataValue(map.get("SITEM").toString(), Types.INTEGER));
                ub6.addCondition("ZITEM", new DataValue(map.get("ZITEM").toString(), Types.INTEGER));

                this.addProcessData(new DataProcessBean(ub6));
            }


        }

        if("1".equals(docType)){
            for (Map<String, Object> map : listDetail){
                UptBean ub6 = new UptBean("DCP_PROCESSTASK_DETAIL");
                ub6.addUpdateValue("ISRETURN", new DataValue("Y", Types.DECIMAL));//其他出库

                ub6.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
                ub6.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub6.addCondition("ITEM", new DataValue(map.get("ITEM").toString(), Types.VARCHAR));
                ub6.addCondition("BATCHNO", new DataValue(map.get("BATCHNO").toString(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub6));
            }
        }


        this.doExecuteDataToDB();
        if("1".equals(ooType)&&"0".equals(docType)){
            List<Map<String,String>> batchTaskMaps = listDetail.stream().map(x ->
            {
                Map<String,String> map=new HashMap();
                map.put("BATCHTASKNO",x.get("BATCHTASKNO").toString());
                map.put("PITEM",x.get("PITEM").toString());
                return map;

            }).distinct().collect(Collectors.toList());
            for (Map<String,String> batchTaskMap : batchTaskMaps){
                String batchTaskNo = batchTaskMap.get("BATCHTASKNO").toString();
                String pItem = batchTaskMap.get("PITEM");
                String sql_batchtask_material="select * from MES_BATCHTASK_MATERIAL a " +
                        "left join MES_BATCHTASK_PROCESS b on a.eid=b.eid and a.organizationno=b.organizationno and a.pitem=b.pitem  and a.batchtaskno=b.batchtaskno  "+
                        "where a.eid='"+req.geteId()+"' " +
                        "and a.organizationno='"+req.getOrganizationNO()+"' " +
                        "and a.batchtaskno='"+batchTaskNo+"' " +
                        "and a.material_type='0' " +
                        "and a.isbatch='Y' " +
                        "and a.BATCHCOPIES<b.COPIES  order by  a.BATCHCOPIES ";
                List<Map<String, Object>> getQ_batchtaskMaterial= this.doQueryData(sql_batchtask_material, null);
                if (getQ_batchtaskMaterial != null && getQ_batchtaskMaterial.size()==0)
                {
                    UptBean ub1 = null;
                    ub1 = new UptBean("MES_BATCHTASK");
                    ub1.addUpdateValue("BATCHSTATUS",new DataValue("Y", Types.VARCHAR));
                    //condition
                    ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                    ub1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(),Types.VARCHAR));
                    ub1.addCondition("BATCHTASKNO", new DataValue(batchTaskNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub1));

                }
                else{
                    String sql_batchtask_material1="select a.* from MES_BATCHTASK_MATERIAL a " +
                            "left join MES_BATCHTASK_PROCESS b on a.eid=b.eid and a.organizationno=b.organizationno and a.pitem=b.pitem  and a.batchtaskno=b.batchtaskno  "+
                            "where a.eid='"+req.geteId()+"' " +
                            "and a.organizationno='"+req.getOrganizationNO()+"' " +
                            "and a.batchtaskno='"+batchTaskNo+"' " +
                            "and a.pitem="+pItem+" " +
                            "and a.material_type='0' " +
                            "and a.isbatch='Y' " +
                            "  order by  a.BATCHCOPIES ";
                    List<Map<String, Object>> getQ_batchtaskMaterial1= this.doQueryData(sql_batchtask_material1, null);
                    if(getQ_batchtaskMaterial1!=null&&getQ_batchtaskMaterial1.size()>0){
                        String batchcopies = getQ_batchtaskMaterial1.get(0).get("BATCHCOPIES").toString();
                        if(Check.Null(batchcopies)){
                            batchcopies="0";
                        }
                        UptBean ub21 = new UptBean("MES_BATCHTASK_PROCESS");
                        ub21.addUpdateValue("BATCHCOPIES",new DataValue(batchcopies, Types.VARCHAR ));
                        //condition
                        ub21.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                        ub21.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
                        ub21.addCondition("PITEM", new DataValue(pItem, Types.VARCHAR));
                        ub21.addCondition("BATCHTASKNO", new DataValue(batchTaskNo, Types.VARCHAR));

                        this.addProcessData(new DataProcessBean(ub21));
                    }
                }
            }
        }

        this.doExecuteDataToDB();

        //IF MATERIAL_TYPE='0' AND ISBATCH='Y' AND BATCHCOPIES<COPIES的数据笔数为0 THEN
        //更新MES_BATCHTASK.BATCHSTATUS='Y'
        //ENDIF
        //获取MES_BATCHTASK_MATERIAL表 MATERIAL_TYPE='0' AND ISBATCH='Y'
        // 数据中最小BATCHCOPIES
        //更新MES_BATCHTASK_PROCESS.BATCHCOPIES=MES_BATCHTASK_MATERIAL.BATCHCOPIES
        //

        //生成扣料单 并过账
        ParseJson pj = new ParseJson();
        DCP_BatchToMStockOutReq mstockReq=new DCP_BatchToMStockOutReq();
        mstockReq.setServiceId("DCP_BatchToMStockOut");
        mstockReq.setToken(req.getToken());
        DCP_BatchToMStockOutReq.Level1Elm request = mstockReq.new Level1Elm();
        request.setBatchNo(batchNo);
        mstockReq.setRequest(request);

        String jsontemp= pj.beanToJson(mstockReq);

        //直接调用CRegisterDCP服务
        DispatchService ds = DispatchService.getInstance();
        String resXml = ds.callService(jsontemp, StaticInfo.dao);
        DCP_BatchToMStockOutRes resserver=pj.jsonToBean(resXml, new TypeToken<DCP_BatchToMStockOutRes>(){});
        if(resserver.isSuccess()==false){

        }


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BatchingDocProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BatchingDocProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BatchingDocProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_BatchingDocProcessReq req) throws Exception {
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

    private String getProdDateFromMesBatch(DCP_BatchingDocProcessReq req, String pluNo, String featureNo, String batchNo) throws Exception{
        String prodDate="";
        String sql="select to_char(a.PRODUCTDATE,'yyyy-MM-dd') as prodDuctDates from mes_batch a where a.eid='"+req.geteId()+"' and a.featureno='"+featureNo+"' and a.pluno='"+pluNo+"' " +
                " and a.batchno='"+batchNo+"' ";
        List<Map<String, Object>> getQ_batch= this.doQueryData(sql, null);
        if(getQ_batch.size()>0){
            prodDate=getQ_batch.get(0).get("PRODUCTDATES").toString();
        }
        return prodDate;
    }

    @Override
    protected TypeToken<DCP_BatchingDocProcessReq> getRequestType() {
        return new TypeToken<DCP_BatchingDocProcessReq>() {
        };
    }

    @Override
    protected DCP_BatchingDocProcessRes getResponseType() {
        return new DCP_BatchingDocProcessRes();
    }
}

