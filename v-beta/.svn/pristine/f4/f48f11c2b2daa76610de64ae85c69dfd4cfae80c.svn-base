package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ShopArBillProcessReq;
import com.dsc.spos.json.cust.res.DCP_ShopArBillProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DCP_ShopArBillProcess extends SPosAdvanceService<DCP_ShopArBillProcessReq, DCP_ShopArBillProcessRes> {
    @Override
    protected void processDUID(DCP_ShopArBillProcessReq req, DCP_ShopArBillProcessRes res) throws Exception {


        String accSql = "select ACCOUNTID,ARPARAMETER,CURRENCY,ISDEPOSITBYORDER,to_char(a.APCLOSINGDATE,'yyyyMMdd') as ARCLOSINGDATE " +
                " from DCP_ACOUNT_SETTING a " +
                " where a.eid='" + req.geteId() + "' and a.STATUS='100' and a.corp='" + req.getRequest().getCorp() + "'  and a.ACCTTYPE='1' ";

        List<Map<String, Object>> asData = doQueryData(accSql, null);
        if (CollectionUtils.isEmpty(asData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, req.getRequest().getCorp() + "对应主帐套未配置或未启用！");
        }
        String arCloseDate = asData.get(0).get("ARCLOSINGDATE").toString();
        if (DateFormatUtils.compareDate(req.getRequest().getBeginDate(), arCloseDate) < 0 ||
                DateFormatUtils.compareDate(req.getRequest().getEndDate(), arCloseDate) < 0) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "日期不可小于应收关账日:" + arCloseDate);
        }

        String createDate = DateFormatUtils.getNowPlainDate();
        String createTime = DateFormatUtils.getNowPlainTime();

        String accountId = asData.get(0).get("ACCOUNTID").toString();
        String arClosingDate = asData.get(0).get("ARCLOSINGDATE").toString();
        String arParameter = asData.get(0).get("ARPARAMETER").toString();
        String isDepositbyOrder = asData.get(0).get("ISDEPOSITBYORDER").toString();
        String currency = asData.get(0).get("CURRENCY").toString();


        String orgNo = req.getRequest().getShopId();
        if (StringUtils.isEmpty(orgNo)) {
            orgNo = req.getOrganizationNO();
        }

        String apSubjectSql = "select * from DCP_ARSETUPSUBJECT a where a.eid='" + req.geteId() + "' and a.accountid='" + accountId + "' and a.setuptype='1' ";
        List<Map<String, Object>> apSubjectList = this.doQueryData(apSubjectSql, null);

//        b. 【账款科目】SETUPITEM=1 对应维护的SUBJECTID
//        c. 【收入科目】SETUPITEM= 5 对应维护的SUBJECTID
        String srSubjectId = "";
        String zkSubjectId = "";
        String arSubjectId = "";
        if (CollUtil.isNotEmpty(apSubjectList)) {
            List<Map<String, Object>> chList = apSubjectList.stream().filter(x -> x.get("SETUPITEM").toString().equals("1")).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(chList)) {
                zkSubjectId = chList.get(0).get("SUBJECTID").toString();
                arSubjectId = chList.get(0).get("SUBJECTID").toString();
            }
            List<Map<String, Object>> zkList = apSubjectList.stream().filter(x -> x.get("SETUPITEM").toString().equals("5")).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(zkList)) {
                srSubjectId = zkList.get(0).get("SUBJECTID").toString();
            }

//            List<Map<String, Object>> czgList = apSubjectList.stream().filter(x -> x.get("SETUPITEM").toString().equals("8")).collect(Collectors.toList());
//            if (CollUtil.isNotEmpty(czgList)) {
//                arSubjectId = chList.get(0).get("SUBJECTID").toString();
//            }
        }

        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无符合条件数据！");
        }

        for (Map<String, Object> master : qData) {

            Map<String, Object> condition = new HashMap<>();
            condition.put("EID", master.get("EID"));
            condition.put("RECONNO", master.get("RECONNO"));
            String arNo = getOrderNO(req, "MDYS");

            String eid = master.get("EID").toString();
            String bizPartnerNo = master.get("BIZPARTNERNO").toString();

            List<Map<String, Object>> detail = MapDistinct.getWhereMap(qData, condition, true);

            int item = 0;
            ColumnDataValueExpend detailExpend = new ColumnDataValueExpend();
            for (Map<String, Object> oneDetail : detail) {

                ColumnDataValue dcp_arBillDetail = new ColumnDataValue();
                dcp_arBillDetail.add("EID", DataValues.newString(eid));
                dcp_arBillDetail.add("ACCOUNTID", DataValues.newString(accountId));
                dcp_arBillDetail.add("ARNO", DataValues.newString(arNo));
                dcp_arBillDetail.add("ITEM", DataValues.newString(++item));
                dcp_arBillDetail.add("ORGANIZATIONNO", DataValues.newString(orgNo));
                dcp_arBillDetail.add("BIZPARTNERNO", DataValues.newString(bizPartnerNo));
                dcp_arBillDetail.add("SOURCETYPE", DataValues.newString("14"));
                dcp_arBillDetail.add("SOURCENO", DataValues.newString(oneDetail.get("RECONNO")));
                dcp_arBillDetail.add("SOURCEITEM", DataValues.newString(oneDetail.get("ITEM")));
                dcp_arBillDetail.add("SOURCEORG", DataValues.newString(oneDetail.get("ORGANIZATIONNO")));
                dcp_arBillDetail.add("PLUNO", DataValues.newString(oneDetail.get("PLUNO")));
                dcp_arBillDetail.add("SPEC", DataValues.newString(oneDetail.get("SPEC")));
                dcp_arBillDetail.add("PRICEUNIT", DataValues.newString(oneDetail.get("PRICEUNIT")));
                dcp_arBillDetail.add("QTY", DataValues.newString(oneDetail.get("BILLQTY")));
//                dcp_arBillDetail.add("OOFNO", DataValues.newString(oneDetail.get("BILLQTY")));
//                dcp_arBillDetail.add("OOITEM", DataValues.newString(oneDetail.get("BILLQTY")));
                dcp_arBillDetail.add("CATEGORY", DataValues.newString(oneDetail.get("CATEGORY")));
//                dcp_arBillDetail.add("ISGIFT", DataValues.newString(oneDetail.get("CATEGORY")));
//                dcp_arBillDetail.add("BSNO", DataValues.newString(oneDetail.get("CATEGORY")));
                dcp_arBillDetail.add("TAXCODE", DataValues.newString(oneDetail.get("TAXCODE")));
                dcp_arBillDetail.add("TAXRATE", DataValues.newString(oneDetail.get("TAXRATE")));
                dcp_arBillDetail.add("REVSUBJECT", DataValues.newString(srSubjectId));
                dcp_arBillDetail.add("ARSUBJECTID", DataValues.newString(arSubjectId));
                dcp_arBillDetail.add("TAXSUBJECTID", DataValues.newString(zkSubjectId));

                double lcyBtAmt = Double.parseDouble(StringUtils.toString(oneDetail.get("BILLAMT"), "0"));
                int dir = lcyBtAmt > 0 ? 1 : -1;

                dcp_arBillDetail.add("DIRECTION", DataValues.newString(dir));
                dcp_arBillDetail.add("ISREVEST", DataValues.newString("N"));
//                dcp_arBillDetail.add("FREECHARS1", DataValues.newString(oneDetail.get("TAXCODE")));
//                dcp_arBillDetail.add("FREECHARS2", DataValues.newString(oneDetail.get("TAXCODE")));
//                dcp_arBillDetail.add("FREECHARS3", DataValues.newString(oneDetail.get("TAXCODE")));
//                dcp_arBillDetail.add("FREECHARS4", DataValues.newString(oneDetail.get("TAXCODE")));
//                dcp_arBillDetail.add("FREECHARS5", DataValues.newString(oneDetail.get("TAXCODE")));
                dcp_arBillDetail.add("CURRENCY", DataValues.newString(oneDetail.get("CURRENCY")));
                dcp_arBillDetail.add("BILLPRICE", DataValues.newString(oneDetail.get("BILLPRICE")));
                dcp_arBillDetail.add("EXRATE", DataValues.newString(1));
                dcp_arBillDetail.add("FCYBTAMT", DataValues.newString(oneDetail.get("PRETAXAMT")));
                dcp_arBillDetail.add("FCYTAMT", DataValues.newString(oneDetail.get("TAXAMT")));
                dcp_arBillDetail.add("FCYTATAMT", DataValues.newString(oneDetail.get("BILLAMT")));
                dcp_arBillDetail.add("PRICE", DataValues.newString(oneDetail.get("BILLPRICE")));
                dcp_arBillDetail.add("LCYBTAMT", DataValues.newString(oneDetail.get("PRETAXAMT")));
                dcp_arBillDetail.add("LCYTAMT", DataValues.newString(oneDetail.get("TAXAMT")));
                dcp_arBillDetail.add("LCYTATAMT", DataValues.newString(oneDetail.get("BILLAMT")));
//                dcp_arBillDetail.add("INVOICEQTY", DataValues.newString(oneDetail.get("TAXCODE")));
//                dcp_arBillDetail.add("INVOICEAMT", DataValues.newString(oneDetail.get("TAXCODE")));
                dcp_arBillDetail.add("INVCRNCYBTAMT", DataValues.newString(oneDetail.get("PRETAXAMT")));
                dcp_arBillDetail.add("INVCRNCYATAMT", DataValues.newString(oneDetail.get("BILLAMT")));
//                dcp_arBillDetail.add("CLASSNO", DataValues.newString(oneDetail.get("TAXCODE")));
//                dcp_arBillDetail.add("ADVSUBJECT", DataValues.newString(oneDetail.get("TAXCODE")));
//                dcp_arBillDetail.add("CARDNO", DataValues.newString(oneDetail.get("TAXCODE")));

                dcp_arBillDetail.add("STATUS", DataValues.newString("0"));
                dcp_arBillDetail.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
                dcp_arBillDetail.add("CREATE_DATE", DataValues.newString(createDate));
                dcp_arBillDetail.add("CREATE_TIME", DataValues.newString(createTime));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_ARBILLDETAIL", dcp_arBillDetail)));

                ColumnDataValue settledCondition = new ColumnDataValue();
                ColumnDataValue dcp_settleData = new ColumnDataValue();
                settledCondition.add("EID", DataValues.newString(eid));
                settledCondition.add("BILLNO", DataValues.newString(oneDetail.get("BILLNO")));
                settledCondition.add("ITEM", DataValues.newString(oneDetail.get("ITEM")));

                dcp_settleData.add("APQTY", DataValues.newString(oneDetail.get("BILLQTY")));
                dcp_settleData.add("APAMT", DataValues.newString(oneDetail.get("PRETAXAMT")));

                double unApAmt = Double.parseDouble(StringUtils.toString(oneDetail.get("UNAPAMT"), "0"));
                double apAmt = Double.parseDouble(StringUtils.toString(oneDetail.get("PRETAXAMT"), "0"));

                dcp_settleData.add("UNAPAMT", DataValues.newString(unApAmt - apAmt));

                this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_SETTLEDATA", settledCondition, dcp_settleData)));


                detailExpend.addWithSameColumn(dcp_arBillDetail);

                ColumnDataValue dcp_arbillwrtoff = new ColumnDataValue();
                dcp_arbillwrtoff.add("EID", DataValues.newString(oneDetail.get("EID")));
                dcp_arbillwrtoff.add("ACCOUNTID", DataValues.newString(accountId));
                dcp_arbillwrtoff.add("ORGANIZATIONNO", DataValues.newString(orgNo));
                dcp_arbillwrtoff.add("WRTOFFNO", DataValues.newString(arNo));
                dcp_arbillwrtoff.add("ITEM", DataValues.newString(item));
                dcp_arbillwrtoff.add("ACCORG", DataValues.newString(oneDetail.get("ORGANIZATIONNO")));
                dcp_arbillwrtoff.add("TASKID", DataValues.newString("4"));
                dcp_arbillwrtoff.add("WRTOFFTYPE", DataValues.newString("30"));
                dcp_arbillwrtoff.add("WRTOFFBILLNO", DataValues.newString(arNo));
                if ("2".equals(arParameter)) {
                    dcp_arbillwrtoff.add("WRTOFFBILLITEM", DataValues.newString(item));
                }
//                dcp_arbillwrtoff.add("INSTPMTSEQ",DataValues.newString(accountId));
                dcp_arbillwrtoff.add("SOURCEORG", DataValues.newString(oneDetail.get("ORGANIZATIONNO")));
//                dcp_arbillwrtoff.add("MEMO",DataValues.newString(accountId));
                dcp_arbillwrtoff.add("WRTOFFDIRECTION", DataValues.newString("C"));
                dcp_arbillwrtoff.add("ARSUBJECTID", DataValues.newString(arSubjectId));
                dcp_arbillwrtoff.add("EMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
                dcp_arbillwrtoff.add("DEPARTNO", DataValues.newString(req.getDepartmentNo()));
                dcp_arbillwrtoff.add("CATEGORY", DataValues.newString(oneDetail.get("CATEGORY")));
//                dcp_arbillwrtoff.add("SECREFNO", DataValues.newString(oneDetail.get("OOFNO")));
//                dcp_arbillwrtoff.add("GLNO", DataValues.newString(oneDetail.get("")));
//                dcp_arbillwrtoff.add("RECEIVER", DataValues.newString(oneDetail.get("RECEIVER")));
//                dcp_arbillwrtoff.add("FREECHARS1",DataValues.newString(accountId));
//                dcp_arbillwrtoff.add("FREECHARS2",DataValues.newString(accountId));
//                dcp_arbillwrtoff.add("FREECHARS3",DataValues.newString(accountId));
//                dcp_arbillwrtoff.add("FREECHARS4",DataValues.newString(accountId));
//                dcp_arbillwrtoff.add("FREECHARS5",DataValues.newString(accountId));
//                dcp_arbillwrtoff.add("INVOICENUMBER",DataValues.newString(accountId));
//                dcp_arbillwrtoff.add("INVOICECODE",DataValues.newString(accountId));
                dcp_arbillwrtoff.add("CURRENCY", DataValues.newString(oneDetail.get("CURRENCY")));
                dcp_arbillwrtoff.add("EXRATE", DataValues.newString(1));

                dcp_arbillwrtoff.add("FCYREVAMT", DataValues.newString(oneDetail.get("PRETAXAMT")));
                dcp_arbillwrtoff.add("LCYREVAMT", DataValues.newString(oneDetail.get("PRETAXAMT")));
                dcp_arbillwrtoff.add("FCYBTAXWRTOFFAMT", DataValues.newString(oneDetail.get("PRETAXAMT")));
                dcp_arbillwrtoff.add("LCYBTAXWRTOFFAMT", DataValues.newString(oneDetail.get("PRETAXAMT")));

                dcp_arbillwrtoff.add("BILLPRICE", DataValues.newString(oneDetail.get("BILLPRICE")));

                dcp_arbillwrtoff.add("FEE", DataValues.newString(oneDetail.get("FEE")));
                dcp_arbillwrtoff.add("DIRECTION", DataValues.newString(dir));
//                dcp_arbillwrtoff.add("PENDOFFSETNO",DataValues.newString(accountId));
//                dcp_arbillwrtoff.add("APNO",DataValues.newString(accountId));
//                dcp_arbillwrtoff.add("ADVNO",DataValues.newString(accountId));

//                dcp_arbillwrtoff.add("STATUS", DataValues.newString("0"));
                dcp_arbillwrtoff.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
                dcp_arbillwrtoff.add("CREATE_DATE", DataValues.newString(createDate));
                dcp_arbillwrtoff.add("CREATE_TIME", DataValues.newString(createTime));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_ARBILLWRTOFF", dcp_arbillwrtoff)));

            }

            //sum 条件：来源单号+来源组织+商品品类/费用编码+是否赠品+冲暂估否+税率+自由核算项
            List<ColumnDataValue> sumList = detailExpend.combineSumDirection("DIRECTION",
                    new String[]{"EID", "SOURCETYPE", "SOURCENO", "SOURCEORG", "CATEGORY", "ISGIFT", "ISREVEST", "TAXRATE"},
                    new String[]{"FCYBTAMT", "FCYTAMT", "FCYTATAMT", "LCYBTAMT", "LCYTAMT", "LCYTATAMT"}
            );

            item = 0;
            for (ColumnDataValue oneSum : sumList) {
                ColumnDataValue dcp_arBillDetailSum = oneSum.copyNew();

                dcp_arBillDetailSum.add("ACCOUNTID", DataValues.newString(accountId));
                dcp_arBillDetailSum.add("ORGANIZATIONNO", DataValues.newString(orgNo));
                dcp_arBillDetailSum.add("APNO", DataValues.newString(arNo));
                dcp_arBillDetailSum.add("ITEM", DataValues.newString(++item));
                dcp_arBillDetailSum.add("BIZPARTNERNO", DataValues.newString(master.get("BIZPARTNERNO")));
//                dcp_arBillDetailSum.add("RECEIVER", DataValues.newString(""));

//                dcp_arBillDetailSum.add("FEE", DataValues.newString(""));
                dcp_arBillDetailSum.add("DEPARTNO", DataValues.newString(req.getDepartmentNo()));

//                dcp_arBillDetailSum.add("TAXRATE", DataValues.newString(""));
                dcp_arBillDetailSum.add("FEESUBJECTID", DataValues.newString(zkSubjectId));
                dcp_arBillDetailSum.add("ARSUBJECTID", DataValues.newString(arSubjectId));
                dcp_arBillDetailSum.add("TAXSUBJECTID", DataValues.newString(srSubjectId));

                dcp_arBillDetailSum.add("EMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
//            dcp_arBillDetailSum.add("FREECHARS1",DataValues.newString(""));
//            dcp_arBillDetailSum.add("FREECHARS2",DataValues.newString(""));
//            dcp_arBillDetailSum.add("FREECHARS3",DataValues.newString(""));
//            dcp_arBillDetailSum.add("FREECHARS4",DataValues.newString(""));
//            dcp_arBillDetailSum.add("FREECHARS5",DataValues.newString(""));
                dcp_arBillDetailSum.add("CURRENCY", DataValues.newString(currency));
                dcp_arBillDetailSum.add("EXRATE", DataValues.newString("1"));

                dcp_arBillDetailSum.add("STATUS", DataValues.newString("0"));
                dcp_arBillDetailSum.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
                dcp_arBillDetailSum.add("CREATE_DATE", DataValues.newString(createDate));
                dcp_arBillDetailSum.add("CREATE_TIME", DataValues.newString(createTime));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_ARBILLDETAILSUM", dcp_arBillDetailSum)));

            }


            ColumnDataValue dcp_arBill = new ColumnDataValue();

            String bDate = master.get("BDATE").toString();
            String payDateNo = master.get("PAYDATENO").toString();
            String payDueDate = getPayDateDueDate(req, bDate, payDateNo);

            dcp_arBill.add("EID", DataValues.newString(master.get("EID")));
            dcp_arBill.add("CORP", DataValues.newString(master.get("CORP")));
            dcp_arBill.add("ACCOUNTID", DataValues.newString(accountId));
            dcp_arBill.add("ARNO", DataValues.newString(arNo));
            dcp_arBill.add("PDATE", DataValues.newString(bDate));
            dcp_arBill.add("TASKID", DataValues.newString("8"));
            dcp_arBill.add("ARTYPE", DataValues.newString("21"));
            dcp_arBill.add("ORGANIZATIONNO", DataValues.newString(master.get("ORGANIZATIONNO")));
            dcp_arBill.add("BIZPARTNERNO", DataValues.newString(master.get("BIZPARTNERNO")));
//            dcp_arBill.add("RECEIVER", DataValues.newString(master.get("BIZPARTNERNO")));
            dcp_arBill.add("PAYDATENO", DataValues.newString(payDateNo));
            dcp_arBill.add("PAYDUEDATE", DataValues.newString(payDueDate));
            dcp_arBill.add("TAXCODE", DataValues.newString(master.get("TAXCODE")));
            dcp_arBill.add("TAXRATE", DataValues.newString(master.get("TAXRATE")));
            dcp_arBill.add("SOURCETYPE", DataValues.newString("14"));
            dcp_arBill.add("SOURCENO", DataValues.newString(master.get("RECONNO")));
            dcp_arBill.add("ARSUBJECTID", DataValues.newString(master.get("ARSUBJECTID")));
            dcp_arBill.add("REVSUBJECT", DataValues.newString(master.get("REVSUBJECT")));
            dcp_arBill.add("CURRENCY", DataValues.newString(currency));
            dcp_arBill.add("EXRATE", DataValues.newString(1));
            dcp_arBill.add("FCYBTAMT", DataValues.newString(master.get("CURRRECONTAXAMT")));
            dcp_arBill.add("FCYTAMT", DataValues.newString(master.get("CURRRECONPRETAXAMT")));
            dcp_arBill.add("FCYREVAMT", DataValues.newString(0));
            dcp_arBill.add("FCYTATAMT", DataValues.newString(master.get("CURRRECONAMT")));
            dcp_arBill.add("LCYBTAMT", DataValues.newString(master.get("CURRRECONTAXAMT")));
            dcp_arBill.add("LCYTAMT", DataValues.newString(master.get("CURRRECONPRETAXAMT")));
            dcp_arBill.add("LCYREVAMT", DataValues.newString(0));
            dcp_arBill.add("LCYTATAMT", DataValues.newString(master.get("CURRRECONAMT")));

            dcp_arBill.add("STATUS", DataValues.newString("0"));
            dcp_arBill.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
            dcp_arBill.add("CREATE_DATE", DataValues.newString(createDate));
            dcp_arBill.add("CREATE_TIME", DataValues.newString(createTime));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_ARBILL", dcp_arBill)));

            ColumnDataValue dcp_arPerd = new ColumnDataValue();

            dcp_arPerd.add("EID", DataValues.newString(master.get("EID")));
            dcp_arPerd.add("ACCOUNTID", DataValues.newString(accountId));
            dcp_arPerd.add("CORP", DataValues.newString(req.getRequest().getCorp()));
            dcp_arPerd.add("ARNO", DataValues.newString(arNo));
            dcp_arPerd.add("ITEM", DataValues.newString(1));
            dcp_arPerd.add("INSTPMTSEQ", DataValues.newString(1));
            dcp_arPerd.add("PAYTYPE", DataValues.newString(10));
            dcp_arPerd.add("PAYDUEDATE", DataValues.newDate(DateFormatUtils.getDate(payDueDate)));
            dcp_arPerd.add("BILLDUEDATE", DataValues.newDate(DateFormatUtils.getDate(payDueDate)));
            dcp_arPerd.add("DIRECTION", DataValues.newString(1));
            dcp_arPerd.add("ORGANIZATIONNO", DataValues.newString(master.get("ORGANIZATIONNO")));
//            dcp_arPerd.add("INVOICENUMBER",DataValues.newString(master.get("ORGANIZATIONNO")));
//            dcp_arPerd.add("INVOICECODE",DataValues.newString(master.get("ORGANIZATIONNO")));
            dcp_arPerd.add("ACCORG", DataValues.newString(master.get("ORGANIZATIONNO")));
//            dcp_arPerd.add("INVOICEDATE", DataValues.newString(master.get("ORGANIZATIONNO")));
//            dcp_arPerd.add("BDATE",DataValues.newString(master.get("BDATE")));
//            dcp_arPerd.add("ACCDATE",DataValues.newString(master.get("ORGANIZATIONNO")));
//            dcp_arPerd.add("PAYDATE",DataValues.newString(master.get("ORGANIZATIONNO")));
//            dcp_arPerd.add("DEDDATE",DataValues.newString(master.get("ORGANIZATIONNO")));
            dcp_arPerd.add("CURRENCY", DataValues.newString(currency));
            dcp_arPerd.add("EXRATE", DataValues.newString(1));
//            dcp_arPerd.add("FCYREVSEDRATE", DataValues.newString(master.get("ORGANIZATIONNO")));
//            dcp_arPerd.add("REVALADJNUM", DataValues.newString(master.get("ORGANIZATIONNO")));
//            dcp_arPerd.add("FCYTATAMT", DataValues.newString(master.get("ORGANIZATIONNO")));
//            dcp_arPerd.add("FCYPMTREVAMT", DataValues.newString(master.get("ORGANIZATIONNO")));
//            dcp_arPerd.add("LCYREVALADJNUM", DataValues.newString(master.get("ORGANIZATIONNO")));
//            dcp_arPerd.add("LCYTATAMT", DataValues.newString(master.get("ORGANIZATIONNO")));
//            dcp_arPerd.add("LCYPMTREVAMT", DataValues.newString(master.get("ORGANIZATIONNO")));
//            dcp_arPerd.add("PAYDATENO", DataValues.newString(master.get("ORGANIZATIONNO")));
//            dcp_arPerd.add("PMTCATEGORY", DataValues.newString(master.get("ORGANIZATIONNO")));
//            dcp_arPerd.add("PONO", DataValues.newString(master.get("ORGANIZATIONNO")));
            dcp_arPerd.add("ARSUBJECTID", DataValues.newString(srSubjectId));

            dcp_arPerd.add("STATUS", DataValues.newString("0"));
            dcp_arPerd.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
            dcp_arPerd.add("CREATE_DATE", DataValues.newString(createDate));
            dcp_arPerd.add("CREATE_TIME", DataValues.newString(createTime));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_ARPERD", dcp_arPerd)));

            ColumnDataValue reconliationCondition = new ColumnDataValue();
            ColumnDataValue dcp_reconliation = new ColumnDataValue();

            reconliationCondition.add("EID", DataValues.newString(master.get("EID")));
            reconliationCondition.add("RECONNO", DataValues.newString(master.get("RECONNO")));

            dcp_reconliation.add("ARNO", DataValues.newString(arNo));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_RECONLIATION", reconliationCondition, dcp_reconliation)));

        }


        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    private String getPayDateDueDate(DCP_ShopArBillProcessReq req, String date, String payDateNo) throws Exception {
        String sql = "select * from DCP_PAYDATE a where a.eid='" + req.geteId() + "' and a.paydateno='" + payDateNo + "' ";
        List<Map<String, Object>> list = this.doQueryData(sql, null);
        if (!list.isEmpty()) {
            Map<String, Object> map = list.get(0);
            Date datef = DateFormatUtils.parseDate(date);
            Integer season = Integer.valueOf(map.get("PSEASONS").toString());
            Integer month = Integer.valueOf(map.get("PMONTHS").toString());
            Integer day = Integer.valueOf(map.get("PDAYS").toString());
            datef = DateFormatUtils.addMonth(datef, 3 * season);
            datef = DateFormatUtils.addMonth(datef, month);
            datef = DateFormatUtils.addDay(datef, day);
            String formatDate = DateFormatUtils.format(datef, "yyyyMMdd");
            return formatDate;
        }
        return date;
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ShopArBillProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ShopArBillProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ShopArBillProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ShopArBillProcessReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ShopArBillProcessReq> getRequestType() {
        return new TypeToken<DCP_ShopArBillProcessReq>() {
        };
    }

    @Override
    protected DCP_ShopArBillProcessRes getResponseType() {
        return new DCP_ShopArBillProcessRes();
    }

    @Override
    protected String getQuerySql(DCP_ShopArBillProcessReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT a.BIZPARTNERNO,b.*  ")
                .append(" FROM DCP_SHOPSETTBILL a ")
                .append(" INNER JOIN DCP_SHOPSETTBILLDETAIL b on a.EID=b.EID AND a.RECONNO=b.RECONNO ")
        ;

        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        querySql.append(" AND NVL(a.ARNO,'')=''");
        querySql.append(" AND a.STATUS='Y' ");
        querySql.append(" AND a.SOURCETYPE='15' ");

        if (StringUtils.isNotEmpty(req.getRequest().getReconNo())) {
            querySql.append(" AND a.RECONNO='").append(req.getRequest().getReconNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())) {
            querySql.append(" AND TO_CHAR('YYYYMMDD',a.BDATE)>='").append(DateFormatUtils.getPlainDate(req.getRequest().getBeginDate())).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getEndDate())) {
            querySql.append(" AND TO_CHAR('YYYYMMDD',a.BDATE)<='").append(DateFormatUtils.getPlainDate(req.getRequest().getEndDate())).append("'");
        }


        return querySql.toString();
    }
}
