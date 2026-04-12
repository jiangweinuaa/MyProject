package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CurInvCostAdjCreateReq;
import com.dsc.spos.json.cust.res.DCP_CurInvCostAdjCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_CurInvCostAdjCreate extends SPosAdvanceService<DCP_CurInvCostAdjCreateReq, DCP_CurInvCostAdjCreateRes> {
    @Override
    protected void processDUID(DCP_CurInvCostAdjCreateReq req, DCP_CurInvCostAdjCreateRes res) throws Exception {

//        1. 来源类型为：1 人工输入
//        2. 新增时判断账套新加年度期别不得小于账套成本关账日成本关账日期；
//        3. 账套对应法人组织启用成本域，成本域及成本域名称为必填字段；
//        4. 商品编码为必填字段，
//        5. 金额可以为负数，但不可为 0；金额=材料+人工+委外+制费；
//        6. 数据源为 1：人工录入，单号人为手工输入；不定格式，判断不得重复；

//        String querySql = " SELECT * FROM DCP_CURINVCOSTADJ WHERE eid='" + req.geteId() + "'" + " AND REFERENCENO='" + req.getRequest().getReferenceNo() + "'";
//        List<Map<String, Object>> qData = doQueryData(querySql, null);
//        if (CollectionUtils.isNotEmpty(qData)) {
//            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单号已存在！");
//        }

        String date = DateFormatUtils.getYearMonthLastDate(req.getRequest().getYear(), req.getRequest().getPeriod());
        String referenceNo = getNormalNOWithDate(req, "CBTZ", date);

        String querySql = " SELECT a.*,b.COST_DOMAIN FROM DCP_ACOUNT_SETTING a " +
                " LEFT JOIN DCP_ORG b on a.eid=b.eid and a.CORP=b.ORGANIZATIONNO " +
                " WHERE a.eid='" + req.geteId() + "' AND a.ACCOUNTID='" + req.getRequest().getAccountID() + "'";
        List<Map<String, Object>> qData = doQueryData(querySql, null);
        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, req.getRequest().getAccountID() + "帐套未设置");
        } else {
            String costclosingdate = DateFormatUtils.getPlainDate(qData.get(0).get("COSTCLOSINGDATE").toString());
            String year = req.getRequest().getYear();
            String period = String.format("%02d", Integer.parseInt(req.getRequest().getPeriod()));

            if (Double.parseDouble(costclosingdate) > Double.parseDouble(year + period + "01")) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "新加年度期别不得小于账套成本关账日成本关账日期");
            }

        }
        String corp = qData.get(0).get("CORP").toString();
        String costDoMain = qData.get(0).get("COST_DOMAIN").toString();

        if ("Y".equals(costDoMain)) {
            querySql = " SELECT * FROM DCP_COSTDOMAIN " +
                    " WHERE eid='" + req.geteId() + "' AND CORP='" + corp + "'";
            qData = doQueryData(querySql, null);
            if (CollectionUtils.isNotEmpty(qData)) {
                for (DCP_CurInvCostAdjCreateReq.InvList inv : req.getRequest().getAdjList()) {
                    if (StringUtils.isEmpty(inv.getCostDomainId())) {
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, corp + "启用成本域:costDomainId不可空白！");
                    }
                }
            }
        }

        ColumnDataValue dcp_curinvcostadj = new ColumnDataValue();

        dcp_curinvcostadj.add("EID", DataValues.newString(req.geteId()));
        dcp_curinvcostadj.add("YEAR", DataValues.newString(req.getRequest().getYear()));
        dcp_curinvcostadj.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));
        dcp_curinvcostadj.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
        dcp_curinvcostadj.add("COST_CALCULATION", DataValues.newString(req.getRequest().getCost_Calculation()));
        dcp_curinvcostadj.add("DATASOURCE", DataValues.newString(req.getRequest().getDataSource()));
//        dcp_curinvcostadj.add("REFERENCENO", DataValues.newString(req.getRequest().getReferenceNo()));
        dcp_curinvcostadj.add("REFERENCENO", DataValues.newString(referenceNo));

        dcp_curinvcostadj.add("STATUS", DataValues.newString(req.getRequest().getStatus()));

        dcp_curinvcostadj.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
        dcp_curinvcostadj.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
        dcp_curinvcostadj.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDate()));

        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_CURINVCOSTADJ", dcp_curinvcostadj)));

        for (DCP_CurInvCostAdjCreateReq.InvList inv : req.getRequest().getAdjList()) {
            ColumnDataValue dcp_curinvcostdetailadj = new ColumnDataValue();

            dcp_curinvcostdetailadj.add("EID", DataValues.newString(req.geteId()));
            dcp_curinvcostdetailadj.add("YEAR", DataValues.newString(req.getRequest().getYear()));
            dcp_curinvcostdetailadj.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));
            dcp_curinvcostdetailadj.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
            dcp_curinvcostdetailadj.add("ACCOUNT", DataValues.newString(req.getRequest().getAccount()));
            dcp_curinvcostdetailadj.add("CORP", DataValues.newString(corp));
//            dcp_curinvcostdetailadj.add("COST_CALCULATION", DataValues.newString(req.getRequest().getCost_Calculation()));
            dcp_curinvcostdetailadj.add("DATASOURCE", DataValues.newString(req.getRequest().getDataSource()));
            dcp_curinvcostdetailadj.add("REFERENCENO", DataValues.newString(referenceNo));
            dcp_curinvcostdetailadj.add("STATUS", DataValues.newString(req.getRequest().getStatus()));

            dcp_curinvcostdetailadj.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_curinvcostdetailadj.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
            dcp_curinvcostdetailadj.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDate()));

            dcp_curinvcostdetailadj.add("ITEM", DataValues.newString(inv.getItem()));
            dcp_curinvcostdetailadj.add("COSTDOMAINID", DataValues.newString(inv.getCostDomainId()));
            dcp_curinvcostdetailadj.add("COSTDOMAINDIS", DataValues.newString(inv.getCostDomainDis()));
            dcp_curinvcostdetailadj.add("PLUNO", DataValues.newString(inv.getPluNo()));
            dcp_curinvcostdetailadj.add("PLUNAME", DataValues.newString(inv.getPluName()));
            dcp_curinvcostdetailadj.add("FEATURENO", DataValues.newString(StringUtils.toString(inv.getFeatureNo(), " ")));
            dcp_curinvcostdetailadj.add("MEMO", DataValues.newString(inv.getMemo()));
            dcp_curinvcostdetailadj.add("TOT_AMT", DataValues.newString(inv.getTotAmt()));
            dcp_curinvcostdetailadj.add("MATERIAL", DataValues.newString(inv.getMaterial()));
            dcp_curinvcostdetailadj.add("LABOR", DataValues.newString(inv.getLabor()));
            dcp_curinvcostdetailadj.add("OEM", DataValues.newString(inv.getOem()));
            dcp_curinvcostdetailadj.add("EXP1", DataValues.newString(inv.getExp1()));
            dcp_curinvcostdetailadj.add("EXP2", DataValues.newString(inv.getExp2()));
            dcp_curinvcostdetailadj.add("EXP3", DataValues.newString(inv.getExp3()));
            dcp_curinvcostdetailadj.add("EXP4", DataValues.newString(inv.getExp4()));
            dcp_curinvcostdetailadj.add("EXP5", DataValues.newString(inv.getExp5()));
            dcp_curinvcostdetailadj.add("TOT_PRETAXAMT", DataValues.newString(inv.getTotPretaxAmt()));
            dcp_curinvcostdetailadj.add("PRETAXMATERIAL", DataValues.newString(inv.getPretaxMaterial()));
            dcp_curinvcostdetailadj.add("PRETAXLABOR", DataValues.newString(inv.getPretaxLabor()));
            dcp_curinvcostdetailadj.add("PRETAXOEM", DataValues.newString(inv.getPretaxOem()));
            dcp_curinvcostdetailadj.add("PRETAXEXP1", DataValues.newString(inv.getPretaxExp1()));
            dcp_curinvcostdetailadj.add("PRETAXEXP2", DataValues.newString(inv.getPretaxExp2()));
            dcp_curinvcostdetailadj.add("PRETAXEXP3", DataValues.newString(inv.getPretaxExp3()));
            dcp_curinvcostdetailadj.add("PRETAXEXP4", DataValues.newString(inv.getPretaxExp4()));
            dcp_curinvcostdetailadj.add("PRETAXEXP5", DataValues.newString(inv.getPretaxExp5()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_CURINVCOSTDETAILADJ", dcp_curinvcostdetailadj)));

        }
        
        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CurInvCostAdjCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CurInvCostAdjCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CurInvCostAdjCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CurInvCostAdjCreateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CurInvCostAdjCreateReq> getRequestType() {
        return new TypeToken<DCP_CurInvCostAdjCreateReq>() {
        };
    }

    @Override
    protected DCP_CurInvCostAdjCreateRes getResponseType() {
        return new DCP_CurInvCostAdjCreateRes();
    }
}
