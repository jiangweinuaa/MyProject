package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_MiscInOutQueryReq;
import com.dsc.spos.json.cust.res.DCP_MiscInOutQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_MiscInOutQuery extends SPosBasicService<DCP_MiscInOutQueryReq, DCP_MiscInOutQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_MiscInOutQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_MiscInOutQueryReq> getRequestType() {
        return new TypeToken<DCP_MiscInOutQueryReq>() {
        };
    }

    @Override
    protected DCP_MiscInOutQueryRes getResponseType() {
        return new DCP_MiscInOutQueryRes();
    }

    @Override
    protected DCP_MiscInOutQueryRes processJson(DCP_MiscInOutQueryReq req) throws Exception {
        DCP_MiscInOutQueryRes res = this.getResponseType();
        int totalRecords = 0;        //总笔数
        int totalPages = 0;
        res.setDatas(new ArrayList<>());

        List<Map<String, Object>> queryData = this.doQueryData(getQuerySql(req), null);
        if (CollectionUtils.isNotEmpty(queryData)) {

            Map<String, Boolean> distinct = new HashMap<>();
            distinct.put("EID", true);
            distinct.put("REFERENCENO", true);
            distinct.put("YEAR", true);
            distinct.put("PERIOD", true);
            List<Map<String, Object>> distinctData = MapDistinct.getMap(queryData, distinct);
            for (Map<String, Object> data : distinctData) {

                DCP_MiscInOutQueryRes.Datas oneData = res.new Datas();
                res.getDatas().add(oneData);

                oneData.setStatus(data.get("STATUS").toString());
                oneData.setCorp(data.get("CORP").toString());
                oneData.setAccountID(data.get("ACCOUNTID").toString());
                oneData.setAccount(data.get("ACCOUNTNAME").toString());
                oneData.setType(data.get("BILLTYPE").toString());
                oneData.setBDate(data.get("BDATE").toString());
                oneData.setReferenceNo(data.get("REFERENCENO").toString());
                oneData.setYear(data.get("YEAR").toString());
                oneData.setPeriod(data.get("PERIOD").toString());

                oneData.setMiscList(new ArrayList<>());

                Map<String, Object> cond = new HashMap<>();
                cond.put("EID", data.get("EID").toString());
                cond.put("REFERENCENO", data.get("REFERENCENO").toString());
                List<Map<String, Object>> whereData = MapDistinct.getWhereMap(queryData, cond, true);

                for (Map<String, Object> whereData1 : whereData) {

                    DCP_MiscInOutQueryRes.MiscList oneMisc = res.new MiscList();
                    oneData.getMiscList().add(oneMisc);

                    oneMisc.setItem(whereData1.get("ITEM").toString());
                    oneMisc.setConfirm_Date(whereData1.get("CONFIRM_DATE").toString());
                    oneMisc.setOrganizationNo(whereData1.get("ORGANIZATIONNO").toString());
                    oneMisc.setOrg_Name(whereData1.get("ORG_NAME").toString());
                    oneMisc.setCostDomainId(whereData1.get("COSTDOMAINID").toString());
                    oneMisc.setCostDomainDis(whereData1.get("COSTDOMAINDIS").toString());
                    oneMisc.setInOutCode(whereData1.get("INOUTCODE").toString());
                    oneMisc.setProdNo(whereData1.get("PRODNO").toString());
                    oneMisc.setProdName(whereData1.get("PRODNAME").toString());
                    oneMisc.setFeatureNo(whereData1.get("FEATURENO").toString());
                    oneMisc.setWarehouseNo(whereData1.get("WAREHOUSENO").toString());
//                    oneMisc.setLocationName(whereData1.get("WAREHOUSENO").toString());
                    oneMisc.setBatch(whereData1.get("BATCH").toString());
//                    oneMisc.setBsName(whereData1.get("BATCH").toString());
                    oneMisc.setBizPartnerNo(whereData1.get("BIZPARTNERNO").toString());
                    oneMisc.setBizPartnerName(whereData1.get("BIZPARTNERNAME").toString());
                    oneMisc.setCostCenter(whereData1.get("COSTCENTER").toString());
                    oneMisc.setCostCenterNo(whereData1.get("COSTCENTERNO").toString());
                    oneMisc.setCategory(whereData1.get("CATEGORY").toString());
                    oneMisc.setInvSubject(whereData1.get("INVSUBJECT").toString());
//                    oneMisc.setInvSubjectName(whereData1.get("INVSUBJECT").toString());
                    oneMisc.setExpCostSubject(whereData1.get("EXPCOSTSUBJECT").toString());
//                    oneMisc.setField_5(whereData1.get("EXPCOSTSUBJECT").toString());
                    oneMisc.setIncomeSubject(whereData1.get("INCOMESUBJECT").toString());
                    oneMisc.setIncomeSubjectName(whereData1.get("INCOMESUBJECTNAME").toString());
                    oneMisc.setTransCurrency(whereData1.get("TRANSCURRENCY").toString());
                    oneMisc.setLocalCurrency(whereData1.get("LOCALCURRENCY").toString());
                    oneMisc.setExRate(whereData1.get("EXRATE").toString());
                    oneMisc.setBaseUnit(whereData1.get("BASEUNIT").toString());
                    oneMisc.setUnitRatio(whereData1.get("UNITRATIO").toString());
                    oneMisc.setTransQty(whereData1.get("TRANSQTY").toString());
                    oneMisc.setQty(whereData1.get("QTY").toString());
                    oneMisc.setTotAmt(whereData1.get("TOT_AMT").toString());
                    oneMisc.setMaterial(whereData1.get("MATERIAL").toString());
                    oneMisc.setLabor(whereData1.get("LABOR").toString());
                    oneMisc.setOem(whereData1.get("OEM").toString());
                    oneMisc.setExp1(whereData1.get("EXP1").toString());
                    oneMisc.setExp2(whereData1.get("EXP2").toString());
                    oneMisc.setExp3(whereData1.get("EXP3").toString());
                    oneMisc.setExp4(whereData1.get("EXP4").toString());
                    oneMisc.setExp5(whereData1.get("EXP5").toString());
                    oneMisc.setAvg_Price(whereData1.get("AVG_PRICE").toString());
                    oneMisc.setAvg_Price_Material(whereData1.get("AVG_PRICE_MATERIAL").toString());
                    oneMisc.setAvg_Price_Man(whereData1.get("AVG_PRICE_LABOR").toString());
                    oneMisc.setAvg_Price_Oem(whereData1.get("AVG_PRICE_OEM").toString());
                    oneMisc.setAvg_Price_Exp1(whereData1.get("AVG_PRICE_EXP1").toString());
                    oneMisc.setAvg_Price_Exp2(whereData1.get("AVG_PRICE_EXP2").toString());
                    oneMisc.setAvg_Price_Exp3(whereData1.get("AVG_PRICE_EXP3").toString());
                    oneMisc.setAvg_Price_Exp4(whereData1.get("AVG_PRICE_EXP4").toString());
                    oneMisc.setAvg_Price_Exp5(whereData1.get("AVG_PRICE_EXP5").toString());

//                    private String featureName;
//                    private String bsName;
//                    private String bsNo;

                }

            }


        }

        res.setSuccess(true);
        res.setServiceStatus("000");
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
    protected String getQuerySql(DCP_MiscInOutQueryReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();
        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        querySql.append(" select * from (");
        querySql.append(
                " SELECT COUNT(*) OVER() NUM ,dense_rank() over(ORDER BY BDATE DESC) rn, " +
                        " temp.* FROM(" +
                        "   SELECT a.*,b.ACCOUNT ACCOUNTNAME  " +
                        "   FROM DCP_MISCINOUT a " +
                        " LEFT JOIN DCP_ACOUNT_SETTING b on a.eid=b.eid and a.ACCOUNTID=b.ACCOUNTID "
        );

        querySql.append(" WHERE a.EId='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getAccountID())) {
            querySql.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountID()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())){
            querySql.append(" AND a.BDATE >= to_Date('").append(req.getRequest().getBeginDate()).append("','yyyyMMdd')");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getEndDate())){
            querySql.append(" AND a.BDATE <= to_Date('").append(req.getRequest().getEndDate()).append("','yyyyMMdd')");
        }


        if (StringUtils.isNotEmpty(req.getRequest().getType())) {
            querySql.append(" AND a.TYPE='").append(req.getRequest().getType()).append("'");
        }


        if (StringUtils.isNotEmpty(req.getRequest().getIsZero())) {
            if ("Y".equals(req.getRequest().getIsZero())) {
                querySql.append(" AND a.AVG_PRICE = '0' ");
            }
        }

        querySql.append(") temp  ");

        querySql.append(" ORDER BY BDATE DESC "
                + " ) where  rn>" + startRow + " and rn<=" + (startRow + pageSize) + "  "
                + " ");

        return querySql.toString();

    }
}
