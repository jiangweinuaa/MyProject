package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DemandToStockOutNocticeProcessReq;
import com.dsc.spos.json.cust.req.DCP_ReceivingStatusUpdateReq;
import com.dsc.spos.json.cust.req.DCP_StockOutNoticeStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_DemandToStockOutNocticeProcessRes;
import com.dsc.spos.json.cust.res.DCP_ReceivingStatusUpdateRes;
import com.dsc.spos.json.cust.res.DCP_StockOutNoticeRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DCP_DemandToStockOutNocticeProcess extends SPosAdvanceService<DCP_DemandToStockOutNocticeProcessReq, DCP_DemandToStockOutNocticeProcessRes>
{
    @Override
    protected void processDUID(DCP_DemandToStockOutNocticeProcessReq req, DCP_DemandToStockOutNocticeProcessRes res) throws Exception
    {
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        //String rDate = formatter.parse(lastmoditime).toString();
        String bDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_DemandToStockOutNocticeProcessReq.LevelItem request = req.getRequest();
        String confirmType = request.getConfirmType();//1-自动审核,2-人工审核
        String billCreateType = request.getBillCreateType();//1.按来源单号分单,2.按需求对象集单,3.按模板编号集单
        List<DCP_DemandToStockOutNocticeProcessReq.OrderList> orderList = request.getOrderList().stream().filter(x->new BigDecimal(x.getPQty()).compareTo(BigDecimal.ZERO)>0).collect(Collectors.toList());
        List<String> createNos=new ArrayList<>();

        if(orderList.size()<=0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "没有可生单的明细！");
        }

        //获取品号信息
        List<String> plunos = orderList.stream().map(x -> x.getPluNo()).distinct().collect(Collectors.toList());
        StringBuffer sJoinPluNo = new StringBuffer();
        for (String pluno : plunos) {
            sJoinPluNo.append(pluno + ",");
        }
        Map<String, String> map = new HashMap<>();
        map.put("PLUNO", sJoinPluNo.toString());

        MyCommon cm = new MyCommon();
        String withPlu = cm.getFormatSourceMultiColWith(map);

        String goodsSql="select a.pluno,a.baseunit,a.wunit from dcp_goods a " +
                 " inner join ("+withPlu+") p on a.pluno=p.pluno "+
                //" left join dcp_goods_unit b on b.eid=a.eid and a.pluno=b.pluno and b.ounit=a.wunit and b.unit=a.baseunit " +
                " where a.eid='"+eId+"' " ;
        List<Map<String, Object>> getGoods = this.doQueryData(goodsSql, null);

        String goodsUnitSql="select a.pluno,a.unit,a.ounit,a.unitratio as unit_ratio,b.udlength as oudlength,c.udlength  from dcp_goods_unit a" +
                " left join dcp_unit b on a.ounit=b.unit and a.eid=b.eid " +
                " left join dcp_unit c on a.unit=c.unit and a.eid=c.eid " +
                " inner join ("+withPlu+") p on a.pluno=p.pluno "+
                " where a.eid='"+eId+"' ";

        List<Map<String, Object>> getUnits = this.doQueryData(goodsUnitSql, null);

        List<String> orderNos = orderList.stream().map(o -> o.getOrderNo()).distinct().collect(Collectors.toList());
        List<String> orderNoMaps = orderNos.stream().map(y -> "'" + y + "'").collect(Collectors.toList());
        //orderNoMaps  用,拼接
        String demandSql = " select a.* from DCP_DEMAND a " +
                " where a.eid='"+eId+"' " +
                " and a.orderno in ("+String.join(",", orderNoMaps)+") ";
        List<Map<String, Object>> demandList = this.doQueryData(demandSql, null);

        orderList.forEach(y->{
            String poQty="0";
            List<Map<String, Object>> filterRows = demandList.stream().filter(x -> x.get("ORDERNO").toString().equals(y.getOrderNo())
                    && x.get("ITEM").toString().equals(y.getOrderItem())).collect(Collectors.toList());

            if(filterRows.size()>0){
                poQty=filterRows.get(0).get("POQTY").toString();
            }
            y.setPoQty(poQty);
        });

        if("1".equals(billCreateType)){
           for(String orderNo : orderNos){

               List<Map<String, Object>> filterDemand = demandList.stream().filter(x -> x.get("ORDERNO").toString().equals(orderNo)).collect(Collectors.toList());


               String billNo = this.getOrderNO(req, "PHTZ");
                createNos.add(billNo);
                List<DCP_DemandToStockOutNocticeProcessReq.OrderList> details = orderList.stream().filter(x -> x.getOrderNo().equals(orderNo)).collect(Collectors.toList());
                int detailItem=0;
                String sourceType="";
                String objectType="";
                String objectId="";
                String warehouse="";
                BigDecimal totCqty=BigDecimal.ZERO;
                BigDecimal totPqty=BigDecimal.ZERO;
                BigDecimal totAmt=BigDecimal.ZERO;
                BigDecimal totPreTaxAmt=BigDecimal.ZERO;
                BigDecimal totTaxAmt=BigDecimal.ZERO;
                BigDecimal totRetailAmt=BigDecimal.ZERO;
                for (DCP_DemandToStockOutNocticeProcessReq.OrderList detail : details){
                    detailItem++;

                    String orderType = detail.getOrderType();

                    List<Map<String, Object>> pluInfos = getGoods.stream().filter(x -> x.get("PLUNO").toString().equals(detail.getPluNo())).collect(Collectors.toList());
                    if(pluInfos.size()<=0){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号信息不存在");
                    }
                    Map<String, Object> singlePlu = pluInfos.get(0);
                    String baseUnit = singlePlu.get("BASEUNIT").toString();
                    String wunit = singlePlu.get("WUNIT").toString();

                    List<Map<String, Object>> plunoUnits = getUnits.stream().filter(x -> x.get("PLUNO").toString().equals(detail.getPluNo())).collect(Collectors.toList());
                    if(plunoUnits.size()<=0){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号单位信息不存在");
                    }
                    List<Map<String, Object>> punitInfos = plunoUnits.stream().filter(x -> x.get("OUNIT").toString().equals(detail.getPUnit())).collect(Collectors.toList());
                    if(punitInfos.size()<=0){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号单位"+detail.getPUnit()+"信息不存在");
                    }
                    List<Map<String, Object>> wunitInfos = plunoUnits.stream().filter(x -> x.get("OUNIT").toString().equals(wunit)).collect(Collectors.toList());
                    if(wunitInfos.size()<=0){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号单位"+wunit+"信息不存在");
                    }
                    String unit_ratiop = punitInfos.get(0).get("UNIT_RATIO").toString();
                    if(Check.Null(unit_ratiop)){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号基准单位-需求单位转换比例不存在");
                    }
                    String unit_ratiow = wunitInfos.get(0).get("UNIT_RATIO").toString();
                    if(Check.Null(unit_ratiow)){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号基准单位-库存单位转换比例不存在");
                    }

                    String udLength=Check.Null(wunitInfos.get(0).get("UDLENGTH").toString())?"0":wunitInfos.get(0).get("OUDLENGTH").toString();
                    String wudLength=Check.Null(wunitInfos.get(0).get("OUDLENGTH").toString())?"0":wunitInfos.get(0).get("OUDLENGTH").toString();

                    BigDecimal baseQty=new BigDecimal(detail.getPQty()).multiply(new BigDecimal(unit_ratiop)).setScale(Integer.parseInt(udLength), BigDecimal.ROUND_HALF_UP);
                    BigDecimal wQty=baseQty.divide(new BigDecimal(unit_ratiow),6).setScale(Integer.parseInt(wudLength), BigDecimal.ROUND_HALF_UP);

                    //taxcode 取采购订单销售订单 todo  销售单还没
                    String taxCode = "";
                    String taxRate ="0";
                    String inclTax = "";
                    BigDecimal retailPrice=BigDecimal.ZERO;
                    BigDecimal retailAmt=BigDecimal.ZERO;

                    BigDecimal price=BigDecimal.ZERO;
                    BigDecimal amount=BigDecimal.ZERO;
                    BigDecimal preTaxAmount=BigDecimal.ZERO;
                    BigDecimal taxAmount=BigDecimal.ZERO;
                    String templateType="2";
                    String templateNo=detail.getTemplateNo();

                    BigDecimal stockOutQty=BigDecimal.ZERO;

                    //枚举：1.采购订单 2.退货申请 3.要货申请 4.大客订单
                    //需求类型=1.要货申请，sourceType=3.要货申请
                    //需求类型=2.大客订单，sourceType=4.大客订单
                    if("1".equals(orderType)){
                        sourceType="3";
                        objectType="3";
                        //price=new BigDecimal(detail.getPrice());
                        if(!Check.Null(templateNo)){
                            templateType="1";
                        }
                    }
                    else{
                        sourceType="4";
                        objectType="2";
                        //price=new BigDecimal(detail.getRetailPrice());
                    }

                    if(sourceType.equals("3")){//要货申请  没有税率
                        //StringBuffer pdSql=new StringBuffer("select a.taxcode,b.taxrate,b.INCLTAX from DCP_PURORDER_DETAIL a" +
                        //        " left join DCP_TAXCATEGORY b on a.eid=b.eid and a.taxcode=b.taxcode" +
                        //        " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' " +
                        //        " and a.PURORDERNO='"+detail.getOrderNo()+"' and a.item='"+detail.getOrderItem()+"' ");
                        //List<Map<String, Object>> pdList = this.doQueryData(pdSql.toString(), null);
                       // taxCode = pdList.get(0).get("TAXCODE").toString();
                        //taxRate = pdList.get(0).get("TAXRATE").toString();
                        //inclTax = pdList.get(0).get("INCLTAX").toString();

                        //查询要货单
                        StringBuffer podSql=new StringBuffer("select * from DCP_PORDER_DETAIL " +
                                "where eid='"+eId+"' " + //and organizationno='"+organizationNO+"'
                                " and porderno='"+detail.getOrderNo()+"' and item='"+detail.getOrderItem()+"' ");
                        List<Map<String, Object>> podList = this.doQueryData(podSql.toString(), null);
                        retailPrice = new BigDecimal(podList.get(0).get("PRICE").toString());
                        price = new BigDecimal(podList.get(0).get("DISTRIPRICE").toString());
                    }
                    if(sourceType.equals("4")){

                    }

                    warehouse=detail.getWareHouse();
                    objectId=detail.getObjectId();
                    amount=price.multiply(new BigDecimal(detail.getPQty())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    retailAmt=retailPrice.multiply(new BigDecimal(detail.getPQty())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    taxAmount=amount.multiply(new BigDecimal(taxRate)).setScale(2, BigDecimal.ROUND_HALF_UP);
                    preTaxAmount=amount.subtract(taxAmount);

                    //合计
                    totPqty=totPqty.add(new BigDecimal(detail.getPQty()));
                    totAmt=totAmt.add(amount);
                    totPreTaxAmt=totPreTaxAmt.add(preTaxAmount);
                    totTaxAmt=totTaxAmt.add(taxAmount);
                    totRetailAmt=totRetailAmt.add(retailAmt);


                    ColumnDataValue detailColumns=new ColumnDataValue();
                    detailColumns.add("EID", DataValues.newString(eId));
                    detailColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                    detailColumns.add("BILLNO", DataValues.newString(billNo));
                    detailColumns.add("ITEM",DataValues.newString(detailItem));
                    detailColumns.add("SOURCETYPE",DataValues.newString(sourceType));//1.采购订单 2.退货申请 3.要货申请 4.大客订单
                    detailColumns.add("SOURCEBILLNO",DataValues.newString(detail.getOrderNo()));
                    detailColumns.add("OITEM",DataValues.newString(detail.getOrderItem()));
                    detailColumns.add("PLUNO",DataValues.newString(detail.getPluNo()));
                    detailColumns.add("FEATURENO",DataValues.newString(detail.getFeatureNo()));
                    detailColumns.add("PLUBARCODE",DataValues.newString(detail.getPluBarCode()));
                    detailColumns.add("PUNIT",DataValues.newString(detail.getPUnit()));
                    detailColumns.add("PQTY",DataValues.newDecimal(detail.getPQty()));
                    detailColumns.add("POQTY",DataValues.newDecimal(detail.getPoQty()));
                    detailColumns.add("WAREHOUSE",DataValues.newString(detail.getWareHouse()));
                    detailColumns.add("PRICE",DataValues.newString(price));
                    detailColumns.add("AMOUNT",DataValues.newString(amount));
                    detailColumns.add("PRETAXAMT",DataValues.newString(preTaxAmount));
                    detailColumns.add("TAXAMT",DataValues.newString(taxAmount));
                    detailColumns.add("TEMPLATETYPE",DataValues.newString(templateType));
                    detailColumns.add("TEMPLATENO",DataValues.newString(templateNo));
                    detailColumns.add("TAXCODE",DataValues.newString(taxCode));
                    detailColumns.add("TAXRATE",DataValues.newString(taxRate));
                    detailColumns.add("INCLTAX",DataValues.newString(inclTax));
                    detailColumns.add("BASEUNIT",DataValues.newString(baseUnit));
                    detailColumns.add("BASEQTY",DataValues.newString(baseQty));
                    detailColumns.add("WUNIT",DataValues.newString(wunit));
                    detailColumns.add("WQTY",DataValues.newString(wQty));
                    detailColumns.add("STOCKOUTQTY",DataValues.newString(stockOutQty));
                    detailColumns.add("STATUS",DataValues.newString("1"));
                    detailColumns.add("OBJECTTYPE",DataValues.newString(objectType));
                    detailColumns.add("OBJECTID",DataValues.newString(objectId));
                    detailColumns.add("RETAILPRICE",DataValues.newString(retailPrice));
                    detailColumns.add("RETAILAMT",DataValues.newString(retailAmt));
                    detailColumns.add("UNITRATIO",DataValues.newString(unit_ratiop));
                    detailColumns.add("NOQTY",DataValues.newDecimal(detail.getPQty()));

                    String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                    DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean ib1=new InsBean("DCP_STOCKOUTNOTICE_DETAIL",detailColumnNames);
                    ib1.addValues(detailDataValues);
                    this.addProcessData(new DataProcessBean(ib1));
                }

                //List<Map<String, Object>> pOrderInfo = pOrderList.stream().filter(x -> x.get("PORDERNO").toString().equals(orderNo)).distinct().collect(Collectors.toList());

                List<Object> cList = details.stream().map(x -> {
                    Map map1 = new HashMap();
                    map1.put("pluno", x.getPluNo());
                    map1.put("featureno", x.getFeatureNo());
                    return map1;
                }).distinct().collect(Collectors.toList());
                totCqty=new BigDecimal(cList.size());

                String payType="";//从源单取 不给值
                String payOrgNo="";
                String billDateNo="";
                String payDateNo="";
                String invoiceCode="";
                String currency="";
                String deliverOrgNo=details.get(0).getDeliverOrgNo();
                String templateNo=details.get(0).getTemplateNo();

                if(Check.Null(templateNo)&&filterDemand.size()>0){
                    templateNo=filterDemand.get(0).get("TEMPLATENO").toString();
                }
                String rDate=bDate;
                if(filterDemand.size()>0){
                    rDate=filterDemand.get(0).get("RDATE").toString();
                }

                ColumnDataValue mainColumns=new ColumnDataValue();
                mainColumns.add("EID", DataValues.newString(eId));
                mainColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                mainColumns.add("BDATE",DataValues.newString(bDate));
                mainColumns.add("BILLTYPE", DataValues.newString("3"));//配货通知
                mainColumns.add("BILLNO",DataValues.newString(billNo));
                mainColumns.add("SOURCETYPE",DataValues.newString(sourceType));
                mainColumns.add("SOURCEBILLNO",DataValues.newString(orderNo));
                mainColumns.add("RDATE",DataValues.newString(rDate));
                mainColumns.add("OBJECTTYPE",DataValues.newString(objectType));
                mainColumns.add("OBJECTID",DataValues.newString(objectId));
                mainColumns.add("PAYTYPE",DataValues.newString(payType));
                mainColumns.add("PAYORGNO",DataValues.newString(payOrgNo));
                mainColumns.add("BILLDATENO",DataValues.newString(billDateNo));
                mainColumns.add("PAYDATENO",DataValues.newString(payDateNo));
                mainColumns.add("INVOICECODE",DataValues.newString(invoiceCode));
                mainColumns.add("CURRENCY",DataValues.newString(currency));
                mainColumns.add("DELIVERORGNO",DataValues.newString(deliverOrgNo));
                mainColumns.add("WAREHOUSE",DataValues.newString(warehouse));
                mainColumns.add("BSNO",DataValues.newString(""));
                mainColumns.add("RETURNTYPE",DataValues.newString(""));
                mainColumns.add("TOTCQTY",DataValues.newDecimal(totCqty));
                mainColumns.add("TOTPQTY",DataValues.newDecimal(totPqty));
                mainColumns.add("TOTAMT",DataValues.newDecimal(totAmt));
                mainColumns.add("TOTRETAILAMT",DataValues.newDecimal(totRetailAmt));
                mainColumns.add("TOTPRETAXAMT",DataValues.newDecimal(totPreTaxAmt));
                mainColumns.add("TOTTAXAMT",DataValues.newDecimal(totTaxAmt));
                mainColumns.add("EMPLOYEEID",DataValues.newString(employeeNo));
                mainColumns.add("DEPARTID",DataValues.newString(departmentNo));
                mainColumns.add("STATUS",DataValues.newString("0"));
                mainColumns.add("MEMO",DataValues.newString(""));
                //mainColumns.add("TEMPLATETYPE",DataValues.newString(""));
                mainColumns.add("TEMPLATENO",DataValues.newString(templateNo));


                mainColumns.add("OWNOPID",DataValues.newString(req.getOpNO()));
                mainColumns.add("OWNDEPTID",DataValues.newString(departmentNo));
                mainColumns.add("CREATEOPID",DataValues.newString(req.getOpNO()));
                mainColumns.add("CREATEDEPTID",DataValues.newString(departmentNo));
                mainColumns.add("CREATETIME",DataValues.newDate(lastmoditime));

                String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
                DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib1=new InsBean("DCP_STOCKOUTNOTICE",mainColumnNames);
                ib1.addValues(mainDataValues);
                this.addProcessData(new DataProcessBean(ib1));

            }
        }
        else if("2".equals(billCreateType)){
            List<String> objectIds = orderList.stream().map(o -> o.getObjectId()).distinct().collect(Collectors.toList());
            for(String objectId : objectIds){
                String billNo = this.getOrderNO(req, "PHTZ");
                createNos.add(billNo);
                String finalObjectId = objectId;
                List<DCP_DemandToStockOutNocticeProcessReq.OrderList> details = orderList.stream().filter(x -> x.getObjectId().equals(finalObjectId)).collect(Collectors.toList());
                int detailItem=0;
                String sourceType="";
                String objectType="";
                String warehouse="";
                BigDecimal totCqty=BigDecimal.ZERO;
                BigDecimal totPqty=BigDecimal.ZERO;
                BigDecimal totAmt=BigDecimal.ZERO;
                BigDecimal totPreTaxAmt=BigDecimal.ZERO;
                BigDecimal totTaxAmt=BigDecimal.ZERO;
                BigDecimal totRetailAmt=BigDecimal.ZERO;
                for (DCP_DemandToStockOutNocticeProcessReq.OrderList detail : details){
                    detailItem++;

                    String orderType = detail.getOrderType();

                    List<Map<String, Object>> pluInfos = getGoods.stream().filter(x -> x.get("PLUNO").toString().equals(detail.getPluNo())).collect(Collectors.toList());
                    if(pluInfos.size()<=0){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号信息不存在");
                    }
                    Map<String, Object> singlePlu = pluInfos.get(0);
                    String baseUnit = singlePlu.get("BASEUNIT").toString();
                    String wunit = singlePlu.get("WUNIT").toString();

                    List<Map<String, Object>> plunoUnits = getUnits.stream().filter(x -> x.get("PLUNO").toString().equals(detail.getPluNo())).collect(Collectors.toList());
                    if(plunoUnits.size()<=0){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号单位信息不存在");
                    }
                    List<Map<String, Object>> punitInfos = plunoUnits.stream().filter(x -> x.get("OUNIT").toString().equals(detail.getPUnit())).collect(Collectors.toList());
                    if(punitInfos.size()<=0){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号单位"+detail.getPUnit()+"信息不存在");
                    }
                    List<Map<String, Object>> wunitInfos = plunoUnits.stream().filter(x -> x.get("OUNIT").toString().equals(wunit)).collect(Collectors.toList());
                    if(wunitInfos.size()<=0){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号单位"+wunit+"信息不存在");
                    }
                    String unit_ratiop = punitInfos.get(0).get("UNIT_RATIO").toString();
                    if(Check.Null(unit_ratiop)){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号基准单位-需求单位转换比例不存在");
                    }
                    String unit_ratiow = wunitInfos.get(0).get("UNIT_RATIO").toString();
                    if(Check.Null(unit_ratiow)){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号基准单位-库存单位转换比例不存在");
                    }

                    String udLength=Check.Null(wunitInfos.get(0).get("UDLENGTH").toString())?"0":wunitInfos.get(0).get("OUDLENGTH").toString();
                    String wudLength=Check.Null(wunitInfos.get(0).get("OUDLENGTH").toString())?"0":wunitInfos.get(0).get("OUDLENGTH").toString();

                    BigDecimal baseQty=new BigDecimal(detail.getPQty()).multiply(new BigDecimal(unit_ratiop)).setScale(Integer.parseInt(udLength), BigDecimal.ROUND_HALF_UP);
                    BigDecimal wQty=baseQty.divide(new BigDecimal(unit_ratiow),6).setScale(Integer.parseInt(wudLength), BigDecimal.ROUND_HALF_UP);

                    //taxcode 取采购订单销售订单 todo  销售单还没
                    String taxCode = "";
                    String taxRate ="0";
                    String inclTax = "";
                    BigDecimal retailPrice=BigDecimal.ZERO;
                    BigDecimal retailAmt=BigDecimal.ZERO;

                    BigDecimal price=BigDecimal.ZERO;
                    BigDecimal amount=BigDecimal.ZERO;
                    BigDecimal preTaxAmount=BigDecimal.ZERO;
                    BigDecimal taxAmount=BigDecimal.ZERO;
                    String templateType="2";
                    String templateNo=detail.getTemplateNo();

                    BigDecimal stockOutQty=BigDecimal.ZERO;

                    //枚举：1.采购订单 2.退货申请 3.要货申请 4.大客订单
                    //需求类型=1.要货申请，sourceType=3.要货申请
                    //需求类型=2.大客订单，sourceType=4.大客订单
                    if("1".equals(orderType)){
                        sourceType="3";
                        objectType="3";
                        //price=new BigDecimal(detail.getPrice());
                        if(!Check.Null(templateNo)){
                            templateType="1";
                        }
                    }
                    else{
                        sourceType="4";
                        objectType="2";
                        //price=new BigDecimal(detail.getRetailPrice());
                    }

                    if(sourceType.equals("3")){
                        //StringBuffer pdSql=new StringBuffer("select a.taxcode,b.taxrate,b.INCLTAX from DCP_PURORDER_DETAIL a" +
                        //        " left join DCP_TAXCATEGORY b on a.eid=b.eid and a.taxcode=b.taxcode" +
                        //        " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' " +
                       //         " and a.PURORDERNO='"+detail.getOrderNo()+"' and a.item='"+detail.getOrderItem()+"' ");
                       // List<Map<String, Object>> pdList = this.doQueryData(pdSql.toString(), null);
                       // taxCode = pdList.get(0).get("TAXCODE").toString();
                       // taxRate = pdList.get(0).get("TAXRATE").toString();
                        //inclTax = pdList.get(0).get("INCLTAX").toString();

                        //查询要货单
                        StringBuffer podSql=new StringBuffer("select * from DCP_PORDER_DETAIL " +
                                "where eid='"+eId+"' " + // and organizationno='"+organizationNO+"'
                                " and porderno='"+detail.getOrderNo()+"' and item='"+detail.getOrderItem()+"' ");
                        List<Map<String, Object>> podList = this.doQueryData(podSql.toString(), null);
                        retailPrice = new BigDecimal(podList.get(0).get("PRICE").toString());
                        price = new BigDecimal(podList.get(0).get("DISTRIPRICE").toString());
                    }

                    warehouse=detail.getWareHouse();
                    objectId=detail.getObjectId();
                    amount=price.multiply(new BigDecimal(detail.getPQty())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    retailAmt=retailPrice.multiply(new BigDecimal(detail.getPQty())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    taxAmount=amount.multiply(new BigDecimal(taxRate)).setScale(2, BigDecimal.ROUND_HALF_UP);
                    preTaxAmount=amount.subtract(taxAmount);

                    //合计
                    totPqty=totPqty.add(new BigDecimal(detail.getPQty()));
                    totAmt=totAmt.add(amount);
                    totPreTaxAmt=totPreTaxAmt.add(preTaxAmount);
                    totTaxAmt=totTaxAmt.add(taxAmount);
                    totRetailAmt=totRetailAmt.add(retailAmt);

                    ColumnDataValue detailColumns=new ColumnDataValue();
                    detailColumns.add("EID", DataValues.newString(eId));
                    detailColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                    detailColumns.add("BILLNO", DataValues.newString(billNo));
                    detailColumns.add("ITEM",DataValues.newString(detailItem));
                    detailColumns.add("SOURCETYPE",DataValues.newString(sourceType));//1.采购订单 2.退货申请 3.要货申请 4.大客订单
                    detailColumns.add("SOURCEBILLNO",DataValues.newString(detail.getOrderNo()));
                    detailColumns.add("OITEM",DataValues.newString(detail.getOrderItem()));
                    detailColumns.add("PLUNO",DataValues.newString(detail.getPluNo()));
                    detailColumns.add("FEATURENO",DataValues.newString(detail.getFeatureNo()));
                    detailColumns.add("PLUBARCODE",DataValues.newString(detail.getPluBarCode()));
                    detailColumns.add("PUNIT",DataValues.newString(detail.getPUnit()));
                    detailColumns.add("PQTY",DataValues.newDecimal(detail.getPQty()));
                    detailColumns.add("POQTY",DataValues.newDecimal(detail.getPoQty()));
                    detailColumns.add("WAREHOUSE",DataValues.newString(detail.getWareHouse()));
                    detailColumns.add("PRICE",DataValues.newString(price));
                    detailColumns.add("AMOUNT",DataValues.newString(amount));
                    detailColumns.add("PRETAXAMT",DataValues.newString(preTaxAmount));
                    detailColumns.add("TAXAMT",DataValues.newString(taxAmount));
                    detailColumns.add("TEMPLATETYPE",DataValues.newString(templateType));
                    detailColumns.add("TEMPLATENO",DataValues.newString(templateNo));
                    detailColumns.add("TAXCODE",DataValues.newString(taxCode));
                    detailColumns.add("TAXRATE",DataValues.newString(taxRate));
                    detailColumns.add("INCLTAX",DataValues.newString(inclTax));
                    detailColumns.add("BASEUNIT",DataValues.newString(baseUnit));
                    detailColumns.add("BASEQTY",DataValues.newString(baseQty));
                    detailColumns.add("WUNIT",DataValues.newString(wunit));
                    detailColumns.add("WQTY",DataValues.newString(wQty));
                    detailColumns.add("STOCKOUTQTY",DataValues.newString(stockOutQty));
                    detailColumns.add("STATUS",DataValues.newString("1"));
                    detailColumns.add("OBJECTTYPE",DataValues.newString(objectType));
                    detailColumns.add("OBJECTID",DataValues.newString(objectId));
                    detailColumns.add("RETAILPRICE",DataValues.newString(retailPrice));
                    detailColumns.add("RETAILAMT",DataValues.newString(retailAmt));
                    detailColumns.add("UNITRATIO",DataValues.newString(unit_ratiop));
                    detailColumns.add("NOQTY",DataValues.newDecimal(detail.getPQty()));

                    String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                    DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean ib1=new InsBean("DCP_STOCKOUTNOTICE_DETAIL",detailColumnNames);
                    ib1.addValues(detailDataValues);
                    this.addProcessData(new DataProcessBean(ib1));
                }

                List<Object> cList = details.stream().map(x -> {
                    Map map1 = new HashMap();
                    map1.put("pluno", x.getPluNo());
                    map1.put("featureno", x.getFeatureNo());
                    return map1;
                }).distinct().collect(Collectors.toList());
                totCqty=new BigDecimal(cList.size());

                String payType="";//从源单取 不给值
                String payOrgNo="";
                String billDateNo="";
                String payDateNo="";
                String invoiceCode="";
                String currency="";
                String deliverOrgNo=details.get(0).getDeliverOrgNo();
                String templateNo=details.get(0).getTemplateNo();

                ColumnDataValue mainColumns=new ColumnDataValue();
                mainColumns.add("EID", DataValues.newString(eId));
                mainColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                mainColumns.add("BDATE",DataValues.newString(bDate));
                mainColumns.add("BILLTYPE", DataValues.newString("3"));
                mainColumns.add("BILLNO",DataValues.newString(billNo));
                mainColumns.add("SOURCETYPE",DataValues.newString(sourceType));
                mainColumns.add("SOURCEBILLNO",DataValues.newString(""));
                mainColumns.add("RDATE",DataValues.newString(bDate));
                mainColumns.add("OBJECTTYPE",DataValues.newString(objectType));
                mainColumns.add("OBJECTID",DataValues.newString(objectId));
                mainColumns.add("PAYTYPE",DataValues.newString(payType));
                mainColumns.add("PAYORGNO",DataValues.newString(payOrgNo));
                mainColumns.add("BILLDATENO",DataValues.newString(billDateNo));
                mainColumns.add("PAYDATENO",DataValues.newString(payDateNo));
                mainColumns.add("INVOICECODE",DataValues.newString(invoiceCode));
                mainColumns.add("CURRENCY",DataValues.newString(currency));
                mainColumns.add("DELIVERORGNO",DataValues.newString(deliverOrgNo));
                mainColumns.add("WAREHOUSE",DataValues.newString(warehouse));
                mainColumns.add("BSNO",DataValues.newString(""));
                mainColumns.add("RETURNTYPE",DataValues.newString(""));
                mainColumns.add("TOTCQTY",DataValues.newDecimal(totCqty));
                mainColumns.add("TOTPQTY",DataValues.newDecimal(totPqty));
                mainColumns.add("TOTAMT",DataValues.newDecimal(totAmt));
                mainColumns.add("TOTRETAILAMT",DataValues.newDecimal(totRetailAmt));
                mainColumns.add("TOTPRETAXAMT",DataValues.newDecimal(totPreTaxAmt));
                mainColumns.add("TOTTAXAMT",DataValues.newDecimal(totTaxAmt));
                mainColumns.add("EMPLOYEEID",DataValues.newString(employeeNo));
                mainColumns.add("DEPARTID",DataValues.newString(departmentNo));
                mainColumns.add("STATUS",DataValues.newString("0"));
                mainColumns.add("MEMO",DataValues.newString(""));
                //mainColumns.add("TEMPLATETYPE",DataValues.newString(""));
                //mainColumns.add("TEMPLATENO",DataValues.newString(templateNo));
                mainColumns.add("OWNOPID",DataValues.newString(req.getOpNO()));
                mainColumns.add("OWNDEPTID",DataValues.newString(departmentNo));
                mainColumns.add("CREATEOPID",DataValues.newString(req.getOpNO()));
                mainColumns.add("CREATEDEPTID",DataValues.newString(departmentNo));
                mainColumns.add("CREATETIME",DataValues.newDate(lastmoditime));

                String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
                DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib1=new InsBean("DCP_STOCKOUTNOTICE",mainColumnNames);
                ib1.addValues(mainDataValues);
                this.addProcessData(new DataProcessBean(ib1));

            }
        }
        else if("3".equals(billCreateType)){
            List<String> templateNos = orderList.stream().map(o -> o.getTemplateNo()).distinct().collect(Collectors.toList());
            for(String templateNo : templateNos){
                String billNo = this.getOrderNO(req, "PHTZ");
                createNos.add(billNo);
                List<DCP_DemandToStockOutNocticeProcessReq.OrderList> details = orderList.stream().filter(x -> x.getTemplateNo().equals(templateNo)).collect(Collectors.toList());
                int detailItem=0;
                String sourceType="";
                String warehouse="";
                BigDecimal totCqty=BigDecimal.ZERO;
                BigDecimal totPqty=BigDecimal.ZERO;
                BigDecimal totAmt=BigDecimal.ZERO;
                BigDecimal totPreTaxAmt=BigDecimal.ZERO;
                BigDecimal totTaxAmt=BigDecimal.ZERO;
                BigDecimal totRetailAmt=BigDecimal.ZERO;
                String templateType="";

                for (DCP_DemandToStockOutNocticeProcessReq.OrderList detail : details){
                    detailItem++;

                    String orderType = detail.getOrderType();

                    List<Map<String, Object>> pluInfos = getGoods.stream().filter(x -> x.get("PLUNO").toString().equals(detail.getPluNo())).collect(Collectors.toList());
                    if(pluInfos.size()<=0){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号信息不存在");
                    }
                    Map<String, Object> singlePlu = pluInfos.get(0);
                    String baseUnit = singlePlu.get("BASEUNIT").toString();
                    String wunit = singlePlu.get("WUNIT").toString();

                    List<Map<String, Object>> plunoUnits = getUnits.stream().filter(x -> x.get("PLUNO").toString().equals(detail.getPluNo())).collect(Collectors.toList());
                    if(plunoUnits.size()<=0){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号单位信息不存在");
                    }
                    List<Map<String, Object>> punitInfos = plunoUnits.stream().filter(x -> x.get("OUNIT").toString().equals(detail.getPUnit())).collect(Collectors.toList());
                    if(punitInfos.size()<=0){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号单位"+detail.getPUnit()+"信息不存在");
                    }
                    List<Map<String, Object>> wunitInfos = plunoUnits.stream().filter(x -> x.get("OUNIT").toString().equals(wunit)).collect(Collectors.toList());
                    if(wunitInfos.size()<=0){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号单位"+wunit+"信息不存在");
                    }
                    String unit_ratiop = punitInfos.get(0).get("UNIT_RATIO").toString();
                    if(Check.Null(unit_ratiop)){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号基准单位-需求单位转换比例不存在");
                    }
                    String unit_ratiow = wunitInfos.get(0).get("UNIT_RATIO").toString();
                    if(Check.Null(unit_ratiow)){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号基准单位-库存单位转换比例不存在");
                    }

                    String udLength=Check.Null(wunitInfos.get(0).get("UDLENGTH").toString())?"0":wunitInfos.get(0).get("OUDLENGTH").toString();
                    String wudLength=Check.Null(wunitInfos.get(0).get("OUDLENGTH").toString())?"0":wunitInfos.get(0).get("OUDLENGTH").toString();

                    BigDecimal baseQty=new BigDecimal(detail.getPQty()).multiply(new BigDecimal(unit_ratiop)).setScale(Integer.parseInt(udLength), BigDecimal.ROUND_HALF_UP);
                    BigDecimal wQty=baseQty.divide(new BigDecimal(unit_ratiow),6).setScale(Integer.parseInt(wudLength), BigDecimal.ROUND_HALF_UP);

                    //taxcode 取采购订单销售订单 todo  销售单还没
                    String taxCode = "";
                    String taxRate ="0";
                    String inclTax = "";
                    BigDecimal retailPrice=BigDecimal.ZERO;
                    BigDecimal retailAmt=BigDecimal.ZERO;

                    BigDecimal price=BigDecimal.ZERO;
                    BigDecimal amount=BigDecimal.ZERO;
                    BigDecimal preTaxAmount=BigDecimal.ZERO;
                    BigDecimal taxAmount=BigDecimal.ZERO;

                    BigDecimal stockOutQty=BigDecimal.ZERO;

                    //枚举：1.采购订单 2.退货申请 3.要货申请 4.大客订单
                    //需求类型=1.要货申请，sourceType=3.要货申请
                    //需求类型=2.大客订单，sourceType=4.大客订单
                    String objectType="";
                    if("1".equals(orderType)){
                        sourceType="3";
                        objectType="3";
                        //price=new BigDecimal(detail.getPrice());
                        if(!Check.Null(templateNo)){
                            templateType="1";
                        }
                    }
                    else{
                        sourceType="4";
                        objectType="2";
                        price=new BigDecimal(detail.getRetailPrice());
                    }

                    if(sourceType.equals("3")){
                        //StringBuffer pdSql=new StringBuffer("select a.taxcode,b.taxrate,b.INCLTAX from DCP_PURORDER_DETAIL a" +
                        //        " left join DCP_TAXCATEGORY b on a.eid=b.eid and a.taxcode=b.taxcode" +
                        //        " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' " +
                        //        " and a.PURORDERNO='"+detail.getOrderNo()+"' and a.item='"+detail.getOrderItem()+"' ");
                        //List<Map<String, Object>> pdList = this.doQueryData(pdSql.toString(), null);
                        //taxCode = pdList.get(0).get("TAXCODE").toString();
                        //taxRate = pdList.get(0).get("TAXRATE").toString();
                        //inclTax = pdList.get(0).get("INCLTAX").toString();

                        //查询要货单
                        StringBuffer podSql=new StringBuffer("select * from DCP_PORDER_DETAIL " +
                                "where eid='"+eId+"'  " +//and organizationno='"+organizationNO+"'
                                " and porderno='"+detail.getOrderNo()+"' and item='"+detail.getOrderItem()+"' ");
                        List<Map<String, Object>> podList = this.doQueryData(podSql.toString(), null);
                        retailPrice = new BigDecimal(podList.get(0).get("PRICE").toString());
                        price = new BigDecimal(podList.get(0).get("DISTRIPRICE").toString());
                    }

                    warehouse=detail.getWareHouse();
                    String objectId=detail.getObjectId();
                    amount=price.multiply(new BigDecimal(detail.getPQty())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    retailAmt=retailPrice.multiply(new BigDecimal(detail.getPQty())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    taxAmount=amount.multiply(new BigDecimal(taxRate)).setScale(2, BigDecimal.ROUND_HALF_UP);
                    preTaxAmount=amount.subtract(taxAmount);

                    //合计
                    totPqty=totPqty.add(new BigDecimal(detail.getPQty()));
                    totAmt=totAmt.add(amount);
                    totPreTaxAmt=totPreTaxAmt.add(preTaxAmount);
                    totTaxAmt=totTaxAmt.add(taxAmount);
                    totRetailAmt=totRetailAmt.add(retailAmt);

                    ColumnDataValue detailColumns=new ColumnDataValue();
                    detailColumns.add("EID", DataValues.newString(eId));
                    detailColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                    detailColumns.add("BILLNO", DataValues.newString(billNo));
                    detailColumns.add("ITEM",DataValues.newString(detailItem));
                    detailColumns.add("SOURCETYPE",DataValues.newString(sourceType));//1.采购订单 2.退货申请 3.要货申请 4.大客订单
                    detailColumns.add("SOURCEBILLNO",DataValues.newString(detail.getOrderNo()));
                    detailColumns.add("OITEM",DataValues.newString(detail.getOrderItem()));
                    detailColumns.add("PLUNO",DataValues.newString(detail.getPluNo()));
                    detailColumns.add("FEATURENO",DataValues.newString(detail.getFeatureNo()));
                    detailColumns.add("PLUBARCODE",DataValues.newString(detail.getPluBarCode()));
                    detailColumns.add("PUNIT",DataValues.newString(detail.getPUnit()));
                    detailColumns.add("PQTY",DataValues.newDecimal(detail.getPQty()));
                    detailColumns.add("POQTY",DataValues.newDecimal(detail.getPoQty()));
                    detailColumns.add("WAREHOUSE",DataValues.newString(detail.getWareHouse()));
                    detailColumns.add("PRICE",DataValues.newString(price));
                    detailColumns.add("AMOUNT",DataValues.newString(amount));
                    detailColumns.add("PRETAXAMT",DataValues.newString(preTaxAmount));
                    detailColumns.add("TAXAMT",DataValues.newString(taxAmount));
                    detailColumns.add("TEMPLATETYPE",DataValues.newString(templateType));
                    detailColumns.add("TEMPLATENO",DataValues.newString(templateNo));
                    detailColumns.add("TAXCODE",DataValues.newString(taxCode));
                    detailColumns.add("TAXRATE",DataValues.newString(taxRate));
                    detailColumns.add("INCLTAX",DataValues.newString(inclTax));
                    detailColumns.add("BASEUNIT",DataValues.newString(baseUnit));
                    detailColumns.add("BASEQTY",DataValues.newString(baseQty));
                    detailColumns.add("WUNIT",DataValues.newString(wunit));
                    detailColumns.add("WQTY",DataValues.newString(wQty));
                    detailColumns.add("STOCKOUTQTY",DataValues.newString(stockOutQty));
                    detailColumns.add("STATUS",DataValues.newString("1"));
                    detailColumns.add("OBJECTTYPE",DataValues.newString(objectType));
                    detailColumns.add("OBJECTID",DataValues.newString(objectId));
                    detailColumns.add("RETAILPRICE",DataValues.newString(retailPrice));
                    detailColumns.add("RETAILAMT",DataValues.newString(retailAmt));
                    detailColumns.add("UNITRATIO",DataValues.newString(unit_ratiop));
                    detailColumns.add("NOQTY",DataValues.newDecimal(detail.getPQty()));

                    String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                    DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean ib1=new InsBean("DCP_STOCKOUTNOTICE_DETAIL",detailColumnNames);
                    ib1.addValues(detailDataValues);
                    this.addProcessData(new DataProcessBean(ib1));
                }

                List<Object> cList = details.stream().map(x -> {
                    Map map1 = new HashMap();
                    map1.put("pluno", x.getPluNo());
                    map1.put("featureno", x.getFeatureNo());
                    return map1;
                }).distinct().collect(Collectors.toList());
                totCqty=new BigDecimal(cList.size());

                String payType="";//从源单取 不给值
                String payOrgNo="";
                String billDateNo="";
                String payDateNo="";
                String invoiceCode="";
                String currency="";
                String deliverOrgNo=details.get(0).getDeliverOrgNo();

                ColumnDataValue mainColumns=new ColumnDataValue();
                mainColumns.add("EID", DataValues.newString(eId));
                mainColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                mainColumns.add("BDATE",DataValues.newString(bDate));
                mainColumns.add("BILLTYPE", DataValues.newString("3"));
                mainColumns.add("BILLNO",DataValues.newString(billNo));
                mainColumns.add("SOURCETYPE",DataValues.newString(sourceType));
                mainColumns.add("SOURCEBILLNO",DataValues.newString(""));
                mainColumns.add("RDATE",DataValues.newString(bDate));
                mainColumns.add("OBJECTTYPE",DataValues.newString(""));
                mainColumns.add("OBJECTID",DataValues.newString(""));
                mainColumns.add("PAYTYPE",DataValues.newString(payType));
                mainColumns.add("PAYORGNO",DataValues.newString(payOrgNo));
                mainColumns.add("BILLDATENO",DataValues.newString(billDateNo));
                mainColumns.add("PAYDATENO",DataValues.newString(payDateNo));
                mainColumns.add("INVOICECODE",DataValues.newString(invoiceCode));
                mainColumns.add("CURRENCY",DataValues.newString(currency));
                mainColumns.add("DELIVERORGNO",DataValues.newString(deliverOrgNo));
                mainColumns.add("WAREHOUSE",DataValues.newString(warehouse));
                mainColumns.add("BSNO",DataValues.newString(""));
                mainColumns.add("RETURNTYPE",DataValues.newString(""));
                mainColumns.add("TOTCQTY",DataValues.newDecimal(totCqty));
                mainColumns.add("TOTPQTY",DataValues.newDecimal(totPqty));
                mainColumns.add("TOTAMT",DataValues.newDecimal(totAmt));
                mainColumns.add("TOTRETAILAMT",DataValues.newDecimal(totRetailAmt));
                mainColumns.add("TOTPRETAXAMT",DataValues.newDecimal(totPreTaxAmt));
                mainColumns.add("TOTTAXAMT",DataValues.newDecimal(totTaxAmt));
                mainColumns.add("EMPLOYEEID",DataValues.newString(employeeNo));
                mainColumns.add("DEPARTID",DataValues.newString(departmentNo));
                mainColumns.add("STATUS",DataValues.newString("0"));
                mainColumns.add("MEMO",DataValues.newString(""));
                //mainColumns.add("TEMPLATETYPE",DataValues.newString(templateType));
                mainColumns.add("TEMPLATENO",DataValues.newString(templateNo));
                mainColumns.add("OWNOPID",DataValues.newString(req.getOpNO()));
                mainColumns.add("OWNDEPTID",DataValues.newString(departmentNo));
                mainColumns.add("CREATEOPID",DataValues.newString(req.getOpNO()));
                mainColumns.add("CREATEDEPTID",DataValues.newString(departmentNo));
                mainColumns.add("CREATETIME",DataValues.newDate(lastmoditime));

                String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
                DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib1=new InsBean("DCP_STOCKOUTNOTICE",mainColumnNames);
                ib1.addValues(mainDataValues);
                this.addProcessData(new DataProcessBean(ib1));

            }
        }


        this.doExecuteDataToDB();
        //创建单据并提交成功即按照【需求单号+项次】更新需求底稿[已通知出货量]=原值+[本次分配量]
        //for (String no:createNos) {
        //    String noticeDetailSql = "select a.sourcebillno,a.oitem,a.pqty,nvl(b.STOCKOUTNOQTY,0) as STOCKOUTNOQTY " +
        //            " from dcp_stockoutnotice_detail a " +
         //           " left join dcp_demand b on b.orderno=a.sourcebillno and b.item=a.oitem and a.eid=b.eid " +
       //             " where a.eid='" + eId + "' " +
       //             " and a.billno='" + no + "'";
        //    List<Map<String, Object>> upList = this.doQueryData(noticeDetailSql, null);
         //   for (Map<String, Object> umap : upList) {
       //         String sourcebillno = umap.get("SOURCEBILLNO").toString();
        //        String oitem = umap.get("OITEM").toString();
        //        BigDecimal pQty = new BigDecimal(umap.get("PQTY").toString());
        //        BigDecimal stockoutnoQty = new BigDecimal(umap.get("STOCKOUTNOQTY").toString());
        //        stockoutnoQty = stockoutnoQty.add(pQty);


         //       UptBean ub1 = new UptBean("DCP_DEMAND");
         //       ub1.addUpdateValue("STOCKOUTNOQTY", DataValues.newString(stockoutnoQty));

        //        ub1.addCondition("EID", DataValues.newString(eId));
        //        ub1.addCondition("ORDERNO", DataValues.newString(sourcebillno));
        //        ub1.addCondition("ITEM", DataValues.newString(oitem));
        //        this.addProcessData(new DataProcessBean(ub1));
        //    }
        //}
        //this.doExecuteDataToDB();
        //审核单据
        if("1".equals(confirmType)){
            ParseJson pj = new ParseJson();
            for (String no:createNos){
                DCP_StockOutNoticeStatusUpdateReq noticeReq=new DCP_StockOutNoticeStatusUpdateReq();
                noticeReq.setServiceId("DCP_StockOutNoticeStatusUpdate");
                noticeReq.setToken(req.getToken());
                DCP_StockOutNoticeStatusUpdateReq.Request noticeRequest = noticeReq.new Request();
                noticeRequest.setOpType("confirm");
                noticeRequest.setBillNo(no);
                //noticeRequest.setDocType("3");
                noticeReq.setRequest(noticeRequest);

                String jsontemp= pj.beanToJson(noticeReq);

                //直接调用CRegisterDCP服务
                DispatchService ds = DispatchService.getInstance();
                String resXml = ds.callService(jsontemp, StaticInfo.dao);
                DCP_StockOutNoticeRes resserver=pj.jsonToBean(resXml, new TypeToken<DCP_StockOutNoticeRes>(){});
                if(resserver.isSuccess()==false)
                {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "通知单审核失败！");
                }
            }
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");


    }


    @Override
    protected List<InsBean> prepareInsertData(DCP_DemandToStockOutNocticeProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DemandToStockOutNocticeProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DemandToStockOutNocticeProcessReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DemandToStockOutNocticeProcessReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_DemandToStockOutNocticeProcessReq> getRequestType() {
        return new TypeToken<DCP_DemandToStockOutNocticeProcessReq>(){};
    }

    @Override
    protected DCP_DemandToStockOutNocticeProcessRes getResponseType() {
        return new DCP_DemandToStockOutNocticeProcessRes();
    }
}
