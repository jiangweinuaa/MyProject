package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_BankAccountQueryReq;
import com.dsc.spos.json.cust.res.DCP_BankAccountQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_BankAccountQuery extends SPosBasicService<DCP_BankAccountQueryReq, DCP_BankAccountQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_BankAccountQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_BankAccountQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_BankAccountQueryReq>() {
        };
    }

    @Override
    protected DCP_BankAccountQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_BankAccountQueryRes();
    }

    @Override
    protected DCP_BankAccountQueryRes processJson(DCP_BankAccountQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String sql = null;

        DCP_BankAccountQueryRes res = this.getResponse();
        int totalRecords = 0;                //总笔数
        int totalPages = 0;
        String cur_langType = req.getLangType();
        //查询原因码信息
        sql = this.getQuerySql(req);

        String[] conditionValues1 = {}; //查詢條件

        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, conditionValues1);
        if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
            //总页数
            String num = getQDataDetail.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            //单头主键字段
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
            condition.put("ACCOUNTNO", true);
            //调用过滤函数
            List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQDataDetail, condition);

            res.setDatas(new ArrayList<>());

            for (Map<String, Object> oneData : getQHeader) {
                DCP_BankAccountQueryRes.Account oneLv1 = res.new Account();
                oneLv1.setAccountName_lang(new ArrayList<DCP_BankAccountQueryRes.AccountLang>());
                String accountNo = String.valueOf(oneData.get("ACCOUNTNO"));
                oneLv1.setAccountCode(accountNo);

                oneLv1.setOrganizationNo(StringUtils.toString(oneData.get("ORGANIZATION"),""));
                oneLv1.setBankAccount(StringUtils.toString(oneData.get("BANKACCOUNT"),""));
                oneLv1.setBankNo(StringUtils.toString(oneData.get("BANKNO"),""));
                oneLv1.setBankName(StringUtils.toString(oneData.get("BANKNAME"),""));
                oneLv1.setAccountName(StringUtils.toString(oneData.get("ACCOUNTNAME"),""));
                oneLv1.setStatus(StringUtils.toString(oneData.get("STATUS"),""));
                oneLv1.setCreatorID(StringUtils.toString(oneData.get("CREATEOPID"),""));
                oneLv1.setCreatorName(StringUtils.toString(oneData.get("CREATEOPNAME"),""));
                oneLv1.setCreatorDeptID(StringUtils.toString(oneData.get("CREATEDEPID"),""));
                oneLv1.setCreatorDeptName(StringUtils.toString(oneData.get("CREATEDEPNAME"),""));
                oneLv1.setLastModifyID(StringUtils.toString(oneData.get("LASTMODIOPID"),""));
                oneLv1.setLastModifyName(StringUtils.toString(oneData.get("LASTMODIOPNAME"),""));
                oneLv1.setCreate_datetime(StringUtils.toString(oneData.get("CREATETIME"),""));
                oneLv1.setLastModify_datetime(StringUtils.toString(oneData.get("LASTMODITIME"),""));
                oneLv1.setOrgName(StringUtils.toString(oneData.get("ORGNAME"),""));
                oneLv1.setAccName(StringUtils.toString(oneData.get("ACCNAME"),""));


                //获取 relateOrg
                String relateSql = getShopAccountSql(accountNo);
                List<Map<String, Object>> getQDataDetail2 = this.doQueryData(relateSql, conditionValues1);
                oneLv1.setRelateOrg(String.valueOf(getQDataDetail2.size()));

                for (Map<String, Object> oneData2 : getQDataDetail) {
                    //过滤属于此单头的明细
                    if (!accountNo.equals(oneData2.get("ACCOUNTNO"))) {
                        continue;
                    }
                    DCP_BankAccountQueryRes.AccountLang oneLv2 = res.new AccountLang();

                    oneLv2.setName(StringUtils.toString(oneData2.get("NAME"),""));
                    oneLv2.setLangType(StringUtils.toString(oneData2.get("LANG_TYPE"),""));

                    oneLv1.getAccountName_lang().add(oneLv2);
                    oneLv2 = null;
                }
                res.getDatas().add(oneLv1);
                oneLv1 = null;
            }
        } else {
            res.setDatas(new ArrayList<DCP_BankAccountQueryRes.Account>());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_BankAccountQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String sql = null;

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;


        String langType = req.getLangType();
        String status = null;
        String bankCode = null;

        String keyTxt = null;// req.getKeyTxt();
        if (req.getRequest() != null) {
            status = req.getRequest().getStatus();
            keyTxt = req.getRequest().getKeyTxt();
            bankCode = req.getRequest().getBankCode();
        }


        String eId = req.geteId();
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT *  FROM ("
                + " SELECT COUNT(DISTINCT a.ACCOUNTNO ) OVER() NUM ,dense_rank() over(ORDER BY a.ACCOUNTNO) rn,ACCOUNTNO,  "
                + " ORGANIZATION , BANKNO , BANKACCOUNT , ACCOUNTNAME , STATUS , NAME,LANG_TYPE ,EID,CREATEOPID,CREATEDEPTID,"
                + " CREATETIME,LASTMODIOPID,LASTMODITIME,BANKNAME,CREATEOPNAME,LASTMODIOPNAME,CREATEDEPTNAME,ACCNAME,ORGNAME  "
                +" FROM ("
                + "  SELECT a.EID,a.ACCOUNTNO,a.ORGANIZATION,a.BANKNO,a.BANKACCOUNT,a.ACCOUNTNAME,a.STATUS,b.NAME, "
                + "  b.LANG_TYPE,a.CREATETIME,a.LASTMODITIME, "
                + "  a.CREATEOPID,e.NAME AS CREATEOPNAME,a.LASTMODIOPID,f.NAME AS LASTMODIOPNAME, "
                + "  a.CREATEDEPTID,g.DEPARTNAME AS CREATEDEPTNAME,c.FULLNAME as BANKNAME, "
                + "  b.NAME AS ACCNAME,f.ORG_NAME ORGNAME "
                + "  FROM dcp_Account a LEFT JOIN dcp_Account_lang b ON a.EID = b.EID AND a.accountno = b.accountno "
                + "  LEFT JOIN DCP_BANK_LANG c ON c.EID=a.EID AND c.BANKNO=a.BANKNO AND c.LANG_TYPE=b.LANG_TYPE "
                + "  LEFT JOIN DCP_EMPLOYEE e ON e.EID=a.EID and e.EMPLOYEENO =a.CREATEOPID "
                + "  LEFT JOIN DCP_EMPLOYEE f ON f.EID=a.EID and f.EMPLOYEENO =a.LASTMODIOPID "
                + "  LEFT JOIN DCP_DEPARTMENT_LANG g on g.EID=b.EID AND g.DEPARTNO=a.CREATEDEPTID AND g.LANG_TYPE=b.LANG_TYPE  "
                + "  LEFT JOIN DCP_ORG_LANG f on f.eid=b.eid AND f.ORGANIZATIONNO=a.ORGANIZATION AND f.LANG_TYPE=b.LANG_TYPE "
                + "  ) a WHERE EID = '" + eId + "'");

        if (StringUtils.isNotEmpty(bankCode)) {
            sqlbuf.append(" AND BANKNO='").append(bankCode).append("' ");
        }

        if (StringUtils.isNotEmpty(bankCode)) {
            sqlbuf.append(" AND status='").append(status).append("' ");
        }

        if (StringUtils.isNotEmpty(keyTxt)) {
            sqlbuf.append(" AND (( NAME like  '%%").append(keyTxt).append("%%'   AND LANG_TYPE = '").append(langType).append("' ) or ACCOUNTNO like '%%").append(keyTxt).append("%%' ) ");
        }

        sqlbuf.append("  )  a "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize) + ""
                + " ORDER BY ACCOUNTNO ");

        sql = sqlbuf.toString();
        return sql;
    }

    private String getShopAccountSql(String account) {
        return String.format("select * from DCP_SHOP_ACCOUNT where account='%s'", account);
    }


}
