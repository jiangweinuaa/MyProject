package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataGenerateReq;
import com.dsc.spos.json.cust.req.DCP_StockOutNoticeCreateReq;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataGenerateRes;
import com.dsc.spos.json.cust.res.DCP_StockOutNoticeRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.ninetyone.util.ColumnDataValue;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import com.dsc.spos.service.utils.ServiceAgentUtils;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  
 *
 * @author 01029
 * @date 2024-10-23
 */
public class DCP_StockOutNoticeCreate extends SPosAdvanceService<DCP_StockOutNoticeCreateReq, DCP_StockOutNoticeRes> {

    @Override
    protected void processDUID(DCP_StockOutNoticeCreateReq req, DCP_StockOutNoticeRes res) throws Exception {

        //try {
 
            processOnCreate(req, res);

        //} catch (Exception e) {
            // TODO Auto-generated catch block
         //   res.setSuccess(false);
         //   res.setServiceStatus("200");
         //   res.setServiceDescription("服务执行异常:" + e.getMessage());

        //}
    }

    private void processOnCreate(DCP_StockOutNoticeCreateReq req, DCP_StockOutNoticeRes res) throws Exception {

        String eId = req.geteId();
        String billNo = null;
        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String sql = null;
        //sql = this.isRepeat(eId, billNo);
        //List<Map<String, Object>> mDatas = this.doQueryData(sql, null);
        List<Map<String, Object>> mDatas = null;

        if(Check.NotNull(req.getRequest().getSourceBillNo())) {
            if (Check.Null(req.getRequest().getPayee())) {
                //采购订单
                String prSql = "select * from dcp_purorder a where a.eid='" + req.geteId() + "' and a.PURORDERNO='" + req.getRequest().getSourceBillNo() + "'";
                List<Map<String, Object>> list = this.doQueryData(prSql, null);
                if(CollUtil.isNotEmpty(list)){
                    req.getRequest().setPayee(list.get(0).get("PAYEE").toString());
                }
            }
            if (Check.Null(req.getRequest().getPayer())) {
                //大客订单
                String prSql = "select * from DCP_CUSTOMERPORDER a where a.eid='" + req.geteId() + "' and a.PORDERNO='" + req.getRequest().getSourceBillNo() + "'";
                List<Map<String, Object>> list = this.doQueryData(prSql, null);
                if(CollUtil.isNotEmpty(list)){
                    req.getRequest().setPayee(list.get(0).get("PAYER").toString());
                }
            }
        }
        if(Check.Null(req.getRequest().getPayee())){
            String bizSql="select * from DCP_BIZPARTNER where eid='"+eId+"' and bizpartnerno='"+req.getRequest().getObjectId()+"'  ";
            List<Map<String, Object>> bizList = this.doQueryData(bizSql, null);
            if(bizList.size()>0){
                req.getRequest().setPayee(bizList.get(0).get("PAYEE").toString());
            }
        }
        if(Check.Null(req.getRequest().getPayer())){
            String bizSql="select * from DCP_BIZPARTNER where eid='"+req.geteId()+"' and bizpartnerno='"+req.getRequest().getObjectId()+"'  ";
            List<Map<String, Object>> bizList = this.doQueryData(bizSql, null);
            if(bizList.size()>0){
                req.getRequest().setPayer(bizList.get(0).get("PAYER").toString());
            }
        }

        if(Check.Null(req.getRequest().getCorp())){
            req.getRequest().setCorp(req.getCorp());
        }

        //billtype 单据类型，枚举：1采退通知  2销货通知 3配货通知 4退配通知 5调拨通知 6移仓通知 7出库通知
        if("1".equals(req.getRequest().getBillType())){
            //● 收货法人RECEIPTCORP（单头单身都有）：出货对象类型objectType="3"(内部组织）时，取objectId所属法人；有来源单取原单，无来源取最新DCP_ORG.CORP
            //  ○ sourceType="3.要货申请"，优先取DCP_PORDER.CORP；值为null或者无源单时取出货对象objectId的所属法人
            if(Check.Null(req.getRequest().getReceiptCorp())){
                //sourceType 来源类型，枚举：1采购订单 2退货申请 3要货申请 4大客订单 5调拨申请 6领料申请 7出库申请

                //if("3".equals(req.getRequest().getObjectType())){
                //    String orgSql="select * from dcp_org a where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getRequest().getObjectId()+"'" ;
                //    List<Map<String, Object>> orgList = this.doQueryData(orgSql, null);
                //    if(orgList.size()>0){
                //        req.getRequest().setReceiptCorp(orgList.get(0).get("CORP").toString());
                //    }
                //}

                if("3".equals(req.getRequest().getSourceType())){
                    String prSql="select * from dcp_porder a where a.eid='"+req.geteId()+"' and a.porderno='"+req.getRequest().getSourceBillNo()+"' ";
                    List<Map<String, Object>> prList = this.doQueryData(prSql, null);
                    if(prList.size()>0){
                        req.getRequest().setReceiptCorp(prList.get(0).get("CORP").toString());
                    }
                }

                if(Check.Null(req.getRequest().getReceiptCorp())){
                    String orgSql="select * from dcp_org a where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getRequest().getObjectId()+"'" ;
                    List<Map<String, Object>> orgList = this.doQueryData(orgSql, null);
                    if(orgList.size()>0){
                        req.getRequest().setReceiptCorp(orgList.get(0).get("CORP").toString());
                    }
                }

            }

            if(Check.Null(req.getRequest().getDeliveryCorp())){
                if("1".equals(req.getRequest().getSourceType())){
                    String prSql="select * from dcp_purorder a where a.eid='"+req.geteId()+"' and a.purorderno='"+req.getRequest().getSourceBillNo()+"' ";
                    List<Map<String, Object>> prList = this.doQueryData(prSql, null);
                    if(prList.size()>0){
                        req.getRequest().setDeliveryCorp(prList.get(0).get("RECEIPTCORP").toString());
                    }
                }
                if("4".equals(req.getRequest().getSourceType())){
                    String prSql="select * from dcp_customerporder a where a.eid='"+req.geteId()+"' and a.porderno='"+req.getRequest().getSourceBillNo()+"' ";
                    List<Map<String, Object>> prList = this.doQueryData(prSql, null);
                    if(prList.size()>0){
                        req.getRequest().setDeliveryCorp(prList.get(0).get("DELIVERCORP").toString());
                    }
                }
                if(Check.Null(req.getRequest().getDeliveryCorp())){
                    if(Check.NotNull(req.getRequest().getDeliverOrgNo())){
                        String orgSql="select * from dcp_org a where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getRequest().getDeliverOrgNo()+"'" ;
                        List<Map<String, Object>> orgList = this.doQueryData(orgSql, null);
                        if(orgList.size()>0){
                            req.getRequest().setDeliveryCorp(orgList.get(0).get("CORP").toString());
                        }
                    }
                }
            }
            //● 发货法人DELIVERCORP：发货组织DELIVERORGNO所属法人，有来源单取原单，无来源取最新DCP_ORG.CORP
            //  ○ sourceType="1.采购订单"，优先取DCP_PURORDER.RECEIPTCORP；值为null或者无源单时取发货组织deliverOrgNo的所属法人
            //  ○ sourceType="4.大客订单"，优先取DCP_CUSTOMERPORDER.DELIVERCORP；值为null或者无源单时取发货组织deliverOrgNo的所属法人

        }


        //价格取值

        //[进货价RECEIVEPRICE]取值逻辑：优先取原单记录进货价，其次取最新供货价
        //● sourceType="1.采购订单"，跨法人采购（业务所属法人corp<>退货组织所属法人deliverCorp）优先取原单DCP_PURORDER_DETAIL.RECEIVEPRICE，
        // 其次取最新供货价：供货机构=organizationNo所属法人，收货机构=退货组织deliverOrgNo；
        //● sourceType="3.要货申请"，跨法人调拨（业务所属法人corp<>收货组织所属法人receiptCorp）
        // 优先取原单DCP_PORDER_DETAlL.DISTRIPRICE, 其次取最新供货价：供货机构=organizationNo所属法人，
        // 收货机构=objectId；
        MyCommon mc = new MyCommon();
        List<Map<String, Object>> plus = new ArrayList<Map<String, Object>>();
        for (DCP_StockOutNoticeCreateReq.Detail1 data : req.getRequest().getDataList()){
            Map<String, Object> plu = new HashMap<String, Object>();
            plu.put("PLUNO", data.getPluNo());
            plu.put("PUNIT", data.getPUnit());
            plu.put("BASEUNIT", data.getBaseUnit());
            plu.put("UNITRATIO", data.getUnitRatio());
            plu.put("SUPPLIERID", req.getOrganizationNO());
            plus.add(plu);
        }
        if("1".equals(req.getRequest().getSourceType())){

            List<Map<String, Object>> getPrice = mc.getDistriPrice(dao, req.geteId(), req.getRequest().getDeliverOrgNo(), plus);
            String purDSql="select * from DCP_PURORDER_DETAIL a where a.eid='"+req.geteId()+"' and a.purorderno='"+req.getRequest().getSourceBillNo()+"' ";
            List<Map<String, Object>> purDList = this.doQueryData(purDSql, null);

            String ptemplateSql = "select distinct a.PURTEMPLATENO,b.TAXCODE,b.pluno,c.taxrate,c.taxcaltype,c.incltax,b.PURBASEPRICE " +
                    " from DCP_PURCHASETEMPLATE a " +
                    " inner join DCP_PURCHASETEMPLATE_GOODS b on a.eid=b.eid and a.PURTEMPLATENO=b.PURTEMPLATENO " +
                    " inner join DCP_TAXCATEGORY c on c.eid=a.eid and c.taxcode=b.taxcode " +
                    " where a.eid='" + req.geteId() + "' and a.status='100' and a.SUPPLIERNO='" + req.getRequest().getObjectId() + "' " +
                    // " and a.PURTYPE='" + req.getRequest().getPurType() + "' " +
                    " ";
            List<Map<String, Object>> purTemplateList = this.doQueryData(ptemplateSql, null);

            String pluNos = req.getRequest().getDataList().stream().map(x -> x.getPluNo()).collect(Collectors.joining(",")) + ",";
            Map<String, String> mapPluNo = new HashMap<String, String>();
            mapPluNo.put("PLUNO", pluNos.toString());

            String withasSql_pluno = "";
            withasSql_pluno = mc.getFormatSourceMultiColWith(mapPluNo);
            mapPluNo = null;

            String pluSql = "with p as (" + withasSql_pluno + ") " +
                    " select a.*,b.taxrate as inputtaxrate,b.taxcaltype as inputtaxcaltype,b.incltax as inputincltax,c.UNITRATIO " +
                    " from dcp_goods a " +
                    " inner join p on a.pluno=p.pluno " +
                    " left join dcp_taxcategory b on a.eid=b.eid and a.inputtaxcode=b.taxcode " +
                    " left join dcp_goods_unit c on c.eid=a.eid and c.pluno=a.pluno and c.ounit=a.purunit " +
                    " where a.eid='" + req.geteId() + "' ";
            List<Map<String, Object>> pluList = this.doQueryData(pluSql, null);


            req.getRequest().getDataList().forEach(x->{
                List<Map<String, Object>> pdListFilter = purDList.stream().filter(y -> x.getOItem().equals(y.get("ITEM").toString())).collect(Collectors.toList());
                if(pdListFilter.size()>0) {
                    if (Check.Null(x.getReceivePrice())) {
                        x.setReceivePrice(pdListFilter.get(0).get("RECEIVEPRICE").toString());
                    }
                    if (Check.Null(x.getSupPrice())) {
                        x.setSupPrice(pdListFilter.get(0).get("SUPPRICE").toString());
                    }

                    BigDecimal purPrice = new BigDecimal(pdListFilter.get(0).get("PURPRICE").toString());
                    BigDecimal purUnitRatio = new BigDecimal(pdListFilter.get(0).get("UNITRATIO").toString());
                    BigDecimal divide = purPrice.divide(purUnitRatio, 0, RoundingMode.HALF_UP);
                    x.setRefPurPrice( divide.toString());//：基准单位单价不做小数位数保留处理，以免出现价格尾差
//sourceBillNo非空：关联取采购订单DCP_PURORDER.DETAIL的PURPRICE，换算为baseUnit对应价格
                }


                if(Check.Null(x.getReceivePrice())){
                    Map<String, Object> condiV = new HashMap<String, Object>();
                    condiV.put("PLUNO", x.getPluNo());
                    condiV.put("PUNIT", x.getPUnit());
                    List<Map<String, Object>> priceList = MapDistinct.getWhereMap(getPrice, condiV, false);
                    if (priceList != null && priceList.size() > 0) {
                        String distriPrice = priceList.get(0).get("DISTRIPRICE").toString();
                        x.setRetailPrice(distriPrice);
                    }
                }

                if(Check.Null(x.getRefPurPrice())){
                    List<Map<String, Object>> templateFilter = purTemplateList.stream().filter(y -> y.get("PURTEMPLATENO").toString().equals(x.getTemplateNo())).collect(Collectors.toList());
                    if(templateFilter.size()>0){
                        x.setRefPurPrice(templateFilter.get(0).get("PURBASEPRICE").toString());
                    }
                }
                if(Check.Null(x.getReceivePrice())){
                    List<Map<String, Object>> pluFilter = pluList.stream().filter(y -> y.get("PLUNO").toString().equals(x.getPluNo())).collect(Collectors.toList());
                    if(pluFilter.size()>0){

                        BigDecimal pluPurPrice = new BigDecimal(pluFilter.get(0).get("PURPRICE").toString());
                        BigDecimal pluPurUnitRatio = new BigDecimal(pluFilter.get(0).get("UNITRATIO").toString());
                        BigDecimal divide = pluPurPrice.divide(pluPurUnitRatio, 0, RoundingMode.HALF_UP);

                        x.setReceivePrice(divide.toString());
                    }
                }
            });

        }
        else if("3".equals(req.getRequest().getSourceType())){
            List<Map<String, Object>> getPrice = mc.getDistriPrice(dao, req.geteId(), req.getRequest().getObjectId(), plus);
            String pOrderSql="select * from DCP_PORDER_DETAlL a where a.eid='"+req.geteId()+"' and a.porderno='"+req.getRequest().getSourceBillNo()+"' ";
            List<Map<String, Object>> poDList = this.doQueryData(pOrderSql, null);

            req.getRequest().getDataList().forEach(x->{
                if(Check.Null(x.getReceivePrice())){
                    List<Map<String, Object>> pdListFilter = poDList.stream().filter(y -> x.getOItem().equals(y.get("ITEM").toString())).collect(Collectors.toList());
                    if(pdListFilter.size()>0){
                        x.setReceivePrice(pdListFilter.get(0).get("DISTRIPRICE").toString());
                    }
                }
                if(Check.Null(x.getReceivePrice())){
                    Map<String, Object> condiV = new HashMap<String, Object>();
                    condiV.put("PLUNO", x.getPluNo());
                    condiV.put("PUNIT", x.getPUnit());
                    List<Map<String, Object>> priceList = MapDistinct.getWhereMap(getPrice, condiV, false);
                    if (priceList != null && priceList.size() > 0) {
                        String distriPrice = priceList.get(0).get("DISTRIPRICE").toString();
                        x.setRetailPrice(distriPrice);
                    }
                }
            });

        }


        if (SUtil.EmptyList(mDatas)) {
            DCP_StockOutNoticeCreateReq.levelRequest request = req.getRequest();
            String billType = request.getBillType();
            ColumnDataValue columns = new ColumnDataValue();
            String[] columns1 = null;  
            DataValue[] insValue1 = null;
            if("1".equals(billType)) {
                billNo = getProcessTaskNO(eId, "001", req.getOrganizationNO() + "-CTTZ");
            }else if("2".equals(billType)){
                billNo=this.getOrderNO(req,"XHTZ");
            }else if("3".equals(billType)){ 
                billNo=this.getOrderNO(req,"PHTZ");
            }
            columns.Columns.clear();
            columns.DataValues.clear();
            columns.Add("EID", eId, Types.VARCHAR);
            columns.Add("ORGANIZATIONNO", req.getOrganizationNO(), Types.VARCHAR);
            columns.Add("BILLNO", billNo, Types.VARCHAR);
            columns.Add("BDATE", req.getRequest().getBDate().replace("-", ""), Types.VARCHAR);
            columns.Add("EMPLOYEEID", req.getRequest().getEmployeeID(), Types.VARCHAR);
            columns.Add("DEPARTID", req.getRequest().getDepartID(), Types.VARCHAR);
            columns.Add("BILLTYPE", req.getRequest().getBillType(), Types.VARCHAR);
            columns.Add("OBJECTTYPE", req.getRequest().getObjectType(), Types.VARCHAR);
            columns.Add("OBJECTID", req.getRequest().getObjectId(), Types.VARCHAR);
            columns.Add("PAYTYPE", req.getRequest().getPayType(), Types.VARCHAR);
            columns.Add("PAYORGNO", req.getRequest().getPayOrgNo(), Types.VARCHAR);
            columns.Add("BILLDATENO", req.getRequest().getBillDateNo(), Types.VARCHAR);
            columns.Add("PAYDATENO", req.getRequest().getPayDateNo(), Types.VARCHAR);
            columns.Add("INVOICECODE", req.getRequest().getInvoiceCode(), Types.VARCHAR);
            columns.Add("CURRENCY", req.getRequest().getCurrency(), Types.VARCHAR);
            columns.Add("DELIVERORGNO", req.getRequest().getDeliverOrgNo(), Types.VARCHAR);
            columns.Add("WAREHOUSE", req.getRequest().getWareHouse(), Types.VARCHAR);
            columns.Add("RETURNTYPE", req.getRequest().getReturnType(), Types.VARCHAR);
            columns.Add("RDATE", req.getRequest().getRDate(), Types.VARCHAR);
            columns.Add("SOURCETYPE", req.getRequest().getSourceType(), Types.VARCHAR);
            columns.Add("SOURCEBILLNO", req.getRequest().getSourceBillNo(), Types.VARCHAR);
            columns.Add("MEMO", req.getRequest().getMemo(), Types.VARCHAR);
            columns.Add("DELIVERYADDRESS", req.getRequest().getDeliveryAddress(), Types.VARCHAR);
            columns.Add("DELIVERYDATE", req.getRequest().getDeliveryDate(), Types.VARCHAR);
            columns.Add("RECEIPTWAREHOUSE", req.getRequest().getReceiptWarehouse(), Types.VARCHAR);
            columns.Add("INVWAREHOUSE", req.getRequest().getInvWarehouse(), Types.VARCHAR);
            columns.Add("ISTRANINCONFIRM", req.getRequest().getIsTranInConfirm(), Types.VARCHAR);
            columns.Add("TOTCQTY", req.getRequest().getTotCqty(), Types.VARCHAR);
            columns.Add("TOTPQTY", req.getRequest().getTotPqty(), Types.VARCHAR);
            columns.Add("TOTAMT", req.getRequest().getTotAmt(), Types.VARCHAR);
            columns.Add("TOTRETAILAMT", req.getRequest().getTotRetailAmt(), Types.VARCHAR);
            columns.Add("TOTPRETAXAMT", req.getRequest().getTotPreTaxAmt(), Types.VARCHAR);
            columns.Add("TOTTAXAMT", req.getRequest().getTotTaxAmt(), Types.VARCHAR);

            columns.Add("OWNOPID", req.getOpNO(), Types.VARCHAR);
            columns.Add("OWNDEPTID", req.getDepartmentNo(), Types.VARCHAR);

            columns.Add("CREATEOPID", req.getOpNO(), Types.VARCHAR);
            columns.Add("CREATEDEPTID", req.getDepartmentNo(), Types.VARCHAR);
            columns.Add("CREATETIME", lastmoditime, Types.DATE);
            columns.Add("STATUS", "0", Types.VARCHAR);
            columns.Add("TEMPLATENO", req.getRequest().getTemplateNo(), Types.VARCHAR);
            columns.Add("PAYEE", req.getRequest().getPayee(), Types.VARCHAR);
            columns.Add("PAYER", req.getRequest().getPayer(), Types.VARCHAR);

            columns.Add("CORP", req.getRequest().getCorp(), Types.VARCHAR);
            columns.Add("RECEIPTCORP", req.getRequest().getReceiptCorp(), Types.VARCHAR);
            columns.Add("DELIVERYCORP", req.getRequest().getDeliveryCorp(), Types.VARCHAR);

            columns1 = columns.Columns.toArray(new String[0]);
            insValue1 = columns.DataValues.toArray(new DataValue[0]);
            InsBean ib1 = new InsBean("DCP_STOCKOUTNOTICE", columns1);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1)); // 新增

            // detail
            List<DCP_StockOutNoticeCreateReq.Detail1> detailLists = req.getRequest().getDataList();

            if (!SUtil.EmptyList(detailLists)) {
                for (DCP_StockOutNoticeCreateReq.Detail1 par : detailLists) {
                    if(Check.Null(par.getReceivePrice())){
                        par.setReceivePrice("0");
                    }
                    if(Check.Null(par.getReceiveAmt())){
                        par.setReceiveAmt("0");
                    }

                    columns.Columns.clear();
                    columns.DataValues.clear();
                    columns.Add("EID", eId, Types.VARCHAR);
                    columns.Add("ORGANIZATIONNO", req.getOrganizationNO(), Types.VARCHAR);
                    columns.Add("BILLNO", billNo, Types.VARCHAR);
                    columns.Add("ITEM", par.getItem(), Types.VARCHAR);
                    columns.Add("PLUNO", par.getPluNo(), Types.VARCHAR);
                    columns.Add("FEATURENO", par.getFeatureNo(), Types.VARCHAR);
                    columns.Add("PLUBARCODE", par.getPluBarcode(), Types.VARCHAR);
                    columns.Add("PRICE", par.getPrice(), Types.VARCHAR);
                    columns.Add("PUNIT", par.getPUnit(), Types.VARCHAR);
                    columns.Add("PQTY", par.getPQty(), Types.VARCHAR);
                    columns.Add("AMOUNT", par.getAmount(), Types.VARCHAR);
                    columns.Add("BSNO", par.getBsNo(), Types.VARCHAR);
                    columns.Add("SOURCETYPE", par.getSourceType(), Types.VARCHAR);
                    columns.Add("SOURCEBILLNO", par.getSourceBillNo(), Types.VARCHAR);
                    columns.Add("OITEM", par.getOItem(), Types.VARCHAR);
                    columns.Add("TEMPLATENO", par.getTemplateNo(), Types.VARCHAR);
                    columns.Add("TAXCODE", par.getTaxCode(), Types.VARCHAR);
                    columns.Add("TAXRATE", par.getTaxRate(), Types.VARCHAR);
                    columns.Add("INCLTAX", par.getInclTax(), Types.VARCHAR);
                    columns.Add("MEMO", par.getMemo(), Types.VARCHAR);
                    columns.Add("RETAILPRICE", par.getRetailPrice(), Types.VARCHAR);
                    columns.Add("RETAILAMT", par.getRetailAmt(), Types.VARCHAR);
                    columns.Add("BASEUNIT", par.getBaseUnit(), Types.VARCHAR);
                    columns.Add("BASEQTY", par.getBaseQty(), Types.VARCHAR);
                    columns.Add("UNITRATIO", par.getUnitRatio(), Types.VARCHAR);
                    columns.Add("PRETAXAMT", par.getPreTaxAmt(), Types.VARCHAR);
                    columns.Add("TAXAMT", par.getTaxAmt(), Types.VARCHAR); 
                    columns.Add("WAREHOUSE", req.getRequest().getWareHouse(), Types.VARCHAR);
                    columns.Add("TAXCALTYPE", par.getTaxCalType(), Types.VARCHAR);
                    columns.Add("OBJECTTYPE", par.getObjectType(), Types.VARCHAR);
                    columns.Add("OBJECTID", par.getObjectId(), Types.VARCHAR);
                    columns.Add("ISGIFT", par.getIsGift(), Types.VARCHAR);
                    columns.Add("NOQTY", par.getNoQty(), Types.VARCHAR);
                    columns.Add("POQTY", par.getPoQty(), Types.VARCHAR);
                    columns.Add("CATEGORY", par.getCategory(), Types.VARCHAR);
                    //columns.Add("RETAILAMT", par.getReceiveAmt(), Types.VARCHAR);

                    //receiveAmt  supAmt 给值
                    if(Check.NotNull(par.getReceivePrice())){
                       par.setReceiveAmt( new BigDecimal(par.getReceivePrice()).multiply(new BigDecimal(par.getPQty())).toString());
                    }
                    if(Check.NotNull(par.getSupPrice())){
                        par.setSupAmt( new BigDecimal(par.getSupPrice()).multiply(new BigDecimal(par.getPQty())).toString());
                    }

                    columns.Add("RECEIVEPRICE", par.getReceivePrice(), Types.VARCHAR);
                    columns.Add("RECEIVEAMT", par.getReceiveAmt(), Types.VARCHAR);
                    columns.Add("REFPURPRICE", par.getRefPurPrice(), Types.VARCHAR);

                    columns.Add("SUPPRICE", par.getSupPrice(), Types.VARCHAR);
                    columns.Add("SUPAMT", par.getSupAmt(), Types.VARCHAR);

                    columns1 = columns.Columns.toArray(new String[0]);
                    insValue1 = columns.DataValues.toArray(new DataValue[0]);
                    //  detail
                    InsBean ib2 = new InsBean("DCP_STOCKOUTNOTICE_DETAIL", columns1);
                    ib2.addValues(insValue1);
                    this.addProcessData(new DataProcessBean(ib2));
                }
            }


            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setBillNo(billNo);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

            Map<String, String> corpData = PosPub.getCorpByOrgNo(req.getOrganizationNO(),req.getRequest().getObjectId(), req.getRequest().getDeliverOrgNo());

            //采退通知
            if("1".equals(req.getRequest().getBillType())) {
                if (!StringUtils.equals(corpData.get(req.getOrganizationNO()),
                        corpData.get(req.getRequest().getDeliverOrgNo()))) {
                    DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                    inReq.setServiceId("DCP_InterSettleDataGenerate");
                    inReq.setToken(req.getToken());
                    DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                    request1.setOrganizationNo(req.getOrganizationNO());
                    request1.setBillNo(billNo);
                    request1.setSupplyOrgNo(req.getOrganizationNO());
                    request1.setReturnSupplyPrice("Y");
                    request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType_11001.getType());
                    request1.setDetail(new ArrayList<>());
                    for (DCP_StockOutNoticeCreateReq.Detail1 par : req.getRequest().getDataList()) {
                        DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                        detail.setReceiveOrgNo(req.getRequest().getDeliverOrgNo());
                        detail.setSourceBillNo(par.getSourceBillNo());
                        detail.setSourceItem(par.getOItem());
                        detail.setItem(String.valueOf(par.getItem()));
                        detail.setPluNo(par.getPluNo());
                        detail.setFeatureNo(par.getFeatureNo());
                        detail.setPUnit(par.getPUnit());
                        detail.setPQty(String.valueOf(par.getPQty()));
                        detail.setReceivePrice(par.getReceivePrice());
                        detail.setReceiveAmt(par.getReceiveAmt());
                        detail.setSupplyPrice("");
                        detail.setSupplyAmt("");
                        request1.getDetail().add(detail);
                    }
                    inReq.setRequest(request1);
                    ParseJson pj = new ParseJson();
                    String jsontemp = pj.beanToJson(inReq);

                    DispatchService ds = DispatchService.getInstance();
                    String resXml = ds.callService(jsontemp, StaticInfo.dao);
                    DCP_InterSettleDataGenerateRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_InterSettleDataGenerateRes>() {
                    });
                    if (resserver.isSuccess() == false) {
                        res.setSuccess(true);
                        res.setServiceStatus("000");
                        res.setServiceDescription("内部结算失败！");
                        return;
                        //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "内部结算失败！");
                    }
                }
            }

            //销货通知
            if("2".equals(req.getRequest().getBillType())) {

                if (!StringUtils.equals(corpData.get(req.getOrganizationNO()),
                        corpData.get(req.getRequest().getDeliverOrgNo()))) {
                    DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                    inReq.setServiceId("DCP_InterSettleDataGenerate");
                    inReq.setToken(req.getToken());
                    DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                    request1.setOrganizationNo(req.getOrganizationNO());
                    request1.setBillNo(billNo);
                    request1.setSupplyOrgNo(req.getRequest().getDeliverOrgNo());
                    request1.setReturnSupplyPrice("Y");
                    request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType12001.getType());
                    request1.setDetail(new ArrayList<>());
                    for (DCP_StockOutNoticeCreateReq.Detail1 par : req.getRequest().getDataList()) {
                        DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                        detail.setReceiveOrgNo(req.getOrganizationNO());
                        detail.setSourceBillNo(par.getSourceBillNo());
                        detail.setSourceItem(par.getOItem());
                        detail.setItem(String.valueOf(par.getItem()));
                        detail.setPluNo(par.getPluNo());
                        detail.setFeatureNo(par.getFeatureNo());
                        detail.setPUnit(par.getPUnit());
                        detail.setPQty(String.valueOf(par.getPQty()));
                        detail.setReceivePrice(par.getReceivePrice());
                        detail.setReceiveAmt(par.getReceiveAmt());
                        detail.setSupplyPrice("");
                        detail.setSupplyAmt("");
                        request1.getDetail().add(detail);
                    }
                    inReq.setRequest(request1);
                    ParseJson pj = new ParseJson();
                    String jsontemp = pj.beanToJson(inReq);

                    DispatchService ds = DispatchService.getInstance();
                    String resXml = ds.callService(jsontemp, StaticInfo.dao);
                    DCP_InterSettleDataGenerateRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_InterSettleDataGenerateRes>() {
                    });
                    if (resserver.isSuccess() == false) {
                        res.setSuccess(true);
                        res.setServiceStatus("000");
                        res.setServiceDescription("内部结算失败！");
                        return;
                        //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "内部结算失败！");
                    }
                }
            }


            if("3".equals(req.getRequest().getBillType())){
                if (!StringUtils.equals(corpData.get(req.getRequest().getObjectId()),
                        corpData.get(req.getRequest().getDeliverOrgNo()))) {
                    DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                    inReq.setServiceId("DCP_InterSettleDataGenerate");
                    inReq.setToken(req.getToken());
                    DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                    request1.setOrganizationNo(req.getOrganizationNO());
                    request1.setBillNo(billNo);
                    request1.setSupplyOrgNo(req.getRequest().getDeliverOrgNo());
                    request1.setReturnSupplyPrice("Y");
                    request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType10002.getType());
                    request1.setDetail(new ArrayList<>());
                    for (DCP_StockOutNoticeCreateReq.Detail1 par : req.getRequest().getDataList()) {
                        DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                        detail.setReceiveOrgNo(par.getObjectId());
                        detail.setSourceBillNo(par.getSourceBillNo());
                        detail.setSourceItem(par.getOItem());
                        detail.setItem(String.valueOf(par.getItem()));
                        detail.setPluNo(par.getPluNo());
                        detail.setFeatureNo(par.getFeatureNo());
                        detail.setPUnit(par.getPUnit());
                        detail.setPQty(String.valueOf(par.getPQty()));
                        detail.setReceivePrice(par.getPrice());
                        detail.setReceiveAmt(par.getAmount());
                        detail.setSupplyPrice("");
                        detail.setSupplyAmt("");
                        request1.getDetail().add(detail);
                    }
                    inReq.setRequest(request1);
                    ParseJson pj = new ParseJson();
                    String jsontemp = pj.beanToJson(inReq);

                    DispatchService ds = DispatchService.getInstance();
                    String resXml = ds.callService(jsontemp, StaticInfo.dao);
                    DCP_InterSettleDataGenerateRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_InterSettleDataGenerateRes>() {
                    });
                    if (resserver.isSuccess() == false) {
                        res.setSuccess(true);
                        res.setServiceStatus("000");
                        res.setServiceDescription("内部结算失败！");
                        return;
                        //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "内部结算失败！");
                    }
                }
            }


        }
        else {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败 ");
            return;
        }
    }


    @Override
    protected List<InsBean> prepareInsertData(DCP_StockOutNoticeCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockOutNoticeCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockOutNoticeCreateReq req) throws Exception {
        return null;
    }


    @Override
    protected boolean isVerifyFail(DCP_StockOutNoticeCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        String sql = " SELECT * FROM DCP_SUPLICENSECHANGE_DETAIL WHERE SUPPLIER='%s'   AND LICENSETYPE='%s' AND LICENSENO='%s'";

        // 必传值不为空
        
        List<DCP_StockOutNoticeCreateReq.Detail1> detailLists = req.getRequest().getDataList();
        if (detailLists == null || detailLists.size() <= 0) {
            errMsg.append("单据明细缺失！ ");
            isFail = true;
        }
         
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_StockOutNoticeCreateReq> getRequestType() {
        return new TypeToken<DCP_StockOutNoticeCreateReq>() {
        };
    }

    @Override
    protected DCP_StockOutNoticeRes getResponseType() {
        return new DCP_StockOutNoticeRes();
    }


    /**
     * 判断 信息时候已存在或重复
     */
    private String isRepeat(String... key) {
        String sql = null;
        sql = " SELECT * FROM DCP_BIZPARTNER WHERE EID='%s'   AND BIZPARTNERNO='%s' ";
        sql = String.format(sql, key);
        return sql;
    }

}
	
