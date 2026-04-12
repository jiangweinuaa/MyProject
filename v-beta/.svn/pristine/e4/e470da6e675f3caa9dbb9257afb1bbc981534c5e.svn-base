package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_AccountQueryReq;
import com.dsc.spos.json.cust.res.DCP_AccountQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_AccountQuery extends SPosBasicService<DCP_AccountQueryReq, DCP_AccountQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_AccountQueryReq req) throws Exception {

        return false;
    }

    @Override
    protected TypeToken<DCP_AccountQueryReq> getRequestType() {

        return new TypeToken<DCP_AccountQueryReq>() {
        };
    }

    @Override
    protected DCP_AccountQueryRes getResponseType() {

        return new DCP_AccountQueryRes();
    }

    @Override
    protected DCP_AccountQueryRes processJson(DCP_AccountQueryReq req) throws Exception {
   
        String sql = null;

        DCP_AccountQueryRes res = this.getResponse();
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

            res.setDatas(new ArrayList<DCP_AccountQueryRes.Account>());

            for (Map<String, Object> oneData : getQHeader) {
                DCP_AccountQueryRes.Account oneLv1 = res.new Account();
                oneLv1.setAccountName_lang(new ArrayList<DCP_AccountQueryRes.AccountLang>());
                String accountNo = String.valueOf(oneData.get("ACCOUNTNO"));
                oneLv1.setAccountCode(accountNo);


                Object organization = oneData.get("ORGANIZATION");
                if (organization != null) {
                    oneLv1.setOrganizationNo(organization.toString());
                }
                Object bankno = oneData.get("BANKNO");
                if (bankno != null) {
                    oneLv1.setBankAccount(bankno.toString());
                }
                Object bankaccount = oneData.get("BANKACCOUNT");
                if (bankaccount != null) {
                    oneLv1.setBankAccount(bankaccount.toString());
                }
                Object accountname = oneData.get("ACCOUNTNAME");
                if (accountname != null) {
                    oneLv1.setAccountName(accountname.toString());
                }
                Object status = oneData.get("STATUS");
                if (status != null) {
                    oneLv1.setStatus(status.toString());
                }

                Object createopid = oneData.get("CREATEOPID");
                if (createopid != null) {
                    oneLv1.setCreatorID(createopid.toString());
                }
                Object createdepid = oneData.get("CREATEDEPID");
                if (createdepid != null) {
                    oneLv1.setCreatorDeptID(createdepid.toString());
                }
                Object createtime = oneData.get("CREATETIME");
                if (createtime != null) {
                    oneLv1.setCreate_datetime(createtime.toString());
                }
                Object lastmoditime = oneData.get("LASTMODITIME");
                if (lastmoditime != null) {
                    oneLv1.setLastModify_datetime(lastmoditime.toString());
                }
                Object lastmodifyopid = oneData.get("LASTMODIOPID");
                if (lastmodifyopid != null) {
                    oneLv1.setLastModifyID(lastmodifyopid.toString());
                }
                oneLv1.setCurrency(oneData.get("CURRENCY").toString());
                oneLv1.setCurrencyName(oneData.get("CURRENCYNAME").toString());
                oneLv1.setSubjectId(oneData.get("SUBJECTID").toString());
                oneLv1.setSubjectName(oneData.get("SUBJECTNAME").toString());


                //获取 relateOrg
                String relateSql = getShopAccountSql(accountNo);
                List<Map<String, Object>> getQDataDetail2 = this.doQueryData(relateSql, conditionValues1);
                oneLv1.setRelateOrg(String.valueOf(getQDataDetail2.size()));


                for (Map<String, Object> oneData2 : getQDataDetail) {
                    //过滤属于此单头的明细
                    if (!accountNo.equals(oneData2.get("ACCOUNTNO"))) {
                        continue;
                    }
                    DCP_AccountQueryRes.AccountLang oneLv2 = res.new AccountLang();

                    String name = oneData2.get("NAME").toString();
                    String langType = oneData2.get("LANG_TYPE").toString();
                    if (cur_langType.equals(langType)) {
                        oneLv1.setAccountName(name);
                    }

                    oneLv2.setName(name);
                    oneLv2.setLangType(langType);

                    oneLv1.getAccountName_lang().add(oneLv2);
                    oneLv2 = null;
                }
                res.getDatas().add(oneLv1);
                oneLv1 = null;
            }
        } else {
            res.setDatas(new ArrayList<DCP_AccountQueryRes.Account>());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {


    }

    @Override
    protected String getQuerySql(DCP_AccountQueryReq req) throws Exception {

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

        StringBuilder sqlbuf = new StringBuilder();
        sqlbuf.append("SELECT *  FROM (")
                .append(" SELECT COUNT(DISTINCT a.ACCOUNTNO ) OVER() NUM ," +
                        " dense_rank() over(ORDER BY a.ACCOUNTNO) rn,ACCOUNTNO,  " +
                        " ORGANIZATION , BANKNO , BANKACCOUNT , ACCOUNTNAME , STATUS , NAME,LANG_TYPE ,EID,CREATEOPID,CREATEDEPTID,CREATETIME,LASTMODIOPID,LASTMODITIME  FROM (" +
                        "  SELECT a.EID,a.ACCOUNTNO,a.ORGANIZATION,a.BANKNO,a.BANKACCOUNT,a.ACCOUNTNAME,a.STATUS,b.NAME, " +
                        "  a.CREATEOPID,a.CREATEDEPTID,a.CREATETIME,a.LASTMODIOPID,a.LASTMODITIME,b.LANG_TYPE" +
                        " ,a.CURRENCY,a.SUBJECTID,c.SUBJECTNAME,cl.NAME CURRENCYNAME ")
                .append("  FROM dcp_Account a ")
                .append(" LEFT JOIN dcp_Account_lang b ON a.EID = b.EID AND a.accountno = b.accountno " + "  ) a ")
                .append(" LEFT JOIN DCP_CURRENCY_LANG cl on cl.eid=a.eid and cl.CURRENCY=a.CURRENCY and cl.NATION='CN' and cl.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_COA c on a.eid=c.eid and c.SUBJECTID=a.SUBJECTID  ")
        ;

        sqlbuf.append(" WHERE EID = '").append(eId)
                .append("' AND BANKNO='").append(bankCode).append("' and status='").append(status).append("'  ");

        if (keyTxt != null && keyTxt.length() > 0)
            sqlbuf.append(" AND (( NAME like  '%%" + keyTxt + "%%'   AND LANG_TYPE = '" + langType + "' ) or ACCOUNTNO like '%%" + keyTxt + "%%' ) "
            );

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
