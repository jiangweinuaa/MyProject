package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.impl.TableEntityDao;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataGenerateReq;
import com.dsc.spos.json.cust.req.DCP_PurReceiveCreateReq;
import com.dsc.spos.json.cust.req.DCP_SStockInCreateReq;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataGenerateRes;
import com.dsc.spos.json.cust.res.DCP_PurReceiveCreateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.model.DcpGood;
import com.dsc.spos.model.DcpGoodsUnit;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 20250513 入参receivingNo,rItem, purOrderNo, poItem, poItem2传null不做默认值处理
 */
public class DCP_PurReceiveCreate extends SPosAdvanceService<DCP_PurReceiveCreateReq, DCP_PurReceiveCreateRes> {
    @Override
    protected void processDUID(DCP_PurReceiveCreateReq req, DCP_PurReceiveCreateRes res) throws Exception {
        //try {
        String nowDate = DateFormatUtils.getPlainDate(DateFormatUtils.getNowDateTime());

        req.getRequest().setOrgNo(req.getOrganizationNO());
        req.getRequest().setCorp(req.getCorp());

        List<Map<String, Object>> queryData=new ArrayList<>();//通知单的数据
        if (StringUtils.isNotEmpty(req.getRequest().getReceivingNo())) {
                String querySql = String.format(" SELECT a.*,b.item,b.PROC_RATE,b.STOCKIN_QTY,b.PQTY FROM DCP_RECEIVING a " +
                        " left join DCP_RECEIVING_detail b on a.eid=b.eid and a.receivingno=b.receivingno " +
                        " WHERE a.EID='%s' AND a.RECEIVINGNO='%s' ", req.geteId(), req.getRequest().getReceivingNo());
                queryData = doQueryData(querySql, null);

                if (queryData == null || queryData.isEmpty()) {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "未查询到对应的收货通知单号");
                }
                if ("0".equals(queryData.get(0).get("STATUS").toString())) {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前收获通知单号:" + req.getRequest().getReceivingNo() + "不可做单！");
                }

            List<Map<String, Object>> finalQueryData = queryData;
            req.getRequest().getDataList().forEach(x->{
                    if(Check.Null(x.getProcRate())&&Check.NotNull(x.getRItem())){
                        List<Map<String, Object>> filterRows = finalQueryData.stream().filter(y -> x.getRItem().equals(y.get("ITEM").toString())).collect(Collectors.toList());
                        if(filterRows.size()>0){
                            x.setProcRate(filterRows.get(0).get("PROC_RATE").toString());
                        }
                    }
                });

            }
            if(Check.Null(req.getRequest().getPayee())){
                String bizSql="select * from DCP_BIZPARTNER where eid='"+req.geteId()+"' and bizpartnerno='"+req.getRequest().getSupplier()+"'  ";
                List<Map<String, Object>> bizList = this.doQueryData(bizSql, null);
                if(bizList.size()>0){
                    req.getRequest().setPayee(bizList.get(0).get("PAYEE").toString());
                }
            }

            if(Check.Null(req.getRequest().getCorp())){
                //String orgSql="select * from dcp_org a where a.eid='"+req.geteId()+"' and a.organizationno='"+ req.getOrganizationNO()+"'";
                //List<Map<String, Object>> orgList = this.doQueryData(orgSql, null);
                //if(orgList.size()>0){
                //    req.getRequest().setCorp(orgList.get(0).get("CORP").toString());
                //}

                req.getRequest().setCorp(req.getCorp());

            }
            if(Check.NotNull(req.getRequest().getPurOrderNo())){
                String purOrderSql="select * from dcp_purorder a where a.eid='"+req.geteId()+"' and a.purorderno='"+req.getRequest().getPurOrderNo()+"'";
                List<Map<String, Object>> purOrderList = this.doQueryData(purOrderSql, null);
                if(purOrderList.size()>0){
                    Map<String, Object> stringObjectMap = purOrderList.get(0);
                    if(Check.Null(req.getRequest().getBizCorp())){
                        req.getRequest().setBizCorp(stringObjectMap.get("CORP").toString());
                    }
                    if(Check.Null(req.getRequest().getBizOrgNo())){
                        req.getRequest().setBizOrgNo(stringObjectMap.get("ORGANIZATIONNO").toString());
                    }
                    if(Check.Null(req.getRequest().getPurType())){
                        req.getRequest().setPurType(stringObjectMap.get("PURTYPE").toString());
                    }

                    if(Check.Null(req.getRequest().getTaxPayerType())){
                        req.getRequest().setTaxPayerType(stringObjectMap.get("TAXPAYER_TYPE").toString());
                    }
                    if(Check.Null(req.getRequest().getInputTaxCode())){
                        req.getRequest().setInputTaxCode(stringObjectMap.get("INPUT_TAXCODE").toString());
                    }
                    if(Check.Null(req.getRequest().getInputTaxRate())){
                        req.getRequest().setInputTaxRate(stringObjectMap.get("INPUT_TAXRATE").toString());
                    }


                }
            }

        //2 小规模纳税人 进项税率为0
        if(("2").equals(req.getRequest().getTaxPayerType())){
            if(new BigDecimal(req.getRequest().getInputTaxRate()).compareTo(BigDecimal.ZERO)!=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "当前组织所属法人为「小规模纳税人」，进项税率需设置为0%，请检查【组织信息】设置".toString());
            }
        }

        String taxSql="select * from DCP_TAXCATEGORY a where a.eid='"+req.geteId()+"' ";
        List<Map<String, Object>> getTax = this.doQueryData(taxSql, null);
        MyCommon cm=new MyCommon();

        //没来源的赠品taxcode 去掉
        req.getRequest().getDataList().forEach(x->{
            if(Check.Null(x.getReceivingNo())){
                x.setTaxCode("");
                x.setTaxRate("0");
                x.setTaxCalType("");
                x.setInclTax("");
            }
        });

        //赠品收货
        List<DCP_PurReceiveCreateReq.DataList> giftFilterList = req.getRequest().getDataList().stream().filter(x -> Check.Null(x.getTaxCode()) ).collect(Collectors.toList());
            if(giftFilterList.size()>0){
                String ptemplateSql="select distinct a.PURTEMPLATENO,b.TAXCODE,b.pluno,c.taxrate,c.taxcaltype,c.incltax " +
                        " from DCP_PURCHASETEMPLATE a " +
                        " inner join DCP_PURCHASETEMPLATE_GOODS b on a.eid=b.eid and a.PURTEMPLATENO=b.PURTEMPLATENO " +
                        " inner join DCP_TAXCATEGORY c on c.eid=a.eid and c.taxcode=b.taxcode " +
                        " where a.eid='"+req.geteId()+"' and a.status='100' and a.SUPPLIERNO='"+req.getRequest().getSupplier()+"' " +
                        //" and a.PURTYPE='"+req.getRequest().getPurType()+"' " +
                        " ";
                List<Map<String, Object>> ptemplateList = this.doQueryData(ptemplateSql, null);

                String querySupplier="select  a.taxcode,c.taxrate,c.taxcaltype,c.incltax " +
                        " from DCP_BIZPARTNER a " +
                        " inner join DCP_TAXCATEGORY c on c.eid=a.eid and c.taxcode=a.taxcode " +
                        " where a.eid='"+req.geteId()+"' " +
                        " and a.bizpartnerno='"+req.getRequest().getSupplier()+"' ";
                List<Map<String, Object>> supplierList = this.doQueryData(querySupplier, null);


                //加上商品上面的进项税
                String pluNos = giftFilterList.stream().map(x -> x.getPluNo()).collect(Collectors.joining(","))+",";
                Map<String, String> mapPluNo=new HashMap<String, String>();
                mapPluNo.put("PLUNO", pluNos.toString());

                String withasSql_pluno="";
                withasSql_pluno=cm.getFormatSourceMultiColWith(mapPluNo);
                mapPluNo=null;

                String pluSql="with p as ("+withasSql_pluno+") " +
                        " select a.*,b.taxrate as inputtaxrate,b.taxcaltype as inputtaxcaltype,b.incltax as inputincltax" +
                        " from dcp_goods a " +
                        " inner join p on a.pluno=p.pluno " +
                        " left join dcp_taxcategory b on a.eid=b.eid and a.inputtaxcode=b.taxcode " +
                        " where a.eid='"+req.geteId()+"' ";
                List<Map<String, Object>> pluList = this.doQueryData(pluSql, null);

                req.getRequest().getDataList().forEach(x->{
                    if(Check.Null(x.getTaxCode())){
                        List<Map<String, Object>> pluFilters = ptemplateList.stream().filter(y -> y.get("PLUNO").toString().equals(x.getPluNo())).collect(Collectors.toList());

                        //如果模板编号不为空 加上模板编号
                        //if(Check.NotNull(x.getpur())){
                        //    pluFilters = ptemplateList.stream().filter(y -> y.get("PLUNO").toString().equals(x.getPluNo())&&y.get("PURTEMPLATENO").toString().equals(x.getPTemplateNo())).collect(Collectors.toList());
                        //}
                        if(pluFilters.size()>0) {
                            x.setTaxCode(pluFilters.get(0).get("TAXCODE").toString());
                            x.setTaxRate(pluFilters.get(0).get("TAXRATE").toString());
                            x.setInclTax(pluFilters.get(0).get("INCLTAX").toString());
                            x.setTaxCalType(pluFilters.get(0).get("TAXCALTYPE").toString());
                        }

                        if(Check.Null(x.getTaxCode())&&supplierList.size()>0){
                            x.setTaxCode(supplierList.get(0).get("TAXCODE").toString());
                            x.setTaxRate(supplierList.get(0).get("TAXRATE").toString());
                            x.setInclTax(supplierList.get(0).get("INCLTAX").toString());
                            x.setTaxCalType(supplierList.get(0).get("TAXCALTYPE").toString());
                        }

                        if(Check.Null(x.getTaxCode())) {
                            List<Map<String, Object>> giftPluFilterRows = pluList.stream().filter(y -> y.get("PLUNO").toString().equals(x.getPluNo())).collect(Collectors.toList());
                            if(giftPluFilterRows.size()>0){
                                x.setTaxCode(giftPluFilterRows.get(0).get("INPUTTAXCODE").toString());
                                x.setTaxRate(giftPluFilterRows.get(0).get("INPUTTAXRATE").toString());
                                x.setInclTax(giftPluFilterRows.get(0).get("INPUTINCLTAX").toString());
                                x.setTaxCalType(giftPluFilterRows.get(0).get("INPUTTAXCALTYPE").toString());
                            }

                        }

                    }
                });

            }

        if(("2").equals(req.getRequest().getTaxPayerType())){
            req.getRequest().getDataList().forEach(x->{
                if(Check.Null(x.getReceivingNo())){//没有前置的通知单 就是没有来源
                    x.setTaxCode(req.getInputTaxCode());
                    List<Map<String, Object>> taxcodes = getTax.stream().filter(y -> y.get("TAXCODE").toString().equals(x.getTaxCode())).collect(Collectors.toList());
                    if(taxcodes.size()>0){
                        x.setTaxRate(taxcodes.get(0).get("TAXRATE").toString());
                        x.setTaxCalType(taxcodes.get(0).get("TAXCALTYPE").toString());
                        x.setInclTax(taxcodes.get(0).get("INCLTAX").toString());
                    }
                }
            });
        }


        String queryPurOrderNo = " SELECT a.*,b.PURAMT,b.PLUNO,b.PURPRICE,b.TAXCODE,b.TAXRATE,b.INCLTAX,b.PURAMT,b.PURTEMPLATENO,b.UNITRATIO," +
                    " b.receiveprice,b.receiveamt,b.item,b.SUPPRICE,c.taxcode as taxcodey,c.taxrate as taxratey,c.item as itemy " +
                    " FROM DCP_PURORDER a " +
                " INNER JOIN DCP_PURORDER_DETAIL b on a.EID=b.EID and a.PURORDERNO=b.PURORDERNO" +
                " left join dcp_purorder_delivery c on c.eid=a.eid and c.purorderno=a.purorderno and c.item2=b.item " +
                " WHERE a.EID='%s' and a.PURORDERNO='%s' ";
            List<Map<String, Object>> getQueryPurOrder = doQueryData(String.format(queryPurOrderNo, req.geteId(), req.getRequest().getPurOrderNo()), null);

            TableEntityDao entityDao = MySpringContext.getBean("entityDao");

            String ticketNo = getOrderNO(req, "CGSH");

            double totCQty = 0;
            double totPQty = 0;
            double totPurAmt = 0;
            BigDecimal totReceiveAmt = BigDecimal.ZERO;
            BigDecimal totSupAmt=BigDecimal.ZERO;

            String lastModiTime = DateFormatUtils.getNowDateTime();

            ColumnDataValue dcp_purReceive = new ColumnDataValue();

            dcp_purReceive.add("EID", DataValues.newString(req.geteId()));
            dcp_purReceive.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrgNo()));
            dcp_purReceive.add("BDATE", DataValues.newString(DateFormatUtils.getPlainDate(req.getRequest().getBDate())));
            dcp_purReceive.add("BILLNO", DataValues.newString(ticketNo));

            dcp_purReceive.add("RECEIVINGNO", DataValues.newStringNull(req.getRequest().getReceivingNo()));
            dcp_purReceive.add("PURORDERNO", DataValues.newStringNull(req.getRequest().getPurOrderNo()));
            dcp_purReceive.add("SUPPLIER", DataValues.newString(req.getRequest().getSupplier()));
            dcp_purReceive.add("EMPLOYEEID", DataValues.newString(req.getRequest().getEmployeeID()));
            dcp_purReceive.add("DEPARTID", DataValues.newString(req.getRequest().getDepartID()));
            dcp_purReceive.add("PURORGNO", DataValues.newString(req.getRequest().getPurOrgNo()));
            dcp_purReceive.add("PAYEE", DataValues.newString(req.getRequest().getPayee()));
            dcp_purReceive.add("CORP", DataValues.newString(req.getRequest().getCorp()));
            dcp_purReceive.add("BIZORGNO", DataValues.newString(req.getRequest().getBizOrgNo()));
            dcp_purReceive.add("BIZCORP", DataValues.newString(req.getRequest().getBizCorp()));
            dcp_purReceive.add("PURTYPE", DataValues.newString(req.getRequest().getPurType()));

            dcp_purReceive.add("WAREHOUSE", DataValues.newString(req.getRequest().getWareHouse()));


            if (getQueryPurOrder != null && !getQueryPurOrder.isEmpty()) {
                Map<String, Object> purOrder = getQueryPurOrder.get(0);
                dcp_purReceive.add("PAYTYPE", DataValues.newString(purOrder.get("PAYTYPE")));
                dcp_purReceive.add("PURORGNO", DataValues.newString(purOrder.get("ORGANIZATIONNO")));
                dcp_purReceive.add("PAYORGNO", DataValues.newString(purOrder.get("PAYORGNO")));
                dcp_purReceive.add("BILLDATENO", DataValues.newString(purOrder.get("BILLDATENO")));
                dcp_purReceive.add("PAYDATENO", DataValues.newString(purOrder.get("PAYDATENO")));
                dcp_purReceive.add("INVOICECODE", DataValues.newString(purOrder.get("INVOICECODE")));
                dcp_purReceive.add("CURRENCY", DataValues.newString(purOrder.get("CURRENCY")));
            }

            dcp_purReceive.add("DELIVERYFEE", DataValues.newString(req.getRequest().getDeliveryFee()));
            dcp_purReceive.add("STATUS", DataValues.newInteger(0));
            dcp_purReceive.add("MEMO", DataValues.newString(req.getRequest().getMemo()));
            dcp_purReceive.add("OWNOPID", DataValues.newString(req.getRequest().getEmployeeID()));
            dcp_purReceive.add("OWNDEPTID", DataValues.newString(req.getRequest().getDepartID()));
            dcp_purReceive.add("CREATEOPID", DataValues.newString(req.getOpNO()));
            dcp_purReceive.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
            dcp_purReceive.add("CREATETIME", DataValues.newDate(lastModiTime));


        req.getRequest().getDataList().forEach(x->{
            if(Check.Null(x.getProcRate())){
                x.setProcRate("0");
            }
        });

        //增加一个校验
        if(req.getRequest().getDataList().size()>0){
            List<String> collect = req.getRequest().getDataList().stream().filter(x -> Check.NotNull(x.getReceivingNo()) && Check.NotNull(x.getRItem())).map(x -> x.getRItem()).collect(Collectors.toList());
            if(collect.size()>0){
                List<String> errorMsg = new ArrayList<>();
                for (String item : collect){
                    List<DCP_PurReceiveCreateReq.DataList> filterRows1 = req.getRequest().getDataList().stream().filter(x ->    Check.NotNull(x.getRItem())&& x.getRItem().equals(item)).collect(Collectors.toList());
                    BigDecimal sumPqty = filterRows1.stream().map(x -> new BigDecimal(x.getPQty())).reduce(BigDecimal::add).get();
                    List<Map<String, Object>> receiveRows = queryData.stream().filter(x -> x.get("ITEM").toString().equals(item)).collect(Collectors.toList());
                    if(receiveRows.size()>0){
                        String pluNo = filterRows1.get(0).getPluNo();
                        String pluName = filterRows1.get(0).getPluName();

                        BigDecimal canReceiveQty = new BigDecimal(receiveRows.get(0).get("PQTY").toString()).subtract( new BigDecimal(Check.Null(receiveRows.get(0).get("STOCKIN_QTY").toString())?"0":receiveRows.get(0).get("STOCKIN_QTY").toString()));
                        BigDecimal procRate = new BigDecimal(Check.Null(receiveRows.get(0).get("PROC_RATE").toString()) ? "0" : receiveRows.get(0).get("PROC_RATE").toString()).divide(new BigDecimal(100),6, RoundingMode.HALF_UP);
                        BigDecimal maxReceiveQty = BigDecimal.ONE.add(procRate).multiply(canReceiveQty).setScale(0, RoundingMode.UP);

                        if(maxReceiveQty.compareTo(sumPqty)<0){
                            String errorMessage=""+pluNo+"/"+pluName+"收货量共"+sumPqty+"个，超出本次收货量的上限值（"+maxReceiveQty+"）";
                            //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, ""+pluNo+"/"+pluName+"收货量共"+sumPqty+"个，超出本次收货量的上限值（"+maxReceiveQty+"）");
                            errorMsg.add(errorMessage);
                        }


                    }
                }

                if(errorMsg.size()>0){
                    Optional<String> reduce = errorMsg.stream().distinct().reduce((x, y) -> x + "\n" + y);
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, reduce.get());
                }
            }
        }


            List<DCP_PurReceiveCreateReq.DataList> dataList = req.getRequest().getDataList();


            for (DCP_PurReceiveCreateReq.DataList detail : dataList) {
                ColumnDataValue dcp_purReceive_detail = new ColumnDataValue();

                //receiveprice  receiveamt
                List<Map<String, Object>> poDetailList = getQueryPurOrder.stream().filter(x -> x.get("ITEM").toString().equals(detail.getPoItem())).collect(Collectors.toList());
                if(poDetailList.size()>0){

                    List<Map<String, Object>> poDetailY = poDetailList.stream().filter(x -> x.get("ITEMY").toString().equals(detail.getPoItem2())).collect(Collectors.toList());

                    if(poDetailY.size()>0){
                        if(Check.Null(detail.getTaxCode())){
                            detail.setTaxCode(poDetailY.get(0).get("TAXCODEY").toString());
                        }
                        if(Check.Null(detail.getTaxRate())){
                            detail.setTaxRate(poDetailY.get(0).get("TAXRATEY").toString());
                        }
                    }


                    if(Check.Null(detail.getReceivePrice())){
                        detail.setReceivePrice(poDetailList.get(0).get("RECEIVEPRICE").toString());

                        if(Check.Null(detail.getReceivePrice())){
                            detail.setReceivePrice("0");
                        }

                    }
                    if(Check.Null(detail.getSupPrice())){
                        detail.setSupPrice(poDetailList.get(0).get("SUPPRICE").toString());
                        if(Check.Null(detail.getSupPrice())){
                            detail.setSupPrice("0");
                        }
                    }
                    if(Check.Null(detail.getReceiveAmt())){
                        BigDecimal multiply = new BigDecimal(detail.getPQty()).multiply(new BigDecimal(detail.getReceivePrice()));
                        detail.setReceiveAmt(multiply.toString());
                    }
                    if(Check.Null(detail.getSupAmt())){
                        BigDecimal multiply = new BigDecimal(detail.getPQty()).multiply(new BigDecimal(detail.getSupPrice()));
                        detail.setSupAmt(multiply.toString());
                    }
                }

                if(Check.Null(detail.getProcRate())){
                    detail.setProcRate("0");
                }

                //有taxcode  更新下taxrate  taxcaltype incltax  防止前端没传
                if(Check.NotNull(detail.getTaxCode())){
                    if(Check.Null(detail.getTaxRate())||Check.Null(detail.getTaxCalType())||Check.Null(detail.getInclTax())){
                        List<Map<String, Object>> taxcodes = getTax.stream().filter(y -> y.get("TAXCODE").toString().equals(detail.getTaxCode())).collect(Collectors.toList());
                        if(taxcodes.size()>0){
                            detail.setTaxRate(taxcodes.get(0).get("TAXRATE").toString());
                            detail.setTaxCalType(taxcodes.get(0).get("TAXCALTYPE").toString());
                            detail.setInclTax(taxcodes.get(0).get("INCLTAX").toString());
                        }
                    }
                }



                totReceiveAmt=totReceiveAmt.add(new BigDecimal(Check.Null(detail.getReceiveAmt())?"0":detail.getReceiveAmt()));
                totSupAmt=totSupAmt.add(new BigDecimal(Check.Null(detail.getSupAmt())?"0":detail.getSupAmt()));


                Map<String, Object> condition = Maps.newHashMap();
                condition.put("PLUNO", detail.getPluNo());
//                condition.put("FEATURENO",detail.getFeatureNo());
                List<Map<String, Object>> purData = MapDistinct.getWhereMap(getQueryPurOrder, condition, true);
                Map<String, Object> oneData;

                DcpGood dcpGood = entityDao.queryOne(String.format(" SELECT * FROM DCP_GOODS WHERE EID='%s' and PLUNO='%s' ", req.geteId(), detail.getPluNo()), DcpGood.class);

                if(detail.getProdDate().length()>8){
                    detail.setProdDate(DateFormatUtils.getPlainDate(detail.getProdDate()));
                }
                if(detail.getExpDate().length()>8){
                    detail.setExpDate(DateFormatUtils.getPlainDate(detail.getExpDate()));
                }

                //收货单保存校验：
                //【生产日期】：
                //1.商品启用批号管理isBatch=Y且保质期天数shelfLife>0时，生产日期不可为空；并且生产日期不可大于系统日期；
                //2.商品启用批号管理isBatch=Y且保质期天数shelfLife=0或者null时，生产日期若未入参，生产日期=系统日期，有效日期为null;
                //
                //【有效日期】：
                //1.商品启用批号管理isBatch=Y且保质期天数shelfLife>0时，生产日期不可为空；并且有效日期值不可小于或等于系统日期；
                //
                //商品启用批号isBatch=N, 则无需赋值生产日期、有效日期；字段值为空
                if (dcpGood.getIsbatch().equals("Y") ) {
                    if(dcpGood.getShelflife()==null||dcpGood.getShelflife()==0){
                        if(Check.Null(detail.getProdDate())){
                            detail.setProdDate(nowDate);
                            detail.setExpDate("");
                        }
                    }else if(dcpGood.getShelflife()>0){
                        if(Check.NotNull(detail.getProdDate())&&Integer.valueOf(detail.getProdDate())>Integer.valueOf(nowDate)){
                            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号"+detail.getPluNo()+"生产日期不可为空,且不可大于当前日期！");
                        }
                        if(Check.NotNull(detail.getExpDate())&&Integer.valueOf(detail.getExpDate())<=Integer.valueOf(nowDate)){
                            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号"+detail.getPluNo()+"有效日期不可小于当前日期！");
                        }
                    }
                }

                if (null != purData && !purData.isEmpty()) {
                    oneData = purData.get(0);
                    totPurAmt += Double.parseDouble(oneData.get("PURAMT").toString());

                    dcp_purReceive_detail.add("PURPRICE", DataValues.newString(oneData.get("PURPRICE")));
                    dcp_purReceive_detail.add("CATEGORY", DataValues.newString(dcpGood.getCategory()));

                    double amt = BigDecimalUtils.mul(Double.parseDouble(detail.getPQty()), Double.parseDouble(oneData.get("PURPRICE").toString()), 3);

                    dcp_purReceive_detail.add("PURAMT", DataValues.newDecimal(amt));
                    dcp_purReceive_detail.add("PURTEMPLATENO", DataValues.newString(oneData.get("PURTEMPLATENO")));

                }
                totCQty++;

                totPQty += Double.parseDouble(detail.getPQty());
                double unitRatio = 1;
                if (StringUtils.isNotEmpty(detail.getUnitRatio())) {
                    unitRatio = Double.parseDouble(detail.getUnitRatio());
                }

                if (unitRatio == 0) {
                    DcpGoodsUnit dcpgoodsunit = entityDao.queryOne(String.format(" SELECT * FROM DCP_GOODS_UNIT WHERE EID='%s' and PLUNO='%s' and OUNIT='%s' and UNIT='%s' ", req.geteId(), detail.getPluNo(), detail.getPUnit(), detail.getBaseUnit()), DcpGoodsUnit.class);
                    if (null != dcpgoodsunit) {
                        unitRatio = dcpgoodsunit.getUnitratio().doubleValue();
                    } else {
                        unitRatio = 1;
                    }
                }

                dcp_purReceive_detail.add("TAXCODE", DataValues.newString(detail.getTaxCode()));
                dcp_purReceive_detail.add("TAXRATE", DataValues.newString(detail.getTaxRate()));
                dcp_purReceive_detail.add("INCLTAX", DataValues.newString(detail.getInclTax()));


                dcp_purReceive_detail.add("EID", DataValues.newString(req.geteId()));
                dcp_purReceive_detail.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrgNo()));
                dcp_purReceive_detail.add("BILLNO", DataValues.newString(ticketNo));
                dcp_purReceive_detail.add("ITEM", DataValues.newInteger(detail.getItem()));
                dcp_purReceive_detail.add("RECEIVINGNO", DataValues.newStringNull(detail.getReceivingNo()));
                dcp_purReceive_detail.add("PURORDERNO", DataValues.newStringNull(detail.getPurOrderNo()));
                dcp_purReceive_detail.add("RECEIVINGITEM", DataValues.newInteger(detail.getRItem()));
                dcp_purReceive_detail.add("POITEM", DataValues.newInteger(detail.getPoItem()));
                dcp_purReceive_detail.add("POITEM2", DataValues.newInteger(detail.getPoItem2()));
//                dcp_purReceive_detail.add("PLUBARCODE",DataValues.newString(detail.getPluBarcode()));
                dcp_purReceive_detail.add("PLUNO", DataValues.newString(detail.getPluNo()));
                dcp_purReceive_detail.add("FEATURENO", DataValues.newString(detail.getFeatureNo()));

                dcp_purReceive_detail.add("MEMO", DataValues.newString(detail.getMemo()));

                dcp_purReceive_detail.add("ISGIFT", DataValues.newString(detail.getIsGift()));
                if ("1".equals(detail.getIsGift())) {
                    dcp_purReceive_detail.add("PURPRICE", DataValues.newString(0));
                    dcp_purReceive_detail.add("PURAMT", DataValues.newDecimal(0));
                }

                if (StringUtils.isEmpty(detail.getWareHouse())) {
                    dcp_purReceive_detail.add("WAREHOUSE", DataValues.newString(req.getRequest().getWareHouse()));
                } else {
                    dcp_purReceive_detail.add("WAREHOUSE", DataValues.newString(detail.getWareHouse()));
                }
                dcp_purReceive_detail.add("PUNIT", DataValues.newString(detail.getPUnit()));
                dcp_purReceive_detail.add("PQTY", DataValues.newDecimal(detail.getPQty()));

                double baseQty;
                if (StringUtils.isEmpty(detail.getBaseQty())) {
                    baseQty = BigDecimalUtils.mul(Double.parseDouble(detail.getPQty()), unitRatio);
                } else {
                    baseQty = Double.parseDouble(detail.getBaseQty());
                }

                dcp_purReceive_detail.add("BASEQTY", DataValues.newDecimal(baseQty));

                String productDate = detail.getProdDate();
                if (StringUtils.isEmpty(productDate)) {
                    productDate = DateFormatUtils.getNowDate();
                }
                dcp_purReceive_detail.add("BATCHNO", DataValues.newString(PosPub.getBatchNo(dao, req.geteId(), req.getOrganizationNO(), "", detail.getPluNo(), detail.getFeatureNo(), productDate, detail.getExpDate(), req.getEmployeeNo(), req.getEmployeeName(), "",false)));
//                dcp_purReceive_detail.add("BATCHNO",DataValues.newString(PosPub.getBatchNo(req.geteId(),req.getRequest().getOrgNo(),detail.getPluNo(),detail.getProdDate())));
                dcp_purReceive_detail.add("PRODDATE", DataValues.newString(detail.getProdDate()));

                dcp_purReceive_detail.add("EXPDATE", DataValues.newString(detail.getExpDate()));


                dcp_purReceive_detail.add("UNITRATIO", DataValues.newString(unitRatio));
                dcp_purReceive_detail.add("QCSTATUS", DataValues.newString(detail.getQcStatus()));
                dcp_purReceive_detail.add("BASEUNIT", DataValues.newString(detail.getBaseUnit()));
                dcp_purReceive_detail.add("TAXCALTYPE", DataValues.newString(detail.getTaxCalType()));
//                dcp_purReceive_detail.add("UNITRATIO",DataValues.newString(good.getBaseunit()));
                dcp_purReceive_detail.add("RECEIVEPRICE", DataValues.newString(detail.getReceivePrice()));
                dcp_purReceive_detail.add("RECEIVEAMT", DataValues.newDecimal(detail.getReceiveAmt()));

                dcp_purReceive_detail.add("SUPPRICE", DataValues.newString(detail.getSupPrice()));
                dcp_purReceive_detail.add("SUPAMT", DataValues.newDecimal(detail.getSupAmt()));
                dcp_purReceive_detail.add("PROCRATE", DataValues.newDecimal(detail.getProcRate()));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("dcp_purReceive_detail", dcp_purReceive_detail)));
            }

            dcp_purReceive.add("TOTCQTY", DataValues.newDecimal(totCQty));
            dcp_purReceive.add("TOTPQTY", DataValues.newDecimal(totPQty));
            dcp_purReceive.add("TOTPURAMT", DataValues.newDecimal(totPurAmt));
            dcp_purReceive.add("TOTRECEIVEAMT", DataValues.newDecimal(totReceiveAmt));
            dcp_purReceive.add("TOTSUPAMT", DataValues.newDecimal(totSupAmt));

        dcp_purReceive.add("TAXPAYER_TYPE",req.getRequest().getTaxPayerType(), Types.VARCHAR);
        dcp_purReceive.add("INPUT_TAXCODE",req.getRequest().getInputTaxCode(),Types.VARCHAR);
        dcp_purReceive.add("INPUT_TAXRATE",req.getRequest().getInputTaxRate(),Types.VARCHAR);



        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("dcp_purReceive", dcp_purReceive)));

            if (StringUtils.isNotEmpty(req.getRequest().getReceivingNo())){
                ColumnDataValue condition = new ColumnDataValue();
                ColumnDataValue dcp_receiving = new ColumnDataValue();
                condition.add("EID", DataValues.newString(req.geteId()));
                condition.add("RECEIVINGNO", DataValues.newString(req.getRequest().getReceivingNo()));

                dcp_receiving.add("STATUS", DataValues.newInteger(1));

                this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_RECEIVING", condition, dcp_receiving)));

            }

            this.doExecuteDataToDB();
            res.setBillNo(ticketNo);

            if(req.getRequest().getCorp().equals(req.getRequest().getBizCorp())) {
                DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                inReq.setServiceId("DCP_InterSettleDataGenerate");
                inReq.setToken(req.getToken());
                DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                request1.setOrganizationNo(req.getOrganizationNO());
                request1.setBillNo(ticketNo);
                request1.setBillType("11001");
                request1.setSupplyOrgNo(req.getRequest().getBizOrgNo());
                request1.setDetail(new ArrayList<>());
                for (DCP_PurReceiveCreateReq.DataList par : req.getRequest().getDataList()) {
                    detail.setReceiveOrgNo(req.getOrganizationNO());
                    detail.setSourceBillNo(req.getRequest().getReceivingNo());
                    detail.setSourceItem(par.getRItem());
                    detail.setItem(String.valueOf(par.getItem()));
                    detail.setPluNo(par.getPluNo());
                    detail.setFeatureNo(par.getFeatureNo());
                    detail.setPUnit(par.getPUnit());
                    detail.setPQty(String.valueOf(par.getPQty()));
                    detail.setReceivePrice(String.valueOf(par.getReceivePrice()));
                    detail.setReceiveAmt(String.valueOf(par.getReceiveAmt()));
                    detail.setSupplyPrice(String.valueOf(par.getSupPrice()));
                    detail.setSupplyAmt(String.valueOf(par.getSupAmt()));
                    request1.getDetail().add(detail);
                }

                inReq.setRequest(request1);
                ParseJson pj = new ParseJson();
                String jsontemp = pj.beanToJson(inReq);

                DispatchService ds = DispatchService.getInstance();
                String resXml = ds.callService(jsontemp, StaticInfo.dao);
                DCP_InterSettleDataGenerateRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_InterSettleDataGenerateRes>() {
                });
                if (resserver.isSuccess() == false) {//先不报错  未对接
                    //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "生成内部结算数据失败！");
                }
            }


            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        //} catch (Exception e) {
        //    res.setSuccess(false);
        //    res.setServiceStatus("200");
        //    res.setServiceDescription("服务执行异常:" + e.getMessage());
        //}


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurReceiveCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurReceiveCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurReceiveCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_PurReceiveCreateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PurReceiveCreateReq> getRequestType() {
        return new TypeToken<DCP_PurReceiveCreateReq>() {
        };
    }

    @Override
    protected DCP_PurReceiveCreateRes getResponseType() {
        return new DCP_PurReceiveCreateRes();
    }
}
