package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_AcctStockMthQueryReq;
import com.dsc.spos.json.cust.res.DCP_AcctStockMthQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_AcctStockMthQuery extends SPosBasicService<DCP_AcctStockMthQueryReq, DCP_AcctStockMthQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_AcctStockMthQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_AcctStockMthQueryReq> getRequestType() {
        return new TypeToken<DCP_AcctStockMthQueryReq>() {
        };
    }

    @Override
    protected DCP_AcctStockMthQueryRes getResponseType() {
        return new DCP_AcctStockMthQueryRes();
    }

    @Override
    protected DCP_AcctStockMthQueryRes processJson(DCP_AcctStockMthQueryReq req) throws Exception {
        DCP_AcctStockMthQueryRes res = this.getResponseType();
        res.setDatas(res.new Datas());

        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        if (CollectionUtils.isNotEmpty(qData)) {
            Map<String, Object> row = qData.get(0);

            res.getDatas().setAccountID(row.get("ACCOUNTID").toString());
            res.getDatas().setAccountName(row.get("ACCOUNT").toString());
            res.getDatas().setYear(row.get("YEAR").toString());
            res.getDatas().setPeriod(row.get("PERIOD").toString());
            res.getDatas().setIsDiffQty(req.getRequest().getIsDiffQty());

            res.getDatas().setQtyList(new ArrayList<>());
            for (Map<String, Object> oneData : qData) {
                DCP_AcctStockMthQueryRes.QtyList oneQty = res.new QtyList();

                oneQty.setCostDomainId(oneData.get("COSTDOMAINID").toString());
                oneQty.setCostDomainIdName(oneData.get("COSTDOMAINIDNAME").toString());
                oneQty.setPluNo(oneData.get("PLUNO").toString());
                oneQty.setPluName(oneData.get("PLU_NAME").toString());
                oneQty.setFeatureNo(oneData.get("FEATURENO").toString());
                oneQty.setFeatureName(oneData.get("FEATURENAME").toString());
                oneQty.setSpec(oneData.get("SPEC").toString());
                oneQty.setBaseUnit(oneData.get("BASEUNIT").toString());
                oneQty.setBaseUnitName(oneData.get("BASEUNITNAME").toString());
                oneQty.setCostQty(oneData.get("COSTQTY").toString());
                oneQty.setStockQty(oneData.get("STOCKQTY").toString());
                oneQty.setDiffQty(oneData.get("DIFFQTY").toString());
                res.getDatas().getQtyList().add(oneQty);

            }

        }


        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_AcctStockMthQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT a.*,dg.BASEUNIT,dg.SPEC,act.ACCOUNT,ol1.ORG_NAME COSTDOMAINNAME,dgl.PLU_NAME ")
                .append(" ,gfl.FEATURENAME,ul.UNAME BASEUNITNAME ")
                .append(" FROM (")
                .append("   SELECT ta.*,NVL(ta.ENDINGBALQTY,0) as COSTQTY,NVL(tb.ENDINGBALQTY,0) as STOCKQTY,NVL(ta.ENDINGBALQTY,0)-NVL(tb.ENDINGBALQTY,0) DIFFQTY ")
                .append("   FROM (").append(getQueryCurInvCostStat(req)).append(" ) ta ")
                .append("   LEFT JOIN (").append(getQueryAcctStockMthStat(req)).append(") tb  ")
                .append("   ON ta.EID = tb.EID and ta.ACCOUNTID=tb.ACCOUNTID AND ta.COSTDOMAINID=tb.ORGANIZATIONNO ")
                .append("   AND ta.YEAR=tb.YEAR and ta.PERIOD=tb.PERIOD and ta.PLUNO=tb.PLUNO and ta.FEATURENO=tb.FEATURENO ")
                .append(" ) a ")
                .append(" LEFT JOIN DCP_ACOUNT_SETTING act ON act.ACCOUNTID= a.ACCOUNTID and a.EID=act.EID ")
                .append(" LEFT JOIN DCP_ORG o1 on o1.eid=a.eid and o1.ORGANIZATIONNO=a.COSTDOMAINID ")
                .append(" LEFT JOIN DCP_GOODS dg on a.eid=dg.eid and dg.PLUNO=a.PLUNO ")
                .append(" LEFT JOIN DCP_GOODS_FEATURE_LANG gfl on gfl.eid=a.eid and gfl.PLUNO=a.PLUNO and gfl.FEATURENO=a.FEATURENO and gfl.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 on ol1.eid=a.eid and ol1.ORGANIZATIONNO=a.COSTDOMAINID and ol1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_UNIT_LANG ul on ul.eid=a.eid and dg.BASEUNIT=ul.UNIT and ul.LANG_TYPE='").append(req.getLangType()).append("'")
        ;

        if ("Y".equals(req.getRequest().getIsDiffQty())) {
            sb.append(" WHERE DIFFQTY<>0  ");
        }


        return sb.toString();
    }

    protected String getQueryAcctStockMthStat(DCP_AcctStockMthQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT ")
                .append("  a.EID,a.ACCOUNTID,a.ORGANIZATIONNO,a.YEAR,a.PERIOD,a.PLUNO,a.FEATURENO ")
                .append(" ,SUM(a.ENDINGBALQTY) ENDINGBALQTY ")
                .append(" FROM DCP_ACCTSTOCKMTHSTAT a ")
//                .append(" LEFT JOIN DCP_ACOUNT_SETTING act ON act.ACCOUNTID= a.ACCOUNTID and a.EID=act.EID ")
        ;

        sb.append(" WHERE a.EID =‘").append(req.geteId()).append("’");
        if (Check.isNotEmpty(req.getRequest().getAccountID())) {
            sb.append(" AND a.ACCOUNTID =‘").append(req.getRequest().getAccountID()).append("’");
        }
        if (Check.isNotEmpty(req.getRequest().getYear())) {
            sb.append(" AND a.YEAR =").append(req.getRequest().getYear());
        }
        if (Check.isNotEmpty(req.getRequest().getPeriod())) {
            sb.append(" AND a.PERIOD =").append(req.getRequest().getPeriod());
        }

        sb.append("   GROUP BY a.EID,a.ACCOUNTID,a.ORGANIZATIONNO,a.YEAR,a.PERIOD,a.PLUNO,a.FEATURENO ");

        return sb.toString();
    }

    protected String getQueryCurInvCostStat(DCP_AcctStockMthQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT ")
                .append("  a.EID,a.ACCOUNTID,a.COSTDOMAINID,a.YEAR,a.PERIOD,a.PLUNO,a.FEATURENO ")
                .append("  ,SUM(a.ENDINGBALQTY) ENDINGBALQTY ")
                .append(" FROM DCP_CURINVCOSTSTAT a ")
//                .append(" LEFT JOIN DCP_ACOUNT_SETTING act ON act.ACCOUNTID= a.ACCOUNTID and a.EID=act.EID ")
        ;

        sb.append(" WHERE a.EID =‘").append(req.geteId()).append("’");
        if (Check.isNotEmpty(req.getRequest().getAccountID())) {
            sb.append(" AND a.ACCOUNTID =‘").append(req.getRequest().getAccountID()).append("’");
        }
        if (Check.isNotEmpty(req.getRequest().getYear())) {
            sb.append(" AND a.YEAR =").append(req.getRequest().getYear());
        }
        if (Check.isNotEmpty(req.getRequest().getPeriod())) {
            sb.append(" AND a.PERIOD =").append(req.getRequest().getPeriod());
        }

        sb.append("  GROUP BY a.EID,a.ACCOUNTID,a.COSTDOMAINID,a.YEAR,a.PERIOD,a.PLUNO,a.FEATURENO ");

        return sb.toString();
    }


}
