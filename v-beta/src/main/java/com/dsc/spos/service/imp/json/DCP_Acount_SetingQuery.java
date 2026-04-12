package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_Acount_SetingQueryReq;
import com.dsc.spos.json.cust.res.DCP_Acount_SetingQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_Acount_SetingQuery extends SPosBasicService<DCP_Acount_SetingQueryReq, DCP_Acount_SetingQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_Acount_SetingQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_Acount_SetingQueryReq> getRequestType() {
        return new TypeToken<DCP_Acount_SetingQueryReq>() {
        };
    }

    @Override
    protected DCP_Acount_SetingQueryRes getResponseType() {
        return new DCP_Acount_SetingQueryRes();
    }

    @Override
    protected DCP_Acount_SetingQueryRes processJson(DCP_Acount_SetingQueryReq req) throws Exception {

        DCP_Acount_SetingQueryRes res = new DCP_Acount_SetingQueryRes();

        int totalRecords;                //总笔数
        int totalPages;
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);
        res.setDatas(new ArrayList<>());
        if (getData != null && !getData.isEmpty()) {
            //总页数
            String num = getData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> data : getData) {

                DCP_Acount_SetingQueryRes.Datas oneData = res.new Datas();
                oneData.setAccount(data.get("ACCOUNT").toString());
                oneData.setAccountID(data.get("ACCOUNTID").toString());

                oneData.setIsPropAutoCode(StringUtils.toString(data.get("ISPROPAUTOCODE"), ""));

                oneData.setFundCloseDate(DateFormatUtils.getDate(data.get("FUNDCLOSEDATE").toString()));
                oneData.setEnableDate(DateFormatUtils.getDate(data.get("ENABLEDATE").toString()));

                oneData.setCorp(StringUtils.toString(data.get("CORP"), ""));
                oneData.setCorpName(StringUtils.toString(data.get("CORPNAME"), ""));
                oneData.setWipLaborCostRule(StringUtils.toString(data.get("WIPLABORCOSTRULE"), ""));
                oneData.setAssetCurMth(StringUtils.toString(data.get("ASSETCURMTH"), ""));
                oneData.setAssetCurYear(StringUtils.toString(data.get("ASSETCURYEAR"), ""));
                oneData.setCostClosingDate(DateFormatUtils.getDate(data.get("COSTCLOSINGDATE").toString()));
                oneData.setClosingDate(DateFormatUtils.getDate(data.get("CLOSINGDATE").toString()));

                oneData.setApPaymentDate(StringUtils.toString(data.get("APPAYMENTDATE"), ""));

                oneData.setIsAssetDispProfit(StringUtils.toString(data.get("ISASSETDISPPROFIT"), ""));
                oneData.setArClosingDateSet(StringUtils.toString(data.get("ARCLOSINGDATESET"), ""));
                oneData.setApParameter(StringUtils.toString(data.get("APPARAMETER"), ""));
                oneData.setIsDepositByPurch(StringUtils.toString(data.get("ISDESPOSITBYPURCH"), ""));
                oneData.setFundCurPeriod(StringUtils.toString(data.get("FUNDCURPERIOD"), ""));
                oneData.setCostClosingDate(DateFormatUtils.getDate(data.get("CLOSINGDATE").toString()));
                oneData.setApClosingDate(DateFormatUtils.getDate(data.get("APCLOSINGDATE").toString()));
                oneData.setAssetClosingDate(DateFormatUtils.getDate(data.get("ASSETCLOSINGDATE").toString()));
                oneData.setArParameter(StringUtils.toString(data.get("ARPARAMETER"), ""));

                oneData.setLeasedAssetRecog(StringUtils.toString(data.get("LEASEDASSETRECOG"), ""));
                oneData.setIsImpairmentReverse(StringUtils.toString(data.get("ISIMPAIRMENTREVERSE"), ""));
                oneData.setIsSaleRetInflCost(StringUtils.toString(data.get("ISSALERETINFLCOST"), ""));
                oneData.setAssetTransfPrice(StringUtils.toString(data.get("ASSETTRANSFPRICE"), ""));
                oneData.setAcctType(StringUtils.toString(data.get("ACCTTYPE"), ""));
                oneData.setPaymentMethod(StringUtils.toString(data.get("PAYMENTMETHOD"), ""));
                oneData.setStatus(StringUtils.toString(data.get("STATUS"), ""));
                oneData.setAssetCurYear(StringUtils.toString(data.get("ASSETCURYEAR"), ""));
                oneData.setLaborCostRule(StringUtils.toString(data.get("LABORCOSTRULE"), ""));
                oneData.setArClosingDate(DateFormatUtils.getDate(data.get("ARCLOSINGDATE").toString()));

                oneData.setIsCardAutoCode(StringUtils.toString(data.get("ISCARDAUTOCODE"), ""));
                oneData.setIsPropAssetCodeMatch(StringUtils.toString(data.get("ISPROPASSETCODEMATCH"), ""));
                oneData.setInvCurrentYear(StringUtils.toString(data.get("INVCURRENTYEAR"), ""));
                oneData.setIsTransferInCost(StringUtils.toString(data.get("ISTRANSFERINCOST"), ""));
                oneData.setDepreciationItem(StringUtils.toString(data.get("DEPRECIATIONITEM"), ""));
                oneData.setApAcctDate(data.get("APACCTDATE").toString());

                oneData.setApClosingDateSet(StringUtils.toString(data.get("APCLOSINGDATESET"), ""));
                oneData.setCurrency(StringUtils.toString(data.get("CURRENCY"), ""));
                oneData.setInvCurrentperiod(StringUtils.toString(data.get("INVCURRENTPERIOD"), ""));
                oneData.setCurrentPeriod(StringUtils.toString(data.get("CURRENTPERIOD"), ""));
                oneData.setIsAssetDispTransfer(StringUtils.toString(data.get("ISASSETDISPTRANSFER"), ""));
                oneData.setIsDepositByOrder(StringUtils.toString(data.get("ISDEPOSITBYORDER"), ""));
                oneData.setApProvisionalTaxOpt(StringUtils.toString(data.get("APPROVISIONALTAXOPT"), ""));
                oneData.setCostCurrentYear(StringUtils.toString(data.get("COSTCURRENTYEAR"), ""));
                oneData.setInvClosingDate(DateFormatUtils.getDate(data.get("INVCLOSINGDATE").toString()));
                oneData.setArReceiptDate(data.get("ARRECEIPTDATE").toString());
                oneData.setArAcctDate(data.get("ARACCTDATE").toString());

                oneData.setFundCurYear(StringUtils.toString(data.get("FUNDCURYEAR"), ""));
                oneData.setAccount(StringUtils.toString(data.get("ACCOUNT"), ""));

                oneData.setCurrentYear(StringUtils.toString(data.get("CURRENTYEAR"), ""));
                oneData.setLaborCostRule(StringUtils.toString(data.get("LABORCOSTRULE"), ""));
                oneData.setCostCurrentMth(StringUtils.toString(data.get("COSTCURRENTMTH"), ""));
                oneData.setApProvisionalTaxType(StringUtils.toString(data.get("APPROVISIONALTAXTYPE"), ""));
                oneData.setCurrencyName(StringUtils.toString(data.get("CURRENCYNAME"), ""));
                oneData.setLaborCostAlloc(StringUtils.toString(data.get("LABORCOSTALLOC"), ""));
                oneData.setIsLaborcoStalloc(StringUtils.toString(data.get("ISLABORCOSTALLOC"), ""));
                oneData.setFXRefID(StringUtils.toString(data.get("FXREFID"), ""));
                oneData.setCoaRefID(StringUtils.toString(data.get("COAREFID"), ""));
                oneData.setFxType(StringUtils.toString(data.get("FXTYPE"), ""));
                oneData.setFxSource(StringUtils.toString(data.get("FXSOURCE"), ""));

                oneData.setCreatorID(data.get("CREATEOPID").toString());
                oneData.setCreatorName(data.get("CREATEOPNAME").toString());
                oneData.setCreatorDeptID(data.get("CREATEDEPTID").toString());
                oneData.setCreatorDeptName(data.get("CREATEDEPTNAME").toString());
                oneData.setCreate_datetime(data.get("CREATETIME").toString());
                oneData.setLastmodifyID(data.get("LASTMODIOPID").toString());
                oneData.setLastmodifyName(data.get("LASTMODIOPNAME").toString());
                oneData.setLastmodify_datetime(data.get("LASTMODITIME").toString());


                res.getDatas().add(oneData);
            }

            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
        }
        res.setSuccess(true);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_Acount_SetingQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        sb.append(" SELECT * FROM( ")
                .append("SELECT row_number() OVER (ORDER BY a.ACCOUNTID DESC) AS RN,COUNT(*) OVER ( ) NUM,A.*,")
                .append(" em1.name AS CREATEOPNAME,em2.name AS LASTMODIOPNAME,dd0.DEPARTNAME AS CREATEDEPTNAME, " +
                        " c.NAME CURRENCYNAME,ol1.ORG_NAME CORPNAME ")
                .append(" FROM DCP_ACOUNT_SETTING a " +
                        " LEFT JOIN DCP_employee em1 ON em1.eid = a.eid AND em1.employeeno = a.CREATEOPID" +
                        " LEFT JOIN DCP_employee em2 ON em2.eid = a.eid AND em2.employeeno = a.LASTMODIOPID" +
                        " LEFT JOIN dcp_department_lang dd0 ON dd0.eid = a.eid AND dd0.departno = a.CREATEDEPTID AND dd0.lang_type='" + req.getLangType() + "'" +
                        " LEFT JOIN DCP_ORG_LANG ol1 ON ol1.eid = a.eid AND ol1.ORGANIZATIONNO = a.CORP AND ol1.lang_type='" + req.getLangType() + "'" +
                        " LEFT JOIN DCP_CURRENCY_LANG c ON c.eid = a.eid AND c.CURRENCY = a.CURRENCY and nation='CN' AND c.lang_type='" + req.getLangType() + "'"

                );
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getAccount())){
            sb.append(" AND a.ACCOUNT like '%%").append(req.getRequest().getAccount()).append("%%'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())){
            sb.append(" AND a.STATUS = '").append(req.getRequest().getStatus()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getKeyTxt())){
            sb.append(" AND ( a.ACCOUNT LIKE '%%").append(req.getRequest().getKeyTxt()).append("%%'")
                    .append(" OR a.ACCOUNTID LIKE '%%").append(req.getRequest().getKeyTxt()).append("%%'")
//                    .append(" OR a.CORP LIKE '%%").append(req.getRequest().getKeyTxt()).append("%%'")
                    .append(")");
        }

        sb.append("  )  a "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY ACCOUNTID ");


        return sb.toString();

    }
}
