package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_FeeQueryReq;
import com.dsc.spos.json.cust.res.DCP_FeeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_FeeQuery extends SPosBasicService<DCP_FeeQueryReq, DCP_FeeQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_FeeQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_FeeQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_FeeQueryReq>() {
        };
    }

    @Override
    protected DCP_FeeQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_FeeQueryRes();
    }

    @Override
    protected DCP_FeeQueryRes processJson(DCP_FeeQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String sql = null;

        //查询条件
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        ;
        String langType_cur = req.getLangType();

        DCP_FeeQueryRes res = null;
        res = this.getResponse();

        //给分页字段赋值
        sql = this.getQuerySql_Count(req);            //查询总笔数

        String[] conditionValues_Count = {eId};            //查詢條件
        List<Map<String, Object>> getQData_Count = this.doQueryData(sql, conditionValues_Count);
        int totalRecords;                                //总笔数
        int totalPages;                                    //总页数
        if (getQData_Count != null && getQData_Count.isEmpty() == false) {
            Map<String, Object> oneData_Count = getQData_Count.get(0);
            String num = oneData_Count.get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
        } else {
            totalRecords = 0;
            totalPages = 0;
        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        sql = null;
        sql = this.getQuerySql(req);
        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
        if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
            //单头主键字段
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
            condition.put("FEE", true);
            //调用过滤函数
            List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQDataDetail, condition);
            res.setDatas(new ArrayList<>());
            for (Map<String, Object> oneData : getQHeader) {
                DCP_FeeQueryRes.Level1Elm oneLv1 = res.new Level1Elm();
                oneLv1.setDatas(new ArrayList<>());
                String fee = oneData.get("FEE").toString();

                oneLv1.setFee(oneData.get("FEE").toString());
                oneLv1.setAccountingPolicy(oneData.get("ACCOUNTINGPOLICY").toString());
                oneLv1.setIsInvoiceIssued(oneData.get("ISINVOICEISSUED").toString());
                oneLv1.setInSettlement(oneData.get("INSETTLEMENT").toString());
                oneLv1.setFeeType(oneData.get("FEE_TYPE").toString());
                oneLv1.setFeeName(oneData.get("FEE_NAME").toString());
                oneLv1.setFeeNature(oneData.get("FEENATURE").toString());
                oneLv1.setStatus(oneData.get("STATUS").toString());
                oneLv1.setTaxCode(oneData.get("TAXCODE").toString());
                oneLv1.setTaxName(StringUtils.toString(oneData.get("TAXNAME"),""));
                oneLv1.setFeeAllocation(StringUtils.toString(oneData.get("FEEALLOCATION"),""));
                oneLv1.setPriceCategory(StringUtils.toString(oneData.get("PRICECATEGORY"),""));
                oneLv1.setIsTourGroup(StringUtils.toString(oneData.get("ISTOURGROUP"),""));

//                oneLv1.setIsTourGroup(oneData.get("ISTOURGROUP").toString());

                for (Map<String, Object> oneData2 : getQDataDetail) {
                    if (fee.equals(oneData2.get("FEE").toString()) == false)
                        continue;
                    DCP_FeeQueryRes.Level2Elm oneLv2 = res.new Level2Elm();
                    String langtype = oneData2.get("LANGTYPE").toString();
                    String feeName = oneData2.get("LFEENAME").toString();

                    oneLv2.setStatus(oneData2.get("DETAILSTATUS").toString());
                    oneLv2.setFeeName(feeName);
                    oneLv2.setLangType(langtype);
                    //添加单身
                    oneLv1.getDatas().add(oneLv2);
                    oneLv2 = null;
                }
                //添加单头
                res.getDatas().add(oneLv1);
                oneLv1 = null;
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
    protected String getQuerySql(DCP_FeeQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String sql = null;
        //查询条件
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        ;
        String langType = req.getLangType();

        String keyTxt = req.getRequest().getKeyTxt();
        String feeType = req.getRequest().getFeeType();
        String status = req.getRequest().getStatus();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = ((pageNumber - 1) * pageSize);
        startRow = ((pageNumber - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM ("
                + " SELECT A.*,B.LANG_TYPE LANGTYPE,B.FEE_NAME AS LFEENAME,b.STATUS DETAILSTATUS,C.TAXNAME "
                + " FROM DCP_FEE A INNER JOIN DCP_FEE_LANG B ON A.FEE=B.FEE AND A.EID=B.EID AND B.LANG_TYPE='" + langType + "'"
                + " LEFT JOIN DCP_TAXCATEGORY_LANG C ON C.TAXCODE=A.TAXCODE AND C.TAXAREA='CN' AND C.EID=A.EID AND C.LANG_TYPE='" + langType + "'"
                + " WHERE A.EID='" + eId + "'"  );
        if (feeType != null && feeType.length() > 0) {
            sqlbuf.append(" and A.FEE_TYPE='" + feeType + "'");
        }
        if (status != null && status.length() > 0) {
            sqlbuf.append(" and A.STATUS='" + status + "'");
        }
        if (keyTxt != null && keyTxt.length() > 0) {
            sqlbuf.append(" and (A.FEE like '%%" + keyTxt + "%%' or A.FEE_NAME like '%%" + keyTxt + "%%')");
        }

        sqlbuf.append(" and A.FEE IN (SELECT FEE FROM ("
                + "SELECT RN,FEE FROM ("
                + "SELECT FEE,ROWNUM RN FROM ("
                + "SELECT DISTINCT A.FEE FROM DCP_FEE A INNER JOIN DCP_FEE_LANG B ON A.FEE=B.FEE AND A.EID=B.EID WHERE A.EID='" + eId + "'");
        if (feeType != null && feeType.length() > 0) {
            sqlbuf.append(" and A.FEE_TYPE='" + feeType + "'");
        }
        if (status != null && status.length() > 0) {
            sqlbuf.append(" and A.STATUS='" + status + "'");
        }
        if (keyTxt != null && keyTxt.length() > 0) {
            sqlbuf.append(" and (A.FEE like '%%" + keyTxt + "%%' or A.FEE_NAME like '%%" + keyTxt + "%%')");
        }
        sqlbuf.append(" order by FEE asc)");
        sqlbuf.append(") where rn>" + startRow + " AND rn <= " + (startRow + pageSize) + "");
        sqlbuf.append(")");
        sqlbuf.append(")");
        sqlbuf.append(") order by FEE asc");
        sql = sqlbuf.toString();
        return sql;
    }

    protected String getQuerySql_Count(DCP_FeeQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("");

        sqlbuf.append("select NUM from (SELECT COUNT(*) NUM FROM ("
                + "SELECT DISTINCT A.FEE FROM DCP_FEE A INNER JOIN DCP_FEE_LANG B ON A.FEE=B.FEE AND A.EID=B.EID WHERE A.EID=?");

        String keyTxt = req.getRequest().getKeyTxt();
        String feeType = req.getRequest().getFeeType();
        String status = req.getRequest().getStatus();
        if (feeType != null && feeType.length() > 0) {
            sqlbuf.append(" and A.FEE_TYPE='" + feeType + "'");
        }
        if (status != null && status.length() > 0) {
            sqlbuf.append(" and A.STATUS='" + status + "'");
        }
        if (keyTxt != null && keyTxt.length() > 0) {
            sqlbuf.append(" and (A.FEE like '%%" + keyTxt + "%%' or A.FEE_NAME like '%%" + keyTxt + "%%')");
        }
        sqlbuf.append(")");
        sqlbuf.append(")");
        sql = sqlbuf.toString();

        return sql;
    }
}
