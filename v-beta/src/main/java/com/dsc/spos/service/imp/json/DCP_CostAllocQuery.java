package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CostAllocQueryReq;
import com.dsc.spos.json.cust.res.DCP_CostAllocQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_CostAllocQuery extends SPosBasicService<DCP_CostAllocQueryReq, DCP_CostAllocQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_CostAllocQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostAllocQueryReq> getRequestType() {
        return new TypeToken<DCP_CostAllocQueryReq>() {
        };
    }

    @Override
    protected DCP_CostAllocQueryRes getResponseType() {
        return new DCP_CostAllocQueryRes();
    }

    @Override
    protected DCP_CostAllocQueryRes processJson(DCP_CostAllocQueryReq req) throws Exception {
        DCP_CostAllocQueryRes res = this.getResponseType();
        res.setDatas(new ArrayList<>());
        int totalRecords = 0;    //总笔数
        int totalPages = 0;        //总页数
        List<Map<String, Object>> getQData = this.doQueryData(getQuerySql(req), null);

        Map<String, Boolean> condition = Maps.newHashMap();

        condition.put("EID", true);
        condition.put("ACCOUNTID", true);
        condition.put("YEAR", true);
        condition.put("PERIOD", true);
        condition.put("COSTCENTERNO", true);
        condition.put("ALLOCTYPE", true);

        List<Map<String, Object>> headData = MapDistinct.getMap(getQData, condition);

        if (!headData.isEmpty()) {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            for (Map<String, Object> head : headData) {

                DCP_CostAllocQueryRes.Datas oneData = res.new Datas();

                oneData.setAccountId(head.get("ACCOUNTID").toString());
                oneData.setAccount(StringUtils.toString(head.get("ACCOUNT"), ""));
                oneData.setYear(StringUtils.toString(head.get("YEAR"), ""));
                oneData.setPeriod(StringUtils.toString(head.get("PERIOD"), ""));
                oneData.setCostCenter(StringUtils.toString(head.get("COSTCENTERNO"), ""));
                oneData.setAllocType(StringUtils.toString(head.get("ALLOCTYPE"), ""));
                oneData.setCorp(StringUtils.toString(head.get("CORP"), ""));
                oneData.setCorpName(StringUtils.toString(head.get("CORPNAME"), ""));

                oneData.setAllocList(new ArrayList<>());
                Map<String, Object> conditionValue = Maps.newHashMap();
                conditionValue.put("EID", req.geteId());
                conditionValue.put("ACCOUNTID", oneData.getAccountId());
                conditionValue.put("YEAR", oneData.getYear());
                conditionValue.put("PERIOD", oneData.getPeriod());
                conditionValue.put("COSTCENTERNO", oneData.getCostCenter());
                conditionValue.put("ALLOCTYPE", oneData.getAllocType());

                List<Map<String, Object>> detailData = MapDistinct.getWhereMap(getQData, conditionValue, true);

                for (Map<String, Object> detail : detailData) {

                    DCP_CostAllocQueryRes.AllocList aList = res.new AllocList();

                    aList.setStatus(StringUtils.toString(detail.get("STATUS"), ""));
                    aList.setOrganizationID(StringUtils.toString(detail.get("ORGANIZATIONNO"), ""));
                    aList.setDeptProperty("1");
                    aList.setAllocSource(StringUtils.toString(detail.get("ALLOCSOURCE"), ""));
                    aList.setItem(StringUtils.toString(detail.get("ITEM"), ""));
                    aList.setDeptId(StringUtils.toString(detail.get("DEPTNO"), ""));
                    aList.setAmt(StringUtils.toString(detail.get("AMT"), ""));
                    aList.setDept(StringUtils.toString(detail.get("DEPT"), ""));
                    aList.setOrg_Name(StringUtils.toString(detail.get("ORG_NAME"), ""));
                    aList.setAllocFormula(StringUtils.toString(detail.get("ALLOCFORMULA"), ""));
                    aList.setAllocWeight(StringUtils.toString(detail.get("ALLOCWEIGHT"), ""));

                    oneData.getAllocList().add(aList);
                }

                res.getDatas().add(oneData);

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

    @Override
    protected String getQuerySql(DCP_CostAllocQueryReq req) throws Exception {
        String eId = req.geteId();

        String accountID = req.getRequest().getAccountID();
        String year = req.getRequest().getYear();
        int period = Integer.parseInt(req.getRequest().getPeriod());


        String keyTxt = req.getRequest().getKeyTxt();
        //計算起啟位置
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        int startRow = (pageNumber - 1) * pageSize;

        StringBuilder sqlBuff = new StringBuilder();

        sqlBuff.append(" select * from ("
                + " select count(*) over() num, row_number() over (order by a.ACCOUNTID,a.YEAR,a.PERIOD,a.ITEM) rn,"
                + " a.*,cs.ACCOUNT ACCOUNTNAME,cs.CORP,ol1.ORG_NAME CORPNAME  FROM DCP_COSTALLOC a "
                + " LEFT JOIN DCP_ORG_LANG b on a.eid=b.eid and a.ORGANIZATIONNO=b.ORGANIZATIONNO and b.LANG_TYPE='" + req.getLangType() + "'"
                + " LEFT JOIN DCP_ACOUNT_SETTING cs on cs.eid=a.eid and cs.ACCOUNTID=a.ACCOUNTID "
                + " LEFT JOIN DCP_ORG_LANG ol1 on cs.eid=ol1.eid and ol1.ORGANIZATIONNO=cs.CORP and b.LANG_TYPE='" + req.getLangType() + "'"
                + " WHERE a.EID='" + eId + "' ");

        if (StringUtils.isNotEmpty(accountID)) {
            sqlBuff.append(" and a.ACCOUNTID='" + accountID + "' ");
        }

        if (StringUtils.isNotEmpty(year)) {
            sqlBuff.append(" and a.YEAR='" + year + "' ");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getPeriod())) {
            sqlBuff.append(" and a.PERIOD='" + period + "' ");
        }
        if (StringUtils.isNotEmpty(keyTxt)) {
            sqlBuff.append(" and cs.ACCOUNT like '%%" + keyTxt + "%%' ");
        }

        sqlBuff.append(" ) a where rn>" + startRow + " and rn<=" + (startRow + pageSize));
        return sqlBuff.toString();
    }
}
