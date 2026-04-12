package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_OrgAccountQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrgAccountQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_OrgAccountQuery extends SPosBasicService<DCP_OrgAccountQueryReq, DCP_OrgAccountQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_OrgAccountQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_OrgAccountQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_OrgAccountQueryReq>() {
        };
    }

    @Override
    protected DCP_OrgAccountQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_OrgAccountQueryRes();
    }

    @Override
    protected DCP_OrgAccountQueryRes processJson(DCP_OrgAccountQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        DCP_OrgAccountQueryRes res = this.getResponse();

        DCP_OrgAccountQueryRes.level1Elm datas = res.new level1Elm();

        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if (getQData != null && !getQData.isEmpty()) {
            datas.setAccountList(new ArrayList<>());
            for (Map<String, Object> map : getQData) {
                DCP_OrgAccountQueryRes.account oneLv1 = res.new account();

                oneLv1.setShopID(StringUtils.toString(map.get("SHOPID"), ""));
                oneLv1.setShopID(StringUtils.toString(map.get("SHOPNAME"), ""));
                oneLv1.setAccount(StringUtils.toString(map.get("ACCOUNT"), ""));
                oneLv1.setBankNo(StringUtils.toString(map.get("BANKNO"), ""));
                oneLv1.setStatus(StringUtils.toString(map.get("STATUS"), ""));

                datas.getAccountList().add(oneLv1);
            }
        }
        res.setDatas(datas);
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_OrgAccountQueryReq req) throws Exception {
        // TODO Auto-generated method stub

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        String shopId = req.getShopId();
        String keyTxt = "";
        if (req.getReuqest().getKeyTxt() != null) {
            keyTxt = req.getReuqest().getKeyTxt();
        }

        String account = req.getReuqest().getAccount();
        String status = req.getReuqest().getStatus();
        String bankNo = req.getReuqest().getBankNo();
        String langType = req.getLangType();

        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(" SELECT * FROM ( ");
        sqlbuf.append("     select COUNT(1) OVER() NUM,dense_rank() over(ORDER BY a.SHOPID) rn, A.EID,A.SHOPID,A.ACCOUNT,A.BANKNO,A.STATUS, B.FULLNAME BANKNAME,b.LANG_TYPE,c. ORG_NAME from DCP_SHOP_ACCOUNT A ");
        sqlbuf.append("     LEFT JOIN Dcp_Bank_Lang b ON a.BANKNO=b.BANKNO AND a.EID=B.EID AND b.LANG_TYPE='" + langType + "'");
        sqlbuf.append("     LEFT JOIN DCP_ORG_LANG c ON c.ORGANIZATIONNO=a.SHOPID and c.LANG_TYPE=b.LANG_TYPE");
        sqlbuf.append("     WHERE A.EID='" + req.geteId() + "'");

        if (Check.isNotEmpty(keyTxt)) {
            sqlbuf.append(" and ( A.BANKNO like '%%" + keyTxt + "%%' " +
                    " or A.ACCOUNT like '%%" + keyTxt + "%%' " +
                    " or A.SHOPID like '%%" + keyTxt + "%%' " +
                    " or B.BANKNAME like '%%" + keyTxt + "%%' )");
        }

        if (Check.isNotEmpty(account)) {
            sqlbuf.append(" and A.ACCOUNT='" + account + "' ");
        }

        if (Check.isNotEmpty(status)) {
            sqlbuf.append(" and A.STATUS='" + status + "' ");
        }

        if (Check.isNotEmpty(bankNo)) {
            sqlbuf.append(" and A.BANKNO='" + bankNo + "' ");
        }

        sqlbuf.append("  ) a "
                + "  WHERE  rn> " + startRow + " and rn<= " + (startRow + pageSize) + ""
                + "  ORDER BY SHOPID ");

        return sqlbuf.toString();
    }

}
