package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ApBillProcessReq;
import com.dsc.spos.json.cust.res.DCP_ApBillProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_ApBillProcess extends SPosAdvanceService<DCP_ApBillProcessReq, DCP_ApBillProcessRes> {

    @Override
    protected void processDUID(DCP_ApBillProcessReq req, DCP_ApBillProcessRes res) throws Exception {

        String eId = req.geteId();
        String bdate = req.getRequest().getBDate();
        String corp = req.getRequest().getCorp();
        List<DCP_ApBillProcessReq.NoList> noList = req.getRequest().getNoList();
        String isInvoiceIncl = req.getRequest().getIsInvoiceIncl();
        List<DCP_ApBillProcessReq.SupplierList> supplierList = req.getRequest().getSupplierList();

        String createDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String createTime = new SimpleDateFormat("HHmmss").format(new Date());


        String accSql="select a.*,to_char(a.APCLOSINGDATE,'yyyyMMdd') as apclosingdates  from DCP_ACOUNT_SETTING a where a.eid='"+eId+"' and a.corp='"+corp+"'  and a.ACCTTYPE='1' and a.status='100' ";
        List<Map<String, Object>> accList = this.doQueryData(accSql, null);
        if(accList.size()<=0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "找不到对应的账套");
        }
        //是否根据taxCode 分单
        String isApSplit = accList.get(0).get("ISAPTAXRATESPLIT").toString();

        //是否按采购核销订金=Y
        String isDespositByPurch = accList.get(0).get("ISDESPOSITBYPURCH").toString();

        //  c. 【账套】DCP_ACOUNT_SETTING 应付冲账类型
        //    ⅰ. APPARAMETER=1 冲销至单号；表内wrtOffBillNo 核销来源单号只记录单号；
        //    ⅱ. APPARAMETER=2：冲账至单号+项次表内wrtOffBillNo 核销来源单号，wrtOffBillitem记录项次
        String apParameter = accList.get(0).get("APPARAMETER").toString();

        String accountId = accList.get(0).get("ACCOUNTID").toString();
        String apClosingDate = accList.get(0).get("APCLOSINGDATES").toString();
        if(Integer.valueOf(apClosingDate)<Integer.valueOf(bdate)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据日期不得小于应付关账日期");
        }

        //数据源  含发票否勾选  只产生供应商售票的单据
        //  a. DCP_PURINVRECON 供应商收票，应付单 APNO 字段
        //  b. DCP_RECONLIATION 对账单，应付单 APNO 字段
        String sql="select a.*,b.payee" +
                " from DCP_PURINVRECON a " +
                " left join dcp_bizpartner b on a.eid=b.eid and a.bizpartnerno=b.bizpartnerno " +
                " where a.eid='"+eId+"' and b.corp='"+corp+"' and nvl(a.APNO,'')='' " +
                " ";
        if(CollUtil.isNotEmpty(noList)){
            List<DCP_ApBillProcessReq.NoList> collect = noList.stream().filter(x -> Check.NotNull(x.getPurInvNo())).distinct().collect(Collectors.toList());
            if(CollUtil.isNotEmpty(collect)){
                sql+=" and a.PURINVNO in ("+noList.stream().map(x -> "'"+ x.getPurInvNo()+"'").distinct().collect(Collectors.joining(","))+")";
            }
        }
        if(CollUtil.isNotEmpty(supplierList)){
            sql+=" and a.bizpartnerno in ("+supplierList.stream().map(x -> "'"+ x.getBizPartnerNo()+"'").distinct().collect(Collectors.joining(","))+")";
        }

        //  a. 【单据信息】中科目抓取：涉及表：DCP_APSETUPSUBJECT 应付配置科目；条件：账套+类型=【应付账款】
        //  b. 【存货科目】SETUPITEM=1 对应维护的SUBJECTID
        //  c. 【账款科目】SETUPITEM=6对应维护的SUBJECTID

        String apSubjectSql="select * from DCP_APSETUPSUBJECT a where a.eid='"+eId+"' and a.accountid='"+accountId+"' and a.setuptype='1' ";
        List<Map<String, Object>> apSubjectList = this.doQueryData(apSubjectSql, null);
        String chSubjectId="";
        String zkSubjectId="";
        String czgSubjectId="";
        if(CollUtil.isNotEmpty(apSubjectList)){
            List<Map<String, Object>> chList = apSubjectList.stream().filter(x -> x.get("SETUPITEM").toString().equals("1")).collect(Collectors.toList());
            if(CollUtil.isNotEmpty(chList)){
                chSubjectId = chList.get(0).get("SUBJECTID").toString();
            }
            List<Map<String, Object>> zkList = apSubjectList.stream().filter(x -> x.get("SETUPITEM").toString().equals("6")).collect(Collectors.toList());
            if(CollUtil.isNotEmpty(zkList)){
                zkSubjectId = zkList.get(0).get("SUBJECTID").toString();
            }

            List<Map<String, Object>> czgList = apSubjectList.stream().filter(x -> x.get("SETUPITEM").toString().equals("8")).collect(Collectors.toList());
            if(CollUtil.isNotEmpty(czgList)){
                czgSubjectId = czgList.get(0).get("SUBJECTID").toString();
            }
        }

        //品类配置科目  明细上面的存货科目是品类配置科目上面的  账款科目就是单据上的
        String categorySubjectSql="select * from DCP_CATEGORYSUBJECT a where a.eid='"+eId+"' and a.accountid='"+accountId+"'  ";
        List<Map<String, Object>> categorySubjectList = this.doQueryData(categorySubjectSql, null);

        //税额科目
        String taxSubjectSql="select * from DCP_TAXSUBJECT a where a.eid='"+eId+"' and a.accountid='"+accountId+"' ";
        List<Map<String, Object>> taxSubjectList = this.doQueryData(taxSubjectSql, null);
        if("Y".equals(isInvoiceIncl)) {
            List<Map<String, Object>> sourceList1 = this.doQueryData(sql, null);
            if (sourceList1.size() > 0) {

                String invDetailSql = "select a.*,b.originno,b.originitem,c.departid,b.category,b.isgift " +
                        " from DCP_PURINVDETAIL a " +
                        " left join dcp_sstockin_detail b on a.eid=b.eid and a.sourceno=b.sstockinno and a.sourceitem=b.item " +
                        " left join dcp_sstockin c on a.eid=c.eid and a.sourceno=c.sstockinno " +
                        " where a.eid='" + req.geteId() + "' and a.purinvno in (" + sourceList1.stream().map(x -> "'" + x.get("PURINVNO").toString() + "'").distinct().collect(Collectors.joining(",")) + ") ";
                List<Map<String, Object>> invDetailList = this.doQueryData(invDetailSql, null);

                List<String> purInvNoList = sourceList1.stream().map(x -> x.get("PURINVNO").toString()).distinct().collect(Collectors.toList());


                //应付单暂估单 账期只有一笔
                //用sourceNo 和sourceItem 去查询

                StringBuffer sJoinSourceNo=new StringBuffer("");
                StringBuffer sJoinSourceNoSeq=new StringBuffer("");

                for (Map<String, Object> singleData : invDetailList){
                    String sourceNo  = singleData.get("ORIGINNO").toString().toString();
                    String sourceNoSeq  = singleData.get("ORIGINITEM").toString().toString();

                    sJoinSourceNo.append(sourceNo+",");
                    sJoinSourceNoSeq.append(sourceNoSeq+",");
                }
                Map<String, String> mapSOrder=new HashMap<String, String>();
                mapSOrder.put("SOURCENO", sJoinSourceNo.toString());
                mapSOrder.put("SOURCENOSEQ", sJoinSourceNoSeq.toString());

                MyCommon cm=new MyCommon();
                String withasSql_mono=cm.getFormatSourceMultiColWith(mapSOrder);

                //应付暂估单明细
                List<Map<String, Object>> zgList=new ArrayList<>();

                //应付待抵单
                List<Map<String,Object>> ddList=new ArrayList<>();

                if(withasSql_mono.length()>0){
                    String zgSql="with p as ("+withasSql_mono+") " +
                            " select a.apno,a.sourceno,a.sourceitem,d.FCYTATAMT,d.FCYPMTREVAMT,d.item,d.INSTPMTSEQ" +
                            " " +
                            " from DCP_APBILLDETAIL a" +
                            " inner join p on a.sourceno=p.sourceno and a.sourceitem=p.sourcenoseq " +
                            " inner join DCP_APBILL c on c.eid=a.eid and c.apno=a.apno and c.accountid=a.accountid " +
                            " inner join DCP_APPERD d on d.eid=a.eid and d.apno=a.apno and d.accountid=a.accountid " +
                            " where a.eid='"+eId+"' and c.accountid='"+accountId+"' and " +
                            "  c.aptype in ('01','02','03') and nvl(d.FCYTATAMT,0)-nvl(d.FCYPMTREVAMT,0)>0 ";

                    zgList=this.doQueryData(zgSql,null);

                    //ISDESPOSITBYPURCH
                    String ddSql="with p as ("+withasSql_mono+") " +
                            " select distinct a.apno,d.FCYTATAMT,d.FCYPMTREVAMT,d.item,d.INSTPMTSEQ " +
                            " " +
                            " from DCP_APBILLDETAIL a" +
                            " inner join DCP_APBILL c on c.eid=a.eid and c.apno=a.apno and c.accountid=a.accountid " +
                            " inner join DCP_APPERD d on d.eid=a.eid and d.apno=a.apno and d.accountid=a.accountid " +
                            " where a.eid='"+eId+"' and c.accountid='"+accountId+"' and " +
                            "  c.aptype in ('04') and nvl(d.FCYTATAMT,0)-nvl(d.FCYPMTREVAMT,0)>0 ";
                    if("Y".equals(isDespositByPurch)){
                        ddSql="with p as ("+withasSql_mono+") " +
                                " select distinct a.apno,d.FCYTATAMT,d.FCYPMTREVAMT,d.item,d.INSTPMTSEQ " +
                                " " +
                                " from DCP_APBILLDETAIL a" +
                                " inner join p on a.sourceno=p.sourceno and a.sourceitem=p.sourcenoseq " +
                                " inner join DCP_APBILL c on c.eid=a.eid and c.apno=a.apno and c.accountid=a.accountid " +
                                " inner join DCP_APPERD d on d.eid=a.eid and d.apno=a.apno and d.accountid=a.accountid " +
                                " where a.eid='"+eId+"' and c.accountid='"+accountId+"' and " +
                                "  c.aptype in ('04') and nvl(d.FCYTATAMT,0)-nvl(d.FCYPMTREVAMT,0)>0 ";
                    }
                    ddList=this.doQueryData(ddSql,null);
                }

                List<Map<String, Object>> zgApList=new ArrayList<>();
                if(zgList.size()>0){
                    String yetApNoStr = zgList.stream().map(x -> "'" + x.get("APNO").toString() + "'").distinct().collect(Collectors.joining(","));
                    String yetApSql="select * from dcp_apbill a where a.eid='"+eId+"' and a.accountid='"+accountId+"' " +
                            " and a.apno in ("+yetApNoStr+") ";
                    zgApList=this.doQueryData(yetApSql,null);
                }

                int num = 0;
                List<String> apNos = new ArrayList<>();

                //isApSplit 拆税分单
                List<Map<String,String>> splitList = sourceList1.stream()
                        .map(
                                x -> {
                                    Map<String,String> map=new HashMap<>();
                                    map.put("purInvNo",x.get("PURINVNO").toString());

                                    if("Y".equals(isApSplit)){
                                        map.put("taxCode",x.get("TAXCODE").toString());
                                    }else{
                                        map.put("taxCode","");
                                    }
                                    return map;
                                }
                        ).distinct().collect(Collectors.toList());

                List<DCP_ApBillProcessReq.ReconDetail> apDetailSum=new ArrayList<>();

                for (Map<String,String> purInvMap : splitList){

                    String thisPurInvNo = purInvMap.get("purInvNo");
                    String taxCodeMap = purInvMap.get("taxCode");


                    List<Map<String, Object>> singleSourceList = sourceList1.stream().filter(x -> x.get("PURINVNO").toString().equals(thisPurInvNo)).collect(Collectors.toList());
                    if("Y".equals(isApSplit)){
                        singleSourceList=singleSourceList.stream().filter(x -> x.get("TAXCODE").toString().equals(taxCodeMap)).collect(Collectors.toList());
                    }
                    String apNo = this.getApNo(req);
                    int zgItem=0;

                    Map<String, Object> singleSource = singleSourceList.get(0);
                    String organizationNo = singleSource.get("ORGANIZATIONNO").toString();
                    String bizpartnerNo = singleSource.get("BIZPARTNERNO").toString();
                    String payee = singleSource.get("PAYEE").toString();
                    String payDateNo = singleSource.get("PAYDATENO").toString();
                    String taxCode = singleSource.get("TAXCODE").toString();
                    String taxRate = singleSource.get("TAXRATE").toString();
                    String exRate = singleSource.get("EXRATE").toString();
                    String currency = singleSource.get("CURRENCY").toString();

                    String invfcybtAmt = singleSource.get("INVFCYBTAMT").toString();
                    String invfcytAmt = singleSource.get("INVFCYTAMT").toString();
                    String invfcyatAmt = singleSource.get("INVFCYATAMT").toString();
                    String invlcybtAmt = singleSource.get("INVLCYBTAMT").toString();
                    String invlcytAmt = singleSource.get("INVLCYTAMT").toString();
                    String invlcyatAmt = singleSource.get("INVLCYATAMT").toString();
                    String invoiceCode = singleSource.get("INVOICECODE").toString();
                    String invoiceNumber = singleSource.get("INVOICENUMBER").toString();
                    String invoiceDate = singleSource.get("INVOICEDATE").toString();

                    String payDueDate = getPayDateDueDate(req, req.getRequest().getBDate(), payDateNo);
                    ColumnDataValue mainColumns = new ColumnDataValue();
                    mainColumns.add("EID", DataValues.newString(eId));
                    mainColumns.add("STATUS", DataValues.newString("1"));
                    mainColumns.add("CORP", DataValues.newString(corp));
                    mainColumns.add("ACCOUNTID", DataValues.newString(accountId));
                    mainColumns.add("APNO", DataValues.newString(apNo));
                    mainColumns.add("PDATE", DataValues.newString(bdate));
                    mainColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
                    mainColumns.add("APTYPE", DataValues.newString("07"));//采购应付
                    mainColumns.add("ACCEMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
                    mainColumns.add("BIZPARTNERNO", DataValues.newString(bizpartnerNo));
                    mainColumns.add("RECEIVER", DataValues.newString(payee));
                    mainColumns.add("TASKID", DataValues.newString("1"));
                    mainColumns.add("PAYDATENO", DataValues.newString(payDateNo));
                    mainColumns.add("PAYDUEDATE", DataValues.newString(payDueDate));
                    mainColumns.add("TAXCODE", DataValues.newString(taxCode));
                    mainColumns.add("TAXRATE", DataValues.newDecimal(taxRate));
                    mainColumns.add("INCLTAX", DataValues.newString(""));
                    //mainColumns.add("APPLICANT", DataValues.newString(request.getApplicant()));
                    mainColumns.add("EMPLOYEENO", DataValues.newString(""));
                    mainColumns.add("DEPARTNO", DataValues.newString(""));
                    mainColumns.add("SOURCETYPE", DataValues.newString("1"));
                    mainColumns.add("SOURCENO", DataValues.newString(thisPurInvNo));
                    mainColumns.add("PENDOFFSETNO", DataValues.newString(""));
                    mainColumns.add("FEESUBJECTID", DataValues.newString(chSubjectId));
                    mainColumns.add("APSUBJECTID", DataValues.newString(zkSubjectId));
                    mainColumns.add("GLNO", DataValues.newString(""));
                    mainColumns.add("GRPPMTNO", DataValues.newString(""));
                    mainColumns.add("MEMO", DataValues.newString(""));
                    mainColumns.add("payList", DataValues.newString("3"));
                    mainColumns.add("CURRENCY", DataValues.newString(currency));
                    mainColumns.add("EXRATE", DataValues.newDecimal(exRate));
                    mainColumns.add("FCYBTAMT", DataValues.newDecimal(invfcybtAmt));
                    mainColumns.add("FCYTAMT", DataValues.newDecimal(invfcytAmt));
                    mainColumns.add("FCYREVAMT", DataValues.newDecimal(0));
                    mainColumns.add("FCYTATAMT", DataValues.newDecimal(invfcyatAmt));
                    mainColumns.add("LCYBTAMT", DataValues.newDecimal(invlcybtAmt));
                    mainColumns.add("LCYTAMT", DataValues.newDecimal(invlcytAmt));
                    mainColumns.add("LCYREVAMT", DataValues.newDecimal("0"));
                    mainColumns.add("LCYTATAMT", DataValues.newDecimal(invlcyatAmt));
                    mainColumns.add("FCYPMTAMT", DataValues.newDecimal(""));
                    mainColumns.add("LCYPMTAMT", DataValues.newDecimal(""));

                    mainColumns.add("CREATEBY", DataValues.newString(req.getOpNO()));
                    mainColumns.add("CREATE_DATE", DataValues.newString(createDate));
                    mainColumns.add("CREATE_TIME", DataValues.newString(createTime));

                    String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
                    DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean ib = new InsBean("DCP_APBILL", mainColumnNames);
                    ib.addValues(mainDataValues);
                    this.addProcessData(new DataProcessBean(ib));
                    Map<String, DCP_ApBillProcessReq.czgList> zgAmtMap=new HashMap<>();
                    List<Map<String, Object>> detailList = invDetailList.stream().filter(x -> x.get("PURINVNO").toString().equals(thisPurInvNo)).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(detailList)) {
                        int item = 0;
                        for (Map<String, Object> singleDetail : detailList) {

                            String sourceNo = singleDetail.get("SOURCENO").toString();
                            String sourceItem = singleDetail.get("SOURCEITEM").toString();
                            String sourceOrg = singleDetail.get("SOURCEORG").toString();
                            String pluNo = singleDetail.get("PLUNO").toString();
                            String spec = singleDetail.get("SPEC").toString();
                            String priceUnit = singleDetail.get("PRICEUNIT").toString();
                            String qty = singleDetail.get("QTY").toString();
                            String billPrice = singleDetail.get("BILLPRICE").toString();
                            String fee = singleDetail.get("FEE").toString();
                            String originNo = singleDetail.get("ORIGINNO").toString();
                            String originItem = singleDetail.get("ORIGINITEM").toString();
                            String departId = singleDetail.get("DEPARTID").toString();
                            String category = singleDetail.get("CATEGORY").toString();
                            String isGift = singleDetail.get("ISGIFT").toString();
                            String direction = singleDetail.get("DIRECTION").toString();

                            String invSubject = "";
                            if (CollUtil.isNotEmpty(categorySubjectList)) {
                                List<Map<String, Object>> categoryFilter = categorySubjectList.stream().filter(x -> x.get("CATEGORY").toString().equals(category)).collect(Collectors.toList());
                                if (categoryFilter.size() > 0) {
                                    invSubject = categoryFilter.get(0).get("INVSUBJECT").toString();
                                }
                            }
                            String taxSubject = "";
                            if (CollUtil.isNotEmpty(taxSubjectList)) {
                                List<Map<String, Object>> taxSubjectFilter = taxSubjectList.stream().filter(x -> x.get("TAXCODE").toString().equals(taxCode) && x.get("SETUPTYPE").toString().equals("1")).collect(Collectors.toList());
                                if (taxSubjectFilter.size() > 0) {
                                    taxSubject = taxSubjectFilter.get(0).get("SUBJECTID").toString();
                                }
                            }

                            String fcybtAmt = singleDetail.get("FCYBTAMT").toString();
                            String fcytAmt = singleDetail.get("FCYTAMT").toString();
                            String fcyatAmt = singleDetail.get("FCYATAMT").toString();
                            String invcrncybtAmt = singleDetail.get("INVCRNCYBTAMT").toString();
                            String invcrncytAmt = singleDetail.get("INVCRNCYTAMT").toString();
                            String invcrncyatAmt = singleDetail.get("INVCRNCYATAMT").toString();

                            String isRevese="N";
                            if(zgList.size()>0){
                                List<Map<String, Object>> filterRows = zgList.stream().filter(x -> x.get("SOURCENO").toString().equals(sourceNo) && x.get("SOURCEITEM").toString().equals(sourceItem)).collect(Collectors.toList());
                                if(filterRows.size()>0){
                                    isRevese="Y";
                                    String yetApNo = filterRows.get(0).get("APNO").toString();
                                    BigDecimal yetFcyTatAmt = new BigDecimal(filterRows.get(0).get("FCYTATAMT").toString());
                                    BigDecimal yetPmtRevAmt = new BigDecimal(filterRows.get(0).get("FCYPMTREVAMT").toString());
                                    BigDecimal subtract = yetFcyTatAmt.subtract(yetPmtRevAmt);
                                    if(zgAmtMap.containsKey(yetApNo)){
                                        DCP_ApBillProcessReq.czgList czgList = zgAmtMap.get(yetApNo);
                                        BigDecimal add1 = czgList.getFcyBtAmt().add(new BigDecimal(fcybtAmt));
                                        BigDecimal add2 = czgList.getFcyBtAmt().add(new BigDecimal(invcrncybtAmt));
                                        czgList.setFcyBtAmt(add1);
                                        czgList.setLcyBtAmt(add2);
                                        zgAmtMap.put(yetApNo,czgList);

                                    }else{

                                        DCP_ApBillProcessReq.czgList czgList = req.new czgList();
                                        czgList.setApNo(yetApNo);
                                        czgList.setFcyBtAmt(new BigDecimal(fcybtAmt));
                                        czgList.setLcyBtAmt(new BigDecimal(invcrncybtAmt));
                                        zgAmtMap.put(yetApNo,czgList);
                                    }
                                    //金额比较
                                    if(subtract.compareTo(new BigDecimal(fcyatAmt))!=0){
                                        //写入一笔到冲暂估明细
                                        BigDecimal yetSubtract = subtract.subtract(new BigDecimal(fcyatAmt));

                                        List<Map<String, Object>> yetApList = zgApList.stream().filter(x -> x.get("APNO").toString().equals(yetApNo)).collect(Collectors.toList());
                                        if(yetApList.size()>0) {

                                            BigDecimal yetExRate = new BigDecimal(yetApList.get(0).get("EXRATE").toString());
                                            BigDecimal yetFcyBtAmt = new BigDecimal(yetApList.get(0).get("FCYBTAMT").toString());
                                            BigDecimal yetLcyBtAmt = new BigDecimal(yetApList.get(0).get("LCYBTAMT").toString());
                                            //BigDecimal subtractLcyBtAmt = lcyBtAmt.subtract(yetLcyBtAmt);
                                            //BigDecimal subtractFcyBtAmt = fcyBtAmt.subtract(yetFcyBtAmt);
                                            //BigDecimal multiplyLcyBtAmt = subtractLcyBtAmt.multiply(yetExRate);
                                            zgItem++;
                                            //写入一笔数据到冲暂估明细
                                            ColumnDataValue czgColumns = new ColumnDataValue();
                                            czgColumns.add("EID", DataValues.newString(req.geteId()));
                                            czgColumns.add("ACCOUNTID", DataValues.newString(accountId));
                                            czgColumns.add("SOURCEORG", DataValues.newString(organizationNo));
                                            czgColumns.add("APNO", DataValues.newString(apNo));
                                            czgColumns.add("ITEM", DataValues.newString(zgItem));
                                            czgColumns.add("ITEM2", DataValues.newString(""));
                                            czgColumns.add("TASKID", DataValues.newString("7"));//采购暂估单
                                            czgColumns.add("WRTOFFTYPE", DataValues.newString("02"));
                                            czgColumns.add("WRTOFFQTY", DataValues.newString(""));
                                            czgColumns.add("ESTBILLNO", DataValues.newString("DIFF"));
                                            czgColumns.add("ESTBILLITEM", DataValues.newString(""));
                                            czgColumns.add("PERIOD", DataValues.newString("0"));
                                            czgColumns.add("TAXRATE", DataValues.newString(yetApList.get(0).get("TAXRATE").toString()));
                                            czgColumns.add("WRTOFFAPSUBJECT", DataValues.newString(yetApList.get(0).get("APSUBJECTID").toString()));
                                            czgColumns.add("WRTOFFBTAXSUBJECT", DataValues.newString(""));
                                            czgColumns.add("WRTOFFTAXSUBJECT", DataValues.newString(""));
                                            czgColumns.add("WRTOFFPRCDIFFSUBJECT", DataValues.newString(czgSubjectId));
                                            czgColumns.add("WRTOFFEXDIFFSUBJECT", DataValues.newString(""));
                                            czgColumns.add("DEPARTNO", DataValues.newString(yetApList.get(0).get("DEPARTNO").toString()));
                                            czgColumns.add("TRADECUSTOMER", DataValues.newString(yetApList.get(0).get("TRADECUSTOMER").toString()));
                                            czgColumns.add("PMTCUSTOMER", DataValues.newString(yetApList.get(0).get("PMTCUSTOMER").toString()));
                                            czgColumns.add("CATEGORY", DataValues.newString(""));
                                            czgColumns.add("EMPLOYEENO", DataValues.newString(yetApList.get(0).get("EMPLOYEENO").toString()));
                                            czgColumns.add("FREECHARS1", DataValues.newString(yetApList.get(0).get("FREECHARS1").toString()));
                                            czgColumns.add("FREECHARS2", DataValues.newString(yetApList.get(0).get("FREECHARS2").toString()));
                                            czgColumns.add("FREECHARS3", DataValues.newString(yetApList.get(0).get("FREECHARS3").toString()));
                                            czgColumns.add("FREECHARS4", DataValues.newString(yetApList.get(0).get("FREECHARS4").toString()));
                                            czgColumns.add("FREECHARS5", DataValues.newString(yetApList.get(0).get("FREECHARS5").toString()));
                                            czgColumns.add("MEMO", DataValues.newString(""));
                                            czgColumns.add("FCYBILLPRICE", DataValues.newString(""));
                                            czgColumns.add("EXRATE", DataValues.newString(yetApList.get(0).get("EXRATE").toString()));
                                            czgColumns.add("FCYBTAMT", DataValues.newString(yetApList.get(0).get("FCYBTAMT").toString()));
                                            czgColumns.add("FCYTAMT", DataValues.newString(yetApList.get(0).get("FCYTAMT").toString()));
                                            czgColumns.add("FCYTATAMT", DataValues.newString(yetApList.get(0).get("FCYTATAMT").toString()));
                                            czgColumns.add("FCYWRTOFFDIFFAMT", DataValues.newString(yetSubtract));
                                            czgColumns.add("LCYBILLPRICE", DataValues.newString(""));
                                            czgColumns.add("LCYBTAMT", DataValues.newString(yetApList.get(0).get("LCYBTAMT").toString()));
                                            czgColumns.add("LCYTAMT", DataValues.newString(yetApList.get(0).get("LCYTAMT").toString()));
                                            czgColumns.add("LCYTATAMT", DataValues.newString(yetApList.get(0).get("LCYTATAMT").toString()));
                                            czgColumns.add("LCYPRCDIFFAMT", DataValues.newString(yetSubtract));
                                            czgColumns.add("LCYEXDIFFAMT", DataValues.newString(yetSubtract));

                                            String[] czgColumnNames = czgColumns.getColumns().toArray(new String[0]);
                                            DataValue[] czgDataValues = czgColumns.getDataValues().toArray(new DataValue[0]);
                                            InsBean czgib = new InsBean("DCP_APBILLESTDTL", czgColumnNames);
                                            czgib.addValues(czgDataValues);
                                            this.addProcessData(new DataProcessBean(czgib));
                                        }


                                    }

                                }
                            }


                            item++;
                            ColumnDataValue detailColumns = new ColumnDataValue();
                            detailColumns.add("EID", DataValues.newString(eId));
                            detailColumns.add("APNO", DataValues.newString(apNo));
                            detailColumns.add("ITEM", DataValues.newString(item));
                            detailColumns.add("BIZPARTNERNO", DataValues.newString(bizpartnerNo));
                            detailColumns.add("RECEIVER", DataValues.newString(payee));
                            detailColumns.add("SOURCETYPE", DataValues.newString("1"));
                            detailColumns.add("SOURCENO", DataValues.newString(sourceNo));
                            detailColumns.add("SOURCEITEM", DataValues.newString(sourceItem));
                            detailColumns.add("SOURCEORG", DataValues.newString(sourceOrg));
                            detailColumns.add("PLUNO", DataValues.newString(pluNo));
                            detailColumns.add("SPEC", DataValues.newString(spec));
                            detailColumns.add("PRICEUNIT", DataValues.newString(priceUnit));
                            detailColumns.add("QTY", DataValues.newDecimal(qty));
                            detailColumns.add("BILLPRICE", DataValues.newDecimal(billPrice));
                            detailColumns.add("FEE", DataValues.newDecimal(fee));
                            detailColumns.add("OOFNO", DataValues.newString(originNo));
                            detailColumns.add("OOITEM", DataValues.newString(originItem));
                            detailColumns.add("DEPARTNO", DataValues.newString(departId));
                            detailColumns.add("CATEGORY", DataValues.newString(category));
                            detailColumns.add("ISGIFT", DataValues.newString(isGift));
                            detailColumns.add("BSNO", DataValues.newString(""));
                            detailColumns.add("TAXRATE", DataValues.newDecimal(taxRate));
                            detailColumns.add("FEESUBJECTID", DataValues.newString(invSubject));
                            detailColumns.add("APSUBJECTID", DataValues.newString(chSubjectId));
                            detailColumns.add("TAXSUBJECTID", DataValues.newString(taxSubject));
                            detailColumns.add("DIRECTION", DataValues.newString(direction));
                            detailColumns.add("ISREVEST", DataValues.newString(isRevese));
                            detailColumns.add("PAYDATENO", DataValues.newString(payDateNo));
                            detailColumns.add("EMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
                            detailColumns.add("MEMO", DataValues.newString(""));
                            detailColumns.add("CURRENCY", DataValues.newString(currency));
                            detailColumns.add("FCYPRICE", DataValues.newDecimal(billPrice));
                            detailColumns.add("EXRATE", DataValues.newDecimal(exRate));
                            detailColumns.add("FCYBTAMT", DataValues.newDecimal(fcybtAmt));
                            detailColumns.add("FCYTAMT", DataValues.newDecimal(fcytAmt));
                            detailColumns.add("FCYTATAMT", DataValues.newDecimal(fcyatAmt));
                            detailColumns.add("FCYSTDCOSTAMT", DataValues.newDecimal(0));
                            detailColumns.add("FCYACTCOSTAMT", DataValues.newDecimal(0));

                            BigDecimal multiply = new BigDecimal(billPrice).multiply(new BigDecimal(exRate));

                            detailColumns.add("LCYPRICE", DataValues.newDecimal(multiply));
                            detailColumns.add("LCYBTAMT", DataValues.newDecimal(invcrncybtAmt));
                            detailColumns.add("LCYTAMT", DataValues.newDecimal(invcrncytAmt));
                            detailColumns.add("LCYTATAMT", DataValues.newDecimal(invcrncyatAmt));
                            detailColumns.add("LCYSTDCOSTAMT", DataValues.newDecimal(0));
                            detailColumns.add("LCYACTCOSTAMT", DataValues.newDecimal(0));
                            detailColumns.add("PURORDERNO", DataValues.newString(""));


                            String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                            DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                            InsBean detailib = new InsBean("DCP_APBILLDETAIL", detailColumnNames);
                            detailib.addValues(detailDataValues);
                            this.addProcessData(new DataProcessBean(detailib));


                            if("1".equals(apParameter)){//核销至单号
                                if(CollUtil.isNotEmpty(ddList)){
                                    BigDecimal hxAmt = new BigDecimal(fcyatAmt);

                                    List<String> apNos1 = ddList.stream().map(x -> x.get("APNO").toString()).distinct().collect(Collectors.toList());
                                    for (String wrApNo : apNos1){
                                        List<Map<String, Object>> filterRows = ddList.stream().filter(x -> x.get("APNO").toString().equals(wrApNo)).collect(Collectors.toList());

                                        for (Map<String, Object> singleDd : filterRows){
                                            BigDecimal fcytatAmtdd = new BigDecimal(singleDd.get("FCYTATAMT").toString());
                                            BigDecimal fcypmtrevAmtdd = new BigDecimal(singleDd.get("FCYPMTREVAMT").toString());

                                            BigDecimal subtract = fcytatAmtdd.subtract(fcypmtrevAmtdd);//剩余可核销的金额
                                            if(subtract.compareTo(BigDecimal.ZERO)<=0){
                                                continue;
                                            }
                                            if(hxAmt.compareTo(BigDecimal.ZERO)<=0){
                                                break;
                                            }

                                            BigDecimal thisHxAmt=BigDecimal.ZERO;
                                            if(hxAmt.compareTo(subtract)>0){
                                                hxAmt=hxAmt.subtract(subtract);
                                                thisHxAmt=subtract;
                                            }else{
                                                hxAmt = new BigDecimal(0);
                                                thisHxAmt=hxAmt;
                                            }

                                            //thishxAmt 加到FCYPMTREVAMT
                                            BigDecimal add = thisHxAmt.add(fcypmtrevAmtdd);
                                            UptBean ub1 = new UptBean("DCP_APPERD");
                                            //add Value
                                            ub1.addUpdateValue("FCYPMTREVAMT", new DataValue(add, Types.VARCHAR));

                                            //condition
                                            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                            ub1.addCondition("ACCOUNTID", new DataValue(accountId, Types.VARCHAR));
                                            ub1.addCondition("ITEM", new DataValue(singleDd.get("ITEM").toString(), Types.VARCHAR));
                                            ub1.addCondition("APNO", new DataValue(singleDd.get("APNO").toString(), Types.VARCHAR));
                                            this.addProcessData(new DataProcessBean(ub1));



                                        }
                                        ColumnDataValue writoffColumns = new ColumnDataValue();
                                        writoffColumns.add("EID", DataValues.newString(req.geteId()));
                                        writoffColumns.add("ACCOUNTID", DataValues.newString(accountId));
                                        writoffColumns.add("CORP", DataValues.newString(corp));
                                        writoffColumns.add("SOURCEORG", DataValues.newString(sourceOrg));
                                        writoffColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
                                        writoffColumns.add("WRTOFFNO", DataValues.newString(wrApNo));
                                        writoffColumns.add("ITEM", DataValues.newString("1"));
                                        writoffColumns.add("TASKID", DataValues.newString("1"));
                                        writoffColumns.add("WRTOFFTYPE", DataValues.newString("10"));
                                        writoffColumns.add("WRTOFFBILLNO", DataValues.newString(apNo));
                                        writoffColumns.add("WRTOFFBILLITEM", DataValues.newString(""));
                                        writoffColumns.add("INSTPMTSEQ", DataValues.newString(""));
                                        writoffColumns.add("MEMO", DataValues.newString(""));
                                        writoffColumns.add("BSNO", DataValues.newString(""));
                                        writoffColumns.add("WRTOFFDIRECTION", DataValues.newString("1"));
                                        writoffColumns.add("APSUBJECTID", DataValues.newString(chSubjectId));
                                        writoffColumns.add("EMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
                                        writoffColumns.add("DEPARTNO", DataValues.newString(req.getDepartmentNo()));
                                        writoffColumns.add("CATEGORY", DataValues.newString(category));
                                        writoffColumns.add("SECREFNO", DataValues.newString(originNo));
                                        writoffColumns.add("GLNO", DataValues.newString(""));
                                        writoffColumns.add("PAYDUEDATE", DataValues.newString(payDueDate));
                                        writoffColumns.add("BIZPARTNERNO", DataValues.newString(bizpartnerNo));
                                        writoffColumns.add("RECEIVER", DataValues.newString(payee));
                                        writoffColumns.add("FREECHARS1", DataValues.newString("0"));
                                        writoffColumns.add("FREECHARS2", DataValues.newString("0"));
                                        writoffColumns.add("FREECHARS3", DataValues.newString("0"));
                                        writoffColumns.add("FREECHARS4", DataValues.newString("0"));
                                        writoffColumns.add("FREECHARS5", DataValues.newString("0"));
                                        writoffColumns.add("CURRENCY", DataValues.newString(currency));
                                        writoffColumns.add("EXRATE", DataValues.newDecimal(exRate));
                                        writoffColumns.add("FCYREVAMT", DataValues.newDecimal(fcybtAmt));
                                        writoffColumns.add("LCYREVAMT", DataValues.newDecimal(fcybtAmt));
                                        writoffColumns.add("FCYBTAXWRTOFFAMT", DataValues.newDecimal(fcybtAmt));
                                        writoffColumns.add("LCYBTAXWRTOFFAMT", DataValues.newDecimal(fcybtAmt));
                                        writoffColumns.add("INVOICENUMBER", DataValues.newString(""));
                                        writoffColumns.add("INVOICECODE", DataValues.newString(""));

                                        String[] writoffColumnNames = writoffColumns.getColumns().toArray(new String[0]);
                                        DataValue[] writoffDataValues = writoffColumns.getDataValues().toArray(new DataValue[0]);
                                        InsBean writoffib = new InsBean("DCP_APBILLWRTOFF", writoffColumnNames);
                                        writoffib.addValues(writoffDataValues);
                                        this.addProcessData(new DataProcessBean(writoffib));


                                    }


                                }

                            }
                            else{
                                if(CollUtil.isNotEmpty(ddList)){
                                    BigDecimal hxAmt = new BigDecimal(fcyatAmt);
                                    for (Map<String, Object> singleDd : ddList){
                                        BigDecimal fcytatAmtdd = new BigDecimal(singleDd.get("FCYTATAMT").toString());
                                        BigDecimal fcypmtrevAmtdd = new BigDecimal(singleDd.get("FCYPMTREVAMT").toString());

                                        BigDecimal subtract = fcytatAmtdd.subtract(fcypmtrevAmtdd);//剩余可核销的金额
                                        if(subtract.compareTo(BigDecimal.ZERO)<=0){
                                            continue;
                                        }
                                        if(hxAmt.compareTo(BigDecimal.ZERO)<=0){
                                            break;
                                        }

                                        BigDecimal thisHxAmt=BigDecimal.ZERO;
                                        if(hxAmt.compareTo(subtract)>0){
                                            hxAmt=hxAmt.subtract(subtract);
                                            thisHxAmt=subtract;
                                        }else{
                                            hxAmt = new BigDecimal(0);
                                            thisHxAmt=hxAmt;
                                        }

                                        //thishxAmt 加到FCYPMTREVAMT
                                        BigDecimal add = thisHxAmt.add(fcypmtrevAmtdd);
                                        UptBean ub1 = new UptBean("DCP_APPERD");
                                        //add Value
                                        ub1.addUpdateValue("FCYPMTREVAMT", new DataValue(add, Types.VARCHAR));

                                        //condition
                                        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                        ub1.addCondition("ACCOUNTID", new DataValue(accountId, Types.VARCHAR));
                                        ub1.addCondition("ITEM", new DataValue(singleDd.get("ITEM").toString(), Types.VARCHAR));
                                        ub1.addCondition("APNO", new DataValue(singleDd.get("APNO").toString(), Types.VARCHAR));
                                        this.addProcessData(new DataProcessBean(ub1));

                                        //记录
                                        ColumnDataValue writoffColumns = new ColumnDataValue();
                                        writoffColumns.add("EID", DataValues.newString(req.geteId()));
                                        writoffColumns.add("ACCOUNTID", DataValues.newString(accountId));
                                        writoffColumns.add("CORP", DataValues.newString(corp));
                                        writoffColumns.add("SOURCEORG", DataValues.newString(sourceOrg));
                                        writoffColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
                                        writoffColumns.add("WRTOFFNO", DataValues.newString(singleDd.get("APNO").toString()));
                                        writoffColumns.add("ITEM", DataValues.newString("1"));
                                        writoffColumns.add("TASKID", DataValues.newString("1"));
                                        writoffColumns.add("WRTOFFTYPE", DataValues.newString("10"));
                                        writoffColumns.add("WRTOFFBILLNO", DataValues.newString(apNo));
                                        writoffColumns.add("WRTOFFBILLITEM", DataValues.newString(singleDd.get("ITEM").toString()));
                                        writoffColumns.add("INSTPMTSEQ", DataValues.newString(""));
                                        writoffColumns.add("MEMO", DataValues.newString(""));
                                        writoffColumns.add("BSNO", DataValues.newString(""));
                                        writoffColumns.add("WRTOFFDIRECTION", DataValues.newString("1"));
                                        writoffColumns.add("APSUBJECTID", DataValues.newString(chSubjectId));
                                        writoffColumns.add("EMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
                                        writoffColumns.add("DEPARTNO", DataValues.newString(req.getDepartmentNo()));
                                        writoffColumns.add("CATEGORY", DataValues.newString(category));
                                        writoffColumns.add("SECREFNO", DataValues.newString(originNo));
                                        writoffColumns.add("GLNO", DataValues.newString(""));
                                        writoffColumns.add("PAYDUEDATE", DataValues.newString(payDueDate));
                                        writoffColumns.add("BIZPARTNERNO", DataValues.newString(bizpartnerNo));
                                        writoffColumns.add("RECEIVER", DataValues.newString(payee));
                                        writoffColumns.add("FREECHARS1", DataValues.newString("0"));
                                        writoffColumns.add("FREECHARS2", DataValues.newString("0"));
                                        writoffColumns.add("FREECHARS3", DataValues.newString("0"));
                                        writoffColumns.add("FREECHARS4", DataValues.newString("0"));
                                        writoffColumns.add("FREECHARS5", DataValues.newString("0"));
                                        writoffColumns.add("CURRENCY", DataValues.newString(currency));
                                        writoffColumns.add("EXRATE", DataValues.newDecimal(exRate));
                                        writoffColumns.add("FCYREVAMT", DataValues.newDecimal(thisHxAmt));
                                        writoffColumns.add("LCYREVAMT", DataValues.newDecimal(thisHxAmt));
                                        writoffColumns.add("FCYBTAXWRTOFFAMT", DataValues.newDecimal(thisHxAmt));
                                        writoffColumns.add("LCYBTAXWRTOFFAMT", DataValues.newDecimal(thisHxAmt));
                                        writoffColumns.add("INVOICENUMBER", DataValues.newString(""));
                                        writoffColumns.add("INVOICECODE", DataValues.newString(""));

                                        String[] writoffColumnNames = writoffColumns.getColumns().toArray(new String[0]);
                                        DataValue[] writoffDataValues = writoffColumns.getDataValues().toArray(new DataValue[0]);
                                        InsBean writoffib = new InsBean("DCP_APBILLWRTOFF", writoffColumnNames);
                                        writoffib.addValues(writoffDataValues);
                                        this.addProcessData(new DataProcessBean(writoffib));


                                    }

                                }

                            }


                        }
                    }

                    //冲暂估明细 一个暂估单只有一个账期 所以理论上只有一个冲暂估
                    if(zgAmtMap.size()>0){
                        for (Map.Entry<String, DCP_ApBillProcessReq.czgList> entry : zgAmtMap.entrySet()) {
                            DCP_ApBillProcessReq.czgList zgInfo = entry.getValue();
                            String yetApNo = zgInfo.getApNo();
                            //这两个是该笔应付单根据yetApNo汇总的
                            BigDecimal lcyBtAmt = zgInfo.getLcyBtAmt();
                            BigDecimal fcyBtAmt = zgInfo.getFcyBtAmt();

                            List<Map<String, Object>> yetApList = zgApList.stream().filter(x -> x.get("APNO").toString().equals(yetApNo)).collect(Collectors.toList());
                            if(yetApList.size()>0) {

                                BigDecimal yetExRate = new BigDecimal(yetApList.get(0).get("EXRATE").toString());
                                BigDecimal yetFcyBtAmt = new BigDecimal(yetApList.get(0).get("FCYBTAMT").toString());
                                BigDecimal yetLcyBtAmt = new BigDecimal(yetApList.get(0).get("LCYBTAMT").toString());
                                BigDecimal subtractLcyBtAmt = lcyBtAmt.subtract(yetLcyBtAmt);
                                BigDecimal subtractFcyBtAmt = fcyBtAmt.subtract(yetFcyBtAmt);
                                BigDecimal multiplyLcyBtAmt = subtractLcyBtAmt.multiply(yetExRate);
                                zgItem++;
                                //写入一笔数据到冲暂估明细
                                ColumnDataValue czgColumns = new ColumnDataValue();
                                czgColumns.add("EID", DataValues.newString(req.geteId()));
                                czgColumns.add("ACCOUNTID", DataValues.newString(accountId));
                                czgColumns.add("SOURCEORG", DataValues.newString(organizationNo));
                                czgColumns.add("APNO", DataValues.newString(apNo));
                                czgColumns.add("ITEM", DataValues.newString(zgItem));
                                czgColumns.add("ITEM2", DataValues.newString(""));
                                czgColumns.add("TASKID", DataValues.newString("7"));//采购暂估单
                                czgColumns.add("WRTOFFTYPE", DataValues.newString("01"));
                                czgColumns.add("WRTOFFQTY", DataValues.newString(""));
                                czgColumns.add("ESTBILLNO", DataValues.newString(yetApNo));
                                czgColumns.add("ESTBILLITEM", DataValues.newString(""));
                                czgColumns.add("PERIOD", DataValues.newString(""));//todo
                                czgColumns.add("TAXRATE", DataValues.newString(yetApList.get(0).get("TAXRATE").toString()));
                                czgColumns.add("WRTOFFAPSUBJECT", DataValues.newString(yetApList.get(0).get("APSUBJECTID").toString()));
                                czgColumns.add("WRTOFFBTAXSUBJECT", DataValues.newString(""));
                                czgColumns.add("WRTOFFTAXSUBJECT", DataValues.newString(""));
                                czgColumns.add("WRTOFFPRCDIFFSUBJECT", DataValues.newString(czgSubjectId));
                                czgColumns.add("WRTOFFEXDIFFSUBJECT", DataValues.newString(""));
                                czgColumns.add("DEPARTNO", DataValues.newString(yetApList.get(0).get("DEPARTNO").toString()));
                                czgColumns.add("TRADECUSTOMER", DataValues.newString(yetApList.get(0).get("TRADECUSTOMER").toString()));
                                czgColumns.add("PMTCUSTOMER", DataValues.newString(yetApList.get(0).get("PMTCUSTOMER").toString()));
                                czgColumns.add("CATEGORY", DataValues.newString(""));
                                czgColumns.add("EMPLOYEENO", DataValues.newString(yetApList.get(0).get("EMPLOYEENO").toString()));
                                czgColumns.add("FREECHARS1", DataValues.newString(yetApList.get(0).get("FREECHARS1").toString()));
                                czgColumns.add("FREECHARS2", DataValues.newString(yetApList.get(0).get("FREECHARS2").toString()));
                                czgColumns.add("FREECHARS3", DataValues.newString(yetApList.get(0).get("FREECHARS3").toString()));
                                czgColumns.add("FREECHARS4", DataValues.newString(yetApList.get(0).get("FREECHARS4").toString()));
                                czgColumns.add("FREECHARS5", DataValues.newString(yetApList.get(0).get("FREECHARS5").toString()));
                                czgColumns.add("MEMO", DataValues.newString(""));
                                czgColumns.add("FCYBILLPRICE", DataValues.newString(""));
                                czgColumns.add("EXRATE", DataValues.newString(yetApList.get(0).get("EXRATE").toString()));
                                czgColumns.add("FCYBTAMT", DataValues.newString(yetApList.get(0).get("FCYBTAMT").toString()));
                                czgColumns.add("FCYTAMT", DataValues.newString(yetApList.get(0).get("FCYTAMT").toString()));
                                czgColumns.add("FCYTATAMT", DataValues.newString(yetApList.get(0).get("FCYTATAMT").toString()));
                                czgColumns.add("FCYWRTOFFDIFFAMT", DataValues.newString(subtractFcyBtAmt));
                                czgColumns.add("LCYBILLPRICE", DataValues.newString(""));
                                czgColumns.add("LCYBTAMT", DataValues.newString(yetApList.get(0).get("LCYBTAMT").toString()));
                                czgColumns.add("LCYTAMT", DataValues.newString(yetApList.get(0).get("LCYTAMT").toString()));
                                czgColumns.add("LCYTATAMT", DataValues.newString(yetApList.get(0).get("LCYTATAMT").toString()));
                                czgColumns.add("LCYPRCDIFFAMT", DataValues.newString(subtractLcyBtAmt));
                                czgColumns.add("LCYEXDIFFAMT", DataValues.newString(multiplyLcyBtAmt));

                                String[] czgColumnNames = czgColumns.getColumns().toArray(new String[0]);
                                DataValue[] czgDataValues = czgColumns.getDataValues().toArray(new DataValue[0]);
                                InsBean czgib = new InsBean("DCP_APBILLESTDTL", czgColumnNames);
                                czgib.addValues(czgDataValues);
                                this.addProcessData(new DataProcessBean(czgib));
                            }

                        }
                    }


                    List<String> sourceOrgList = detailList.stream().map(x -> x.get("SOURCEORG").toString()).distinct().collect(Collectors.toList());
                    int predItem = 0;
                    for (String sourceOrg : sourceOrgList) {
                        List<Map<String, Object>> filterRows = detailList.stream().filter(x -> x.get("SOURCEORG").toString().equals(sourceOrg)).collect(Collectors.toList());
                        predItem += 1;

                        //BigDecimal fcyTatAmt=new BigDecimal(0);
                        BigDecimal lcyTatAmt = new BigDecimal(0);
                        for (Map<String, Object> sourceOrgRow : filterRows) {

                            BigDecimal invcrncyatamt = new BigDecimal(sourceOrgRow.get("INVCRNCYATAMT").toString());
                            String detailDirection = sourceOrgRow.get("DIRECTION").toString();
                            if (detailDirection.equals("-1")) {
                                invcrncyatamt = invcrncyatamt.multiply(BigDecimal.ONE.negate());
                            }
                            lcyTatAmt = lcyTatAmt.add(invcrncyatamt);
                        }

                        int direction = 1;
                        if (lcyTatAmt.compareTo(BigDecimal.ZERO) < 0) {
                            direction = -1;
                        }

                        //apperd  根据sourceOrg 分多个账期
                        ColumnDataValue detailColumns = new ColumnDataValue();
                        detailColumns.add("EID", DataValues.newString(req.geteId()));
                        detailColumns.add("ACCOUNTID", DataValues.newString(accountId));
                        detailColumns.add("CORP", DataValues.newString(corp));
                        detailColumns.add("SOURCEORG", DataValues.newString(sourceOrg));
                        detailColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
                        detailColumns.add("APNO", DataValues.newString(apNo));
                        detailColumns.add("ITEM", DataValues.newString(predItem));
                        detailColumns.add("INSTPMTSEQ", DataValues.newString(predItem));
                        detailColumns.add("PAYTYPE", DataValues.newString("10"));
                        detailColumns.add("PAYDUEDATE", DataValues.newString(payDueDate));
                        detailColumns.add("BILLDUEDATE", DataValues.newString(payDueDate));
                        detailColumns.add("DIRECTION", DataValues.newString(direction));
                        detailColumns.add("FCYREQAMT", DataValues.newDecimal("0"));
                        detailColumns.add("CURRENCY", DataValues.newString(currency));
                        detailColumns.add("EXRATE", DataValues.newDecimal(exRate));
                        detailColumns.add("FCYREVSEDRATE", DataValues.newDecimal("0"));
                        detailColumns.add("FCYTATAMT", DataValues.newDecimal(lcyTatAmt));
                        detailColumns.add("FCYPMTREVAMT", DataValues.newDecimal("0"));
                        detailColumns.add("REVALADJNUM", DataValues.newString("0"));
                        detailColumns.add("LCYTATAMT", DataValues.newDecimal(lcyTatAmt));
                        detailColumns.add("LCYPMTREVAMT", DataValues.newDecimal(""));
                        detailColumns.add("PAYDATENO", DataValues.newString(payDateNo));
                        detailColumns.add("PMTCATEGORY", DataValues.newString("3"));
                        detailColumns.add("PURORDERNO", DataValues.newString(""));
                        detailColumns.add("APSUBJECTID", DataValues.newString(chSubjectId));
                        detailColumns.add("INVOICENUMBER", DataValues.newString(invoiceNumber));
                        detailColumns.add("INVOICECODE", DataValues.newString(invoiceCode));
                        detailColumns.add("INVOICEDATE", DataValues.newString(invoiceDate));

                        String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                        DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean detailib = new InsBean("DCP_APPERD", detailColumnNames);
                        detailib.addValues(detailDataValues);
                        this.addProcessData(new DataProcessBean(detailib));
                    }

                    List<Map> sumCollect = detailList.stream().map(x -> {
                        Map map = new HashMap();
                        map.put("sourceNo", x.get("SOURCENO").toString());
                        map.put("sourceOrg", x.get("SOURCEORG").toString());
                        map.put("category", x.get("CATEGORY").toString());
                        map.put("isGift", x.get("ISGIFT").toString());

                        return map;
                    }).distinct().collect(Collectors.toList());
                    for (Map sumCollectMap : sumCollect) {
                        String sourceNo = sumCollectMap.get("sourceNo").toString();
                        String category = sumCollectMap.get("category").toString();
                        String sourceOrg = sumCollectMap.get("SOURCEORG").toString();
                        String isGift = sumCollectMap.get("isGift").toString();

                        List<Map<String, Object>> collect = detailList.stream().filter(x -> x.get("SOURCENO").toString().equals(sourceNo)
                                && x.get("SOURCEORG").toString().equals(sourceOrg)
                                && x.get("CATEGORY").toString().equals(category)
                                && x.get("ISGIFT").toString().equals(isGift)).collect(Collectors.toList());


                    }

                    if (StringUtils.isEmpty(res.getApNo())) {
                        res.setApNo(apNo);
                    }
                    if (StringUtils.isEmpty(res.getFirstApNo())) {
                        res.setFirstApNo(apNo);
                    }
                    res.setLastApNo(apNo);
                    apNos.add(apNo);
                    num++;
                    res.setNum(num);

                    res.setApNos(apNos);

                }
            }
        }

        if("N".equals(isInvoiceIncl)) {
            //对账单的数据
            StringBuffer sql2 = new StringBuffer("select a.*,b.payee" +
                    " from DCP_RECONLIATION a " +
                    " left join dcp_bizpartner b on a.eid=b.eid and a.bizpartnerno=b.bizpartnerno " +
                    " where a.eid='" + eId + "' and a.corp='" + corp + "' and  ( a.APNO is null or a.apno='') " +
                    " and a.ISINVOICEINCL='N' and a.DATATYPE='1' ");
            if (CollUtil.isNotEmpty(noList)) {
                List<DCP_ApBillProcessReq.NoList> collect = noList.stream().filter(x -> Check.NotNull(x.getBillNo())).distinct().collect(Collectors.toList());
                if (CollUtil.isNotEmpty(collect)) {
                    sql2.append( " and a.RECONNO in (" + noList.stream().map(x -> "'" + x.getBillNo() + "'").distinct().collect(Collectors.joining(",")) + ")");
                }
            }
            if (CollUtil.isNotEmpty(supplierList)) {
                sql2.append(" and a.bizpartnerno in (" + supplierList.stream().map(x -> "'" + x.getBizPartnerNo() + "'").distinct().collect(Collectors.joining(",")) + ")");
            }
            List<Map<String, Object>> sourceList2 = this.doQueryData(sql2.toString(), null);

            if(sourceList2.size()>0){

                String reconDetailSql = "select a.*,b.originno,b.originitem,c.departid,b.category,b.isgift,b.punit as priceunit,d.spec,b.pqty as qty,b.price as billprice  " +
                        " from DCP_RECONDETAIL a " +
                        " left join dcp_sstockin_detail b on a.eid=b.eid and a.sourceno=b.sstockinno and a.sourcenoseq=b.item " +
                        " left join dcp_sstockin c on a.eid=c.eid and a.sourceno=c.sstockinno " +
                        " left join dcp_goods d on d.eid=a.eid and d.pluno=a.pluno " +
                        " where a.eid='" + req.geteId() + "' and a.reconno in (" + sourceList2.stream().map(x -> "'" + x.get("RECONNO").toString() + "'").distinct().collect(Collectors.joining(",")) + ") ";
                List<Map<String, Object>> reconDetailList = this.doQueryData(reconDetailSql, null);

                //应付单暂估单 账期只有一笔
                //用sourceNo 和sourceItem 去查询

                StringBuffer sJoinSourceNo=new StringBuffer("");
                StringBuffer sJoinSourceNoSeq=new StringBuffer("");

                for (Map<String, Object> singleData : reconDetailList){
                    String sourceNo  = singleData.get("SOURCENO").toString().toString();
                    String sourceNoSeq  = singleData.get("SOURCENOSEQ").toString().toString();

                    sJoinSourceNo.append(sourceNo+",");
                    sJoinSourceNoSeq.append(sourceNoSeq+",");
                }
                Map<String, String> mapSOrder=new HashMap<String, String>();
                mapSOrder.put("SOURCENO", sJoinSourceNo.toString());
                mapSOrder.put("SOURCENOSEQ", sJoinSourceNoSeq.toString());

                MyCommon cm=new MyCommon();
                String withasSql_mono=cm.getFormatSourceMultiColWith(mapSOrder);

                //应付暂估单明细
                List<Map<String, Object>> zgList=new ArrayList<>();

                //应付待抵单
                List<Map<String,Object>> ddList=new ArrayList<>();

                if(withasSql_mono.length()>0){
                    String zgSql="with p as ("+withasSql_mono+") " +
                            " select a.apno,a.sourceno,a.sourceitem,d.FCYTATAMT,d.FCYPMTREVAMT,d.item,d.INSTPMTSEQ" +
                            " " +
                            " from DCP_APBILLDETAIL a" +
                            " inner join p on a.sourceno=p.sourceno and a.sourceitem=p.sourcenoseq " +
                            " inner join DCP_APBILL c on c.eid=a.eid and c.apno=a.apno and c.accountid=a.accountid " +
                            " inner join DCP_APPERD d on d.eid=a.eid and d.apno=a.apno and d.accountid=a.accountid " +
                            " where a.eid='"+eId+"' and c.accountid='"+accountId+"' and " +
                            "  c.aptype in ('01','02','03') and nvl(d.FCYTATAMT,0)-nvl(d.FCYPMTREVAMT,0)>0 ";

                    zgList=this.doQueryData(zgSql,null);

                    //ISDESPOSITBYPURCH
                    String ddSql="with p as ("+withasSql_mono+") " +
                            " select distinct a.apno,d.FCYTATAMT,d.FCYPMTREVAMT,d.item,d.INSTPMTSEQ " +
                            " " +
                            " from DCP_APBILLDETAIL a" +
                            " inner join DCP_APBILL c on c.eid=a.eid and c.apno=a.apno and c.accountid=a.accountid " +
                            " inner join DCP_APPERD d on d.eid=a.eid and d.apno=a.apno and d.accountid=a.accountid " +
                            " where a.eid='"+eId+"' and c.accountid='"+accountId+"' and " +
                            "  c.aptype in ('04') and nvl(d.FCYTATAMT,0)-nvl(d.FCYPMTREVAMT,0)>0 ";
                    if("Y".equals(isDespositByPurch)){
                        ddSql="with p as ("+withasSql_mono+") " +
                                " select distinct a.apno,d.FCYTATAMT,d.FCYPMTREVAMT,d.item,d.INSTPMTSEQ " +
                                " " +
                                " from DCP_APBILLDETAIL a" +
                                " inner join p on a.sourceno=p.sourceno and a.sourceitem=p.sourcenoseq " +
                                " inner join DCP_APBILL c on c.eid=a.eid and c.apno=a.apno and c.accountid=a.accountid " +
                                " inner join DCP_APPERD d on d.eid=a.eid and d.apno=a.apno and d.accountid=a.accountid " +
                                " where a.eid='"+eId+"' and c.accountid='"+accountId+"' and " +
                                "  c.aptype in ('04') and nvl(d.FCYTATAMT,0)-nvl(d.FCYPMTREVAMT,0)>0 ";
                    }
                    ddList=this.doQueryData(ddSql,null);
                }

                List<Map<String, Object>> zgApList=new ArrayList<>();
                if(zgList.size()>0){
                    String yetApNoStr = zgList.stream().map(x -> "'" + x.get("APNO").toString() + "'").distinct().collect(Collectors.joining(","));
                    String yetApSql="select * from dcp_apbill a where a.eid='"+eId+"' and a.accountid='"+accountId+"' " +
                            " and a.apno in ("+yetApNoStr+") ";
                    zgApList=this.doQueryData(yetApSql,null);
                }

                //isApSplit 拆税分单
                List<Map<String,String>> splitList = sourceList2.stream()
                        .map(
                                x -> {
                                    Map<String,String> map=new HashMap<>();
                                    map.put("reconNo",x.get("RECONNO").toString());

                                    if("Y".equals(isApSplit)){
                                        map.put("taxCode",x.get("TAXCODE").toString());
                                    }else{
                                        map.put("taxCode","");
                                    }
                                    return map;
                                }
                        ).distinct().collect(Collectors.toList());

                List<DCP_ApBillProcessReq.ReconDetail> apDetailSum=new ArrayList<>();

                for (Map<String,String> reconMap : splitList){

                    String reconNo = reconMap.get("reconNo");
                    String taxCodeMap = reconMap.get("taxCode");


                    List<Map<String, Object>> singleSourceList = sourceList2.stream().filter(x -> x.get("RECONNO").toString().equals(reconNo)).collect(Collectors.toList());
                    if("Y".equals(isApSplit)){
                        singleSourceList=singleSourceList.stream().filter(x -> x.get("TAXCODE").toString().equals(taxCodeMap)).collect(Collectors.toList());
                    }

                    String apNo = this.getApNo(req);
                    int zgItem=0;

                    Map<String, Object> singleSource = singleSourceList.get(0);
                    String organizationNo = singleSource.get("ORGANIZATIONNO").toString();
                    String bizpartnerNo = singleSource.get("BIZPARTNERNO").toString();
                    String payee = singleSource.get("PAYEE").toString();
                    String payDateNo = singleSource.get("PAYDATENO").toString();
                    String taxCode = reconDetailList.get(0).get("TAXCODE").toString();
                    String taxRate = reconDetailList.get(0).get("TAXRATE").toString();
                    String exRate = "1";//汇率 还没有
                    String currency = singleSource.get("CURRENCY").toString();

                    String invfcybtAmt = singleSource.get("CURRRECONPRETAXAMT").toString();
                    String invfcytAmt = singleSource.get("CURRRECONTAXAMT").toString();
                    String invfcyatAmt = singleSource.get("CURRRECONAMT").toString();
                    String invlcybtAmt = singleSource.get("CURRRECONPRETAXAMT").toString();
                    String invlcytAmt = singleSource.get("CURRRECONTAXAMT").toString();
                    String invlcyatAmt = singleSource.get("CURRRECONAMT").toString();
                    String invoiceCode = "";
                    String invoiceNumber = "";
                    String invoiceDate = "";

                    String apType="09";

                    if(new BigDecimal(invfcyatAmt).compareTo(BigDecimal.ZERO)<=0){
                        apType="14";
                    }

                    String payDueDate = getPayDateDueDate(req, req.getRequest().getBDate(), payDateNo);
                    ColumnDataValue mainColumns = new ColumnDataValue();
                    mainColumns.add("EID", DataValues.newString(eId));
                    mainColumns.add("STATUS", DataValues.newString("1"));
                    mainColumns.add("CORP", DataValues.newString(corp));
                    mainColumns.add("ACCOUNTID", DataValues.newString(accountId));
                    mainColumns.add("APNO", DataValues.newString(apNo));
                    mainColumns.add("PDATE", DataValues.newString(bdate));
                    mainColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
                    mainColumns.add("APTYPE", DataValues.newString(apType));//采购应付
                    mainColumns.add("ACCEMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
                    mainColumns.add("BIZPARTNERNO", DataValues.newString(bizpartnerNo));
                    mainColumns.add("RECEIVER", DataValues.newString(payee));
                    mainColumns.add("TASKID", DataValues.newString("1"));
                    mainColumns.add("PAYDATENO", DataValues.newString(payDateNo));
                    mainColumns.add("PAYDUEDATE", DataValues.newString(payDueDate));
                    mainColumns.add("TAXCODE", DataValues.newString(taxCode));
                    mainColumns.add("TAXRATE", DataValues.newDecimal(taxRate));
                    mainColumns.add("INCLTAX", DataValues.newString(""));
                    //mainColumns.add("APPLICANT", DataValues.newString(request.getApplicant()));
                    mainColumns.add("EMPLOYEENO", DataValues.newString(""));
                    mainColumns.add("DEPARTNO", DataValues.newString(""));
                    mainColumns.add("SOURCETYPE", DataValues.newString("1"));
                    mainColumns.add("SOURCENO", DataValues.newString(reconNo));
                    mainColumns.add("PENDOFFSETNO", DataValues.newString(""));
                    mainColumns.add("FEESUBJECTID", DataValues.newString(chSubjectId));
                    mainColumns.add("APSUBJECTID", DataValues.newString(zkSubjectId));
                    mainColumns.add("GLNO", DataValues.newString(""));
                    mainColumns.add("GRPPMTNO", DataValues.newString(""));
                    mainColumns.add("MEMO", DataValues.newString(""));
                    mainColumns.add("payList", DataValues.newString("3"));
                    mainColumns.add("CURRENCY", DataValues.newString(currency));
                    mainColumns.add("EXRATE", DataValues.newDecimal(exRate));
                    mainColumns.add("FCYBTAMT", DataValues.newDecimal(invfcybtAmt));
                    mainColumns.add("FCYTAMT", DataValues.newDecimal(invfcytAmt));
                    mainColumns.add("FCYREVAMT", DataValues.newDecimal(0));
                    mainColumns.add("FCYTATAMT", DataValues.newDecimal(invfcyatAmt));
                    mainColumns.add("LCYBTAMT", DataValues.newDecimal(invlcybtAmt));
                    mainColumns.add("LCYTAMT", DataValues.newDecimal(invlcytAmt));
                    mainColumns.add("LCYREVAMT", DataValues.newDecimal("0"));
                    mainColumns.add("LCYTATAMT", DataValues.newDecimal(invlcyatAmt));
                    mainColumns.add("FCYPMTAMT", DataValues.newDecimal(""));
                    mainColumns.add("LCYPMTAMT", DataValues.newDecimal(""));

                    mainColumns.add("CREATEBY", DataValues.newString(req.getOpNO()));
                    mainColumns.add("CREATE_DATE", DataValues.newString(createDate));
                    mainColumns.add("CREATE_TIME", DataValues.newString(createTime));

                    String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
                    DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean ib = new InsBean("DCP_APBILL", mainColumnNames);
                    ib.addValues(mainDataValues);
                    this.addProcessData(new DataProcessBean(ib));

                    Map<String, DCP_ApBillProcessReq.czgList> zgAmtMap=new HashMap<>();
                    List<DCP_ApBillProcessReq.ApBillDetailList> apBillDetailList=new ArrayList();
                    List<Map<String, Object>> detailList = reconDetailList.stream().filter(x -> x.get("RECONNO").toString().equals(reconNo)).collect(Collectors.toList());
                    if("Y".equals(isApSplit)){
                        detailList=detailList.stream().filter(x->x.get("TAXCODE").toString().equals(taxCodeMap)).collect(Collectors.toList());
                    }
                    if (CollUtil.isNotEmpty(detailList)) {
                        int item = 0;
                        for (Map<String, Object> singleDetail : detailList) {

                            String reconNoS = singleDetail.get("RECONNO").toString();
                            String reconItemS = singleDetail.get("ITEM").toString();
                            String sourceNo = singleDetail.get("SOURCENO").toString();
                            String sourceItem = singleDetail.get("SOURCENOSEQ").toString();
                            String qty = singleDetail.get("QTY").toString();
                            String amt = singleDetail.get("AMT").toString();

                            DCP_ApBillProcessReq.ReconDetail reconDetail = req.new ReconDetail();
                            reconDetail.setReconNo(reconNoS);
                            reconDetail.setItem(reconItemS);
                            reconDetail.setApNo(apNo);
                            reconDetail.setSourceNo(sourceNo);
                            reconDetail.setSourceItem(sourceItem);
                            reconDetail.setQty(qty);
                            reconDetail.setAmt(amt);
                            apDetailSum.add(reconDetail);

                            String sourceOrg = singleDetail.get("ORGANIZATIONNO").toString();
                            String pluNo = singleDetail.get("PLUNO").toString();
                            String spec = singleDetail.get("SPEC").toString();
                            String priceUnit = singleDetail.get("PRICEUNIT").toString();
                            String billPrice = singleDetail.get("BILLPRICE").toString();
                            String fee = singleDetail.get("FEE").toString();
                            String originNo = singleDetail.get("ORIGINNO").toString();
                            String originItem = singleDetail.get("ORIGINITEM").toString();
                            String departId = singleDetail.get("DEPARTID").toString();
                            String category = singleDetail.get("CATEGORY").toString();
                            String isGift = singleDetail.get("ISGIFT").toString();
                            String direction = singleDetail.get("DIRECTION").toString();

                            String invSubject = "";
                            if (CollUtil.isNotEmpty(categorySubjectList)) {
                                List<Map<String, Object>> categoryFilter = categorySubjectList.stream().filter(x -> x.get("CATEGORY").toString().equals(category)).collect(Collectors.toList());
                                if (categoryFilter.size() > 0) {
                                    invSubject = categoryFilter.get(0).get("INVSUBJECT").toString();
                                }
                            }
                            String taxSubject = "";
                            if (CollUtil.isNotEmpty(taxSubjectList)) {
                                List<Map<String, Object>> taxSubjectFilter = taxSubjectList.stream().filter(x -> x.get("TAXCODE").toString().equals(taxCode) && x.get("SETUPTYPE").toString().equals("1")).collect(Collectors.toList());
                                if (taxSubjectFilter.size() > 0) {
                                    taxSubject = taxSubjectFilter.get(0).get("SUBJECTID").toString();
                                }
                            }

                            String fcybtAmt = singleDetail.get("PRETAXAMT").toString();
                            String fcyatAmt = singleDetail.get("AMT").toString();
                            String fcytAmt =new BigDecimal(fcyatAmt).subtract(new BigDecimal(fcybtAmt)).toString();

                            String lcybtAmt = singleDetail.get("PRETAXAMT").toString();
                            String lcyatAmt = singleDetail.get("AMT").toString();
                            String lcytAmt = new BigDecimal(lcyatAmt).subtract(new BigDecimal(lcybtAmt)).toString();

                            String isRevese="N";
                            if(zgList.size()>0){
                                List<Map<String, Object>> filterRows = zgList.stream().filter(x -> x.get("SOURCENO").toString().equals(sourceNo) && x.get("SOURCEITEM").toString().equals(sourceItem)).collect(Collectors.toList());
                                if(filterRows.size()>0){
                                    isRevese="Y";
                                    String yetApNo = filterRows.get(0).get("APNO").toString();
                                    BigDecimal yetFcyTatAmt = new BigDecimal(filterRows.get(0).get("FCYTATAMT").toString());
                                    BigDecimal yetPmtRevAmt = new BigDecimal(filterRows.get(0).get("FCYPMTREVAMT").toString());
                                    BigDecimal subtract = yetFcyTatAmt.subtract(yetPmtRevAmt);
                                    if(zgAmtMap.containsKey(yetApNo)){
                                        DCP_ApBillProcessReq.czgList czgList = zgAmtMap.get(yetApNo);
                                        BigDecimal add1 = czgList.getFcyBtAmt().add(new BigDecimal(fcybtAmt));
                                        BigDecimal add2 = czgList.getFcyBtAmt().add(new BigDecimal(lcybtAmt));
                                        czgList.setFcyBtAmt(add1);
                                        czgList.setLcyBtAmt(add2);
                                        zgAmtMap.put(yetApNo,czgList);

                                    }else{

                                        DCP_ApBillProcessReq.czgList czgList = req.new czgList();
                                        czgList.setApNo(yetApNo);
                                        czgList.setFcyBtAmt(new BigDecimal(fcybtAmt));
                                        czgList.setLcyBtAmt(new BigDecimal(lcybtAmt));
                                        zgAmtMap.put(yetApNo,czgList);
                                    }
                                    //金额比较
                                    if(subtract.compareTo(new BigDecimal(fcyatAmt))!=0){
                                        //写入一笔到冲暂估明细
                                        BigDecimal yetSubtract = subtract.subtract(new BigDecimal(fcyatAmt));

                                        List<Map<String, Object>> yetApList = zgApList.stream().filter(x -> x.get("APNO").toString().equals(yetApNo)).collect(Collectors.toList());
                                        if(yetApList.size()>0) {

                                            BigDecimal yetExRate = new BigDecimal(yetApList.get(0).get("EXRATE").toString());
                                            BigDecimal yetFcyBtAmt = new BigDecimal(yetApList.get(0).get("FCYBTAMT").toString());
                                            BigDecimal yetLcyBtAmt = new BigDecimal(yetApList.get(0).get("LCYBTAMT").toString());
                                            //BigDecimal subtractLcyBtAmt = lcyBtAmt.subtract(yetLcyBtAmt);
                                            //BigDecimal subtractFcyBtAmt = fcyBtAmt.subtract(yetFcyBtAmt);
                                            //BigDecimal multiplyLcyBtAmt = subtractLcyBtAmt.multiply(yetExRate);
                                            zgItem++;
                                            //写入一笔数据到冲暂估明细
                                            ColumnDataValue czgColumns = new ColumnDataValue();
                                            czgColumns.add("EID", DataValues.newString(req.geteId()));
                                            czgColumns.add("ACCOUNTID", DataValues.newString(accountId));
                                            czgColumns.add("SOURCEORG", DataValues.newString(organizationNo));
                                            czgColumns.add("APNO", DataValues.newString(apNo));
                                            czgColumns.add("ITEM", DataValues.newString(zgItem));
                                            czgColumns.add("ITEM2", DataValues.newString(""));
                                            czgColumns.add("TASKID", DataValues.newString("7"));//采购暂估单
                                            czgColumns.add("WRTOFFTYPE", DataValues.newString("02"));
                                            czgColumns.add("WRTOFFQTY", DataValues.newString(""));
                                            czgColumns.add("ESTBILLNO", DataValues.newString("DIFF"));
                                            czgColumns.add("ESTBILLITEM", DataValues.newString(""));
                                            czgColumns.add("PERIOD", DataValues.newString("0"));
                                            czgColumns.add("TAXRATE", DataValues.newString(yetApList.get(0).get("TAXRATE").toString()));
                                            czgColumns.add("WRTOFFAPSUBJECT", DataValues.newString(yetApList.get(0).get("APSUBJECTID").toString()));
                                            czgColumns.add("WRTOFFBTAXSUBJECT", DataValues.newString(""));
                                            czgColumns.add("WRTOFFTAXSUBJECT", DataValues.newString(""));
                                            czgColumns.add("WRTOFFPRCDIFFSUBJECT", DataValues.newString(czgSubjectId));
                                            czgColumns.add("WRTOFFEXDIFFSUBJECT", DataValues.newString(""));
                                            czgColumns.add("DEPARTNO", DataValues.newString(yetApList.get(0).get("DEPARTNO").toString()));
                                            czgColumns.add("TRADECUSTOMER", DataValues.newString(yetApList.get(0).get("TRADECUSTOMER").toString()));
                                            czgColumns.add("PMTCUSTOMER", DataValues.newString(yetApList.get(0).get("PMTCUSTOMER").toString()));
                                            czgColumns.add("CATEGORY", DataValues.newString(""));
                                            czgColumns.add("EMPLOYEENO", DataValues.newString(yetApList.get(0).get("EMPLOYEENO").toString()));
                                            czgColumns.add("FREECHARS1", DataValues.newString(yetApList.get(0).get("FREECHARS1").toString()));
                                            czgColumns.add("FREECHARS2", DataValues.newString(yetApList.get(0).get("FREECHARS2").toString()));
                                            czgColumns.add("FREECHARS3", DataValues.newString(yetApList.get(0).get("FREECHARS3").toString()));
                                            czgColumns.add("FREECHARS4", DataValues.newString(yetApList.get(0).get("FREECHARS4").toString()));
                                            czgColumns.add("FREECHARS5", DataValues.newString(yetApList.get(0).get("FREECHARS5").toString()));
                                            czgColumns.add("MEMO", DataValues.newString(""));
                                            czgColumns.add("FCYBILLPRICE", DataValues.newString(""));
                                            czgColumns.add("EXRATE", DataValues.newString(yetApList.get(0).get("EXRATE").toString()));
                                            czgColumns.add("FCYBTAMT", DataValues.newString(yetApList.get(0).get("FCYBTAMT").toString()));
                                            czgColumns.add("FCYTAMT", DataValues.newString(yetApList.get(0).get("FCYTAMT").toString()));
                                            czgColumns.add("FCYTATAMT", DataValues.newString(yetApList.get(0).get("FCYTATAMT").toString()));
                                            czgColumns.add("FCYWRTOFFDIFFAMT", DataValues.newString(yetSubtract));
                                            czgColumns.add("LCYBILLPRICE", DataValues.newString(""));
                                            czgColumns.add("LCYBTAMT", DataValues.newString(yetApList.get(0).get("LCYBTAMT").toString()));
                                            czgColumns.add("LCYTAMT", DataValues.newString(yetApList.get(0).get("LCYTAMT").toString()));
                                            czgColumns.add("LCYTATAMT", DataValues.newString(yetApList.get(0).get("LCYTATAMT").toString()));
                                            czgColumns.add("LCYPRCDIFFAMT", DataValues.newString(yetSubtract));
                                            czgColumns.add("LCYEXDIFFAMT", DataValues.newString(yetSubtract));

                                            String[] czgColumnNames = czgColumns.getColumns().toArray(new String[0]);
                                            DataValue[] czgDataValues = czgColumns.getDataValues().toArray(new DataValue[0]);
                                            InsBean czgib = new InsBean("DCP_APBILLESTDTL", czgColumnNames);
                                            czgib.addValues(czgDataValues);
                                            this.addProcessData(new DataProcessBean(czgib));
                                        }


                                    }

                                }
                            }

                            item++;
                            ColumnDataValue detailColumns = new ColumnDataValue();
                            detailColumns.add("EID", DataValues.newString(eId));
                            detailColumns.add("APNO", DataValues.newString(apNo));
                            detailColumns.add("ACCOUNTID", DataValues.newString(accountId));
                            detailColumns.add("ITEM", DataValues.newString(item));
                            detailColumns.add("BIZPARTNERNO", DataValues.newString(bizpartnerNo));
                            detailColumns.add("RECEIVER", DataValues.newString(payee));
                            detailColumns.add("SOURCETYPE", DataValues.newString("1"));
                            detailColumns.add("SOURCENO", DataValues.newString(sourceNo));
                            detailColumns.add("SOURCEITEM", DataValues.newString(sourceItem));
                            detailColumns.add("SOURCEORG", DataValues.newString(sourceOrg));
                            detailColumns.add("PLUNO", DataValues.newString(pluNo));
                            detailColumns.add("SPEC", DataValues.newString(spec));
                            detailColumns.add("PRICEUNIT", DataValues.newString(priceUnit));
                            detailColumns.add("QTY", DataValues.newDecimal(qty));
                            detailColumns.add("BILLPRICE", DataValues.newDecimal(billPrice));
                            detailColumns.add("FEE", DataValues.newDecimal(fee));
                            detailColumns.add("OOFNO", DataValues.newString(originNo));
                            detailColumns.add("OOITEM", DataValues.newString(originItem));
                            detailColumns.add("DEPARTNO", DataValues.newString(departId));
                            detailColumns.add("CATEGORY", DataValues.newString(category));
                            detailColumns.add("ISGIFT", DataValues.newString(isGift));
                            detailColumns.add("BSNO", DataValues.newString(""));
                            detailColumns.add("TAXRATE", DataValues.newDecimal(taxRate));
                            detailColumns.add("FEESUBJECTID", DataValues.newString(invSubject));
                            detailColumns.add("APSUBJECTID", DataValues.newString(chSubjectId));
                            detailColumns.add("TAXSUBJECTID", DataValues.newString(taxSubject));
                            detailColumns.add("DIRECTION", DataValues.newString(direction));
                            detailColumns.add("ISREVEST", DataValues.newString(isRevese));
                            detailColumns.add("PAYDATENO", DataValues.newString(payDateNo));
                            detailColumns.add("EMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
                            detailColumns.add("MEMO", DataValues.newString(""));
                            detailColumns.add("CURRENCY", DataValues.newString(currency));
                            detailColumns.add("FCYPRICE", DataValues.newDecimal(billPrice));
                            detailColumns.add("EXRATE", DataValues.newDecimal(exRate));
                            detailColumns.add("FCYBTAMT", DataValues.newDecimal(fcybtAmt));
                            detailColumns.add("FCYTAMT", DataValues.newDecimal(fcytAmt));
                            detailColumns.add("FCYTATAMT", DataValues.newDecimal(fcyatAmt));
                            detailColumns.add("FCYSTDCOSTAMT", DataValues.newDecimal(0));
                            detailColumns.add("FCYACTCOSTAMT", DataValues.newDecimal(0));

                            BigDecimal multiply = new BigDecimal(billPrice).multiply(new BigDecimal(exRate));
                            detailColumns.add("LCYPRICE", DataValues.newDecimal(multiply));
                            detailColumns.add("LCYBTAMT", DataValues.newDecimal(lcybtAmt));
                            detailColumns.add("LCYTAMT", DataValues.newDecimal(lcytAmt));
                            detailColumns.add("LCYTATAMT", DataValues.newDecimal(lcyatAmt));
                            detailColumns.add("LCYSTDCOSTAMT", DataValues.newDecimal(0));
                            detailColumns.add("LCYACTCOSTAMT", DataValues.newDecimal(0));
                            detailColumns.add("PURORDERNO", DataValues.newString(""));


                            String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                            DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                            InsBean detailib = new InsBean("DCP_APBILLDETAIL", detailColumnNames);
                            detailib.addValues(detailDataValues);
                            this.addProcessData(new DataProcessBean(detailib));

                            DCP_ApBillProcessReq.ApBillDetailList apBillDetailInfo = req.new ApBillDetailList();
                            apBillDetailInfo.setAccountId(accountId);
                            apBillDetailInfo.setOrganizationNo(organizationNo);
                            apBillDetailInfo.setApNo(apNo);
                            apBillDetailInfo.setItem(String.valueOf(item));
                            apBillDetailInfo.setBizPartnerNo(bizpartnerNo);
                            apBillDetailInfo.setReceiver(payee);
                            apBillDetailInfo.setSourceType("1");
                            apBillDetailInfo.setSourceNo(sourceNo);
                            apBillDetailInfo.setSourceItem(sourceItem);
                            apBillDetailInfo.setSourceOrg(sourceOrg);
                            apBillDetailInfo.setPluNo(pluNo);
                            apBillDetailInfo.setSpec(spec);
                            apBillDetailInfo.setPriceUnit(priceUnit);
                            apBillDetailInfo.setQty(String.valueOf(qty));
                            apBillDetailInfo.setBillPrice(String.valueOf(billPrice));
                            apBillDetailInfo.setFee(String.valueOf(fee));
                            apBillDetailInfo.setOofNo(originNo);
                            apBillDetailInfo.setOoItem(originItem);
                            apBillDetailInfo.setDepartId(departId);
                            apBillDetailInfo.setCateGory(category);
                            apBillDetailInfo.setIsGift(isGift);
                            apBillDetailInfo.setBsNo("");
                            apBillDetailInfo.setTaxRate(String.valueOf(taxRate));
                            apBillDetailInfo.setFeeSubjectId(invSubject);
                            apBillDetailInfo.setApSubjectId(chSubjectId);
                            apBillDetailInfo.setTaxSubjectId(taxSubject);
                            apBillDetailInfo.setDirection(direction);
                            apBillDetailInfo.setIsRevEst(isRevese);
                            apBillDetailInfo.setPayDateNo(payDateNo);
                            apBillDetailInfo.setEmployeeNo(req.getEmployeeNo());
                            apBillDetailInfo.setFreeChars1("");
                            apBillDetailInfo.setFreeChars2("");
                            apBillDetailInfo.setFreeChars3("");
                            apBillDetailInfo.setFreeChars4("");
                            apBillDetailInfo.setFreeChars5("");
                            apBillDetailInfo.setMemo("");
                            apBillDetailInfo.setCurrency(currency);
                            apBillDetailInfo.setFCYPrice(String.valueOf(billPrice));
                            apBillDetailInfo.setExRate(String.valueOf(exRate));
                            apBillDetailInfo.setFCYBTAmt(String.valueOf(fcybtAmt));
                            apBillDetailInfo.setFCYTAmt(String.valueOf(fcytAmt));
                            apBillDetailInfo.setFCYTATAmt(String.valueOf(fcyatAmt));
                            apBillDetailInfo.setFCYStdCostAmt(String.valueOf(0));
                            apBillDetailInfo.setFCYActCostAmt(String.valueOf(0));
                            apBillDetailInfo.setLCYPrice(String.valueOf(multiply));
                            apBillDetailInfo.setLCYBTAmt(String.valueOf(lcybtAmt));
                            apBillDetailInfo.setLCYTAmt(String.valueOf(lcytAmt));
                            apBillDetailInfo.setLCYTATAmt(String.valueOf(lcyatAmt));
                            apBillDetailInfo.setLCYStdCostAmt(String.valueOf(0));
                            apBillDetailInfo.setLCYActCostAmt(String.valueOf(0));
                            apBillDetailInfo.setPurOrderNo("");
                            apBillDetailList.add(apBillDetailInfo);

                            if("1".equals(apParameter)){//核销至单号
                                if(CollUtil.isNotEmpty(ddList)){
                                    BigDecimal hxAmt = new BigDecimal(fcyatAmt);

                                    List<String> apNos = ddList.stream().map(x -> x.get("APNO").toString()).distinct().collect(Collectors.toList());
                                    for (String wrApNo : apNos){
                                        List<Map<String, Object>> filterRows = ddList.stream().filter(x -> x.get("APNO").toString().equals(wrApNo)).collect(Collectors.toList());

                                        for (Map<String, Object> singleDd : filterRows){
                                            BigDecimal fcytatAmtdd = new BigDecimal(singleDd.get("FCYTATAMT").toString());
                                            BigDecimal fcypmtrevAmtdd = new BigDecimal(singleDd.get("FCYPMTREVAMT").toString());

                                            BigDecimal subtract = fcytatAmtdd.subtract(fcypmtrevAmtdd);//剩余可核销的金额
                                            if(subtract.compareTo(BigDecimal.ZERO)<=0){
                                                continue;
                                            }
                                            if(hxAmt.compareTo(BigDecimal.ZERO)<=0){
                                                break;
                                            }

                                            BigDecimal thisHxAmt=BigDecimal.ZERO;
                                            if(hxAmt.compareTo(subtract)>0){
                                                hxAmt=hxAmt.subtract(subtract);
                                                thisHxAmt=subtract;
                                            }else{
                                                hxAmt = new BigDecimal(0);
                                                thisHxAmt=hxAmt;
                                            }

                                            //thishxAmt 加到FCYPMTREVAMT
                                            BigDecimal add = thisHxAmt.add(fcypmtrevAmtdd);
                                            UptBean ub1 = new UptBean("DCP_APPERD");
                                            //add Value
                                            ub1.addUpdateValue("FCYPMTREVAMT", new DataValue(add, Types.VARCHAR));

                                            //condition
                                            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                            ub1.addCondition("ACCOUNTID", new DataValue(accountId, Types.VARCHAR));
                                            ub1.addCondition("ITEM", new DataValue(singleDd.get("ITEM").toString(), Types.VARCHAR));
                                            ub1.addCondition("APNO", new DataValue(singleDd.get("APNO").toString(), Types.VARCHAR));
                                            this.addProcessData(new DataProcessBean(ub1));



                                        }
                                        ColumnDataValue writoffColumns = new ColumnDataValue();
                                        writoffColumns.add("EID", DataValues.newString(req.geteId()));
                                        writoffColumns.add("ACCOUNTID", DataValues.newString(accountId));
                                        writoffColumns.add("CORP", DataValues.newString(corp));
                                        writoffColumns.add("SOURCEORG", DataValues.newString(sourceOrg));
                                        writoffColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
                                        writoffColumns.add("WRTOFFNO", DataValues.newString(wrApNo));
                                        writoffColumns.add("ITEM", DataValues.newString("1"));
                                        writoffColumns.add("TASKID", DataValues.newString("1"));
                                        writoffColumns.add("WRTOFFTYPE", DataValues.newString("10"));
                                        writoffColumns.add("WRTOFFBILLNO", DataValues.newString(apNo));
                                        writoffColumns.add("WRTOFFBILLITEM", DataValues.newString(""));
                                        writoffColumns.add("INSTPMTSEQ", DataValues.newString(""));
                                        writoffColumns.add("MEMO", DataValues.newString(""));
                                        writoffColumns.add("BSNO", DataValues.newString(""));
                                        writoffColumns.add("WRTOFFDIRECTION", DataValues.newString("1"));
                                        writoffColumns.add("APSUBJECTID", DataValues.newString(chSubjectId));
                                        writoffColumns.add("EMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
                                        writoffColumns.add("DEPARTNO", DataValues.newString(req.getDepartmentNo()));
                                        writoffColumns.add("CATEGORY", DataValues.newString(category));
                                        writoffColumns.add("SECREFNO", DataValues.newString(originNo));
                                        writoffColumns.add("GLNO", DataValues.newString(""));
                                        writoffColumns.add("PAYDUEDATE", DataValues.newString(payDueDate));
                                        writoffColumns.add("BIZPARTNERNO", DataValues.newString(bizpartnerNo));
                                        writoffColumns.add("RECEIVER", DataValues.newString(payee));
                                        writoffColumns.add("FREECHARS1", DataValues.newString("0"));
                                        writoffColumns.add("FREECHARS2", DataValues.newString("0"));
                                        writoffColumns.add("FREECHARS3", DataValues.newString("0"));
                                        writoffColumns.add("FREECHARS4", DataValues.newString("0"));
                                        writoffColumns.add("FREECHARS5", DataValues.newString("0"));
                                        writoffColumns.add("CURRENCY", DataValues.newString(currency));
                                        writoffColumns.add("EXRATE", DataValues.newDecimal(exRate));
                                        writoffColumns.add("FCYREVAMT", DataValues.newDecimal(fcybtAmt));
                                        writoffColumns.add("LCYREVAMT", DataValues.newDecimal(fcybtAmt));
                                        writoffColumns.add("FCYBTAXWRTOFFAMT", DataValues.newDecimal(fcybtAmt));
                                        writoffColumns.add("LCYBTAXWRTOFFAMT", DataValues.newDecimal(fcybtAmt));
                                        writoffColumns.add("INVOICENUMBER", DataValues.newString(""));
                                        writoffColumns.add("INVOICECODE", DataValues.newString(""));

                                        String[] writoffColumnNames = writoffColumns.getColumns().toArray(new String[0]);
                                        DataValue[] writoffDataValues = writoffColumns.getDataValues().toArray(new DataValue[0]);
                                        InsBean writoffib = new InsBean("DCP_APBILLWRTOFF", writoffColumnNames);
                                        writoffib.addValues(writoffDataValues);
                                        this.addProcessData(new DataProcessBean(writoffib));


                                    }


                                }

                            }
                            else{
                                if(CollUtil.isNotEmpty(ddList)){
                                    BigDecimal hxAmt = new BigDecimal(fcyatAmt);
                                    for (Map<String, Object> singleDd : ddList){
                                        BigDecimal fcytatAmtdd = new BigDecimal(singleDd.get("FCYTATAMT").toString());
                                        BigDecimal fcypmtrevAmtdd = new BigDecimal(singleDd.get("FCYPMTREVAMT").toString());

                                        BigDecimal subtract = fcytatAmtdd.subtract(fcypmtrevAmtdd);//剩余可核销的金额
                                        if(subtract.compareTo(BigDecimal.ZERO)<=0){
                                            continue;
                                        }
                                        if(hxAmt.compareTo(BigDecimal.ZERO)<=0){
                                            break;
                                        }

                                        BigDecimal thisHxAmt=BigDecimal.ZERO;
                                        if(hxAmt.compareTo(subtract)>0){
                                            hxAmt=hxAmt.subtract(subtract);
                                            thisHxAmt=subtract;
                                        }else{
                                            hxAmt = new BigDecimal(0);
                                            thisHxAmt=hxAmt;
                                        }

                                        //thishxAmt 加到FCYPMTREVAMT
                                        BigDecimal add = thisHxAmt.add(fcypmtrevAmtdd);
                                        UptBean ub1 = new UptBean("DCP_APPERD");
                                        //add Value
                                        ub1.addUpdateValue("FCYPMTREVAMT", new DataValue(add, Types.VARCHAR));

                                        //condition
                                        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                        ub1.addCondition("ACCOUNTID", new DataValue(accountId, Types.VARCHAR));
                                        ub1.addCondition("ITEM", new DataValue(singleDd.get("ITEM").toString(), Types.VARCHAR));
                                        ub1.addCondition("APNO", new DataValue(singleDd.get("APNO").toString(), Types.VARCHAR));
                                        this.addProcessData(new DataProcessBean(ub1));

                                        //记录
                                        ColumnDataValue writoffColumns = new ColumnDataValue();
                                        writoffColumns.add("EID", DataValues.newString(req.geteId()));
                                        writoffColumns.add("ACCOUNTID", DataValues.newString(accountId));
                                        writoffColumns.add("CORP", DataValues.newString(corp));
                                        writoffColumns.add("SOURCEORG", DataValues.newString(sourceOrg));
                                        writoffColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
                                        writoffColumns.add("WRTOFFNO", DataValues.newString(singleDd.get("APNO").toString()));
                                        writoffColumns.add("ITEM", DataValues.newString("1"));
                                        writoffColumns.add("TASKID", DataValues.newString("1"));
                                        writoffColumns.add("WRTOFFTYPE", DataValues.newString("10"));
                                        writoffColumns.add("WRTOFFBILLNO", DataValues.newString(apNo));
                                        writoffColumns.add("WRTOFFBILLITEM", DataValues.newString(singleDd.get("ITEM").toString()));
                                        writoffColumns.add("INSTPMTSEQ", DataValues.newString(""));
                                        writoffColumns.add("MEMO", DataValues.newString(""));
                                        writoffColumns.add("BSNO", DataValues.newString(""));
                                        writoffColumns.add("WRTOFFDIRECTION", DataValues.newString("1"));
                                        writoffColumns.add("APSUBJECTID", DataValues.newString(chSubjectId));
                                        writoffColumns.add("EMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
                                        writoffColumns.add("DEPARTNO", DataValues.newString(req.getDepartmentNo()));
                                        writoffColumns.add("CATEGORY", DataValues.newString(category));
                                        writoffColumns.add("SECREFNO", DataValues.newString(originNo));
                                        writoffColumns.add("GLNO", DataValues.newString(""));
                                        writoffColumns.add("PAYDUEDATE", DataValues.newString(payDueDate));
                                        writoffColumns.add("BIZPARTNERNO", DataValues.newString(bizpartnerNo));
                                        writoffColumns.add("RECEIVER", DataValues.newString(payee));
                                        writoffColumns.add("FREECHARS1", DataValues.newString("0"));
                                        writoffColumns.add("FREECHARS2", DataValues.newString("0"));
                                        writoffColumns.add("FREECHARS3", DataValues.newString("0"));
                                        writoffColumns.add("FREECHARS4", DataValues.newString("0"));
                                        writoffColumns.add("FREECHARS5", DataValues.newString("0"));
                                        writoffColumns.add("CURRENCY", DataValues.newString(currency));
                                        writoffColumns.add("EXRATE", DataValues.newDecimal(exRate));
                                        writoffColumns.add("FCYREVAMT", DataValues.newDecimal(thisHxAmt));
                                        writoffColumns.add("LCYREVAMT", DataValues.newDecimal(thisHxAmt));
                                        writoffColumns.add("FCYBTAXWRTOFFAMT", DataValues.newDecimal(thisHxAmt));
                                        writoffColumns.add("LCYBTAXWRTOFFAMT", DataValues.newDecimal(thisHxAmt));
                                        writoffColumns.add("INVOICENUMBER", DataValues.newString(""));
                                        writoffColumns.add("INVOICECODE", DataValues.newString(""));

                                        String[] writoffColumnNames = writoffColumns.getColumns().toArray(new String[0]);
                                        DataValue[] writoffDataValues = writoffColumns.getDataValues().toArray(new DataValue[0]);
                                        InsBean writoffib = new InsBean("DCP_APBILLWRTOFF", writoffColumnNames);
                                        writoffib.addValues(writoffDataValues);
                                        this.addProcessData(new DataProcessBean(writoffib));


                                    }

                                }

                            }
                        }
                    }

                    //冲暂估明细 一个暂估单只有一个账期 所以理论上只有一个冲暂估
                    if(zgAmtMap.size()>0){
                        for (Map.Entry<String, DCP_ApBillProcessReq.czgList> entry : zgAmtMap.entrySet()) {
                            DCP_ApBillProcessReq.czgList zgInfo = entry.getValue();
                            String yetApNo = zgInfo.getApNo();
                            //这两个是该笔应付单根据yetApNo汇总的
                            BigDecimal lcyBtAmt = zgInfo.getLcyBtAmt();
                            BigDecimal fcyBtAmt = zgInfo.getFcyBtAmt();

                            List<Map<String, Object>> yetApList = zgApList.stream().filter(x -> x.get("APNO").toString().equals(yetApNo)).collect(Collectors.toList());
                            if(yetApList.size()>0) {

                                BigDecimal yetExRate = new BigDecimal(yetApList.get(0).get("EXRATE").toString());
                                BigDecimal yetFcyBtAmt = new BigDecimal(yetApList.get(0).get("FCYBTAMT").toString());
                                BigDecimal yetLcyBtAmt = new BigDecimal(yetApList.get(0).get("LCYBTAMT").toString());
                                BigDecimal subtractLcyBtAmt = lcyBtAmt.subtract(yetLcyBtAmt);
                                BigDecimal subtractFcyBtAmt = fcyBtAmt.subtract(yetFcyBtAmt);
                                BigDecimal multiplyLcyBtAmt = subtractLcyBtAmt.multiply(yetExRate);
                                zgItem++;
                                //写入一笔数据到冲暂估明细
                                ColumnDataValue czgColumns = new ColumnDataValue();
                                czgColumns.add("EID", DataValues.newString(req.geteId()));
                                czgColumns.add("ACCOUNTID", DataValues.newString(accountId));
                                czgColumns.add("SOURCEORG", DataValues.newString(organizationNo));
                                czgColumns.add("APNO", DataValues.newString(apNo));
                                czgColumns.add("ITEM", DataValues.newString(zgItem));
                                czgColumns.add("ITEM2", DataValues.newString(""));
                                czgColumns.add("TASKID", DataValues.newString("7"));//采购暂估单
                                czgColumns.add("WRTOFFTYPE", DataValues.newString("01"));
                                czgColumns.add("WRTOFFQTY", DataValues.newString(""));
                                czgColumns.add("ESTBILLNO", DataValues.newString(yetApNo));
                                czgColumns.add("ESTBILLITEM", DataValues.newString(""));
                                czgColumns.add("PERIOD", DataValues.newString(""));//todo
                                czgColumns.add("TAXRATE", DataValues.newString(yetApList.get(0).get("TAXRATE").toString()));
                                czgColumns.add("WRTOFFAPSUBJECT", DataValues.newString(yetApList.get(0).get("APSUBJECTID").toString()));
                                czgColumns.add("WRTOFFBTAXSUBJECT", DataValues.newString(""));
                                czgColumns.add("WRTOFFTAXSUBJECT", DataValues.newString(""));
                                czgColumns.add("WRTOFFPRCDIFFSUBJECT", DataValues.newString(czgSubjectId));
                                czgColumns.add("WRTOFFEXDIFFSUBJECT", DataValues.newString(""));
                                czgColumns.add("DEPARTNO", DataValues.newString(yetApList.get(0).get("DEPARTNO").toString()));
                                czgColumns.add("TRADECUSTOMER", DataValues.newString(yetApList.get(0).get("TRADECUSTOMER").toString()));
                                czgColumns.add("PMTCUSTOMER", DataValues.newString(yetApList.get(0).get("PMTCUSTOMER").toString()));
                                czgColumns.add("CATEGORY", DataValues.newString(""));
                                czgColumns.add("EMPLOYEENO", DataValues.newString(yetApList.get(0).get("EMPLOYEENO").toString()));
                                czgColumns.add("FREECHARS1", DataValues.newString(yetApList.get(0).get("FREECHARS1").toString()));
                                czgColumns.add("FREECHARS2", DataValues.newString(yetApList.get(0).get("FREECHARS2").toString()));
                                czgColumns.add("FREECHARS3", DataValues.newString(yetApList.get(0).get("FREECHARS3").toString()));
                                czgColumns.add("FREECHARS4", DataValues.newString(yetApList.get(0).get("FREECHARS4").toString()));
                                czgColumns.add("FREECHARS5", DataValues.newString(yetApList.get(0).get("FREECHARS5").toString()));
                                czgColumns.add("MEMO", DataValues.newString(""));
                                czgColumns.add("FCYBILLPRICE", DataValues.newString(""));
                                czgColumns.add("EXRATE", DataValues.newString(yetApList.get(0).get("EXRATE").toString()));
                                czgColumns.add("FCYBTAMT", DataValues.newString(yetApList.get(0).get("FCYBTAMT").toString()));
                                czgColumns.add("FCYTAMT", DataValues.newString(yetApList.get(0).get("FCYTAMT").toString()));
                                czgColumns.add("FCYTATAMT", DataValues.newString(yetApList.get(0).get("FCYTATAMT").toString()));
                                czgColumns.add("FCYWRTOFFDIFFAMT", DataValues.newString(subtractFcyBtAmt));
                                czgColumns.add("LCYBILLPRICE", DataValues.newString(""));
                                czgColumns.add("LCYBTAMT", DataValues.newString(yetApList.get(0).get("LCYBTAMT").toString()));
                                czgColumns.add("LCYTAMT", DataValues.newString(yetApList.get(0).get("LCYTAMT").toString()));
                                czgColumns.add("LCYTATAMT", DataValues.newString(yetApList.get(0).get("LCYTATAMT").toString()));
                                czgColumns.add("LCYPRCDIFFAMT", DataValues.newString(subtractLcyBtAmt));
                                czgColumns.add("LCYEXDIFFAMT", DataValues.newString(multiplyLcyBtAmt));

                                String[] czgColumnNames = czgColumns.getColumns().toArray(new String[0]);
                                DataValue[] czgDataValues = czgColumns.getDataValues().toArray(new DataValue[0]);
                                InsBean czgib = new InsBean("DCP_APBILLESTDTL", czgColumnNames);
                                czgib.addValues(czgDataValues);
                                this.addProcessData(new DataProcessBean(czgib));
                            }

                        }
                    }

                    List<String> sourceOrgList = detailList.stream().map(x -> x.get("ORGANIZATIONNO").toString()).distinct().collect(Collectors.toList());
                    int predItem = 0;
                    for (String sourceOrg : sourceOrgList) {
                        List<Map<String, Object>> filterRows = detailList.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(sourceOrg)).collect(Collectors.toList());
                        predItem += 1;

                        //BigDecimal fcyTatAmt=new BigDecimal(0);
                        BigDecimal lcyTatAmt = new BigDecimal(0);
                        for (Map<String, Object> sourceOrgRow : filterRows) {

                            BigDecimal invcrncyatamt = new BigDecimal(sourceOrgRow.get("AMT").toString());
                            String detailDirection = sourceOrgRow.get("DIRECTION").toString();
                            if (detailDirection.equals("-1")) {
                                invcrncyatamt = invcrncyatamt.multiply(BigDecimal.ONE.negate());
                            }
                            lcyTatAmt = lcyTatAmt.add(invcrncyatamt);
                        }

                        int direction = 1;
                        if (lcyTatAmt.compareTo(BigDecimal.ZERO) < 0) {
                            direction = -1;
                        }

                        //apperd  根据sourceOrg 分多个账期
                        ColumnDataValue apperdColumns = new ColumnDataValue();
                        apperdColumns.add("EID", DataValues.newString(req.geteId()));
                        apperdColumns.add("ACCOUNTID", DataValues.newString(accountId));
                        apperdColumns.add("CORP", DataValues.newString(corp));
                        apperdColumns.add("SOURCEORG", DataValues.newString(sourceOrg));
                        apperdColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
                        apperdColumns.add("APNO", DataValues.newString(apNo));
                        apperdColumns.add("ITEM", DataValues.newString(predItem));
                        apperdColumns.add("INSTPMTSEQ", DataValues.newString(predItem));
                        apperdColumns.add("PAYTYPE", DataValues.newString("10"));
                        apperdColumns.add("PAYDUEDATE", DataValues.newString(payDueDate));
                        apperdColumns.add("BILLDUEDATE", DataValues.newString(payDueDate));
                        apperdColumns.add("DIRECTION", DataValues.newString(direction));
                        apperdColumns.add("FCYREQAMT", DataValues.newDecimal("0"));
                        apperdColumns.add("CURRENCY", DataValues.newString(currency));
                        apperdColumns.add("EXRATE", DataValues.newDecimal(exRate));
                        apperdColumns.add("FCYREVSEDRATE", DataValues.newDecimal("0"));
                        apperdColumns.add("FCYTATAMT", DataValues.newDecimal(lcyTatAmt));
                        apperdColumns.add("FCYPMTREVAMT", DataValues.newDecimal("0"));
                        apperdColumns.add("REVALADJNUM", DataValues.newString("0"));
                        apperdColumns.add("LCYTATAMT", DataValues.newDecimal(lcyTatAmt));
                        apperdColumns.add("LCYPMTREVAMT", DataValues.newDecimal(""));
                        apperdColumns.add("PAYDATENO", DataValues.newString(payDateNo));
                        apperdColumns.add("PMTCATEGORY", DataValues.newString("3"));
                        apperdColumns.add("PURORDERNO", DataValues.newString(""));
                        apperdColumns.add("APSUBJECTID", DataValues.newString(chSubjectId));
                        apperdColumns.add("INVOICENUMBER", DataValues.newString(invoiceNumber));
                        apperdColumns.add("INVOICECODE", DataValues.newString(invoiceCode));
                        apperdColumns.add("INVOICEDATE", DataValues.newString(invoiceDate));

                        String[] apperdColumnNames = apperdColumns.getColumns().toArray(new String[0]);
                        DataValue[] apperdDataValues = apperdColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean apperdib = new InsBean("DCP_APPERD", apperdColumnNames);
                        apperdib.addValues(apperdDataValues);
                        this.addProcessData(new DataProcessBean(apperdib));
                    }

                    //应付单明细汇总
                    List<Map> sumCollect = apBillDetailList.stream().map(x -> {
                        Map map = new HashMap();
                        map.put("sourceNo", x.getSourceNo());
                        map.put("sourceOrg", x.getSourceOrg());
                        map.put("category", x.getCateGory());
                        map.put("isGift", x.getIsGift());
                        map.put("fee",x.getFee());
                        //map.put("currency",x.get("CURRENCY").toString());
                        map.put("exRate",x.getExRate());
                        map.put("isRevEst",x.getIsRevEst());
                        //map.put("taxCode",);
                        map.put("taxRate",x.getTaxRate());
                        map.put("departNo",x.getDepartId());
                        return map;
                    }).distinct().collect(Collectors.toList());
                    int sumItem=0;
                    for (Map sumCollectMap : sumCollect) {
                        String sourceNo = sumCollectMap.get("sourceNo").toString();
                        String category = sumCollectMap.get("category").toString();
                        String sourceOrg = sumCollectMap.get("sourceOrg").toString();
                        String isGift = sumCollectMap.get("isGift").toString();
                        String fee = sumCollectMap.get("fee").toString();
                        //String detailTaxCode =  sumCollectMap.get("taxCode").toString();
                        String detailTaxRate = sumCollectMap.get("taxRate").toString();
                        String departNo = sumCollectMap.get("departNo").toString();
                        String isRevEst = sumCollectMap.get("isRevEst").toString();
                        String invSubject = "";
                        if (CollUtil.isNotEmpty(categorySubjectList)) {
                            List<Map<String, Object>> categoryFilter = categorySubjectList.stream().filter(x -> x.get("CATEGORY").toString().equals(category)).collect(Collectors.toList());
                            if (categoryFilter.size() > 0) {
                                invSubject = categoryFilter.get(0).get("INVSUBJECT").toString();
                            }
                        }
                        String taxSubject = "";
                        if (CollUtil.isNotEmpty(taxSubjectList)) {
                            List<Map<String, Object>> taxSubjectFilter = taxSubjectList.stream().filter(x -> x.get("TAXCODE").toString().equals(taxCode) && x.get("SETUPTYPE").toString().equals("1")).collect(Collectors.toList());
                            if (taxSubjectFilter.size() > 0) {
                                taxSubject = taxSubjectFilter.get(0).get("SUBJECTID").toString();
                            }
                        }

                        sumItem++;
                        List<DCP_ApBillProcessReq.ApBillDetailList> collect = apBillDetailList.stream().filter(
                                x -> x.getSourceNo().equals(sourceNo)
                                && x.getSourceOrg().equals(sourceOrg)
                                && x.getCateGory().equals(category)
                                && x.getIsGift().equals(isGift)
                                && x.getFee().equals(fee)
                                &&x.getIsRevEst().equals(isRevEst)
                                &&x.getTaxRate().equals(detailTaxRate)
                                &&x.getDepartId().equals(departNo)
                        ).collect(Collectors.toList());

                        BigDecimal amt = collect.stream().map(x -> new BigDecimal(x.getFCYTATAmt())).collect(Collectors.toList()).stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal preTaxAmt = collect.stream().map(x -> new BigDecimal(x.getFCYBTAmt())).collect(Collectors.toList()).stream().reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal subtract = amt.subtract(preTaxAmt);

                        String direction="-1";
                        if(amt.compareTo(BigDecimal.ZERO)>0){
                            direction="1";
                        }

                        ColumnDataValue sumColumns=new ColumnDataValue();
                        sumColumns.add("EID", DataValues.newString(req.geteId()));
                        sumColumns.add("ACCOUNTID", DataValues.newString(accountId));
                        sumColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
                        sumColumns.add("APNO", DataValues.newString(apNo));
                        sumColumns.add("ITEM", DataValues.newString(sumItem));
                        sumColumns.add("BIZPARTNERNO", DataValues.newString(bizpartnerNo));
                        sumColumns.add("RECEIVER", DataValues.newString(""));//todo
                        sumColumns.add("SOURCETYPE", DataValues.newString("1"));
                        sumColumns.add("SOURCENO", DataValues.newString(sourceNo));
                        sumColumns.add("SOURCEORG", DataValues.newString(sourceOrg));
                        sumColumns.add("FEE", DataValues.newString(fee));
                        sumColumns.add("DEPARTNO", DataValues.newString(departNo));
                        sumColumns.add("CATEGORY", DataValues.newString(category));
                        sumColumns.add("ISGIFT", DataValues.newString(isGift));
                        sumColumns.add("TAXRATE", DataValues.newString(detailTaxRate));
                        sumColumns.add("FEESUBJECTID", DataValues.newString(invSubject));
                        sumColumns.add("APSUBJECTID", DataValues.newString(chSubjectId));
                        sumColumns.add("TAXSUBJECTID", DataValues.newString(taxSubject));
                        sumColumns.add("DIRECTION", DataValues.newString(direction));
                        //sumColumns.add("ISREVEST", DataValues.newString(apBillSum.getIsRevEst()));
                        sumColumns.add("EMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
                        sumColumns.add("FreeChars1", DataValues.newString(""));
                        sumColumns.add("FreeChars2", DataValues.newString(""));
                        sumColumns.add("FreeChars3", DataValues.newString(""));
                        sumColumns.add("FreeChars4", DataValues.newString(""));
                        sumColumns.add("FreeChars5", DataValues.newString(""));
                        sumColumns.add("CURRENCY", DataValues.newString(currency));
                        sumColumns.add("EXRATE", DataValues.newString(exRate));
                        sumColumns.add("FCYBTAMT", DataValues.newString(preTaxAmt));
                        sumColumns.add("FCYTAMT", DataValues.newString(subtract));
                        sumColumns.add("FCYTATAMT", DataValues.newString(amt));
                        sumColumns.add("FCYSTDCOSTAMT", DataValues.newString("0"));
                        sumColumns.add("FCYACTCOSTAMT", DataValues.newString("0"));
                        sumColumns.add("LCYBTAMT", DataValues.newString(preTaxAmt));
                        sumColumns.add("LCYTAMT", DataValues.newString(subtract));
                        sumColumns.add("LCYTATAMT", DataValues.newString(amt));
                        sumColumns.add("LCYSTDCOSTAMT", DataValues.newString("0"));
                        sumColumns.add("LCYACTCOSTAMT", DataValues.newString("0"));


                        String[] sumColumnNames = sumColumns.getColumns().toArray(new String[0]);
                        DataValue[] sumDataValues = sumColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean sumib=new InsBean("DCP_APBILLDETAILSUM",sumColumnNames);
                        sumib.addValues(sumDataValues);
                        this.addProcessData(new DataProcessBean(sumib));

                    }





                }

                if(apDetailSum.size()>0){

                    //先查一下DCP_SETTLEDATA

                    StringBuffer sJoinno=new StringBuffer("");
                    for (DCP_ApBillProcessReq.ReconDetail apBillDetailSum : apDetailSum){
                        sJoinno.append(apBillDetailSum.getSourceNo()+",");

                    }
                    Map<String, String> mapOrder=new HashMap<String, String>();
                    mapOrder.put("SOURCENO", sJoinno.toString());

                    String withasSql_moSno=cm.getFormatSourceMultiColWith(mapOrder);
                    String setSql=" with p as ("+withasSql_moSno+") " +
                            "select * from DCP_SETTLEDATA a " +
                            " inner join p on p.SOURCENO=a.billno" +
                            " where a.eid='"+eId+"' ";
                    List<Map<String, Object>> setData=this.doQueryData(setSql,null);



                    for (DCP_ApBillProcessReq.ReconDetail apBillDetailSum : apDetailSum){
                        UptBean ub1 = new UptBean("DCP_RECONDETAIL");
                        ub1.addUpdateValue("APNO", new DataValue(apBillDetailSum.getApNo(), Types.VARCHAR));
                        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub1.addCondition("RECONNO", new DataValue(apBillDetailSum.getReconNo(), Types.VARCHAR));
                        ub1.addCondition("ITEM", new DataValue(apBillDetailSum.getItem(), Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub1));

                        List<Map<String, Object>> collect = setData.stream().filter(x -> x.get("BILLNO").toString().equals(apBillDetailSum.getSourceNo())
                                && x.get("ITEM").toString().equals(apBillDetailSum.getSourceItem())).collect(Collectors.toList());
                        if(collect.size()>0){
                            //BigDecimal apQty = new BigDecimal(collect.get(0).get("APQTY").toString());
                            //BigDecimal apAmt = new BigDecimal(collect.get(0).get("APAMT").toString());
                            BigDecimal unApAmt = new BigDecimal(collect.get(0).get("UNAPAMT").toString());

                            unApAmt=unApAmt.subtract(new BigDecimal(apBillDetailSum.getAmt()));
                            UptBean ub2 = new UptBean("DCP_SETTLEDATA");
                            ub2.addUpdateValue("APQTY", new DataValue(apBillDetailSum.getQty(), Types.VARCHAR));
                            ub2.addUpdateValue("APAMT", new DataValue(apBillDetailSum.getAmt(), Types.VARCHAR));
                            ub2.addUpdateValue("UNAPAMT", new DataValue(unApAmt, Types.VARCHAR));

                            ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub2.addCondition("BILLNO", new DataValue(apBillDetailSum.getSourceNo(), Types.VARCHAR));
                            ub2.addCondition("ITEM", new DataValue(apBillDetailSum.getSourceItem(), Types.VARCHAR));
                            this.addProcessData(new DataProcessBean(ub2));

                        }


                    }
                }

            }

        }



        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ApBillProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ApBillProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ApBillProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ApBillProcessReq req) throws Exception {
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
    protected TypeToken<DCP_ApBillProcessReq> getRequestType() {
        return new TypeToken<DCP_ApBillProcessReq>() {
        };
    }

    @Override
    protected DCP_ApBillProcessRes getResponseType() {
        return new DCP_ApBillProcessRes();
    }

    private String getApNo(DCP_ApBillProcessReq req) throws Exception{
        String apNo = "";
        String preFix="";
        //1：采购应付单，2：预付账款单，3：其他应付单，4 ：内部应付单，5：员工报销单，6：员工借款单，
        // 7：采购暂估单；8：预付待抵单；9：员工借支待抵单，10-应付核销单
        preFix = "APZK";  //采购应付单
        apNo=this.getOrderNO(req, preFix);

        return apNo;
    }

    private String getPayDateDueDate(DCP_ApBillProcessReq req,String date,String payDateNo) throws Exception{
        String sql="select * from DCP_PAYDATE a where a.eid='"+req.geteId()+"' and a.paydateno='"+payDateNo+"' ";
        List<Map<String, Object>> list = this.doQueryData(sql, null);
        if(list.size()>0){
            Map<String, Object> map = list.get(0);
            Date datef = DateFormatUtils.parseDate(date);
            Integer season = Integer.valueOf(map.get("PSEASONS").toString());
            Integer month = Integer.valueOf(map.get("PMONTHS").toString());
            Integer day = Integer.valueOf(map.get("PDAYS").toString());
            datef = DateFormatUtils.addMonth(datef, 3 * season);
            datef = DateFormatUtils.addMonth(datef, month);
            datef = DateFormatUtils.addDay(datef, day);
            String formatDate = DateFormatUtils.format(datef,"yyyyMMdd");
            return formatDate;
        }
        return date;
    }

}

