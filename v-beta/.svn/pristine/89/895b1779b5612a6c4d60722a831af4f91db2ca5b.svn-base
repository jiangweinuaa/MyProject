package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PurchaseApplyCreateReq;
import com.dsc.spos.json.cust.req.DCP_PurchaseApplyUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PurchaseApplyUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_PurchaseApplyUpdate extends SPosAdvanceService<DCP_PurchaseApplyUpdateReq, DCP_PurchaseApplyUpdateRes> {

    @Override
    protected void processDUID(DCP_PurchaseApplyUpdateReq req, DCP_PurchaseApplyUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        DCP_PurchaseApplyUpdateReq.LevelRequest request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        String createDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        List<DCP_PurchaseApplyUpdateReq.Detail> details = request.getDetail();

        String billNo = request.getBillNo();

        DelBean db2 = new DelBean("DCP_PURCHASEAPPLY_DETAIL");
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
        db2.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        UptBean ub1 = new UptBean("DCP_PURCHASEAPPLY");
        ub1.addUpdateValue("BDATE", DataValues.newString(request.getBDate()));
        ub1.addUpdateValue("RDATE", DataValues.newString(request.getRDate()));
        ub1.addUpdateValue("TOTPQTY", DataValues.newString(request.getTotPqty()));
        ub1.addUpdateValue("TOTCQTY", DataValues.newString(request.getTotCqty()));
        ub1.addUpdateValue("TOTAMT", DataValues.newString(request.getTotAmt()));
        ub1.addUpdateValue("TOTPURAMT", DataValues.newString(request.getTotPurAmt()));
        ub1.addUpdateValue("EMPLOYEEID", DataValues.newString(request.getEmployeeId()));
        ub1.addUpdateValue("DEPARTID", DataValues.newString(request.getDepartId()));
        ub1.addUpdateValue("MEMO", DataValues.newString(request.getMemo()));
        ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
        ub1.addUpdateValue("LASTMODITIME", DataValues.newDate(createTime));
        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
        ub1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));



        if(CollUtil.isNotEmpty(details)){
            MyCommon mc = new MyCommon();
            //仓库范围
            String warehouseSql="select b.* from dcp_warehouse a " +
                    " left join DCP_WAREHOUSE_RANGE b on a.eid=b.eid and a.organizationno=b.organizationno and a.warehouse=b.warehouse " +
                    " and a.status='100' and a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                    " and b.status='100' ";
            List<Map<String, Object>> warehouseList = this.doQueryData(warehouseSql, null);

            //要先给仓库赋值 在算库存
            String withPluSingle = "";
            if (details !=null && details.size()>0 ) {
                Map<String,String> map = new HashMap<>();
                String sJoinPlu = "";
                for(DCP_PurchaseApplyUpdateReq.Detail detail :details) {
                    sJoinPlu += detail.getPluNo() +",";
                }
                map.put("PLU", sJoinPlu);
                withPluSingle = mc.getFormatSourceMultiColWith(map);
            }
            List<Map<String, Object>> getPlu=new ArrayList();
            if(Check.NotNull(withPluSingle)) {
                String goodsSql="select distinct a.* from dcp_goods a " +
                        " inner join (" + withPluSingle + ") b on a.pluno=b.plu " +
                        " where a.eid='"+req.geteId()+"' ";
                getPlu = this.doQueryData(goodsSql, null);

            }
            List<Map<String, Object>> finalGetPlu = getPlu;
            req.getRequest().getDetail().forEach(y->{
                if(Check.Null(y.getWarehouse())){
                    List<Map<String, Object>> warehouseFilter1 = warehouseList.stream().filter(x -> x.get("CODE").toString().equals(y.getPluNo()) && x.get("TYPE").toString().equals("2")).collect(Collectors.toList());
                    if(warehouseFilter1.size()>0){
                        y.setWarehouse(warehouseFilter1.get(0).get("WAREHOUSE").toString());
                    }

                    List<Map<String, Object>> pluFilter = finalGetPlu.stream().filter(x -> x.get("PLUNO").toString().equals(y.getPluNo())).collect(Collectors.toList());
                    if(pluFilter.size()>0){
                        List<Map<String, Object>> warehouseFilter2 = warehouseList.stream().filter(x -> x.get("CODE").toString().equals(pluFilter.get(0).get("CATEGORY").toString()) && x.get("TYPE").toString().equals("1")).collect(Collectors.toList());
                        if(warehouseFilter2.size()>0){
                            y.setWarehouse(warehouseFilter2.get(0).get("WAREHOUSE").toString());
                        }
                    }
                }

                //还是没有仓库 给个默认的
                if(Check.Null(y.getWarehouse())){
                    y.setWarehouse(req.getIn_cost_warehouse());
                }

            });



            List<Map<String, Object>> getStock=new ArrayList<>();
            String withPlu = "";
            if (details !=null && details.size()>0 ) {
                Map<String,String> map = new HashMap<>();
                String sJoinPlu = "";
                String sJoinWarehouse="";
                for(DCP_PurchaseApplyUpdateReq.Detail detail :details) {
                    sJoinPlu += detail.getPluNo() +",";
                    sJoinWarehouse+=detail.getWarehouse()+",";
                }
                map.put("PLU", sJoinPlu);
                map.put("WAREHOUSE",sJoinWarehouse);
                withPlu = mc.getFormatSourceMultiColWith(map);
            }

            if(Check.NotNull(withPlu)) {
                String sql = " select a.pluno,a.warehouse,sum(a.qty) as baseqty,sum(a.qty-a.lockqty-a.onlineqty) as availableqty " +
                        " from dcp_stock a"
                        + " inner join (" + withPlu + ") b on a.pluno=b.plu and a.warehouse=b.warehouse "
                        + " where a.eid='" + req.geteId() + "' and a.organizationno='" + req.getOrganizationNO() + "'  ";


                sql += " group by a.pluno,a.warehouse";
                getStock = this.doQueryData(sql, null);

            }


            List<Map<String,Object>> getGoodsUnit=new ArrayList<>();
            if(Check.NotNull(withPlu)){
                String goodsUnitSql="select * from DCP_GOODS_UNIT a" +
                        " inner join (" + withPlu + ") b on a.pluno=b.plu " +
                        " where a.eid='"+req.geteId()+"' ";
                getGoodsUnit = this.doQueryData(goodsUnitSql, null);
            }

            String supplierSql="select * from dcp_bizpartner a " +
                    " where a.status='100' and a.eid='"+eId+"' " +
                    " and to_char(a.begindate,'yyyyMMdd')<='"+createDate+"' " +
                    " and to_char(a.enddate,'yyyyMMdd')>='"+createDate+"' ";
            List<Map<String, Object>> getSupplier = this.doQueryData(supplierSql, null);

            //查询有效的采购模板
            String ptSql="select a.SUPPLIERNO,c.pluno,a.purtemplateno,a.SUPPLIERNO,a.PURTYPE,a.pre_day,c.PURUNIT " +
                    " from DCP_PURCHASETEMPLATE a" +
                    " left join DCP_PURCHASETEMPLATE_ORG B ON A.EID=B.EID AND a.PURTEMPLATENO=b.PURTEMPLATENO " +
                    " left join DCP_PURCHASETEMPLATE_GOODS c on c.eid=a.eid and c.purtemplateno=a.purtemplateno " +
                    " left join dcp_goods d on d.eid=a.eid and d.pluno=c.pluno " +
                    " where a.status='100' and b.status='100' and c.status='100' and a.eid='"+eId+"' and b.ORGANIZATIONNO='"+req.getOrganizationNO()+"' ";
            List<Map<String, Object>> getPt = this.doQueryData(ptSql, null);


            //商品模板
            String tempSql=" select * from ( SELECT a.TEMPLATEID,a.TEMPLATETYPE,c.pluno,c.SUPPLIERTYPE,c.SUPPLIERID FROM DCP_GOODSTEMPLATE a " +
                    " JOIN DCP_GOODSTEMPLATE_RANGE b on a.EID = b.EID and a.TEMPLATEID = b.TEMPLATEID " +
                    " inner join dcp_goodstemplate_goods c on c.eid=a.eid and c.TEMPLATEID=a.TEMPLATEID and c.status='100'" +
                    " WHERE a.EID = '"+eId+"' AND b.ID = '"+req.getShopId()+"' AND a.STATUS = 100 " +
                    " AND b.RANGETYPE = 2  " +
                    " order by a.CREATETIME desc " +
                    " ) " +
                    " union all" +
                    " select * from ( SELECT a.TEMPLATEID,a.TEMPLATETYPE,c.pluno,c.SUPPLIERTYPE,c.SUPPLIERID FROM DCP_GOODSTEMPLATE a " +
                    " JOIN DCP_GOODSTEMPLATE_RANGE b on a.EID = b.EID and a.TEMPLATEID = b.TEMPLATEID " +
                    " inner join dcp_goodstemplate_goods c on c.eid=a.eid and c.TEMPLATEID=a.TEMPLATEID and c.status='100'" +
                    " WHERE a.EID = '"+eId+"' AND b.ID = '"+req.getBELFIRM()+"' AND a.STATUS = 100 " +
                    " AND b.RANGETYPE = 1  " +
                    " order by a.CREATETIME desc " +
                    " ) " +
                    "";
            List<Map<String, Object>> tempList = this.doQueryData(tempSql, null);



            for (DCP_PurchaseApplyUpdateReq.Detail detail : details){
                List<Map<String, Object>> ptFilter = getPt.stream().filter(x -> x.get("PLUNO").toString().equals(detail.getPluNo())).distinct().collect(Collectors.toList());
                List<Map<String, Object>> tempFilter = tempList.stream().filter(x -> x.get("PLUNO").toString().equals(detail.getPluNo())).distinct().collect(Collectors.toList());

                if(Check.NotNull(detail.getSupplier())){
                    List<Map<String, Object>> filterSupplier = getSupplier.stream().filter(x -> x.get("BIZPARTNERNO").toString().equals(detail.getSupplier())).collect(Collectors.toList());
                    if(filterSupplier.size()<=0){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, detail.getSupplier()+"供应商不存在或已过期！".toString());
                    }
                }else if(Check.Null(detail.getSupplier())){
                    if(ptFilter.size()==1){
                        detail.setSupplier(ptFilter.get(0).get("SUPPLIERNO").toString());
                        //detail.setTemplateNo(ptFilter.get(0).get("PURTEMPLATENO").toString());
                    }else if(ptFilter.size()>1){
                        for (Map<String, Object> pt : ptFilter){
                            String ptSupplierNo = pt.get("SUPPLIERNO").toString();
                            List<Map<String, Object>> tempFilter2 = tempFilter.stream().filter(x -> x.get("SUPPLIERID").toString().equals(ptSupplierNo)).distinct().collect(Collectors.toList());
                            if(tempFilter2.size()>0){
                                detail.setSupplier(ptSupplierNo);
                                //detail.setTemplateNo(pt.get("PURTEMPLATENO").toString());
                            }
                        }
                        if(Check.Null(detail.getSupplier())){
                            String supplier = ptFilter.get(0).get("SUPPLIERNO").toString();//dcp_goods.supplier
                            detail.setSupplier(supplier);

                        }
                    }

                }
                List<Map<String, Object>> ptFilter3 = ptFilter.stream().filter(x -> x.get("SUPPLIERNO").toString().equals(detail.getSupplier())).collect(Collectors.toList());
                if(Check.Null(detail.getTemplateNo())){
                    if(ptFilter3.size()>0){
                        detail.setTemplateNo(ptFilter3.get(0).get("PURTEMPLATENO").toString());
                    }
                }

                if(Check.Null(detail.getPurType())){
                    if(ptFilter3.size()>0){
                        detail.setPurType(ptFilter3.get(0).get("PURTYPE").toString());
                    }
                }
                if(Check.Null(detail.getPreDays())){
                    if(ptFilter3.size()>0){
                        detail.setPreDays(ptFilter3.get(0).get("PRE_DAY").toString());
                    }
                }

                if(Check.Null(detail.getMinQty())){
                    BigDecimal minQty = new BigDecimal(ptFilter3.get(0).get("MINQTY").toString());
                    String purUnit = ptFilter3.get(0).get("PURUNIT").toString();
                    if(!purUnit.equals(detail.getPUnit())) {
                        if (getGoodsUnit.size() > 0) {
                            List<Map<String, Object>> guFilter = getGoodsUnit.stream().filter(y -> y.get("PLUNO").toString().equals(detail.getPluNo()) && y.get("UNIT").toString().equals(purUnit)).distinct().collect(Collectors.toList());
                            if (guFilter.size() > 0) {
                                BigDecimal purUnitRatio = new BigDecimal(guFilter.get(0).get("UNITRATIO").toString());
                                BigDecimal divide = minQty.multiply(purUnitRatio).divide(new BigDecimal(detail.getUnitRatio()), 6, RoundingMode.HALF_UP);
                                detail.setMinQty(divide.toString());
                            }
                        }
                    }else{
                        detail.setMinQty(minQty.toString());
                    }
                }
                if(Check.Null(detail.getMulQty())){
                    BigDecimal mulQty = new BigDecimal(ptFilter3.get(0).get("MULQTY").toString());
                    String purUnit = ptFilter3.get(0).get("PURUNIT").toString();
                    if(!purUnit.equals(detail.getPUnit())) {
                        if (getGoodsUnit.size() > 0) {
                            List<Map<String, Object>> guFilter = getGoodsUnit.stream().filter(y -> y.get("PLUNO").toString().equals(detail.getPluNo()) && y.get("UNIT").toString().equals(purUnit)).distinct().collect(Collectors.toList());
                            if (guFilter.size() > 0) {
                                BigDecimal purUnitRatio = new BigDecimal(guFilter.get(0).get("UNITRATIO").toString());
                                BigDecimal divide = mulQty.multiply(purUnitRatio).divide(new BigDecimal(detail.getUnitRatio()), 6, RoundingMode.HALF_UP);
                                detail.setMulQty(divide.toString());
                            }
                        }
                    }else{
                        detail.setMinQty(mulQty.toString());
                    }
                }

                if(Check.Null(detail.getPurAmt())){
                    BigDecimal pQtyDecimal = new BigDecimal(detail.getPQty());
                    BigDecimal purPriceDecimal = new BigDecimal(detail.getPurPrice());
                    detail.setPurAmt(pQtyDecimal.multiply(purPriceDecimal).toString());
                }

                if(Check.Null(detail.getPurDate())){
                    //入参值为空，计算：purDate=预计到货日（需求日期）-到货前置天数；
                    //若purDate<系统日期，修正日期值=系统日期，预计到货日=purDate+到货前置天数
                    Integer subDate =Integer.valueOf(detail.getArrivalDate())- Integer.valueOf(detail.getPreDays());
                    if(subDate<Integer.valueOf(createDate)){
                        detail.setPurDate(createDate);
                        Integer addDate =Integer.valueOf(createDate)+ Integer.valueOf(detail.getPreDays());
                        detail.setArrivalDate(addDate.toString());
                    }else{
                        detail.setPurDate(subDate.toString());
                    }
                }




                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(eId));
                detailColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                detailColumns.add("BILLNO", DataValues.newString(billNo));

                detailColumns.add("ITEM", DataValues.newString(detail.getItem()));
                detailColumns.add("PLUBARCODE", DataValues.newString(detail.getPluBarcode()));
                detailColumns.add("PLUNO", DataValues.newString(detail.getPluNo()));
                detailColumns.add("FEATURENO", DataValues.newString(detail.getFeatureNo()));
                detailColumns.add("PUNIT", DataValues.newString(detail.getPUnit()));
                detailColumns.add("PQTY", DataValues.newString(detail.getPQty()));
                detailColumns.add("BASEUNIT", DataValues.newString(detail.getBaseUnit()));
                detailColumns.add("BASEQTY", DataValues.newString(detail.getBaseQty()));
                detailColumns.add("UNITRATIO", DataValues.newString(detail.getUnitRatio()));
                detailColumns.add("PRICE", DataValues.newString(detail.getPrice()));
                detailColumns.add("AMT", DataValues.newString(detail.getAmt()));
                detailColumns.add("PURPRICE", DataValues.newString(detail.getPurPrice()));
                detailColumns.add("PURAMT", DataValues.newString(detail.getPurAmt()));
                detailColumns.add("WAREHOUSE", DataValues.newString(detail.getWarehouse()));
                detailColumns.add("PROPQTY", DataValues.newString(detail.getPropQty()));
                detailColumns.add("MINQTY", DataValues.newString(detail.getMinQty()));
                detailColumns.add("MULQTY", DataValues.newString(detail.getMulQty()));
                detailColumns.add("REVIEWQTY", DataValues.newString("0"));
                detailColumns.add("STATUS", DataValues.newString("0"));
                detailColumns.add("MEMO", DataValues.newString(detail.getMemo()));
                detailColumns.add("SUPPLIER", DataValues.newString(detail.getSupplier()));
                detailColumns.add("TEMPLATENO", DataValues.newString(detail.getTemplateNo()));
                detailColumns.add("PURTYPE", DataValues.newString(detail.getPurType()));
                detailColumns.add("PREDAYS", DataValues.newString(detail.getPreDays()));
                detailColumns.add("PURDATE", DataValues.newString(detail.getPurDate()));
                detailColumns.add("ARRIVALDATE", DataValues.newString(detail.getArrivalDate()));

                if(Check.Null(detail.getRefWqty())) {
                    if (getStock.size() > 0) {
                        List<Map<String, Object>> stockFilter = getStock.stream().filter(x -> x.get("PLUNO").toString().equals(detail.getPluNo()) && x.get("WAREHOUSE").toString().equals(detail.getWarehouse())).distinct().collect(Collectors.toList());
                        if (stockFilter.size() > 0) {
                            detailColumns.add("REFWQTY", DataValues.newString(stockFilter.get(0).get("AVAILABLEQTY").toString()));
                        } else {
                            detailColumns.add("REFWQTY", DataValues.newString("0"));
                        }
                    } else {
                        detailColumns.add("REFWQTY", DataValues.newString("0"));
                    }
                }else{
                    detailColumns.add("REFWQTY", DataValues.newString(detail.getRefWqty()));
                }

                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailib=new InsBean("DCP_PURCHASEAPPLY_DETAIL",detailColumnNames);
                detailib.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailib));
            }
        }



        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurchaseApplyUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurchaseApplyUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurchaseApplyUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PurchaseApplyUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_PurchaseApplyUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_PurchaseApplyUpdateReq>(){};
    }

    @Override
    protected DCP_PurchaseApplyUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_PurchaseApplyUpdateRes();
    }

}


