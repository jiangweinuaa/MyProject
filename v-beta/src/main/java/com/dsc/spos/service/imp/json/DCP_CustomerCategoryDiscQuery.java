package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CustomerCategoryDiscQueryReq;
import com.dsc.spos.json.cust.res.DCP_CustomerCategoryDiscQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_CustomerCategoryDiscQuery extends SPosBasicService<DCP_CustomerCategoryDiscQueryReq, DCP_CustomerCategoryDiscQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_CustomerCategoryDiscQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");


        return isFail;
    }

    @Override
    protected TypeToken<DCP_CustomerCategoryDiscQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_CustomerCategoryDiscQueryReq>() {
        };
    }

    @Override
    protected DCP_CustomerCategoryDiscQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_CustomerCategoryDiscQueryRes();
    }

    @Override
    protected DCP_CustomerCategoryDiscQueryRes processJson(DCP_CustomerCategoryDiscQueryReq req) throws Exception {
        String sql = null;
        DCP_CustomerCategoryDiscQueryRes res = null;
        res = this.getResponse();

        sql = this.getQuerySql(req);
        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);

        res.setDatas(new ArrayList<>());

        if (getQDataDetail != null && !getQDataDetail.isEmpty()) {
            for (Map<String, Object> map : getQDataDetail) {
                DCP_CustomerCategoryDiscQueryRes.Datas oneLv1 = res.new Datas();

                oneLv1.setCategory(map.get("CATEGORYID").toString());
                oneLv1.setCategoryName(map.get("CATEGORY_NAME").toString());
                oneLv1.setCustomerNo(map.get("CUSTOMERNO").toString());
//				oneLv1.setCustomerName(map.get("CUSTOMER_NAME").toString());
                oneLv1.setDiscRate(map.get("DISCRATE").toString());
                oneLv1.setStatus(map.get("STATUS").toString());

                res.getDatas().add(oneLv1);
                oneLv1 = null;
            }
        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_CustomerCategoryDiscQueryReq req) throws Exception {
        String keyTxt = req.getRequest().getKeyTxt();
        String customerNo = req.getRequest().getCustomerNo();
        String eId = req.geteId();
        String langType = req.getLangType();
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        int startRow = ((pageNumber - 1) * pageSize);
        startRow = ((pageNumber - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
        StringBuffer sqlbuf = new StringBuffer();
        String sql = null;

        sqlbuf.append("  SELECT a.*,d.category_name FROM dcp_customer_cate_disc a " +
                "  LEFT JOIN DCP_BIZPARTNER b on a.eid=b.eid and a.CUSTOMERNO=b.BIZPARTNERNO and a.CUSTOMERTYPE='2'" +
                "  left JOIN DCP_CUSTGROUP c on a.eid=c.eid and a.CUSTOMERNO=C.CUSTGROUPNO and a.CUSTOMERTYPE='1' " +
                "  LEFT JOIN dcp_category_lang d ON a.eid = d.eid AND a.categoryid = d.category AND d.lang_type = '" + langType + "'" +
                "  where a.eid='" + eId + "'"
                );

        if (StringUtils.isNotEmpty(req.getRequest().getCustomerType())) {
            sqlbuf.append(" and a.CUSTOMERTYPE = '" + req.getRequest().getCustomerType() + "'");
        }

        if (StringUtils.isNotEmpty(keyTxt)) {
            sqlbuf.append(" and (d.category like '%%" + keyTxt + "%%' or d.category_name like '%%" + keyTxt + "%%'" +
                    " or a.CUSTOMERNO like '%%" +keyTxt + "%%'" +
                    ")");
        }
        if (customerNo != null && customerNo.isEmpty() == false && customerNo.length() > 0) {
            sqlbuf.append(" and a.customerno='" + customerNo + "' ");
        }

        sql = sqlbuf.toString();


        return sql;
    }


}
