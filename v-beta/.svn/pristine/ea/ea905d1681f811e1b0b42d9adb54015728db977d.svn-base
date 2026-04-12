package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_IniInvCostOpnQueryReq;
import com.dsc.spos.json.cust.res.DCP_IniInvCostOpnQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_IniInvCostOpnQuery extends SPosBasicService<DCP_IniInvCostOpnQueryReq, DCP_IniInvCostOpnQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_IniInvCostOpnQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_IniInvCostOpnQueryReq> getRequestType() {
        return new TypeToken<DCP_IniInvCostOpnQueryReq>() {
        };
    }

    @Override
    protected DCP_IniInvCostOpnQueryRes getResponseType() {
        return new DCP_IniInvCostOpnQueryRes();
    }

    @Override
    protected DCP_IniInvCostOpnQueryRes processJson(DCP_IniInvCostOpnQueryReq req) throws Exception {
        DCP_IniInvCostOpnQueryRes res = this.getResponseType();

        int totalRecords = 0;
        int totalPages = 0;
        //单头查询
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {
            //算總頁數
            String num = getQData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            Map<String, Boolean> distinct = new HashMap<>();
            distinct.put("ACCOUNTID", true);
            distinct.put("YEAR", true);
            distinct.put("PERIOD", true);
            distinct.put("COST_CALCULATION", true);
            List<Map<String, Object>> distinctData = MapDistinct.getMap(getQData, distinct);
            for (Map<String, Object> row : distinctData) {
                DCP_IniInvCostOpnQueryRes.Datas oneData = res.new Datas();
                res.getDatas().add(oneData);

                oneData.setAccount(row.get("ACCOUNT").toString());
                oneData.setAccountID(row.get("ACCOUNTID").toString());
                oneData.setYear(row.get("YEAR").toString());
                oneData.setPeriod(row.get("PERIOD").toString());
                oneData.setCost_Calculation(row.get("COST_CALCULATION").toString());
                oneData.setStatus(row.get("STATUS").toString());
                oneData.setTotQty(row.get("TOTQTY").toString());
                oneData.setTotCostAmt(row.get("TOTCOSTAMT").toString());

                oneData.setInvList(new ArrayList<>());

                Map<String, Object> condition = new HashMap<>();
                condition.put("ACCOUNTID", row.get("ACCOUNTID").toString());
                condition.put("YEAR", row.get("YEAR").toString());
                condition.put("PERIOD", row.get("PERIOD").toString());
                condition.put("COST_CALCULATION", row.get("COST_CALCULATION").toString());
                List<Map<String, Object>> detail = MapDistinct.getWhereMap(getQData, condition, true);
                for (Map<String, Object> oneDetail : detail) {
                    DCP_IniInvCostOpnQueryRes.InvList oneInv = res.new InvList();
                    oneData.getInvList().add(oneInv);

                    oneInv.setItem(oneDetail.get("ITEM").toString());
                    oneInv.setMaterial(oneDetail.get("MATERIAL").toString());
                    oneInv.setAvg_Price(oneDetail.get("AVG_PRICE").toString());
                    oneInv.setAvg_Price_Material(oneDetail.get("AVG_PRICE_MATERIAL").toString());
                    oneInv.setAvg_Price_Oem(oneDetail.get("AVG_PRICE_OEM").toString());
//                    oneInv.setAvg_Price_Man(oneDetail.get("AVG_PRICE_MATERIAL").toString());
                    oneInv.setAvg_Price_Exp1(oneDetail.get("AVG_PRICE_EXP1").toString());
                    oneInv.setAvg_Price_Exp2(oneDetail.get("AVG_PRICE_EXP2").toString());
                    oneInv.setAvg_Price_Exp3(oneDetail.get("AVG_PRICE_EXP3").toString());
                    oneInv.setAvg_Price_Exp4(oneDetail.get("AVG_PRICE_EXP4").toString());
                    oneInv.setAvg_Price_Exp5(oneDetail.get("AVG_PRICE_EXP5").toString());

                    oneInv.setCostDomainId(oneDetail.get("COSTDOMAINID").toString());
                    oneInv.setCostDomainDis(oneDetail.get("ORG_NAME").toString());
                    oneInv.setTotAmt(oneDetail.get("TOT_AMT").toString());
                    oneInv.setQty(oneDetail.get("QTY").toString());
                    oneInv.setProdNo(oneDetail.get("PRODNO").toString());
                    oneInv.setProdName(oneDetail.get("PLU_NAME").toString());
                    oneInv.setFeatureNo(oneDetail.get("FEATURENO").toString());
                    oneInv.setFeatureName(oneDetail.get("FEATURENAME").toString());
                    oneInv.setLabor(oneDetail.get("LABOR").toString());
                    oneInv.setOem(oneDetail.get("OEM").toString());
                    oneInv.setExp1(oneDetail.get("EXP1").toString());
                    oneInv.setExp2(oneDetail.get("EXP2").toString());
                    oneInv.setExp3(oneDetail.get("EXP3").toString());
                    oneInv.setExp4(oneDetail.get("EXP4").toString());
                    oneInv.setExp5(oneDetail.get("EXP5").toString());

                }
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

    protected String getQuerySumSql(DCP_IniInvCostOpnQueryReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT a.EID,a.ACCOUNTID,a.YEAR,a.PERIOD,a.COST_CALCULATION,SUM(QTY) TOTQTY,SUM(TOT_AMT) TOTCOSTAMT ")
                .append(" FROM DCP_INIINVCOSTOPN a ")
        ;
        querySql.append(" WHERE a.eid='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getAccountID())) {
            querySql.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountID()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getYear())) {
            querySql.append(" AND a.YEAR='").append(req.getRequest().getYear()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getPeriod())) {
            querySql.append(" AND a.PERIOD='").append(Integer.parseInt(req.getRequest().getPeriod())).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getCost_Calculation())) {
            querySql.append(" AND a.COST_CALCULATION='").append(req.getRequest().getCost_Calculation()).append("'");
        }

        querySql.append(" GROUP BY a.EID,a.ACCOUNTID,a.YEAR,a.PERIOD,a.COST_CALCULATION ");
        return querySql.toString();
    }

    @Override
    protected String getQuerySql(DCP_IniInvCostOpnQueryReq req) throws Exception {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        StringBuilder querySql = new StringBuilder();
        querySql.append("SELECT *  FROM (");
        querySql.append("  SELECT COUNT(*) OVER() NUM " +
                        " ,row_number() over(ORDER BY a.ACCOUNTID) rn " +
                        " ,a.* " +
                        " ,gl1.PLU_NAME,c.ORG_NAME,gfl1.FEATURENAME " +
                        " ,tp.TOTQTY,tp.TOTCOSTAMT " +
                        " FROM DCP_INIINVCOSTOPN a  ")
                .append(" LEFT JOIN DCP_GOODS_LANG gl1 on gl1.EID=a.EID and a.PRODNO=gl1.PLUNO AND gl1.LANG_TYPE='").append(req.getLangType()).append("'")
//                .append(" LEFT JOIN DCP_COSTDOMAIN c on c.EID=a.EID and a.COSTDOMAINID=c.COSTDOMAINID AND c.COSTDOMAINTYPE='1' ")  //已废弃
                .append(" LEFT JOIN DCP_ORG_LANG c on c.EID=a.EID and a.COSTDOMAINID=c.ORGANIZATIONNO  AND c.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_GOODS_FEATURE_LANG gfl1 on gfl1.EID=a.EID and a.PRODNO=gfl1.PLUNO AND gfl1.FEATURENO=a.FEATURENO AND gfl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN (").append(getQuerySumSql(req)).append(") tp on tp.eid=a.eid and tp.ACCOUNTID=a.ACCOUNTID and tp.YEAR=a.YEAR and tp.PERIOD=a.PERIOD and tp.COST_CALCULATION=a.COST_CALCULATION ")
        ;
        querySql.append(" WHERE a.eid='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getAccountID())) {
            querySql.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountID()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getYear())) {
            querySql.append(" AND a.YEAR='").append(req.getRequest().getYear()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getPeriod())) {
            querySql.append(" AND a.PERIOD='").append(Integer.parseInt(req.getRequest().getPeriod())).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getCost_Calculation())) {
            querySql.append(" AND a.COST_CALCULATION='").append(req.getRequest().getCost_Calculation()).append("'");
        }

        querySql.append("  )  a "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize) + ""
                + " ORDER BY rn ");
        return querySql.toString();
    }
}
