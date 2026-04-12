package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_BankJournalQueryReq;
import com.dsc.spos.json.cust.res.DCP_BankJournalQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.BigDecimalUtils;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_BankJournalQuery extends SPosBasicService<DCP_BankJournalQueryReq, DCP_BankJournalQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_BankJournalQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_BankJournalQueryReq> getRequestType() {
        return new TypeToken<DCP_BankJournalQueryReq>() {
        };
    }

    @Override
    protected DCP_BankJournalQueryRes getResponseType() {
        return new DCP_BankJournalQueryRes();
    }

    @Override
    protected DCP_BankJournalQueryRes processJson(DCP_BankJournalQueryReq req) throws Exception {
        DCP_BankJournalQueryRes res = this.getResponseType();
        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        String beginDate = DateFormatUtils.getPlainDate(req.getRequest().getBeginDate());

        String lastMonth = DateFormatUtils.addMonth(beginDate, -1);

        String year = beginDate.substring(0, 4);  //年
        String month = beginDate.substring(4, 6);  //月
        String firstDay = year + month + "01";

        List<Map<String, Object>> beginningData = doQueryData(getQueryBankMonthData(req, lastMonth), null);
        List<Map<String, Object>> endData = null;
        if (!StringUtils.equals(firstDay, beginDate)) {
            endData = doQueryData(getQueryEndingSql(req, firstDay, beginDate), null);
        }

        String accountName = "";
        if (CollectionUtils.isEmpty(qData)) {
            String querySql = " SELECT NAME FROM DCP_ACCOUNT_LANG WHERE EID='" + req.geteId() + "' AND ACCOUNTNO='" + req.getRequest().getAccountCode() + "' AND LANG_TYPE='" + req.getLangType() + "'";
            List<Map<String, Object>> accountData = doQueryData(querySql, null);
            if (CollectionUtils.isNotEmpty(accountData)) {
                accountName = accountData.get(0).get("NAME").toString();
            }
        } else {
            accountName = qData.get(0).get("ACCOUNTNAME").toString();
        }

        DCP_BankJournalQueryRes.Datas oneData = res.new Datas();
        res.setDatas(oneData);
        oneData.setAccountCode(req.getRequest().getAccountCode());
        oneData.setAccName(accountName);
        oneData.setAccList(new ArrayList<>());

        //组期初数据
        DCP_BankJournalQueryRes.AccList beginAccList = res.new AccList();
        oneData.getAccList().add(beginAccList);//加入期初

        if (CollectionUtils.isEmpty(beginningData)) {
            beginAccList.setBeginAmt("0.0");
        } else {
            int digit = Integer.parseInt(beginningData.get(0).get("AMOUNTDIGIT").toString());
            double lcyamt_dr = 0;
            double lcyamt_cr = 0;
            if (CollectionUtils.isEmpty(endData)) {  //当前查询的日期是1号
                lcyamt_dr = Double.parseDouble(beginningData.get(0).get("LCYAMT_DR").toString());
                lcyamt_cr = Double.parseDouble(beginningData.get(0).get("LCYAMT_CR").toString());

            } else { //当前查询不是1号

                //汇总End
                for (Map<String, Object> oneEnd : endData) {
                    if ("1".equals(oneEnd.get("DWTYPE").toString())) {
                        lcyamt_dr += Double.parseDouble(oneEnd.get("SUMLCYAMT").toString());
                    } else {
                        lcyamt_cr += Double.parseDouble(oneEnd.get("SUMLCYAMT").toString());
                    }
                }

                lcyamt_dr += Double.parseDouble(beginningData.get(0).get("LCYAMT_DR").toString());
                lcyamt_cr += Double.parseDouble(beginningData.get(0).get("LCYAMT_CR").toString());

            }
            double beginAmt = BigDecimalUtils.sub(
                    lcyamt_dr,
                    lcyamt_cr,
                    digit);
            beginAccList.setBeginAmt(String.valueOf(beginAmt));
        }

        if (CollectionUtils.isEmpty(qData)) {
            beginAccList.setEndAmt(beginAccList.getBeginAmt());
        } else {
            double beginAmt = Double.parseDouble(beginAccList.getBeginAmt());
            for (Map<String, Object> oneDetail : qData) {

                DCP_BankJournalQueryRes.AccList oneAcc = res.new AccList();
                oneData.getAccList().add(oneAcc);

                oneAcc.setBeginAmt(beginAccList.getBeginAmt());
                oneAcc.setItem(oneDetail.get("ITEM").toString());
                oneAcc.setOrganizationNo(oneDetail.get("ORGANIZATIONNO").toString());
                oneAcc.setOrgName(oneDetail.get("ORGANIZATIONNAME").toString());

                oneAcc.setSourceOrg(oneDetail.get("SOURCEORG").toString());
                oneAcc.setSourceOrgName(oneDetail.get("SOURCEORGNAME").toString());
                oneAcc.setSourceNo(oneDetail.get("SOURCENO").toString());
                oneAcc.setSourceNoSeq(oneDetail.get("SOURCENOSEQ").toString());
                oneAcc.setBizPartnerNo(oneDetail.get("BIZPARTNERNO").toString());
                oneAcc.setBizPartnerName(oneDetail.get("BIZPARTNERNAME").toString());
//                oneAcc.setAccountDate(oneDetail.get("ACCOUNTDATE").toString());
                oneAcc.setDirection(oneDetail.get("DWTYPE").toString());
                oneAcc.setDepWdrawCode(oneDetail.get("DEPWDRAWCODE").toString());
                oneAcc.setDepWdrawName(oneDetail.get("DEPWDRAWNAME").toString());
//                oneAcc.setTaskId(oneDetail.get("TASKID").toString());
                oneAcc.setExRate(oneDetail.get("EXRATE").toString());
                oneAcc.setCurrency(oneDetail.get("CURRENCY").toString());
                oneAcc.setFCYAmt(oneDetail.get("FCYAMT").toString());
                oneAcc.setPayAmt(oneDetail.get("PAYAMT").toString());
                oneAcc.setRevAmt(oneDetail.get("REVAmt").toString());
                oneAcc.setGlNo(oneDetail.get("GLNO").toString());

                double lcyamt = Double.parseDouble(oneDetail.get("LCYAMT").toString());
                beginAmt -= lcyamt;
                oneAcc.setEndAmt(String.valueOf(beginAmt));

            }

        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }


    //月结档
    private String getQueryBankMonthData(DCP_BankJournalQueryReq req, String date) {

        date = DateFormatUtils.getPlainDate(date);

        String year = date.substring(0, 4);  //年
        String month = date.substring(4, 6);  //月

        StringBuilder querysql = new StringBuilder();

        querysql.append(" SELECT CORP,ORGANIZATIONNO,ACCOUNTCODE,YEAR,PERIOD,FCYAMT_DR,FCYAMT_CR,LCYAMT_DR,LCYAMT_CR ")
                .append(" ,b.AMOUNTDIGIT ")
                .append(" FROM DCP_BANKMONTHDATA a ")
                .append(" LEFT JOIN DCP_CURRENCY b on a.eid=b.eid and a.CURRENCY=b.CURRENCY and a.NATION='CN' ")
        ;
        querysql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (Check.isNotEmpty(req.getRequest().getAccountCode())) {
            querysql.append(" AND a.ACCOUNTCODE='").append(req.getRequest().getAccountCode()).append("'");
        }
        if (Check.isNotEmpty(year)) {
            querysql.append(" AND a.YEAR=").append(year);
        }
        if (Check.isNotEmpty(month)) {
            querysql.append(" AND a.PERIOD=").append(month);
        }

        return querysql.toString();
    }

    //汇总银存收缴单
    private String getQueryEndingSql(DCP_BankJournalQueryReq req, String beginDate, String endDate) {

        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT b.CORP,b.ORGANIZATIONNO,b.ACCOUNTCODE,c.DWTYPE,SUM(DWTYPE*b.LCYAMT) SUMLCYAMT ")
                .append(" FROM DCP_BANKRECEIPT a ")
                .append(" LEFT JOIN DCP_BANKRECEIPTDETAIL b on a.eid=b.eid and a.CMNO=b.CMNO and a.CORP=b.CORP ")
                .append(" LEFT JOIN DCP_DEPWDRAW c on b.eid=c.eid and b.DEPWDRAWCODE=c.DEPWDRAWCODE ")
                .append(" LEFT JOIN  ")
        ;

        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (Check.isNotEmpty(req.getRequest().getAccountCode())) {
            querySql.append(" AND a.ACCOUNTCODE='").append(req.getRequest().getAccountCode()).append("'");
        }
        if (Check.isNotEmpty(beginDate)) {
            querySql.append(" AND a.BDATE>='").append(beginDate).append("'");
        }

        if (Check.isNotEmpty(endDate)) {
            querySql.append(" AND a.BDATE<'").append(endDate).append("'");
        }

        querySql.append(" GROUP BY b.CORP,b.ORGANIZATIONNO,b.ACCOUNTCODE,c.DWTYPE ");

        return querySql.toString();
    }

    @Override
    protected String getQuerySql(DCP_BankJournalQueryReq req) throws Exception {

        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT a.BDATE,b.*,al.NAME,c.DEPWDRAWNAME,c.DWTYPE AS ACCOUNTNAME ")
                .append(" ,ol1.SNAME ORGANIZATIONNAME,ol2.SNAME CORPNAME,ol3.SNAME SOURCEORGNAME,bz.SNAME BIZPARTNERNAME  ")
                .append(" FROM DCP_BANKRECEIPT a ")
                .append(" LEFT JOIN DCP_BANKRECEIPTDETAIL b on a.eid=b.eid and a.CORP=b.CORP and a.CMNO=b.CMNO ")
                .append(" LEFT JOIN DCP_DEPWDRAW c on b.eid=c.eid and b.DEPWDRAWCODE=c.DEPWDRAWCODE ")
                .append(" LEFT JOIN DCP_BIZPARTNER bz1 on bz1.eid=b.eid and bz1.BIZPARTNERNO=b.BIZPARTNERNO ")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 on o1.eid=a.eid and ol1.ORGANIZATIONNO=a.ORGANIZATIONNO and ol1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol2 on o2.eid=a.eid and ol2.ORGANIZATIONNO=a.CORP and ol2.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol3 on o3.eid=b.eid and ol3.ORGANIZATIONNO=b.SOURCEORG and ol3.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ACCOUNT_LANG al on all.EID=a.EID and al1.ACCOUNTNO=a.ACCOUNTCODE and al1.LANG_TYPE='").append(req.getLangType()).append("'")

        ;
        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (Check.isNotEmpty(req.getRequest().getAccountCode())) {
            querySql.append(" AND a.ACCOUNTCODE='").append(req.getRequest().getAccountCode()).append("'");
        }

        if (Check.isNotEmpty(req.getRequest().getBeginDate())) {
            querySql.append(" AND a.BDATE>='").append(DateFormatUtils.getPlainDate(req.getRequest().getBeginDate())).append("'");
        }

        if (Check.isNotEmpty(req.getRequest().getEndDate())) {
            querySql.append(" AND a.BDATE<='").append(DateFormatUtils.getPlainDate(req.getRequest().getEndDate())).append("'");
        }

        querySql.append(" ORDER BY a.BDATE,b.ITEM ");

        return querySql.toString();
    }
}
