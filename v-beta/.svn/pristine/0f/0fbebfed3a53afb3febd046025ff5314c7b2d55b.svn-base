package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_AcctBankAccountQryReq;
import com.dsc.spos.json.cust.res.DCP_AcctBankAccountQryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_AcctBankAccountQry extends SPosBasicService<DCP_AcctBankAccountQryReq, DCP_AcctBankAccountQryRes> {
    @Override
    protected boolean isVerifyFail(DCP_AcctBankAccountQryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_AcctBankAccountQryReq> getRequestType() {
        return new TypeToken<DCP_AcctBankAccountQryReq>() {
        };
    }

    @Override
    protected DCP_AcctBankAccountQryRes getResponseType() {
        return new DCP_AcctBankAccountQryRes();
    }

    @Override
    protected DCP_AcctBankAccountQryRes processJson(DCP_AcctBankAccountQryReq req) throws Exception {
        DCP_AcctBankAccountQryRes res = this.getResponseType();

        List<Map<String, Object>> queryData = doQueryData(this.getQuerySql(req), null);
        int totalRecords = 0;    //总笔数
        int totalPages = 0;        //总页数

        res.setDatas(new ArrayList<>());
        if (CollectionUtils.isNotEmpty(queryData)) {
            String num = queryData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            for (Map<String, Object> oneMaster : queryData) {
                DCP_AcctBankAccountQryRes.Datas oneData = res.new Datas();
                res.getDatas().add(oneData);

                oneData.setAccountCode(oneMaster.get("ACCOUNTNO").toString());
                oneData.setAccName(oneMaster.get("NAME").toString());
                oneData.setAccountName(oneMaster.get("ACCOUNTNAME").toString());
                oneData.setOrganizationNo(oneMaster.get("ORGANIZATION").toString());
                oneData.setOrgName(oneMaster.get("ORGANIZATIONNAME").toString());
                oneData.setBankaccount(oneMaster.get("BANKACCOUNT").toString());
                oneData.setBankName(oneMaster.get("BANKNAME").toString());
//                oneData.setRelateOrg(oneMaster.get("BANKNAME").toString());
                oneData.setStatus(oneMaster.get("STATUS").toString());
                oneData.setCurrency(oneMaster.get("CURRENCY").toString());
                oneData.setSubjectId(oneMaster.get("SUBJECTID").toString());
                oneData.setSubjectName(oneMaster.get("SUBJECTNAME").toString());

                oneData.setCreatorID(oneMaster.get("CREATEOPID").toString());
                oneData.setCreatorName(oneMaster.get("CREATEOPNAME").toString());
                oneData.setCreatorDeptID(oneMaster.get("CREATEDEPTID").toString());
                oneData.setCreatorDeptName(oneMaster.get("CREATEDEPTNAME").toString());
                oneData.setCreate_datetime(oneMaster.get("CREATETIME").toString());
                oneData.setLastmodifyID(oneMaster.get("LASTMODIOPID").toString());
                oneData.setLastmodifyName(oneMaster.get("LASTMODIOPNAME").toString());
                oneData.setLastmodify_datetime(oneMaster.get("LASTMODITIME").toString());

                DCP_AcctBankAccountQryRes.AccountNameLang accountNameLang = res.new AccountNameLang();
                oneData.setAccountName_lang(accountNameLang);

                accountNameLang.setLangType(oneMaster.get("LANG_TYPE").toString());
                accountNameLang.setName(oneMaster.get("NAME").toString());
                
            }
        }

        res.setSuccess(true);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);


        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_AcctBankAccountQryReq req) throws Exception {
        StringBuilder querysql = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        querysql.append("SELECT *  FROM (")
                .append("  SELECT COUNT(*) OVER() NUM ,dense_rank() over(ORDER BY a.ACCOUNTNO) rn, ")
                .append(" ,a.* ")
                .append(" ,o1.CORP,ol1.ORG_NAME ORGANIZATIONNAME ")
                .append(" ,al1.LANG_TYPE,al1.NAME ")
                .append(" FROM DCP_ACCOUNT a ")
                .append(" LEFT JOIN DCP_ORG o1 on o1.eid=a.eid and o1.ORGANIZATIONNO =a.ORGANIZATION ")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 on ol1.eid=a.eid and ol1.ORGANIZATIONNO=a.ORGANIZATION ")
                .append(" LEFT JOIN DCP_ORG_LANG ol2 on ol2.eid=o1.eid and o1.CORP=ol2.ORGANIZATIONNO ")
                .append(" LEFT JOIN DCP_ACCOUNT_LANG al1 on al1.eid=a.eid and al1.ACCOUNTNO=a.ACCOUNTNO and al1.LANG_TYPE='").append(req.getLangType()).append("'")
        ;

        querysql.append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (Check.isNotEmpty(req.getRequest().getOrganizationNo())) {
            querysql.append(" AND a.ORGANIZATION='").append(req.getRequest().getOrganizationNo()).append("'");
        }
        if (Check.isNotEmpty(req.getRequest().getAccountCode())) {
            querysql.append(" AND a.ACCOUNTCODE='").append(req.getRequest().getAccountCode()).append("'");
        }
        if (Check.isNotEmpty(req.getRequest().getStatus())) {
            querysql.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }

        if (Check.isNotEmpty(req.getRequest().getCorp())) {
            querysql.append(" AND o1.CORP='").append(req.getRequest().getCorp()).append("'");
        }

        querysql.append("  )  a "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize) + ""
                + " ORDER BY ACCOUNTNO ");
        return querysql.toString();
    }
}
