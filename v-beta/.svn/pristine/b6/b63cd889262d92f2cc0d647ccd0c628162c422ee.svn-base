package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_BankQueryReq;
import com.dsc.spos.json.cust.res.DCP_BankQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_BankQuery extends SPosBasicService<DCP_BankQueryReq, DCP_BankQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_BankQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_BankQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_BankQueryReq>() {
        };
    }

    @Override
    protected DCP_BankQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_BankQueryRes();
    }

    @Override
    protected DCP_BankQueryRes processJson(DCP_BankQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String sql;

        DCP_BankQueryRes res = this.getResponse();
        int totalRecords;                //总笔数
        int totalPages;
        String cur_langType = req.getLangType();
        //查询原因码信息
        sql = this.getQuerySql(req);

        String[] conditionValues1 = {}; //查詢條件

        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, conditionValues1);


        if (getQDataDetail != null && !getQDataDetail.isEmpty()) {
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
            Map<String, Boolean> condition = new HashMap<>(); //查詢條件
            condition.put("EID", true);
            condition.put("BANKNO", true);

            //调用过滤函数
            List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQDataDetail, condition);

            res.setDatas(new ArrayList<>());

            for (Map<String, Object> oneData : getQHeader) {
                DCP_BankQueryRes.Bank oneLv1 = new DCP_BankQueryRes.Bank();
                String bankNo = String.valueOf(oneData.get("BANKNO"));

                oneLv1.setBanksname(new ArrayList<>());
                oneLv1.setBankfname(new ArrayList<>());

                oneLv1.setBankcode(bankNo);
                oneLv1.setStatus(String.valueOf(oneData.get("STATUS")));
                oneLv1.setCreate_datetime(null == oneData.get("CREATETIME") ? "" : oneData.get("CREATETIME").toString());
                oneLv1.setLastmodify_datetime(null == oneData.get("LASTMODITIME") ? "" : oneData.get("LASTMODITIME").toString());

                oneLv1.setCreatorID(null == oneData.get("CREATEOPID") ? "" : oneData.get("CREATEOPID").toString());
                oneLv1.setCreatorName(null == oneData.get("CREATEOPNAME") ? "" : oneData.get("CREATEOPNAME").toString());

                oneLv1.setCreatorDeptID(null == oneData.get("CREATEDEPID") ? "" : oneData.get("CREATEDEPID").toString());
                oneLv1.setCreatorDeptName(null == oneData.get("CREATEDEPTNAME") ? "" : oneData.get("CREATEDEPTNAME").toString());

                oneLv1.setLastmodifyID(null == oneData.get("CREATEDEPNAME") ? "" : oneData.get("CREATEDEPNAME").toString());
                oneLv1.setLastmodifyName(null == oneData.get("LASTMODIOPNAME") ? "" : oneData.get("LASTMODIOPNAME").toString());

                oneLv1.setStatus(String.valueOf(oneData.get("STATUS")));
                oneLv1.setLastmodifyID(null == oneData.get("LASTMODIOPID") ? "" : oneData.get("LASTMODIOPID").toString());
                oneLv1.setLastmodifyName(null == oneData.get("LASTMODIOPNAME") ? "" : oneData.get("LASTMODIOPNAME").toString());

                oneLv1.setNation(String.valueOf(oneData.get("NATION")));
                oneLv1.setEbankCode(null == oneData.get("EBANKNO") ? "" : oneData.get("EBANKNO").toString());
                oneLv1.setRelateAccount(null == oneData.get("relateAccount") ? "0" : oneData.get("relateAccount").toString());
                oneLv1.setRelatePartner(null == oneData.get("relatePartner") ? "0" : oneData.get("relatePartner").toString());


                for (Map<String, Object> oneData2 : getQDataDetail) {
                    //过滤属于此单头的明细
                    if (!bankNo.equals(oneData2.get("BANKNO"))) {
                        continue;
                    }
                    DCP_BankQueryRes.BanksName banksName = new DCP_BankQueryRes.BanksName();
                    DCP_BankQueryRes.BankfName bankfName = new DCP_BankQueryRes.BankfName();

                    String shortName = oneData2.get("SHORTNAME").toString();
                    String fullName = oneData2.get("FULLNAME").toString();
                    String langType = oneData2.get("LANGTYPE").toString();
                    if (cur_langType.equals(langType)) {
                        oneLv1.setBankname_s(shortName);
                        oneLv1.setBankname_f(fullName);
                    }

                    banksName.setName(shortName);
                    bankfName.setName(fullName);
                    banksName.setLangType(langType);
                    bankfName.setLangType(langType);

                    oneLv1.getBanksname().add(banksName);
                    oneLv1.getBankfname().add(bankfName);

                }
                res.getDatas().add(oneLv1);

            }
        } else {
            res.setDatas(new ArrayList<>());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_BankQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String sql = null;

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;


        String langType = req.getLangType();
        String status = null;

        String keyTxt = null;// req.getKeyTxt();
        if (req.getRequest() != null) {
            status = req.getRequest().getStatus();
            keyTxt = req.getRequest().getKeytxt();
        }

        String eId = req.geteId();

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM ("
                + " SELECT COUNT(DISTINCT a.BANKNO ) OVER() NUM ,dense_rank() over(ORDER BY a.BANKNO) rn,  "
                + " COUNT(DISTINCT BANKACCOUNT) over() relateAccount, "
                + " COUNT(DISTINCT ACCOUNTNO) over() relatePartner, "
                + " BANKNO , status , EID , NATION , EBANKNO , FULLNAME ,langType,SHORTNAME,CREATETIME,LASTMODITIME, "
                + " CREATEOPID,CREATEOPNAME,CREATEDEPTID,CREATEDEPTNAME,LASTMODIOPID,LASTMODIOPNAME "
                + " FROM ( "
                + "  SELECT a.BANKNO ,a.status ,a.EBANKNO,  a.EID ,a.NATION,"
                + "  b.FULLNAME AS FULLNAME, b.lang_type AS langType , b.SHORTNAME AS SHORTNAME,  "
                + " c.ACCOUNTNO,c.BANKACCOUNT, "
                + "  a.CREATEOPID,e.NAME AS CREATEOPNAME,a.LASTMODIOPID,f.NAME AS LASTMODIOPNAME, "
                + "  a.CREATEDEPTID,g.DEPARTNAME AS CREATEDEPTNAME, "
                + " TO_CHAR(a.CREATETIME, 'YYYY-MM-DD HH24:MI:SS') AS CREATETIME ,TO_CHAR(a.LASTMODITIME, 'YYYY-MM-DD HH24:MI:SS') AS LASTMODITIME"
                + "  FROM dcp_bank a LEFT JOIN dcp_bank_lang b ON a.EID = b.EID AND a.BANKNO = b.BANKNO "
                + "  LEFT JOIN dcp_account c on c.EID=a.EID and c.BANKNO=a.BANKNO "
                + "  LEFT JOIN DCP_EMPLOYEE e ON e.EID=a.EID and e.EMPLOYEENO=a.CREATEOPID "
                + "  LEFT JOIN DCP_EMPLOYEE f ON f.EID=a.EID and f.EMPLOYEENO=a.LASTMODIOPID "
                + "  LEFT JOIN DCP_DEPARTMENT_LANG g on g.EID=b.EID AND g.DEPARTNO=a.CREATEDEPTID AND g.LANG_TYPE=b.LANG_TYPE  "
                + "  ) a WHERE EID = '" + eId + "' ");

        if (StringUtils.isNotEmpty(keyTxt))
            sqlbuf.append(" AND ( FULLNAME like '%%" + keyTxt + "%%' or SHORTNAME like '%%" + keyTxt + "%%' or BANKNO like '%%" + keyTxt + "%%' ) "
                    + " AND langType = '" + langType + "' ");

        if (StringUtils.isNotEmpty(status))
            sqlbuf.append(" AND (status= ").append(status).append(")");

        sqlbuf.append("  )  a "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize) + ""
                + " ORDER BY BANKNO ");

        sql = sqlbuf.toString();
        return sql;
    }


}
