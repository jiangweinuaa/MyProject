package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ApEstProcessReq;
import com.dsc.spos.json.cust.req.DCP_ApPrepayProcessReq;
import com.dsc.spos.json.cust.res.DCP_ApEstProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_ApEstProcess extends SPosAdvanceService<DCP_ApEstProcessReq, DCP_ApEstProcessRes> {

    @Override
    protected void processDUID(DCP_ApEstProcessReq req, DCP_ApEstProcessRes res) throws Exception {

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();

        String bDate = req.getRequest().getBdate();
        String corp = req.getRequest().getCorp();
        String apType = req.getRequest().getApType();

        String createDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String createTime = new SimpleDateFormat("HHmmss").format(new Date());



        //1. 判断：账套+单据日期不可小于【DCP_ACOUNT_SETTING账套设置】中 APCLOSINGDATE 应付关账日
        String accSql="select a.*,to_char(a.APCLOSINGDATE,'yyyyMMdd') as apclosingdates  from DCP_ACOUNT_SETTING a where a.eid='"+eId+"' and a.corp='"+corp+"'  and a.ACCTTYPE='1' ";
        List<Map<String, Object>> accList = this.doQueryData(accSql, null);
        if(accList.size()>0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "找不到对应的账套");
        }
        String accountId = accList.get(0).get("ACCOUNTID").toString();
        String apClosingDate = accList.get(0).get("APCLOSINGDATES").toString();
        if(Integer.valueOf(apClosingDate)<Integer.valueOf(bDate)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据日期不得小于应付关账日期");
        }

//2. 涉及表：DCP_APBILL 应付单单据，DCP_APBILLDETAIL应付单明细，DCP_APPERD应付单账期，DCP_APBILLPMT应付单付款
        //3. 条件：结算法人corp/单据日期bDate
        //4. 数据来源：结算底稿：DCP_SETTLEDATA；BIZTYPE=1 供应商
        //  a. 将结算底稿内【未立账金额】>0 的单据相关信息产生至【DCP_APBILLDETAIL】应付单明细；
        //  b. 同时将已立账数量、已立账金额 更新，未立账金额-已立账金额；
        //  c. 条件为：执行单据日期的当月 1 日-31 日；
        //  d. 暂估单据无需写入到应付汇总表内；
        String sub = bDate.substring(0, 6);
        String startDate=sub+"01";
        String endDate=sub+"31";

        String settleSql="select a.*,nvl(b1.sstockinno,'') as sstockinno,nvl(b2.sstockoutno,'') as sstockoutno,nvl(b1.payee,'') as inpayee,nvl(b2.payee,'') as outpayee" +
                " ,c.payee as defaultpayee,d.PSEASONS,d.pmonths,d.pdays " +
                " from dcp_settledata a " +
                " left join dcp_sstockin b1 on a.eid=b1.eid and a.billno=b1.sstockinno " +
                " left join dcp_sstockout b2 on a.eid=b2.eid and a.billno=b2.sstockoutno " +
                " left join dcp_bizpartner c on c.eid=a.eid and c.bizpartnerno=b.bizpartnerno " +
                " left join DCP_PAYDATE d on d.eid=a.eid and d.paydateno=a.paydateno " +
                " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' " +
                " and a.UNAPAMT>0  and a.bdate>='"+startDate+"' and a.bdate<='"+endDate+"' ";
        List<Map<String, Object>> settleList = this.doQueryData(settleSql, null);
        if(settleList.size()>0){
            //应付科目数据
            String apSubjectSql="select * from DCP_APSETUPSUBJECT a where a.eid='"+eId+"' and a.accountid='"+accountId+"' ";
            List<Map<String, Object>> apSubjectList = this.doQueryData(apSubjectSql, null);
            String feeSubjectSql="select * from DCP_FEESUBJECT a where a.eid='"+eId+"' and a.accountid='"+accountId+"' ";
            List<Map<String, Object>> feeSubjectList = this.doQueryData(feeSubjectSql, null);


            List<DCP_ApEstProcessReq.ApDoc> apDocList = settleList.stream().map(x -> {
                DCP_ApEstProcessReq.ApDoc apDoc = req.new ApDoc();
                apDoc.setBDate(x.get("BDATE").toString());
                apDoc.setOrganizationNo(x.get("ORGANIZATIONNO").toString());
                apDoc.setBizPartnerNo(x.get("BIZPARTNERNO").toString());
                apDoc.setPayDateNo(x.get("PAYDATENO").toString());
                apDoc.setTaxCode(x.get("TAXCODE").toString());
                apDoc.setBillNo(x.get("BILLNO").toString());
                return apDoc;
            }).distinct().collect(Collectors.toList());
            for (DCP_ApEstProcessReq.ApDoc apDoc : apDocList) {
                List<Map<String, Object>> apDetails = settleList.stream().filter(x -> x.get("BIZPARTNERNO").toString().equals(apDoc.getBizPartnerNo())
                        && x.get("PAYDATENO").toString().equals(apDoc.getPayDateNo())
                        && x.get("TAXCODE").toString().equals(apDoc.getTaxCode())
                        && x.get("ORGANIZATIONNO").toString().equals(apDoc.getOrganizationNo())
                        && x.get("BDATE").toString().equals(apDoc.getBDate())
                        && x.get("BILLNO").toString().equals(apDoc.getBillNo())
                ).collect(Collectors.toList());
                String apNo = this.getApNo(req);

                String taxRate = apDetails.get(0).get("TAXRATE").toString();
                String taxAmt = apDetails.get(0).get("TAXAMT").toString();
                String preTaxAmt = apDetails.get(0).get("PRETAXAMT").toString();
                String sstockInNo = apDetails.get(0).get("SSTOCKINNO").toString();
                String sstockOutNo = apDetails.get(0).get("SSTOCKOUTNO").toString();
                String inPayee = apDetails.get(0).get("INPAYEE").toString();
                String outPayee = apDetails.get(0).get("OUTPAYEE").toString();
                String defaultPayee = apDetails.get(0).get("DEFAULTPAYEE").toString();
                String payDate = apDetails.get(0).get("PAYDATE").toString();
                String payee="";
                if(Check.NotNull(sstockInNo)){
                    payee = inPayee;
                }else{
                    payee = outPayee;
                }
                if(Check.Null(payee)){
                    payee=defaultPayee;
                }

                String apSubjectId="";
                String feeSubjectId="";
                List<Map<String, Object>> filterApSubs = apSubjectList.stream().filter(x -> x.get("SETUPTYPE").toString().equals("1")).collect(Collectors.toList());
                if(filterApSubs.size()>0){
                    apSubjectId = filterApSubs.get(0).get("APSUBJECTID").toString();
                }
                List<Map<String, Object>> filterFeeSubs = feeSubjectList.stream().filter(x -> x.get("SETUPTYPE").toString().equals("1")).collect(Collectors.toList());
                if(filterFeeSubs.size()>0){
                    feeSubjectId = filterFeeSubs.get(0).get("FEESUBJECTID").toString();
                }

                BigDecimal exRate=new BigDecimal("1");
                BigDecimal lcytAmt=exRate.multiply(new BigDecimal(taxAmt));
                BigDecimal lcyBtAmt=exRate.multiply(new BigDecimal(preTaxAmt));

                int item=0;
                for (Map<String, Object> apDetail : apDetails){
                    String sourceItem = apDetail.get("ITEM").toString();
                    String sourceOrgNo = apDetail.get("ORGANIZATIONNO").toString();
                    String pluno = apDetail.get("PLUNO").toString();
                    String spec = apDetail.get("SPEC").toString();
                    String priceUnit = apDetail.get("PRICEUNIT").toString();
                    String fee = apDetail.get("FEE").toString();
                    String billPrice = apDetail.get("BILLPRICE").toString();
                    String billQty = apDetail.get("BILLQTY").toString();
                    String direction = apDetail.get("DIRECTION").toString();
                    BigDecimal taxAmtd = new BigDecimal(apDetail.get("TAXAMT").toString());
                    BigDecimal amt = new BigDecimal(apDetail.get("AMT").toString());
                    BigDecimal preTaxAmtd = new BigDecimal(apDetail.get("PRETAXAMT").toString());
                    BigDecimal subtract = amt.subtract(preTaxAmtd);
                    BigDecimal multiplyPrice = exRate.multiply(new BigDecimal(billPrice));
                    item++;
                    ColumnDataValue detailColumns=new ColumnDataValue();
                    detailColumns.add("EID", DataValues.newString(eId));
                    detailColumns.add("APNO", DataValues.newString(apNo));
                    detailColumns.add("ITEM", DataValues.newString(item));
                    detailColumns.add("BIZPARTNERNO", DataValues.newString(apDoc.getBizPartnerNo()));
                    detailColumns.add("RECEIVER", DataValues.newString(payee));
                    detailColumns.add("SOURCETYPE", DataValues.newString("5"));//5.采购单
                    detailColumns.add("SOURCENO", DataValues.newString(apDoc.getBillNo()));
                    detailColumns.add("SOURCEITEM", DataValues.newString(sourceItem));
                    detailColumns.add("SOURCEORG", DataValues.newString(sourceOrgNo));
                    detailColumns.add("PLUNO", DataValues.newString(pluno));
                    detailColumns.add("SPEC", DataValues.newString(spec));
                    detailColumns.add("PRICEUNIT", DataValues.newString(priceUnit));
                    detailColumns.add("QTY", DataValues.newDecimal(billQty));
                    detailColumns.add("BILLPRICE", DataValues.newDecimal(billPrice));
                    detailColumns.add("FEE", DataValues.newDecimal(fee));
                    //detailColumns.add("OOFNO", DataValues.newString(apBill.getOofNo()));
                    //detailColumns.add("OOITEM", DataValues.newString(apBill.getOoItem()));
                    //detailColumns.add("DEPARTNO", DataValues.newString(apBill.getDepartId()));
                    //detailColumns.add("CATEGORY", DataValues.newString(apBill.getCateGory()));
                    //detailColumns.add("ISGIFT", DataValues.newString(apBill.getIsGift()));
                    detailColumns.add("BSNO", DataValues.newString(""));
                    detailColumns.add("TAXRATE", DataValues.newDecimal(taxRate));
                    detailColumns.add("FEESUBJECTID", DataValues.newString(feeSubjectId));
                    detailColumns.add("APSUBJECTID", DataValues.newString(apSubjectId));
                    //detailColumns.add("TAXSUBJECTID", DataValues.newString(apBill.getTaxSubjectId()));
                    detailColumns.add("DIRECTION", DataValues.newString(direction));
                    //detailColumns.add("ISREVEST", DataValues.newString(apBill.getIsRevEst()));
                    detailColumns.add("PAYDATENO", DataValues.newString(apDoc.getPayDateNo()));
                    //detailColumns.add("EMPLOYEENO", DataValues.newString(apBill.getEmployeeNo()));
                    detailColumns.add("MEMO", DataValues.newString(""));
                    detailColumns.add("CURRENCY", DataValues.newString("CNY"));
                    detailColumns.add("FCYPRICE", DataValues.newDecimal(billPrice));
                    detailColumns.add("EXRATE", DataValues.newDecimal("1"));
                    detailColumns.add("FCYBTAMT", DataValues.newDecimal(preTaxAmtd));
                    detailColumns.add("FCYTAMT", DataValues.newDecimal(subtract));
                    detailColumns.add("FCYTATAMT", DataValues.newDecimal(amt));
                    detailColumns.add("FCYSTDCOSTAMT", DataValues.newDecimal(0));
                    detailColumns.add("FCYACTCOSTAMT", DataValues.newDecimal(0));
                    detailColumns.add("LCYPRICE", DataValues.newDecimal(multiplyPrice));
                    detailColumns.add("LCYBTAMT", DataValues.newDecimal(preTaxAmtd));
                    detailColumns.add("LCYTAMT", DataValues.newDecimal(subtract));
                    detailColumns.add("LCYTATAMT", DataValues.newDecimal(amt));
                    detailColumns.add("LCYSTDCOSTAMT", DataValues.newDecimal(0));
                    detailColumns.add("LCYACTCOSTAMT", DataValues.newDecimal(0));
                    detailColumns.add("PURORDERNO", DataValues.newString(""));

                    String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                    DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean detailib=new InsBean("DCP_APBILLDETAIL",detailColumnNames);
                    detailib.addValues(detailDataValues);
                    this.addProcessData(new DataProcessBean(detailib));
                }

                //apperd
                int erdItem=0;
                List<Map<String,String>> sourceOrgNos = apDetails.stream().map(x ->{
                    Map<String,String> map=new HashMap();
                    map.put("ORGANIZATIONNO", x.get("ORGANIZATIONNO").toString());
                    map.put("PAYDATENO",x.get("PAYDATENO").toString());
                    return map;
                }).distinct().collect(Collectors.toList());

                for (Map<String,String> sourceOrgMap : sourceOrgNos){
                    String sourceOrg = sourceOrgMap.get("ORGANIZATIONNO").toString();
                    String payDateNo = sourceOrgMap.get("PAYDATENO").toString();
                    erdItem++;
                    List<Map<String, Object>> sourceDetails = apDetails.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(sourceOrg)&&x.get("PAYDATENO").toString().equals(payDateNo)).collect(Collectors.toList());
                    BigDecimal unapAmt = sourceDetails.stream().map(x->new BigDecimal(x.get("UNAPAMT").toString())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    String direction=unapAmt.compareTo(BigDecimal.ZERO)>0?"1":"-1";
                    ColumnDataValue detailColumns=new ColumnDataValue();
                    detailColumns.add("EID", DataValues.newString(req.geteId()));
                    detailColumns.add("ACCOUNTID", DataValues.newString(accountId));
                    detailColumns.add("CORP", DataValues.newString(corp));
                    detailColumns.add("SOURCEORG", DataValues.newString(sourceOrg));
                    detailColumns.add("ORGANIZATIONNO", DataValues.newString(sourceOrg));
                    detailColumns.add("APNO", DataValues.newString(apNo));
                    detailColumns.add("ITEM", DataValues.newString(erdItem));
                    detailColumns.add("INSTPMTSEQ", DataValues.newString(erdItem));
                    detailColumns.add("PAYTYPE", DataValues.newString("10"));
                    detailColumns.add("PAYDUEDATE", DataValues.newDate(payDate));
                    detailColumns.add("BILLDUEDATE", DataValues.newDate(payDate));
                    detailColumns.add("DIRECTION", DataValues.newString(direction));
                    detailColumns.add("FCYREQAMT", DataValues.newDecimal("0"));
                    detailColumns.add("CURRENCY", DataValues.newString(unapAmt));
                    detailColumns.add("EXRATE", DataValues.newDecimal(exRate));
                    detailColumns.add("FCYREVSEDRATE", DataValues.newDecimal("0"));
                    detailColumns.add("FCYTATAMT", DataValues.newDecimal(unapAmt));
                    detailColumns.add("FCYPMTREVAMT", DataValues.newDecimal("0"));
                    detailColumns.add("REVALADJNUM", DataValues.newString("0"));
                    detailColumns.add("LCYTATAMT", DataValues.newDecimal(unapAmt));
                    detailColumns.add("LCYPMTREVAMT", DataValues.newDecimal(""));
                    detailColumns.add("PAYDATENO", DataValues.newString(payDateNo));
                    detailColumns.add("PMTCATEGORY", DataValues.newString("3"));
                    detailColumns.add("PURORDERNO", DataValues.newString(""));
                    detailColumns.add("APSUBJECTID", DataValues.newString(apSubjectId));
                    detailColumns.add("INVOICENUMBER", DataValues.newString(""));
                    detailColumns.add("INVOICECODE", DataValues.newString(""));
                    detailColumns.add("INVOICEDATE", DataValues.newDate(""));

                    String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                    DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean detailib=new InsBean("DCP_APPERD",detailColumnNames);
                    detailib.addValues(detailDataValues);
                    this.addProcessData(new DataProcessBean(detailib));
                }

                ColumnDataValue mainColumns=new ColumnDataValue();
                mainColumns.add("EID", DataValues.newString(eId));
                mainColumns.add("STATUS", DataValues.newString("1"));
                mainColumns.add("CORP", DataValues.newString(corp));
                mainColumns.add("ACCOUNTID", DataValues.newString(accountId));
                mainColumns.add("APNO", DataValues.newString(apNo));
                mainColumns.add("PDATE", DataValues.newDate(bDate));
                mainColumns.add("ORGANIZATIONNO", DataValues.newString(apDoc.getOrganizationNo()));
                mainColumns.add("APTYPE", DataValues.newString(apType));
                mainColumns.add("ACCEMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
                mainColumns.add("BIZPARTNERNO", DataValues.newString(apDoc.getBizPartnerNo()));
                mainColumns.add("RECEIVER", DataValues.newString(payee));
                mainColumns.add("TASKID", DataValues.newString("7"));//暂估单
                mainColumns.add("PAYDATENO", DataValues.newString(apDoc.getPayDateNo()));
                mainColumns.add("PAYDUEDATE", DataValues.newDate(payDate));
                mainColumns.add("TAXCODE", DataValues.newString(apDoc.getTaxCode()));
                mainColumns.add("TAXRATE", DataValues.newDecimal(taxRate));
                mainColumns.add("INCLTAX", DataValues.newString(""));
                //mainColumns.add("APPLICANT", DataValues.newString(request.getApplicant()));
                mainColumns.add("EMPLOYEENO", DataValues.newString(""));
                mainColumns.add("DEPARTNO", DataValues.newString(""));
                mainColumns.add("SOURCETYPE", DataValues.newString("5"));//采购单
                mainColumns.add("SOURCENO", DataValues.newString(apDoc.getBillNo()));
                mainColumns.add("PENDOFFSETNO", DataValues.newString(""));
                mainColumns.add("FEESUBJECTID", DataValues.newString(feeSubjectId));
                mainColumns.add("APSUBJECTID", DataValues.newString(apSubjectId));
                mainColumns.add("GLNO", DataValues.newString(""));
                mainColumns.add("GRPPMTNO", DataValues.newString(""));
                mainColumns.add("MEMO", DataValues.newString(""));
                mainColumns.add("payList", DataValues.newString("3"));
                mainColumns.add("CURRENCY", DataValues.newString("CNY"));//todo
                mainColumns.add("EXRATE", DataValues.newDecimal("1"));//todo
                mainColumns.add("FCYBTAMT", DataValues.newDecimal(preTaxAmt));
                mainColumns.add("FCYTAMT", DataValues.newDecimal(taxAmt));
                mainColumns.add("FCYREVAMT", DataValues.newDecimal(""));
                //mainColumns.add("FCYTATAMT", DataValues.newDecimal(request.getFCYTATAmt()));
                mainColumns.add("LCYBTAMT", DataValues.newDecimal(lcyBtAmt));
                mainColumns.add("LCYTAMT", DataValues.newDecimal(lcytAmt));
                mainColumns.add("LCYREVAMT", DataValues.newDecimal(""));
                mainColumns.add("LCYTATAMT", DataValues.newDecimal(lcytAmt));
                mainColumns.add("FCYPMTAMT", DataValues.newDecimal(""));
                mainColumns.add("LCYPMTAMT", DataValues.newDecimal(""));

                mainColumns.add("CREATEBY", DataValues.newString(req.getOpNO()));
                mainColumns.add("CREATE_DATE", DataValues.newString(createDate));
                mainColumns.add("CREATE_TIME", DataValues.newString(createTime));

                String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
                DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib=new InsBean("DCP_APBILL",mainColumnNames);
                ib.addValues(mainDataValues);
                this.addProcessData(new DataProcessBean(ib));

            }
        }



        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ApEstProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ApEstProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ApEstProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ApEstProcessReq req) throws Exception {
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
    protected TypeToken<DCP_ApEstProcessReq> getRequestType() {
        return new TypeToken<DCP_ApEstProcessReq>() {
        };
    }

    @Override
    protected DCP_ApEstProcessRes getResponseType() {
        return new DCP_ApEstProcessRes();
    }

    private String getApNo(DCP_ApEstProcessReq req) throws Exception{
        String apNo = "";
        String preFix="";
        //1：采购应付单，2：预付账款单，3：其他应付单，4 ：内部应付单，5：员工报销单，6：员工借款单，
        // 7：采购暂估单；8：预付待抵单；9：员工借支待抵单，10-应付核销单
        preFix = "APZG";
        apNo=this.getOrderNO(req, preFix);

        return apNo;
    }

}

