package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CurInvCostAdjDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_CurInvCostAdjDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_CurInvCostAdjDetailQuery extends SPosBasicService<DCP_CurInvCostAdjDetailQueryReq, DCP_CurInvCostAdjDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_CurInvCostAdjDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CurInvCostAdjDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_CurInvCostAdjDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_CurInvCostAdjDetailQueryRes getResponseType() {
        return new DCP_CurInvCostAdjDetailQueryRes();
    }

    @Override
    protected DCP_CurInvCostAdjDetailQueryRes processJson(DCP_CurInvCostAdjDetailQueryReq req) throws Exception {
        DCP_CurInvCostAdjDetailQueryRes res = this.getResponseType();
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);
        res.setInvList(new ArrayList<>());
        if (getData != null && !getData.isEmpty()) {

            Map<String, Object> oneData = getData.get(0);

            res.setStatus(oneData.get("MASTERSTATUS").toString());
            res.setAccountID(oneData.get("ACCOUNTID").toString());
            res.setAccount(oneData.get("ACCOUNTNAME").toString());
            res.setYear(oneData.get("YEAR").toString());
            res.setPeriod(oneData.get("PERIOD").toString());
            res.setCost_Calculation(oneData.get("COST_CALCULATION").toString());
            res.setDataSource(oneData.get("DATASOURCE").toString());
            res.setReferenceNo(oneData.get("REFERENCENO").toString());

            for (Map<String, Object> data : getData) {
                DCP_CurInvCostAdjDetailQueryRes.InvList invList = res.new InvList();
                res.getInvList().add(invList);

                invList.setItem(data.get("ITEM").toString());
                invList.setCostDomainId(data.get("COSTDOMAINID").toString());
                invList.setCostDomainDis(data.get("COSTDOMAINNAME").toString());
                invList.setPulNo(data.get("PLUNO").toString());
                invList.setPluName(data.get("PLUNAME").toString());
                invList.setFeatureNo(data.get("FEATURENO").toString());
                invList.setBaseUnitName(data.get("BASEUNITNAME").toString());
                invList.setMemo(data.get("MEMO").toString());
                invList.setBsName(data.get("BASEUNIT").toString());
                invList.setBaseUnitName(data.get("BASEUNITNAME").toString());
                invList.setCategory(data.get("CATEGORY").toString());
                invList.setCategoryName(data.get("CATEGORY_NAME").toString());
                invList.setQty("");
                invList.setTotPretaxAmt(data.get("TOT_PRETAXAMT").toString());
                invList.setPretaxMaterial(data.get("PRETAXMATERIAL").toString());
                invList.setPretaxLabor(data.get("PRETAXLABOR").toString());
                invList.setPretaxOem(data.get("PRETAXOEM").toString());
                invList.setPretaxExp1(data.get("PRETAXEXP1").toString());
                invList.setPretaxExp2(data.get("PRETAXEXP2").toString());
                invList.setPretaxExp3(data.get("PRETAXEXP3").toString());
                invList.setPretaxExp4(data.get("PRETAXEXP4").toString());
                invList.setPretaxExp5(data.get("PRETAXEXP5").toString());
                invList.setMemo(data.get("MEMO").toString());

            }

        }
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_CurInvCostAdjDetailQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

//        int pageNumber = req.getPageNumber();
//        int pageSize = req.getPageSize();
//
//        //計算起啟位置
//        int startRow = (pageNumber - 1) * pageSize;

        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.REFERENCENO DESC) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" c.*,a.COST_CALCULATION ")
                .append(" ,a.STATUS MASTERSTATUS,b.ACCOUNT ACCOUNTNAME ")
                .append(" ,g.BASEUNIT,g.CATEGORY,ul1.UNAME BASEUNITNAME,cl1.CATEGORY_NAME ")
                .append(" ,ol1.ORG_NAME COSTDOMAINNAME ")
                .append(" FROM DCP_CURINVCOSTADJ a ")
                .append(" INNER JOIN DCP_CURINVCOSTDETAILADJ c on a.eid=c.eid and a.ACCOUNTID=c.ACCOUNTID and a.YEAR=c.YEAR and a.PERIOD=c.PERIOD and a.REFERENCENO=c.REFERENCENO  ")
                .append(" LEFT JOIN DCP_ACOUNT_SETTING b on a.eid=b.eid and a.ACCOUNTID=b.ACCOUNTID ")
                .append(" LEFT JOIN DCP_GOODS g on g.eid=b.eid and g.PLUNO=c.PLUNO ")
                .append(" LEFT JOIN DCP_UNIT_LANG ul1 on ul1.eid=g.eid and ul1.UNIT=g.BASEUNIT and ul1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_CATEGORY_LANG cl1 on cl1.eid=g.eid and cl1.CATEGORY=g.CATEGORY and cl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 on ol1.EID=c.EID and ol1.ORGANIZATIONNO=c.COSTDOMAINID AND ol1.LANG_TYPE='").append(req.getLangType()).append("'")

        ;

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getYear())) {
            sb.append(" and a.YEAR='").append(req.getRequest().getYear()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getPeriod())) {
            sb.append(" and a.PERIOD='").append(req.getRequest().getPeriod()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getAccountID())) {
            sb.append(" and a.ACCOUNTID='").append(req.getRequest().getAccountID()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getReferenceNo())) {
            sb.append(" and a.REFERENCENO='").append(req.getRequest().getReferenceNo()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getCost_Calculation())) {
            sb.append(" and a.COST_CALCULATION='").append(req.getRequest().getCost_Calculation()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getDataSource())) {
            sb.append(" and c.DATASOURCE='").append(req.getRequest().getDataSource()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getProdNo())) {
            sb.append(" and c.PLUNO='").append(req.getRequest().getProdNo()).append("'");
        }

        sb.append("  ) a "
//                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
//                + " ORDER BY REFERENCENO "
        );

        return sb.toString();
    }
}
