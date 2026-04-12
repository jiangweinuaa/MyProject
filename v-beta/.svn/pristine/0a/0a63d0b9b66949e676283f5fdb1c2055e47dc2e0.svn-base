package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ReconliationProcessReq;
import com.dsc.spos.json.cust.res.DCP_ReconliationProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_ReconliationProcess extends SPosAdvanceService<DCP_ReconliationProcessReq, DCP_ReconliationProcessRes> {

    @Override
    protected void processDUID(DCP_ReconliationProcessReq req, DCP_ReconliationProcessRes res) throws Exception {

        String createTime =	new SimpleDateFormat("HHmmss").format(new Date());
        String createDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        String eId = req.geteId();
        String startDate = req.getRequest().getStartDate();
        String endDate = req.getRequest().getEndDate();
        String organizationNo = req.getRequest().getOrganizationNo();
        if(Check.Null(organizationNo)){
            organizationNo = req.getOrganizationNO();
        }
        String dataType = req.getRequest().getDataType();
        String corp = req.getRequest().getCorp();
        String bizPartnerNo = req.getRequest().getBizPartnerNo();

        String bizType ="1";
        if("1".equals(dataType)){
            bizType ="1";
        }else if("3".equals(dataType)){
            bizType="2";
        }
        String bDate = req.getRequest().getBDate();
        if(Check.Null(bDate)){
            bDate=createDate;
        }
        String ym = bDate.substring(0, 6);
        String ymd=ym+"31";

        //当organizationNo=法人，
        // 或非法人同时对应的上层法人为一般纳税人时，
        // 批量对账生成对账单【含发票否】，默认=Y
        String isInvoiceIncl="N";
        String orgSql="select a.*,b.up_org,c.TAXPAYER_TYPE from dcp_org a" +
                " left join DCP_ORG_LEVEL b on a.eid=b.eid and a.organizationno=b.organizationno " +
                " left join dcp_org c on a.eid=c.eid and b.up_org=c.organizationno " +
                " where a.eid='"+req.geteId()+"' and a.organizationno='"+organizationNo+"'";
        List<Map<String, Object>> orgList = this.doQueryData(orgSql, null);
        if(CollUtil.isNotEmpty(orgList)){
            String is_corp = orgList.get(0).get("IS_CORP").toString();
            if("Y".equals(is_corp)){
                isInvoiceIncl="Y";
            }else{
                String taxPayerType = orgList.get(0).get("TAXPAYER_TYPE").toString();
                if("1".equals(taxPayerType)){
                    isInvoiceIncl="Y";
                }
            }
        }


        StringBuffer settleSB=new StringBuffer("select * from DCP_SETTLEDATA a " +
                " where a.eid='"+eId+"' " +
                " and a.BIZTYPE='"+bizType+"'" +
                " and a.ORGANIZATIONNO='"+organizationNo+"'" +
                " and a.status='0' " +
                " and a.bdate<='"+endDate+"'" +
                " and a.bdate>='"+startDate+"'" +
                " and a.bdate<='"+ymd+"'");
        if(Check.NotNull(bizPartnerNo)){
            settleSB.append(" and a.BIZPARTNERNO='"+bizPartnerNo+"'");
        }
        List<Map<String, Object>> list = this.doQueryData(settleSB.toString(), null);
        if(CollUtil.isNotEmpty(list)){

            String payDateSql="select * from DCP_PAYDATE a where a.eid='"+req.geteId()+"'";
            List<Map<String, Object>> payDateList = this.doQueryData(payDateSql, null);

            List<String> createNos=new ArrayList<>();

            List<Map> collect = list.stream().map(x -> {
                Map disMap = new HashMap<>();
                disMap.put("bizpartnerNo", x.get("BIZPARTNERNO").toString());
                disMap.put("year", x.get("YEAR").toString());
                disMap.put("month", x.get("MONTH").toString());
                disMap.put("currency",x.get("CURRENCY").toString());
                disMap.put("invoiceCode",x.get("INVOICECODE").toString());
                disMap.put("payDateNo",x.get("PAYDATENO").toString());
                return disMap;
            }).distinct().collect(Collectors.toList());
            for (Map map : collect){
                String bizpartnerNo = map.get("bizpartnerNo").toString();
                String year = map.get("year").toString();
                String month = map.get("month").toString();
                String currency = map.get("currency").toString();
                String payDateNo = map.get("payDateNo").toString();
                String invoiceCode = map.get("invoiceCode").toString();
                //String isInvoiceIncl=Check.NotNull(invoiceCode)?"Y":"N";

                List<Map<String, Object>> filterRows = list.stream().filter(y -> y.get("MONTH").toString().equals(month)
                        && y.get("YEAR").toString().equals(year)
                &&y.get("BIZPARTNERNO").toString().equals(bizpartnerNo)
                &&y.get("CURRENCY").toString().equals(currency)
                &&y.get("PAYDATENO").toString().equals(payDateNo)
                &&y.get("INVOICECODE").toString().equals(invoiceCode)).collect(Collectors.toList());
                String reconNo ="1".equals(dataType)? this.getOrderNO(req,"GYDZ"):this.getOrderNO(req,"KHDZ");
                createNos.add(reconNo);
                BigDecimal curreconAmt=new BigDecimal(0);
                BigDecimal curreconTaxAmt=new BigDecimal(0);
                BigDecimal curreconPreTaxAmt=new BigDecimal(0);
                BigDecimal paidReceAmt=new BigDecimal(0);
                BigDecimal unPaidReceAmt=new BigDecimal(0);

                int detailItem=0;
                for (Map<String, Object> singleRow : filterRows){
                    detailItem++;
                    String billNo = singleRow.get("BILLNO").toString();
                    String bType = singleRow.get("BTYPE").toString();
                    String fee = singleRow.get("FEE").toString();
                    String pluNo = singleRow.get("PLUNO").toString();
                    String taxRate = singleRow.get("TAXRATE").toString();
                    String rDate = singleRow.get("BDATE").toString();
                    String item = singleRow.get("ITEM").toString();
                    BigDecimal direction = new BigDecimal(singleRow.get("DIRECTION").toString());
                    BigDecimal billQty = new BigDecimal(singleRow.get("BILLQTY").toString());
                    BigDecimal reconQtyYet = new BigDecimal(0);//对账数量没记  只记了对账金额
                    BigDecimal reconQty = billQty.subtract(reconQtyYet);
                    BigDecimal billPrice = new BigDecimal(singleRow.get("BILLPRICE").toString());
                    BigDecimal preTaxAmt = new BigDecimal(Check.Null(singleRow.get("PRETAXAMT").toString())?"0":singleRow.get("PRETAXAMT").toString());
                    BigDecimal billAmt = new BigDecimal(singleRow.get("BILLAMT").toString());
                    BigDecimal reconAmt = new BigDecimal(singleRow.get("SETTLEAMT").toString());//已对账金额
                    BigDecimal unSettleAmt = new BigDecimal(singleRow.get("UNSETTLEAMT").toString());//未对账金额
                    BigDecimal currentReconAmt = billAmt.subtract(reconAmt);
                    curreconAmt=curreconAmt.add(currentReconAmt.multiply(direction));

                    String category = singleRow.get("CATEGORY").toString();
                    String taxCode = singleRow.get("TAXCODE").toString();
                    String departId = singleRow.get("DEPARTID").toString();
                    BigDecimal paidAmt = new BigDecimal(singleRow.get("PAIDAMT").toString());
                    BigDecimal unPaidAmt = new BigDecimal(singleRow.get("UNPAIDAMT").toString());
                    paidReceAmt=paidReceAmt.add(paidAmt.multiply(direction));
                    unPaidReceAmt=unPaidReceAmt.add(unPaidAmt.multiply(direction));
                    BigDecimal taxAmt = new BigDecimal(singleRow.get("TAXAMT").toString());

                    BigDecimal reconTaxAmt=new BigDecimal(0);
                    BigDecimal reconPreTaxAmt=new BigDecimal(0);
                    if(billAmt.compareTo(BigDecimal.ZERO)!=0){
                         reconTaxAmt = taxAmt.multiply(currentReconAmt).divide(billAmt, 2, RoundingMode.HALF_UP);
                         reconPreTaxAmt = preTaxAmt.multiply(currentReconAmt).divide(billAmt, 2, RoundingMode.HALF_UP);
                    }
                    curreconTaxAmt=curreconTaxAmt.add(reconTaxAmt.multiply(direction));
                    curreconPreTaxAmt=curreconPreTaxAmt.add(reconPreTaxAmt.multiply(direction));
                    ColumnDataValue detailColumns=new ColumnDataValue();
                    detailColumns.add("EID", DataValues.newString(eId));
                    detailColumns.add("CREATEBY", DataValues.newString(employeeNo));
                    detailColumns.add("CREATE_DATE", DataValues.newString(createDate));
                    detailColumns.add("CREATE_TIME", DataValues.newString(createTime));
                    detailColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
                    detailColumns.add("CORP", DataValues.newString(corp));
                    detailColumns.add("RECONNO", DataValues.newString(reconNo));
                    detailColumns.add("ITEM", DataValues.newString(detailItem));
                    detailColumns.add("SOURCETYPE", DataValues.newString(bType));
                    detailColumns.add("SOURCENO", DataValues.newString(billNo));
                    detailColumns.add("SOURCENOSEQ", DataValues.newString(item));
                    detailColumns.add("RDATE", DataValues.newString(rDate));
                    detailColumns.add("FEE", DataValues.newString(fee));
                    detailColumns.add("PLUNO", DataValues.newString(pluNo));
                    detailColumns.add("CURRENCY", DataValues.newString(currency));
                    detailColumns.add("TAXRATE", DataValues.newString(taxRate));
                    detailColumns.add("DIRECTION", DataValues.newString(direction));
                    detailColumns.add("BILLQTY", DataValues.newString(billQty));
                    detailColumns.add("RECONQTY", DataValues.newString(reconQty));
                    detailColumns.add("BILLPRICE", DataValues.newString(billPrice));
                    detailColumns.add("PRETAXAMT", DataValues.newString(preTaxAmt));
                    detailColumns.add("AMT", DataValues.newString(billAmt));
                    detailColumns.add("RECONAMT", DataValues.newString(reconAmt));
                    detailColumns.add("UNPAIDAMT", DataValues.newString(unSettleAmt));
                    detailColumns.add("CURRRECONAMT", DataValues.newString(currentReconAmt));
                    detailColumns.add("DEPARTID", DataValues.newString(departId));
                    detailColumns.add("CATEGORY", DataValues.newString(category));
                    detailColumns.add("TAXCODE", DataValues.newString(taxCode));
                    detailColumns.add("ISINVOCEINCL", DataValues.newString(isInvoiceIncl));

                    String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                    DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean detailib=new InsBean("DCP_RECONDETAIL",detailColumnNames);
                    detailib.addValues(detailDataValues);
                    this.addProcessData(new DataProcessBean(detailib));

                    //更新状态  对账中
                    UptBean ub1 = new UptBean("DCP_SETTLEDATA");
                    ub1.addUpdateValue("status", new DataValue("1", Types.VARCHAR));
                    //本次对账等于billamt-已对账   那对账金额就是全部了
                    ub1.addUpdateValue("SETTLEAMT", new DataValue(billAmt.toString(), Types.VARCHAR));
                    ub1.addUpdateValue("UNSETTLEAMT", new DataValue("0".toString(), Types.VARCHAR));

                    //condition
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                    ub1.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub1));
                }

                List<Map<String, Object>> payDateNoList = payDateList.stream().filter(x -> x.get("PAYDATENO").toString().equals(payDateNo)).collect(Collectors.toList());
                String payDate=bDate;
                if (payDateNoList.size() > 0) {
                    Date date =  DateFormatUtils.parseDate(bDate);
                    Integer season1 = Integer.valueOf(payDateNoList.get(0).get("PSEASONS").toString());
                    Integer month1 = Integer.valueOf(payDateNoList.get(0).get("PMONTHS").toString());
                    Integer day1 = Integer.valueOf(payDateNoList.get(0).get("PDAYS").toString());
                    date = DateFormatUtils.addMonth(date, 3 * season1);
                    date = DateFormatUtils.addMonth(date, month1);
                    date = DateFormatUtils.addDay(date, day1);
                    payDate = DateFormatUtils.format(date,"yyyyMMdd");
                }

                ColumnDataValue mainColumns=new ColumnDataValue();
                mainColumns.add("EID", DataValues.newString(eId));
                mainColumns.add("CREATEBY", DataValues.newString(employeeNo));
                mainColumns.add("CREATE_DATE", DataValues.newString(createDate));
                mainColumns.add("CREATE_TIME", DataValues.newString(createTime));
                mainColumns.add("STATUS", DataValues.newString("0"));
                mainColumns.add("MEMO", DataValues.newString("对账单批量生成"));

                mainColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
                mainColumns.add("RECONNO", DataValues.newString(reconNo));
                mainColumns.add("BDATE", DataValues.newString(bDate));
                mainColumns.add("CORP", DataValues.newString(corp));
                mainColumns.add("DATATYPE", DataValues.newString(dataType));
                mainColumns.add("BIZPARTNERNO", DataValues.newString(bizpartnerNo));
                mainColumns.add("YEAR", DataValues.newString(year));
                mainColumns.add("MONTH", DataValues.newString(month));
                mainColumns.add("ESTRECEEXPDAY", DataValues.newString(payDate));
                mainColumns.add("CURRRECONAMT", DataValues.newDecimal(curreconAmt));
                mainColumns.add("CURRRECONTAXAMT", DataValues.newDecimal(curreconTaxAmt));
                mainColumns.add("CURRRECONPRETAXAMT", DataValues.newDecimal(curreconPreTaxAmt));
                mainColumns.add("PAIDRECEAMT", DataValues.newDecimal(paidReceAmt));
                mainColumns.add("NOTPAIDRECEAMT", DataValues.newDecimal(unPaidReceAmt));
                mainColumns.add("ISINVOICEINCL", DataValues.newString(isInvoiceIncl));
                mainColumns.add("STARTDATE", DataValues.newString(startDate));
                mainColumns.add("ENDDATE", DataValues.newString(endDate));
                mainColumns.add("CURRENCY", DataValues.newString(currency));
                mainColumns.add("PAYDATENO", DataValues.newString(payDateNo));


                String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
                DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib=new InsBean("DCP_RECONLIATION",mainColumnNames);
                ib.addValues(mainDataValues);
                this.addProcessData(new DataProcessBean(ib));



            }

            if(createNos.size()>0){
                String message="";
                for(String thisNo:createNos){
                    message=message+thisNo+",";
                }
                message=message.substring(0,message.length()-1);
                res.setReconNo( message);
                res.setNum(createNos.size());
                res.setFirstReconNo(createNos.get(0));
                res.setLastReconNo(createNos.get(createNos.size()-1));
            }
            res.setReconNos(createNos);

        }


        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReconliationProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReconliationProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReconliationProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReconliationProcessReq req) throws Exception {
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
    protected TypeToken<DCP_ReconliationProcessReq> getRequestType() {
        return new TypeToken<DCP_ReconliationProcessReq>() {
        };
    }

    @Override
    protected DCP_ReconliationProcessRes getResponseType() {
        return new DCP_ReconliationProcessRes();
    }
}

