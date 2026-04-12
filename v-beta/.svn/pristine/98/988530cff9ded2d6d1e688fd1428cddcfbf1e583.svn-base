package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataGenerateReq;
import com.dsc.spos.json.cust.req.DCP_PurOrderCreateReq;
import com.dsc.spos.json.cust.req.DCP_PurOrderUpdateReq;
import com.dsc.spos.json.cust.req.DCP_SStockInCreateReq;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataGenerateRes;
import com.dsc.spos.json.cust.res.DCP_PurOrderCreateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_PurOrderCreate extends SPosAdvanceService<DCP_PurOrderCreateReq, DCP_PurOrderCreateRes> {
    Logger logger = LogManager.getLogger(DCP_ReceivingCreate.class);
    @Override
    protected boolean isVerifyFail(DCP_PurOrderCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        DCP_PurOrderCreateReq.levelElm request = req.getRequest();
        List<DCP_PurOrderCreateReq.GoodsList> goodsList = request.getGoodsList();
        List<DCP_PurOrderCreateReq.PayList> payList = request.getPayList();
        List<DCP_PurOrderCreateReq.SourceBillList> sourceBillList = request.getSourceBillList();
        if (Check.Null(request.getPurOrgNo())){
            errMsg.append("purOrgNo不可为空值, ");
            isFail = true;
        }

        if (Check.Null(request.getBdate())){
            errMsg.append("bdate不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getSupplier())){
            errMsg.append("supplier不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getPayType())){
            errMsg.append("payType不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getPayOrgNo())){
            errMsg.append("payOrgNo不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getBilldateNo())){
            errMsg.append("billdateNo不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getPaydateNo())){
            errMsg.append("paydateNo不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getInvoiceCode())){
            errMsg.append("invoiceCode不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getPurOrgNo())){
            errMsg.append("purOrgNo不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getCurrency())){
            errMsg.append("currency不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getPurEmpNo())){
            errMsg.append("purEmpNo不可为空值, ");
            isFail = true;
        }

        if (Check.Null(request.getPurDeptNo())){
            errMsg.append("purDeptNo不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getPurType())){
            errMsg.append("purType不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getSourceType())){
            errMsg.append("sourceType不可为空值, ");
            isFail = true;
        }else {
            if("1".equals(request.getSourceType())||"3".equals(request.getSourceType())){
                if(sourceBillList==null||sourceBillList.size()<=0){
                    errMsg.append("sourceType为要货申请、大客订单时，sourceBillList不可为空, ");
                    isFail = true;
                }
            }
        }
        //if (Check.Null(request.getSourceBillNo())){
            //errMsg.append("sourceBillNo不可为空值, ");
            //isFail = true;
        //}
        if (Check.Null(request.getReceiptOrgNo())){
            errMsg.append("receiptOrgNo不可为空值, ");
            isFail = true;
        }


        //if (Check.Null(request.getDistriCenter())){
          //  errMsg.append("distriCenter不可为空值, ");
           // isFail = true;
        //}
        if (Check.Null(request.getAddress())){
            errMsg.append("address不可为空值, ");
            isFail = true;
        }
        //if (Check.Null(request.getTelephone())){
         //   errMsg.append("telephone不可为空值, ");
          //  isFail = true;
        //}

       // if (Check.Null(request.getContact())){
         //   errMsg.append("contact不可为空值, ");
         //   isFail = true;
        //}
        if (Check.Null(request.getExpireDate())){
            errMsg.append("expireDate不可为空值, ");
            isFail = true;
        }
        //if (Check.Null(request.getIs_prePay())){
         //   errMsg.append("is_prePay不可为空值, ");
         //   isFail = true;
        //}


        //if (Check.Null(request.getMemo())){
         //   errMsg.append("memo不可为空值, ");
         //   isFail = true;
        //}

        if(goodsList==null||goodsList.size()==0){
            errMsg.append("商品信息不可为空, ");
            isFail = true;
        }else{
            for (DCP_PurOrderCreateReq.GoodsList par : goodsList) {
                if (Check.Null(par.getItem())){
                    errMsg.append("item不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getPluNo())){
                    errMsg.append("pluNo不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getFeatureNo())){
                    par.setFeatureNo(" ");
                    //errMsg.append("featureNo不可为空值, ");
                    //isFail = true;
                }
                if (Check.Null(par.getBarcode())){
                    errMsg.append("barcode不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getCategory())){
                    errMsg.append("category不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getTaxCode())){
                    errMsg.append("taxCode不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getTaxRate())){
                    errMsg.append("taxRate不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getIncltax())){
                    errMsg.append("incltax不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getPurPrice())){
                    errMsg.append("purPrice不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getPurUnit())){
                    errMsg.append("purUnit不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getPurQty())){
                    errMsg.append("purQty不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getPurAmt())){
                    errMsg.append("purAmt不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getPreTaxAmt())){
                    errMsg.append("preTaxAmt不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getTaxAmt())){
                    errMsg.append("taxAmt不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getArrivalDate())){
                    errMsg.append("arrivalDate不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getMultiDate())){
                    errMsg.append("multiDate不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getIs_qualityCheck())){
                    errMsg.append("is_qualityCheck不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getIs_qualityCheck())){
                    errMsg.append("is_qualityCheck不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getBaseUnit())){
                    errMsg.append("baseUnit不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getUnitRatio())){
                    errMsg.append("unitRatio不可为空值, ");
                    isFail = true;
                }

                List<DCP_PurOrderCreateReq.MultiDatedetail> multiDatedetail = par.getMultiDateDetail();
                if(multiDatedetail!=null&&multiDatedetail.size()>0){
                    for (DCP_PurOrderCreateReq.MultiDatedetail par2 : multiDatedetail) {
                        if (Check.Null(par2.getArrivalDate())) {
                            errMsg.append("arrivalDate不可为空值, ");
                            isFail = true;
                        }
                        if (Check.Null(par2.getPurQty())) {
                            errMsg.append("purQty不可为空值, ");
                            isFail = true;
                        }


                    }
                }

            }
        }

        if(payList==null||payList.size()==0) {
        }else{
            for (DCP_PurOrderCreateReq.PayList par : payList) {
                if (Check.Null(par.getItem())) {
                    errMsg.append("item不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getAmount())) {
                    errMsg.append("amount不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getPayType())) {
                    errMsg.append("payType不可为空值, ");
                    isFail = true;
                }

                if (isFail) {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }
            }
        }

        if(sourceBillList!=null&&sourceBillList.size()>0){
            for (DCP_PurOrderCreateReq.SourceBillList par : sourceBillList) {

                if (Check.Null(par.getItem())){
                    errMsg.append("item不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getSourceBillNo())){
                    errMsg.append("sourceBillNo不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getOItem())){
                    errMsg.append("oItem不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getPluNo())){
                    errMsg.append("pluNo不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getFeatureNo())){
                    errMsg.append("featureNo不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getOUnit())){
                    errMsg.append("oUnit不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getOQty())){
                    errMsg.append("oQty不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getPurUnit())){
                    errMsg.append("purUnit不可为空值, ");
                    isFail = true;
                }
                //if (Check.Null(par.getConvertPurQty())){
                //    errMsg.append("convertPurQty不可为空值, ");
                 //   isFail = true;
                //}
                if (Check.Null(par.getPurQty())){
                    errMsg.append("purQty不可为空值, ");
                    isFail = true;
                }
            }
        }


        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_PurOrderCreateReq> getRequestType() {
        return new TypeToken<DCP_PurOrderCreateReq>(){};
    }

    @Override
    protected DCP_PurOrderCreateRes getResponseType() {
        return new DCP_PurOrderCreateRes();
    }

    @Override
    public void processDUID(DCP_PurOrderCreateReq req,DCP_PurOrderCreateRes res) throws Exception {

        //DCP_PURORDER
        //DCP_PURORDER_DETAIL
        //DCP_PURORDER_DELIVERY
        //DCP_PURORDER_PAY
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        String purOrderNo = this.getPurOrderNO(req);
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        BigDecimal totcQty=new BigDecimal(0);
        BigDecimal totpQty=new BigDecimal(0);
        BigDecimal totPurAmt=new BigDecimal(0);
        BigDecimal totPreTaxAmt=new BigDecimal(0);
        BigDecimal totTaxAmt=new BigDecimal(0);

        DCP_PurOrderCreateReq.levelElm request = req.getRequest();
        List<DCP_PurOrderCreateReq.PayList> payList = request.getPayList();
        List<DCP_PurOrderCreateReq.GoodsList> goodsList = request.getGoodsList();
        List<DCP_PurOrderCreateReq.SourceBillList> sourceBillList = request.getSourceBillList();
        int deliveryItem=0;

        List pluNoSingles=new ArrayList();
        StringBuffer sJoinPluNo = new StringBuffer();
        for (DCP_PurOrderCreateReq.GoodsList par : goodsList){
            if(!pluNoSingles.contains(par.getPluNo())){
                pluNoSingles.add(par.getPluNo());
                sJoinPluNo.append(par.getPluNo() + ",");
            }

        }
        Map<String, String> map = new HashMap<>();
        map.put("PLUNO", sJoinPluNo.toString());

        MyCommon cm = new MyCommon();
        String withPlu = cm.getFormatSourceMultiColWith(map);
        String stockSql = " select a.pluno,a.featureno,sum(nvl(a.qty,0)-nvl(a.lockqty,0)-nvl(a.onlineqty,0)) as baseqty from dcp_stock a"
                + " inner join ("+withPlu+") b on a.pluno=b.pluno "
              //  + " inner join dcp_warehouse c on a.eid=c.eid and a.organizationno=c.organizationno and a.warehouse=c.warehouse and c.warehouse_type<>'3'"
                + " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"'  "
                + " group by a.pluno,a.featureno";
        List<Map<String, Object>> getStock = this.doQueryData(stockSql, null);

        String deliverySql="select a.pluno,a.featureno,a.purunit ,sum(a.nonArrivalQty)  as nonArrivalQty from ( select c.pluno,c.featureno,c.purunit,(nvl(c.purqty,0)-nvl(c.RECEIVEQTY,0)) nonArrivalQty from dcp_purorder a " +
                " left join  dcp_purorder_delivery c on a.eid=c.eid and a.organizationno=c.organizationno " +
                " and a.purorderno=c.purorderno "+
                " inner join ("+withPlu+")p on c.pluno=p.pluno "+
                " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"'" +
                " and a.status!='0' and a.status!='3' and a.RECEIPTORGNO='"+req.getRequest().getReceiptOrgNo()+"' ) a " +
                " group by a.pluno,a.featureno,a.purunit ";
        List<Map<String, Object>> getPurOrderDelivery = this.doQueryData(deliverySql, null);

        //获取税别
        String taxSql="select * from DCP_TAXCATEGORY a where a.eid='"+req.geteId()+"' ";
        List<Map<String, Object>> getTax = this.doQueryData(taxSql, null);

        if(Check.Null(request.getPayee())){
            String bizSql="select * from DCP_BIZPARTNER where eid='"+eId+"' and bizpartnerno='"+req.getRequest().getSupplier()+"'  ";
            List<Map<String, Object>> bizList = this.doQueryData(bizSql, null);
            if(bizList.size()>0){
                request.setPayee(bizList.get(0).get("PAYEE").toString());
            }
        }

        if(Check.Null(request.getCorp())){
            //String orgSql1="select * from dcp_org where eid='"+req.geteId()+"' and organizationno='"+req.getOrganizationNO()+"' ";
            //List<Map<String, Object>> orgList1 = this.doQueryData(orgSql1, null);
            //if(CollUtil.isNotEmpty(orgList1)){
             //   request.setCorp( orgList1.get(0).get("CORP").toString());
            //}
            request.setCorp(req.getCorp());
        }
        if(Check.Null(request.getReceiptCorp())){
            String orgSql1="select * from dcp_org where eid='"+req.geteId()+"' and organizationno='"+request.getReceiptOrgNo()+"' ";
            List<Map<String, Object>> orgList1 = this.doQueryData(orgSql1, null);
            if(CollUtil.isNotEmpty(orgList1)){
                request.setReceiptCorp( orgList1.get(0).get("CORP").toString());
            }
        }

        request.setTaxPayerType(req.getTaxPayerType());
        request.setInputTaxCode(req.getInputTaxCode());
        request.setInputTaxRate(req.getInputTaxRate());
        //2 小规模纳税人 进项税率为0
        if(("2").equals(request.getTaxPayerType())){
            if(new BigDecimal(request.getInputTaxRate()).compareTo(BigDecimal.ZERO)!=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "当前组织所属法人为「小规模纳税人」，进项税率需设置为0%，请检查【组织信息】设置".toString());
            }
        }



        for (DCP_PurOrderCreateReq.GoodsList par : goodsList){

            if(Check.Null(par.getPurPrice())){
                par.setPurPrice("0");
            }
            if(Check.Null(par.getTaxRate())){
                par.setTaxRate("0");
            }

            //receivePrice 后面会加上
            if(Check.Null(par.getReceivePrice())){
                par.setReceivePrice("0");
            }
            if(Check.Null(par.getReceiveAmt())){
                par.setReceiveAmt("0");
            }
            if(Check.Null(par.getSupPrice())){
                par.setSupPrice("0");
            }
            if(Check.Null(par.getSupAmt())){
                par.setSupAmt("0");
            }

            if(("2").equals(request.getTaxPayerType())){
                //取法人的

                if(Check.NotNull(request.getInputTaxCode())) {
                    par.setTaxCode(request.getInputTaxCode());
                    List<Map<String, Object>> taxcodes = getTax.stream().filter(x -> x.get("TAXCODE").toString().equals(par.getTaxCode())).collect(Collectors.toList());
                    if(taxcodes.size()>0){
                        par.setTaxRate(taxcodes.get(0).get("TAXRATE").toString());
                        par.setTaxCalType(taxcodes.get(0).get("TAXCALTYPE").toString());
                        par.setIncltax(taxcodes.get(0).get("INCLTAX").toString());
                    }

                }
            }


            BigDecimal purPrice = new BigDecimal(par.getPurPrice());
            BigDecimal taxRate = new BigDecimal(par.getTaxRate());
            BigDecimal unitRatio = new BigDecimal(par.getUnitRatio());
            BigDecimal purQtySum = new BigDecimal("0");
            BigDecimal purAmtSum = new BigDecimal("0");
            BigDecimal purTaxAmtSum = new BigDecimal("0");
            BigDecimal purPreTaxAmtSum = new BigDecimal("0");
            //判断是否多交期
            String multiDate = par.getMultiDate();
            List<Map<String, Object>> taxcodes = getTax.stream().filter(x -> x.get("TAXCODE").toString().equals(par.getTaxCode())).collect(Collectors.toList());
            String taxCalType="1";//一般
            if(CollUtil.isNotEmpty(taxcodes)){
                taxCalType=taxcodes.get(0).get("TAXCALTYPE").toString();
            }

            if(multiDate!=null&&multiDate.equals("Y")){
                List<DCP_PurOrderCreateReq.MultiDatedetail> multiDatedetail = par.getMultiDateDetail();
                multiDatedetail=multiDatedetail.stream().sorted(  Comparator.comparing(p->p.getArrivalDate())).collect(Collectors.toList());
                if(multiDatedetail!=null&&multiDatedetail.size()>0){

                    for (DCP_PurOrderCreateReq.MultiDatedetail par2 : multiDatedetail) {
                        deliveryItem++;
                        BigDecimal purQty = new BigDecimal(par2.getPurQty());
                        BigDecimal purAmt = purQty.multiply(purPrice);
                        BigDecimal purTaxAmt= new BigDecimal(0);
                        BigDecimal purPreTaxAmt=  new BigDecimal(0);
                        if(taxCalType.equals("1")){
                            BigDecimal augend = taxRate.divide(new BigDecimal("100"),6, RoundingMode.HALF_UP).add(new BigDecimal("1"));
                            purPreTaxAmt=purAmt.divide(augend,2, RoundingMode.HALF_UP);
                            purTaxAmt= purAmt.subtract(purPreTaxAmt);
                        }else{
                             purTaxAmt= purAmt.multiply(taxRate).divide(new BigDecimal(100),2, RoundingMode.HALF_UP);
                             purPreTaxAmt= purAmt.subtract(purTaxAmt);
                        }

                        purQtySum=purQtySum.add(purQty);
                        purAmtSum=purAmtSum.add(purAmt);
                        purTaxAmtSum=purTaxAmtSum.add(purTaxAmt);
                        purPreTaxAmtSum=purPreTaxAmtSum.add(purPreTaxAmt);
                        BigDecimal baseQty = new BigDecimal(par2.getPurQty()).multiply(unitRatio);

                        ColumnDataValue deliveryColumns=new ColumnDataValue();
                        deliveryColumns.add("EID",req.geteId(),Types.VARCHAR);
                        deliveryColumns.add("ORGANIZATIONNO",req.getOrganizationNO(),Types.VARCHAR);
                        deliveryColumns.add("PURORDERNO",purOrderNo,Types.VARCHAR);
                        deliveryColumns.add("ITEM",String.valueOf(deliveryItem),Types.VARCHAR);
                        deliveryColumns.add("ITEM2",par.getItem(),Types.VARCHAR);
                        deliveryColumns.add("PLUNO",par.getPluNo(),Types.VARCHAR);
                        deliveryColumns.add("FEATURENO",par.getFeatureNo(),Types.VARCHAR);
                        deliveryColumns.add("PURUNIT",par.getPurUnit(),Types.VARCHAR);
                        deliveryColumns.add("PURQTY",par2.getPurQty(),Types.VARCHAR);
                        deliveryColumns.add("ARRIVALDATE",par2.getArrivalDate(),Types.VARCHAR);
                        //    deliveryColumns.add("BOOKQTY",par.getBookQty(),Types.VARCHAR);
                        //    deliveryColumns.add("RECEIVEQTY",par.getReceiveQty(),Types.VARCHAR);
                        //    deliveryColumns.add("REJECTQTY",par.getRejectQty(),Types.VARCHAR);
                        //    deliveryColumns.add("STOCKINQTY",par.getStockInQty(),Types.VARCHAR);
                        //    deliveryColumns.add("RETURNQTY",par.getReturnQty(),Types.VARCHAR);
                        deliveryColumns.add("ISGIFT",par.getIsGift(),Types.VARCHAR);
                        deliveryColumns.add("TAXCODE",par.getTaxCode(),Types.VARCHAR);
                        deliveryColumns.add("TAXRATE",par.getTaxRate(),Types.VARCHAR);
                        deliveryColumns.add("PURPRICE",par.getPurPrice(),Types.VARCHAR);
                        deliveryColumns.add("PURAMT",purAmt.toString(),Types.VARCHAR);
                        deliveryColumns.add("PRETAXAMT",purPreTaxAmt.toString(),Types.VARCHAR);
                        deliveryColumns.add("TAXAMT",purTaxAmt.toString(),Types.VARCHAR);
                        deliveryColumns.add("BASEUNIT",par.getBaseUnit(),Types.VARCHAR);
                        deliveryColumns.add("UNITRATIO",par.getUnitRatio(),Types.VARCHAR);
                        deliveryColumns.add("BASEQTY",baseQty.toString(),Types.VARCHAR);
                        deliveryColumns.add("TAXCALTYPE",par.getTaxCalType(),Types.VARCHAR);


                        deliveryColumns.add("RECEIVEPRICE",par.getReceivePrice(),Types.VARCHAR);
                        deliveryColumns.add("RECEIVEAMT",par.getReceiveAmt(),Types.VARCHAR);
                        deliveryColumns.add("SUPPRICE",par.getSupPrice(),Types.VARCHAR);
                        deliveryColumns.add("SUPAMT",par.getSupAmt(),Types.VARCHAR);


                        String[] deliveryColumnNames = deliveryColumns.getColumns().toArray(new String[0]);
                        DataValue[] deliveryDataValues = deliveryColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean ib1=new InsBean("DCP_PURORDER_DELIVERY",deliveryColumnNames);
                        ib1.addValues(deliveryDataValues);
                        this.addProcessData(new DataProcessBean(ib1));


                    }
                }

            }
            else
            {
                BigDecimal purQty = new BigDecimal(par.getPurQty());
                BigDecimal purAmt = new BigDecimal(par.getPurAmt());
                BigDecimal purTaxAmt= new BigDecimal(0);
                BigDecimal purPreTaxAmt=  new BigDecimal(0);
                if(taxCalType.equals("1")){
                    BigDecimal augend = taxRate.divide(new BigDecimal(100),6, RoundingMode.HALF_UP).add(new BigDecimal("1"));
                    purPreTaxAmt=purAmt.divide(augend,2, RoundingMode.HALF_UP);
                    purTaxAmt= purAmt.subtract(purPreTaxAmt);
                }else{
                    purTaxAmt= purAmt.multiply(taxRate).divide(new BigDecimal(100),2, RoundingMode.HALF_UP);
                    purPreTaxAmt= purAmt.subtract(purTaxAmt);
                }
                purQtySum=purQtySum.add(purQty);
                purAmtSum=purAmtSum.add(purAmt);
                purTaxAmtSum=purTaxAmtSum.add(purTaxAmt);
                purPreTaxAmtSum=purPreTaxAmtSum.add(purPreTaxAmt);
                BigDecimal baseQty = new BigDecimal(par.getPurQty()).multiply(unitRatio);

                deliveryItem++;
                ColumnDataValue deliveryColumns=new ColumnDataValue();
                deliveryColumns.add("EID",eId,Types.VARCHAR);
                deliveryColumns.add("ORGANIZATIONNO",req.getOrganizationNO(),Types.VARCHAR);
                deliveryColumns.add("PURORDERNO",purOrderNo,Types.VARCHAR);
                deliveryColumns.add("ITEM",String.valueOf(deliveryItem),Types.VARCHAR);
                deliveryColumns.add("ITEM2",par.getItem(),Types.VARCHAR);
                deliveryColumns.add("PLUNO",par.getPluNo(),Types.VARCHAR);
                deliveryColumns.add("FEATURENO",par.getFeatureNo(),Types.VARCHAR);
                deliveryColumns.add("PURUNIT",par.getPurUnit(),Types.VARCHAR);
                deliveryColumns.add("PURQTY",par.getPurQty(),Types.VARCHAR);
                deliveryColumns.add("ARRIVALDATE",par.getArrivalDate(),Types.VARCHAR);
                //    deliveryColumns.add("BOOKQTY",par.getBookQty(),Types.VARCHAR);
                //    deliveryColumns.add("RECEIVEQTY",par.getReceiveQty(),Types.VARCHAR);
                //    deliveryColumns.add("REJECTQTY",par.getRejectQty(),Types.VARCHAR);
                //    deliveryColumns.add("STOCKINQTY",par.getStockInQty(),Types.VARCHAR);
                //    deliveryColumns.add("RETURNQTY",par.getReturnQty(),Types.VARCHAR);
                deliveryColumns.add("ISGIFT",par.getIsGift(),Types.VARCHAR);
                deliveryColumns.add("TAXCODE",par.getTaxCode(),Types.VARCHAR);
                deliveryColumns.add("TAXRATE",par.getTaxRate(),Types.VARCHAR);
                deliveryColumns.add("PURPRICE",par.getPurPrice(),Types.VARCHAR);
                deliveryColumns.add("PURAMT",purAmt.toString(),Types.VARCHAR);
                deliveryColumns.add("PRETAXAMT",purPreTaxAmt.toString(),Types.VARCHAR);
                deliveryColumns.add("TAXAMT",purTaxAmt.toString(),Types.VARCHAR);
                deliveryColumns.add("BASEUNIT",par.getBaseUnit(),Types.VARCHAR);
                deliveryColumns.add("UNITRATIO",par.getUnitRatio(),Types.VARCHAR);
                deliveryColumns.add("BASEQTY",baseQty.toString(),Types.VARCHAR);
                deliveryColumns.add("TAXCALTYPE",par.getTaxCalType(),Types.VARCHAR);


                deliveryColumns.add("RECEIVEPRICE",par.getReceivePrice(),Types.VARCHAR);
                deliveryColumns.add("RECEIVEAMT",par.getReceiveAmt(),Types.VARCHAR);
                deliveryColumns.add("SUPPRICE",par.getSupPrice(),Types.VARCHAR);
                deliveryColumns.add("SUPAMT",par.getSupAmt(),Types.VARCHAR);


                String[] deliveryColumnNames = deliveryColumns.getColumns().toArray(new String[0]);
                DataValue[] deliveryDataValues = deliveryColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib1=new InsBean("DCP_PURORDER_DELIVERY",deliveryColumnNames);
                ib1.addValues(deliveryDataValues);
                this.addProcessData(new DataProcessBean(ib1));


            }


            //  ○ 基准单位/基准单位数量：查询商品基准单位以及采购单位与基准单位换算关系，计算基准单位数量=采购单位数量* 换算率
            //  ○ 库存单位/库存单位数量：查询商品库存单位以及库存单位与基准单位换算关系，计算库存单位数量=基准单位数量* 1/换算率
            //  ○ 采购单价：获取价格定价方式为【2-分量计价】的商品明细，根据传入采购单位数量，换算成模板标准采购单位数量，匹配对应标准采购单位数量区间的价格更新【采购单价】。获取采购价格后同步更新金额相关字段

            //Map<String, Object> mapBase = PosPub.getBaseQty(dao, req.geteId(), par.getPluNo(), par.getPurUnit(), par.getPurQty());
            //if (mapBase == null)
            //{
            //    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+par.getPluNo()+",OUNIT="+par.getPurUnit()+"无记录！");
            //}

            String goodSql="select * from dcp_goods where eid='"+eId+"' and pluno='"+par.getPluNo()+"'";
            List<Map<String, Object>> goodData = dao.executeQuerySQL(goodSql, null);
            if(goodData==null||goodData.size()<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS表pluno="+par.getPluNo()+"无记录！");
            }
            String wunit = goodData.get(0).get("WUNIT").toString();
            Map<String, Object> mapWunit = PosPub.getBaseQty(dao, req.geteId(), par.getPluNo(), wunit,"1");
            if (mapWunit == null)
            {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+par.getPluNo()+",OUNIT="+wunit+"无记录！");
            }
            //int unitdlength=PosPub.getUnitUDLength(dao, eId, baseUnit);
            //                BigDecimal bdcUnitRatio=new BigDecimal(unitRatio);
            //                BigDecimal bdcQty=new BigDecimal(qty);
            //                baseQty=bdcQty.multiply(bdcUnitRatio).setScale(unitdlength, RoundingMode.HALF_UP).toString();
            //
            String baseUnit = par.getBaseUnit();
            BigDecimal baseQty = new BigDecimal(par.getPurQty()).multiply(unitRatio);
            int unitdlength=PosPub.getUnitUDLength(dao, eId, baseUnit);
            int unitdlengthp=PosPub.getUnitUDLength(dao, eId, par.getPurUnit());
            BigDecimal wunitRatio = new BigDecimal(mapWunit.get("unitRatio").toString());
            String wqty=(baseQty).divide(wunitRatio,4).setScale(unitdlength, RoundingMode.HALF_UP).toString();

            List<Map<String, Object>> plunos = getStock.stream().filter(x -> x.get("PLUNO").toString().equals(par.getPluNo())&&x.get("FEATURENO").toString().equals(par.getFeatureNo())).distinct().collect(Collectors.toList());
            String stockQtyString =plunos.size() > 0 ? plunos.get(0).get("BASEQTY").toString() : "0";
            BigDecimal stockQty=new BigDecimal(stockQtyString);
            if(unitRatio.compareTo(BigDecimal.ZERO)!=0){
                stockQty=stockQty.divide(unitRatio,unitdlengthp);
            }

            BigDecimal nonArrivalQty=BigDecimal.ZERO;
            List<Map<String, Object>> arrivalList = getPurOrderDelivery.stream().filter(x -> x.get("PLUNO").toString().equals(par.getPluNo()) && x.get("FEATURENO").toString().equals(par.getFeatureNo())).distinct().collect(Collectors.toList());
            if(CollUtil.isNotEmpty(arrivalList)){
                for (Map<String, Object> arrivalMap : arrivalList){
                    String purunit = arrivalMap.get("PURUNIT").toString();
                    String nonarrivalqtySingle = arrivalMap.get("NONARRIVALQTY").toString();

                    Map<String, Object> nmapBase = PosPub.getBaseQty(dao, req.geteId(), par.getPluNo(), purunit,nonarrivalqtySingle);
                    if (nmapBase == null)
                    {
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+par.getPluNo()+",OUNIT="+purunit+"无记录！");
                    }
                    String nonarrivalqtyBase=nmapBase.get("baseQty").toString();
                    if(unitRatio.compareTo(BigDecimal.ZERO)!=0){
                        nonArrivalQty=nonArrivalQty.add(new BigDecimal(nonarrivalqtyBase).divide(unitRatio,unitdlengthp));
                    }

                }
            }

            //baseQty=bdcQty.multiply(bdcUnitRatio).setScale(unitdlength, RoundingMode.HALF_UP).toString();

            ColumnDataValue goodColumns=new ColumnDataValue();
            goodColumns.add("EID",eId,Types.VARCHAR);
            goodColumns.add("ORGANIZATIONNO",req.getOrganizationNO(),Types.VARCHAR);
            goodColumns.add("PURORDERNO",purOrderNo,Types.VARCHAR);
            goodColumns.add("ITEM",par.getItem(),Types.VARCHAR);
            goodColumns.add("PLUBARCODE",par.getBarcode(),Types.VARCHAR);
            goodColumns.add("PLUNO",par.getPluNo(),Types.VARCHAR);
            goodColumns.add("FEATURENO",par.getFeatureNo(),Types.VARCHAR);
            goodColumns.add("CATEGORY",par.getCategory(),Types.VARCHAR);
            goodColumns.add("TAXCODE",par.getTaxCode(),Types.VARCHAR);
            goodColumns.add("TAXRATE",par.getTaxRate(),Types.DECIMAL);
            goodColumns.add("INCLTAX",par.getIncltax(),Types.VARCHAR);
            goodColumns.add("PURUNIT",par.getPurUnit(),Types.VARCHAR);
            goodColumns.add("PURQTY",purQtySum,Types.DECIMAL);
            goodColumns.add("PURPRICE",par.getPurPrice(),Types.DECIMAL);
            goodColumns.add("PURAMT",purAmtSum,Types.DECIMAL);
            goodColumns.add("PRETAXAMT",purPreTaxAmtSum,Types.DECIMAL);
            goodColumns.add("TAXAMT",purTaxAmtSum,Types.DECIMAL);
            goodColumns.add("ARRIVALDATE",par.getArrivalDate(),Types.VARCHAR);
            goodColumns.add("MULTIDATE",par.getMultiDate(),Types.VARCHAR);
            goodColumns.add("IS_QUALITYCHECK",par.getIs_qualityCheck(),Types.VARCHAR);
            goodColumns.add("CLOSE_STATUS","0",Types.VARCHAR);
            goodColumns.add("WUNIT",wunit,Types.VARCHAR);
            goodColumns.add("WQTY",wqty,Types.DECIMAL);
            goodColumns.add("BASEUNIT",par.getBaseUnit(),Types.VARCHAR);
            goodColumns.add("UNITRATIO",par.getUnitRatio(),Types.VARCHAR);
            goodColumns.add("BASEQTY",baseQty.toString(),Types.DECIMAL);
            goodColumns.add("PURTEMPLATENO",par.getPurTemplateNo(),Types.VARCHAR);
            goodColumns.add("STOCKQTY",stockQty,Types.DECIMAL);
            goodColumns.add("NONARRIVALQTY",nonArrivalQty.toString(),Types.DECIMAL);
            goodColumns.add("TAXCALTYPE",par.getTaxCalType(),Types.VARCHAR);
            goodColumns.add("ISGIFT",par.getIsGift(),Types.VARCHAR);
            goodColumns.add("RECEIVEPRICE",par.getReceivePrice(),Types.VARCHAR);
            goodColumns.add("RECEIVEAMT",par.getReceiveAmt(),Types.VARCHAR);
            goodColumns.add("SUPPRICE",par.getSupPrice(),Types.VARCHAR);
            goodColumns.add("SUPAMT",par.getSupAmt(),Types.VARCHAR);

            String[] goodColumnNames = goodColumns.getColumns().toArray(new String[0]);
            DataValue[] goodDataValues = goodColumns.getDataValues().toArray(new DataValue[0]);
            InsBean ib1=new InsBean("DCP_PURORDER_DETAIL",goodColumnNames);
            ib1.addValues(goodDataValues);
            this.addProcessData(new DataProcessBean(ib1));


           //汇总good
            totpQty=totpQty.add(purQtySum);
            totPurAmt=totPurAmt.add(purAmtSum);
            totTaxAmt=totTaxAmt.add(purTaxAmtSum);
            totPreTaxAmt=totPreTaxAmt.add(purPreTaxAmtSum);


        }
        if(payList!=null&&payList.size()>0) {
            for (DCP_PurOrderCreateReq.PayList par : payList) {
                ColumnDataValue columns = new ColumnDataValue();
                columns.add("eId", eId, Types.VARCHAR);
                columns.add("ORGANIZATIONNO",req.getOrganizationNO(),Types.VARCHAR);
                columns.add("PURORDERNO", purOrderNo, Types.VARCHAR);
                columns.add("ITEM", par.getItem(), Types.VARCHAR);
                columns.add("PAYTYPE", par.getPayType(), Types.VARCHAR);
                columns.add("PURAMT", par.getAmount(), Types.VARCHAR);
                //columns.add("PAYBILLNO", eId, Types.VARCHAR);
                //columns.add("PAYAMOUNT", par.getAmount(), Types.VARCHAR);

                String[] columnNames = columns.getColumns().toArray(new String[0]);
                DataValue[] dataValues = columns.getDataValues().toArray(new DataValue[0]);
                InsBean ib1 = new InsBean("DCP_PURORDER_PAY", columnNames);
                ib1.addValues(dataValues);
                this.addProcessData(new DataProcessBean(ib1));
            }
        }

        if(sourceBillList!=null&&sourceBillList.size()>0){
            for (DCP_PurOrderCreateReq.SourceBillList par : sourceBillList) {
                ColumnDataValue columns = new ColumnDataValue();
                List<DCP_PurOrderCreateReq.GoodsList> goodsCollection = goodsList.stream().filter(x -> x.getPluNo().equals(par.getPluNo()) && x.getFeatureNo().equals(par.getFeatureNo())).distinct().collect(Collectors.toList());
                String item2="";
                if(goodsCollection!=null&&goodsCollection.size()>0){
                    item2=goodsCollection.get(0).getItem();
                }

                //if(Check.Null(par.getConvertPurQty())){

                //}

                columns.add("EID", DataValues.newString(eId));
                columns.add("ORGANIZATIONNO",DataValues.newString(organizationNO));
                columns.add("PURORDERNO",DataValues.newString(purOrderNo));
                columns.add("ITEM",DataValues.newString(par.getItem()));
                columns.add("ITEM2",DataValues.newString(item2));
                columns.add("PLUNO",DataValues.newString(par.getPluNo()));
                columns.add("FEATURENO",DataValues.newString(par.getFeatureNo()));
                columns.add("SOURCEBILLNO",DataValues.newString(par.getSourceBillNo()));
                columns.add("OITEM",DataValues.newString(par.getOItem()));
                columns.add("OUNIT",DataValues.newString(par.getOUnit()));
                columns.add("OQTY",DataValues.newDecimal(par.getOQty()));
                columns.add("PURUNIT",DataValues.newString(par.getPurUnit()));
                columns.add("CONVERTPURQTY",DataValues.newDecimal(par.getConvertPurQty()));
                columns.add("PURQTY",DataValues.newDecimal(par.getPurQty()));

                String[] columnNames = columns.getColumns().toArray(new String[0]);
                DataValue[] dataValues = columns.getDataValues().toArray(new DataValue[0]);
                InsBean ib1 = new InsBean("DCP_PURORDER_SOURCE", columnNames);
                ib1.addValues(dataValues);
                this.addProcessData(new DataProcessBean(ib1));

                //更新底稿 DISTRISTATUS
                UptBean ub1 = new UptBean("DCP_DEMAND");
                ub1.addUpdateValue("DISTRISTATUS", new DataValue("21", Types.VARCHAR));

                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("ORDERNO", new DataValue(par.getSourceBillNo(), Types.VARCHAR));
                ub1.addCondition("ITEM", new DataValue(par.getOItem(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));

            }
        }

        List<Map<String,String>> pluNos = goodsList.stream().map(x -> {
            Map<String,String> map1 = new HashMap();
            map1.put("pluNo", x.getPluNo().toString());
            map1.put("featureNo",x.getFeatureNo().toString());
            return map1;
        }).distinct().collect(Collectors.toList());
        totcQty=new BigDecimal(pluNos.size());

        ColumnDataValue orderColumns=new ColumnDataValue();

        orderColumns.add("EID",eId,Types.VARCHAR);
        orderColumns.add("ORGANIZATIONNO",organizationNO,Types.VARCHAR);
        orderColumns.add("PURORDERNO",purOrderNo,Types.VARCHAR);
        //orderColumns.add("PURORDERID","6F9619FF-8B86-D011-B42D-00C04FC964FF",Types.VARCHAR);
        orderColumns.add("BDATE",request.getBdate(),Types.VARCHAR);
        orderColumns.add("VERSION",req.getVersion(),Types.VARCHAR);
        orderColumns.add("EMPLOYEEID",request.getPurEmpNo(),Types.VARCHAR);
        orderColumns.add("DEPARTID",request.getPurDeptNo(),Types.VARCHAR);
        orderColumns.add("SUPPLIER",request.getSupplier(),Types.VARCHAR);
        orderColumns.add("PURTYPE",request.getPurType(),Types.VARCHAR);
        orderColumns.add("DISTRICENTER",request.getDistriCenter(),Types.VARCHAR);
        orderColumns.add("RECEIPTORGNO",request.getReceiptOrgNo(),Types.VARCHAR);
        orderColumns.add("ADDRESS",request.getAddress(),Types.VARCHAR);
        orderColumns.add("SOURCETYPE",request.getSourceType(),Types.VARCHAR);
        orderColumns.add("SOURCEBILLNO",request.getSourceBillNo(),Types.VARCHAR);
        orderColumns.add("TELEPHONE",request.getTelephone(),Types.VARCHAR);
        orderColumns.add("CONTACT",request.getContact(),Types.VARCHAR);
        orderColumns.add("PAYTYPE",request.getPayType(),Types.VARCHAR);
        orderColumns.add("PAYORGNO",request.getPayOrgNo(),Types.VARCHAR);
        orderColumns.add("BILLDATENO",request.getBilldateNo(),Types.VARCHAR);
        orderColumns.add("PAYDATENO",request.getPaydateNo(),Types.VARCHAR);
        orderColumns.add("INVOICECODE",request.getInvoiceCode(),Types.VARCHAR);
        orderColumns.add("CURRENCY",request.getCurrency(),Types.VARCHAR);
        orderColumns.add("EXPIREDATE",request.getExpireDate(),Types.VARCHAR);
        orderColumns.add("TOT_CQTY",totcQty,Types.DECIMAL);
        orderColumns.add("TOT_PQTY",totpQty,Types.DECIMAL);
        orderColumns.add("TOT_PURAMT",totPurAmt,Types.DECIMAL);
        orderColumns.add("TOT_PRETAXAMT",totPreTaxAmt,Types.DECIMAL);
        orderColumns.add("TOT_TAXAMT",totTaxAmt,Types.DOUBLE);
        orderColumns.add("STATUS","0",Types.VARCHAR);
        orderColumns.add("MEMO",request.getMemo(),Types.VARCHAR);
        orderColumns.add("VERSION",request.getVersion(),Types.VARCHAR);
        orderColumns.add("OWNOPID",employeeNo,Types.VARCHAR);
        orderColumns.add("OWNDEPTID",departmentNo,Types.VARCHAR);
        orderColumns.add("CREATEOPID",req.getOpNO(),Types.VARCHAR);
        orderColumns.add("CREATEDEPTID",departmentNo,Types.VARCHAR);
        orderColumns.add("CREATETIME",lastmoditime,Types.DATE);
        orderColumns.add("LASTMODIOPID",req.getOpNO(),Types.VARCHAR);
        orderColumns.add("TAXCODE",request.getTaxCode(),Types.VARCHAR);
        orderColumns.add("PAYEE",request.getPayee(),Types.VARCHAR);
        orderColumns.add("LASTMODITIME",lastmoditime,Types.DATE);
        orderColumns.add("CORP",request.getCorp(),Types.VARCHAR);
        orderColumns.add("RECEIPTCORP",request.getReceiptCorp(),Types.VARCHAR);

        orderColumns.add("TAXPAYER_TYPE",request.getTaxPayerType(),Types.VARCHAR);
        orderColumns.add("INPUT_TAXCODE",request.getInputTaxCode(),Types.VARCHAR);
        orderColumns.add("INPUT_TAXRATE",request.getInputTaxRate(),Types.VARCHAR);



        String[] orderColumnNames = orderColumns.getColumns().toArray(new String[0]);
        DataValue[] orderDataValues = orderColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib1=new InsBean("DCP_PURORDER",orderColumnNames);
        ib1.addValues(orderDataValues);
        this.addProcessData(new DataProcessBean(ib1));

        //来源类型=“1-要货申请”或“3-大客订单"，更新需求底稿物流状态码distriStatus=[21.已转采购单]
        //DCP_DEMAND，关联条件：需求单号=来源单号，需求项次=来源项次
        if("3".equals(request.getSourceType())||"1".equals(request.getSourceType())){
            if(sourceBillList.size()>0){
                StringBuffer sJoinBillNo=new StringBuffer("");
                StringBuffer sJoinOitemNo=new StringBuffer("");
                for(DCP_PurOrderCreateReq.SourceBillList sourceBill:sourceBillList){
                    String sourceBillNo = sourceBill.getSourceBillNo();
                    String oItem = sourceBill.getOItem();
                    sJoinBillNo.append(sourceBillNo+",");
                    sJoinOitemNo.append(oItem+",");
                }
                Map<String, String> mapSource=new HashMap<String, String>();
                mapSource.put("BILLNO", sJoinBillNo.toString());
                mapSource.put("OITEM", sJoinOitemNo.toString());

                String withasSql_source="";
                withasSql_source=cm.getFormatSourceMultiColWith(mapSource);
                mapSource=null;

                String sqlSource="with p AS ( " + withasSql_source + ") " +
                        "select a.* from DCP_DEMAND a " +
                        "inner join p on p.BILLNO=a.orderno and p.oitem=a.item " +
                        "where a.eid='"+req.geteId()+"' " +
                        "and a.organizationno='"+organizationNO+"' ";
                List<Map<String, Object>> listSource=this.doQueryData(sqlSource,null);

                for (Map<String, Object> row : listSource){
                    UptBean ub1 = new UptBean("DCP_DEMAND");
                    //add Value
                    ub1.addUpdateValue("DISTRISTATUS", new DataValue("21", Types.VARCHAR));

                    //condition
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                    ub1.addCondition("ORDERNO", new DataValue(row.get("ORDERNO").toString(), Types.VARCHAR));
                    ub1.addCondition("ITEM", new DataValue(row.get("ITEM").toString(), Types.VARCHAR));

                    this.addProcessData(new DataProcessBean(ub1));

                }

            }
        }


        this.doExecuteDataToDB();
        res.setPurOrderNo(purOrderNo);

        //内部结算
        //调用内部结算交易
        if(!req.getRequest().getCorp().equals(req.getRequest().getReceiptCorp())) {
            DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
            inReq.setServiceId("DCP_InterSettleDataGenerate");
            inReq.setToken(req.getToken());
            //DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
            DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
            request1.setOrganizationNo(req.getOrganizationNO());
            request1.setBillNo(purOrderNo);
            request1.setSupplyOrgNo(req.getOrganizationNO());
            request1.setReturnSupplyPrice("Y");
            request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType11000.getType());
            request1.setDetail(new ArrayList<>());
            for (DCP_PurOrderCreateReq.GoodsList par : goodsList) {
                DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                detail.setReceiveOrgNo(req.getRequest().getReceiptOrgNo());
                detail.setSourceBillNo("");
                detail.setSourceItem("");

                if(CollUtil.isNotEmpty(sourceBillList)){
                    List<DCP_PurOrderCreateReq.SourceBillList> collect = sourceBillList.stream().filter(x -> x.getPluNo().equals(par.getPluNo())&&x.getFeatureNo().equals(par.getFeatureNo())).collect(Collectors.toList());
                    if(CollUtil.isNotEmpty(collect)){
                        detail.setSourceBillNo(collect.get(0).getSourceBillNo());
                        detail.setSourceItem(collect.get(0).getOItem());
                    }
                }

                detail.setItem(String.valueOf(par.getItem()));
                detail.setPluNo(par.getPluNo());
                detail.setFeatureNo(par.getFeatureNo());
                detail.setPUnit(par.getPurUnit());
                detail.setPQty(String.valueOf(par.getPurQty()));
                detail.setReceivePrice("");
                detail.setReceiveAmt("");
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
            }else{
                List<DCP_InterSettleDataGenerateRes.SupPriceDetail> supPriceDetail = resserver.getSupPriceDetail();
                if(supPriceDetail.size()>0){
                    for(DCP_InterSettleDataGenerateRes.SupPriceDetail supd:supPriceDetail){
                        String item = supd.getItem();
                        String receivePrice = supd.getReceivePrice();
                        String supplyPrice = supd.getSupplyPrice();

                        List<DCP_PurOrderCreateReq.GoodsList> collect = goodsList.stream().filter(x -> x.getItem().equals(item)).collect(Collectors.toList());
                        if(CollUtil.isNotEmpty(collect)){
                            BigDecimal purQty = new BigDecimal(collect.get(0).getPurQty());
                            BigDecimal receiveAmt = purQty.multiply(new BigDecimal(receivePrice));
                            BigDecimal supplyAmt = purQty.multiply(new BigDecimal(supplyPrice));
                            UptBean ub1 = new UptBean("DCP_PURORDER_DETAIL");
                            ub1.addUpdateValue("RECEIVEPRICE", DataValues.newString(receivePrice));
                            ub1.addUpdateValue("RECEIVEAMT",DataValues.newString(receiveAmt));
                            ub1.addUpdateValue("SUPPRICE",DataValues.newString(supplyPrice));
                            ub1.addUpdateValue("SUPAMT",DataValues.newString(supplyAmt));

                            ub1.addCondition("EID", DataValues.newString(eId));
                            ub1.addCondition("PURORDERNO",DataValues.newString(purOrderNo));
                            ub1.addCondition("ITEM",DataValues.newString(item));
                            this.addProcessData(new DataProcessBean(ub1));

                            UptBean ub2 = new UptBean("DCP_PURORDER_DELIVERY");
                            ub2.addUpdateValue("RECEIVEPRICE", DataValues.newString(receivePrice));
                            ub2.addUpdateValue("RECEIVEAMT",DataValues.newString(receiveAmt));
                            ub2.addUpdateValue("SUPPRICE",DataValues.newString(supplyPrice));
                            ub2.addUpdateValue("SUPAMT",DataValues.newString(supplyAmt));

                            ub2.addCondition("EID", DataValues.newString(eId));
                            ub2.addCondition("PURORDERNO",DataValues.newString(purOrderNo));
                            ub2.addCondition("ITEM2",DataValues.newString(item));
                            this.addProcessData(new DataProcessBean(ub2));

                        }

                    }

                    this.doExecuteDataToDB();
                }

            }
        }



        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurOrderCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurOrderCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurOrderCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected String getQuerySql(DCP_PurOrderCreateReq req) throws Exception {
        return null;
    }

    private String getPurOrderNO(DCP_PurOrderCreateReq req) throws Exception  {
        //billcode
        String billCode="";
        String sql = null;
        String templateNo = null;
        String shopId = req.getShopId();
        String eId = req.geteId();

        StringBuffer sqlbufOrg = new StringBuffer("select billcode FROM dcp_org where eid='"+req.geteId()+"' and organizationno='"+req.getOrganizationNO()+"' ");
        sql = sqlbufOrg.toString();
        List<Map<String, Object>> getQDataOrg = this.doQueryData(sql, null);
        if(getQDataOrg!=null&&getQDataOrg.isEmpty()==false){
            billCode = (String) getQDataOrg.get(0).get("BILLCODE");
        }

        StringBuffer sqlbuf = new StringBuffer("select F_DCP_GETBILLNO('"+eId+"','"+shopId+"','"+billCode+"-PO') TEMPLATENO FROM dual");
        sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && getQData.isEmpty() == false)
        {
            templateNo = (String) getQData.get(0).get("TEMPLATENO");
        }
        else
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "取加工任务单号失败！");
        }
        return templateNo;
    }


    //科学计数法转数字
    private String snToString (String sn){
        if (Check.Null(sn)){
            return "";
        }

        if (sn.contains("E")) {
            try {
                BigDecimal b = new BigDecimal(sn);
                return b.toPlainString();
            } catch (Exception e) {
                return "";
            }
        }else{
            return "";
        }
    }

}

