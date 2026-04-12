package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProdScheduleStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ProdScheduleStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_ProdScheduleStatusUpdate extends SPosAdvanceService<DCP_ProdScheduleStatusUpdateReq, DCP_ProdScheduleStatusUpdateRes> {


    @Override
    protected boolean isVerifyFail(DCP_ProdScheduleStatusUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();


        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ProdScheduleStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_ProdScheduleStatusUpdateReq>(){};
    }

    @Override
    protected DCP_ProdScheduleStatusUpdateRes getResponseType() {
        return new DCP_ProdScheduleStatusUpdateRes();
    }

    @Override
    public void processDUID(DCP_ProdScheduleStatusUpdateReq req,DCP_ProdScheduleStatusUpdateRes res) throws Exception {
        String billNo = req.getRequest().getBillNo();
        String opType = req.getRequest().getOpType();
        String eid = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String employeeNo = req.getEmployeeNo();
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


        String querySql = this.getQuerySql(req);
        List<Map<String, Object>> mainData = dao.executeQuerySQL(querySql, null);

        if(mainData.size()==0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据不存在");
        }
        String status = mainData.get(0).get("STATUS").toString();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String bdate = mainData.get(0).get("BDATE").toString();

        List<String> solveDemandList=new ArrayList();

        if(Constant.OPR_TYPE_CANCEL.equals(opType)){
            if(!"0".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非【新建】不可作废！");
            }

            UptBean ub2 = new UptBean("DCP_PRODSCHEDULE");
            ub2.addUpdateValue("STATUS", DataValues.newString("5"));
            ub2.addUpdateValue("MODIFYBY", DataValues.newString(employeeNo));
            ub2.addUpdateValue("MODIFYTIME",DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("CANCELBY", DataValues.newString(employeeNo));
            ub2.addUpdateValue("CANCELTIME", DataValues.newDate(lastmoditime));


            ub2.addCondition("EID", DataValues.newString(eid));
            ub2.addCondition("BILLNO",DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub2));

        }

        if(Constant.OPR_TYPE_CLOSE.equals(opType)){
            //1.单据状态<>“1.待下发”/“2.全部下发”/“3.部分下发”不可结案！
            if(!("1".equals(status)||"2".equals(status)||"3".equals(status))){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非【下发】不可结案！");
            }

            UptBean ub2 = new UptBean("DCP_PRODSCHEDULE");
            ub2.addUpdateValue("STATUS", DataValues.newString("4"));
            ub2.addUpdateValue("MODIFYBY", DataValues.newString(employeeNo));
            ub2.addUpdateValue("MODIFYTIME",DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("CLOSEBY", DataValues.newString(employeeNo));
            ub2.addUpdateValue("CLOSETIME", DataValues.newDate(lastmoditime));


            ub2.addCondition("EID", DataValues.newString(eid));
            ub2.addCondition("BILLNO",DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub2));

        }

        if("unconfirm".equals(opType)){
            if(!"1".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非【待下发】不可取消审核！");
            }
            String sourceSql="select d.unitratio as punitratio,h.unitratio as dpunitratio ,c.udlength as pudlength,g.udlength as dpudlength," +
                    " f.orderno,f.item,a.ALLOTPQTY,f.PRODUCEQTY " +
                    " from  DCP_PRODSCHEDULE_SOURCE a " +
                    " inner join DCP_PRODSCHEDULE_DETAIL b on a.eid=b.eid and a.organizationno=b.organizationno and a.billno=b.billno and a.oitem=b.item " +
                    " left join dcp_unit c on c.eid=a.eid and c.unit=b.punit " +//生产单位
                    " left join dcp_goods_unit d on d.eid=a.eid and d.pluno=b.pluno and d.unit=c.unit and d.ounit=b.baseunit " +
                    " left join dcp_demand f on f.eid=a.eid and f.orderno=a.orderno and f.item=a.orderitem " +
                    " left join dcp_unit g on g.eid=a.eid and g.unit=f.punit " +//需求单位
                    " left join dcp_goods_unit h on h.eid=a.eid and h.pluno=b.pluno and h.unit=g.unit and h.ounit=b.baseunit " +
                    " ";
            sourceSql+=" where a.eid='"+eid+"' and a.organizationno='"+organizationNO+"' and a.billno='"+billNo+"'";
            List<Map<String, Object>> sourceList = this.doQueryData(sourceSql, null);
            for(Map<String, Object> sourceMap:sourceList){
                BigDecimal allotpqty = new BigDecimal(sourceMap.get("ALLOTPQTY").toString());
                BigDecimal punitratio = new BigDecimal(sourceMap.get("PUNITRATIO").toString());
                //BigDecimal pudlength = new BigDecimal(sourceMap.get("PUDLENGTH").toString());
                BigDecimal dpunitratio = new BigDecimal(sourceMap.get("DPUNITRATIO").toString());
                BigDecimal yetProduceQty = new BigDecimal(sourceMap.get("PRODUCEQTY").toString());
                String dpudlength = sourceMap.get("DPUDLENGTH").toString();
                BigDecimal produceQty = yetProduceQty.subtract(
                        allotpqty.multiply(punitratio)
                                .divide(dpunitratio,Integer.valueOf(dpudlength), RoundingMode.HALF_UP)
                );

                String orderNo = sourceMap.get("ORDERNO").toString();
                String item = sourceMap.get("ITEM").toString();

                UptBean ub1 = new UptBean("DCP_DEMAND");
                ub1.addUpdateValue("PRODUCEQTY", DataValues.newString(produceQty));

                ub1.addCondition("EID", DataValues.newString(eid));
                ub1.addCondition("ORDERNO",DataValues.newString(orderNo));
                ub1.addCondition("ITEM",DataValues.newString(item));
                this.addProcessData(new DataProcessBean(ub1));

                if(!solveDemandList.contains(orderNo)){
                    solveDemandList.add(orderNo);
                }
            }


            UptBean ub2 = new UptBean("DCP_PRODSCHEDULE");
            ub2.addUpdateValue("STATUS", DataValues.newString("0"));
            ub2.addUpdateValue("MODIFYBY", DataValues.newString(employeeNo));
            ub2.addUpdateValue("MODIFYTIME",DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("CONFIRMBY", DataValues.newString(""));
            ub2.addUpdateValue("CONFIRMTIME", DataValues.newDate(null));
            ub2.addCondition("EID", DataValues.newString(eid));
            ub2.addCondition("BILLNO",DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub2));

        }

        if(Constant.OPR_TYPE_CONFIRM.equals(opType)){
            if(!"0".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非【新建】不可审核！");
            }

            String genSQL="SELECT * FROM DCP_PRODSCHEDULE_GEN a where a.eid='"+eid+"' and a.organizationno='"+organizationNO+"' and a.billno='"+billNo+"'";
            List<Map<String, Object>> genList = this.doQueryData(genSQL, null);
            if(genList.size()<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "生产任务未生成，请检查！");
            }


            String sourceSql="select d.unitratio as punitratio,h.unitratio as dpunitratio ,c.udlength as pudlength,g.udlength as dpudlength," +
                    " f.orderno,f.item,a.ALLOTPQTY,f.PRODUCEQTY " +
                    " from  DCP_PRODSCHEDULE_SOURCE a " +
                    " inner join DCP_PRODSCHEDULE_DETAIL b on a.eid=b.eid and a.organizationno=b.organizationno and a.billno=b.billno and a.oitem=b.item " +
                    " left join dcp_unit c on c.eid=a.eid and c.unit=b.punit " +//生产单位
                    " left join dcp_goods_unit d on d.eid=a.eid and d.pluno=b.pluno and d.unit=c.unit and d.ounit=b.baseunit " +
                    " left join dcp_demand f on f.eid=a.eid and f.orderno=a.orderno and f.item=a.orderitem " +
                    " left join dcp_unit g on g.eid=a.eid and g.unit=f.punit " +//需求单位
                    " left join dcp_goods_unit h on h.eid=a.eid and h.pluno=b.pluno and h.unit=g.unit and h.ounit=b.baseunit " +
                    " ";
            sourceSql+=" where a.eid='"+eid+"' and a.organizationno='"+organizationNO+"' and a.billno='"+billNo+"'";
            List<Map<String, Object>> sourceList = this.doQueryData(sourceSql, null);
            for(Map<String, Object> sourceMap:sourceList){
                BigDecimal allotpqty = new BigDecimal(sourceMap.get("ALLOTPQTY").toString());
                BigDecimal punitratio = new BigDecimal(sourceMap.get("PUNITRATIO").toString());
                //BigDecimal pudlength = new BigDecimal(sourceMap.get("PUDLENGTH").toString());
                BigDecimal dpunitratio = new BigDecimal(sourceMap.get("DPUNITRATIO").toString());
                BigDecimal yetProduceQty = new BigDecimal(sourceMap.get("PRODUCEQTY").toString());
                String dpudlength = sourceMap.get("DPUDLENGTH").toString();
                BigDecimal produceQty = yetProduceQty
                        .add(allotpqty.multiply(punitratio)
                                .divide(dpunitratio,Integer.valueOf(dpudlength), RoundingMode.HALF_UP));

                String orderNo = sourceMap.get("ORDERNO").toString();
                String item = sourceMap.get("ITEM").toString();

                UptBean ub1 = new UptBean("DCP_DEMAND");
                ub1.addUpdateValue("PRODUCEQTY", DataValues.newString(produceQty));

                ub1.addCondition("EID", DataValues.newString(eid));
                ub1.addCondition("ORDERNO",DataValues.newString(orderNo));
                ub1.addCondition("ITEM",DataValues.newString(item));
                this.addProcessData(new DataProcessBean(ub1));

                if(!solveDemandList.contains(orderNo)){
                    solveDemandList.add(orderNo);
                }
            }


            UptBean ub2 = new UptBean("DCP_PRODSCHEDULE");
            ub2.addUpdateValue("STATUS", DataValues.newString("1"));
            ub2.addUpdateValue("MODIFYBY", DataValues.newString(employeeNo));
            ub2.addUpdateValue("MODIFYTIME",DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("CONFIRMBY", DataValues.newString(employeeNo));
            ub2.addUpdateValue("CONFIRMTIME", DataValues.newDate(lastmoditime));


            ub2.addCondition("EID", DataValues.newString(eid));
            ub2.addCondition("BILLNO",DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub2));
        }

        this.doExecuteDataToDB();

        //通过dcp_demand 更新rorder  需求单
        if(CollUtil.isNotEmpty(solveDemandList)){
            String collect = solveDemandList.stream().map(x -> x).collect(Collectors.joining(","));
            collect+=",";
            Map<String, String> mapOrder=new HashMap<String, String>();
            mapOrder.put("ORDERNO", collect.toString());
            MyCommon cm=new MyCommon();
            String withasSql_mono=cm.getFormatSourceMultiColWith(mapOrder);

            String demandSql="with p as ("+withasSql_mono+") " +
                    " select a.* from dcp_demand a " +
                    " inner join p on p.orderno=a.orderno " +
                    " inner join DCP_RORDER_DETAIL c on c.eid=a.eid and c.organizationno=a.organizationno and c.RORDERNO=a.orderno and c.item=a.item " +
                    " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                    " ";
            List<Map<String, Object>> demandList = this.doQueryData(demandSql, null);

            //2.回写DCP_RORDER_DETAIL.STATUS,
            // 当对应DCP_DEMAND的PRODUCEQTY>=PQTY，则更新为2.已排产

            for (Map<String, Object> singleDemand : demandList) {
                String rOrderNo = singleDemand.get("ORDERNO").toString();
                String item = singleDemand.get("ITEM").toString();
                BigDecimal produceQty = new BigDecimal(singleDemand.get("PRODUCEQTY").toString());
                BigDecimal pqty = new BigDecimal(singleDemand.get("PQTY").toString());
                if(pqty.compareTo(produceQty)<=0){
                    UptBean ub = new UptBean("DCP_RORDER_DETAIL");
                    ub.addUpdateValue("STATUS", DataValues.newString("2"));

                    ub.addCondition("EID", DataValues.newString(eid));
                    ub.addCondition("RORDERNO",DataValues.newString(rOrderNo));
                    ub.addCondition("ITEM",DataValues.newString(item));
                    this.addProcessData(new DataProcessBean(ub));
                }

            }
            this.doExecuteDataToDB();

            //重查一遍
            String rOrderSql="with p as ("+withasSql_mono+") " +
                    " select b.* from DCP_RORDER_DETAIL a " +
                    " inner join p on  p.orderno=a.rorderno " +
                    " left join dcp_demand b on a.eid=b.eid and a.organizationno=b.organizationno and a.rorderno=b.orderno and a.item=b.item " +
                    " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"'  ";
            List<Map<String, Object>> list = this.doQueryData(rOrderSql, null);

            //.回写DCP_RORDER.STATUS，
            // 当单身对应的DCP_DEMAND所有的PRODUCEQTY>=PQTY，更新为2.全部排产；
            // 当单身对应的DCP_DEMAND有一笔PRODUCEQTY不为0且PRODUCEQTY<PQTY的，更新为3.部分排产

            for (String rOrderNo : solveDemandList) {
                List<Map<String, Object>> rOrderList = list.stream().filter(x -> x.get("ORDERNO").toString().equals(rOrderNo)).collect(Collectors.toList());
                if(rOrderList.size()>0){
                    List<Map<String, Object>> filterRows1 = rOrderList.stream().filter(x -> new BigDecimal(x.get("PRODUCEQTY").toString()).compareTo(new BigDecimal(x.get("PQTY").toString())) < 0).collect(Collectors.toList());
                    if(filterRows1.size()<=0){
                        UptBean ub = new UptBean("DCP_RORDER");
                        ub.addUpdateValue("STATUS", DataValues.newString("2"));

                        ub.addCondition("EID", DataValues.newString(eid));
                        ub.addCondition("RORDERNO",DataValues.newString(rOrderNo));
                        this.addProcessData(new DataProcessBean(ub));
                    }
                    else{
                        List<Map<String, Object>> filterRows12 = rOrderList.stream().filter(x -> new BigDecimal(x.get("PRODUCEQTY").toString()).compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
                        if(filterRows12.size()>0){
                            UptBean ub = new UptBean("DCP_RORDER");
                            ub.addUpdateValue("STATUS", DataValues.newString("3"));

                            ub.addCondition("EID", DataValues.newString(eid));
                            ub.addCondition("RORDERNO",DataValues.newString(rOrderNo));
                            this.addProcessData(new DataProcessBean(ub));
                        }
                    }
                }



            }

            this.doExecuteDataToDB();

        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProdScheduleStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProdScheduleStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProdScheduleStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected String getQuerySql(DCP_ProdScheduleStatusUpdateReq req) throws Exception {
        StringBuilder sb=new StringBuilder();
        sb.append("select * from DCP_PRODSCHEDULE a " +
                " where a.eid='"+req.geteId()+"' " +
                " and a.ORGANIZATIONNO='"+req.getOrganizationNO()+"' " +
                " and a.BILLNO='"+req.getRequest().getBillNo()+"' ");

        return sb.toString();
    }




}

