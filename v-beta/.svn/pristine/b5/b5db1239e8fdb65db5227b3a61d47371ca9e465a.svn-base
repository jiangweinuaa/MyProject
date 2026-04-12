package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PurchaseApplyProcessReq;
import com.dsc.spos.json.cust.res.DCP_PurchaseApplyProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DCP_PurchaseApplyProcess extends SPosAdvanceService<DCP_PurchaseApplyProcessReq, DCP_PurchaseApplyProcessRes> {

    @Override
    protected void processDUID(DCP_PurchaseApplyProcessReq req, DCP_PurchaseApplyProcessRes res) throws Exception {

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String billNo = req.getRequest().getBillNo();
        String oprType = req.getRequest().getOprType();

        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String nowDate = new SimpleDateFormat("yyyyMMdd").format(new Date());


        String sql="select * from DCP_PURCHASEAPPLY a where a.eid='"+eId+"' " +
                " and a.organizationno='"+organizationNO+"' " +
                " and a.billno='"+billNo+"' ";
        List<Map<String, Object>> list = this.doQueryData( sql, null);

        String detailSql="select a.*,b.purcenter from DCP_PURCHASEAPPLY_DETAIL a " +
                " left join DCP_PURCHASETEMPLATE b on a.eid=b.eid and a.TEMPLATENO=b.PURTEMPLATENO " +
                " where a.eid='"+eId+"' " +
                " and a.organizationno='"+organizationNO+"' " +
                " and a.billno='"+billNo+"' ";
        List<Map<String, Object>> detailList = this.doQueryData( detailSql, null);

        if(CollUtil.isEmpty(list)||CollUtil.isEmpty(detailList)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "单据不存在！".toString());
        }

        Map<String, Object> stringObjectMap = list.get(0);
        String status = stringObjectMap.get("STATUS").toString();
        String organizationNo = stringObjectMap.get("ORGANIZATIONNO").toString();
        String bDate = stringObjectMap.get("BDATE").toString();
        String rDate = stringObjectMap.get("RDATE").toString();
        String employeeId = stringObjectMap.get("EMPLOYEEID").toString();
        String departId = stringObjectMap.get("DEPARTID").toString();

        //submit：提交
        //revoke：撤销
        //cancel：作废
        //close：结案
        //unclose：取消结案
        if ("submit".equals(oprType)) {
            //1.单据状态<>"0新建"不可提交！
            if(!"0".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "单据非新建状态，不能进行提交！".toString());
            }

            for (Map<String, Object> detail : detailList){
                String item = detail.get("ITEM").toString();
                String pluBarCode = detail.get("PLUBARCODE").toString();
                String pluNo = detail.get("PLUNO").toString();
                String featureNo = detail.get("FEATURENO").toString();
                String pUnit = detail.get("PUNIT").toString();
                BigDecimal pQty = new BigDecimal(detail.get("PQTY").toString());
                BigDecimal baseQty = new BigDecimal(detail.get("BASEQTY").toString());
                String baseUnit = detail.get("BASEUNIT").toString();
                String warehouse = detail.get("WAREHOUSE").toString();
                //String supplierType = detail.get("SUPPLIERTYPE").toString();
                String supplier = detail.get("SUPPLIER").toString();
                String purType = detail.get("PURTYPE").toString();
                String purCenter = detail.get("PURCENTER").toString();
                String templateno = detail.get("TEMPLATENO").toString();
                ColumnDataValue demandColumns=new ColumnDataValue();
                demandColumns.add("EID",eId,Types.VARCHAR);
                demandColumns.add("ORGANIZATIONNO",organizationNo,Types.VARCHAR);
                demandColumns.add("BDATE",bDate,Types.VARCHAR);
                demandColumns.add("ORDERTYPE","4",Types.VARCHAR);
                demandColumns.add("ORDERNO",billNo,Types.VARCHAR);
                demandColumns.add("ITEM",item,Types.VARCHAR);
                demandColumns.add("PLUBARCODE",pluBarCode,Types.VARCHAR);
                demandColumns.add("PLUNO",pluNo,Types.VARCHAR);
                demandColumns.add("FEATURENO",Check.Null(featureNo)?" ":featureNo,Types.VARCHAR);
                demandColumns.add("PUNIT",pUnit,Types.VARCHAR);
                demandColumns.add("POQTY",pQty,Types.VARCHAR);
                demandColumns.add("PQTY",pQty,Types.VARCHAR);
                demandColumns.add("BASEUNIT",baseUnit,Types.VARCHAR);
                demandColumns.add("BASEQTY",baseQty.toString(),Types.VARCHAR);
                demandColumns.add("RDATE",rDate,Types.VARCHAR);
                demandColumns.add("OBJECTTYPE","1",Types.VARCHAR);
                demandColumns.add("OBJECTID",organizationNO,Types.VARCHAR);
                demandColumns.add("EMPLOYEEID",employeeId,Types.VARCHAR);
                demandColumns.add("DEPARTID",departId,Types.VARCHAR);
                demandColumns.add("RECEIPTWAREHOUSE",warehouse,Types.VARCHAR);
                demandColumns.add("SUPPLIERTYPE","SUPPLIER",Types.VARCHAR);
                demandColumns.add("SUPPLIER",supplier,Types.VARCHAR);
                demandColumns.add("PURTYPE",purType,Types.VARCHAR);
                demandColumns.add("PURCENTER",purCenter,Types.VARCHAR);
                demandColumns.add("SUBMITTIME",lastmoditime,Types.VARCHAR);
                demandColumns.add("TEMPLATENO",templateno,Types.VARCHAR);
                demandColumns.add("STATUS",DataValues.newString("0"));

                demandColumns.add("STOCKOUTNOQTY","0",Types.VARCHAR);
                demandColumns.add("PURQTY","0",Types.VARCHAR);
                demandColumns.add("STOCKINQTY","0",Types.VARCHAR);
                demandColumns.add("STOCKOUTQTY","0",Types.VARCHAR);
                demandColumns.add("CLOSESTATUS","0",Types.VARCHAR);
                demandColumns.add("DISTRISTATUS","00",Types.VARCHAR);


                String[] mainColumnNames = demandColumns.getColumns().toArray(new String[0]);
                DataValue[] mainDataValues = demandColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib1=new InsBean("DCP_DEMAND",mainColumnNames);
                ib1.addValues(mainDataValues);
                this.addProcessData(new DataProcessBean(ib1));
            }



            UptBean ub2 = new UptBean("DCP_PURCHASEAPPLY");
            ub2.addUpdateValue("STATUS", DataValues.newString("1"));
            ub2.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("LASTMODITIME",DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("SUBMITOPID", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("SUBMITTIME", DataValues.newDate(lastmoditime));
            ub2.addCondition("EID", DataValues.newString(eId));
            ub2.addCondition("BILLNO",DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub2));
        }
        else if ("revoke".equals(oprType)) {
            //1.单据状态<>"1待核准"不可撤销！
            if(!"1".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "单据非待核准状态，不能进行撤销！".toString());
            }
            //2.采购申请已存在有效的采购订单，不可撤销！
            //采购单：
            //DCP_PURORDER
            //DCP_PURORDER_SOURCE（关联条件：SOURCEBILLNO=申请单号，OITEM=申请单项次）
            sql="select * from DCP_PURORDER_SOURCE a " +
                    " where a.eid='"+eId+"' " +
                    " and a.sourcebillno='"+billNo+"' " +
                    " ";
            List<Map<String, Object>> list2 = this.doQueryData( sql, null);
            if(CollUtil.isNotEmpty(list2)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "单据已存在有效的采购订单，不能进行撤销！".toString());
            }

            DelBean db3 = new DelBean("DCP_DEMAND");
            db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db3.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
            db3.addCondition("ORDERNO", new DataValue(billNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db3));

            UptBean ub2 = new UptBean("DCP_PURCHASEAPPLY");
            ub2.addUpdateValue("STATUS", DataValues.newString("0"));
            ub2.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("LASTMODITIME",DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("SUBMITOPID", DataValues.newString(""));
            ub2.addUpdateValue("SUBMITTIME", DataValues.newDate(null));
            ub2.addCondition("EID", DataValues.newString(eId));
            ub2.addCondition("BILLNO",DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub2));

        }
        else if ("cancel".equals(oprType)) {
            if(!"0".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "单据非新建状态，不能进行取消！".toString());
            }
            UptBean ub2 = new UptBean("DCP_PURCHASEAPPLY");
            ub2.addUpdateValue("STATUS", DataValues.newString("-1"));
            ub2.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("LASTMODITIME",DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("CANCELOPID", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("CANCELTIME", DataValues.newDate(lastmoditime));
            ub2.addCondition("EID", DataValues.newString(eId));
            ub2.addCondition("BILLNO",DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub2));
        }
        else if ("close".equals(oprType)) {
            //1.单据状态<>"1待核准"OR "2已审核" 不可结案！
            if(!"1".equals(status) && !"2".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "单据非待核准或已审核状态，不能进行结案！".toString());
            }
            //1.更新明细状态=「9已结束」（仅更新状态为null的行，状态=「1已核准」或者「2已驳回」无需更新）
            List<Map<String, Object>> filterDetails = detailList.stream().filter(x -> Check.Null(x.get("STATUS").toString())).collect(Collectors.toList());
            for (Map<String, Object> detail : filterDetails){
                UptBean ub2 = new UptBean("DCP_PURCHASEAPPLY_DETAIL");
                ub2.addUpdateValue("STATUS", DataValues.newString("9"));
                ub2.addCondition("EID", DataValues.newString(eId));
                ub2.addCondition("BILLNO",DataValues.newString(billNo));
                ub2.addCondition("ITEM",DataValues.newString(detail.get("ITEM").toString()));
                this.addProcessData(new DataProcessBean(ub2));
            }

            //2.更新单据状态=「9已结束」，更新「结案人」/「修改人」=用户编号、「结案时间」/「修改时间」=系统时间
            UptBean ub2 = new UptBean("DCP_PURCHASEAPPLY");
            ub2.addUpdateValue("STATUS", DataValues.newString("9"));
            ub2.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("LASTMODITIME",DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("CLOSEOPID", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("CLOSETIME", DataValues.newDate(lastmoditime));
            ub2.addCondition("EID", DataValues.newString(eId));
            ub2.addCondition("BILLNO",DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub2));

            //3.更新底稿状态=「9已结束」（仅更新状态=「0待核准」的行，状态=「1已核准」或者「2已驳回」无需更新）
            String demandSql="select * from dcp_demand a where a.eid='"+eId+"' and a.orderno='"+billNo+"' ";
            List<Map<String, Object>> demandList = this.doQueryData( demandSql, null);
            List<Map<String, Object>> filterDemand = demandList.stream().filter(x -> "0".equals(x.get("STATUS").toString())).distinct().collect(Collectors.toList());
            for (Map<String, Object> demand : filterDemand){
                UptBean ub3 = new UptBean("DCP_DEMAND");
                ub3.addUpdateValue("STATUS", DataValues.newString("9"));
                ub3.addCondition("EID", DataValues.newString(eId));
                ub3.addCondition("ORDERNO",DataValues.newString(billNo));
                ub3.addCondition("ITEM",DataValues.newString(demand.get("ITEM").toString()));
                this.addProcessData(new DataProcessBean(ub3));
            }

        }

        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurchaseApplyProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurchaseApplyProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurchaseApplyProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PurchaseApplyProcessReq req) throws Exception {
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
    protected TypeToken<DCP_PurchaseApplyProcessReq> getRequestType() {
        return new TypeToken<DCP_PurchaseApplyProcessReq>() {
        };
    }

    @Override
    protected DCP_PurchaseApplyProcessRes getResponseType() {
        return new DCP_PurchaseApplyProcessRes();
    }
}

