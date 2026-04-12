package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_InterSettBillDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_InterSettBillDetailQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class DCP_InterSettBillDetailQuery extends SPosAdvanceService<DCP_InterSettBillDetailQueryReq, DCP_InterSettBillDetailQueryRes> {
    @Override
    protected void processDUID(DCP_InterSettBillDetailQueryReq req, DCP_InterSettBillDetailQueryRes res) throws Exception {

        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);

        res.setDatas(new ArrayList<>());
        if (CollectionUtils.isNotEmpty(qData)) {

            Map<String, Boolean> distinct = new HashMap<>();
            distinct.put("EID", true);
            distinct.put("BILLNO", true);
            distinct.put("CORP", true);

            List<Map<String, Object>> master = MapDistinct.getMap(qData, distinct);

            for (Map<String, Object> oneHead : master) {

                DCP_InterSettBillDetailQueryRes.Datas oneData = res.new Datas();
                oneData.setDetail(new ArrayList<>());
                res.getDatas().add(oneData);

                oneData.setCorp(oneHead.get("CORP").toString());
                oneData.setBillNo(oneHead.get("BILLNO").toString());
                oneData.setBDate(oneHead.get("BDATE").toString());
                oneData.setBizpartner1(oneHead.get("BIZPARTNER1").toString());
                oneData.setBizpartner2(oneHead.get("BIZPARTNER2").toString());
                oneData.setSettAccPeriod(oneHead.get("SETTACPERIOD").toString());
                oneData.setStartDate(oneHead.get("STARTDATE").toString());
                oneData.setEndDate(oneHead.get("ENDDATE").toString());
                oneData.setEmployeeId(oneHead.get("EMPLOYEEID").toString());
                oneData.setDepartId(oneHead.get("DEPARTID").toString());
                oneData.setAmt(oneHead.get("AMT").toString());
                oneData.setApNo(oneHead.get("APNO").toString());
                oneData.setArNo(oneHead.get("ARNO").toString());

                Map<String, Object> condition = new HashMap<>();
                condition.put("BILLNO", oneHead.get("BILLNO"));
                condition.put("CORP", oneHead.get("CORP"));

                List<Map<String, Object>> detail = MapDistinct.getWhereMap(qData, condition, true);

                for (Map<String, Object> oneDetail : detail) {
                    DCP_InterSettBillDetailQueryRes.Detail d = res.new Detail();
                    oneData.getDetail().add(d);

                    d.setItem(oneDetail.get("ITEM").toString());
                    d.setOrganizationNo(oneDetail.get("ORGANIZATIONNO").toString());
                    d.setBType(oneDetail.get("BTYPE").toString());
                    d.setInterTradeType(oneDetail.get("INTERTRADETYPE").toString());
                    d.setSourceNo(oneDetail.get("SOURCENO").toString());
                    d.setSourceNoSeq(oneDetail.get("SOURCENOSEQ").toString());
                    d.setRDate(oneDetail.get("RDATE").toString());
                    d.setFee(oneDetail.get("FEE").toString());
                    d.setPluNo(oneDetail.get("PLUNO").toString());
                    d.setCurrency(oneDetail.get("CURRENCY").toString());
                    d.setTaxRate(oneDetail.get("TAXRATE").toString());
                    d.setDirection(oneDetail.get("DIRECTION").toString());
                    d.setPreTaxAmt(oneDetail.get("PRETAXAMT").toString());
                    d.setAmt(oneDetail.get("AMOT").toString());
                    d.setUnsettAmt(oneDetail.get("UNSETTAMT").toString());
                    d.setSettledAmt(oneDetail.get("SETTLEDAMT").toString());
                    d.setCurSettledAmt(oneDetail.get("CURSETTEDAMT").toString());
                    d.setInvOrg(oneDetail.get("INVORG").toString());
                    d.setInvCorp(oneDetail.get("INVCORP").toString());
                    d.setWarehouse(oneDetail.get("WAREHOUSE").toString());
                    d.setBizpartnerNo(oneDetail.get("BIZPARTNERNO").toString());
                    d.setIntertradeOrg(oneDetail.get("INTERTRADEORG").toString());
                    d.setIntertradeCorp(oneDetail.get("INTERTRADECORP").toString());
                    d.setSettAccPeriod(oneDetail.get("SETTACPERIOD").toString());
                    d.setYear(oneDetail.get("YEAR").toString());
                    d.setPeriod(oneDetail.get("PERIOD").toString());
                    d.setAccountOrg(oneDetail.get("ACCOUNTORG").toString());
                    d.setCorp(oneDetail.get("CORP").toString());
                    d.setGlType(oneDetail.get("GLTYPE").toString());
                    d.setPostedAmt(oneDetail.get("POSTEDAMT").toString());
                    d.setOItem(oneDetail.get("OITEM").toString());
                    d.setInOutCode(oneDetail.get("INOUTCODE").toString());
                    d.setQty(oneDetail.get("QTY").toString());
                    d.setPrice(oneDetail.get("PRICE").toString());
                    d.setBaseUnit(oneDetail.get("BASEUNIT").toString());

                }

            }

        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_InterSettBillDetailQueryReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_InterSettBillDetailQueryReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_InterSettBillDetailQueryReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_InterSettBillDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_InterSettBillDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_InterSettBillDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_InterSettBillDetailQueryRes getResponseType() {
        return new DCP_InterSettBillDetailQueryRes();
    }

    @Override
    protected String getQuerySql(DCP_InterSettBillDetailQueryReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT b.*  ")
                .append(" FROM DCP_INTERSETTBILL a ")
                .append(" INNER JOIN DCP_INTERSETTBILLDETAIL b on a.eid=b.eid and a.BILLNO=b.BILLNO ")
        ;

        querySql.append(" WHERE a.eid='").append(req.geteId()).append("' ");

        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            querySql.append(" and a.BILLNO='").append(req.getRequest().getBillNo()).append("' ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            querySql.append(" and a.CORP='").append(req.getRequest().getCorp()).append("' ");
        }

        return querySql.toString();
    }
}
