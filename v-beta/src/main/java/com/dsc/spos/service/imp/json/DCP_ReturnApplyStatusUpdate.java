package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ReturnApplyStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ReturnApplyStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.tax.TaxAmount2;
import com.dsc.spos.utils.tax.TaxAmountCalculation;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_ReturnApplyStatusUpdate extends SPosAdvanceService<DCP_ReturnApplyStatusUpdateReq, DCP_ReturnApplyStatusUpdateRes> {

    private static final String TYPE_SUBMIT = "submit";
    private static final String TYPE_WITHDRAW = "withdraw";
    private static final String TYPE_CANCEL = "cancel";
    private static final String TYPE_APPROVE = "approve";
    private static final String TYPE_REJECT = "reject";

    @Override
    protected boolean isVerifyFail(DCP_ReturnApplyStatusUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ReturnApplyStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_ReturnApplyStatusUpdateReq>(){};
    }

    @Override
    protected DCP_ReturnApplyStatusUpdateRes getResponseType() {
        return new DCP_ReturnApplyStatusUpdateRes();
    }

    @Override
    public void processDUID(DCP_ReturnApplyStatusUpdateReq req,DCP_ReturnApplyStatusUpdateRes res) throws Exception {
        String billNo = req.getRequest().getBillNo();
        String opType = req.getRequest().getOpType();
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();//审核的组织
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String bDate=new SimpleDateFormat("yyyyMMdd").format(new Date());
        String returnOrgNo="";


        String sql="select * from DCP_RETURNAPPLY a where a.eid='"+eId+"' and a.billNO='"+billNo+"'";
        List<Map<String, Object>> list = this.doQueryData(sql,null);
        if(CollUtil.isEmpty(list)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "单据不存在");
        }
        returnOrgNo=list.get(0).get("ORGANIZATIONNO").toString();
        String detailSql="select a.*,b.OUT_COST_WAREHOUSE,b.INV_COST_WAREHOUSE,nvl(c.RETURN_COST_WAREHOUSE,'') as RETURN_COST_WAREHOUSE,nvl(noticed.billno,'') as noticeno " +
                " from DCP_RETURNAPPLY_DETAIL a " +
                " left join dcp_org b on a.eid=b.eid and a.organizationno=b.organizationno " +
                " left join dcp_org c on a.eid=c.eid and a.approveorgno=c.organizationno " +
                " left join DCP_STOCKOUTNOTICE_detail noticed on noticed.eid=a.eid and noticed.ORGANIZATIONNO=a.ORGANIZATIONNO  and noticed.SOURCEBILLNO=a.billno and noticed.OITEM=a.item and noticed.SOURCETYPE='2'  " +
                " where a.eid='"+eId+"' and a.billNO='"+billNo+"'";
        List<Map<String, Object>> detailList = this.doQueryData(detailSql,null);
        String status = list.get(0).get("STATUS").toString();

        if(TYPE_SUBMIT.equals(opType)){
            if(!"0".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "单据状态非【0-新建】状态不可提交！");
            }

            UptBean ub2 = new UptBean("DCP_RETURNAPPLY");
            ub2.addUpdateValue("STATUS", DataValues.newString("1"));
            ub2.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("LASTMODITIME",DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("SUBMITBY", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("SUBMITTIME", DataValues.newDate(lastmoditime));

            ub2.addCondition("EID", DataValues.newString(eId));
            ub2.addCondition("BILLNO",DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub2));
        }

        if(TYPE_WITHDRAW.equals(opType)){
            //【撤销】：
            //撤销前检查：
            //1.单据状态非【1-待审核】状态不可撤销！
            //2.明细状态存在非【0-未核准】状态不可撤销！
            //撤销后处理：
            //1、更新单据状态=【0-新建】，更新提交人、提交时间字段为空，更新最新修改人、最近修改时间
            if(!"1".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "单据状态非【1-待审核】状态不可撤销！");
            }
            List<Map<String, Object>> filterRows = detailList.stream().filter(x -> !x.get("APPROVESTATUS").toString().equals("0")).collect(Collectors.toList());
            if(CollUtil.isNotEmpty(filterRows)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "明细状态存在非【0-未核准】状态不可撤销！");
            }
            UptBean ub2 = new UptBean("DCP_RETURNAPPLY");
            ub2.addUpdateValue("STATUS", DataValues.newString("0"));
            ub2.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("LASTMODITIME",DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("SUBMITBY", DataValues.newString(""));
            ub2.addUpdateValue("SUBMITTIME", DataValues.newDate(null));

            ub2.addCondition("EID", DataValues.newString(eId));
            ub2.addCondition("BILLNO",DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub2));
        }

        if(TYPE_CANCEL.equals(opType)){
            //【作废】：
            //作废前检查：
            //1.单据状态非【0-新建】状态不可作废！
            //作废后处理：
            //1、更新单据状态=【5-已作废】，更新作废人、作废时间；
            if(!"0".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "单据状态非【0-新建】状态不可作废！");
            }
            UptBean ub2 = new UptBean("DCP_RETURNAPPLY");
            ub2.addUpdateValue("STATUS", DataValues.newString("5"));
            ub2.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("LASTMODITIME",DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("CANCELBY", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("CANCELTIME", DataValues.newDate(lastmoditime));

            ub2.addCondition("EID", DataValues.newString(eId));
            ub2.addCondition("BILLNO",DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub2));
        }

        if(TYPE_REJECT.equals(opType)){
            //【驳回】：
            //驳回前检查：
            //1.明细状态非【0-未核准】状态不可驳回！
            //2前所在组织视角与明细对应核准组织不一致不可执行驳回！（非权责范围内）
            //驳回后处理：
            //1、更新退货申请明细核准日期、核准人员、核准部门、核准数量(=0)、核准单价(=0)；
            // 数据源：DCP_RETURNAPPLY_DETAIL
            //2、更新明细核准状态=[2-已驳回]；单头状态更新判断：
            //● 明细状态全部=[1-已核准]，则单头状态=【3-审核完成】；
            //● 明细状态全部=[2-已驳回]，则单头状态=【4-已驳回】；
            //● 不满足以上两种情况则单头状态=【2-部分审核】（部分核准、部分驳回）

            List<DCP_ReturnApplyStatusUpdateReq.GoodList> goodsList = req.getRequest().getGoodsList();
            if(CollUtil.isNotEmpty(goodsList)) {
                for (DCP_ReturnApplyStatusUpdateReq.GoodList good : goodsList) {
                    List<Map<String, Object>> filterRows0 = detailList.stream().filter(x -> x.get("ITEM").toString().equals(good.getItem())).collect(Collectors.toList());
                    List<Map<String, Object>> filterRows = filterRows0.stream().filter(x -> !x.get("APPROVESTATUS").toString().equals("0")).collect(Collectors.toList());
                    if(CollUtil.isNotEmpty(filterRows)){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "明细状态存在非【0-未核准】状态不可驳回！");
                    }
                    List<Map<String, Object>> filterRows2 = filterRows0.stream().filter(x -> !x.get("APPROVEORGNO").toString().equals(organizationNO)).collect(Collectors.toList());
                    if(CollUtil.isNotEmpty(filterRows2)){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "所在组织视角与明细对应核准组织不一致不可执行驳回！");
                    }
                }
            }else {
                List<Map<String, Object>> filterRows = detailList.stream().filter(x -> !x.get("APPROVESTATUS").toString().equals("0")).collect(Collectors.toList());
                if(CollUtil.isNotEmpty(filterRows)){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "明细状态存在非【0-未核准】状态不可驳回！");
                }
                List<Map<String, Object>> filterRows2 = detailList.stream().filter(x -> !x.get("APPROVEORGNO").toString().equals(organizationNO)).collect(Collectors.toList());
                if(CollUtil.isNotEmpty(filterRows2)){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "所在组织视角与明细对应核准组织不一致不可执行驳回！");
                }
            }

            if(CollUtil.isNotEmpty(goodsList)) {
                for (DCP_ReturnApplyStatusUpdateReq.GoodList good : goodsList){
                    UptBean ub1 = new UptBean("DCP_RETURNAPPLY_DETAIL");
                    ub1.addUpdateValue("APPROVESTATUS", DataValues.newString("2"));
                    ub1.addUpdateValue("REASON", DataValues.newString(good.getReason()));
                    ub1.addUpdateValue("APPROVEQTY", DataValues.newString("0"));
                    ub1.addUpdateValue("APPROVEEMPID", DataValues.newString(employeeNo));
                    ub1.addUpdateValue("APPROVEDATE", DataValues.newString(bDate));
                    ub1.addUpdateValue("APPROVEPRICE", DataValues.newString("0"));
                    ub1.addUpdateValue("APPROVEDEPTID", DataValues.newString(departmentNo));
                    ub1.addCondition("EID", DataValues.newString(eId));
                    ub1.addCondition("BILLNO", DataValues.newString(billNo));
                    ub1.addCondition("ITEM", DataValues.newString(good.getItem()));
                    this.addProcessData(new DataProcessBean(ub1));
                }

                List<Map> approveStatusList = detailList.stream().map(x -> {
                    Map map = new HashMap();
                    map.put("ITEM", x.get("ITEM").toString());
                    map.put("APPROVESTATUS", x.get("APPROVESTATUS").toString());
                    return map;
                }).distinct().collect(Collectors.toList());

                for (Map apMap : approveStatusList){
                    String item = apMap.get("ITEM").toString();
                    List<DCP_ReturnApplyStatusUpdateReq.GoodList> goodListf = goodsList.stream().filter(x -> x.getItem().equals(item)).collect(Collectors.toList());
                    if(CollUtil.isNotEmpty(goodListf)){
                        apMap.put("APPROVESTATUS","2");
                    }
                }
                List<String> singleStatusList = approveStatusList.stream().map(x -> x.get("APPROVESTATUS").toString()).distinct().collect(Collectors.toList());
                //approveStatus  0 1 2
                String mainStatus="";
                if(singleStatusList.size()==1){
                    mainStatus="4";//驳回
                }else{
                    mainStatus="2";//多种审核状态  单头就是部分的
                }


                UptBean ub2 = new UptBean("DCP_RETURNAPPLY");
                ub2.addUpdateValue("STATUS", DataValues.newString(mainStatus));
                ub2.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getOpNO()));
                ub2.addUpdateValue("LASTMODITIME",DataValues.newDate(lastmoditime));
                ub2.addUpdateValue("CONFIRMBY", DataValues.newString(req.getOpNO()));
                ub2.addUpdateValue("CONFIRMTIME", DataValues.newDate(lastmoditime));
                ub2.addCondition("EID", DataValues.newString(eId));
                ub2.addCondition("BILLNO",DataValues.newString(billNo));
                this.addProcessData(new DataProcessBean(ub2));


            }else {

                UptBean ub1 = new UptBean("DCP_RETURNAPPLY_DETAIL");
                ub1.addUpdateValue("APPROVESTATUS", DataValues.newString("2"));
                ub1.addUpdateValue("APPROVEQTY", DataValues.newString("0"));
                ub1.addUpdateValue("APPROVEEMPID", DataValues.newString(employeeNo));
                ub1.addUpdateValue("APPROVEDATE", DataValues.newString(bDate));
                ub1.addUpdateValue("APPROVEPRICE", DataValues.newString("0"));
                ub1.addUpdateValue("APPROVEDEPTID", DataValues.newString(departmentNo));
                ub1.addCondition("EID", DataValues.newString(eId));
                ub1.addCondition("BILLNO", DataValues.newString(billNo));
                this.addProcessData(new DataProcessBean(ub1));


                UptBean ub2 = new UptBean("DCP_RETURNAPPLY");
                ub2.addUpdateValue("STATUS", DataValues.newString("4"));
                ub2.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getOpNO()));
                ub2.addUpdateValue("LASTMODITIME", DataValues.newDate(lastmoditime));
                ub2.addUpdateValue("CONFIRMBY", DataValues.newString(req.getOpNO()));
                ub2.addUpdateValue("CONFIRMTIME", DataValues.newDate(lastmoditime));
                ub2.addCondition("EID", DataValues.newString(eId));
                ub2.addCondition("BILLNO", DataValues.newString(billNo));
                this.addProcessData(new DataProcessBean(ub2));
            }

        }

        if(TYPE_APPROVE.equals(opType)){
            List<DCP_ReturnApplyStatusUpdateReq.GoodList> goodList = req.getRequest().getGoodsList();

            if(CollUtil.isNotEmpty(goodList)) {
                for (DCP_ReturnApplyStatusUpdateReq.GoodList good : goodList) {
                    List<Map<String, Object>> filterRows0 = detailList.stream().filter(x -> x.get("ITEM").toString().equals(good.getItem())).collect(Collectors.toList());
                    List<Map<String, Object>> filterRows = filterRows0.stream().filter(x -> !x.get("APPROVESTATUS").toString().equals("0")).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(filterRows)) {
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "明细状态存在非【0-未核准】状态不可核准！");
                    }
                    List<Map<String, Object>> filterRows2 = filterRows0.stream().filter(x -> !x.get("APPROVEORGNO").toString().equals(organizationNO)).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(filterRows2)) {
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "所在组织视角与明细对应核准组织不一致不可执行核准！");
                    }
                }
            }else {
                List<Map<String, Object>> filterRows = detailList.stream().filter(x -> !x.get("APPROVESTATUS").toString().equals("0")).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(filterRows)) {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "明细状态存在非【0-未核准】状态不可核准！");
                }
                List<Map<String, Object>> filterRows2 = detailList.stream().filter(x -> !x.get("APPROVEORGNO").toString().equals(organizationNO)).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(filterRows2)) {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "所在组织视角与明细对应核准组织不一致不可执行核准！");
                }
            }

            if(CollUtil.isNotEmpty(goodList)){
                for (DCP_ReturnApplyStatusUpdateReq.GoodList good : goodList){
                    UptBean ub1 = new UptBean("DCP_RETURNAPPLY_DETAIL");
                    ub1.addUpdateValue("APPROVESTATUS", DataValues.newString("1"));
                    ub1.addUpdateValue("APPROVEQTY", DataValues.newString(good.getApproveQty()));
                    ub1.addUpdateValue("APPROVEEMPID", DataValues.newString(req.getEmployeeNo()));
                    ub1.addUpdateValue("APPROVEDATE", DataValues.newString(bDate));
                    ub1.addUpdateValue("APPROVEPRICE", DataValues.newString(good.getApprovePrice()));
                    ub1.addUpdateValue("APPROVEDEPTID", DataValues.newString(req.getDepartmentNo()));
                    ub1.addUpdateValue("REASON", DataValues.newString(good.getReason()));

                    ub1.addCondition("EID", DataValues.newString(eId));
                    ub1.addCondition("BILLNO",DataValues.newString(billNo));
                    ub1.addCondition("ITEM",DataValues.newString(good.getItem()));
                    ub1.addCondition("PLUNO",DataValues.newString(good.getPluNo()));
                    ub1.addCondition("FEATURENO",DataValues.newString(good.getFeatureNo()));
                    this.addProcessData(new DataProcessBean(ub1));
                }

                List<Map> approveStatusList = detailList.stream().map(x -> {
                    Map map = new HashMap();
                    map.put("ITEM", x.get("ITEM").toString());
                    map.put("APPROVESTATUS", x.get("APPROVESTATUS").toString());
                    return map;
                }).distinct().collect(Collectors.toList());

                for (Map apMap : approveStatusList){
                    String item = apMap.get("ITEM").toString();
                    List<DCP_ReturnApplyStatusUpdateReq.GoodList> goodListf = goodList.stream().filter(x -> x.getItem().equals(item)).collect(Collectors.toList());
                    if(CollUtil.isNotEmpty(goodListf)){
                        apMap.put("APPROVESTATUS","1");
                    }
                }
                List<String> singleStatusList = approveStatusList.stream().map(x -> x.get("APPROVESTATUS").toString()).distinct().collect(Collectors.toList());
                //approveStatus  0 1 2
                String mainStatus="";
                if(singleStatusList.size()==1){
                    mainStatus="3";//只有一种 再加上是审核  就是全部审核了
                }else{
                    mainStatus="2";//多种审核状态  单头就是部分的
                }


                UptBean ub2 = new UptBean("DCP_RETURNAPPLY");
                ub2.addUpdateValue("STATUS", DataValues.newString(mainStatus));
                ub2.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getOpNO()));
                ub2.addUpdateValue("LASTMODITIME",DataValues.newDate(lastmoditime));
                ub2.addUpdateValue("CONFIRMBY", DataValues.newString(req.getOpNO()));
                ub2.addUpdateValue("CONFIRMTIME", DataValues.newDate(lastmoditime));
                ub2.addCondition("EID", DataValues.newString(eId));
                ub2.addCondition("BILLNO",DataValues.newString(billNo));
                this.addProcessData(new DataProcessBean(ub2));

            }
            else{

                for (Map<String,Object> singleMap : detailList){
                    String item = singleMap.get("ITEM").toString();

                    UptBean ub1 = new UptBean("DCP_RETURNAPPLY_DETAIL");
                    ub1.addUpdateValue("APPROVESTATUS", DataValues.newString("1"));
                    ub1.addUpdateValue("APPROVEQTY", DataValues.newString(singleMap.get("POQTY").toString()));
                    ub1.addUpdateValue("APPROVEEMPID", DataValues.newString(employeeNo));
                    ub1.addUpdateValue("APPROVEDATE", DataValues.newString(bDate));
                    ub1.addUpdateValue("APPROVEPRICE", DataValues.newString(singleMap.get("PRICE").toString()));
                    ub1.addUpdateValue("APPROVEDEPTID", DataValues.newString(departmentNo));

                    ub1.addCondition("EID", DataValues.newString(eId));
                    ub1.addCondition("BILLNO",DataValues.newString(billNo));
                    ub1.addCondition("ITEM",DataValues.newString(item));
                    this.addProcessData(new DataProcessBean(ub1));

                }

                UptBean ub2 = new UptBean("DCP_RETURNAPPLY");
                ub2.addUpdateValue("STATUS", DataValues.newString("3"));
                ub2.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getOpNO()));
                ub2.addUpdateValue("LASTMODITIME",DataValues.newDate(lastmoditime));
                ub2.addUpdateValue("CONFIRMBY", DataValues.newString(req.getOpNO()));
                ub2.addUpdateValue("CONFIRMTIME", DataValues.newDate(lastmoditime));
                ub2.addCondition("EID", DataValues.newString(eId));
                ub2.addCondition("BILLNO",DataValues.newString(billNo));
                this.addProcessData(new DataProcessBean(ub2));
            }
            //先把数据给更新了
            this.doExecuteDataToDB();

            String pTempSql="select a.PURTEMPLATENO,b.pluno,a.PURTYPE,a.supplierno,b.purbaseprice,b.taxcode,d.TAXRATE,d.INCLTAX,d.TAXCALTYPE,e.corp" +
                    " from " +
                    " DCP_PURCHASETEMPLATE a" +
                    " inner join DCP_PURCHASETEMPLATE_goods b on a.eid=b.eid and a.PURTEMPLATENO=b.PURTEMPLATENO " +
                    " inner join DCP_PURCHASETEMPLATE_ORG c on a.eid=c.eid and a.purtemplateno=c.purtemplateno " +
                    " left join DCP_TAXCATEGORY d on b.eid=d.eid and b.taxcode=d.taxcode " +
                    " left join dcp_org e on e.eid=a.eid and e.organizationno=a.purcenter " +
                    " where a.eid='"+eId+"' " +
                    " and c.organizationno='"+returnOrgNo+"' " +
                    " and a.status='100'" +
                    " and b.status='100' and c.status='100'";
            List<Map<String, Object>> pTempList = this.doQueryData(pTempSql, null);


            //RETURNTYPE 1.退配 2.退供 APPROVESTATUS=1
            List<Map<String, Object>> detailList2 = this.doQueryData(detailSql,null);
            List<String> detailItems = goodList.stream().map(x -> x.getItem()).distinct().collect(Collectors.toList());
            List<Map<String, Object>> detailList3=detailList2;
            if(detailItems.size()>0)
            {
                detailList3 = detailList2.stream().filter(x -> detailItems.contains(x.get("ITEM").toString())).collect(Collectors.toList());
            }


            List<Map<String, Object>> tpList = detailList3.stream().filter(x -> x.get("RETURNTYPE").toString().equals("1") && x.get("APPROVESTATUS").toString().equals("1")&&x.get("NOTICENO").toString().length()<=0).collect(Collectors.toList());
            if(CollUtil.isNotEmpty(tpList)){
                String detailOrgNo = tpList.get(0).get("ORGANIZATIONNO").toString();
                String outCostWarehouse = tpList.get(0).get("OUT_COST_WAREHOUSE").toString();
                String invCostWarehouse = tpList.get(0).get("INV_COST_WAREHOUSE").toString();
                //退货仓库
                String returnCostWarehouse = tpList.get(0).get("RETURN_COST_WAREHOUSE").toString();
                String bsno = tpList.get(0).get("BSNO").toString();
                BigDecimal totPQty=new BigDecimal(0);
                BigDecimal totAmt=new BigDecimal(0);
                BigDecimal totRetailAmt=new BigDecimal(0);
                BigDecimal totPreTaxAmt=new BigDecimal(0);
                BigDecimal totTaxAmt=new BigDecimal(0);
                BigDecimal totCQty=new BigDecimal(0);
                List<Map> pfList = tpList.stream().map(x -> {
                    Map map = new HashMap();
                    map.put("PLUNO", x.get("PLUNO").toString());
                    map.put("FEATURE", x.get("FEATURENO").toString());
                    return map;
                }).distinct().collect(Collectors.toList());
                totCQty=new BigDecimal(pfList.size());

                String noticeNo = this.getOrderNO(req,detailOrgNo, "THTZ");
                String receiptOrgNo="";
                int ndItem=0;
                for (Map<String, Object> map : tpList){
                    ndItem++;
                    List<Map<String, Object>> pFilterRows = pTempList.stream().filter(x -> x.get("PLUNO").toString().equals(map.get("PLUNO").toString())).collect(Collectors.toList());
                    String supplierType = map.get("SUPPLIERTYPE").toString();
                    String approvePrice = map.get("APPROVEPRICE").toString();
                    String distriPrice = map.get("DISTRIPRICE").toString();
                    String approveQty = map.get("APPROVEQTY").toString();
                    String unitRatio = Check.Null(map.get("UNITRATIO").toString())?"0":map.get("UNITRATIO").toString();
                    receiptOrgNo = map.get("RECEIPTORGNO").toString();
                    String templateNo="";
                    String taxCode="";
                    String inclTax="";
                    String taxCalType="";
                    String taxRate="0";
                    BigDecimal noticePrice=BigDecimal.ZERO;
                    if(Check.NotNull(approvePrice)){
                        noticePrice=new BigDecimal(approvePrice);
                    }
                    else {
                        if ("FACTORY".equals(supplierType)) {//统配
                            if(Check.NotNull(distriPrice)){
                                noticePrice=new BigDecimal(distriPrice);
                            }
                        }
                        if ("SUPPLIER".equals(supplierType)) {//采购
                            //商品所在采购模板最新采购基准价PURBASEPRICE
                            if(CollUtil.isNotEmpty(pFilterRows)){
                                noticePrice=new BigDecimal(pFilterRows.get(0).get("PURBASEPRICE").toString());
                            }
                        }
                    }
                    BigDecimal noticeAmount = noticePrice.multiply(new BigDecimal(approveQty));

                    if(CollUtil.isNotEmpty(pFilterRows)){
                        taxCode=pFilterRows.get(0).get("TAXCODE").toString();
                        taxCalType=pFilterRows.get(0).get("TAXCALTYPE").toString();
                        inclTax=pFilterRows.get(0).get("INCLTAX").toString();
                        taxRate=pFilterRows.get(0).get("TAXRATE").toString();
                        templateNo=pFilterRows.get(0).get("PURTEMPLATENO").toString();
                    }
                    taxRate=new BigDecimal(taxRate).divide(new BigDecimal(100)).toString();
                    TaxAmount2 taxAmount2 = TaxAmountCalculation.calculateAmount(inclTax, noticeAmount, new BigDecimal(taxRate), taxCalType, 2);
                    BigDecimal baseQty = new BigDecimal(approveQty).multiply(new BigDecimal(unitRatio));
                    BigDecimal pQty = new BigDecimal(map.get("APPROVEQTY").toString());
                    totPQty=pQty.add(pQty);
                    totAmt=totAmt.add(noticeAmount);
                    totPreTaxAmt=totPreTaxAmt.add(taxAmount2.getPreAmount());
                    totTaxAmt=totTaxAmt.add(taxAmount2.getTaxAmount());
                    totRetailAmt=totRetailAmt.add(new BigDecimal(map.get("AMT").toString()));
                    if(Check.NotNull(map.get("BSNO").toString())){
                        bsno=map.get("BSNO").toString();
                    }
                    ColumnDataValue noticeDetailColumns=new ColumnDataValue();
                    noticeDetailColumns.add("EID",eId,Types.VARCHAR);
                    noticeDetailColumns.add("ORGANIZATIONNO",detailOrgNo,Types.VARCHAR);
                    noticeDetailColumns.add("BILLNO",noticeNo,Types.VARCHAR);
                    noticeDetailColumns.add("SOURCETYPE","2",Types.VARCHAR);
                    noticeDetailColumns.add("SOURCEBILLNO",billNo,Types.VARCHAR);
                    noticeDetailColumns.add("OITEM",map.get("ITEM").toString(),Types.VARCHAR);
                    noticeDetailColumns.add("ITEM",ndItem,Types.VARCHAR);
                    noticeDetailColumns.add("PLUNO",map.get("PLUNO").toString(),Types.VARCHAR);
                    noticeDetailColumns.add("FEATURENO",map.get("FEATURENO").toString(),Types.VARCHAR);
                    noticeDetailColumns.add("PLUBARCODE",map.get("PLUBARCODE").toString(),Types.VARCHAR);
                    noticeDetailColumns.add("TEMPLATENO",templateNo,Types.VARCHAR);
                    noticeDetailColumns.add("PUNIT",map.get("PUNIT").toString(),Types.VARCHAR);
                    noticeDetailColumns.add("PQTY",pQty.toString(),Types.VARCHAR);
                    noticeDetailColumns.add("WAREHOUSE",map.get("OUT_COST_WAREHOUSE").toString(),Types.VARCHAR);
                    noticeDetailColumns.add("PRICE",noticePrice,Types.VARCHAR);
                    noticeDetailColumns.add("AMOUNT",noticeAmount.toString(),Types.VARCHAR);
                    noticeDetailColumns.add("PRETAXAMT",map.get("PRETAXAMT"),Types.VARCHAR);
                    noticeDetailColumns.add("TAXAMT",taxAmount2.getTaxAmount().toString(),Types.VARCHAR);
                    noticeDetailColumns.add("TAXCALTYPE",taxCalType,Types.VARCHAR);
                    noticeDetailColumns.add("TAXCODE",taxCode,Types.VARCHAR);
                    noticeDetailColumns.add("TAXRATE",taxRate,Types.VARCHAR);
                    noticeDetailColumns.add("INCLTAX",inclTax,Types.VARCHAR);
                    noticeDetailColumns.add("BSNO",map.get("BSNO").toString(),Types.VARCHAR);
                    noticeDetailColumns.add("BASEUNIT",map.get("BASEUNIT"),Types.VARCHAR);
                    noticeDetailColumns.add("BASEQTY",baseQty.toString(),Types.VARCHAR);
                    noticeDetailColumns.add("UNITRATIO",unitRatio,Types.VARCHAR);
                    noticeDetailColumns.add("STOCKOUTQTY","0",Types.VARCHAR);
                    noticeDetailColumns.add("STATUS","1",Types.VARCHAR);
                    noticeDetailColumns.add("MEMO","",Types.VARCHAR);
                    noticeDetailColumns.add("OBJECTTYPE","3",Types.VARCHAR);
                    noticeDetailColumns.add("OBJECTID",receiptOrgNo,Types.VARCHAR);
                    noticeDetailColumns.add("TEMPLATETYPE","1",Types.VARCHAR);
                    noticeDetailColumns.add("RETAILPRICE",map.get("PRICE").toString(),Types.VARCHAR);
                    noticeDetailColumns.add("RETAILAMT",map.get("AMT").toString(),Types.VARCHAR);
                    noticeDetailColumns.add("POQTY",map.get("POQTY").toString(),Types.VARCHAR);
                    noticeDetailColumns.add("NOQTY",map.get("APPROVEQTY").toString(),Types.VARCHAR);


                    String[] noticeDetailColumnNames = noticeDetailColumns.getColumns().toArray(new String[0]);
                    DataValue[] noticeDetailDataValues = noticeDetailColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean ib1=new InsBean("DCP_STOCKOUTNOTICE_DETAIL",noticeDetailColumnNames);
                    ib1.addValues(noticeDetailDataValues);
                    this.addProcessData(new DataProcessBean(ib1));
                }

                ColumnDataValue noticeColumns=new ColumnDataValue();
                noticeColumns.add("EID",eId,Types.VARCHAR);
                noticeColumns.add("ORGANIZATIONNO",detailOrgNo,Types.VARCHAR);
                noticeColumns.add("BILLNO",noticeNo,Types.VARCHAR);
                noticeColumns.add("BILLTYPE","4",Types.VARCHAR);
                noticeColumns.add("SOURCETYPE","2",Types.VARCHAR);
                noticeColumns.add("SOURCEBILLNO",billNo,Types.VARCHAR);
                noticeColumns.add("RDATE",bDate,Types.VARCHAR);
                noticeColumns.add("BDATE",bDate,Types.VARCHAR);
                noticeColumns.add("OBJECTTYPE","3",Types.VARCHAR);
                noticeColumns.add("OBJECTID",receiptOrgNo,Types.VARCHAR);
                //noticeColumns.add("PAYTYPE","",Types.VARCHAR);
                //noticeColumns.add("PAYORGNO","",Types.VARCHAR);
                //noticeColumns.add("BILLDATENO","",Types.VARCHAR);
                //noticeColumns.add("PAYDATENO","",Types.VARCHAR);
                //noticeColumns.add("INVOICECODE","",Types.VARCHAR);
                //noticeColumns.add("CURRENCY","",Types.VARCHAR);
                noticeColumns.add("DELIVERORGNO",detailOrgNo,Types.VARCHAR);
                noticeColumns.add("INVWAREHOUSE",invCostWarehouse,Types.VARCHAR);
                noticeColumns.add("RECEIPTWAREHOUSE",returnCostWarehouse,Types.VARCHAR);
                noticeColumns.add("WAREHOUSE",outCostWarehouse,Types.VARCHAR);
                noticeColumns.add("BSNO","",Types.VARCHAR);
                //noticeColumns.add("RETURNTYPE","",Types.VARCHAR);
                noticeColumns.add("TOTCQTY",totCQty.toString(),Types.VARCHAR);
                noticeColumns.add("TOTPQTY",totPQty.toString(),Types.VARCHAR);
                noticeColumns.add("TOTSTOCKOUTQTY","0",Types.VARCHAR);
                noticeColumns.add("TOTAMT",totAmt.toString(),Types.VARCHAR);
                noticeColumns.add("TOTPRETAXAMT",totPreTaxAmt.toString(),Types.VARCHAR);
                noticeColumns.add("TOTTAXAMT",totTaxAmt.toString(),Types.VARCHAR);
                noticeColumns.add("EMPLOYEEID",employeeNo,Types.VARCHAR);
                noticeColumns.add("DEPARTID",departmentNo,Types.VARCHAR);
                noticeColumns.add("STATUS","1",Types.VARCHAR);
                noticeColumns.add("MEMO","",Types.VARCHAR);
                noticeColumns.add("OWNOPID",req.getOpNO(),Types.VARCHAR);
                noticeColumns.add("OWNDEPTID",departmentNo,Types.VARCHAR);
                noticeColumns.add("CREATEOPID",req.getOpNO(),Types.VARCHAR);
                noticeColumns.add("CREATEDEPTID",departmentNo,Types.VARCHAR);
                noticeColumns.add("CREATETIME",lastmoditime,Types.DATE);
                noticeColumns.add("CONFIRMBY",req.getOpNO(),Types.VARCHAR);
                noticeColumns.add("CONFIRMTIME",lastmoditime,Types.DATE);
                noticeColumns.add("TOTRETAILAMT",totRetailAmt.toString(),Types.VARCHAR);

                String[] noticeColumnNames = noticeColumns.getColumns().toArray(new String[0]);
                DataValue[] noticeDataValues = noticeColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib1=new InsBean("DCP_STOCKOUTNOTICE",noticeColumnNames);
                ib1.addValues(noticeDataValues);
                this.addProcessData(new DataProcessBean(ib1));

            }

            //退供
            List<Map<String, Object>> tgList = detailList3.stream().filter(x -> x.get("RETURNTYPE").toString().equals("2") && x.get("APPROVESTATUS").toString().equals("1")&&x.get("NOTICENO").toString().length()<=0).collect(Collectors.toList());
            if(CollUtil.isNotEmpty(tgList)){
                String detailOrgNo = tgList.get(0).get("ORGANIZATIONNO").toString();
                String outCostWarehouse = tgList.get(0).get("OUT_COST_WAREHOUSE").toString();
                String invCostWarehouse = tgList.get(0).get("INV_COST_WAREHOUSE").toString();

                String bsno = tgList.get(0).get("BSNO").toString();
                BigDecimal totPQty=new BigDecimal(0);
                BigDecimal totAmt=new BigDecimal(0);
                BigDecimal totPreTaxAmt=new BigDecimal(0);
                BigDecimal totTaxAmt=new BigDecimal(0);
                BigDecimal totCQty=new BigDecimal(0);
                BigDecimal totRetailAmt=new BigDecimal(0);
                List<Map> pfList = tgList.stream().map(x -> {
                    Map map = new HashMap();
                    map.put("PLUNO", x.get("PLUNO").toString());
                    map.put("FEATURE", x.get("FEATURENO").toString());
                    return map;
                }).distinct().collect(Collectors.toList());
                totCQty=new BigDecimal(pfList.size());

                String noticeNo = this.getOrderNO(req,detailOrgNo, "CTTZ");
                String supplier="";
                int ndItem=0;
                for (Map<String, Object> map : tgList){
                    ndItem++;
                    List<Map<String, Object>> pFilterRows = pTempList.stream().filter(x -> x.get("PLUNO").toString().equals(map.get("PLUNO").toString())).collect(Collectors.toList());
                    String supplierType = map.get("SUPPLIERTYPE").toString();
                    String approvePrice = map.get("APPROVEPRICE").toString();
                    String distriPrice = map.get("DISTRIPRICE").toString();
                    String approveQty = map.get("APPROVEQTY").toString();
                    String unitRatio = Check.Null(map.get("UNITRATIO").toString())?"0":map.get("UNITRATIO").toString();
                    supplier= map.get("SUPPLIERID").toString();
                    String templateNo="";
                    String taxCode="";
                    String inclTax="";
                    String taxCalType="";
                    String taxRate="0";
                    BigDecimal noticePrice=BigDecimal.ZERO;
                    if(Check.NotNull(approvePrice)){
                        noticePrice=new BigDecimal(approvePrice);
                    }
                    else {
                        if ("FACTORY".equals(supplierType)) {//统配
                            if(Check.NotNull(distriPrice)){
                                noticePrice=new BigDecimal(distriPrice);
                            }
                        }
                        if ("SUPPLIER".equals(supplierType)) {//采购
                            //商品所在采购模板最新采购基准价PURBASEPRICE
                            if(CollUtil.isNotEmpty(pFilterRows)){
                                noticePrice=new BigDecimal(pFilterRows.get(0).get("PURBASEPRICE").toString());
                            }
                        }
                    }
                    BigDecimal noticeAmount = noticePrice.multiply(new BigDecimal(approveQty));

                    if(CollUtil.isNotEmpty(pFilterRows)){
                        taxCode=pFilterRows.get(0).get("TAXCODE").toString();
                        taxCalType=pFilterRows.get(0).get("TAXCALTYPE").toString();
                        inclTax=pFilterRows.get(0).get("INCLTAX").toString();
                        taxRate=pFilterRows.get(0).get("TAXRATE").toString();
                        templateNo=pFilterRows.get(0).get("PURTEMPLATENO").toString();
                    }
                    taxRate=new BigDecimal(taxRate).divide(new BigDecimal(100)).toString();
                    TaxAmount2 taxAmount2 = TaxAmountCalculation.calculateAmount(inclTax, noticeAmount, new BigDecimal(taxRate), taxCalType, 2);
                    BigDecimal baseQty = new BigDecimal(approveQty).multiply(new BigDecimal(unitRatio));
                    BigDecimal pQty = new BigDecimal(map.get("APPROVEQTY").toString());
                    totPQty=totPQty.add(pQty);
                    totAmt=totAmt.add(noticeAmount);
                    totPreTaxAmt=totPreTaxAmt.add(taxAmount2.getPreAmount());
                    totTaxAmt=totTaxAmt.add(taxAmount2.getTaxAmount());
                    totRetailAmt=totRetailAmt.add(new BigDecimal(map.get("AMT").toString()));

                    if(Check.NotNull(map.get("BSNO").toString())){
                        bsno=map.get("BSNO").toString();
                    }

                    ColumnDataValue noticeDetailColumns=new ColumnDataValue();
                    noticeDetailColumns.add("EID",eId,Types.VARCHAR);
                    noticeDetailColumns.add("ORGANIZATIONNO",detailOrgNo,Types.VARCHAR);
                    noticeDetailColumns.add("BILLNO",noticeNo,Types.VARCHAR);
                    noticeDetailColumns.add("SOURCETYPE","2",Types.VARCHAR);
                    noticeDetailColumns.add("SOURCEBILLNO",billNo,Types.VARCHAR);
                    noticeDetailColumns.add("OITEM",map.get("ITEM").toString(),Types.VARCHAR);
                    noticeDetailColumns.add("ITEM",ndItem,Types.VARCHAR);
                    noticeDetailColumns.add("PLUNO",map.get("PLUNO").toString(),Types.VARCHAR);
                    noticeDetailColumns.add("FEATURENO",map.get("FEATURENO").toString(),Types.VARCHAR);
                    noticeDetailColumns.add("PLUBARCODE",map.get("PLUBARCODE").toString(),Types.VARCHAR);
                    noticeDetailColumns.add("TEMPLATENO",templateNo,Types.VARCHAR);
                    noticeDetailColumns.add("PUNIT",map.get("PUNIT").toString(),Types.VARCHAR);
                    noticeDetailColumns.add("PQTY",pQty.toString(),Types.VARCHAR);
                    noticeDetailColumns.add("WAREHOUSE",map.get("OUT_COST_WAREHOUSE").toString(),Types.VARCHAR);
                    noticeDetailColumns.add("PRICE",noticePrice,Types.VARCHAR);
                    noticeDetailColumns.add("AMOUNT",noticeAmount.toString(),Types.VARCHAR);
                    noticeDetailColumns.add("PRETAXAMT",taxAmount2.getPreAmount(),Types.VARCHAR);
                    noticeDetailColumns.add("TAXAMT",taxAmount2.getTaxAmount().toString(),Types.VARCHAR);
                    noticeDetailColumns.add("TAXCODE",taxCode,Types.VARCHAR);
                    noticeDetailColumns.add("TAXCALTYPE",taxCalType,Types.VARCHAR);
                    noticeDetailColumns.add("TAXRATE",taxRate,Types.VARCHAR);
                    noticeDetailColumns.add("INCLTAX",inclTax,Types.VARCHAR);
                    noticeDetailColumns.add("BSNO",map.get("BSNO").toString(),Types.VARCHAR);
                    noticeDetailColumns.add("BASEUNIT",map.get("BASEUNIT"),Types.VARCHAR);
                    noticeDetailColumns.add("BASEQTY",baseQty.toString(),Types.VARCHAR);
                    noticeDetailColumns.add("UNITRATIO",unitRatio,Types.VARCHAR);
                    noticeDetailColumns.add("STOCKOUTQTY","0",Types.VARCHAR);
                    noticeDetailColumns.add("STATUS","1",Types.VARCHAR);
                    noticeDetailColumns.add("MEMO","",Types.VARCHAR);
                    noticeDetailColumns.add("OBJECTTYPE","1",Types.VARCHAR);
                    noticeDetailColumns.add("OBJECTID",supplier,Types.VARCHAR);
                    noticeDetailColumns.add("TEMPLATETYPE","1",Types.VARCHAR);
                    noticeDetailColumns.add("RETAILPRICE",map.get("PRICE").toString(),Types.VARCHAR);
                    noticeDetailColumns.add("RETAILAMT",map.get("AMT").toString(),Types.VARCHAR);
                    noticeDetailColumns.add("POQTY",map.get("POQTY").toString(),Types.VARCHAR);
                    noticeDetailColumns.add("NOQTY",map.get("APPROVEQTY").toString(),Types.VARCHAR);


                    String[] noticeDetailColumnNames = noticeDetailColumns.getColumns().toArray(new String[0]);
                    DataValue[] noticeDetailDataValues = noticeDetailColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean ib1=new InsBean("DCP_STOCKOUTNOTICE_DETAIL",noticeDetailColumnNames);
                    ib1.addValues(noticeDetailDataValues);
                    this.addProcessData(new DataProcessBean(ib1));
                }

                String bizSql="select * from dcp_bizpartner a where a.eid='"+eId+"'  " + //and a.organizationno='"+detailOrgNo+"'
                        " and a.bizpartnerno='"+supplier+"' and a.biztype in ('1','3') ";
                List<Map<String, Object>> bizList = executeQuerySQL_BindSQL(bizSql, null);
                String payType="";
                String payDateNo="";
                String billDateNo="";
                String payOrgNo="";
                String invoiceCode="";
                String currency="";
                if(CollUtil.isNotEmpty(bizList)){
                    payType=bizList.get(0).get("PAYTYPE").toString();
                    payDateNo=bizList.get(0).get("PAYDATENO").toString();
                    billDateNo=bizList.get(0).get("BILLDATENO").toString();
                    invoiceCode=bizList.get(0).get("INVOICECODE").toString();
                    currency=bizList.get(0).get("MAINCURRENCY").toString();
                    if("1".equals(payType)){
                        payOrgNo=bizList.get(0).get("PAYCENTER").toString();
                    }else if("2".equals(payType)){
                        payOrgNo=pTempList.get(0).get("CORP").toString();
                    }
                }

                ColumnDataValue noticeColumns=new ColumnDataValue();
                noticeColumns.add("EID",eId,Types.VARCHAR);
                noticeColumns.add("ORGANIZATIONNO",detailOrgNo,Types.VARCHAR);
                noticeColumns.add("BILLNO",noticeNo,Types.VARCHAR);
                noticeColumns.add("BILLTYPE","1",Types.VARCHAR);
                noticeColumns.add("SOURCETYPE","2",Types.VARCHAR);
                noticeColumns.add("SOURCEBILLNO",billNo,Types.VARCHAR);
                noticeColumns.add("RDATE",bDate,Types.VARCHAR);
                noticeColumns.add("BDATE",bDate,Types.VARCHAR);
                noticeColumns.add("OBJECTTYPE","1",Types.VARCHAR);
                noticeColumns.add("OBJECTID",supplier,Types.VARCHAR);
                noticeColumns.add("PAYTYPE",payType,Types.VARCHAR);
                noticeColumns.add("PAYORGNO",payOrgNo,Types.VARCHAR);
                noticeColumns.add("BILLDATENO",billDateNo,Types.VARCHAR);
                noticeColumns.add("PAYDATENO",payDateNo,Types.VARCHAR);
                noticeColumns.add("INVOICECODE",invoiceCode,Types.VARCHAR);
                noticeColumns.add("CURRENCY",currency,Types.VARCHAR);
                noticeColumns.add("DELIVERORGNO",detailOrgNo,Types.VARCHAR);
                noticeColumns.add("WAREHOUSE",outCostWarehouse,Types.VARCHAR);
                noticeColumns.add("INVWAREHOUSE",invCostWarehouse,Types.VARCHAR);
                noticeColumns.add("BSNO","",Types.VARCHAR);
                noticeColumns.add("RETURNTYPE","1",Types.VARCHAR);
                noticeColumns.add("TOTCQTY",totCQty.toString(),Types.VARCHAR);
                noticeColumns.add("TOTPQTY",totPQty.toString(),Types.VARCHAR);
                noticeColumns.add("TOTSTOCKOUTQTY","0",Types.VARCHAR);
                noticeColumns.add("TOTAMT",totAmt.toString(),Types.VARCHAR);
                noticeColumns.add("TOTPRETAXAMT",totPreTaxAmt.toString(),Types.VARCHAR);
                noticeColumns.add("TOTTAXAMT",totTaxAmt.toString(),Types.VARCHAR);
                noticeColumns.add("TOTRETAILAMT",totRetailAmt.toString(),Types.VARCHAR);
                noticeColumns.add("EMPLOYEEID",employeeNo,Types.VARCHAR);
                noticeColumns.add("DEPARTID",departmentNo,Types.VARCHAR);
                noticeColumns.add("STATUS","1",Types.VARCHAR);
                noticeColumns.add("MEMO","",Types.VARCHAR);
                noticeColumns.add("OWNOPID",req.getOpNO(),Types.VARCHAR);
                noticeColumns.add("OWNDEPTID",departmentNo,Types.VARCHAR);
                noticeColumns.add("CREATEOPID",req.getOpNO(),Types.VARCHAR);
                noticeColumns.add("CREATEDEPTID",departmentNo,Types.VARCHAR);
                noticeColumns.add("CREATETIME",lastmoditime,Types.DATE);
                noticeColumns.add("CONFIRMBY",req.getOpNO(),Types.VARCHAR);
                noticeColumns.add("CONFIRMTIME",lastmoditime,Types.DATE);

                String[] noticeColumnNames = noticeColumns.getColumns().toArray(new String[0]);
                DataValue[] noticeDataValues = noticeColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib1=new InsBean("DCP_STOCKOUTNOTICE",noticeColumnNames);
                ib1.addValues(noticeDataValues);
                this.addProcessData(new DataProcessBean(ib1));

            }


        }


        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReturnApplyStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReturnApplyStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReturnApplyStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected String getQuerySql(DCP_ReturnApplyStatusUpdateReq req) throws Exception {
        StringBuilder sb=new StringBuilder();

        return sb.toString();
    }



}

